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
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.*;
import com.fluidbpm.ws.client.v1.flow.step.ABaseTestFlowStep;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestFlowItemClient extends ABaseTestFlowStep {
    public static final class TestStatics {
        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";
    }

    /**
     * Send a work-item into the workflow and
     * let the workflow rules execute.
     */
    @Test
    public void testCreateEmailFormAndSendToWorkflow() {
        if (!this.isConnectionValid()) return;

        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, this.serviceTicket);
        FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket);
        FlowClient flowClient = new FlowClient(BASE_URL, this.serviceTicket);

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
    @Test(timeout = 240_000)//seconds.
    public void testSmallWorkflowAssignStepWithConcurrency() {
        if (!this.isConnectionValid()) return;

        int itemCount = 50;
        try (
                FlowClient flowClient = new FlowClient(BASE_URL, this.serviceTicket);
                FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, this.serviceTicket);
                FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, this.serviceTicket);
                FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, this.serviceTicket);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket);
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

            // run once to cache the view:
            this.executeUntilOrTO(flowItmClient, viewWorkView, 0, 10);

            // create the work-items:
            ExecutorService executor = Executors.newFixedThreadPool(6);
            long itmCreate = System.currentTimeMillis();
            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem toCreate = flowItmClient.createFlowItem(
                            emailItem(UUID.randomUUID().toString()), flowName);
                    TestCase.assertNotNull(toCreate);
                    TestCase.assertNotNull(toCreate.getId());
                });
            }

            List<FluidItem> itemsFromLookup = this.executeUntilOrTO(flowItmClient, viewWorkView, itemCount, 80);
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
            this.executeUntilOrTO(flowItmClient, viewWorkView, 0, 80);

            try {
                Integer count = flowItmClient.getFluidItemsForView(viewWorkView, itemCount, 0).getListingCount();
                TestCase.fail("Did not expect any items in the queue. Total "+count);
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
                fcClient.deleteFormContainer(formById);
            });

            // cleanup:
            if (flow == null) flow = flowClient.getFlowByName(flowName);
            flowClient.forceDeleteFlow(flow);
        }
    }

    @Test(timeout = 600_000)//seconds.
    public void testWorkflowRuleExecutionWithConcurrency() {
        if (!this.isConnectionValid()) return;

        int itemCount = 100;
        try (
                FlowClient flowClient = new FlowClient(BASE_URL, this.serviceTicket);
                FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, this.serviceTicket);
                FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, this.serviceTicket);
                RouteFieldClient rfClient = new RouteFieldClient(BASE_URL, this.serviceTicket);
                FlowItemClient fiClient = new FlowItemClient(BASE_URL, this.serviceTicket);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket);
        ) {
            // create the flow:
            final String flowName = "JUnit Rule Execution Time Test";
            Flow flow = new Flow(flowName);
            try {
                flow = flowClient.createFlow(new Flow(flowName, "Testing rule execution."));
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() == FluidClientException.ErrorCode.DUPLICATE) {
                    flowClient.forceDeleteFlow(flow);
                    flow = flowClient.createFlow(new Flow(flowName, "Testing rule execution."));
                } else throw fce;
            }
            TestCase.assertNotNull(flow);

            String rfNameStatus = "Status", rfNameEmailFrom = "Mail From Always";
            Field rfStatus = new Field(rfNameStatus);
            Field rfEmailFrom = new Field(rfNameEmailFrom);
            try {
                // route fields:
                rfStatus.setFieldDescription("Status field for route.");
                rfStatus = rfClient.createFieldTextPlain(rfStatus);
                rfEmailFrom.setFieldDescription("Status field for route.");
                rfEmailFrom = rfClient.createFieldTextPlain(rfEmailFrom);
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            }

            // create the assignment flow step and update the rules:
            FlowStep introductionStep = flowStepClient.getFlowStepByStep(new FlowStep("Introduction", flow));
            List<FlowStepRule> introductionExitRules =
                    flowStepRuleClient.getExitRulesByStep(introductionStep).getListing();
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            flowStepRuleClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            String assignStepName = "Wait For Items Processed";
            FlowStep assignStep = new FlowStep(assignStepName, "Step for assignment.");
            assignStep.setFlow(flow);
            assignStep.setFlowStepType(FlowStep.StepType.ASSIGNMENT);

            assignStep.setStepProperty(
                FlowStep.StepProperty.PropName.RouteFields,
                rfNameStatus.concat(",").concat(rfNameEmailFrom)
            );

            assignStep = flowStepClient.createFlowStep(assignStep);

            TestCase.assertNotNull(assignStep);

            // update the rules to send to the assignment step and from assignment to exit:
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET ROUTE.%s TO FORM.Email From Address", rfNameEmailFrom))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET ROUTE.%s TO 'System Is Up'", rfNameStatus))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET ROUTE.%s TO 'System Is Now Degraded'", rfNameStatus))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET ROUTE.%s TO 'System Is Now Down'", rfNameStatus))
            );
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

            // run once to cache the view:
            this.executeUntilOrTO(fiClient, viewWorkView, 0, 10);

            // create the work-items:
            ExecutorService executor = Executors.newFixedThreadPool(6);
            long itmCreate = System.currentTimeMillis();
            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem toCreate = fiClient.createFlowItem(
                            emailItem(UUID.randomUUID().toString()), flowName);
                    TestCase.assertNotNull(toCreate);
                    TestCase.assertNotNull(toCreate.getId());
                });
            }

            List<FluidItem> itemsFromLookup = this.executeUntilOrTO(fiClient, viewWorkView, itemCount, 100);
            TestCase.assertEquals(itemCount, itemsFromLookup.size());
            long timeTakenInS = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - itmCreate);
            log.info(String.format("Took [%d] seconds to create [%d] items.", timeTakenInS, itemCount));
            TestCase.assertTrue(String.format("Performance is too slow! [%d] seconds to create [%d] items!",
                    timeTakenInS, itemCount), timeTakenInS < 40);

            // ensure the correct steps have taken place and within a timely fashion:
            AtomicLong alAll = new AtomicLong(), alPerRule  = new AtomicLong();
            itemsFromLookup.forEach(itm -> {
                long formId = itm.getForm().getId();
                FluidItem fluidItm = fiClient.getFluidItemByFormId(formId, true, false);
                Form form = fluidItm.getForm();
                List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(form);
                TestCase.assertNotNull(flowHistoryData);
                boolean newStep = false, newRoute = false, moved = false, exit = false;
                Date start = null, end = null;
                int ruleIndex = 0;
                for (FormFlowHistoricData historyData : flowHistoryData) {
                    ruleIndex++;
                    Date created = historyData.getDateCreated();
                    if (start == null || start.after(created)) start = created;
                    if (end == null || end.before(created)) end = created;

                    String logEntryType = historyData.getLogEntryType();
                    switch (logEntryType) {
                        case "New Route Item": newRoute = true; break;
                        case "Exit Rule": exit = true; break;
                        case "To New Step": newStep = true; break;
                        case "Moved to User View": moved = true; break;
                    }
                }
                TestCase.assertTrue(UtilGlobal.isAllTrue(newRoute, exit, newStep, moved));
                TestCase.assertNotNull(form);
                TestCase.assertEquals("WorkInProgress", form.getFlowState());
                TestCase.assertEquals("The number of rules executed is not as expected.", 7, ruleIndex);
                long timeTakenMillis = (end.getTime() - start.getTime());
                long avgTimePerRule = (timeTakenMillis / ruleIndex);
                alPerRule.addAndGet(avgTimePerRule);
                alAll.addAndGet(timeTakenMillis);
                log.info(String.format("Processing [%d]millis for [%d]rules with [%d]avg on [%d]item",
                        timeTakenMillis, ruleIndex, avgTimePerRule, form.getId()));
            });

            log.info(String.format("%n"));
            log.info(String.format("%n"));
            long allAvgItemCount = (alAll.get() / itemCount);
            long allRuleItemCount = (alPerRule.get() / itemCount);

            log.info(String.format("Total(%d): [%d]per-item, [%d]per-rule", itemCount, allAvgItemCount, allRuleItemCount));

            TestCase.assertFalse("Avg. processing per ITEM is taking too long.", allAvgItemCount > 9000L);
            TestCase.assertFalse("Avg. processing per RULE is taking too long.", allRuleItemCount > 1000L);

            // send it on:
            itemsFromLookup.forEach(itm -> {
                fcClient.lockFormContainer(itm.getForm(), viewWorkView);
                fiClient.sendFlowItemOn(itm);
            });

            // wait until all the items are moved out:
            this.executeUntilOrTO(fiClient, viewWorkView, 0, 100);

            try {
                int count = fiClient.getFluidItemsForView(viewWorkView, itemCount, 0).getListing().size();
                TestCase.fail(String.format("Did not expect any items in the queue. Total of '%d' detected", count));
            } catch (FluidClientException noEntries) {
                if (noEntries.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw noEntries;
            }

            // ensure the correct steps have taken place:
            itemsFromLookup.forEach(itm -> {
                fcClient.deleteFormContainer(itm.getForm());
            });

            // cleanup:
            if (flow == null) flow = flowClient.getFlowByName(flowName);
            flowClient.forceDeleteFlow(flow);

            rfClient.deleteField(rfStatus);
            rfClient.deleteField(rfEmailFrom);
        }
    }
}
