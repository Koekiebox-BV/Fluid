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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.flow.Flow;

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
public class Form extends ABaseFluidJSONObject {

    private String formType;
    private String formDescription;

    private String title;

    private Date dateCreated;
    private Date dateLastUpdated;

    private List<Field> formFields;
    private List<Flow> associatedFlows;

    private static final String EMPTY_TITLE_MARKER = "[No Title from Custom Program]";

    /**
     * The JSON mapping for the {@code Form} object.
     */
    public static class JSONMapping
    {
        public static final String FORM_TYPE = "formType";
        public static final String FORM_DESCRIPTION = "formDescription";
        public static final String TITLE = "title";

        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";

        public static final String FORM_FIELDS = "formFields";
        public static final String ASSOCIATED_FLOWS = "associatedFlows";
    }

    /**
     * Default constructor.
	 * 
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

        //Form Type...
        if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE)) {
            this.setFormType(this.jsonObject.getString(JSONMapping.FORM_TYPE));
        }

        //Date Created...
        if (!this.jsonObject.isNull(JSONMapping.DATE_CREATED)) {
            this.setDateCreated(
                    this.getLongAsDateFromJson(
                            this.jsonObject.getLong(JSONMapping.DATE_CREATED)));
        }

        //Date Last Updated...
        if (!this.jsonObject.isNull(JSONMapping.DATE_LAST_UPDATED)) {
            this.setDateLastUpdated(
                    this.getLongAsDateFromJson(
                            this.jsonObject.getLong(JSONMapping.DATE_LAST_UPDATED)));
        }

        //Associated Flows...
        if (!this.jsonObject.isNull(JSONMapping.ASSOCIATED_FLOWS)) {

            JSONArray associatedJobsArr = this.jsonObject.getJSONArray(
                    JSONMapping.ASSOCIATED_FLOWS);

            List<Flow> assFlowsObj = new ArrayList<>();
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

            List<Field> assFormFields = new ArrayList<>();
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
     */
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
     * @return The value for the Form Field as {@code Date}.
     *
     * @see com.fluid.program.api.vo.Field.Type#DateTime
     */
    public Date getFieldValueAsDate(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof Date)
        {
            return (Date)obj;
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
    public void setFieldValue(String fieldNameParam, Object fieldValueParam)
    {
        if(fieldNameParam == null || fieldNameParam.trim().length() == 0)
        {
            return;
        }

        if(this.getFormFields() == null || this.getFormFields().isEmpty())
        {
            this.setFormFields(new ArrayList<>());
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
    public void setFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) {
            return;
        }

        if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
            this.setFormFields(new ArrayList<>());
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
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Form Type...
        if(this.getFormType() != null)
        {
            returnVal.put(JSONMapping.FORM_TYPE, this.getFormType());
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
     * Prints all the Fields and their values to the standard
     * {@code System.out}.
     *
     */
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
}
