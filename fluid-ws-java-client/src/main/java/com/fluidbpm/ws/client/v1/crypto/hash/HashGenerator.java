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

package com.fluidbpm.ws.client.v1.crypto.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash generator supporting SHA-1, SHA-256, and SHA-512.
 *
 * <p>Produces both raw byte[] and hex-string representations.
 */
public class HashGenerator {

    public enum Algorithm {
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final String jcaName;

        Algorithm(String jcaName) {
            this.jcaName = jcaName;
        }

        public String getJcaName() {
            return jcaName;
        }
    }

    /**
     * Computes a hash using the specified algorithm.
     *
     * @param algorithm The hash algorithm to use
     * @param input     Data to hash
     * @return Raw hash bytes
     * @throws NoSuchAlgorithmException if the algorithm is unavailable
     */
    public static byte[] hash(Algorithm algorithm, byte[] input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm.getJcaName()).digest(input);
    }

    /**
     * Computes a hash and returns it as an uppercase hex string.
     *
     * @param algorithm The hash algorithm to use
     * @param input     Data to hash
     * @return Uppercase hex string
     * @throws NoSuchAlgorithmException if the algorithm is unavailable
     */
    public static String hashHex(Algorithm algorithm, byte[] input) throws NoSuchAlgorithmException {
        return bytesToHex(hash(algorithm, input), false);
    }

    /**
     * Computes a hash and returns it as a readable hex string with colon separators.
     *
     * @param algorithm The hash algorithm to use
     * @param input     Data to hash
     * @return Uppercase hex string with ':' separators (e.g., "0A:4D:55")
     * @throws NoSuchAlgorithmException if the algorithm is unavailable
     */
    public static String hashHexReadable(Algorithm algorithm, byte[] input) throws NoSuchAlgorithmException {
        return bytesToHex(hash(algorithm, input), true);
    }

    /**
     * Computes SHA-1 hash.
     *
     * @param input Data to hash
     * @return Raw 20-byte hash
     * @throws NoSuchAlgorithmException if SHA-1 is unavailable
     */
    public static byte[] sha1(byte[] input) throws NoSuchAlgorithmException {
        return hash(Algorithm.SHA1, input);
    }

    /**
     * Computes SHA-1 hash as hex string.
     *
     * @param input    Data to hash
     * @param readable If true, adds colon separators between bytes
     * @return Uppercase hex string
     * @throws NoSuchAlgorithmException if SHA-1 is unavailable
     */
    public static String sha1Hex(byte[] input, boolean readable) throws NoSuchAlgorithmException {
        return bytesToHex(sha1(input), readable);
    }

    /**
     * Computes SHA-256 hash.
     *
     * @param input Data to hash
     * @return Raw 32-byte hash
     * @throws NoSuchAlgorithmException if SHA-256 is unavailable
     */
    public static byte[] sha256(byte[] input) throws NoSuchAlgorithmException {
        return hash(Algorithm.SHA256, input);
    }

    /**
     * Computes SHA-256 hash as hex string.
     *
     * @param input    Data to hash
     * @param readable If true, adds colon separators between bytes
     * @return Uppercase hex string
     * @throws NoSuchAlgorithmException if SHA-256 is unavailable
     */
    public static String sha256Hex(byte[] input, boolean readable) throws NoSuchAlgorithmException {
        return bytesToHex(sha256(input), readable);
    }

    /**
     * Computes SHA-512 hash.
     *
     * @param input Data to hash
     * @return Raw 64-byte hash
     * @throws NoSuchAlgorithmException if SHA-512 is unavailable
     */
    public static byte[] sha512(byte[] input) throws NoSuchAlgorithmException {
        return hash(Algorithm.SHA512, input);
    }

    /**
     * Computes SHA-512 hash as hex string.
     *
     * @param input    Data to hash
     * @param readable If true, adds colon separators between bytes
     * @return Uppercase hex string
     * @throws NoSuchAlgorithmException if SHA-512 is unavailable
     */
    public static String sha512Hex(byte[] input, boolean readable) throws NoSuchAlgorithmException {
        return bytesToHex(sha512(input), readable);
    }

    private static String bytesToHex(byte[] bytes, boolean readable) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (readable && i > 0) sb.append(':');
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
}
