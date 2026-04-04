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

package com.fluidbpm.ws.client.v1.crypto.asymmetric.pgp;

import lombok.Getter;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.api.OpenPGPCertificate;
import org.bouncycastle.openpgp.api.OpenPGPDetachedSignatureGenerator;
import org.bouncycastle.openpgp.api.OpenPGPDetachedSignatureProcessor;
import org.bouncycastle.openpgp.api.OpenPGPKey;
import org.bouncycastle.openpgp.api.OpenPGPSignature;
import org.bouncycastle.openpgp.api.SignatureParameters;
import org.bouncycastle.openpgp.api.SignatureSubpacketsFunction;
import org.bouncycastle.openpgp.api.jcajce.JcaOpenPGPImplementation;
import org.bouncycastle.openpgp.api.jcajce.JcaOpenPGPKeyGenerator;
import org.bouncycastle.openpgp.operator.PGPKeyPairGenerator;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * PGP utility supporting RSA and Ed25519 key generation, encryption, decryption, signing, and verification.
 *
 * <p>Uses BouncyCastle's {@code bcpg-jdk18on} library.
 *
 * <h2>Supported Key Types</h2>
 * <ul>
 *   <li>{@link KeyType#RSA} — 4096-bit RSA master (sign) + RSA subkey (encrypt)</li>
 *   <li>{@link KeyType#Ed25519} — Ed25519 master (sign) + X25519 subkey (encrypt)</li>
 * </ul>
 */
public class PGPUtil {
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static final int RSA_KEY_SIZE = 4096;
    private static final int ENCRYPT_BUFFER_SIZE = 4096;
    private static final int VERSION = PublicKeyPacket.VERSION_6;

    /** Supported PGP key algorithms. */
    public enum KeyType {
        RSA,
        Ed25519
    }

    /**
     * Holds a generated PGP secret key ring, its corresponding public key ring,
     * and the original {@link OpenPGPKey} (required for signing).
     */
    public static class PGPKeyPairResult {
        private final PGPSecretKeyRing secretKeyRing;
        private final PGPPublicKeyRing publicKeyRing;
        private final OpenPGPKey openPGPKey;

        PGPKeyPairResult(PGPSecretKeyRing secretKeyRing, PGPPublicKeyRing publicKeyRing, OpenPGPKey openPGPKey) {
            this.secretKeyRing = secretKeyRing;
            this.publicKeyRing = publicKeyRing;
            this.openPGPKey = openPGPKey;
        }

        /** @return The secret (private) key ring containing master key and encryption subkey. */
        public PGPSecretKeyRing getSecretKeyRing() { return secretKeyRing; }

        /** @return The public key ring containing master key and encryption subkey. */
        public PGPPublicKeyRing getPublicKeyRing() { return publicKeyRing; }

        /** @return The original {@link OpenPGPKey} — needed for signing operations. */
        public OpenPGPKey getOpenPGPKey() { return openPGPKey; }
    }

    // -------------------------------------------------------------------------
    // Key generation and export
    // -------------------------------------------------------------------------

    /**
     * Generates a PGP key pair with an encryption subkey.
     *
     * <p>For {@link KeyType#RSA}: creates a 4096-bit RSA master key (sign/certify) and a
     * 4096-bit RSA encryption subkey.
     *
     * <p>For {@link KeyType#Ed25519}: creates an Ed25519 master key (sign/certify) and an
     * X25519 ECDH encryption subkey.
     *
     * @param keyType    The key algorithm to use
     * @param userId     The user ID string (e.g. {@code "Alice <alice@example.com>"})
     * @param passphrase Passphrase to protect the secret key
     * @return A {@link PGPKeyPairResult} containing both the secret and public key rings
     * @throws PGPException             if key ring assembly fails
     * @throws GeneralSecurityException if key pair generation fails
     * @throws IOException              if encoding fails
     */
    public static PGPKeyPairResult generateKeyPair(
            KeyType keyType,
            String userId,
            char[] passphrase
    ) throws PGPException, GeneralSecurityException, IOException {
        JcaOpenPGPKeyGenerator gen = new JcaOpenPGPKeyGenerator(VERSION,
                Security.getProvider(BouncyCastleProvider.PROVIDER_NAME));

        // Signature callback that enables both CERTIFY and SIGN on the primary key
        SignatureParameters.Callback primaryKeySignFlags = new SignatureParameters.Callback() {
            @Override
            public SignatureParameters apply(SignatureParameters params) {
                return params.setHashedSubpacketsFunction(new SignatureSubpacketsFunction() {
                    @Override
                    public PGPSignatureSubpacketGenerator apply(PGPSignatureSubpacketGenerator subpkts) {
                        subpkts.setKeyFlags(false, PGPKeyFlags.CAN_CERTIFY | PGPKeyFlags.CAN_SIGN);
                        return subpkts;
                    }
                });
            }
        };

        OpenPGPKey key;
        if (keyType == KeyType.RSA) {
            key = gen.withPrimaryKey(g -> g.generateRsaKeyPair(RSA_KEY_SIZE), primaryKeySignFlags)
                    .addUserId(userId)
                    .addEncryptionSubkey(g -> g.generateRsaKeyPair(RSA_KEY_SIZE))
                    .build(passphrase);
        } else {
            key = gen.withPrimaryKey(PGPKeyPairGenerator::generateEd25519KeyPair, primaryKeySignFlags)
                    .addUserId(userId)
                    .addEncryptionSubkey(PGPKeyPairGenerator::generateX25519KeyPair)
                    .build(passphrase);
        }

        return new PGPKeyPairResult(key.getPGPSecretKeyRing(), key.getPGPPublicKeyRing(), key);
    }

    /**
     * Exports the public key ring as an ASCII-armored PGP public key block string.
     *
     * @param result The key pair result from {@link #generateKeyPair}
     * @return ASCII-armored public key string (starts with {@code -----BEGIN PGP PUBLIC KEY BLOCK-----})
     * @throws IOException if encoding fails
     */
    public static String exportPublicKey(PGPKeyPairResult result) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArmoredOutputStream armor = new ArmoredOutputStream(baos)) {
            result.getPublicKeyRing().encode(armor);
        }
        return baos.toString("UTF-8");
    }

    /**
     * Exports the public key ring as raw (non-armored) bytes.
     *
     * @param result The key pair result from {@link #generateKeyPair}
     * @return Raw binary encoding of the public key ring
     * @throws IOException if encoding fails
     */
    public static byte[] exportPublicKeyBytes(PGPKeyPairResult result) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.getPublicKeyRing().encode(baos);
        return baos.toByteArray();
    }

    // -------------------------------------------------------------------------
    // Encrypt / Decrypt
    // -------------------------------------------------------------------------

    /**
     * Encrypts plaintext for a recipient using their PGP public key ring.
     *
     * <p>The output is a binary PGP message with AES-256 symmetric encryption, ZIP compression,
     * and an integrity protection packet. The recipient's encryption subkey is used for key wrapping.
     *
     * @param plaintext              The data to encrypt
     * @param recipientPublicKeyRing The recipient's public key ring (must contain an encryption subkey)
     * @return Binary PGP encrypted message
     * @throws PGPException if no encryption key is found or encryption fails
     * @throws IOException  if I/O fails
     */
    public static byte[] encrypt(
            byte[] plaintext,
            PGPPublicKeyRing recipientPublicKeyRing
    ) throws PGPException, IOException {

        PGPPublicKey encKey = findEncryptionKey(recipientPublicKeyRing);

        ByteArrayOutputStream encOut = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256)
                        .setWithIntegrityPacket(true)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME));
        encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey)
                .setProvider(BouncyCastleProvider.PROVIDER_NAME));

        try (OutputStream encryptedOut = encGen.open(encOut, new byte[ENCRYPT_BUFFER_SIZE])) {
            PGPCompressedDataGenerator compGen = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
            try (OutputStream compOut = compGen.open(encryptedOut)) {
                PGPLiteralDataGenerator litGen = new PGPLiteralDataGenerator();
                try (OutputStream litOut = litGen.open(compOut, PGPLiteralData.BINARY, "", plaintext.length, new Date())) {
                    litOut.write(plaintext);
                }
            }
        }

        return encOut.toByteArray();
    }

    /**
     * Decrypts a PGP-encrypted message using the recipient's secret key.
     *
     * <p>Accepts both binary and ASCII-armored input.
     *
     * @param ciphertext    The PGP-encrypted message (binary or armored)
     * @param secretKeyRing The recipient's key pair (must contain the matching secret key)
     * @param passphrase    Passphrase to unlock the secret key
     * @return Decrypted plaintext bytes
     * @throws PGPException if no matching key is found or decryption fails
     * @throws IOException  if I/O fails
     */
    public static byte[] decrypt(
            byte[] ciphertext,
            PGPSecretKeyRing secretKeyRing,
            char[] passphrase
    ) throws PGPException, IOException {

        InputStream decoderStream = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(
                new ByteArrayInputStream(ciphertext));
        PGPObjectFactory pgpF = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());

        PGPEncryptedDataList encList = null;
        Object obj;
        while ((obj = pgpF.nextObject()) != null) {
            if (obj instanceof PGPEncryptedDataList) {
                encList = (PGPEncryptedDataList) obj;
                break;
            }
        }
        if (encList == null) throw new PGPException("No encrypted data found in input");

        Iterator<PGPEncryptedData> encDataIt = encList.getEncryptedDataObjects();
        PGPPrivateKey privateKey = null;
        PGPPublicKeyEncryptedData pked = null;

        while (encDataIt.hasNext()) {
            PGPEncryptedData encData = encDataIt.next();
            if (encData instanceof PGPPublicKeyEncryptedData) {
                pked = (PGPPublicKeyEncryptedData) encData;
                privateKey = findSecretKey(
                        secretKeyRing,
                        pked.getKeyIdentifier(),
                        passphrase
                );
                if (privateKey != null) break;
            }
        }

        if (privateKey == null) throw new PGPException("No matching private key found for decryption");

        InputStream clear = pked.getDataStream(
                new JcePublicKeyDataDecryptorFactoryBuilder()
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .build(privateKey));

        PGPObjectFactory plainFact = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());
        Object message = plainFact.nextObject();

        if (message instanceof PGPCompressedData) {
            PGPCompressedData compData = (PGPCompressedData) message;
            plainFact = new PGPObjectFactory(compData.getDataStream(), new JcaKeyFingerprintCalculator());
            message = plainFact.nextObject();
        }

        if (message instanceof PGPLiteralData) {
            return Streams.readAll(((PGPLiteralData) message).getInputStream());
        }

        throw new PGPException("Unexpected PGP message type during decryption: " + message.getClass().getName());
    }

    // -------------------------------------------------------------------------
    // Sign / Verify
    // -------------------------------------------------------------------------

    /**
     * Creates a detached PGP signature over the given data using the master signing key.
     *
     * <p>The returned bytes are an ASCII-armored detached signature block
     * (starts with {@code -----BEGIN PGP SIGNATURE-----}).
     *
     * @param data       The data to sign
     * @param openPGPKey The signer's key (obtained from {@link PGPKeyPairResult#getOpenPGPKey()})
     * @param passphrase Passphrase to unlock the secret signing key
     * @return ASCII-armored detached PGP signature
     * @throws PGPException if signing fails
     * @throws IOException  if I/O fails
     */
    public static byte[] sign(
            byte[] data,
            OpenPGPKey openPGPKey,
            char[] passphrase
    ) throws PGPException, IOException {
        JcaOpenPGPImplementation impl = new JcaOpenPGPImplementation(
                Security.getProvider(BouncyCastleProvider.PROVIDER_NAME), new SecureRandom());

        OpenPGPDetachedSignatureGenerator sigGen = new OpenPGPDetachedSignatureGenerator(impl);
        sigGen.addSigningKey(openPGPKey, key -> passphrase);

        List<OpenPGPSignature.OpenPGPDocumentSignature> sigs =
                sigGen.sign(new ByteArrayInputStream(data));

        ByteArrayOutputStream sigOut = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOut = new ArmoredOutputStream(sigOut)) {
            sigs.get(0).getSignature().encode(armoredOut);
        }
        return sigOut.toByteArray();
    }

    /**
     * Verifies a detached PGP signature against the given data.
     *
     * <p>Accepts both binary and ASCII-armored signature input.
     *
     * @param data           The original data that was signed
     * @param signature      The detached PGP signature (binary or armored)
     * @param publicKeyRing  The signer's public key ring
     * @return {@code true} if the signature is valid; {@code false} if the signing key is not found
     *         or the signature does not verify
     * @throws PGPException if signature parsing or verification fails
     * @throws IOException  if I/O fails
     */
    public static boolean verify(
            byte[] data,
            byte[] signature,
            PGPPublicKeyRing publicKeyRing
    ) throws PGPException, IOException {
        JcaOpenPGPImplementation impl = new JcaOpenPGPImplementation(
                Security.getProvider(BouncyCastleProvider.PROVIDER_NAME), new SecureRandom());
        OpenPGPCertificate cert = new OpenPGPCertificate(publicKeyRing, impl);

        OpenPGPDetachedSignatureProcessor processor = new OpenPGPDetachedSignatureProcessor(impl);
        processor.addSignatures(new ByteArrayInputStream(signature));
        processor.addVerificationCertificate(cert);

        List<OpenPGPSignature.OpenPGPDocumentSignature> results =
                processor.process(new ByteArrayInputStream(data));

        if (results.isEmpty()) return false;
        try {
            return results.get(0).isValid();
        } catch (PGPSignatureException e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Key info
    // -------------------------------------------------------------------------

    /**
     * Metadata extracted from a single PGP public key.
     */
    @Getter
    public static class KeyInfo {
        private final long keyId;
        private final String keyIdHex;
        private final String fingerprint;
        private final int algorithm;
        private final String algorithmName;
        private final int bitStrength;
        private final Date creationDate;
        private final boolean masterKey;
        private final boolean encryptionKey;
        private final List<String> userIds;

        KeyInfo(long keyId, String fingerprint, int algorithm, String algorithmName,
                int bitStrength, Date creationDate, boolean masterKey,
                boolean encryptionKey, List<String> userIds
        ) {
            this.keyId = keyId;
            this.keyIdHex = String.format("%016X", keyId);
            this.fingerprint = fingerprint;
            this.algorithm = algorithm;
            this.algorithmName = algorithmName;
            this.bitStrength = bitStrength;
            this.creationDate = creationDate;
            this.masterKey = masterKey;
            this.encryptionKey = encryptionKey;
            this.userIds = userIds;
        }
    }

    /**
     * Returns metadata for every key in the given public key ring (master key first, then subkeys).
     *
     * @param publicKeyRing The public key ring to inspect
     * @return Ordered list of {@link KeyInfo} — one entry per key
     */
    public static List<KeyInfo> getKeyInfo(PGPPublicKeyRing publicKeyRing) {
        List<KeyInfo> infos = new ArrayList<>();
        Iterator<PGPPublicKey> it = publicKeyRing.getPublicKeys();

        while (it.hasNext()) infos.add(toKeyInfo(it.next()));

        return infos;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static KeyInfo toKeyInfo(PGPPublicKey key) {
        List<String> userIds = new ArrayList<>();
        Iterator<String> uidIt = key.getUserIDs();
        while (uidIt.hasNext()) userIds.add(uidIt.next());
        return new KeyInfo(
                key.getKeyID(),
                fingerprintHex(key.getFingerprint()),
                key.getAlgorithm(),
                algorithmName(key.getAlgorithm()),
                key.getBitStrength(),
                key.getCreationTime(),
                key.isMasterKey(),
                key.isEncryptionKey(),
                userIds
        );
    }

    private static String fingerprintHex(byte[] fingerprint) {
        StringBuilder sb = new StringBuilder();
        for (byte b : fingerprint) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    private static String algorithmName(int algorithm) {
        switch (algorithm) {
            case PGPPublicKey.RSA_GENERAL:   return "RSA";
            case PGPPublicKey.RSA_ENCRYPT:   return "RSA (encrypt-only)";
            case PGPPublicKey.RSA_SIGN:      return "RSA (sign-only)";
            case PGPPublicKey.DSA:           return "DSA";
            case PGPPublicKey.ECDH:          return "ECDH";
            case PGPPublicKey.ECDSA:         return "ECDSA";
            case PGPPublicKey.EDDSA:         return "EdDSA (legacy)";
            case PGPPublicKey.Ed25519:       return "Ed25519";
            case PGPPublicKey.X25519:        return "X25519";
            default:                         return "Unknown (" + algorithm + ")";
        }
    }

    private static PGPPublicKey findEncryptionKey(PGPPublicKeyRing keyRing) throws PGPException {
        Iterator<PGPPublicKey> it = keyRing.getPublicKeys();
        while (it.hasNext()) {
            PGPPublicKey key = it.next();
            if (key.isEncryptionKey()) return key;
        }
        throw new PGPException("No encryption key found in public key ring");
    }

    private static PGPPrivateKey findSecretKey(
            PGPSecretKeyRing secretKeyRing,
            KeyIdentifier keyID,
            char[] passphrase
    ) throws PGPException {
        PGPSecretKey secretKey = secretKeyRing.getSecretKey(keyID);
        if (secretKey == null) return null;
        return secretKey.extractPrivateKey(
                new JcePBESecretKeyDecryptorBuilder()
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .build(passphrase));
    }
}
