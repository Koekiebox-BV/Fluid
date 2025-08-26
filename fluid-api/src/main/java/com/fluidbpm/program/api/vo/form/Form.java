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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidElasticSearchJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * Represents an Electronic Form and Form Definition in Fluid.
 * <p>
 * Depending on whether the {@code Form} is used for the;
 *
 * <ul>
 *     <li>
 *         Web Service for Document creation.
 *     </li>
 *     <li>
 *         Web Service for Form Definition adjustment.
 *     </li>
 *     <li>
 *         Third Party class loader for Flow Programs.
 *     </li>
 *     <li>
 *         Third Party class loader for Web Form Actions.
 *     </li>
 * </ul>
 *
 * @author jasonbruwer
 * @see com.fluidbpm.program.api.vo.historic.FormFlowHistoricDataListing
 * @see Field
 * @see Flow
 * @since v1.0
 */
@Getter
@Setter
public class Form extends ABaseFluidElasticSearchJSONObject {
    private static final long serialVersionUID = 1L;

    private String formType;
    private Long formTypeId;

    private String formDescription;

    private String title;

    private String flowState;
    private String state;
    private User currentUser;

    private Date dateCreated;
    private Date dateLastUpdated;

    private List<Field> formFields;
    private List<Flow> associatedFlows;

    //Specifically for Elasticsearch...
    private Long ancestorId;
    private List<Long> descendantIds;
    private Long tableFieldParentId;
    private String tableFieldParentName;
    private String ancestorLabel;
    private String descendantsLabel;
    private Boolean numberInputs;

    private static final String EMPTY_TITLE_MARKER = "[No Title from Custom Program]";

    /**
     * The JSON mapping for the {@code Form} object.
     */
    public static class JSONMapping {
        public static final String FORM_TYPE = "formType";
        public static final String FORM_TYPE_ID = "formTypeId";
        public static final String FORM_DESCRIPTION = "formDescription";

        public static final String TITLE = "title";
        public static final String STATE = "state";
        public static final String FLOW_STATE = "flowState";
        public static final String CURRENT_USER = "currentUser";

        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";

        public static final String FORM_FIELDS = "formFields";
        public static final String ASSOCIATED_FLOWS = "associatedFlows";

        //Labels...
        public static final String ANCESTOR_LABEL = "ancestorLabel";
        public static final String DESCENDANTS_LABEL = "descendantsLabel";
        public static final String NUMBER_INPUTS = "numberInputs";

        //Fields used for Search engine indexing...
        public static final String TABLE_FIELD_PARENT_ID = "tableFieldParentId";
        public static final String TABLE_FIELD_PARENT_NAME = "tableFieldParentName";
        public static final String ANCESTOR_ID = "ancestorId";
        public static final String DESCENDANT_IDS = "descendantIds";

        public static final String _PARENT = "_parent";
    }

    /**
     * The JSON mapping for the {@code Form} object as a flat object.
     */
    public static class FlatFormJSONMapping {
        //Form...
        public static final String ID = "id";
        public static final String FORM_ID = "form_id";
        public static final String FORM_TITLE = "form_title";
        public static final String FORM_STATE = "form_state";
        public static final String FORM_TYPE = "form_type";
        public static final String FORM_FLOW_STATE = "form_flow_state";
        public static final String FORM_DATE_CREATED = "form_date_created";
        public static final String FORM_DATE_LAST_UPDATED = "form_date_last_updated";

        //Fields
        public static final String FORM_FIELD_PREFIX = "form_field_";
        public static final String FORM_FIELD_ID_PREFIX = "form_field_id_";
    }

    /**
     * The state the {@code Form} can be in.
     */
    public static class State {
        public static final String OPEN = "Open";//1
        public static final String LOCKED = "Locked";//2
    }

    /**
     * Default constructor.
     */
    public Form() {
        super();
    }

