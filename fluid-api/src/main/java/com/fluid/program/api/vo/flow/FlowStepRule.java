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


    }

}
