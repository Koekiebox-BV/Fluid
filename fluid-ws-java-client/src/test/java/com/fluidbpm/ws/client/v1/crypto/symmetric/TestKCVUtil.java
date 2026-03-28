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

import static junit.framework.TestCase.*;

/**
 * Test case for {@link KCVUtil}.
 */
public class TestKCVUtil {

    // AES-192 all-zeros key
    private static final byte[] AES192_ZEROS_KEY = new byte[24];
    // AES-256 all-zeros key (well-known KCV: DC95C0)
    private static final byte[] AES256_ZEROS_KEY = new byte[32];
    // 3DES all-zeros key (112-bit, 16 bytes)
    private static final byte[] TDES_ZEROS_KEY = new byte[16];

    @Test
    public void testAESKCVLength() throws GeneralSecurityException {
        byte[] kcv = KCVUtil.generateAESKCV(AES192_ZEROS_KEY);
        assertEquals("AES KCV should be 3 bytes", 3, kcv.length);
    }

    @Test
    public void testAES256KCVKnownValue() throws GeneralSecurityException {
        // AES-256 encrypt 16 zero bytes with all-zero key → DC95C078A2408989AD48A21492842087
        // KCV = first 3 bytes = DC95C0
        String kcvHex = KCVUtil.generateAESKCVHex(AES256_ZEROS_KEY);
        assertEquals("AES-256 KCV of all-zeros key", "DC95C0", kcvHex);
    }

    @Test
    public void testAESKCVHexLength() throws GeneralSecurityException {
        byte[] key = hexToBytes("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
        String kcvHex = KCVUtil.generateAESKCVHex(key);
        assertNotNull("KCV hex should not be null", kcvHex);
        assertEquals("KCV hex should be 6 characters", 6, kcvHex.length());
    }

    @Test
    public void testTDESKCVLength() throws GeneralSecurityException {
        byte[] kcv = KCVUtil.generateTDESKCV(TDES_ZEROS_KEY);
        assertEquals("3DES KCV should be 3 bytes", 3, kcv.length);
    }

    @Test
    public void testTDESKCVHexLength() throws GeneralSecurityException {
        byte[] key = hexToBytes("0123456789ABCDEF0123456789ABCDEF");
        String kcvHex = KCVUtil.generateTDESKCVHex(key);
        assertNotNull("3DES KCV hex should not be null", kcvHex);
        assertEquals("3DES KCV hex should be 6 characters", 6, kcvHex.length());
    }

    @Test
    public void testKCVConsistency() throws GeneralSecurityException {
        byte[] key = hexToBytes("89ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567");
        String kcv1 = KCVUtil.generateAESKCVHex(key);
        String kcv2 = KCVUtil.generateAESKCVHex(key);
        assertEquals("Same key should always produce same KCV", kcv1, kcv2);
    }

    @Test
    public void testDifferentKeysProduceDifferentKCV() throws GeneralSecurityException {
        byte[] key1 = hexToBytes("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
        byte[] key2 = hexToBytes("FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210");
        String kcv1 = KCVUtil.generateAESKCVHex(key1);
        String kcv2 = KCVUtil.generateAESKCVHex(key2);
        assertFalse("Different keys should produce different KCVs", kcv1.equals(kcv2));
    }

    @Test
    public void testKCVIsUpperCase() throws GeneralSecurityException {
        byte[] key = hexToBytes("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
        String kcvHex = KCVUtil.generateAESKCVHex(key);
        assertEquals("KCV hex should be uppercase", kcvHex.toUpperCase(), kcvHex);
    }

    private static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
