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

package com.fluidbpm.ws.client.v1.flow.step;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Base test case for scenarios where workflow steps are being tested.
 */
@Log
public abstract class ABaseTestFlowStep extends ABaseTestCase {
    private LoginClient loginClient;
    protected String serviceTicket;
    private String prevConfMemory;

    private static String CONFIG_KEY_MEM = "MemoryCacheType";
    private static String DESC = "Created by step Unit tests.";

    protected int ITEM_COUNT_PER_SUB = 15;

    /**
     * Initialize.
     */
    @Before
    public void init() {
        if (!this.isConnectionValid()) return;

        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
        this.loginClient = new LoginClient(BASE_URL);

        this.serviceTicket = this.loginClient.login(USERNAME, PASSWORD).getServiceTicket();

        try (ConfigurationClient confClient = new ConfigurationClient(BASE_URL, this.serviceTicket)) {
            Configuration conf = confClient.getConfigurationByKey(CONFIG_KEY_MEM);
            if (conf == null) this.prevConfMemory = "None";
            else this.prevConfMemory = conf.getValue();

            if ("None".equalsIgnoreCase(this.prevConfMemory)) {
                confClient.upsertConfiguration(CONFIG_KEY_MEM, "Internal");
            }
        }
    }

    /**
     * Teardown.
     */
    @After
    public void destroy() {
        if (!this.isConnectionValid()) return;

        try (ConfigurationClient confClient = new ConfigurationClient(BASE_URL, this.serviceTicket)) {
            confClient.upsertConfiguration(CONFIG_KEY_MEM, this.prevConfMemory);
        }

        this.loginClient.closeAndClean();
    }

    protected List<FluidItem> executeUntilOrTO(FlowItemClient fiClient, JobView view, int attemptCount) {
        // wait for up to 80 seconds:
        for (int iter = 0; iter < 80; iter++) {
            sleepForSeconds(1);
            try {
                List<FluidItem> attempt = fiClient.getFluidItemsForView(view, attemptCount, 0).getListing();
                if (attempt != null && attempt.size() == attemptCount) return attempt;
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                if (attemptCount == 0) return null;
            }
        }
        return null;
    }

    protected boolean executeUntilInState(
        FlowItemClient fiClient,
        List<FluidItem> list,
        FluidItem.FlowState state,
        int timeoutInSeconds
    ) {
        // wait for up to 60 seconds:
        AtomicInteger isNotDone = new AtomicInteger(0);
        for (int iter = 0; iter < timeoutInSeconds; iter++) {
            sleepForSeconds(1);
            isNotDone.set(0);
            list.forEach(toLookup -> {
                try {
                    FluidItem attempt = fiClient.getFluidItemByFormId(
                            toLookup.getForm().getId(), true, false);
                    if (attempt.getFlowState() != state) isNotDone.incrementAndGet();
                } catch (FluidClientException fce) {
                    if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                }
            });
            if (isNotDone.get() == 0) return true;
        }
        log.warning("After "+timeoutInSeconds+" seconds we still have "+isNotDone.get()+" that is not "+state+"!!!");
        return (isNotDone.get() == 0);
    }

