package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestBinaryTransmissionContract {
    @Test(timeout = 30000) public void payloadPopulateRoundTripsOverBinaryWebSocket() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture();
             WebSocketASNDERClient client = new WebSocketASNDERClient(server.endpoint(), "010203", 3000)) {
            assertNotNull(client.requestFullPayloadPopulate());
            assertNotNull(client.getNewHandlerInstance());
            assertNull(client.request(null));
            assertTrue(server.uri.contains("010203"));
        }
    }
}
