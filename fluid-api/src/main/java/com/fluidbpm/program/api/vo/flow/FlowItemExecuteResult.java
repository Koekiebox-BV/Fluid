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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.mail.MailMessage;
import com.fluidbpm.program.api.vo.user.User;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
@Getter
@Setter
public class FlowItemExecuteResult extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private FlowStepRule flowStepRule;
	private FluidItem fluidItem;
	private List<FluidItem> fluidItems;

	private String assignmentRuleValue;
	private String statementResultAsString;

	//regards to schedule and flow-program...
	//flow-program...
	private String executePerFluidItemQuery;
	private Boolean progressToNextPhase;

	//schedule-program...
	private String fluidItemQuery;
	private String executionResult;
	private List<User> executeUsers;
	private User loggedInUser;
	private JobView view;
	private List<MailMessage> mailMessagesToSend;

	/**
	 * The JSON mapping for the {@code FlowItemExecuteResult} object.
	 */
	public static class JSONMapping {
		public static final String FLOW_STEP_RULE = "flowStepRule";
		public static final String FLUID_ITEM = "fluidItem";
		public static final String LOGGED_IN_USER = "loggedInUser";
		public static final String VIEW = "view";
		public static final String FLUID_ITEMS = "fluidItems";
		public static final String ASSIGNMENT_RULE_VALUE = "assignmentRuleValue";
		public static final String STATEMENT_RESULT_AS_STRING = "statementResultAsString";

		//flow programs...
		public static final String EXECUTE_PER_FLUID_ITEM_QUERY = "executePerFluidItemQuery";
		public static final String PROGRESS_TO_NEXT_PHASE = "progressToNextPhase";

		//schedule programs...
		public static final String FLUID_ITEM_QUERY = "fluidItemQuery";
		public static final String EXECUTION_RESULT = "executionResult";
		public static final String EXECUTE_USERS = "executeUsers";
		public static final String MAIL_MESSAGES_TO_SEND = "mailMessagesToSend";
	}

	/**
	 * Default constructor.
	 */
	public FlowItemExecuteResult() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FlowItemExecuteResult(JsonObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) return;

		//Fluid Item...
		if (!this.jsonObject.isNull(JSONMapping.FLUID_ITEM)) {
			this.setFluidItem(new FluidItem(this.jsonObject.getJSONObject(JSONMapping.FLUID_ITEM)));
		}

		//Logged In User...
		if (!this.jsonObject.isNull(JSONMapping.LOGGED_IN_USER)) {
			this.setLoggedInUser(new User(this.jsonObject.getJSONObject(JSONMapping.LOGGED_IN_USER)));
		}

		//View...
		if (!this.jsonObject.isNull(JSONMapping.VIEW)) {
			this.setView(new JobView(this.jsonObject.getJSONObject(JSONMapping.VIEW)));
		}

		//Flow Step Rule...
		if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_RULE)) {
			this.setFlowStepRule(new FlowStepRule(this.jsonObject.getJSONObject(JSONMapping.FLOW_STEP_RULE)));
		}

		//Assignment Rule Value...
		if (!this.jsonObject.isNull(JSONMapping.ASSIGNMENT_RULE_VALUE)) {
			this.setAssignmentRuleValue(this.jsonObject.getString(JSONMapping.ASSIGNMENT_RULE_VALUE));
		}

		//Statement Result As String...
		if (!this.jsonObject.isNull(JSONMapping.STATEMENT_RESULT_AS_STRING)) {
			this.setStatementResultAsString(this.jsonObject.getString(JSONMapping.STATEMENT_RESULT_AS_STRING));
		}

		//Execute per Fluid Item Query...
		if (!this.jsonObject.isNull(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY)) {
			this.setExecutePerFluidItemQuery(this.jsonObject.getString(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY));
		}

		//Progress to next phase...
		if (!this.jsonObject.isNull(JSONMapping.PROGRESS_TO_NEXT_PHASE)) {
			this.setProgressToNextPhase(this.jsonObject.getBoolean(
					JSONMapping.PROGRESS_TO_NEXT_PHASE));
		}

		//Fluid Item Query...
		if (!this.jsonObject.isNull(JSONMapping.FLUID_ITEM_QUERY)) {
			this.setFluidItemQuery(this.jsonObject.getString(JSONMapping.FLUID_ITEM_QUERY));
		}

		//Execution Result...
		if (!this.jsonObject.isNull(JSONMapping.EXECUTION_RESULT)) {
			this.setExecutionResult(this.jsonObject.getString(JSONMapping.EXECUTION_RESULT));
		}

		//Fluid Items...
		if (!this.jsonObject.isNull(JSONMapping.FLUID_ITEMS)) {
			JsonArray fluidItemsArr = this.jsonObject.getJSONArray(JSONMapping.FLUID_ITEMS);
			List<FluidItem> listOfItems = new ArrayList();
			for (int index = 0;index < fluidItemsArr.length();index++) {
				listOfItems.add(new FluidItem(fluidItemsArr.getJSONObject(index)));
			}

			this.setFluidItems(listOfItems);
		}

		//Execute Users...
		if (!this.jsonObject.isNull(JSONMapping.EXECUTE_USERS)) {
			JsonArray executeUsersArr = this.jsonObject.getJSONArray(JSONMapping.EXECUTE_USERS);
			List<User> listOfItems = new ArrayList();
			for (int index = 0;index < executeUsersArr.length();index++) {
				listOfItems.add(new User(executeUsersArr.getJSONObject(index)));
			}
			this.setExecuteUsers(listOfItems);
		}

		//Mail Messages to send...
		if (!this.jsonObject.isNull(JSONMapping.MAIL_MESSAGES_TO_SEND)) {
			JsonArray mailMessagesToSendArr = this.jsonObject.getJSONArray(JSONMapping.MAIL_MESSAGES_TO_SEND);
			List<MailMessage> listOfItems = new ArrayList();
			for (int index = 0;index < mailMessagesToSendArr.length();index++) {
				listOfItems.add(new MailMessage(mailMessagesToSendArr.getJSONObject(index)));
			}
			this.setMailMessagesToSend(listOfItems);
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
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		//Fluid Item...
		if (this.getFluidItem() != null) {
			returnVal.put(JSONMapping.FLUID_ITEM, this.getFluidItem().toJsonObject());
		}

		//Logged In User...
		if (this.getLoggedInUser() != null) {
			returnVal.put(JSONMapping.LOGGED_IN_USER, this.getLoggedInUser().toJsonObject());
		}

		//View...
		if (this.getView() != null) {
			returnVal.put(JSONMapping.VIEW, this.getView().toJsonObject());
		}

		//Flow Step Rule...
		if (this.getFlowStepRule() != null) {
			returnVal.put(JSONMapping.FLOW_STEP_RULE, this.getFlowStepRule().toJsonObject());
		}

		//Assignment Rule...
		if (this.getAssignmentRuleValue() != null) {
			returnVal.put(JSONMapping.ASSIGNMENT_RULE_VALUE, this.getAssignmentRuleValue());
		}

		//Statement Result as String...
		if (this.getStatementResultAsString() != null) {
			returnVal.put(JSONMapping.STATEMENT_RESULT_AS_STRING, this.getStatementResultAsString());
		}

		//Execute per Fluid Item Query...
		if (this.getExecutePerFluidItemQuery() != null) {
			returnVal.put(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY, this.getExecutePerFluidItemQuery());
		}

		//Fluid Item Query...
		if (this.getFluidItemQuery() != null) {
			returnVal.put(JSONMapping.FLUID_ITEM_QUERY, this.getFluidItemQuery());
		}

		//Execution Result...
		if (this.getExecutionResult() != null) {
			returnVal.put(JSONMapping.EXECUTION_RESULT, this.getExecutionResult());
		}

		//Progress to next phase...
		if (this.getProgressToNextPhase() != null) {
			returnVal.put(JSONMapping.PROGRESS_TO_NEXT_PHASE, this.getProgressToNextPhase());
		}

		//Fluid Items...
		if (this.getFluidItems() != null && !this.getFluidItems().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (FluidItem item : this.getFluidItems()) {
				jsonArray.put(item.toJsonObject());
			}
			returnVal.put(JSONMapping.FLUID_ITEMS, jsonArray);
		}

		//Execute Users...
		if (this.getExecuteUsers() != null && !this.getExecuteUsers().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (User item : this.getExecuteUsers()) {
				jsonArray.put(item.toJsonObject());
			}
			returnVal.put(JSONMapping.EXECUTE_USERS, jsonArray);
		}

		//Mail Messages To Send...
		if (this.getMailMessagesToSend() != null && !this.getMailMessagesToSend().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (MailMessage item : this.getMailMessagesToSend()) {
				jsonArray.put(item.toJsonObject());
			}
			returnVal.put(JSONMapping.MAIL_MESSAGES_TO_SEND, jsonArray);
		}

		return returnVal;
	}
}
