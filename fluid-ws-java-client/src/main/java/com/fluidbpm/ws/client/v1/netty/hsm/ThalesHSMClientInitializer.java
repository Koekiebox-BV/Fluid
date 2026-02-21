package com.fluidbpm.ws.client.v1.netty.hsm;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Channel initializer for setting up the Netty Thales HSM client pipeline.
 * Configures SSL/TLS, frame encoding/decoding, command/response codecs,
 * and the business logic handler.
 *
 * Pipeline order (inbound/outbound):
 * 1. SSL Handler (if configured)
 * 2. Idle State Handler (connection keepalive)
 * 3. Frame Decoder (inbound: bytes -> bytes with length stripped)
 * 4. Frame Encoder (outbound: bytes -> bytes with length header)
 * 5. Response Decoder (inbound: bytes -> ThalesResponse)
 * 6. Command Encoder (outbound: ThalesCommand -> bytes)
 * 7. Business Logic Handler (ThalesHSMClientHandler)
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesHSMClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private final ThalesHSMClientHandler handler;
    private final int readTimeoutSeconds;
    private final int writeTimeoutSeconds;

    /**
     * Constructs a ThalesHSMClientInitializer with SSL support.
     *
     * @param sslContext The SSL context for secure connections (can be null for unencrypted)
     * @param handler The business logic handler
     */
    public ThalesHSMClientInitializer(SslContext sslContext, ThalesHSMClientHandler handler) {
        this(sslContext, handler, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClientInitializer with SSL and timeout configuration.
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
        this.sslContext = sslContext;
        this.handler = handler;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.writeTimeoutSeconds = writeTimeoutSeconds;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL/TLS Handler (if configured)
        if (sslContext != null) {
            pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
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
        // Inbound: strips 4-byte length header and validates message
        pipeline.addLast("frameDecoder", new ThalesFrameDecoder());

        // Outbound: adds 4-byte length header
        pipeline.addLast("frameEncoder", new ThalesFrameEncoder());

        // Thales message handlers
        // Inbound: converts raw bytes to ThalesResponse objects
        pipeline.addLast("responseDecoder", new ThalesResponseDecoder());

        // Outbound: converts ThalesCommand objects to raw bytes
        pipeline.addLast("commandEncoder", new ThalesCommandEncoder());

        // Business logic handler
        pipeline.addLast("hsmHandler", handler);
    }
}
