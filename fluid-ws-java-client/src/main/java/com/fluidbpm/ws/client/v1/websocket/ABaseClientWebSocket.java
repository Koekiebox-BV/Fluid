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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.ABaseListing;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Base class for all REST related calls.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see ABaseClientWS
 */
public abstract class ABaseClientWebSocket<RespHandler extends IMessageResponseHandler> extends ABaseClientWS {

	protected String webSocketEndpointUrl;
	protected WebSocketClient webSocketClient;

	private long timeoutInMillis;
	private Map<String, RespHandler> messageHandler;

	protected IMessageReceivedCallback messageReceivedCallback;
	protected boolean compressResponse;

	/**
	 * The constant variables used.
	 */
	public static class Constant {
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
	 * @param messageReceivedCallbackParam Optional callback object (observer).
	 * @param timeoutInMillisParam The timeout for the Web Socket response in millis.
	 * @param postFixForUrlParam The URL Postfix.
	 * @param compressResponseParam Expect the response to be compressed.
	 */
	public ABaseClientWebSocket(
			String endpointBaseUrlParam,
			IMessageReceivedCallback messageReceivedCallbackParam,
			long timeoutInMillisParam,
			String postFixForUrlParam,
			boolean compressResponseParam) {
		this(endpointBaseUrlParam, messageReceivedCallbackParam, timeoutInMillisParam, postFixForUrlParam);
		this.compressResponse = compressResponseParam;
	}

