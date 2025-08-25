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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Fluid workflow Step Rule that belongs to,
 * Entry Step, Exit Step and View.
 *
 * @author jasonbruwer
 * @see Flow
 * @see FlowStep
 * @see ABaseFluidGSONObject
 * @since v1.0
 */
@NoArgsConstructor
@Getter
@Setter
public class FlowStepRule extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private Long order;
    private String rule;

    private Flow flow;
    private FlowStep flowStep;

    private List<String> nextValidSyntaxWords;
    private String currentTypedSyntax;

    /**
     * The JSON mapping for the {@code FlowStepRule} object.
     */
    public static class JSONMapping {
        public static final String ORDER = "order";
        public static final String RULE = "rule";
        public static final String FLOW = "flow";
        public static final String FLOW_STEP = "flowStep";

        public static final String NEXT_VALID_SYNTAX_WORDS = "nextValidSyntaxWords";
        public static final String CURRENT_TYPED_SYNTAX = "currentTypedSyntax";
    }

    /**
     * The unique identifier of the Step Rule.
     *
     * @param flowStepRuleIdParam Rule Primary Key.
     */
    public FlowStepRule(Long flowStepRuleIdParam) {
        super();
        this.setId(flowStepRuleIdParam);
    }

    /**
     * Constructor to set the flow, step and rule.
     *
     * @param flow     The workflow the rule is part of.
     * @param flowStep The workflow step the rule is part of.
     * @param rule     The rule to be executed.
     */
    public FlowStepRule(Flow flow, FlowStep flowStep, String rule) {
        super();
        this.setFlow(flow);
        this.setFlowStep(flowStep);
        this.setRule(rule);
    }

    /**
     * Constructor to set the flow, step and rule.
     *
     * @param flow     The workflow the rule is part of.
     * @param flowStep The workflow step the rule is part of.
     * @param rule     The rule to be executed.
     * @param order    The order for the rule.
     */
    public FlowStepRule(Flow flow, FlowStep flowStep, String rule, Number order) {
        super();
        this.setFlow(flow);
        this.setFlowStep(flowStep);
        this.setRule(rule);
        if (order != null) this.setOrder(order.longValue());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FlowStepRule(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setOrder(this.getAsLongNullSafe(JSONMapping.ORDER));
        this.setRule(this.getAsStringNullSafe(JSONMapping.RULE));
        this.setCurrentTypedSyntax(this.getAsStringNullSafe(JSONMapping.CURRENT_TYPED_SYNTAX));
        this.setFlow(this.extractObject(JSONMapping.FLOW, Flow::new));
        this.setFlowStep(this.extractObject(JSONMapping.FLOW_STEP, FlowStep::new));
        this.setNextValidSyntaxWords(this.extractStrings(JSONMapping.NEXT_VALID_SYNTAX_WORDS));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FlowStepRule}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        returnVal.addProperty(JSONMapping.ORDER, this.getOrder());
        returnVal.addProperty(JSONMapping.RULE, this.getRule());
        returnVal.addProperty(JSONMapping.CURRENT_TYPED_SYNTAX, this.getCurrentTypedSyntax());
        returnVal.add(JSONMapping.FLOW, this.getFlow().toJsonObject());
        returnVal.add(JSONMapping.FLOW_STEP, this.getFlowStep().toJsonObject());
        returnVal.add(JSONMapping.NEXT_VALID_SYNTAX_WORDS, this.toJsonArray(this.getNextValidSyntaxWords()));
        return returnVal;
    }
}