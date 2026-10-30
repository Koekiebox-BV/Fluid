package com.fluidbpm.ws.client.v1.netty.hsm.common;

import io.netty.channel.ChannelHandler;

import java.util.List;

/**
 * The vendor-specific part of an HSM client: the codec pipeline and the rules for the
 * correlation id. Everything else (connecting, TLS, pending-request tracking, timeouts,
 * counters) lives in {@link AbstractHsmClient}.
 *
 * @param <C> The vendor command type
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
public interface HsmProtocol<C extends HsmCommand, R extends HsmResponse> {

    /**
     * @return Human-readable vendor/model name used in log and error messages, e.g. "Thales HSM"
     */
    String displayName();

    /**
     * @return The concrete response class, needed by Netty to match inbound messages
     */
    Class<R> responseType();

    /**
     * Creates the codec handlers for one channel, in pipeline order (frame decoder/encoder,
     * response decoder, command encoder). Called once per connection; handlers must be new
     * instances unless they are {@link io.netty.channel.ChannelHandler.Sharable}.
     *
     * @return The codec handlers
     */
    List<ChannelHandler> newCodecHandlers();

    /**
     * Whether every command must carry a request id. When {@code false}, commands sent
     * without one are matched to responses in send order (FIFO) instead.
     *
     * @return {@code true} to always allocate an id
     */
    boolean requiresRequestId();

    /**
     * Validates a caller-supplied request id against the vendor's constraints
     * (e.g. the configured payShield header length).
     *
     * @param requestId The id supplied on the command
     * @throws IllegalArgumentException if it cannot be used
     */
    void validateRequestId(String requestId);

    /**
     * Produces the request id for the given allocation sequence number.
     * Should yield distinct values for consecutive sequence numbers.
     *
     * @param sequence Monotonic, non-negative sequence number
     * @return A request id in the vendor's format
     */
    String nextRequestId(int sequence);

    /**
     * Attaches an allocated request id to the command before it is encoded.
     *
     * @param command The command about to be sent
     * @param requestId The id allocated by the client
     */
    void assignRequestId(C command, String requestId);
}
