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

package com.fluidbpm.ws.client.v1.crypto.symmetric.tr31;

import com.fluidbpm.ws.client.v1.crypto.symmetric.SymmetricCryptoUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * TR-31 key block generator and parser.
 *
 * <p>TR-31 (ANSI X9.143) defines a format for securely transporting symmetric keys between HSMs.
 * This implementation supports Version B (AES key-derivation binding) and Version D.
 *
 * <p>Key Block Format:
 * <pre>
 *   Header (16 chars minimum) | Optional Blocks | Encrypted Key Material | MAC (8 bytes = 16 hex chars)
 * </pre>
 *
 * <p>Usage example — wrap a key:
 * <pre>
 *   byte[] kbpk = hexToBytes("...");  // Key Block Protection Key
 *   byte[] key  = hexToBytes("...");  // Key to wrap
 *   TR31KeyBlock block = TR31Util.wrap(kbpk, key,
 *       TR31KeyBlock.KeyUsage.PIN_ENCRYPTION,
 *       TR31KeyBlock.Algorithm.TDES,
 *       TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
 *       TR31KeyBlock.Exportability.EXPORTABLE);
 *   String encoded = TR31Util.encode(block);
 * </pre>
 */
public class TR31Util {
    private static final int HEADER_LENGTH = 16;
    private static final int MAC_LENGTH_BYTES = 8;
    private static final int MAC_LENGTH_HEX = 16;

    // Key derivation usage constants (TR-31 Version B/D)
    private static final byte[] KD_USAGE_ENC = {0x00, 0x01};
    private static final byte[] KD_USAGE_MAC = {0x00, 0x02};
    private static final byte AES_KEY_TYPE = 0x00;
    private static final byte TDES_KEY_TYPE = 0x01;

    /**
     * Wraps (encrypts) a key into a TR-31 key block.
     *
     * @param kbpk         Key Block Protection Key (AES-128, AES-192, AES-256, or 3DES-128/192)
     * @param keyToWrap    The clear key to protect
     * @param keyUsage     Intended usage of the wrapped key
     * @param algorithm    Algorithm of the key being wrapped
     * @param modeOfUse    Mode of use for the wrapped key
     * @param exportability Exportability flag
     * @return Populated {@link TR31KeyBlock} with encrypted key and MAC
     * @throws GeneralSecurityException on crypto failure
     */
    public static TR31KeyBlock wrap(
            byte[] kbpk,
            byte[] keyToWrap,
            TR31KeyBlock.KeyUsage keyUsage,
            TR31KeyBlock.Algorithm algorithm,
            TR31KeyBlock.ModeOfUse modeOfUse,
            TR31KeyBlock.Exportability exportability
    ) throws GeneralSecurityException {
        boolean isAes = (kbpk.length == 16 || kbpk.length == 24 || kbpk.length == 32);
        TR31KeyBlock.Version version = isAes ? TR31KeyBlock.Version.D : TR31KeyBlock.Version.B;

        TR31KeyBlock block = new TR31KeyBlock();
        block.setVersion(version);
        block.setKeyUsage(keyUsage);
        block.setAlgorithm(algorithm);
        block.setModeOfUse(modeOfUse);
        block.setExportability(exportability);

        // Build header to compute lengths and derive keys
        String optionalBlocksStr = encodeOptionalBlocks(block.getOptionalBlocks());
        int payloadBlocks = computePayloadBlocks(keyToWrap.length, isAes);
        int totalLength = HEADER_LENGTH + optionalBlocksStr.length() + (payloadBlocks * (isAes ? 16 : 8) * 2) + MAC_LENGTH_HEX;
        String header = buildHeader(block, totalLength, optionalBlocksStr);

        // Derive encryption and MAC keys from KBPK
        byte[] kenc = deriveKey(kbpk, KD_USAGE_ENC, isAes);
        byte[] kmac = deriveKey(kbpk, KD_USAGE_MAC, isAes);

        // Pad and encrypt key material
        byte[] padded = padKeyMaterial(keyToWrap, isAes);
        byte[] encrypted = isAes
                ? aesEcbEncrypt(kenc, padded)
                : SymmetricCryptoUtil.encryptTDES(kenc, padded);
        block.setEncryptedKeyData(encrypted);

        // Compute MAC over header + encrypted key
        String macInput = header + bytesToHex(encrypted);
        byte[] mac = computeMAC(kmac, macInput.getBytes(StandardCharsets.US_ASCII), isAes);
        block.setMac(mac);

        return block;
    }

    /**
     * Parses (decrypts) a TR-31 key block string and recovers the clear key.
     *
     * @param kbpk        Key Block Protection Key
     * @param keyBlockStr The TR-31 key block string to parse
     * @return The clear key bytes
     * @throws GeneralSecurityException   on crypto failure
     * @throws IllegalArgumentException   if the key block format is invalid or MAC verification fails
     */
    public static byte[] unwrap(byte[] kbpk, String keyBlockStr) throws GeneralSecurityException {
        TR31KeyBlock block = parse(keyBlockStr);
        return unwrap(kbpk, block);
    }

