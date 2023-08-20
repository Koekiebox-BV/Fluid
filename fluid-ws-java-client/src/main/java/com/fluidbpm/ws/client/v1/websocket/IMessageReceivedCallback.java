package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ws.Error;

/**
 * Callback interface when a message response
 * is received by the client.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public interface IMessageReceivedCallback<T extends ABaseFluidJSONObject> {

	/**
	 * When an error message is sent back.
	 *
	 * @param errorReceivedParam The error message.
	 */
	public abstract void errorMessageReceived(Error errorReceivedParam);

	/**
	 * When a {@code T} message is received.
	 *
	 * @param messageReceivedParam The message received.
	 */
	public abstract void messageReceived(T messageReceivedParam);
}
