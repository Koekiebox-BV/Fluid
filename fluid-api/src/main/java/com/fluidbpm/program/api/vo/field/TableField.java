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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;

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
 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil
 */
public class TableField extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private List<Form> tableRecords;
	private Boolean sumDecimals;

	/**
	 * The JSON mapping for the {@code Form} object.
	 */
	public static class JSONMapping
	{
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
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public TableField(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null)
		{
			return;
		}

		//Sum Decimals...
		if (!this.jsonObject.isNull(JSONMapping.SUM_DECIMALS)) {

			this.setSumDecimals(this.jsonObject.getBoolean(JSONMapping.SUM_DECIMALS));
		}

		//Table Field Records...
		if (!this.jsonObject.isNull(JSONMapping.TABLE_RECORDS)) {

			JSONArray formsArr = this.jsonObject.getJSONArray(
					JSONMapping.TABLE_RECORDS);

			List<Form> assForms = new ArrayList();
			for (int index = 0;index < formsArr.length();index++)
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
	 * Set List of Table Records for {@code TableField}.
	 *
	 * @param tableRecordsParam {@code List} of {@code Form}s for {@code TableField}.
	 */
	public void setTableRecords(List<Form> tableRecordsParam) {
		this.tableRecords = tableRecordsParam;
	}

	/**
	 * Determine whether decimal columns should be summed.
	 *
	 * @return {@code Boolean} for Sum Decimals.
	 */
	public Boolean getSumDecimals() {
		return this.sumDecimals;
	}

	/**
	 * Set whether decimal columns should be summed.
	 *
	 * @param sumDecimalsParam Whether table record decimal columns should be summed.
	 */
	public void setSumDecimals(Boolean sumDecimalsParam) {
		this.sumDecimals = sumDecimalsParam;
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

		//Sum Decimals...
		if (this.getSumDecimals() != null)
		{
			returnVal.put(JSONMapping.SUM_DECIMALS, this.getSumDecimals());
		}

		//Table Field Records...
		if (this.getTableRecords() != null && !this.getTableRecords().isEmpty())
		{
			JSONArray assoFormsArr = new JSONArray();
			for (Form toAdd :this.getTableRecords())
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

		if (jsonObject != null)
		{
			return jsonObject.toString();
		}

		return UtilGlobal.EMPTY;
	}
}
