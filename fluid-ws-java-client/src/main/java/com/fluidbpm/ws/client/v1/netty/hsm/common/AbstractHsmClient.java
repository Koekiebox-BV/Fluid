package com.fluidbpm.ws.client.v1.netty.hsm.common;

import com.fluidbpm.ws.client.FluidClientException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Vendor-neutral Netty HSM client. Owns the connection, the TLS handshake, request/response
 * correlation, timeouts and counters; the vendor plugs in its codecs and request-id rules
 * through an {@link HsmProtocol}.
 *
 * Correlation: every command sent with a request id is matched to the response that echoes
 * the same id, so commands may be pipelined on one connection and responses may arrive in any
 * order. If the protocol does not require an id, untagged commands are matched in send order.
 *
 * @param <C> The vendor command type
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
@Log
public abstract class AbstractHsmClient<C extends HsmCommand, R extends HsmResponse> implements AutoCloseable {

    private final Channel channel;
    private final EventLoopGroup group;
    private final HsmClientHandler<R> handler;
    private final PendingRequests<R> pending = new PendingRequests<>();

    private final HsmClientConfig config;
    private final HsmProtocol<C, R> protocol;

    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final boolean useSsl;

    private final IHsmResponseHandler<R> defaultHandler = new IHsmResponseHandler<R>() {
        @Override
        public void handleResponse(R response) {
            if (!pending.complete(response)) {
                log.warning("Received response without matching request: " + response);
            }
        }

        @Override
        public void connectionClosed() {
            log.info("HSM connection closed");
            pending.failAll(new FluidClientException(
                    "Connection closed",
                    FluidClientException.ErrorCode.IO_ERROR
            ));
        }

        @Override
        public void handleError(Throwable error) {
            log.severe("HSM error: " + error.getMessage());
            pending.failAll(error);
        }
    };

    /**
     * Validates the configuration and connects to the HSM.
     *
     * @param config Connection configuration
     * @param protocol Vendor protocol
     * @throws FluidClientException If the configuration is invalid or the connection fails
     */
    protected AbstractHsmClient(HsmClientConfig config, HsmProtocol<C, R> protocol) {
        config.validate();
        this.config = config;
        this.protocol = protocol;
        this.host = config.getHost();
        this.port = config.getPort();
        this.useSsl = config.isTls();

        this.group = new NioEventLoopGroup();
        try {
            this.handler = new HsmClientHandler<>(protocol.responseType(), protocol.displayName(), defaultHandler);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
                    .handler(new HsmClientInitializer<>(
                            config.getSslContext(),
                            config.isVerifyHostname(),
                            host,
                            port,
                            protocol::newCodecHandlers,
                            handler,
                            config.getReadTimeoutSeconds(),
                            config.getWriteTimeoutSeconds()
                    ));

            // Connect to HSM
            ChannelFuture connectFuture = bootstrap.connect(host, port).sync();
            if (!connectFuture.isSuccess()) {
                throw new FluidClientException(
                        "Failed to connect to " + protocol.displayName() + " at " + host + ":" + port,
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

            log.info("Connected to " + protocol.displayName() + " at " + host + ":" + port
                    + (useSsl ? " (TLS)" : "") + describeConnection());

        } catch (Exception e) {
            group.shutdownGracefully();
            throw new FluidClientException(
                    "Failed to initialize " + protocol.displayName() + " client: " + e.getMessage(),
                    e,
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }
    }

    /**
     * Extra detail appended to the "connected" log line, e.g. ", header length 4".
     *
     * @return Detail text (may be empty)
     */
    protected String describeConnection() {
        return "";
    }

    /**
     * Gets the configuration this client was built from.
     *
     * @return The configuration
     */
    public HsmClientConfig getConfig() {
        return config;
    }

    /**
     * Gets the vendor protocol in use.
     *
     * @return The protocol
     */
    protected HsmProtocol<C, R> getProtocol() {
        return protocol;
    }

    /**
     * Sends a command to the HSM asynchronously.
     *
     * @param command The command to send
     * @return CompletableFuture that will contain the response
     */
    public CompletableFuture<R> sendCommandAsync(C command) {
        CompletableFuture<R> future = new CompletableFuture<>();
        if (channel == null || !channel.isActive()) {
            future.completeExceptionally(new FluidClientException(
                    "Channel is not active",
                    FluidClientException.ErrorCode.SESSION_EXPIRED
            ));
            return future;
        }

        try {
            registerPending(command, future);
        } catch (RuntimeException e) {
            future.completeExceptionally(e);
            return future;
        }

        channel.writeAndFlush(command).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                handler.incrementSentCommands();
            } else {
                pending.remove(command.getRequestId(), future);
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
     * Records the pending future for the command. A caller-supplied request id is honoured if
     * the protocol accepts it and it is not already in flight; otherwise a unique id is
     * allocated, unless the protocol allows untagged commands, which are queued in send order.
     */
    private void registerPending(C command, CompletableFuture<R> future) {
        String requested = command.getRequestId();
        if (requested != null && !requested.isEmpty()) {
            protocol.validateRequestId(requested);
            pending.register(requested, future);
            return;
        }
        if (!protocol.requiresRequestId()) {
            pending.addUntagged(future);
            return;
        }
        String id = pending.allocate(future, protocol::nextRequestId);
        protocol.assignRequestId(command, id);
    }

    /**
     * Sends a command to the HSM synchronously with default timeout.
     *
     * @param command The command to send
     * @return Response from the HSM
     * @throws Exception If command fails or times out
     */
    public R sendCommand(C command) throws Exception {
        return sendCommand(command, 30, TimeUnit.SECONDS);
    }

    /**
     * Sends a command to the HSM synchronously with timeout.
     *
     * @param command The command to send
     * @param timeout Timeout value
     * @param unit Timeout unit
     * @return Response from the HSM
     * @throws Exception If command fails or times out
     */
    public R sendCommand(C command, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<R> future = sendCommandAsync(command);
        try {
            return future.get(timeout, unit);
        } catch (java.util.concurrent.TimeoutException e) {
            pending.remove(command.getRequestId(), future);
            future.cancel(true);
            throw e;
        }
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
        return pending.size();
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
        pending.failAll(new FluidClientException("Client closed", FluidClientException.ErrorCode.IO_ERROR));
        log.info("Closed connection to " + protocol.displayName() + " " + host + ":" + port);
    }
}
