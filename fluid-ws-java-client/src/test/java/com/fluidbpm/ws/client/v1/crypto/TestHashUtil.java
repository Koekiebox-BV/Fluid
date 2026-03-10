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

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * Test case for {@link HashUtil}.
 */
public class TestHashUtil {

    /**
     * Test SHA-1 hashing.
     */
    @Test
    public void testSha1() throws NoSuchAlgorithmException {
        byte[] input = "Hello World".getBytes();
        String hash = HashUtil.sha1(input);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-1 hash length should be 40 characters", 40, hash.length());
        assertEquals("SHA-1 hash of 'Hello World'", "0A4D55A8D778E5022FAB701977C5D840BBC486D0", hash);
    }

    /**
     * Test SHA-1 hashing with empty string.
     */
    @Test
    public void testSha1EmptyString() throws NoSuchAlgorithmException {
        byte[] input = "".getBytes();
        String hash = HashUtil.sha1(input);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-1 hash length should be 40 characters", 40, hash.length());
        assertEquals("SHA-1 hash of empty string", "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", hash);
    }

    /**
     * Test SHA-256 hashing.
     */
    @Test
    public void testSha256() throws NoSuchAlgorithmException {
        byte[] input = "Hello World".getBytes();
        String hash = HashUtil.sha256(input);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-256 hash length should be 64 characters", 64, hash.length());
        assertEquals("SHA-256 hash of 'Hello World'", "A591A6D40BF420404A011733CFB7B190D62C65BF0BCDA32B57B277D9AD9F146E", hash);
    }

    /**
     * Test SHA-256 hashing with empty string.
     */
    @Test
    public void testSha256EmptyString() throws NoSuchAlgorithmException {
        byte[] input = "".getBytes();
        String hash = HashUtil.sha256(input);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-256 hash length should be 64 characters", 64, hash.length());
        assertEquals("SHA-256 hash of empty string", "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", hash);
    }

    /**
     * Test SHA-1 consistency - same input should produce same hash.
     */
    @Test
    public void testSha1Consistency() throws NoSuchAlgorithmException {
        String input = "Test Input 123";
        String hash1 = HashUtil.sha1(input.getBytes());
        String hash2 = HashUtil.sha1(input.getBytes());

        assertEquals("Same input should produce same SHA-1 hash", hash1, hash2);
    }

    /**
     * Test SHA-256 consistency - same input should produce same hash.
     */
    @Test
    public void testSha256Consistency() throws NoSuchAlgorithmException {
        String input = "Test Input 123";
        String hash1 = HashUtil.sha256(input.getBytes());
        String hash2 = HashUtil.sha256(input.getBytes());

        assertEquals("Same input should produce same SHA-256 hash", hash1, hash2);
    }

    /**
     * Test SHA-1 hashing with readable format (colon separators).
     */
    @Test
    public void testSha1Readable() throws NoSuchAlgorithmException {
        byte[] input = "Hello World".getBytes();
        String hash = HashUtil.sha1(input, true);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-1 readable hash of 'Hello World'", "0A:4D:55:A8:D7:78:E5:02:2F:AB:70:19:77:C5:D8:40:BB:C4:86:D0", hash);
    }

    /**
     * Test SHA-256 hashing with readable format (colon separators).
     */
    @Test
    public void testSha256Readable() throws NoSuchAlgorithmException {
        byte[] input = "Hello World".getBytes();
        String hash = HashUtil.sha256(input, true);

        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-256 readable hash of 'Hello World'", "A5:91:A6:D4:0B:F4:20:40:4A:01:17:33:CF:B7:B1:90:D6:2C:65:BF:0B:CD:A3:2B:57:B2:77:D9:AD:9F:14:6E", hash);
    }

    /**
     * Test that readable format contains colons.
     */
    @Test
    public void testReadableFormatContainsColons() throws NoSuchAlgorithmException {
        byte[] input = "Test".getBytes();
        String readableHash = HashUtil.sha1(input, true);
        String normalHash = HashUtil.sha1(input, false);

        assertNotNull("Readable hash should not be null", readableHash);
        assertNotNull("Normal hash should not be null", normalHash);
        assertTrue("Readable hash should contain colons", readableHash.contains(":"));
        assertFalse("Normal hash should not contain colons", normalHash.contains(":"));
    }
}
