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

package com.fluidbpm.program.api.vo.historic;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import java.util.Date;

/**
 * <p>
 * Value Object for Fluid Item Historic information.
 * </p>
 *
 * @author jasonbruwer
 * @see FormFlowHistoricDataListing
 * @since v1.0
 */
@Getter
@Setter
public class FormFlowHistoricData extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

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
    public static class JSONMapping {
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
    public FormFlowHistoricData(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setRuleExecuted(this.getAsStringNullSafe(JSONMapping.RULE_EXECUTED));
        this.setRuleExecutedResult(this.getAsStringNullSafe(JSONMapping.RULE_EXECUTED_RESULT));
        this.setFlowRuleOrder(this.getAsLongNullSafe(JSONMapping.FLOW_RULE_ORDER));
        this.setLogEntryType(this.getAsStringNullSafe(JSONMapping.LOG_ENTRY_TYPE));
        this.setUser(this.extractObject(JSONMapping.USER, User::new));
        this.setFlowStep(this.extractObject(JSONMapping.FLOW_STEP, FlowStep::new));
        this.setForm(this.extractObject(JSONMapping.FORM, Form::new));
        this.setJobView(this.getAsStringNullSafe(JSONMapping.JOB_VIEW));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        //Date Created...
        if (this.getDateCreated() != null) {
            returnVal.addProperty(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Rule Executed...
        if (this.getRuleExecuted() != null) {
            returnVal.addProperty(JSONMapping.RULE_EXECUTED, this.getRuleExecuted());
        }

        //Rule Executed Result...
        if (this.getRuleExecutedResult() != null) {
            returnVal.addProperty(JSONMapping.RULE_EXECUTED_RESULT, this.getRuleExecutedResult());
        }

        //Rule Order...
        if (this.getFlowRuleOrder() != null) {
            returnVal.addProperty(JSONMapping.FLOW_RULE_ORDER, this.getFlowRuleOrder());
        }

        //Log Entry Type...
        if (this.getLogEntryType() != null) {
            returnVal.addProperty(JSONMapping.LOG_ENTRY_TYPE, this.getLogEntryType());
        }

        //User...
        if (this.getUser() != null) {
            returnVal.add(JSONMapping.USER, this.getUser().toJsonObject());
        }

        //Flow Step...
        if (this.getFlowStep() != null) {
            returnVal.add(JSONMapping.FLOW_STEP, this.getFlowStep().toJsonObject());
        }

        //Form...
        if (this.getForm() != null) {
            returnVal.add(JSONMapping.FORM, this.getForm().toJsonObject());
        }

        //Job View...
        if (this.getJobView() != null) {
            returnVal.addProperty(JSONMapping.JOB_VIEW, this.getJobView());
        }
        return returnVal;
    }
}
