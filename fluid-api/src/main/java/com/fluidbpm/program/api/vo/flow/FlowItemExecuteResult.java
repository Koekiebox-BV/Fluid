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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.FluidItem;

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

    public static final long serialVersionUID = 1L;

    private FlowStepRule flowStepRule;
    private FluidItem fluidItem;
    private List<FluidItem> fluidItems;

    private String assignmentRuleValue;
    private String statementResultAsString;

    //regards to schedule and flow-program...
    private String executePerFluidItemQuery;
    private Boolean progressToNextPhase;

    /**
     * The JSON mapping for the {@code FlowItemExecuteResult} object.
     */
    public static class JSONMapping
    {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
        public static final String FLUID_ITEMS = "fluidItems";
        public static final String ASSIGNMENT_RULE_VALUE = "assignmentRuleValue";
        public static final String STATEMENT_RESULT_AS_STRING = "statementResultAsString";

        public static final String EXECUTE_PER_FLUID_ITEM_QUERY = "executePerFluidItemQuery";
        public static final String PROGRESS_TO_NEXT_PHASE = "progressToNextPhase";
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
        if (!this.jsonObject.isNull(JSONMapping.FLUID_ITEM)) {

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

        //Execute per Fluid Item Query...
        if (!this.jsonObject.isNull(
                JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY)) {

            this.setExecutePerFluidItemQuery(this.jsonObject.getString(
                            JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY));
        }

        //Progress to next phase...
        if (!this.jsonObject.isNull(
                JSONMapping.PROGRESS_TO_NEXT_PHASE)) {

            this.setProgressToNextPhase(this.jsonObject.getBoolean(
                    JSONMapping.PROGRESS_TO_NEXT_PHASE));
        }

        //Fluid Items...
        if (!this.jsonObject.isNull(JSONMapping.FLUID_ITEMS)) {

            JSONArray fluidItemsArr = this.jsonObject.getJSONArray(JSONMapping.FLUID_ITEMS);

            List<FluidItem> listOfItems = new ArrayList();
            for(int index = 0;index < fluidItemsArr.length();index++)
            {
                listOfItems.add(new FluidItem(fluidItemsArr.getJSONObject(index)));
            }

            this.setFluidItems(listOfItems);
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

        //Execute per Fluid Item Query...
        if(this.getExecutePerFluidItemQuery() != null)
        {
            returnVal.put(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY,
                    this.getExecutePerFluidItemQuery());
        }

        //Progress to next phase...
        if(this.getProgressToNextPhase() != null)
        {
            returnVal.put(JSONMapping.PROGRESS_TO_NEXT_PHASE,
                    this.getProgressToNextPhase());
        }
        
        //Fluid Items...
        if(this.getFluidItems() != null && !this.getFluidItems().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(FluidItem item : this.getFluidItems())
            {
                jsonArray.put(item.toJsonObject());
            }

            returnVal.put(JSONMapping.FLUID_ITEMS, jsonArray);
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
     * Gets the Fluid items.
     *
     * @return Fluid items.
     *
     * @see FluidItem
     */
    public List<FluidItem> getFluidItems() {
        return this.fluidItems;
    }

    /**
     * Sets the Fluid items.
     *
     * @param fluidItemsParam Fluid items.
     *
     * @see FluidItem
     */
    public void setFluidItems(List<FluidItem> fluidItemsParam) {
        this.fluidItems = fluidItemsParam;
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
     * Gets the Statement result value.
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

    /**
     * Gets the ExecutePerFluidItem query.
     *
     * @return Execute Per Fluid Item Query Text.
     */
    public String getExecutePerFluidItemQuery() {
        return this.executePerFluidItemQuery;
    }

    /**
     * Sets the ExecutePerFluidItem query.
     *
     * @param executePerFluidItemQueryParam Execute Per Fluid Item Query Text.
     */
    public void setExecutePerFluidItemQuery(
            String executePerFluidItemQueryParam) {
        this.executePerFluidItemQuery = executePerFluidItemQueryParam;
    }

    /**
     * Gets the flag to progress to the next phase.
     *
     * @return Progress to the next phase.
     */
    public Boolean getProgressToNextPhase() {
        return this.progressToNextPhase;
    }

    /**
     * Sets the flag to progress to the next phase.
     *
     * @param progressToNextPhaseParam Progress to the next phase.
     */
    public void setProgressToNextPhase(Boolean progressToNextPhaseParam) {
        this.progressToNextPhase = progressToNextPhaseParam;
    }
}
