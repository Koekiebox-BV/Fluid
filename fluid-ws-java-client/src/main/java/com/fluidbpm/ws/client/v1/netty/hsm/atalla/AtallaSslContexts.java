package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.KeyStore;

/**
 * Factory for the {@link SslContext} values accepted by {@link AtallaHSMClientConfig}.
 *
 * @deprecated The factories are vendor-neutral; use {@link HsmSslContexts}. This class
 *             delegates and will be removed in a future release.
 * @author jasonbruwer
 * @since 1.15
 */
@Deprecated
public final class AtallaSslContexts {

    private AtallaSslContexts() {
    }

    /**
     * @param identity Key store containing at least one private key entry
     * @param keyPassword Password protecting the private key entries
     * @param trust Key store containing the trusted CA certificate(s); may be the same store
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     * @see HsmSslContexts#mutualTls(KeyStore, char[], KeyStore)
     */
    public static SslContext mutualTls(KeyStore identity, char[] keyPassword, KeyStore trust) throws SSLException {
        return HsmSslContexts.mutualTls(identity, keyPassword, trust);
    }

    /**
     * @param keyStore Key store containing the client key pair and the CA chain
     * @param keyPassword Password protecting the private key entries
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     * @see HsmSslContexts#mutualTls(KeyStore, char[])
     */
    public static SslContext mutualTls(KeyStore keyStore, char[] keyPassword) throws SSLException {
        return HsmSslContexts.mutualTls(keyStore, keyPassword);
    }

    /**
     * @param clientCertChain PEM file with the client certificate (chain)
     * @param clientKey PEM file with the unencrypted private key
     * @param trustChain PEM file with the trusted CA certificate(s)
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     * @see HsmSslContexts#mutualTls(File, File, File)
     */
    public static SslContext mutualTls(File clientCertChain, File clientKey, File trustChain) throws SSLException {
        return HsmSslContexts.mutualTls(clientCertChain, clientKey, trustChain);
    }

    /**
     * @param trust Key store containing the trusted CA certificate(s)
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     * @see HsmSslContexts#serverAuthOnly(KeyStore)
     */
    public static SslContext serverAuthOnly(KeyStore trust) throws SSLException {
        return HsmSslContexts.serverAuthOnly(trust);
    }

    /**
     * @return SslContext for the client that trusts any server certificate
     * @throws SSLException if the context cannot be built
     * @see HsmSslContexts#insecure()
     */
    public static SslContext insecure() throws SSLException {
        return HsmSslContexts.insecure();
    }
}
