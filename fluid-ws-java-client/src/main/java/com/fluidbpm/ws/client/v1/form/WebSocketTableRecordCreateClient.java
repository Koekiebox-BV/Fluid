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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code TableRecord} related actions.
 *
 * IMPORTANT: This class is Thread safe.
 *
 * @author jasonbruwer on 2017/06/10
 * @since v1.5
 *
 * @see JSONObject
 * @see WS.Path.FormContainer
 * @see Form
 * @see com.fluidbpm.program.api.vo.form.TableRecord
 */
public class WebSocketTableRecordCreateClient extends
        ABaseClientWebSocket<WebSocketTableRecordCreateClient.CreateTableRecordMessageHandler> {

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
    public WebSocketTableRecordCreateClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<TableRecord> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam) {
        super(endpointBaseUrlParam,
                new CreateTableRecordMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.FormContainerTableRecord.Version1.formContainerTableRecordCreateWebSocket(
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Creates a new (TableRecord) for the {@code formToCreateParam}.
     *
     * @param tableRecordToCreateParam The Fluid Form to create.
     *
     * @return The {@code tableRecordToCreateParam} created as {@code TableRecord}.
     */
    public TableRecord createTableRecordSynchronized(
            TableRecord tableRecordToCreateParam) {

        this.getMessageHandler().clear();

        if(tableRecordToCreateParam == null)
        {
            return null;
        }

        //Send all the messages...
        if(tableRecordToCreateParam.getEcho() == null ||
                tableRecordToCreateParam.getEcho().trim().isEmpty())
        {
            tableRecordToCreateParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<TableRecord>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);
        
        //Send the actual message...
        this.sendMessage(tableRecordToCreateParam);

        try {
            List<TableRecord> returnValue = completableFuture.get(
                    this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if(this.getMessageHandler().isConnectionClosed())
            {
                throw new FluidClientException(
                        "WebSocket-CreateTableRecord: " +
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
                    "WebSocket-Interrupted-CreateTableRecord: " +
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
                        "WebSocket-CreateTableRecord: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "WebSocket-CreateTableRecord: " +
                            "Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Creates a new Table Record from {@code formToGetAncestorsForForParam}
     * asynchronously.
     *
     * @param tableRecordParamToCreateParam The Fluid Table Record to create.
     */
    public void createTableRecordAsynchronous(TableRecord tableRecordParamToCreateParam) {

        if(tableRecordParamToCreateParam == null)
        {
            return;
        }

        //Send the actual message...
        this.sendMessage(tableRecordParamToCreateParam);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    static class CreateTableRecordMessageHandler extends GenericListMessageHandler<TableRecord>
    {
        private TableRecord returnedTableRecord;

        /**
         * The default constructor that sets a ancestor message handler.
         *
         * @param messageReceivedCallbackParam The optional message callback.
         */
        public CreateTableRecordMessageHandler(
                IMessageReceivedCallback<TableRecord> messageReceivedCallbackParam) {

            super(messageReceivedCallbackParam);
        }

        /**
         * New {@code Form} by {@code jsonObjectParam}
         *
         * @param jsonObjectParam The JSON Object to parse.
         * @return new {@code Form}.
         */
        @Override
        public TableRecord getNewInstanceBy(JSONObject jsonObjectParam) {

            this.returnedTableRecord = new TableRecord(jsonObjectParam);

            return this.returnedTableRecord;
        }

        /**
         * Gets the value from that was returned after the WS call.
         *
         * @return The returned form.
         */
        public TableRecord getReturnedTableRecord() {
            return this.returnedTableRecord;
        }
    }
}
