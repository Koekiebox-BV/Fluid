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

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.google.common.io.BaseEncoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 *
 */
public class TestASNFluidItemMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Form form = new Form();
        form.setId(453L);
        form.setFormType("form type");
        form.setFormTypeId(88L);
        form.setFormDescription("form desc");
        form.setTitle("form title");
        form.setFlowState(FluidItem.FlowState.UserSend.name());
        form.setState("form state");
        form.setCurrentUser(new User(44L, "pete-the-user"));
        form.setDateCreated(new Date(1767906456000L));
        form.setDateLastUpdated(new Date(1767906456000L));

        form.setFormFields(new ArrayList<>());
        form.getFormFields().add(new Field(111L, "nickname"));
        form.getFormFields().add(new Field(222L, "surname"));

        List<Field> fieldsUser = new ArrayList<>();
        fieldsUser.add(new Field("user field 1", "the val", Field.Type.Text));
        List<Field> fieldsRoute = new ArrayList<>();
        fieldsRoute.add(new Field("route field 1", "the val route", Field.Type.Text));
        List<Field> fieldsGlobal = new ArrayList<>();
        fieldsGlobal.add(new Field("global field 1", "the val global", Field.Type.Text));

        FluidItem item = new FluidItem(form);
        item.setStepEnteredTime(new Date(1767906456000L));
        item.setFlow("flow");
        item.setStep("step");
        item.setUserFields(fieldsUser);
        item.setRouteFields(fieldsRoute);
        item.setGlobalFields(fieldsGlobal);

        List<Attachment> attachments = new ArrayList<>();
        Attachment att1 = new Attachment();
        att1.setId(22542L);
        att1.setName("att name.png");
        att1.setContentType("image/png");
        att1.setDateCreated(new Date(1767906456000L));
        att1.setDateLastUpdated(new Date(1767904356000L));
        att1.setFormId(4442L);
        att1.setVersion("v1");
        att1.setAttachmentDataBase64(BaseEncoding.base64().encode("some data".getBytes()));
        //make use of the attachement mapper for the above

        attachments.add(att1);
        item.setAttachments(attachments);

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", Long.valueOf(-1L), decoded.getId());
    }
}
