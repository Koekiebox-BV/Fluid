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

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepRule;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepRuleClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{

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
                    public static final String PASS_01 = "SET ROUTE.Zool TO 'Cool'";
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

        /**
         *
         */
        public static final class ExitRules
        {
            /**
             *
             */
            public static final class Pass{

                /**
                 *
                 */
                public static final class Set{
                    public static final String PASS_01 = "SET ROUTE.Zool TO 'Cool'";
                }

                /**
                 *
                 */
                public static final class RouteTo{
                    public static final String PASS_01 = "ROUTE TO 'Exit'";
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

        this.loginClient = new LoginClient();
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

        FlowClient flowClient = new FlowClient(serviceTicket);
        FlowStepClient flowStepClient = new FlowStepClient(serviceTicket);
        RouteFieldClient routeFieldClient = new RouteFieldClient(serviceTicket);
        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(serviceTicket);

        //1. The Test Flow Step...
        Flow createdFlow = new Flow();
        createdFlow.setName("JUnit Test Flow");
        createdFlow.setDescription("Test Flow Description.");
        createdFlow = flowClient.createFlow(createdFlow);

        Field fieldZool = new Field();
        fieldZool.setFieldName("Zool");
        fieldZool.setFieldDescription("Field Description");
        fieldZool = routeFieldClient.createFieldTextPlain(fieldZool);

        Field fieldBadool = new Field();
        fieldBadool.setFieldName("Badool");
        fieldBadool.setFieldDescription("Field Description");
        fieldBadool = routeFieldClient.createFieldTextPlain(fieldBadool);

        FlowStep toCreate = new FlowStep();
        toCreate.setName("JUnit Step");
        toCreate.setDescription("JUnit Step Description.");
        toCreate.setFlow(createdFlow);
        toCreate.setFlowStepType(FlowStep.StepType.ASSIGNMENT);

        //2. Create...
        FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);
        TestCase.assertNotNull("The 'Id' needs to be set.", createdFlowStep.getId());

        //. The Rule...
        FlowStepRule entryRule = new FlowStepRule();
        entryRule.setFlowStep(createdFlowStep);
        entryRule.setRule(TestStatics.EntryRules.Pass.Set.PASS_01);

        FlowStepRule createdEntryRule = flowStepRuleClient.createFlowStepEntryRule(entryRule);


        //4. Get by Id...
        FlowStep byIdFlowStep = flowStepClient.getFlowStepById(
                createdFlowStep.getId(), FlowStep.StepType.ASSIGNMENT);

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlowStep.getId());

        //TODO @Jason, need to look at the rules within...

        //5. Delete...
        flowStepClient.deleteFlowStep(byIdFlowStep);

        //Cleanup...
        flowClient.deleteFlow(createdFlow);

        routeFieldClient.deleteField(fieldZool);
        routeFieldClient.deleteField(fieldBadool);
    }



    /**
     *
     * @param ruleParam
     * @param flowStepParam
     */
    private void createEntryRuleMustFail(
            String ruleParam,
            FlowStep flowStepParam)
    {

        //TODO need to add the fail...
    }

}
