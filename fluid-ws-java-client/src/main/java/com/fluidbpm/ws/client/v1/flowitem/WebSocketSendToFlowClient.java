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

package com.fluidbpm.ws.client.v1.flowitem;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java Web Socket Client for sending {@code Form} to a specific Flow.
 *
 * IMPORTANT: This class is Thread safe.
 *
 * @author jasonbruwer
 * @since v1.5
 *
 * @see JSONObject
 * @see WS.Path.FormContainer
 * @see Form
 */
public class WebSocketSendToFlowClient extends
		ABaseClientWebSocket<WebSocketSendToFlowClient.SendToFlowMessageHandler> {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 *
	 * @param messageReceivedCallback Callback for when a message is received.
	 *
	 * @param serviceTicketAsHex The Server issued Service Ticket.
	 * @param timeoutInMillis The timeout of the request in millis.
	 * @param waitForRuleExecComplete Wait for all the program rules to finish execution
	 *                                     before returning web socket message is sent.
	 *                                     The response message will include the result.
	 */
	public WebSocketSendToFlowClient(
			String endpointBaseUrl,
			IMessageReceivedCallback<FluidItem> messageReceivedCallback,
			String serviceTicketAsHex,
			long timeoutInMillis,
			boolean waitForRuleExecComplete
	) {
		super(endpointBaseUrl, messageReceivedCallback, timeoutInMillis,
				WS.Path.FlowItem.Version1.sendToFlowWebSocket(waitForRuleExecComplete, serviceTicketAsHex)
		);
		this.setServiceTicket(serviceTicketAsHex);
	}

	/**
	 * Sends the {@code formToSendToFlowParam} to a {@code Flow} in Fluid.
	 * The return value is the {@code FluidItem} created as a result.
	 *
	 * The Web socket has the ability to wait for the workflow to finish
	 * and then only respond with the final result of the item.
	 *
	 * @param formToSendToFlow The Fluid Form to send to flow.
	 * @param destinationFlow The destination flow.
	 *
	 * @return The {@code formToSendToFlowParam} created as {@code FluidItem}.
	 */
	public FluidItem sendToFlowSynchronized(Form formToSendToFlow, String destinationFlow) {
		if (formToSendToFlow == null) return null;
		if (destinationFlow == null || destinationFlow.trim().isEmpty()) {
			throw new FluidClientException(
					"No destination Flow provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE
			);
		}

		FluidItem itemToSend = new FluidItem();
		itemToSend.setFlow(destinationFlow);
		itemToSend.setForm(formToSendToFlow);

		//Send all the messages...
		itemToSend.setEcho(UtilGlobal.randomUUID());

		//Start a new request...
		String uniqueReqId = this.initNewRequest();

		//Send the actual message...
		this.sendMessage(itemToSend, uniqueReqId);

		try {
			List<FluidItem> returnValue = this.getHandler(uniqueReqId).getCF().get(
					this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

			//Connection was closed.. this is a problem....
			if (this.getHandler(uniqueReqId).isConnectionClosed()) {
				throw new FluidClientException(
						"WebSocket-SendToFlow: " +
								"The connection was closed by the server prior to the response received.",
						FluidClientException.ErrorCode.IO_ERROR);
			}
			if (returnValue == null || returnValue.isEmpty()) return null;
			return returnValue.get(0);
		} catch (InterruptedException exceptParam) {
			throw new FluidClientException(
					"WebSocket-Interrupted-SendToFlow: " + exceptParam.getMessage(),
					exceptParam,
					FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR
			);
		} catch (ExecutionException executeProblem) {
			//Error on the web-socket...
			Throwable cause = executeProblem.getCause();
			if (cause instanceof FluidClientException) {
				throw (FluidClientException)cause;
			} else throw new FluidClientException(
						"WebSocket-SendToFlow: " + cause.getMessage(),
						cause,
						FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
		} catch (TimeoutException eParam) {
			String errMessage = this.getExceptionMessageVerbose(
					"WebSocket-SendToFlow",
					uniqueReqId,
					itemToSend);
			throw new FluidClientException(errMessage, FluidClientException.ErrorCode.IO_ERROR);
		}
		finally {
			this.removeHandler(uniqueReqId);
		}
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of {@code SendToFlowMessageHandler}
	 */
	@Override
	public SendToFlowMessageHandler getNewHandlerInstance() {
		return new SendToFlowMessageHandler(this.messageReceivedCallback, this.webSocketClient);
	}

	/**
	 * Gets the single {@link FluidItem}. Still relying on a single session.
	 */
	public static class SendToFlowMessageHandler extends AGenericListMessageHandler<FluidItem> {
		private FluidItem returnedFluidItem;

		/**
		 * The default constructor that sets a ancestor message handler.
		 *
		 * @param messageReceivedCallback The optional message callback.
		 * @param webSocketClient The web-socket client.
		 */
		public SendToFlowMessageHandler(
				IMessageReceivedCallback<FluidItem> messageReceivedCallback,
				WebSocketClient webSocketClient
		) {
			super(messageReceivedCallback, webSocketClient);
		}

		/**
		 * New {@code Form} by {@code jsonObjectParam}
		 *
		 * @param jsonObject The JSON Object to parse.
		 * @return new {@code Form}.
		 */
		@Override
		public FluidItem getNewInstanceBy(JSONObject jsonObject) {
			this.returnedFluidItem = new FluidItem(jsonObject);
			return this.returnedFluidItem;
		}

		/**
		 * Gets the value from that was returned after the WS call.
		 *
		 * @return The returned Fluid item.
		 */
		public FluidItem getReturnedFluidItem() {
			return this.returnedFluidItem;
		}
	}
}
