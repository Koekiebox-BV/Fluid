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

package com.fluidbpm.program.api.vo.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.*;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fluid workflow Step that belongs to a Flow.
 *
 * @author jasonbruwer
 * @see Flow
 * @see FlowStepRule
 * @see ABaseFluidGSONObject
 * @since v1.0
 */
@Getter
@Setter
public class FlowStep extends ABaseFluidGSONObject {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    private Date dateCreated;
    private Date dateLastUpdated;
    private Flow flow;
    private String flowStepType;

    private List<FlowStepRule> entryRules;
    private List<FlowStepRule> exitRules;
    private List<FlowStepRule> viewRules;

    private List<StepProperty> stepProperties;

    private Long flowStepParentId;

    /**
     * The JSON mapping for the {@code FlowStep} object.
     */
    public static class JSONMapping {
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

        public static final String FLOW_STEP_PARENT_ID = "flowStepParentId";
    }

    /**
     * The Type of Step to create or update.
     */
    public static class StepType {
        public static final String INTRODUCTION = "Introduction";
        public static final String RE_ROUTE = "Re Route";
        public static final String EXIT = "Exit";
        public static final String ASSIGNMENT = "Assignment";
        public static final String MAIL_CAPTURE = "Mail Capture";
        public static final String DATABASE_CAPTURE = "Database Capture";
        public static final String SEND_MAIL = "Send Mail";
        public static final String POLLING = "Polling";
        public static final String JAVA_PROGRAM = "Java Program";
        public static final String CLONE_ITEM = "Clone Item";
    }

    /**
     * Additional properties applicable to specific <code>FlowStep</code> types.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class StepProperty extends ABaseFluidGSONObject {
        private String name;
        private String value;

        /**
         * A list of the available properties to set.
         */
        public static class PropName {
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
            public static final String ViewGroupWebKit = "ViewGroupWebKit";
            public static final String ViewGroupPriority = "ViewGroupPriority";

            //Send Mail...
            public static final String MailTemplateDescription = "MailTemplateDescription";
            public static final String MailTemplateSubject = "MailTemplateSubject";
            public static final String MailTemplateBodyType = "MailTemplateBodyType";
            public static final String MailTemplateBodyContent = "MailTemplateBodyContent";
            public static final String RecipientSyntax = "RecipientSyntax";
            public static final String MailTransfer = "MailTransfer";
            public static final String IncludeAttachments = "IncludeAttachments";

            //Polling...
            public static final String PollingType = "PollingType";

            //Java Program...
            public static final String TaskIdentifier = "TaskIdentifier";
            public static final String ProgramNameValueProperties = "ProgramNameValueProperties";
            /**
             * The following two values are allowed:
             * - Internal Custom Runner
             * - External Custom Runner
             */
            public static final String AutoCreateCustomRunnerType = "AutoCreateCustomRunnerType";

            //Clone Item...
            public static final String CloneAttachments = "CloneAttachments";
            public static final String SendCloneTo = "SendCloneTo";
            public static final String DestinationForm = "DestinationForm";

            //Document Template...
            public static final String FilenamePatternOutput = "FilenamePatternOutput";
            public static final String UserQueryTemplateLookup = "UserQueryTemplateLookup";
            public static final String ContentTypeTemplate = "ContentTypeTemplate";
            public static final String ContentTypeOutput = "ContentTypeOutput";
        }

        /**
         * The JSON mapping for the {@code StepProperty} object.
         */
        public static class JSONMapping {
            public static final String NAME = "name";
            public static final String VALUE = "value";
        }

        /**
         * Populates local variables with {@code jsonObjectParam}.
         *
         * @param jsonObjectParam The JSON Object.
         */
        public StepProperty(JsonObject jsonObjectParam) {
            super(jsonObjectParam);
            if (this.jsonObject == null) return;

            this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
            this.setValue(this.getAsStringNullSafe(JSONMapping.VALUE));
        }

