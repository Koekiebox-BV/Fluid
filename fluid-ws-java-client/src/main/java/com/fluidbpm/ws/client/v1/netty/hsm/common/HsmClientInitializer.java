package com.fluidbpm.ws.client.v1.netty.hsm.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Channel initializer shared by all HSM clients. Configures SSL/TLS, the idle-state
 * handler, the vendor codec handlers and the business logic handler.
 *
 * Pipeline order (inbound/outbound):
 * 1. SSL Handler (if configured)
 * 2. Idle State Handler (connection keepalive)
 * 3..n. Vendor codecs from {@link HsmProtocol#newCodecHandlers()} (framing, response decoder, command encoder)
 * n+1. Business Logic Handler ({@link HsmClientHandler})
 *
 * @param <R> The vendor response type
 * @author jasonbruwer
 * @since 1.15
 */
public class HsmClientInitializer<R extends HsmResponse> extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private final boolean verifyHostname;
    private final String host;
    private final int port;
    private final Supplier<List<ChannelHandler>> codecHandlers;
    private final HsmClientHandler<R> handler;
    private final int readTimeoutSeconds;
    private final int writeTimeoutSeconds;

    /**
     * Constructs a fully configured initializer.
     *
     * @param sslContext The SSL context for secure connections (can be null)
     * @param verifyHostname Whether to enable TLS endpoint identification against {@code host}
     * @param host Peer host (used for SNI/endpoint identification when TLS is on; may be null)
     * @param port Peer port
     * @param codecHandlers Supplies fresh vendor codec handlers for each channel, in pipeline order
     * @param handler The business logic handler
     * @param readTimeoutSeconds Read timeout in seconds (0 to disable)
     * @param writeTimeoutSeconds Write timeout in seconds (0 to disable)
     */
    public HsmClientInitializer(
            SslContext sslContext,
            boolean verifyHostname,
            String host,
            int port,
            Supplier<List<ChannelHandler>> codecHandlers,
            HsmClientHandler<R> handler,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        this.sslContext = sslContext;
        this.verifyHostname = verifyHostname;
        this.host = host;
        this.port = port;
        this.codecHandlers = codecHandlers;
        this.handler = handler;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.writeTimeoutSeconds = writeTimeoutSeconds;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL/TLS Handler (if configured)
        if (sslContext != null) {
            SslHandler sslHandler = host == null
                    ? sslContext.newHandler(ch.alloc())
                    : sslContext.newHandler(ch.alloc(), host, port);
            // Netty 4.2 enables HTTPS endpoint identification by default; HSM certificates
            // usually name the unit (e.g. "HSM-34") rather than a host, so apply the
            // caller's choice explicitly in both directions.
            SSLEngine engine = sslHandler.engine();
            SSLParameters params = engine.getSSLParameters();
            params.setEndpointIdentificationAlgorithm(verifyHostname ? "HTTPS" : null);
            engine.setSSLParameters(params);
            pipeline.addLast("ssl", sslHandler);
        }

        // Idle state handler for connection management
        // Triggers idle events if no read/write activity
        if (readTimeoutSeconds > 0 || writeTimeoutSeconds > 0) {
            pipeline.addLast("idleStateHandler", new IdleStateHandler(
                    readTimeoutSeconds,
                    writeTimeoutSeconds,
                    0,
                    TimeUnit.SECONDS
            ));
        }

        // Vendor protocol codecs: framing, response decoding, command encoding
        List<ChannelHandler> codecs = codecHandlers.get();
        for (int i = 0; i < codecs.size(); i++) {
            pipeline.addLast("codec" + i, codecs.get(i));
        }

        // Business logic handler
        pipeline.addLast("hsmHandler", handler);
    }
}
