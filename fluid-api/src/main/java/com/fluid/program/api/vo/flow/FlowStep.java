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

package com.fluid.program.api.vo.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * @author jasonbruwer
 * @since 2014-07-01
 *
 * Represents a step in the <code>Flow</code> process.
 *
 */
public class FlowStep extends ABaseFluidJSONObject {

    private String name;
    private String description;
    private Date dateCreated;
    private Date dateLastUpdated;

    //
    private Flow flow;
    private String flowStepType;

    private List<FlowStepRule> entryRules;
    private List<FlowStepRule> exitRules;
    private List<FlowStepRule> viewRules;

    private List<StepProperty> stepProperties;

    /**
     * JSON Mapping for the object.
     */
    public static class JSONMapping
    {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
        public static final String FLOW = "flow";
        public static final String FLOW_STEP_TYPE = "flowStepType";
        public static final String ENTRY_RULES = "entryRules";
        public static final String EXIT_RULES = "exitRules";
        public static final String VIEW_RULES = "viewRules";
        public static final String STEP_PROPERTIES = "stepProperties";
    }

    /**
     * The Type of Step to create or update.
     */
    public static class StepType {
        public static final String INTRODUCTION = "Introduction";
        public static final String RE_route = "Re Route";
        public static final String EXIT = "Exit";
        public static final String ASSIGNMENT = "Assignment";
        public static final String MAIL_CAPTURE = "Mail Capture";
        public static final String DATABASE_CAPTURE = "Database Capture";
        public static final String SEND_MAIL = "Send Mail";
        public static final String TWEET = "Tweet";
        public static final String JAVA_PROGRAM = "Java Program";
        public static final String CLONE_ITEM = "Clone Item";

    }

    /**
     * Additional properties appicable to specrific <code>FlowStep</code> types.
     */
    public static class StepProperty extends ABaseFluidJSONObject
    {
        private String name;
        private String value;

        /**
         * A list of the available properties to set.
         */
        public static class PropName
        {
            //Assignment Step...
            public static final String ItemTimeoutDays = "ItemTimeoutDays";
            public static final String ItemTimeoutHours = "ItemTimeoutHours";
            public static final String ItemTimeoutMinutes = "ItemTimeoutMinutes";

            public static final String UserItemTimeoutDays = "UserItemTimeoutDays";
            public static final String UserItemTimeoutHours = "UserItemTimeoutHours";
            public static final String UserItemTimeoutMinutes = "UserItemTimeoutMinutes";

            public static final String PerformMeasureExpectedDailyAbsurd = "PerformMeasureExpectedDailyAbsurd";
            public static final String PerformMeasureExpectedDailyHigh = "PerformMeasureExpectedDailyHigh";
            public static final String PerformMeasureExpectedDailyLow = "PerformMeasureExpectedDailyLow";

            public static final String RouteFields = "RouteFields";
            public static final String ViewGroup = "ViewGroup";
        }

        /**
         * JSON Mapping for the object.
         */
        public static class JSONMapping
        {
            public static final String NAME = "name";
            public static final String VALUE = "value";
        }

        /**
         *
         */
        public StepProperty() {
            super();
        }

        /**
         *
         * @param nameParam
         * @param valueParam
         */
        public StepProperty(String nameParam, String valueParam) {

            this.setName(nameParam);
            this.setValue(valueParam);
        }

        /**
         *
         * @param jsonObjectParam
         */
        public StepProperty(JSONObject jsonObjectParam) {
            super(jsonObjectParam);

            if(this.jsonObject == null)
            {
                return;
            }

            //Name...
            if (!this.jsonObject.isNull(JSONMapping.NAME)) {
                this.setName(this.jsonObject.getString(JSONMapping.NAME));
            }

            //Value...
            if (!this.jsonObject.isNull(JSONMapping.VALUE)) {
                this.setValue(this.jsonObject.getString(JSONMapping.VALUE));
            }
        }

        /**
         *
         * @return
         */
        public String getName() {
            return this.name;
        }

        /**
         *
         * @param nameParam
         */
        public void setName(String nameParam) {
            this.name = nameParam;
        }

        /**
         *
         * @return
         */
        public String getValue() {
            return this.value;
        }

