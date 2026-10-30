package com.fluidbpm.ws.client.v1.netty.hsm.common;

/**
 * A host command that can be sent through an {@link AbstractHsmClient}.
 *
 * Every vendor protocol carries some field that the HSM returns unmodified (the payShield
 * message header, the AT1000 context tag). The client uses it as the request id to correlate
 * responses; how it is allocated and attached to the command is the job of {@link HsmProtocol}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public interface HsmCommand {

    /**
     * Gets the correlation id that will be (or was) sent with this command.
     *
     * @return The request id, or {@code null} if the client has not assigned one yet
     */
    String getRequestId();

    /**
     * Gets the bytes to transmit, request id included, as expected by the vendor's encoder.
     *
     * @return The bytes to transmit
     */
    byte[] toWireBytes();
}
