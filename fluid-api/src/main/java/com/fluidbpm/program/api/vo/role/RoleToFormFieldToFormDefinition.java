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

package com.fluidbpm.program.api.vo.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;

/**
 * <p>
 *     Represents the permission relationship between
 *     {@code Role} to {@code Field} and {@code Form}.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Form
 * @see Field
 * @see Role
 */
public class RoleToFormFieldToFormDefinition extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private FormFieldToFormDefinition formFieldToFormDefinition;

	private Boolean canView;
	private Boolean canCreateAndModify;

	/**
	 * The JSON mapping for the {@code RoleToFormFieldToFormDefinition} object.
	 */
	public static class JSONMapping {
		public static final String FORM_FIELD_TO_FORM_DEFINITION = "formFieldToFormDefinition";
		public static final String CAN_VIEW = "canView";
		public static final String CAN_CREATE_AND_MODIFY = "canCreateAndModify";
	}

	/**
	 * Default constructor.
	 */
	public RoleToFormFieldToFormDefinition() {
		super();
	}

	/**
	 * Sets the Id associated with a 'Form Field To Form Definition'.
	 *
	 * @param roleToFormFieldToFormDefinitionIdParam Field Id.
	 */
	public RoleToFormFieldToFormDefinition(Long roleToFormFieldToFormDefinitionIdParam) {
		super();

		this.setId(roleToFormFieldToFormDefinitionIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public RoleToFormFieldToFormDefinition(JsonObject jsonObjectParam){
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		//Form Definition...
		if (!this.jsonObject.isNull(JSONMapping.FORM_FIELD_TO_FORM_DEFINITION)) {
			this.setFormFieldToFormDefinition(
					new FormFieldToFormDefinition(this.jsonObject.getJSONObject(
							JSONMapping.FORM_FIELD_TO_FORM_DEFINITION)));
		}

		//Can View...
		if (!this.jsonObject.isNull(JSONMapping.CAN_VIEW)) {
			this.setCanView(this.jsonObject.getBoolean(JSONMapping.CAN_VIEW));
		}

		//Can Create and Modify...
		if (!this.jsonObject.isNull(JSONMapping.CAN_CREATE_AND_MODIFY)) {
			this.setCanCreateAndModify(this.jsonObject.getBoolean(
					JSONMapping.CAN_CREATE_AND_MODIFY));
		}
	}

	/**
	 * Gets the {@code FormFieldToFormDefinition} relationship.
	 *
	 * @return {@code FormFieldToFormDefinition}.
	 */
	public FormFieldToFormDefinition getFormFieldToFormDefinition() {
		return this.formFieldToFormDefinition;
	}

	/**
	 * Sets the {@code FormFieldToFormDefinition} relationship.
	 *
	 * @param formFieldToFormDefinitionParam {@code FormFieldToFormDefinition}.
	 */
	public void setFormFieldToFormDefinition(
			FormFieldToFormDefinition formFieldToFormDefinitionParam) {

		this.formFieldToFormDefinition = formFieldToFormDefinitionParam;
	}

	/**
	 * Gets whether a {@code Field} may be viewed.
	 *
	 * @return Whether the {@code Form}, {@code Field} and role
	 * relationship allows for viewing.
	 *
	 * @see Field
	 * @see Form
	 * @see Role
	 */
	public Boolean isCanView() {
		return this.canView;
	}

	/**
	 * Gets whether a {@code Field} may be viewed.
	 *
	 * @param canViewParam Whether the {@code Form}, {@code Field} and role
	 * relationship allows for viewing.
	 *
	 * @see Field
	 * @see Form
	 * @see Role
	 */
	public void setCanView(Boolean canViewParam) {
		this.canView = canViewParam;
	}

	/**
	 * Gets whether a {@code Field} may created and modified.
	 *
	 * @return Whether the {@code Form}, {@code Field} and role
	 * relationship allows for creation and modification.
	 *
	 * @see Field
	 * @see Form
	 * @see Role
	 */
	public Boolean isCanCreateAndModify() {
		return this.canCreateAndModify;
	}

	/**
	 * Gets whether a {@code Field} may created and modified.
	 *
	 * @param canCreateAndModifyParam Whether the {@code Form}, {@code Field} and role
	 * relationship allows for creation and modification.
	 *
	 * @see Field
	 * @see Form
	 * @see Role
	 */
	public void setCanCreateAndModify(Boolean canCreateAndModifyParam) {
		this.canCreateAndModify = canCreateAndModifyParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code RoleToFormFieldToFormDefinition}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		//Can Create...
		if (this.isCanCreateAndModify() != null) {
			returnVal.put(JSONMapping.CAN_CREATE_AND_MODIFY,
					this.isCanCreateAndModify().booleanValue());
		}

		//Can View...
		if (this.isCanView() != null) {
			returnVal.put(JSONMapping.CAN_VIEW,
					this.isCanView().booleanValue());
		}

		//Form Definition...
		if (this.getFormFieldToFormDefinition() != null) {
			returnVal.put(JSONMapping.FORM_FIELD_TO_FORM_DEFINITION,
					this.getFormFieldToFormDefinition().toJsonObject());
		}

		return returnVal;
	}
}
