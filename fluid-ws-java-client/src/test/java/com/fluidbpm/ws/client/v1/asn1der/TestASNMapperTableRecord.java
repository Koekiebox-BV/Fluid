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
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperTableRecord to encode
 * and decode TableRecord objects.
 */
public class TestASNMapperTableRecord extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Form formContainer = new Form();
        formContainer.setId(453L);
        formContainer.setFormType("form type");
        formContainer.setTitle("form title");
        formContainer.setCurrentUser(new User(44L, "pete-the-user"));
        formContainer.setDateCreated(new Date(1767906456000L));
        formContainer.setDateLastUpdated(new Date(1767906456000L));

        formContainer.setFormFields(new ArrayList<>());
        formContainer.getFormFields().add(new Field(111L, "nickname"));
        formContainer.getFormFields().add(new Field(222L, "surname"));

        Form parentFormContainer = new Form();
        parentFormContainer.setId(999L);
        parentFormContainer.setFormType("parent type");
        parentFormContainer.setTitle("parent title");

        Field parentFormField = new Field(777L, "parentField");
        parentFormField.setTypeAsEnum(Field.Type.Text);
        parentFormField.setFieldValue("value");

        TableRecord record = new TableRecord(formContainer, parentFormContainer, parentFormField);
        record.setId(123L);

        PayloadPopulate payloadPopulate = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(payloadPopulate);
        ASNMapperField mapField = new ASNMapperField(payloadPopulate);
        ASNMapperTableRecord mapper = new ASNMapperTableRecord(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(record));
        TableRecord decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", record.getId(), decoded.getId());
        Assert.assertNotNull("Decoded form container should not be null.", decoded.getFormContainer());
        Assert.assertEquals("Decoded form container id is not as expected.",
                record.getFormContainer().getId(), decoded.getFormContainer().getId());
        Assert.assertNotNull("Decoded parent form container should not be null.", decoded.getParentFormContainer());
        Assert.assertEquals("Decoded parent form container id is not as expected.",
                record.getParentFormContainer().getId(), decoded.getParentFormContainer().getId());
        Assert.assertNotNull("Decoded parent form field should not be null.", decoded.getParentFormField());
        Assert.assertEquals("Decoded parent form field id is not as expected.",
                record.getParentFormField().getId(), decoded.getParentFormField().getId());
        Assert.assertEquals("Decoded parent form field name is not as expected.",
                record.getParentFormField().getFieldName(), decoded.getParentFormField().getFieldName());
    }
}
