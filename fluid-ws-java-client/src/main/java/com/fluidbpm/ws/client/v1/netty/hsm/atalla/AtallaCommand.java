package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmCommand;
import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an Atalla AT1000 host command.
 *
 * Wire layout (AT1000 Command Reference Manual, "Command and response format"):
 * <pre>
 * &lt;CMDID#FIELD 1#FIELD 2#...#FIELD N#[^Context Tag#]&gt;
 * </pre>
 * <ul>
 *   <li>{@code <} starts the command, {@code >} ends it.</li>
 *   <li>{@code CMDID} is the 2, 3 or 4 character command identifier.</li>
 *   <li>Every field, including the last, is followed by a {@code #} delimiter.</li>
 *   <li>The optional context tag starts with {@code ^} and is returned unmodified in the
 *       response. {@link AtallaHSMClient} assigns one per in-flight command and uses it to
 *       correlate responses.</li>
 *   <li>The characters {@code < > # ^} and CR cannot appear in field data.</li>
 * </ul>
 *
 * The HSM is ASCII only; text is encoded as ISO-8859-1 (1:1 byte mapping).
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaCommand implements HsmCommand {

    /** Charset used for all text/byte conversions; every byte value maps to exactly one char. */
    public static final Charset WIRE_CHARSET = StandardCharsets.ISO_8859_1;

    /** Maximum command length accepted by the HSM, in characters. */
    public static final int MAX_COMMAND_LENGTH = 5000;

    public static final char START = '<';
    public static final char END = '>';
    public static final char DELIMITER = '#';
    public static final char CONTEXT_TAG_PREFIX = '^';
    public static final char CR = '\r';

    @Getter
    private final String commandId;
    private final List<String> fields;
    /** Correlation tag (without the leading {@code ^}); assigned by the client unless supplied up front. */
    private volatile String contextTag;

    /**
     * Constructs a command with no fields, e.g. {@code <1101#>}.
     *
     * @param commandId The 2-4 character command identifier (e.g. "00", "10", "1101")
     */
    public AtallaCommand(String commandId) {
        this(commandId, Collections.<String>emptyList(), null);
    }

    /**
     * Constructs a command with fields.
     *
     * @param commandId The 2-4 character command identifier
     * @param fields The command fields in order; {@code null} entries are sent as empty fields
     */
    public AtallaCommand(String commandId, String... fields) {
        this(commandId, fields == null ? Collections.<String>emptyList() : Arrays.asList(fields), null);
    }

    /**
     * Constructs a command with fields and a caller-chosen context tag.
     *
     * @param commandId The 2-4 character command identifier
     * @param fields The command fields in order; {@code null} entries are sent as empty fields
     * @param contextTag Context tag (without {@code ^}) used for correlation;
     *                   {@code null} lets the client assign one
     */
    public AtallaCommand(String commandId, List<String> fields, String contextTag) {
        if (commandId == null || commandId.length() < 2 || commandId.length() > 4) {
            throw new IllegalArgumentException("Command id must be 2 to 4 characters: '" + commandId + "'");
        }
        validateText("Command id", commandId);
        List<String> copy = new ArrayList<>(fields == null ? 0 : fields.size());
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                String f = fields.get(i) == null ? "" : fields.get(i);
                validateText("Field " + (i + 1), f);
                copy.add(f);
            }
        }
        if (contextTag != null) validateText("Context tag", contextTag);
        this.commandId = commandId;
        this.fields = Collections.unmodifiableList(copy);
        this.contextTag = contextTag;
    }

    /**
     * Rejects characters that have special meaning on the wire.
     */
    static void validateText(String what, String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == START || c == END || c == DELIMITER || c == CONTEXT_TAG_PREFIX || c == CR) {
                throw new IllegalArgumentException(what + " contains reserved character '"
                        + (c == CR ? "\\r" : String.valueOf(c)) + "' at index " + i);
            }
        }
    }

    /**
     * Gets the command fields (field 1 onwards; field 0 is the command id).
     *
     * @return Unmodifiable list of fields
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * Gets a field by its 1-based position as used in the manual.
     *
     * @param fieldNumber 1-based field number
     * @return The field value
     */
    public String getField(int fieldNumber) {
        return fields.get(fieldNumber - 1);
    }

    /**
     * Gets the context tag used to correlate this command with its response.
     *
     * @return The tag (without {@code ^}), or {@code null} if not yet assigned
     */
    public String getContextTag() {
        return contextTag;
    }

    /**
     * Alias of {@link #getContextTag()} for parity with the Thales client.
     *
     * @return The correlation id
     */
    @Override
    public String getRequestId() {
        return contextTag;
    }

    /**
     * Assigns the correlation tag. Called by {@link AtallaHSMClient} before sending.
     *
     * @param contextTag The context tag (without {@code ^})
     */
    void assignContextTag(String contextTag) {
        this.contextTag = contextTag;
    }

    /**
     * Builds the command text without a context tag, e.g. {@code <00#Hello#>}.
     *
     * @return The command string
     */
    public String getFullCommand() {
        return toWireString(null);
    }

    /**
     * Builds the command text with the given context tag.
     *
     * @param tag Context tag to append (without {@code ^}); {@code null} or empty omits the field
     * @return The command string
     */
    public String toWireString(String tag) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(START).append(commandId).append(DELIMITER);
        for (String f : fields) sb.append(f).append(DELIMITER);
        if (tag != null && !tag.isEmpty()) sb.append(CONTEXT_TAG_PREFIX).append(tag).append(DELIMITER);
        sb.append(END);
        return sb.toString();
    }

    /**
     * Gets the bytes to transmit using the assigned context tag (omitted if none).
     *
     * @return The bytes to transmit
     */
    @Override
    public byte[] toWireBytes() {
        String wire = toWireString(contextTag);
        if (wire.length() > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException("Atalla command too long: " + wire.length()
                    + " characters (max " + MAX_COMMAND_LENGTH + ")");
        }
        return wire.getBytes(WIRE_CHARSET);
    }

    @Override
    public String toString() {
        return "AtallaCommand{" +
                "commandId='" + commandId + '\'' +
                ", fieldCount=" + fields.size() +
                ", contextTag='" + contextTag + '\'' +
                '}';
    }

    /**
     * Factory methods for common Atalla commands.
     */
    public static class Commands {

        /** Maximum echo message length (command 00, field 1). */
        public static final int MAX_ECHO_LENGTH = 1999;

        /**
         * 00 - Echo Test Message. Tests the communications link between host and HSM;
         * the HSM returns {@code <00#00YYVV#message#>} where {@code VV} is the software version.
         *
         * @param message The test message, 1-1999 characters, not containing {@code # < >}
         * @return AtallaCommand for echo
         */
        public static AtallaCommand echo(String message) {
            if (message == null || message.isEmpty()) {
                throw new IllegalArgumentException("Echo message must contain at least 1 character");
            }
            if (message.length() > MAX_ECHO_LENGTH) {
                throw new IllegalArgumentException("Echo message exceeds " + MAX_ECHO_LENGTH + " characters");
            }
            return new AtallaCommand("00", message);
        }

        /**
         * 1101 - HSM Software Version. Returns the software image version, CRC and product code.
         *
         * @return AtallaCommand
         */
        public static AtallaCommand softwareVersion() {
            return new AtallaCommand("1101");
        }
    }
}
