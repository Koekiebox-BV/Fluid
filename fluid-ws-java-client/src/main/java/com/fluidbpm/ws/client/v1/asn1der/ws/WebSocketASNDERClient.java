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

package com.fluidbpm.ws.client.v1.asn1der.ws;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.ASNMapperError;
import com.fluidbpm.ws.client.v1.asn1der.ASNMapperFactory;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ServerProcessStats;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bouncycastle.asn1.ASN1Sequence;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.fluidbpm.ws.client.v1.asn1der.ASNGlobal.Type.ERROR_TYPE;

/**
 * A WebSocket client implementation for handling ASNDER-based transmissions.
 * This class extends the generic functionality of {@code ABaseClientWebSocket}
 * and provides support for processing transmission messages using a WebSocket protocol.
 *
 * The client handles WebSocket communication by sending requests of type {@code BaseTransmission}
 * and processing responses in a synchronized manner with explicit error handling for interruptions,
 * timeouts, and server-side disconnections.
 *
 * This implementation is tailored towards single-session communication
 * and supports custom service tickets, message handling, and unique request identifiers.
 *
 * The {@code TransmissionMessageHandler} is used internally by this class to manage WebSocket
 * message parsing and response handling.
 *
 * <h2>Features</h2>
 * - Processes ASNDER-based transmission messages.
 * - Provides timeout handling for synchronous requests.
 * - Extends base WebSocket client functionality with a specialized message handler.
 * - Adds support for error conditions such as disconnections and execution failures.
 * - Leverages unique request identifiers for managing synchronous responses.
 *
 * <h2>Error Handling</h2>
 * This client throws exceptions such as {@code FluidClientException} in the following conditions:
 * - If the response is interrupted during processing.
 * - If the server closes the WebSocket connection before a response is received.
 * - If a timeout occurs while waiting for a response.
 * - If an execution error arises due to internal exceptions.
 */
