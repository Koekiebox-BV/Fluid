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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.FluidItem;

/**
 * <p>
 *     Container <code>POJO</code> used to send back result from
 *     {@code FlowItemExecutePacket}.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 * @see FlowItemExecutePacket
 */
public class FlowItemExecuteResult extends ABaseFluidJSONObject {

    private FlowStepRule flowStepRule;
    private FluidItem fluidItem;
    private String assignmentRuleValue;
    private String statementResultAsString;

    /**
     * The JSON mapping for the {@code FlowItemExecuteResult} object.
     */
    public static class JSONMapping
    {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
        public static final String ASSIGNMENT_RULE_VALUE = "assignmentRuleValue";
        public static final String STATEMENT_RESULT_AS_STRING = "statementResultAsString";
    }

    /**
     * Default constructor.
     */
    public FlowItemExecuteResult() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FlowItemExecuteResult(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Fluid Item...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_RULE)) {

            this.setFluidItem(new FluidItem(this.jsonObject.getJSONObject(
                    JSONMapping.FLUID_ITEM)));
        }

        //Flow Step Rule...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_RULE)) {

            this.setFlowStepRule(new FlowStepRule(
                    this.jsonObject.getJSONObject(JSONMapping.FLOW_STEP_RULE)));
        }

        //Assignment Rule Value...
        if (!this.jsonObject.isNull(JSONMapping.ASSIGNMENT_RULE_VALUE)) {

            this.setAssignmentRuleValue(
                    this.jsonObject.getString(JSONMapping.ASSIGNMENT_RULE_VALUE));
        }

        //Statement Result As String...
        if (!this.jsonObject.isNull(JSONMapping.STATEMENT_RESULT_AS_STRING)) {

            this.setStatementResultAsString(
                    this.jsonObject.getString(JSONMapping.STATEMENT_RESULT_AS_STRING));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FlowItemExecuteResult}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Fluid Item...
        if(this.getFluidItem() != null)
        {
            returnVal.put(JSONMapping.FLUID_ITEM,
                    this.getFluidItem().toJsonObject());
        }

        //Flow Step Rule...
        if(this.getFlowStepRule() != null)
        {
            returnVal.put(JSONMapping.FLOW_STEP_RULE,
                    this.getFlowStepRule().toJsonObject());
        }

        //Assignment Rule...
        if(this.getAssignmentRuleValue() != null)
        {
            returnVal.put(JSONMapping.ASSIGNMENT_RULE_VALUE,
                    this.getAssignmentRuleValue());
        }

        //Statement Result as String...
        if(this.getStatementResultAsString() != null)
        {
            returnVal.put(JSONMapping.STATEMENT_RESULT_AS_STRING,
                    this.getStatementResultAsString());
        }

        return returnVal;
    }

    /**
     * Gets the rule to execute.
     *
     * @return Rule to execute.
     *
     * @see FlowStepRule
     */
    public FlowStepRule getFlowStepRule() {
        return this.flowStepRule;
    }

    /**
     * Sets the rule to execute.
     *
     * @param flowStepRuleParam Rule to execute.
     *
     * @see FlowStepRule
     */
    public void setFlowStepRule(FlowStepRule flowStepRuleParam) {
        this.flowStepRule = flowStepRuleParam;
    }

    /**
     * Gets the Fluid item.
     *
     * @return Fluid item.
     *
     * @see FluidItem
     */
    public FluidItem getFluidItem() {
        return this.fluidItem;
    }

    /**
     * Sets the Fluid item.
     *
     * @param fluidItemParam Fluid item.
     *
     * @see FluidItem
     */
    public void setFluidItem(FluidItem fluidItemParam) {
        this.fluidItem = fluidItemParam;
    }

    /**
     * Gets the new Field assignment value.
     *
     * @return New Assignment value.
     */
    public String getAssignmentRuleValue() {
        return this.assignmentRuleValue;
    }

    /**
     * Sets the new Field assignment value.
     *
     * @param assignmentRuleValueParam New Assignment value.
     */
    public void setAssignmentRuleValue(String assignmentRuleValueParam) {
        this.assignmentRuleValue = assignmentRuleValueParam;
    }

    /**
     * Sets the Statement result value.
     *
     * @return Statement result as Text.
     */
    public String getStatementResultAsString() {
        return this.statementResultAsString;
    }

    /**
     * Sets the Statement result value.
     *
     * @param statementResultAsStringParam Statement result as Text.
     */
    public void setStatementResultAsString(String statementResultAsStringParam) {
        this.statementResultAsString = statementResultAsStringParam;
    }
}
