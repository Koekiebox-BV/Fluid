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

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestFormFieldClientInvalids extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FIELD_NAME = "JUnit Form Field";
        public static final String FIELD_DESCRIPTION = "JUnit Form Field Description.";
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
    public void testFormFieldInvalid_NoFieldName()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName("");
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try {
            formFieldClient.createFieldTextMasked(toCreate, "");

            TestCase.fail("Should not be allowed to create a Field with no Name.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldInvalid_NoFieldDescription()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription("");

        //2. Create...
        try {
            formFieldClient.createFieldTextMasked(toCreate, "");

            TestCase.fail("Should not be allowed to create a Field with no Description.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldInvalid_TextMasked()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try {
            formFieldClient.createFieldTextMasked(toCreate, "");

            TestCase.fail("Should not be allowed to create a Text Masked field with no value.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldInvalid_TextBarcode()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try {
            formFieldClient.createFieldTextBarcode(toCreate, "zool");

            TestCase.fail("Should not be allowed to create a Text Barcode field with 'zool' value.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldInvalid_MultiChoicePlain()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try {
            formFieldClient.createFieldMultiChoicePlain(toCreate, new ArrayList<String>());

            TestCase.fail("Should not be allowed to create a Multi Choice with no selections.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }
    }
}
