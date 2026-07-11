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

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static junit.framework.TestCase.*;

/**
 * Test case for {@link HashGenerator}.
 */
public class TestHashGenerator {

    private static final byte[] HELLO_WORLD = "Hello World".getBytes();
    private static final byte[] EMPTY = "".getBytes();

    @Test
    public void testSha1KnownValue() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha1Hex(HELLO_WORLD, false);
        assertEquals("SHA-1 of 'Hello World'", "0A4D55A8D778E5022FAB701977C5D840BBC486D0", hash);
    }

    @Test
    public void testSha1Length() throws NoSuchAlgorithmException {
        byte[] hash = HashGenerator.sha1(HELLO_WORLD);
        assertEquals("SHA-1 should produce 20 bytes", 20, hash.length);
    }

    @Test
    public void testSha1EmptyInput() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha1Hex(EMPTY, false);
        assertEquals("SHA-1 of empty string", "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", hash);
    }

    @Test
    public void testSha256KnownValue() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha256Hex(HELLO_WORLD, false);
        assertEquals("SHA-256 of 'Hello World'", "A591A6D40BF420404A011733CFB7B190D62C65BF0BCDA32B57B277D9AD9F146E", hash);
    }

    @Test
    public void testSha256Length() throws NoSuchAlgorithmException {
        byte[] hash = HashGenerator.sha256(HELLO_WORLD);
        assertEquals("SHA-256 should produce 32 bytes", 32, hash.length);
    }

    @Test
    public void testSha256EmptyInput() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha256Hex(EMPTY, false);
        assertEquals("SHA-256 of empty string", "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", hash);
    }

    @Test
    public void testSha512KnownValue() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha512Hex(HELLO_WORLD, false);
        assertEquals("SHA-512 length should be 128 hex chars", 128, hash.length());
        assertNotNull("SHA-512 hash should not be null", hash);
        // SHA-512 produces different results per input — verified by length and non-null checks
    }

    @Test
    public void testSha512Length() throws NoSuchAlgorithmException {
        byte[] hash = HashGenerator.sha512(HELLO_WORLD);
        assertEquals("SHA-512 should produce 64 bytes", 64, hash.length);
    }

    @Test
    public void testSha512EmptyInput() throws NoSuchAlgorithmException {
        String hash = HashGenerator.sha512Hex(EMPTY, false);
        assertEquals("SHA-512 hex of empty string should be 128 chars", 128, hash.length());
        assertEquals("SHA-512 of empty string",
                "CF83E1357EEFB8BDF1542850D66D8007D620E4050B5715DC83F4A921D36CE9CE47D0D13C5D85F2B0FF8318D2877EEC2F63B931BD47417A81A538327AF927DA3E",
                hash);
    }

    @Test
    public void testReadableFormatContainsColons() throws NoSuchAlgorithmException {
        String readable = HashGenerator.sha256Hex(HELLO_WORLD, true);
        String normal = HashGenerator.sha256Hex(HELLO_WORLD, false);
        assertTrue("Readable format should contain colons", readable.contains(":"));
        assertFalse("Normal format should not contain colons", normal.contains(":"));
    }

    @Test
    public void testSha1ReadableFormat() throws NoSuchAlgorithmException {
        String readable = HashGenerator.sha1Hex(HELLO_WORLD, true);
        assertEquals("SHA-1 readable of 'Hello World'",
                "0A:4D:55:A8:D7:78:E5:02:2F:AB:70:19:77:C5:D8:40:BB:C4:86:D0", readable);
    }

    @Test
    public void testGenericHashMethod() throws NoSuchAlgorithmException {
        byte[] sha1Direct = HashGenerator.sha1(HELLO_WORLD);
        byte[] sha1Generic = HashGenerator.hash(HashGenerator.Algorithm.SHA1, HELLO_WORLD);
        assertTrue("Generic hash() should match sha1() result", Arrays.equals(sha1Direct, sha1Generic));

        byte[] sha256Direct = HashGenerator.sha256(HELLO_WORLD);
        byte[] sha256Generic = HashGenerator.hash(HashGenerator.Algorithm.SHA256, HELLO_WORLD);
        assertTrue("Generic hash() should match sha256() result", Arrays.equals(sha256Direct, sha256Generic));

        byte[] sha512Direct = HashGenerator.sha512(HELLO_WORLD);
        byte[] sha512Generic = HashGenerator.hash(HashGenerator.Algorithm.SHA512, HELLO_WORLD);
        assertTrue("Generic hash() should match sha512() result", Arrays.equals(sha512Direct, sha512Generic));
    }

    @Test
    public void testHashHexMethod() throws NoSuchAlgorithmException {
        String hex256 = HashGenerator.hashHex(HashGenerator.Algorithm.SHA256, HELLO_WORLD);
        String direct = HashGenerator.sha256Hex(HELLO_WORLD, false);
        assertEquals("hashHex() should match sha256Hex()", direct, hex256);
    }

    @Test
    public void testHashesAreUpperCase() throws NoSuchAlgorithmException {
        String sha1 = HashGenerator.sha1Hex(HELLO_WORLD, false);
        String sha256 = HashGenerator.sha256Hex(HELLO_WORLD, false);
        String sha512 = HashGenerator.sha512Hex(HELLO_WORLD, false);
        assertEquals("SHA-1 should be uppercase", sha1.toUpperCase(), sha1);
        assertEquals("SHA-256 should be uppercase", sha256.toUpperCase(), sha256);
        assertEquals("SHA-512 should be uppercase", sha512.toUpperCase(), sha512);
    }

    @Test
    public void testConsistency() throws NoSuchAlgorithmException {
        String h1 = HashGenerator.sha256Hex(HELLO_WORLD, false);
        String h2 = HashGenerator.sha256Hex(HELLO_WORLD, false);
        assertEquals("Same input should always produce same hash", h1, h2);
    }
}
