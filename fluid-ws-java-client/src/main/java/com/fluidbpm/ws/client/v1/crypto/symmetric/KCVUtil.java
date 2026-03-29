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

import java.security.GeneralSecurityException;

/**
 * Utility class for generating Key Check Values (KCV).
 *
 * <p>KCV is computed by encrypting a block of zero bytes with the clear key:
 * <ul>
 *   <li>AES: encrypt 16 zero bytes, take first 3 bytes of result</li>
 *   <li>3DES: encrypt 8 zero bytes, take first 3 bytes of result</li>
 * </ul>
 */
public class KCVUtil {
    private static final int KCV_LENGTH = 3;

    /**
     * Generates a 3-byte KCV for an AES key by encrypting 16 zero bytes.
     *
     * @param aesKey 16, 24, or 32-byte AES key
     * @return 3-byte KCV
     * @throws GeneralSecurityException on encryption failure
     */
    public static byte[] generateAESKCV(byte[] aesKey) throws GeneralSecurityException {
        byte[] zeroBlock = new byte[16];
        byte[] encrypted = SymmetricCryptoUtil.encryptAESBlock(aesKey, zeroBlock);
        byte[] kcv = new byte[KCV_LENGTH];
        System.arraycopy(encrypted, 0, kcv, 0, KCV_LENGTH);
        return kcv;
    }

    /**
     * Generates a 3-byte KCV for an AES key as a hex string.
     *
     * @param aesKey 16, 24, or 32-byte AES key
     * @return 6-character uppercase hex string (e.g., "A1B2C3")
     * @throws GeneralSecurityException on encryption failure
     */
    public static String generateAESKCVHex(byte[] aesKey) throws GeneralSecurityException {
        return bytesToHex(generateAESKCV(aesKey));
    }

    /**
     * Generates a 3-byte KCV for a 3DES key by encrypting 8 zero bytes.
     *
     * @param tdesKey 16 or 24-byte 3DES key
     * @return 3-byte KCV
     * @throws GeneralSecurityException on encryption failure
     */
    public static byte[] generateTDESKCV(byte[] tdesKey) throws GeneralSecurityException {
        byte[] zeroBlock = new byte[8];
        byte[] encrypted = SymmetricCryptoUtil.encryptTDESBlock(tdesKey, zeroBlock);
        byte[] kcv = new byte[KCV_LENGTH];
        System.arraycopy(encrypted, 0, kcv, 0, KCV_LENGTH);
        return kcv;
    }

    /**
     * Generates a 3-byte KCV for a 3DES key as a hex string.
     *
     * @param tdesKey 16 or 24-byte 3DES key
     * @return 6-character uppercase hex string (e.g., "A1B2C3")
     * @throws GeneralSecurityException on encryption failure
     */
    public static String generateTDESKCVHex(byte[] tdesKey) throws GeneralSecurityException {
        return bytesToHex(generateTDESKCV(tdesKey));
    }

    /**
     * Converts an array of bytes into an uppercase hexadecimal string representation.
     * Each byte is represented by two hexadecimal characters.
     *
     * @param bytes the array of bytes to be converted
     * @return a string containing the hexadecimal representation of the input bytes
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
