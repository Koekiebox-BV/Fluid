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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
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
                new CreateFormContainerMessageHandler(messageReceivedCallbackParam),
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

        this.getMessageHandler().clear();

        if(formToCreateParam == null)
        {
            return null;
        }

        //Send all the messages...
        if(formToCreateParam.getEcho() == null || formToCreateParam.getEcho().trim().isEmpty())
        {
            formToCreateParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<Form>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);
        
        //Send the actual message...
        this.sendMessage(formToCreateParam);

        try {
            List<Form> returnValue = completableFuture.get(
                    this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if(this.getMessageHandler().isConnectionClosed())
            {
                throw new FluidClientException(
                        "WebSocket-CreateFormContainer: " +
                                "The connection was closed by the server prior to the response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            if(returnValue == null || returnValue.isEmpty())
            {
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
                        "WebSocket-CreateFormContainer: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "WebSocket-CreateFormContainer: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Creates a new Form Container from {@code formToGetAncestorsForForParam}
     * asynchronously.
     *
     * @param formToCreateParam The Fluid Form create.
     */
    public void createFormAsynchronous(Form formToCreateParam) {

        if(formToCreateParam == null)
        {
            return;
        }

        //Send the actual message...
        this.sendMessage(formToCreateParam);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    static class CreateFormContainerMessageHandler extends GenericListMessageHandler<Form>
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
