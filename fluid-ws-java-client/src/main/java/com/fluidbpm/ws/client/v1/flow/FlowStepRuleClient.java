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

package com.fluidbpm.ws.client.v1.flow;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.flow.*;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONObject;

import java.util.List;

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
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FlowStepRule
 * @see FlowStepRule
 * @see ABaseClientWS
 */
public class FlowStepRuleClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public FlowStepRuleClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Create a new Flow Step Entry rule.
	 *
	 * @param flowStepRule Rule to create.
	 * @return Created rule.
	 */
	public FlowStepRule createFlowStepEntryRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.putJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleEntryCreate())
		);
	}

	/**
	 * Create a new Flow Step Exit rule.
	 *
	 * @param flowStepRule Rule to create.
	 * @return Created rule.
	 */
	public FlowStepRule createFlowStepExitRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.putJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleExitCreate())
		);
	}

	/**
	 * Create a new Flow Step View rule.
	 *
	 * @param flowStepRule Rule to create.
	 * @return Created rule.
	 */
	public FlowStepRule createFlowStepViewRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.putJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleViewCreate()));
	}

	/**
	 * Update an existing Flow Step Entry rule.
	 *
	 * @param flowStepRule Rule to update.
	 * @return Updated rule.
	 */
	public FlowStepRule updateFlowStepEntryRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateEntry())
		);
	}

	/**
	 * Update an existing Flow Step Exit rule.
	 *
	 * @param flowStepRule Rule to update.
	 * @return Updated rule.
	 */
	public FlowStepRule updateFlowStepExitRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateExit())
		);
	}

	/**
	 * Update an existing Flow Step View rule.
	 *
	 * @param flowStepRule Rule to update.
	 * @return Updated rule.
	 */
	public FlowStepRule updateFlowStepViewRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateView())
		);
	}

	/**
	 * Compiles the {@code viewRuleSyntaxParam} text within the Fluid workflow engine.
	 *
	 * @param viewRuleSyntax The syntax to compile.
	 * @return Compiled rule.
	 */
	public FlowStepRule compileFlowStepViewRule(String viewRuleSyntax) {
		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(viewRuleSyntax);
		flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.compileViewSyntax())
		);
	}

	/**
	 * Retrieves the exit rules by step {@code flowStep}.
	 * Name or id can be provided.
	 *
	 * @param flowStep The flow step to get the exit rules for.
	 * @return All the exit rules for the step {@code flowStep}.
	 */
	public List<FlowStepRule> getExitRulesByStep(FlowStep flowStep) {
		if (flowStep == null) return null;

		flowStep.setServiceTicket(this.serviceTicket);
		return new FlowStepRuleListing(this.postJson(
				flowStep, WS.Path.FlowStepRule.Version1.getExitRulesByStep()
		)).getListing();
	}

	/**
	 * Retrieves the entry rules by step {@code flowStep}.
	 * Name or id can be provided.
	 *
	 * @param flowStep The flow step to get the exit rules for.
	 * @return All the entry rules for the step {@code flowStep}.
	 */
	public List<FlowStepRule> getEntryRulesByStep(FlowStep flowStep) {
		if (flowStep == null) return null;

		flowStep.setServiceTicket(this.serviceTicket);
		return new FlowStepRuleListing(this.postJson(
				flowStep, WS.Path.FlowStepRule.Version1.getEntryRulesByStep())
		).getListing();
	}

	/**
	 * Retrieves the view rules by step {@code flowStep}.
	 * Name or id can be provided.
	 *
	 * @param flowStep The flow step to get the exit rules for.
	 * @return All the exit rules for the step {@code flowStep}.
	 */
	public List<FlowStepRule> getViewRulesByStep(FlowStep flowStep) {
		if (flowStep == null) return null;

		flowStep.setServiceTicket(this.serviceTicket);
		return new FlowStepRuleListing(this.postJson(
				flowStep, WS.Path.FlowStepRule.Version1.getViewRulesByStep()
		)).getListing();
	}

	/**
	 * Compiles and Executes the {@code viewRuleSyntaxParam}
	 * text within the Fluid workflow engine.
	 *
	 * @param viewRuleSyntax The syntax to compile.
	 * @param fluidItemToExecuteOn The item to execute the rules on.
	 *
	 * @return Execution result.
	 */
	public FlowItemExecuteResult compileFlowStepViewRuleAndExecute(
			String viewRuleSyntax,
			FluidItem fluidItemToExecuteOn
	) {
		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(viewRuleSyntax);

		FlowItemExecutePacket toPost = new FlowItemExecutePacket();
		toPost.setServiceTicket(this.serviceTicket);
		toPost.setFlowStepRule(flowStepRule);
		toPost.setFluidItem(fluidItemToExecuteOn);

		return new FlowItemExecuteResult(this.postJson(
				toPost, WS.Path.FlowStepRule.Version1.compileViewSyntaxAndExecute())
		);
	}

	/**
	 * Compiles the {@code entryRuleSyntaxParam} text within the Fluid workflow engine.
	 * @param entryRuleSyntax The syntax to compile.
	 * @return Compiled rule.
	 */
	public FlowStepRule compileFlowStepEntryRule(String entryRuleSyntax) {
		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(entryRuleSyntax);
		flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.compileEntrySyntax())
		);
	}

	/**
	 * Compiles and Executes the {@code entryRuleSyntaxParam}
	 * text within the Fluid workflow engine.
	 * @param entryRuleSyntax The syntax to compile.
	 * @param fluidItemToExecuteOn The item to execute the rules on.
	 *
	 * @return Execution result.
	 */
	public FlowItemExecuteResult compileFlowStepEntryRuleAndExecute(
		String entryRuleSyntax,
		FluidItem fluidItemToExecuteOn
	) {
		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(entryRuleSyntax);

		FlowItemExecutePacket toPost = new FlowItemExecutePacket();
		toPost.setServiceTicket(this.serviceTicket);
		toPost.setFlowStepRule(flowStepRule);
		toPost.setFluidItem(fluidItemToExecuteOn);

		return new FlowItemExecuteResult(this.postJson(
				toPost, WS.Path.FlowStepRule.Version1.compileEntrySyntaxAndExecute())
		);
	}

	/**
	 * Moves an entry rule order one up from the current location.
	 * @param flowStepRule The Rule to move up.
	 * @return The result after the move.
	 */
	public FlowStepRule moveFlowStepEntryRuleUp(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleMoveEntryUp())
		);
	}

	/**
	 * Moves an entry rule order one down from the current location.
	 * @param flowStepRule The Rule to move down.
	 * @return The result after the move.
	 */
	public FlowStepRule moveFlowStepEntryRuleDown(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleMoveEntryDown())
		);
	}

	/**
	 * Deletes a Step Entry rule.
	 * @param flowStepRule The rule to delete.
	 * @return The deleted rule.
	 */
	public FlowStepRule deleteFlowStepEntryRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteEntry())
		);
	}

	/**
	 * Deletes a Step Exit rule.
	 * @param flowStepRule The rule to delete.
	 * @return The deleted rule.
	 */
	public FlowStep deleteFlowStepExitRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStep(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteExit())
		);
	}

	/**
	 * Deletes a Step View rule.
	 * @param flowStepRule The rule to delete.
	 * @return The deleted rule.
	 */
	public FlowStep deleteFlowStepViewRule(FlowStepRule flowStepRule) {
		if (flowStepRule != null) flowStepRule.setServiceTicket(this.serviceTicket);

		return new FlowStep(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteView())
		);
	}

	/**
	 * Retrieves the next valid syntax rules for {@code inputRuleParam}.
	 * @param inputRule The text to use as input.
	 * @return Listing of valid syntax words to use.
	 */
	public List<String> getNextValidSyntaxWordsEntryRule(String inputRule) {
		if (inputRule == null) inputRule = UtilGlobal.EMPTY;

		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(inputRule);

		if (this.serviceTicket != null) flowStepRule.setServiceTicket(this.serviceTicket);

		FlowStepRule returnedObj = new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.getNextValidEntrySyntax())
		);
		return returnedObj.getNextValidSyntaxWords();
	}

	/**
	 * Retrieves the next valid syntax rules for {@code inputRuleParam}.
	 * @param inputRule The text to use as input.
	 * @return Listing of valid syntax words to use.
	 */
	public List<String> getNextValidSyntaxWordsExitRule(String inputRule) {
		if (inputRule == null) inputRule = UtilGlobal.EMPTY;

		FlowStepRule flowStepRule = new FlowStepRule();
		flowStepRule.setRule(inputRule);

		if (this.serviceTicket != null) flowStepRule.setServiceTicket(this.serviceTicket);

		FlowStepRule returnedObj = new FlowStepRule(this.postJson(
				flowStepRule, WS.Path.FlowStepRule.Version1.getNextValidExitSyntax())
		);
		return returnedObj.getNextValidSyntaxWords();
	}
}