        /**
         *
         * @param valueParam
         */
        public void setValue(String valueParam) {
            this.value = valueParam;
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

            //Name...
            if(this.getName() != null)
            {
                returnVal.put(JSONMapping.NAME, this.getName());
            }

            //Value...
            if(this.getValue() != null)
            {
                returnVal.put(JSONMapping.VALUE, this.getValue());
            }

            return returnVal;
        }
    }

    /**
	 *
	 */
    public FlowStep() {
        super();
    }

    /**
     *
     * @param flowStepIdParam
     */
    public FlowStep(Long flowStepIdParam) {
        super();

        this.setId(flowStepIdParam);
    }

    /**
     *
     * @param jsonObjectParam
     */
    public FlowStep(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(JSONMapping.NAME));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
        }

        //Date Created...
        if (!this.jsonObject.isNull(JSONMapping.DATE_CREATED)) {
            this.setDateCreated(
                    this.getLongAsDateFromJson(this.jsonObject.getLong(JSONMapping.DATE_CREATED)));
        }

        //Date Last Updated...
        if (!this.jsonObject.isNull(JSONMapping.DATE_LAST_UPDATED)) {
            this.setDateLastUpdated(
                    this.getLongAsDateFromJson(this.jsonObject.getLong(JSONMapping.DATE_LAST_UPDATED)));
        }

        //Flow...
        if (!this.jsonObject.isNull(JSONMapping.FLOW)) {
            this.setFlow(new Flow(this.jsonObject.getJSONObject(JSONMapping.FLOW)));
        }

        //Flow Step Type...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_TYPE)) {
            this.setFlowStepType(this.jsonObject.getString(JSONMapping.FLOW_STEP_TYPE));
        }

        //Entry Rules...
        if (!this.jsonObject.isNull(JSONMapping.ENTRY_RULES)) {

            JSONArray entryRules = this.jsonObject.getJSONArray(JSONMapping.ENTRY_RULES);

            List<FlowStepRule> listOfRules = new ArrayList<FlowStepRule>();
            for(int index = 0;index < entryRules.length();index++)
            {
                listOfRules.add(new FlowStepRule(entryRules.getJSONObject(index)));
            }

            this.setEntryRules(listOfRules);
        }

        //Exit Rules...
        if (!this.jsonObject.isNull(JSONMapping.EXIT_RULES)) {

            JSONArray exitRules = this.jsonObject.getJSONArray(JSONMapping.EXIT_RULES);

            List<FlowStepRule> listOfRules = new ArrayList<FlowStepRule>();
            for(int index = 0;index < exitRules.length();index++)
            {
                listOfRules.add(new FlowStepRule(exitRules.getJSONObject(index)));
            }

            this.setExitRules(listOfRules);
        }

        //View Rules...
        if (!this.jsonObject.isNull(JSONMapping.VIEW_RULES)) {

            JSONArray viewRules = this.jsonObject.getJSONArray(JSONMapping.VIEW_RULES);

            List<FlowStepRule> listOfRules = new ArrayList<FlowStepRule>();
            for(int index = 0;index < viewRules.length();index++)
            {
                listOfRules.add(new FlowStepRule(viewRules.getJSONObject(index)));
            }

            this.setViewRules(listOfRules);
        }

        //Step Properties...
        if (!this.jsonObject.isNull(JSONMapping.STEP_PROPERTIES)) {
            JSONArray stepProperties = this.jsonObject.getJSONArray(JSONMapping.STEP_PROPERTIES);

            List<StepProperty> listOfStepProps = new ArrayList<StepProperty>();
            for(int index = 0;index < stepProperties.length();index++)
            {
                listOfStepProps.add(new StepProperty(stepProperties.getJSONObject(index)));
            }

            this.setStepProperties(listOfStepProps);
        }
    }

    /**
     *
     * @param nameParam
     * @param valueParam
     */
    public void setStepProperty(
            String nameParam, String valueParam)
    {
        if(this.getStepProperties() == null)
        {
            this.setStepProperties(new ArrayList<StepProperty>());
        }

        if(nameParam == null || nameParam.trim().isEmpty())
        {
            return;
        }

        if(valueParam.trim().isEmpty())
        {
            return;
        }

        String paramLower = nameParam.toLowerCase();

        for(StepProperty existingProp : this.getStepProperties())
        {
            if(existingProp.getName().toLowerCase().equals(paramLower))
            {
                existingProp.setValue(valueParam);
                return;
            }
        }

        this.getStepProperties().add(new StepProperty(nameParam, valueParam));
    }

    /**
     *
     * @param nameParam
     * @return
     */
    public String getStepProperty(String nameParam)
    {
        if(this.getStepProperties() == null || this.getStepProperties().isEmpty())
        {
            return null;
        }

        if(nameParam == null || nameParam.trim().isEmpty())
        {
            return null;
        }

        String paramLower = nameParam.toLowerCase();

        for(StepProperty stepProperty : this.getStepProperties())
        {
            if(stepProperty.getName().toLowerCase().equals(paramLower))
            {
                return stepProperty.getValue();
            }
        }

        return null;
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

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME,this.getName());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION,this.getDescription());
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

        //Flow...
        if(this.getFlow() != null)
        {
            returnVal.put(JSONMapping.FLOW,this.getFlow().toJsonObject());
        }

        //Flow Step Type...
        if(this.getFlowStepType() != null)
        {
            returnVal.put(JSONMapping.FLOW_STEP_TYPE,this.getFlowStepType());
        }

        //Entry Rules...
        if(this.getEntryRules() != null && !this.getEntryRules().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(FlowStepRule rule : this.getEntryRules())
            {
                jsonArray.put(rule.toJsonObject());
            }

            returnVal.put(JSONMapping.ENTRY_RULES, jsonArray);
        }

        //Exit Rules...
        if(this.getExitRules() != null && !this.getExitRules().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(FlowStepRule rule : this.getExitRules())
            {
                jsonArray.put(rule.toJsonObject());
            }

            returnVal.put(JSONMapping.EXIT_RULES, jsonArray);
        }

        //View Rules...
        if(this.getViewRules() != null && !this.getViewRules().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(FlowStepRule rule : this.getViewRules())
            {
                jsonArray.put(rule.toJsonObject());
            }

            returnVal.put(JSONMapping.VIEW_RULES, jsonArray);
        }

        //Step Properties...
        if(this.getStepProperties() != null && !this.getStepProperties().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(StepProperty stepProperty : this.getStepProperties())
            {
                jsonArray.put(stepProperty.toJsonObject());
            }

            returnVal.put(JSONMapping.STEP_PROPERTIES, jsonArray);
        }

        return returnVal;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param nameParam
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @param descriptionParam
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
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
    public Flow getFlow() {
        return this.flow;
    }

    /**
     *
     * @param flowParam
     */
    public void setFlow(Flow flowParam) {
        this.flow = flowParam;
    }

    /**
     *
     * @return
     */
    public String getFlowStepType() {
        return this.flowStepType;
    }

    /**
     *
     * @param flowStepTypeParam
     */
    public void setFlowStepType(String flowStepTypeParam) {
        this.flowStepType = flowStepTypeParam;
    }

    /**
     *
     * @return
     */
    public List<FlowStepRule> getEntryRules() {
        return this.entryRules;
    }

    /**
     *
     * @param entryRulesParam
     */
    public void setEntryRules(List<FlowStepRule> entryRulesParam) {
        this.entryRules = entryRulesParam;
    }

    /**
     *
     * @return
     */
    public List<FlowStepRule> getExitRules() {
        return this.exitRules;
    }

    /**
     *
     * @param exitRulesParam
     */
    public void setExitRules(List<FlowStepRule> exitRulesParam) {
        this.exitRules = exitRulesParam;
    }

    /**
     *
     * @return
     */
    public List<FlowStepRule> getViewRules() {
        return this.viewRules;
    }

    /**
     *
     * @param viewRulesParam
     */
    public void setViewRules(List<FlowStepRule> viewRulesParam) {
        this.viewRules = viewRulesParam;
    }

    /**
     *
     * @return
     */
    public List<StepProperty> getStepProperties() {
        return this.stepProperties;
    }

    /**
     *
     * @param stepPropertiesParam
     */
    public void setStepProperties(List<StepProperty> stepPropertiesParam) {
        this.stepProperties = stepPropertiesParam;
    }
}
