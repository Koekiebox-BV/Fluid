package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ws.Error;

/**
 * A callback interface to handle messages received and error messages.
 *
 * @param <T> The type of the message object that extends {@code ABaseFluidGSONObject}.
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public interface IMessageReceivedCallback<T extends ABaseFluidGSONObject> {

    /**
     * When an error message is sent back.
     *
     * @param errorReceivedParam The error message.
     */
    void errorMessageReceived(Error errorReceivedParam);

    /**
     * When a {@code T} message is received.
     *
     * @param messageReceivedParam The message received.
     */
    void messageReceived(T messageReceivedParam);
}
