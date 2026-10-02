package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import lombok.Getter;

import java.util.Arrays;

/**
 * Represents a response from a Thales HSM.
 *
 * Wire layout: {@code [message header][2-char response code][2-char error code][response data]}.
 * The header is the one sent with the request, echoed back unmodified by the HSM.
 * Response data is retained as raw bytes so binary fields survive intact; the
 * string views use ISO-8859-1 (1:1 byte mapping).
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Getter
public class ThalesResponse {
    /** Error code reported when the frame is too short to carry one. */
    public static final String MALFORMED_ERROR_CODE = "99";

    private final String header;
    private final String responseCode;
    private final String errorCode;
    private final byte[] responseDataBytes;
    private final byte[] rawResponse;

    /**
     * Constructs a response from raw bytes for an HSM configured without a message header.
     *
     * @param rawResponse The raw response bytes (length prefix already stripped)
     */
    public ThalesResponse(byte[] rawResponse) {
        this(rawResponse, 0);
    }

    /**
     * Constructs a response from raw bytes, splitting off the echoed message header.
     *
     * @param rawResponse The raw response bytes (length prefix already stripped)
     * @param headerLength The message header length configured on the HSM
     */
    public ThalesResponse(byte[] rawResponse, int headerLength) {
        this.rawResponse = Arrays.copyOf(rawResponse, rawResponse.length);
        int hdr = Math.max(0, Math.min(headerLength, rawResponse.length));
        this.header = new String(rawResponse, 0, hdr, ThalesCommand.WIRE_CHARSET);

        int remaining = rawResponse.length - hdr;
        if (remaining >= 4) {
            this.responseCode = new String(rawResponse, hdr, 2, ThalesCommand.WIRE_CHARSET);
            this.errorCode = new String(rawResponse, hdr + 2, 2, ThalesCommand.WIRE_CHARSET);
            this.responseDataBytes = Arrays.copyOfRange(rawResponse, hdr + 4, rawResponse.length);
        } else {
            this.responseCode = new String(rawResponse, hdr, Math.min(2, remaining), ThalesCommand.WIRE_CHARSET);
            this.errorCode = MALFORMED_ERROR_CODE;
            this.responseDataBytes = new byte[0];
        }
    }

    /**
     * Constructs a response with an explicit correlation id (no header on the wire).
     *
     * @param rawResponse The raw response bytes
     * @param requestId Correlation id
     * @deprecated Correlation now uses the echoed message header; use {@link #ThalesResponse(byte[], int)}.
     */
    @Deprecated
    public ThalesResponse(byte[] rawResponse, String requestId) {
        this(prefix(requestId, rawResponse), requestId == null ? 0 : requestId.length());
    }

    private static byte[] prefix(String header, byte[] body) {
        if (header == null || header.isEmpty()) return body;
        byte[] h = header.getBytes(ThalesCommand.WIRE_CHARSET);
        byte[] r = new byte[h.length + body.length];
        System.arraycopy(h, 0, r, 0, h.length);
        System.arraycopy(body, 0, r, h.length, body.length);
        return r;
    }

    /**
     * Gets the correlation id (the echoed message header).
     *
     * @return The header, or {@code null} if the HSM has no header configured
     */
    public String getRequestId() {
        return header.isEmpty() ? null : header;
    }

    /**
     * Gets the response data as text (ISO-8859-1 decoded).
     *
     * @return The response data
     */
    public String getResponseData() {
        return new String(responseDataBytes, ThalesCommand.WIRE_CHARSET);
    }

    /**
     * Gets a copy of the raw response data bytes (after header, response code and error code).
     *
     * @return The response data bytes
     */
    public byte[] getResponseDataBytes() {
        return Arrays.copyOf(responseDataBytes, responseDataBytes.length);
    }

    /**
     * Gets a copy of the complete raw response (header included).
     *
     * @return The raw response bytes
     */
    public byte[] getRawResponse() {
        return Arrays.copyOf(rawResponse, rawResponse.length);
    }

    /**
     * Checks if the response indicates success.
     * Error code "00" indicates success in Thales HSM.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return "00".equals(errorCode);
    }

    /**
     * Gets a human-readable error message based on the error code.
     *
     * @return Error message description
     */
    public String getErrorMessage() {
        return ThalesErrorCode.getMessage(errorCode);
    }

    /**
     * Gets the full response as a string (header included).
     *
     * @return The complete response string
     */
    public String getFullResponse() {
        return new String(rawResponse, ThalesCommand.WIRE_CHARSET);
    }

    @Override
    public String toString() {
        return "ThalesResponse{" +
                "header='" + header + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", success=" + isSuccess() +
                ", responseDataLength=" + responseDataBytes.length +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }

    /**
     * Thales HSM error codes based on the payShield host command set.
     */
    public static class ThalesErrorCode {
        public static final String SUCCESS = "00";
        public static final String VERIFICATION_FAILURE = "01";
        public static final String KEY_PARITY_ERROR = "02";
        public static final String NO_KEY_LOADED = "03";
        public static final String INVALID_KEY_TYPE = "04";
        public static final String INVALID_KEY_LENGTH = "05";
        public static final String INVALID_INPUT_DATA = "10";
        public static final String INVALID_PIN_BLOCK = "11";
        public static final String INVALID_ACCOUNT_NUMBER = "12";
        public static final String INVALID_PIN_LENGTH = "13";
        public static final String DECIMALIZATION_ERROR = "14";
        public static final String INVALID_COMMAND_CODE = "15";
        public static final String SYNTAX_ERROR = "20";
        public static final String FUNCTION_NOT_AVAILABLE = "21";
        public static final String HSM_CRYPTO_FAILURE = "26";
        public static final String LMK_ERROR = "27";
        public static final String INVALID_LMK_IDENTIFIER = "28";
        public static final String HSM_NOT_AUTHORIZED = "30";
        public static final String TIMEOUT = "90";
        public static final String UNKNOWN_ERROR = "99";

        /**
         * Gets a human-readable message for an error code.
         *
         * @param errorCode The Thales error code
         * @return Error message description
         */
        public static String getMessage(String errorCode) {
            if (errorCode == null) return "Unknown error";

            switch (errorCode) {
                case "00": return "No error - Command executed successfully";
                case "01": return "Verification failure";
                case "02": return "Key parity error";
                case "03": return "No key type loaded";
                case "04": return "Invalid key type code";
                case "05": return "Invalid key length flag";
                case "10": return "Invalid input data";
                case "11": return "Invalid PIN block";
                case "12": return "Invalid account number";
                case "13": return "Invalid PIN length or decimalization table";
                case "14": return "PIN decimalization error";
                case "15": return "Invalid command code or message format";
                case "20": return "Syntax error in command";
                case "21": return "Function not available in this HSM";
                case "26": return "HSM cryptographic failure";
                case "27": return "LMK error - Key check value failed";
                case "28": return "Invalid LMK identifier";
                case "30": return "HSM is not authorized for this command";
                case "90": return "Timeout waiting for response";
                case "99": return "Unknown error";
                default: return "Error code: " + errorCode;
            }
        }
    }
}
