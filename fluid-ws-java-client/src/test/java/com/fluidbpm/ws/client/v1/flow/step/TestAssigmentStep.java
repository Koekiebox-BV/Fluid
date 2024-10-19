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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flow.RouteFieldClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test the workflow assign step create and delete.
 */
@Log
public class TestAssigmentStep extends ABaseTestFlowStep {
    private Form formDef;
    private Flow flow;

    private Field rfTxt;
    private Field rfTxtOther;
    private Field rfTF;

    @Test(timeout = 60_000)//seconds.
    public void testAssigmentStepCreateAndDelete() {
        if (this.isConnectionInValid) return;

        try (
                FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepClient fsClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FlowStepRuleClient fsrClient = new FlowStepRuleClient(BASE_URL, ADMIN_SERVICE_TICKET);
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

            // create a route field:
            this.rfTxt = createRouteField(rfClient, "RF Text Plain", Field.Type.Text);
            this.rfTxtOther = createRouteField(rfClient, "RF Text Other", Field.Type.Text);
            this.rfTF = createRouteField(rfClient, "RF True False", Field.Type.TrueFalse);

            // create the assignment step:
            FlowStep assignStep = new FlowStep(
                    "Review Work",
                    "Review the work to be done.",
                    this.flow
            );
            assignStep.setStepProperty(
                    FlowStep.StepProperty.PropName.RouteFields,
                    this.rfTxt.getFieldName().concat(",").concat(this.rfTxtOther.getFieldName())
            );
            assignStep.setStepProperty(FlowStep.StepProperty.PropName.ViewGroup, "Review Grouping");
            assignStep.setStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutDays, "1");
            assignStep.setStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutHours, "2");
            assignStep.setStepProperty(FlowStep.StepProperty.PropName.ItemTimeoutMinutes, "3");
            assignStep.setFlowStepType(FlowStep.StepType.ASSIGNMENT);
            
            // create step and delete it:
            FlowStep createdAssignStep = fsClient.createFlowStep(assignStep);
            TestCase.assertNotNull(createdAssignStep);
            TestCase.assertNotNull(createdAssignStep.getId());

            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, introductionStep, String.format("Route TO '%s'", assignStep.getName()))
            );
            fsrClient.createFlowStepExitRule(
                    new FlowStepRule(this.flow, assignStep, "Route TO 'Exit'")
            );

            fsClient.deleteFlowStep(createdAssignStep);

            // create the form definition:
            List<Flow> flows = new ArrayList<>();
            flows.add(this.flow);

            this.formDef = createFormDef(fdClient, ffClient, "Employee Routes", flows, employeeFields());

            // update the rules to send to the assignment step and from assignment to exit:
            String employeeName = UUID.randomUUID().toString();
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
                this.formDef.getFormFields().forEach(ffClient::forceDeleteField);
            }

            if (this.rfTxt != null) rfClient.deleteField(this.rfTxt);
            if (this.rfTxtOther != null) rfClient.deleteField(this.rfTxtOther);
            if (this.rfTF != null) rfClient.deleteField(this.rfTF);
        }
    }
}
