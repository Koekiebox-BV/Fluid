package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientHandler;
import com.fluidbpm.ws.client.v1.netty.hsm.common.IHsmResponseHandler;

import java.util.Map;

/**
 * Netty channel handler for processing Thales HSM responses; a typed
 * {@link HsmClientHandler} for {@link ThalesResponse}.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesHSMClientHandler extends HsmClientHandler<ThalesResponse> {

    /**
     * Constructs a ThalesHSMClientHandler with response handlers.
     *
     * @param responseHandlers Map of request ID to response handler
     * @param defaultHandler Default handler for responses without a request ID
     */
    public ThalesHSMClientHandler(
            Map<String, IHsmResponseHandler<ThalesResponse>> responseHandlers,
            IHsmResponseHandler<ThalesResponse> defaultHandler
    ) {
        super(ThalesResponse.class, "Thales HSM", responseHandlers, defaultHandler);
    }

    /**
     * Constructs a ThalesHSMClientHandler with a default handler only.
     *
     * @param defaultHandler Default handler for all responses
     */
    public ThalesHSMClientHandler(IHsmResponseHandler<ThalesResponse> defaultHandler) {
        super(ThalesResponse.class, "Thales HSM", defaultHandler);
    }
}
