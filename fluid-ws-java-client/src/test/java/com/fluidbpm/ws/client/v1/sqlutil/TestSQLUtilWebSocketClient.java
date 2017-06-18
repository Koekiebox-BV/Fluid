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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.TableField;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

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
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(serviceTicket));
        }

        SQLUtilWebSocketGetTableFormsClient webSocketClient =
                new SQLUtilWebSocketGetTableFormsClient(
                        BASE_URL,
                        null, serviceTicketHex, TimeUnit.SECONDS.toMillis(60), true);

        long start = System.currentTimeMillis();
        int numberOfRecords = 1;

        List<FormListing> formListing = webSocketClient.getTableFormsSynchronized(
                generateLotsOfFormsFor(numberOfRecords, 2192L));

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

        if(formListing != null)
        {
            System.out.println("Listing is '"+formListing.size()+"' -> \n\n\n");

            for(FormListing listing : formListing)
            {
                //System.out.println("Response For ::: "+listing.getEcho());

                List<Form> tableForms = listing.getListing();

                for(Form form : tableForms)
                {
                    System.out.println("\n-> "+form.getFormType() + " - " + form.getTitle());

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
    public void testGetAncestorFormWithSpecificId()
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
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(serviceTicket));
        }

        SQLUtilWebSocketGetAncestorClient webSocketClient =
                new SQLUtilWebSocketGetAncestorClient(
                        BASE_URL,
                        null, serviceTicketHex, TimeUnit.SECONDS.toMillis(60), true, true);

        long start = System.currentTimeMillis();

        Form toGetParentFor = new Form(8457L);
        toGetParentFor.setEcho("event-patoel");

        Form ancestorForm = webSocketClient.getAncestorSynchronized(toGetParentFor);

        TestCase.assertNotNull(
                "Ancestor Form not set.", ancestorForm);

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for lookup.");

        System.out.println(ancestorForm.getFormType() +
                " - " + ancestorForm.getTitle());

        if(ancestorForm.getFormFields() != null)
        {
            for(Field field : ancestorForm.getFormFields())
            {
                System.out.println("["+field.getFieldName()+"] = '"+
                        field.getFieldValue()+"'");
            }
        }
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
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(serviceTicket));
        }

        SQLUtilWebSocketGetDescendantsClient webSocketClient =
                new SQLUtilWebSocketGetDescendantsClient(
                        BASE_URL,
                        null,
                        serviceTicketHex,
                        TimeUnit.SECONDS.toMillis(60),
                        true,
                        true,
                        true);

        long start = System.currentTimeMillis();

        int numberOfRecords = 1;

        List<FormListing> formListing = webSocketClient.getDescendantsSynchronized(
                generateLotsOfFormsFor(numberOfRecords, 2/*1769*//*246L*/));

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
                    System.out.println("\n--> "+form.getFormType() +
                            " - " +
                            form.getTitle());

                    if(form.getFormFields() != null)
                    {
                        for(Field field : form.getFormFields())
                        {
                            System.out.println("["+field.getFieldName()+"] = '"+
                                    field.getFieldValue()+"'");

                            if(field.getTypeAsEnum() == Field.Type.Table)
                            {
                                TableField tableField =
                                        form.getFieldValueAsTableField(field.getFieldName());

                                if(tableField == null ||
                                        (tableField.getTableRecords() == null ||
                                                tableField.getTableRecords().isEmpty()))
                                {
                                    continue;
                                }

                                for(Form tableRecord : tableField.getTableRecords())
                                {
                                    if(tableRecord.getFormFields() == null)
                                    {
                                        continue;
                                    }

                                    for(Field tableRecordField : tableRecord.getFormFields())
                                    {
                                        System.out.println("["+field.getFieldName()+":"+
                                                tableRecordField.getFieldName()+"] = '"+
                                                tableRecordField.getFieldValue()+"'");
                                    }
                                }
                            }
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
     * Make use of a WebSocket to test the SQL execution function.
     *
     */
    @Test
    @Ignore
    public void testExecuteSQLWhereIdGreaterThan()
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
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(serviceTicket));
        }

        SQLUtilWebSocketExecuteSQLClient webSocketClient =
                new SQLUtilWebSocketExecuteSQLClient(
                        BASE_URL,
                        null, serviceTicketHex, TimeUnit.SECONDS.toMillis(10));

        long start = System.currentTimeMillis();

        int numberOfRecords = 1;

        Form formToUse = new Form();
        formToUse.setEcho("zool");
        formToUse.setFieldValue("SQL Query",
                "SELECT * FROM form_container " +
                "WHERE id > ?" +
                " LIMIT 0,1000;");

        formToUse.getFormFields().add(new Field(1L,"Zool",new Long(1000L), Field.Type.Decimal));

        List<FormListing> formListing = webSocketClient.executeSQLSynchronized(formToUse);

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

        if(formListing != null)
        {
            for(FormListing listing : formListing)
            {
                List<Form> resultForms = listing.getListing();

                for(Form form : resultForms)
                {
                    System.out.println(form.getFormTypeId() +
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
