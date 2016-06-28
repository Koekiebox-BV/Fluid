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

package com.fluid.ws.client.v1.sqlutil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.form.FormListing;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;
import com.google.common.io.BaseEncoding;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestSQLUtilWebSocketClient extends ABaseTestCase {

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
    @Ignore
    public void testGetTableFormsWithSpecificId()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();
        String serviceTicketHex = null;
        if(serviceTicket != null && !serviceTicket.isEmpty())
        {
            serviceTicketHex =
                    BaseEncoding.base16().encode(BaseEncoding.base64().decode(serviceTicket));
        }

        SQLUtilWebSocketGetTableFormsClient webSocketClient =
                new SQLUtilWebSocketGetTableFormsClient(
                        BASE_URL,
                        null, serviceTicketHex, TimeUnit.SECONDS.toMillis(60), true);

        long start = System.currentTimeMillis();

        int numberOfRecords = 1;

        List<FormListing> formListing = webSocketClient.getTableFormsSynchronized(
                generateLotsOfFormsFor(numberOfRecords, 28L));

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

        if(formListing != null)
        {
            for(FormListing listing : formListing)
            {
                //System.out.println("Response For ::: "+listing.getEcho());

                List<Form> tableForms = listing.getListing();

                for(Form form : tableForms)
                {
                    System.out.println(form.getFormType() + " - " + form.getTitle());

                    if(form.getFormFields() == null)
                    {
                        continue;
                    }

                    for(Field field : form.getFormFields())
                    {
                        System.out.println("|"+field.getFieldName()+"|"+
                                field.getType()
                                +"| ->" +field.getFieldValue());
                    }
                }
            }
        }
        else
        {
            System.out.println("Nothing...");
        }

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
    }

    /**
     *
     */
    @Test
    @Ignore
    public void testGetDescendantFormsWithSpecificId()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();
        String serviceTicketHex = null;
        if(serviceTicket != null && !serviceTicket.isEmpty())
        {
            serviceTicketHex =
                    BaseEncoding.base16().encode(BaseEncoding.base64().decode(serviceTicket));
        }

        SQLUtilWebSocketGetDescendantsClient webSocketClient =
                new SQLUtilWebSocketGetDescendantsClient(
                        BASE_URL,
                        null, serviceTicketHex, TimeUnit.SECONDS.toMillis(60), true, true);

        long start = System.currentTimeMillis();

        int numberOfRecords = 1;

        List<FormListing> formListing = webSocketClient.getDescendantsSynchronized(
                generateLotsOfFormsFor(numberOfRecords, 32L));

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

        if(formListing != null)
        {
            for(FormListing listing : formListing)
            {
                //System.out.println("Response For ::: "+listing.getEcho());

                List<Form> descendantsForms = listing.getListing();

                for(Form form : descendantsForms)
                {
                    System.out.println(form.getFormType() +
                            " - " +
                            form.getTitle());

                    if(form.getFormFields() != null)
                    {
                        for(Field field : form.getFormFields())
                        {
                            System.out.println("["+field.getFieldName()+"] = '"+
                                    field.getFieldValue()+"'");
                        }
                    }
                }
            }
        }
        else
        {
            System.out.println("Nothing...");
        }

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
    }


    /**
     *
     * @param numberOfFormsParam
     * @param idsToPicFrom
     * @return
     */
    public static Form[] generateLotsOfFormsFor(
            int numberOfFormsParam,long ... idsToPicFrom)
    {
        Form[] returnVal = new Form[numberOfFormsParam];
        for(int index = 0;index < numberOfFormsParam; index++)
        {
            //Pic a random form...
            long randomId = idsToPicFrom[0];

            String uuidForm1 = UUID.randomUUID().toString();
            Form form1 = new Form(randomId);
            form1.setEcho(uuidForm1);
            //System.out.println("ExpectResponse: "+uuidForm1);

            returnVal[index] = form1;
        }

        return returnVal;
    }
}
