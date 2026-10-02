package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.FluidClientException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty-based Thales payShield HSM client for the host command interface.
 *
 * Wire protocol (payShield 10K Host Programmers Manual):
 * <ul>
 *   <li>Each message is prefixed with a 2-byte binary length (section 2.1.2 / 2.1.3).</li>
 *   <li>Each command starts with the site-configured message header, which the HSM
 *       echoes back unmodified (section 1.2). The client allocates a unique header per
 *       in-flight command and uses it to correlate responses, so commands may be
 *       pipelined on a single connection.</li>
 *   <li>With a header length of 0 the client falls back to strict FIFO correlation.</li>
 * </ul>
 *
 * TLS / mutual TLS is enabled by supplying an {@link SslContext} through
 * {@link ThalesHSMClientConfig}; see {@link ThalesSslContexts}.
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Log
public class ThalesHSMClient implements AutoCloseable {

    private static final int HEADER_ALLOCATION_ATTEMPTS = 64;

    private final Channel channel;
    private final EventLoopGroup group;
    private final ThalesHSMClientHandler handler;

    /** Pending requests keyed by message header (header mode). */
    private final Map<String, CompletableFuture<ThalesResponse>> pendingRequests = new ConcurrentHashMap<>();
    /** Pending requests in send order (FIFO mode, header length 0). */
    private final Deque<CompletableFuture<ThalesResponse>> pendingFifo = new ArrayDeque<>();
    private final Object fifoLock = new Object();

    private final AtomicInteger headerSequence = new AtomicInteger();

    @Getter
    private final ThalesHSMClientConfig config;
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final boolean useSsl;
    @Getter
    private final int headerLength;

    private final IThalesResponseHandler defaultHandler = new IThalesResponseHandler() {
        @Override
        public void handleResponse(ThalesResponse response) {
            CompletableFuture<ThalesResponse> future;
            if (headerLength == 0) {
                synchronized (fifoLock) {
                    future = pendingFifo.pollFirst();
                }
            } else {
                String header = response.getHeader();
                future = header == null ? null : pendingRequests.remove(header);
            }

            if (future != null) {
                future.complete(response);
                return;
            }
            log.warning("Received response without matching request: " + response);
        }

        @Override
        public void connectionClosed() {
            log.info("HSM connection closed");
            failAllPending(new FluidClientException(
                    "Connection closed",
                    FluidClientException.ErrorCode.IO_ERROR
            ));
        }

        @Override
        public void handleError(Throwable error) {
            log.severe("HSM error: " + error.getMessage());
            failAllPending(error);
        }
    };

