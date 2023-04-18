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

package com.fluidbpm.ws.client.v1.flowitem;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flow.TestFlowClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestFlowItemClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics {
        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";
    }

    /**
     * Initialize.
     */
    @Before
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
        this.loginClient = new LoginClient(BASE_URL);
    }

    /**
     * Teardown.
     */
    @After
    public void destroy() {
        this.loginClient.closeAndClean();
    }

    /**
     * Send a work-item into the workflow and
     * let the workflow rules execute.
     */
    @Test
    public void testCreateEmailFormAndSendToWorkflow() {
        if (!this.isConnectionValid()) return;

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, serviceTicket);
        FormContainerClient fcClient = new FormContainerClient(BASE_URL, serviceTicket);
        FlowClient flowClient = new FlowClient(BASE_URL, serviceTicket);

        // Fluid Item:
        FluidItem toCreate = emailItem("Single Email Item");

        // 0. Create the Flow...
        Flow flowToCreate = new Flow();
        flowToCreate.setName(TestFlowClient.TestStatics.FLOW_NAME);
        flowToCreate.setDescription(TestFlowClient.TestStatics.FLOW_DESCRIPTION);

        Flow createdFlow = null;
        String flowName = TestFlowClient.TestStatics.FLOW_NAME;
        try {
            createdFlow = flowClient.createFlow(flowToCreate);
            flowName = createdFlow.getName();
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }

        // Create...
        FluidItem createdItem = flowItmClient.createFlowItem(toCreate, flowName);

        // Wait for 3 seconds...
        sleepForSeconds(3);

        TestCase.assertNotNull(createdItem.getId());
        TestCase.assertNotNull(createdItem.getForm());
        TestCase.assertNotNull(createdItem.getForm().getId());

        Form formById = fcClient.getFormContainerById(createdItem.getForm().getId());
        List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(createdItem.getForm());
        TestCase.assertNotNull(flowHistoryData);

        boolean containFlowEnd = false, containToNewStep = false, containNewRouteItem = false;
        for (FormFlowHistoricData itm : flowHistoryData) {
            String logEntryType = itm.getLogEntryType();
            switch (logEntryType) {
                case "Flow End": containFlowEnd = true; break;
                case "To New Step": containToNewStep = true; break;
                case "New Route Item": containNewRouteItem = true; break;
            }
        }

        TestCase.assertTrue(containFlowEnd && containToNewStep && containNewRouteItem);
        TestCase.assertNotNull(formById);
        TestCase.assertEquals("NotInFlow", formById.getFlowState());

        // Confirm item is no longer in flow due to exit rule...
        try {
            flowClient.deleteFlow(createdFlow);
            TestCase.fail("Not allowed to Delete Flow ");
        } catch (FluidClientException fluidExcept) {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        //Cleanup...
        if (createdFlow == null) createdFlow = flowClient.getFlowByName(flowName);
        flowClient.forceDeleteFlow(createdFlow);
    }

    /**
     * Test creating a workflow step with an assignment option.
     * Then test whether the items may be viewed from the workflow step.
     */
    @Test(timeout = 45000)//seconds.
    public void testSmallWorkflowStepWithConcurrency() {
        if (!this.isConnectionValid()) return;

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        String serviceTicket = appRequestToken.getServiceTicket();
        int itemCount = 40;
        try (
                FlowClient flowClient = new FlowClient(BASE_URL, serviceTicket);
                FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, serviceTicket);
                FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, serviceTicket);
                FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, serviceTicket);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, serviceTicket);
        ) {
            // create the flow:
            final String flowName = "JUnit Assign Flow Test";
            Flow flow = new Flow(flowName);
            try {
                flow = flowClient.createFlow(new Flow(flowName, "Testing assignment."));
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() == FluidClientException.ErrorCode.DUPLICATE) {
                    flowClient.forceDeleteFlow(flow);
                    flow = flowClient.createFlow(new Flow(flowName, "Testing assignment."));
                } else throw fce;
            }
            TestCase.assertNotNull(flow);

            // create the assignment flow step and update the rules:
            FlowStep introductionStep = flowStepClient.getFlowStepByStep(new FlowStep("Introduction", flow));
            List<FlowStepRule> introductionExitRules =
                    flowStepRuleClient.getExitRulesByStep(introductionStep).getListing();
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            flowStepRuleClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            String assignStepName = "Assign Mail Items";
            FlowStep assignStep = new FlowStep(assignStepName, "Step for assignment.");
            assignStep.setFlow(flow);
            assignStep.setFlowStepType(FlowStep.StepType.ASSIGNMENT);
            assignStep = flowStepClient.createFlowStep(assignStep);

            TestCase.assertNotNull(assignStep);

            // update the rules to send to the assignment step and from assignment to exit:
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("ROUTE TO '%s'", assignStepName))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, assignStep, String.format("ROUTE TO 'Exit'"))
            );

            List<JobView> viewsForAssignStep =
                    flowStepClient.getJobViewsByStep(assignStep).getListing();
            TestCase.assertNotNull(viewsForAssignStep);
            TestCase.assertEquals(2, viewsForAssignStep.size());
            JobView viewWorkView = viewsForAssignStep.get(1);

            // create the work-items:
            ExecutorService executor = Executors.newFixedThreadPool(5);
            long itmCreate = System.currentTimeMillis();
            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem toCreate = flowItmClient.createFlowItem(
                            emailItem(UUID.randomUUID().toString()), flowName);
                    TestCase.assertNotNull(toCreate);
                    TestCase.assertNotNull(toCreate.getId());
                });
            }

            List<FluidItem> itemsFromLookup = this.executeUntilOrTO(flowItmClient, viewWorkView, itemCount);
            TestCase.assertEquals(itemCount, itemsFromLookup.size());
            long timeTakenInS = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - itmCreate);
            log.info(String.format("Took [%d] seconds to create [%d] items.", timeTakenInS, itemCount));
            TestCase.assertTrue(String.format("Performance is too slow! [%d] seconds to create [%d] items!",
                            timeTakenInS, itemCount), timeTakenInS < 10);

            // send it on:
            itemsFromLookup.forEach(itm -> {
                fcClient.lockFormContainer(itm.getForm(), viewWorkView);
                flowItmClient.sendFlowItemOn(itm);
            });

            // wait until all the items are moved out:
            this.executeUntilOrTO(flowItmClient, viewWorkView, 0);

            try {
                flowItmClient.getFluidItemsForView(viewWorkView, 100, 0).getListing();
                TestCase.fail("Did not expect any items in the queue.");
            } catch (FluidClientException noEntries) {
                if (noEntries.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw noEntries;
            }

            // ensure the correct steps have taken place:
            itemsFromLookup.forEach(itm -> {
                Form formById = fcClient.getFormContainerById(itm.getForm().getId());
                List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(formById);
                TestCase.assertNotNull(flowHistoryData);
                boolean flowEnd = false, newStep = false, newRoute = false, usrDone = false,
                        open = false, moved = false;
                for (FormFlowHistoricData historyData : flowHistoryData) {
                    String logEntryType = historyData.getLogEntryType();
                    switch (logEntryType) {
                        case "Flow End": flowEnd = true; break;
                        case "To New Step": newStep = true; break;
                        case "New Route Item": newRoute = true; break;
                        case "User done, Back to Route": usrDone = true; break;
                        case "Open From View and Lock": open = true; break;
                        case "Moved to User View": moved = true; break;
                    }
                }
                TestCase.assertTrue(UtilGlobal.isAllTrue(flowEnd, newStep, newRoute, usrDone, open, moved));
                TestCase.assertNotNull(formById);
                TestCase.assertEquals("NotInFlow", formById.getFlowState());
            });

            // cleanup:
            if (flow == null) flow = flowClient.getFlowByName(flowName);
            flowClient.forceDeleteFlow(flow);
        }
    }

    private List<FluidItem> executeUntilOrTO(FlowItemClient fiClient, JobView view, int attemptCount) {
        // wait for up to 30 seconds:
        for (int iter = 0; iter < 30; iter++) {
            sleepForSeconds(1);
            try {
                List<FluidItem> attempt = fiClient.getFluidItemsForView(view, attemptCount, 0).getListing();
                if (attempt != null && attempt.size() == attemptCount) {
                    return attempt;
                }
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                if (attemptCount == 0) return null;
            }
        }
        return null;
    }

    private static FluidItem emailItem(String identifier) {
        //Fluid Item...
        Form frm = new Form(TestStatics.FORM_DEFINITION, TestStatics.FORM_TITLE_PREFIX+new Date().toString()+ " "+identifier);
        frm.setFieldValue("Email From Address", String.format("jack@frost@%s.com", identifier), Field.Type.Text);
        frm.setFieldValue("Email Received Date", new Date(), Field.Type.DateTime);
        frm.setFieldValue("Email Sent Date", new Date(), Field.Type.DateTime);
        frm.setFieldValue("Email Subject", String.format("I am a subject [%s]", identifier), Field.Type.Text);
        frm.setFieldValue("Email To Address", String.format("peter@zool@%s.com", identifier), Field.Type.Text);
        frm.setFieldValue("Email Unique Identifier", Math.random(), Field.Type.Decimal);
        FluidItem toCreate = new FluidItem(frm);

        //2. Attachments...
        List<Attachment> attachments = new ArrayList<Attachment>();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonMemberObject = new JSONObject();

            jsonMemberObject.put("firstname","Jason"+identifier);
            jsonMemberObject.put("lastname", "Bruwer"+identifier);
            jsonMemberObject.put("id_number","81212211122");
            jsonMemberObject.put("cellphone","1111");
            jsonMemberObject.put("member_number","ZOOOOL");

            jsonObject.put("member",jsonMemberObject);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
            return toCreate;
        }

        //First Attachment...
        Attachment attachmentToAdd = new Attachment();
        attachmentToAdd.setAttachmentDataBase64(
                UtilGlobal.encodeBase64(jsonObject.toString().getBytes()));

        attachmentToAdd.setName("Test assessment JSON.json");
        attachmentToAdd.setContentType("application/json");

        //Second Attachment...
        Attachment secondToAdd = new Attachment();
        secondToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64("De Beers".toString().getBytes()));

        secondToAdd.setName("Test Text Plain.txt");
        secondToAdd.setContentType("text/plain");

        attachments.add(attachmentToAdd);
        attachments.add(secondToAdd);

        toCreate.setAttachments(attachments);

        return toCreate;
    }
}
