package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesHSMClient;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesHSMClientConfig;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesSslContexts;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ServerSocketFactory;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Mutual TLS against {@link FakeThalesHsmServer} running behind an {@link SSLServerSocket}
 * that requires a client certificate, mirroring payShield Secure Host Communications.
 * Certificates are generated at test time so no secrets live in the repository.
 */
public class TestThalesSslContexts {

    private static final char[] PW = "test".toCharArray();

    private static KeyStore trustStore;     // CA only
    private static KeyStore clientStore;    // client key pair + CA
    private static KeyStore serverStore;    // server key pair

    @BeforeClass
    public static void generateMaterial() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        KeyPair caKp = kpg.generateKeyPair();
        X509Certificate ca = cert("CN=Test HSM CA", caKp, "CN=Test HSM CA", caKp.getPrivate(), true);

        KeyPair serverKp = kpg.generateKeyPair();
        X509Certificate server = cert("CN=HSM-34", serverKp, "CN=Test HSM CA", caKp.getPrivate(), false);

        KeyPair clientKp = kpg.generateKeyPair();
        X509Certificate client = cert("CN=pericard-host", clientKp, "CN=Test HSM CA", caKp.getPrivate(), false);

        trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", ca);

        clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(null, null);
        clientStore.setKeyEntry("client", clientKp.getPrivate(), PW, new X509Certificate[]{client, ca});
        clientStore.setCertificateEntry("ca", ca);

        serverStore = KeyStore.getInstance("PKCS12");
        serverStore.load(null, null);
        serverStore.setKeyEntry("server", serverKp.getPrivate(), PW, new X509Certificate[]{server, ca});
    }

    private static X509Certificate cert(String subject, KeyPair subjectKp, String issuer, PrivateKey issuerKey, boolean isCa)
            throws Exception {
        long now = System.currentTimeMillis();
        X509v3CertificateBuilder b = new JcaX509v3CertificateBuilder(
                new X500Name(issuer), BigInteger.valueOf(now), new Date(now - 60_000),
                new Date(now + 3_600_000L), new X500Name(subject), subjectKp.getPublic());
        b.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCa));
        return new JcaX509CertificateConverter().setProvider("BC")
                .getCertificate(b.build(new JcaContentSignerBuilder("SHA256withRSA").build(issuerKey)));
    }

    /** Server socket factory that requires a client certificate signed by the test CA. */
    private static ServerSocketFactory serverFactory() throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(serverStore, PW);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        final SSLServerSocketFactory delegate = ctx.getServerSocketFactory();
        return new ServerSocketFactory() {
            private ServerSocket configure(ServerSocket s) {
                ((SSLServerSocket) s).setNeedClientAuth(true);
                return s;
            }
            @Override public ServerSocket createServerSocket(int port) throws java.io.IOException {
                return configure(delegate.createServerSocket(port));
            }
            @Override public ServerSocket createServerSocket(int port, int backlog) throws java.io.IOException {
                return configure(delegate.createServerSocket(port, backlog));
            }
            @Override public ServerSocket createServerSocket(int port, int backlog, java.net.InetAddress addr) throws java.io.IOException {
                return configure(delegate.createServerSocket(port, backlog, addr));
            }
        };
    }

    @Test(timeout = 30000)
    public void mutualTlsHandshakeAndCommand() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4, serverFactory());
             ThalesHSMClient client = new ThalesHSMClient(ThalesHSMClientConfig.builder()
                     .host("127.0.0.1").port(hsm.getPort()).headerLength(4)
                     .sslContext(ThalesSslContexts.mutualTls(clientStore, PW, trustStore))
                     .readTimeoutSeconds(0).writeTimeoutSeconds(0)
                     .build())) {
            assertTrue(client.isUseSsl());
            assertEquals("SECURE", client.echo("SECURE").getResponseData());
        }
    }

    @Test(timeout = 30000)
    public void missingClientCertificateIsRejected() throws Exception {
        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4, serverFactory())) {
            try (ThalesHSMClient client = new ThalesHSMClient(ThalesHSMClientConfig.builder()
                    .host("127.0.0.1").port(hsm.getPort()).headerLength(4)
                    .sslContext(ThalesSslContexts.serverAuthOnly(trustStore))
                    .readTimeoutSeconds(0).writeTimeoutSeconds(0)
                    .build())) {
                // Some JSSE versions only surface the client-auth failure on first I/O
                client.echo("X");
                fail("handshake without a client certificate must fail");
            } catch (FluidClientException | java.util.concurrent.ExecutionException expected) {
                // ok
            }
        }
    }

    @Test(timeout = 30000)
    public void untrustedServerIsRejected() throws Exception {
        KeyStore emptyTrust = KeyStore.getInstance("PKCS12");
        emptyTrust.load(null, null);
        emptyTrust.setCertificateEntry("unrelated", (X509Certificate) serverStore.getCertificateChain("server")[0]);
        // trusting only the leaf server cert as an anchor still validates; use a fresh CA instead
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair otherCa = kpg.generateKeyPair();
        KeyStore wrongTrust = KeyStore.getInstance("PKCS12");
        wrongTrust.load(null, null);
        wrongTrust.setCertificateEntry("other", cert("CN=Other CA", otherCa, "CN=Other CA", otherCa.getPrivate(), true));

        try (FakeThalesHsmServer hsm = new FakeThalesHsmServer(4, serverFactory())) {
            try (ThalesHSMClient client = new ThalesHSMClient(ThalesHSMClientConfig.builder()
                    .host("127.0.0.1").port(hsm.getPort()).headerLength(4)
                    .sslContext(ThalesSslContexts.mutualTls(clientStore, PW, wrongTrust))
                    .readTimeoutSeconds(0).writeTimeoutSeconds(0)
                    .build())) {
                client.echo("X");
                fail("server signed by an unknown CA must be rejected");
            } catch (FluidClientException | java.util.concurrent.ExecutionException expected) {
                // ok
            }
        }
    }
}