    /**
     * Constructs a ThalesHSMClient and connects to the HSM using no message header.
     * When {@code useSsl} is true the server certificate is <b>not</b> verified; use
     * {@link #ThalesHSMClient(ThalesHSMClientConfig)} with {@link ThalesSslContexts} for
     * production connections.
     *
     * @param host The HSM host address
     * @param port The HSM port (typically 1500 for Thales)
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @throws Exception If connection fails
     */
    public ThalesHSMClient(String host, int port, boolean useSsl) throws Exception {
        this(host, port, useSsl, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClient with timeout configuration and no message header.
     * When {@code useSsl} is true the server certificate is <b>not</b> verified.
     *
     * @param host The HSM host address
     * @param port The HSM port
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @param readTimeoutSeconds Read timeout in seconds
     * @param writeTimeoutSeconds Write timeout in seconds
     * @throws Exception If connection fails
     */
    public ThalesHSMClient(
            String host,
            int port,
            boolean useSsl,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) throws Exception {
        this(legacyConfig(host, port, useSsl, readTimeoutSeconds, writeTimeoutSeconds));
    }

    private static ThalesHSMClientConfig legacyConfig(
            String host, int port, boolean useSsl, int readTimeoutSeconds, int writeTimeoutSeconds
    ) throws Exception {
        SslContext sslCtx = null;
        if (useSsl) {
            log.warning("ThalesHSMClient created with insecure TLS trust for " + host + ":" + port
                    + "; supply an SslContext via ThalesHSMClientConfig for verified/mutual TLS.");
            sslCtx = ThalesSslContexts.insecure();
        }
        return ThalesHSMClientConfig.builder()
                .host(host)
                .port(port)
                .headerLength(0)
                .sslContext(sslCtx)
                .readTimeoutSeconds(readTimeoutSeconds)
                .writeTimeoutSeconds(writeTimeoutSeconds)
                .build();
    }

    /**
     * Constructs a ThalesHSMClient from a configuration and connects to the HSM.
     *
     * @param config Connection configuration
     * @throws FluidClientException If the configuration is invalid or the connection fails
     */
    public ThalesHSMClient(ThalesHSMClientConfig config) {
        config.validate();
        this.config = config;
        this.host = config.getHost();
        this.port = config.getPort();
        this.useSsl = config.isTls();
        this.headerLength = config.getHeaderLength();

        this.group = new NioEventLoopGroup();
        try {
            this.handler = new ThalesHSMClientHandler(defaultHandler);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
                    .handler(new ThalesHSMClientInitializer(
                            config.getSslContext(),
                            config.isVerifyHostname(),
                            host,
                            port,
                            handler,
                            headerLength,
                            config.getReadTimeoutSeconds(),
                            config.getWriteTimeoutSeconds()
                    ));

            // Connect to HSM
            ChannelFuture connectFuture = bootstrap.connect(host, port).sync();
            if (!connectFuture.isSuccess()) {
                throw new FluidClientException(
                        "Failed to connect to Thales HSM at " + host + ":" + port,
                        connectFuture.cause(),
                        FluidClientException.ErrorCode.IO_ERROR
                );
            }
            this.channel = connectFuture.channel();

            // Complete the TLS handshake before handing the channel out
            SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
            if (sslHandler != null) {
                sslHandler.handshakeFuture().sync();
            }

            log.info("Connected to Thales HSM at " + host + ":" + port
                    + (useSsl ? " (TLS)" : "") + ", header length " + headerLength);

        } catch (Exception e) {
            group.shutdownGracefully();
            throw new FluidClientException(
                    "Failed to initialize Thales HSM client: " + e.getMessage(),
                    e,
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }
    }

    /**
     * Sends a command to the HSM asynchronously.
     *
     * @param command The ThalesCommand to send
     * @return CompletableFuture that will contain the response
     */
    public CompletableFuture<ThalesResponse> sendCommandAsync(ThalesCommand command) {
        CompletableFuture<ThalesResponse> future = new CompletableFuture<>();
        if (channel == null || !channel.isActive()) {
            future.completeExceptionally(new FluidClientException(
                    "Channel is not active",
                    FluidClientException.ErrorCode.SESSION_EXPIRED
            ));
            return future;
        }

        final String header;
        try {
            header = registerPending(command, future);
        } catch (RuntimeException e) {
            future.completeExceptionally(e);
            return future;
        }

        channel.writeAndFlush(command).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                handler.incrementSentCommands();
            } else {
                unregisterPending(header, future);
                future.completeExceptionally(new FluidClientException(
                        "Failed to send command: " + channelFuture.cause().getMessage(),
                        channelFuture.cause(),
                        FluidClientException.ErrorCode.IO_ERROR
                ));
            }
        });

        return future;
    }

    /**
     * Assigns a message header to the command and records the pending future.
     *
     * @return the header assigned ({@code ""} in FIFO mode)
     */
    private String registerPending(ThalesCommand command, CompletableFuture<ThalesResponse> future) {
        if (headerLength == 0) {
            synchronized (fifoLock) {
                command.assignRequestId("");
                pendingFifo.addLast(future);
            }
            return "";
        }

        String requested = command.getRequestId();
        if (requested != null && !requested.isEmpty()) {
            if (requested.length() != headerLength) {
                throw new IllegalArgumentException("Request id '" + requested + "' must be "
                        + headerLength + " characters to be used as the message header");
            }
            if (pendingRequests.putIfAbsent(requested, future) != null) {
                throw new IllegalStateException("Request id '" + requested + "' is already in flight");
            }
            return requested;
        }

        for (int attempt = 0; attempt < HEADER_ALLOCATION_ATTEMPTS; attempt++) {
            String header = nextHeader();
            if (pendingRequests.putIfAbsent(header, future) == null) {
                command.assignRequestId(header);
                return header;
            }
        }
        throw new IllegalStateException("Unable to allocate a free message header; "
                + pendingRequests.size() + " commands in flight");
    }

