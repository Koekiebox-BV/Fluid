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

package com.fluidbpm.ws.client.v1.crypto.symmetric;

import org.junit.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import static junit.framework.TestCase.*;

/**
 * Test case for {@link SymmetricCryptoUtil}.
 */
public class TestSymmetricCryptoUtil {

    // AES-192 key (24 bytes)
    private static final byte[] AES192_KEY = hexToBytes(
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
    // AES-256 key (32 bytes)
    private static final byte[] AES256_KEY = hexToBytes(
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
    // AES-512 key (64 bytes — double-AES-256)
    private static final byte[] AES512_KEY = hexToBytes(
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF" +
            "FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210");

    // 3DES 112-bit key (16 bytes)
    private static final byte[] TDES112_KEY = hexToBytes("0123456789ABCDEF0123456789ABCDEF");
    // 3DES 192-bit key (24 bytes)
    private static final byte[] TDES192_KEY = hexToBytes("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");

    private static final byte[] AES_IV    = hexToBytes("00000000000000000000000000000000");
    private static final byte[] TDES_IV   = hexToBytes("0000000000000000");
    private static final byte[] GCM_NONCE = hexToBytes("000000000000000000000000"); // 12 bytes

    private static final byte[] PLAINTEXT = "Hello, World! TR-31 Crypto Test.".getBytes();

    // -------------------------------------------------------------------------
    // Key size validation
    // -------------------------------------------------------------------------

    @Test
    public void testUnsupportedAESKeySizeThrows() {
        byte[] badKey = hexToBytes("0123456789ABCDEF0123456789ABCDEF"); // 16 bytes — not supported
        try {
            SymmetricCryptoUtil.encryptAES(badKey, PLAINTEXT);
            fail("Should throw IllegalArgumentException for unsupported key size");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception should mention key size", e.getMessage().contains("16"));
        } catch (GeneralSecurityException e) {
            fail("Should throw IllegalArgumentException, not GeneralSecurityException");
        }
    }

    @Test
    public void testUnsupportedTDESKeySizeThrows() {
        byte[] badKey = hexToBytes("0123456789ABCDEF"); // 8 bytes — not supported
        try {
            SymmetricCryptoUtil.encryptTDES(badKey, PLAINTEXT);
            fail("Should throw IllegalArgumentException for unsupported key size");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception should mention key size", e.getMessage().contains("8"));
        } catch (GeneralSecurityException e) {
            fail("Should throw IllegalArgumentException, not GeneralSecurityException");
        }
    }

    @Test
    public void testAesKeySizeEnum() {
        assertEquals(24, SymmetricCryptoUtil.AesKeySize.AES_192.getBytes());
        assertEquals(32, SymmetricCryptoUtil.AesKeySize.AES_256.getBytes());
        assertEquals(64, SymmetricCryptoUtil.AesKeySize.AES_512.getBytes());
        assertEquals(SymmetricCryptoUtil.AesKeySize.AES_192, SymmetricCryptoUtil.AesKeySize.fromKey(AES192_KEY));
        assertEquals(SymmetricCryptoUtil.AesKeySize.AES_256, SymmetricCryptoUtil.AesKeySize.fromKey(AES256_KEY));
        assertEquals(SymmetricCryptoUtil.AesKeySize.AES_512, SymmetricCryptoUtil.AesKeySize.fromKey(AES512_KEY));
    }

    @Test
    public void testTdesKeySizeEnum() {
        assertEquals(16, SymmetricCryptoUtil.TdesKeySize.TDES_112.getBytes());
        assertEquals(24, SymmetricCryptoUtil.TdesKeySize.TDES_192.getBytes());
        assertEquals(SymmetricCryptoUtil.TdesKeySize.TDES_112, SymmetricCryptoUtil.TdesKeySize.fromKey(TDES112_KEY));
        assertEquals(SymmetricCryptoUtil.TdesKeySize.TDES_192, SymmetricCryptoUtil.TdesKeySize.fromKey(TDES192_KEY));
    }

    // -------------------------------------------------------------------------
    // AES — ECB
    // -------------------------------------------------------------------------

    @Test
    public void testAES192EncryptDecryptECB() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAES(AES192_KEY, PLAINTEXT);
        assertNotNull("Encrypted data should not be null", encrypted);
        assertFalse("Encrypted data should differ from plaintext", Arrays.equals(encrypted, PLAINTEXT));

        byte[] decrypted = SymmetricCryptoUtil.decryptAES(AES192_KEY, encrypted);
        assertTrue("AES-192 ECB round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES256EncryptDecryptECB() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAES(AES256_KEY, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptAES(AES256_KEY, encrypted);
        assertTrue("AES-256 ECB round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES512EncryptDecryptECB() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAES(AES512_KEY, PLAINTEXT);
        assertNotNull("AES-512 encrypted data should not be null", encrypted);
        assertFalse("AES-512 encrypted data should differ from plaintext", Arrays.equals(encrypted, PLAINTEXT));

        byte[] decrypted = SymmetricCryptoUtil.decryptAES(AES512_KEY, encrypted);
        assertTrue("AES-512 ECB round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    // -------------------------------------------------------------------------
    // AES — CBC
    // -------------------------------------------------------------------------

    @Test
    public void testAES192EncryptDecryptCBC() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESCBC(AES192_KEY, AES_IV, PLAINTEXT);
        assertNotNull("AES-192 CBC encrypted data should not be null", encrypted);

        byte[] decrypted = SymmetricCryptoUtil.decryptAESCBC(AES192_KEY, AES_IV, encrypted);
        assertTrue("AES-192 CBC round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES256EncryptDecryptCBC() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESCBC(AES256_KEY, AES_IV, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptAESCBC(AES256_KEY, AES_IV, encrypted);
        assertTrue("AES-256 CBC round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES512EncryptDecryptCBC() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESCBC(AES512_KEY, AES_IV, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptAESCBC(AES512_KEY, AES_IV, encrypted);
        assertTrue("AES-512 CBC round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAESCBCDifferentFromECB() throws GeneralSecurityException {
        byte[] ecb = SymmetricCryptoUtil.encryptAES(AES256_KEY, PLAINTEXT);
        byte[] cbc = SymmetricCryptoUtil.encryptAESCBC(AES256_KEY, AES_IV, PLAINTEXT);
        assertFalse("ECB and CBC outputs should differ", Arrays.equals(ecb, cbc));
    }

    // -------------------------------------------------------------------------
    // AES — GCM
    // -------------------------------------------------------------------------

    @Test
    public void testAES192EncryptDecryptGCM() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESGCM(AES192_KEY, GCM_NONCE, PLAINTEXT);
        assertNotNull("AES-192 GCM encrypted data should not be null", encrypted);
        // GCM output = ciphertext + 16-byte auth tag
        assertEquals("GCM output should be plaintext length + 16-byte tag",
                PLAINTEXT.length + 16, encrypted.length);

        byte[] decrypted = SymmetricCryptoUtil.decryptAESGCM(AES192_KEY, GCM_NONCE, encrypted);
        assertTrue("AES-192 GCM round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES256EncryptDecryptGCM() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESGCM(AES256_KEY, GCM_NONCE, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptAESGCM(AES256_KEY, GCM_NONCE, encrypted);
        assertTrue("AES-256 GCM round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAES512EncryptDecryptGCM() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptAESGCM(AES512_KEY, GCM_NONCE, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptAESGCM(AES512_KEY, GCM_NONCE, encrypted);
        assertTrue("AES-512 GCM round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAESGCMWithAAD() throws GeneralSecurityException {
        byte[] aad = "transaction-id=42".getBytes();
        byte[] encrypted = SymmetricCryptoUtil.encryptAESGCM(AES256_KEY, GCM_NONCE, PLAINTEXT, aad);
        byte[] decrypted = SymmetricCryptoUtil.decryptAESGCM(AES256_KEY, GCM_NONCE, encrypted, aad);
        assertTrue("AES-256 GCM+AAD round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testAESGCMAuthTagFailsWithWrongAAD() throws GeneralSecurityException {
        byte[] aad = "transaction-id=42".getBytes();
        byte[] wrongAad = "transaction-id=99".getBytes();
        byte[] encrypted = SymmetricCryptoUtil.encryptAESGCM(AES256_KEY, GCM_NONCE, PLAINTEXT, aad);
        try {
            SymmetricCryptoUtil.decryptAESGCM(AES256_KEY, GCM_NONCE, encrypted, wrongAad);
            fail("Should throw on AAD mismatch (authentication tag check fails)");
        } catch (GeneralSecurityException e) {
            // Expected: AEADBadTagException
        }
    }

    @Test
    public void testGCMDifferentFromECBAndCBC() throws GeneralSecurityException {
        byte[] ecb = SymmetricCryptoUtil.encryptAES(AES256_KEY, PLAINTEXT);
        byte[] cbc = SymmetricCryptoUtil.encryptAESCBC(AES256_KEY, AES_IV, PLAINTEXT);
        byte[] gcm = SymmetricCryptoUtil.encryptAESGCM(AES256_KEY, GCM_NONCE, PLAINTEXT);
        assertFalse("GCM should differ from ECB", Arrays.equals(gcm, ecb));
        assertFalse("GCM should differ from CBC", Arrays.equals(gcm, cbc));
    }

    // -------------------------------------------------------------------------
    // 3DES — ECB and CBC
    // -------------------------------------------------------------------------

    @Test
    public void testTDES112EncryptDecryptECB() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptTDES(TDES112_KEY, PLAINTEXT);
        assertNotNull("3DES-112 ECB encrypted data should not be null", encrypted);

        byte[] decrypted = SymmetricCryptoUtil.decryptTDES(TDES112_KEY, encrypted);
        assertTrue("3DES-112 ECB round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testTDES192EncryptDecryptECB() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptTDES(TDES192_KEY, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptTDES(TDES192_KEY, encrypted);
        assertTrue("3DES-192 ECB round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testTDES112EncryptDecryptCBC() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptTDESCBC(TDES112_KEY, TDES_IV, PLAINTEXT);
        assertNotNull("3DES-112 CBC encrypted data should not be null", encrypted);

        byte[] decrypted = SymmetricCryptoUtil.decryptTDESCBC(TDES112_KEY, TDES_IV, encrypted);
        assertTrue("3DES-112 CBC round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    @Test
    public void testTDES192EncryptDecryptCBC() throws GeneralSecurityException {
        byte[] encrypted = SymmetricCryptoUtil.encryptTDESCBC(TDES192_KEY, TDES_IV, PLAINTEXT);
        byte[] decrypted = SymmetricCryptoUtil.decryptTDESCBC(TDES192_KEY, TDES_IV, encrypted);
        assertTrue("3DES-192 CBC round-trip should recover plaintext", Arrays.equals(PLAINTEXT, decrypted));
    }

    // -------------------------------------------------------------------------
    // Raw block operations
    // -------------------------------------------------------------------------

    @Test
    public void testAESBlockEncryptDecrypt() throws GeneralSecurityException {
        byte[] block = hexToBytes("00112233445566778899AABBCCDDEEFF");
        byte[] encrypted = SymmetricCryptoUtil.encryptAESBlock(AES256_KEY, block);
        assertEquals("AES block encryption should produce 16 bytes", 16, encrypted.length);

        byte[] decrypted = SymmetricCryptoUtil.decryptAESBlock(AES256_KEY, encrypted);
        assertTrue("AES block round-trip should recover original block", Arrays.equals(block, decrypted));
    }

    @Test
    public void testTDESBlockEncryptDecrypt() throws GeneralSecurityException {
        byte[] block = hexToBytes("0011223344556677");
        byte[] encrypted = SymmetricCryptoUtil.encryptTDESBlock(TDES112_KEY, block);
        assertEquals("3DES block encryption should produce 8 bytes", 8, encrypted.length);

        byte[] decrypted = SymmetricCryptoUtil.decryptTDESBlock(TDES112_KEY, encrypted);
        assertTrue("3DES block round-trip should recover original block", Arrays.equals(block, decrypted));
    }

    // -------------------------------------------------------------------------
    // Negative tests
    // -------------------------------------------------------------------------

    @Test
    public void testWrongKeyDecryptionFails() throws GeneralSecurityException {
        byte[] wrongKey = hexToBytes(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        byte[] encrypted = SymmetricCryptoUtil.encryptAES(AES256_KEY, PLAINTEXT);
        try {
            byte[] decrypted = SymmetricCryptoUtil.decryptAES(wrongKey, encrypted);
            assertFalse("Wrong key should not decrypt to original plaintext", Arrays.equals(PLAINTEXT, decrypted));
        } catch (GeneralSecurityException e) {
            // Expected: PKCS5 padding validation fails with wrong key
        }
    }

    private static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
