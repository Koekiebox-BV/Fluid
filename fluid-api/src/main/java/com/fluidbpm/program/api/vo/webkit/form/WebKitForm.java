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
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

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
public class WebKitForm extends ABaseFluidGSONObject {
    private static final String DEF_GROUP = "Add New";
    private static final String DEF_NEW_INSTANCE = "pi pi-file-o";

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
    private String createNewInstanceIcon = DEF_NEW_INSTANCE;
    private String createNewInstanceGroup = DEF_GROUP;

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
     *
     * @return {@code WebKitForm} for Email form type.
     */
    public static WebKitForm emailWebKitForm() {
        return new WebKitForm(new JsonObject());
    }

    /**
     * Default.
     */
    public WebKitForm() {
        this(new JsonObject());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitForm(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        // For Form object, we need to handle it specially since it's a complex object
        if (this.jsonObject.has(JSONMapping.FORM) && !this.jsonObject.get(JSONMapping.FORM).isJsonNull()) {
            JsonObject formJsonObj = this.jsonObject.getAsJsonObject(JSONMapping.FORM);
            Form form = new Form(
                    formJsonObj.has(ABaseFluidGSONObject.JSONMapping.ID) &&
                            !formJsonObj.get(ABaseFluidGSONObject.JSONMapping.ID).isJsonNull() ?
                            formJsonObj.get(ABaseFluidGSONObject.JSONMapping.ID).getAsLong() : null
            );
            if (formJsonObj.has(Form.JSONMapping.FORM_TYPE_ID) && !formJsonObj.get(Form.JSONMapping.FORM_TYPE_ID).isJsonNull()) {
                form.setFormTypeId(formJsonObj.get(Form.JSONMapping.FORM_TYPE_ID).getAsLong());
            }
            if (formJsonObj.has(Form.JSONMapping.FORM_TYPE) && !formJsonObj.get(Form.JSONMapping.FORM_TYPE).isJsonNull()) {
                form.setFormType(formJsonObj.get(Form.JSONMapping.FORM_TYPE).getAsString());
            }
            this.setForm(form);
        }

        // Basic properties
        this.setInputLayout(this.getAsStringNullSafe(JSONMapping.INPUT_LAYOUT));
        this.setDisplayFormDescription(this.getAsBooleanNullSafe(JSONMapping.DISPLAY_FORM_DESCRIPTION));
        this.setDisplayFieldDescription(this.getAsBooleanNullSafe(JSONMapping.DISPLAY_FIELD_DESCRIPTION));
        this.setAttachmentSize(this.getAsIntegerNullSafeStrictVal(JSONMapping.ATTACHMENT_SIZE));
        this.setAttachmentDisplayLocation(this.getAsStringNullSafe(JSONMapping.ATTACHMENT_DISPLAY_LOCATION));
        this.setAttachmentDisplayType(this.getAsStringNullSafe(JSONMapping.ATTACHMENT_DISPLAY_TYPE));
        this.setDisplayWidth(this.getAsIntegerNullSafeStrictVal(JSONMapping.DISPLAY_WIDTH));
        this.setDisplayHeight(this.getAsIntegerNullSafe(JSONMapping.DISPLAY_HEIGHT));
        this.setVisibleSectionsDisplayBehaviour(this.getAsStringNullSafe(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR));
        this.setFormDisplayBehaviour(this.getAsStringNullSafe(JSONMapping.FORM_DISPLAY_BEHAVIOUR));
        this.setLockFormOnOpen(this.getAsBooleanNullSafe(JSONMapping.LOCK_FORM_ON_OPEN));
        this.setUnlockFormOnSave(this.getAsBooleanNullSafe(JSONMapping.UNLOCK_FORM_ON_SAVE));
        this.setSendOnAfterSave(this.getAsBooleanNullSafe(JSONMapping.SEND_ON_AFTER_SAVE));
        this.setSendToWorkflowAfterCreate(this.getAsBooleanNullSafe(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE));
        this.setEnableCalculatedLabels(this.getAsBooleanNullSafe(JSONMapping.ENABLE_CALCULATED_LABELS));
        this.setEnableFormFieldHistory(this.getAsBooleanNullSafe(JSONMapping.ENABLE_FORM_FIELD_HISTORY));
        this.setNewFormTitleFormula(this.getAsStringNullSafe(JSONMapping.NEW_FORM_TITLE_FORMULA));
        this.setCreateNewInstanceIcon(this.getAsStringNullSafe(JSONMapping.CREATE_NEW_INSTANCE_ICON));
        this.setCreateNewInstanceGroup(this.getAsStringNullSafe(JSONMapping.CREATE_NEW_INSTANCE_GROUP));

        // String arrays
        this.setVisibleSections(this.extractStrings(JSONMapping.VISIBLE_SECTIONS));
        this.setTableFieldsToInclude(this.extractStrings(JSONMapping.TABLE_FIELDS_TO_INCLUDE));
        this.setMandatoryFields(this.extractStrings(JSONMapping.MANDATORY_FIELDS));
        this.setAutoCompleteTextFields(this.extractStrings(JSONMapping.AUTO_COMPLETE_TEXT_FIELDS));
        this.setUserToFormFieldLimitOnMultiChoice(this.extractStrings(JSONMapping.USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE));

        // Handle NewInstanceDefault objects
        this.setNewInstanceDefaults(new ArrayList<>());
        if (this.jsonObject.has(JSONMapping.NEW_INSTANCE_DEFAULTS) && 
            !this.jsonObject.get(JSONMapping.NEW_INSTANCE_DEFAULTS).isJsonNull() &&
            this.jsonObject.get(JSONMapping.NEW_INSTANCE_DEFAULTS).isJsonArray()) {
            
            this.jsonObject.getAsJsonArray(JSONMapping.NEW_INSTANCE_DEFAULTS).forEach(element -> {
                if (element.isJsonObject()) {
                    this.getNewInstanceDefaults().add(new NewInstanceDefault(element.getAsJsonObject()));
                }
            });
        }
        
        // Handle WebKitFormLayoutAdvance objects
        this.setLayoutAdvances(this.extractObjects(JSONMapping.LAYOUT_ADVANCES, WebKitFormLayoutAdvance::new));
    }

    /**
     * Set the WebKit txt as well as the {@code Form}.
     *
     * @param jsonObject The WebKit JSONObject.
     * @param form       The Fluid {@code Form}.
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
     * @return {@code JSONObject} representation of {@code ABaseFluidGSONObject}
     * @see com.google.gson.JsonObject
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        if (this.getForm() != null) {
            if (this.jsonIncludeAll) {
                this.setAsObj(JSONMapping.FORM, returnVal, this::getForm);
            } else {
                Form reducedForm = new Form(this.getForm().getId());
                reducedForm.setFormType(this.getForm().getFormType());
                reducedForm.setFormTypeId(this.getForm().getFormTypeId());
                returnVal.add(JSONMapping.FORM, reducedForm.toJsonObject());
            }
        }

        this.setAsProperty(JSONMapping.INPUT_LAYOUT, returnVal, this.getInputLayout());
        this.setAsProperty(JSONMapping.DISPLAY_FORM_DESCRIPTION, returnVal, this.isDisplayFormDescription());
        this.setAsProperty(JSONMapping.DISPLAY_FIELD_DESCRIPTION, returnVal, this.isDisplayFieldDescription());
        this.setAsProperty(JSONMapping.ATTACHMENT_SIZE, returnVal, this.getAttachmentSize());
        this.setAsProperty(JSONMapping.ATTACHMENT_DISPLAY_LOCATION, returnVal, this.getAttachmentDisplayLocation());
        this.setAsProperty(JSONMapping.ATTACHMENT_DISPLAY_TYPE, returnVal, this.getAttachmentDisplayType());
        this.setAsProperty(JSONMapping.VISIBLE_SECTIONS_DISPLAY_BEHAVIOUR, returnVal, this.getVisibleSectionsDisplayBehaviour());
        this.setAsProperty(JSONMapping.FORM_DISPLAY_BEHAVIOUR, returnVal, this.getFormDisplayBehaviour());
        this.setAsProperty(JSONMapping.DISPLAY_WIDTH, returnVal, this.getDisplayWidth());
        this.setAsProperty(JSONMapping.DISPLAY_HEIGHT, returnVal, this.getDisplayHeight());
        this.setAsProperty(JSONMapping.NEW_FORM_TITLE_FORMULA, returnVal, this.getNewFormTitleFormula());
        this.setAsProperty(JSONMapping.CREATE_NEW_INSTANCE_ICON, returnVal, this.getCreateNewInstanceIcon());
        this.setAsProperty(JSONMapping.CREATE_NEW_INSTANCE_GROUP, returnVal, this.getCreateNewInstanceGroup());

        this.setAsStringArray(JSONMapping.VISIBLE_SECTIONS, returnVal, this.getVisibleSections());
        this.setAsStringArray(JSONMapping.TABLE_FIELDS_TO_INCLUDE, returnVal, this.getTableFieldsToInclude());
        this.setAsStringArray(JSONMapping.MANDATORY_FIELDS, returnVal, this.getMandatoryFields());
        this.setAsStringArray(JSONMapping.AUTO_COMPLETE_TEXT_FIELDS, returnVal, this.getAutoCompleteTextFields());
        this.setAsStringArray(JSONMapping.USER_TO_FORM_FIELD_LIMIT_ON_MULTI_CHOICE, returnVal, this.getUserToFormFieldLimitOnMultiChoice());

        if (this.getNewInstanceDefaults() != null) {
            JsonArray newInstDef = new JsonArray();
            this.getNewInstanceDefaults()
                    .stream()
                    .filter(itm -> UtilGlobal.isNotBlank(itm.getDefaultVal()))
                    .forEach(defField -> newInstDef.add(defField.toJsonObject()));
            returnVal.add(JSONMapping.NEW_INSTANCE_DEFAULTS, newInstDef);
        }

        // Use setAsObjArray for layout advances
        this.setAsObjArray(JSONMapping.LAYOUT_ADVANCES, returnVal, this::getLayoutAdvances);

        // Set boolean properties
        this.setAsProperty(JSONMapping.LOCK_FORM_ON_OPEN, returnVal, this.isLockFormOnOpen());
        this.setAsProperty(JSONMapping.UNLOCK_FORM_ON_SAVE, returnVal, this.isUnlockFormOnSave());
        this.setAsProperty(JSONMapping.SEND_ON_AFTER_SAVE, returnVal, this.isSendOnAfterSave());
        this.setAsProperty(JSONMapping.SEND_TO_WORKFLOW_AFTER_CREATE, returnVal, this.isSendToWorkflowAfterCreate());
        this.setAsProperty(JSONMapping.ENABLE_CALCULATED_LABELS, returnVal, this.isEnableCalculatedLabels());
        this.setAsProperty(JSONMapping.ENABLE_FORM_FIELD_HISTORY, returnVal, this.isEnableFormFieldHistory());

        return returnVal;
    }

    /**
     * Verify if any table forms are included.
     *
     * @return {@code true} if table forms are included.
     */
    @XmlTransient
    public boolean isAnyTableFormsEnabled() {
        return this.tableFieldsToInclude != null && !this.tableFieldsToInclude.isEmpty();
    }

    /**
     * Validate whether the {@code newFormTitleFormula} is correctly formatted.
     *
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
     * @return The new instance group if set, otherwise [Add New].
     */
    public String getCreateNewInstanceGroup() {
        return UtilGlobal.isBlank(this.createNewInstanceGroup) ? DEF_GROUP : this.createNewInstanceGroup;
    }

    /**
     * @return The new instance icon if set, otherwise [pi pi-file-o].
     */
    public String getCreateNewInstanceIcon() {
        return UtilGlobal.isBlank(this.createNewInstanceIcon) ? DEF_NEW_INSTANCE : this.createNewInstanceIcon;
    }

    /**
     * @return The attachment display type or [grid] if not set.
     */
    public String getAttachmentDisplayType() {
        return UtilGlobal.isBlank(this.attachmentDisplayType) ? AttachmentDisplayType.GRID : this.attachmentDisplayType;
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
