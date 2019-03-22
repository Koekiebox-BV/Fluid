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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code Form} related actions.
 *
 * IMPORTANT: This class is Thread safe.
 *
 * @author jasonbruwer
 * @since v1.4
 *
 * @see JSONObject
 * @see WS.Path.FormContainer
 * @see Form
 */
public class WebSocketFormContainerCreateClient extends
		ABaseClientWebSocket<WebSocketFormContainerCreateClient.CreateFormContainerMessageHandler> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 *
	 * @param messageReceivedCallbackParam Callback for when a message is received.
	 *
	 * @param serviceTicketAsHexParam The Server issued Service Ticket.
	 * @param timeoutInMillisParam The timeout of the request in millis.
	 */
	public WebSocketFormContainerCreateClient(
			String endpointBaseUrlParam,
			IMessageReceivedCallback<Form> messageReceivedCallbackParam,
			String serviceTicketAsHexParam,
			long timeoutInMillisParam) {
		super(endpointBaseUrlParam,
				messageReceivedCallbackParam,
				timeoutInMillisParam,
				WS.Path.FormContainer.Version1.formContainerCreateWebSocket(
						serviceTicketAsHexParam));

		this.setServiceTicket(serviceTicketAsHexParam);
	}

	/**
	 * Creates a new (Form) for the {@code formToCreateParam}.
	 *
	 * @param formToCreateParam The Fluid Form to create.
	 *
	 * @return The {@code formToCreateParam} created as {@code Form}.
	 */
	public Form createFormContainerSynchronized(
			Form formToCreateParam) {

		if(formToCreateParam == null)
		{
			return null;
		}

		//Send all the messages...
		if(formToCreateParam.getEcho() == null || formToCreateParam.getEcho().trim().isEmpty())
		{
			formToCreateParam.setEcho(UUID.randomUUID().toString());
		}

		//Start a new request...
		String uniqueReqId = this.initNewRequest();

		//Send the actual message...
		this.sendMessage(formToCreateParam, uniqueReqId);

		try {
			List<Form> returnValue = this.getHandler(uniqueReqId).getCF().get(
					this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if(this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"WebSocket-CreateFormContainer: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}

			if(returnValue == null || returnValue.isEmpty()) {
				return null;
			}

			return returnValue.get(0);
		}
		//Interrupted...
		catch (InterruptedException exceptParam) {

			throw new FluidClientException(
					"WebSocket-Interrupted-CreateFormContainer: " +
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
			}
			else
			{
				throw new FluidClientException(
						"WebSocket-CreateFormContainer: " +
								cause.getMessage(), cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
			}
		} catch (TimeoutException eParam) {
			//Timeout...
			String errMessage = this.getExceptionMessageVerbose(
					"WebSocket-CreateFormContainer",
					uniqueReqId,
					formToCreateParam);
			throw new FluidClientException(
					errMessage, FluidClientException.ErrorCode.IO_ERROR);
		}
		finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code CreateFormContainerMessageHandler}
	 */
	@Override
	public CreateFormContainerMessageHandler getNewHandlerInstance() {
		return new CreateFormContainerMessageHandler(this.messageReceivedCallback);
	}

	/**
	 * Gets the single form. Still relying on a single session.
	 */
	static class CreateFormContainerMessageHandler extends AGenericListMessageHandler<Form>
	{
		private Form returnedForm;

		/**
		 * The default constructor that sets a ancestor message handler.
		 *
		 * @param messageReceivedCallbackParam The optional message callback.
		 */
		public CreateFormContainerMessageHandler(
				IMessageReceivedCallback<Form> messageReceivedCallbackParam) {

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

			this.returnedForm = new Form(jsonObjectParam);

			return this.returnedForm;
		}

		/**
		 * Gets the value from that was returned after the WS call.
		 *
		 * @return The returned form.
		 */
		public Form getReturnedForm() {
			return this.returnedForm;
		}
	}
}
