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
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.flow.RouteFieldClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base test case for scenarios where workflow steps are being tested.
 */
@Log
public abstract class ABaseTestFlowStep extends ABaseLoggedInTestCase {
    private String prevConfMemory;
    private static String CONFIG_KEY_MEM = Configuration.Key.MemoryCacheType.name();

    /**
     * Initialize.
     */
    @Before
    public void init() {
        if (this.isConnectionInValid) return;

        super.init();

        try (ConfigurationClient confClient = new ConfigurationClient(BASE_URL, ADMIN_SERVICE_TICKET)) {
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
        if (this.isConnectionInValid) return;

        try (ConfigurationClient confClient = new ConfigurationClient(BASE_URL, ADMIN_SERVICE_TICKET)) {
            confClient.upsertConfiguration(CONFIG_KEY_MEM, this.prevConfMemory);
        }

        super.destroy();
    }

    protected List<FluidItem> executeUntilOrTO(
            FlowItemClient fiClient,
            JobView view,
            int attemptCount,
            int maxWaitSeconds
    ) {
        for (int iter = 0; iter < maxWaitSeconds; iter++) {
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

    protected static Field createRouteField(RouteFieldClient rfClient, String name, Field.Type type) {
        try {
            Field toCreate = new Field(name);
            toCreate.setFieldDescription("Route field by unit test.");

            switch (type) {
                case Text: return rfClient.createFieldTextPlain(toCreate);
                case TrueFalse: return rfClient.createFieldTrueFalse(toCreate);
                default: return null;
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            return rfClient.getFieldByName(name);
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

    protected static Field[] interviewFields() {
        Field[] returnVal = new Field[]{
                new Field("JUnit Candidate Name", null, Field.Type.Text),
                new Field("JUnit Candidate Surname", null, Field.Type.Text),
                new Field("JUnit Candidate Birth Date", null, Field.Type.DateTime),
        };
        returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE);

        for (Field fld : returnVal) fld.setFieldDescription(DESC);

        return returnVal;
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

        JsonObject jsonObject = new JsonObject();
        try {
            JsonObject jsonMemberObject = new JsonObject();
            jsonMemberObject.addProperty("firstname","Jason"+identifier);
            jsonMemberObject.addProperty("lastname", "Bruwer"+identifier);
            jsonMemberObject.addProperty("id_number","81212211122");
            jsonMemberObject.addProperty("cellphone","1111");
            jsonMemberObject.addProperty("member_number","ZOOOOL");

            jsonObject.add("member",jsonMemberObject);
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
}
