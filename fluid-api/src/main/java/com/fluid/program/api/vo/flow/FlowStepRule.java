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
