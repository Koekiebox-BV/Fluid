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

package com.fluid.program.api.util.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fluid.program.api.util.sql.exception.FluidSQLException;

/**
 * Base class used for SQL Type operations in Fluid.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Connection
 * @see javax.sql.DataSource
 * @see PreparedStatement
 * @see java.sql.Statement
 */
public abstract class ABaseSQLUtil {

    private Connection connection;

    /**
     * The type of SQL Database engine.
     */
    public static enum SQLType{
        Unknown(""),
        MySQL("mysql"),
        MicrosoftSQL("microsoft sql server");

        private String productName;

        /**
         * The Database product name.
         *
         * @param productNameLowerParam Product name in lower case.
         */
        SQLType(String productNameLowerParam)
        {
            this.productName = productNameLowerParam;
        }

        /**
         * Get {@code SQLType} by {@code productNameParam}
         *
         * @param productNameParam Product Name.
         * @return {@code enum} for SQL Type.
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
     * The SQL Connection.
     *
     * @param connectionParam Connection used.
     */
    public ABaseSQLUtil(Connection connectionParam)
    {
        this.connection = connectionParam;
    }

    /**
     * Close the SQL Connection.
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
     * Retrieves SQLType from the local {@code Connection}.
     *
     * @return The {@code SQLType} from the {@code Connection}.
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
     * Gets the local {@code Connection}.
     *
     * @return local Connection.
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
     * Closes the {@code preparedStatementParam} and {@code resultSetParam}.
     *
     * @param preparedStatementParam The SQL Prepared Statement.
     * @param resultSetParam The SQL ResultSet.
     *
     * @see PreparedStatement
     * @see ResultSet
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
     * Closes the {@code preparedStatementParam}.
     *
     * @param preparedStatementParam The SQL Prepared Statement.
     *
     * @see PreparedStatement
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
