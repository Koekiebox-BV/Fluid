package com.fluid.ws.client.v1.websocket;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.ws.Error;

/**
 * TODO Description comes here.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.0
 */
public interface IMessageReceivedCallback<T extends ABaseFluidJSONObject> {

    /**
     *
     * @param errorReceivedParam
     */
    public abstract void errorMessageReceived(Error errorReceivedParam);

    /**
     *
     * @param messageReceivedParam
     */
    public abstract void messageReceived(T messageReceivedParam);
}
