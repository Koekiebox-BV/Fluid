package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientHandler;
import com.fluidbpm.ws.client.v1.netty.hsm.common.IHsmResponseHandler;

import java.util.Map;

/**
 * Netty channel handler for processing Atalla HSM responses; a typed
 * {@link HsmClientHandler} for {@link AtallaResponse}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaHSMClientHandler extends HsmClientHandler<AtallaResponse> {

    /**
     * Constructs an AtallaHSMClientHandler with response handlers.
     *
     * @param responseHandlers Map of context tag to response handler
     * @param defaultHandler Default handler for responses without a registered tag
     */
    public AtallaHSMClientHandler(
            Map<String, IHsmResponseHandler<AtallaResponse>> responseHandlers,
            IHsmResponseHandler<AtallaResponse> defaultHandler
    ) {
        super(AtallaResponse.class, "Atalla HSM", responseHandlers, defaultHandler);
    }

    /**
     * Constructs an AtallaHSMClientHandler with a default handler only.
     *
     * @param defaultHandler Default handler for all responses
     */
    public AtallaHSMClientHandler(IHsmResponseHandler<AtallaResponse> defaultHandler) {
        super(AtallaResponse.class, "Atalla HSM", defaultHandler);
    }
}
