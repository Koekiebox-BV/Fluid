package com.fluidbpm.ws.client.v1.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Utility class for creating in-memory test keystores populated with private keys
 * and certificates. This is intended for testing purposes only.
 */
public class KeystoreTestUtil {
    public static byte[] testKeystore(
            String keystoreType,
            char[] keystorePassword,
            char[] keyPassword
    ) throws
            KeyStoreException,
            OperatorCreationException,
            CertificateException,
            NoSuchAlgorithmException,
            IOException
    {
        // Add BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());

        // Create a test keystore in memory
        KeyStore testKeyStore = KeyStore.getInstance(keystoreType);
        testKeyStore.load(null, keystorePassword);

        // Generate a key pair for testing
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Create a self-signed certificate
        X509Certificate cert = generateSelfSignedCertificate(keyPair, "CN=Test Certificate");
        Certificate[] chain = new Certificate[] { cert };

        // Add key entry to keystore
        testKeyStore.setKeyEntry("testkey", keyPair.getPrivate(), keyPassword, chain);

        // Generate another key pair for a second entry
        KeyPair keyPair2 = keyPairGen.generateKeyPair();
        X509Certificate cert2 = generateSelfSignedCertificate(keyPair2, "CN=Test Certificate 2");
        Certificate[] chain2 = new Certificate[] { cert2 };
        testKeyStore.setKeyEntry("testkey2", keyPair2.getPrivate(), keyPassword, chain2);

        // Add a trust certificate entry
        KeyPair trustKeyPair = keyPairGen.generateKeyPair();
        X509Certificate trustCert = generateSelfSignedCertificate(trustKeyPair, "CN=Trusted CA");
        testKeyStore.setCertificateEntry("trustedca", trustCert);

        // Convert keystore to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testKeyStore.store(baos, keystorePassword);
        return baos.toByteArray();
    }

    /**
     * Helper method to generate a self-signed X.509 certificate using BouncyCastle.
     */
    private static X509Certificate generateSelfSignedCertificate(
            KeyPair keyPair,
            String dn
    ) throws OperatorCreationException, CertificateException {
        Date from = new Date();
        Date to = new Date(from.getTime() + 365 * 24 * 60 * 60 * 1000L); // 1 year validity

        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X500Name issuer = new X500Name(dn);
        X500Name subject = new X500Name(dn);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                from,
                to,
                subject,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);
    }
}
