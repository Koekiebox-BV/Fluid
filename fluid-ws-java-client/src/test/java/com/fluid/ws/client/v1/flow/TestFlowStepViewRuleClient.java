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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepRule;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepViewRuleClient extends ABaseTestCase {

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
        public static final class ViewRules
        {
            /**
             *
             */
            public static final class Pass{

                /**
                 *
                 */
                public static final class View{
                    public static final String PASS_01 = "VIEW 'JUnit Zool' IF(FORM.Email Subject IS_EMPTY)";
                }

                /**
                 *
                 */
                public static final String[] COMPILE_LIST_PASS =
                        {
                                "VIEW 'JUnit Zool' IF(FORM.Email Subject IS_EMPTY)",
                        };
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
    public void testFlowStepViewRule_CRUD()
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

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //THE COMPILE RULES THAT PASSES...
        for(String passRule : TestStatics.ViewRules.Pass.COMPILE_LIST_PASS)
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
        viewRule.setRule(TestStatics.ViewRules.Pass.View.PASS_01);

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

        //Test the Execution...
        /*TODO FlowItemExecuteResult executionResult = flowStepRuleClient.compileFlowStepViewRuleAndExecute(
                TestStatics.ViewRules.Pass.View.PASS_01,
                new FluidItem());

        TestCase.assertNotNull("BY_ID: Execute result is not set.", executionResult);*/
    }
}
