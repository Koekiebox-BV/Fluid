package com.fluidbpm.ws.client.v1.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.websocket.*;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;

/**
 * The {@code ClientEndpoint} Web Socket client.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
@ClientEndpoint()
public class WebSocketClient<RespHandler extends IMessageResponseHandler> {

    private Session userSession = null;
    private Map<String,RespHandler> messageHandlers;

    /**
     * Default constructor with an endpoint.
     * Establishes a connection to remote.
     *
     * @param endpointURIParam The Endpoint URI.
     * @param messageHandlersParam Map of message handlers.
     * @throws DeploymentException If there is a connection problem.
     * @throws IOException If there is a I/O problem.
     */
    public WebSocketClient(
            URI endpointURIParam,
            Map<String,RespHandler> messageHandlersParam) throws DeploymentException, IOException {
        this.messageHandlers = messageHandlersParam;

        //ContainerProvider.getWebSocketContainer()
        ClientManager clMng = ClientManager.createClient(
                GrizzlyClientContainer.class.getName());

        clMng.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, "15000");
        
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
        
        if (this.messageHandlers != null) {

            this.messageHandlers.values().forEach(handle ->{
                handle.connectionClosed();
            });
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when
     * a client sends a message.
     * @param messageParam The text message      
     */
    @OnMessage
    public void onMessage(String messageParam) {

        boolean handlerFoundForMsg = false;
        for(IMessageResponseHandler handler : this.messageHandlers.values()){

            Object qualifyObj =
                    handler.doesHandlerQualifyForProcessing(messageParam);

            if(qualifyObj instanceof Error){
                handler.handleMessage(qualifyObj);
            } else if(qualifyObj instanceof JSONObject){

                handler.handleMessage(qualifyObj);
                handlerFoundForMsg = true;
                break;
            }
        }

        if(!handlerFoundForMsg){
            throw new FluidClientException(
                    "No handler found for message;\n"+messageParam,
                    FluidClientException.ErrorCode.IO_ERROR);
        }
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
