/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class for working with Java KeyStore operations.
 * Provides methods to extract private/public key pairs and trust entries from keystores.
 */
public class KeystoreUtil {

    /**
     * Represents a private-public key pair extracted from a keystore.
     */
    public static class KeyPairEntry {
        private final String alias;
        private final PrivateKey privateKey;
        private final PublicKey publicKey;
        private final Certificate[] certificateChain;

        public KeyPairEntry(String alias, PrivateKey privateKey, PublicKey publicKey, Certificate[] certificateChain) {
            this.alias = alias;
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.certificateChain = certificateChain;
        }

        public String getAlias() {
            return alias;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public Certificate[] getCertificateChain() {
            return certificateChain;
        }
    }

    /**
     * Represents a trust entry extracted from a keystore.
     */
    public static class TrustEntry {
        private final String alias;
        private final X509Certificate certificate;

        public TrustEntry(String alias, X509Certificate certificate) {
            this.alias = alias;
            this.certificate = certificate;
        }

        public String getAlias() {
            return alias;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }
    }

    /**
     * Creates a KeyStore from a byte array.
     *
     * @param keystoreBytes The keystore data as byte array
     * @param password The password to unlock the keystore
     * @param keystoreType The type of keystore (e.g., "JKS", "PKCS12")
     * @return The loaded KeyStore
     * @throws KeyStoreException If keystore cannot be created
     * @throws IOException If keystore data cannot be read
     * @throws NoSuchAlgorithmException If the algorithm used to check the integrity of the keystore cannot be found
     * @throws CertificateException If any of the certificates in the keystore could not be loaded
     */
    public static KeyStore loadKeystore(byte[] keystoreBytes, char[] password, String keystoreType)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(keystoreBytes)) {
            keyStore.load(bais, password);
        }
        return keyStore;
    }

    /**
     * Extracts a private-public key pair from a specific keystore entry.
     *
     * @param keyStore The keystore to extract from
     * @param alias The alias of the key entry
     * @param password The password for the key entry
     * @return KeyPairEntry containing the private key, public key, and certificate chain
     * @throws KeyStoreException If the keystore has not been initialized
     * @throws NoSuchAlgorithmException If the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException If the key cannot be recovered (e.g., the given password is wrong)
     */
    public static KeyPairEntry extractKeyPair(KeyStore keyStore, String alias, char[] password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (!keyStore.isKeyEntry(alias)) {
            throw new KeyStoreException("Alias '" + alias + "' is not a key entry");
        }

        Key key = keyStore.getKey(alias, password);
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("Entry '" + alias + "' does not contain a private key");
        }

        PrivateKey privateKey = (PrivateKey) key;
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        
        if (certificateChain == null || certificateChain.length == 0) {
            throw new KeyStoreException("No certificate chain found for alias '" + alias + "'");
        }

        PublicKey publicKey = certificateChain[0].getPublicKey();
        
        return new KeyPairEntry(alias, privateKey, publicKey, certificateChain);
    }

    /**
     * Extracts all private-public key pairs from a keystore.
     *
     * @param keyStore The keystore to extract from
     * @param password The password for the key entries
     * @return List of KeyPairEntry objects
     * @throws KeyStoreException If the keystore has not been initialized
     * @throws NoSuchAlgorithmException If the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException If the key cannot be recovered
     */
    public static List<KeyPairEntry> extractAllKeyPairs(KeyStore keyStore, char[] password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        List<KeyPairEntry> keyPairs = new ArrayList<>();
        Enumeration<String> aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                try {
                    keyPairs.add(extractKeyPair(keyStore, alias, password));
                } catch (KeyStoreException e) {
                    // Skip entries that cannot be extracted
                    System.err.println("Could not extract key pair for alias '" + alias + "': " + e.getMessage());
                }
            }
        }

        return keyPairs;
    }

    /**
     * Extracts a trust entry from a specific keystore alias.
     *
     * @param keyStore The keystore to extract from
     * @param alias The alias of the trust entry
     * @return TrustEntry containing the certificate
     * @throws KeyStoreException If the keystore has not been initialized
     */
    public static TrustEntry extractTrustEntry(KeyStore keyStore, String alias) throws KeyStoreException {
        if (!keyStore.isCertificateEntry(alias)) {
            throw new KeyStoreException("Alias '" + alias + "' is not a certificate entry");
        }

        Certificate certificate = keyStore.getCertificate(alias);
        if (!(certificate instanceof X509Certificate)) {
            throw new KeyStoreException("Certificate for alias '" + alias + "' is not an X509 certificate");
        }

        return new TrustEntry(alias, (X509Certificate) certificate);
    }

    /**
     * Extracts all trust entries from a keystore.
     *
     * @param keyStore The keystore to extract from
     * @return List of TrustEntry objects
     * @throws KeyStoreException If the keystore has not been initialized
     */
    public static List<TrustEntry> extractAllTrustEntries(KeyStore keyStore) throws KeyStoreException {
        List<TrustEntry> trustEntries = new ArrayList<>();
        Enumeration<String> aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isCertificateEntry(alias)) {
                trustEntries.add(extractTrustEntry(keyStore, alias));
            }
        }

        return trustEntries;
    }

    /**
     * Lists all aliases in the keystore.
     *
     * @param keyStore The keystore to list aliases from
     * @return List of all alias names in the keystore
     * @throws KeyStoreException If the keystore has not been initialized
     */
    public static List<String> listAliases(KeyStore keyStore) throws KeyStoreException {
        List<String> aliases = new ArrayList<>();
        Enumeration<String> aliasEnum = keyStore.aliases();

        while (aliasEnum.hasMoreElements()) {
            aliases.add(aliasEnum.nextElement());
        }

        return aliases;
    }

    /**
     * Detects the keystore type from keystore bytes by inspecting file signatures and trying common keystore types.
     *
     * @param keystoreBytes The keystore data as byte array
     * @param password The password to unlock the keystore
     * @return The detected keystore type (e.g., "JKS", "PKCS12", "JCEKS")
     * @throws KeyStoreException If the keystore type cannot be determined
     */
    public static String detectKeystoreType(byte[] keystoreBytes, char[] password) throws KeyStoreException {
        if (keystoreBytes == null || keystoreBytes.length < 4) {
            throw new KeyStoreException("Invalid keystore bytes");
        }

        // Inspect keystore content using magic bytes/signatures
        String detectedType = detectKeystoreTypeBySignature(keystoreBytes);
        if (detectedType != null) {
            // Verify the detected type can actually load the keystore
            try {
                KeyStore keyStore = KeyStore.getInstance(detectedType);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(keystoreBytes)) {
                    keyStore.load(bais, password);
                    return detectedType;
                }
            } catch (Exception e) {
                // Signature matched but loading failed, fall through to iteration
            }
        }

        // Fallback: iterate through common types
        String[] commonTypes = {"PKCS12", "JKS", "JCEKS"};

        for (String type : commonTypes) {
            try {
                KeyStore keyStore = KeyStore.getInstance(type);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(keystoreBytes)) {
                    keyStore.load(bais, password);
                    return type;
                }
            } catch (Exception e) {
                // Try next type
            }
        }

        throw new KeyStoreException("Unable to detect keystore type. Tried: PKCS12, JKS, JCEKS");
    }

    /**
     * Detects keystore type by inspecting file signature/magic bytes.
     *
     * @param keystoreBytes The keystore data as byte array
     * @return The detected keystore type or null if unable to detect from signature
     */
    private static String detectKeystoreTypeBySignature(byte[] keystoreBytes) {
        if (keystoreBytes == null || keystoreBytes.length < 4) {
            return null;
        }

        // JKS/JCEKS magic number: 0xFEEDFEED (big-endian)
        if (keystoreBytes.length >= 4) {
            int magic = ((keystoreBytes[0] & 0xFF) << 24) |
                       ((keystoreBytes[1] & 0xFF) << 16) |
                       ((keystoreBytes[2] & 0xFF) << 8) |
                       (keystoreBytes[3] & 0xFF);

            if (magic == 0xFEEDFEED) {
                // JKS and JCEKS both use the same magic number
                // Try JKS first as it's more common
                return "JKS";
            }
        }

        // PKCS12 starts with ASN.1 SEQUENCE tag (0x30) followed by length encoding
        if (keystoreBytes[0] == 0x30 && (keystoreBytes[1] & 0xFF) >= 0x80) {
            // This is likely a PKCS12 file (ASN.1 DER encoded)
            return "PKCS12";
        }

        // Check for PEM-encoded PKCS12 (begins with "-----BEGIN")
        if (keystoreBytes.length >= 10) {
            String prefix = new String(keystoreBytes, 0, Math.min(10, keystoreBytes.length));
            if (prefix.startsWith("-----BEGIN")) {
                return "PKCS12";
            }
        }

        return null;
    }
}
