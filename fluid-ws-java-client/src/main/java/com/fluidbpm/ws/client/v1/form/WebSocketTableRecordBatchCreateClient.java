/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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

import com.fluidbpm.program.api.vo.form.TableRecordListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java WebSocket client for bounded table-record creation.
 *
 * @see TableRecordListing
 */
public class WebSocketTableRecordBatchCreateClient extends
        ABaseClientWebSocket<WebSocketTableRecordBatchCreateClient.Handler, TableRecordListing> {

    /**
     * Constructor that sets the service ticket and request options.
     *
     * @param endpoint URL to base endpoint.
     * @param ticket The server-issued service ticket as hexadecimal.
     * @param timeout The response timeout in milliseconds.
     */
    public WebSocketTableRecordBatchCreateClient(
            String endpoint,
            String ticket,
            long timeout
    ) {
        super(endpoint, null, timeout, WS.Path.FormContainerTableRecord.Version1.ROOT_WEB_SOCKET + "/batch/" + ticket);
        this.setServiceTicket(ticket);
    }

    /**
     * Creates a synchronous batch. Reconcile a lost response before retrying.
     *
     * @param request The table records to create.
     * @return Created records in input order.
     */
    public TableRecordListing createTableRecordsBatch(TableRecordListing request) {
        if (request == null || request.getListing() == null || request.getListing().isEmpty()
                || request.getListing().size() > TableRecordListing.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("A batch must contain 1..250 records.");
        }
        String reference = this.initNewRequest();
        try {
            request.setEcho(reference);
            this.sendMessage(request, reference);
            List<TableRecordListing> result = this.getHandler(reference).getCF().get(
                    this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);
            if (this.getHandler(reference).isConnectionClosed() || result == null || result.isEmpty()) {
                throw new FluidClientException(
                        "No complete table-operation response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }
            return result.get(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FluidClientException(
                    "Table operation interrupted.",
                    e, FluidClientException.ErrorCode.IO_ERROR);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof FluidClientException) {
                throw (FluidClientException) e.getCause();
            }
            throw new FluidClientException(
                    "Table operation failed.",
                    e, FluidClientException.ErrorCode.IO_ERROR);
        } catch (TimeoutException e) {
            throw new FluidClientException(
                    "Table operation timed out; reconcile writes before retrying.",
                    e, FluidClientException.ErrorCode.IO_ERROR);
        } finally {
            this.removeHandler(reference);
        }
    }

    /**
     * Creates a new handler for a table-record response.
     *
     * @return New response handler.
     */
    @Override
    public Handler getNewHandlerInstance() {
        return new Handler(this.webSocketClient);
    }

    /**
     * Message handler for the table-record response envelope.
     */
    public static class Handler extends AGenericListMessageHandler<TableRecordListing> {
        public Handler(WebSocketClient client) {
            super(null, client);
        }

        @Override
        public TableRecordListing getNewInstanceBy(JsonObject json) {
            return new TableRecordListing(json);
        }
    }

}