    /**
     * Sets the Id associated with either the Form or Form Definition.
     *
     * @param formIdParam Form / Form Definition Id.
     */
    public Form(Long formIdParam) {
        super();
        this.setId(formIdParam);
    }

    /**
     * Constructor that also sets The Form Definition
     * associated with the {@code Form}.
     *
     * @param formType The Form Definition.
     */
    public Form(String formType) {
        this.setFormType(formType);
    }

    /**
     * Constructor that also sets The Form Definition and Form Instance Title
     * associated with the {@code Form}.
     *
     * @param formType The Form Definition.
     * @param title    The Form instance Title.
     */
    public Form(String formType, String title) {
        this.setFormType(formType);
        this.setTitle(title);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Form(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFormDescription(this.getAsStringNullSafe(JSONMapping.FORM_DESCRIPTION));
        this.setAncestorLabel(this.getAsStringNullSafe(JSONMapping.ANCESTOR_LABEL));
        this.setDescendantsLabel(this.getAsStringNullSafe(JSONMapping.DESCENDANTS_LABEL));
        this.setNumberInputs(this.getAsBooleanNullSafe(JSONMapping.NUMBER_INPUTS));
        this.setTitle(this.getAsStringNullSafe(JSONMapping.TITLE));
        this.setFlowState(this.getAsStringNullSafe(JSONMapping.FLOW_STATE));
        this.setState(this.getAsStringNullSafe(JSONMapping.STATE));
        this.setFormType(this.getAsStringNullSafe(JSONMapping.FORM_TYPE));
        this.setFormTypeId(this.getAsLongNullSafe(JSONMapping.FORM_TYPE_ID));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
        this.setAssociatedFlows(this.extractObjects(JSONMapping.ASSOCIATED_FLOWS, Flow::new));
        this.setFormFields(this.extractObjects(JSONMapping.FORM_FIELDS, Field::new));
        this.setAncestorId(this.getAsLongNullSafe(JSONMapping.ANCESTOR_ID));
        this.setTableFieldParentId(this.getAsLongNullSafe(JSONMapping.TABLE_FIELD_PARENT_ID));
        this.setTableFieldParentName(this.getAsStringNullSafe(JSONMapping.TABLE_FIELD_PARENT_NAME));
        this.setDescendantIds(this.extractLongs(JSONMapping.DESCENDANT_IDS));

        //User...
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.CURRENT_USER)) {
            JsonObject jsonObjCurUsr = this.jsonObject.getAsJsonObject(JSONMapping.CURRENT_USER);
            User currentUser = new User();
            //User Id
            if (this.isPropertyNotNull(jsonObjCurUsr, User.JSONMapping.Elastic.USER_ID)) {
                currentUser.setId(jsonObjCurUsr.get(User.JSONMapping.Elastic.USER_ID).getAsLong());
            } else if (this.isPropertyNotNull(jsonObjCurUsr, ABaseFluidGSONObject.JSONMapping.ID)) {
                //Id is set, make use of that instead...
                currentUser.setId(jsonObjCurUsr.get(ABaseFluidGSONObject.JSONMapping.ID).getAsLong());
            }
            if (this.isPropertyNotNull(jsonObjCurUsr, User.JSONMapping.USERNAME)) {
                currentUser.setUsername(jsonObjCurUsr.get(User.JSONMapping.USERNAME).getAsString());
            }
            this.setCurrentUser(currentUser);
        }
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as one of the {@code Field.Type}s.
     * @see Field.Type
     */
    @XmlTransient
    @JsonIgnore
    public Object getFieldValueForField(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValue();
    }

    /**
     * <p>
     * Returns the {@code Field} of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The Form Field.
     * @see Field
     */
    @XmlTransient
    @JsonIgnore
    public Field getField(String fieldNameParam) {
        if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) return null;
        if (this.getFormFields() == null || this.getFormFields().isEmpty()) return null;

