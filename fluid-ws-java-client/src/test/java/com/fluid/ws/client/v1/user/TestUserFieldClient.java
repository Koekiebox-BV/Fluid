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

package com.fluid.ws.client.v1.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.MultiChoice;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestUserFieldClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FIELD_NAME = "JUnit Route Field";
        public static final String FIELD_DESCRIPTION = "JUnit Route Field Description.";

        public static final String FIELD_NAME_UPDATE = "JUnit Route Field UPDATED";
        public static final String FIELD_DESCRIPTION_UPDATE = "JUnit Route Field Description UPDATED.";

        /**
         *
         */
        public static final class MultiChoice{

            public static final String OPTION_1 = "Andrew";
            public static final String OPTION_2 = "Benny";
            public static final String OPTION_3 = "Charles";
            public static final String OPTION_4 = "Derick";
            public static final String OPTION_5 = "Edwin";
            public static final String OPTION_6 = "Frik";

            /**
             *
             */
            public static final List CREATE_LIST = new ArrayList();
            static {
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_1);
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_2);
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_3);
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_4);
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_5);
                CREATE_LIST.add(TestStatics.MultiChoice.OPTION_6);
            }

            /**
             *
             */
            public static final List UPDATE_LIST = new ArrayList();
            static {
                UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_1);
                UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_2);
                UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_3);
            }
        }
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
    public void testRouteField_TextPlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Text.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldTextPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Text.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Text.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_TrueFalse_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldTrueFalse(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.TrueFalse.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldTrueFalse(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.TrueFalse.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.TrueFalse.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_ParagraphTextPlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldParagraphTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldParagraphTextPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_ParagraphTextHTML_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldParagraphTextHTML(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.HTML, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldParagraphTextHTML(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.HTML, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.ParagraphText.HTML, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_MultiChoicePlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldMultiChoicePlain(
                toCreate, TestStatics.MultiChoice.CREATE_LIST);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.PLAIN, createdField.getTypeMetaData());

        if(!(createdField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice casted = (MultiChoice)createdField.getFieldValue();

        TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
        TestCase.assertEquals("The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.CREATE_LIST.size(),
                casted.getSelectedMultiChoices().size());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldMultiChoicePlain(
                createdField, TestStatics.MultiChoice.UPDATE_LIST);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.PLAIN, updatedField.getTypeMetaData());

        if(!(updatedField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
                castedUpdated.getSelectedMultiChoices());
        TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.UPDATE_LIST.size(),
                castedUpdated.getSelectedMultiChoices().size());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.PLAIN, byIdField.getTypeMetaData());

        if(!(byIdField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
                castedById.getSelectedMultiChoices());
        TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.UPDATE_LIST.size(),
                castedById.getSelectedMultiChoices().size());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_MultiChoiceSelectMany_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldMultiChoiceSelectMany(
                toCreate, TestStatics.MultiChoice.CREATE_LIST);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, createdField.getTypeMetaData());

        if(!(createdField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice casted = (MultiChoice)createdField.getFieldValue();

        TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
        TestCase.assertEquals("The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.CREATE_LIST.size(),
                casted.getSelectedMultiChoices().size());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldMultiChoiceSelectMany(
                createdField, TestStatics.MultiChoice.UPDATE_LIST);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, updatedField.getTypeMetaData());

        if(!(updatedField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
                castedUpdated.getSelectedMultiChoices());
        TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.UPDATE_LIST.size(),
                castedUpdated.getSelectedMultiChoices().size());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, byIdField.getTypeMetaData());

        if(!(byIdField.getFieldValue() instanceof MultiChoice))
        {
            TestCase.fail("Return value for field Object must be 'MultiChoice'.");
        }

        MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

        TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
                castedById.getSelectedMultiChoices());
        TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
                TestStatics.MultiChoice.UPDATE_LIST.size(),
                castedById.getSelectedMultiChoices().size());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_DateTimeDate_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldDateTimeDate(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldDateTimeDate(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_DateTimeDateAndTime_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldDateTimeDateAndTime(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldDateTimeDateAndTime(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testFormField_DecimalPlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserFieldClient userFieldClient = new UserFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = userFieldClient.createFieldDecimalPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Decimal.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = userFieldClient.updateFieldDecimalPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Decimal.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = userFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                UserFieldClient.FieldMetaData.Decimal.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = userFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }
}
