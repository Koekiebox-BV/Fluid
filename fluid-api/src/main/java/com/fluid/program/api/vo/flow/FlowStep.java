package com.fluid.program.api.vo.flow;

import java.util.Date;
import java.util.List;

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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public String getFlowStepType() {
        return flowStepType;
    }

    public void setFlowStepType(String flowStepType) {
        this.flowStepType = flowStepType;
    }

    public List<FlowStepRule> getEntryRules() {
        return entryRules;
    }

    public void setEntryRules(List<FlowStepRule> entryRules) {
        this.entryRules = entryRules;
    }

    public List<FlowStepRule> getExitRules() {
        return exitRules;
    }

    public void setExitRules(List<FlowStepRule> exitRules) {
        this.exitRules = exitRules;
    }

    public List<FlowStepRule> getViewRules() {
        return viewRules;
    }

    public void setViewRules(List<FlowStepRule> viewRules) {
        this.viewRules = viewRules;
    }

    public List<StepProperty> getStepProperties() {
        return stepProperties;
    }

    public void setStepProperties(List<StepProperty> stepProperties) {
        this.stepProperties = stepProperties;
    }
}
