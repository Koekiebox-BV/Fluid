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

package com.fluidbpm.program.api.vo.webkit.form;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.exception.UtilException;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
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
	private String inputLayout = InputLayout.VERTICAL;//vertical / advanced
	//The [webKitFormLayoutAdvances] is only applicable for [inputLayout] value 'advanced'.
	private List<WebKitFormLayoutAdvance> layoutAdvances;
	
	private boolean displayFormDescription;
	private boolean displayFieldDescription;

	//Attachment...
	private int attachmentSize = 300;
	private String attachmentDisplayLocation;//tab / bottom / top / none
	private String attachmentDisplayType = AttachmentDisplayType.GRID;//galleria / list / grid

	private int displayWidth = 900;
	private Integer displayHeight;

	private List<String> visibleSections;
	private String visibleSectionsDisplayBehaviour;//main / accordion / tab
	private String formDisplayBehaviour;//dialog / workspace

	private List<String> additionalSectionOptions;
	private List<String> tableFieldsToInclude;
	private List<String> mandatoryFields;
	private List<String> autoCompleteTextFields;
	private List<NewInstanceDefault> newInstanceDefaults;
	private List<String> userToFormFieldLimitOnMultiChoice;

	//Workflow related props...
	private boolean lockFormOnOpen;//Lock as the form is being opened...
	private boolean unlockFormOnSave;//Unlock as the form is saved...
	private boolean sendOnAfterSave;//Send on after save if in workflow...
	private boolean sendToWorkflowAfterCreate;//Send to first workflow (if only 1)

	private boolean enableCalculatedLabels;

	private String newFormTitleFormula;// string format|Name,Surname
	private String createNewInstanceIcon = "pi pi-file-o";
	private String createNewInstanceGroup = "Add New";

	private boolean enableFormFieldHistory;

	public static final String EMAIL_FORM_TYPE = "Email";
	public static final int DIALOG_WIDTH_MIN_AUTO = 19;
	public static final int DIALOG_HEIGHT_MIN_AUTO = 1;

	private static final String NONE = "[None]";
	
	/**
	 * The type of InputLayout.
	 */
	public static final class InputLayout {
		public static final String VERTICAL = "vertical";
		public static final String ADVANCED = "advanced";
	}

	/**
	 * The type of FormDisplayBehaviour.
	 */
	public static final class FormDisplayBehaviour {
		public static final String DIALOG = "dialog";
		public static final String WORKSPACE = "workspace";
	}

	/**
	 * The type of AttachmentDisplayLocation.
	 */
	public static final class AttachmentDisplayLocation {
		public static final String TAB = "tab";
		public static final String BOTTOM = "bottom";
		public static final String TOP = "top";
		public static final String NONE = "none";
	}

	/**
	 * The type of AttachmentDisplayType.
	 */
	public static final class AttachmentDisplayType {
		public static final String GALLERIA = "galleria";
		public static final String LIST = "list";
		public static final String GRID = "grid";
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
		public static final String ATTACHMENT_DISPLAY_TYPE = "attachmentDisplayType";

		public static final String DISPLAY_WIDTH = "displayWidth";
		public static final String DISPLAY_HEIGHT = "displayHeight";

		public static final String VISIBLE_SECTIONS = "visibleSections";
		public static final String VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR = "visibleSectionsDisplayBehaviour";
		public static final String FORM_DISPLAY_BEHAVIOUR = "formDisplayBehaviour";
		public static final String TABLE_FIELDS_TO_INCLUDE = "tableFieldsToInclude";
		public static final String MANDATORY_FIELDS = "mandatoryFields";
		public static final String AUTO_COMPLETE_TEXT_FIELDS = "autoCompleteTextFields";
		public static final String NEW_INSTANCE_DEFAULTS = "newInstanceDefaults";
		public static final String USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE = "userToFormFieldLimitOnMultiChoice";

		public static final String LOCK_FORM_ON_OPEN = "lockFormOnOpen";
		public static final String UNLOCK_FORM_ON_SAVE = "unlockFormOnSave";
		public static final String SEND_ON_AFTER_SAVE = "sendOnAfterSave";
		public static final String SEND_TO_WORKFLOW_AFTER_CREATE = "sendToWorkflowAfterCreate";
		public static final String NEW_FORM_TITLE_FORMULA = "newFormTitleFormula";
		public static final String CREATE_NEW_INSTANCE_ICON = "createNewInstanceIcon";
		public static final String CREATE_NEW_INSTANCE_GROUP = "createNewInstanceGroup";

		public static final String ENABLE_CALCULATED_LABELS = "enableCalculatedLabels";
		public static final String ENABLE_FORM_FIELD_HISTORY = "enableFormFieldHistory";
		public static final String LAYOUT_ADVANCES = "layoutAdvances";
	}

	/**
	 * Email WebKit form.
	 * @return {@code WebKitForm} for Email form type.
	 */
	public static WebKitForm emailWebKitForm() {
		return new WebKitForm(new JSONObject());
	}

	/**
	 * Default.
	 */
	public WebKitForm() {
		this(new JSONObject());
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObject The JSON Object.
	 */
	public WebKitForm(JsonObject jsonObject) {
		super(jsonObject);
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

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_DISPLAY_TYPE))
			this.setAttachmentDisplayType(this.jsonObject.getString(JSONMapping.ATTACHMENT_DISPLAY_TYPE));

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

		this.setMandatoryFields(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.MANDATORY_FIELDS))
			this.jsonObject.getJSONArray(JSONMapping.MANDATORY_FIELDS).forEach(
					manField -> this.getMandatoryFields().add(manField.toString()));

		this.setAutoCompleteTextFields(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.AUTO_COMPLETE_TEXT_FIELDS))
			this.jsonObject.getJSONArray(JSONMapping.AUTO_COMPLETE_TEXT_FIELDS).forEach(
					autoCompl -> this.getAutoCompleteTextFields().add(autoCompl.toString())
			);

		this.setNewInstanceDefaults(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.NEW_INSTANCE_DEFAULTS))
			this.jsonObject.getJSONArray(JSONMapping.NEW_INSTANCE_DEFAULTS).forEach(object -> {
				if (object instanceof JSONObject) {
					this.getNewInstanceDefaults().add(new NewInstanceDefault((JSONObject)object));
				}
			});

		this.setUserToFormFieldLimitOnMultiChoice(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE))
			this.jsonObject.getJSONArray(JSONMapping.USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE).forEach(
					manField -> this.getUserToFormFieldLimitOnMultiChoice().add(manField.toString()));

		if (!this.jsonObject.isNull(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR))
			this.setVisibleSectionsDisplayBehaviour(this.jsonObject.getString(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR));

		if (!this.jsonObject.isNull(JSONMapping.FORM_DISPLAY_BEHAVIOUR))
			this.setFormDisplayBehaviour(this.jsonObject.getString(JSONMapping.FORM_DISPLAY_BEHAVIOUR));

		if (!this.jsonObject.isNull(JSONMapping.LOCK_FORM_ON_OPEN))
			this.setLockFormOnOpen(this.jsonObject.getBoolean(JSONMapping.LOCK_FORM_ON_OPEN));

		if (!this.jsonObject.isNull(JSONMapping.UNLOCK_FORM_ON_SAVE))
			this.setUnlockFormOnSave(this.jsonObject.getBoolean(JSONMapping.UNLOCK_FORM_ON_SAVE));

		if (!this.jsonObject.isNull(JSONMapping.SEND_ON_AFTER_SAVE))
			this.setSendOnAfterSave(this.jsonObject.getBoolean(JSONMapping.SEND_ON_AFTER_SAVE));

		if (!this.jsonObject.isNull(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE))
			this.setSendToWorkflowAfterCreate(this.jsonObject.getBoolean(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE));

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_CALCULATED_LABELS))
			this.setEnableCalculatedLabels(this.jsonObject.getBoolean(JSONMapping.ENABLE_CALCULATED_LABELS));

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_FORM_FIELD_HISTORY))
			this.setEnableFormFieldHistory(this.jsonObject.getBoolean(JSONMapping.ENABLE_FORM_FIELD_HISTORY));

		if (!this.jsonObject.isNull(JSONMapping.NEW_FORM_TITLE_FORMULA))
			this.setNewFormTitleFormula(this.jsonObject.getString(JSONMapping.NEW_FORM_TITLE_FORMULA));

		if (!this.jsonObject.isNull(JSONMapping.CREATE_NEW_INSTANCE_ICON))
			this.setCreateNewInstanceIcon(this.jsonObject.getString(JSONMapping.CREATE_NEW_INSTANCE_ICON));

		if (!this.jsonObject.isNull(JSONMapping.CREATE_NEW_INSTANCE_GROUP))
			this.setCreateNewInstanceGroup(this.jsonObject.getString(JSONMapping.CREATE_NEW_INSTANCE_GROUP));

		this.setLayoutAdvances(new ArrayList<>());
		if (!this.jsonObject.isNull(JSONMapping.LAYOUT_ADVANCES)) {
			JsonArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.LAYOUT_ADVANCES);
			List<WebKitFormLayoutAdvance> objs = new ArrayList();
			for (int index = 0; index < jsonArray.length(); index++) {
				objs.add(new WebKitFormLayoutAdvance(jsonArray.getJSONObject(index)));
			}
			this.setLayoutAdvances(objs);
		}
	}

	/**
	 * Set the WebKit txt as well as the {@code Form}.
	 * @param jsonObject The WebKit JSONObject.
	 * @param form The Fluid {@code Form}.
	 */
	public WebKitForm(JsonObject jsonObject, Form form) {
		this(jsonObject);
		this.setForm(form);
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
	@XmlTransient
	public JsonObject toJsonObject() {
		JsonObject returnVal = super.toJsonObject();

		if (this.getForm() != null) {
			if (this.jsonIncludeAll) {
				returnVal.put(JSONMapping.FORM, this.getForm().toJsonObject());
			} else {
				Form reducedForm = new Form(this.getForm().getId());
				reducedForm.setFormType(this.getForm().getFormType());
				reducedForm.setFormTypeId(this.getForm().getFormTypeId());
				returnVal.put(JSONMapping.FORM, reducedForm.toJsonObject());
			}
		}

		returnVal.put(JSONMapping.INPUT_LAYOUT, this.getInputLayout());
		returnVal.put(JSONMapping.DISPLAY_FORM_DESCRIPTION, this.isDisplayFormDescription());
		returnVal.put(JSONMapping.DISPLAY_FIELD_DESCRIPTION, this.isDisplayFieldDescription());
		returnVal.put(JSONMapping.ATTACHMENT_SIZE, this.getAttachmentSize());
		returnVal.put(JSONMapping.ATTACHMENT_DISPLAY_LOCATION, this.getAttachmentDisplayLocation());
		returnVal.put(JSONMapping.ATTACHMENT_DISPLAY_TYPE, this.getAttachmentDisplayType());
		returnVal.put(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR, this.getVisibleSectionsDisplayBehaviour());
		returnVal.put(JSONMapping.FORM_DISPLAY_BEHAVIOUR, this.getFormDisplayBehaviour());
		returnVal.put(JSONMapping.DISPLAY_WIDTH, this.getDisplayWidth());

		if (this.getDisplayHeight() == null || this.getDisplayHeight() == 0) returnVal.put(JSONMapping.DISPLAY_HEIGHT, JSONObject.NULL);
		else returnVal.put(JSONMapping.DISPLAY_HEIGHT, this.getDisplayHeight());

		if (this.getNewFormTitleFormula() == null || this.getNewFormTitleFormula().isEmpty()) returnVal.put(JSONMapping.NEW_FORM_TITLE_FORMULA, JSONObject.NULL);
		else returnVal.put(JSONMapping.NEW_FORM_TITLE_FORMULA, this.getNewFormTitleFormula());

		if (this.getCreateNewInstanceIcon() == null || this.getCreateNewInstanceIcon().isEmpty()) returnVal.put(JSONMapping.CREATE_NEW_INSTANCE_ICON, JSONObject.NULL);
		else returnVal.put(JSONMapping.CREATE_NEW_INSTANCE_ICON, this.getCreateNewInstanceIcon());

		if (this.getCreateNewInstanceGroup() == null || this.getCreateNewInstanceGroup().isEmpty()) returnVal.put(JSONMapping.CREATE_NEW_INSTANCE_GROUP, JSONObject.NULL);
		else returnVal.put(JSONMapping.CREATE_NEW_INSTANCE_GROUP, this.getCreateNewInstanceGroup());

		if (this.getVisibleSections() != null) {
			JsonArray visSections = new JsonArray();
			this.getVisibleSections().forEach(section -> visSections.put(section));
			returnVal.put(JSONMapping.VISIBLE_SECTIONS, visSections);
		}

		if (this.getTableFieldsToInclude() != null) {
			JsonArray tabFields = new JsonArray();
			this.getTableFieldsToInclude().forEach(tblField -> tabFields.put(tblField));
			returnVal.put(JSONMapping.TABLE_FIELDS_TO_INCLUDE, tabFields);
		}

		if (this.getMandatoryFields() != null) {
			JsonArray arr = new JsonArray();
			this.getMandatoryFields().forEach(manField -> arr.put(manField));
			returnVal.put(JSONMapping.MANDATORY_FIELDS, arr);
		}

		if (this.getAutoCompleteTextFields() != null) {
			JsonArray arr = new JsonArray();
			this.getAutoCompleteTextFields().forEach(itm -> arr.put(itm));
			returnVal.put(JSONMapping.AUTO_COMPLETE_TEXT_FIELDS, arr);
		}

		if (this.getNewInstanceDefaults() != null) {
			JsonArray newInstDef = new JsonArray();
			this.getNewInstanceDefaults()
					.stream()
					.filter(itm -> UtilGlobal.isNotBlank(itm.getDefaultVal()))
					.forEach(defField -> newInstDef.put(defField.toJsonObject()));
			returnVal.put(JSONMapping.NEW_INSTANCE_DEFAULTS, newInstDef);
		}

		if (this.getUserToFormFieldLimitOnMultiChoice() != null) {
			JsonArray userToFormFields = new JsonArray();
			this.getUserToFormFieldLimitOnMultiChoice().forEach(manField -> userToFormFields.put(manField));
			returnVal.put(JSONMapping.USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE, userToFormFields);
		}

		JsonArray arrAdvances = new JsonArray();
		if (this.getLayoutAdvances() != null) {
			for (WebKitFormLayoutAdvance toAdd : this.getLayoutAdvances()) arrAdvances.put(toAdd.toJsonObject());
		}
		returnVal.put(JSONMapping.LAYOUT_ADVANCES, arrAdvances);

		returnVal.put(JSONMapping.LOCK_FORM_ON_OPEN, this.isLockFormOnOpen());
		returnVal.put(JSONMapping.UNLOCK_FORM_ON_SAVE, this.isUnlockFormOnSave());
		returnVal.put(JSONMapping.SEND_ON_AFTER_SAVE, this.isSendOnAfterSave());
		returnVal.put(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE, this.isSendToWorkflowAfterCreate());

		returnVal.put(JSONMapping.ENABLE_CALCULATED_LABELS, this.isEnableCalculatedLabels());
		returnVal.put(JSONMapping.ENABLE_FORM_FIELD_HISTORY, this.isEnableFormFieldHistory());

		return returnVal;
	}

	/**
	 * Verify if any table forms are included.
	 * @return {@code true} if table forms are included.
	 */
	@XmlTransient
	public boolean isAnyTableFormsEnabled() {
		return this.tableFieldsToInclude != null && !this.tableFieldsToInclude.isEmpty();
	}

	/**
	 * Validate whether the {@code newFormTitleFormula} is correctly formatted.
	 * @throws UtilException if format invalid.
	 */
	@XmlTransient
	public void validateAndFormatNewFormFormula() {
		if (UtilGlobal.isBlank(this.newFormTitleFormula)) return;

		int indexOfPipe = this.newFormTitleFormula.lastIndexOf("|");
		if (indexOfPipe < 0) throw new UtilException(
				"No [|] pipe for style and field separation detected.", UtilException.ErrorCode.GENERAL);
		this.newFormTitleFormula = this.newFormTitleFormula.trim();
	}

	/**
	 * Verify whether a field should be included.
	 * 
	 * @param tableFieldName The name of the field to verify whether included.
	 * @return {@code true} If field {@code tableFieldName} is included, otherwise {@code false}.
	 */
	@XmlTransient
	public boolean includeTableField(String tableFieldName) {
		if (this.getTableFieldsToInclude() == null) return false;
		return this.getTableFieldsToInclude().contains(tableFieldName);
	}

	/**
	 * Verify whether a field is mandatory.
	 *
	 * @param fieldName The name of the field to verify whether mandatory.
	 * @return {@code true} If field {@code fieldName} is mandatory, otherwise {@code false}.
	 */
	@XmlTransient
	public boolean fieldMandatory(String fieldName) {
		if (this.getMandatoryFields() == null) return false;
		return this.getMandatoryFields().contains(fieldName);
	}

	/**
	 * Indicate whether the attachment type is {@code none}
	 *
	 * @return {@code true} If field {@code fieldName} is mandatory, otherwise {@code false}.
	 */
	@XmlTransient
	public boolean isAttachmentDisplayLocationNone() {
		if (this.getAttachmentDisplayLocation() == null) return true;

		return this.getAttachmentDisplayLocation().equalsIgnoreCase("none");
	}

	/**
	 * Indicate whether the [inputLayout] is {@code advanced}.
	 *
	 * @return {@code true} If [inputLayout] is {@code advanced}, otherwise {@code false}.
	 */
	@XmlTransient
	public boolean isInputLayoutAdvanced() {
		if (this.getInputLayout() == null) return false;

		return this.getInputLayout().equalsIgnoreCase(InputLayout.ADVANCED);
	}

	/**
	 * Locates the {@code WebKitFormLayoutAdvance} based on {@code field#getFieldName}.
	 *
	 * @param field The field to retrieve the advanced properties for.
	 * @return {@code WebKitFormLayoutAdvance} for field {@code field}.
	 */
	@XmlTransient
	public WebKitFormLayoutAdvance retrieveLayoutAdvanceForField(Field field) {
		if (this.getLayoutAdvances() == null) return null;

		String name = field.getFieldName();
		if (UtilGlobal.isBlank(name)) return null;

		return this.getLayoutAdvances().stream()
				.filter(itm -> itm.getField() != null)
				.filter(itm -> name.equalsIgnoreCase(itm.getField().getFieldName()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Text representation for advanced layout properties.
	 * 
	 * @return {@code String} for {@code layoutAdvances}.
	 */
	@XmlTransient
	public String getToStringLayoutAdvances() {
		if (this.getLayoutAdvances() == null || this.getLayoutAdvances().isEmpty()) return NONE;

		StringBuilder returnVal = new StringBuilder();
		this.getLayoutAdvances().stream()
				.forEach(itm -> {
					returnVal.append(String.format("'%s' = %d\n", itm.getField().getFieldName(), itm.getColSpan()));
				});
		return returnVal.toString();
	}

	/**
	 * Text representation for mandatory fields.
	 *
	 * @return {@code String} for {@code mandatoryFields}.
	 */
	@XmlTransient
	public String getToStringMandatoryFields() {
		if (this.getMandatoryFields() == null || this.getMandatoryFields().isEmpty()) return NONE;

		StringBuilder returnVal = new StringBuilder();
		this.getMandatoryFields().forEach(itm -> returnVal.append(String.format("%s\n", itm)));
		return returnVal.toString();
	}

	/**
	 * Text representation for auto complete text fields.
	 *
	 * @return {@code String} for {@code autoCompleteTextFields}.
	 */
	@XmlTransient
	public String getToStringAutoCompleteTextFields() {
		if (this.getAutoCompleteTextFields() == null || this.getAutoCompleteTextFields().isEmpty()) return NONE;

		StringBuilder returnVal = new StringBuilder();
		this.getAutoCompleteTextFields().forEach(itm -> returnVal.append(String.format("%s\n", itm)));
		return returnVal.toString();
	}

	/**
	 * Text representation for User to Form mapping fields.
	 *
	 * @return {@code String} for {@code userToFormFieldLimitOnMultiChoice}.
	 */
	@XmlTransient
	public String getToStringUserToFormFields() {
		if (this.getUserToFormFieldLimitOnMultiChoice() == null ||
				this.getUserToFormFieldLimitOnMultiChoice().isEmpty()) return NONE;

		StringBuilder returnVal = new StringBuilder();
		this.getMandatoryFields().stream()
				.forEach(itm -> returnVal.append(String.format("%s\n", itm)));
		return returnVal.toString();
	}
	
	/**
	 * {@code String} representation of {@code this} object.
	 *
	 * @return {@code super #toString}
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
