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
import com.fluid.program.api.vo.MultiChoice;
import com.fluid.program.api.vo.form.TableRecord;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
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
    public void testCRUDFormContainerBasic()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, serviceTicket);

        //1. Form...
        Form toCreate = new Form(TestStatics.FORM_DEFINITION);
        toCreate.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        List<Field> fields = new ArrayList();
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

    /**
     *
     */
    @Test
    public void testCRUDFormContainerAdvanced()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, serviceTicket);

        //Create the Multi Choice Field...
        Field fieldToCreate = new Field();
        fieldToCreate.setFieldName(TestFormFieldClient.TestStatics.FIELD_NAME);
        fieldToCreate.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);

        FormFieldClient formFieldClient =
                new FormFieldClient(BASE_URL, serviceTicket);

        Field createdField = null;
        try{
            createdField = formFieldClient.getFieldByName(
                    TestFormFieldClient.TestStatics.FIELD_NAME);
        }
        catch(FluidClientException fce)
        {
            if(fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT)
            {
                TestCase.fail(fce.getMessage());
            }

            createdField = formFieldClient.createFieldMultiChoicePlain(
                    fieldToCreate, TestFormFieldClient.TestStatics.MultiChoice.CREATE_LIST);
        }

        List<Field> createdFields = new ArrayList();
        createdFields.add(createdField);

        //Create Form Definition...
        Form formDefToCreate = new Form();
        formDefToCreate.setFormType(TestFormDefinitionClient.TestStatics.FORM_TYPE);
        formDefToCreate.setFormDescription(TestFormDefinitionClient.TestStatics.FORM_DESCRIPTION);
        formDefToCreate.setFormFields(createdFields);

        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(BASE_URL, serviceTicket);
        Form createdFormDef = null;

        try
        {
            createdFormDef = formDefinitionClient.getFormDefinitionByName(
                    TestFormDefinitionClient.TestStatics.FORM_TYPE);
        }
        catch(FluidClientException fce)
        {
            if(fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT)
            {
                TestCase.fail(fce.getMessage());
            }

            createdFormDef = formDefinitionClient.createFormDefinition(formDefToCreate);
        }

        //Create the Form Container...
        Form formContainerToCreate = new Form(createdFormDef.getFormType());
        formContainerToCreate.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        formContainerToCreate.setFieldValue(
                TestFormFieldClient.TestStatics.FIELD_NAME, new MultiChoice(
                        TestFormFieldClient.TestStatics.MultiChoice.OPTION_1));

        //Create...
        Form createdForm = formContainerClient.createFormContainer(formContainerToCreate);

        TestCase.assertNotNull("The 'Form Container' needs to be set.", createdForm);
        TestCase.assertNotNull("The 'Form Container Id' needs to be set.",
                createdForm.getId());
        TestCase.assertNotNull("The 'Form Fields' needs to be set.",
                createdForm.getFormFields());
        TestCase.assertEquals("The number of 'Form Fields' is not equal.",
                1, createdForm.getFormFields().size());

        MultiChoice multiChoiceFromCreatedForm = createdForm.getFieldValueAsMultiChoice(
                TestFormFieldClient.TestStatics.FIELD_NAME);

        TestCase.assertNotNull("The 'Multi Choice' needs to be set.",
                multiChoiceFromCreatedForm);
        TestCase.assertNotNull("The 'Multi Choice - SelectedMultiChoices' needs to be set.",
                multiChoiceFromCreatedForm.getSelectedMultiChoices());

        TestCase.assertEquals("The 'Multi Choice' needs to be set.",
                TestFormFieldClient.TestStatics.MultiChoice.OPTION_1,
                multiChoiceFromCreatedForm.getSelectedMultiChoices().get(0));

        //GET FORM CONTAINER TO CONFIRM...
        createdForm = formContainerClient.getFormContainerById(createdForm.getId());

        multiChoiceFromCreatedForm = createdForm.getFieldValueAsMultiChoice(
                TestFormFieldClient.TestStatics.FIELD_NAME);
        TestCase.assertNotNull("FETCH: The 'Multi Choice' needs to be set.",
                multiChoiceFromCreatedForm);
        TestCase.assertEquals("The 'Multi Choice' needs to be set.",
                TestFormFieldClient.TestStatics.MultiChoice.OPTION_1,
                multiChoiceFromCreatedForm.getSelectedMultiChoices().get(0));

        //UPDATE FORM...
        Form formToUpdate = new Form(createdForm.getId());
        formToUpdate.setFormType(createdForm.getFormType());
        formToUpdate.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        formToUpdate.setFieldValue(
                TestFormFieldClient.TestStatics.FIELD_NAME, new MultiChoice(
                        TestFormFieldClient.TestStatics.MultiChoice.OPTION_2));

        Form updatedForm = formContainerClient.updateFormContainer(formToUpdate);

        TestCase.assertNotNull("UPDATE: The 'Form Container' needs to be set.", updatedForm);
        TestCase.assertNotNull("UPDATE:The 'Form Container Id' needs to be set.", updatedForm.getId());
        TestCase.assertNotNull("UPDATE:The 'Form Fields' needs to be set.", updatedForm.getFormFields());
        TestCase.assertEquals("UPDATE:The number of 'Form Fields' is not equal.",
                1, updatedForm.getFormFields().size());

        MultiChoice multiChoiceFromUpdatedForm = updatedForm.getFieldValueAsMultiChoice(
                TestFormFieldClient.TestStatics.FIELD_NAME);

        TestCase.assertNotNull("UPDATE:The 'Multi Choice' needs to be set.",
                multiChoiceFromUpdatedForm);
        TestCase.assertNotNull("UPDATE:The 'Multi Choice - SelectedMultiChoices' needs to be set.",
                multiChoiceFromUpdatedForm.getSelectedMultiChoices());

        TestCase.assertEquals("UPDATE:The 'Multi Choice' needs to be set.",
                TestFormFieldClient.TestStatics.MultiChoice.OPTION_2,
                multiChoiceFromUpdatedForm.getSelectedMultiChoices().get(0));

        //GET FORM CONTAINER TO CONFIRM UPDATE...
        updatedForm = formContainerClient.getFormContainerById(updatedForm.getId());

        multiChoiceFromUpdatedForm = updatedForm.getFieldValueAsMultiChoice(
                TestFormFieldClient.TestStatics.FIELD_NAME);
        TestCase.assertNotNull("FETCH-UPDATE: The 'Multi Choice' needs to be set.",
                multiChoiceFromUpdatedForm);
        TestCase.assertEquals("FETCH-UPDATE: The 'Multi Choice' needs to be set.",
                TestFormFieldClient.TestStatics.MultiChoice.OPTION_2,
                multiChoiceFromUpdatedForm.getSelectedMultiChoices().get(0));

        //DELETE ALL CREATED INFO...
        Form deletedFormContainer = formContainerClient.deleteFormContainer(createdForm);

        TestCase.assertNotNull("The 'Form Container' needs to be set.", deletedFormContainer);
        TestCase.assertNotNull("The 'Form Container Id' needs to be set.", deletedFormContainer.getId());

        //5. Delete Form Def...
        Form deletedForm = formDefinitionClient.deleteFormDefinition(createdFormDef);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedForm.getId());

        //6. Delete Fields...
        formFieldClient.deleteField(createdField);
    }

    /**
     *
     */
    @Test
    public void testCreateDeleteTableRecord()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FormContainerClient formContainerClient = new FormContainerClient(BASE_URL,serviceTicket);
        FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);
        FormDefinitionClient formDefinitionClient = new FormDefinitionClient(BASE_URL,serviceTicket);

        //1. Create the Definitions...
        Field tempTextPlainInvoiceNumber = new Field();
        tempTextPlainInvoiceNumber.setFieldName("JUNIT Invoice Number");
        tempTextPlainInvoiceNumber.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);
        tempTextPlainInvoiceNumber = formFieldClient.createFieldTextPlain(tempTextPlainInvoiceNumber);

        Field tempTextPlainCode = new Field();
        tempTextPlainCode.setFieldName("JUNIT Code");
        tempTextPlainCode.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);
        tempTextPlainCode = formFieldClient.createFieldTextPlain(tempTextPlainCode);

        Field tempTextPlainDescription = new Field();
        tempTextPlainDescription.setFieldName("JUNIT Description");
        tempTextPlainDescription.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);
        tempTextPlainDescription = formFieldClient.createFieldTextPlain(tempTextPlainDescription);

        Field tempMultiPlainTitle = new Field();
        tempMultiPlainTitle.setFieldName("JUNIT Title");
        tempMultiPlainTitle.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);

        List<String> availTitles = new ArrayList();
        availTitles.add("Mr");
        availTitles.add("Mrs");
        availTitles.add("Dr");
        tempMultiPlainTitle = formFieldClient.createFieldMultiChoicePlain(
                tempMultiPlainTitle,availTitles);

        Form formDefinitionLineItem = new Form();
        formDefinitionLineItem.setFormType("JUNIT Line Item");
        formDefinitionLineItem.setFormDescription("Line item definition.");

        List<Field> frmFieldsLineItem = new ArrayList();
        frmFieldsLineItem.add(tempTextPlainCode);
        frmFieldsLineItem.add(tempTextPlainDescription);
        frmFieldsLineItem.add(tempMultiPlainTitle);

        formDefinitionLineItem.setFormFields(frmFieldsLineItem);

        formDefinitionLineItem =
                formDefinitionClient.createFormDefinition(formDefinitionLineItem);

        Field tableFieldToCreate = new Field();
        tableFieldToCreate.setFieldName("JUNIT Line Items");
        tableFieldToCreate.setFieldDescription(TestFormFieldClient.TestStatics.FIELD_DESCRIPTION);

        //2.1 Create Line Item Table Field...
        Field createdFieldTableLineItems = formFieldClient.createFieldTable(
                tableFieldToCreate, formDefinitionLineItem,
                TestFormFieldClient.TestStatics.TableField.SUM_DECIMALS);

        //2.2 Create Form Definition Invoice.
        Form formDefinitionInvoice = new Form();
        formDefinitionInvoice.setFormType("JUNIT Invoice");
        formDefinitionInvoice.setFormDescription("Invoice description.");

        List<Field> frmFieldsInvoice = new ArrayList();
        frmFieldsInvoice.add(tempTextPlainInvoiceNumber);
        frmFieldsInvoice.add(createdFieldTableLineItems);

        formDefinitionInvoice.setFormFields(frmFieldsInvoice);

        formDefinitionInvoice =
                formDefinitionClient.createFormDefinition(formDefinitionInvoice);

        //CREATE THE ITEMS...
        Form toCreate = new Form("JUNIT Invoice");
        toCreate.setTitle("INV_12345");

        List<Field> fields = new ArrayList();
        fields.add(new Field("JUNIT Invoice Number", "INV_12345"));
        toCreate.setFormFields(fields);

        //Create...
        Form createdInvoice = formContainerClient.createFormContainer(toCreate);

        Form formContainerLineItem = new Form("JUNIT Line Item");
        formContainerLineItem.setTitle("INV_12345");

        List<Field> lineItemFields = new ArrayList();

        lineItemFields.add(new Field("JUNIT Description", "Line item description."));
        lineItemFields.add(new Field("JUNIT Code", "Pills001"));
        MultiChoice multiChoiceTitle = new MultiChoice("Mr");
        lineItemFields.add(new Field("JUNIT Title", multiChoiceTitle));

        toCreate.setFormFields(lineItemFields);

        formContainerLineItem.setFormFields(lineItemFields);

        TableRecord tableRecordLineItemToCreate = new TableRecord();
        tableRecordLineItemToCreate.setParentFormContainer(createdInvoice);
        tableRecordLineItemToCreate.setParentFormField(createdFieldTableLineItems);
        tableRecordLineItemToCreate.setFormContainer(formContainerLineItem);

        tableRecordLineItemToCreate =
                formContainerClient.createTableRecord(tableRecordLineItemToCreate);

        TestCase.assertNotNull("Table Record Not Set.", tableRecordLineItemToCreate);

        List<Field> formFields =
                tableRecordLineItemToCreate.getFormContainer().getFormFields();

        TestCase.assertEquals("Expected 3 Table Fields.",
                3,formFields.size());

        for(Field formField : formFields)
        {
            TestCase.assertNotNull("Form Field Id is not set. " +
                    "Expected value to be set.",
                    formField.getId());
        }

        //DELETE THE ITEMS...
        formContainerClient.deleteFormContainer(tableRecordLineItemToCreate.getFormContainer());
        formContainerClient.deleteFormContainer(createdInvoice);

        //5. Delete...
        formDefinitionClient.deleteFormDefinition(formDefinitionInvoice);
        formFieldClient.deleteField(tempTextPlainInvoiceNumber);
        formFieldClient.deleteField(createdFieldTableLineItems);

        //Delete the other temp field...
        formDefinitionClient.deleteFormDefinition(formDefinitionLineItem);
        formFieldClient.deleteField(tempTextPlainCode);
        formFieldClient.deleteField(tempTextPlainDescription);
        formFieldClient.deleteField(tempMultiPlainTitle);
    }
}
