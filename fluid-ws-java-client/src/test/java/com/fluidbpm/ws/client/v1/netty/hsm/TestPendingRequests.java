package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmResponse;
import com.fluidbpm.ws.client.v1.netty.hsm.common.PendingRequests;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for the vendor-neutral in-flight registry.
 */
public class TestPendingRequests {

    /** Minimal response carrying only a request id. */
    private static final class Resp implements HsmResponse {
        private final String id;
        Resp(String id) { this.id = id; }
        @Override public String getRequestId() { return id; }
        @Override public boolean isSuccess() { return true; }
        @Override public String getErrorCode() { return "00"; }
        @Override public String getErrorMessage() { return "ok"; }
        @Override public byte[] getRawResponse() { return new byte[0]; }
    }

    private static String fourHex(int seq) {
        return String.format("%04X", seq & 0xFFFF);
    }

    @Test
    public void matchesByIdRegardlessOfOrder() {
        PendingRequests<Resp> p = new PendingRequests<>();
        CompletableFuture<Resp> a = new CompletableFuture<>();
        CompletableFuture<Resp> b = new CompletableFuture<>();
        String idA = p.allocate(a, TestPendingRequests::fourHex);
        String idB = p.allocate(b, TestPendingRequests::fourHex);
        assertEquals("0000", idA);
        assertEquals("0001", idB);
        assertEquals(2, p.size());

        Resp rb = new Resp(idB);
        assertTrue(p.complete(rb));
        assertTrue(b.isDone());
        assertFalse(a.isDone());
        assertSame(rb, b.join());

        assertTrue(p.complete(new Resp(idA)));
        assertEquals(0, p.size());
    }

    @Test
    public void allocationSkipsIdsInFlight() {
        PendingRequests<Resp> p = new PendingRequests<>();
        p.register("0000", new CompletableFuture<>());
        // Allocation starts at sequence 0, which collides with the registered id and must be skipped.
        assertEquals("0001", p.allocate(new CompletableFuture<>(), TestPendingRequests::fourHex));
    }

    @Test
    public void allocationGivesUpWhenNoFreeId() {
        PendingRequests<Resp> p = new PendingRequests<>();
        p.register("X", new CompletableFuture<>());
        try {
            p.allocate(new CompletableFuture<>(), seq -> "X");
            fail("expected allocation failure");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("1 commands in flight"));
        }
    }

    @Test
    public void duplicateCallerIdIsRejected() {
        PendingRequests<Resp> p = new PendingRequests<>();
        p.register("DUP", new CompletableFuture<>());
        try {
            p.register("DUP", new CompletableFuture<>());
            fail("expected duplicate rejection");
        } catch (IllegalStateException expected) {
            // ok
        }
        assertEquals(1, p.size());
    }

    @Test
    public void untaggedResponsesMatchInSendOrder() {
        PendingRequests<Resp> p = new PendingRequests<>();
        CompletableFuture<Resp> first = new CompletableFuture<>();
        CompletableFuture<Resp> second = new CompletableFuture<>();
        p.addUntagged(first);
        p.addUntagged(second);

        Resp r1 = new Resp(null);
        assertTrue(p.complete(r1));
        assertSame(r1, first.join());
        assertFalse(second.isDone());

        // A tagged response that matches nothing also falls back to the queue
        Resp r2 = new Resp("unknown");
        assertTrue(p.complete(r2));
        assertSame(r2, second.join());

        assertFalse("nothing left to match", p.complete(new Resp(null)));
        assertEquals(0, p.size());
    }

    @Test
    public void removeReleasesSlotByIdOrQueue() {
        PendingRequests<Resp> p = new PendingRequests<>();
        CompletableFuture<Resp> tagged = new CompletableFuture<>();
        CompletableFuture<Resp> untagged = new CompletableFuture<>();
        p.register("T", tagged);
        p.addUntagged(untagged);
        assertEquals(2, p.size());

        p.remove("T", tagged);
        p.remove(null, untagged);
        assertEquals(0, p.size());

        // removing again, or with a different future, is a no-op
        p.remove("T", tagged);
        p.register("T", new CompletableFuture<>());
        p.remove("T", tagged);
        assertEquals(1, p.size());
    }

    @Test
    public void failAllCompletesEverythingExceptionally() throws Exception {
        PendingRequests<Resp> p = new PendingRequests<>();
        CompletableFuture<Resp> tagged = new CompletableFuture<>();
        CompletableFuture<Resp> untagged = new CompletableFuture<>();
        p.register("T", tagged);
        p.addUntagged(untagged);

        p.failAll(new IllegalStateException("closed"));
        assertEquals(0, p.size());
        for (CompletableFuture<Resp> f : new CompletableFuture[]{tagged, untagged}) {
            try {
                f.get();
                fail("future must have failed");
            } catch (ExecutionException e) {
                assertEquals("closed", e.getCause().getMessage());
            }
        }
    }
}