    private void unregisterPending(String header, CompletableFuture<ThalesResponse> future) {
        if (headerLength == 0) {
            synchronized (fifoLock) {
                pendingFifo.remove(future);
            }
        } else {
            pendingRequests.remove(header, future);
        }
    }

    /** Next header: upper-case hex sequence, zero-padded/truncated to the header length. */
    private String nextHeader() {
        int seq = headerSequence.getAndIncrement() & 0x7FFFFFFF;
        String hex = Integer.toHexString(seq).toUpperCase(Locale.ROOT);
        if (hex.length() >= headerLength) {
            return hex.substring(hex.length() - headerLength);
        }
        StringBuilder sb = new StringBuilder(headerLength);
        for (int i = hex.length(); i < headerLength; i++) sb.append('0');
        return sb.append(hex).toString();
    }

    private void failAllPending(Throwable error) {
        List<CompletableFuture<ThalesResponse>> toFail = new ArrayList<>(pendingRequests.values());
        pendingRequests.clear();
        synchronized (fifoLock) {
            toFail.addAll(pendingFifo);
            pendingFifo.clear();
        }
        toFail.forEach(f -> f.completeExceptionally(error));
    }

    /**
     * Sends a command to the HSM synchronously with default timeout.
     *
     * @param command The ThalesCommand to send
     * @return ThalesResponse from the HSM
     * @throws Exception If command fails or times out
     */
    public ThalesResponse sendCommand(ThalesCommand command) throws Exception {
        return sendCommand(command, 30, TimeUnit.SECONDS);
    }

    /**
     * Sends a command to the HSM synchronously with timeout.
     *
     * @param command The ThalesCommand to send
     * @param timeout Timeout value
     * @param unit Timeout unit
     * @return ThalesResponse from the HSM
     * @throws Exception If command fails or times out
     */
    public ThalesResponse sendCommand(ThalesCommand command, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<ThalesResponse> future = sendCommandAsync(command);
        try {
            return future.get(timeout, unit);
        } catch (java.util.concurrent.TimeoutException e) {
            unregisterPending(command.getRequestId() == null ? "" : command.getRequestId(), future);
            future.cancel(true);
            throw e;
        }
    }

    /**
     * Sends an {@code NO} (HSM status) command to test HSM connectivity.
     *
     * @param echoData The command data
     * @return ThalesResponse
     * @throws Exception If command fails
     */
    public ThalesResponse echo(String echoData) throws Exception {
        return sendCommand(ThalesCommand.Commands.echo(echoData));
    }

    /**
     * Sends a diagnostics command to check HSM status.
     *
     * @return ThalesResponse with HSM status
     * @throws Exception If command fails
     */
    public ThalesResponse diagnostics() throws Exception {
        return sendCommand(ThalesCommand.Commands.diagnostics());
    }

    /**
     * Checks if the connection to the HSM is active.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * Gets the channel ID.
     *
     * @return Channel ID string
     */
    public String getChannelId() {
        return channel != null ? channel.id().asLongText() : null;
    }

    /**
     * Gets the count of sent commands.
     *
     * @return Number of commands sent
     */
    public int getSentCommands() {
        return handler != null ? handler.getSentCommands() : 0;
    }

    /**
     * Gets the count of received responses.
     *
     * @return Number of responses received
     */
    public int getReceivedResponses() {
        return handler != null ? handler.getReceivedResponses() : 0;
    }

    /**
     * Gets the number of commands awaiting a response.
     *
     * @return In-flight command count
     */
    public int getPendingCount() {
        synchronized (fifoLock) {
            return pendingRequests.size() + pendingFifo.size();
        }
    }

    /**
     * Closes the connection to the HSM.
     */
    @Override
    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close().awaitUninterruptibly();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        failAllPending(new FluidClientException("Client closed", FluidClientException.ErrorCode.IO_ERROR));
        log.info("Closed connection to Thales HSM " + host + ":" + port);
    }
}
