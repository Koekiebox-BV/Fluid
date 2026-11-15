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

package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
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
 * Java WebSocket client for bounded table-record page retrieval.
 *
 * @see FormListing
 */
public class SQLUtilWebSocketGetTableFormsPageClient extends
        ABaseClientWebSocket<SQLUtilWebSocketGetTableFormsPageClient.Handler, FormListing> {

    /**
     * Constructor that sets the service ticket and request options.
     *
     * @param endpoint URL to base endpoint.
     * @param ticket The server-issued service ticket as hexadecimal.
     * @param timeout The response timeout in milliseconds.
     * @param includeFields Whether to populate field data.
     * @param definitionId Form definition filter; nonpositive values include all definitions.
     * @param limit Maximum number of records to retrieve.
     * @param offset Zero-based offset of the first record.
     */
    public SQLUtilWebSocketGetTableFormsPageClient(
            String endpoint,
            String ticket,
            long timeout,
            boolean includeFields,
            long definitionId,
            int limit,
            int offset
    ) {
        super(endpoint, null, timeout,
                WS.Path.SQLUtil.Version1.getTableFormsPageWebSocket(
                        includeFields, ticket, definitionId, limit, offset));
        this.setServiceTicket(ticket);
    }

    /**
     * Retrieves a page of table records for the parent form.
     *
     * @param request The parent form containing the ID.
     * @return The requested page and total matching record count.
     */
    public FormListing getTableFormsPage(Form request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("Parent form ID required.");
        }
        String reference = this.initNewRequest();
        try {
            request.setEcho(reference);
            this.sendMessage(request, reference);
            List<FormListing> result = this.getHandler(reference).getCF().get(
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
    public static class Handler extends AGenericListMessageHandler<FormListing> {
        public Handler(WebSocketClient client) {
            super(null, client);
        }

        @Override
        public FormListing getNewInstanceBy(JsonObject json) {
            return new FormListing(json);
        }
    }
}
