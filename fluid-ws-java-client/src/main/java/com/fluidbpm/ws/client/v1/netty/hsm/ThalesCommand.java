package com.fluidbpm.ws.client.v1.netty.hsm;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * Represents a Thales HSM command based on the international command set.
 * Thales HSM commands follow a specific format with a 2-character command code
 * followed by command-specific data.
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Getter
public class ThalesCommand {
    private final String commandCode;
    private final String commandData;
    private final String requestId;

    /**
     * Constructs a Thales command.
     *
     * @param commandCode The 2-character command code (e.g., "NC", "EC", "DC")
     * @param commandData The command-specific data
     */
    public ThalesCommand(String commandCode, String commandData) {
        this(commandCode, commandData, null);
    }

    /**
     * Constructs a Thales command with a request ID for tracking.
     *
     * @param commandCode The 2-character command code
     * @param commandData The command-specific data
     * @param requestId Optional request ID for tracking
     */
    public ThalesCommand(String commandCode, String commandData, String requestId) {
        if (commandCode == null || commandCode.length() != 2) {
            throw new IllegalArgumentException("Command code must be exactly 2 characters");
        }
        this.commandCode = commandCode.toUpperCase();
        this.commandData = commandData != null ? commandData : "";
        this.requestId = requestId;
    }

    /**
     * Gets the full command as a string (command code + data).
     *
     * @return The complete command string
     */
    public String getFullCommand() {
        return commandCode + commandData;
    }

    /**
     * Gets the command as bytes.
     *
     * @return The command as a byte array
     */
    public byte[] toBytes() {
        return getFullCommand().getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public String toString() {
        return "ThalesCommand{" +
                "commandCode='" + commandCode + '\'' +
                ", commandData='" + commandData + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    /**
     * Factory methods for common Thales commands
     */
    public static class Commands {

        /**
         * NO - Perform diagnostics (Echo test)
         * This command echoes back the data sent to the HSM.
         *
         * @param echoData The data to echo back
         * @return ThalesCommand for echo test
         */
        public static ThalesCommand echo(String echoData) {
            return new ThalesCommand("NO", echoData);
        }

        /**
         * A0 - Generate a ZMK, TMK, TPK or PVK
         *
         * @param keyType Key type (0=ZMK, 1=TMK, 2=TPK, 3=PVK)
         * @param keyScheme Key scheme (U=Single length, X=Double length, Y=Triple length)
         * @return ThalesCommand for key generation
         */
        public static ThalesCommand generateKey(String keyType, String keyScheme) {
            return new ThalesCommand("A0", keyType + keyScheme);
        }

        /**
         * NC - Perform diagnostics
         * No command data required - returns HSM status and LMK check value.
         *
         * @return ThalesCommand for diagnostics
         */
        public static ThalesCommand diagnostics() {
            return new ThalesCommand("NC", "");
        }

        /**
         * M0 - Generate a MAC (Message Authentication Code)
         *
         * @param keyType Key type
         * @param key The MAC key under LMK
         * @param messageLength Length of message
         * @param message The message to MAC
         * @return ThalesCommand for MAC generation
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
