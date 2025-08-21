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
import com.fluidbpm.program.api.vo.form.Form;

/**
 * <p>
 *     Represents what a {@code Role} permits a {@code User} to do
 *     with a {@code Form} Definition type.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Form
 * @see Role
 */
public class RoleToFormDefinition extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private Form formDefinition;

	private Boolean canCreate;
	private Boolean attachmentsCreateUpdate;
	private Boolean attachmentsView;

	/**
	 * The JSON mapping for the {@code RoleToFormDefinition} object.
	 */
	public static class JSONMapping
	{
		public static final String FORM_DEFINITION = "formDefinition";

		public static final String CAN_CREATE = "canCreate";
		public static final String ATTACHMENTS_CREATE_UPDATE = "attachmentsCreateUpdate";
		public static final String ATTACHMENTS_VIEW = "attachmentsView";
	}

	/**
	 * Default constructor.
	 */
	public RoleToFormDefinition() {
		super();
	}

	/**
	 * Sets the Id associated with a 'Role To Form Definition'.
	 *
	 * @param roleToFormDefinitionIdParam RoleToFormDefinition Id.
	 */
	public RoleToFormDefinition(Long roleToFormDefinitionIdParam) {
		super();

		this.setId(roleToFormDefinitionIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public RoleToFormDefinition(JSONObject jsonObjectParam){
		super(jsonObjectParam);

		if (this.jsonObject == null)
		{
			return;
		}

		//Form Definition...
		if (!this.jsonObject.isNull(JSONMapping.FORM_DEFINITION)) {
			this.setFormDefinition(new Form(this.jsonObject.getJSONObject(
					JSONMapping.FORM_DEFINITION)));
		}

		//Can Create...
		if (!this.jsonObject.isNull(JSONMapping.CAN_CREATE)) {
			this.setCanCreate(this.jsonObject.getBoolean(JSONMapping.CAN_CREATE));
		}

		//Attachment View...
		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENTS_VIEW)) {
			this.setAttachmentsView(
					this.jsonObject.getBoolean(JSONMapping.ATTACHMENTS_VIEW));
		}

		//Attachment Create and Modification...
		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENTS_CREATE_UPDATE)) {
			this.setAttachmentsCreateUpdate(
					this.jsonObject.getBoolean(JSONMapping.ATTACHMENTS_CREATE_UPDATE));
		}
	}

	/**
	 * Gets the {@code Form} Definition associated with the
	 * {@code Role} to Form Definition permission.
	 *
	 * @return {@code Form} Definition.
	 */
	public Form getFormDefinition() {
		return this.formDefinition;
	}

	/**
	 * Sets the {@code Form} Definition associated with the
	 * {@code Role} to Form Definition permission.
	 *
	 * @param formDefinitionParam {@code Form} Definition.
	 */
	public void setFormDefinition(Form formDefinitionParam) {
		this.formDefinition = formDefinitionParam;
	}

	/**
	 * Gets whether the {@code Role} allow for {@code Form} creation.
	 *
	 * @return Does the {@code Role} allow for {@code Form} creation.
	 */
	public Boolean isCanCreate() {
		return this.canCreate;
	}

	/**
	 * Sets whether the {@code Role} allow for {@code Form} creation.
	 *
	 * @param canCreateParam Does the {@code Role} allow for {@code Form} creation.
	 */
	public void setCanCreate(Boolean canCreateParam) {
		this.canCreate = canCreateParam;
	}

	/**
	 * Gets whether the {@code Role} allow for creating or modifying
	 * {@code Form} attachments.
	 *
	 * @return Does the {@code Role} allow for {@code Form} attachment create
	 * or modification.
	 */
	public Boolean isAttachmentsCreateUpdate() {
		return this.attachmentsCreateUpdate;
	}

	/**
	 * Sets whether the {@code Role} allow for {@code Form} attachment
	 * creation or modification.
	 *
	 * @param attachmentsCreateUpdateParam Does the {@code Role} allow for {@code Form} attachment
	 *                                     creation or modification.
	 */
	public void setAttachmentsCreateUpdate(Boolean attachmentsCreateUpdateParam) {
		this.attachmentsCreateUpdate = attachmentsCreateUpdateParam;
	}

	/**
	 * Gets whether the {@code Role} allow for viewing {@code Form} attachments.
	 *
	 * @return Does the {@code Role} allow for {@code Form} attachment view.
	 */
	public Boolean isAttachmentsView() {
		return this.attachmentsView;
	}

	/**
	 * Sets whether the {@code Role} allow for {@code Form} attachment viewing.
	 *
	 * @param attachmentsViewParam Does the {@code Role} allow for
	 * {@code Form} attachment viewing.
	 */
	public void setAttachmentsView(Boolean attachmentsViewParam) {
		this.attachmentsView = attachmentsViewParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code RoleToFormDefinition}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {

		JsonObject returnVal = super.toJsonObject();

		//Can Create...
		if (this.isCanCreate() != null)
		{
			returnVal.put(JSONMapping.CAN_CREATE,
					this.isCanCreate().booleanValue());
		}

		//Attachment can View...
		if (this.isAttachmentsView() != null)
		{
			returnVal.put(JSONMapping.ATTACHMENTS_VIEW,
					this.isAttachmentsView().booleanValue());
		}

		//Attachment can Create or Modify...
		if (this.isAttachmentsCreateUpdate() != null)
		{
			returnVal.put(JSONMapping.ATTACHMENTS_CREATE_UPDATE,
					this.isAttachmentsCreateUpdate().booleanValue());
		}

		//Form Definition...
		if (this.getFormDefinition() != null)
		{
			returnVal.put(JSONMapping.FORM_DEFINITION,
					this.getFormDefinition().toJsonObject());
		}

		return returnVal;
	}
}
