/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2020] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.webkit;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * WebKit associated with {@code Form} definitions.
 *
 * @see com.fluidbpm.program.api.vo.form.Form
 */
@Getter
@Setter
public class WebKitForm extends ABaseFluidJSONObject {
	private Form form;
	private String inputLayout = InputLayout.VERTICAL;
	
	private boolean displayFormDescription;
	private boolean displayFieldDescription;

	//Attachment...
	private int attachmentSize = 300;
	private String attachmentDisplayLocation;//tab / bottom / top / none

	private int displayWidth = 450;
	private Integer displayHeight;

	private List<String> visibleSections;
	private String visibleSectionsDisplayBehaviour;//accordion / tab

	private List<String> additionalSectionOptions;
	private List<String> tableFieldsToInclude;

	//Workflow related props...
	private boolean lockFormOnOpen;//Lock as the form is being opened...
	private boolean unlockFormOnSave;//Unlock as the form is saved...
	private boolean sendOnAfterSave;//Send on after save if in workflow...
	private boolean sendToWorkflowAfterCreate;//Send to first workflow (if only 1)

	public static final String EMAIL_FORM_TYPE = "Email";
	public static WebKitForm emailWebKitForm() {
		WebKitForm webKitEmail = new WebKitForm(new JSONObject());
		return webKitEmail;
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitForm(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.FORM))
			this.setForm(new Form(this.jsonObject.getJSONObject(JSONMapping.FORM)));

		if (!this.jsonObject.isNull(JSONMapping.INPUT_LAYOUT))
			this.setInputLayout(this.jsonObject.getString(JSONMapping.INPUT_LAYOUT));

		if (!this.jsonObject.isNull(JSONMapping.DISPLAY_FORM_DESCRIPTION))
			this.setDisplayFormDescription(this.jsonObject.getBoolean(JSONMapping.DISPLAY_FORM_DESCRIPTION));

