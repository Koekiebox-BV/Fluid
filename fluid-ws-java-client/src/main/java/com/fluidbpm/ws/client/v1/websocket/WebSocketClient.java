package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;
import lombok.Getter;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;
import org.json.JSONObject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

/**
 * The {@code ClientEndpoint} Web Socket client.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
@ClientEndpoint()
public class WebSocketClient<RespHandler extends IMessageResponseHandler> {

	private Session userSession = null;
	private Map<String, RespHandler> messageHandlers;

	@Getter
	protected int sentMessages = 0;
	@Getter
	protected int receivedMessages = 0;

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
			Map<String, RespHandler> messageHandlersParam
	) throws DeploymentException, IOException {
		this.messageHandlers = messageHandlersParam;

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
			this.messageHandlers.values().forEach(handle -> {
				handle.connectionClosed();
			});
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
		for (IMessageResponseHandler handler :
				new ArrayList<IMessageResponseHandler>(this.messageHandlers.values())) {
			Object qualifyObj = handler.doesHandlerQualifyForProcessing(message);
			if (qualifyObj instanceof Error) {
				handler.handleMessage(qualifyObj);
			} else if (qualifyObj instanceof JSONObject) {
				handler.handleMessage(qualifyObj);
				handlerFoundForMsg = true;
				break;
			}
		}

		if (!handlerFoundForMsg) {
			throw new FluidClientException(
					"No handler found for message;\n"+message,
					FluidClientException.ErrorCode.IO_ERROR
			);
		}
	}

	/**
	 * Send a message.
	 *
	 * @param aBaseFluidJSONObject The JSON Object to send.
	 */
	public void sendMessage(ABaseFluidJSONObject aBaseFluidJSONObject) {
		if (aBaseFluidJSONObject == null) {
			throw new FluidClientException("No JSON Object to send.", FluidClientException.ErrorCode.IO_ERROR);
		} else this.sendMessage(aBaseFluidJSONObject.toJsonObject().toString());
	}

	/**
	 * Send a message as text.
	 *
	 * @param messageToSend The text message to send.
	 */
	public void sendMessage(String messageToSend) {
		if (this.userSession == null) {
			throw new FluidClientException(
					"User Session is not set. Check if connection is open.",
					FluidClientException.ErrorCode.SESSION_EXPIRED
			);
		}

		RemoteEndpoint.Async asyncRemote = null;
		if ((asyncRemote = this.userSession.getAsyncRemote()) == null) {
			throw new FluidClientException(
					"Remote Session is not set. Check if connection is open.",
					FluidClientException.ErrorCode.IO_ERROR);
		}

		asyncRemote.sendText(messageToSend);
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
					"Unable to close session. "+e.getMessage(),
					e,FluidClientException.ErrorCode.IO_ERROR);
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
}
