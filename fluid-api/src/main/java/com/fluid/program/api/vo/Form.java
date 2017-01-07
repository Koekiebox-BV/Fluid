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

package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.user.User;

/**
 * <p>
 * Represents an Electronic Form and Form Definition in Fluid.
 *
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
 * @since v1.0
 *
 * @see FormFlowHistoricDataContainer
 * @see Field
 * @see Flow
 */
public class Form extends ABaseFluidElasticSearchJSONObject {

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
    private Long tableFieldParentId;
    private List<Long> descendantIds;

    private static final String EMPTY_TITLE_MARKER = "[No Title from Custom Program]";

    /**
     * The JSON mapping for the {@code Form} object.
     */
    public static class JSONMapping
    {
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

        //Fields used for Search engine indexing...
        public static final String TABLE_FIELD_PARENT_ID = "tableFieldParentId";
        public static final String ANCESTOR_ID = "ancestorId";
        public static final String DESCENDANT_IDS = "descendantIds";
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
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Form(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Form Description...
        if (!this.jsonObject.isNull(JSONMapping.FORM_DESCRIPTION)) {
            this.setFormDescription(this.jsonObject.getString(JSONMapping.FORM_DESCRIPTION));
        }

        //Title...
        if (!this.jsonObject.isNull(JSONMapping.TITLE)) {
            this.setTitle(this.jsonObject.getString(JSONMapping.TITLE));
        }

        //Flow State...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STATE)) {
            this.setFlowState(this.jsonObject.getString(JSONMapping.FLOW_STATE));
        }

        //State...
        if (!this.jsonObject.isNull(JSONMapping.STATE)) {
            this.setState(this.jsonObject.getString(JSONMapping.STATE));
        }

        //User...
        if (!this.jsonObject.isNull(JSONMapping.CURRENT_USER)) {

            JSONObject jsonObj = this.jsonObject.getJSONObject(
                    JSONMapping.CURRENT_USER);
            User currentUser = new User();

            //User Id
            if (!jsonObj.isNull(User.JSONMapping.Elastic.USER_ID)) {

                currentUser.setId(jsonObj.getLong(
                        User.JSONMapping.Elastic.USER_ID));
            }

            //Username
            if (!jsonObj.isNull(User.JSONMapping.USERNAME)) {

                currentUser.setUsername(jsonObj.getString(User.JSONMapping.USERNAME));
            }

            this.setCurrentUser(currentUser);
        }