    /**
     * Decrypts the key material from an already-parsed {@link TR31KeyBlock}.
     */
    public static byte[] unwrap(byte[] kbpk, TR31KeyBlock block) throws GeneralSecurityException {
        boolean isAes = kbpk.length == 16 || kbpk.length == 24 || kbpk.length == 32;

        byte[] kenc = deriveKey(kbpk, KD_USAGE_ENC, isAes);
        byte[] kmac = deriveKey(kbpk, KD_USAGE_MAC, isAes);

        // Re-derive and verify MAC
        String optStr = encodeOptionalBlocks(block.getOptionalBlocks());
        int totalLength = HEADER_LENGTH + optStr.length()
                + block.getEncryptedKeyData().length * 2
                + MAC_LENGTH_HEX;
        String header = buildHeader(block, totalLength, optStr);
        String macInput = header + bytesToHex(block.getEncryptedKeyData());
        byte[] expectedMac = computeMAC(kmac, macInput.getBytes(StandardCharsets.US_ASCII), isAes);

        if (!Arrays.equals(expectedMac, block.getMac())) {
            throw new SecurityException("TR-31 MAC verification failed");
        }

        // Decrypt
        byte[] decrypted = isAes
                ? aesEcbDecrypt(kenc, block.getEncryptedKeyData())
                : SymmetricCryptoUtil.decryptTDES(kenc, block.getEncryptedKeyData());

        // Strip the 2-byte length prefix and padding
        return stripPadding(decrypted);
    }

    /**
     * Encodes a {@link TR31KeyBlock} into its string representation.
     *
     * @param block The populated key block
     * @return TR-31 key block string
     */
    public static String encode(TR31KeyBlock block) {
        String optStr = encodeOptionalBlocks(block.getOptionalBlocks());
        int totalLength = HEADER_LENGTH + optStr.length()
                + block.getEncryptedKeyData().length * 2
                + MAC_LENGTH_HEX;
        String header = buildHeader(block, totalLength, optStr);
        return header + bytesToHex(block.getEncryptedKeyData()) + bytesToHex(block.getMac());
    }

    /**
     * Parses a TR-31 key block string into a {@link TR31KeyBlock}.
     *
     * @param keyBlockStr The TR-31 key block string
     * @return Parsed key block (encrypted key material not yet decrypted)
     */
    public static TR31KeyBlock parse(String keyBlockStr) {
        if (keyBlockStr == null || keyBlockStr.length() < HEADER_LENGTH + MAC_LENGTH_HEX) {
            throw new IllegalArgumentException("Key block too short");
        }

        TR31KeyBlock block = new TR31KeyBlock();
        int pos = 0;

        // Version ID (1 char)
        block.setVersion(TR31KeyBlock.Version.fromCode(keyBlockStr.charAt(pos++)));

        // Block Length (4 chars)
        int blockLength = Integer.parseInt(keyBlockStr.substring(pos, pos + 4));
        pos += 4;

        // Key Usage (2 chars)
        block.setKeyUsageRaw(keyBlockStr.substring(pos, pos + 2));
        pos += 2;

        // Algorithm (1 char)
        block.setAlgorithm(TR31KeyBlock.Algorithm.fromCode(keyBlockStr.charAt(pos++)));

        // Mode of Use (1 char)
        block.setModeOfUse(TR31KeyBlock.ModeOfUse.fromCode(keyBlockStr.charAt(pos++)));

        // Key Version Number (2 chars)
        block.setKeyVersionNumber(keyBlockStr.substring(pos, pos + 2));
        pos += 2;

        // Exportability (1 char)
        block.setExportability(TR31KeyBlock.Exportability.fromCode(keyBlockStr.charAt(pos++)));

        // Number of optional blocks (2 chars decimal)
        int numOptionals = Integer.parseInt(keyBlockStr.substring(pos, pos + 2));
        pos += 2;

        // Reserved (2 chars)
        pos += 2;

        // Optional blocks
        for (int i = 0; i < numOptionals; i++) {
            String optId = keyBlockStr.substring(pos, pos + 2);
            pos += 2;
            int optLen = Integer.parseInt(keyBlockStr.substring(pos, pos + 2), 16);
            pos += 2;
            int dataLen = optLen - 4;
            String optData = keyBlockStr.substring(pos, pos + dataLen);
            pos += dataLen;
            block.getOptionalBlocks().add(new TR31KeyBlock.OptionalBlock(optId, optData));
        }

        // Encrypted key material
        int macStartPos = blockLength - MAC_LENGTH_HEX;
        String encHex = keyBlockStr.substring(pos, macStartPos);
        block.setEncryptedKeyData(hexToBytes(encHex));

        // MAC (last 16 hex chars = 8 bytes)
        String macHex = keyBlockStr.substring(macStartPos, blockLength);
        block.setMac(hexToBytes(macHex));

        return block;
    }

    // --- Private helpers ---

    private static String buildHeader(TR31KeyBlock block, int totalLength, String optionalBlocksStr) {
        return String.valueOf(block.getVersion().getCode())
                + String.format("%04d", totalLength)
                + block.getKeyUsageRaw()
                + block.getAlgorithm().getCode()
                + block.getModeOfUse().getCode()
                + block.getKeyVersionNumber()
                + block.getExportability().getCode()
                + String.format("%02d", block.getOptionalBlocks().size())
                + "00"
                + optionalBlocksStr;
    }

