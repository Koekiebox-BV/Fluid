/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1.netty;

import com.fluidbpm.ws.client.v1.netty.websocket.WebSocketClient;
import com.fluidbpm.ws.client.v1.websocket.IMessageResponseHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Test the Netty WebSocket client implementation.
 */
@Log
public class TestNettyWebSocketClient extends TestCase {

    /**
     * Test basic connectivity and text message sending/receiving.
     * This test connects to a public WebSocket echo server.
     *
     * NOTE: This test is ignored by default as it depends on an external service.
     * Remove @Ignore annotation to run it manually.
     */
    /*@Ignore("Disabled: Depends on external echo.websocket.org service which may be unreliable")
    @Test(timeout = 15000)
    public void testTextMessageEcho() throws Exception {
        // Using a public WebSocket echo server for testing
        URI uri = new URI("wss://echo.websocket.org/");
        CountDownLatch messageLatch = new CountDownLatch(1);
        CountDownLatch connectionLatch = new CountDownLatch(1);
        final String[] receivedMessage = new String[1];
        final boolean[] connectionClosed = new boolean[1];

        // Create message handler
        Map<String, TestMessageHandler> handlers = new HashMap<>();
        TestMessageHandler handler = new TestMessageHandler(messageLatch, receivedMessage, connectionLatch, connectionClosed);
        handlers.put("test", handler);

        WebSocketClient<TestMessageHandler> client = null;
        try {
            // Create WebSocket client
            client = new WebSocketClient<>(
                    uri,
                    handlers,
                    WebSocketClient.Mode.Text,
                    0
            );

            // Verify connection is open
            assertTrue("WebSocket connection should be open", client.isSessionOpen());
            assertNotNull("Session ID should not be null", client.getSessionId());

            // Send a test message
            String testMessage = "{\"test\":\"Hello Netty WebSocket\"}";
            client.sendMessage(testMessage);

            log.info("Sent message: " + testMessage);

            // Wait for response or connection close
            boolean received = messageLatch.await(5, TimeUnit.SECONDS);

            // Check if connection was closed prematurely
            if (connectionClosed[0] && !received) {
                log.warning("Echo server closed connection before response - test skipped");
                // This is acceptable for public echo servers that may be unreliable
                return;
            }

            assertTrue("Should receive echo response within timeout", received);
            assertNotNull("Received message should not be null", receivedMessage[0]);

            // Verify message counts
            assertTrue("Should have sent at least 1 message", client.getSentMessages() >= 1);

        } catch (Exception e) {
            // If the public echo server is down, skip the test
            log.warning("Could not connect to echo server: " + e.getMessage() + " - test skipped");
            // Don't fail the test if the external service is unavailable
            return;
        } finally {
            // Close connection
            if (client != null) {
                client.closeSession();
                assertFalse("WebSocket connection should be closed", client.isSessionOpen());
            }
        }
    }*/

    /**
     * Test connection to invalid URI.
     */
    @Test
    public void testInvalidConnection() {
        try {
            URI uri = new URI("ws://invalid-host-that-does-not-exist.example.com:9999/");
            Map<String, TestMessageHandler> handlers = new HashMap<>();
            handlers.put("test", new TestMessageHandler(null, null));

            WebSocketClient<TestMessageHandler> client = new WebSocketClient<>(
                    uri,
                    handlers,
                    WebSocketClient.Mode.Text,
                    0
            );
            fail("Should throw exception for invalid connection");
        } catch (Exception e) {
            // Expected exception
            assertTrue("Exception should be thrown for invalid connection", true);
        }
    }

    /**
     * Test message handler for testing purposes.
     */
    private static class TestMessageHandler implements IMessageResponseHandler {
        private final CountDownLatch latch;
        private final String[] messageContainer;
        private final CountDownLatch connectionLatch;
        private final boolean[] connectionClosed;

        public TestMessageHandler(CountDownLatch latch, String[] messageContainer,
                                  CountDownLatch connectionLatch, boolean[] connectionClosed) {
            this.latch = latch;
            this.messageContainer = messageContainer;
            this.connectionLatch = connectionLatch;
            this.connectionClosed = connectionClosed;
        }

        public TestMessageHandler(CountDownLatch latch, String[] messageContainer) {
            this(latch, messageContainer, null, null);
        }

        @Override
        public void handleMessage(Object messageObj) {
            if (messageObj instanceof JsonObject) {
                JsonObject json = (JsonObject) messageObj;
                if (messageContainer != null) {
                    messageContainer[0] = json.toString();
                }
                if (latch != null) {
                    latch.countDown();
                }
            }
        }

        @Override
        public Object doesHandlerQualifyForProcessing(String message) {
            try {
                JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public Object doesHandlerQualifyForProcessing(byte[] message) {
            // For binary messages, return null as this handler only processes text
            return null;
        }

        @Override
        public void connectionClosed() {
            log.info("Connection closed");
            if (connectionClosed != null && connectionClosed.length > 0) {
                connectionClosed[0] = true;
            }
            if (connectionLatch != null) {
                connectionLatch.countDown();
            }
        }
    }
}
