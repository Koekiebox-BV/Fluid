package com.fluidbpm.ws.client.v1.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;

/**
 * Channel initializer for setting up the Netty WebSocket client pipeline.
 * Configures SSL, HTTP codecs, and WebSocket handlers.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class NettyWebSocketClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;
    private final WebSocketClientHandshaker handshaker;
    private final NettyWebSocketClientHandler handler;

    /**
     * Constructs a NettyWebSocketClientInitializer.
     *
     * @param sslContext The SSL context for secure connections (can be null for ws://)
     * @param handshaker The WebSocket handshaker
     * @param handler The custom handler for WebSocket frames
     */
    public NettyWebSocketClientInitializer(
            SslContext sslContext,
            WebSocketClientHandshaker handshaker,
            NettyWebSocketClientHandler handler
    ) {
        this.sslContext = sslContext;
        this.handshaker = handshaker;
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 1024)); // 1GB max message size
        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
        pipeline.addLast(handler);
    }
}
