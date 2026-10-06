package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaHSMClient;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaHSMClientConfig;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Exercises request/response correlation against {@link FakeAtallaHsmServer}.
 */
public class TestAtallaHSMClientCorrelation {

    private static AtallaHSMClient connect(int port, boolean contextTags) {
        return new AtallaHSMClient(AtallaHSMClientConfig.builder()
                .host("127.0.0.1").port(port).contextTagEnabled(contextTags)
                .readTimeoutSeconds(0).writeTimeoutSeconds(0)
                .build());
    }

    @Test(timeout = 15000)
    public void echoRoundTrip() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            AtallaResponse r = client.echo("This is a test.");
            assertTrue(r.toString(), r.isSuccess());
            assertEquals("00", r.getResponseId());
            assertEquals("This is a test.", r.getEchoMessage());
            assertEquals(FakeAtallaHsmServer.VERSION, r.getSoftwareVersion());
            assertNotNull(r.getContextTag());
            assertEquals(0, client.getPendingCount());
            assertEquals(1, client.getSentCommands());
            assertEquals(1, client.getReceivedResponses());
        }
    }

    @Test(timeout = 15000)
    public void correlatesOutOfOrderResponsesByContextTag() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {

            AtallaCommand slow = AtallaCommand.Commands.echo("DELAY:400:SLOW");
            AtallaCommand fast = AtallaCommand.Commands.echo("FAST");
            CompletableFuture<AtallaResponse> f1 = client.sendCommandAsync(slow);
            CompletableFuture<AtallaResponse> f2 = client.sendCommandAsync(fast);

            assertNotEquals(slow.getContextTag(), fast.getContextTag());
            assertEquals(4, slow.getContextTag().length());

            AtallaResponse r2 = f2.get(5, TimeUnit.SECONDS);
            assertFalse("fast response must not wait for slow one", f1.isDone());
            AtallaResponse r1 = f1.get(5, TimeUnit.SECONDS);

            assertEquals("FAST", r2.getEchoMessage());
            assertEquals(fast.getContextTag(), r2.getContextTag());
            assertEquals("SLOW", r1.getEchoMessage());
            assertEquals(slow.getContextTag(), r1.getContextTag());
            assertEquals(0, client.getPendingCount());
            assertEquals(2, client.getSentCommands());
            assertEquals(2, client.getReceivedResponses());
        }
    }

    @Test(timeout = 15000)
    public void manyConcurrentCommandsAllMatch() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            List<CompletableFuture<AtallaResponse>> futures = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                futures.add(client.sendCommandAsync(AtallaCommand.Commands.echo("DELAY:" + (i % 5) * 20 + ":N" + i)));
            }
            for (int i = 0; i < 50; i++) {
                assertEquals("N" + i, futures.get(i).get(5, TimeUnit.SECONDS).getEchoMessage());
            }
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void fifoModeMatchesUntaggedResponsesInOrder() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), false)) {
            AtallaCommand cmd = AtallaCommand.Commands.echo("ONE");
            AtallaResponse r = client.sendCommand(cmd);
            assertNull("no tag must be sent in FIFO mode", cmd.getContextTag());
            assertNull(r.getContextTag());
            assertEquals("ONE", r.getEchoMessage());

            // Sequential commands keep working
            assertEquals("TWO", client.echo("TWO").getEchoMessage());
            assertEquals("2101", client.softwareVersion().getResponseId());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void untaggedResponseFallsBackToFifoWhenTagsEnabled() throws Exception {
        // An HSM that strips the tag (e.g. error response with binary data) still completes the request.
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), false)) {
            AtallaResponse r = client.sendCommand(AtallaCommand.Commands.echo("NOTAG:X"));
            assertEquals("X", r.getEchoMessage());
            assertNull(r.getContextTag());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void callerSuppliedTagIsUsedAndDuplicatesRejected() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            AtallaResponse r = client.sendCommand(new AtallaCommand("00", Collections.singletonList("X"), "ATM 325"));
            assertEquals("ATM 325", r.getContextTag());
            assertEquals("X", r.getEchoMessage());

            CompletableFuture<AtallaResponse> first = client.sendCommandAsync(
                    new AtallaCommand("00", Collections.singletonList("DELAY:300:A"), "DUP"));
            try {
                client.sendCommand(new AtallaCommand("00", Collections.singletonList("B"), "DUP"));
                fail("duplicate in-flight tag must be rejected");
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof IllegalStateException);
            }
            assertEquals("A", first.get(5, TimeUnit.SECONDS).getEchoMessage());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void otherCommandsAndErrorsAreDecoded() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            AtallaResponse version = client.softwareVersion();
            assertEquals("2101", version.getResponseId());
            assertTrue(version.getField(1).startsWith("Atalla HSM AT1000"));
            assertEquals("3", version.getField(3));

            AtallaResponse gen = client.sendCommand(new AtallaCommand("10", "3PUNE000", "", "D"));
            assertEquals("20", gen.getResponseId());
            assertEquals("3PUNE000", gen.getField(1));
            assertEquals("", gen.getField(2));
            assertEquals("D", gen.getField(3));

            AtallaResponse err = client.sendCommand(new AtallaCommand("99", "X"));
            assertFalse(err.isSuccess());
            assertEquals("23", err.getErrorCode());
            assertEquals("Invalid command or option", err.getErrorMessage());
            assertEquals("00", err.getErrorFieldNumber());
            assertEquals("201", err.getErrorDetail());
            assertNotNull("error responses carry the tag too", err.getContextTag());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void reassemblesChunkedResponseAndWorksWithoutCrlf() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer(false);
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            assertEquals("CHUNKED DATA", client.echo("CHUNK:CHUNKED DATA").getEchoMessage());
            assertEquals("NEXT", client.echo("NEXT").getEchoMessage());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void timeoutReleasesPendingSlot() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            try {
                client.sendCommand(AtallaCommand.Commands.echo("DROP"), 300, TimeUnit.MILLISECONDS);
                fail("expected timeout");
            } catch (TimeoutException expected) {
                // ok
            }
            assertEquals(0, client.getPendingCount());
            // connection still usable afterwards
            assertEquals("OK", client.echo("OK").getEchoMessage());
        }
    }

    @Test(timeout = 15000)
    public void connectionDropFailsPendingFutures() throws Exception {
        try (FakeAtallaHsmServer hsm = new FakeAtallaHsmServer();
             AtallaHSMClient client = connect(hsm.getPort(), true)) {
            CompletableFuture<AtallaResponse> pending = client.sendCommandAsync(AtallaCommand.Commands.echo("DROP"));
            client.sendCommandAsync(AtallaCommand.Commands.echo("CLOSE"));
            try {
                pending.get(5, TimeUnit.SECONDS);
                fail("pending future must fail when the connection drops");
            } catch (ExecutionException expected) {
                // ok
            }
            assertFalse(client.isConnected());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test
    public void configValidation() {
        try {
            AtallaHSMClientConfig.builder().host(" ").build().validate();
            fail("blank host must be rejected");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            AtallaHSMClientConfig.builder().host("h").port(70000).build().validate();
            fail("port out of range must be rejected");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        AtallaHSMClientConfig cfg = AtallaHSMClientConfig.builder().host("h").build();
        cfg.validate();
        assertEquals(AtallaHSMClientConfig.DEFAULT_PORT, cfg.getPort());
        assertTrue(cfg.isContextTagEnabled());
        assertFalse(cfg.isTls());
    }
}
