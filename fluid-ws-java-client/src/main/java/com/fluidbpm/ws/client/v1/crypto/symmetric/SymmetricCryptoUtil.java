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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * Utility class for symmetric encryption and decryption using 3DES and AES.
 *
 * <h3>Supported Key Sizes</h3>
 * <pre>
 *   AES 192-bit  (24 bytes)
 *   AES 256-bit  (32 bytes)
 *   AES 512-bit  (64 bytes) — implemented as double-AES-256: E(K1, E(K2, plaintext))
 *                             where K1 = bytes[0..31], K2 = bytes[32..63]
 *
 *   3DES 112-bit + parity (16 bytes) — expanded to 24 bytes by repeating first 8 bytes
 *   3DES 192-bit + parity (24 bytes)
 * </pre>
 *
 * <h3>Supported Modes</h3>
 * <pre>
 *   ECB — Electronic Codebook      (no IV; not recommended for real data)
 *   CBC — Cipher Block Chaining    (IV required; secure for real data)
 *   GCM — Galois/Counter Mode      (IV/nonce required; authenticated encryption; AES only)
 * </pre>
 */
public class SymmetricCryptoUtil {

    private static final String TDES_ALGORITHM = "DESede";
    private static final String AES_ALGORITHM = "AES";

    /** GCM authentication tag length in bits (128 = 16 bytes). */
    public static final int GCM_TAG_LENGTH_BITS = 128;

    /** Recommended GCM nonce (IV) length in bytes. */
    public static final int GCM_IV_LENGTH_BYTES = 12;

    // -------------------------------------------------------------------------
    // Supported key size enumerations
    // -------------------------------------------------------------------------

    /**
     * Supported AES key sizes.
     */
    public enum AesKeySize {
        AES_192(24),
        AES_256(32),
        /** Double-AES-256: 64-byte key split into two 32-byte AES-256 keys. */
        AES_512(64);

        private final int bytes;

        AesKeySize(int bytes) {
            this.bytes = bytes;
        }

        public int getBytes() {
            return bytes;
        }

        public static AesKeySize fromKey(byte[] key) {
            for (AesKeySize s : values()) {
                if (s.bytes == key.length) return s;
            }
            throw new IllegalArgumentException(
                    "Unsupported AES key size: " + key.length + " bytes. Supported: 24, 32, 64.");
        }
    }

    /**
     * Supported 3DES key sizes (key material bytes, including parity bits).
     */
    public enum TdesKeySize {
        /** 112-bit effective strength; 16-byte key expanded to 24 bytes internally. */
        TDES_112(16),
        /** 192-bit effective strength; 24-byte key. */
        TDES_192(24);

        private final int bytes;

        TdesKeySize(int bytes) {
            this.bytes = bytes;
        }

        public int getBytes() {
            return bytes;
        }

        public static TdesKeySize fromKey(byte[] key) {
            for (TdesKeySize s : values()) {
                if (s.bytes == key.length) return s;
            }
            throw new IllegalArgumentException(
                    "Unsupported 3DES key size: " + key.length + " bytes. Supported: 16, 24.");
        }
    }

    // -------------------------------------------------------------------------
    // 3DES — ECB
    // -------------------------------------------------------------------------

