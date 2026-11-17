package com.fluidbpm.ws.client.v1.netty.websocket;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.ASNMapperFactory;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.websocket.IMessageResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Netty-based WebSocket client for managing communication with a WebSocket server.
 * This implementation replaces the Tyrus-based approach with Netty for production use.
 *
 * @param <RespHandler> Type extending {@link IMessageResponseHandler} for handling server responses.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class WebSocketClient<RespHandler extends IMessageResponseHandler> {
    private Channel channel;
    private final EventLoopGroup group;
    private final NettyWebSocketClientHandler handler;
    private final long timeoutMillis;

    @Getter
    private final Mode mode;

    private ASNMapperFactory asnMapperFactory;

    public enum Mode {
        Text,
        Binary
    }

    /**
     * Constructs a WebSocketClient with the specified endpoint URI and message handlers.
     * Establishes a WebSocket connection to the remote server using Netty.
     *
     * @param endpointURIParam The URI of the WebSocket endpoint to connect to.
     * @param messageHandlersParam A map of message handlers, where the keys represent
     *                              specific message types and the values are responsible
     *                              for handling corresponding messages.
     * @throws Exception If there is an error establishing the WebSocket connection.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam
    ) throws Exception {
        this(endpointURIParam, messageHandlersParam, Mode.Text, ASNGlobal.Type.UNKNOWN);
    }

    /**
     * Default constructor with an endpoint.
     * Establishes a connection to remote using Netty.
     *
     * @param endpointURIParam The Endpoint URI.
     * @param messageHandlersParam Map of message handlers.
     * @param mode Mode to use.
     * @param requestAsn1Type ASN.1 type for the request.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam,
            Mode mode,
            int requestAsn1Type
    ) {
        this(endpointURIParam, messageHandlersParam, mode, requestAsn1Type, 30000);
    }

    /** Connect and complete the WebSocket handshake within the supplied timeout. */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam,
            Mode mode,
            int requestAsn1Type,
            long timeoutMillis
    ) {
        if (timeoutMillis <= 0) throw new IllegalArgumentException("WebSocket timeout must be positive.");
        this.timeoutMillis = timeoutMillis;
        this.mode = mode;
        this.asnMapperFactory = new ASNMapperFactory(requestAsn1Type);

        String scheme = endpointURIParam.getScheme() == null ? "ws" : endpointURIParam.getScheme();
        final String host = endpointURIParam.getHost() == null ? "127.0.0.1" : endpointURIParam.getHost();
        final int port;
        if (endpointURIParam.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = endpointURIParam.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Only WS(S) is supported.");
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            } catch (SSLException e) {
                throw new FluidClientException(
                        e.getMessage(),
                        e,
                        FluidClientException.ErrorCode.CRYPTOGRAPHY
                );
            }
        } else sslCtx = null;

        group = new NioEventLoopGroup();
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);

        try {
            HttpHeaders customHeaders = new DefaultHttpHeaders();
            customHeaders.add("Connection", "Upgrade");
            customHeaders.add("Upgrade", "websocket");

            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                    endpointURIParam,
                    WebSocketVersion.V13,
                    null,
                    true,
                    customHeaders,
                    1024 * 1024 * 1024 // 1GB max frame size
            );

            this.handler = new NettyWebSocketClientHandler(handshaker, messageHandlersParam, mode);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) Math.min(timeoutMillis, Integer.MAX_VALUE))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyWebSocketClientInitializer(sslCtx, handshaker, handler));

            ChannelFuture connected = bootstrap.connect(host, port);
            this.channel = connected.channel();
            awaitConnection(connected, deadline);
            awaitConnection(this.handler.handshakeFuture(), deadline);
        } catch (Exception e) {
            if (this.channel != null) this.channel.close();
            this.group.shutdownGracefully(0, timeoutMillis, TimeUnit.MILLISECONDS);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new FluidClientException("WebSocket connection interrupted.", e,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
            if (e instanceof FluidClientException) throw (FluidClientException) e;
            throw new FluidClientException(
                    // The full URI includes the service ticket; do not include it in errors.
                    "Failed to connect to WebSocket at " + host + ":" + port + ".",
                    e,
                    FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR
            );
        }
    }

    private static void awaitConnection(ChannelFuture future, long deadline) throws Exception {
        long remaining = deadline - System.nanoTime();
        if (remaining <= 0 || !future.await(remaining, TimeUnit.NANOSECONDS)) {
            throw new TimeoutException("WebSocket connection/handshake timed out.");
        }
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (cause instanceof Exception) throw (Exception) cause;
            throw new FluidClientException("WebSocket connection failed.", cause,
                    FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR);
        }
    }

    /**
     * Send a message.
     *
     * @param aFluidVo The JSON Object to send.
     */
    public void sendMessage(ABaseFluidVO aFluidVo) {
        if (aFluidVo == null) {
            throw new FluidClientException("No Object to send!", FluidClientException.ErrorCode.IO_ERROR);
        }

        if (UtilGlobal.isNotBlank(aFluidVo.getEcho())) {
            PerfStats.timedStart(aFluidVo.getEcho());
        }

        if (this.mode == Mode.Binary) {
            if (aFluidVo instanceof BaseTransmission) {
                BaseTransmission bt = (BaseTransmission) aFluidVo;

                // Do not write the Payload Populate to the host:
                this.asnMapperFactory.setSkipPPForEncDec(true);

                //System.out.println("------------");
                //System.out.println("Zool (1) -> "+bt.getRequestObject().getPath() + " -> "+bt.getTransmissionObject().getClass().getSimpleName());
                byte[] bytesToSend = this.asnMapperFactory.writeObjectForSend(bt);
                //System.out.println("Zool (2) -> "+bytesToSend.length);
                this.sendMessage(bytesToSend);
                //System.out.println("Zool (3) -> Sent! "+bt.getRequestObject().getPath());
            } else {
                byte[] bytesToSend = this.asnMapperFactory.writeObjectForSend(aFluidVo);
                this.sendMessage(bytesToSend);
            }
        } else if (aFluidVo instanceof ABaseFluidGSONObject) {
            ABaseFluidGSONObject casted = (ABaseFluidGSONObject) aFluidVo;
            this.sendMessage(casted.toJsonObject().toString());
        } else {
            throw new FluidClientException(
                    "Unable to process '" + aFluidVo + "'.",
                    FluidClientException.ErrorCode.ASN_1_ERROR
            );
        }
    }

    /**
     * Send a text message.
     *
     * @param messageToSend The text message to send.
     */
    public void sendMessage(String messageToSend) {
        if (channel == null || !channel.isActive()) {
            throw new FluidClientException(
                    "Channel is not set or not active. Verify if connection is open.",
                    FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR
            );
        }

        observeWrite(channel.writeAndFlush(new TextWebSocketFrame(messageToSend)));
        handler.incrementSentMessages();
    }

    /**
     * Send a binary message.
     *
     * @param messageToSend The binary message to send.
     */
    public void sendMessage(byte[] messageToSend) {
        if (channel == null || !channel.isActive()) {
            throw new FluidClientException(
                    "(send-binary) Channel is not set or not active. Verify if connection is open.",
                    FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR
            );
        }

        PerfStats.increment(PerfStats.Label.Asn1Der_BytesSent, messageToSend.length);
        observeWrite(channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageToSend))));
        handler.incrementSentMessages();
    }

    private void observeWrite(ChannelFuture written) {
        written.addListener(future -> {
            if (!future.isSuccess()) {
                written.channel().pipeline().fireExceptionCaught(future.cause());
            }
        });
    }

    /**
     * Closes the WebSocket connection.
     */
    public void closeSession() {
        if (channel != null && channel.isActive()) {
            // Closing must also complete when the peer never acknowledges the close frame.
            channel.writeAndFlush(new CloseWebSocketFrame()).addListener(future -> channel.close());
            if (!channel.eventLoop().inEventLoop()
                    && !channel.closeFuture().awaitUninterruptibly(Math.min(timeoutMillis, 1000))) {
                channel.close();
            }
        }
        if (group != null) {
            group.shutdownGracefully(0, Math.min(timeoutMillis, 1000), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Check to see whether the session is open.
     *
     * @return {@code true} if session is open, otherwise {@code false}.
     */
    public boolean isSessionOpen() {
        return channel != null && channel.isActive();
    }

    /**
     * Return the current session id.
     *
     * @return {@code Session ID} if session is open, otherwise {@code null}.
     */
    public String getSessionId() {
        if (channel == null) return null;
        return channel.id().asLongText();
    }

    /**
     * Gets the count of sent messages.
     *
     * @return The number of messages sent
     */
    public int getSentMessages() {
        return handler != null ? handler.getSentMessages() : 0;
    }

    /**
     * Gets the count of received messages.
     *
     * @return The number of messages received
     */
    public int getReceivedMessages() {
        return handler != null ? handler.getReceivedMessages() : 0;
    }

    /**
     * Sets the type of the ASN (Abstract Syntax Notation) mapper factory.
     * If the {@code asnMapperFactory} is {@code null}, the method does nothing.
     *
     * @param type an integer representing the desired configuration type
     *             for the {@code asnMapperFactory}.
     */
    public void setAsnMapperFactoryType(int type) {
        if (this.asnMapperFactory == null) return;
        this.asnMapperFactory.setType(type);
    }

    /**
     * Sets the provided {@code PayloadPopulate} instance on the {@code asnMapperFactory}.
     * If the {@code asnMapperFactory} is {@code null}, the method does nothing.
     *
     * @param payloadPopulate An instance of {@code PayloadPopulate} to be set on the {@code asnMapperFactory}.
     */
    public void setPPOnAsnFactory(PayloadPopulate payloadPopulate) {
        if (this.asnMapperFactory == null) return;
        this.asnMapperFactory.setPayloadPopulate(payloadPopulate);
    }
}
