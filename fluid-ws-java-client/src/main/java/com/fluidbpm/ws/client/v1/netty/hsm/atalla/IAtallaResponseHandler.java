package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.IHsmResponseHandler;

/**
 * Interface for handling Atalla HSM responses; a typed alias of
 * {@link IHsmResponseHandler} for {@link AtallaResponse}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public interface IAtallaResponseHandler extends IHsmResponseHandler<AtallaResponse> {
}
