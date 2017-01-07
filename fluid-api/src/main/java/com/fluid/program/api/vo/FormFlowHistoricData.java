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

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.user.User;

/**
 * <p>
 * Value Object for Fluid Item Historic information.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see FormFlowHistoricDataContainer
 */
public class FormFlowHistoricData extends ABaseFluidJSONObject {

    private Date dateCreated;
    private String ruleExecuted;
    private String ruleExecutedResult;

    private Long flowRuleOrder;

    // ------------- Relationships -------------//
    private String logEntryType;
    private User user;
    private FlowStep flowStep;

    private Form form;
    private String jobView;

    /**
     * The JSON mapping for the {@code FormFlowHistoricData} object.
     */
    public static class JSONMapping
    {
        public static final String DATE_CREATED = "dateCreated";
        public static final String RULE_EXECUTED = "ruleExecuted";
        public static final String RULE_EXECUTED_RESULT = "ruleExecutedResult";
        public static final String FLOW_RULE_ORDER = "flowRuleOrder";
        public static final String LOG_ENTRY_TYPE = "logEntryType";
        public static final String USER = "user";
        public static final String FLOW_STEP = "flowStep";
        public static final String FORM = "form";
        public static final String JOB_VIEW = "jobView";
    }

    /**
     * Default constructor.
     */
    public FormFlowHistoricData() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormFlowHistoricData(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_CREATED));

        //Rule Executed...
        if (!this.jsonObject.isNull(JSONMapping.RULE_EXECUTED)) {
            this.setRuleExecuted(this.jsonObject.getString(
                    JSONMapping.RULE_EXECUTED));
        }

        //Rule Executed Result...
        if (!this.jsonObject.isNull(JSONMapping.RULE_EXECUTED_RESULT)) {
            this.setRuleExecutedResult(this.jsonObject.getString(
                    JSONMapping.RULE_EXECUTED_RESULT));
        }

        //Rule Order...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_RULE_ORDER)) {
            this.setFlowRuleOrder(this.jsonObject.getLong(
                    JSONMapping.FLOW_RULE_ORDER));
        }

        //Rule Type...
        if (!this.jsonObject.isNull(JSONMapping.LOG_ENTRY_TYPE)) {
            this.setLogEntryType(this.jsonObject.getString(
                    JSONMapping.LOG_ENTRY_TYPE));
        }

        //User...
        if (!this.jsonObject.isNull(JSONMapping.USER)) {
            this.setUser(new User(this.jsonObject.getJSONObject(
                    JSONMapping.USER)));
        }

        //Flow Step...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP)) {
            this.setFlowStep(new FlowStep(
                    this.jsonObject.getJSONObject(JSONMapping.FLOW_STEP)));
        }

        //Form...
        if (!this.jsonObject.isNull(JSONMapping.FORM)) {
            this.setForm(new Form(this.jsonObject.getJSONObject(
                    JSONMapping.FORM)));
        }

        //Job View...
        if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW)) {
            this.setJobView(this.jsonObject.getString(JSONMapping.JOB_VIEW));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Rule Executed...
        if(this.getRuleExecuted() != null)
        {
            returnVal.put(JSONMapping.RULE_EXECUTED,
                    this.getRuleExecuted());
        }

        //Rule Executed Result...
        if(this.getRuleExecutedResult() != null)
        {
            returnVal.put(JSONMapping.RULE_EXECUTED_RESULT,
                    this.getRuleExecutedResult());
        }

        //Rule Order...
        if(this.getFlowRuleOrder() != null)
        {
            returnVal.put(JSONMapping.FLOW_RULE_ORDER,
                    this.getFlowRuleOrder());
        }

        //Log Entry Type...
        if(this.getLogEntryType() != null)
        {
            returnVal.put(JSONMapping.LOG_ENTRY_TYPE,
                    this.getLogEntryType());
        }

        //User...
        if(this.getUser() != null)
        {
            returnVal.put(JSONMapping.USER,
                    this.getUser().toJsonObject());
        }

        //Flow Step...
        if(this.getFlowStep() != null)
        {
            returnVal.put(JSONMapping.FLOW_STEP,
                    this.getFlowStep().toJsonObject());
        }

        //Form...
        if(this.getForm() != null)
        {
            returnVal.put(JSONMapping.FORM,
                    this.getForm().toJsonObject());
        }

        //Job View...
        if(this.getJobView() != null)
        {
            returnVal.put(JSONMapping.JOB_VIEW,
                    this.getJobView());
        }

        return returnVal;
    }

    /**
     * Gets Date Created.
     *
     * @return {@code Date} of when Historic entry was created.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets Date Created.
     *
     * @param dateCreatedParam {@code Date} of when Historic entry was created.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * Gets Rule Executed formula.
     *
     * @return {@code String} of rule executed.
     */
    public String getRuleExecuted() {
        return this.ruleExecuted;
    }

    /**
     * Sets Rule Executed formula.
     *
     * @param ruleExecutedParam {@code String} of rule executed.
     */
    public void setRuleExecuted(String ruleExecutedParam) {
        this.ruleExecuted = ruleExecutedParam;
    }

    /**
     * Gets Rule Executed Result.
     *
     * @return {@code String} of rule executed result / outcome.
     */
    public String getRuleExecutedResult() {
        return this.ruleExecutedResult;
    }

    /**
     * Sets Rule Executed Result.
     *
     * @param ruleExecutedResultParam {@code String} of rule executed result / outcome.
     */
    public void setRuleExecutedResult(String ruleExecutedResultParam) {
        this.ruleExecutedResult = ruleExecutedResultParam;
    }

    /**
     * Gets Rule Order in the step.
     *
     * @return Order in the Step of the rule executed.
     */
    public Long getFlowRuleOrder() {
        return this.flowRuleOrder;
    }

    /**
     * Sets Rule Order in the step.
     *
     * @param flowRuleOrderParam Order in the Step of the rule executed.
     */
    public void setFlowRuleOrder(Long flowRuleOrderParam) {
        this.flowRuleOrder = flowRuleOrderParam;
    }

    /**
     * Gets Type of Historic entry.
     *
     * @return Type of historic log entry.
     */
    public String getLogEntryType() {
        return this.logEntryType;
    }

    /**
     * Sets Type of Historic entry.
     *
     * @param logEntryTypeParam Type of historic log entry.
     */
    public void setLogEntryType(String logEntryTypeParam) {
        this.logEntryType = logEntryTypeParam;
    }

    /**
     * Gets {@code User} responsible for historic entry.
     *
     * @return Person or User responsible for historic data.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets {@code User} responsible for historic entry.
     *
     * @param userParam Person or User responsible for historic data.
     */
    public void setUser(User userParam) {
        this.user = userParam;
    }

    /**
     * Gets {@code FlowStep} historic entry and rule belongs to.
     *
     * @return The {@code FlowStep} the rule belongs to.
     */
    public FlowStep getFlowStep() {
        return this.flowStep;
    }

    /**
     * Sets {@code FlowStep} historic entry and rule belongs to.
     *
     * @param flowStepParam The {@code FlowStep} the rule belongs to.
     */
    public void setFlowStep(FlowStep flowStepParam) {
        this.flowStep = flowStepParam;
    }

    /**
     * Gets {@code Form} historic entry belongs to.
     *
     * @return The {@code Form} the rule belongs to.
     */
    public Form getForm() {
        return this.form;
    }

    /**
     * Sets {@code Form} historic entry belongs to.
     *
     * @param formParam The {@code Form} the rule belongs to.
     */
    public void setForm(Form formParam) {
        this.form = formParam;
    }

    /**
     * Gets View the entry belongs to.
     *
     * @return View the entry belongs to.
     *
     * @see com.fluid.program.api.vo.flow.FlowStepRule
     */
    public String getJobView() {
        return this.jobView;
    }

    /**
     * Sets View the entry belongs to.
     *
     * @param jobViewParam View the entry belongs to.
     *
     * @see com.fluid.program.api.vo.flow.FlowStepRule
     */
    public void setJobView(String jobViewParam) {
        this.jobView = jobViewParam;
    }
}