    /**
     * Encrypts data using 3DES in ECB mode with PKCS5 padding.
     *
     * @param key       16-byte (112-bit) or 24-byte (192-bit) 3DES key
     * @param plaintext Data to encrypt
     * @return Encrypted bytes
     * @throws GeneralSecurityException     on encryption failure
     * @throws IllegalArgumentException     if key size is not supported
     */
    public static byte[] encryptTDES(byte[] key, byte[] plaintext) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plaintext);
    }

    /**
     * Decrypts data using 3DES in ECB mode with PKCS5 padding.
     *
     * @param key        16-byte (112-bit) or 24-byte (192-bit) 3DES key
     * @param ciphertext Data to decrypt
     * @return Decrypted bytes
     * @throws GeneralSecurityException on decryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptTDES(byte[] key, byte[] ciphertext) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(ciphertext);
    }

    // -------------------------------------------------------------------------
    // 3DES — CBC
    // -------------------------------------------------------------------------

    /**
     * Encrypts data using 3DES in CBC mode with PKCS5 padding.
     *
     * @param key       16-byte (112-bit) or 24-byte (192-bit) 3DES key
     * @param iv        8-byte initialisation vector
     * @param plaintext Data to encrypt
     * @return Encrypted bytes
     * @throws GeneralSecurityException on encryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] encryptTDESCBC(byte[] key, byte[] iv, byte[] plaintext) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(plaintext);
    }

    /**
     * Decrypts data using 3DES in CBC mode with PKCS5 padding.
     *
     * @param key        16-byte (112-bit) or 24-byte (192-bit) 3DES key
     * @param iv         8-byte initialisation vector
     * @param ciphertext Data to decrypt
     * @return Decrypted bytes
     * @throws GeneralSecurityException on decryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptTDESCBC(byte[] key, byte[] iv, byte[] ciphertext) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    // -------------------------------------------------------------------------
    // AES — ECB
    // -------------------------------------------------------------------------

    /**
     * Encrypts data using AES in ECB mode with PKCS5 padding.
     *
     * <p>For 192-bit (24-byte) and 256-bit (32-byte) keys, standard AES is used.
     * For 512-bit (64-byte) keys, double-AES-256 is applied:
     * {@code E(K1, E(K2, plaintext))} where {@code K1 = key[0..31]}, {@code K2 = key[32..63]}.
     *
     * @param key       24-, 32-, or 64-byte AES key
     * @param plaintext Data to encrypt
     * @return Encrypted bytes
     * @throws GeneralSecurityException on encryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] encryptAES(byte[] key, byte[] plaintext) throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            return doubleAES256Encrypt(key, plaintext, "AES/ECB/PKCS5Padding", null);
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM));
        return cipher.doFinal(plaintext);
    }

    /**
     * Decrypts data using AES in ECB mode with PKCS5 padding.
     *
     * <p>For 512-bit (64-byte) keys, double-AES-256 decryption is applied:
     * {@code D(K2, D(K1, ciphertext))} where {@code K1 = key[0..31]}, {@code K2 = key[32..63]}.
     *
     * @param key        24-, 32-, or 64-byte AES key
     * @param ciphertext Data to decrypt
     * @return Decrypted bytes
     * @throws GeneralSecurityException on decryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptAES(byte[] key, byte[] ciphertext) throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            return doubleAES256Decrypt(key, ciphertext, "AES/ECB/PKCS5Padding", null);
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM));
        return cipher.doFinal(ciphertext);
    }

    // -------------------------------------------------------------------------
    // AES — CBC
    // -------------------------------------------------------------------------

    /**
     * Encrypts data using AES in CBC mode with PKCS5 padding.
     *
     * <p>For 512-bit (64-byte) keys, double-AES-256 CBC is applied using the same IV for both passes.
     *
     * @param key       24-, 32-, or 64-byte AES key
     * @param iv        16-byte initialisation vector
     * @param plaintext Data to encrypt
     * @return Encrypted bytes
     * @throws GeneralSecurityException on encryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] encryptAESCBC(byte[] key, byte[] iv, byte[] plaintext) throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            return doubleAES256Encrypt(key, plaintext, "AES/CBC/PKCS5Padding", iv);
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new IvParameterSpec(iv));
        return cipher.doFinal(plaintext);
    }

    /**
     * Decrypts data using AES in CBC mode with PKCS5 padding.
     *
     * <p>For 512-bit (64-byte) keys, double-AES-256 CBC decryption is applied.
     *
     * @param key        24-, 32-, or 64-byte AES key
     * @param iv         16-byte initialisation vector
     * @param ciphertext Data to decrypt
     * @return Decrypted bytes
     * @throws GeneralSecurityException on decryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptAESCBC(byte[] key, byte[] iv, byte[] ciphertext) throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            return doubleAES256Decrypt(key, ciphertext, "AES/CBC/PKCS5Padding", iv);
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    // -------------------------------------------------------------------------
    // AES — GCM (authenticated encryption)
    // -------------------------------------------------------------------------

    /**
     * Encrypts data using AES in GCM mode (authenticated encryption, no padding).
     *
     * <p>Output is {@code ciphertext || authTag} — the 16-byte authentication tag is appended
     * to the ciphertext by the JCA provider. Pass the full output to {@link #decryptAESGCM}.
     *
     * <p>For 512-bit (64-byte) keys, double-AES-256 GCM is applied:
     * the inner pass uses K2, the outer pass uses K1, each with the supplied nonce.
     *
     * @param key     24-, 32-, or 64-byte AES key
     * @param nonce   12-byte GCM nonce (IV) — <strong>must be unique per encryption</strong>
     * @param plaintext Data to encrypt
     * @return {@code ciphertext || 16-byte authTag}
     * @throws GeneralSecurityException on encryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] encryptAESGCM(byte[] key, byte[] nonce, byte[] plaintext) throws GeneralSecurityException {
        return encryptAESGCM(key, nonce, plaintext, null);
    }

    /**
     * Encrypts data using AES-GCM with Additional Authenticated Data (AAD).
     *
     * <p>Output is {@code ciphertext || authTag}. The AAD is authenticated but not encrypted.
     *
     * @param key     24-, 32-, or 64-byte AES key
     * @param nonce   12-byte GCM nonce — <strong>must be unique per encryption</strong>
     * @param plaintext Data to encrypt
     * @param aad     Additional authenticated data (may be {@code null})
     * @return {@code ciphertext || 16-byte authTag}
     * @throws GeneralSecurityException on encryption failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] encryptAESGCM(byte[] key, byte[] nonce, byte[] plaintext, byte[] aad)
            throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            // Double-GCM: inner with K2, outer with K1 (AAD applied on outer pass only)
            byte[] k1 = splitKey512(key, 0);
            byte[] k2 = splitKey512(key, 1);
            byte[] inner = gcmEncrypt(k2, nonce, plaintext, null);
            return gcmEncrypt(k1, nonce, inner, aad);
        }
        return gcmEncrypt(key, nonce, plaintext, aad);
    }

    /**
     * Decrypts data using AES-GCM and verifies the authentication tag.
     *
     * <p>Input must be {@code ciphertext || authTag} as produced by {@link #encryptAESGCM}.
     * Throws {@link javax.crypto.AEADBadTagException} if authentication fails.
     *
     * @param key            24-, 32-, or 64-byte AES key
     * @param nonce          12-byte GCM nonce used during encryption
     * @param ciphertextWithTag {@code ciphertext || 16-byte authTag}
     * @return Decrypted plaintext
     * @throws GeneralSecurityException on decryption or authentication failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptAESGCM(byte[] key, byte[] nonce, byte[] ciphertextWithTag)
            throws GeneralSecurityException {
        return decryptAESGCM(key, nonce, ciphertextWithTag, null);
    }

    /**
     * Decrypts data using AES-GCM with Additional Authenticated Data (AAD).
     *
     * @param key            24-, 32-, or 64-byte AES key
     * @param nonce          12-byte GCM nonce used during encryption
     * @param ciphertextWithTag {@code ciphertext || 16-byte authTag}
     * @param aad            Additional authenticated data used during encryption (may be {@code null})
     * @return Decrypted plaintext
     * @throws GeneralSecurityException on decryption or authentication failure
     * @throws IllegalArgumentException if key size is not supported
     */
    public static byte[] decryptAESGCM(byte[] key, byte[] nonce, byte[] ciphertextWithTag, byte[] aad)
            throws GeneralSecurityException {
        AesKeySize keySize = AesKeySize.fromKey(key);
        if (keySize == AesKeySize.AES_512) {
            byte[] k1 = splitKey512(key, 0);
            byte[] k2 = splitKey512(key, 1);
            byte[] inner = gcmDecrypt(k1, nonce, ciphertextWithTag, aad);
            return gcmDecrypt(k2, nonce, inner, null);
        }
        return gcmDecrypt(key, nonce, ciphertextWithTag, aad);
    }

    // -------------------------------------------------------------------------
    // Raw block operations (no padding — for KCV and TR-31 internal use)
    // -------------------------------------------------------------------------

    /**
     * Encrypts a single 16-byte block using AES-ECB with no padding.
     * Supports 24- or 32-byte keys only (not AES-512).
     *
     * @param key   24- or 32-byte AES key
     * @param block Exactly 16-byte block
     * @return Encrypted 16-byte block
     * @throws GeneralSecurityException on encryption failure
     */
    public static byte[] encryptAESBlock(byte[] key, byte[] block) throws GeneralSecurityException {
        SecretKey secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(block);
    }

    /**
     * Decrypts a single 16-byte block using AES-ECB with no padding.
     * Supports 24- or 32-byte keys only (not AES-512).
     *
     * @param key   24- or 32-byte AES key
     * @param block Exactly 16-byte block
     * @return Decrypted 16-byte block
     * @throws GeneralSecurityException on decryption failure
     */
    public static byte[] decryptAESBlock(byte[] key, byte[] block) throws GeneralSecurityException {
        SecretKey secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(block);
    }

    /**
     * Encrypts a single 8-byte block using 3DES-ECB with no padding.
     * Used internally for KCV generation and TR-31 operations.
     *
     * @param key   16- or 24-byte 3DES key
     * @param block Exactly 8-byte block
     * @return Encrypted 8-byte block
     * @throws GeneralSecurityException on encryption failure
     */
    public static byte[] encryptTDESBlock(byte[] key, byte[] block) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(block);
    }

    /**
     * Decrypts a single 8-byte block using 3DES-ECB with no padding.
     *
     * @param key   16- or 24-byte 3DES key
     * @param block Exactly 8-byte block
     * @return Decrypted 8-byte block
     * @throws GeneralSecurityException on decryption failure
     */
    public static byte[] decryptTDESBlock(byte[] key, byte[] block) throws GeneralSecurityException {
        SecretKey secretKey = buildTDESKey(key);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(block);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a 3DES {@link SecretKey}.
     * A 16-byte (112-bit) key is expanded to 24 bytes by repeating the first 8 bytes (K1=K3).
     */
    private static SecretKey buildTDESKey(byte[] keyMaterial) {
        TdesKeySize.fromKey(keyMaterial); // validate
        if (keyMaterial.length == 16) {
            byte[] key24 = new byte[24];
            System.arraycopy(keyMaterial, 0, key24, 0, 16);
            System.arraycopy(keyMaterial, 0, key24, 16, 8);
            return new SecretKeySpec(key24, TDES_ALGORITHM);
        }
        return new SecretKeySpec(keyMaterial, TDES_ALGORITHM);
    }

    /** Extracts one of the two 32-byte halves from a 64-byte AES-512 key. */
    private static byte[] splitKey512(byte[] key64, int half) {
        byte[] half32 = new byte[32];
        System.arraycopy(key64, half * 32, half32, 0, 32);
        return half32;
    }

    /**
     * Double-AES-256 encryption.
     * Encrypt with K2 first, then encrypt result with K1.
     */
    private static byte[] doubleAES256Encrypt(byte[] key64, byte[] data, String transformation, byte[] iv)
            throws GeneralSecurityException {
        byte[] k1 = splitKey512(key64, 0);
        byte[] k2 = splitKey512(key64, 1);
        byte[] pass1 = aesCipher(Cipher.ENCRYPT_MODE, k2, transformation, iv, data);
        return aesCipher(Cipher.ENCRYPT_MODE, k1, transformation, iv, pass1);
    }

    /**
     * Double-AES-256 decryption.
     * Decrypt with K1 first, then decrypt result with K2.
     */
    private static byte[] doubleAES256Decrypt(byte[] key64, byte[] data, String transformation, byte[] iv)
            throws GeneralSecurityException {
        byte[] k1 = splitKey512(key64, 0);
        byte[] k2 = splitKey512(key64, 1);
        byte[] pass1 = aesCipher(Cipher.DECRYPT_MODE, k1, transformation, iv, data);
        return aesCipher(Cipher.DECRYPT_MODE, k2, transformation, iv, pass1);
    }

    private static byte[] aesCipher(int mode, byte[] key, String transformation, byte[] iv, byte[] data)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(transformation);
        SecretKey secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        if (iv != null) {
            cipher.init(mode, secretKey, new IvParameterSpec(iv));
        } else {
            cipher.init(mode, secretKey);
        }
        return cipher.doFinal(data);
    }

    private static byte[] gcmEncrypt(byte[] key, byte[] nonce, byte[] plaintext, byte[] aad)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM),
                new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        if (aad != null) cipher.updateAAD(aad);
        return cipher.doFinal(plaintext);
    }

    private static byte[] gcmDecrypt(byte[] key, byte[] nonce, byte[] ciphertextWithTag, byte[] aad)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM),
                new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        if (aad != null) cipher.updateAAD(aad);
        return cipher.doFinal(ciphertextWithTag);
    }
}