    protected static Form createFormDef(
            FormDefinitionClient fdClient,
            FormFieldClient ffClient,
            String formDef,
            List<Flow> associatedFlows,
            Field ... fields
    ) {
        if (fields == null || fields.length < 1) return null;

        try {
            Form formToCreate = new Form(formDef);
            formToCreate.setAssociatedFlows(associatedFlows);
            formToCreate.setFormDescription(DESC);

            List<Field> fieldsCreated = new ArrayList<>();
            Stream.of(fields).forEach(itm -> {
                try {
                    switch (itm.getTypeAsEnum()) {
                        case MultipleChoice:
                            fieldsCreated.add(ffClient.createField(itm,
                                    itm.getFieldValueAsMultiChoice().getAvailableMultiChoices()));
                        break;
                        default:
                            fieldsCreated.add(ffClient.createField(itm));
                    }
                } catch (FluidClientException fce) {
                    if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

                    fieldsCreated.add(itm);
                }
            });
            formToCreate.setFormFields(fieldsCreated);

            return fdClient.createFormDefinition(formToCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            return fdClient.getFormDefinitionByName(formDef);
        }
    }

    protected static Field[] employeeFields() {
        List<String> options = new ArrayList<>();
        options.add("Male");
        options.add("Female");

        Field[] returnVal = new Field[]{
                new Field("JUnit Employee Name", null, Field.Type.Text),
                new Field("JUnit Employee Surname", null, Field.Type.Text),
                new Field("JUnit Employee Bio", null, Field.Type.ParagraphText),
                new Field("JUnit Employee Secret", null, Field.Type.TextEncrypted),
                new Field("JUnit Employee Gender", new MultiChoice(options, options), Field.Type.MultipleChoice),
                new Field("JUnit Employee Age", null, Field.Type.Decimal),
                new Field("JUnit Employee Birth Date", null, Field.Type.DateTime),
                new Field("JUnit Employee Is Director", null, Field.Type.TrueFalse),
        };
        returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.ParagraphText.PLAIN);
        returnVal[3].setTypeMetaData(ABaseFieldClient.FieldMetaData.EncryptedText.PLAIN);
        returnVal[4].setTypeMetaData(ABaseFieldClient.FieldMetaData.MultiChoice.PLAIN);
        returnVal[5].setTypeMetaData(ABaseFieldClient.FieldMetaData.Decimal.PLAIN);
        returnVal[6].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE_AND_TIME);
        returnVal[7].setTypeMetaData(ABaseFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE);

        for (Field fld : returnVal) fld.setFieldDescription(DESC);

