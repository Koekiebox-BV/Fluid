/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.flow.step;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flow.RouteFieldClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test the workflow route field logic.
 */
@Log
public class TestRouteFieldsInWorkflow extends ABaseTestFlowStep {
    private Form formDef;
    private Flow flow;

    private Field rfTxt;
    private Field rfTxtOther;
    private Field rfTF;

    @Test(timeout = 60_000)//seconds.
    public void testWorkflowAssignmentOfRouteFields() {
        if (this.isConnectionInValid) return;
        
        try (
                FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepClient fsClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepRuleClient fsrClient = new FlowStepRuleClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowItemClient fiClient = new FlowItemClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                RouteFieldClient rfClient = new RouteFieldClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            // create the flow:
            final String flowName = "JUnit Test Step - Route Field Assignment";
            this.flow = new Flow(flowName);
            try {
                this.flow = flowClient.createFlow(new Flow(flowName, "Testing rule execution."));
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() == FluidClientException.ErrorCode.DUPLICATE) {
                    flowClient.forceDeleteFlow(this.flow);
                    this.flow = flowClient.createFlow(new Flow(flowName, "Testing rule execution."));
                } else throw fce;
            }
            TestCase.assertNotNull(this.flow);

            // fetch the introduction flow step and update the rules:
            FlowStep introductionStep = fsClient.getFlowStepByStep(new FlowStep("Introduction", this.flow));
            TestCase.assertNotNull(introductionStep);

            List<FlowStepRule> introductionExitRules = fsrClient.getExitRulesByStep(introductionStep);
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            fsrClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            // create the form definition:
            List<Flow> flows = new ArrayList<>();
            flows.add(this.flow);

            this.formDef = createFormDef(fdClient, ffClient, "Employee Routes", flows, employeeFields());

            // create a route field:
            this.rfTxt = createRouteField(rfClient, "RF Text Plain", Field.Type.Text);
            TestCase.assertNotNull(this.rfTxt);
            this.rfTxtOther = createRouteField(rfClient, "RF Text Other", Field.Type.Text);
            TestCase.assertNotNull(this.rfTxtOther);
            this.rfTF = createRouteField(rfClient, "RF True False", Field.Type.TrueFalse);
            TestCase.assertNotNull(this.rfTF);

            // update the rules to send to the assignment step and from assignment to exit:
            String employeeName = UUID.randomUUID().toString(),
                    employeeSurname = UUID.randomUUID().toString(),
                    employeeSecret = UUID.randomUUID().toString(),
                    employeeBio = String.format(
                            "%s %s %s",
                            UUID.randomUUID().toString(),
                            UUID.randomUUID().toString(),
                            UUID.randomUUID().toString()
                    );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET ROUTE.RF Text Plain TO '%s'", employeeName))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, "SET ROUTE.RF Text Other TO 'Zool'")
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, "SET ROUTE.RF True False TO 'true'")
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("ROUTE TO '%s'", "Exit"))
            );

            //TODO need to also send the item to a Assignment step to inspect and update route field values
            //TODO while being a route field value.

            //TODO execute a view to also look at the route field values.

            // create the work-items:
            List<FluidItem> createdItems = new CopyOnWriteArrayList<>();
            ExecutorService executor = Executors.newFixedThreadPool(6);
            long itmCreate = System.currentTimeMillis();

            // create item to warm-up the container:
            FluidItem createdFirst = fiClient.createFlowItem(item(
                    UUID.randomUUID().toString(),
                    this.formDef.getFormType(),
                    true,
                    employeeFields()), flowName);
            TestCase.assertNotNull(createdFirst);
            TestCase.assertNotNull(createdFirst.getId());
            createdItems.add(createdFirst);

            sleepForSeconds(5);

            StringBuilder content = new StringBuilder();
            for (int cycleTimes = 0; cycleTimes < 1; cycleTimes++) {
                executor.submit(() -> {
                    try {
                        FluidItem toCreate = item(
                            UUID.randomUUID().toString(),
                            this.formDef.getFormType(),
                            false,
                            employeeFields()
                        );

                        long start = System.currentTimeMillis();
                        FluidItem created = fiClient.createFlowItem(toCreate, flowName);
                        long roundTripTime = (System.currentTimeMillis() - start);
                        createdItems.add(created);

                        TestCase.assertNotNull(created);
                        TestCase.assertNotNull(created.getId());
                        if (roundTripTime > 500) content.append(String.format("%nItem [%s] took [%d]millis!", created.getId(), roundTripTime));
                        TestCase.assertTrue(roundTripTime < 1_500);
                    } catch (Exception err) {
                        err.printStackTrace();
                        log.warning(err.getMessage());
                        TestCase.fail(err.getMessage());
                    }
                });
            }
            log.warning(content.toString());

            sleepForSeconds(5);
            TestCase.assertTrue(!createdItems.isEmpty());

            TestCase.assertTrue("Could not reach state where items are all out of workflow.",
                    this.executeUntilInState(fiClient, createdItems, FluidItem.FlowState.NotInFlow, 10));
            
            long timeTakenInS = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - itmCreate);
            log.info(String.format("Took [%d] seconds to create [%d] items.", timeTakenInS, ITEM_COUNT_PER_SUB));
            TestCase.assertTrue(String.format("Performance is too slow! [%d] seconds to create [%d] items!",
                    timeTakenInS, ITEM_COUNT_PER_SUB), timeTakenInS < 40);

            // ensure the correct steps have taken place and within a timely fashion:
            AtomicLong alAll = new AtomicLong(), alPerRule  = new AtomicLong();
            createdItems.forEach(itm -> {
                long formId = itm.getForm().getId();
                FluidItem fluidItm = fiClient.getFluidItemByFormId(formId, true, false);
                Form form = fluidItm.getForm();
                List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(form);
                TestCase.assertNotNull(flowHistoryData);
                boolean newStep = false, newRoute = false, exit = false, flowEnd = false;
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
                        case "Flow End": flowEnd = true; break;
                    }
                }
                TestCase.assertTrue(UtilGlobal.isAllTrue(newStep, newRoute, exit, flowEnd));
                TestCase.assertNotNull(form);
                TestCase.assertEquals(FluidItem.FlowState.NotInFlow, fluidItm.getFlowState());
                TestCase.assertEquals("The number of rules executed is not as expected.", 6, ruleIndex);

                // form fields:
                TestCase.assertNotNull(form.getFieldValueAsString("JUnit Employee Name"));

                long timeTakenMillis = (end.getTime() - start.getTime());
                long avgTimePerRule = (timeTakenMillis / ruleIndex);
                alPerRule.addAndGet(avgTimePerRule);
                alAll.addAndGet(timeTakenMillis);
                log.info(String.format("Processing [%d]millis for [%d]rules with [%d]avg on [%d]item",
                        timeTakenMillis, ruleIndex, avgTimePerRule, form.getId()));
            });

            log.info(String.format("%n"));
            log.info(String.format("%n"));
            long allAvgItemCount = (alAll.get() / ITEM_COUNT_PER_SUB);
            long allRuleItemCount = (alPerRule.get() / ITEM_COUNT_PER_SUB);

            log.info(String.format("Total(%d): [%d]per-item, [%d]per-rule", ITEM_COUNT_PER_SUB, allAvgItemCount, allRuleItemCount));

            TestCase.assertFalse("Assignment: Avg. processing per ITEM is taking too long.", allAvgItemCount > 5000L);
            TestCase.assertFalse("Assignment: Avg. processing per RULE is taking too long.", allRuleItemCount > 1000L);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        if (this.formDef == null) return;

        try (
                FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
                RouteFieldClient rfClient = new RouteFieldClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            // ensure the correct steps have taken place:
            UserQuery uqCleanup = userQueryForFormType(uqClient, this.formDef.getFormType(),
                    this.formDef.getFormFields().get(0).getFieldName());
            deleteFormContainersAndUserQuery(
                    uqClient,
                    fcClient,
                    uqCleanup
            );

            // cleanup:
            if (this.flow == null) this.flow = flowClient.getFlowByName(this.flow.getName());
            flowClient.forceDeleteFlow(this.flow);

            if (this.formDef != null) fdClient.deleteFormDefinition(this.formDef);
            if (this.formDef != null && this.formDef.getFormFields() != null) {
                this.formDef.getFormFields().forEach(fldItm -> {
                    ffClient.forceDeleteField(fldItm);
                });
            }

            if (this.rfTxt != null) rfClient.deleteField(this.rfTxt);
            if (this.rfTxtOther != null) rfClient.deleteField(this.rfTxtOther);
            if (this.rfTF != null) rfClient.deleteField(this.rfTF);
        }
    }
}
