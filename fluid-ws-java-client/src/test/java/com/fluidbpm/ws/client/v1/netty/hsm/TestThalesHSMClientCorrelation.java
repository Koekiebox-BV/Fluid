package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesHSMClient;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesHSMClientConfig;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Exercises request/response correlation against {@link FakeThalesHsmServer}.
 */
public class TestThalesHSMClientCorrelation {

    private static ThalesHSMClient connect(int port, int headerLength) {
        return new ThalesHSMClient(ThalesHSMClientConfig.builder()
                .host("127.0.0.1").port(port).headerLength(headerLength)
                .readTimeoutSeconds(0).writeTimeoutSeconds(0)
                .build());
    }

    @Test(timeout = 15000)
    public void correlatesOutOfOrderResponsesByHeader() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {

            ThalesCommand slow = ThalesCommand.Commands.echo("DELAY:400:SLOW");
            ThalesCommand fast = ThalesCommand.Commands.echo("FAST");
            CompletableFuture<ThalesResponse> f1 = client.sendCommandAsync(slow);
            CompletableFuture<ThalesResponse> f2 = client.sendCommandAsync(fast);

            assertNotEquals(slow.getRequestId(), fast.getRequestId());
            assertEquals(4, slow.getRequestId().length());

            ThalesResponse r2 = f2.get(5, TimeUnit.SECONDS);
            assertFalse("fast response must not wait for slow one", f1.isDone());
            ThalesResponse r1 = f1.get(5, TimeUnit.SECONDS);

            assertEquals("FAST", r2.getResponseData());
            assertEquals(fast.getRequestId(), r2.getHeader());
            assertEquals("SLOW", r1.getResponseData());
            assertEquals(slow.getRequestId(), r1.getHeader());
            assertEquals(0, client.getPendingCount());
            assertEquals(2, client.getSentCommands());
            assertEquals(2, client.getReceivedResponses());
        }
    }

    @Test(timeout = 15000)
    public void manyConcurrentCommandsAllMatch() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            List<CompletableFuture<ThalesResponse>> futures = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                futures.add(client.sendCommandAsync(ThalesCommand.Commands.echo("DELAY:" + (i % 5) * 20 + ":N" + i)));
            }
            for (int i = 0; i < 50; i++) {
                assertEquals("N" + i, futures.get(i).get(5, TimeUnit.SECONDS).getResponseData());
            }
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void fifoModeWithoutHeader() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(0);
             ThalesHSMClient client = connect(hsm.getPort(), 0)) {
            ThalesResponse a = client.echo("ONE");
            ThalesResponse b = client.echo("TWO");
            assertEquals("ONE", a.getResponseData());
            assertEquals("TWO", b.getResponseData());
            assertEquals(null, a.getRequestId());
            assertEquals(0, client.getPendingCount());
        }
    }

    @Test(timeout = 15000)
    public void callerSuppliedHeaderIsUsed() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            ThalesResponse r = client.sendCommand(new ThalesCommand("NO", "X", "ZZ99"));
            assertEquals("ZZ99", r.getHeader());
            assertEquals("X", r.getResponseData());

            try {
                client.sendCommand(new ThalesCommand("NO", "X", "TOO-LONG"));
                fail("wrong-length header must be rejected");
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        }
    }

    @Test(timeout = 15000)
    public void otherCommandCodesGetMatchingResponseCode() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            assertEquals("ND", client.diagnostics().getResponseCode());
            assertEquals("A1", client.sendCommand(new ThalesCommand("A0", "0FFFS")).getResponseCode());
        }
    }

    @Test(timeout = 15000)
    public void binaryResponseSurvives() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            byte[] data = client.echo("BIN").getResponseDataBytes();
            assertEquals(256, data.length);
            for (int i = 0; i < 256; i++) assertEquals((byte) i, data[i]);
        }
    }

    @Test(timeout = 15000)
    public void timeoutReleasesPendingSlot() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            try {
                client.sendCommand(ThalesCommand.Commands.echo("DROP"), 300, TimeUnit.MILLISECONDS);
                fail("expected timeout");
            } catch (TimeoutException expected) {
                // ok
            }
            assertEquals(0, client.getPendingCount());
            // connection still usable afterwards
            assertEquals("OK", client.echo("OK").getResponseData());
        }
    }

    @Test(timeout = 15000)
    public void connectionDropFailsPendingFutures() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4);
             ThalesHSMClient client = connect(hsm.getPort(), 4)) {
            CompletableFuture<ThalesResponse> pending = client.sendCommandAsync(ThalesCommand.Commands.echo("DROP"));
            client.sendCommandAsync(ThalesCommand.Commands.echo("CLOSE"));
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
}
