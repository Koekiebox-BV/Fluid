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
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The Native SQL Query that will be executed on the
 * Fluid BPM Core server.
 * </p>
 *
 * @author jasonbruwer on 2018-03-13
 * @see java.sql.PreparedStatement
 * @see java.sql.Connection#prepareCall(String)
 * @since v1.8
 */
@Getter
@Setter
public class NativeSQLQuery extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

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
    public NativeSQLQuery(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setDatasourceName(this.getAsStringNullSafe(JSONMapping.DATASOURCE_NAME));
        this.setQuery(this.getAsStringNullSafe(JSONMapping.QUERY));
        this.setStoredProcedure(this.getAsStringNullSafe(JSONMapping.STORED_PROCEDURE));
        this.setSqlInputs(this.extractObjects(JSONMapping.SQL_INPUTS, SQLColumn::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code NativeSQLQuery}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.DATASOURCE_NAME, returnVal, this.getDatasourceName());
        this.setAsProperty(JSONMapping.QUERY, returnVal, this.getQuery());
        this.setAsProperty(JSONMapping.STORED_PROCEDURE, returnVal, this.getStoredProcedure());
        this.setAsObjArray(JSONMapping.SQL_INPUTS, returnVal, this::getSqlInputs);

        return returnVal;
    }

    /**
     * Add a SQL input parameter.
     * If the sql inputs is {@code null}, a new instance
     * of {@code ArrayList} will be created prior to adding the parameter.
     *
     * @param sqlInputToAdd The SQL Input to add.
     * @see SQLColumn
     */
    @XmlTransient
    @JsonIgnore
    public void addSqlInput(SQLColumn sqlInputToAdd) {
        if (this.sqlInputs == null) this.sqlInputs = new ArrayList<>();
        if (sqlInputToAdd == null) return;

        this.sqlInputs.add(sqlInputToAdd);
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
                this.getStoredProcedure().trim().isEmpty()) ? false : true;
    }
}
