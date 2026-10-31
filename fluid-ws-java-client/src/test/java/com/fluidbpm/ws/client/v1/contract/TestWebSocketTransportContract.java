package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.websocket.*;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.google.gson.*;
import org.junit.Test;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.Assert.*;

public class TestWebSocketTransportContract {
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
