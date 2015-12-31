package com.fluid.program.api.util.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fluid.program.api.util.sql.exception.FluidSQLException;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public abstract class ABaseSQLUtil {

    private Connection connection;

    /**
     *
     */
    public static enum SQLType{
        Unknown(""),
        MySQL("mysql"),
        MicrosoftSQL("microsoft sql server");

        private String productName;

        /**
         *
         * @param productNameLowerParam
         */
        SQLType(String productNameLowerParam)
        {
            this.productName = productNameLowerParam;
        }

        /**
         *
         * @param productNameParam
         * @return
         */
        public static SQLType getSQLTypeFromProductName(String productNameParam)
        {
            if(productNameParam == null || productNameParam.trim().isEmpty())
            {
                return SQLType.Unknown;
            }

            String paramLower = productNameParam.toLowerCase();

            for(SQLType sqlType : SQLType.values())
            {
                if(sqlType.productName.equals(paramLower))
                {
                    return sqlType;
                }
            }

            return SQLType.Unknown;
        }
    }

    /**
     *
     * @param connectionParam
     */
    public ABaseSQLUtil(Connection connectionParam)
    {
        this.connection = connectionParam;
    }

    /**
     *
     */
    public void closeConnection()
    {
        if(this.connection == null)
        {
            return;
        }

        try
        {
            if(this.connection.isClosed())
            {
                return;
            }

            this.connection.close();
        }
        //
        catch (SQLException sqlExcept)
        {
            throw new FluidSQLException(sqlExcept);
        }
    }



    /**
     *
     * @return
     */
    public SQLType getSQLTypeFromConnection()
    {
        try {
            String productName =
                    this.getConnection().getMetaData().getDatabaseProductName();

            return SQLType.getSQLTypeFromProductName(productName);
        }
        //
        catch (SQLException sqlExcept) {
            throw new FluidSQLException(sqlExcept);
        }
    }

    /**
     * @return
     */
    public Connection getConnection()
    {
        if(this.connection == null)
        {
            throw new FluidSQLException(new SQLException(
                    "Connection is not set. Critical!!!"));
        }

        return this.connection;
    }

    /**
     *
     * @param preparedStatementParam
     * @param resultSetParam
     */
    protected void closeStatement(PreparedStatement preparedStatementParam,
                                  ResultSet resultSetParam)
    {
        this.closeStatement(preparedStatementParam);

        if(resultSetParam == null)
        {
            return;
        }

        try {
            resultSetParam.close();
        }
        //
        catch (SQLException sqlExcept) {
            throw new FluidSQLException(sqlExcept);
        }
    }

    /**
     *
     * @param preparedStatementParam
     */
    protected void closeStatement(PreparedStatement preparedStatementParam)
    {
        if(preparedStatementParam == null)
        {
            return;
        }

        try {
            preparedStatementParam.close();
        }
        //
        catch (SQLException sqlExcept) {
            throw new FluidSQLException(sqlExcept);
        }
    }
}
