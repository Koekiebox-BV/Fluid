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

package com.fluidbpm.ws.client.v1.flow.step;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test the workflow introduction step.
 */
@Log
public class TestIntroductionStep extends ABaseTestFlowStep {

    @Test(timeout = 15_000)//seconds.
    public void testWorkflowIntroductionStep() {
        if (!this.isConnectionValid()) return;

        int itemCount = 5;
        try (
                FlowClient flowClient = new FlowClient(BASE_URL, this.serviceTicket);
                FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, this.serviceTicket);
                FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, this.serviceTicket);
                FlowItemClient fiClient = new FlowItemClient(BASE_URL, this.serviceTicket);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket);
        ) {
            // create the flow:
            final String flowName = "JUnit Test Step - Introduction";
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

            // create the assignment flow step and update the rules:
            FlowStep introductionStep = flowStepClient.getFlowStepByStep(new FlowStep("Introduction", flow));
            TestCase.assertNotNull(introductionStep);

            List<FlowStepRule> introductionExitRules =
                    flowStepRuleClient.getExitRulesByStep(introductionStep).getListing();
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            flowStepRuleClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            // update the rules to send to the assignment step and from assignment to exit:
            /*TODO flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET FORM.Email From Address TO '%s'", UUID.randomUUID().toString()))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET FORM.Email Subject TO '%s'", UUID.randomUUID().toString()))
            );
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("SET FORM.Email Unique Identifier TO '%s'", Math.random()))
            );*/
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("ROUTE TO '%s'", "Exit"))
            );

            // create the work-items:
            ExecutorService executor = Executors.newFixedThreadPool(6);
            long itmCreate = System.currentTimeMillis();
            List<FluidItem> createdItems = new CopyOnWriteArrayList<>();
            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem created = fiClient.createFlowItem(
                            emailItem(UUID.randomUUID().toString()), flowName);
                    TestCase.assertNotNull(created);
                    TestCase.assertNotNull(created.getId());
                    createdItems.add(created);
                });
            }

            TestCase.assertTrue("Could not reach state where items are all out of workflow.",
                    this.executeUntilInState(fiClient, createdItems, FluidItem.FlowState.NotInFlow));
            
            long timeTakenInS = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - itmCreate);
            log.info(String.format("Took [%d] seconds to create [%d] items.", timeTakenInS, itemCount));
            TestCase.assertTrue(String.format("Performance is too slow! [%d] seconds to create [%d] items!",
                    timeTakenInS, itemCount), timeTakenInS < 40);

            // ensure the correct steps have taken place and within a timely fashion:
            AtomicLong alAll = new AtomicLong(), alPerRule  = new AtomicLong();
            createdItems.forEach(itm -> {
                long formId = itm.getForm().getId();
                FluidItem fluidItm = fiClient.getFluidItemByFormId(formId, true, false);
                Form form = fluidItm.getForm();
                List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(form);
                TestCase.assertNotNull(flowHistoryData);
                boolean newStep = false, newRoute = false, moved = false, exit = false, flowEnd = false;
                Date start = null, end = null;
                int ruleIndex = 0;
                for (FormFlowHistoricData historyData : flowHistoryData) {
                    ruleIndex++;
                    Date created = historyData.getDateCreated();
                    if (start == null || start.after(created)) start = created;
                    if (end == null || end.before(created)) end = created;

                    String logEntryType = historyData.getLogEntryType();
                    switch (logEntryType) {
                        case "Flow End": flowEnd = true; break;
                    }
                }
                TestCase.assertTrue(UtilGlobal.isAllTrue(flowEnd));
                TestCase.assertNotNull(form);
                TestCase.assertEquals(FluidItem.FlowState.NotInFlow, fluidItm.getFlowState());
                TestCase.assertEquals("The number of rules executed is not as expected.", 3, ruleIndex);
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

            TestCase.assertFalse("Avg. processing per ITEM is taking too long.", allAvgItemCount > 5000L);
            TestCase.assertFalse("Avg. processing per RULE is taking too long.", allRuleItemCount > 1000L);

            // ensure the correct steps have taken place:
            createdItems.forEach(itm -> fcClient.deleteFormContainer(itm.getForm()));

            // cleanup:
            if (flow == null) flow = flowClient.getFlowByName(flowName);
            flowClient.forceDeleteFlow(flow);
        }
    }
}
