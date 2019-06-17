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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code SQLUtil} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see WS.Path.FlowItem
 * @see FluidItem
 */
public class SQLUtilWebSocketGetFormFieldsClient extends
		ABaseClientWebSocket<AGenericListMessageHandler<FormFieldListing>> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 * @param includeFieldDataParam Should Form Field data be included.
	 * @param compressResponseParam Compress the Form Field Result in Base-64.
	 * @param compressResponseCharsetParam Compress response using provided charset.
	 */
	public SQLUtilWebSocketGetFormFieldsClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormFieldListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam,
			boolean compressResponseParam,
			String compressResponseCharsetParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getFormFieldsWebSocket(
						includeFieldDataParam,
						serviceTicketAsHexParam,
						compressResponseParam,
						compressResponseCharsetParam),
				compressResponseParam);
		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 * @param includeFieldDataParam Should Form Field data be included.
	 */
	public SQLUtilWebSocketGetFormFieldsClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormFieldListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getFormFieldsWebSocket(
						includeFieldDataParam,
						serviceTicketAsHexParam,
						false,
						UtilGlobal.EMPTY));
		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}.
	 *
	 * @param formsToGetFieldListingForForParam The Fluid Form to get Ancestors for.
	 *
	 * @return The {@code formToGetAncestorsForForParam} Table Records as {@code Form}'s.
	 */
	public List<FormFieldListing> getFormFieldsSynchronized(
			Form ... formsToGetFieldListingForForParam
	) {
		if (formsToGetFieldListingForForParam == null) {
			return null;
		}

		if (formsToGetFieldListingForForParam.length == 0) {
			return null;
		}

		//Start a new request...
		String uniqueReqId = this.initNewRequest();
		//Send all the messages...
		for (Form formToSend : formsToGetFieldListingForForParam) {
			this.setEchoIfNotSet(formToSend);

			//Send the actual message...
			this.sendMessage(formToSend, uniqueReqId);
		}

		try {
			List<FormFieldListing> returnValue =
					this.getHandler(uniqueReqId).getCF().get(
							this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if (this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetFormFields: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			return returnValue;
		} catch (InterruptedException exceptParam) {
			//Interrupted...
			throw new FluidClientException(
					"SQLUtil-WebSocket-Interrupted-GetFormFields: " +
							exceptParam.getMessage(),
					exceptParam,
					FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
		} catch (ExecutionException executeProblem) {
			//Error on the web-socket...
			Throwable cause = executeProblem.getCause();

			//Fluid client exception...
			if (cause instanceof FluidClientException) {
				throw (FluidClientException)cause;
			} else {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetFormFields: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException timeoutErrParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"SQLUtil-WebSocket-GetFormFields",
					uniqueReqId, (Object[]) formsToGetFieldListingForForParam);
			throw new FluidClientException(errMessage, FluidClientException.ErrorCode.IO_ERROR);
		} finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code GenericFormFieldListingMessageHandler}
	 */
	@Override
	public GenericFormFieldListingMessageHandler getNewHandlerInstance() {
		return new GenericFormFieldListingMessageHandler(
				this.messageReceivedCallback,
				this.webSocketClient,
				this.compressResponse
		);
	}
}
