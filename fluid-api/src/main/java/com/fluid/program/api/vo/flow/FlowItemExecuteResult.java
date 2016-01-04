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

    /**
     * The JSON mapping for the {@code FlowItemExecuteResult} object.
     */
    public static class JSONMapping
    {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
    }

    /**
     *
     */
    public FlowItemExecuteResult() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
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

        return returnVal;
    }

    /**
     *
     * @return
     */
    public FlowStepRule getFlowStepRule() {
        return null;
    }

    /**
     *
     * @param flowStepRuleParam
     */
    public void setFlowStepRule(FlowStepRule flowStepRuleParam) {

    }

    /**
     *
     * @return
     */
    public FluidItem getFluidItem() {
        return null;
    }

    /**
     *
     * @param fluidItemParam
     */
    public void setFluidItem(FluidItem fluidItemParam) {

    }
}
