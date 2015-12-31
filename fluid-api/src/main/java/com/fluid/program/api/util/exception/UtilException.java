package com.fluid.program.api.util.exception;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class UtilException extends RuntimeException {

    private int errorCode;

    /**
     *
     */
    public static final class ErrorCode {
        public static final int GENERAL = 10001;
        public static final int SQL = 10002;
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public UtilException(String message, int errorCodeParam) {
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
    public UtilException(String message, Throwable cause, int errorCodeParam) {
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
