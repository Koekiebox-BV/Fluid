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

import lombok.extern.java.Log;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import static junit.framework.TestCase.*;

/**
 * Test case for {@link TR31Util}.
 */
@Log
public class TestTR31Util {

    // AES-128 KBPK (Key Block Protection Key)
    private static final byte[] AES128_KBPK = hexToBytes("89ABCDEF0123456789ABCDEF01234567");
    // AES-256 KBPK
    private static final byte[] AES256_KBPK = hexToBytes("89ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567");
    // 3DES KBPK
    private static final byte[] TDES_KBPK = hexToBytes("0123456789ABCDEF0123456789ABCDEF");

    // A 128-bit AES key to wrap
    private static final byte[] AES_KEY_TO_WRAP = hexToBytes("0123456789ABCDEF0123456789ABCDEF");
    // A 128-bit 3DES key to wrap
    private static final byte[] TDES_KEY_TO_WRAP = hexToBytes("FEDCBA9876543210FEDCBA9876543210");

    @Test
    public void testWrapUnwrapAESKey() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                AES128_KBPK,
                AES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.PIN_ENCRYPTION,
                TR31KeyBlock.Algorithm.AES,
                TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
                TR31KeyBlock.Exportability.EXPORTABLE
        );
        assertNotNull("Wrapped block should not be null", block);
        assertNotNull("Encrypted key data should not be null", block.getEncryptedKeyData());
        assertNotNull("MAC should not be null", block.getMac());
        assertEquals("MAC should be 8 bytes", 8, block.getMac().length);

        byte[] recovered = TR31Util.unwrap(AES128_KBPK, block);
        assertTrue("Unwrapped key should match original", Arrays.equals(AES_KEY_TO_WRAP, recovered));
    }

    @Test
    public void testWrapUnwrapTDESKey() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                TDES_KBPK,
                TDES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.ZPK,
                TR31KeyBlock.Algorithm.TDES,
                TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
                TR31KeyBlock.Exportability.NON_EXPORTABLE
        );
        assertNotNull("Wrapped 3DES block should not be null", block);

        byte[] recovered = TR31Util.unwrap(TDES_KBPK, block);
        assertTrue("Unwrapped 3DES key should match original", Arrays.equals(TDES_KEY_TO_WRAP, recovered));
    }

    @Test
    public void testEncodeDecodeRoundTrip() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                AES128_KBPK,
                AES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.DATA_ENCRYPTION,
                TR31KeyBlock.Algorithm.AES,
                TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
                TR31KeyBlock.Exportability.SENSITIVE
        );

        String encoded = TR31Util.encode(block);
        assertNotNull("Encoded block should not be null", encoded);
        assertTrue("Encoded block should not be empty", encoded.length() > 0);

        // Parse back
        TR31KeyBlock parsed = TR31Util.parse(encoded);
        assertEquals("Version should survive encode/parse", block.getVersion(), parsed.getVersion());
        assertEquals("Key usage should survive encode/parse", block.getKeyUsageRaw(), parsed.getKeyUsageRaw());
        assertEquals("Algorithm should survive encode/parse", block.getAlgorithm(), parsed.getAlgorithm());
        assertEquals("Mode of use should survive encode/parse", block.getModeOfUse(), parsed.getModeOfUse());
        assertEquals("Exportability should survive encode/parse", block.getExportability(), parsed.getExportability());
    }

    @Test
    public void testEncodeParseUnwrapRoundTrip() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                AES256_KBPK,
                AES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.BDK,
                TR31KeyBlock.Algorithm.AES,
                TR31KeyBlock.ModeOfUse.DERIVE_KEY,
                TR31KeyBlock.Exportability.NON_EXPORTABLE
        );

        String encoded = TR31Util.encode(block);
        byte[] recovered = TR31Util.unwrap(AES256_KBPK, encoded);
        assertTrue("Full encode-parse-unwrap round-trip should recover key", Arrays.equals(AES_KEY_TO_WRAP, recovered));
    }

    @Test
    public void testMACVerificationFailsWithWrongKBPK() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                AES128_KBPK,
                AES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.PIN_ENCRYPTION,
                TR31KeyBlock.Algorithm.AES,
                TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
                TR31KeyBlock.Exportability.EXPORTABLE
        );

        byte[] wrongKbpk = hexToBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        try {
            TR31Util.unwrap(wrongKbpk, block);
            fail("Should throw SecurityException for wrong KBPK (MAC mismatch)");
        } catch (SecurityException e) {
            assertTrue("Exception should mention MAC verification", e.getMessage().contains("MAC"));
        }
    }

    @Test
    public void testBlockVersionIsSet() throws GeneralSecurityException {
        TR31KeyBlock block = TR31Util.wrap(
                AES128_KBPK,
                AES_KEY_TO_WRAP,
                TR31KeyBlock.KeyUsage.PIN_ENCRYPTION,
                TR31KeyBlock.Algorithm.AES,
                TR31KeyBlock.ModeOfUse.ENCRYPT_DECRYPT,
                TR31KeyBlock.Exportability.EXPORTABLE
        );
        assertNotNull("Version should be set", block.getVersion());
        assertEquals("AES KBPK should produce version D", TR31KeyBlock.Version.D, block.getVersion());
    }

    @Test
    public void testParseMinimalHeader() {
        // Construct a minimal parseable header string with known content
        // Format: V(1) + Len(4) + Usage(2) + Algo(1) + Mode(1) + KVN(2) + Export(1) + NumOpt(2) + Res(2) + encData + MAC
        // We'll just test that parse does not throw for a valid-format string
        String fakeEncAndMac = "00000000000000000000000000000000" + "0000000000000000"; // 32 hex enc + 16 hex mac
        int len = 16 + fakeEncAndMac.length();
        String header = "D" + String.format("%04d", len) + "P0" + "A" + "E" + "00" + "E" + "00" + "00";
        String blockStr = header + fakeEncAndMac;

        TR31KeyBlock parsed = TR31Util.parse(blockStr);
        assertEquals("Parsed version should be D", TR31KeyBlock.Version.D, parsed.getVersion());
        assertEquals("Parsed key usage should be P0", "P0", parsed.getKeyUsageRaw());
        assertEquals("Parsed algorithm should be AES", TR31KeyBlock.Algorithm.AES, parsed.getAlgorithm());
    }

    private static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
