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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.FluidItem;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

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
     * @param endpointBaseUrlParam URL to base endpoint.
     *
     * @param messageReceivedCallbackParam Callback for when a message is received.
     *
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     * @param waitForRuleExecCompleteParam Wait for all the program rules to finish execution
     *                                     before returning web socket message is sent.
     *                                     The response message will include the result.
     */
    public WebSocketSendToFlowClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<FluidItem> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean waitForRuleExecCompleteParam) {
        super(endpointBaseUrlParam,
                new SendToFlowMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.FlowItem.Version1.sendToFlowWebSocket(
                        waitForRuleExecCompleteParam,
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Sends the {@code formToSendToFlowParam} to a {@code Flow} in Fluid.
     * The return value is the {@code FluidItem} created as a result.
     *
     * @param formToSendToFlowParam The Fluid Form to send to flow.
     *
     * @return The {@code formToSendToFlowParam} created as {@code FluidItem}.
     */
    public FluidItem sendToFlowSynchronized(
            Form formToSendToFlowParam) {

        this.getMessageHandler().clear();

        if(formToSendToFlowParam == null)
        {
            return null;
        }

        //Send all the messages...
        if(formToSendToFlowParam.getEcho() == null || formToSendToFlowParam.getEcho().trim().isEmpty())
        {
            formToSendToFlowParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<FluidItem>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);
        
        //Send the actual message...
        this.sendMessage(formToSendToFlowParam);

        try {
            List<FluidItem> returnValue = completableFuture.get(
                    this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

            if(returnValue == null || returnValue.isEmpty())
            {
                return null;
            }

            return returnValue.get(0);
        }
        //Interrupted...
        catch (InterruptedException exceptParam) {

            throw new FluidClientException(
                    "WebSocket-Interrupted-SendToFlow: " +
                            exceptParam.getMessage(),
                    exceptParam,
                    FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
        }
        //Error on the web-socket...
        catch (ExecutionException executeProblem) {

            Throwable cause = executeProblem.getCause();

            //Fluid client exception...
            if(cause instanceof FluidClientException)
            {
                throw (FluidClientException)cause;
            }
            else
            {
                throw new FluidClientException(
                        "WebSocket-SendToFlow: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "WebSocket-SendToFlow: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Sends {@code formToCreateParam} to Flow asynchronously.
     *
     * @param formToSendToFlowParam The Fluid Form create.
     */
    public void sendToFlowAsynchronous(Form formToSendToFlowParam) {

        if(formToSendToFlowParam == null)
        {
            return;
        }

        //Send the actual message...
        this.sendMessage(formToSendToFlowParam);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    public static class SendToFlowMessageHandler extends GenericListMessageHandler<FluidItem>
    {
        private FluidItem returnedFluidItem;

        /**
         * The default constructor that sets a ancestor message handler.
         *
         * @param messageReceivedCallbackParam The optional message callback.
         */
        public SendToFlowMessageHandler(
                IMessageReceivedCallback<FluidItem> messageReceivedCallbackParam) {

            super(messageReceivedCallbackParam);
        }

        /**
         * New {@code Form} by {@code jsonObjectParam}
         *
         * @param jsonObjectParam The JSON Object to parse.
         * @return new {@code Form}.
         */
        @Override
        public FluidItem getNewInstanceBy(JSONObject jsonObjectParam) {

            this.returnedFluidItem = new FluidItem(jsonObjectParam);

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
