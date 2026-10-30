package com.fluidbpm.ws.client.v1.netty.hsm.common;

/**
 * Interface for handling HSM responses.
 * Implementations of this interface process responses received from the HSM.
 *
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
public interface IHsmResponseHandler<R extends HsmResponse> {

    /**
     * Handles a response received from the HSM.
     *
     * @param response The response object containing the HSM response
     */
    void handleResponse(R response);

    /**
     * Called when the connection to the HSM is closed.
     */
    void connectionClosed();

    /**
     * Called when an error occurs during HSM communication.
     *
     * @param error The error that occurred
     */
    void handleError(Throwable error);
}
