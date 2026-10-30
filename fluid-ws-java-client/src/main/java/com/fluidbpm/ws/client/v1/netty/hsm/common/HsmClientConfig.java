package com.fluidbpm.ws.client.v1.netty.hsm.common;

import io.netty.handler.ssl.SslContext;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Connection settings shared by every HSM client. Vendor configs extend this class and add
 * their protocol-specific options (payShield header length, AT1000 context tag mode, ...).
 *
 * <ul>
 *   <li>{@code port} left unset (0) selects the vendor's {@link #defaultPort()}.</li>
 *   <li>{@code sslContext} enables TLS. Build one with {@link HsmSslContexts} for mutual TLS.
 *       {@code null} means plain TCP.</li>
 *   <li>{@code verifyHostname} enables endpoint identification on the TLS handshake. HSM
 *       certificates typically carry a unit name rather than a DNS name, so this is off by default.</li>
 * </ul>
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Getter
@SuperBuilder(toBuilder = true)
public abstract class HsmClientConfig {

    private final String host;

    /** 0 means "use {@link #defaultPort()}"; read through {@link #getPort()}. */
    private final int port;

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
     * @return The port used when none was configured
     */
    protected abstract int defaultPort();

    /**
     * @return The configured port, or {@link #defaultPort()} if none was set
     */
    public int getPort() {
        return port > 0 ? port : defaultPort();
    }

    /**
     * Validates the configuration. Subclasses should call {@code super.validate()}.
     *
     * @throws IllegalArgumentException if a value is out of range
     */
    public void validate() {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("HSM host is required");
        }
        int p = getPort();
        if (p < 1 || p > 65535) {
            throw new IllegalArgumentException("HSM port out of range: " + p);
        }
    }

    /**
     * @return {@code true} when TLS is configured
     */
    public boolean isTls() {
        return sslContext != null;
    }
}
