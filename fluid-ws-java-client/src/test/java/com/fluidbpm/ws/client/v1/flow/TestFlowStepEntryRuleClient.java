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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.FluidItem;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowItemExecuteResult;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepEntryRuleClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    @Before
    public void init()
    {
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
    public void testFlowStepEntryRule_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);
        FlowStepClient flowStepClient = new FlowStepClient(BASE_URL,serviceTicket);
        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);
        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //1. The Test Flow Step...
        Flow createdFlow = new Flow();
        createdFlow.setName("JUnit Test Flow");
        createdFlow.setDescription("Test Flow Description.");
        createdFlow = flowClient.createFlow(createdFlow);

        Field fieldZool = new Field();
        fieldZool.setFieldName("Zool JUnit");
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        Field fieldBadool = new Field();
        fieldBadool.setFieldName("Badool JUnit");
        fieldBadool.setFieldDescription("Field Description");
        fieldBadool = routeFieldClient.createFieldTextPlain(fieldBadool);

        FlowStep toCreate = new FlowStep();
        toCreate.setName("JUnit Step");
        toCreate.setDescription("JUnit Step Description.");
        toCreate.setFlow(createdFlow);
        toCreate.setFlowStepType(FlowStep.StepType.ASSIGNMENT);

        //2. Create...
        FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);
        TestCase.assertNotNull("The 'Id' needs to be set for Step.", createdFlowStep.getId());

        //. The Rule...
        FlowStepRule entryRule = new FlowStepRule();
        entryRule.setFlowStep(createdFlowStep);
        entryRule.setRule("SET ROUTE.Zool JUnit TO 'Cool'");

        FlowStepRule createdEntryRule = flowStepRuleClient.createFlowStepEntryRule(entryRule);
        TestCase.assertNotNull("The 'Id' needs to be set for Entry rule.", createdEntryRule.getId());

        //4. Get by Id...
        FlowStep byIdFlowStep = flowStepClient.getFlowStepById(
                createdFlowStep.getId(), FlowStep.StepType.ASSIGNMENT);

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlowStep.getId());

        //5. Delete...
        flowStepClient.deleteFlowStep(byIdFlowStep);

        //Cleanup...
        flowClient.deleteFlow(createdFlow);

        routeFieldClient.deleteField(fieldZool);
        routeFieldClient.deleteField(fieldBadool);
    }

    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_GetNextValidSyntax()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //Empty...
        try {
            flowStepRuleClient.getNextValidSyntaxWordsEntryRule(null);
            Assert.fail("Expected to fail.");
        }
        //
        catch(FluidClientException prob)
        {

        }

        //S...
        try {
            flowStepRuleClient.getNextValidSyntaxWordsEntryRule("S");
            Assert.fail("Expected to fail.");
        }
        //
        catch(FluidClientException prob)
        {

        }

        //SET...
        List<String> nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule("SET");

        Assert.assertNotNull("Next valid syntax must be set.",nextValidSyntaxWords);
        Assert.assertEquals("Expected number of rules missmatch.",
                4,nextValidSyntaxWords.size());

        boolean isScope = false;
        for(String iter : nextValidSyntaxWords)
        {
            if(iter.equals("FORM."))
            {
                isScope = true;
            }
        }

        Assert.assertTrue("Expected at least FORM.",isScope);

        //FORM.
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule("SET FORM.");

        Assert.assertTrue("Expected at least 5 fields.",
                nextValidSyntaxWords.size() > 5);

        for(String iter : nextValidSyntaxWords)
        {
            if(!iter.startsWith("FORM."))
            {
                Assert.fail("Did not start with FORM. "+iter);
            }
        }

        //SET FORM.FORM.Email Subject
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule(
                        "SET FORM.Email Subject");

        Assert.assertTrue("Expected only 1 value.",
                nextValidSyntaxWords.size() == 1);

        Assert.assertTrue("Expected TO.",
                nextValidSyntaxWords.get(0).equals("TO"));

        //SET FORM.FORM.Email Subject TO
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule(
                        "SET FORM.Email Subject TO");

        Assert.assertTrue("Expected 5 values.",
                nextValidSyntaxWords.size() == 5);
        

        //SET FORM.FORM.Email Subject TO 'Zool'
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule(
                        "SET FORM.Email Subject TO 'Zool'");

        Assert.assertTrue("Expected 1 values.",
                nextValidSyntaxWords.size() == 1);

        Assert.assertEquals("Expected an IF",
                "IF", nextValidSyntaxWords.get(0));

        //SET FORM.FORM.Email Subject TO 'Zool' IF
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule(
                        "SET FORM.Email Subject TO 'Zool' if");
        Assert.assertTrue("Expected 0 values.",
                nextValidSyntaxWords.size() == 0);

        //SET FORM.Email Sent Date TO FORM.
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsEntryRule(
                        "SET FORM.Email Subject TO FORM.Age");

        Assert.assertTrue("Expected 1 values.",
                nextValidSyntaxWords.size() == 1);

        Assert.assertEquals("Expected an IF",
                "IF", nextValidSyntaxWords.get(0));
    }

    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_CompileSucceed()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //First field...
        Field fieldZool = new Field();
        fieldZool.setFieldName("JUnit Test Entry Rule");
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        //Second field...
        Field fieldKapoel = new Field();
        fieldKapoel.setFieldName("JUnit Test Entry Rule Kapoel");
        fieldKapoel.setFieldDescription("Field Description");
        fieldKapoel = routeFieldClient.createFieldTextPlain(fieldKapoel);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        String[] compileListPass = {
                "SET ROUTE.JUnit Test Entry Rule TO 'Cool'",

        };

        //THE COMPILE RULES THAT PASSES...
        for(String passRule : compileListPass)
        {
            try{
                flowStepRuleClient.compileFlowStepEntryRule(passRule);
            }
            //
            catch(FluidClientException cle)
            {
                cle.printStackTrace();

                Assert.fail(cle.getMessage());
                routeFieldClient.deleteField(fieldZool);
                return;
            }
        }

        routeFieldClient.deleteField(fieldZool);
        routeFieldClient.deleteField(fieldKapoel);
    }

    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_CompileFail()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //First field...
        Field fieldZool = new Field();
        fieldZool.setFieldName("JUnit Test Entry Rule");
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        //Second field...
        Field fieldKapoel = new Field();
        fieldKapoel.setFieldName("JUnit Test Entry Rule Kapoel");
        fieldKapoel.setFieldDescription("Field Description");
        fieldKapoel = routeFieldClient.createFieldTextPlain(fieldKapoel);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //THE COMPILE RULES THAT FAILS...
        String[] compListFail = {
                "SET",
                "SET ROUTE.JUnit Test",
                "SET ROUTE.JUnit Test Entry Rule TO",
                "SET ROUTE.JUnit Test Entry TO 'Cool'",};

        for(String passRule : compListFail)
        {
            try{
                flowStepRuleClient.compileFlowStepEntryRule(passRule);

                Assert.fail("Expected rule: \n\n"+passRule+"\n to fail.");
                routeFieldClient.deleteField(fieldZool);
                return;
            }
            //
            catch(FluidClientException cle)
            {
                if(FluidClientException.ErrorCode.STATEMENT_SYNTAX_ERROR == cle.getErrorCode())
                {
                    System.out.println(cle.getErrorCode() + ":"+ cle.getMessage());
                    continue;
                }

                Assert.fail("Expected rule: \n\n"+passRule+"\n to fail with Error-Code ["+
                        FluidClientException.ErrorCode.STATEMENT_SYNTAX_ERROR+"].");
                routeFieldClient.deleteField(fieldZool);
                routeFieldClient.deleteField(fieldKapoel);
                return;
            }
        }

        routeFieldClient.deleteField(fieldZool);
        routeFieldClient.deleteField(fieldKapoel);
    }


    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_CompileAndRunSucceed_StaticValue()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        Field fieldZool = new Field();
        String fieldName = "JUnit Compile Run";

        fieldZool.setFieldName(fieldName);
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, serviceTicket);

        //1. Form...
        Form frm = new Form("Email");
        frm.setTitle("Test api doc with email... "+new Date().toString());

        //Fluid Item...
        FluidItem itemToSend = new FluidItem();
        itemToSend.setForm(frm);

        //0. Create the Flow...
        Flow flowToCreate = new Flow();
        flowToCreate.setName("JUnit FlowForTestCompileRun");
        flowToCreate.setDescription("A Flow to Compile, Test and Run.");

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);
        Flow createdFlow = flowClient.createFlow(flowToCreate);

        //Create...
        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL,serviceTicket);
        itemToSend = flowItmClient.createFlowItem(itemToSend, createdFlow.getName());

        //Wait for 1 seconds...
        this.sleepForSeconds(1);

        //Item Created... Add the field...
        List<Field> routeFields = new ArrayList();
        String routeFieldValue = "RunInTheWind";
        routeFields.add(new Field(fieldName, routeFieldValue));
        itemToSend.setRouteFields(routeFields);

        String textVal = "Cool";

        FlowItemExecuteResult executionResult = flowStepRuleClient.compileFlowStepEntryRuleAndExecute(
                "SET ROUTE."+fieldName+" TO '"+textVal+"'",
                itemToSend);

        TestCase.assertEquals("'"+fieldName+"' mismatch.",
                textVal,
                executionResult.getAssignmentRuleValue());

        //Cleanup...
        routeFieldClient.deleteField(fieldZool);
        flowClient.forceDeleteFlow(createdFlow);
    }

    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_CompileAndRunSucceed_AnotherFieldValue()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        //Route Fields...
        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //Field One...
        Field fieldOne = new Field();
        String fieldOneName = "JUnit CRSAFV One";

        fieldOne.setFieldName(fieldOneName);
        fieldOne.setFieldDescription(fieldOneName);
        fieldOne = routeFieldClient.createFieldTextPlain(fieldOne);

        //1. Form...
        String formFieldValue = "piet@ziet.com";
        Form frm = new Form("Email");
        frm.setTitle("Test api doc with email... "+new Date().toString());
        frm.setFieldValue(
                "Email From Address",formFieldValue, Field.Type.Text);

        //Fluid Item...
        FluidItem itemToSend = new FluidItem();
        itemToSend.setForm(frm);

        //0. Create the Flow...
        Flow flowToCreate = new Flow();
        String flowName = "JUnit FlowForTestCompileRun Another Field";

        flowToCreate.setName(flowName);
        flowToCreate.setDescription("A Flow to Compile, Test and Run.");

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);

        Flow createdFlow = null;
        try {
            createdFlow = flowClient.getFlowByName(flowName);
        }
        //
        catch(FluidClientException fce)
        {
            createdFlow = flowClient.createFlow(flowToCreate);
        }

        //Create...
        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL,serviceTicket);
        itemToSend = flowItmClient.createFlowItem(itemToSend, createdFlow.getName());

        //Wait for 1 seconds...
        this.sleepForSeconds(1);

        //Item Created... Add the field...
        List<Field> routeFields = new ArrayList();
        routeFields.add(new Field(fieldOneName, "RunInTheWind"));
        itemToSend.setRouteFields(routeFields);

        //Execute the rule...
        String textVal = "Cool";
        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, serviceTicket);

        try {
            FlowItemExecuteResult executionResult =
                    flowStepRuleClient.compileFlowStepEntryRuleAndExecute(
                            "SET ROUTE."+fieldOneName+
                                    " TO FORM.Email From Address",
                            itemToSend);

            TestCase.assertEquals("'"+fieldOneName+"' mismatch.",
                    formFieldValue,
                    executionResult.getAssignmentRuleValue());
        }
        catch (Exception problem)
        {
            problem.printStackTrace();
            TestCase.fail(problem.getMessage());
        }
        finally {
            //Cleanup...
            FormContainerClient formContainerClient =
                    new FormContainerClient(BASE_URL, serviceTicket);
            formContainerClient.deleteFormContainer(itemToSend.getForm());
            formContainerClient.closeAndClean();

            routeFieldClient.deleteField(fieldOne);
            routeFieldClient.closeAndClean();

            flowClient.forceDeleteFlow(createdFlow);
            flowClient.closeAndClean();
        }
    }

}
