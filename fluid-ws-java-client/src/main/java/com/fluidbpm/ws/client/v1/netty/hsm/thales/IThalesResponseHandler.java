package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.v1.netty.hsm.common.IHsmResponseHandler;

/**
 * Interface for handling Thales HSM responses; a typed alias of
 * {@link IHsmResponseHandler} for {@link ThalesResponse}.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public interface IThalesResponseHandler extends IHsmResponseHandler<ThalesResponse> {
}
