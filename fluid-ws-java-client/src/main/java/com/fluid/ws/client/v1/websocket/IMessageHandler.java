package com.fluid.ws.client.v1.websocket;

/**
 * Contract interface for message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public interface IMessageHandler {

    /**
     * Handle the {@code messageParam}.
     *
     * @param messageParam The message from the server.
     */
    public void handleMessage(String messageParam);

    /**
     * When a connection closed has been initiated
     * remotely.
     */
    public void connectionClosed();
}
