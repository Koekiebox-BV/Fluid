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

package com.fluidbpm.ws.client.v1.sqlutil.sqlnative;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code SQLUtil} related actions.
 * Implementation is making use of {@link CompletableFuture}.
 *
 * {@code this} client allows one to execute native SQL using
 * one of the configured SQL DataSources.
 *
 * @author jasonbruwer on 2018-05-26
 * @since v1.8
 *
 * @see JSONObject
 * @see SQLResultSet
 */
public class SQLUtilWebSocketExecuteNativeSQLClient extends
        ABaseClientWebSocket<AGenericListMessageHandler<SQLResultSet>> {

    /**
     * Constructor that sets the Service Ticket from authentication.
     * The ResultSet will not be compressed.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param messageReceivedCallbackParam Callback for when a message is received.
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     *
     * @see com.fluidbpm.program.api.vo.compress.CompressedResponse
     */
    public SQLUtilWebSocketExecuteNativeSQLClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<SQLResultSet> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam) {
        this(endpointBaseUrlParam,
                messageReceivedCallbackParam,
                serviceTicketAsHexParam,
                timeoutInMillisParam,
                false);
    }

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param messageReceivedCallbackParam Callback for when a message is received.
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     * @param compressResponseParam Compress the SQL Result in Base-64.
     *
     * @see com.fluidbpm.program.api.vo.compress.CompressedResponse
     */
    public SQLUtilWebSocketExecuteNativeSQLClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<SQLResultSet> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean compressResponseParam) {
        super(endpointBaseUrlParam,
                new SQLResultSetMessageHandler(messageReceivedCallbackParam, compressResponseParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getExecuteNativeSQLWebSocket(
                        serviceTicketAsHexParam,
                        compressResponseParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Executes a native SQL query on the remote Fluid instance.
     *
     * The relevant access must exist.
     *
     * @param nativeSQLQueryParam The SQL Query to execute.
     *
     * @return The SQL Execution result as {@code SQLResultSet}'s.
     */
    public List<SQLResultSet> executeNativeSQLSynchronized(
            NativeSQLQuery nativeSQLQueryParam) {

        this.getMessageHandler().clear();

        if(nativeSQLQueryParam == null)
        {
            return null;
        }

        if(nativeSQLQueryParam.getDatasourceName() == null ||
                nativeSQLQueryParam.getDatasourceName().isEmpty())
        {
            return this.getMessageHandler().getReturnValue();
        }

        //No query to execute...
        if((nativeSQLQueryParam.getQuery() == null ||
                nativeSQLQueryParam.getQuery().isEmpty()) &&
                (nativeSQLQueryParam.getStoredProcedure() == null ||
                        nativeSQLQueryParam.getStoredProcedure().isEmpty()))
        {
            return this.getMessageHandler().getReturnValue();
        }

        //Validate the echo...
        if(nativeSQLQueryParam.getEcho() == null ||
                nativeSQLQueryParam.getEcho().trim().isEmpty())
        {
            nativeSQLQueryParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<SQLResultSet>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);

        //Send the actual message...
        this.sendMessage(nativeSQLQueryParam);

        try {
            List<SQLResultSet> returnValue = completableFuture.get(
                            this.getTimeoutInMillis(),TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if(this.getMessageHandler().isConnectionClosed())
            {
                throw new FluidClientException(
                        "SQLUtil-WebSocket-ExecuteNativeSQL: " +
                                "The connection was closed by the server prior to the response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            return returnValue;
        }
        //Interrupted...
        catch (InterruptedException exceptParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-ExecuteNativeSQL: " +
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
                        "SQLUtil-WebSocket-ExecuteNativeSQL: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-ExecuteNativeSQL: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }
}
