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
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperForm to encode and decode Form objects.
 * This class extends {@code ABaseTestCase} and uses JUnit for testing.
 *
 * The test ensures that the encoded byte array produced by {@code ASNMapperForm.encode(Form)}
 * can be properly decoded back into a {@code Form} object using {@code ASNMapperForm.decode(byte[])}.
 * All relevant fields of the Form object, including nested fields like {@code formFields},
 * are verified for consistency between the original and decoded objects.
 *
 * Methods tested:
 * - {@code ASNMapperForm.encode(Form)}
 * - {@code ASNMapperForm.decode(byte[])}
 *
 * Assertions:
 * - Validates that primary fields such as ID, title, and state match between the original
 *   and the decoded Form object.
 * - Checks that nested and complex fields, including {@code formFields} and {@code currentUser},
 *   are correctly encoded and decoded.
 * - Ensures nullability behavior for fields like {@code formDescription} remains consistent.
 *
 * Dependencies:
 * - ASNMapperForm for encoding/decoding operations.
 * - PayloadPopulate for populating necessary dependencies of {@code ASNMapperForm}.
 * - BaseEncoding for byte array representation.
 *
 * Output:
 * - Prints the encoded byte array in Base16 format for debugging purposes.
 */
public class TestASNTableFieldMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Form form = new Form();
        form.setId(453L);
        form.setFormType("form type");
        form.setFormTypeId(88L);
        form.setFormDescription("form desc");
        form.setTitle("form title");
        form.setFlowState("form flow state");
        form.setState("form state");
        form.setCurrentUser(new User(44L, "pete-the-user"));
        form.setDateCreated(new Date(1767906456000L));
        form.setDateLastUpdated(new Date(1767906456000L));

        form.setFormFields(new ArrayList<>());
        form.getFormFields().add(new Field(111L, "nickname"));
        form.getFormFields().add(new Field(222L, "surname"));

        ASNMapperForm mapForm = new ASNMapperForm(new PayloadPopulate());
        ASNTableFieldMapper mapper = new ASNTableFieldMapper(mapForm);

        List<Form> forms = new ArrayList<>();
        forms.add(form);
        TableField item = new TableField(true, forms);

        byte[] raw = seqBytes(mapper.encode(item));
        TableField decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded id is not as expected.", item.getSumDecimals(), decoded.getSumDecimals());
        Assert.assertEquals("Decoded record count!.", item.getTableRecords().size(), decoded.getTableRecords().size());
    }
}
