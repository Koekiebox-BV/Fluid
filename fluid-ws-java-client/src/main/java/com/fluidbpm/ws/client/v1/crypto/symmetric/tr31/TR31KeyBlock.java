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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed or constructed TR-31 key block.
 *
 * <p>TR-31 is an ANSI standard for the secure export/import of symmetric keys between HSMs.
 * The block format is: {@code [Header][Encrypted Key][MAC]}
 *
 * <p>Header fields:
 * <pre>
 *   [0]   Version ID           (1 char)  e.g. 'B' or 'D'
 *   [1-4] Block Length         (4 chars) total length of the key block
 *   [5-6] Key Usage            (2 chars) e.g. "B0" = BDK
 *   [7]   Algorithm            (1 char)  e.g. 'A'=AES, 'T'=3DES
 *   [8]   Mode of Use          (1 char)  e.g. 'E'=Encrypt/Decrypt
 *   [9]   Key Version Number   (2 chars)
 *   [11]  Exportability        (1 char)  e.g. 'E'=Exportable
 *   [12-13] Number of Optionals (2 chars)
 *   [14-15] Reserved           (2 chars)
 * </pre>
 */
@Getter
@Setter
public class TR31KeyBlock {

    /**
     * TR-31 version identifier:
     * 'A': Key block protected using the Key Variant Binding Method
     * 'B': Key block protected using the Key Derivation Binding Method
     * 'C': Key block protected using the Key Variant Binding Method
     * 'D': Key block protected using the AES Key Derivation Binding Method
     * */
    @Getter
    @RequiredArgsConstructor
    public enum Version {
        A('A'),
        B('B'),
        C('C'),
        D('D');

        private final char code;

        public static Version fromCode(char code) {
            for (Version v : values()) {
                if (v.code == code) return v;
            }
            throw new IllegalArgumentException("Unknown TR-31 version: " + code);
        }
    }

    /**
     * Key usage codes (subset of common usages)
     */
    @Getter
    @RequiredArgsConstructor
    public enum KeyUsage {
        BDK("B0", "Base Derivation Key"),
        DATA_ENCRYPTION("D0", "Data Encryption"),
        PIN_ENCRYPTION("P0", "PIN Encryption"),
        MAC_GENERATION("M0", "MAC Generation"),
        KEY_ENCRYPTION_KEY("K0", "Key Encryption Key"),
        ZMK("K1", "Zone Master Key"),
        ZPK("P1", "Zone PIN Key"),
        CVK("C0", "Card Verification Key"),
        DUKPT_INITIAL("B1", "DUKPT Initial Key");

        private final String code;
        private final String description;

        public static KeyUsage fromCode(String code) {
            for (KeyUsage ku : values()) {
                if (ku.code.equals(code)) return ku;
            }
            return null;
        }
    }

    /** Key algorithm codes */
    @Getter
    @RequiredArgsConstructor
    public enum Algorithm {
        AES('A'),
        TDES('T'),
        DES('D'),
        RSA('R');

        private final char code;

        public static Algorithm fromCode(char code) {
            for (Algorithm a : values()) {
                if (a.code == code) return a;
            }
            throw new IllegalArgumentException("Unknown algorithm code: " + code);
        }
    }

    /** Mode of use codes */
    @Getter
    @RequiredArgsConstructor
    public enum ModeOfUse {
        ENCRYPT_DECRYPT('B'),
        MAC_GENERATE_VERIFY('C'),
        DECRYPT('D'),
        ENCRYPT('E'),
        MAC_GENERATE('G'),
        NO_RESTRICTION('N'),
        MAC_VERIFY('V'),
        DERIVE_KEY('X'),
        ANY('Y');

        private final char code;

        public static ModeOfUse fromCode(char code) {
            for (ModeOfUse m : values()) {
                if (m.code == code) return m;
            }
            return NO_RESTRICTION;
        }
    }

    /** Exportability codes */
    @Getter
    @RequiredArgsConstructor
    public enum Exportability {
        EXPORTABLE('E'),
        NON_EXPORTABLE('N'),
        SENSITIVE('S');

        private final char code;

        public static Exportability fromCode(char code) {
            for (Exportability e : values()) {
                if (e.code == code) return e;
            }
            return NON_EXPORTABLE;
        }
    }

    /** An optional header block (2-char ID + 2-char length + data) */
    public static class OptionalBlock {
        private final String id;
        private final String data;

        public OptionalBlock(String id, String data) {
            if (id == null || id.length() != 2) throw new IllegalArgumentException("Optional block ID must be 2 chars");
            this.id = id;
            this.data = data == null ? "" : data;
        }

        public String getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        /**
         * Encodes this optional block as: ID (2) + length in multiples of 8 (2 hex) + data + padding
         */
        public String encode() {
            // format: ID(2) + length(2 hex chars) + data; total must be multiple of 8
            String content = id + "00" + data; // placeholder length
            int rawLen = 4 + data.length(); // ID(2) + len(2) + data
            int padded = ((rawLen + 7) / 8) * 8;
            int paddingNeeded = padded - rawLen;
            String lenHex = String.format("%02X", padded);
            StringBuilder sb = new StringBuilder();
            sb.append(id).append(lenHex).append(data);
            for (int i = 0; i < paddingNeeded; i++) sb.append('0');
            return sb.toString();
        }
    }

    private Version version;
    private KeyUsage keyUsage;
    private String keyUsageRaw;
    private Algorithm algorithm;
    private ModeOfUse modeOfUse;
    private String keyVersionNumber;
    private Exportability exportability;
    private List<OptionalBlock> optionalBlocks;
    private byte[] encryptedKeyData;
    private byte[] mac;

    public TR31KeyBlock() {
        this.optionalBlocks = new ArrayList<>();
        this.keyVersionNumber = "00";
    }

    public void setKeyUsage(KeyUsage keyUsage) {
        this.keyUsage = keyUsage;
        this.keyUsageRaw = keyUsage.getCode();
    }
    public void setKeyUsageRaw(String raw) {
        this.keyUsageRaw = raw;
        this.keyUsage = KeyUsage.fromCode(raw);
    }
}
