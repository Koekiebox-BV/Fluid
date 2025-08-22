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

package com.fluidbpm.program.api.util.sql.syntax.impl;

import com.fluidbpm.program.api.util.sql.syntax.ISyntax;

/**
 * Syntax implementation for Stored Procedures.
 *
 * @author jasonbruwer
 * @see ISyntax
 * @since v1.0
 */
public class StoredProcedureSyntax implements ISyntax {
    private String returnVal = null;

    /**
     * Sets the Stored Procedure name and number of parameters.
     *
     * @param storedProcedureName The stored procedure.
     * @param numberOfParams      The number of parameters.
     */
    public StoredProcedureSyntax(String storedProcedureName, int numberOfParams) {
        super();
        String assignment = String.format("{CALL %s(", storedProcedureName);

        if (numberOfParams > 0) {
            for (int counter = 0; counter < numberOfParams; counter++) {
                assignment += "?";
                assignment += ",";
            }
            assignment = (assignment.substring(0, assignment.length() - 1));
        }
        assignment += ")}";
        this.returnVal = assignment;
    }

    /**
     * Implementation call for executing the stored procedure.
     *
     * @return SQL to Execute with Java {@code PreparedStatement}.
     * @see java.sql.PreparedStatement
     */
    @Override
    public String getPreparedStatement() {
        return this.returnVal;
    }
}
