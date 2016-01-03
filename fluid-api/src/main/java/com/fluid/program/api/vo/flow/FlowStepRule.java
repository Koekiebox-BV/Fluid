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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FlowStepRule extends ABaseFluidJSONObject {

    private Long order;
    private String rule;

    private Flow flow;
    private FlowStep flowStep;

    private List<String> nextValidSyntaxWords;
    private String currentTypedSyntax;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String ORDER = "order";
        public static final String RULE = "rule";
        public static final String FLOW = "flow";
        public static final String FLOW_STEP = "flowStep";

        public static final String NEXT_VALID_SYNTAX_WORDS = "nextValidSyntaxWords";
        public static final String CURRENT_TYPED_SYNTAX = "currentTypedSyntax";
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

        //Current Typed Syntax...
        if (!this.jsonObject.isNull(JSONMapping.CURRENT_TYPED_SYNTAX)) {
            this.setCurrentTypedSyntax(this.jsonObject.getString(JSONMapping.CURRENT_TYPED_SYNTAX));
        }

        //Flow...
        if (!this.jsonObject.isNull(JSONMapping.FLOW)) {
            this.setFlow(new Flow(this.jsonObject.getJSONObject(JSONMapping.FLOW)));
        }

        //Flow Step...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP)) {
            this.setFlowStep(new FlowStep(this.jsonObject.getJSONObject(JSONMapping.FLOW_STEP)));
        }

        //Next Valid Syntax Words...
        if (!this.jsonObject.isNull(JSONMapping.NEXT_VALID_SYNTAX_WORDS)) {

            JSONArray listOfValidWordsArray =
                    this.jsonObject.getJSONArray(JSONMapping.NEXT_VALID_SYNTAX_WORDS);

            List<String> validWordsString = new ArrayList<String>();

            for(int index = 0;index < listOfValidWordsArray.length();index++)
            {
                validWordsString.add(listOfValidWordsArray.getString(index));
            }

            this.setNextValidSyntaxWords(validWordsString);
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

        //Current Typed Syntax...
        if(this.getCurrentTypedSyntax() != null)
        {
            returnVal.put(JSONMapping.CURRENT_TYPED_SYNTAX,
                    this.getCurrentTypedSyntax());
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

        //Next Valid Syntax Words...
        if(this.getNextValidSyntaxWords() != null && !this.getNextValidSyntaxWords().isEmpty())
        {
            JSONArray jsonArrayOfValidWords = new JSONArray();

            for(String validWord : this.getNextValidSyntaxWords())
            {
                jsonArrayOfValidWords.put(validWord);
            }

            returnVal.put(JSONMapping.NEXT_VALID_SYNTAX_WORDS, jsonArrayOfValidWords);
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

    /**
     *
     * @return
     */
    public String getCurrentTypedSyntax() {
        return this.currentTypedSyntax;
    }

    /**
     *
     * @param currentTypedSyntaxParam
     */
    public void setCurrentTypedSyntax(String currentTypedSyntaxParam) {
        this.currentTypedSyntax = currentTypedSyntaxParam;
    }

    /**
     *
     * @return
     */
    public List<String> getNextValidSyntaxWords() {
        return this.nextValidSyntaxWords;
    }

    /**
     *
     * @param nextValidSyntaxWordsParam
     */
    public void setNextValidSyntaxWords(List<String> nextValidSyntaxWordsParam) {
        this.nextValidSyntaxWords = nextValidSyntaxWordsParam;
    }
}
