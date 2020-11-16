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
import org.json.JSONException;
import org.json.JSONObject;

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
	private Boolean displayFormDescription = false;
	private Boolean displayFieldDescription = false;
	private Boolean displayAttachments = true;

	private int attachmentSize = 300;
	private int displayWidth = 450;
	private Integer displayHeight;

	private List<String> visibleSections;

	private List<String> additionalSectionOptions;
	private List<String> tableFieldsToInclude;

	//Workflow related props...
	private Boolean lockFormOnOpen = false;//Lock as the form is being opened...
	private Boolean unlockFormOnSave = false;//Unlock as the form is saved...
	private Boolean sendOnAfterSave = false;//Send on after save if in workflow...
	private Boolean sendToWorkflowAfterCreate = false;//Send to first workflow (if only 1)

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

		if (!this.jsonObject.isNull(JSONMapping.DISPLAY_ATTACHMENTS))
			this.setDisplayAttachments(this.jsonObject.getBoolean(JSONMapping.DISPLAY_ATTACHMENTS));

		if (!this.jsonObject.isNull(JSONMapping.DISPLAY_WIDTH))
			this.setDisplayWidth(this.jsonObject.getInt(JSONMapping.DISPLAY_WIDTH));
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

		public static final String DISPLAY_WIDTH = "displayWidth";
		public static final String DISPLAY_ATTACHMENTS = "displayAttachments";
		public static final String ATTACHMENT_SIZE = "attachmentSize";
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
		returnVal.put(JSONMapping.DISPLAY_FORM_DESCRIPTION, this.getDisplayFormDescription());
		returnVal.put(JSONMapping.DISPLAY_FIELD_DESCRIPTION, this.getDisplayFieldDescription());
		returnVal.put(JSONMapping.DISPLAY_ATTACHMENTS, this.getDisplayAttachments());
		returnVal.put(JSONMapping.ATTACHMENT_SIZE, this.getAttachmentSize());
		returnVal.put(JSONMapping.DISPLAY_WIDTH, this.getDisplayWidth());

		return returnVal;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
