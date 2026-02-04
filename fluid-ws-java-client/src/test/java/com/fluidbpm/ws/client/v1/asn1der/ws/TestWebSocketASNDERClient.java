/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2027] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.asn1der.ws;

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.TestFormContainerClient;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TestWebSocketASNDERClient extends ABaseLoggedInTestCase {

    @Test
    public void testAttachment() {
        if (this.isConnectionInValid) return;

        try (WebSocketASNDERClient wClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            Form toCreate = new Form(TestFormContainerClient.TestStatics.FORM_DEFINITION);
            toCreate.setTitle(TestFormContainerClient.TestStatics.FORM_TITLE_PREFIX+new Date().toString());

            List<Field> fields = new ArrayList<>();
            fields.add(new Field(TestFormContainerClient.TestStatics.FieldName.EMAIL_FROM_ADDRESS, "zd2@zool.com"));
            fields.add(new Field(TestFormContainerClient.TestStatics.FieldName.EMAIL_SUBJECT, "This subj..."));
            toCreate.setFormFields(fields);

            //Create...
            Form createdForm = formContainerClient.createFormContainer(toCreate);

            Attachment attCreate = new Attachment();
            attCreate.setName("Test Attachment.json");
            attCreate.setContentType("application/json");
            attCreate.setFormId(createdForm.getId());
            attCreate.setAttachmentData("{'name':'cool'}".getBytes());

            BaseTransmission bt = new BaseTransmission(ASNGlobal.Type.ATTACHMENT);
            bt.setRequestObject(new RequestObject(ASNGlobal.Path.Attachment.ATTACHMENT_CREATE));
            bt.setTransmissionObject(attCreate);

            BaseTransmission btAttCreated = wClient.request(bt);

            TestCase.assertNotNull(btAttCreated);
        }
    }
}
