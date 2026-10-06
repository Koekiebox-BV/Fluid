package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmCommand;
import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Represents a Thales HSM host command.
 *
 * Wire layout (payShield 10K Host Programmers Manual section 1.2):
 * {@code [message header][2-char command code][command data]}.
 * The message header is assigned by {@link ThalesHSMClient} just before the command
 * is written, and is echoed back by the HSM so the response can be correlated.
 *
 * Command data is held as raw bytes so that commands carrying binary fields
 * (certificates, DER public keys, signatures) are transmitted unmodified.
 * String data is encoded as ISO-8859-1, a 1:1 byte mapping.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesCommand implements HsmCommand {

    /** Charset used for all text/byte conversions; every byte value maps to exactly one char. */
    public static final Charset WIRE_CHARSET = StandardCharsets.ISO_8859_1;

    @Getter
    private final String commandCode;
    private final byte[] commandData;
    /** Correlation header; assigned by the client unless supplied up front. */
    private volatile String requestId;

    /**
     * Constructs a Thales command with text data.
     *
     * @param commandCode The 2-character command code (e.g., "NC", "A0", "EI")
     * @param commandData The command-specific data (ISO-8859-1 encoded)
     */
    public ThalesCommand(String commandCode, String commandData) {
        this(commandCode, commandData, null);
    }

    /**
     * Constructs a Thales command with binary data.
     *
     * @param commandCode The 2-character command code
     * @param commandData The command-specific data bytes (copied)
     */
    public ThalesCommand(String commandCode, byte[] commandData) {
        this(commandCode, commandData, null);
    }

    /**
     * Constructs a Thales command with a caller-chosen message header.
     *
     * @param commandCode The 2-character command code
     * @param commandData The command-specific data
     * @param requestId Message header to use for correlation; must match the client's
     *                  configured header length. {@code null} lets the client assign one.
     */
    public ThalesCommand(String commandCode, String commandData, String requestId) {
        this(commandCode, commandData == null ? new byte[0] : commandData.getBytes(WIRE_CHARSET), requestId);
    }

    /**
     * Constructs a Thales command with binary data and a caller-chosen message header.
     *
     * @param commandCode The 2-character command code
     * @param commandData The command-specific data bytes (copied)
     * @param requestId Message header to use for correlation; {@code null} lets the client assign one
     */
    public ThalesCommand(String commandCode, byte[] commandData, String requestId) {
        if (commandCode == null || commandCode.length() != 2) {
            throw new IllegalArgumentException("Command code must be exactly 2 characters");
        }
        this.commandCode = commandCode.toUpperCase();
        this.commandData = commandData == null ? new byte[0] : Arrays.copyOf(commandData, commandData.length);
        this.requestId = requestId;
    }

    /**
     * Gets the command data as text (ISO-8859-1 decoded).
     *
     * @return The command data
     */
    public String getCommandData() {
        return new String(commandData, WIRE_CHARSET);
    }

    /**
     * Gets a copy of the raw command data bytes.
     *
     * @return The command data bytes
     */
    public byte[] getCommandDataBytes() {
        return Arrays.copyOf(commandData, commandData.length);
    }

    /**
     * Gets the message header used to correlate this command with its response.
     *
     * @return The header, or {@code null} if not yet assigned
     */
    @Override
    public String getRequestId() {
        return requestId;
    }

    /**
     * Assigns the correlation header. Called by {@link ThalesHSMClient} before sending.
     *
     * @param requestId The message header
     */
    void assignRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the command code and data as a string (no header).
     *
     * @return The command string
     */
    public String getFullCommand() {
        return commandCode + getCommandData();
    }

    /**
     * Gets the command code and data as bytes (no header).
     *
     * @return The command bytes
     */
    public byte[] toBytes() {
        return toBytes("");
    }

    /**
     * Gets {@code header + command code + data} as bytes.
     *
     * @param header The message header to prepend (may be empty)
     * @return The bytes to transmit
     */
    public byte[] toBytes(String header) {
        byte[] headerBytes = header == null ? new byte[0] : header.getBytes(WIRE_CHARSET);
        byte[] codeBytes = commandCode.getBytes(WIRE_CHARSET);
        byte[] result = new byte[headerBytes.length + codeBytes.length + commandData.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(codeBytes, 0, result, headerBytes.length, codeBytes.length);
        System.arraycopy(commandData, 0, result, headerBytes.length + codeBytes.length, commandData.length);
        return result;
    }

    /**
     * Gets the bytes to transmit using the assigned header (empty if none).
     *
     * @return The bytes to transmit
     */
    @Override
    public byte[] toWireBytes() {
        return toBytes(requestId == null ? "" : requestId);
    }

    @Override
    public String toString() {
        return "ThalesCommand{" +
                "commandCode='" + commandCode + '\'' +
                ", commandDataLength=" + commandData.length +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    /**
     * Factory methods for common Thales commands
     */
    public static class Commands {

        /**
         * NO - HSM status (also usable as a connectivity test).
         *
         * @param echoData The command data (mode; "00" for status)
         * @return ThalesCommand
         */
        public static ThalesCommand echo(String echoData) {
            return new ThalesCommand("NO", echoData);
        }

        /**
         * B2 - Echo. Returns the supplied data unchanged.
         *
         * @param data The data to echo (max 65535 bytes)
         * @return ThalesCommand for echo
         */
        public static ThalesCommand echoB2(String data) {
            byte[] bytes = data == null ? new byte[0] : data.getBytes(WIRE_CHARSET);
            return new ThalesCommand("B2", String.format("%04X", bytes.length) + new String(bytes, WIRE_CHARSET));
        }

        /**
         * A0 - Generate a key.
         *
         * @param keyType Key type
         * @param keyScheme Key scheme
         * @return ThalesCommand for key generation
         */
        public static ThalesCommand generateKey(String keyType, String keyScheme) {
            return new ThalesCommand("A0", keyType + keyScheme);
        }

        /**
         * NC - Perform diagnostics.
         *
         * @return ThalesCommand for diagnostics
         */
        public static ThalesCommand diagnostics() {
            return new ThalesCommand("NC", "");
        }

        /**
         * M0 - Encrypt data block.
         *
         * @param keyType Key type
         * @param key The key under LMK
         * @param messageLength Length of message
         * @param message The message
         * @return ThalesCommand
         */
        public static ThalesCommand generateMAC(String keyType, String key, String messageLength, String message) {
            return new ThalesCommand("M0", keyType + key + messageLength + message);
        }

        /**
         * EC - Verify a terminal PIN using the comparison method
         *
         * @param tpk Terminal PIN Key
         * @param pvk PIN Verification Key
         * @param maxPinLength Maximum PIN length
         * @param pinBlock Encrypted PIN block
         * @param pinBlockFormat PIN block format
         * @param accountNumber Account number
         * @return ThalesCommand for PIN verification
         */
        public static ThalesCommand verifyPIN(String tpk, String pvk, String maxPinLength,
                                               String pinBlock, String pinBlockFormat, String accountNumber) {
            return new ThalesCommand("EC", tpk + pvk + maxPinLength + pinBlock + pinBlockFormat + accountNumber);
        }

        /**
         * BA - Translate a PIN from TPK to ZPK
         *
         * @param sourceTPK Source Terminal PIN Key
         * @param destZPK Destination Zone PIN Key
         * @param maxPinLength Maximum PIN length
         * @param sourcePinBlock Source PIN block
         * @param sourcePinBlockFormat Source PIN block format
         * @param destPinBlockFormat Destination PIN block format
         * @param accountNumber Account number
         * @return ThalesCommand for PIN translation
         */
        public static ThalesCommand translatePIN(String sourceTPK, String destZPK, String maxPinLength,
                                                  String sourcePinBlock, String sourcePinBlockFormat,
                                                  String destPinBlockFormat, String accountNumber) {
            return new ThalesCommand("BA", sourceTPK + destZPK + maxPinLength + sourcePinBlock +
                                     sourcePinBlockFormat + destPinBlockFormat + accountNumber);
        }
    }
}
