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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing operations
 */
public class HashUtil {

    /**
     * Computes SHA-1 hash of the input byte array
     * @param input The byte array to hash
     * @return Hexadecimal string representation of the hash
     * @throws NoSuchAlgorithmException if SHA-1 algorithm is not available
     */
    public static String sha1(byte[] input) throws NoSuchAlgorithmException {
        return sha1(input, false);
    }

    /**
     * Computes SHA-1 hash of the input byte array
     * @param input The byte array to hash
     * @param readable If true, returns hash with colon separators (e.g., "AA:BB:CC")
     * @return Hexadecimal string representation of the hash
     * @throws NoSuchAlgorithmException if SHA-1 algorithm is not available
     */
    public static String sha1(byte[] input, boolean readable) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(input);
        return bytesToHex(hash, readable);
    }

    /**
     * Computes SHA-256 hash of the input byte array
     * @param input The byte array to hash
     * @return Hexadecimal string representation of the hash
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String sha256(byte[] input) throws NoSuchAlgorithmException {
        return sha256(input, false);
    }

    /**
     * Computes SHA-256 hash of the input byte array
     * @param input The byte array to hash
     * @param readable If true, returns hash with colon separators (e.g., "AA:BB:CC")
     * @return Hexadecimal string representation of the hash
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String sha256(byte[] input, boolean readable) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input);
        return bytesToHex(hash, readable);
    }

    /**
     * Converts byte array to hexadecimal string
     * @param bytes The byte array to convert
     * @param readable If true, adds colon separators between bytes (e.g., "AA:BB:CC")
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes, boolean readable) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (readable && i > 0) {
                hexString.append(':');
            }
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}