        String fieldNameLower = fieldNameParam.toLowerCase().trim();
        return this.getFormFields().stream()
                .filter(itm -> itm.getFieldName() != null &&
                        fieldNameLower.equals(itm.getFieldName().toLowerCase().trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code String}.
     * @see Field.Type#Text
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldValueAsString(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsString();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code TableField}.
     * @see Field.Type#Table
     * @see TableField
     */
    @XmlTransient
    @JsonIgnore
    public TableField getFieldValueAsTableField(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsTableField();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code MultiChoice}.
     * @see Field.Type#MultipleChoice
     * @see MultiChoice
     */
    @XmlTransient
    @JsonIgnore
    public MultiChoice getFieldValueAsMultiChoice(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsMultiChoice();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Date}.
     * @see Field.Type#DateTime
     */
    @XmlTransient
    @JsonIgnore
    public Date getFieldValueAsDate(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsDate();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Boolean}.
     * @see Field.Type#TrueFalse
     */
    @XmlTransient
    @JsonIgnore
    public Boolean getFieldValueAsBoolean(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsBoolean();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Double}.
     * @see Field.Type#Decimal
     */
    @XmlTransient
    @JsonIgnore
    public Double getFieldValueAsDouble(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsDouble();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     *     <li>Field Value is not of type {@code Number}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Integer}.
     * @see Field.Type#Decimal
     */
    @XmlTransient
    @JsonIgnore
    public Integer getFieldValueAsInt(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsInteger();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     *     <li>Field Value is not of type {@code Number}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Long}.
     * @see Field.Type#Decimal
     */
    @XmlTransient
    @JsonIgnore
    public Long getFieldValueAsLong(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsLong();
    }

    /**
     * <p>
     * Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * A {@code null} will be returned if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     *     <li>Field Value is not of type {@code Number}.</li>
     * </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Number}.
     * @see Field.Type#Decimal
     */
    @XmlTransient
    @JsonIgnore
    public Number getFieldValueAsNumber(String fieldNameParam) {
        Field fieldWithName = this.getField(fieldNameParam);
        return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsNumber();
    }

    /**
     * <p>
     * Sets the value of the {@code fieldNameParam} requested.
     * <p>
     * If there is an existing value, the value will be override with
     * the value of {@code fieldValueParam}.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     * The value won't be set if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam  The name of the Form Field as in Fluid.
     * @param fieldValueParam The value of the {@code Field}.
     * @see Field.Type
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValue(String fieldNameParam, Object fieldValueParam) {
        if (this.getFormFields() == null) this.setFormFields(new ArrayList<>());

        this.setFieldValue(this.getFormFields(), fieldNameParam, fieldValueParam, null);
    }

    /**
     * Set the field value to {@code fieldValueParam} where name is {@code fieldNameParam}
     * on the list {@code fieldToSelectFrom}.
     *
     * @param fieldToSelectFrom The field listing to set or add to.
     * @param fieldName         The name of the field.
     * @param fieldValue        The new value.
     * @param type              The type of field.
     */
    private void setFieldValue(List<Field> fieldToSelectFrom, String fieldName, Object fieldValue, Field.Type type) {
        if (fieldToSelectFrom == null) return;
        if (fieldName == null || fieldName.trim().isEmpty()) return;

        String fieldNameLower = fieldName.toLowerCase();
        List<Field> copyList = new ArrayList<>(fieldToSelectFrom);

        Field fieldWithName = copyList.stream()
                .filter(itm -> itm.getFieldName() != null &&
                        fieldNameLower.equals(itm.getFieldName().toLowerCase()))
                .findFirst()
                .orElse(null);
        if (fieldWithName == null) {
            fieldToSelectFrom.add(new Field(fieldName, fieldValue, type));
        } else {
            fieldWithName.setFieldValue(fieldValue);
            if (type != null) fieldWithName.setTypeAsEnum(type);
        }
    }

    /**
     * Remove the {@code Form} field where the name is {@code fieldNameParam}.
     *
     * @param fieldName The name of the field to remove.
     */
    public void removeField(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) return;
        if (this.getFormFields() == null || this.getFormFields().isEmpty()) return;

        String fieldNameLower = fieldName.toLowerCase();
        this.getFormFields().removeIf(fieldItm -> fieldItm.getFieldName() != null &&
                fieldNameLower.equals(fieldItm.getFieldName().toLowerCase()));
    }

