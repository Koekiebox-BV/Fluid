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

package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.util.UtilGlobal;

/**
 * <p>
 *     Represents a {@code Form} Table Field.
 *     Table fields have the ability to store other {@code Field}s inside itself.
 *     This allows for a much richer Electronic Form.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see com.fluid.program.api.util.sql.impl.SQLFormFieldUtil
 */
public class TableField extends ABaseFluidJSONObject {

    private List<Form> tableRecords;

    /**
     * The JSON mapping for the {@code Form} object.
     */
    public static class JSONMapping
    {
        public static final String TABLE_RECORDS = "tableRecords";
    }

    /**
     * Default constructor.
     */
    public TableField() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public TableField(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Table Field Records...
        if (!this.jsonObject.isNull(JSONMapping.TABLE_RECORDS)) {

            JSONArray formsArr = this.jsonObject.getJSONArray(
                    JSONMapping.TABLE_RECORDS);

            List<Form> assForms = new ArrayList();
            for(int index = 0;index < formsArr.length();index++)
            {
                assForms.add(new Form(formsArr.getJSONObject(index)));
            }

            this.setTableRecords(assForms);
        }
    }

    /**
     * Gets List of Table Records for {@code TableField}.
     *
     * @return {@code List} of {@code Form}s for {@code TableField}.
     */
    public List<Form> getTableRecords() {
        return this.tableRecords;
    }

    /**
     * Gets List of Table Records for {@code TableField}.
     *
     * @param tableRecordsParam {@code List} of {@code Form}s for {@code TableField}.
     */
    public void setTableRecords(List<Form> tableRecordsParam) {
        this.tableRecords = tableRecordsParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code TableField}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Table Field Records...
        if(this.getTableRecords() != null && !this.getTableRecords().isEmpty())
        {
            JSONArray assoFormsArr = new JSONArray();
            for(Form toAdd :this.getTableRecords())
            {
                assoFormsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.TABLE_RECORDS, assoFormsArr);
        }

        return returnVal;
    }

    /**
     * String value for a table field.
     *
     * @return JSON text from the table field.
     */
    @Override
    public String toString() {

        JSONObject jsonObject = this.toJsonObject();

        if(jsonObject != null)
        {
            return jsonObject.toString();
        }

        return UtilGlobal.EMPTY;
    }
}
