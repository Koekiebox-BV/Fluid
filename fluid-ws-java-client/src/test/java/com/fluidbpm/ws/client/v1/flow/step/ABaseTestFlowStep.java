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
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base test case for scenarios where workflow steps are being tested.
 */
public abstract class ABaseTestFlowStep extends ABaseTestCase {
    private LoginClient loginClient;
    protected String serviceTicket;
    private String prevConfMemory;

    private static String CONFIG_KEY_MEM = "MemoryCacheType";

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
        // wait for up to 60 seconds:
        for (int iter = 0; iter < 60; iter++) {
            sleepForSeconds(1);
            try {
                List<FluidItem> attempt = fiClient.getFluidItemsForView(view, attemptCount, 0).getListing();
                if (attempt != null && attempt.size() == attemptCount) {
                    return attempt;
                }
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                if (attemptCount == 0) return null;
            }
        }
        return null;
    }

    protected boolean executeUntilInState(
        FlowItemClient fiClient, List<FluidItem> list, FluidItem.FlowState state) {
        // wait for up to 60 seconds:
        AtomicBoolean isAllDone = new AtomicBoolean(true);
        for (int iter = 0; iter < 60; iter++) {
            sleepForSeconds(1);
            isAllDone.set(true);
            list.forEach(toLookup -> {
                try {
                    if (!isAllDone.get()) return;

                    FluidItem attempt = fiClient.getFluidItemByFormId(
                            toLookup.getForm().getId(), true, false);
                    if (attempt.getFlowState() != state) {
                        isAllDone.set(false);
                    }
                } catch (FluidClientException fce) {
                    if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                }
            });
            if (isAllDone.get()) return true;
        }
        return isAllDone.get();
    }

    protected static FluidItem item(String identifier, String formType) {
        //Fluid Item...
        Form frm = new Form(formType, new Date().toString()+ " "+identifier);
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
}
