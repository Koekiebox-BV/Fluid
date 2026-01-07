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

import com.google.gson.JsonObject;

/**
 * Contract interface for message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 * @version  v1.8
 */
public interface IMessageResponseHandler {

    /**
     * Checks whether {@code subclass} message handler can process
     * the message {@code messageParam}.
     *
     * If the handler can't process the message, a {@code null} value
     * should be returned.
     *
     * @param message The message to check for qualification.
     * @return The JSONObject.
     *
     * @see JsonObject
     */
    Object doesHandlerQualifyForProcessing(String message);

    /**
     * Determines whether the handler qualifies for processing the given message.
     *
     * This method evaluates the provided byte array message to determine if the
     * implementing handler can process it. If the handler is unable to process
     * the message, the method should return {@code null}.
     *
     * @param message The byte array representation of the message to be checked.
     * @return An {@code Object} indicating that the handler qualifies to process
     *         the message, or {@code null} if the handler cannot process it.
     */
    Object doesHandlerQualifyForProcessing(byte[] message);

    /**
     * Handle the {@code messageParam}.
     *
     * @param messageParam The message from the server.
     */
    void handleMessage(Object messageParam);

    /**
     * When a connection closed has been initiated
     * remotely.
     */
    void connectionClosed();
}
