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

package com.fluidbpm.program.api.util.exception;

import com.fluidbpm.program.api.util.command.CommandUtil;

/**
 * Exception class related to using Fluid's Utility classes
 * from within a custom program.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see CommandUtil
 * @see com.fluidbpm.program.api.util.sql.exception.FluidSQLException
 * @see RuntimeException
 */
public class UtilException extends RuntimeException {

	private int errorCode;

	/**
	 * Class for hosting error codes.
	 */
	public static final class ErrorCode {
		public static final int GENERAL = 10001;
		public static final int SQL = 10002;
		public static final int CACHE = 10003;
		public static final int COMMAND = 10004;
		public static final int ELASTIC = 10005;
	}

	/**
	 * Constructs a new runtime exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call
	 * to {@link #initCause}.
	 *
	 * @param messageParam the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param errorCodeParam Error code of the {@code Exception}.
	 *
	 * @see ErrorCode
	 */
	public UtilException(String messageParam, int errorCodeParam) {
		super(messageParam);
		this.errorCode = errorCodeParam;
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause. <p> Note that the detail message associated with {@code cause} is
	 * <i>not</i> automatically incorporated in this runtime exception's detail
	 * message.
	 *
	 * @param messageParam the detail message (which is saved for later retrieval by
	 *            the {@link #getMessage()} method).
	 * @param causeParam the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param errorCodeParam Error code of the {@code Exception}.
	 *
	 * @see ErrorCode
	 */
	public UtilException(String messageParam, Throwable causeParam, int errorCodeParam) {
		super(messageParam, causeParam);
		this.errorCode = errorCodeParam;
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
