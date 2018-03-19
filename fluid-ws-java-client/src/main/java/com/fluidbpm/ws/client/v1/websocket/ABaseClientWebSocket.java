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

package com.fluidbpm.ws.client.v1.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Base class for all REST related calls.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see ABaseClientWS
 */
public abstract class ABaseClientWebSocket<T extends IMessageResponseHandler> extends ABaseClientWS {

    protected String webSocketEndpointUrl;

    private WebSocketClient webSocketClient;
    private long timeoutInMillis;
    private ThreadLocal<T> messageHandlerThreadLocal;

    /**
     * The constant variables used.
     */
    public static class Constant
    {
        public static final String HTTP = "http";
        public static final String HTTPS = "https";

        public static final String WS = "ws";
        public static final String WSS = "wss";

        public static final String SCHEME_SEP = "://";
        public static final String COLON = ":";
    }

    /**
     * Default constructor.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param messageHandlerParam {@code IMessageHandler} to process incoming messages.
     * @param timeoutInMillisParam The timeout for the Web Socket response in millis.
     * @param postFixForUrlParam The URL Postfix.
     */
    public ABaseClientWebSocket(
            String endpointBaseUrlParam,
            T messageHandlerParam,
            long timeoutInMillisParam,
            String postFixForUrlParam) {
        super(endpointBaseUrlParam);

        if(messageHandlerParam == null)
        {
            throw new FluidClientException(
                    "[IMessageHandler] has not been implemented.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        
        this.messageHandlerThreadLocal = new ThreadLocal<T>();
        this.messageHandlerThreadLocal.set(messageHandlerParam);
        
        this.timeoutInMillis = timeoutInMillisParam;

        if(this.webSocketEndpointUrl == null && this.endpointUrl != null)
        {
            this.webSocketEndpointUrl =
                    this.getWebSocketBaseURIFrom(this.endpointUrl);
        }

        //Confirm Web Socket Endpoint is set.
        if(this.webSocketEndpointUrl == null ||
                this.webSocketEndpointUrl.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Base Web Socket Endpoint URL not set.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        //Issue #23... Don't do double //...
        String completeUrl = null;
        if(this.webSocketEndpointUrl.endsWith(UtilGlobal.FORWARD_SLASH) &&
                postFixForUrlParam.startsWith(UtilGlobal.FORWARD_SLASH))
        {
            completeUrl = (this.webSocketEndpointUrl + postFixForUrlParam.substring(1));
        }
        else
        {
            completeUrl = (this.webSocketEndpointUrl + postFixForUrlParam);
        }

        try {
            this.webSocketClient = new WebSocketClient(new URI(completeUrl));
            this.webSocketClient.setMessageHandler(messageHandlerParam);
        }
        //Deploy...
        catch (DeploymentException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (Deployment). URL ["+ completeUrl+"]: "
                            +e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
        //I/O...
        catch (IOException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (I/O). URL ["+ completeUrl+"]:"+e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
        //URI Syntax...
        catch (URISyntaxException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (URI). URL ["+completeUrl+"]: "+e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Send the {@code baseFluidJSONObjectParam} via Web Socket.
     *
     * @param baseFluidJSONObjectParam The JSONObject to send.
     *
     * @see org.json.JSONObject
     */
    public void sendMessage(ABaseFluidJSONObject baseFluidJSONObjectParam)
    {
        if(baseFluidJSONObjectParam != null)
        {
            baseFluidJSONObjectParam.setServiceTicket(this.serviceTicket);

            //Add the echo to the listing if [GenericListMessageHandler].
            if(this.getMessageHandler() instanceof GenericListMessageHandler)
            {
                GenericListMessageHandler listHandler =
                        (GenericListMessageHandler)this.getMessageHandler();
                listHandler.addExpectedMessage(baseFluidJSONObjectParam.getEcho());
            }
        }

        this.webSocketClient.sendMessage(baseFluidJSONObjectParam);
    }

    /**
     * If the HTTP Client is set, this will
     * close and clean any connections that needs to be closed.
     *
     * @since v1.1
     */
    @Override
    public void closeAndClean()
    {
        CloseConnectionRunnable closeConnectionRunnable =
                new CloseConnectionRunnable(this);

        Thread closeConnThread = new Thread(
                closeConnectionRunnable,"Close ABaseClientWebSocket Connection");
        closeConnThread.start();
    }

    /**
     * Close the SQL and ElasticSearch Connection, but not in
     * a separate {@code Thread}.
     */
    protected void closeConnectionNonThreaded()
    {
        if(this.webSocketClient == null)
        {
            return;
        }

        this.webSocketClient.closeSession();
        
        super.closeConnectionNonThreaded();
    }

    /**
     * Returns the {@code ThreadLocal} instance of MessageHandler.
     *
     * @see ThreadLocal
     *
     * @return The message handler.
     */
    protected T getMessageHandler()
    {
        return this.messageHandlerThreadLocal.get();
    }

    /**
     * Retrieves the Web Service URL from {@code webServiceURLParam}.
     *
     * @param webServiceURLParam The Web Service URL to convert.
     * @return The Web Socket URL version of {@code webServiceURLParam}.
     */
    private String getWebSocketBaseURIFrom(String webServiceURLParam)
    {
        if(webServiceURLParam == null)
        {
            return null;
        }

        if(webServiceURLParam.trim().length() == 0)
        {
            return UtilGlobal.EMPTY;
        }

        URI uri = URI.create(webServiceURLParam);
        StringBuilder returnBuffer = new StringBuilder();

        String scheme = uri.getScheme();

        if(scheme == null)
        {
            throw new FluidClientException(
                    "Unable to get scheme from '"+webServiceURLParam+"' URL.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        scheme = scheme.trim().toLowerCase();

        //https://localhost:8443/fluid-ws/
        //Scheme...
        if(Constant.HTTP.equals(scheme)) {

            returnBuffer.append(Constant.WS);
        }
        else if(Constant.HTTPS.equals(scheme)) {

            returnBuffer.append(Constant.WSS);
        }
        else {
            returnBuffer.append(uri.getScheme());
        }

        // ://
        returnBuffer.append(Constant.SCHEME_SEP);
        returnBuffer.append(uri.getHost());

        // 80 / 443
        if(uri.getPort() > 0)
        {
            returnBuffer.append(Constant.COLON);
            returnBuffer.append(uri.getPort());
        }

        // /fluid-ws/
        returnBuffer.append(uri.getPath());

        return returnBuffer.toString();
    }

    /**
     * The timeout in millis.
     *
     * @return Timeout in millis.
     */
    protected long getTimeoutInMillis() {
        return this.timeoutInMillis;
    }

    /**
     * Confirms whether the Web-Socket client connection session is open.
     * 
     * @return Whether the connection is valid or not.
     *          {@code true} if the connection is valid.
     */
    @Override
    public boolean isConnectionValid() {

        if(this.webSocketClient == null)
        {
            return false;
        }
        
        return this.webSocketClient.isSessionOpen();
    }

    /**
     * Utility class to close the connection in a thread.
     */
    private static class CloseConnectionRunnable implements Runnable{

        private ABaseClientWebSocket baseClientWebSocket;

        /**
         * The resource to close.
         *
         * @param baseClientWebSocketParam Base WS utility to close.
         */
        CloseConnectionRunnable(ABaseClientWebSocket baseClientWebSocketParam) {
            this.baseClientWebSocket = baseClientWebSocketParam;
        }

        /**
         * Performs the threaded operation.
         */
        @Override
        public void run() {

            if(this.baseClientWebSocket == null)
            {
                return;
            }

            this.baseClientWebSocket.closeConnectionNonThreaded();
        }
    }
}
