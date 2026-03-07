package com.fluidbpm.ws.client.v1.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Test case for {@link KeystoreUtil}.
 */
public class TestKeystoreUtil {

    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final char[] KEYSTORE_PASSWORD = "test123".toCharArray();
    private static final char[] KEY_PASSWORD = "test123".toCharArray();
    
    private byte[] keystoreBytes;

    @Before
    public void setUp() throws Exception {
        this.keystoreBytes = KeystoreTestUtil.testKeystore(
                KEYSTORE_TYPE, KEYSTORE_PASSWORD, KEY_PASSWORD
        );
    }

    @After
    public void tearDown() {
        keystoreBytes = null;
    }

    /**
     * Test loading keystore from byte array.
     */
    @Test
    public void testLoadKeystore() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        assertNotNull("KeyStore should be loaded", loadedKeyStore);
        assertTrue("KeyStore should contain testkey alias", loadedKeyStore.containsAlias("testkey"));
        assertTrue("KeyStore should contain testkey2 alias", loadedKeyStore.containsAlias("testkey2"));
        assertTrue("KeyStore should contain trustedca alias", loadedKeyStore.containsAlias("trustedca"));
    }

    /**
     * Test extracting a single key pair from keystore.
     */
    @Test
    public void testExtractKeyPair() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        KeystoreUtil.KeyPairEntry keyPairEntry = KeystoreUtil.extractKeyPair(
            loadedKeyStore, "testkey", KEY_PASSWORD
        );

        assertNotNull("KeyPairEntry should not be null", keyPairEntry);
        assertEquals("Alias should match", "testkey", keyPairEntry.getAlias());
        assertNotNull("Private key should not be null", keyPairEntry.getPrivateKey());
        assertNotNull("Public key should not be null", keyPairEntry.getPublicKey());
        assertNotNull("Certificate chain should not be null", keyPairEntry.getCertificateChain());
        assertTrue("Certificate chain should have at least one certificate", 
            keyPairEntry.getCertificateChain().length > 0);
        
        // Verify the public key matches the certificate's public key
        PublicKey certPublicKey = keyPairEntry.getCertificateChain()[0].getPublicKey();
        assertEquals("Public key should match certificate's public key", 
            certPublicKey, keyPairEntry.getPublicKey());
    }

    /**
     * Test extracting all key pairs from keystore.
     */
    @Test
    public void testExtractAllKeyPairs() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        List<KeystoreUtil.KeyPairEntry> keyPairs = KeystoreUtil.extractAllKeyPairs(
            loadedKeyStore, KEY_PASSWORD
        );

        assertNotNull("Key pairs list should not be null", keyPairs);
        assertEquals("Should have 2 key pairs", 2, keyPairs.size());
        
        // Verify both entries are present
        boolean hasTestkey = false;
        boolean hasTestkey2 = false;
        
        for (KeystoreUtil.KeyPairEntry entry : keyPairs) {
            if ("testkey".equals(entry.getAlias())) {
                hasTestkey = true;
            } else if ("testkey2".equals(entry.getAlias())) {
                hasTestkey2 = true;
            }
            
            assertNotNull("Private key should not be null", entry.getPrivateKey());
            assertNotNull("Public key should not be null", entry.getPublicKey());
        }
        
        assertTrue("Should contain testkey", hasTestkey);
        assertTrue("Should contain testkey2", hasTestkey2);
    }

    /**
     * Test extracting a single trust entry from keystore.
     */
    @Test
    public void testExtractTrustEntry() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        KeystoreUtil.TrustEntry trustEntry = KeystoreUtil.extractTrustEntry(
            loadedKeyStore, "trustedca"
        );

        assertNotNull("TrustEntry should not be null", trustEntry);
        assertEquals("Alias should match", "trustedca", trustEntry.getAlias());
        assertNotNull("Certificate should not be null", trustEntry.getCertificate());
        
        X509Certificate cert = trustEntry.getCertificate();
        assertTrue("Subject DN should contain CN=Trusted CA", 
            cert.getSubjectDN().getName().contains("CN=Trusted CA"));
    }

    /**
     * Test extracting all trust entries from keystore.
     */
    @Test
    public void testExtractAllTrustEntries() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        List<KeystoreUtil.TrustEntry> trustEntries = KeystoreUtil.extractAllTrustEntries(loadedKeyStore);

        assertNotNull("Trust entries list should not be null", trustEntries);
        assertEquals("Should have 1 trust entry", 1, trustEntries.size());
        
        KeystoreUtil.TrustEntry trustEntry = trustEntries.get(0);
        assertEquals("Alias should be trustedca", "trustedca", trustEntry.getAlias());
        assertNotNull("Certificate should not be null", trustEntry.getCertificate());
    }

    /**
     * Test error handling when extracting key pair with wrong alias.
     */
    @Test(expected = KeyStoreException.class)
    public void testExtractKeyPairWithInvalidAlias() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        // Try to extract key pair from a certificate entry (should fail)
        KeystoreUtil.extractKeyPair(loadedKeyStore, "trustedca", KEY_PASSWORD);
    }

    /**
     * Test error handling when extracting trust entry with wrong alias.
     */
    @Test(expected = KeyStoreException.class)
    public void testExtractTrustEntryWithInvalidAlias() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);
        
        // Try to extract trust entry from a key entry (should fail)
        KeystoreUtil.extractTrustEntry(loadedKeyStore, "testkey");
    }

    /**
     * Test error handling with wrong password.
     */
    @Test(expected = UnrecoverableKeyException.class)
    public void testExtractKeyPairWithWrongPassword() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);

        // Try to extract key pair with wrong password
        KeystoreUtil.extractKeyPair(loadedKeyStore, "testkey", "wrongpassword".toCharArray());
    }

    /**
     * Test listing all aliases in the keystore.
     */
    @Test
    public void testListAliases() throws Exception {
        KeyStore loadedKeyStore = KeystoreUtil.loadKeystore(keystoreBytes, KEYSTORE_PASSWORD, KEYSTORE_TYPE);

        List<String> aliases = KeystoreUtil.listAliases(loadedKeyStore);

        assertNotNull("Aliases list should not be null", aliases);
        assertEquals("Should have 3 aliases", 3, aliases.size());
        assertTrue("Should contain testkey", aliases.contains("testkey"));
        assertTrue("Should contain testkey2", aliases.contains("testkey2"));
        assertTrue("Should contain trustedca", aliases.contains("trustedca"));
    }

    /**
     * Test detecting keystore type.
     */
    @Test
    public void testDetectKeystoreType() throws Exception {
        String detectedType = KeystoreUtil.detectKeystoreType(keystoreBytes, KEYSTORE_PASSWORD);

        assertNotNull("Detected type should not be null", detectedType);
        assertEquals("Detected type should be PKCS12", KEYSTORE_TYPE, detectedType);
    }

    /**
     * Test detecting keystore type with wrong password.
     */
    @Test(expected = KeyStoreException.class)
    public void testDetectKeystoreTypeWithWrongPassword() throws Exception {
        KeystoreUtil.detectKeystoreType(keystoreBytes, "wrongpassword".toCharArray());
    }

    /**
     * Helper method to generate a self-signed X.509 certificate using BouncyCastle.
     */
    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String dn) throws Exception {
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
