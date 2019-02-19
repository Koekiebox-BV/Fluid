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
public class SQLUtilWebSocketGetAncestorClient extends
		ABaseClientWebSocket<SQLUtilWebSocketGetAncestorClient.GetAncestorMessageHandler> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 *
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 *
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 *
	 * @param includeFieldDataParam Should Form Field data be included.
	 * @param includeTableFieldsParam Should Table Fields be included.
	 * @param compressResponseParam Compress the Ancestor Result in Base-64.
	 * @param compressResponseCharsetParam Compress response using provided charset.
	 */
	public SQLUtilWebSocketGetAncestorClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<Form> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean compressResponseParam,
			String compressResponseCharsetParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getAncestorWebSocket(
						includeFieldDataParam,
						includeTableFieldsParam,
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
	 *
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 *
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 *
	 * @param includeFieldDataParam Should Form Field data be included.
	 * @param includeTableFieldsParam Should Table Fields be included.
	 */
	public SQLUtilWebSocketGetAncestorClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<Form> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.SQLUtil.Version1.getAncestorWebSocket(
						includeFieldDataParam,
						includeTableFieldsParam,
						serviceTicketAsHexParam,
						false,
						UtilGlobal.EMPTY));

		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Retrieves the Ancestor (Form) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetAncestorForParam The Fluid Form to get Ancestor for.
	 *
	 * @return The {@code formToGetDescendantsForParam} Table Records as {@code Form}'s.
	 */
	public Form getAncestorSynchronized(Form formToGetAncestorForParam) {

		if(formToGetAncestorForParam == null) {
			return null;
		}

		//Send all the messages...
		this.setEchoIfNotSet(formToGetAncestorForParam);

		//Start a new request...
		String uniqueReqId = this.initNewRequest();

		//Send the actual message...
		int numberOfSentForms = 0;
		this.sendMessage(formToGetAncestorForParam, uniqueReqId);
		numberOfSentForms++;

		try {
			List<Form> returnValue = this.getHandler(uniqueReqId).getCF().get(
					this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if(this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetAncestor: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			if(returnValue == null || returnValue.isEmpty()) {
				return null;
			}

			return returnValue.get(0);
		} catch (InterruptedException exceptParam) {
			//Interrupted...
			throw new FluidClientException(
					"SQLUtil-WebSocket-Interrupted-GetAncestor: " +
							exceptParam.getMessage(),
					exceptParam,
					FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
		} catch (ExecutionException executeProblem) {
			//Error on the web-socket...
			Throwable cause = executeProblem.getCause();

			//Fluid client exception...
			if(cause instanceof FluidClientException)
			{
				throw (FluidClientException)cause;
			} else {
				throw new FluidClientException(
						"SQLUtil-WebSocket-GetAncestor: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException eParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"SQLUtil-WebSocket-GetAncestor",
					uniqueReqId,
					numberOfSentForms);

			throw new FluidClientException(
					errMessage, FluidClientException.ErrorCode.IO_ERROR);
		} finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code GetAncestorMessageHandler}
	 */
	@Override
	public GetAncestorMessageHandler getNewHandlerInstance() {
		return new GetAncestorMessageHandler(this.messageReceivedCallback, this.compressResponse);
	}

	/**
	 * Gets the single form. Still relying on a single session.
	 */
	public static class GetAncestorMessageHandler extends AGenericListMessageHandler<Form>
	{
		/**
		 * The default constructor that sets a ancestor message handler.
		 *
		 * @param messageReceivedCallbackParam The optional message callback.
		 * @param compressedResponseParam Compress the SQL Result in Base-64.
		 */
		public GetAncestorMessageHandler(
				IMessageReceivedCallback<Form> messageReceivedCallbackParam,
				boolean compressedResponseParam) {

			super(messageReceivedCallbackParam, compressedResponseParam);
		}

		/**
		 * The default constructor that sets a ancestor message handler.
		 *
		 * @param messageReceivedCallbackParam The optional message callback.
		 */
		public GetAncestorMessageHandler(IMessageReceivedCallback<Form> messageReceivedCallbackParam) {

			super(messageReceivedCallbackParam);
		}

		/**
		 * New {@code Form} by {@code jsonObjectParam}
		 *
		 * @param jsonObjectParam The JSON Object to parse.
		 * @return new {@code Form}.
		 */
		@Override
		public Form getNewInstanceBy(JSONObject jsonObjectParam) {

			return new Form(jsonObjectParam);
		}
	}
}
