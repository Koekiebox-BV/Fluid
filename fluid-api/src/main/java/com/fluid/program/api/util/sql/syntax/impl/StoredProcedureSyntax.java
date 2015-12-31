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

package com.fluid.program.api.util.sql.syntax.impl;

import com.fluid.program.api.util.sql.syntax.ISyntax;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class StoredProcedureSyntax implements ISyntax {
    private String returnVal = null;

    /**
     *
     * @param storedProcedureNameParam
     * @param numberOfParamsParam
     */
    public StoredProcedureSyntax(
            String storedProcedureNameParam,
            int numberOfParamsParam) {
        super();

        String assignment = ("{CALL "+storedProcedureNameParam+"(");

        if(numberOfParamsParam > 0)
        {
            for(int counter = 0;counter < numberOfParamsParam;counter++)
            {
                assignment += "?";
                assignment += ",";
            }

            assignment = (assignment.substring(0,assignment.length() -1));
        }

        assignment += ")}";

        this.returnVal = assignment;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPreparedStatement() {
        return this.returnVal;
    }
}
