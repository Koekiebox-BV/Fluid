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

package com.fluid.ws.client.v1.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.flow.FlowItemExecuteResult;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepRule;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.flowitem.FlowItemClient;
import com.fluid.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepEntryRuleClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{

        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";

        /**
         *
         */
        public static final class EntryRules
        {
            /**
             *
             */
            public static final class Pass{

                /**
                 *
                 */
                public static final class Set{
                    public static final String PASS_01 = "SET ROUTE.Zool JUnit TO 'Cool'";

                    public static final String PASS_02_FIELD_VAL = "Cool";
                    public static final String PASS_02_COMPILE_EXEC =
                            "SET ROUTE.Zool JUnit TO '"+PASS_02_FIELD_VAL+"'";

                    /**
                     *
                     */
                    public static final String[] COMPILE_LIST_PASS =
                    {
                        "SET ROUTE.Zool JUnit TO 'Cool'",
                    };

                    /**
                     *
                     */
                    public static final String[] COMPILE_LIST_FAIL =
                    {
                        "SET",
                        "SET ROUTE.Zool",
                        "SET ROUTE.Zool TO",
                        "SET ROUTE.Zool TO 'Cool'",
                    };
                }

                /**
                 *
                 */
                public static final class Expire{
                    public static final String PASS_01 = "EXPIRE";
                }
            }

            /**
             *
             */
            public static final class Fail{

                public static final String FAIL_01 = "";
            }
        }
    }

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
        entryRule.setRule(TestStatics.EntryRules.Pass.Set.PASS_01);

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
    public void testFlowStepEntryRule_CompileSucceedAndFail()
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
        fieldZool.setFieldName("Zool JUnit");
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //THE COMPILE RULES THAT PASSES...
        for(String passRule : TestStatics.EntryRules.Pass.Set.COMPILE_LIST_PASS)
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

        //THE COMPILE RULES THAT FAILS...
        for(String passRule : TestStatics.EntryRules.Pass.Set.COMPILE_LIST_FAIL)
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
                return;
            }
        }

        routeFieldClient.deleteField(fieldZool);
    }


    /**
     *
     */
    @Test
    public void testFlowStepEntryRule_CompileAndRunSucceed()
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
        String fieldName = "Zool JUnit";

        fieldZool.setFieldName(fieldName);
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, serviceTicket);

        //1. Form...
        Form frm = new Form(TestStatics.FORM_DEFINITION);
        frm.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        //Fluid Item...
        FluidItem itemToSend = new FluidItem();
        itemToSend.setForm(frm);

        //0. Create the Flow...
        Flow flowToCreate = new Flow();
        flowToCreate.setName(TestFlowClient.TestStatics.FLOW_NAME);
        flowToCreate.setDescription(TestFlowClient.TestStatics.FLOW_DESCRIPTION);

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);
        Flow createdFlow = flowClient.createFlow(flowToCreate);

        //Create...
        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL,serviceTicket);
        itemToSend = flowItmClient.createFlowItem(itemToSend, createdFlow.getName());

        //Wait for 1 seconds...
        sleepForSeconds(1);

        //Item Created... Add the field...
        List<Field> routeFields = new ArrayList<Field>();
        String routeFieldValue = "RunInTheWind";
        routeFields.add(new Field(fieldName, routeFieldValue));
        itemToSend.setRouteFields(routeFields);

        FlowItemExecuteResult executionResult = flowStepRuleClient.compileFlowStepEntryRuleAndExecute(
                TestStatics.EntryRules.Pass.Set.PASS_02_COMPILE_EXEC,
                itemToSend);

        TestCase.assertEquals("'"+fieldName+"' mismatch.",
                TestStatics.EntryRules.Pass.Set.PASS_02_FIELD_VAL,
                executionResult.getAssignmentRuleValue());

        //Cleanup...
        routeFieldClient.deleteField(fieldZool);
        flowClient.forceDeleteFlow(createdFlow);
    }

}
