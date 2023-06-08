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

package com.fluidbpm.ws.client.v1.flowitem;

import com.fluidbpm.program.api.util.UtilGlobal;
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
import com.fluidbpm.ws.client.v1.flow.step.ABaseTestFlowStep;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.form.WebSocketFormContainerCreateClient;
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
 * Test the WebSocketSendToFlowClient.
 */
@Log
public class TestSendToFlowWebSocketClient extends ABaseTestFlowStep {
    private Form formDef;
    private Flow flow;

    @Test(timeout = 60_000)//seconds.
    public void testWebSocketSendToFlowClient() {
        if (this.isConnectionInValid) return;

        String serviceTicketHex = UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(ADMIN_SERVICE_TICKET));
        long timeout = TimeUnit.SECONDS.toMillis(60);

        try (
                FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepClient fsClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepRuleClient fsrClient = new FlowStepRuleClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowItemClient fiClient = new FlowItemClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                WebSocketSendToFlowClient wsSendToFlowWait = new WebSocketSendToFlowClient(
                        BASE_URL,
                        null,
                        serviceTicketHex,
                        timeout,
                        true
                );
                WebSocketSendToFlowClient wsSendToFlowNoWait = new WebSocketSendToFlowClient(
                        BASE_URL,
                        null,
                        serviceTicketHex,
                        timeout,
                        false
                );
                WebSocketFormContainerCreateClient wsCreateFormCont = new WebSocketFormContainerCreateClient(
                        BASE_URL,
                        null,
                        serviceTicketHex,
                        timeout
                )
                
        ) {
            String desc = "Testing create via WebSocket rule execution.";
            // create the flow:
            final String flowName = "JUnit Test Step - WebSocket Client";
            this.flow = new Flow(flowName);
            try {
                this.flow = flowClient.createFlow(new Flow(flowName, desc));
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() == FluidClientException.ErrorCode.DUPLICATE) {
                    flowClient.forceDeleteFlow(this.flow);
                    this.flow = flowClient.createFlow(new Flow(flowName, desc));
                } else throw fce;
            }
            TestCase.assertNotNull(this.flow);

            // fetch the introduction flow step and update the rules:
            FlowStep introductionStep = fsClient.getFlowStepByStep(new FlowStep("Introduction", this.flow));
            TestCase.assertNotNull(introductionStep);

            List<FlowStepRule> introductionExitRules = fsrClient.getExitRulesByStep(introductionStep).getListing();
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            fsrClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            // create the form definition:
            List<Flow> flows = new ArrayList<>();
            flows.add(this.flow);

            this.formDef = createFormDef(fdClient, ffClient, "Employee WebSocket SendToFlow", flows, employeeFields());

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
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Name TO '%s'", employeeName))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Surname TO '%s'", employeeSurname))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Bio TO '%s'", employeeBio))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Secret TO '%s'", employeeSecret))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Gender TO '[%s]'", "Male"))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Age TO '%s'", (int)(Math.random()* 90)))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("SET FORM.JUnit Employee Is Director TO '%s'", "true"))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("ROUTE TO '%s'", "Exit"))
            );

            // create the work-items:
            List<FluidItem> createdItems = new CopyOnWriteArrayList<>();
            ExecutorService executor = Executors.newFixedThreadPool(6);
            long itmCreate = System.currentTimeMillis();

            // create item to warm-up the container:
            Form createdFirstForm = wsCreateFormCont.createFormContainerSynchronized(item(
                    UUID.randomUUID().toString(),
                    this.formDef.getFormType(),
                    true,
                    employeeFields()).getForm()
            );
            FluidItem createdFirst = wsSendToFlowWait.sendToFlowSynchronized(createdFirstForm, flowName);
            TestCase.assertNotNull(createdFirst);
            TestCase.assertNotNull(createdFirst.getId());
            createdItems.add(createdFirst);

            sleepForSeconds(7);

            StringBuilder content = new StringBuilder();
            for (int cycleTimes = 0; cycleTimes < ITEM_COUNT_PER_SUB; cycleTimes++) {
                executor.submit(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        Form toCreate = wsCreateFormCont.createFormContainerSynchronized(item(
                                UUID.randomUUID().toString(),
                                this.formDef.getFormType(),
                                false,
                                employeeFields()
                        ).getForm());
                        long roundTripTime = (System.currentTimeMillis() - start);
                        if (roundTripTime > 100) {
                            content.append(String.format("%nForm: Item [%s] took [%d]millis!", toCreate.getId(), roundTripTime));
                        }
                        TestCase.assertTrue(roundTripTime < 200);

                        start = System.currentTimeMillis();
                        FluidItem created = wsSendToFlowNoWait.sendToFlowSynchronized(toCreate, flowName);
                        roundTripTime = (System.currentTimeMillis() - start);
                        createdItems.add(created);

                        TestCase.assertNotNull(created);
                        TestCase.assertNotNull(created.getId());
                        if (roundTripTime > 100) {
                            content.append(String.format("%nFlowItem: Item [%s] took [%d]millis!", created.getId(), roundTripTime));
                        }
                        TestCase.assertTrue(roundTripTime < 200);
                    } catch (Exception err) {
                        err.printStackTrace();
                        log.warning(err.getMessage());
                        TestCase.fail(err.getMessage());
                    }
                });
            }
            log.warning(content.toString());

            sleepForSeconds(5);
            TestCase.assertTrue(createdItems.size() > 1);

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
                TestCase.assertEquals("The number of rules executed is not as expected.", 10, ruleIndex);

                // form fields:
                TestCase.assertEquals(employeeName, form.getFieldValueAsString("JUnit Employee Name"));
                TestCase.assertEquals(employeeSurname, form.getFieldValueAsString("JUnit Employee Surname"));
                TestCase.assertEquals(employeeBio, form.getFieldValueAsString("JUnit Employee Bio"));
                TestCase.assertEquals(employeeSecret, form.getFieldValueAsString("JUnit Employee Secret"));
                TestCase.assertEquals("Male", form.getFieldValueAsMultiChoice("JUnit Employee Gender").getSelectedMultiChoice());
                TestCase.assertEquals(Boolean.TRUE, form.getFieldValueAsBoolean("JUnit Employee Is Director"));
                TestCase.assertNotNull(form.getFieldValueAsString("JUnit Employee Bio"));
                TestCase.assertNotNull(form.getFieldValueAsDouble("JUnit Employee Age"));

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

            TestCase.assertFalse("Avg. processing per ITEM is taking too long.", allAvgItemCount > 5000L);
            TestCase.assertFalse("Avg. processing per RULE is taking too long.", allRuleItemCount > 1000L);
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
                UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET)
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
        }
    }
}