    /**
     * <p>
     * Sets the value of the {@code fieldNameParam} requested.
     *
     * <p>
     * If there is an existing value, the value will be override with
     * the value of {@code fieldValueParam}.
     *
     * <p>
     * The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <br>
     *
     * <p>
     * The value won't be set if;
     * <ul>
     *     <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *     <li>{@code getFormFields()} is {@code null} or empty.</li>
     *     <li>Field is not found by {@code fieldNameParam}.</li>
     * </ul>
     *
     * @param fieldNameParam  The name of the Form Field as in Fluid.
     * @param fieldValueParam The value of the {@code Field}.
     * @param typeParam       The {@code Field.Type} of {@code Field}.
     * @see Field.Type
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) return;
        if (this.getFormFields() == null) this.setFormFields(new ArrayList<>());

        this.setFieldValue(this.getFormFields(), fieldNameParam, fieldValueParam, typeParam);
    }

    /**
     * <p>
     * Determine whether the current {@code Form} Type / Definition is
     * of type {@code formTypeParam}
     *
     * <p>
     * If the {@code formTypeParam} is {@code null} or empty, {@code false}
     * will be returned.
     *
     * @param formTypeParam String value of the Form Definition.
     * @return Whether the {@code Form} is of type {@code formTypeParam}
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFormType(String formTypeParam) {
        if ((formTypeParam == null || formTypeParam.trim().isEmpty()) ||
                (this.getFormType() == null || this.getFormType().trim().isEmpty())) return false;

        return formTypeParam.toLowerCase().equals(getFormType().toLowerCase());
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Form}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.FORM_TYPE, returnVal, this.getFormType());
        this.setAsProperty(JSONMapping.FORM_TYPE_ID, returnVal, this.getFormTypeId());
        this.setAsProperty(JSONMapping.TITLE, returnVal, this.getTitle());
        this.setAsProperty(JSONMapping.FORM_DESCRIPTION, returnVal, this.getFormDescription());
        this.setAsProperty(JSONMapping.ANCESTOR_LABEL, returnVal, this.getAncestorLabel());
        this.setAsProperty(JSONMapping.DESCENDANTS_LABEL, returnVal, this.getDescendantsLabel());
        this.setAsProperty(JSONMapping.NUMBER_INPUTS, returnVal, this.getNumberInputs());
        this.setAsProperty(JSONMapping.DATE_CREATED, returnVal, this.getDateAsLongFromJson(this.getDateCreated()));
        this.setAsProperty(JSONMapping.DATE_LAST_UPDATED, returnVal, this.getDateAsLongFromJson(this.getDateLastUpdated()));
        this.setAsObj(JSONMapping.CURRENT_USER, returnVal, this::getCurrentUser);

        this.setAsProperty(JSONMapping.STATE, returnVal, this.getState());
        this.setAsProperty(JSONMapping.FLOW_STATE, returnVal, this.getFlowState());

        this.setAsObjArray(JSONMapping.FORM_FIELDS, returnVal, this::getFormFields);
        this.setAsObjArray(JSONMapping.ASSOCIATED_FLOWS, returnVal, this::getAssociatedFlows);

        this.setAsProperty(JSONMapping.ANCESTOR_ID, returnVal, this.getAncestorId());
        this.setAsProperty(JSONMapping.TABLE_FIELD_PARENT_ID, returnVal, this.getTableFieldParentId());
        this.setAsProperty(JSONMapping.TABLE_FIELD_PARENT_NAME, returnVal, this.getTableFieldParentName());

        this.setAsObjArray(JSONMapping.ASSOCIATED_FLOWS, returnVal, this::getAssociatedFlows);
        this.setAsLongArray(JSONMapping.DESCENDANT_IDS, returnVal, this.getDescendantIds());

        return returnVal;
    }

    /**
     * Creates the mapping object required by Elastic Search when making
     * use of enhanced data-types.
     * <p>
     * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
     *
     * @return {@code JSONObject} representation of {@code Form} for
     * ElasticSearch mapping.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonMappingForElasticSearch() {
        JsonObject returnVal = new JsonObject();
        //Id...
        {
            JsonObject idJsonObj = new JsonObject();
            idJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.LONG
            );
            returnVal.add(ABaseFluidGSONObject.JSONMapping.ID, idJsonObj);
        }

        //Form Type...
        {
            JsonObject formTypeJsonObj = new JsonObject();
            formTypeJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.add(JSONMapping.FORM_TYPE, formTypeJsonObj);
        }

        //Form Type Id...
        {
            JsonObject formTypeIdJsonObj = new JsonObject();
            formTypeIdJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.add(JSONMapping.FORM_TYPE_ID, formTypeIdJsonObj);
        }

        //Title...
        {
            JsonObject titleJsonObj = new JsonObject();
            titleJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.TEXT);
            returnVal.add(JSONMapping.TITLE, titleJsonObj);
        }

        //Form Description...
        {
            JsonObject formDescJsonObj = new JsonObject();
            formDescJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.add(JSONMapping.FORM_DESCRIPTION, formDescJsonObj);
        }

        //State...
        {
            JsonObject stateJsonObj = new JsonObject();
            stateJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.add(JSONMapping.STATE, stateJsonObj);
        }

        //Flow State...
        {
            JsonObject flowStateJsonObj = new JsonObject();
            flowStateJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.add(JSONMapping.FLOW_STATE, flowStateJsonObj);
        }

        //Current User...
        {
            JsonObject currentUserJsonObj = new JsonObject();
            currentUserJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.OBJECT
            );
            JsonObject properties = new JsonObject();
            //Current User Id...
            JsonObject currentUserUserIdJsonObj = new JsonObject();
            currentUserUserIdJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.LONG);
            properties.add(User.JSONMapping.Elastic.USER_ID, currentUserUserIdJsonObj);

            //Current User Id...
            JsonObject currentUserUsernameJsonObj = new JsonObject();
            currentUserUsernameJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.KEYWORD);
            properties.add(User.JSONMapping.USERNAME, currentUserUsernameJsonObj);

            currentUserJsonObj.add(ABaseFluidGSONObject.JSONMapping.Elastic.PROPERTIES, properties);
            returnVal.add(JSONMapping.CURRENT_USER, currentUserJsonObj);
        }

        //Date Created...
        {
            JsonObject dateCreatedJsonObj = new JsonObject();
            dateCreatedJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.DATE);
            returnVal.add(JSONMapping.DATE_CREATED, dateCreatedJsonObj);
        }

        //Date Last Updated...
        {
            JsonObject dateLastUpdatedJsonObj = new JsonObject();
            dateLastUpdatedJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.DATE);
            returnVal.add(JSONMapping.DATE_LAST_UPDATED, dateLastUpdatedJsonObj);
        }

        //Get the listing of form fields...
        if (this.getFormFields() != null && !this.getFormFields().isEmpty()) {
            for (Field toAdd : this.getFormFields()) {
                JsonObject convertedField = toAdd.toJsonMappingForElasticSearch();
                if (convertedField == null) {
                    continue;
                }

                String fieldNameAsCamel = toAdd.getFieldNameAsUpperCamel();
                returnVal.add(fieldNameAsCamel, convertedField);
            }
        }

        //Ancestor Obj...
        {
            JsonObject ancestorJsonObj = new JsonObject();
            ancestorJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.add(JSONMapping.ANCESTOR_ID, ancestorJsonObj);
        }

        //Table field parent id...
        {
            JsonObject tblFieldParentIdJsonObj = new JsonObject();
            tblFieldParentIdJsonObj.addProperty(
                    Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.add(JSONMapping.TABLE_FIELD_PARENT_ID, tblFieldParentIdJsonObj);
        }
        return returnVal;
    }

    /**
     * Conversion to {@code JSONObject} for storage in ElasticCache for {@code Form}.
     * @return {@code JSONObject} representation of {@code Form}
     * @see Form
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonForElasticSearch() {
        JsonObject returnVal = super.toJsonObject();

        //Form Type...
        if (this.getFormType() != null) returnVal.addProperty(JSONMapping.FORM_TYPE, this.getFormType());

        //Form Type Id...
        if (this.getFormTypeId() != null) returnVal.addProperty(JSONMapping.FORM_TYPE_ID, this.getFormTypeId());

        //Title...
        if (this.getTitle() != null) returnVal.addProperty(JSONMapping.TITLE, this.getTitle());

        //Form Description...
        if (this.getFormDescription() != null)
            returnVal.addProperty(JSONMapping.FORM_DESCRIPTION, this.getFormDescription());

        //State...
        if (this.getState() != null) returnVal.addProperty(JSONMapping.STATE, this.getState());

        //Flow State...
        if (this.getFlowState() != null) returnVal.addProperty(JSONMapping.FLOW_STATE, this.getFlowState());

        //Current User...
        JsonObject currentUserJsonObj = new JsonObject();
        if (this.getCurrentUser() == null) {
            currentUserJsonObj.add(User.JSONMapping.Elastic.USER_ID, JsonNull.INSTANCE);
            currentUserJsonObj.add(User.JSONMapping.USERNAME, JsonNull.INSTANCE);
        } else {
            //Id...
            if (this.getCurrentUser().getId() == null ||
                    this.getCurrentUser().getId().longValue() < 1) {
                currentUserJsonObj.add(User.JSONMapping.Elastic.USER_ID, JsonNull.INSTANCE);
            } else {
                currentUserJsonObj.addProperty(
                        User.JSONMapping.Elastic.USER_ID,
                        this.getCurrentUser().getId());
            }

            //Username...
            if (this.getCurrentUser().getUsername() == null || this.getCurrentUser().getUsername().trim().isEmpty()) {
                currentUserJsonObj.add(User.JSONMapping.USERNAME, JsonNull.INSTANCE);
            } else {
                currentUserJsonObj.addProperty(User.JSONMapping.USERNAME, this.getCurrentUser().getUsername());
            }
        }
        returnVal.add(JSONMapping.CURRENT_USER, currentUserJsonObj);

        //Date Created...
        if (this.getDateCreated() != null) {
            returnVal.addProperty(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Date Last Updated...
        if (this.getDateLastUpdated() != null) {
            returnVal.addProperty(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(this.getDateLastUpdated()));
        }

        //Form Fields...
        if (this.getFormFields() != null && !this.getFormFields().isEmpty()) {
            this.getFormFields().forEach(toAdd -> {
                JsonObject convertedFieldObj = toAdd.toJsonForElasticSearch();
                if (convertedFieldObj == null) return;

                Iterator<String> iterKeys = convertedFieldObj.keySet().iterator();
                while (iterKeys.hasNext()) {
                    String key = iterKeys.next();
                    if (convertedFieldObj.has(key)) {
                        returnVal.addProperty(key, convertedFieldObj.get(key).getAsString());
                    }
                }
            });
        }
        returnVal.addProperty(JSONMapping.ANCESTOR_ID, this.getAncestorId());

        //Table Field Parent Id...
        returnVal.addProperty(JSONMapping.TABLE_FIELD_PARENT_ID, this.getTableFieldParentId());
        returnVal.addProperty(JSONMapping.TABLE_FIELD_PARENT_NAME, this.getTableFieldParentName());
        returnVal.add(JSONMapping.DESCENDANT_IDS, this.toJsonLongArray(this.getDescendantIds()));

        return returnVal;
    }

    /**
     * Serialize {@code this} object into a JSONObject.
     * <p>
     * Any fields provided with a Java {@code null} value will be stored
     * as {@code JsonNull.INSTANCE}. {@code Field.Type.Table} fields are not supported and will be skipped.
     *
     * @return Flat {@code JSON} object (No inner fields).
     */
    @XmlTransient
    @JsonIgnore
    public JsonObject convertToFlatJSONObject() {
        JsonObject returnVal = new JsonObject();
        returnVal.addProperty(FlatFormJSONMapping.ID, this.getId());
        returnVal.addProperty(FlatFormJSONMapping.FORM_ID, this.getId());
        returnVal.addProperty(FlatFormJSONMapping.FORM_TITLE, this.getTitle());
        returnVal.addProperty(FlatFormJSONMapping.FORM_TYPE, this.getFormType());
        returnVal.addProperty(FlatFormJSONMapping.FORM_STATE, this.getState());
        returnVal.addProperty(FlatFormJSONMapping.FORM_FLOW_STATE, this.getFlowState());
        returnVal.addProperty(FlatFormJSONMapping.FORM_DATE_CREATED, this.getDateCreated() == null ? null : this.getDateCreated().getTime());
        returnVal.addProperty(FlatFormJSONMapping.FORM_DATE_LAST_UPDATED, (this.getDateLastUpdated() == null) ? null : this.getDateLastUpdated().getTime());

        //Form Fields...
        if (this.getFormFields() == null || this.getFormFields().isEmpty()) return returnVal;

        //Set the form fields...
        UtilGlobal utilGlobal = new UtilGlobal();
        this.getFormFields().forEach(
                (formFieldItem) -> {
                    utilGlobal.setFlatFieldOnJSONObj(
                            FlatFormJSONMapping.FORM_FIELD_PREFIX,
                            FlatFormJSONMapping.FORM_FIELD_ID_PREFIX,
                            formFieldItem,
                            returnVal
                    );
                }
        );

        return returnVal;
    }

