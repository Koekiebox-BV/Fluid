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
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FLOW_NAME = "JUnit - Main";
        public static final String FLOW_DESCRIPTION = "JUnit - Testing of Flow.";

        public static final String FLOW_DESCRIPTION_UPDATE = "JUnit - Testing of Flow UPDATED.";
        public static final String FLOW_NAME_UPDATE = "JUnit - Main UPDATED";
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
    public void testFlowCRUD()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);

        //1. The Test Flow...
        Flow toCreate = new Flow();
        toCreate.setName(TestStatics.FLOW_NAME);
        toCreate.setDescription(TestStatics.FLOW_DESCRIPTION);

        //2. Create...
        Flow createdFlow = flowClient.createFlow(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdFlow.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FLOW_NAME, createdFlow.getName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FLOW_DESCRIPTION, createdFlow.getDescription());
        TestCase.assertNotNull("The 'Date Created' needs to be set.", createdFlow.getDateCreated());
        TestCase.assertNotNull("The 'Date Last Updated' needs to be set.", createdFlow.getDateLastUpdated());

        //3. Update...
        createdFlow.setName(TestStatics.FLOW_NAME_UPDATE);
        createdFlow.setDescription(TestStatics.FLOW_DESCRIPTION_UPDATE);
        Flow updatedFlow = flowClient.updateFlow(createdFlow);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedFlow.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FLOW_NAME_UPDATE, updatedFlow.getName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FLOW_DESCRIPTION_UPDATE, updatedFlow.getDescription());
        TestCase.assertNotNull("UPDATE: The 'Date Created' needs to be set.", updatedFlow.getDateCreated());
        TestCase.assertNotNull("UPDATE: The 'Date Last Updated' needs to be set.", updatedFlow.getDateLastUpdated());

        //4. Get by Id...
        Flow byIdFlow = flowClient.getFlowById(updatedFlow.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFlow.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdFlow.getName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdFlow.getDescription());
        TestCase.assertNotNull("BY_ID: The 'Date Created' needs to be set.", byIdFlow.getDateCreated());
        TestCase.assertNotNull("BY_ID: The 'Date Last Updated' needs to be set.", byIdFlow.getDateLastUpdated());

        //5. Delete...
        Flow deletedFlow = flowClient.deleteFlow(byIdFlow);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedFlow.getId());
    }

    /**
     *
     */
    @Test
    public void testInvalidCreateData()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowClient flowClient = new FlowClient(BASE_URL,serviceTicket);

        //1. The Test Flow...
        Flow toCreate = new Flow();
        toCreate.setName("");
        toCreate.setDescription(TestStatics.FLOW_DESCRIPTION);

        //2. Create - NAME...
        try{
            flowClient.createFlow(toCreate);

            TestCase.fail("Expected an error due to Name being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        //3. Create - DESCRIPTION...
        toCreate.setName(TestStatics.FLOW_NAME);
        toCreate.setDescription("");

        try{
            flowClient.createFlow(toCreate);

            TestCase.fail("Expected an error due to Description being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }
}
