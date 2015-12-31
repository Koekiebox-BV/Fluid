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
 *
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
	 * 
	 */
    public Form() {
        super();
    }

    /**
     *
     * @param fieldIdParam
     */
    public Form(Long fieldIdParam) {
        super();

        this.setId(fieldIdParam);
    }

    /**
     *
     * @param jsonObjectParam
     */
    public Form(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

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

            List<Flow> assFlowsObj = new ArrayList<Flow>();
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

            List<Field> assFormFields = new ArrayList<Field>();
            for(int index = 0;index < formFieldsArr.length();index++)
            {
                assFormFields.add(new Field(formFieldsArr.getJSONObject(index)));
            }

            this.setFormFields(assFormFields);
        }
    }


    /**
     *
     * @param formTypeParam
     */
    public Form(String formTypeParam) {
        this.setFormType(formTypeParam);
    }

    /**
     *
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
     * 
     * @return
     */
    public List<Field> getFormFields() {
        return this.formFields;
    }

    /**
     * 
     * @param formFieldsParam
     */
    public void setFormFields(List<Field> formFieldsParam) {
        this.formFields = formFieldsParam;
    }

    /**
     * 
     * @return
     */
    public String getFormType() {
        return this.formType;
    }

    /**
     * 
     * @param formTypeParam
     */
    public void setFormType(String formTypeParam) {
        this.formType = formTypeParam;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @param titleParam
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
     *
     * @return
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     *
     * @param dateCreatedParam
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     *
     * @return
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     *
     * @param dateLastUpdatedParam
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     *
     * @return
     */
    public String getFormDescription() {
        return this.formDescription;
    }

    /**
     *
     * @param formDescriptionParam
     */
    public void setFormDescription(String formDescriptionParam) {
        this.formDescription = formDescriptionParam;
    }

    /**
     *
     * @return
     */
    public List<Flow> getAssociatedFlows() {
        return this.associatedFlows;
    }

    /**
     *
     * @param associatedFlowsParam
     */
    public void setAssociatedFlows(List<Flow> associatedFlowsParam) {
        this.associatedFlows = associatedFlowsParam;
    }

    /**
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
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
     *
     * @param fieldNameParam
     * @return
     */
    public Integer getFieldValueAsInt(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam);

        if(obj == null)
        {
            return null;
        }

        if(obj instanceof Short)
        {
            return ((Short)obj).intValue();
        }

        if(obj instanceof Integer)
        {
            return (Integer)obj;
        }

        if(obj instanceof Long)
        {
            return ((Long)obj).intValue();
        }

        if(obj instanceof Float)
        {
            return ((Float)obj).intValue();
        }

        if(obj instanceof Double)
        {
            return ((Double)obj).intValue();
        }

        return null;
    }

    /**
     *
     * @param fieldNameParam
     * @param fieldValueParam
     */
    public void setFieldValue(String fieldNameParam,Object fieldValueParam)
    {
        if(fieldNameParam == null || fieldNameParam.trim().length() == 0)
        {
            return;
        }

        if(this.getFormFields() == null || this.getFormFields().isEmpty())
        {
            this.setFormFields(new ArrayList<Field>());
        }

        String fieldNameParamLower = fieldNameParam.toLowerCase();

        boolean fieldFound = false;

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
                fieldFound = true;
                break;
            }
        }

        //When the Field is not added...
        if(!fieldFound)
        {
            this.getFormFields().add(new Field(fieldNameParam,fieldValueParam));
        }
    }

    /**
     *
     * @param fieldNameParam
     * @param fieldValueParam
     * @param typeParam
     */
    public void setFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) {
            return;
        }

        if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
            this.setFormFields(new ArrayList<Field>());
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
     *
     * @param formTypeParam
     * @return
     */
    public boolean isFormType(String formTypeParam) {
        //
        if (formTypeParam == null || formTypeParam.trim().isEmpty()) {
            return false;
        }

        //
        if (this.getFormType() == null || this.getFormType().trim().isEmpty()) {
            return false;
        }

        return formTypeParam.toLowerCase().equals(getFormType().toLowerCase());
    }

    /**
     *
     * @return
     * @throws org.json.JSONException
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
}
