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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperCustomWebAction to encode
 * and decode CustomWebAction objects.
 */
public class TestASNMapperCustomWebAction extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Form form = new Form();
        form.setId(453L);
        form.setFormType("form type");
        form.setTitle("form title");
        form.setCurrentUser(new User(44L, "pete-the-user"));
        form.setDateCreated(new Date(1767906456000L));
        form.setDateLastUpdated(new Date(1767906456000L));

        form.setFormFields(new ArrayList<>());
        form.getFormFields().add(new Field(111L, "nickname"));
        form.getFormFields().add(new Field(222L, "surname"));

        CustomWebAction action = new CustomWebAction(form, "task-id");
        action.setId(999L);
        action.setIsTableRecord(true);
        action.setFormTableRecordBelongsTo(55L);
        action.setExecutionTimeMillis(12345L);

        ASNMapperForm mapForm = new ASNMapperForm(new PayloadPopulate());
        ASNMapperCustomWebAction mapper = new ASNMapperCustomWebAction(mapForm);

        byte[] raw = seqBytes(mapper.encode(action));
        CustomWebAction decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", action.getId(), decoded.getId());
        Assert.assertEquals("Decoded task identifier is not as expected.",
                action.getTaskIdentifier(), decoded.getTaskIdentifier());
        Assert.assertEquals("Decoded isTableRecord is not as expected.",
                action.getIsTableRecord(), decoded.getIsTableRecord());
        Assert.assertEquals("Decoded record belongs-to is not as expected.",
                action.getFormTableRecordBelongsTo(), decoded.getFormTableRecordBelongsTo());
        Assert.assertEquals("Decoded execution time is not as expected.",
                action.getExecutionTimeMillis(), decoded.getExecutionTimeMillis());
        Assert.assertNotNull("Decoded form should not be null.", decoded.getForm());
        Assert.assertEquals("Decoded form id is not as expected.",
                action.getForm().getId(), decoded.getForm().getId());
    }
}
