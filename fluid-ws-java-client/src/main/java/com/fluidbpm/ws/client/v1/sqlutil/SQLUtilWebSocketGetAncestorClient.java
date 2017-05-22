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
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.FluidItem;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
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
     */
    public SQLUtilWebSocketGetAncestorClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<Form> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {
        super(endpointBaseUrlParam,
                new GetAncestorMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getAncestorWebSocket(
                        includeFieldDataParam,
                        includeTableFieldsParam,
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves the Ancestor (Form) for the {@code formToGetTableFormsForParam}.
     *
     * @param formToGetAncestorForParam The Fluid Form to get Ancestor for.
     *
     * @return The {@code formToGetDescendantsForParam} Table Records as {@code Form}'s.
     */
    public Form getAncestorSynchronized(
            Form formToGetAncestorForParam) {

        this.messageHandler.clear();

        if(formToGetAncestorForParam == null)
        {
            return null;
        }

        //Send all the messages...
        List<String> echoMessagesExpected = new ArrayList();
        if(formToGetAncestorForParam.getEcho() == null || formToGetAncestorForParam.getEcho().isEmpty())
        {
            throw new FluidClientException("Echo needs to be set to bind to return.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        else if(echoMessagesExpected.contains(formToGetAncestorForParam.getEcho()))
        {
            throw new FluidClientException("Echo message '"+formToGetAncestorForParam.getEcho()
                    +"' already added.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        echoMessagesExpected.add(formToGetAncestorForParam.getEcho());

        //Send the actual message...
        this.sendMessage(formToGetAncestorForParam);

        long timeoutTime = (System.currentTimeMillis() +
                this.getTimeoutInMillis());

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
                    Thread.sleep(Constant.RESPONSE_CHECKER_SLEEP);
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
                        "SQLUtil-WebSocket-GetAncestor: Timeout while waiting for all return data. There were '"
                                +this.messageHandler.getReturnValue().size()
                                +"' items after a Timeout of "+(
                                TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                        ,FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }


    /**
     * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}
     * asynchronously.
     *
     * @param formToGetAncestorsForForParam The Fluid Form to get Ancestors for.
     */
    public void getAncestorAsynchronous(
            Form formToGetAncestorsForForParam) {

        if(formToGetAncestorsForForParam == null)
        {
            return;
        }

        //Send the actual message...
        this.sendMessage(formToGetAncestorsForForParam);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    public static class GetAncestorMessageHandler extends GenericListMessageHandler<Form>
    {
        private Form returnedForm;

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
