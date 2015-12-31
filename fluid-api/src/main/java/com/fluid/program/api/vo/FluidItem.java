package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 */
public class FluidItem extends ABaseFluidJSONObject {

    private Properties customProperties;

    private List<Field> userFields;
    private List<Field> routeFields;
    private List<Field> globalFields;

    private Form form;

    private List<Attachment> attachments;

    private FlowState flowState;

    private String flow;

    private Boolean inCaseOfCreateLinkToParent;

    private Form tableFieldParentForm;
    private String tableFieldNameOnParentForm;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String CUSTOM_PROPERTIES = "customProperties";
        public static final String USER_FIELDS = "userFields";
        public static final String ROUTE_FIELDS = "routeFields";
        public static final String GLOBAL_FIELDS = "globalFields";
        public static final String FORM = "form";
        public static final String ATTACHMENTS = "attachments";
        public static final String FLOW_STATE = "flowState";

        public static final String FLOW = "flow";
    }

    /**
     *
     */
    public FluidItem() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public FluidItem(JSONObject jsonObjectParam) throws JSONException {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Custom Properties...
        if (!this.jsonObject.isNull(JSONMapping.CUSTOM_PROPERTIES)) {
            JSONArray jsonPropArray = this.jsonObject.getJSONArray(JSONMapping.CUSTOM_PROPERTIES);

            for(int customPropIndex = 0;customPropIndex < jsonPropArray.length();customPropIndex++)
            {
                Object objAtIndex = jsonPropArray.get(customPropIndex);


            }
        }
    }

    /**
	 * 
	 */
    public enum FlowState {
        NotInFlow,
        WorkInProgress,
        UserSend,
        UserSendWorkInProgress,
        Archive
    }

    /**
     *
     * @param fieldNameParam
     * @return
     */
    public String getUserFieldValueAsString(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(
                fieldNameParam,this.getUserFields());

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
    public Object getUserFieldValue(String fieldNameParam)
    {
        return this.getFieldValueForField(fieldNameParam, this.getUserFields());
    }

    /**
     *
     * @param fieldNameParam
     * @return
     */
    public Object getRouteFieldValue(String fieldNameParam)
    {
        return this.getFieldValueForField(fieldNameParam, this.getRouteFields());
    }

    /**
     *
     * @param fieldNameParam
     * @return
     */
    public Object getGlobalFieldValue(String fieldNameParam)
    {
        return this.getFieldValueForField(fieldNameParam,this.getGlobalFields());
    }

    /**
     *
     * @param fieldNameParam
     * @return
     */
    public String getRouteFieldValueAsString(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(
                fieldNameParam,this.getRouteFields());

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
    public Double getRouteFieldValueAsDouble(String fieldNameParam)
    {
        Object obj = this.getRouteFieldValue(fieldNameParam);

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
    public String getGlobalFieldValueAsString(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(
                fieldNameParam, this.getGlobalFields());

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
    public Double getGlobalFieldValueAsDouble(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam, this.getGlobalFields());

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
    public Integer getGlobalFieldValueAsInt(String fieldNameParam)
    {
        Object obj = this.getFieldValueForField(fieldNameParam,this.getGlobalFields());

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
    public void setGlobalFieldValue(String fieldNameParam,Object fieldValueParam)
    {
        if(fieldNameParam == null || fieldNameParam.trim().length() == 0)
        {
            return;
        }

        if(this.getGlobalFields() == null || this.getGlobalFields().isEmpty())
        {
            this.setGlobalFields(new ArrayList<Field>());
        }

        String fieldNameParamLower = fieldNameParam.toLowerCase();

        boolean fieldFound = false;

        for(Field field : this.getGlobalFields())
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
            this.getGlobalFields().add(new Field(fieldNameParam,fieldValueParam));
        }
    }

    /**
     *
     * @param fieldNameParam
     * @param fieldValueParam
     * @param typeParam
     */
    public void setGlobalFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) {
            return;
        }

        if (this.getGlobalFields() == null || this.getGlobalFields().isEmpty()) {
            this.setGlobalFields(new ArrayList<Field>());
        }

        String paramLower = fieldNameParam.toLowerCase().trim();

        boolean valueFound = false;

        //Iterate the Form Fields...
        int fieldIndex = -1;
        for (Field field : this.getGlobalFields()) {
            fieldIndex++;

            String toCheckNameLower = field.getFieldName();
            if (toCheckNameLower == null || toCheckNameLower.trim().isEmpty()) {
                continue;
            }

            toCheckNameLower = toCheckNameLower.trim().toLowerCase();

            if (paramLower.equals(toCheckNameLower)) {
                valueFound = true;
                this.getGlobalFields().get(fieldIndex).setFieldValue(fieldValueParam);
                this.getGlobalFields().get(fieldIndex).setTypeAsEnum(typeParam);
                break;
            }
        }

        if (!valueFound) {
            this.getGlobalFields().add(new Field(fieldNameParam, fieldValueParam, typeParam));
        }
    }

    /**
     *
     * @param fieldNameParam
     * @param toGetPropertyFromParam
     * @return
     */
    public Object getFieldValueForField(
            String fieldNameParam,List<Field> toGetPropertyFromParam) {

        if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) {
            return null;
        }

        if (toGetPropertyFromParam == null || toGetPropertyFromParam.isEmpty()) {
            return null;
        }

        String fieldNameParamLower = fieldNameParam.trim().toLowerCase();

        for (Field field : toGetPropertyFromParam) {

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
     * @return
     */
    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    /**
     *
     * @param attachmentsParam
     */
    public void setAttachments(List<Attachment> attachmentsParam) {
        this.attachments = attachmentsParam;
    }

    /**
     *
     * @param toAddParam
     */
    public void addAttachment(Attachment toAddParam) {
        if (!this.containsAttachments()) {
            this.setAttachments(new ArrayList<Attachment>());
        }

        this.getAttachments().add(toAddParam);
    }

    /**
     *
     * @return
     */
    public List<Field> getUserFields() {
        return this.userFields;
    }

    /**
     *
     * @param userFieldsParam
     */
    public void setUserFields(List<Field> userFieldsParam) {
        this.userFields = userFieldsParam;
    }

    /**
     *
     * @return
     */
    public List<Field> getRouteFields() {
        return this.routeFields;
    }

    /**
     *
     * @param routeFieldsParam
     */
    public void setRouteFields(List<Field> routeFieldsParam) {
        this.routeFields = routeFieldsParam;
    }

    /**
     *
     * @param routeFieldsParam
     */
    @JsonIgnore
    @XmlTransient
    public void setRouteFields(ArrayList<Field> routeFieldsParam) {
        this.routeFields = routeFieldsParam;
    }

    /**
     *
     * @return
     */
    public List<Field> getGlobalFields() {
        return this.globalFields;
    }

    /**
     *
     * @param globalFieldsParam
     */
    public void setGlobalFields(List<Field> globalFieldsParam) {
        this.globalFields = globalFieldsParam;
    }

    /**
     *
     * @return
     */
    public Form getForm() {
        return this.form;
    }

    /**
     *
     * @param formParam
     */
    public void setForm(Form formParam) {
        this.form = formParam;
    }

    /**
     *
     * @return
     */
    public Properties getCustomProperties() {
        return this.customProperties;
    }

    /**
     *
     * @param customPropertiesParam
     */
    public void setCustomProperties(Properties customPropertiesParam) {
        this.customProperties = customPropertiesParam;
    }

    /**
     * 
     * @return
     */
    public FlowState getFlowState() {
        return this.flowState;
    }

    /**
     * 
     * @param flowStateParam
     */
    public void setFlowState(FlowState flowStateParam) {
        this.flowState = flowStateParam;
    }

    /**
     * 
     * @param flowStateParam
     */
    @JsonIgnore
    @XmlTransient
    public void setFlowState(String flowStateParam) {
        this.flowState = FlowState.valueOf(flowStateParam);
    }

    /**
     * 
     * @return
     */
    public boolean containsAttachments() {
        return (this.attachments == null || this.attachments.isEmpty()) ? false : true;
    }

    /**
     * 
     * @param propertyNameParam
     * @return
     */
    public boolean isPropertySet(String propertyNameParam) {
        if (propertyNameParam == null || propertyNameParam.trim().isEmpty()) {
            return false;
        }

        if (this.customProperties == null || this.customProperties.isEmpty()) {
            return false;
        }

        String propVal = this.customProperties.getProperty(propertyNameParam);

        if (propVal == null || propVal.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param propertyNameParam
     * @return
     */
    public String getStringProperty(String propertyNameParam) {
        if (this.customProperties == null) {
            return null;
        }

        return this.customProperties.getProperty(propertyNameParam);
    }

    /**
     *
     * @param propertyNameParam
     * @param propertyValueParam
     */
    public void setStringProperty(String propertyNameParam,String propertyValueParam)
    {
        if (this.customProperties == null) {
            this.customProperties = new Properties();
        }

        //Validate Property Name...
        if(propertyNameParam == null || propertyNameParam.trim().isEmpty())
        {
            return;
        }

        if(propertyValueParam == null)
        {
            propertyValueParam = "";
        }

        this.customProperties.setProperty(propertyNameParam,propertyValueParam);
    }

    /**
     * 
     * @param fieldNameParam
     * @param fieldValueParam
     * @param typeParam
     */
    public void setRouteFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
        if (fieldNameParam == null) {
            return;
        }

        if (this.routeFields == null) {
            this.routeFields = new ArrayList<Field>();
        }

        String paramLower = fieldNameParam.toLowerCase().trim();

        boolean valueFound = false;

        //Iterate the Route Fields...
        for (Field existingField : this.routeFields) {
            String toCheckNameLower = existingField.getFieldName();
            if (toCheckNameLower == null || toCheckNameLower.trim().isEmpty()) {
                continue;
            }

            toCheckNameLower = toCheckNameLower.trim().toLowerCase();
            if (paramLower.equals(toCheckNameLower)) {
                valueFound = true;

                existingField.setTypeAsEnum(typeParam);
                existingField.setFieldValue(fieldValueParam);
                break;
            }
        }

        if (!valueFound) {
            this.routeFields.add(new Field(fieldNameParam, fieldValueParam, typeParam));
        }
    }

    /**
     *
     * @return
     */
    public String getFlow() {
        return this.flow;
    }

    /**
     *
     * @param flowParam
     */
    public void setFlow(String flowParam) {
        this.flow = flowParam;
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

        //Custom Properties...
        if(this.getCustomProperties() != null)
        {
            returnVal.put(JSONMapping.CUSTOM_PROPERTIES,this.getCustomProperties());
        }
        //Flow...
        if(this.getFlow() != null)
        {
            returnVal.put(JSONMapping.FLOW,this.getFlow());
        }
        //Form...
        if(this.getForm() != null)
        {
            returnVal.put(JSONMapping.FORM,this.getForm().toJsonObject());
        }
        //Attachments...
        if(this.getAttachments() != null)
        {
            JSONArray jsonArray = new JSONArray();

            for(Attachment toAdd : this.getAttachments())
            {
                jsonArray.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.ATTACHMENTS,jsonArray);
        }
        //Flow State...
        if(this.getFlowState() != null)
        {
            returnVal.put(JSONMapping.FLOW_STATE,this.getFlowState().toString());
        }

        return returnVal;
    }

    /**
     *
     * @return
     */
    public Boolean getInCaseOfCreateLinkToParent() {
        return this.inCaseOfCreateLinkToParent;
    }

    /**
     *
     * @param inCaseOfCreateLinkToParentParam
     */
    public void setInCaseOfCreateLinkToParent(Boolean inCaseOfCreateLinkToParentParam) {
        this.inCaseOfCreateLinkToParent = inCaseOfCreateLinkToParentParam;
    }

    /**
     *
     * @return
     */
    public Form getTableFieldParentForm() {
        return this.tableFieldParentForm;
    }

    /**
     *
     * @param tableFieldParentFormParam
     */
    public void setTableFieldParentForm(Form tableFieldParentFormParam) {
        this.tableFieldParentForm = tableFieldParentFormParam;
    }

    /**
     *
     * @return
     */
    public String getTableFieldNameOnParentForm() {
        return this.tableFieldNameOnParentForm;
    }

    /**
     *
     * @param tableFieldNameParam
     */
    public void setTableFieldNameOnParentForm(String tableFieldNameParam) {
        this.tableFieldNameOnParentForm = tableFieldNameParam;
    }
}