public class WebSocketASNDERClient extends
        ABaseClientWebSocket<WebSocketASNDERClient.TransmissionMessageHandler, BaseTransmission> {

    /**
     * Constructs a new instance of the WebSocketASNDERClient. This client is designed to
     * initialize and manage a WebSocket connection for transmitting and receiving
     * {@code BaseTransmission} messages. It extends the functionality of
     * {@code ABaseClientWebSocket} and supports handling specific messaging operations
     * related to ASN transmission.
     *
     * @param endpointBase The base URL endpoint for the WebSocket connection.
     * @param msgRecCallback The callback implementation to handle received messages and errors.
     *                        It processes {@code BaseTransmission} objects.
     * @param serviceTicketAsHex A hexadecimal string representation of the service ticket, used
     *                           to authenticate and generate the connection URL.
     * @param timeoutInMillis The timeout value in milliseconds for the WebSocket connection.
     */
    public WebSocketASNDERClient(
            String endpointBase,
            IMessageReceivedCallback<BaseTransmission> msgRecCallback,
            String serviceTicketAsHex,
            long timeoutInMillis
    ) {
        super(
                endpointBase,
                msgRecCallback,
                timeoutInMillis,
                ASNGlobal.Path.transmissionPath(serviceTicketAsHex),
                WebSocketClient.Mode.Binary
        );
        this.setServiceTicket(serviceTicketAsHex);
    }

    /**
     * Constructs a new instance of the WebSocketASNDERClient. This constructor is a simplified
     * version that initializes the client with mandatory parameters to manage a WebSocket
     * connection for transmitting and receiving {@code BaseTransmission} messages.
     * It utilizes default configurations for message handling and connection initialization.
     *
     * @param endpointBase The base URL endpoint for the WebSocket connection.
     * @param serviceTicketAsHex A hexadecimal string representation of the service ticket, used
     *                           to authenticate and generate the connection URL.
     * @param timeoutInMillis The timeout value in milliseconds for the WebSocket connection.
     */
    public WebSocketASNDERClient(
            String endpointBase,
            String serviceTicketAsHex,
            long timeoutInMillis
    ) {
        this(endpointBase, null, serviceTicketAsHex, timeoutInMillis);
    }

    /**
     * Sends the specified {@code BaseTransmission} request via WebSocket, processes the response,
     * and returns the first message received in the response list.
     * If the request does not contain an echo identifier, a new unique echo identifier is generated and set.
     * Handles various exceptions such as interruptions, execution errors, and timeouts during the process.
     *
     * @param req The {@code BaseTransmission} request to be sent. It must not be null.
     *            If the `echo` property in the request is null or empty, a new unique identifier is assigned to it.
     * @return The first {@code BaseTransmission} object from the response, or {@code null} if no response is received.
     * @throws FluidClientException If an interruption, execution, timeout, or WebSocket connection error occurs.
     */
    public BaseTransmission request(BaseTransmission req) {
        if (req == null) return null;

        //Send all the messages...
        if (req.getEcho() == null || req.getEcho().trim().isEmpty()) {
            req.setEcho(UtilGlobal.randomUUID());
        }

        //Start a new request...
        String uniqueReqId = this.initNewRequest();

        if (req.getId() != null) {
            this.webSocketClient.setAsnMapperFactoryType(req.getId().intValue());
        }

        //Send the actual message...
        this.sendMessage(req, uniqueReqId);

        try {
            List<BaseTransmission> returnValue = this.getHandler(uniqueReqId)
                    .getCF()
                    .get(this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

            //Connection was closed.. this is a problem....
            if (this.getHandler(uniqueReqId).isConnectionClosed()) {
                throw new FluidClientException(
                        "WebSocket-Transmission: " +
                                "The connection was closed by the server prior to the response received.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }
            if (returnValue == null || returnValue.isEmpty()) return null;

            return returnValue.get(0);
        } catch (InterruptedException error) {
            //Interrupted...
            throw new FluidClientException(
                    "WebSocket-Interrupted-Transmission: " +
                            error.getMessage(),
                    error,
                    FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
        } catch (ExecutionException executeProblem) {
            //Error on the web-socket...
            Throwable cause = executeProblem.getCause();
            //Fluid client exception...
            if (cause instanceof FluidClientException) {
                throw (FluidClientException) cause;
            } else {
                throw new FluidClientException(
                        "WebSocket-Transmission: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        } catch (TimeoutException timeout) {
            //Timeout...
            String errMessage = this.getExceptionMessageVerbose(
                    "WebSocket-Transmission",
                    uniqueReqId,
                    req);
            throw new FluidClientException(
                    errMessage, FluidClientException.ErrorCode.IO_ERROR);
        } finally {
            this.removeHandler(uniqueReqId);
        }
    }

    /**
     * Constructs and sends a {@code BaseTransmission} request to populate the full payload,
     * processes the response, and extracts the {@code PayloadPopulate} object from it.
     * This method communicates with a predefined endpoint path for general payload population.
     *
     * @return The populated {@code PayloadPopulate} object extracted from the response,
     *         or {@code null} if the response does not contain the expected payload.
     */
    public PayloadPopulate requestFullPayloadPopulate() {
        BaseTransmission btPayPop = new BaseTransmission(ASNGlobal.Type.SKIP_TRANSMISSION_OBJ);
        btPayPop.setRequestObject(new RequestObject(ASNGlobal.Path.General.GENERAL_PAYLOAD_POPULATE));

        BaseTransmission rsp = this.request(btPayPop);
        return rsp.getPayloadPopulate();
    }

    /**
     * Creates and returns a new instance of the {@code TransmissionMessageHandler}.
     * This handler is initialized with the provided message received callback and WebSocket client,
     * enabling it to handle incoming {@code BaseTransmission} messages and manage message processing.
     *
     * @return A new {@code TransmissionMessageHandler} instance configured with the current message received callback
     *         and WebSocket client.
     */
    @Override
    public TransmissionMessageHandler getNewHandlerInstance() {
        return new TransmissionMessageHandler(this.messageReceivedCallback, this.webSocketClient);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    @Getter
    public static class TransmissionMessageHandler extends AGenericListMessageHandler<BaseTransmission> {
        private BaseTransmission returnedBT;

        public TransmissionMessageHandler(
                IMessageReceivedCallback<BaseTransmission> messageReceivedCallback,
                WebSocketClient<?> webSocketClient
        ) {
            super(messageReceivedCallback, webSocketClient);
        }

        @Override
        public BaseTransmission getNewInstanceBy(JsonObject jsonObject) {
            this.returnedBT = new BaseTransmission();
            return this.returnedBT;
        }

        /**
         * Determines whether the handler qualifies for processing based on the given DER-encoded data.
         * The method decodes the provided ASN.1 DER sequence and determines if it corresponds to an error
         * object or creates a BaseTransmission object for further processing.
         *
         * @param der the byte array containing the DER-encoded data to be evaluated.
         *            This data is expected to represent an ASN.1 sequence that can be parsed
         *            to determine its type and content.
         * @return an {@code Object} representing the result of processing. If the data corresponds
         *         to an error, an error-related object is returned. If it corresponds to a BaseTransmission
         *         or other type, the appropriate object is created and returned.
         */
        @Override
        public Object doesHandlerQualifyForProcessing(byte[] der) {
            long ts = System.currentTimeMillis();
            ASNMapperError initial = new ASNMapperError();
            final ASN1Sequence asn1Seq = initial.initSeq(der);

            int typeCode = initial.asInt(asn1Seq.getObjectAt(ASNBaseMapper.Map.ID), "Type Code");
            if (typeCode == ERROR_TYPE) {
                return initial.decode(asn1Seq);
            } else {
                // We want the [BaseTransmission] object:
                String echo = initial.asGeneralTxt(asn1Seq.getObjectAt(ASNBaseMapper.Map.ECHO), "Echo");
                if (this.expectedEchoMessagesBeforeComplete.contains(echo)) {
                    BaseTransmission bt = new ASNMapperFactory(typeCode).readObjectFromReceived(asn1Seq);
                    ServerProcessStats servStats = bt.getServerProcessStats();
                    if (servStats != null) {
                        long serverRespondedAt = servStats.getAppLogicTsResponded();

                        PerfStats.increment(PerfStats.Label.Asn1Der_Latency, ts - serverRespondedAt);
                        PerfStats.increment(PerfStats.Label.Asn1Der_ServerAppProcessDuration, servStats.getProcessingDurationMs());
                        PerfStats.increment(PerfStats.Label.Asn1Der_ServerAppDecodeDuration, servStats.getDecodeRequestDurationMs());
                        PerfStats.increment(PerfStats.Label.Asn1Der_ServerAppEncodeDuration, servStats.getEncodeResponseDurationMs());
                    }
                    return bt;
                }
                return null;
            }
        }
    }
}
