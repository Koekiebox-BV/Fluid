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
public abstract class ABaseClientWebSocket<T extends IMessageHandler> extends ABaseClientWS {

    protected String webSocketEndpointUrl;

    private WebSocketClient webSocketClient;
    private long timeoutInMillis;
    protected T messageHandler;

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

        public static final long RESPONSE_CHECKER_SLEEP = 5;
        //public static final String FORWARD_SLASH = "/";
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

        this.messageHandler = messageHandlerParam;
        this.timeoutInMillis = timeoutInMillisParam;

        if(this.webSocketEndpointUrl == null &&
                this.endpointUrl != null)
        {
            this.webSocketEndpointUrl =
                    this.getWebSocketBaseURIFrom(this.endpointUrl);
        }

        //Confirm Web Socket Endpoint is set.
        if(this.webSocketEndpointUrl == null ||
                this.webSocketEndpointUrl.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Base Web Socket Enpoint URL not set.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        if(messageHandlerParam == null)
        {
            throw new FluidClientException(
                    "[IMessageHandler] has not been implemented.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        String completeUrl = (this.webSocketEndpointUrl +
                postFixForUrlParam);

        try {
            this.webSocketClient = new WebSocketClient(new URI(completeUrl));
            this.webSocketClient.setMessageHandler(messageHandlerParam);
        }
        //
        catch (DeploymentException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (Deployment). "+e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
        //
        catch (IOException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (I/O). "+e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
        //
        catch (URISyntaxException e) {

            throw new FluidClientException(
                    "Unable to create Web Socket client (URI). "+e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Creates a new client and sets the Base Endpoint URL.
     *
     * @param baseEndpointUrlParam URL to base endpoint.
     */
    public ABaseClientWebSocket(String baseEndpointUrlParam) {
        super(baseEndpointUrlParam);

        this.webSocketEndpointUrl = this.getWebSocketBaseURIFrom(baseEndpointUrlParam);
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

        if(this.messageHandler != null)
        {
            this.messageHandler.connectionClosed();
        }

        super.closeConnectionNonThreaded();
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
        if(uri == null)
        {
            throw new FluidClientException(
                    "URI created from '"+webServiceURLParam+"' is not set.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

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
    public long getTimeoutInMillis() {
        return this.timeoutInMillis;
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
        public CloseConnectionRunnable(ABaseClientWebSocket baseClientWebSocketParam) {
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
