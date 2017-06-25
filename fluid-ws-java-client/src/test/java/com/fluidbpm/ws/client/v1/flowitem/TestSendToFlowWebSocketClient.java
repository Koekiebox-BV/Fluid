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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.FluidItem;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.flow.FlowStepRuleListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 17/06/23.
 */
public class TestSendToFlowWebSocketClient extends ABaseTestCase {

    private LoginClient loginClient;

    private String serviceTicket = null;
    private String serviceTicketHex = null;

    //Clients...
    private FlowClient flowClient = null;
    private FormContainerClient formContainerClient = null;
    private FormDefinitionClient formDefinitionClient = null;
    private FormFieldClient formFieldClient = null;
    private FlowStepRuleClient flowStepRuleClient;

    private WebSocketSendToFlowClient webSocketClient = null;

    //Variables to cleanup...
    private Form testForm;
    private Form testFormDefinition;
    private Field testField;
    private Flow testFlow;
    private FlowStepRule flowStepRule;

    /**
     *
     */
    @Before
    public void init()
    {
        this.serviceTicketHex = null;
        this.serviceTicket = null;
        
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        this.loginClient = new LoginClient(BASE_URL);
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        this.serviceTicket = appRequestToken.getServiceTicket();
        if(serviceTicket != null && !this.serviceTicket.isEmpty())
        {
            this.serviceTicketHex =
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(
                            this.serviceTicket));
        }

        //Init the clients...
        this.flowClient = new FlowClient(
                BASE_URL, this.serviceTicket);

        this.flowStepRuleClient = new FlowStepRuleClient(
                BASE_URL, this.serviceTicket);

        this.formContainerClient = new FormContainerClient(
                BASE_URL, this.serviceTicket);

        this.formDefinitionClient = new FormDefinitionClient(
                BASE_URL, this.serviceTicket);

        this.formFieldClient = new FormFieldClient(
                BASE_URL, this.serviceTicket);

        //New synchronous client...
        this.webSocketClient =
                new WebSocketSendToFlowClient(
                        BASE_URL,
                        null,
                        this.serviceTicketHex,
                        TimeUnit.SECONDS.toMillis(60),
                        true);
        
        //Flow...
        String jUnitTestFlowName = "JUnit TestFlow";

        try
        {
            Flow flowToDelete = this.flowClient.getFlowByName(jUnitTestFlowName);

            if(flowToDelete != null)
            {
                this.flowClient.forceDeleteFlow(flowToDelete);
            }
        }
        catch (FluidClientException fce)
        {

        }

        Flow flowToCreate = new Flow(jUnitTestFlowName);
        flowToCreate.setDescription("Test case Flow that should be deleted.");
        this.testFlow = this.flowClient.createFlow(flowToCreate);

        //Form Fields...
        String fieldName = "JUnit Test Field";
        Field testField = new Field(
                fieldName,
                null,
                Field.Type.Text);
        testField.setFieldDescription("The test field.");
        this.testField = this.formFieldClient.createFieldTextPlain(testField);

        //Flow Step Rule...
        FlowStepRule flowStepSetRule = new FlowStepRule();
        flowStepSetRule.setFlow(this.testFlow);
        FlowStep introductFlowStep = new FlowStep();
        introductFlowStep.setName("Introduction");
        introductFlowStep.setFlow(this.testFlow);
        flowStepSetRule.setFlowStep(introductFlowStep);
        flowStepSetRule.setRule("SET FORM."+
                fieldName+" TO 'ZabberMan2000'");
        this.flowStepRule = this.flowStepRuleClient.createFlowStepExitRule(flowStepSetRule);

        //Form Definition...
        String junitTestFormDef = "JUnit Test Form Def";
        Form formDefinitionToCreate = new Form(junitTestFormDef);
        List<Field> formFields = new ArrayList();
        formFields.add(this.testField);
        
        formDefinitionToCreate.setFormFields(formFields);

        List<Flow> assFlows = new ArrayList();
        assFlows.add(flowToCreate);
        formDefinitionToCreate.setAssociatedFlows(assFlows);
        formDefinitionToCreate.setFormDescription("Form Desc for creation.");

        this.testFormDefinition = this.formDefinitionClient.createFormDefinition(formDefinitionToCreate);

        //Form Instance...
        Form formToCreate = new Form(junitTestFormDef);
        formToCreate.setTitle("JUNIT - Test Send To Flow Web Socket - "+ new Date().toString());

        List<Field> formFieldsToCreate = new ArrayList();
        formFieldsToCreate.add(
                new Field(fieldName, "This is a test", Field.Type.Text));
        formToCreate.setFormFields(formFieldsToCreate);