    /**
     * Populate the object based on the ElasticSearch JSON structure.
     * @param jsonObject The JSON object to populate from.
     * @param formFields The Form Fields to use for mapping.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public void populateFromElasticSearchJson(JsonObject jsonObject, List<Field> formFields) {
        this.jsonObject = jsonObject;
        if (jsonObject == null) return;

        this.setId(this.getAsLongNullSafe(ABaseFluidGSONObject.JSONMapping.ID));
        this.setFormType(this.getAsStringNullSafe(JSONMapping.FORM_TYPE));
        this.setFormTypeId(this.getAsLongNullSafe(JSONMapping.FORM_TYPE_ID));
        this.setTitle(this.getAsStringNullSafe(JSONMapping.TITLE));
        this.setFlowState(this.getAsStringNullSafe(JSONMapping.FLOW_STATE));
        this.setState(this.getAsStringNullSafe(JSONMapping.STATE));
        this.setFormDescription(this.getAsStringNullSafe(JSONMapping.FORM_DESCRIPTION));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));

        if (this.isPropertyNull(this.jsonObject, JSONMapping.CURRENT_USER)) {
            this.setCurrentUser(null);
        } else {
            JsonObject currUserJsonObj = jsonObject.getAsJsonObject(JSONMapping.CURRENT_USER);
            User currentUser = new User();
            currentUser.setId(currUserJsonObj.get(User.JSONMapping.Elastic.USER_ID).getAsLong());
            currentUser.setUsername(currUserJsonObj.get(User.JSONMapping.USERNAME).getAsString());
            this.setCurrentUser(currentUser);
        }

        //Form Fields...
        if (formFields == null || formFields.isEmpty()) {
            this.setFormFields(null);
        } else {
            //There are fields...
            List<Field> fieldsToSet = new ArrayList<>();
            for (Field formField : formFields) {
                Field fieldToAdd = formField.populateFromElasticSearchJson(jsonObject);
                if (fieldToAdd == null) continue;
                fieldsToSet.add(fieldToAdd);
            }
            //Confirm there are values...
            if (fieldsToSet.isEmpty()) this.setFormFields(null);
            else this.setFormFields(fieldsToSet);
        }

        //Ancestor...
        this.setAncestorId(this.getAsLongNullSafe(JSONMapping.ANCESTOR_ID));
        this.setTableFieldParentId(this.getAsLongNullSafe(JSONMapping.TABLE_FIELD_PARENT_ID));
        this.setTableFieldParentName(this.getAsStringNullSafe(JSONMapping.TABLE_FIELD_PARENT_NAME));
        this.setDescendantIds(this.extractLongs(JSONMapping.DESCENDANT_IDS));
    }

    /**
     * Converts the {@code getFormType} to upper_camel_case.
     * @return {@code getFieldName()} as upper_camel_case.
     */
    @XmlTransient
    @JsonIgnore
    public String getFormTypeAsUpperCamel() {
        return new UtilGlobal().toCamelUpperCase(this.getFormType());
    }

