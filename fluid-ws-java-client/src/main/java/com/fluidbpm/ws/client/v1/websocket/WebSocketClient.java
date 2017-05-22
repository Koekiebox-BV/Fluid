package com.fluidbpm.ws.client.v1.websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.*;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.ws.client.FluidClientException;

/**
 * The {@code ClientEndpoint} Web Socket client.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
@ClientEndpoint()
public class WebSocketClient {

    private Session userSession = null;
    private IMessageHandler messageHandler;

    /**
     * Default constructor with an endpoint.
     * Establishes a connection to remote.
     *
     * @param endpointURI The Endpoint URI.
     * @throws DeploymentException If there is a connection problem.
     * @throws IOException If there is a I/O problem.
     */
    public WebSocketClient(URI endpointURI) throws DeploymentException, IOException {

        //ContainerProvider.getWebSocketContainer()

        WebSocketContainer container = ClientManager.createClient(GrizzlyClientContainer.class.getName());

        //WebSocketContainer container = GrizzlyContainerProvider.getWebSocketContainer();
        //WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        container.connectToServer(this, endpointURI);
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSessionParam the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSessionParam) {

        this.userSession = userSessionParam;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSessionParam The userSession which is getting closed.
     * @param reasonParam The reason for connection close.
     *
     */
    @OnClose
    public void onClose(Session userSessionParam, CloseReason reasonParam) {

        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when
     * a client sends a message.
     * @param messageParam The text message      
     */
    @OnMessage
    public void onMessage(String messageParam) {

        if (this.messageHandler != null) {

            this.messageHandler.handleMessage(messageParam);
        }
    }

    /**
     * Register message handler.
     *
     * @param msgHandlerParam The message handler.
     */
    public void setMessageHandler(IMessageHandler msgHandlerParam) {
        this.messageHandler = msgHandlerParam;
    }

    /**
     * Send a message.
     *
     * @param aBaseFluidJSONObjectParam The JSON Object to send.
     */
    public void sendMessage(ABaseFluidJSONObject aBaseFluidJSONObjectParam) {

        if(aBaseFluidJSONObjectParam == null)
        {
            throw new FluidClientException(
                    "No JSON Object to send.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }
        else
        {
            this.sendMessage(aBaseFluidJSONObjectParam.toJsonObject().toString());
        }
    }

    /**
     * Send a message as text.
     *
     * @param messageToSendParam The text message to send.
     */
    public void sendMessage(String messageToSendParam) {

        if(this.userSession == null)
        {
            throw new FluidClientException(
                    "User Session is not set. Check if connection is open.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }

        RemoteEndpoint.Async asyncRemote = null;
        if((asyncRemote = this.userSession.getAsyncRemote()) == null)
        {
            throw new FluidClientException(
                    "Remote Session is not set. Check if connection is open.",
                    FluidClientException.ErrorCode.IO_ERROR);
        }

        asyncRemote.sendText(messageToSendParam);
    }

    /**
     * Closes the Web Socket User session.
     */
    public void closeSession()
    {
        if(this.userSession == null)
        {
            return;
        }

        try {
            this.userSession.close();
        }
        //
        catch (IOException e) {
            throw new FluidClientException(
                    "Unable to close session. "+e.getMessage(),
                    e,FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Check to see whether the session is open.
     *
     * @return {@code true} if session is open, otherwise {@code false}.
     */
    public boolean isSessionOpen()
    {
        if(this.userSession == null)
        {
            return false;
        }

        return this.userSession.isOpen();
    }
}
