package com.fluid.ws.client.v1.sqlutil;

import org.json.JSONObject;

import com.fluid.program.api.vo.form.FormListing;
import com.fluid.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluid.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * A Form Listing handler for {@code FormListing}'s.
 *
 * @author jasonbruwer on 2016/03/12.
 * @since 1.1
 */
public class GenericFormListingMessageHandler extends GenericListMessageHandler<FormListing> {

    /**
     * @param messageReceivedCallbackParam
     */
    public GenericFormListingMessageHandler(IMessageReceivedCallback<FormListing> messageReceivedCallbackParam) {

        super(messageReceivedCallbackParam);
    }

    /**
     * New {@code FormListing} by {@code jsonObjectParam}
     *
     * @param jsonObjectParam The JSON Object to parse.
     * @return new {@code FormListing}.
     */
    @Override
    public FormListing getNewInstanceBy(JSONObject jsonObjectParam) {
        return new FormListing(jsonObjectParam);
    }
}
