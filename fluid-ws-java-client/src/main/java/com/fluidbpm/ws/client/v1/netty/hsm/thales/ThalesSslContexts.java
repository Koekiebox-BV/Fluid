package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * Factory for the {@link SslContext} values accepted by {@link ThalesHSMClientConfig}.
 *
 * payShield 10K "Secure Host Communications" authenticates the host with a client
 * certificate and presents an HSM certificate signed by the customer CA, so the normal
 * configuration is {@link #mutualTls(KeyStore, char[], KeyStore)}: a key store holding the
 * host's key pair and a trust store holding the CA chain.
 *
 * Hostname (endpoint) identification is disabled on every context built here because
 * payShield certificates typically name the unit rather than a DNS host; opt back in with
 * {@code ThalesHSMClientConfig.builder().verifyHostname(true)}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public final class ThalesSslContexts {

    private ThalesSslContexts() {
    }

    /**
     * Mutual TLS: present the client key pair from {@code identity} and trust the
     * certificates in {@code trust}.
     *
     * @param identity Key store containing at least one private key entry
     * @param keyPassword Password protecting the private key entries
     * @param trust Key store containing the trusted CA certificate(s); may be the same store
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     */
    public static SslContext mutualTls(KeyStore identity, char[] keyPassword, KeyStore trust) throws SSLException {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(identity, keyPassword);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trust);
            return SslContextBuilder.forClient()
                    .keyManager(kmf)
                    .trustManager(tmf)
                    .endpointIdentificationAlgorithm(null)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            throw new SSLException("Unable to build mutual TLS context: " + e.getMessage(), e);
        }
    }

    /**
     * Mutual TLS using a single key store for both identity and trust.
     *
     * @param keyStore Key store containing the client key pair and the CA chain
     * @param keyPassword Password protecting the private key entries
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     */
    public static SslContext mutualTls(KeyStore keyStore, char[] keyPassword) throws SSLException {
        return mutualTls(keyStore, keyPassword, keyStore);
    }

    /**
     * Server-authenticated TLS only (no client certificate).
     *
     * @param trust Key store containing the trusted CA certificate(s)
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     */
    public static SslContext serverAuthOnly(KeyStore trust) throws SSLException {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trust);
            return SslContextBuilder.forClient()
                    .trustManager(tmf)
                    .endpointIdentificationAlgorithm(null)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new SSLException("Unable to build TLS context: " + e.getMessage(), e);
        }
    }

    /**
     * TLS that accepts any server certificate. Only for local simulators and diagnostics;
     * never for production traffic.
     *
     * @return SslContext for the client
     * @throws SSLException if the context cannot be built
     */
    public static SslContext insecure() throws SSLException {
        return SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .endpointIdentificationAlgorithm(null)
                .build();
    }
}
