package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.netty.hsm.common.AbstractHsmClient;
import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts;
import io.netty.handler.ssl.SslContext;
import lombok.extern.java.Log;

/**
 * Netty-based Atalla AT1000 HSM client for the host command interface.
 *
 * Wire protocol (AT1000 Command Reference Manual, "Command and response format"):
 * <ul>
 *   <li>Messages are ASCII text, {@code <CMDID#field#...#>}, with no length prefix; a
 *       response ends at {@code >} and may be followed by CRLF.</li>
 *   <li>Any command may carry an optional context tag field ({@code ^tag}) which the HSM
 *       returns unmodified. By default the client allocates a unique tag per in-flight
 *       command and uses it to correlate responses, so commands may be pipelined on a single
 *       connection and responses may arrive in any order. With {@code contextTagEnabled} off
 *       in {@link AtallaHSMClientConfig}, untagged commands are matched in send order instead.</li>
 * </ul>
 *
 * TLS / mutual TLS is enabled by supplying an {@link SslContext} through
 * {@link AtallaHSMClientConfig}; see {@link HsmSslContexts}.
 *
 * Connection handling, correlation and timeouts are inherited from {@link AbstractHsmClient}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Log
public class AtallaHSMClient extends AbstractHsmClient<AtallaCommand, AtallaResponse> {

    /**
     * Constructs an AtallaHSMClient and connects to the HSM. When {@code useSsl} is true the
     * server certificate is <b>not</b> verified; use {@link #AtallaHSMClient(AtallaHSMClientConfig)}
     * with {@link HsmSslContexts} for production connections.
     *
     * @param host The HSM host address
     * @param port The HSM port
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @throws Exception If connection fails
     */
    public AtallaHSMClient(String host, int port, boolean useSsl) throws Exception {
        this(host, port, useSsl, 120, 120);
    }

    /**
     * Constructs an AtallaHSMClient with timeout configuration.
     * When {@code useSsl} is true the server certificate is <b>not</b> verified.
     *
     * @param host The HSM host address
     * @param port The HSM port
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @param readTimeoutSeconds Read timeout in seconds
     * @param writeTimeoutSeconds Write timeout in seconds
     * @throws Exception If connection fails
     */
    public AtallaHSMClient(
            String host,
            int port,
            boolean useSsl,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) throws Exception {
        this(legacyConfig(host, port, useSsl, readTimeoutSeconds, writeTimeoutSeconds));
    }

    private static AtallaHSMClientConfig legacyConfig(
            String host, int port, boolean useSsl, int readTimeoutSeconds, int writeTimeoutSeconds
    ) throws Exception {
        SslContext sslCtx = null;
        if (useSsl) {
            log.warning("AtallaHSMClient created with insecure TLS trust for " + host + ":" + port
                    + "; supply an SslContext via AtallaHSMClientConfig for verified/mutual TLS.");
            sslCtx = HsmSslContexts.insecure();
        }
        return AtallaHSMClientConfig.builder()
                .host(host)
                .port(port)
                .sslContext(sslCtx)
                .readTimeoutSeconds(readTimeoutSeconds)
                .writeTimeoutSeconds(writeTimeoutSeconds)
                .build();
    }

    /**
     * Constructs an AtallaHSMClient from a configuration and connects to the HSM.
     *
     * @param config Connection configuration
     * @throws FluidClientException If the configuration is invalid or the connection fails
     */
    public AtallaHSMClient(AtallaHSMClientConfig config) {
        super(config, new AtallaProtocol(config.isContextTagEnabled(), config.getMaxResponseLength()));
    }

    @Override
    public AtallaHSMClientConfig getConfig() {
        return (AtallaHSMClientConfig) super.getConfig();
    }

    @Override
    protected String describeConnection() {
        return getConfig().isContextTagEnabled() ? ", context tags on" : ", context tags off";
    }

    /**
     * Sends a {@code 00} (Echo Test Message) command to test HSM connectivity.
     *
     * @param message The test message (1-1999 characters, no {@code # < >})
     * @return AtallaResponse; {@link AtallaResponse#getEchoMessage()} holds the echoed text
     * @throws Exception If command fails
     */
    public AtallaResponse echo(String message) throws Exception {
        return sendCommand(AtallaCommand.Commands.echo(message));
    }

    /**
     * Sends a {@code 1101} (HSM Software Version) command.
     *
     * @return AtallaResponse with version, image CRC and product code fields
     * @throws Exception If command fails
     */
    public AtallaResponse softwareVersion() throws Exception {
        return sendCommand(AtallaCommand.Commands.softwareVersion());
    }
}
