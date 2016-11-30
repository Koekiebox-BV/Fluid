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
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

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
 * @since v1.3
 *
 * @see JSONObject
 * @see Form
 */
public class SQLUtilWebSocketExecuteSQLClient extends
        ABaseClientWebSocket<GenericListMessageHandler> {

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
    public List<FormListing> executeSQLSynchronized(
            Form formWithSQLFieldParam) {

        this.messageHandler.clear();

        if(formWithSQLFieldParam == null)
        {
            return null;
        }

        if(formWithSQLFieldParam.getFormFields() == null ||
                formWithSQLFieldParam.getFormFields().isEmpty())
        {
            return this.messageHandler.getReturnValue();
        }

        //Send all the messages...
        List<String> echoMessagesExpected = new ArrayList();
        if(formWithSQLFieldParam.getEcho() == null || formWithSQLFieldParam.getEcho().isEmpty())
        {
            throw new FluidClientException("Echo needs to be set to bind to return.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        echoMessagesExpected.add(formWithSQLFieldParam.getEcho());

        //Send the actual message...
        this.sendMessage(formWithSQLFieldParam);

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
                        "SQLUtil-WebSocket-ExecuteSQL: Timeout while waiting for all return data. There were '"
                                +this.messageHandler.getReturnValue().size()
                                +"' items after a Timeout of "+(
                                TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                        ,FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }
}
