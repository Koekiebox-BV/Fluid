package com.fluidbpm.ws.client.v1.contract;

import com.google.gson.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import java.net.InetSocketAddress;
import java.util.*;

/** Real loopback WebSocket handshake and frames, with deterministic application replies. */
final class LocalWebSocketFixture implements AutoCloseable {
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup worker = new NioEventLoopGroup(1);
    private final Channel server;
    private final List<Channel> connections = Collections.synchronizedList(new ArrayList<>());
    final List<JsonObject> requests = Collections.synchronizedList(new ArrayList<>());
    volatile String uri;
    volatile boolean error;
    volatile Throwable failure;
    LocalWebSocketFixture() throws InterruptedException {
        server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override protected void initChannel(SocketChannel ch) {
                        connections.add(ch);
                        ch.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(65536));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                if (msg instanceof FullHttpRequest) uri = ((FullHttpRequest)msg).uri();
                                ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(WebSocketServerProtocolConfig.newBuilder()
                                .websocketPath("/").checkStartsWith(true).build()));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<WebSocketFrame>() {
                            @Override protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
                                if (frame instanceof TextWebSocketFrame) {
                                    JsonObject request = JsonParser.parseString(((TextWebSocketFrame)frame).text()).getAsJsonObject();
                                    requests.add(request);
                                    JsonObject response = JsonParser.parseString("{\"id\":917,\"listing\":[{\"id\":918}],\"listingCount\":1}").getAsJsonObject();
                                    response.add("echo", request.get("echo"));
                                    if (error) { response.addProperty("errorCode", 1001); response.addProperty("errorMessage", "fixture failure"); }
                                    ctx.writeAndFlush(new TextWebSocketFrame(response.toString()));
                                } else if (frame instanceof BinaryWebSocketFrame) {
                                    ctx.writeAndFlush(new BinaryWebSocketFrame(frame.content().retain()));
                                }
                            }
                            @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { failure = cause; ctx.close(); }
                        });
                    }
                }).bind("127.0.0.1", 0).sync().channel();
    }
    String endpoint() { return "http://127.0.0.1:" + ((InetSocketAddress)server.localAddress()).getPort() + "/"; }
    @Override public void close() {
        server.close().syncUninterruptibly();
        for (Channel connection : connections) connection.closeFuture().awaitUninterruptibly(3000);
        worker.shutdownGracefully(100, 2000, java.util.concurrent.TimeUnit.MILLISECONDS).syncUninterruptibly();
        boss.shutdownGracefully(100, 2000, java.util.concurrent.TimeUnit.MILLISECONDS).syncUninterruptibly();
        if (failure != null) throw new AssertionError("WebSocket fixture failed", failure);
    }
}
