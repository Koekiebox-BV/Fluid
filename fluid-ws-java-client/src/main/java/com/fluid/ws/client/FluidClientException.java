package com.fluid.ws.client;

/**
 * Created by jasonbruwer on 15/01/16.
 */
public class FluidClientException extends RuntimeException{

    private int errorCode;

    /**
     *
     */
    public static final class ErrorCode {
        public static final int STATEMENT_SYNTAX_ERROR = 10001;
        public static final int STATEMENT_EXECUTION_ERROR = 10002;
        public static final int ILLEGAL_STATE_ERROR = 10003;
        public static final int OBJECT_REQUIRED = 10004;
        public static final int IO_ERROR = 10005;
        public static final int SESSION_EXPIRED = 10006;
        public static final int NO_RESULT = 10007;
        public static final int FIELD_VALIDATE = 10008;
        public static final int QUERY_PARAM = 10009;
        public static final int DIGEST = 10010;
        public static final int DUPLICATE = 10011;

        public static final int JMS = 10012;

        public static final int JSON_PARSING = 10013;

        public static final int AES_256 = 10014;
        public static final int MAIL = 10015;
        public static final int REFLECT = 10016;

        public static final int WEB_SERVICE = 10017;
        public static final int CRYPTOGRAPHY = 10018;
        public static final int CONNECT_ERROR = 10019;
        public static final int LOGIN_FAILURE = 10020;
        public static final int PDF_GENERATION = 10021;
        public static final int LICENSE = 10022;
        public static final int PERMISSION_DENIED = 10023;
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public FluidClientException(String message, int errorCodeParam) {
        super(message);
        this.errorCode = errorCodeParam;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause. <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this runtime exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by
     *            the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     *
     * @since 1.4
     */
    public FluidClientException(String message, Throwable cause, int errorCodeParam) {
        super(message, cause);
        this.errorCode = errorCodeParam;
    }

    /**
     *
     * @return
     */
    public int getErrorCode() {
        return this.errorCode;
    }
}