        /**
         * Conversion to {@code JsonObject} from Java Object.
         *
         * @return {@code JsonObject} representation of {@code StepProperty}
         * 
         */
        @Override
        @XmlTransient
        @JsonIgnore
        public JsonObject toJsonObject() {
            JsonObject returnVal = super.toJsonObject();
            returnVal.addProperty(JSONMapping.NAME, this.getName());
            returnVal.addProperty(JSONMapping.VALUE, this.getValue());
            return returnVal;
        }
    }

    /**
     * Default constructor.
     */
    public FlowStep() {
        super();
    }

    /**
     * Unique identifier of the Flow Step.
     *
     * @param flowStepIdParam Flow Step Primary key.
     */
    public FlowStep(Long flowStepIdParam) {
        super();
        this.setId(flowStepIdParam);
    }

    /**
     * Flow Step name constructor.
     *
     * @param flowStepName Flow Step Name.
     */
    public FlowStep(String flowStepName) {
        super();
        this.setName(flowStepName);
    }

    /**
     * Flow Step name and flow constructor.
     *
     * @param flowStepName Flow Step Name.
     * @param flow         Flow Step workflow.
     */
    public FlowStep(String flowStepName, Flow flow) {
        this(flowStepName);
        this.setFlow(flow);
    }

    /**
     * Flow Step name and description constructor.
     *
     * @param flowStepName        Flow Step name.
     * @param flowStepDescription Flow Step description.
     */
    public FlowStep(String flowStepName, String flowStepDescription) {
        this(flowStepName);
        this.setDescription(flowStepDescription);
    }

    /**
     * Flow Step name and flow constructor.
     *
     * @param flowStepName        Flow Step Name.
     * @param flowStepDescription Flow Step description.
     * @param flow                Flow Step workflow.
     */
    public FlowStep(String flowStepName, String flowStepDescription, Flow flow) {
        this(flowStepName, flowStepDescription);
        this.setFlow(flow);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FlowStep(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
        this.setFlow(this.extractObject(JSONMapping.FLOW, Flow::new));
        this.setFlowStepType(this.getAsStringNullSafe(JSONMapping.FLOW_STEP_TYPE));
        this.setFlowStepParentId(this.getAsLongNullSafe(JSONMapping.FLOW_STEP_PARENT_ID));
        this.setEntryRules(this.extractObjects(JSONMapping.ENTRY_RULES, FlowStepRule::new));
        this.setExitRules(this.extractObjects(JSONMapping.EXIT_RULES, FlowStepRule::new));
        this.setViewRules(this.extractObjects(JSONMapping.VIEW_RULES, FlowStepRule::new));
        this.setStepProperties(this.extractObjects(JSONMapping.STEP_PROPERTIES, StepProperty::new));
    }

    /**
     * Sets the property value of the step with
     * property name {@code nameParam}.
     *
     * @param name  The property name.
     * @param value The property value.
     */
    public void setStepProperty(String name, String value) {
        if (this.getStepProperties() == null) this.setStepProperties(new ArrayList<>());
        if (name == null || name.trim().isEmpty()) return;
        if (value.trim().isEmpty()) return;

        String paramLower = name.toLowerCase();
        for (StepProperty existingProp : this.getStepProperties()) {
            if (existingProp.getName().toLowerCase().equals(paramLower)) {
                existingProp.setValue(value);
                return;
            }
        }
        this.getStepProperties().add(new StepProperty(name, value));
    }

    /**
     * Gets the step property with name {@code nameParam}
     *
     * @param name The property to retrieve.
     * @return Value of the property with name {@code nameParam}.
     */
    public String getStepProperty(String name) {
        if (this.getStepProperties() == null || this.getStepProperties().isEmpty()) return null;

        if (name == null || name.trim().isEmpty()) return null;

        String paramLower = name.toLowerCase();

        for (StepProperty stepProperty : this.getStepProperties()) {
            if (stepProperty.getName().toLowerCase().equals(paramLower)) return stepProperty.getValue();
        }

        return null;
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FlowStep}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        returnVal.addProperty(JSONMapping.NAME, this.getName());
        returnVal.addProperty(JSONMapping.DESCRIPTION, this.getDescription());
        returnVal.addProperty(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(this.getDateCreated()));
        returnVal.addProperty(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(this.getDateLastUpdated()));
        returnVal.add(JSONMapping.FLOW, this.getFlow().toJsonObject());
        returnVal.addProperty(JSONMapping.FLOW_STEP_TYPE, this.getFlowStepType());
        returnVal.addProperty(JSONMapping.FLOW_STEP_PARENT_ID, this.getFlowStepParentId());
        returnVal.add(JSONMapping.ENTRY_RULES, this.toJsonObjArray(this.getEntryRules()));
        returnVal.add(JSONMapping.EXIT_RULES, this.toJsonObjArray(this.getExitRules()));
        returnVal.add(JSONMapping.VIEW_RULES, this.toJsonObjArray(this.getViewRules()));
        returnVal.add(JSONMapping.STEP_PROPERTIES, this.toJsonObjArray(this.getStepProperties()));

        return returnVal;
    }
}