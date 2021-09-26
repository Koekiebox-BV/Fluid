/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client;

/**
 * Exception class related to the Fluid Java WS Client.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see RuntimeException
 */
public class FluidClientException extends RuntimeException {
	private int errorCode;

	/**
	 * Mapping of Error codes received from server and local.
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

		public static final int WEB_SOCKET_IO_ERROR = 10024;
		public static final int WEB_SOCKET_DEPLOY_ERROR = 10025;
		public static final int WEB_SOCKET_URI_SYNTAX_ERROR = 10026;
	}

	/**
	 * Constructs a new runtime exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call
	 * to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param errorCode Error code of the {@code Exception}.
	 *
	 */
	public FluidClientException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
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
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param errorCode Error code of the {@code Exception}.
	 */
	public FluidClientException(String message, Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error code for {@code this} Exception.
	 *
	 * @return Numerical error code category for the exception.
	 *
	 * @see ErrorCode
	 */
	public int getErrorCode() {
		return this.errorCode;
	}
}
