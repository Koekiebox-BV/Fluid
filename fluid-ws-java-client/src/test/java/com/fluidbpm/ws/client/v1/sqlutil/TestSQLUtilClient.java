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

package com.fluidbpm.ws.client.v1.sqlutil;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.TestFlowClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestSQLUtilClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";
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
    @Ignore
    public void testGetTableFormsWithSpecificId()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, serviceTicket);

        int numberOfRecords = 5;
        Form[] testForms =
                TestSQLUtilWebSocketClient.generateLotsOfFormsFor(
                        numberOfRecords, 2192L);

        long start = System.currentTimeMillis();

        for(Form forToSend :testForms)
        {
            List<Form> tableForms = sqlUtilClient.getTableForms(
                    forToSend,true);

            if(tableForms != null && tableForms.size() > 0)
            {
                for(Form form : tableForms)
                {
                    /*System.out.println(form.getFormType() +
                            " - " +
                            form.getTitle());*/
                }
            }
            else
            {
                System.out.println("Nothing...");
            }
        }

        long took = (System.currentTimeMillis() - start);
        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
    }

    /**
     *
     */
    @Test
    @Ignore
    public void testGetTableForms()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, serviceTicket);
        FlowClient flowClient = new FlowClient(BASE_URL, serviceTicket);

        //1. Form...
        Form frm = new Form(TestStatics.FORM_DEFINITION);
        frm.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        //Fluid Item...
        FluidItem toCreate = new FluidItem();
        toCreate.setForm(frm);

        //0. Create the Flow...
        Flow flowToCreate = new Flow();
        flowToCreate.setName(TestFlowClient.TestStatics.FLOW_NAME);
        flowToCreate.setDescription(TestFlowClient.TestStatics.FLOW_DESCRIPTION);

        Flow createdFlow = flowClient.createFlow(flowToCreate);

        //Create...
        FluidItem createdItem =
                flowItmClient.createFlowItem(toCreate, createdFlow.getName());

        //Wait for 3 seconds...
        sleepForSeconds(3);

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, serviceTicket);


        List<Form> tableForms = sqlUtilClient.getTableForms(
                createdItem.getForm(),true);

        //Cleanup...
        flowClient.forceDeleteFlow(createdFlow);
    }
}
