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

import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Test case for {@link PGPUtil}.
 */
public class TestPGPUtil {
    private static final String USER_ID = "Test User <test@example.com>";
    private static final char[] PASSPHRASE = "test-passphrase-123".toCharArray();

    // --------- RSA key pair ---------

    @Test
    public void testGenerateRsaKeyPair() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        assertNotNull("RSA key pair result should not be null", result);
        assertNotNull("RSA secret key ring should not be null", result.getSecretKeyRing());
        assertNotNull("RSA public key ring should not be null", result.getPublicKeyRing());
    }

    @Test
    public void testRsaKeyRingHasTwoKeys() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        int count = 0;
        Iterator<PGPPublicKey> it = result.getPublicKeyRing().getPublicKeys();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals("RSA key ring should have master key + encryption subkey", 2, count);
    }

    @Test
    public void testRsaMasterKeyAlgorithm() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPPublicKey masterKey = result.getPublicKeyRing().getPublicKey();
        assertTrue("RSA master key should be a master key", masterKey.isMasterKey());
        assertEquals("RSA master key algorithm should be RSA_GENERAL",
                PGPPublicKey.RSA_GENERAL, masterKey.getAlgorithm());
    }

    @Test
    public void testRsaSubkeyIsEncryption() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        Iterator<PGPPublicKey> it = result.getPublicKeyRing().getPublicKeys();
        it.next(); // skip master key
        PGPPublicKey subKey = it.next();
        assertFalse("RSA subkey should not be a master key", subKey.isMasterKey());
        assertTrue("RSA subkey should be an encryption key", subKey.isEncryptionKey());
    }

    // --------- Ed25519 key pair ---------

    @Test
    public void testGenerateEd25519KeyPair() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        assertNotNull("Ed25519 key pair result should not be null", result);
        assertNotNull("Ed25519 secret key ring should not be null", result.getSecretKeyRing());
        assertNotNull("Ed25519 public key ring should not be null", result.getPublicKeyRing());
    }

    @Test
    public void testEd25519KeyRingHasTwoKeys() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        int count = 0;
        Iterator<PGPPublicKey> it = result.getPublicKeyRing().getPublicKeys();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals("Ed25519 key ring should have master key + encryption subkey", 2, count);
    }

    @Test
    public void testEd25519MasterKeyAlgorithm() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        PGPPublicKey masterKey = result.getPublicKeyRing().getPublicKey();
        assertTrue("Ed25519 master key should be a master key", masterKey.isMasterKey());
        assertEquals("Ed25519 master key algorithm should be Ed25519 (v6)",
                PGPPublicKey.Ed25519, masterKey.getAlgorithm());
    }

    @Test
    public void testEd25519SubkeyIsEncryption() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        Iterator<PGPPublicKey> it = result.getPublicKeyRing().getPublicKeys();
        it.next(); // skip master key
        PGPPublicKey subKey = it.next();
        assertFalse("Ed25519 subkey should not be a master key", subKey.isMasterKey());
        assertTrue("Ed25519 subkey (X25519) should be an encryption key", subKey.isEncryptionKey());
    }

    // --------- Public key export ---------

    @Test
    public void testExportRsaPublicKeyArmored() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        String armored = PGPUtil.exportPublicKey(result);
        assertNotNull("Armored RSA public key should not be null", armored);
        assertTrue("Armored RSA public key should have PGP begin header",
                armored.contains("BEGIN PGP PUBLIC KEY BLOCK"));
        assertTrue("Armored RSA public key should have PGP end footer",
                armored.contains("END PGP PUBLIC KEY BLOCK"));
    }

    @Test
    public void testExportRsaPublicKeyAsBytes() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] keyBytes = PGPUtil.exportPublicKeyBytes(result);
        assertNotNull("RSA public key bytes should not be null", keyBytes);
        assertTrue("RSA public key bytes should not be empty", keyBytes.length > 0);
    }

    @Test
    public void testExportEd25519PublicKeyArmored() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        String armored = PGPUtil.exportPublicKey(result);
        assertNotNull("Armored Ed25519 public key should not be null", armored);
        assertTrue("Armored Ed25519 public key should have PGP begin header",
                armored.contains("BEGIN PGP PUBLIC KEY BLOCK"));
        assertTrue("Armored Ed25519 public key should have PGP end footer",
                armored.contains("END PGP PUBLIC KEY BLOCK"));
    }

    @Test
    public void testExportEd25519PublicKeyAsBytes() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        byte[] keyBytes = PGPUtil.exportPublicKeyBytes(result);
        assertNotNull("Ed25519 public key bytes should not be null", keyBytes);
        assertTrue("Ed25519 public key bytes should not be empty", keyBytes.length > 0);
    }

    // --------- Uniqueness and consistency ---------

    @Test
    public void testTwoRsaKeyPairsAreDifferent() throws Exception {
        PGPUtil.PGPKeyPairResult result1 = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.PGPKeyPairResult result2 = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] pub1 = PGPUtil.exportPublicKeyBytes(result1);
        byte[] pub2 = PGPUtil.exportPublicKeyBytes(result2);
        assertFalse("Two separately generated RSA keys should be different", Arrays.equals(pub1, pub2));
    }

    @Test
    public void testTwoEd25519KeyPairsAreDifferent() throws Exception {
        PGPUtil.PGPKeyPairResult result1 = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        PGPUtil.PGPKeyPairResult result2 = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        byte[] pub1 = PGPUtil.exportPublicKeyBytes(result1);
        byte[] pub2 = PGPUtil.exportPublicKeyBytes(result2);
        assertFalse("Two separately generated Ed25519 keys should be different", Arrays.equals(pub1, pub2));
    }

    @Test
    public void testArmoredAndBytesExportAreConsistent() throws Exception {
        PGPUtil.PGPKeyPairResult result = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] rawBytes = PGPUtil.exportPublicKeyBytes(result);
        String armored = PGPUtil.exportPublicKey(result);
        // Both exports should be non-empty and represent the same key
        assertTrue("Raw bytes should not be empty", rawBytes.length > 0);
        assertTrue("Armored string should not be empty", armored.length() > 0);
        // Armored is always larger (it includes headers and base64 encoding)
        assertTrue("Armored export should be larger than raw bytes", armored.length() > rawBytes.length);
    }

    // --------- Enum ---------

    @Test
    public void testKeyTypeEnum() {
        assertEquals("Should have exactly 2 key types", 2, PGPUtil.KeyType.values().length);
        assertNotNull("RSA key type should exist", PGPUtil.KeyType.RSA);
        assertNotNull("Ed25519 key type should exist", PGPUtil.KeyType.Ed25519);
    }

    // --------- Encrypt / Decrypt ---------

    @Test
    public void testRsaEncryptDecryptRoundtrip() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] plaintext = "Hello, PGP World!".getBytes("UTF-8");

        byte[] encrypted = PGPUtil.encrypt(plaintext, keys.getPublicKeyRing());
        assertNotNull("Encrypted bytes should not be null", encrypted);
        assertTrue("Encrypted bytes should not be empty", encrypted.length > 0);
        assertFalse("Ciphertext should differ from plaintext", Arrays.equals(plaintext, encrypted));

        byte[] decrypted = PGPUtil.decrypt(encrypted, keys.getSecretKeyRing(), PASSPHRASE);
        assertNotNull("Decrypted bytes should not be null", decrypted);
        assertTrue("Decrypted content should match original plaintext", Arrays.equals(plaintext, decrypted));
    }

    @Test
    public void testEd25519EncryptDecryptRoundtrip() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        byte[] plaintext = "Ed25519 encryption test".getBytes("UTF-8");

        byte[] encrypted = PGPUtil.encrypt(plaintext, keys.getPublicKeyRing());
        byte[] decrypted = PGPUtil.decrypt(encrypted, keys.getSecretKeyRing(), PASSPHRASE);

        assertTrue("Ed25519 decrypt should return original plaintext", Arrays.equals(plaintext, decrypted));
    }

    @Test
    public void testEncryptWithBinaryData() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] plaintext = new byte[256];
        for (int i = 0; i < plaintext.length; i++) plaintext[i] = (byte) i;

        byte[] encrypted = PGPUtil.encrypt(plaintext, keys.getPublicKeyRing());
        byte[] decrypted = PGPUtil.decrypt(encrypted, keys.getSecretKeyRing(), PASSPHRASE);

        assertTrue("Binary data should survive encrypt/decrypt roundtrip", Arrays.equals(plaintext, decrypted));
    }

    @Test
    public void testEncryptProducesDifferentOutputEachTime() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] plaintext = "same plaintext".getBytes("UTF-8");

        byte[] enc1 = PGPUtil.encrypt(plaintext, keys.getPublicKeyRing());
        byte[] enc2 = PGPUtil.encrypt(plaintext, keys.getPublicKeyRing());

        assertFalse("Two encryptions of the same plaintext should produce different ciphertext",
                Arrays.equals(enc1, enc2));
    }

    // --------- Sign / Verify ---------

    @Test
    public void testRsaSignVerify() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] data = "Data to sign".getBytes("UTF-8");

        byte[] signature = PGPUtil.sign(data, keys.getOpenPGPKey(), PASSPHRASE);
        assertNotNull("Signature should not be null", signature);
        assertTrue("Signature should not be empty", signature.length > 0);

        boolean valid = PGPUtil.verify(data, signature, keys.getPublicKeyRing());
        assertTrue("RSA signature should verify successfully", valid);
    }

    @Test
    public void testEd25519SignVerify() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        byte[] data = "Ed25519 signed data".getBytes("UTF-8");

        byte[] signature = PGPUtil.sign(data, keys.getOpenPGPKey(), PASSPHRASE);
        boolean valid = PGPUtil.verify(data, signature, keys.getPublicKeyRing());
        assertTrue("Ed25519 signature should verify successfully", valid);
    }

    @Test
    public void testSignatureIsArmoredPgpBlock() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] signature = PGPUtil.sign("test".getBytes("UTF-8"), keys.getOpenPGPKey(), PASSPHRASE);
        String armoredSig = new String(signature, "UTF-8");
        assertTrue("Signature should contain PGP begin header", armoredSig.contains("BEGIN PGP SIGNATURE"));
        assertTrue("Signature should contain PGP end footer", armoredSig.contains("END PGP SIGNATURE"));
    }

    @Test
    public void testTamperedDataFailsVerification() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        byte[] original = "Original data".getBytes("UTF-8");
        byte[] signature = PGPUtil.sign(original, keys.getOpenPGPKey(), PASSPHRASE);

        byte[] tampered = "Tampered data".getBytes("UTF-8");
        boolean valid = PGPUtil.verify(tampered, signature, keys.getPublicKeyRing());
        assertFalse("Signature over tampered data should not verify", valid);
    }

    @Test
    public void testVerifyWithWrongKeyReturnsFalse() throws Exception {
        PGPUtil.PGPKeyPairResult signer = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.PGPKeyPairResult other = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, "Other <other@example.com>", PASSPHRASE);

        byte[] data = "Signed by signer".getBytes("UTF-8");
        byte[] signature = PGPUtil.sign(data, signer.getOpenPGPKey(), PASSPHRASE);

        // Verifying with a different key ring — the key ID won't match, so verify returns false
        boolean valid = PGPUtil.verify(data, signature, other.getPublicKeyRing());
        assertFalse("Signature should not verify with a different public key", valid);
    }

    // --------- Key info ---------

    @Test
    public void testRsaKeyInfoReturnsTwoEntries() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        List<PGPUtil.KeyInfo> infos = PGPUtil.getKeyInfo(keys.getPublicKeyRing());
        assertEquals("RSA key ring should yield 2 KeyInfo entries", 2, infos.size());
    }

    @Test
    public void testEd25519KeyInfoReturnsTwoEntries() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        List<PGPUtil.KeyInfo> infos = PGPUtil.getKeyInfo(keys.getPublicKeyRing());
        assertEquals("Ed25519 key ring should yield 2 KeyInfo entries", 2, infos.size());
    }

    @Test
    public void testRsaMasterKeyInfo() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo master = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(0);

        assertTrue("Master key flag should be true", master.isMasterKey());
        assertTrue("RSA_GENERAL master key is capable of encryption", master.isEncryptionKey());
        assertEquals("RSA master key algorithm name", "RSA", master.getAlgorithmName());
        assertEquals("RSA master key algorithm tag", PGPPublicKey.RSA_GENERAL, master.getAlgorithm());
        assertEquals("RSA master key bit strength", 4096, master.getBitStrength());
        assertNotNull("Master key creation date should not be null", master.getCreationDate());
        assertFalse("Master key user IDs should not be empty", master.getUserIds().isEmpty());
        assertTrue("Master key user ID should contain the specified userId",
                master.getUserIds().get(0).contains("Test User"));
    }

    @Test
    public void testRsaSubkeyInfo() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo subkey = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(1);

        assertFalse("Subkey master flag should be false", subkey.isMasterKey());
        assertTrue("RSA subkey should be flagged as encryption key", subkey.isEncryptionKey());
        assertEquals("RSA subkey algorithm name", "RSA", subkey.getAlgorithmName());
        assertEquals("RSA subkey bit strength", 4096, subkey.getBitStrength());
    }

    @Test
    public void testEd25519MasterKeyInfo() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo master = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(0);

        assertTrue("Ed25519 master key flag should be true", master.isMasterKey());
        assertEquals("Ed25519 master key algorithm name", "Ed25519", master.getAlgorithmName());
        assertEquals("Ed25519 master key algorithm tag", PGPPublicKey.Ed25519, master.getAlgorithm());
    }

    @Test
    public void testEd25519SubkeyInfo() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.Ed25519, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo subkey = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(1);

        assertFalse("Ed25519 subkey master flag should be false", subkey.isMasterKey());
        assertTrue("X25519 subkey should be flagged as encryption key", subkey.isEncryptionKey());
        assertEquals("X25519 subkey algorithm name", "X25519", subkey.getAlgorithmName());
    }

    @Test
    public void testKeyInfoFingerprintFormat() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo master = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(0);

        String fp = master.getFingerprint();
        assertNotNull("Fingerprint should not be null", fp);
        // v6 RSA fingerprint is SHA-256 = 32 bytes = 64 hex chars
        assertEquals("RSA fingerprint should be 64 hex characters", 64, fp.length());
        assertEquals("Fingerprint should be uppercase", fp.toUpperCase(), fp);
        assertTrue("Fingerprint should only contain hex characters", fp.matches("[0-9A-F]+"));
    }

    @Test
    public void testKeyInfoKeyIdHexFormat() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo master = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(0);

        String keyIdHex = master.getKeyIdHex();
        assertNotNull("Key ID hex should not be null", keyIdHex);
        assertEquals("Key ID hex should be 16 characters", 16, keyIdHex.length());
        assertEquals("Key ID hex should be uppercase", keyIdHex.toUpperCase(), keyIdHex);
    }

    @Test
    public void testKeyInfoKeyIdMatchesLastFingerprintBytes() throws Exception {
        PGPUtil.PGPKeyPairResult keys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, USER_ID, PASSPHRASE);
        PGPUtil.KeyInfo master = PGPUtil.getKeyInfo(keys.getPublicKeyRing()).get(0);

        // For v6 keys, the key ID is the FIRST 8 bytes (16 hex chars) of the 32-byte SHA-256 fingerprint
        String fp = master.getFingerprint();
        String expectedKeyId = fp.substring(0, 16);
        assertEquals("Key ID hex should match first 16 chars of fingerprint",
                expectedKeyId, master.getKeyIdHex());
    }

    // --------- Encrypt-then-Sign round-trip ---------

    @Test
    public void testEncryptThenSignRoundtrip() throws Exception {
        PGPUtil.PGPKeyPairResult aliceKeys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, "Alice <alice@example.com>", PASSPHRASE);
        PGPUtil.PGPKeyPairResult bobKeys = PGPUtil.generateKeyPair(PGPUtil.KeyType.RSA, "Bob <bob@example.com>", PASSPHRASE);

        byte[] message = "Secret message from Alice to Bob".getBytes("UTF-8");

        // Alice encrypts for Bob and signs
        byte[] encrypted = PGPUtil.encrypt(message, bobKeys.getPublicKeyRing());
        byte[] signature = PGPUtil.sign(encrypted, aliceKeys.getOpenPGPKey(), PASSPHRASE);

        // Bob verifies Alice's signature and decrypts
        assertTrue("Alice's signature over ciphertext should verify", PGPUtil.verify(encrypted, signature, aliceKeys.getPublicKeyRing()));
        byte[] decrypted = PGPUtil.decrypt(encrypted, bobKeys.getSecretKeyRing(), PASSPHRASE);
        assertTrue("Bob should recover Alice's original message", Arrays.equals(message, decrypted));
    }
}
