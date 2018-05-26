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

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * <p>
 * The same as a SQL {@code ResultSet} row.
 * </p>
 *
 * @see PreparedStatement
 * @see java.sql.Connection#prepareCall(String)
 * @see java.sql.ResultSet
 *
 * @author jasonbruwer on 2018-03-13
 * @since v1.8
 */
public class SQLRow extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private List<SQLColumn> sqlColumns;

    /**
     * The JSON mapping for the {@code SQLRow} object.
     */
    public static class JSONMapping {
        
        public static final String SQL_COLUMNS = "sqlColumns";
    }

    /**
     * Default constructor.
     */
    public SQLRow() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SQLRow(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }
        
        //SQL Columns...
        if (!this.jsonObject.isNull(JSONMapping.SQL_COLUMNS)) {

            JSONArray rulesArr = this.jsonObject.getJSONArray(
                    JSONMapping.SQL_COLUMNS);

            List<SQLColumn> sqlColumns = new ArrayList();
            for(int index = 0;index < rulesArr.length();index++)
            {
                sqlColumns.add(new SQLColumn(rulesArr.getJSONObject(index)));
            }

            this.setSqlColumns(sqlColumns);
        }
        else{
            this.setSqlColumns(null);
        }
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

        //SQL Columns...
        if(this.getSqlColumns() != null)
        {
            JSONArray jsonArray = new JSONArray();

            for(SQLColumn toAdd : this.getSqlColumns())
            {
                jsonArray.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.SQL_COLUMNS, jsonArray);
        }
        
        return returnVal;
    }
    
    /**
     * Get the SQL columns.
     *
     * @return The SQL columns.
     */
    public List<SQLColumn> getSqlColumns() {
        return this.sqlColumns;
    }

    /**
     * Set the SQL columns.
     *
     * @param sqlInputsParam The SQL columns.
     */
    public void setSqlColumns(List<SQLColumn> sqlInputsParam) {
        this.sqlColumns = sqlInputsParam;
    }
}
