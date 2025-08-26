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
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.sql.Types;
import java.util.Date;

/**
 * <p>
 * The input value used for a prepared statement or stored procedure that
 * would be set on the Fluid BPM Core.
 * </p>
 *
 * @author jasonbruwer on 2018-03-12
 * @see java.sql.SQLType
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 * @since v1.8
 */
@Getter
@Setter
public class SQLColumn extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String columnName;
    private Integer columnIndex;
    private Integer sqlType;
    private Object sqlValue;

    /**
     * The JSON mapping for the {@code Input} object.
     */
    public static class JSONMapping {
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
     * @param columnNameParam  The column name.
     * @param columnIndexParam The column index.
     * @param sqlTypeParam     The SQL Type. See {@code java.sql.Types}
     * @param sqlValueParam    The value of the param at index {@code columnIndexParam}.
     * @see java.sql.Types
     */
    public SQLColumn(
            String columnNameParam,
            Integer columnIndexParam,
            Integer sqlTypeParam,
            Object sqlValueParam
    ) {
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
    public SQLColumn(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setColumnName(this.getAsStringNullSafe(JSONMapping.COLUMN_NAME));
        this.setColumnIndex(this.getAsIntegerNullSafe(JSONMapping.COLUMN_INDEX));
        this.setSqlType(this.getAsIntegerNullSafe(JSONMapping.SQL_TYPE));
        
        // Handle SQL Value
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.SQL_VALUE)) {
            JsonElement valElement = this.jsonObject.get(JSONMapping.SQL_VALUE);
            if ((!valElement.isJsonNull() && valElement.isJsonPrimitive()) &&
                    this.getSqlType() != null) {
                int sqlType = this.getSqlType();
                switch (sqlType) {
                    // String types
                    case Types.VARCHAR:
                    case Types.CHAR:
                    case Types.LONGVARCHAR:
                    case Types.NCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGNVARCHAR:
                    case Types.CLOB:
                    case Types.NCLOB:
                    case Types.SQLXML:
                    case Types.DATALINK:
                        this.setSqlValue(valElement.getAsString());
                        break;
                    
                    // Integer types
                    case Types.INTEGER:
                    case Types.SMALLINT:
                    case Types.TINYINT:
                        this.setSqlValue(valElement.getAsInt());
                        break;
                    
                    // Long types
                    case Types.BIGINT:
                    case Types.ROWID:
                        this.setSqlValue(valElement.getAsLong());
                        break;
                    
                    // Floating point types
                    case Types.FLOAT:
                    case Types.REAL:
                    case Types.DOUBLE:
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                        this.setSqlValue(valElement.getAsDouble());
                        break;
                    
                    // Boolean type
                    case Types.BOOLEAN:
                    case Types.BIT:
                        this.setSqlValue(valElement.getAsBoolean());
                        break;
                    
                    // Date and Time types
                    case Types.DATE:
                    case Types.TIME:
                    case Types.TIMESTAMP:
                    case Types.TIME_WITH_TIMEZONE:
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                        // For date/time types, store as string and let the caller parse as needed
                        this.setSqlValue(valElement.getAsString());
                        break;
                    
                    // Binary types
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                    case Types.BLOB:
                        // For binary types, store as string and let the caller decode as needed
                        this.setSqlValue(valElement.getAsString());
                        break;
                    
                    // Complex types
                    case Types.ARRAY:
                    case Types.STRUCT:
                    case Types.REF:
                    case Types.REF_CURSOR:
                    case Types.JAVA_OBJECT:
                    case Types.DISTINCT:
                    case Types.OTHER:
                        // For complex types, store as string representation
                        this.setSqlValue(valElement.getAsString());
                        break;
                    
                    // NULL type
                    case Types.NULL:
                        this.setSqlValue(null);
                        break;
                    
                    // Default case for any unhandled types
                    default:
                        this.setSqlValue(valElement.getAsString());
                        break;
                }
            }
        }
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code SQLColumn}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.COLUMN_NAME, returnVal, this.getColumnName());
        this.setAsProperty(JSONMapping.COLUMN_INDEX, returnVal, this.getColumnIndex());
        this.setAsProperty(JSONMapping.SQL_TYPE, returnVal, this.getSqlType());
        
        // Handle SQL Value based on its type
        Object sqlValue = this.getSqlValue();
        if (sqlValue != null) {
            if (sqlValue instanceof String) {
                returnVal.addProperty(JSONMapping.SQL_VALUE, (String) sqlValue);
            } else if (sqlValue instanceof Number) {
                returnVal.addProperty(JSONMapping.SQL_VALUE, (Number) sqlValue);
            } else if (sqlValue instanceof Date) {
                returnVal.addProperty(JSONMapping.SQL_VALUE, ((Date) sqlValue).getTime());
            } else if (sqlValue instanceof Boolean) {
                returnVal.addProperty(JSONMapping.SQL_VALUE, (Boolean) sqlValue);
            } else {
                // For other types, convert to string
                returnVal.addProperty(JSONMapping.SQL_VALUE, sqlValue.toString());
            }
        }

        return returnVal;
    }
}