        //Form Type...
        if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE)) {
            this.setFormType(this.jsonObject.getString(JSONMapping.FORM_TYPE));
        }

        //Form Type Id...
        if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE_ID)) {
            this.setFormTypeId(this.jsonObject.getLong(JSONMapping.FORM_TYPE_ID));
        }

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_CREATED));

        //Date Last Updated...
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_LAST_UPDATED));

        //Associated Flows...
        if (!this.jsonObject.isNull(JSONMapping.ASSOCIATED_FLOWS)) {

            JSONArray associatedJobsArr = this.jsonObject.getJSONArray(
                    JSONMapping.ASSOCIATED_FLOWS);

            List<Flow> assFlowsObj = new ArrayList();
            for(int index = 0;index < associatedJobsArr.length();index++)
            {
                assFlowsObj.add(new Flow(associatedJobsArr.getJSONObject(index)));
            }

            this.setAssociatedFlows(assFlowsObj);
        }

        //Form Fields...
        if (!this.jsonObject.isNull(JSONMapping.FORM_FIELDS)) {

            JSONArray formFieldsArr = this.jsonObject.getJSONArray(
                    JSONMapping.FORM_FIELDS);

            List<Field> assFormFields = new ArrayList();
            for(int index = 0;index < formFieldsArr.length();index++)
            {
                assFormFields.add(new Field(formFieldsArr.getJSONObject(index)));
            }

            this.setFormFields(assFormFields);
        }
    }

    /**
     * Constructor that also sets The Form Definition
     * associated with the {@code Form}.
     *
     * @param formTypeParam The Form Definition.
     */
    public Form(String formTypeParam) {
        this.setFormType(formTypeParam);
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as one of the {@code Field.Type}s.
     *
     * @see com.fluid.program.api.vo.Field.Type
     */
    @XmlTransient
    public Object getFieldValueForField(String fieldNameParam) {

        if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) {
            return null;
        }

        if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
            return null;
        }

        String fieldNameParamLower = fieldNameParam.trim().toLowerCase();

        for (Field field : this.getFormFields()) {

            String fieldName = field.getFieldName();

            if (fieldName == null || fieldName.trim().isEmpty()) {
                continue;
            }

            String fieldNameLower = fieldName.trim().toLowerCase();

            if (fieldNameParamLower.equals(fieldNameLower)) {

                return field.getFieldValue();
            }
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code String}.
     *
     * @see com.fluid.program.api.vo.Field.Type#Text
     */
    @XmlTransient
    public String getFieldValueAsString(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        return obj.toString();
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code TableField}.
     *
     * @see com.fluid.program.api.vo.Field.Type#Table
     * @see TableField
     */
    @XmlTransient
    public TableField getFieldValueAsTableField(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof TableField)
        {
            return (TableField)obj;
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code MultiChoice}.
     *
     * @see com.fluid.program.api.vo.Field.Type#MultipleChoice
     * @see MultiChoice
     */
    @XmlTransient
    public MultiChoice getFieldValueAsMultiChoice(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof MultiChoice)
        {
            return (MultiChoice)obj;
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Date}.
     *
     * @see com.fluid.program.api.vo.Field.Type#DateTime
     */
    @XmlTransient
    public Date getFieldValueAsDate(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        //Real Date...
        if(obj instanceof Date)
        {
            return (Date)obj;
        }
        //Long...
        else if(obj instanceof Long)
        {
            Long longValue = (Long)obj;

            if(longValue.longValue() > 0)
            {
                return new Date(longValue.longValue());
            }
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Boolean}.
     *
     * @see com.fluid.program.api.vo.Field.Type#TrueFalse
     */
    @XmlTransient
    public Boolean getFieldValueAsBoolean(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof Boolean)
        {
            return (Boolean)obj;
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Double}.
     *
     * @see com.fluid.program.api.vo.Field.Type#Decimal
     */
    @XmlTransient
    public Double getFieldValueAsDouble(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof Double)
        {
            return (Double)obj;
        }

        return null;
    }

    /**
     * <p>
     *     Returns the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     A {@code null} will be returned if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *         <li>Field Value is not of type {@code Number}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @return The value for the Form Field as {@code Integer}.
     *
     * @see com.fluid.program.api.vo.Field.Type#Decimal
     */
    @XmlTransient
    public Integer getFieldValueAsInt(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof Number)
        {
            Number casted = ((Number)obj);

            return casted.intValue();
        }

        return null;
    }

    /**
     * <p>
     *     Sets the value of the {@code fieldNameParam} requested.
     * <p>
     *     If there is an existing value, the value will be override with
     *     the value of {@code fieldValueParam}.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <p>
     *     The value won't be set if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @param fieldValueParam The value of the {@code Field}.
     *
     * @see com.fluid.program.api.vo.Field.Type
     */
    @XmlTransient
    public void setFieldValue(String fieldNameParam, Object fieldValueParam)
    {
        if(fieldNameParam == null || fieldNameParam.trim().length() == 0)
        {
            return;
        }

        if(this.getFormFields() == null || this.getFormFields().isEmpty())
        {
            this.setFormFields(new ArrayList());
        }

        String fieldNameParamLower = fieldNameParam.toLowerCase();

        for(Field field : this.getFormFields())
        {
            if(field.getFieldName() == null || field.getFieldName().trim().length() == 0)
            {
                continue;
            }

            String fieldNameLower = field.getFieldName().toLowerCase();
            if(fieldNameParamLower.equals(fieldNameLower))
            {
                field.setFieldValue(fieldValueParam);
                return;
            }
        }

        //When the Field is not added previously...
        this.getFormFields().add(new Field(fieldNameParam,fieldValueParam));
    }

    /**
     * <p>
     *     Sets the value of the {@code fieldNameParam} requested.
     *
     * <p>
     *     If there is an existing value, the value will be override with
     *     the value of {@code fieldValueParam}.
     *
     * <p>
     *     The {@code fieldNameParam} <b>is not</b> case sensitive.
     *
     * <br>
     *
     * <p>
     *     The value won't be set if;
     *     <ul>
     *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
     *         <li>{@code getFormFields()} is {@code null} or empty.</li>
     *         <li>Field is not found by {@code fieldNameParam}.</li>
     *     </ul>
     *
     * @param fieldNameParam The name of the Form Field as in Fluid.
     * @param fieldValueParam The value of the {@code Field}.
     * @param typeParam The {@code Field.Type} of {@code Field}.
     *
     * @see com.fluid.program.api.vo.Field.Type
     */
    @XmlTransient
    public void setFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) {
            return;
        }

        if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
            this.setFormFields(new ArrayList());
        }

        String paramLower = fieldNameParam.toLowerCase().trim();

        boolean valueFound = false;

        //Iterate the Form Fields...
        int fieldIndex = -1;
        for (Field field : this.getFormFields()) {
            fieldIndex++;

            String toCheckNameLower = field.getFieldName();
            if (toCheckNameLower == null || toCheckNameLower.trim().isEmpty()) {
                continue;
            }

            toCheckNameLower = toCheckNameLower.trim().toLowerCase();

            if (paramLower.equals(toCheckNameLower)) {
                valueFound = true;
                this.getFormFields().get(fieldIndex).setFieldValue(fieldValueParam);
                this.getFormFields().get(fieldIndex).setTypeAsEnum(typeParam);
                break;
            }
        }

        if (!valueFound) {
            this.getFormFields().add(new Field(fieldNameParam, fieldValueParam, typeParam));
        }
    }

    /**
     * <p>
     *     Determine whether the current {@code Form} Type / Definition is
     *     of type {@code formTypeParam}
     *
     * <p>
     *     If the {@code formTypeParam} is {@code null} or empty, {@code false}
     *     will be returned.
     *
     * @param formTypeParam String value of the Form Definition.
     * @return Whether the {@code Form} is of type {@code formTypeParam}
     */
    @XmlTransient
    public boolean isFormType(String formTypeParam) {

        if ((formTypeParam == null || formTypeParam.trim().isEmpty()) ||
                (this.getFormType() == null || this.getFormType().trim().isEmpty())) {
            return false;
        }

        return formTypeParam.toLowerCase().equals(getFormType().toLowerCase());
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Form}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    //@JsonIgnore
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Form Type...
        if(this.getFormType() != null)
        {
            returnVal.put(JSONMapping.FORM_TYPE, this.getFormType());
        }

        //Form Type Id...
        if(this.getFormTypeId() != null)
        {
            returnVal.put(JSONMapping.FORM_TYPE_ID, this.getFormTypeId());
        }

        //Title...
        if(this.getTitle() != null)
        {
            returnVal.put(JSONMapping.TITLE, this.getTitle());
        }

        //Form Description...
        if(this.getFormDescription() != null)
        {
            returnVal.put(JSONMapping.FORM_DESCRIPTION, this.getFormDescription());
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED,
                    this.getDateAsLongFromJson(this.getDateLastUpdated()));
        }

        //Form Fields...
        if(this.getFormFields() != null && !this.getFormFields().isEmpty())
        {
            JSONArray formFieldsArr = new JSONArray();
            for(Field toAdd :this.getFormFields())
            {
                formFieldsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.FORM_FIELDS, formFieldsArr);
        }

        //Associated Flows...
        if(this.getAssociatedFlows() != null && !this.getAssociatedFlows().isEmpty())
        {
            JSONArray assoJobsArr = new JSONArray();
            for(Flow toAdd :this.getAssociatedFlows())
            {
                assoJobsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.ASSOCIATED_FLOWS, assoJobsArr);
        }

        return returnVal;
    }

    /**
     * Creates the mapping object required by Elastic Search when making
     * use of enhanced data-types.
     *
     * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
     *
     * @return {@code JSONObject} representation of {@code Form} for
     * ElasticSearch mapping.
     *
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    @XmlTransient
    public JSONObject toJsonMappingForElasticSearch() throws JSONException {

        JSONObject returnVal = new JSONObject();

        //Id...
        {
            JSONObject idJsonObj = new JSONObject();
            idJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.put(ABaseFluidJSONObject.JSONMapping.ID, idJsonObj);
        }

        //Form Type...
        {
            JSONObject formTypeJsonObj = new JSONObject();
            formTypeJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.put(JSONMapping.FORM_TYPE, formTypeJsonObj);
        }

        //Form Type Id...
        {
            JSONObject formTypeIdJsonObj = new JSONObject();
            formTypeIdJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.put(JSONMapping.FORM_TYPE_ID, formTypeIdJsonObj);
        }

        //Title...
        {
            JSONObject titleJsonObj = new JSONObject();
            titleJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.TEXT);
            returnVal.put(JSONMapping.TITLE, titleJsonObj);
        }

        //Form Description...
        {
            JSONObject formDescJsonObj = new JSONObject();
            formDescJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.put(JSONMapping.FORM_DESCRIPTION, formDescJsonObj);
        }

        //State...
        {
            JSONObject stateJsonObj = new JSONObject();
            stateJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.put(JSONMapping.STATE, stateJsonObj);
        }

        //Flow State...
        {
            JSONObject flowStateJsonObj = new JSONObject();
            flowStateJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.KEYWORD);
            returnVal.put(JSONMapping.FLOW_STATE, flowStateJsonObj);
        }

        //Current User...
        {
            JSONObject currentUserJsonObj = new JSONObject();
            currentUserJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.NESTED);

            JSONObject properties = new JSONObject();

            //Current User Id...
            JSONObject currentUserUserIdJsonObj = new JSONObject();
            currentUserUserIdJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.LONG);
            properties.put(User.JSONMapping.Elastic.USER_ID, currentUserUserIdJsonObj);

            //Current User Id...
            JSONObject currentUserUsernameJsonObj = new JSONObject();
            currentUserUsernameJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.KEYWORD);
            properties.put(User.JSONMapping.USERNAME, currentUserUsernameJsonObj);

            currentUserJsonObj.put(
                    ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
                    properties);

            returnVal.put(JSONMapping.CURRENT_USER, currentUserJsonObj);
        }

        //Date Created...
        {
            JSONObject dateCreatedJsonObj = new JSONObject();
            dateCreatedJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.DATE);
            returnVal.put(JSONMapping.DATE_CREATED, dateCreatedJsonObj);
        }

        //Date Last Updated...
        {
            JSONObject dateLastUpdatedJsonObj = new JSONObject();
            dateLastUpdatedJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.DATE);
            returnVal.put(JSONMapping.DATE_LAST_UPDATED, dateLastUpdatedJsonObj);
        }

        //Get the listing of form fields...
        if(this.getFormFields() != null &&
                !this.getFormFields().isEmpty())
        {
            for(Field toAdd : this.getFormFields())
            {
                JSONObject convertedField = toAdd.toJsonMappingForElasticSearch();
                if(convertedField == null)
                {
                    continue;
                }

                String fieldNameAsCamel = toAdd.getFieldNameAsUpperCamel();
                returnVal.put(fieldNameAsCamel, convertedField);
            }
        }

        //Ancestor Obj...
        {
            JSONObject ancestorJsonObj = new JSONObject();
            ancestorJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.put(JSONMapping.ANCESTOR_ID, ancestorJsonObj);
        }

        //Table field parent id...
        {
            JSONObject tblFieldParentIdJsonObj = new JSONObject();
            tblFieldParentIdJsonObj.put(
                    Field.JSONMapping.Elastic.TYPE,
                    Field.ElasticSearchType.LONG);
            returnVal.put(JSONMapping.TABLE_FIELD_PARENT_ID, tblFieldParentIdJsonObj);
        }

        return returnVal;
    }

    /**
     * Conversion to {@code JSONObject} for storage in ElasticCache for {@code Form}.
     *
     * @return {@code JSONObject} representation of {@code Form}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     * @see Form
     */
    @Override
    @XmlTransient
    public JSONObject toJsonForElasticSearch() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Form Type...
        if(this.getFormType() != null)
        {
            returnVal.put(JSONMapping.FORM_TYPE, this.getFormType());
        }

        //Form Type Id...
        if(this.getFormTypeId() != null)
        {
            returnVal.put(JSONMapping.FORM_TYPE_ID, this.getFormTypeId());
        }

        //Title...
        if(this.getTitle() != null)
        {
            returnVal.put(JSONMapping.TITLE, this.getTitle());
        }

        //Form Description...
        if(this.getFormDescription() != null)
        {
            returnVal.put(JSONMapping.FORM_DESCRIPTION, this.getFormDescription());
        }

        //State...
        if(this.getState() != null)
        {
            returnVal.put(JSONMapping.STATE, this.getState());
        }

        //Flow State...
        if(this.getFlowState() != null)
        {
            returnVal.put(JSONMapping.FLOW_STATE, this.getFlowState());
        }

        //Current User...
        if(this.getCurrentUser() != null)
        {
            JSONObject currentUserJsonObj = new JSONObject();

            if(this.getCurrentUser().getId() != null)
            {
                currentUserJsonObj.put(
                        User.JSONMapping.Elastic.USER_ID,
                        this.getCurrentUser().getId());
            }

            if(this.getCurrentUser().getUsername() != null)
            {
                currentUserJsonObj.put(User.JSONMapping.USERNAME,
                        this.getCurrentUser().getUsername());
            }

            returnVal.put(JSONMapping.CURRENT_USER, currentUserJsonObj);
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED,
                    this.getDateAsLongFromJson(this.getDateLastUpdated()));
        }

        //Form Fields...
        if(this.getFormFields() != null && !this.getFormFields().isEmpty())
        {
            for(Field toAdd : this.getFormFields())
            {
                JSONObject convertedField = toAdd.toJsonForElasticSearch();
                if(convertedField == null)
                {
                    continue;
                }

                Iterator<String> iterKeys = convertedField.keys();
                while(iterKeys.hasNext())
                {
                    String key = iterKeys.next();
                    returnVal.put(key, convertedField.get(key));
                }
            }
        }

        //Ancestor...
        if(this.getAncestorId() != null)
        {
            returnVal.put(JSONMapping.ANCESTOR_ID, this.getAncestorId());
        }

        //Table Field Parent Id...
        if(this.getTableFieldParentId() != null)
        {
            returnVal.put(JSONMapping.TABLE_FIELD_PARENT_ID,
                    this.getTableFieldParentId());
        }

        //Descendant Ids...
        if(this.getDescendantIds() != null && !this.getDescendantIds().isEmpty())
        {
            JSONArray array = new JSONArray();

            for(Long formId : this.getDescendantIds())
            {
                array.put(formId);
            }

            returnVal.put(JSONMapping.DESCENDANT_IDS, array);
        }

        return returnVal;
    }

    /**
     * Populate the object based on the ElasticSearch JSON structure.
     *
     * @param jsonObjectParam The JSON object to populate from.
     * @param formFieldsParam The Form Fields to use for mapping.
     *
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    public void populateFromElasticSearchJson(
            JSONObject jsonObjectParam,
            List<Field> formFieldsParam) throws JSONException {

        this.jsonObject = jsonObjectParam;
        if(jsonObjectParam == null)
        {
            return;
        }

        //Id...
        if(jsonObjectParam.isNull(ABaseFluidJSONObject.JSONMapping.ID))
        {
            this.setId(null);
        }
        else {
            this.setId(jsonObjectParam.getLong(ABaseFluidJSONObject.JSONMapping.ID));
        }

        //Form Type...
        if(jsonObjectParam.isNull(JSONMapping.FORM_TYPE))
        {
            this.setFormType(null);
        }
        else {
            this.setFormType(jsonObjectParam.getString(JSONMapping.FORM_TYPE));
        }

        //Form Type Id...
        if(jsonObjectParam.isNull(JSONMapping.FORM_TYPE_ID))
        {
            this.setFormTypeId(null);
        }
        else {
            this.setFormTypeId(jsonObjectParam.getLong(JSONMapping.FORM_TYPE_ID));
        }

        //Title...
        if(jsonObjectParam.isNull(JSONMapping.TITLE))
        {
            this.setTitle(null);
        }
        else {
            this.setTitle(jsonObjectParam.getString(JSONMapping.TITLE));
        }

        //Flow State...
        if(jsonObjectParam.isNull(JSONMapping.FLOW_STATE))
        {
            this.setFlowState(null);
        }
        else {
            this.setFlowState(jsonObjectParam.getString(JSONMapping.FLOW_STATE));
        }

        //State...
        if(jsonObjectParam.isNull(JSONMapping.STATE))
        {
            this.setState(null);
        }
        else {
            this.setState(jsonObjectParam.getString(JSONMapping.STATE));
        }

        //Form Description...
        if(jsonObjectParam.isNull(JSONMapping.FORM_DESCRIPTION))
        {
            this.setFormDescription(null);
        }
        else {
            this.setFormDescription(jsonObjectParam.getString(
                    JSONMapping.FORM_DESCRIPTION));
        }

        //Date Created...
        if(jsonObjectParam.isNull(JSONMapping.DATE_CREATED))
        {
            this.setDateCreated(null);
        }
        else {
            this.setDateCreated(new Date(
                    jsonObjectParam.getLong(JSONMapping.DATE_CREATED)));
        }

        //Date Last Updated...
        if(jsonObjectParam.isNull(JSONMapping.DATE_LAST_UPDATED))
        {
            this.setDateLastUpdated(null);
        }
        else {
            this.setDateLastUpdated(new Date(
                    jsonObjectParam.getLong(JSONMapping.DATE_LAST_UPDATED)));
        }

        //Current User...
        if(jsonObjectParam.isNull(JSONMapping.CURRENT_USER))
        {
            this.setCurrentUser(null);
        }
        else {

            JSONObject currUserJsonObj =
                    jsonObjectParam.getJSONObject(JSONMapping.CURRENT_USER);

            User currentUser = new User();
            if(!currUserJsonObj.isNull(User.JSONMapping.Elastic.USER_ID))
            {
                currentUser.setId(currUserJsonObj.getLong(
                        User.JSONMapping.Elastic.USER_ID));
            }

            if(!currUserJsonObj.isNull(User.JSONMapping.USERNAME))
            {
                currentUser.setUsername(currUserJsonObj.getString(
                        User.JSONMapping.USERNAME));
            }

            this.setCurrentUser(currentUser);
        }

        //Form Fields...
        if(formFieldsParam != null && !formFieldsParam.isEmpty())
        {
            List<Field> fieldsToSet = new ArrayList();

            for(Field formField : formFieldsParam)
            {
                Field fieldToAdd = formField.populateFromElasticSearchJson(jsonObjectParam);
                if(fieldToAdd == null)
                {
                    continue;
                }

                fieldsToSet.add(fieldToAdd);
            }

            //Confirm there are values...
            if(fieldsToSet.isEmpty())
            {
                this.setFormFields(null);
            }
            else {
                this.setFormFields(fieldsToSet);
            }
        }
        else {
            this.setFormFields(null);
        }

        //Ancestor...
        if(jsonObjectParam.isNull(JSONMapping.ANCESTOR_ID))
        {
            this.setAncestorId(null);
        }
        else {
            this.setAncestorId(jsonObjectParam.getLong(
                    JSONMapping.ANCESTOR_ID));
        }

        //Table Field Parent Id...
        if(jsonObjectParam.isNull(JSONMapping.TABLE_FIELD_PARENT_ID))
        {
            this.setTableFieldParentId(null);
        }
        else {
            this.setTableFieldParentId(jsonObjectParam.getLong(
                    JSONMapping.TABLE_FIELD_PARENT_ID));
        }

        //Descendant Ids...
        if(jsonObjectParam.isNull(JSONMapping.DESCENDANT_IDS))
        {
            this.setDescendantIds(null);
        }
        else {
            JSONArray jsonArray = jsonObjectParam.getJSONArray(
                    JSONMapping.DESCENDANT_IDS);
            List<Long> descendantIds = new ArrayList();
            for(int index = 0;index < jsonArray.length();index++)
            {
                descendantIds.add(jsonArray.getLong(index));
            }

            if(descendantIds.isEmpty())
            {
                this.setDescendantIds(null);
            }
            else {
                this.setDescendantIds(descendantIds);
            }
        }
    }

    /**
     * Prints all the Fields and their values to the standard
     * {@code System.out}.
     *
     */
    @XmlTransient
    public void printFormFields()
    {
        System.out.println("\n\n*** PRINTING FORM FIELDS ***");
        if(this.getFormFields() != null)
        {
            for(com.fluid.program.api.vo.Field formField : this.getFormFields())
            {
                System.out.println("Field Exists: '"+formField.getFieldName()+"' with value: "+
                        formField.getFieldValue());

                if(formField.getTypeAsEnum() == com.fluid.program.api.vo.Field.Type.MultipleChoice)
                {
                    if(formField.getFieldValue() instanceof MultiChoice)
                    {
                        MultiChoice casted = (MultiChoice)formField.getFieldValue();

                        if(casted.getSelectedMultiChoices() == null)
                        {
                            continue;
                        }

                        for(String selected : casted.getSelectedMultiChoices())
                        {
                            System.out.println("--> Selected: "+selected);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all the {@code Form} {@code Field}s.
     *
     * @return All the Form Fields.
     *
     * @see Field
     */
    public List<Field> getFormFields() {
        return this.formFields;
    }

    /**
     * Sets all the {@code Form}{@code Field}s.
     *
     * @param formFieldsParam The new {@code Form}{@code Field}s.
     *
     * @see Field
     */
    public void setFormFields(List<Field> formFieldsParam) {
        this.formFields = formFieldsParam;
    }

    /**
     * Gets the Form Type / Form Definition of {@code this} {@code Form}.
     *
     * @return Form Type / Form Definition.
     */
    public String getFormType() {
        return this.formType;
    }

    /**
     * Sets the Form Type / Form Definition of {@code this} {@code Form}.
     * 
     * @param formTypeParam Form Type / Form Definition.
     */
    public void setFormType(String formTypeParam) {
        this.formType = formTypeParam;
    }

    /**
     * Gets the Form Type / Form Definition Id of {@code this} {@code Form}.
     *
     * @return Form Type / Form Definition Id.
     */
    public Long getFormTypeId() {
        return this.formTypeId;
    }

    /**
     * Sets the Form Type / Form Definition Id of {@code this} {@code Form}.
     *
     * @param formTypeIdParam Form Type / Form Definition Id.
     */
    public void setFormTypeId(Long formTypeIdParam) {
        this.formTypeId = formTypeIdParam;
    }

    /**
     * Gets the Form Title as in Fluid.
     *
     * @return Form Title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * <p>Sets the Form Title as in Fluid.
     *
     * <p>
     *     If {@code titleParam} is {@code null} or empty, the
     *     Title will be set to <br>
     *     "[No Title from Custom Program]"
     *
     * @param titleParam Form Title.
     */
    public void setTitle(String titleParam) {

        if(titleParam == null || titleParam.trim().isEmpty())
        {
            this.title = EMPTY_TITLE_MARKER;
            return;
        }

        this.title = titleParam;
    }

    /**
     * Gets the Flow State as in Fluid.
     *
     * @return Flow State.
     */
    public String getFlowState() {
        return this.flowState;
    }

    /**
     * <p>Sets the Flow State as in Fluid.
     *
     * @param flowStateParam Flow State.
     */
    public void setFlowState(String flowStateParam) {
        this.flowState = flowStateParam;
    }

    /**
     * Gets the State as in Fluid.
     *
     * @return Flow State.
     */
    public String getState() {
        return this.state;
    }

    /**
     * <p>Sets the State as in Fluid.
     *
     * @param stateParam State.
     */
    public void setState(String stateParam) {
        this.state = stateParam;
    }

    /**
     * Gets the Current User as in Fluid.
     *
     * @return Current User.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * <p>Sets the Current User as in Fluid.
     *
     * @param currentUserParam Current User.
     */
    public void setCurrentUser(User currentUserParam) {
        this.currentUser = currentUserParam;
    }

    /**
     * Gets The {@code Date} the Electronic Form / Form Definition
     * was created.
     *
     * @return Date Created.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets The {@code Date} the Electronic Form / Form Definition
     * was created.
     *
     * @param dateCreatedParam Date Created.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * Gets The {@code Date} the Electronic Form / Form Definition
     * was last updated.
     *
     * @return Date Last Updated.
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     * Sets The {@code Date} the Electronic Form / Form Definition
     * was last updated.
     *
     * @param dateLastUpdatedParam Date Last Updated.
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     * Gets the Electronic Form description.
     *
     * @return Electronic Form description.
     */
    public String getFormDescription() {
        return this.formDescription;
    }

    /**
     * Sets the Electronic Form description.
     *
     * @param formDescriptionParam Electronic Form description.
     */
    public void setFormDescription(String formDescriptionParam) {
        this.formDescription = formDescriptionParam;
    }

    /**
     * Gets the {@code List<Flow>} of Flows associated with {@code this}
     * Form Type.
     *
     * @return List of Associated Flows.
     *
     * @see Flow
     */
    public List<Flow> getAssociatedFlows() {
        return this.associatedFlows;
    }

    /**
     * Sets the {@code List<Flow>} of Flows associated with {@code this}
     * Form Type.
     *
     * @param associatedFlowsParam List of Associated Flows.
     *
     * @see Flow
     */
    public void setAssociatedFlows(List<Flow> associatedFlowsParam) {
        this.associatedFlows = associatedFlowsParam;
    }

    /**
     * Gets the Ancestor Id as a {@code Long} for {@code this} {@code Form}.
     *
     * @return Ancestor Id.
     */
    public Long getAncestorId() {
        return this.ancestorId;
    }

    /**
     * Sets the Ancestor Id as a {@code Long} for {@code this} {@code Form}.
     *
     * @param ancestorIdParam Ancestor Id.
     */
    public void setAncestorId(Long ancestorIdParam) {
        this.ancestorId = ancestorIdParam;
    }

    /**
     * Gets the Table Field Parent Id as a {@code Long} for {@code this} {@code Form}.
     *
     * @return Table Field Parent Id.
     */
    public Long getTableFieldParentId() {
        return this.tableFieldParentId;
    }

    /**
     * Sets the Table Field Parent Id as a {@code Long} for {@code this} {@code Form}.
     *
     * @param tableFieldParentIdParam Table Field Parent Id.
     */
    public void setTableFieldParentId(Long tableFieldParentIdParam) {
        this.tableFieldParentId = tableFieldParentIdParam;
    }

    /**
     * Gets the Descendant Ids as a {@code List<Long>} for {@code this} {@code Form}.
     *
     * @return List of Descendant Form Ids.
     */
    public List<Long> getDescendantIds() {
        return this.descendantIds;
    }

    /**
     * Sets the Descendant Ids as a {@code List<Long>} for {@code this} {@code Form}.
     *
     * @param descendantIdsParam List of Descendant Form Ids.
     */
    public void setDescendantIds(List<Long> descendantIdsParam) {
        this.descendantIds = descendantIdsParam;
    }
}
