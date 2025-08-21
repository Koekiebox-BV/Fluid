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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * <p>
 * The input value used for a prepared statement or stored procedure that
 * would be set on the Fluid BPM Core.
 * </p>
 *
 * @see java.sql.SQLType
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 *
 * @author jasonbruwer on 2018-03-12
 * @since v1.8
 */
public class SQLColumn extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String columnName;
    private Integer columnIndex;

    private Integer sqlType;
    private Object sqlValue;

    /**
     * The JSON mapping for the {@code Input} object.
     */
    public static class JSONMapping
    {
        public static final String COLUMN_NAME = "columnName";
        public static final String COLUMN_INDEX = "columnIndex";
        public static final String SQL_TYPE = "sqlType";
        public static final String SQL_VALUE = "sqlValue";
    }

    /**
     * Default constructor.
     */
    public SQLColumn() {
        super();
    }

    /**
     * Sets all the values for the column.
     *
     * @param columnNameParam The column name.
     * @param columnIndexParam The column index.
     * @param sqlTypeParam The SQL Type. See {@code java.sql.Types}
     * @param sqlValueParam The value of the param at index {@code columnIndexParam}.
     *
     * @see java.sql.Types
     */
    public SQLColumn(
            String columnNameParam,
            Integer columnIndexParam,
            Integer sqlTypeParam,
            Object sqlValueParam) {
        super();

        this.columnName = columnNameParam;
        this.columnIndex = columnIndexParam;
        this.sqlType = sqlTypeParam;
        this.sqlValue = sqlValueParam;
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SQLColumn(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if (this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.COLUMN_NAME)) {

            this.setColumnName(
                    this.jsonObject.getString(
                            JSONMapping.COLUMN_NAME));
        }
        
        //Index...
        if (!this.jsonObject.isNull(JSONMapping.COLUMN_INDEX)) {
            
            this.setColumnIndex(
                    this.jsonObject.getInt(JSONMapping.COLUMN_INDEX));
        }

        //SQL Type...
        if (!this.jsonObject.isNull(JSONMapping.SQL_TYPE)) {
            
            this.setSqlType(
                    this.jsonObject.getInt(JSONMapping.SQL_TYPE));
        }

        //Value...
        if (!this.jsonObject.isNull(JSONMapping.SQL_VALUE)) {

            this.setSqlValue(this.jsonObject.get(JSONMapping.SQL_VALUE));
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
    public JsonObject toJsonObject() throws JSONException {

        JsonObject returnVal = super.toJsonObject();

        //Name...
        if (this.getColumnName() != null)
        {
            returnVal.put(JSONMapping.COLUMN_NAME,
                    this.getColumnName());
        }
        
        //Index...
        if (this.getColumnIndex() != null)
        {
            returnVal.put(JSONMapping.COLUMN_INDEX,
                    this.getColumnIndex());
        }

        //SQL Type...
        if (this.getSqlType() != null)
        {
            returnVal.put(
                    JSONMapping.SQL_TYPE,this.getSqlType());
        }

        //SQL Value...
        if (this.getSqlValue() != null)
        {
            returnVal.put(JSONMapping.SQL_VALUE,
                    this.getSqlValue());
        }

        return returnVal;
    }

    /**
     * Get the SQL column name.
     *
     * @return The SQL column name.
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Set the SQL column name.
     *
     * @param columnNameParam The SQL column name.
     */
    public void setColumnName(String columnNameParam) {
        this.columnName = columnNameParam;
    }

    /**
     * Get the SQL parameter index.
     * The first parameter is 1.
     *
     * @return The SQL parameter index.
     *
     * @see java.sql.PreparedStatement#setObject(int, Object)
     */
    public Integer getColumnIndex() {
        return this.columnIndex;
    }

    /**
     * Set the SQL parameter index.
     * The first parameter is 1.
     *
     * @param indexParam The SQL parameter index.
     *
     * @see java.sql.PreparedStatement#setObject(int, Object)
     */
    public void setColumnIndex(Integer indexParam) {
        this.columnIndex = indexParam;
    }
    
    /**
     * Get the SQL Type as {@code int}.
     *
     * @return The SQL Type.
     *
     * @see java.sql.Types
     */
    public Integer getSqlType() {
        return this.sqlType;
    }

    /**
     * Set the SQL Type as {@code Integer}.
     *
     * @param sqlTypeParam The SQL Type.
     *
     * @see java.sql.Types
     */
    public void setSqlType(Integer sqlTypeParam) {
        this.sqlType = sqlTypeParam;
    }

    /**
     * Get the param value.
     * The type will be converted at time of use.
     *
     * @return The value of the parameter.
     *
     * @see java.sql.PreparedStatement#setObject(int, Object) 
     */
    public Object getSqlValue() {
        return this.sqlValue;
    }

    /**
     * Get the param value.
     * The type will be converted at time of use.
     *
     * @param paramValueParam The value of the parameter.
     *
     * @see java.sql.PreparedStatement#setObject(int, Object)
     */
    public void setSqlValue(Object paramValueParam) {
        this.sqlValue = paramValueParam;
    }
}
