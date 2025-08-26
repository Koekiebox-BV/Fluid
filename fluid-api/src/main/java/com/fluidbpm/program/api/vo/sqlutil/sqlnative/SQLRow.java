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
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
 * @author jasonbruwer on 2018-03-13
 * @see PreparedStatement
 * @see java.sql.Connection#prepareCall(String)
 * @see java.sql.ResultSet
 * @since v1.8
 */
@NoArgsConstructor
@Getter
@Setter
public class SQLRow extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
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
     * no column with the specified name exists or if the column list is
     * {@code null}.
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
    public SQLRow(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setSqlColumns(this.extractObjects(JSONMapping.SQL_COLUMNS, SQLColumn::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code SQLRow}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        
        this.setAsObjArray(JSONMapping.SQL_COLUMNS, returnVal, this::getSqlColumns);
        
        return returnVal;
    }
}