    /**
     * JSON {@code String} value for this form.
     * @return JSON value of form.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public String toString() {
        JsonObject jsonObj = this.toJsonObject();
        if (jsonObj == null) return null;
        return jsonObj.toString();
    }

    /**
     * Compares {@code objParam} against {@code this} to see
     * if they are equal.
     * @param objParam The object to compare against.
     * @return Whether {@code objParam} is equal.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public boolean equals(Object objParam) {
        if (objParam == null || this.getId() == null) return false;
        if (objParam instanceof Form) {
            Form paramCasted = (Form) objParam;
            if (paramCasted.getId() == null) return false;
            return (this.getId().equals(paramCasted.getId()));
        }
        return false;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * @return The hascode of {@code this} object.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public int hashCode() {
        int hash = 10000000;
        if (this.getId() == null) {
            return hash;
        }
        hash += this.getId().hashCode();
        return hash;
    }

    /**
     * <p>Sets the Form Title as in Fluid.
     * <p>
     * If {@code titleParam} is {@code null}, the
     * Title will be set to <br>
     * "[No Title from Custom Program]"
     *
     * @param title Form Title.
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = EMPTY_TITLE_MARKER;
            return;
        }
        this.title = title;
    }

    /**
     * Create a {@code Map} from the Form fields.
     * @return {@code Map} with field name as key and {@code Field} as value.
     */
    @XmlTransient
    @JsonIgnore
    public Map<String, Field> getFormFieldsAsMap() {
        List<Field> fields = this.getFormFields();
        Map<String, Field> returnVal = new HashMap<>();
        if (fields == null || fields.isEmpty()) return returnVal;
        fields.forEach(field -> returnVal.put(field.getFieldName(), field));
        return returnVal;
    }

    /**
     * Prints all the Fields and their values to the standard
     * {@code System.out}.
     */
    @XmlTransient
    @JsonIgnore
    public void printFormFields() {
        System.out.println("\n\n*** PRINTING FORM FIELDS ***");
        if (this.getFormFields() != null) {
            for (Field formField : this.getFormFields()) {
                System.out.println("Field : '" + formField.getFieldName() + "' with value: " +
                        formField.getFieldValue());
            }
        }
    }

    /**
     * Verify whether all Form fields are empty.
     * @return {@code true} if all fields are empty, otherwise {@code false}
     */
    @XmlTransient
    @JsonIgnore
    public boolean isAllFormFieldsEmpty() {
        if (this.getFormFields() == null || this.getFormFields().isEmpty()) return true;
        AtomicBoolean allEmpty = new AtomicBoolean(true);
        this.getFormFields().stream().forEach(itm -> {
            if (!allEmpty.get()) return;//already marked as empty...

            if (!itm.isFieldValueEmpty()) allEmpty.set(false);
        });
        return allEmpty.get();
    }
}
