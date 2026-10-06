package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientHandler;
import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientInitializer;
import io.netty.handler.ssl.SslContext;

/**
 * Channel initializer for the Thales HSM client pipeline: the shared SSL/idle/handler
 * wiring from {@link HsmClientInitializer} plus the payShield codecs from {@link ThalesProtocol}
 * (frame decoder/encoder, response decoder, command encoder).
 *
 * {@link ThalesHSMClient} builds this itself; it is public for callers assembling their own pipeline.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesHSMClientInitializer extends HsmClientInitializer<ThalesResponse> {

    /**
     * Constructs a ThalesHSMClientInitializer with SSL support and the default message header length.
     *
     * @param sslContext The SSL context for secure connections (can be null for unencrypted)
     * @param handler The business logic handler
     */
    public ThalesHSMClientInitializer(SslContext sslContext, HsmClientHandler<ThalesResponse> handler) {
        this(sslContext, handler, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClientInitializer with SSL and timeout configuration and the
     * default message header length.
     *
     * @param sslContext The SSL context for secure connections (can be null)
     * @param handler The business logic handler
     * @param readTimeoutSeconds Read timeout in seconds (0 to disable)
     * @param writeTimeoutSeconds Write timeout in seconds (0 to disable)
     */
    public ThalesHSMClientInitializer(
            SslContext sslContext,
            HsmClientHandler<ThalesResponse> handler,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        this(sslContext, false, null, 0, handler,
                ThalesHSMClientConfig.DEFAULT_HEADER_LENGTH, readTimeoutSeconds, writeTimeoutSeconds);
    }

    /**
     * Constructs a fully configured initializer.
     *
     * @param sslContext The SSL context for secure connections (can be null)
     * @param verifyHostname Whether to enable TLS endpoint identification against {@code host}
     * @param host Peer host (used for SNI/endpoint identification when TLS is on)
     * @param port Peer port
     * @param handler The business logic handler
     * @param headerLength Message header length configured on the HSM
     * @param readTimeoutSeconds Read timeout in seconds (0 to disable)
     * @param writeTimeoutSeconds Write timeout in seconds (0 to disable)
     */
    public ThalesHSMClientInitializer(
            SslContext sslContext,
            boolean verifyHostname,
            String host,
            int port,
            HsmClientHandler<ThalesResponse> handler,
            int headerLength,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        super(sslContext, verifyHostname, host, port,
                new ThalesProtocol(headerLength)::newCodecHandlers,
                handler, readTimeoutSeconds, writeTimeoutSeconds);
    }
}