        this.testForm = this.formContainerClient.createFormContainer(formToCreate);

        //Order the rules correctly so that the rule can execute...
        FlowStepRuleListing exitRuleListing =
                this.flowStepRuleClient.getExitRulesByStep(introductFlowStep);

        if(exitRuleListing != null &&
                exitRuleListing.getListing() != null)
        {
            for(FlowStepRule flowStepRule : exitRuleListing.getListing())
            {
                //When its 1, make it 2...
                if(flowStepRule.getOrder() != null && flowStepRule.getOrder().longValue() == 1L)
                {
                    flowStepRule.setOrder(2L);
                }
                else if(flowStepRule.getOrder() != null && flowStepRule.getOrder().longValue() == 2L)
                {
                    flowStepRule.setOrder(1L);
                }
                
                this.flowStepRuleClient.updateFlowStepExitRule(flowStepRule);
            }
        }
    }

    /**
     * Make use of a WebSocket to test the synchronous execution of Flow.
     */
    @Test
    @Ignore
    public void testWebSocketSendToFlowClientWaitForRule()
    {
        if(this.serviceTicket == null || this.serviceTicket.isEmpty())
        {
            return;
        }
        
        long start = System.currentTimeMillis();

        FluidItem testFluidItem = this.webSocketClient.sendToFlowSynchronized(
                this.testForm, this.testFlow.getName());
        
        long took = (System.currentTimeMillis() - start);
        System.out.println("Took '"+took+"' millis.");

        Assert.assertNotNull("Fluid Item is not set.",testFluidItem);
        Assert.assertNotNull("Fluid Item [id] is not set.", testFluidItem.getId());

        System.out.println(testFluidItem.getForm().getFormTypeId() +
                " - " +
                testFluidItem.getForm().getTitle());

        Assert.assertNotNull("Form is not set.",testFluidItem.getForm());
        Assert.assertNotNull("Form Fields is not set.",testFluidItem.getForm().getFormFields());
        Assert.assertNotNull("Form Fields Value is not set.",
                testFluidItem.getForm().getFormFields().get(0));
        Assert.assertNotNull("Form Field Value '"+
                        this.testField.getFieldName()+"' is not set.",
                testFluidItem.getForm().getFieldValueAsString(
                        this.testField.getFieldName()));

        //ZabberMan2000
        Assert.assertEquals(
                "Form Field Value '"+
                        this.testField.getFieldName()+"' must be 'ZabberMan2000'.",
                "ZabberMan2000",
                testFluidItem.getForm().getFieldValueAsString(
                        this.testField.getFieldName()));
        
        if(testFluidItem.getForm().getFormFields() != null)
        {
            for(Field field : testFluidItem.getForm().getFormFields())
            {
                System.out.println("["+field.getFieldName()+"] = '"+
                        field.getFieldValue()+"'");
            }
        }

        System.out.println("Took '"+took+"' millis.");
    }

    /**
     * 
     */
    @After
    public void destroy()
    {
        //Delete the instances...
        if(this.testForm != null &&
                this.testForm.getId() != null)
        {
            this.formContainerClient.deleteFormContainer(this.testForm);
        }

        if(this.testFormDefinition != null &&
                this.testFormDefinition.getId() != null)
        {
            this.formDefinitionClient.deleteFormDefinition(this.testFormDefinition);
        }

        if(this.testField != null && this.testField.getId() != null)
        {
            this.formFieldClient.deleteField(this.testField);
        }

        if(this.flowStepRule != null && this.flowStepRule.getId() != null)
        {
            this.flowStepRuleClient.deleteFlowStepExitRule(this.flowStepRule);
        }
        
        if(this.testFlow != null &&
                this.testFlow.getId() != null)
        {
            this.flowClient.deleteFlow(this.testFlow);
        }

        //Close the clients...
        if(this.webSocketClient != null)
        {
            this.webSocketClient.closeAndClean();
        }

        if(this.flowStepRuleClient != null)
        {
            this.flowStepRuleClient.closeAndClean();
        }
        
        if(this.flowClient != null)
        {
            this.flowClient.closeAndClean();
        }
        
        if(this.loginClient != null)
        {
            this.loginClient.closeAndClean();
        }

        if(this.formContainerClient != null)
        {
            this.formContainerClient.closeAndClean();
        }

        if(this.formDefinitionClient != null)
        {
            this.formDefinitionClient.closeAndClean();
        }

        if(this.formFieldClient != null)
        {
            this.formFieldClient.closeAndClean();
        }
    }
}
