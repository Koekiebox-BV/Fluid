package com.fluid.ws.client.v1.websocket;

/**
 * TODO Description comes here.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.0
 */
public interface IMessageHandler {

    /**
     *
     * @param messageParam
     */
    public void handleMessage(String messageParam);

    /**
     * When a connection closed has been initiated
     * remotely.
     */
    public void connectionClosed();
}
