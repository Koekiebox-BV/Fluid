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

package com.fluid.ws.client.v1.form;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.ws.Error;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluid.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluid.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code Form} related actions.
 *
 * @author jasonbruwer
 * @since v1.4
 *
 * @see JSONObject
 * @see WS.Path.FormContainer
 * @see Form
 */
public class WebSocketFormContainerClient extends
        ABaseClientWebSocket<WebSocketFormContainerClient.CreateFormContainerMessageHandler> {

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
    public WebSocketFormContainerClient(
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

        this.messageHandler.clear();

        if(formToCreateParam == null)
        {
            return null;
        }

        //Send all the messages...
        List<String> echoMessagesExpected = new ArrayList();
        if(formToCreateParam.getEcho() == null || formToCreateParam.getEcho().isEmpty())
        {
            throw new FluidClientException("Echo needs to be set to bind to return.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        else if(echoMessagesExpected.contains(formToCreateParam.getEcho()))
        {
            throw new FluidClientException("Echo message '"+formToCreateParam.getEcho()
                    +"' already added.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        echoMessagesExpected.add(formToCreateParam.getEcho());

        //Send the actual message...
        this.sendMessage(formToCreateParam);

        long timeoutTime = (System.currentTimeMillis() + this.getTimeoutInMillis());

        //Wait for all the results...
        while(true)
        {
            if(this.messageHandler.hasErrorOccurred())
            {
                List<Error> listOfErrors = this.messageHandler.getErrors();
                Error firstError = listOfErrors.get(0);

                throw new FluidClientException(
                        firstError.getErrorMessage(),
                        firstError.getErrorCode());
            }
            else if(this.messageHandler.isConnectionClosed() ||
                    this.messageHandler.doReturnValueEchoMessageContainAll(echoMessagesExpected))
            {
                return this.messageHandler.getReturnedForm();
            }
            //
            else
            {
                try {
                    Thread.sleep(50);
                }
                //
                catch (InterruptedException e) {

                    throw new FluidClientException(
                            "Thread interrupted. "+e.getMessage(),
                            e,FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
                }
            }

            long now = System.currentTimeMillis();
            //Timeout...
            if(now > timeoutTime)
            {
                throw new FluidClientException(
                        "SQLUtil-WebSocket-CreateFormContainer: Timeout while waiting for all return data. There were '"
                                +this.messageHandler.getReturnValue().size()
                                +"' items after a Timeout of "+(
                                TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                        ,FluidClientException.ErrorCode.IO_ERROR);
            }
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
    public static class CreateFormContainerMessageHandler extends GenericListMessageHandler<Form>
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
