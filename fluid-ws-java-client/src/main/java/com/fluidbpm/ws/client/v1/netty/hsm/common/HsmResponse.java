package com.fluidbpm.ws.client.v1.netty.hsm.common;

/**
 * A response received from an HSM, independent of the vendor's wire format.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public interface HsmResponse {

    /**
     * Gets the correlation id echoed back by the HSM.
     *
     * @return The request id, or {@code null} if the response carries none
     */
    String getRequestId();

    /**
     * Checks if the response indicates success.
     *
     * @return true if successful, false otherwise
     */
    boolean isSuccess();

    /**
     * Gets the vendor error code.
     *
     * @return The error code, or {@code null} if the vendor format has none for this response
     */
    String getErrorCode();

    /**
     * Gets a human-readable error message based on the error code.
     *
     * @return Error message description
     */
    String getErrorMessage();

    /**
     * Gets a copy of the complete raw response as received (framing stripped).
     *
     * @return The raw response bytes
     */
    byte[] getRawResponse();
}
