package com.fluidbpm.ws.client.v1.contract;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Real loopback WebSocket handshake and frames, with deterministic application replies. */
final class LocalWebSocketFixture implements AutoCloseable {
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup worker = new NioEventLoopGroup(1);
    private final Channel server;
    private final List<Channel> connections = Collections.synchronizedList(new ArrayList<>());
    final List<JsonObject> requests = Collections.synchronizedList(new ArrayList<>());
    final List<JsonObject> restRequests = Collections.synchronizedList(new ArrayList<>());
    final List<String> restUris = Collections.synchronizedList(new ArrayList<>());
    final AtomicInteger handshakes = new AtomicInteger();
    final AtomicInteger disconnectReads = new AtomicInteger();
    volatile BiConsumer<ChannelHandlerContext, CloseWebSocketFrame> closeHandler;
    volatile int handshakeStatus = 101;
    volatile boolean stallHandshake;
    volatile boolean closeDuringHandshake;
    volatile Integer errorCode;
    volatile Function<byte[], byte[]> binaryResponse;
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
                                if (msg instanceof CloseWebSocketFrame && closeHandler != null) {
                                    try {
                                        closeHandler.accept(ctx, (CloseWebSocketFrame) msg);
                                    } finally {
                                        ReferenceCountUtil.release(msg);
                                    }
                                    return;
                                }
                                if (msg instanceof FullHttpRequest) {
                                    FullHttpRequest request = (FullHttpRequest) msg;
                                    uri = request.uri();
                                    if ("websocket".equalsIgnoreCase(request.headers().get(HttpHeaderNames.UPGRADE))) {
                                        handshakes.incrementAndGet();
                                        if (closeDuringHandshake || stallHandshake) {
                                            ReferenceCountUtil.release(msg);
                                            if (closeDuringHandshake) ctx.close();
                                            return;
                                        }
                                        if (handshakeStatus != 101) {
                                            respondHttp(ctx, handshakeStatus, "{}");
                                            ReferenceCountUtil.release(msg);
                                            return;
                                        }
                                    } else {
                                        restUris.add(request.uri());
                                        JsonObject body = JsonParser.parseString(request.content().toString(StandardCharsets.UTF_8)).getAsJsonObject();
                                        restRequests.add(body);
                                        JsonObject response = response(body);
                                        respondHttp(ctx, 200, response.toString());
                                        ReferenceCountUtil.release(msg);
                                        return;
                                    }
                                }
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
                                    if (disconnectReads.getAndUpdate(count -> Math.max(0, count - 1)) > 0) {
                                        ctx.close();
                                        return;
                                    }
                                    JsonObject response = response(request);
                                    ctx.writeAndFlush(new TextWebSocketFrame(response.toString()));
                                } else if (frame instanceof BinaryWebSocketFrame) {
                                    if (binaryResponse == null) ctx.writeAndFlush(new BinaryWebSocketFrame(frame.content().retain()));
                                    else {
                                        byte[] bytes = new byte[frame.content().readableBytes()];
                                        frame.content().getBytes(frame.content().readerIndex(), bytes);
                                        ctx.writeAndFlush(new BinaryWebSocketFrame(io.netty.buffer.Unpooled.wrappedBuffer(binaryResponse.apply(bytes))));
                                    }
                                }
                            }
                            @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { failure = cause; ctx.close(); }
                        });
                    }
                }).bind("127.0.0.1", 0).sync().channel();
    }

    private JsonObject response(JsonObject request) {
        JsonObject response = JsonParser.parseString("{\"id\":917,\"listing\":[{\"id\":918,\"fieldName\":\"response\"}],\"listingCount\":1}").getAsJsonObject();
        response.add("echo", request.get("echo"));
        if (error || errorCode != null) {
            response.addProperty("errorCode", errorCode == null ? 1001 : errorCode);
            response.addProperty("errorMessage", "fixture failure");
        }
        return response;
    }

    private void respondHttp(ChannelHandlerContext ctx, int status, String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(status), Unpooled.wrappedBuffer(bytes));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    void sendClose(int status, String reason) {
        synchronized (connections) {
            for (Channel connection : connections) {
                connection.writeAndFlush(new CloseWebSocketFrame(status, reason)).syncUninterruptibly();
            }
        }
    }

    void disconnectAll() {
        List<Channel> snapshot;
        synchronized (connections) { snapshot = new ArrayList<>(connections); }
        for (Channel connection : snapshot) connection.close().syncUninterruptibly();
    }
    String endpoint() { return "http://127.0.0.1:" + ((InetSocketAddress)server.localAddress()).getPort() + "/"; }

    @Override public void close() {
        server.close().syncUninterruptibly();
        disconnectAll();
        worker.shutdownGracefully(100, 2000, java.util.concurrent.TimeUnit.MILLISECONDS).syncUninterruptibly();
        boss.shutdownGracefully(100, 2000, java.util.concurrent.TimeUnit.MILLISECONDS).syncUninterruptibly();
        if (failure != null) throw new AssertionError("WebSocket fixture failed", failure);
    }
}
