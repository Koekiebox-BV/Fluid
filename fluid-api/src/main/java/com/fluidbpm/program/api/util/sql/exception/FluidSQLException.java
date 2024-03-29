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

package com.fluidbpm.program.api.util.sql.exception;

import java.sql.SQLException;

import com.fluidbpm.program.api.util.exception.UtilException;

/**
 * Exception class related to SQL Exceptions exclusively.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see UtilException
 * @see SQLException
 */
public class FluidSQLException extends UtilException {

	/**
	 * Constructor used to wrap a traditional {@code SQLException}
	 * into an {@code FluidSQLException}.
	 *
	 * @param sqlExceptionParam Traditional Java SQL Exception.
	 */
	public FluidSQLException(SQLException sqlExceptionParam) {
		super("SQL Problem: "+((sqlExceptionParam == null) ? null:
				sqlExceptionParam.getMessage()),
				sqlExceptionParam, ErrorCode.SQL);
	}
}
