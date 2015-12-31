package com.fluid.program.api.util.sql.exception;

import java.sql.SQLException;

import com.fluid.program.api.util.exception.UtilException;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class FluidSQLException extends UtilException {

    /**
     *
     * @param sqlExceptionParam
     */
    public FluidSQLException(SQLException sqlExceptionParam) {
        super("SQL Problem: "+((sqlExceptionParam == null) ? null:
                sqlExceptionParam.getMessage()),
                sqlExceptionParam, ErrorCode.SQL);
    }
}
