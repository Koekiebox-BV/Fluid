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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.flow.JobViewListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepClient extends ABaseLoggedInTestCase {

	private LoginClient loginClient;

	/**
	 *
	 */
	public static final class TestStatics{
		public static final String FLOW_STEP_NAME = "JUnit Step";
		public static final String FLOW_STEP_DESCRIPTION = "JUnit - Testing of Flow Step.";

		/**
		 *
		 */
		public static final class Assignment{
			public static final String PROP_ITEM_TIMEOUT_DAYS = "1";
			public static final String PROP_ITEM_TIMEOUT_HOURS = "2";
			public static final String PROP_ITEM_TIMEOUT_MINUTES = "3";

			public static final String PROP_USER_ITEM_TIMEOUT_DAYS = "1";
			public static final String PROP_USER_ITEM_TIMEOUT_HOURS = "2";
			public static final String PROP_USER_ITEM_TIMEOUT_MINUTES = "3";

			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD = "10";
			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH = "5";
			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_LOW = "2";

			public static final String PROP_ROUTE_FIELDS = "Zool,Badool";
			public static final String PROP_VIEW_GROUP = "The View";

			public static final int VIEW_RULES_COUNT_CREATE = 2;
			public static final int VIEW_RULES_COUNT_FETCH = 3;

			//Update...

			public static final String PROP_ITEM_TIMEOUT_DAYS_UPDATE = "4";
			public static final String PROP_ITEM_TIMEOUT_HOURS_UPDATE = "5";
			public static final String PROP_ITEM_TIMEOUT_MINUTES_UPDATE = "6";

			public static final String PROP_USER_ITEM_TIMEOUT_DAYS_UPDATE = "4";
			public static final String PROP_USER_ITEM_TIMEOUT_HOURS_UPDATE = "5";
			public static final String PROP_USER_ITEM_TIMEOUT_MINUTES_UPDATE = "6";

			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD_UPDATE = "15";
			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH_UPDATE = "10";
			public static final String PROP_PERFORMMEASUREEXPECTEDDAILY_LOW_UPDATE = "4";

			public static final String PROP_ROUTE_FIELDS_UPDATE = "Zool";
			public static final String PROP_VIEW_GROUP_UPDATE = "The View Updated";

			public static final int VIEW_RULES_COUNT_UPDATE = 3;
		}


		public static final String FLOW_STEP_NAME_UPDATE = "JUnit Step UPDATED";
		public static final String FLOW_STEP_DESCRIPTION_UPDATE = "JUnit - Testing of Flow Step UPDATED.";
	}

	/**
	 *
	 */
	@Before
	public void init() {
		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
		this.loginClient = new LoginClient(BASE_URL);
	}

	/**
	 *
	 */
	@After
	public void destroy()
	{
		this.loginClient.closeAndClean();
	}

	/**
	 *
	 */
	@Test
	public void testFlowStep_Assignment_CRUD() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FlowClient flowClient = new FlowClient(BASE_URL, serviceTicket);
		FlowStepClient flowStepClient = new FlowStepClient(BASE_URL,serviceTicket);
		RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

		//1. The Test Flow Step...
		Flow createdFlow = new Flow();
		createdFlow.setName("JUnit Test Flow");
		createdFlow.setDescription("Test Flow Description.");
		createdFlow = flowClient.createFlow(createdFlow);

		Field fieldZool = new Field();
		fieldZool.setFieldName("Zool");
		fieldZool.setFieldDescription("Field Description");
		fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

		Field fieldBadool = new Field();
		fieldBadool.setFieldName("Badool");
		fieldBadool.setFieldDescription("Field Description");
		fieldBadool = routeFieldClient.createFieldTextPlain(fieldBadool);

		FlowStep toCreate = new FlowStep();
		toCreate.setName(TestStatics.FLOW_STEP_NAME);
		toCreate.setDescription(TestStatics.FLOW_STEP_DESCRIPTION);
		toCreate.setFlow(createdFlow);
		toCreate.setFlowStepType(FlowStep.StepType.ASSIGNMENT);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutDays,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_DAYS);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutHours,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_HOURS);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutMinutes,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_MINUTES);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutDays,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_DAYS);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutHours,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_HOURS);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutMinutes,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_MINUTES);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyAbsurd,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyHigh,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyLow,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_LOW);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.RouteFields,
				TestStatics.Assignment.PROP_ROUTE_FIELDS);
		toCreate.setStepProperty(
				FlowStep.StepProperty.PropName.ViewGroup,
				TestStatics.Assignment.PROP_VIEW_GROUP);

		//2. Create...
		FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdFlowStep.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FLOW_STEP_NAME, createdFlowStep.getName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FLOW_STEP_DESCRIPTION,
				createdFlowStep.getDescription());
		TestCase.assertNotNull("The 'Date Created' needs to be set.", createdFlowStep.getDateCreated());
		TestCase.assertNotNull("The 'Date Last Updated' needs to be set.", createdFlowStep.getDateLastUpdated());

		TestCase.assertEquals("'ItemTimeoutDays' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_DAYS,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutDays));
		TestCase.assertEquals("'ItemTimeoutHours' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_HOURS,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutHours));
		TestCase.assertEquals("'ItemTimeoutMinutes' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_MINUTES,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutMinutes));

		TestCase.assertEquals("'UserItemTimeoutDays' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_DAYS,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutDays));
		TestCase.assertEquals("'UserItemTimeoutHours' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_HOURS,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutHours));
		TestCase.assertEquals("'UserItemTimeoutMinutes' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_MINUTES,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutMinutes));

		TestCase.assertEquals("'PerformMeasureExpectedDailyAbsurd' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyAbsurd));
		TestCase.assertEquals("'PerformMeasureExpectedDailyHigh' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyHigh));
		TestCase.assertEquals("'PerformMeasureExpectedDailyLow' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_LOW,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyLow));
		TestCase.assertEquals("'ViewGroup' mismatch.",
				TestStatics.Assignment.PROP_VIEW_GROUP,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ViewGroup));
		TestCase.assertEquals("'ViewGroup' mismatch.",
				TestStatics.Assignment.PROP_ROUTE_FIELDS,
				createdFlowStep.getStepProperty(FlowStep.StepProperty.PropName.RouteFields));

		TestCase.assertNotNull("'ViewRules' mismatch.",
				createdFlowStep.getViewRules());
		TestCase.assertEquals("'ViewRules' mismatch.",
				TestStatics.Assignment.VIEW_RULES_COUNT_CREATE,
				createdFlowStep.getViewRules().size());

		//3. Update...
		createdFlowStep.setName(TestStatics.FLOW_STEP_NAME_UPDATE);
		createdFlowStep.setDescription(TestStatics.FLOW_STEP_DESCRIPTION_UPDATE);
		createdFlowStep.setFlow(createdFlow);
		createdFlowStep.setFlowStepType(FlowStep.StepType.ASSIGNMENT);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutDays,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_DAYS_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutHours,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_HOURS_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.ItemTimeoutMinutes,
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_MINUTES_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutDays,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_DAYS_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutHours,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_HOURS_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.UserItemTimeoutMinutes,
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_MINUTES_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyAbsurd,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyHigh,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyLow,
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_LOW_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.RouteFields,
				TestStatics.Assignment.PROP_ROUTE_FIELDS_UPDATE);
		createdFlowStep.setStepProperty(
				FlowStep.StepProperty.PropName.ViewGroup,
				TestStatics.Assignment.PROP_VIEW_GROUP_UPDATE);

		FlowStepRule viewRuleToAdd = new FlowStepRule();
		viewRuleToAdd.setRule("VIEW 'JUnit Test Awesome'");
		createdFlowStep.getViewRules().add(viewRuleToAdd);

		FlowStep updatedFlowStep = flowStepClient.updateFlowStep(createdFlowStep);

		TestCase.assertEquals("UPDATE: 'Id' mismatch.", createdFlowStep.getId(), updatedFlowStep.getId());
		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedFlowStep.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FLOW_STEP_NAME_UPDATE, updatedFlowStep.getName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FLOW_STEP_DESCRIPTION_UPDATE,
				updatedFlowStep.getDescription());
		TestCase.assertNotNull("UPDATE: The 'Date Created' needs to be set.", updatedFlowStep.getDateCreated());
		TestCase.assertNotNull("UPDATE: The 'Date Last Updated' needs to be set.", updatedFlowStep.getDateLastUpdated());

		TestCase.assertEquals("UPDATE: 'ItemTimeoutDays' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_DAYS_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutDays));
		TestCase.assertEquals("UPDATE: 'ItemTimeoutHours' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_HOURS_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutHours));
		TestCase.assertEquals("UPDATE: 'ItemTimeoutMinutes' mismatch.",
				TestStatics.Assignment.PROP_ITEM_TIMEOUT_MINUTES_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutMinutes));

		TestCase.assertEquals("UPDATE: 'UserItemTimeoutDays' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_DAYS_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutDays));
		TestCase.assertEquals("UPDATE: 'UserItemTimeoutHours' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_HOURS_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutHours));
		TestCase.assertEquals("UPDATE: 'UserItemTimeoutMinutes' mismatch.",
				TestStatics.Assignment.PROP_USER_ITEM_TIMEOUT_MINUTES_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.UserItemTimeoutMinutes));

		TestCase.assertEquals("UPDATE: 'PerformMeasureExpectedDailyAbsurd' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_ABSURD_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyAbsurd));
		TestCase.assertEquals("UPDATE: 'PerformMeasureExpectedDailyHigh' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_HIGH_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyHigh));
		TestCase.assertEquals("UPDATE: 'PerformMeasureExpectedDailyLow' mismatch.",
				TestStatics.Assignment.PROP_PERFORMMEASUREEXPECTEDDAILY_LOW_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.PerformMeasureExpectedDailyLow));
		TestCase.assertEquals("UPDATE: 'ViewGroup' mismatch.",
				TestStatics.Assignment.PROP_VIEW_GROUP_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.ViewGroup));
		TestCase.assertEquals("UPDATE: 'ViewGroup' mismatch.",
				TestStatics.Assignment.PROP_ROUTE_FIELDS_UPDATE,
				updatedFlowStep.getStepProperty(FlowStep.StepProperty.PropName.RouteFields));

		TestCase.assertNotNull("UPDATE: 'ViewRules' mismatch.",
				updatedFlowStep.getViewRules());
		TestCase.assertEquals("UPDATE: 'ViewRules' mismatch.",
				TestStatics.Assignment.VIEW_RULES_COUNT_UPDATE,
				updatedFlowStep.getViewRules().size());

		//4.1 Get by Id...
		FlowStep byIdFlowStep = flowStepClient.getFlowStepById(
				updatedFlowStep.getId(), FlowStep.StepType.ASSIGNMENT);

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlowStep.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdFlowStep.getName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdFlowStep.getDescription());
		TestCase.assertNotNull("BY_ID: The 'Date Created' needs to be set.", byIdFlowStep.getDateCreated());

		//TODO @Jason, need to test there diffirently.
		/*TestCase.assertEquals(
				"BY_ID: The 'Date Created' must match original create date.",
				createdFlowStep.getDateCreated().toString(),
				byIdFlowStep.getDateCreated().toString());*/
		TestCase.assertNotNull("BY_ID: The 'Date Last Updated' needs to be set.", byIdFlowStep.getDateLastUpdated());

		//4.2 Get Job Views by Id...
		JobViewListing jobViewListing =
				flowStepClient.getJobViewsByStepId(updatedFlowStep.getId());

		TestCase.assertNotNull("VIEWS_BY_STEP_ID: The 'Job View' needs to be set.", jobViewListing);
		TestCase.assertEquals("VIEWS_BY_STEP_ID: The listing count mismatch.",
				TestStatics.Assignment.VIEW_RULES_COUNT_FETCH,
				jobViewListing.getListingCount().intValue());

		//5. Delete...
		FlowStep deletedFlowStep = flowStepClient.deleteFlowStep(byIdFlowStep);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedFlowStep.getId());

		//Cleanup...
		flowClient.deleteFlow(createdFlow);

		routeFieldClient.deleteField(fieldZool);
		routeFieldClient.deleteField(fieldBadool);
	}

	@Ignore
	@Test
	public void testFetchViewsForLoggedIn() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login("checker", PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		FlowStepClient fsClient = new FlowStepClient(BASE_URL, serviceTicket);

		JobViewListing views = fsClient.getJobViewsByLoggedInUser();
		System.out.println(views);
	}
}