		if (!this.jsonObject.isNull(JSONMapping.DISPLAY_FIELD_DESCRIPTION))
			this.setDisplayFieldDescription(this.jsonObject.getBoolean(JSONMapping.DISPLAY_FIELD_DESCRIPTION));

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_SIZE))
			this.setAttachmentSize(this.jsonObject.getInt(JSONMapping.ATTACHMENT_SIZE));

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_DISPLAY_LOCATION))
			this.setAttachmentDisplayLocation(this.jsonObject.getString(JSONMapping.ATTACHMENT_DISPLAY_LOCATION));

		if (!this.jsonObject.isNull(JSONMapping.DISPLAY_WIDTH))
			this.setDisplayWidth(this.jsonObject.getInt(JSONMapping.DISPLAY_WIDTH));

		if (this.jsonObject.isNull(JSONMapping.DISPLAY_HEIGHT))
			this.setDisplayHeight(null);
		else
			this.setDisplayHeight(this.jsonObject.getInt(JSONMapping.DISPLAY_HEIGHT));

		this.setVisibleSections(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.VISIBLE_SECTIONS))
			this.jsonObject.getJSONArray(JSONMapping.VISIBLE_SECTIONS).forEach(
					section -> this.getVisibleSections().add(section.toString()));

		this.setTableFieldsToInclude(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.TABLE_FIELDS_TO_INCLUDE))
			this.jsonObject.getJSONArray(JSONMapping.TABLE_FIELDS_TO_INCLUDE).forEach(
					section -> this.getTableFieldsToInclude().add(section.toString()));

		if (!this.jsonObject.isNull(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR))
			this.setVisibleSectionsDisplayBehaviour(
					this.jsonObject.getString(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR));

		if (!this.jsonObject.isNull(JSONMapping.LOCK_FORM_ON_OPEN))
			this.setLockFormOnOpen(this.jsonObject.getBoolean(JSONMapping.LOCK_FORM_ON_OPEN));

		if (!this.jsonObject.isNull(JSONMapping.UNLOCK_FORM_ON_SAVE))
			this.setUnlockFormOnSave(this.jsonObject.getBoolean(JSONMapping.UNLOCK_FORM_ON_SAVE));

		if (!this.jsonObject.isNull(JSONMapping.SEND_ON_AFTER_SAVE))
			this.setSendOnAfterSave(this.jsonObject.getBoolean(JSONMapping.SEND_ON_AFTER_SAVE));

		if (!this.jsonObject.isNull(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE))
			this.setSendToWorkflowAfterCreate(
					this.jsonObject.getBoolean(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE));
	}

	/**
	 * The type of InputLayout.
	 */
	public static final class InputLayout {
		public static final String VERTICAL = "vertical";
		public static final String HORIZONTAL = "horizontal";
		public static final String ADVANCED = "advanced";
	}

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String FORM = "form";
		public static final String INPUT_LAYOUT = "inputLayout";

		public static final String DISPLAY_FORM_DESCRIPTION = "displayFormDescription";
		public static final String DISPLAY_FIELD_DESCRIPTION = "displayFieldDescription";

		public static final String ATTACHMENT_SIZE = "attachmentSize";
		public static final String ATTACHMENT_DISPLAY_LOCATION = "attachmentDisplayLocation";

		public static final String DISPLAY_WIDTH = "displayWidth";
		public static final String DISPLAY_HEIGHT = "displayHeight";

		public static final String VISIBLE_SECTIONS = "visibleSections";
		public static final String VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR = "visibleSectionsDisplayBehaviour";
		public static final String TABLE_FIELDS_TO_INCLUDE = "tableFieldsToInclude";
		
		public static final String LOCK_FORM_ON_OPEN = "lockFormOnOpen";
		public static final String UNLOCK_FORM_ON_SAVE = "unlockFormOnSave";
		public static final String SEND_ON_AFTER_SAVE = "sendOnAfterSave";
		public static final String SEND_TO_WORKFLOW_AFTER_CREATE = "sendToWorkflowAfterCreate";
	}

	/**
	 * <p>
	 * Base {@code toJsonObject} that creates a {@code JSONObject}
	 * with the Id and ServiceTicket set.
	 * </p>
	 *
	 * @return {@code JSONObject} representation of {@code ABaseFluidJSONObject}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see org.json.JSONObject
	 */
	@Override
	public JSONObject toJsonObject() {
		JSONObject returnVal = super.toJsonObject();

		if (this.getForm() != null) {
			Form reducedForm = new Form(this.getForm().getId());
			reducedForm.setTitle(this.getForm().getTitle());
			returnVal.put(JSONMapping.FORM, reducedForm.toJsonObject());
		}

		returnVal.put(JSONMapping.INPUT_LAYOUT, this.getInputLayout());
		returnVal.put(JSONMapping.DISPLAY_FORM_DESCRIPTION, this.isDisplayFormDescription());
		returnVal.put(JSONMapping.DISPLAY_FIELD_DESCRIPTION, this.isDisplayFieldDescription());
		returnVal.put(JSONMapping.ATTACHMENT_SIZE, this.getAttachmentSize());
		returnVal.put(JSONMapping.ATTACHMENT_DISPLAY_LOCATION, this.getAttachmentDisplayLocation());
		returnVal.put(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR, this.getVisibleSectionsDisplayBehaviour());
		returnVal.put(JSONMapping.DISPLAY_WIDTH, this.getDisplayWidth());

		if (this.getDisplayHeight() == null || this.getDisplayHeight() == 0) returnVal.put(JSONMapping.DISPLAY_HEIGHT, JSONObject.NULL);
		else returnVal.put(JSONMapping.DISPLAY_HEIGHT, this.getDisplayHeight());

		if (this.getVisibleSections() != null) {
			JSONArray visSections = new JSONArray();
			this.getVisibleSections().forEach(section -> visSections.put(section));
			returnVal.put(JSONMapping.VISIBLE_SECTIONS, visSections);
		}

		if (this.getTableFieldsToInclude() != null) {
			JSONArray tabFields = new JSONArray();
			this.getTableFieldsToInclude().forEach(tblField -> tabFields.put(tblField));
			returnVal.put(JSONMapping.TABLE_FIELDS_TO_INCLUDE, tabFields);
		}

		returnVal.put(JSONMapping.LOCK_FORM_ON_OPEN, this.isLockFormOnOpen());
		returnVal.put(JSONMapping.UNLOCK_FORM_ON_SAVE, this.isUnlockFormOnSave());
		returnVal.put(JSONMapping.SEND_ON_AFTER_SAVE, this.isSendOnAfterSave());
		returnVal.put(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE, this.isSendToWorkflowAfterCreate());

		return returnVal;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
