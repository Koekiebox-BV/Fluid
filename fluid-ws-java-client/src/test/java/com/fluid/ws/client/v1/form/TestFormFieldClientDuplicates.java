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
public class TestFormFieldClientDuplicates extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FIELD_NAME = "JUnit Form Field";
        public static final String FIELD_DESCRIPTION = "JUnit Form Field Description.";

        public static final String FIELD_NAME_UPDATE = "JUnit Form Field UPDATED";
        public static final String FIELD_DESCRIPTION_UPDATE = "JUnit Form Field Description UPDATED.";
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
    public void testFormField_TextPlain_Duplicate()
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
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = formFieldClient.createFieldTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());

        try{
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Expected an error due to Name being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }

        //5. Delete...
        Field deletedField = formFieldClient.deleteField(createdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }
}
