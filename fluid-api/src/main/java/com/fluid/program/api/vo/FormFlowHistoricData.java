package com.fluid.program.api.vo;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.flow.FlowStep;

/**
 * The Fluid Item historic information.
 *
 * Created by jasonbruwer on 2015/12/28.
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
     *
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
     *
     */
    public FormFlowHistoricData() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public FormFlowHistoricData(JSONObject jsonObjectParam) throws JSONException {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Date Created...
        if (!this.jsonObject.isNull(JSONMapping.DATE_CREATED)) {
            this.setDateCreated(this.getLongAsDateFromJson(
                    this.jsonObject.getLong(JSONMapping.DATE_CREATED)));
        }

        //TODO add the fields...
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
    public String getRuleExecuted() {
        return this.ruleExecuted;
    }

    /**
     *
     * @param ruleExecutedParam
     */
    public void setRuleExecuted(String ruleExecutedParam) {
        this.ruleExecuted = ruleExecutedParam;
    }

    /**
     *
     * @return
     */
    public String getRuleExecutedResult() {
        return this.ruleExecutedResult;
    }

    /**
     *
     * @param ruleExecutedResultParam
     */
    public void setRuleExecutedResult(String ruleExecutedResultParam) {
        this.ruleExecutedResult = ruleExecutedResultParam;
    }

    /**
     *
     * @return
     */
    public Long getFlowRuleOrder() {
        return this.flowRuleOrder;
    }

    /**
     *
     * @param flowRuleOrderParam
     */
    public void setFlowRuleOrder(Long flowRuleOrderParam) {
        this.flowRuleOrder = flowRuleOrderParam;
    }

    /**
     *
     * @return
     */
    public String getLogEntryType() {
        return this.logEntryType;
    }

    /**
     *
     * @param logEntryTypeParam
     */
    public void setLogEntryType(String logEntryTypeParam) {
        this.logEntryType = logEntryTypeParam;
    }

    /**
     *
     * @return
     */
    public User getUser() {
        return this.user;
    }

    /**
     *
     * @param userParam
     */
    public void setUser(User userParam) {
        this.user = userParam;
    }

    /**
     *
     * @return
     */
    public FlowStep getFlowStep() {
        return this.flowStep;
    }

    /**
     *
     * @param flowStepParam
     */
    public void setFlowStep(FlowStep flowStepParam) {
        this.flowStep = flowStepParam;
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
    public String getJobView() {
        return this.jobView;
    }

    /**
     *
     * @param jobViewParam
     */
    public void setJobView(String jobViewParam) {
        this.jobView = jobViewParam;
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

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //TODO add the others..

        return returnVal;
    }
}
