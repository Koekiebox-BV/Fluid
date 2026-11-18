package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.netty.websocket.WebSocketClient;
import com.fluidbpm.ws.client.v1.websocket.IMessageResponseHandler;
import com.google.gson.JsonParser;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/** Exercise the closing handshake over a real socket, including delayed and absent peer replies. */
public class TestWebSocketCloseContract {
    @Test(timeout = 10000)
    public void concurrentCloseWaitsForPeerAndSendsOnlyOneFrame() throws Exception {
        ExecutorService callers = Executors.newFixedThreadPool(2);
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            BlockingQueue<CloseRequest> closes = captureCloses(server);
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.emptyMap());
            try {
                Future<?> first = callers.submit(client::closeSession);
                CloseRequest close = nextClose(closes);
                Future<?> second = callers.submit(client::closeSession);
                assertStillWaiting(first);
                assertTrue("TCP must remain open until the peer can reply", close.ctx.channel().isActive());
                close.acknowledgeAndDisconnect();
                first.get(3, TimeUnit.SECONDS);
                second.get(3, TimeUnit.SECONDS);
                assertFalse(client.isSessionOpen());
                client.closeSession();
                assertTrue("Repeated close must not send another frame", closes.isEmpty());
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        } finally {
            callers.shutdownNow();
        }
    }

    @Test(timeout = 10000)
    public void closeFromResponseCallbackIsNonBlockingAndWaitsForPeer() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            BlockingQueue<CloseRequest> closes = captureCloses(server);
            AtomicReference<WebSocketClient<IMessageResponseHandler>> socket = new AtomicReference<>();
            CompletableFuture<Void> callback = new CompletableFuture<>();
            CountDownLatch disconnected = new CountDownLatch(1);
            AtomicInteger notifications = new AtomicInteger();
            IMessageResponseHandler handler = new IMessageResponseHandler() {
                public Object doesHandlerQualifyForProcessing(String message) { return JsonParser.parseString(message); }
                public Object doesHandlerQualifyForProcessing(byte[] message) { return null; }
                public void handleMessage(Object message) {
                    try {
                        socket.get().closeSession();
                        try {
                            socket.get().sendMessage("{}");
                            fail("Data must not be sent after the close frame");
                        } catch (FluidClientException expected) {
                            assertEquals(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, expected.getErrorCode());
                        }
                        callback.complete(null);
                    } catch (Throwable failure) {
                        callback.completeExceptionally(failure);
                    }
                }
                public void connectionClosed() { notifications.incrementAndGet(); disconnected.countDown(); }
            };
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.singletonMap("reply", handler));
            socket.set(client);
            try {
                client.sendMessage("{}");
                callback.get(3, TimeUnit.SECONDS);
                CloseRequest close = nextClose(closes);
                assertFalse("Callback must return before disconnect", disconnected.await(150, TimeUnit.MILLISECONDS));
                close.acknowledgeAndDisconnect();
                assertTrue(disconnected.await(3, TimeUnit.SECONDS));
                assertEquals(1, notifications.get());
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        }
    }

    @Test(timeout = 10000)
    public void unresponsivePeerIsClosedWithinBoundedTimeout() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            BlockingQueue<CloseRequest> closes = captureCloses(server);
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.emptyMap());
            try {
                long started = System.nanoTime();
                client.closeSession();
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
                assertTrue("Peer must be given time to acknowledge", elapsed >= 500);
                assertTrue("Unresponsive peer must not prevent shutdown", elapsed < 3000);
                nextClose(closes);
                assertTrue(channel(client).closeFuture().await(3, TimeUnit.SECONDS));
                assertFalse(client.isSessionOpen());
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        }
    }

    @Test(timeout = 10000)
    public void serverInitiatedCloseEchoesStatusAndReasonAndReleasesThreads() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            BlockingQueue<CloseRequest> closes = captureCloses(server);
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.emptyMap());
            try {
                server.sendClose(1001, "server restarting");
                CloseRequest reply = nextClose(closes);
                assertEquals(1001, reply.status);
                assertEquals("server restarting", reply.reason);
                reply.ctx.close().syncUninterruptibly();
                assertTrue(channel(client).closeFuture().await(3, TimeUnit.SECONDS));
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        }
    }

    @Test(timeout = 10000)
    public void failedCloseWriteReleasesChannelAndThreads() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.emptyMap());
            try {
                Channel channel = channel(client);
                // Fail the encoded close write at the transport boundary.
                channel.pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
                    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                        ReferenceCountUtil.release(msg);
                        promise.setFailure(new IOException("simulated close write failure"));
                    }
                });
                client.closeSession();
                assertTrue(channel.closeFuture().await(3, TimeUnit.SECONDS));
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        }
    }

    @Test(timeout = 10000)
    public void eventLoopCloseTimesOutEvenWhenCloseWriteNeverCompletes() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> client = client(server, Collections.emptyMap());
            try {
                Channel channel = channel(client);
                channel.pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
                    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                        // Simulate a socket that cannot flush; release the retained write when it is closed.
                        ctx.channel().closeFuture().addListener(closed -> {
                            ReferenceCountUtil.release(msg);
                            promise.tryFailure(new java.nio.channels.ClosedChannelException());
                        });
                    }
                });
                channel.eventLoop().submit(client::closeSession).get(3, TimeUnit.SECONDS);
                assertFalse("A stalled write must still have a grace period", channel.closeFuture().await(150, TimeUnit.MILLISECONDS));
                assertTrue("The event loop must enforce the close timeout", channel.closeFuture().await(3, TimeUnit.SECONDS));
                assertGroupTerminated(client);
            } finally {
                client.closeSession();
            }
        }
    }

    private static WebSocketClient<IMessageResponseHandler> client(LocalWebSocketFixture server,
            java.util.Map<String, IMessageResponseHandler> handlers) throws Exception {
        return new WebSocketClient<>(URI.create(server.endpoint().replace("http:", "ws:")), handlers);
    }

    private static BlockingQueue<CloseRequest> captureCloses(LocalWebSocketFixture server) {
        BlockingQueue<CloseRequest> closes = new LinkedBlockingQueue<>();
        server.closeHandler = (ctx, frame) -> closes.add(new CloseRequest(ctx, frame));
        return closes;
    }

    private static CloseRequest nextClose(BlockingQueue<CloseRequest> closes) throws InterruptedException {
        CloseRequest close = closes.poll(3, TimeUnit.SECONDS);
        assertNotNull("Expected a WebSocket close frame", close);
        return close;
    }

    private static void assertStillWaiting(Future<?> close) throws Exception {
        try {
            close.get(150, TimeUnit.MILLISECONDS);
            fail("Client closed TCP before the peer's close reply");
        } catch (TimeoutException expected) {
            // The peer intentionally has not replied yet.
        }
    }

    private static Channel channel(WebSocketClient<?> client) throws Exception {
        Field field = WebSocketClient.class.getDeclaredField("channel");
        field.setAccessible(true);
        return (Channel) field.get(client);
    }

    private static void assertGroupTerminated(WebSocketClient<?> client) throws Exception {
        Field field = WebSocketClient.class.getDeclaredField("group");
        field.setAccessible(true);
        EventLoopGroup group = (EventLoopGroup) field.get(client);
        assertTrue("Connection threads must be released", group.terminationFuture().await(3, TimeUnit.SECONDS));
    }

    private static final class CloseRequest {
        final ChannelHandlerContext ctx;
        final int status;
        final String reason;

        CloseRequest(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
            this.ctx = ctx;
            this.status = frame.statusCode();
            this.reason = frame.reasonText();
        }

        void acknowledgeAndDisconnect() {
            CloseWebSocketFrame response = status < 0 ? new CloseWebSocketFrame() : new CloseWebSocketFrame(status, reason);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