    private static String encodeOptionalBlocks(List<TR31KeyBlock.OptionalBlock> blocks) {
        if (blocks == null || blocks.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (TR31KeyBlock.OptionalBlock ob : blocks) {
            sb.append(ob.encode());
        }
        return sb.toString();
    }

    /**
     * Derives a working key from KBPK using TR-31 key derivation.
     * Uses CMAC-based KDF: derived key = CMAC(KBPK, usage_indicator || counter || separator || key_length)
     */
    private static byte[] deriveKey(byte[] kbpk, byte[] usageIndicator, boolean isAes) throws GeneralSecurityException {
        // Build derivation data: usage(2) + 0x00(1) + key_type(1) + 0x0000(2) + key_len(2)
        int keyLen = kbpk.length * 8; // key length in bits
        byte[] derivationData = new byte[8];
        derivationData[0] = usageIndicator[0];
        derivationData[1] = usageIndicator[1];
        derivationData[2] = 0x00; // counter
        derivationData[3] = isAes ? AES_KEY_TYPE : TDES_KEY_TYPE;
        derivationData[4] = 0x00;
        derivationData[5] = 0x00;
        derivationData[6] = (byte) ((keyLen >> 8) & 0xFF);
        derivationData[7] = (byte) (keyLen & 0xFF);

        if (isAes) {
            // For AES: use AES-CMAC derivation
            byte[] padded = new byte[16];
            System.arraycopy(derivationData, 0, padded, 0, 8);
            return SymmetricCryptoUtil.encryptAESBlock(kbpk, padded);
        } else {
            // For 3DES: use 3DES encryption
            byte[] padded = new byte[8];
            System.arraycopy(derivationData, 0, padded, 0, 8);
            return SymmetricCryptoUtil.encryptTDESBlock(kbpk, padded);
        }
    }

    /**
     * Pads key material for encryption.
     * Format: 2-byte length (big-endian, in bits) + key bytes + random padding to block boundary.
     */
    private static byte[] padKeyMaterial(byte[] key, boolean isAes) {
        int blockSize = isAes ? 16 : 8;
        int contentLen = 2 + key.length;
        int paddedLen = ((contentLen + blockSize - 1) / blockSize) * blockSize;
        byte[] padded = new byte[paddedLen];
        int keyLenBits = key.length * 8;
        padded[0] = (byte) ((keyLenBits >> 8) & 0xFF);
        padded[1] = (byte) (keyLenBits & 0xFF);
        System.arraycopy(key, 0, padded, 2, key.length);
        // Remaining bytes stay as zero padding
        return padded;
    }

    /**
     * Strips the 2-byte length prefix and padding from decrypted key material.
     */
    private static byte[] stripPadding(byte[] padded) {
        int keyLenBits = ((padded[0] & 0xFF) << 8) | (padded[1] & 0xFF);
        int keyLen = keyLenBits / 8;
        byte[] key = new byte[keyLen];
        System.arraycopy(padded, 2, key, 0, keyLen);
        return key;
    }

    private static int computePayloadBlocks(int keyLen, boolean isAes) {
        int blockSize = isAes ? 16 : 8;
        int contentLen = 2 + keyLen;
        return (contentLen + blockSize - 1) / blockSize;
    }

    /**
     * Computes a truncated CBC-MAC over the given data.
     */
    private static byte[] computeMAC(byte[] macKey, byte[] data, boolean isAes) throws GeneralSecurityException {
        int blockSize = isAes ? 16 : 8;

        // Pad data to block boundary with 0x80 + zeroes (ISO/IEC 9797-1 Padding Method 2)
        int padLen = blockSize - (data.length % blockSize);
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        padded[data.length] = (byte) 0x80;

        // CBC-MAC
        byte[] prev = new byte[blockSize];
        for (int i = 0; i < padded.length; i += blockSize) {
            byte[] block = new byte[blockSize];
            System.arraycopy(padded, i, block, 0, blockSize);
            for (int j = 0; j < blockSize; j++) block[j] ^= prev[j];
            prev = isAes
                    ? SymmetricCryptoUtil.encryptAESBlock(macKey, block)
                    : SymmetricCryptoUtil.encryptTDESBlock(macKey, block);
        }

        // Return first 8 bytes as MAC
        return Arrays.copyOf(prev, MAC_LENGTH_BYTES);
    }

    /**
     * AES-ECB encryption (NoPadding) for TR-31 internal use.
     * The caller must ensure {@code data} is already aligned to a 16-byte boundary.
     * Accepts any standard AES key size (16, 24, or 32 bytes) without going through
     * {@link SymmetricCryptoUtil}'s supported-size validation.
     */
    private static byte[] aesEcbEncrypt(byte[] key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(data);
    }

    /**
     * AES-ECB decryption (NoPadding) for TR-31 internal use.
     */
    private static byte[] aesEcbDecrypt(byte[] key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(data);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Hex string must have even length");
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
