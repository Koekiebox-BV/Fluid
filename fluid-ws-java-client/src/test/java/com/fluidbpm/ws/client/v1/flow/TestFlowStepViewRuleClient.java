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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepViewRuleClient extends ABaseTestCase {

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
    public void testFlowStepViewRule_CRUD()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);
        FlowStepClient flowStepClient = new FlowStepClient(BASE_URL,serviceTicket);

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        String[] compileListPass = {
                "VIEW 'JUnit Zool' IF(FORM.Email Subject IS_EMPTY)",

        };

        //THE COMPILE RULES THAT PASSES...
        for(String passRule : compileListPass)
        {
            try{
                flowStepRuleClient.compileFlowStepViewRule(passRule);
            }
            //
            catch(FluidClientException cle)
            {
                cle.printStackTrace();

                Assert.fail(cle.getMessage());
                return;
            }
        }

        //1. The Test Flow Step...
        Flow createdFlow = new Flow();
        createdFlow.setName("JUnit Test Flow");
        createdFlow.setDescription("Test Flow Description.");
        createdFlow = flowClient.createFlow(createdFlow);

        FlowStep toCreate = new FlowStep();
        toCreate.setName("JUnit Step");
        toCreate.setDescription("JUnit Step Description.");
        toCreate.setFlow(createdFlow);
        toCreate.setFlowStepType(FlowStep.StepType.ASSIGNMENT);

        //2. Create...
        FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);
        TestCase.assertNotNull("The 'Id' needs to be set for Step.", createdFlowStep.getId());

        //. The Rule...
        FlowStepRule viewRule = new FlowStepRule();
        viewRule.setFlowStep(createdFlowStep);
        viewRule.setRule("VIEW 'JUnit Zool' IF(FORM.Email Subject IS_EMPTY)");

        FlowStepRule createdViewRule = flowStepRuleClient.createFlowStepViewRule(viewRule);
        TestCase.assertNotNull("The 'Id' needs to be set for Entry rule.", createdViewRule.getId());

        //4. Get by Id...
        FlowStep byIdFlowStep = flowStepClient.getFlowStepById(
                createdFlowStep.getId(), FlowStep.StepType.ASSIGNMENT);

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlowStep.getId());

        //5. Delete...
        flowStepClient.deleteFlowStep(byIdFlowStep);

        //Cleanup...
        flowClient.deleteFlow(createdFlow);
    }

    /**
     *
     */
    @Test
    public void testFlowStepViewRule_CompileSucceed()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowStepRuleClient flowStepRuleClient =
                new FlowStepRuleClient(BASE_URL,serviceTicket);

        String[] compileListPass = {
                //Standard...
                "VIEW 'JUnit Zool'",

                //IS_EMPTY...
                "VIEW 'JUnit Zool' IF(FORM.Email Subject IS_EMPTY)",

                //EQUAL...
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL 'I am a Title')",
                "VIEW 'JUnit Zool' IF(FORM.Email Subject EQUAL 'This is cool')",
                "VIEW 'JUnit Zool' IF(FORM.Email From Address EQUAL 'This is cool')",

                //EQUAL with AND ...
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL 'I am a Title' AND FORM.Email Subject EQUAL 'This is cool')",
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL 'Hello' AND FORM.Email Subject EQUAL 'Bye')",

                //EQUAL against Field...
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL FORM.Title)",
                "VIEW 'JUnit Zool' IF(FORM.Email Subject EQUAL FORM.Title)",
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL FORM.Email Subject)",
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL FORM.Email From Address)",

                //EQUAL with AND against Field MIXED...
                "VIEW 'JUnit Zool' IF(FORM.Title EQUAL 'I am a Title' AND FORM.Email Subject EQUAL FORM.Title)",

                //NOT_EQUAL...
                "VIEW 'JUnit Zool' IF(FORM.Title NOT_EQUAL 'I am a Title')",
                "VIEW 'JUnit Zool' IF(FORM.Email Subject NOT_EQUAL 'This is cool')",
                "VIEW 'JUnit Zool' IF(FORM.Email From Address NOT_EQUAL 'This is cool')",

        };

        //THE COMPILE RULES THAT PASSES...
        System.out.println("*** START ***");
        for(String passRule : compileListPass)
        {
            try{
                flowStepRuleClient.compileFlowStepViewRule(passRule);

                System.out.println("PASS ["+passRule+"]");
            }
            //
            catch(FluidClientException cle)
            {
                Assert.fail("Rule Failing is \n\n"+
                        passRule+"\n\n : "+cle.getMessage());
                
                cle.printStackTrace();
                return;
            }
        }
        System.out.println("*** END ***");
    }

}