	/**
	 * Default constructor.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param messageReceivedCallbackParam Optional callback object (observer).
	 * @param timeoutInMillisParam The timeout for the Web Socket response in millis.
	 * @param postFixForUrlParam The URL Postfix.
	 */
	public ABaseClientWebSocket(
			String endpointBaseUrlParam,
			IMessageReceivedCallback messageReceivedCallbackParam,
			long timeoutInMillisParam,
			String postFixForUrlParam) {
		super(endpointBaseUrlParam);

		this.messageHandler = new HashMap<>();
		this.messageHandler = Collections.synchronizedMap(this.messageHandler);

		this.timeoutInMillis = timeoutInMillisParam;
		this.messageReceivedCallback = messageReceivedCallbackParam;

		if (this.webSocketEndpointUrl == null && this.endpointUrl != null) {
			this.webSocketEndpointUrl =
					this.getWebSocketBaseURIFrom(this.endpointUrl);
		}

		//Confirm Web Socket Endpoint is set.
		if (this.webSocketEndpointUrl == null ||
				this.webSocketEndpointUrl.trim().isEmpty()) {
			throw new FluidClientException(
					"Base Web Socket Endpoint URL not set.",
					FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
		}

		//Issue #23... Don't do double //...
		String completeUrl = null;
		if (this.webSocketEndpointUrl.endsWith(UtilGlobal.FORWARD_SLASH) &&
				postFixForUrlParam.startsWith(UtilGlobal.FORWARD_SLASH)) {
			completeUrl = (this.webSocketEndpointUrl + postFixForUrlParam.substring(1));
		} else {
			completeUrl = (this.webSocketEndpointUrl + postFixForUrlParam);
		}

		try {
			this.webSocketClient = new WebSocketClient(
					new URI(completeUrl), this.messageHandler);
		} catch (DeploymentException e) {
			//Deploy...
			throw new FluidClientException(
					"Unable to create Web Socket client (Deployment). URL ["+ completeUrl+"]: "
							+e.getMessage(),
					e, FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR);
		} catch (IOException e) {
			//I/O...
			throw new FluidClientException(
					"Unable to create Web Socket client (I/O). URL ["+ completeUrl+"]:"+e.getMessage(),
					e, FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR);
		} catch (URISyntaxException e) {
			//URI Syntax...
			throw new FluidClientException(
					"Unable to create Web Socket client (URI). URL ["+completeUrl+"]: "+e.getMessage(),
					e, FluidClientException.ErrorCode.WEB_SOCKET_URI_SYNTAX_ERROR);
		}
	}

	/**
	 * Send the {@code baseFluidJSONObjectParam} via Web Socket.
	 *
	 * @param baseFluidJSONObjectParam The JSONObject to send.
	 * @param requestIdParam The unique request id.
	 *
	 * @see org.json.JSONObject
	 */
	public void sendMessage(
		ABaseFluidJSONObject baseFluidJSONObjectParam,
		String requestIdParam
	) {
		if (baseFluidJSONObjectParam != null) {
			baseFluidJSONObjectParam.setServiceTicket(this.serviceTicket);

			//Add the echo to the listing if [GenericListMessageHandler].
			if (this.getHandler(requestIdParam) instanceof AGenericListMessageHandler) {
				AGenericListMessageHandler listHandler =
						(AGenericListMessageHandler)this.getHandler(requestIdParam);
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
	public void closeAndClean() {
		new Thread(() -> {
			this.closeConnectionNonThreaded();
		},"Close "+this.getClass().getSimpleName()+" Connection")
				.start();
	}

	/**
	 * Close the SQL and ElasticSearch Connection, but not in
	 * a separate {@code Thread}.
	 */
	protected void closeConnectionNonThreaded() {
		if (this.webSocketClient == null) {
			return;
		}

		this.webSocketClient.closeSession();
		super.closeConnectionNonThreaded();
	}

	/**
	 * Initiate a new request process.
	 *
	 * Synchronized.
	 *
	 * @return A randomly generated identifier for the request.
	 */
	public synchronized String initNewRequest() {
		String returnVal = UtilGlobal.randomUUID();
		this.messageHandler.put(returnVal, this.getNewHandlerInstance());

		return returnVal;
	}

	/**
	 * Create a new instance of the handler class for {@code this} client.
	 *
	 * @return new instance of the handler for response messages.
	 */
	public abstract RespHandler getNewHandlerInstance();

	/**
	 * Returns the {@code ThreadLocal} instance of MessageHandler.
	 *
	 * @param requestUniqueIdParam The unique request id.
	 *
	 * @see ThreadLocal
	 *
	 * @return The message handler.
	 */
	protected RespHandler getHandler(String requestUniqueIdParam) {
		return this.messageHandler.get(requestUniqueIdParam);
	}

	/**
	 * Remove the handler with identifier {@code requestUniqueIdParam}.
	 * @param requestUniqueIdParam The unique request id.
	 */
	protected void removeHandler(String requestUniqueIdParam){
		this.messageHandler.remove(requestUniqueIdParam);
	}

	/**
	 * Retrieves the Web Service URL from {@code webServiceURLParam}.
	 *
	 * @param webServiceURLParam The Web Service URL to convert.
	 * @return The Web Socket URL version of {@code webServiceURLParam}.
	 */
	private String getWebSocketBaseURIFrom(String webServiceURLParam) {
		if (webServiceURLParam == null) {
			return null;
		}

		if (webServiceURLParam.trim().length() == 0) {
			return UtilGlobal.EMPTY;
		}

		URI uri = URI.create(webServiceURLParam);
		StringBuilder returnBuffer = new StringBuilder();

		String scheme = uri.getScheme();
		if (scheme == null) {
			throw new FluidClientException(
					"Unable to get scheme from '"+webServiceURLParam+"' URL.",
					FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
		}

		scheme = scheme.trim().toLowerCase();

		//https://localhost:8443/fluid-ws/
		//Scheme...
		if (Constant.HTTP.equals(scheme)) {
			returnBuffer.append(Constant.WS);
		} else if (Constant.HTTPS.equals(scheme)) {
			returnBuffer.append(Constant.WSS);
		} else {
			returnBuffer.append(uri.getScheme());
		}

		// ://
		returnBuffer.append(Constant.SCHEME_SEP);
		returnBuffer.append(uri.getHost());

		// 80 / 443
		if (uri.getPort() > 0) {
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
		if (this.webSocketClient == null) {
			return false;
		}
		return this.webSocketClient.isSessionOpen();
	}

	/**
	 * Return the current user session id.
	 *
	 * @return {@code Session ID} if session is open, otherwise {@code null}.
	 */
	public String getSessionId() {
		if (this.webSocketClient == null) {
			return null;
		}

		return this.webSocketClient.getSessionId();
	}

	/**
	 * Set the {@code echo} value if not set.
	 *
	 * @param baseToSetEchoOnIfNotSetParam The value object to set {@code echo} on.
	 */
	protected void setEchoIfNotSet(ABaseFluidVO baseToSetEchoOnIfNotSetParam) {
		if (baseToSetEchoOnIfNotSetParam == null) {
			throw new FluidClientException(
					"Cannot provide 'null' for value object / pojo.",
					FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
		} else if (baseToSetEchoOnIfNotSetParam.getEcho() == null ||
				baseToSetEchoOnIfNotSetParam.getEcho().trim().isEmpty()) {
			baseToSetEchoOnIfNotSetParam.setEcho(UtilGlobal.randomUUID());
		}
	}

	/**
	 * Generate a verbose Exception message.
	 *
	 * @param prefixParam The component prefix.
	 * @param uniqueReqIdParam The unique request reference.
	 * @param sentItemsParam The items sent to the host.
	 * @return Verbose error message.
	 */
	protected String getExceptionMessageVerbose(
			String prefixParam,
			String uniqueReqIdParam,
			Object ... sentItemsParam
	) {
		//Request...
		StringBuilder formFieldsCombined = new StringBuilder(),
				formFieldsRequestCombined = new StringBuilder(),
				expectedMessagesCombined = new StringBuilder();

		if (sentItemsParam != null) {
			for (Object objSent : sentItemsParam) {
				if (objSent == null) {
					continue;
				}
				formFieldsRequestCombined.append(objSent.toString());
				formFieldsRequestCombined.append(UtilGlobal.PIPE);
			}
		}

		//Response...
		int returnValSize = -1;
		RespHandler respHandler = this.getHandler(uniqueReqIdParam);
		if (respHandler instanceof AGenericListMessageHandler) {
			AGenericListMessageHandler handlerCasted = ((AGenericListMessageHandler)respHandler);
			List<? extends ABaseFluidJSONObject> returnValue = handlerCasted.getReturnValue();
			if (returnValue != null) {
				returnValSize = returnValue.size();
				returnValue.forEach(listingItm -> {
					if (listingItm instanceof ABaseListing) {
						ABaseListing castedToListing = (ABaseListing)listingItm;
						formFieldsCombined.append(castedToListing.toJsonObject().toString());
					} else {
						formFieldsCombined.append(listingItm.toString());
					}

					formFieldsCombined.append(UtilGlobal.PIPE);
				});
			}

			Set<String> expectedMessages =
					handlerCasted.getExpectedEchoMessagesBeforeComplete();
			if (expectedMessages != null) {
				expectedMessages.forEach(expItm -> {
					expectedMessagesCombined.append(expItm);
					expectedMessagesCombined.append(UtilGlobal.PIPE);
				});
			}
		}

		String reqToString = formFieldsRequestCombined.toString(),
		respToString = formFieldsCombined.toString(),
		expectedToString = expectedMessagesCombined.toString();

		if (reqToString.length() > 0) {
			reqToString = reqToString.substring(0, reqToString.length() - 1);
		}

		if (respToString.length() > 0) {
			respToString = respToString.substring(0, respToString.length() - 1);
		}

		if (expectedToString.length() > 0) {
			expectedToString = expectedToString.substring(0, expectedToString.length() - 1);
		}

		return (prefixParam + ": " +
				"Timeout while waiting for all return data. There were '"+
				returnValSize +"' items after a Timeout of "+(
				TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds on req-ref-nr '"+
				uniqueReqIdParam+"'. Expected a total of '" +
				(sentItemsParam == null ? 0: sentItemsParam.length) + "' forms. " +
				"\nRequest-Data '"+ reqToString+"'. \n" +
				"\nReturned-Data '"+ respToString+"'. \n" +
				"\nExpected-Data '"+ expectedToString +"'.");
	}
}
