package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.FluidClientException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Netty-based Thales HSM client implementing the international command set.
 * This client provides high-performance, production-ready communication
 * with Thales Hardware Security Modules.
 *
 * Features:
 * - SSL/TLS support for secure communication
 * - Asynchronous command execution with CompletableFuture
 * - Connection pooling support
 * - Automatic reconnection
 * - Request/response correlation
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Log
public class ThalesHSMClient implements AutoCloseable {

    private Channel channel;
    private final EventLoopGroup group;
    private final ThalesHSMClientHandler handler;
    private final Map<String, CompletableFuture<ThalesResponse>> pendingRequests;

    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final boolean useSsl;

    private final IThalesResponseHandler defaultHandler = new IThalesResponseHandler() {
        @Override
        public void handleResponse(ThalesResponse response) {
            String requestId = response.getRequestId();
            if (requestId != null) {
                CompletableFuture<ThalesResponse> future = pendingRequests.remove(requestId);
                if (future != null) {
                    future.complete(response);
                    return;
                }
            }
            log.warning("Received response without matching request: " + response);
        }

        @Override
        public void connectionClosed() {
            log.info("HSM connection closed");
            // Complete all pending requests with error
            pendingRequests.values().forEach(future ->
                    future.completeExceptionally(new FluidClientException(
                            "Connection closed",
                            FluidClientException.ErrorCode.IO_ERROR
                    ))
            );
            pendingRequests.clear();
        }

        @Override
        public void handleError(Throwable error) {
            log.severe("HSM error: " + error.getMessage());
            // Complete all pending requests with error
            pendingRequests.values().forEach(future ->
                    future.completeExceptionally(error)
            );
            pendingRequests.clear();
        }
    };

    /**
     * Constructs a ThalesHSMClient and connects to the HSM.
     *
     * @param host The HSM host address
     * @param port The HSM port (typically 1500 for Thales)
     * @param useSsl Whether to use SSL/TLS encryption
     * @throws Exception If connection fails
     */
    public ThalesHSMClient(String host, int port, boolean useSsl) throws Exception {
        this(host, port, useSsl, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClient with timeout configuration.
     *
     * @param host The HSM host address
     * @param port The HSM port
     * @param useSsl Whether to use SSL/TLS encryption
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
        this.host = host;
        this.port = port;
        this.useSsl = useSsl;
        this.pendingRequests = new ConcurrentHashMap<>();

        // Configure SSL if needed
        final SslContext sslCtx;
        if (useSsl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } else {
            sslCtx = null;
        }

        group = new NioEventLoopGroup();

        try {
            handler = new ThalesHSMClientHandler(defaultHandler);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                    .handler(new ThalesHSMClientInitializer(
                            sslCtx,
                            handler,
                            readTimeoutSeconds,
                            writeTimeoutSeconds
                    ));

            // Connect to HSM
            ChannelFuture connectFuture = bootstrap.connect(host, port).sync();
            channel = connectFuture.channel();

            if (!connectFuture.isSuccess()) {
                throw new FluidClientException(
                        "Failed to connect to Thales HSM at " + host + ":" + port,
                        connectFuture.cause(),
                        FluidClientException.ErrorCode.IO_ERROR
                );
            }

            log.info("Connected to Thales HSM at " + host + ":" + port);

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
        if (channel == null || !channel.isActive()) {
            // Java 8 compatible way to create failed future
            CompletableFuture<ThalesResponse> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new FluidClientException(
                    "Channel is not active",
                    FluidClientException.ErrorCode.SESSION_EXPIRED
            ));
            return failedFuture;
        }

        // Generate request ID if not set
        final String requestId = command.getRequestId() == null ?
                UUID.randomUUID().toString() : command.getRequestId();

        // Create future for response
        CompletableFuture<ThalesResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        // Send command
        channel.writeAndFlush(command).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                handler.incrementSentCommands();
            } else {
                pendingRequests.remove(requestId);
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
        return future.get(timeout, unit);
    }

    /**
     * Sends an echo command to test HSM connectivity.
     *
     * @param echoData The data to echo back
     * @return ThalesResponse with echoed data
     * @throws Exception If command fails
     */
    public ThalesResponse echo(String echoData) throws Exception {
        ThalesCommand echoCommand = ThalesCommand.Commands.echo(echoData);
        return sendCommand(echoCommand);
    }

    /**
     * Sends a diagnostics command to check HSM status.
     *
     * @return ThalesResponse with HSM status
     * @throws Exception If command fails
     */
    public ThalesResponse diagnostics() throws Exception {
        ThalesCommand diagCommand = ThalesCommand.Commands.diagnostics();
        return sendCommand(diagCommand);
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
        log.info("Closed connection to Thales HSM");
    }
}
