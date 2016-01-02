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

/**
 *
 */
public class FlowStepRule extends ABaseFluidJSONObject {

    private Long order;
    private String rule;

    private Flow flow;
    private FlowStep flowStep;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String ORDER = "order";
        public static final String RULE = "rule";
        public static final String FLOW = "flow";
        public static final String FLOW_STEP = "flowStep";
    }

    /**
     *
     * @param flowStepRuleIdParam
     */
    public FlowStepRule(Long flowStepRuleIdParam) {
        super();

        this.setId(flowStepRuleIdParam);
    }

    /**
	 *
	 */
    public FlowStepRule() {
        super();
    }


    /**
     *
     * @param jsonObjectParam
     */
    public FlowStepRule(JSONObject jsonObjectParam) {
        super(jsonObjectParam);


        if(this.jsonObject == null)
        {
            return;
        }

        //Order...
        if (!this.jsonObject.isNull(JSONMapping.ORDER)) {
            this.setOrder(this.jsonObject.getLong(JSONMapping.ORDER));
        }

        //Rule...
        if (!this.jsonObject.isNull(JSONMapping.RULE)) {
            this.setRule(this.jsonObject.getString(JSONMapping.RULE));
        }

        //Flow...
        if (!this.jsonObject.isNull(JSONMapping.FLOW)) {
            this.setFlow(new Flow(this.jsonObject.getJSONObject(JSONMapping.FLOW)));
        }

        //Flow Step...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP)) {
            this.setFlowStep(new FlowStep(this.jsonObject.getJSONObject(JSONMapping.FLOW_STEP)));
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

        //Order...
        if(this.getOrder() != null)
        {
            returnVal.put(JSONMapping.ORDER, this.getOrder());
        }

        //Rule...
        if(this.getRule() != null)
        {
            returnVal.put(JSONMapping.RULE, this.getRule());
        }

        //Flow...
        if(this.getFlow() != null)
        {
            returnVal.put(JSONMapping.FLOW, this.getFlow().toJsonObject());
        }

        //Flow Step...
        if(this.getFlowStep() != null)
        {
            returnVal.put(JSONMapping.FLOW_STEP, this.getFlowStep().toJsonObject());
        }

        return returnVal;
    }

    /**
     * @return
     */
    public Long getOrder() {
        return this.order;
    }

    /**
     *
     * @param orderParam
     */
    public void setOrder(Long orderParam) {
        this.order = orderParam;
    }

    /**
     *
     * @return
     */
    public String getRule() {
        return this.rule;
    }

    /**
     *
     * @param ruleParam
     */
    public void setRule(String ruleParam) {
        this.rule = ruleParam;
    }

    /**
     *
     * @return
     */
    public Flow getFlow() {
        return this.flow;
    }

    /**
     *
     * @param flowParam
     */
    public void setFlow(Flow flowParam) {
        this.flow = flowParam;
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
}
