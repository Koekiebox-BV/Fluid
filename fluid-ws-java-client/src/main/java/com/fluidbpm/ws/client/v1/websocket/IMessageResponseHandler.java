/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.websocket;

/**
 * Contract interface for message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public interface IMessageResponseHandler {

    /**
     *
     * @param messageParam
     * @return
     */
    public Object doesHandlerQualifyForProcessing(String messageParam);

    /**
     * Handle the {@code messageParam}.
     *
     * @param messageParam The message from the server.
     */
    public abstract void handleMessage(Object messageParam);

    /**
     * When a connection closed has been initiated
     * remotely.
     */
    public abstract void connectionClosed();
}
