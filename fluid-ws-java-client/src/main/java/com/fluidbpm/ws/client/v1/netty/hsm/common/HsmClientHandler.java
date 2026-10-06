package com.fluidbpm.ws.client.v1.netty.hsm.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Netty channel handler for processing HSM responses of any vendor.
 * This handler manages the lifecycle of HSM communication and routes
 * responses to the appropriate response handlers.
 *
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
@Log
public class HsmClientHandler<R extends HsmResponse> extends SimpleChannelInboundHandler<R> {

    private final String displayName;
    private final Map<String, IHsmResponseHandler<R>> responseHandlers;
    private final IHsmResponseHandler<R> defaultHandler;

    private final AtomicInteger sentCommands = new AtomicInteger();
    private final AtomicInteger receivedResponses = new AtomicInteger();

    /**
     * Constructs a handler with per-request handlers and a default handler.
     *
     * @param responseType The concrete response class (Netty inbound type matcher)
     * @param displayName Vendor name for log messages
     * @param responseHandlers Map of request id to response handler
     * @param defaultHandler Default handler for responses without a registered id
     */
    public HsmClientHandler(
            Class<R> responseType,
            String displayName,
            Map<String, IHsmResponseHandler<R>> responseHandlers,
            IHsmResponseHandler<R> defaultHandler
    ) {
        super(responseType);
        this.displayName = displayName;
        this.responseHandlers = responseHandlers != null ? responseHandlers : new ConcurrentHashMap<>();
        this.defaultHandler = defaultHandler;
    }

    /**
     * Constructs a handler with a default handler only.
     *
     * @param responseType The concrete response class
     * @param displayName Vendor name for log messages
     * @param defaultHandler Default handler for all responses
     */
    public HsmClientHandler(Class<R> responseType, String displayName, IHsmResponseHandler<R> defaultHandler) {
        this(responseType, displayName, new ConcurrentHashMap<>(), defaultHandler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connected to " + displayName + ": " + ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Disconnected from " + displayName + ": " + ctx.channel().remoteAddress());

        // Notify all handlers of connection closure
        if (defaultHandler != null) {
            defaultHandler.connectionClosed();
        }
        responseHandlers.values().forEach(IHsmResponseHandler::connectionClosed);
        responseHandlers.clear();

        ctx.fireChannelInactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, R response) {
        this.receivedResponses.incrementAndGet();

        String requestId = response.getRequestId();

        // Try to find specific handler for this request id
        IHsmResponseHandler<R> handler = null;
        if (requestId != null) {
            handler = responseHandlers.remove(requestId);
        }

        // Fall back to default handler
        if (handler == null) {
            handler = defaultHandler;
        }

        // Handle the response
        if (handler != null) {
            try {
                handler.handleResponse(response);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error handling HSM response: " + e.getMessage(), e);
                handler.handleError(e);
            }
        } else {
            log.warning("No handler found for HSM response: " + response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.warning(displayName + " connection idle (" + ((IdleStateEvent) evt).state() + "): "
                    + ctx.channel().remoteAddress());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.log(Level.SEVERE, "HSM communication error: " + cause.getMessage(), cause);

        // Notify handlers of error
        if (defaultHandler != null) {
            defaultHandler.handleError(cause);
        }
        responseHandlers.values().forEach(h -> h.handleError(cause));
        responseHandlers.clear();

        ctx.close();
    }

    /**
     * Registers a response handler for a specific request id.
     *
     * @param requestId The request id to track
     * @param handler The handler to call when the response arrives
     */
    public void registerHandler(String requestId, IHsmResponseHandler<R> handler) {
        if (requestId != null && handler != null) {
            responseHandlers.put(requestId, handler);
        }
    }

    /**
     * Gets the count of sent commands.
     *
     * @return The number of commands sent
     */
    public int getSentCommands() {
        return sentCommands.get();
    }

    /**
     * Gets the count of received responses.
     *
     * @return The number of responses received
     */
    public int getReceivedResponses() {
        return receivedResponses.get();
    }

    /**
     * Increments the sent commands counter.
     */
    public void incrementSentCommands() {
        this.sentCommands.incrementAndGet();
    }
}
