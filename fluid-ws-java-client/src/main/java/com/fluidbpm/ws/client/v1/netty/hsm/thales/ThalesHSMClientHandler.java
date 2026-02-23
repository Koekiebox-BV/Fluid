package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty channel handler for processing Thales HSM responses.
 * This handler manages the lifecycle of HSM communication and routes
 * responses to the appropriate response handlers.
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Log
public class ThalesHSMClientHandler extends SimpleChannelInboundHandler<ThalesResponse> {

    private final Map<String, IThalesResponseHandler> responseHandlers;
    private final IThalesResponseHandler defaultHandler;

    private int sentCommands = 0;
    private int receivedResponses = 0;

    /**
     * Constructs a ThalesHSMClientHandler with response handlers.
     *
     * @param responseHandlers Map of request ID to response handler
     * @param defaultHandler Default handler for responses without a request ID
     */
    public ThalesHSMClientHandler(
            Map<String, IThalesResponseHandler> responseHandlers,
            IThalesResponseHandler defaultHandler
    ) {
        this.responseHandlers = responseHandlers != null ? responseHandlers : new ConcurrentHashMap<>();
        this.defaultHandler = defaultHandler;
    }

    /**
     * Constructs a ThalesHSMClientHandler with a default handler only.
     *
     * @param defaultHandler Default handler for all responses
     */
    public ThalesHSMClientHandler(IThalesResponseHandler defaultHandler) {
        this(new ConcurrentHashMap<>(), defaultHandler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connected to Thales HSM: " + ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Disconnected from Thales HSM: " + ctx.channel().remoteAddress());

        // Notify all handlers of connection closure
        if (defaultHandler != null) {
            defaultHandler.connectionClosed();
        }
        responseHandlers.values().forEach(IThalesResponseHandler::connectionClosed);

        ctx.fireChannelInactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ThalesResponse response) {
        this.receivedResponses++;

        String requestId = response.getRequestId();

        // Try to find specific handler for this request ID
        IThalesResponseHandler handler = null;
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
                log.severe("Error handling HSM response: " + e.getMessage());
                handler.handleError(e);
            }
        } else {
            log.warning("No handler found for HSM response: " + response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.severe("HSM communication error: " + cause.getMessage());
        cause.printStackTrace();

        // Notify handlers of error
        if (defaultHandler != null) {
            defaultHandler.handleError(cause);
        }
        responseHandlers.values().forEach(h -> h.handleError(cause));

        ctx.close();
    }

    /**
     * Registers a response handler for a specific request ID.
     *
     * @param requestId The request ID to track
     * @param handler The handler to call when the response arrives
     */
    public void registerHandler(String requestId, IThalesResponseHandler handler) {
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
        return sentCommands;
    }

    /**
     * Gets the count of received responses.
     *
     * @return The number of responses received
     */
    public int getReceivedResponses() {
        return receivedResponses;
    }

    /**
     * Increments the sent commands counter.
     */
    public void incrementSentCommands() {
        this.sentCommands++;
    }
}
