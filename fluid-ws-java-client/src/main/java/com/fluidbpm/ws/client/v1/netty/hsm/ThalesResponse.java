package com.fluidbpm.ws.client.v1.netty.hsm;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * Represents a response from a Thales HSM.
 * Thales responses follow the format: [Response Code][Error Code][Response Data]
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Getter
public class ThalesResponse {
    private final String responseCode;
    private final String errorCode;
    private final String responseData;
    private final String requestId;
    private final byte[] rawResponse;

    /**
     * Constructs a Thales response from raw bytes.
     *
     * @param rawResponse The raw response bytes
     */
    public ThalesResponse(byte[] rawResponse) {
        this(rawResponse, null);
    }

    /**
     * Constructs a Thales response from raw bytes with a request ID.
     *
     * @param rawResponse The raw response bytes
     * @param requestId Optional request ID for correlation
     */
    public ThalesResponse(byte[] rawResponse, String requestId) {
        this.rawResponse = rawResponse;
        this.requestId = requestId;

        String response = new String(rawResponse, StandardCharsets.US_ASCII);

        // Thales response format: [2-char response code][2-char error code][response data]
        if (response.length() >= 4) {
            this.responseCode = response.substring(0, 2);
            this.errorCode = response.substring(2, 4);
            this.responseData = response.length() > 4 ? response.substring(4) : "";
        } else {
            this.responseCode = response.length() >= 2 ? response.substring(0, 2) : response;
            this.errorCode = "99"; // Unknown error
            this.responseData = "";
        }
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
     * Gets the full response as a string.
     *
     * @return The complete response string
     */
    public String getFullResponse() {
        return new String(rawResponse, StandardCharsets.US_ASCII);
    }

    @Override
    public String toString() {
        return "ThalesResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", success=" + isSuccess() +
                ", responseData='" + responseData + '\'' +
                ", requestId='" + requestId + '\'' +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }

    /**
     * Thales HSM error codes based on international command set.
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
