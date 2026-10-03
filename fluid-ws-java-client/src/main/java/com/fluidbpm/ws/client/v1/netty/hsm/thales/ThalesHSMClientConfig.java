package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.handler.ssl.SslContext;
import lombok.Builder;
import lombok.Getter;

/**
 * Connection settings for {@link ThalesHSMClient}.
 *
 * <ul>
 *   <li>{@code headerLength} must match the message header length configured on the HSM
 *       (payShield: 1-255, default 4). The HSM echoes the header back unmodified and the
 *       client relies on it to correlate responses, so a header is required; an HSM
 *       configured with a 0-length header must be reconfigured before use.</li>
 *   <li>{@code sslContext} enables TLS. Build one with {@link ThalesSslContexts} for mutual TLS.
 *       {@code null} means plain TCP.</li>
 *   <li>{@code verifyHostname} enables endpoint identification on the TLS handshake. payShield
 *       certificates typically carry a unit name rather than a DNS name, so this is off by default.</li>
 * </ul>
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Getter
@Builder(toBuilder = true)
public class ThalesHSMClientConfig {

    /** Default payShield host port. */                 
    public static final int DEFAULT_PORT = 1500;
    /** Default message header length. */
    public static final int DEFAULT_HEADER_LENGTH = 4;

    private final String host;

    @Builder.Default
    private final int port = DEFAULT_PORT;

    @Builder.Default
    private final int headerLength = DEFAULT_HEADER_LENGTH;

    private final SslContext sslContext;

    @Builder.Default
    private final boolean verifyHostname = false;

    @Builder.Default
    private final int connectTimeoutMillis = 30000;

    @Builder.Default
    private final int readTimeoutSeconds = 120;

    @Builder.Default
    private final int writeTimeoutSeconds = 120;

    /**
     * Validates the configuration.
     *
     * @throws IllegalArgumentException if a value is out of range
     */
    public void validate() {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("HSM host is required");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("HSM port out of range: " + port);
        }
        if (headerLength < 1 || headerLength > 255) {
            throw new IllegalArgumentException("Message header length must be 1-255 "
                    + "(responses are correlated by the echoed header): " + headerLength);
        }
    }

    /**
     * @return {@code true} when TLS is configured
     */
    public boolean isTls() {
        return sslContext != null;
    }
}
