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

package com.fluidbpm.program.api.vo.sqlutil.sqlnative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The Native SQL Query that will be executed on the
 * Fluid BPM Core server.
 * </p>
 *
 * @see java.sql.PreparedStatement
 * @see java.sql.Connection#prepareCall(String) 
 *
 * @author jasonbruwer on 2018-03-13
 * @since v1.8
 */
public class NativeSQLQuery extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private String datasourceName;

	private String query;
	private String storedProcedure;

	private List<SQLColumn> sqlInputs;

	/**
	 * The JSON mapping for the {@code NativeSQLQuery} object.
	 */
	public static class JSONMapping {

		public static final String DATASOURCE_NAME = "datasourceName";
		public static final String QUERY = "query";
		public static final String STORED_PROCEDURE = "storedProcedure";

		public static final String SQL_INPUTS = "sqlInputs";
	}

	/**
	 * Default constructor.
	 */
	public NativeSQLQuery() {
		super();
	}

	/**
	 * Sets the Id associated with a 'User Query'.
	 *
	 * @param userQueryIdParam UserQuery Id.
	 */
	public NativeSQLQuery(Long userQueryIdParam) {
		super();

		this.setId(userQueryIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public NativeSQLQuery(JSONObject jsonObjectParam){
		super(jsonObjectParam);

		if (this.jsonObject == null)
		{
			return;
		}

		//Datasource Name...
		if (!this.jsonObject.isNull(JSONMapping.DATASOURCE_NAME)) {
			this.setDatasourceName(
					this.jsonObject.getString(JSONMapping.DATASOURCE_NAME));
		}

		//Query...
		if (!this.jsonObject.isNull(JSONMapping.QUERY)) {
			this.setQuery(this.jsonObject.getString(JSONMapping.QUERY));
		}

		//Stored Procedure...
		if (!this.jsonObject.isNull(JSONMapping.STORED_PROCEDURE)) {
			this.setStoredProcedure(
					this.jsonObject.getString(JSONMapping.STORED_PROCEDURE));
		}

		//Inputs...
		if (!this.jsonObject.isNull(JSONMapping.SQL_INPUTS)) {

			JSONArray rulesArr = this.jsonObject.getJSONArray(
					JSONMapping.SQL_INPUTS);

			List<SQLColumn> inputs = new ArrayList();
			for (int index = 0;index < rulesArr.length();index++)
			{
				inputs.add(new SQLColumn(rulesArr.getJSONObject(index)));
			}

			this.setSqlInputs(inputs);
		}
		else this.setSqlInputs(null);
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code UserQuery}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {

		JSONObject returnVal = super.toJsonObject();

		//Datasource Name...
		if (this.getDatasourceName() != null) {
			returnVal.put(JSONMapping.DATASOURCE_NAME,
					this.getDatasourceName());
		}

		//Query...
		if (this.getQuery() != null) {
			returnVal.put(JSONMapping.QUERY, this.getQuery());
		}

		//Stored Procedure...
		if (this.getStoredProcedure() != null) {
			returnVal.put(
					JSONMapping.STORED_PROCEDURE,
					this.getStoredProcedure());
		}

		//Inputs...
		if (this.getSqlInputs() != null) {
			JSONArray jsonArray = new JSONArray();

			for (SQLColumn toAdd : this.getSqlInputs()) {
				jsonArray.put(toAdd.toJsonObject());
			}

			returnVal.put(JSONMapping.SQL_INPUTS, jsonArray);
		}

		return returnVal;
	}

	/**
	 * Gets name of the datasource alias to use as lookup.
	 *
	 * @return The datasource name.
	 *
	 * @see javax.sql.DataSource
	 */
	public String getDatasourceName() {
		return this.datasourceName;
	}

	/**
	 * Sets name of the datasource alias to use as lookup.
	 *
	 * @param datasourceNameParam The datasource name.
	 *
	 * @see javax.sql.DataSource
	 */
	public void setDatasourceName(String datasourceNameParam) {
		this.datasourceName = datasourceNameParam;
	}

	/**
	 * Gets SQL Query to execute.
	 * If the query is not provided, the stored procedure should be.
	 *
	 * @return The SQL Query to execute.
	 *
	 * @see PreparedStatement#executeQuery()
	 */
	public String getQuery() {
		return this.query;
	}

	/**
	 * Sets SQL Query to execute.
	 * If the query is not provided, the stored procedure should be.
	 *
	 * @param queryParam The SQL Query to execute.
	 *
	 * @see PreparedStatement#executeQuery()
	 */
	public void setQuery(String queryParam) {
		this.query = queryParam;
	}

	/**
	 * Get the stored procedure to execute.
	 *
	 * The format for the procedure is as follows;
	 *
	 * {@code {{call spShowSuppliers(?,?,?)}}}
	 * {@code {{call spShowSuppliers()}}}
	 *
	 * @return The stored procedure.
	 */
	public String getStoredProcedure() {
		return this.storedProcedure;
	}

	/**
	 * Set the stored procedure to execute.
	 *
	 * The format for the procedure is as follows;
	 *
	 * {@code {{call spShowSuppliers(?,?,?)}}}
	 * {@code {{call spShowSuppliers()}}}
	 *
	 * @param storedProcedureParam The stored procedure to execute.
	 *
	 * @see java.sql.Connection#prepareCall(String)
	 */
	public void setStoredProcedure(String storedProcedureParam) {
		this.storedProcedure = storedProcedureParam;
	}

	/**
	 * Add a SQL input parameter.
	 * If the sql inputs is {@code null}, a new instance
	 * of {@code ArrayList} will be created prior to adding the parameter.
	 *
	 * @param sqlInputToAdd The SQL Input to add.
	 *
	 * @see SQLColumn
	 */
	@XmlTransient
	@JsonIgnore
	public void addSqlInput(SQLColumn sqlInputToAdd){
		if (this.sqlInputs == null) this.sqlInputs = new ArrayList<>();
		if (sqlInputToAdd == null) return;

		this.sqlInputs.add(sqlInputToAdd);
	}

	/**
	 * Get the SQL input parameters.
	 *
	 * @return The SQL input parameters.
	 */
	public List<SQLColumn> getSqlInputs() {
		return this.sqlInputs;
	}

	/**
	 * Set the SQL input parameters.
	 *
	 * @param sqlInputsParam The SQL input parameters.
	 */
	public void setSqlInputs(List<SQLColumn> sqlInputsParam) {
		this.sqlInputs = sqlInputsParam;
	}

	/**
	 * Checks whether the stored procedure value is populated.
	 *
	 * @return {@code true} if stored procedure is populated, otherwise {@code false}.
	 */
	@XmlTransient
	@JsonIgnore
	public boolean isTypeStoredProcedure() {
		return (this.getStoredProcedure() == null ||
				this.getStoredProcedure().trim().isEmpty()) ? false:true;
	}
}
