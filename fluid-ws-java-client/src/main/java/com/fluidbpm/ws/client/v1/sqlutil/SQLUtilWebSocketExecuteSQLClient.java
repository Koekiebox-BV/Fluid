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

import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code SQLUtil} related actions.
 * Implementation is making use of {@link CompletableFuture}.
 *
 * @author jasonbruwer
 * @since v1.3
 *
 * @see JSONObject
 * @see Form
 */
public class SQLUtilWebSocketExecuteSQLClient extends
        ABaseClientWebSocket<GenericListMessageHandler<FormListing>> {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param messageReceivedCallbackParam Callback for when a message is received.
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     */
    public SQLUtilWebSocketExecuteSQLClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<FormListing> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam) {
        super(endpointBaseUrlParam,
                new GenericFormListingMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getExecuteSQLWebSocket(
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves all the Descendants (Forms) for the {@code formToGetTableFormsForParam}.
     *
     * @param formWithSQLFieldParam The Fluid Form to Execute custom SQL for.
     *
     * @return The SQL Execution result as {@code Form}'s.
     */
    public List<FormListing> executeSQLSynchronized(Form formWithSQLFieldParam) {

        this.getMessageHandler().clear();

        if(formWithSQLFieldParam == null)
        {
            return null;
        }

        if(formWithSQLFieldParam.getFormFields() == null ||
                formWithSQLFieldParam.getFormFields().isEmpty())
        {
            return this.getMessageHandler().getReturnValue();
        }

        //Validate the echo...
        if(formWithSQLFieldParam.getEcho() == null ||
                formWithSQLFieldParam.getEcho().trim().isEmpty())
        {
            formWithSQLFieldParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<FormListing>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);

        //Send the actual message...
        this.sendMessage(formWithSQLFieldParam);

        try {
            List<FormListing> returnValue = completableFuture.get(
                            this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if(this.getMessageHandler().isConnectionClosed())
            {
                throw new FluidClientException(
                        "SQLUtil-WebSocket-ExecuteSQL: " +
                                "The connection was closed by the server prior to the response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            return returnValue;
        }
        //Interrupted...
        catch (InterruptedException exceptParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-Interrupted-ExecuteSQL: " +
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
                        "SQLUtil-WebSocket-ExecuteSQL: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-ExecuteSQL: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }
}
