/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.util.sql.syntax.impl;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.sql.syntax.ISyntax;

/**
 * Syntax implementation for PostgreSQL Stored Procedures / Functions.
 * @author jasonbruwer
 * @since v1.13
 *
 * @see ISyntax
 */
public class StoredProcedureSyntaxPostgreSQL extends StoredProcedureSyntax {
	public static final String SP_CALL_TAG = "{call";
	public static final String FUNC_SELECT = "SELECT * FROM ";

	/**
	 * Sets the Stored Procedure name and number of parameters.
	 *
	 * @param storedProcedureName The stored procedure.
	 * @param numberOfParams The number of parameters.
	 */
	public StoredProcedureSyntaxPostgreSQL(String storedProcedureName, int numberOfParams) {
		super(storedProcedureName, numberOfParams);
	}

	/**
	 * Implementation call for executing the stored procedure.
	 *
	 * @return SQL to Execute with Java {@code PreparedStatement}.
	 *
	 * @see java.sql.PreparedStatement
	 */
	@Override
	public String getPreparedStatement() {
		String returnVal = super.getPreparedStatement();
		if (UtilGlobal.isBlank(returnVal)) return returnVal;

		if (returnVal.toLowerCase().startsWith(SP_CALL_TAG)) {
			return String.format(
					"%s%s", FUNC_SELECT,
					returnVal.substring(SP_CALL_TAG.length(), returnVal.length() - 1)
			);
		}
		return returnVal;
	}
}
