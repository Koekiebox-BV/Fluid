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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestFormFieldClientDuplicates extends ABaseLoggedInTestCase {

    private LoginClient loginClient;

    public static final String FIELD_NAME = "JUnit Form Field";
    public static final String FIELD_DESCRIPTION = "JUnit Form Field Description.";
    
    /**
     *
     */
    public static final class MultiChoiceTest {

        public static final String OPTION_1 = "Andrew";
        public static final String OPTION_2 = "Benny";
        public static final String OPTION_3 = "Charles";
        public static final String OPTION_3_SPACE = " Charles ";
        public static final String OPTION_4 = "Derick";
        public static final String OPTION_5 = "Edwin";
        public static final String OPTION_6 = "Frik";

        /**
         *
         */
        public static final List CREATE_LIST = new ArrayList();
        static {
            CREATE_LIST.add(MultiChoiceTest.OPTION_1);
            CREATE_LIST.add(MultiChoiceTest.OPTION_2);
            CREATE_LIST.add(MultiChoiceTest.OPTION_3);
            CREATE_LIST.add(MultiChoiceTest.OPTION_4);
            CREATE_LIST.add(MultiChoiceTest.OPTION_5);
            CREATE_LIST.add(MultiChoiceTest.OPTION_6);
        }

        /**
         *
         */
        public static final List UPDATE_LIST = new ArrayList();
        static {
            UPDATE_LIST.add(MultiChoiceTest.OPTION_1);
            UPDATE_LIST.add(MultiChoiceTest.OPTION_2);
            UPDATE_LIST.add(MultiChoiceTest.OPTION_3);
        }

        /**
         *
         */
        public static final List DUPLICATE_UPDATE_LIST = new ArrayList();
        static {
            DUPLICATE_UPDATE_LIST.add(MultiChoiceTest.OPTION_1);
            DUPLICATE_UPDATE_LIST.add(MultiChoiceTest.OPTION_2);
            DUPLICATE_UPDATE_LIST.add(MultiChoiceTest.OPTION_3);
            DUPLICATE_UPDATE_LIST.add(MultiChoiceTest.OPTION_3_SPACE);
        }
    }
    
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
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
        this.loginClient = new LoginClient(BASE_URL);
    }

    /**
     *
     */
    @After
    public void destroy() {
        this.loginClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testFormField_TextPlain_Duplicate() {
        if (this.isConnectionInValid) return;

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(FIELD_NAME);
        toCreate.setFieldDescription(FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = formFieldClient.createFieldTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", FIELD_NAME, createdField.getFieldName());

        try {
            formFieldClient.createFieldTextPlain(toCreate);

            TestCase.fail("Expected an error due to Name being empty.");
        } catch(FluidClientException fluidExcept) {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }

        //5. Delete...
        Field deletedField = formFieldClient.deleteField(createdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_MultiChoicePlain_DuplicateUpdate() {
        if (this.isConnectionInValid) return;

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);
        String serviceTicket = appRequestToken.getServiceTicket();
        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);
        
        Field toCreate = new Field();
        toCreate.setFieldName(FIELD_NAME);
        toCreate.setFieldDescription(FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = formFieldClient.createFieldMultiChoicePlain(toCreate, MultiChoiceTest.CREATE_LIST);
        if (!(createdField.getFieldValue() instanceof MultiChoice)) {
            TestCase.fail("Return value for field Object must be 'MultiChoice'. Type is '"+
                    (createdField.getFieldValue() == null ? "not-set": createdField.getFieldValue().getClass())+"'.");
        }

        //3. Update list with 3 fewer options...
        Field updatedField = formFieldClient.updateFieldMultiChoicePlain(createdField, MultiChoiceTest.UPDATE_LIST);
        if (!(updatedField.getFieldValue() instanceof MultiChoice)) {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
                castedUpdated.getAvailableMultiChoices());
        TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
                MultiChoiceTest.UPDATE_LIST.size(), castedUpdated.getAvailableMultiChoices().size());

        //4. Get by Id...
        Field byIdField = formFieldClient.getFieldByName(FIELD_NAME);
        if (!(byIdField.getFieldValue() instanceof MultiChoice)) {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
                castedById.getAvailableMultiChoices());
        TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
                MultiChoiceTest.UPDATE_LIST.size(), castedById.getAvailableMultiChoices().size());

        try {
            formFieldClient.updateFieldMultiChoicePlain(byIdField, MultiChoiceTest.DUPLICATE_UPDATE_LIST);
            //TestCase.fail("Expected duplicate check");
        } catch (Exception err) {
            System.out.println(err);
        }

        //5. Delete...
        Field deletedField = formFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

}
