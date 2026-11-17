package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.websocket.*;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetTableFormsClient;
import com.google.gson.*;
import org.junit.Test;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.Assert.*;

public class TestWebSocketTransportContract {
    @Test(timeout = 10000) public void closedChannelIsTransportFailureForTextAndBinary() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> client = new WebSocketClient<>(
                    URI.create(server.endpoint().replace("http:", "ws:")), Collections.emptyMap());
            client.closeSession();
            for (Runnable send : Arrays.<Runnable>asList(
                    () -> client.sendMessage("{}"), () -> client.sendMessage(new byte[]{1}))) {
                try { send.run(); fail("Expected closed transport"); }
                catch (FluidClientException expected) {
                    assertEquals(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, expected.getErrorCode());
                }
            }
        }
    }

    @Test(timeout = 10000) public void silentHandshakeTimesOutWithoutExposingTicket() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            server.stallHandshake = true;
            long start = System.nanoTime();
            try {
                new WebSocketClient<>(URI.create(server.endpoint().replace("http:", "ws:") + "secret-ticket"),
                        Collections.emptyMap(), WebSocketClient.Mode.Text, ASNGlobal.Type.UNKNOWN, 150);
                fail("Expected timeout");
            } catch (FluidClientException expected) {
                assertEquals(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, expected.getErrorCode());
                assertFalse(expected.getMessage().contains("secret-ticket"));
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 3000);
            }
        }
    }

    @Test(timeout = 10000) public void closeDuringHandshakeFailsPromptly() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            server.closeDuringHandshake = true;
            long start = System.nanoTime();
            try {
                new WebSocketClient<>(URI.create(server.endpoint().replace("http:", "ws:")),
                        Collections.emptyMap(), WebSocketClient.Mode.Text, ASNGlobal.Type.UNKNOWN, 5000);
                fail("Expected handshake failure");
            } catch (FluidClientException expected) {
                assertEquals(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, expected.getErrorCode());
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 3000);
            }
        }
    }

    @Test(timeout = 10000) public void failedSendReleasesRequestAndEchoHandlers() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            class TableClient extends SQLUtilWebSocketGetTableFormsClient {
                TableClient() { super(server.endpoint(), null, "010203", 1000, true, null, false, "", WebSocketClient.Mode.Text); }
                void disconnect() { this.webSocketClient.closeSession(); }
            }
            try (TableClient client = new TableClient()) {
                client.disconnect();
                try { client.getTableFormsSynchronized(new Form(203L)); fail("Expected failed send"); }
                catch (FluidClientException expected) {
                    assertEquals(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, expected.getErrorCode());
                }
                for (String name : Arrays.asList("messageHandlerByRequestId", "messageHandlerByEcho", "requestIdToEchoes")) {
                    java.lang.reflect.Field field = ABaseClientWebSocket.class.getDeclaredField(name);
                    field.setAccessible(true);
                    assertTrue(name, ((Map<?, ?>) field.get(client)).isEmpty());
                }
            }
        }
    }

    @Test(timeout = 30000) public void textAndVoMessagesMaintainSessionAndCounters() throws Exception {
        BlockingQueue<Object> replies = new LinkedBlockingQueue<>();
        IMessageResponseHandler handler = new IMessageResponseHandler() {
            public Object doesHandlerQualifyForProcessing(String message) { return JsonParser.parseString(message); }
            public Object doesHandlerQualifyForProcessing(byte[] message) { return message; }
            public void handleMessage(Object message) { replies.add(message); }
            public void connectionClosed() { }
        };
        Map<String, IMessageResponseHandler> handlers = new ConcurrentHashMap<>();
        handlers.put("first", handler); handlers.put("second", handler);
        try (LocalWebSocketFixture server = new LocalWebSocketFixture()) {
            WebSocketClient<IMessageResponseHandler> client = new WebSocketClient<>(
                    URI.create(server.endpoint().replace("http:", "ws:")), handlers);
            try {
                assertTrue(client.isSessionOpen()); assertNotNull(client.getSessionId());
                assertEquals(0, client.getSentMessages()); assertEquals(0, client.getReceivedMessages());
                client.setAsnMapperFactoryType(ASNGlobal.Type.FORM);
                client.setPPOnAsnFactory(new PayloadPopulate());
                client.sendMessage("{\"echo\":\"first\"}");
                JsonObject first = (JsonObject)replies.poll(3, TimeUnit.SECONDS);
                assertNotNull(first); assertEquals("first", first.get("echo").getAsString());
                Form form = new Form(7L); form.setEcho("second");
                client.sendMessage(form);
                JsonObject second = (JsonObject)replies.poll(3, TimeUnit.SECONDS);
                assertNotNull(second); assertEquals("second", second.get("echo").getAsString());
                assertEquals(2, client.getSentMessages()); assertEquals(2, client.getReceivedMessages());
                handlers.put("binary", handler);
                Form binary = new Form(9L); binary.setEcho("binary");
                byte[] encoded = new com.fluidbpm.ws.client.v1.asn1der.ASNMapperFactory(ASNGlobal.Type.FORM).writeObjectForSend(binary);
                client.sendMessage(encoded);
                Object binaryReply = replies.poll(3, TimeUnit.SECONDS);
                assertNotNull(binaryReply); assertArrayEquals(encoded, (byte[])binaryReply);
                assertEquals(3, client.getSentMessages()); assertEquals(3, client.getReceivedMessages());
                assertEquals(7L, server.requests.get(1).get("id").getAsLong());
            } finally { client.closeSession(); }
            assertFalse(client.isSessionOpen());
        }
    }
}
