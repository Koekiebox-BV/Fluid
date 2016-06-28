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

package com.fluid.ws.client.v1.sqlutil;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.form.FormFieldListing;
import com.fluid.program.api.vo.form.FormListing;
import com.fluid.program.api.vo.ws.Error;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluid.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluid.ws.client.v1.websocket.IMessageReceivedCallback;

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
        ABaseClientWebSocket<GenericListMessageHandler> {

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
            IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam) {
        super(endpointBaseUrlParam,
                new GenericFormListingMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getFormFieldsWebSocket(
                        includeFieldDataParam,serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}.
     *
     * @param formToGetAncestorsForForParam The Fluid Form to get Ancestors for.
     *
     * @return The {@code formToGetAncestorsForForParam} Table Records as {@code Form}'s.
     */
    public List<FormFieldListing> getFormFieldsSynchronized(
            Form ... formToGetAncestorsForForParam) {

        this.messageHandler.clear();

        if(formToGetAncestorsForForParam == null)
        {
            return null;
        }

        if(formToGetAncestorsForForParam.length == 0)
        {
            return this.messageHandler.getReturnValue();
        }

        //Send all the messages...
        List<String> echoMessagesExpected = new ArrayList();
        for(Form formToSend : formToGetAncestorsForForParam)
        {
            if(formToSend == null)
            {
                throw new FluidClientException(
                        "Cannot provide 'null' for Form.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }
            else if(formToSend.getEcho() == null || formToSend.getEcho().isEmpty())
            {
                throw new FluidClientException("Echo needs to be set to bind to return.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }
            else if(echoMessagesExpected.contains(formToSend.getEcho()))
            {
                throw new FluidClientException("Echo message '"+formToSend.getEcho()
                        +"' already added.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }

            echoMessagesExpected.add(formToSend.getEcho());

            //Send the actual message...
            this.sendMessage(formToSend);
        }

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
                return this.messageHandler.getReturnValue();
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
                        "Timeout while waiting for all return data. There were '"
                                +this.messageHandler.getReturnValue().size()+"'."
                        ,FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }

    /**
     *
     */
    private static class GetAncestorMessageHandler extends GenericListMessageHandler<FormFieldListing>
    {

        /**
         *
         * @param messageReceivedCallbackParam
         */
        public GetAncestorMessageHandler(
                IMessageReceivedCallback<FormFieldListing> messageReceivedCallbackParam) {

            super(messageReceivedCallbackParam);
        }

        /**
         * New {@code FormFieldListing} by {@code jsonObjectParam}
         *
         * @param jsonObjectParam The JSON Object to parse.
         * @return new {@code FormFieldListing}.
         */
        @Override
        public FormFieldListing getNewInstanceBy(JSONObject jsonObjectParam) {
            return new FormFieldListing(jsonObjectParam);
        }
    }
}
