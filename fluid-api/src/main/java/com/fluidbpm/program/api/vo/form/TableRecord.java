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

package com.fluidbpm.program.api.vo.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;

/**
 * Represents an Fluid Table Record.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Form
 * @see Field
 */
@Getter
@Setter
@NoArgsConstructor
public class TableRecord extends ABaseFluidJSONObject {
	private static final long serialVersionUID = 1L;

	private Form formContainer;
	private Form parentFormContainer;
	private Field parentFormField;

	/**
	 * The JSON mapping for the {@code TableRecord} object.
	 */
	public static class JSONMapping {
		public static final String FORM_CONTAINER= "formContainer";
		public static final String PARENT_FORM_CONTAINER= "parentFormContainer";
		public static final String PARENT_FORM_FIELD = "parentFormField";
	}

	/**
	 * Sets the Id associated with a Field.
	 *
	 * @param tableRecordIdParam Field Id.
	 */
	public TableRecord(Long tableRecordIdParam) {
		super();
		this.setId(tableRecordIdParam);
	}

	/**
	 * Set {@code formContainerParam}, {@code parentFormContainer} and {@code parentFormField} to
	 * create a complete {@code TableRecord}.
	 * 
	 * @param formContainerParam The form container with the table record fields.
	 * @param parentFormContainer The containing form.
	 * @param parentFormField The Field the table record belongs to in the {@code formContainerParam}.
	 *
	 * @see Form
	 * @see Field
	 */
	public TableRecord(Form formContainerParam, Form parentFormContainer, Field parentFormField) {
		super();
		this.setFormContainer(formContainerParam);
		this.setParentFormContainer(parentFormContainer);
		this.setParentFormField(parentFormField);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public TableRecord(JsonObject jsonObjectParam) {
		super(jsonObjectParam);

		//Form Container...
		if (!this.jsonObject.isNull(JSONMapping.FORM_CONTAINER)) {
			this.setFormContainer(
					new Form(this.jsonObject.getJSONObject(JSONMapping.FORM_CONTAINER)));
		}

		//Parent Form Container...
		if (!this.jsonObject.isNull(JSONMapping.PARENT_FORM_CONTAINER)) {
			this.setParentFormContainer(
					new Form(this.jsonObject.getJSONObject(JSONMapping.PARENT_FORM_CONTAINER)));
		}

		//Parent Field...
		if (!this.jsonObject.isNull(JSONMapping.PARENT_FORM_FIELD)) {
			this.setParentFormField(new Field(this.jsonObject.getJSONObject(JSONMapping.PARENT_FORM_FIELD)));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code Field}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();
		//Form Container...
		if (this.getFormContainer() != null) {
			returnVal.put(JSONMapping.FORM_CONTAINER, this.getFormContainer().toJsonObject());
		}
		//Parent Form Container...
		if (this.getParentFormContainer() != null) {
			returnVal.put(JSONMapping.PARENT_FORM_CONTAINER, this.getParentFormContainer().toJsonObject());
		}
		//Parent Form Field...
		if (this.getParentFormField() != null) {
			returnVal.put(JSONMapping.PARENT_FORM_FIELD, this.getParentFormField().toJsonObject());
		}
		return returnVal;
	}
}
