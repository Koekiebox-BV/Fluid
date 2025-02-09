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
import lombok.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fluid workflow Step that belongs to a Flow.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Flow
 * @see FlowStepRule
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class FlowStep extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private String name;
	private String description;

	private Date dateCreated;
	private Date dateLastUpdated;
	private Flow flow;
	private String flowStepType;

	private List<FlowStepRule> entryRules;
	private List<FlowStepRule> exitRules;
	private List<FlowStepRule> viewRules;

	private List<StepProperty> stepProperties;

	private Long flowStepParentId;

	/**
	 * The JSON mapping for the {@code FlowStep} object.
	 */
	public static class JSONMapping {
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";

		public static final String DATE_CREATED = "dateCreated";
		public static final String DATE_LAST_UPDATED = "dateLastUpdated";

		public static final String FLOW = "flow";
		public static final String FLOW_STEP_TYPE = "flowStepType";
		public static final String ENTRY_RULES = "entryRules";
		public static final String EXIT_RULES = "exitRules";
		public static final String VIEW_RULES = "viewRules";
		public static final String STEP_PROPERTIES = "stepProperties";

		public static final String FLOW_STEP_PARENT_ID = "flowStepParentId";
	}

	/**
	 * The Type of Step to create or update.
	 */
	public static class StepType {
		public static final String INTRODUCTION = "Introduction";
		public static final String RE_ROUTE = "Re Route";
		public static final String EXIT = "Exit";
		public static final String ASSIGNMENT = "Assignment";
		public static final String MAIL_CAPTURE = "Mail Capture";
		public static final String DATABASE_CAPTURE = "Database Capture";
		public static final String SEND_MAIL = "Send Mail";
		public static final String POLLING = "Polling";
		public static final String JAVA_PROGRAM = "Java Program";
		public static final String CLONE_ITEM = "Clone Item";
	}

	/**
	 * Additional properties applicable to specific <code>FlowStep</code> types.
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	public static class StepProperty extends ABaseFluidJSONObject {
		private String name;
		private String value;

		/**
		 * A list of the available properties to set.
		 */
		public static class PropName {
			//Assignment Step...
			public static final String ItemTimeoutDays = "ItemTimeoutDays";
			public static final String ItemTimeoutHours = "ItemTimeoutHours";
			public static final String ItemTimeoutMinutes = "ItemTimeoutMinutes";

			public static final String UserItemTimeoutDays = "UserItemTimeoutDays";
			public static final String UserItemTimeoutHours = "UserItemTimeoutHours";
			public static final String UserItemTimeoutMinutes = "UserItemTimeoutMinutes";

			public static final String PerformMeasureExpectedDailyAbsurd = "PerformMeasureExpectedDailyAbsurd";
			public static final String PerformMeasureExpectedDailyHigh = "PerformMeasureExpectedDailyHigh";
			public static final String PerformMeasureExpectedDailyLow = "PerformMeasureExpectedDailyLow";

			public static final String RouteFields = "RouteFields";
			public static final String ViewGroup = "ViewGroup";
			public static final String ViewGroupWebKit = "ViewGroupWebKit";
			public static final String ViewGroupPriority = "ViewGroupPriority";

			//Send Mail...
			public static final String MailTemplateDescription = "MailTemplateDescription";
			public static final String MailTemplateSubject = "MailTemplateSubject";
			public static final String MailTemplateBodyType = "MailTemplateBodyType";
			public static final String MailTemplateBodyContent = "MailTemplateBodyContent";
			public static final String RecipientSyntax = "RecipientSyntax";
			public static final String MailTransfer = "MailTransfer";
			public static final String IncludeAttachments = "IncludeAttachments";

			//Polling...
			public static final String PollingType = "PollingType";

			//Java Program...
			public static final String TaskIdentifier = "TaskIdentifier";
			public static final String ProgramNameValueProperties = "ProgramNameValueProperties";
			/**
			 * The following two values are allowed:
			 * - Internal Custom Runner
			 * - External Custom Runner
			 */
			public static final String AutoCreateCustomRunnerType = "AutoCreateCustomRunnerType";

			//Clone Item...
			public static final String CloneAttachments = "CloneAttachments";
			public static final String SendCloneTo = "SendCloneTo";
			public static final String DestinationForm = "DestinationForm";

			//Document Template...
			public static final String FilenamePatternOutput = "FilenamePatternOutput";
			public static final String UserQueryTemplateLookup = "UserQueryTemplateLookup";
			public static final String ContentTypeTemplate = "ContentTypeTemplate";
			public static final String ContentTypeOutput = "ContentTypeOutput";
		}

		/**
		 * The JSON mapping for the {@code StepProperty} object.
		 */
		public static class JSONMapping {
			public static final String NAME = "name";
			public static final String VALUE = "value";
		}

		/**
		 * Populates local variables with {@code jsonObjectParam}.
		 *
		 * @param jsonObjectParam The JSON Object.
		 */
		public StepProperty(JSONObject jsonObjectParam) {
			super(jsonObjectParam);

			if (this.jsonObject == null) return;

			//Name...
			if (!this.jsonObject.isNull(JSONMapping.NAME)) {
				this.setName(this.jsonObject.getString(JSONMapping.NAME));
			}

			//Value...
			if (!this.jsonObject.isNull(JSONMapping.VALUE)) {
				this.setValue(this.jsonObject.getString(JSONMapping.VALUE));
			}
		}

		/**
		 * Conversion to {@code JSONObject} from Java Object.
		 *
		 * @return {@code JSONObject} representation of {@code StepProperty}
		 * @throws JSONException If there is a problem with the JSON Body.
		 *
		 * @see ABaseFluidJSONObject#toJsonObject()
		 */
		@Override
		public JSONObject toJsonObject() throws JSONException {
			JSONObject returnVal = super.toJsonObject();

			//Name...
			if (this.getName() != null) {
				returnVal.put(JSONMapping.NAME, this.getName());
			}

			//Value...
			if (this.getValue() != null) {
				returnVal.put(JSONMapping.VALUE, this.getValue());
			}

			return returnVal;
		}
	}

	/**
	 * Default constructor.
	 */
	public FlowStep() {
		super();
	}

	/**
	 * Unique identifier of the Flow Step.
	 *
	 * @param flowStepIdParam Flow Step Primary key.
	 */
	public FlowStep(Long flowStepIdParam) {
		super();
		this.setId(flowStepIdParam);
	}

	/**
	 * Flow Step name constructor.
	 *
	 * @param flowStepName Flow Step Name.
	 */
	public FlowStep(String flowStepName) {
		super();
		this.setName(flowStepName);
	}

	/**
	 * Flow Step name and flow constructor.
	 *
	 * @param flowStepName Flow Step Name.
	 * @param flow Flow Step workflow.
	 */
	public FlowStep(String flowStepName, Flow flow) {
		this(flowStepName);
		this.setFlow(flow);
	}

	/**
	 * Flow Step name and description constructor.
	 *
	 * @param flowStepName Flow Step name.
	 * @param flowStepDescription Flow Step description.
	 */
	public FlowStep(String flowStepName, String flowStepDescription) {
		this(flowStepName);
		this.setDescription(flowStepDescription);
	}

	/**
	 * Flow Step name and flow constructor.
	 *
	 * @param flowStepName Flow Step Name.
	 * @param flowStepDescription Flow Step description.
	 * @param flow Flow Step workflow.
	 */
	public FlowStep(String flowStepName, String flowStepDescription, Flow flow) {
		this(flowStepName, flowStepDescription);
		this.setFlow(flow);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FlowStep(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) return;

		//Name...
		if (!this.jsonObject.isNull(JSONMapping.NAME)) {
			this.setName(this.jsonObject.getString(JSONMapping.NAME));
		}

		//Description...
		if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
			this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
		}

		//Date Created...
		this.setDateCreated(this.getDateFieldValueFromFieldWithName(
				JSONMapping.DATE_CREATED));

		//Date Last Updated...
		this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
				JSONMapping.DATE_LAST_UPDATED));

		//Flow...
		if (!this.jsonObject.isNull(JSONMapping.FLOW)) {
			this.setFlow(new Flow(this.jsonObject.getJSONObject(JSONMapping.FLOW)));
		}

		//Flow Step Type...
		if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_TYPE)) {
			this.setFlowStepType(this.jsonObject.getString(JSONMapping.FLOW_STEP_TYPE));
		}

		//Flow Step Parent Id...
		if (!this.jsonObject.isNull(JSONMapping.FLOW_STEP_PARENT_ID)) {

			this.setFlowStepParentId(this.jsonObject.getLong(
					JSONMapping.FLOW_STEP_PARENT_ID));
		}

		//Entry Rules...
		if (!this.jsonObject.isNull(JSONMapping.ENTRY_RULES)) {

			JSONArray entryRules = this.jsonObject.getJSONArray(JSONMapping.ENTRY_RULES);

			List<FlowStepRule> listOfRules = new ArrayList();
			for (int index = 0;index < entryRules.length();index++)
			{
				listOfRules.add(new FlowStepRule(entryRules.getJSONObject(index)));
			}

			this.setEntryRules(listOfRules);
		}

		//Exit Rules...
		if (!this.jsonObject.isNull(JSONMapping.EXIT_RULES)) {

			JSONArray exitRules = this.jsonObject.getJSONArray(JSONMapping.EXIT_RULES);

			List<FlowStepRule> listOfRules = new ArrayList();
			for (int index = 0;index < exitRules.length();index++)
			{
				listOfRules.add(new FlowStepRule(exitRules.getJSONObject(index)));
			}

			this.setExitRules(listOfRules);
		}

		//View Rules...
		if (!this.jsonObject.isNull(JSONMapping.VIEW_RULES)) {

			JSONArray viewRules = this.jsonObject.getJSONArray(JSONMapping.VIEW_RULES);

			List<FlowStepRule> listOfRules = new ArrayList();
			for (int index = 0;index < viewRules.length();index++)
			{
				listOfRules.add(new FlowStepRule(viewRules.getJSONObject(index)));
			}

			this.setViewRules(listOfRules);
		}

		//Step Properties...
		if (!this.jsonObject.isNull(JSONMapping.STEP_PROPERTIES)) {
			JSONArray stepProperties = this.jsonObject.getJSONArray(JSONMapping.STEP_PROPERTIES);

			List<StepProperty> listOfStepProps = new ArrayList();
			for (int index = 0;index < stepProperties.length();index++)
			{
				listOfStepProps.add(new StepProperty(stepProperties.getJSONObject(index)));
			}

			this.setStepProperties(listOfStepProps);
		}
	}

	/**
	 * Sets the property value of the step with
	 * property name {@code nameParam}.
	 *
	 * @param nameParam The property name.
	 * @param valueParam The property value.
	 */
	public void setStepProperty(
			String nameParam, String valueParam)
	{
		if (this.getStepProperties() == null) this.setStepProperties(new ArrayList());
		if (nameParam == null || nameParam.trim().isEmpty()) return;
		if (valueParam.trim().isEmpty()) return;

		String paramLower = nameParam.toLowerCase();
		for (StepProperty existingProp : this.getStepProperties()) {
			if (existingProp.getName().toLowerCase().equals(paramLower)) {
				existingProp.setValue(valueParam);
				return;
			}
		}

		this.getStepProperties().add(new StepProperty(nameParam, valueParam));
	}

	/**
	 * Gets the step property with name {@code nameParam}
	 *
	 * @param nameParam The property to retrieve.
	 * @return Value of the property with name {@code nameParam}.
	 */
	public String getStepProperty(String nameParam) {
		if (this.getStepProperties() == null || this.getStepProperties().isEmpty()) return null;

		if (nameParam == null || nameParam.trim().isEmpty()) return null;

		String paramLower = nameParam.toLowerCase();

		for (StepProperty stepProperty : this.getStepProperties()) {
			if (stepProperty.getName().toLowerCase().equals(paramLower)) return stepProperty.getValue();
		}

		return null;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code FlowStep}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Name...
		if (this.getName() != null) returnVal.put(JSONMapping.NAME,this.getName());

		//Description...
		if (this.getDescription() != null) returnVal.put(JSONMapping.DESCRIPTION,this.getDescription());

		//Date Created...
		if (this.getDateCreated() != null) returnVal.put(JSONMapping.DATE_CREATED,
					this.getDateAsObjectFromJson(this.getDateCreated()));

		//Date Last Updated...
		if (this.getDateLastUpdated() != null)
		{
			returnVal.put(JSONMapping.DATE_LAST_UPDATED,
					this.getDateAsObjectFromJson(this.getDateLastUpdated()));
		}

		//Flow...
		if (this.getFlow() != null) returnVal.put(JSONMapping.FLOW, this.getFlow().toJsonObject());

		//Flow Step Type...
		if (this.getFlowStepType() != null) {
			returnVal.put(JSONMapping.FLOW_STEP_TYPE,this.getFlowStepType());
		}

		//Flow Step Parent Id...
		if (this.getFlowStepParentId() != null) {
			returnVal.put(JSONMapping.FLOW_STEP_PARENT_ID, this.getFlowStepParentId());
		}

		//Entry Rules...
		if (this.getEntryRules() != null && !this.getEntryRules().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (FlowStepRule rule : this.getEntryRules()) jsonArray.put(rule.toJsonObject());
			returnVal.put(JSONMapping.ENTRY_RULES, jsonArray);
		}

		//Exit Rules...
		if (this.getExitRules() != null && !this.getExitRules().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (FlowStepRule rule : this.getExitRules()) jsonArray.put(rule.toJsonObject());
			returnVal.put(JSONMapping.EXIT_RULES, jsonArray);
		}

		//View Rules...
		if (this.getViewRules() != null && !this.getViewRules().isEmpty()) {
			JSONArray jsonArray = new JSONArray();

			for (FlowStepRule rule : this.getViewRules()) jsonArray.put(rule.toJsonObject());

			returnVal.put(JSONMapping.VIEW_RULES, jsonArray);
		}

		//Step Properties...
		if (this.getStepProperties() != null && !this.getStepProperties().isEmpty()) {
			JSONArray jsonArray = new JSONArray();

			for (StepProperty stepProperty : this.getStepProperties()) {
				jsonArray.put(stepProperty.toJsonObject());
			}

			returnVal.put(JSONMapping.STEP_PROPERTIES, jsonArray);
		}

		return returnVal;
	}
}
