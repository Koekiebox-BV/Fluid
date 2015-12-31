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
     *
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
     *
     */
    public static class StepProperty extends ABaseFluidJSONObject
    {
        private String name;
        private String value;

        /**
         *
         */
        public static class JSONMapping
        {
            public static final String NAME = "name";
            public static final String VALUE = "value";
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
                returnVal.put(JSONMapping.NAME,this.getName());
            }

            //Value...
            if(this.getValue() != null)
            {
                returnVal.put(JSONMapping.VALUE,this.getValue());
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
            this.setDateCreated(
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
