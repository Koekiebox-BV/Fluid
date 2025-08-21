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
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
@NoArgsConstructor
public class SQLRow extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private List<SQLColumn> sqlColumns;

    /**
     * The JSON mapping for the {@code SQLRow} object.
     */
    public static class JSONMapping {
        public static final String SQL_COLUMNS = "sqlColumns";
    }

    /**
     * Constructs a new {@code SQLRow} object with the provided list of {@code SQLColumn}s.
     *
     * @param sqlColumns The list of {@code SQLColumn} objects representing the columns of the row.
     */
    public SQLRow(List<SQLColumn> sqlColumns) {
        this.setSqlColumns(sqlColumns);
    }

    /**
     * Retrieves a specific {@code SQLColumn} from the list of columns associated
     * with this {@code SQLRow}, based on the provided column name.
     *
     * @param name The name of the column to retrieve. This is case-insensitive.
     *             If {@code null}, the method will return {@code null}.
     * @return The {@code SQLColumn} with the specified name, or {@code null} if
     *         no column with the specified name exists or if the column list is
     *         {@code null}.
     */
    @JsonIgnore
    @XmlTransient
    public Optional<SQLColumn> getColumnWithName(String name) {
        if (UtilGlobal.isBlank(name) || this.sqlColumns == null) return Optional.empty();

        return this.sqlColumns.stream()
                .filter(itm -> name.equalsIgnoreCase(itm.getColumnName()))
                .findFirst();
    }

    /**
     * Adds a new {@code SQLColumn} to the list of columns associated with this {@code SQLRow}.
     * If the column list is {@code null}, a new list is initialized before adding the column.
     *
     * @param column The {@code SQLColumn} object to be added. If {@code null}, no action is taken.
     */
    @JsonIgnore
    @XmlTransient
    public void upsertColumn(SQLColumn column) {
        if (column == null) return;
        if (this.sqlColumns == null) this.sqlColumns = new ArrayList<>();

        Optional<SQLColumn> existing = this.getColumnWithName(column.getColumnName());
        if (existing.isPresent()) {
            this.sqlColumns.remove(existing.orElse(null));
        } else this.sqlColumns.add(column);
    }


    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SQLRow(JSONObject jsonObjectParam){
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        //SQL Columns...
        if (!this.jsonObject.isNull(JSONMapping.SQL_COLUMNS)) {
            JsonArray rulesArr = this.jsonObject.getJSONArray(JSONMapping.SQL_COLUMNS);
            List<SQLColumn> sqlColumns = new ArrayList();
            for (int index = 0;index < rulesArr.length();index++) {
                sqlColumns.add(new SQLColumn(rulesArr.getJSONObject(index)));
            }
            this.setSqlColumns(sqlColumns);
        } else this.setSqlColumns(null);
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
        //SQL Columns...
        if (this.getSqlColumns() != null) {
            JsonArray jsonArray = new JsonArray();
            for (SQLColumn toAdd : this.getSqlColumns()) {
                jsonArray.put(toAdd.toJsonObject());
            }
            returnVal.put(JSONMapping.SQL_COLUMNS, jsonArray);
        }

        return returnVal;
    }
}
