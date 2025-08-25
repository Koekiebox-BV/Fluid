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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;

import com.fluidbpm.program.api.vo.item.FluidItem;

/**
 * <p>
 * Container <code>POJO</code> used to contain
 * the {@code FluidItem} and {@code FlowStepRule}.
 *
 * <p>
 * Purely used to {@code compile} and {@code execute} a
 * Fluid Rule against a {@code FluidItem} in realtime.
 *
 * @author jasonbruwer
 * @see FluidItem
 * @see FlowStepRule
 * @since v1.0
 */
public class FlowItemExecutePacket extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private FlowStepRule flowStepRule;
    private FluidItem fluidItem;

    /**
     * The JSON mapping for the {@code FlowItemExecutePacket} object.
     */
    public static class JSONMapping {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
    }

    /**
     * Default constructor.
     */
    public FlowItemExecutePacket() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FlowItemExecutePacket(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFluidItem(this.extractObject(JSONMapping.FLUID_ITEM, FluidItem::new));
        this.setFlowStepRule(this.extractObject(JSONMapping.FLOW_STEP_RULE, FlowStepRule::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FlowItemExecutePacket}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        //Fluid Item...
        if (this.getFluidItem() != null) {
            returnVal.add(JSONMapping.FLUID_ITEM, this.getFluidItem().toJsonObject());
        }

        //Flow Step Rule...
        if (this.getFlowStepRule() != null) {
            returnVal.add(JSONMapping.FLOW_STEP_RULE, this.getFlowStepRule().toJsonObject());
        }

        return returnVal;
    }

    /**
     * Gets the rule to execute.
     *
     * @return Rule to execute.
     * @see FlowStepRule
     */
    public FlowStepRule getFlowStepRule() {
        return this.flowStepRule;
    }

    /**
     * Sets the rule to execute.
     *
     * @param flowStepRuleParam Rule to execute.
     * @see FlowStepRule
     */
    public void setFlowStepRule(FlowStepRule flowStepRuleParam) {
        this.flowStepRule = flowStepRuleParam;
    }

    /**
     * Gets the Fluid item.
     *
     * @return Fluid item.
     * @see FluidItem
     */
    public FluidItem getFluidItem() {
        return this.fluidItem;
    }

    /**
     * Sets the Fluid item.
     *
     * @param fluidItemParam Fluid item.
     * @see FluidItem
     */
    public void setFluidItem(FluidItem fluidItemParam) {
        this.fluidItem = fluidItemParam;
    }
}