package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flowitem.WebSocketSendToFlowClient.SendToFlowMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageResponseHandler;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/** Delivers events before the caller obtains its future, without timing or sleeps. */
public class TestWebSocketCompletionContract {
    @Test(timeout = 30000) public void replyBeforeGetFutureIsRetainedAndCompletesRequest() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> socket = new WebSocketClient<>(
                    URI.create(server.endpoint().replace("http:", "ws:")), Collections.emptyMap());
            try {
                SendToFlowMessageHandler handler = new SendToFlowMessageHandler(null, socket);
                handler.addExpectedMessage("early-reply");
                FluidItem reply = new FluidItem();
                reply.setId(917L);
                reply.setEcho("early-reply");

                // Reproduce the CI ordering: the reply arrives before the first getCF().
                handler.handleMessage(reply);

                assertTrue("An already received reply must complete the future", handler.getCF().isDone());
                assertEquals(1, handler.getCF().get(1, TimeUnit.SECONDS).size());
                assertSame(reply, handler.getCF().get().get(0));
                assertTrue(handler.getExpectedEchoMessagesBeforeComplete().isEmpty());
                assertSame(handler.getCF(), handler.getCF());
            } finally {
                socket.closeSession();
            }
        }
    }

    @Test(timeout = 30000) public void earlyBatchReplyDoesNotCompleteBeforeRemainingReplies() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> socket = new WebSocketClient<IMessageResponseHandler>(
                    URI.create(server.endpoint().replace("http:", "ws:")), Collections.emptyMap()) {
                @Override public int getSentMessages() { return 2; }
                @Override public int getReceivedMessages() { return 1; }
            };
            try {
                SendToFlowMessageHandler handler = new SendToFlowMessageHandler(null, socket);
                handler.addExpectedMessage("first");
                FluidItem first = new FluidItem();
                first.setEcho("first");
                handler.handleMessage(first);

                // The first reply beats registration of the next message in the batch.
                handler.addExpectedMessage("second");
                assertFalse("The second reply is still outstanding", handler.getCF().isDone());
                FluidItem second = new FluidItem();
                second.setEcho("second");
                handler.handleMessage(second);

                assertEquals(2, handler.getCF().get(1, TimeUnit.SECONDS).size());
                assertTrue(handler.getExpectedEchoMessagesBeforeComplete().isEmpty());
            } finally {
                socket.closeSession();
            }
        }
    }

    @Test(timeout = 30000) public void errorBeforeGetFutureCompletesExceptionally() throws Exception {
        SendToFlowMessageHandler handler = new SendToFlowMessageHandler(null, null);
        handler.handleMessage(new Error(1001, "early error"));

        assertTrue("An early error must not be lost", handler.getCF().isCompletedExceptionally());
        try {
            handler.getCF().get(1, TimeUnit.SECONDS);
            fail("Expected the original server error");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof FluidClientException);
            assertEquals(1001, ((FluidClientException)e.getCause()).getErrorCode());
            assertEquals("early error", e.getCause().getMessage());
        }
    }

    @Test(timeout = 30000) public void closeBeforeGetFutureUnblocksCaller() throws Exception {
        SendToFlowMessageHandler handler = new SendToFlowMessageHandler(null, null);
        handler.connectionClosed();

        assertTrue(handler.isConnectionClosed());
        assertTrue("An early close must unblock the caller", handler.getCF().isDone());
        assertTrue(handler.getCF().get(1, TimeUnit.SECONDS).isEmpty());
    }
}
