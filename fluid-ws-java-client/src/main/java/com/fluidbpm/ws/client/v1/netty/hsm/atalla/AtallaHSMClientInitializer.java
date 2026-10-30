package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientHandler;
import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientInitializer;
import io.netty.handler.ssl.SslContext;

/**
 * Channel initializer for the Atalla HSM client pipeline: the shared SSL/idle/handler
 * wiring from {@link HsmClientInitializer} plus the AT1000 codecs from {@link AtallaProtocol}
 * (frame decoder, response decoder, command encoder).
 *
 * {@link AtallaHSMClient} builds this itself; it is public for callers assembling their own pipeline.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaHSMClientInitializer extends HsmClientInitializer<AtallaResponse> {

    /**
     * Constructs an initializer with SSL support and default limits/timeouts.
     *
     * @param sslContext The SSL context for secure connections (can be null for unencrypted)
     * @param handler The business logic handler
     */
    public AtallaHSMClientInitializer(SslContext sslContext, HsmClientHandler<AtallaResponse> handler) {
        this(sslContext, false, null, 0, handler, AtallaFrameDecoder.DEFAULT_MAX_FRAME_LENGTH, 120, 120);
    }

    /**
     * Constructs a fully configured initializer.
     *
     * @param sslContext The SSL context for secure connections (can be null)
     * @param verifyHostname Whether to enable TLS endpoint identification against {@code host}
     * @param host Peer host (used for SNI/endpoint identification when TLS is on)
     * @param port Peer port
     * @param handler The business logic handler
     * @param maxResponseLength Largest single response accepted
     * @param readTimeoutSeconds Read timeout in seconds (0 to disable)
     * @param writeTimeoutSeconds Write timeout in seconds (0 to disable)
     */
    public AtallaHSMClientInitializer(
            SslContext sslContext,
            boolean verifyHostname,
            String host,
            int port,
            HsmClientHandler<AtallaResponse> handler,
            int maxResponseLength,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        super(sslContext, verifyHostname, host, port,
                new AtallaProtocol(true, maxResponseLength)::newCodecHandlers,
                handler, readTimeoutSeconds, writeTimeoutSeconds);
    }
}
