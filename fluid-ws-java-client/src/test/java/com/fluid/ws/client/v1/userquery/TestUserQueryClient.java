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

package com.fluid.ws.client.v1.userquery;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.MultiChoice;
import com.fluid.program.api.vo.item.FluidItemListing;
import com.fluid.program.api.vo.userquery.UserQuery;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserQueryClient extends ABaseTestCase {

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
    @Ignore
    public void executeUserQueryWithSpecificName()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserQueryClient userQueryClient = new UserQueryClient(serviceTicket);

        UserQuery userQueryToExec = new UserQuery();
        userQueryToExec.setName("Client is Input");

        List<Field> inputs = new ArrayList<>();


        List<String> selectedClients = new ArrayList<>();
        selectedClients.add("PPC");

        MultiChoice selectedMultiChoices = new MultiChoice(selectedClients);

        inputs.add(new Field("Client",selectedMultiChoices));
        userQueryToExec.setInputs(inputs);

        long start = System.currentTimeMillis();

        FluidItemListing itemListing =
                userQueryClient.executeUserQuery(userQueryToExec);

        if(itemListing.getListingCount() > 0)
        {
            for(FluidItem returnVal :itemListing.getListing())
            {
                System.out.println("*** [[[ " + returnVal.getForm().getFormType() + " - "+
                        returnVal.getForm().getTitle() + " ]]] ***");

                for(Field formField : returnVal.getForm().getFormFields())
                {
                    System.out.println(formField.getFieldName() + " : "+formField.getFieldValue());
                }
            }
        }

        long took = (System.currentTimeMillis() - start);
        System.out.println("Took '"+took+"' millis for '"+itemListing.getListingCount()+
                "' records.");
    }
}
