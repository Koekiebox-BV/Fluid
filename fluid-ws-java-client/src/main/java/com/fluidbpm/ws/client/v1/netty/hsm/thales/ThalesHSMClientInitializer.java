package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.util.concurrent.TimeUnit;

/**
 * Channel initializer for setting up the Netty Thales HSM client pipeline.
 * Configures SSL/TLS, frame encoding/decoding, command/response codecs,
 * and the business logic handler.
 *
 * Pipeline order (inbound/outbound):
 * 1. SSL Handler (if configured)
 * 2. Idle State Handler (connection keepalive)
 * 3. Frame Decoder (inbound: strips the 2-byte binary length prefix)
 * 4. Frame Encoder (outbound: adds the 2-byte binary length prefix)
 * 5. Response Decoder (inbound: bytes to ThalesResponse, header split off)
 * 6. Command Encoder (outbound: ThalesCommand to header + code + data bytes)
 * 7. Business Logic Handler (ThalesHSMClientHandler)
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesHSMClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private final boolean verifyHostname;
    private final String host;
    private final int port;
    private final ThalesHSMClientHandler handler;
    private final int headerLength;
    private final int readTimeoutSeconds;
    private final int writeTimeoutSeconds;

    /**
     * Constructs a ThalesHSMClientInitializer with SSL support and no message header.
     *
     * @param sslContext The SSL context for secure connections (can be null for unencrypted)
     * @param handler The business logic handler
     */
    public ThalesHSMClientInitializer(SslContext sslContext, ThalesHSMClientHandler handler) {
        this(sslContext, handler, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClientInitializer with SSL and timeout configuration and no message header.
     *
     * @param sslContext The SSL context for secure connections (can be null)
     * @param handler The business logic handler
     * @param readTimeoutSeconds Read timeout in seconds (0 to disable)
     * @param writeTimeoutSeconds Write timeout in seconds (0 to disable)
     */
    public ThalesHSMClientInitializer(
            SslContext sslContext,
            ThalesHSMClientHandler handler,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        this(sslContext, false, null, 0, handler, 0, readTimeoutSeconds, writeTimeoutSeconds);
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
            ThalesHSMClientHandler handler,
            int headerLength,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        this.sslContext = sslContext;
        this.verifyHostname = verifyHostname;
        this.host = host;
        this.port = port;
        this.handler = handler;
        this.headerLength = headerLength;
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
            // Netty 4.2 enables HTTPS endpoint identification by default; payShield
            // certificates usually name the unit (e.g. "HSM-34") rather than a host,
            // so apply the caller's choice explicitly in both directions.
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

        // Thales protocol frame handlers
        // Inbound: strips 2-byte binary length prefix
        pipeline.addLast("frameDecoder", new ThalesFrameDecoder());

        // Outbound: adds 2-byte binary length prefix
        pipeline.addLast("frameEncoder", new ThalesFrameEncoder());

        // Thales message handlers
        // Inbound: converts raw bytes to ThalesResponse objects (header split off)
        pipeline.addLast("responseDecoder", new ThalesResponseDecoder(headerLength));

        // Outbound: converts ThalesCommand objects to raw bytes
        pipeline.addLast("commandEncoder", new ThalesCommandEncoder());

        // Business logic handler
        pipeline.addLast("hsmHandler", handler);
    }
}
