package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a response from an Atalla AT1000 HSM.
 *
 * Wire layout (AT1000 Command Reference Manual, "Command and response format"):
 * <pre>
 * &lt;RESPID#FIELD 1#FIELD 2#...#FIELD N#[^Context Tag#]&gt;[CRLF]
 * </pre>
 * The response id is the command id with its first digit incremented (command {@code 10}
 * answers as {@code 20}, {@code 37B} as {@code 47B}). A response id of {@code 00} carries the
 * error format {@code <00#XXYYZZ#[detail#]>} where {@code XX} is the error number (Table 11-1),
 * {@code YY} the first field in error and {@code ZZ} the software version. Error number
 * {@code 00} is not an error: it is the reply to the echo command, {@code <00#00YYZZ#message#>}.
 *
 * The context tag, if one was sent, is returned unmodified and is used for correlation.
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Getter
public class AtallaResponse implements HsmResponse {
    /** Response id reported when the frame does not parse as {@code <...>}. */
    public static final String MALFORMED_RESPONSE_ID = "";
    /** Error number used when the frame is malformed. */
    public static final String MALFORMED_ERROR_CODE = "99";
    /** Error number the HSM uses for the reply to a test (echo) message. */
    public static final String ECHO_ERROR_CODE = "00";
    /** Response id shared by error responses and the echo reply. */
    public static final String ERROR_RESPONSE_ID = "00";

    private final String responseId;
    private final List<String> fields;
    private final String contextTag;
    private final boolean malformed;
    private final byte[] rawResponse;

    /**
     * Constructs a response from a de-framed {@code <...>} message (CRLF already stripped).
     *
     * @param rawResponse The raw response bytes
     */
    public AtallaResponse(byte[] rawResponse) {
        this.rawResponse = Arrays.copyOf(rawResponse, rawResponse.length);
        String text = new String(rawResponse, AtallaCommand.WIRE_CHARSET);

        int start = text.indexOf(AtallaCommand.START);
        int end = text.lastIndexOf(AtallaCommand.END);
        if (start < 0 || end < 0 || end <= start) {
            this.malformed = true;
            this.responseId = MALFORMED_RESPONSE_ID;
            this.fields = Collections.emptyList();
            this.contextTag = null;
            return;
        }

        String body = text.substring(start + 1, end);
        // "ID#f1#f2#" splits to [ID, f1, f2, ""]; the trailing empty element is the final delimiter.
        String[] parts = body.split(String.valueOf(AtallaCommand.DELIMITER), -1);
        List<String> list = new ArrayList<>(Arrays.asList(parts));
        boolean terminated = list.size() > 1 && list.get(list.size() - 1).isEmpty();
        if (terminated) list.remove(list.size() - 1);

        String tag = null;
        if (list.size() > 1) {
            String last = list.get(list.size() - 1);
            if (!last.isEmpty() && last.charAt(0) == AtallaCommand.CONTEXT_TAG_PREFIX) {
                tag = last.substring(1);
                list.remove(list.size() - 1);
            }
        }

        this.malformed = !terminated || list.isEmpty() || list.get(0).isEmpty();
        this.responseId = list.isEmpty() ? MALFORMED_RESPONSE_ID : list.remove(0);
        this.fields = Collections.unmodifiableList(list);
        this.contextTag = tag;
    }

    /**
     * Gets the correlation id (the echoed context tag).
     *
     * @return The context tag without {@code ^}, or {@code null} if none was returned
     */
    public String getRequestId() {
        return contextTag;
    }

    /**
     * Gets the number of data fields (excluding the response id and context tag).
     *
     * @return Field count
     */
    public int getFieldCount() {
        return fields.size();
    }

    /**
     * Gets a field by its 1-based position as numbered in the manual (field 0 is the response id).
     *
     * @param fieldNumber 1-based field number
     * @return The field value, or {@code null} if the response has fewer fields
     */
    public String getField(int fieldNumber) {
        int idx = fieldNumber - 1;
        return idx >= 0 && idx < fields.size() ? fields.get(idx) : null;
    }

    /**
     * @return {@code true} when the response id is {@code 00} (error format, also used by the echo reply)
     */
    public boolean isErrorFormat() {
        return ERROR_RESPONSE_ID.equals(responseId);
    }

    /**
     * Gets the error number ({@code XX} of {@code XXYYZZ}).
     *
     * @return The two-digit error number, {@link #MALFORMED_ERROR_CODE} if malformed,
     *         or {@code null} when the response is not in error format
     */
    public String getErrorCode() {
        if (malformed) return MALFORMED_ERROR_CODE;
        if (!isErrorFormat()) return null;
        String status = getField(1);
        return status == null || status.length() < 2 ? MALFORMED_ERROR_CODE : status.substring(0, 2);
    }

    /**
     * Gets the number of the first field found in error ({@code YY} of {@code XXYYZZ}).
     *
     * @return The two-digit field number, or {@code null} if not an error-format response
     */
    public String getErrorFieldNumber() {
        String status = isErrorFormat() ? getField(1) : null;
        return status == null || status.length() < 4 ? null : status.substring(2, 4);
    }

    /**
     * Gets the software version of the command processor ({@code ZZ} of {@code XXYYZZ}).
     *
     * @return The two-digit version, or {@code null} if not an error-format response
     */
    public String getSoftwareVersion() {
        String status = isErrorFormat() ? getField(1) : null;
        return status == null || status.length() < 6 ? null : status.substring(4, 6);
    }

    /**
     * Gets the detailed application error (Table 11-2) appended when HSM option 21 is enabled.
     *
     * @return The detail field, or {@code null} if absent
     */
    public String getErrorDetail() {
        return isError() ? getField(2) : null;
    }

    /**
     * Gets the message returned by the echo command ({@code <00#00YYZZ#message#>}).
     *
     * @return The echoed message, or {@code null} if this is not an echo reply
     */
    public String getEchoMessage() {
        return isErrorFormat() && ECHO_ERROR_CODE.equals(getErrorCode()) ? getField(2) : null;
    }

    /**
     * @return {@code true} when the HSM reported an error (response id {@code 00} with a non-zero
     *         error number) or the frame is malformed
     */
    public boolean isError() {
        if (malformed) return true;
        return isErrorFormat() && !ECHO_ERROR_CODE.equals(getErrorCode());
    }

    /**
     * Checks if the response indicates success.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return !isError();
    }

    /**
     * Gets a human-readable error message based on the error number.
     *
     * @return Error message description
     */
    public String getErrorMessage() {
        if (malformed) return "Malformed response";
        if (!isErrorFormat()) return "No error";
        return AtallaErrorCode.getMessage(getErrorCode());
    }

    /**
     * Gets a copy of the complete raw response.
     *
     * @return The raw response bytes
     */
    public byte[] getRawResponse() {
        return Arrays.copyOf(rawResponse, rawResponse.length);
    }

    /**
     * Gets the full response as a string.
     *
     * @return The complete response string
     */
    public String getFullResponse() {
        return new String(rawResponse, AtallaCommand.WIRE_CHARSET);
    }

    @Override
    public String toString() {
        return "AtallaResponse{" +
                "responseId='" + responseId + '\'' +
                ", fieldCount=" + fields.size() +
                ", contextTag='" + contextTag + '\'' +
                ", success=" + isSuccess() +
                ", errorCode='" + getErrorCode() + '\'' +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }

    /**
     * Atalla AT1000 error numbers (Command Reference Manual, Table 11-1 "Error types").
     */
    public static class AtallaErrorCode {
        public static final String TEST_MESSAGE_RESPONSE = "00";
        public static final String LENGTH_OUT_OF_RANGE = "01";
        public static final String INVALID_CHARACTER = "02";
        public static final String VALUE_OUT_OF_RANGE = "03";
        public static final String INVALID_NUMBER_OF_PARAMETERS = "04";
        public static final String PARITY_ERROR = "05";
        public static final String KEY_USAGE_ERROR = "06";
        public static final String EXECUTION_ERROR = "08";
        public static final String KEY_LENGTH_ERROR = "10";
        public static final String NON_EXISTENT_COMMAND = "22";
        public static final String INVALID_COMMAND = "23";
        public static final String HEADER_MISMATCH = "73";
        public static final String UNKNOWN_ERROR = "99";

        /**
         * Gets a human-readable message for an error number.
         *
         * @param errorCode The Atalla error number ({@code XX})
         * @return Error message description
         */
        public static String getMessage(String errorCode) {
            if (errorCode == null) return "Unknown error";

            switch (errorCode) {
                case "00": return "Response to test message";
                case "01": return "Length out of range";
                case "02": return "Invalid character";
                case "03": return "Value out of range";
                case "04": return "Invalid number of parameters";
                case "05": return "Parity error";
                case "06":
                case "07": return "Key usage error";
                case "08":
                case "09": return "Execution error";
                case "10": return "Key length error";
                case "11": return "Printing error";
                case "12": return "Marker string not found";
                case "20": return "Serial number set, cannot modify it";
                case "21": return "HSM not in a Security Association, or serial number not present";
                case "22": return "Non-existent command or option";
                case "23": return "Invalid command or option";
                case "24": return "Incorrect challenge";
                case "25": return "Incorrect acknowledgment";
                case "26": return "Duplicate command or option";
                case "27": return "No challenge to verify (command 109 without prior command 108)";
                case "28": return "Configuration string in command 108 too long";
                case "29": return "Unable to allocate memory for the configuration string";
                case "41": return "ASRM timed out waiting for the response from the HSM";
                case "73": return "Header mismatch";
                case "92": return "Autokey error";
                case "93": return "Factory keys already generated";
                case "94": return "No factory keys generated";
                case "99": return "Malformed response";
                default: return "Error code: " + errorCode;
            }
        }
    }
}
