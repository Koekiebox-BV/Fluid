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

package com.fluidbpm.ws.client.v1.flow;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.MultiChoice;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestRouteFieldClient extends ABaseTestCase {

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

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Text.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldTextPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Text.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Text.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testRouteField_TrueFalse_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldTrueFalse(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.TrueFalse.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldTrueFalse(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.TrueFalse.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.TrueFalse.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testRouteField_ParagraphTextPlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldParagraphTextPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldParagraphTextPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testRouteField_ParagraphTextHTML_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldParagraphTextHTML(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.HTML, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldParagraphTextHTML(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.HTML, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.ParagraphText.HTML, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRouteField_MultiChoicePlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldMultiChoicePlain(
                toCreate, TestStatics.MultiChoice.CREATE_LIST);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.MultiChoice.PLAIN, createdField.getTypeMetaData());

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
        Field updatedField = routeFieldClient.updateFieldMultiChoicePlain(
                createdField, TestStatics.MultiChoice.UPDATE_LIST);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.MultiChoice.PLAIN, updatedField.getTypeMetaData());

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
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.MultiChoice.PLAIN, byIdField.getTypeMetaData());

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
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRouteField_MultiChoiceSelectMany_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        try
        {
            routeFieldClient.createFieldMultiChoiceSelectMany(
                    toCreate, TestStatics.MultiChoice.CREATE_LIST);

            TestCase.fail("Expected an error for creating " +
                    "multiple choice.");
        }
        catch (FluidClientException fce)
        {
            if(FluidClientException.ErrorCode.FIELD_VALIDATE != fce.getErrorCode())
            {
                TestCase.fail("Expected field validate error code.");
            }
        }
    }

    /**
     *
     */
    @Test
    public void testRouteField_DateTimeDate_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL,serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldDateTimeDate(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldDateTimeDate(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testRouteField_DateTimeDateAndTime_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldDateTimeDateAndTime(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldDateTimeDateAndTime(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }

    /**
     *
     */
    @Test
    public void testRouteField_DecimalPlain_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RouteFieldClient routeFieldClient = new RouteFieldClient(BASE_URL, serviceTicket);

        //1. Text...
        Field toCreate = new Field();
        toCreate.setFieldName(TestStatics.FIELD_NAME);
        toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

        //2. Create...
        Field createdField = routeFieldClient.createFieldDecimalPlain(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
        TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getFieldType());
        TestCase.assertEquals("'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Decimal.PLAIN, createdField.getTypeMetaData());

        //3. Update...
        createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
        createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
        Field updatedField = routeFieldClient.updateFieldDecimalPlain(createdField);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
        TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getFieldType());
        TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Decimal.PLAIN, updatedField.getTypeMetaData());

        //4. Get by Id...
        Field byIdField = routeFieldClient.getFieldById(updatedField.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
        TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getFieldType());
        TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
                RouteFieldClient.FieldMetaData.Decimal.PLAIN, byIdField.getTypeMetaData());

        //5. Delete...
        Field deletedField = routeFieldClient.deleteField(byIdField);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
    }
}
