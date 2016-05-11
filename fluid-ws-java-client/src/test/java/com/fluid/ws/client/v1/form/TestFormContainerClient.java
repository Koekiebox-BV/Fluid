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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFormContainerClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";

        /**
         *
         */
        public static final class FieldName{
            public static final String EMAIL_FROM_ADDRESS = "Email From Address";
            public static final String EMAIL_TO_ADDRESS = "Email To Address";
            public static final String EMAIL_SUBJECT = "Email Subject";
            public static final String EMAIL_SENT_DATE = "Email Sent Date";
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
    public void testCRUDFormContainer()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(serviceTicket);

        //1. Form...
        Form toCreate = new Form(TestStatics.FORM_DEFINITION);
        toCreate.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        List<Field> fields = new ArrayList<>();
        fields.add(new Field(TestStatics.FieldName.EMAIL_FROM_ADDRESS, "zapper@zool.com"));
        fields.add(new Field(TestStatics.FieldName.EMAIL_TO_ADDRESS, "pateldream@correct.com"));
        fields.add(new Field(TestStatics.FieldName.EMAIL_SUBJECT, "This must be a subject..."));

        toCreate.setFormFields(fields);

        //Create...
        Form createdForm = formContainerClient.createFormContainer(toCreate);

        TestCase.assertNotNull("The 'Form Container' needs to be set.",
                createdForm);
        TestCase.assertNotNull("The 'Form Container Id' needs to be set.",
                createdForm.getId());
        TestCase.assertNotNull("The 'Form Fields' needs to be set.",
                createdForm.getFormFields());
        TestCase.assertEquals("The number of 'Form Fields' is not equal.",
                3, createdForm.getFormFields().size());

        createdForm.getFormFields().add(
                new Field(TestStatics.FieldName.EMAIL_SENT_DATE, new Date()));

        //Updated
        Form updatedForm = formContainerClient.updateFormContainer(createdForm);

        TestCase.assertNotNull("The 'Form Container' needs to be set.",
                updatedForm);
        TestCase.assertNotNull("The 'Form Container Id' needs to be set.",
                updatedForm.getId());
        TestCase.assertNotNull("The 'Form Fields' needs to be set.",
                updatedForm.getFormFields());
        TestCase.assertEquals("The number of 'Form Fields' is not equal.",
                4, updatedForm.getFormFields().size());

        Form deletedForm = formContainerClient.deleteFormContainer(updatedForm);

        TestCase.assertNotNull("The 'Form Container' needs to be set.",
                deletedForm);
        TestCase.assertNotNull("The 'Form Container Id' needs to be set.",
                deletedForm.getId());
    }
}
