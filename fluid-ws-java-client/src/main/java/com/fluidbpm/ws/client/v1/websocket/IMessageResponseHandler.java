package com.fluidbpm.ws.client.v1.websocket;

/**
 * Contract interface for message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public interface IMessageResponseHandler {

    /**
     * Handle the {@code messageParam}.
     *
     * @param messageParam The message from the server.
     */
    public abstract void handleMessage(String messageParam);

    /**
     * When a connection closed has been initiated
     * remotely.
     */
    public abstract void connectionClosed();
}
