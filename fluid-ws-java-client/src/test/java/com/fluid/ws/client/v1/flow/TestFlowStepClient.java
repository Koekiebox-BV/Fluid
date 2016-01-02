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

import com.fluid.program.api.vo.flow.FlowStep;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FLOW_STEP_NAME = "JUnit Step";
        public static final String FLOW_STEP_DESCRIPTION = "JUnit - Testing of Flow Step.";

        public static final String FLOW_STEP_NAME_UPDATE = "JUnit Step UPDATED";
        public static final String FLOW_STEP_DESCRIPTION_UPDATE = "JUnit - Testing of Flow Step UPDATED.";
    }

    /**
     *
     */
    @Before
    public void init()
    {
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @Test
    public void testFlowStep_Assignment_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowStepClient flowStepClient = new FlowStepClient(serviceTicket);

        //1. The Test Flow Step...
        FlowStep toCreate = new FlowStep();
        toCreate.setName(TestStatics.FLOW_STEP_NAME);
        toCreate.setDescription(TestStatics.FLOW_STEP_DESCRIPTION);

        //2. Create...
        FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdFlowStep.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FLOW_STEP_NAME, createdFlowStep.getName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FLOW_STEP_DESCRIPTION_UPDATE,
                createdFlowStep.getDescription());
        TestCase.assertNotNull("The 'Date Created' needs to be set.", createdFlowStep.getDateCreated());
        TestCase.assertNotNull("The 'Date Last Updated' needs to be set.", createdFlowStep.getDateLastUpdated());

        //3. Update...
        createdFlowStep.setName(TestStatics.FLOW_STEP_NAME_UPDATE);
        createdFlowStep.setDescription(TestStatics.FLOW_STEP_DESCRIPTION_UPDATE);
        FlowStep updatedFlowStep = flowStepClient.updateFlowStep(createdFlowStep);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedFlowStep.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FLOW_STEP_NAME_UPDATE, updatedFlowStep.getName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FLOW_STEP_DESCRIPTION_UPDATE,
                updatedFlowStep.getDescription());
        TestCase.assertNotNull("UPDATE: The 'Date Created' needs to be set.", updatedFlowStep.getDateCreated());
        TestCase.assertNotNull("UPDATE: The 'Date Last Updated' needs to be set.", updatedFlowStep.getDateLastUpdated());

        //4. Get by Id...
        FlowStep byIdFlowStep = flowStepClient.getFlowStepById(updatedFlowStep.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlowStep.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdFlowStep.getName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdFlowStep.getDescription());
        TestCase.assertNotNull("BY_ID: The 'Date Created' needs to be set.", byIdFlowStep.getDateCreated());
        TestCase.assertNotNull("BY_ID: The 'Date Last Updated' needs to be set.", byIdFlowStep.getDateLastUpdated());

        //5. Delete...
        FlowStep deletedFlowStep = flowStepClient.deleteFlowStep(byIdFlowStep);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedFlowStep.getId());
    }


}
