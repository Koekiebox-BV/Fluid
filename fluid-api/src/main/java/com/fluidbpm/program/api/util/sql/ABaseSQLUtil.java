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

package com.fluidbpm.program.api.util.sql;

import java.sql.*;

import com.fluidbpm.program.api.util.ABaseUtil;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.sql.exception.FluidSQLException;

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
public abstract class ABaseSQLUtil extends ABaseUtil {
	private Connection connection;
	private DatabaseMetaData databaseMetaData;

	/**
	 * The type of SQL Database engine.
	 */
	public static enum SQLServerType {
		Unknown(UtilGlobal.EMPTY),
		MySQL("mysql"),
		MicrosoftSQL("microsoft sql server");

		private String productName;

		/**
		 * The Database product name.
		 *
		 * @param productNameLowerParam Product name in lower case.
		 */
		SQLServerType(String productNameLowerParam)
		{
			this.productName = productNameLowerParam;
		}

		/**
		 * Get {@code SQLType} by {@code productNameParam}
		 *
		 * @param productNameParam Product Name.
		 * @return {@code enum} for SQL Type.
		 */
		public static SQLServerType getSQLTypeFromProductName(String productNameParam) {
			if (productNameParam == null || productNameParam.trim().isEmpty()) {
				return SQLServerType.Unknown;
			}

			String paramLower = productNameParam.toLowerCase();

			for (SQLServerType sqlType : SQLServerType.values()) {
				if (sqlType.productName.equals(paramLower)) {
					return sqlType;
				}
			}

			return SQLServerType.Unknown;
		}
	}

	/**
	 * The SQL Connection.
	 *
	 * @param connectionParam Connection used.
	 */
	public ABaseSQLUtil(Connection connectionParam) {
		super();
		this.connection = connectionParam;
	}

	/**
	 * The SQL Connection with {@link CacheUtil}.
	 *
	 * @param connectionParam Connection used.
	 * @param cacheUtilParam The Cache utility for use.
	 */
	public ABaseSQLUtil(
			Connection connectionParam, CacheUtil cacheUtilParam) {
		super(cacheUtilParam);
		this.connection = connectionParam;
	}

	/**
	 * Close the SQL Connection.
	 */
	public void closeConnection() {
		if (this.connection == null) {
			return;
		}

		try {
			if (this.connection.isClosed()) {
				return;
			}

			this.connection.close();
		} catch (SQLException sqlExcept) {
			throw new FluidSQLException(sqlExcept);
		}
	}

	/**
	 * Retrieves SQLType from the local {@code Connection}.
	 *
	 * @return The {@code SQLType} from the {@code Connection}.
	 */
	public SQLServerType getSQLTypeFromConnection() {
		try {
			if (this.databaseMetaData == null){
				this.databaseMetaData = this.getConnection().getMetaData();
			}

			return SQLServerType.getSQLTypeFromProductName(
					this.databaseMetaData.getDatabaseProductName());
		} catch (SQLException sqlExcept) {
			//Unable to retrieve the product name.
			throw new FluidSQLException(sqlExcept);
		}
	}

	/**
	 * Gets the local {@code Connection}.
	 *
	 * @return local Connection.
	 */
	public Connection getConnection() {
		if (this.connection == null) {
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
								  ResultSet resultSetParam
	) {
		if (resultSetParam == null) {
			this.closeStatement(preparedStatementParam);
			return;
		}

		try {
			resultSetParam.close();
			this.closeStatement(preparedStatementParam);
		} catch (SQLException sqlExcept) {
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
	protected void closeStatement(PreparedStatement preparedStatementParam) {
		if (preparedStatementParam == null) {
			return;
		}

		try {
			preparedStatementParam.close();
		} catch (SQLException sqlExcept) {
			throw new FluidSQLException(sqlExcept);
		}
	}
}
