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
 *     Container <code>POJO</code> used to contain
 *     the {@code FluidItem} and {@code FlowStepRule}.
 * </p>
 *
 * <p>
 *     Purely used to <code>compile</code> and <code>execute</code> a
 *     Fluid Rule against a {@code FluidItem} in realtime.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 * @see FluidItem
 * @see FlowStepRule
 */
public class FlowItemExecutePacket extends ABaseFluidJSONObject {

    private FlowStepRule flowStepRule;
    private FluidItem fluidItem;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
    }

    /**
     *
     */
    public FlowItemExecutePacket() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     */
    public FlowItemExecutePacket(JSONObject jsonObjectParam) {
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

        return returnVal;
    }

    /**
     *
     * @return
     */
    public FlowStepRule getFlowStepRule() {
        return this.flowStepRule;
    }

    /**
     *
     * @param flowStepRuleParam
     */
    public void setFlowStepRule(FlowStepRule flowStepRuleParam) {
        this.flowStepRule = flowStepRuleParam;
    }

    /**
     *
     * @return
     */
    public FluidItem getFluidItem() {
        return this.fluidItem;
    }

    /**
     *
     * @param fluidItemParam
     */
    public void setFluidItem(FluidItem fluidItemParam) {
        this.fluidItem = fluidItemParam;
    }
}
