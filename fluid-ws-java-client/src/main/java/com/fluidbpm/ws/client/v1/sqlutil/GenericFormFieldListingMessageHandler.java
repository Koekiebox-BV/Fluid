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

package com.fluidbpm.ws.client.v1.sqlutil;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * A Form Listing handler for {@code FormFieldListing}'s.
 *
 * @author jasonbruwer on 2018/03/01.
 * @since 1.1
 */
public class GenericFormFieldListingMessageHandler extends GenericListMessageHandler<FormFieldListing> {

    /**
     * Constructor for FormFieldListing callbacks.
     *
     * @param messageReceivedCallbackParam The callback events.
     */
    public GenericFormFieldListingMessageHandler(
            IMessageReceivedCallback<FormFieldListing> messageReceivedCallbackParam) {

        super(messageReceivedCallbackParam);
    }

    /**
     * New {@code FormFieldListing} by {@code jsonObjectParam}
     *
     * @param jsonObjectParam The JSON Object to parse.
     * @return new {@code FormFieldListing}.
     */
    @Override
    public FormFieldListing getNewInstanceBy(JSONObject jsonObjectParam) {
        return new FormFieldListing(jsonObjectParam);
    }
}
