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
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.flow.FlowClient;
import com.fluid.ws.client.v1.user.LoginClient;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestFormDefinitionClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FORM_TYPE = "JUnit Form Definition";
        public static final String FORM_DESCRIPTION = "JUnit Form Definition Description.";

        //Fields...
        public static final String FIELD_NAME_FIRST = "JUnit First Form Field";
        public static final String FIELD_NAME_SECOND = "JUnit Second Form Field";

        //Flow...
        public static final String FLOW_NAME_FIRST = "JUnit First Flow";
        public static final String FLOW_NAME_SECOND = "JUnit Second Flow";

        public static final String FORM_TYPE_UPDATE = "JUnit Form Definition UPDATED";
        public static final String FORM_DESCRIPTION_UPDATE = "JUnit Form Definition Description UPDATED.";

        /**
         *
         */
        public static final class SystemFormType{
            private static final String E_MAIL = "Email";
        }
    }

    /**
     *
     */
    @Before
    public void init()
    {
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @Test
    public void testFormDefinitionCRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(serviceTicket);
        FormFieldClient formFieldClient = new FormFieldClient(serviceTicket);
        FlowClient flowClient = new FlowClient(serviceTicket);

        //1. Create 2 Form Fields...
        Field fieldFirst = new Field();
        fieldFirst.setFieldName(TestStatics.FIELD_NAME_FIRST);
        fieldFirst.setFieldDescription(TestStatics.FIELD_NAME_FIRST);

        Field fieldSecond = new Field();
        fieldSecond.setFieldName(TestStatics.FIELD_NAME_SECOND);
        fieldSecond.setFieldDescription(TestStatics.FIELD_NAME_SECOND);

        fieldFirst = formFieldClient.createFieldTextPlain(fieldFirst);
        fieldSecond = formFieldClient.createFieldTextPlain(fieldSecond);

        List<Field> createdFields = new ArrayList<Field>();
        createdFields.add(fieldFirst);
        createdFields.add(fieldSecond);

        //2. Create 2 Flows...
        Flow flowFirst = new Flow();
        flowFirst.setName(TestStatics.FLOW_NAME_FIRST);
        flowFirst.setDescription(TestStatics.FLOW_NAME_FIRST);

        Flow flowSecond = new Flow();
        flowSecond.setName(TestStatics.FLOW_NAME_SECOND);
        flowSecond.setDescription(TestStatics.FLOW_NAME_SECOND);

        flowFirst = flowClient.createFlow(flowFirst);
        flowSecond = flowClient.createFlow(flowSecond);

        List<Flow> createdFlows = new ArrayList<Flow>();
        createdFlows.add(flowFirst);
        createdFlows.add(flowSecond);

        //3. Form...
        Form toCreate = new Form();
        toCreate.setFormType(TestStatics.FORM_TYPE);
        toCreate.setFormDescription(TestStatics.FORM_DESCRIPTION);
        toCreate.setFormFields(createdFields);
        toCreate.setAssociatedFlows(createdFlows);

        //2. Create...
        Form createdFormDef = formDefinitionClient.createFormDefinition(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdFormDef.getId());
        TestCase.assertEquals("'Form Type' mismatch.", TestStatics.FORM_TYPE, createdFormDef.getFormType());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.FORM_DESCRIPTION,
                createdFormDef.getFormDescription());
        TestCase.assertNotNull("The 'Form Fields' needs to be set.", createdFormDef.getFormFields());
        TestCase.assertEquals("'Associated Fields' count mismatch.",
                createdFields.size(),
                createdFormDef.getFormFields().size());
        TestCase.assertNotNull("The 'Associated Flows' needs to be set.", createdFormDef.getAssociatedFlows());
        TestCase.assertEquals("'Associated Flows' count mismatch.",
                createdFlows.size(),
                createdFormDef.getAssociatedFlows().size());

        //3. Update...
        createdFormDef.setFormType(TestStatics.FORM_TYPE_UPDATE);
        createdFormDef.setFormDescription(TestStatics.FORM_DESCRIPTION_UPDATE);

        List<Field> updateFields = new ArrayList<Field>();
        updateFields.add(fieldFirst);
        createdFormDef.setFormFields(updateFields);

        List<Flow> updateFlows = new ArrayList<Flow>();
        updateFlows.add(flowFirst);
        createdFormDef.setAssociatedFlows(updateFlows);

        Form updatedFormDef = formDefinitionClient.updateFormDefinition(createdFormDef);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedFormDef.getId());
        TestCase.assertEquals("UPDATE: 'Form Type' mismatch.", TestStatics.FORM_TYPE_UPDATE, updatedFormDef.getFormType());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FORM_DESCRIPTION_UPDATE,
                updatedFormDef.getFormDescription());
        TestCase.assertNotNull("UPDATE: The 'Form Fields' needs to be set.", updatedFormDef.getFormFields());
        TestCase.assertEquals("'UPDATE: Associated Fields' count mismatch.",
                updateFields.size(),
                updatedFormDef.getFormFields().size());
        TestCase.assertNotNull("UPDATE: The 'Associated Flows' needs to be set.", updatedFormDef.getAssociatedFlows());
        TestCase.assertEquals("'UPDATE: Associated Flows' count mismatch.",
                updateFlows.size(),
                updatedFormDef.getAssociatedFlows().size());

        //4. Get by Id...
        Form byIdFormDef = formDefinitionClient.getFormDefinitionById(updatedFormDef.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdFormDef.getId());
        TestCase.assertNotNull("BY_ID: The 'Form Type' needs to be set.", byIdFormDef.getFormType());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdFormDef.getFormDescription());
        TestCase.assertNotNull("BY_ID: The 'Form Fields' needs to be set.", byIdFormDef.getFormFields());
        TestCase.assertEquals("'BY_ID: Associated Fields' count mismatch.",
                updateFields.size(),
                byIdFormDef.getFormFields().size());
        TestCase.assertNotNull("BY_ID: The 'Associated Flows' needs to be set.", byIdFormDef.getAssociatedFlows());
        TestCase.assertEquals("'BY_ID: Associated Flows' count mismatch.",
                updateFlows.size(),
                byIdFormDef.getAssociatedFlows().size());

        //5. Delete Form Def...
        Form deletedForm = formDefinitionClient.deleteFormDefinition(byIdFormDef);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedForm.getId());

        //6. Delete Fields...
        formFieldClient.deleteField(fieldFirst);
        formFieldClient.deleteField(fieldSecond);

        //7. Delete Flows...
        flowClient.deleteFlow(flowFirst);
        flowClient.deleteFlow(flowSecond);
    }

    /**
     *
     */
    @Test
    public void testInvalidCreateData()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(serviceTicket);
        FormFieldClient formFieldClient = new FormFieldClient(serviceTicket);

        //1. The Test Flow - NO FIELDS...
        Form toCreate = new Form();
        toCreate.setFormType(TestStatics.FORM_TYPE);
        toCreate.setFormDescription(TestStatics.FORM_DESCRIPTION);

        try{
            formDefinitionClient.createFormDefinition(toCreate);

            TestCase.fail("Expected an error due to Associated Fields being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        //Create a Field...
        Field fieldFirst = new Field();
        fieldFirst.setFieldName(TestStatics.FIELD_NAME_FIRST);
        fieldFirst.setFieldDescription(TestStatics.FIELD_NAME_FIRST);

        fieldFirst = formFieldClient.createFieldTextPlain(fieldFirst);

        List<Field> fieldsToUse = new ArrayList<>();
        fieldsToUse.add(fieldFirst);


        //1. The Test Flow...
        toCreate = new Form();
        toCreate.setFormType("");
        toCreate.setFormDescription(TestStatics.FORM_DESCRIPTION);
        toCreate.setFormFields(fieldsToUse);

        //2. Create - NAME...
        try{
            formDefinitionClient.createFormDefinition(toCreate);

            TestCase.fail("Expected an error due to Type being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        //1. The Test Flow...
        toCreate = new Form();
        toCreate.setFormType(TestStatics.FORM_TYPE);
        toCreate.setFormDescription("");
        toCreate.setFormFields(fieldsToUse);

        try{
            formDefinitionClient.createFormDefinition(toCreate);

            TestCase.fail("Expected an error due to Description being empty.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        formFieldClient.deleteField(fieldFirst);
    }

    /**
     *
     */
    @Test
    public void testFormDefinitionSystem_Mail()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(serviceTicket);
        FormFieldClient formFieldClient = new FormFieldClient(serviceTicket);

        //1. Text...
        Field fieldFirst = new Field();
        fieldFirst.setFieldName(TestStatics.FIELD_NAME_FIRST);
        fieldFirst.setFieldDescription(TestStatics.FIELD_NAME_FIRST);

        fieldFirst = formFieldClient.createFieldTextPlain(fieldFirst);

        List<Field> fieldsToUse = new ArrayList<>();
        fieldsToUse.add(fieldFirst);

        Form toCreate = new Form();
        toCreate.setFormType(TestStatics.SystemFormType.E_MAIL);
        toCreate.setFormDescription(TestStatics.FORM_DESCRIPTION);
        toCreate.setFormFields(fieldsToUse);

        //2. Create...
        try{
            formDefinitionClient.createFormDefinition(toCreate);

            TestCase.fail("Should not be allowed to create a system form definition.");
        }
        catch (FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }

        formFieldClient.deleteField(fieldFirst);
    }

    /**
     *
     */
    @Test
    public void testDuplicateCreateData()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(serviceTicket);
        FormFieldClient formFieldClient = new FormFieldClient(serviceTicket);

        //Create a Field...
        Field fieldFirst = new Field();
        fieldFirst.setFieldName(TestStatics.FIELD_NAME_FIRST);
        fieldFirst.setFieldDescription(TestStatics.FIELD_NAME_FIRST);

        fieldFirst = formFieldClient.createFieldTextPlain(fieldFirst);

        List<Field> fieldsToUse = new ArrayList<>();
        fieldsToUse.add(fieldFirst);

        //1. The Test Flow...
        Form toCreate = new Form();
        toCreate.setFormType(TestStatics.FORM_TYPE);
        toCreate.setFormDescription(TestStatics.FORM_DESCRIPTION);
        toCreate.setFormFields(fieldsToUse);

        //2. Create - NAME...
        try{
            toCreate = formDefinitionClient.createFormDefinition(toCreate);

            formDefinitionClient.createFormDefinition(toCreate);
            TestCase.fail("Expected an error due to Definition duplication.");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.DUPLICATE,
                    fluidExcept.getErrorCode());
        }

        formDefinitionClient.deleteFormDefinition(toCreate);
        formFieldClient.deleteField(fieldFirst);
    }
}
