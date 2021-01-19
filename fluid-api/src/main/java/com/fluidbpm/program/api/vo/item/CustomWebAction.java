/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2021] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.item;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     Represents a {@code CustomWebAction} for executing custom web actions on the host.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.11
 *
 * @see Form
 */
@Getter
@Setter
public class CustomWebAction extends ABaseFluidJSONObject {
	public static final long serialVersionUID = 1L;

	private Form form;
	private String taskIdentifier;
	private Boolean isTableRecord;
	private Long formTableRecordBelongsTo;

	private Long executionTimeMillis;

	/**
	 * The JSON mapping for the {@code CustomWebAction} object.
	 */
	public static class JSONMapping {
		public static final String FORM = "form";
		public static final String TASK_IDENTIFIER = "taskIdentifier";
		public static final String IS_TABLE_RECORD = "isTableRecord";
		public static final String FORM_TABLE_RECORD_BELONGS_TO = "formTableRecordBelongsTo";
		public static final String EXECUTION_TIME_MILLIS = "executionTimeMillis";
	}

	/**
	 * Default constructor.
	 */
	public CustomWebAction() {
		super();
	}

	/**
	 * Constructor to create {@code CustomWebAction} with records.
	 *
	 * @param form The Form item to apply on custom action.
	 * @param taskIdentifier The unique task identifier to execute
	 */
	public CustomWebAction(Form form, String taskIdentifier) {
		super();
		this.setForm(form);
		this.setTaskIdentifier(taskIdentifier);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 * @param jsonObjectParam The JSON Object.
	 */
	public CustomWebAction(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.TASK_IDENTIFIER)) {
			this.setTaskIdentifier(this.jsonObject.getString(JSONMapping.TASK_IDENTIFIER));
		}

		if (!this.jsonObject.isNull(JSONMapping.EXECUTION_TIME_MILLIS)) {
			this.setExecutionTimeMillis(this.jsonObject.getLong(JSONMapping.EXECUTION_TIME_MILLIS));
		}

		if (!this.jsonObject.isNull(JSONMapping.FORM)) {
			this.setForm(new Form(this.jsonObject.getJSONObject(JSONMapping.FORM)));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code CustomWebAction}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();
		if (this.getTaskIdentifier() != null) {
			returnVal.put(JSONMapping.TASK_IDENTIFIER, this.getTaskIdentifier());
		}
		if (this.getExecutionTimeMillis() != null) {
			returnVal.put(JSONMapping.EXECUTION_TIME_MILLIS, this.getExecutionTimeMillis());
		}
		if (this.getForm() != null) {
			returnVal.put(JSONMapping.FORM, this.getForm().toJsonObject());
		}
		return returnVal;
	}

	/**
	 * String value for a table field.
	 * @return JSON text from the table field.
	 */
	@Override
	public String toString() {
		JSONObject jsonObject = this.toJsonObject();
		if (jsonObject != null) return jsonObject.toString();
		return UtilGlobal.EMPTY;
	}
}
