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

package com.fluid.ws.client.v1.form;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestFormFieldClientSystemFields extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FIELD_NAME = "JUnit Form Field";

        /**
         *
         */
        public static final class SystemField{
            public static final String DATE_CREATED = "Date Created";
            public static final String DATE_LAST_UPDATED = "Date Last Updated";
            public static final String FORM_TYPE = "Form Type";
            public static final String FORM_FLOW_STATE = "Form Flow State";
            public static final String TITLE = "Title";

            public static final String E_MAIL_FROM_ADDRESS = "Email From Address";
            public static final String E_MAIL_TO_ADDRESS = "Email To Address";
            public static final String E_MAIL_SUBJECT = "Email Subject";
            public static final String E_MAIL_SENT_DATE = "Email Sent Date";
            public static final String E_MAIL_RECEIVED_DATE = "Email Received Date";
            public static final String E_MAIL_UNIQUE_IDENTIFIER = "Email Unique Identifier";

        }

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
    public void testFormFieldSystemField_DateCreated()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.DATE_CREATED);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_DateLastUpdated()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.DATE_LAST_UPDATED);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_FormType()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.FORM_TYPE);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_FormFlowState()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.FORM_FLOW_STATE);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_Title()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.TITLE);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailFromAddress()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_FROM_ADDRESS);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailToAddress()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_TO_ADDRESS);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailSubject()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_SUBJECT);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailSentDate()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_SENT_DATE);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailReceivedDate()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_RECEIVED_DATE);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }

    /**
     *
     */
    @Test
    public void testFormFieldSystemField_EmailUniqueIdentifierDate()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.SystemField.E_MAIL_UNIQUE_IDENTIFIER);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Should not be allowed to create a system field.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }
    }
}
