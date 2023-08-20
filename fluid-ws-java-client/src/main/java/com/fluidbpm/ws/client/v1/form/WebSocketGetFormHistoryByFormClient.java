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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java Web Socket Client for retrieving Form history values by Form.
 *
 * @author jasonbruwer
 * @since v1.13
 *
 * @see JSONObject
 * @see WS.Path.FlowItem
 * @see Form
 * @see Form
 */
public class WebSocketGetFormHistoryByFormClient extends
		ABaseClientWebSocket<AGenericListMessageHandler<FormHistoricDataListing>> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param messageReceivedCallback Callback for when a message is received.
	 * @param serviceTicketAsHex The Server issued Service Ticket.
	 * @param timeoutInMillis The timeout of the request in millis.
	 * @param includeCurrent Include the current field values.
	 * @param labelFieldName Make use of label field names.
	 */
	public WebSocketGetFormHistoryByFormClient(
			String endpointBaseUrl,
			IMessageReceivedCallback<FormFieldListing> messageReceivedCallback,
			String serviceTicketAsHex,
			long timeoutInMillis,
			boolean includeCurrent,
			boolean labelFieldName
	) {
		super(
				endpointBaseUrl,
				messageReceivedCallback,
				timeoutInMillis,
				WS.Path.FormHistory.Version1.getByFormContainerWebSocket(
						serviceTicketAsHex,
						includeCurrent,
						labelFieldName
				)
		);
		this.setServiceTicket(serviceTicketAsHex);
	}

	public List<FormHistoricDataListing> getByFormSynchronized(List<Form> formsToGetFieldListingForFor) {
		if (formsToGetFieldListingForFor == null) return null;

		return this.getByFormSynchronized(formsToGetFieldListingForFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}.
	 *
	 * @param formsToGetFieldListingForFor The Fluid Form to get Ancestors for.
	 *
	 * @return The {@code formToGetAncestorsForForParam} Table Records as {@code Form}'s.
	 */
	public List<FormHistoricDataListing> getByFormSynchronized(Form ... formsToGetFieldListingForFor) {
		if (formsToGetFieldListingForFor == null) return null;
		if (formsToGetFieldListingForFor.length == 0) return null;

		//Start a new request...
		String uniqueReqId = this.initNewRequest();
		//Send all the messages...
		for (Form formToSend : formsToGetFieldListingForFor) {
			this.setEchoIfNotSet(formToSend);
			//Send the actual message...
			this.sendMessage(formToSend, uniqueReqId);
		}

		try {
			List<FormHistoricDataListing> returnValue =
					this.getHandler(uniqueReqId).getCF().get(this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if (this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"WebSocket-GetFormHistory: The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			return returnValue;
		} catch (InterruptedException exceptParam) {
			//Interrupted...
			throw new FluidClientException(
					"WebSocket-GetFormHistory: " + exceptParam.getMessage(),
					exceptParam,
					FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
		} catch (ExecutionException executeProblem) {
			//Error on the web-socket...
			Throwable cause = executeProblem.getCause();
			if (cause instanceof FluidClientException) {
				throw (FluidClientException)cause;
			} else {
				throw new FluidClientException(
						"WebSocket-GetFormHistory: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException timeoutErrParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"WebSocket-GetFormHistory",
					uniqueReqId,
					(Object[]) formsToGetFieldListingForFor
			);
			throw new FluidClientException(errMessage, FluidClientException.ErrorCode.IO_ERROR);
		} finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code GenericFormHistoryListingMessageHandler}
	 */
	@Override
	public GenericFormHistoryListingMessageHandler getNewHandlerInstance() {
		return new GenericFormHistoryListingMessageHandler(this.messageReceivedCallback, this.webSocketClient);
	}
}
