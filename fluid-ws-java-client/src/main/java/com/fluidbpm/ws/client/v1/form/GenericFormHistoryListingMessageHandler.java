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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import org.json.JSONObject;

/**
 * A Form Listing handler for {@code FormHistoricDataListing}'s.
 *
 * @author jasonbruwer on 2018/03/01.
 * @since 1.1
 */
public class GenericFormHistoryListingMessageHandler extends AGenericListMessageHandler<FormHistoricDataListing> {

	/**
	 * Constructor for FormListing callbacks.
	 *
	 * @param messageReceivedCallback The callback events.
	 * @param webSocketClient The web-socket client.
	 * @param compressedResponse Compress the SQL Result in Base-64.
	 */
	public GenericFormHistoryListingMessageHandler(
		IMessageReceivedCallback<FormHistoricDataListing> messageReceivedCallback,
		WebSocketClient webSocketClient
	) {
		super(messageReceivedCallback, webSocketClient);
	}

	/**
	 * New {@code FormFieldListing} by {@code jsonObjectParam}
	 *
	 * @param jsonObjectParam The JSON Object to parse.
	 * @return new {@code FormFieldListing}.
	 */
	@Override
	public FormHistoricDataListing getNewInstanceBy(JsonObject jsonObjectParam) {
		return new FormHistoricDataListing(jsonObjectParam);
	}
}
