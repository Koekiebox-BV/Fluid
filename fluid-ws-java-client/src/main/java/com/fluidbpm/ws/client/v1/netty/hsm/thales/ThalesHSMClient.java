package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.netty.hsm.common.AbstractHsmClient;
import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts;
import io.netty.handler.ssl.SslContext;
import lombok.extern.java.Log;

/**
 * Netty-based Thales payShield HSM client for the host command interface.
 *
 * Wire protocol (payShield 10K Host Programmers Manual):
 * <ul>
 *   <li>Each message is prefixed with a 2-byte binary length (section 2.1.2 / 2.1.3).</li>
 *   <li>Each command starts with the site-configured message header, which the HSM
 *       echoes back unmodified (section 1.2). The client allocates a unique header per
 *       in-flight command and uses it to correlate responses, so commands may be
 *       pipelined on a single connection and responses may arrive in any order.</li>
 * </ul>
 *
 * TLS / mutual TLS is enabled by supplying an {@link SslContext} through
 * {@link ThalesHSMClientConfig}; see {@link HsmSslContexts}.
 *
 * Connection handling, correlation and timeouts are inherited from {@link AbstractHsmClient}.
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Log
public class ThalesHSMClient extends AbstractHsmClient<ThalesCommand, ThalesResponse> {

    /**
     * Constructs a ThalesHSMClient and connects to the HSM using the default
     * {@link ThalesHSMClientConfig#DEFAULT_HEADER_LENGTH 4-character} message header.
     * When {@code useSsl} is true the server certificate is <b>not</b> verified; use
     * {@link #ThalesHSMClient(ThalesHSMClientConfig)} with {@link HsmSslContexts} for
     * production connections.
     *
     * @param host The HSM host address
     * @param port The HSM port (typically 1500 for Thales)
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @throws Exception If connection fails
     */
    public ThalesHSMClient(String host, int port, boolean useSsl) throws Exception {
        this(host, port, useSsl, 120, 120);
    }

    /**
     * Constructs a ThalesHSMClient with timeout configuration and the default
     * {@link ThalesHSMClientConfig#DEFAULT_HEADER_LENGTH 4-character} message header.
     * When {@code useSsl} is true the server certificate is <b>not</b> verified.
     *
     * @param host The HSM host address
     * @param port The HSM port
     * @param useSsl Whether to use SSL/TLS encryption (insecure trust)
     * @param readTimeoutSeconds Read timeout in seconds
     * @param writeTimeoutSeconds Write timeout in seconds
     * @throws Exception If connection fails
     */
    public ThalesHSMClient(
            String host,
            int port,
            boolean useSsl,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) throws Exception {
        this(legacyConfig(host, port, useSsl, readTimeoutSeconds, writeTimeoutSeconds));
    }

    private static ThalesHSMClientConfig legacyConfig(
            String host, int port, boolean useSsl, int readTimeoutSeconds, int writeTimeoutSeconds
    ) throws Exception {
        SslContext sslCtx = null;
        if (useSsl) {
            log.warning("ThalesHSMClient created with insecure TLS trust for " + host + ":" + port
                    + "; supply an SslContext via ThalesHSMClientConfig for verified/mutual TLS.");
            sslCtx = HsmSslContexts.insecure();
        }
        return ThalesHSMClientConfig.builder()
                .host(host)
                .port(port)
                .sslContext(sslCtx)
                .readTimeoutSeconds(readTimeoutSeconds)
                .writeTimeoutSeconds(writeTimeoutSeconds)
                .build();
    }

    /**
     * Constructs a ThalesHSMClient from a configuration and connects to the HSM.
     *
     * @param config Connection configuration
     * @throws FluidClientException If the configuration is invalid or the connection fails
     */
    public ThalesHSMClient(ThalesHSMClientConfig config) {
        super(config, new ThalesProtocol(config.getHeaderLength()));
    }

    @Override
    public ThalesHSMClientConfig getConfig() {
        return (ThalesHSMClientConfig) super.getConfig();
    }

    /**
     * Gets the message header length this client correlates responses with.
     *
     * @return The header length configured on the HSM
     */
    public int getHeaderLength() {
        return getConfig().getHeaderLength();
    }

    @Override
    protected String describeConnection() {
        return ", header length " + getHeaderLength();
    }

    /**
     * Sends an {@code NO} (HSM status) command to test HSM connectivity.
     *
     * @param echoData The command data
     * @return ThalesResponse
     * @throws Exception If command fails
     */
    public ThalesResponse echo(String echoData) throws Exception {
        return sendCommand(ThalesCommand.Commands.echo(echoData));
    }

    /**
     * Sends a diagnostics command to check HSM status.
     *
     * @return ThalesResponse with HSM status
     * @throws Exception If command fails
     */
    public ThalesResponse diagnostics() throws Exception {
        return sendCommand(ThalesCommand.Commands.diagnostics());
    }
}
