package com.fluidbpm.ws.client.v1.netty.hsm.common;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

/**
 * Tracks in-flight commands and matches responses to them.
 *
 * Commands that carry a request id are kept in a map keyed by that id; commands sent
 * without one (only when the protocol permits it) are queued in send order. A response is
 * matched by id first and, failing that, to the oldest untagged command.
 *
 * Thread-safe.
 *
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
public class PendingRequests<R extends HsmResponse> {

    /** How many ids to try before giving up on allocation. */
    public static final int ALLOCATION_ATTEMPTS = 64;

    private final Map<String, CompletableFuture<R>> byId = new ConcurrentHashMap<>();
    private final Deque<CompletableFuture<R>> untagged = new ConcurrentLinkedDeque<>();
    private final AtomicInteger sequence = new AtomicInteger();

    /**
     * Records a future under a caller-chosen request id.
     *
     * @param requestId The id sent with the command
     * @param future The future to complete when the response arrives
     * @throws IllegalStateException if the id is already in flight
     */
    public void register(String requestId, CompletableFuture<R> future) {
        if (byId.putIfAbsent(requestId, future) != null) {
            throw new IllegalStateException("Request id '" + requestId + "' is already in flight");
        }
    }

    /**
     * Allocates a request id that is not currently in flight and records the future under it.
     *
     * @param future The future to complete when the response arrives
     * @param idFactory Produces an id for a sequence number (see {@link HsmProtocol#nextRequestId(int)})
     * @return The allocated id
     * @throws IllegalStateException if no free id was found within {@link #ALLOCATION_ATTEMPTS}
     */
    public String allocate(CompletableFuture<R> future, IntFunction<String> idFactory) {
        for (int attempt = 0; attempt < ALLOCATION_ATTEMPTS; attempt++) {
            String id = idFactory.apply(sequence.getAndIncrement() & 0x7FFFFFFF);
            if (byId.putIfAbsent(id, future) == null) {
                return id;
            }
        }
        throw new IllegalStateException("Unable to allocate a free request id; " + byId.size() + " commands in flight");
    }

    /**
     * Queues a future for a command sent without a request id.
     *
     * @param future The future to complete when the next unmatched response arrives
     */
    public void addUntagged(CompletableFuture<R> future) {
        untagged.addLast(future);
    }

    /**
     * Removes a registration, by id if the command had one, otherwise from the FIFO queue.
     * Safe to call if the entry was already completed.
     *
     * @param requestId The id sent with the command, or {@code null}
     * @param future The future that was registered
     */
    public void remove(String requestId, CompletableFuture<R> future) {
        if (requestId != null && !requestId.isEmpty()) {
            byId.remove(requestId, future);
        } else {
            untagged.remove(future);
        }
    }

    /**
     * Completes the future matching the response.
     *
     * @param response The response received
     * @return {@code true} if a pending command was matched and completed
     */
    public boolean complete(R response) {
        String id = response.getRequestId();
        CompletableFuture<R> future = id == null ? null : byId.remove(id);
        if (future == null) {
            future = untagged.pollFirst();
        }
        if (future == null) {
            return false;
        }
        future.complete(response);
        return true;
    }

    /**
     * Fails every pending future with the given error and clears the registry.
     *
     * @param error The failure cause
     */
    public void failAll(Throwable error) {
        List<CompletableFuture<R>> toFail = new ArrayList<>(byId.values());
        byId.clear();
        CompletableFuture<R> f;
        while ((f = untagged.pollFirst()) != null) toFail.add(f);
        toFail.forEach(x -> x.completeExceptionally(error));
    }

    /**
     * @return Number of commands awaiting a response
     */
    public int size() {
        return byId.size() + untagged.size();
    }
}
