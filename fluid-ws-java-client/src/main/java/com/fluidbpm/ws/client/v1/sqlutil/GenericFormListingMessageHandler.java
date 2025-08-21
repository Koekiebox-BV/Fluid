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

import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;

/**
 * A Form Listing handler for {@code FormListing}'s.
 *
 * @author jasonbruwer on 2016/03/12.
 * @since 1.1
 */
public class GenericFormListingMessageHandler extends AGenericListMessageHandler<FormListing> {

	/**
	 * Constructor for FormListing callbacks.
	 *
	 * @param messageReceivedCallbackParam The callback events.
	 * @param webSocketClientParam The web-socket client.
	 * @param compressedResponseParam Compress the SQL Result in Base-64.
	 */
	public GenericFormListingMessageHandler(
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			WebSocketClient webSocketClientParam,
			boolean compressedResponseParam) {

		super(messageReceivedCallbackParam, webSocketClientParam, compressedResponseParam);
	}

	/**
	 * Constructor for FormListing callbacks.
	 *
	 * @param messageReceivedCallbackParam The callback events.
	 * @param webSocketClientParam The web-socket client.   
	 */
	public GenericFormListingMessageHandler(
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			WebSocketClient webSocketClientParam
	) {
		super(messageReceivedCallbackParam, webSocketClientParam);
	}

	/**
	 * New {@code FormListing} by {@code jsonObjectParam}
	 *
	 * @param jsonObjectParam The JSON Object to parse.
	 * @return new {@code FormListing}.
	 */
	@Override
	public FormListing getNewInstanceBy(JsonObject jsonObjectParam) {
		return new FormListing(jsonObjectParam);
	}
}
