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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
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
            IMessageReceivedCallback<FormFieldListing> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam) {
        super(endpointBaseUrlParam,
                new GenericFormFieldListingMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getFormFieldsWebSocket(
                        includeFieldDataParam,serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}.
     *
     * @param formsToGetFieldListingForForParam The Fluid Form to get Ancestors for.
     *
     * @return The {@code formToGetAncestorsForForParam} Table Records as {@code Form}'s.
     */
    public List<FormFieldListing> getFormFieldsSynchronized(
            Form ... formsToGetFieldListingForForParam) {

        this.getMessageHandler().clear();

        if(formsToGetFieldListingForForParam == null)
        {
            return null;
        }

        if(formsToGetFieldListingForForParam.length == 0)
        {
            return this.getMessageHandler().getReturnValue();
        }

        CompletableFuture<List<FormFieldListing>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);

        //Send all the messages...
        for(Form formToSend : formsToGetFieldListingForForParam)
        {
            if(formToSend == null)
            {
                throw new FluidClientException(
                        "Cannot provide 'null' for Form.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }
            else if(formToSend.getEcho() == null || formToSend.getEcho().trim().isEmpty())
            {
                formToSend.setEcho(UUID.randomUUID().toString());
            }

            //Send the actual message...
            this.sendMessage(formToSend);
        }

        try {
            List<FormFieldListing> returnValue = completableFuture.get(
                    this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if(this.getMessageHandler().isConnectionClosed())
            {
                throw new FluidClientException(
                        "SQLUtil-WebSocket-GetFormFields: " +
                                "The connection was closed by the server prior to the response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            return returnValue;
        }
        //Interrupted...
        catch (InterruptedException exceptParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-Interrupted-GetFormFields: " +
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
                        "SQLUtil-WebSocket-GetFormFields: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-GetFormFields: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }
}
