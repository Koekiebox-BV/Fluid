package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientConfig;
import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Connection settings for {@link AtallaHSMClient}.
 *
 * <ul>
 *   <li>{@code contextTagEnabled} (default {@code true}) makes the client append a unique
 *       {@code ^tag} field to every command and correlate responses by the tag the HSM echoes
 *       back, so commands can be pipelined on one connection. When disabled, untagged commands
 *       are matched to responses in send order (FIFO) and should not be pipelined.</li>
 *   <li>{@code sslContext} enables TLS. Build one with {@link HsmSslContexts} for mutual TLS.
 *       {@code null} means plain TCP.</li>
 *   <li>{@code verifyHostname} enables endpoint identification on the TLS handshake. HSM
 *       certificates typically carry a unit name rather than a DNS name, so this is off by default.</li>
 *   <li>{@code maxResponseLength} bounds a single response frame; exceeding it fails the connection.</li>
 * </ul>
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Getter
@SuperBuilder(toBuilder = true)
public class AtallaHSMClientConfig extends HsmClientConfig {

    /** Default AT1000 host port (as used in the manual's sample program). */
    public static final int DEFAULT_PORT = 7000;

    @Builder.Default
    private final boolean contextTagEnabled = true;

    @Builder.Default
    private final int maxResponseLength = AtallaFrameDecoder.DEFAULT_MAX_FRAME_LENGTH;

    @Override
    protected int defaultPort() {
        return DEFAULT_PORT;
    }

    /**
     * Validates the configuration.
     *
     * @throws IllegalArgumentException if a value is out of range
     */
    @Override
    public void validate() {
        super.validate();
        if (maxResponseLength < 1) {
            throw new IllegalArgumentException("Max response length must be positive: " + maxResponseLength);
        }
    }
}
