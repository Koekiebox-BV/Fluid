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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code SQLUtil} related actions.
 * Implementation is making use of {@link CompletableFuture}.
 *
 * @author jasonbruwer
 * @since v1.3
 *
 * @see JSONObject
 * @see Form
 */
public class SQLUtilWebSocketExecuteSQLClient extends
		ABaseClientWebSocket<AGenericListMessageHandler<FormListing>> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 * @param compressResponseParam Compress the SQL Result in Base-64.
	 * @param compressResponseCharsetParam Compress response using provided charset.
	 */
	public SQLUtilWebSocketExecuteSQLClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean compressResponseParam,
			String compressResponseCharsetParam
	) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getExecuteSQLWebSocket(
						serviceTicketAsHexParam,
						compressResponseParam,
						compressResponseCharsetParam), compressResponseParam);
		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 * Responses are not compressed.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 */
	public SQLUtilWebSocketExecuteSQLClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam
	) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getExecuteSQLWebSocket(
						serviceTicketAsHexParam,
						false,
						UtilGlobal.EMPTY));

		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Retrieves all the Descendants (Forms) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formWithSQLFieldParam The Fluid Form to Execute custom SQL for.
	 *
	 * @return The SQL Execution result as {@code Form}'s.
	 */
	public List<FormListing> executeSQLSynchronized(Form formWithSQLFieldParam) {

		if (formWithSQLFieldParam == null) {
			return null;
		}

		if (formWithSQLFieldParam.getFormFields() == null ||
				formWithSQLFieldParam.getFormFields().isEmpty()) {
			return null;
		}

		//Validate the echo...
		this.setEchoIfNotSet(formWithSQLFieldParam);

		//Start a new request...
		String uniqueReqId = this.initNewRequest();

		//Send the actual message...
		this.sendMessage(formWithSQLFieldParam, uniqueReqId);

		try {
			List<FormListing> returnValue = this.getHandler(uniqueReqId).getCF().get(
							this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if (this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"SQLUtil-WebSocket-ExecuteSQL: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			return returnValue;
		} catch (InterruptedException exceptParam) {
			//Interrupted...
			throw new FluidClientException(
					"SQLUtil-WebSocket-Interrupted-ExecuteSQL: " +
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
						"SQLUtil-WebSocket-ExecuteSQL: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException eParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"SQLUtil-WebSocket-ExecuteSQL",
					uniqueReqId,
					formWithSQLFieldParam);

			throw new FluidClientException(errMessage,
					FluidClientException.ErrorCode.IO_ERROR);
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
				this.messageReceivedCallback,
				this.webSocketClient,
				this.compressResponse
		);
	}
}
