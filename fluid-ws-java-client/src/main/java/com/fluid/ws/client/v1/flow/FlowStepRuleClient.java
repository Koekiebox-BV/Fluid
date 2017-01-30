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

package com.fluid.ws.client.v1.flow;

import java.util.List;

import org.json.JSONObject;

import com.fluid.program.api.util.UtilGlobal;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.flow.FlowItemExecutePacket;
import com.fluid.program.api.vo.flow.FlowItemExecuteResult;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepRule;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow rules and
 * underlying steps and rules.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.FlowStepRule
 * @see FlowStepRule
 * @see ABaseClientWS
 */
public class FlowStepRuleClient extends ABaseClientWS {


    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FlowStepRuleClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Create a new Flow Step Entry rule.
     *
     * @param flowStepRuleParam Rule to create.
     * @return Created rule.
     */
    public FlowStepRule createFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleEntryCreate()));
    }

    /**
     * Create a new Flow Step Exit rule.
     *
     * @param flowStepRuleParam Rule to create.
     * @return Created rule.
     */
    public FlowStepRule createFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleExitCreate()));
    }

    /**
     * Create a new Flow Step View rule.
     *
     * @param flowStepRuleParam Rule to create.
     * @return Created rule.
     */
    public FlowStepRule createFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleViewCreate()));
    }

    /**
     * Update an existing Flow Step Entry rule.
     *
     * @param flowStepRuleParam Rule to update.
     * @return Updated rule.
     */
    public FlowStepRule updateFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateEntry()));
    }

    /**
     * Update an existing Flow Step Exit rule.
     *
     * @param flowStepRuleParam Rule to update.
     * @return Updated rule.
     */
    public FlowStepRule updateFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateExit()));
    }

    /**
     * Update an existing Flow Step View rule.
     *
     * @param flowStepRuleParam Rule to update.
     * @return Updated rule.
     */
    public FlowStepRule updateFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateView()));
    }

    /**
     * Compiles the {@code viewRuleSyntaxParam} text within the Fluid workflow engine.
     *
     * @param viewRuleSyntaxParam The syntax to compile.
     * @return Compiled rule.
     */
    public FlowStepRule compileFlowStepViewRule(String viewRuleSyntaxParam)
    {
        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(viewRuleSyntaxParam);

        if(this.serviceTicket != null)
        {
            flowStepRule.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRule, WS.Path.FlowStepRule.Version1.compileViewSyntax()));
    }

    /**
     * Compiles and Executes the {@code viewRuleSyntaxParam}
     * text within the Fluid workflow engine.
     *
     * @param viewRuleSyntaxParam The syntax to compile.
     * @param fluidItemToExecuteOnParam The item to execute the rules on.
     *
     * @return Execution result.
     */
    public FlowItemExecuteResult compileFlowStepViewRuleAndExecute(
            String viewRuleSyntaxParam, FluidItem fluidItemToExecuteOnParam)
    {
        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(viewRuleSyntaxParam);

        FlowItemExecutePacket toPost = new FlowItemExecutePacket();

        if(this.serviceTicket != null)
        {
            toPost.setServiceTicket(this.serviceTicket);
        }

        toPost.setFlowStepRule(flowStepRule);
        toPost.setFluidItem(fluidItemToExecuteOnParam);

        return new FlowItemExecuteResult(this.postJson(
                toPost, WS.Path.FlowStepRule.Version1.compileViewSyntaxAndExecute()));
    }

    /**
     * Compiles the {@code entryRuleSyntaxParam} text within the Fluid workflow engine.
     *
     * @param entryRuleSyntaxParam The syntax to compile.
     * @return Compiled rule.
     */
    public FlowStepRule compileFlowStepEntryRule(String entryRuleSyntaxParam)
    {
        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(entryRuleSyntaxParam);

        if(this.serviceTicket != null)
        {
            flowStepRule.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRule, WS.Path.FlowStepRule.Version1.compileEntrySyntax()));
    }

    /**
     * Compiles and Executes the {@code entryRuleSyntaxParam}
     * text within the Fluid workflow engine.
     *
     * @param entryRuleSyntaxParam The syntax to compile.
     * @param fluidItemToExecuteOnParam The item to execute the rules on.
     *
     * @return Execution result.
     */
    public FlowItemExecuteResult compileFlowStepEntryRuleAndExecute(
            String entryRuleSyntaxParam, FluidItem fluidItemToExecuteOnParam)
    {
        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(entryRuleSyntaxParam);

        FlowItemExecutePacket toPost = new FlowItemExecutePacket();

        if(this.serviceTicket != null)
        {
            toPost.setServiceTicket(this.serviceTicket);
        }

        toPost.setFlowStepRule(flowStepRule);
        toPost.setFluidItem(fluidItemToExecuteOnParam);

        return new FlowItemExecuteResult(this.postJson(
                toPost, WS.Path.FlowStepRule.Version1.compileEntrySyntaxAndExecute()));
    }

    /**
     * Moves an entry rule order one up from the current location.
     *
     * @param flowStepRuleParam The Rule to move up.
     * @return The result after the move.
     */
    public FlowStepRule moveFlowStepEntryRuleUp(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleMoveEntryUp()));
    }

    /**
     * Moves an entry rule order one down from the current location.
     *
     * @param flowStepRuleParam The Rule to move down.
     * @return The result after the move.
     */
    public FlowStepRule moveFlowStepEntryRuleDown(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleMoveEntryDown()));
    }

    /**
     * Deletes an Step Entry rule.
     *
     * @param flowStepRuleParam The rule to delete.
     * @return The deleted rule.
     */
    public FlowStepRule deleteFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteEntry()));
    }

    /**
     * Deletes an Step Exit rule.
     *
     * @param flowStepRuleParam The rule to delete.
     * @return The deleted rule.
     */
    public FlowStep deleteFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteExit()));
    }

    /**
     * Deletes an Step View rule.
     *
     * @param flowStepRuleParam The rule to delete.
     * @return The deleted rule.
     */
    public FlowStep deleteFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteView()));
    }

    /**
     * Retrieves the next valid syntax rules for {@code inputRuleParam}.
     *
     * @param inputRuleParam The text to use as input.
     * @return Listing of valid syntax words to use.
     */
    public List<String> getNextValidSyntaxWordsEntryRule(String inputRuleParam)
    {
        if(inputRuleParam == null)
        {
            inputRuleParam = UtilGlobal.EMPTY;
        }

        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(inputRuleParam);

        if(this.serviceTicket != null)
        {
            flowStepRule.setServiceTicket(this.serviceTicket);
        }

        FlowStepRule returnedObj = new FlowStepRule(this.postJson(
                        flowStepRule, WS.Path.FlowStepRule.Version1.getNextValidEntrySyntax()));

        return returnedObj.getNextValidSyntaxWords();
    }

    /**
     * Retrieves the next valid syntax rules for {@code inputRuleParam}.
     *
     * @param inputRuleParam The text to use as input.
     * @return Listing of valid syntax words to use.
     */
    public List<String> getNextValidSyntaxWordsExitRule(String inputRuleParam)
    {
        if(inputRuleParam == null)
        {
            inputRuleParam = UtilGlobal.EMPTY;
        }

        FlowStepRule flowStepRule = new FlowStepRule();
        flowStepRule.setRule(inputRuleParam);

        if(this.serviceTicket != null)
        {
            flowStepRule.setServiceTicket(this.serviceTicket);
        }

        FlowStepRule returnedObj = new FlowStepRule(this.postJson(
                flowStepRule, WS.Path.FlowStepRule.Version1.getNextValidExitSyntax()));

        return returnedObj.getNextValidSyntaxWords();
    }
}
