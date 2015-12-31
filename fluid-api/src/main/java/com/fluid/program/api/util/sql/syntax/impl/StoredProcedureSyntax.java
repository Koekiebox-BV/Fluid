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
