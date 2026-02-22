package com.fluidbpm.ws.client.v1.netty.hsm;

/**
 * Interface for handling Thales HSM responses.
 * Implementations of this interface process responses received from the HSM.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public interface IThalesResponseHandler {

    /**
     * Handles a response received from the Thales HSM.
     *
     * @param response The ThalesResponse object containing the HSM response
     */
    void handleResponse(ThalesResponse response);

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
