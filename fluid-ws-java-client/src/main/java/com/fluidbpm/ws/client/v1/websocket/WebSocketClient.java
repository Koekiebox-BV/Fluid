package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.ASNMapperFactory;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a WebSocket client for managing communication with a WebSocket server.
 * This class includes methods to handle connection lifecycle events, processing received messages,
 * and sending messages through the WebSocket.
 *
 * @param <RespHandler> Type extending {@link IMessageResponseHandler} for handling server responses.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
@ClientEndpoint()
public class WebSocketClient<RespHandler extends IMessageResponseHandler> {
    private Session userSession = null;
    private final Map<String, RespHandler> messageHandlers;

    @Getter
    protected int sentMessages = 0;
    @Getter
    protected int receivedMessages = 0;

    @Getter
    private final Mode mode;

    private ASNMapperFactory asnMapperFactory;

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
     * @throws DeploymentException If there is an error establishing the web socket connection.
     * @throws IOException If an I/O error occurs during the connection process.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam
    ) throws DeploymentException, IOException {
        this(endpointURIParam, messageHandlersParam, Mode.Text, ASNGlobal.Type.UNKNOWN);
    }

    /**
     * Default constructor with an endpoint.
     * Establishes a connection to remote.
     *
     * @param endpointURIParam The Endpoint URI.
     * @param messageHandlersParam Map of message handlers.
     * @param mode Mode to use.
     * @throws DeploymentException If there is a connection problem.
     * @throws IOException If there is a I/O problem.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String, RespHandler> messageHandlersParam,
            Mode mode,
            int requestAsn1Type
    ) throws DeploymentException, IOException {
        this.messageHandlers = messageHandlersParam;
        this.mode = mode;
        this.asnMapperFactory = new ASNMapperFactory(requestAsn1Type);

        this.sentMessages = 0;
        this.receivedMessages = 0;

        //ContainerProvider.getWebSocketContainer()
        ClientManager clMng = ClientManager.createClient(GrizzlyClientContainer.class.getName());

        clMng.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, String.valueOf(15000));
        WebSocketContainer container = clMng;

        //WebSocketContainer container = GrizzlyContainerProvider.getWebSocketContainer();
        //WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        int tenMB = (1000000 * 10);
        int oneGB = (tenMB * 100);

        container.setDefaultMaxTextMessageBufferSize(oneGB);
        container.setDefaultMaxBinaryMessageBufferSize(oneGB);

        container.connectToServer(this, endpointURIParam);
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
        // No session timeout:
        this.userSession.setMaxIdleTimeout(0L);
        this.receivedMessages = 0;
        this.sentMessages = 0;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession The userSession which is getting closed.
     * @param reason The reason for connection close.
     *
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;

        if (this.messageHandlers != null) {
            this.messageHandlers.values().forEach(IMessageResponseHandler::connectionClosed);
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when
     * a client sends a message.
     * @param message The text message.
     */
    @OnMessage
    public void onMessage(String message) {
        this.receivedMessages++;

        boolean handlerFoundForMsg = false;
        for (IMessageResponseHandler handler : new ArrayList<IMessageResponseHandler>(this.messageHandlers.values())) {
            Object qualifyObj = handler.doesHandlerQualifyForProcessing(message);
            if (qualifyObj instanceof Error) {
                handler.handleMessage(qualifyObj);
            } else if (qualifyObj instanceof JsonObject) {
                handler.handleMessage(qualifyObj);
                handlerFoundForMsg = true;
                break;
            }
        }

        if (!handlerFoundForMsg) {
            throw new FluidClientException(
                    "(Text): No handler found for message;\n"+message,
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }
    }

    /**
     * Callback hook for receiving binary message events.
     * Invoked when the server sends a binary message to the client.
     * @param message The binary message payload received from the server.
     */
    @OnMessage
    public void onMessage(byte[] message) {
        this.receivedMessages++;

        boolean handlerFoundForMsg = false;
        for (IMessageResponseHandler handler : new ArrayList<IMessageResponseHandler>(this.messageHandlers.values())) {
            Object qualifyObj = handler.doesHandlerQualifyForProcessing(message);
            if (qualifyObj instanceof Error) {
                handler.handleMessage(qualifyObj);
            } else if (qualifyObj instanceof ABaseFluidVO) {
                handler.handleMessage(qualifyObj);
                handlerFoundForMsg = true;
                break;
            } else if (qualifyObj instanceof JsonObject) {
                handler.handleMessage(qualifyObj);
                handlerFoundForMsg = true;
                break;
            }
        }

        if (!handlerFoundForMsg) {
            throw new FluidClientException(
                    "(Binary): No handler found for message;\n"+ BaseEncoding.base16().encode(message),
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }
    }

    /**
     * Callback hook for handling errors that occur during WebSocket communication.
     * This method is invoked when an error is encountered either due to a session issue
     * or other exceptions during WebSocket events.
     *
     * @param session The WebSocket session during which the error occurred.
     *                It may be {@code null} if the session is unavailable.
     * @param t The {@code Throwable} error or exception that was encountered.
     */
    @OnError
    public void onError(Session session, Throwable t) {
        System.err.println("WS error: " + (session != null ? session.getId() : "n/a") + " -> ");
        t.printStackTrace();
        for (IMessageResponseHandler handler : new ArrayList<IMessageResponseHandler>(this.messageHandlers.values())) {
            Error err = new Error(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, t.getMessage());
            handler.handleMessage(err);
        }
    }

    /**
     * Send a message.
     *
     * @param aFluidVo The JSON Object to send.
     */
    public void sendMessage(ABaseFluidVO aFluidVo) {
        if (aFluidVo == null) {
            throw new FluidClientException("No Object to send!", FluidClientException.ErrorCode.IO_ERROR);
        }

        if (this.mode == Mode.Binary) {
            if (aFluidVo instanceof BaseTransmission) {
                BaseTransmission bt = (BaseTransmission)aFluidVo;
                this.sendMessage(this.asnMapperFactory.writeObjectForSend(bt));
            } else {
                this.sendMessage(this.asnMapperFactory.writeObjectForSend(aFluidVo));
            }
        } else if (aFluidVo instanceof ABaseFluidGSONObject) {
            ABaseFluidGSONObject casted = (ABaseFluidGSONObject)aFluidVo;
            this.sendMessage(casted.toJsonObject().toString());
        } else {
            throw new FluidClientException(
                    "Unable to process '"+aFluidVo+"'.",
                    FluidClientException.ErrorCode.ASN_1_ERROR
            );
        }
    }

    /**
     * Send a message as text.
     * @param messageToSend The text message to send.
     */
    public void sendMessage(String messageToSend) {
        if (this.userSession == null) {
            throw new FluidClientException(
                    "User Session is not set. Verify if connection is open.",
                    FluidClientException.ErrorCode.SESSION_EXPIRED
            );
        }

        RemoteEndpoint.Async asyncRemote = null;
        if ((asyncRemote = this.userSession.getAsyncRemote()) == null) {
            throw new FluidClientException(
                    "Remote Session is not set. Verify if connection is open.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }
        asyncRemote.sendText(messageToSend);
        this.sentMessages++;
    }

    /**
     * Send a message as text.
     * @param messageToSend The text message to send.
     */
    public void sendMessage(byte[] messageToSend) {
        if (this.userSession == null) {
            throw new FluidClientException(
                    "(send-binary) User Session is not set. Verify if connection is open.",
                    FluidClientException.ErrorCode.SESSION_EXPIRED
            );
        }

        RemoteEndpoint.Async asyncRemote = null;
        if ((asyncRemote = this.userSession.getAsyncRemote()) == null) {
            throw new FluidClientException(
                    "(send-binary) Remote Session is not set. Verify if connection is open.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }
        asyncRemote.sendBinary(ByteBuffer.wrap(messageToSend));
        this.sentMessages++;
    }

    /**
     * Closes the Web Socket User session.
     */
    public void closeSession() {
        if (this.userSession == null) return;

        try {
            this.userSession.close();
        } catch (IOException e) {
            throw new FluidClientException(
                    "Unable to close session. "+e.getMessage(), e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Check to see whether the session is open.
     *
     * @return {@code true} if session is open, otherwise {@code false}.
     */
    public boolean isSessionOpen() {
        if (this.userSession == null) return false;
        return this.userSession.isOpen();
    }

    /**
     * Return the current user session id.
     *
     * @return {@code Session ID} if session is open, otherwise {@code null}.
     */
    public String getSessionId(){
        if (this.userSession == null) return null;
        return this.userSession.getId();
    }

    /**
     * Sets the type of the ASN (Abstract Syntax Notation) mapper factory.
     * If the {@code asnMapperFactory} is {@code null}, the method does nothing.
     *
     * @param type an integer representing the desired configuration type
     *             for the {@code asnMapperFactory}.
     */
    public void setAsnMapperFactoryType(int type) {
        if (this.asnMapperFactory == null) return;
        this.asnMapperFactory.setType(type);
    }
}
