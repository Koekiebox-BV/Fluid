package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import lombok.Getter;

import java.net.URI;
import java.util.Map;

/**
 * Represents a WebSocket client for managing communication with a WebSocket server.
 * This class includes methods to handle connection lifecycle events, processing received messages,
 * and sending messages through the WebSocket.
 *
 * This implementation uses Netty for production-ready WebSocket communication.
 *
 * @param <RespHandler> Type extending {@link IMessageResponseHandler} for handling server responses.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public class WebSocketClient<RespHandler extends IMessageResponseHandler> {
    private final com.fluidbpm.ws.client.v1.netty.websocket.WebSocketClient<RespHandler> nettyClient;

    @Getter
    private final Mode mode;

    public enum Mode {
        Text,
        Binary
    }

    /**
     * Constructs a WebSocketClient with the specified endpoint URI and message handlers.
     * Establishes a web socket connection to the remote server.
     *
     * @param endpointURIParam The URI of the web socket endpoint to connect to.
     * @param messageHandlersParam A map of message handlers, where the keys represent
     *                              specific message types and the values are responsible
     *                              for handling corresponding messages.
     * @throws Exception If there is an error establishing the web socket connection.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam
    ) throws Exception {
        this(endpointURIParam, messageHandlersParam, Mode.Text, ASNGlobal.Type.UNKNOWN);
    }

    /**
     * Default constructor with an endpoint.
     * Establishes a connection to remote.
     *
     * @param endpointURIParam The Endpoint URI.
     * @param messageHandlersParam Map of message handlers.
     * @param mode Mode to use.
     * @param requestAsn1Type ASN.1 type for the request.
     * @throws Exception If there is a connection problem.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam,
            Mode mode,
            int requestAsn1Type
    ) {
        this.mode = mode;
        this.nettyClient = new com.fluidbpm.ws.client.v1.netty.websocket.WebSocketClient<>(
                endpointURIParam,
                messageHandlersParam,
                com.fluidbpm.ws.client.v1.netty.websocket.WebSocketClient.Mode.valueOf(mode.name()),
                requestAsn1Type
        );
    }


    /**
     * Send a message.
     *
     * @param aFluidVo The JSON Object to send.
     */
    public void sendMessage(ABaseFluidVO aFluidVo) {
        this.nettyClient.sendMessage(aFluidVo);
    }

    /**
     * Send a message as text.
     * @param messageToSend The text message to send.
     */
    public void sendMessage(String messageToSend) {
        this.nettyClient.sendMessage(messageToSend);
    }

    /**
     * Send a message as binary.
     * @param messageToSend The binary message to send.
     */
    public void sendMessage(byte[] messageToSend) {
        this.nettyClient.sendMessage(messageToSend);
    }

    /**
     * Closes the Web Socket User session.
     */
    public void closeSession() {
        this.nettyClient.closeSession();
    }

    /**
     * Check to see whether the session is open.
     *
     * @return {@code true} if session is open, otherwise {@code false}.
     */
    public boolean isSessionOpen() {
        return this.nettyClient.isSessionOpen();
    }

    /**
     * Return the current user session id.
     *
     * @return {@code Session ID} if session is open, otherwise {@code null}.
     */
    public String getSessionId(){
        return this.nettyClient.getSessionId();
    }

    /**
     * Gets the count of sent messages.
     *
     * @return The number of messages sent
     */
    public int getSentMessages() {
        return this.nettyClient.getSentMessages();
    }

    /**
     * Gets the count of received messages.
     *
     * @return The number of messages received
     */
    public int getReceivedMessages() {
        return this.nettyClient.getReceivedMessages();
    }

    /**
     * Sets the type of the ASN (Abstract Syntax Notation) mapper factory.
     * If the {@code asnMapperFactory} is {@code null}, the method does nothing.
     *
     * @param type an integer representing the desired configuration type
     *             for the {@code asnMapperFactory}.
     */
    public void setAsnMapperFactoryType(int type) {
        this.nettyClient.setAsnMapperFactoryType(type);
    }
}
