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

package com.fluidbpm.ws.client.v1.form;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFormContainerHistoricClient extends ABaseTestCase {

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
     * Test fetching the form flow historic data.
     */
    @Test
    @Ignore
    public void testRetrieveFormFlowHistoricData()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, serviceTicket);

        List<FormFlowHistoricData> flowHistoricData =
                formContainerClient.getFormFlowHistoricData(new Form(674L));

        //Confirm the historic data is set...
        TestCase.assertNotNull(flowHistoricData);

        for(FormFlowHistoricData data : flowHistoricData)
        {
            TestCase.assertNotNull(data);
            TestCase.assertNotNull("Id not set",data.getId());
            TestCase.assertNotNull("Date Created not set",data.getDateCreated());
            TestCase.assertNotNull("Rule Executed not set",data.getRuleExecuted());
            TestCase.assertNotNull("Rule Executed Result not set",data.getRuleExecutedResult());
            TestCase.assertNotNull("Log entry type not set",data.getLogEntryType());
        }

        System.out.println("Total of '"+flowHistoricData.size()+"' workflow rules executed.");
    }

    /**
     * Test retrieving the form field historic data.
     */
    @Test
    @Ignore
    public void testRetrieveFormFieldHistoricData()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, serviceTicket);

        List<FormHistoricData> formHistoricData = formContainerClient.getFormAndFieldHistoricData(
                new Form(674L),
                false);


        //Confirm the historic data is set...
        TestCase.assertNotNull(formHistoricData);

        for(FormHistoricData data : formHistoricData)
        {
            TestCase.assertNotNull(data);
            TestCase.assertNotNull("Id not set",data.getId());
            TestCase.assertNotNull("Date not set",data.getDate());
            TestCase.assertNotNull("Description not set",data.getDescription());
            TestCase.assertNotNull("Field not set",data.getField());
            TestCase.assertNotNull("Log entry type not set",data.getLogEntryType());
        }

        System.out.println("Total of '"+formHistoricData.size()+"' versions of form.");
        

        
    }
}
