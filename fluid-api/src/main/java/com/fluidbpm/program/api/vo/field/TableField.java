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

package com.fluidbpm.program.api.vo.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Represents a {@code Form} Table Field.
 * Table fields can store other {@code Field}s inside itself.
 * This allows for a much richer Electronic Form.
 * </p>
 *
 * @author jasonbruwer
 * @see Field
 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil
 * @since v1.0
 */
@Getter
@Setter
public class TableField extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private List<Form> tableRecords;
    private Boolean sumDecimals;

    /**
     * The JSON mapping for the {@code TableField} object.
     */
    public static class JSONMapping {
        public static final String TABLE_RECORDS = "tableRecords";
        public static final String SUM_DECIMALS = "sumDecimals";
    }

    /**
     * Default constructor.
     */
    public TableField() {
        super();
    }

    /**
     * Constructor to create {@code TableField} with records.
     *
     * @param tableRecordsParam The records to create.
     */
    public TableField(List<Form> tableRecordsParam) {
        super();
        this.setTableRecords(tableRecordsParam);
    }

    /**
     * Constructor to create {@code TableField} with records.
     *
     * @param toClone The records to clone.
     */
    public TableField(TableField toClone) {
        this();
        if (toClone == null) return;
        this.setId(toClone.getId());
        this.sumDecimals = toClone.sumDecimals;
        if (toClone.getTableRecords() == null) this.tableRecords = null;
        else {
            this.setTableRecords(toClone.getTableRecords().stream()
                    .map(itm -> new Form(itm.getId()))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public TableField(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        //Sum Decimals...
        this.setSumDecimals(this.getAsBooleanNullSafe(this.jsonObject, JSONMapping.SUM_DECIMALS));

        //Table Field Records...
        this.setTableRecords(this.extractObjects(this.jsonObject, JSONMapping.TABLE_RECORDS, Form::new));
    }

    @XmlTransient
    @JsonIgnore
    public boolean isTableRecordsEmpty() {
        return (this.getTableRecords() == null || this.getTableRecords().isEmpty());
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code TableField}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        returnVal.addProperty(JSONMapping.SUM_DECIMALS, this.getSumDecimals());
        returnVal.add(JSONMapping.TABLE_RECORDS, this.toJsonObjArray(this.getTableRecords()));
        return returnVal;
    }


    /**
     * @return Cloned object from {@code this}
     */
    @XmlTransient
    @JsonIgnore
    @Override
    public TableField clone() {
        return new TableField(this);
    }

    /**
     * String value for a table field.
     *
     * @return JSON text from the table field.
     */
    @Override
    public String toString() {
        JsonObject jsonObject = this.toJsonObject();
        if (jsonObject != null) return jsonObject.toString();
        return UtilGlobal.EMPTY;
    }
}
