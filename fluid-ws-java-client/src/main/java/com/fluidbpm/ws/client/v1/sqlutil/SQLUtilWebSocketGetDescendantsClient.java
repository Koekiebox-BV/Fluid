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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
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
public class SQLUtilWebSocketGetDescendantsClient extends
		ABaseClientWebSocket<AGenericListMessageHandler<FormListing>> {

	private boolean massFetch;

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 * @param includeFieldDataParam Should Form Field data be included.
	 * @param includeTableFieldsParam Should Table Fields be included.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 * @param massFetchParam Is the fetch a large fetch.
	 * @param compressResponseParam Compress the Descendants result in Base-64.
	 * @param compressResponseCharsetParam Compress response using provided charset.
	 */
	public SQLUtilWebSocketGetDescendantsClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean includeTableFieldFormRecordInfoParam,
			boolean massFetchParam,
			boolean compressResponseParam,
			String compressResponseCharsetParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getDescendantsWebSocket(
						includeFieldDataParam,
						includeTableFieldsParam,
						includeTableFieldFormRecordInfoParam,
						massFetchParam,
						serviceTicketAsHexParam,
						compressResponseParam,
						compressResponseCharsetParam),
				compressResponseParam);

		this.setServiceTicket(serviceTicketAsHexParam);
		this.massFetch = massFetchParam;
	}

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 * @param includeFieldDataParam Should Form Field data be included.
	 * @param includeTableFieldsParam Should Table Fields be included.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 * @param massFetchParam Is the fetch a large fetch.
	 */
	public SQLUtilWebSocketGetDescendantsClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean includeTableFieldFormRecordInfoParam,
			boolean massFetchParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getDescendantsWebSocket(
						includeFieldDataParam,
						includeTableFieldsParam,
						includeTableFieldFormRecordInfoParam,
						massFetchParam,
						serviceTicketAsHexParam,
						false,
						UtilGlobal.EMPTY));

		this.setServiceTicket(serviceTicketAsHexParam);
		this.massFetch = massFetchParam;
	}

	/**
	 * Retrieves all the Descendants (Forms) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetDescendantsForParam The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formToGetDescendantsForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getDescendantsSynchronized(
			Form ... formToGetDescendantsForParam) {

		if(formToGetDescendantsForParam == null ||
				formToGetDescendantsForParam.length == 0) {
			return null;
		}

		//Start a new request...
		String uniqueReqId = this.initNewRequest();

		//Mass data fetch...
		int numberOfSentForms = 0;
		if(this.massFetch) {
			FormListing listingToSend = new FormListing();
			List<Form> listOfValidForms = new ArrayList();
			for(Form formToSend : formToGetDescendantsForParam) {
				if(formToSend == null) {
					throw new FluidClientException(
							"Cannot provide 'null' for Form.",
							FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
				}

				listOfValidForms.add(new Form(formToSend.getId()));
			}

			listingToSend.setEcho(UUID.randomUUID().toString());
			listingToSend.setListing(listOfValidForms);

			//Send the actual message...
			this.sendMessage(listingToSend, uniqueReqId);
			numberOfSentForms++;
		} else {
			//Single...
			//Send all the messages...
			for(Form formToSend : formToGetDescendantsForParam) {
				this.setEchoIfNotSet(formToSend);

				//Send the actual message...
				this.sendMessage(formToSend, uniqueReqId);
				numberOfSentForms++;
			}
		}

		try {
			List<FormListing> returnValue =
					this.getHandler(uniqueReqId).getCF().get(
							this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if(this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetDescendants: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			return returnValue;
		} catch (InterruptedException exceptParam) {
			//Interrupted...

			throw new FluidClientException(
					"SQLUtil-WebSocket-Interrupted-GetDescendants: " +
							exceptParam.getMessage(),
					exceptParam,
					FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
		} catch (ExecutionException executeProblem) {
			//Error on the web-socket...

			Throwable cause = executeProblem.getCause();

			//Fluid client exception...
			if(cause instanceof FluidClientException) {
				throw (FluidClientException)cause;
			} else {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetDescendants: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException eParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"SQLUtil-WebSocket-GetDescendants",
					uniqueReqId, (Object[]) formToGetDescendantsForParam);
			throw new FluidClientException(
					errMessage, FluidClientException.ErrorCode.IO_ERROR);
		} finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code GenericFormListingMessageHandler}
	 */
	@Override
	public GenericFormListingMessageHandler getNewHandlerInstance() {
		return new GenericFormListingMessageHandler(
				this.messageReceivedCallback, this.compressResponse);
	}
}
