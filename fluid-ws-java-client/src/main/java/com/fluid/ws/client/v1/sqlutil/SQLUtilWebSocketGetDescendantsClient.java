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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
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
public class SQLUtilWebSocketGetDescendantsClient extends
        ABaseClientWebSocket<GenericListMessageHandler> {

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
     * @param massFetchParam Is the fetch a large fetch.
     */
    public SQLUtilWebSocketGetDescendantsClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam,
            boolean massFetchParam) {
        super(endpointBaseUrlParam,
                new GenericFormListingMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getDescendantsWebSocket(
                        includeFieldDataParam,
                        includeTableFieldsParam,
                        massFetchParam,
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
        this.massFetch = massFetchParam;
    }

    /**
     * Retrieves all the Descendants (Forms) for the {@code formToGetTableFormsForParam}.
     *
     * @param formToGetDescendantsForParam The Fluid Form to get Descendants for.
     *
     * @return The {@code formToGetDescendantsForParam} Table Records as {@code Form}'s.
     */
    public List<FormListing> getDescendantsSynchronized(
            Form ... formToGetDescendantsForParam) {

        this.messageHandler.clear();

        if(formToGetDescendantsForParam == null)
        {
            return null;
        }

        if(formToGetDescendantsForParam.length == 0)
        {
            return this.messageHandler.getReturnValue();
        }

        List<String> echoMessagesExpected = new ArrayList();

        //Mass data fetch...
        if(this.massFetch)
        {
            FormListing listingToSend = new FormListing();
            List<Form> listOfValidForms = new ArrayList();
            for(Form formToSend : formToGetDescendantsForParam)
            {
                if(formToSend == null)
                {
                    throw new FluidClientException(
                            "Cannot provide 'null' for Form.",
                            FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
                }
                
                listOfValidForms.add(new Form(formToSend.getId()));
            }

            listingToSend.setEcho(UUID.randomUUID().toString());
            listingToSend.setListing(listOfValidForms);

            echoMessagesExpected.add(listingToSend.getEcho());

            //Send the actual message...
            this.sendMessage(listingToSend);
        }
        //Single...
        else
        {
            //Send all the messages...
            for(Form formToSend : formToGetDescendantsForParam)
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
                        "SQLUtil-WebSocket-GetDescendants: Timeout while waiting for all return data. There were '"
                                +this.messageHandler.getReturnValue().size()
                                +"' items after a Timeout of "+(
                                TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                        ,FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }
}