        return returnVal;
    }

    protected static FluidItem item(
        String identifier,
        String formType,
        boolean includeAttachment,
        Field ... fields
    ) {
        //Fluid Item...
        Form frm = new Form(formType, new Date().toString()+ " "+identifier);
        Stream.of(fields).forEach(itm -> {
            if (itm.getTypeAsEnum() == null) {
                log.warning("Field ["+itm+"] does not have a type set!");
                return;
            }

            switch (itm.getTypeAsEnum()) {
                case Text:
                case TextEncrypted:
                case ParagraphText:
                    frm.setFieldValue(itm.getFieldName(), UUID.randomUUID().toString(), itm.getTypeAsEnum());
                break;
                case TrueFalse:
                    frm.setFieldValue(itm.getFieldName(), new Boolean(true), itm.getTypeAsEnum());
                break;
                case MultipleChoice:
                    MultiChoice multiChoice = itm.getFieldValueAsMultiChoice();
                    if (multiChoice != null &&
                            multiChoice.getAvailableMultiChoices() != null && !multiChoice.getAvailableMultiChoices().isEmpty()) {
                        multiChoice.setSelectedMultiChoice(
                                multiChoice.getAvailableMultiChoices().get(0)
                        );
                    }
                    frm.setFieldValue(itm.getFieldName(), multiChoice, itm.getTypeAsEnum());
                break;
                case DateTime:
                    frm.setFieldValue(itm.getFieldName(), new Date(), itm.getTypeAsEnum());
                break;
                case Decimal:
                    frm.setFieldValue(itm.getFieldName(), Math.random() * 90, itm.getTypeAsEnum());
                break;
            }
        });

        FluidItem toCreate = new FluidItem(frm);
        if (!includeAttachment) return toCreate;

        //2. Attachments...
        List<Attachment> attachments = new ArrayList();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonMemberObject = new JSONObject();

            jsonMemberObject.put("firstname","Piet"+identifier);
            jsonMemberObject.put("lastname", "Brarer"+identifier);
            jsonMemberObject.put("id_number",identifier);
            jsonMemberObject.put("cellphone","1111");
            jsonMemberObject.put("member_number","ZOOOOL");

            jsonObject.put("member",jsonMemberObject);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
            return toCreate;
        }

        //First Attachment...
        Attachment attachmentToAdd = new Attachment();
        attachmentToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64(jsonObject.toString().getBytes()));

        attachmentToAdd.setName("Test assessment JSON.json");
        attachmentToAdd.setContentType("application/json");

        //Second Attachment...
        Attachment secondToAdd = new Attachment();
        secondToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64("De Beers".toString().getBytes()));

        secondToAdd.setName("Test Text Plain.txt");
        secondToAdd.setContentType("text/plain");

        attachments.add(attachmentToAdd);
        attachments.add(secondToAdd);

        toCreate.setAttachments(attachments);

        return toCreate;
    }

    public static final class TestStatics {
        public static final String FORM_DEFINITION = "Email";
    }

    protected static FluidItem emailItem(String identifier) {
        //Fluid Item...
        Form frm = new Form(TestIntroductionStep.TestStatics.FORM_DEFINITION, new Date().toString()+ " "+identifier);
        frm.setFieldValue("Email From Address", String.format("jack@frost@%s.com", identifier), Field.Type.Text);
        frm.setFieldValue("Email Received Date", new Date(), Field.Type.DateTime);
        frm.setFieldValue("Email Sent Date", new Date(), Field.Type.DateTime);
        frm.setFieldValue("Email Subject", String.format("I am a subject [%s]", identifier), Field.Type.Text);
        frm.setFieldValue("Email To Address", String.format("peter@zool@%s.com", identifier), Field.Type.Text);
        frm.setFieldValue("Email Unique Identifier", Math.random(), Field.Type.Decimal);
        FluidItem toCreate = new FluidItem(frm);

        //2. Attachments...
        List<Attachment> attachments = new ArrayList<Attachment>();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonMemberObject = new JSONObject();

            jsonMemberObject.put("firstname","Jason"+identifier);
            jsonMemberObject.put("lastname", "Bruwer"+identifier);
            jsonMemberObject.put("id_number","81212211122");
            jsonMemberObject.put("cellphone","1111");
            jsonMemberObject.put("member_number","ZOOOOL");

            jsonObject.put("member",jsonMemberObject);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
            return toCreate;
        }

        //First Attachment...
        Attachment attachmentToAdd = new Attachment();
        attachmentToAdd.setAttachmentDataBase64(
                UtilGlobal.encodeBase64(jsonObject.toString().getBytes()));

        attachmentToAdd.setName("Test assessment JSON.json");
        attachmentToAdd.setContentType("application/json");

        //Second Attachment...
        Attachment secondToAdd = new Attachment();
        secondToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64("De Beers".toString().getBytes()));

        secondToAdd.setName("Test Text Plain.txt");
        secondToAdd.setContentType("text/plain");

        attachments.add(attachmentToAdd);
        attachments.add(secondToAdd);

        toCreate.setAttachments(attachments);

        return toCreate;
    }

    protected static UserQuery userQueryForFormType(
        UserQueryClient uqClient,
        String formDef,
        String ... formFields
    ) {
        List<Field> inputs = new ArrayList<>();
        if (formFields != null && formFields.length > 0) {
            for (String field : formFields) inputs.add(new Field(field));
        }

        List<String> ruleByFormType = new ArrayList<>();
        ruleByFormType.add(String.format("[Form Type] = '%s'", formDef));
        String uqName = String.format("JUnit By Form Type %s", formDef);
        UserQuery toCreate = new UserQuery(uqName, inputs);
        toCreate.setRules(ruleByFormType);
        toCreate.setInputs(inputs);
        toCreate.setDescription(DESC);

        try {
            return uqClient.createUserQuery(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            return uqClient.getUserQueryByName(uqName);
        }
    }

    protected static void deleteFormContainersAndUserQuery(
            UserQueryClient uqClient,
            FormContainerClient fcClient,
            UserQuery queryToExecAndDel
    ) {
        List<FluidItem> toDelete = uqClient.executeUserQuery(
                queryToExecAndDel,
                false,
                10000,
                0
        ).getListing();
        if (toDelete != null) {
            toDelete.stream()
                    .map(itm -> itm.getForm())
                    .forEach(itm -> fcClient.deleteFormContainer(itm));
        } else log.warning("No items to delete/cleanup!");

        if (uqClient != null) uqClient.deleteUserQuery(queryToExecAndDel, true);
    }
}
