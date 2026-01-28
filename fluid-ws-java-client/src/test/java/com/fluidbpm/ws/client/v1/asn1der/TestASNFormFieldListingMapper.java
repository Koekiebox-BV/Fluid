/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.google.common.io.BaseEncoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperFormFieldListing to encode and decode FormFieldListing objects.
 *
 * The test ensures that the encoded byte array produced by {@code ASNMapperFormFieldListing.encode(FormFieldListing)}
 * can be properly decoded back into a {@code FormFieldListing} object using {@code ASNMapperFormFieldListing.decode(byte[])}.
 * All relevant fields of the FormFieldListing object, including nested Field entries, are verified for consistency
 * between the original and decoded objects.
 */
public class TestASNFormFieldListingMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        FormFieldListing listing = new FormFieldListing();
        listing.setId(789L);
        listing.setServiceTicket("svc-ticket-123");
        listing.setRequestUuid("req-uuid-456");
        listing.setEcho("echo-789");
        listing.setLoggedInUserFromTicket(new User(88L, "ticket-user"));
        listing.setListingCount(2);
        listing.setListingIndex(0);
        listing.setListingPage(1);

        listing.setListing(new ArrayList<>());

        Field field1 = new Field(1001L);
        field1.setFieldName("first-name");
        field1.setTypeAsEnum(Field.Type.Text);
        field1.setFieldValue("Pete");

        Field field2 = new Field(1002L);
        field2.setFieldName("is-active");
        field2.setTypeAsEnum(Field.Type.TrueFalse);
        field2.setFieldValue(Boolean.TRUE);

        listing.getListing().add(field1);
        listing.getListing().add(field2);

        PayloadPopulate payPop = new PayloadPopulate();
        ASNMapperFormFieldListing mapper = new ASNMapperFormFieldListing(new ASNMapperField(payPop));

        byte[] raw = seqBytes(mapper.encode(listing));
        FormFieldListing decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", listing.getId(), decoded.getId());
        Assert.assertEquals("Decoded listing count is not as expected.", listing.getListingCount(), decoded.getListingCount());
        Assert.assertEquals("Decoded listing index is not as expected.", listing.getListingIndex(), decoded.getListingIndex());
        Assert.assertEquals("Decoded listing page is not as expected.", listing.getListingPage(), decoded.getListingPage());
        Assert.assertEquals("Decoded service ticket is not as expected.", listing.getServiceTicket(), decoded.getServiceTicket());
        Assert.assertEquals("Decoded request uuid is not as expected.", listing.getRequestUuid(), decoded.getRequestUuid());
        Assert.assertEquals("Decoded echo is not as expected.", listing.getEcho(), decoded.getEcho());
        Assert.assertEquals("Decoded logged in user id is not as expected.",
                listing.getLoggedInUserFromTicket().getId(), decoded.getLoggedInUserFromTicket().getId());
        Assert.assertEquals("Decoded logged in user username is not as expected.",
                listing.getLoggedInUserFromTicket().getUsername(), decoded.getLoggedInUserFromTicket().getUsername());

        Assert.assertNotNull("Decoded listing should not be null.", decoded.getListing());
        Assert.assertEquals("Decoded listing size is not as expected.", 2, decoded.getListing().size());

        Field decodedField1 = decoded.getListing().get(0);
        Assert.assertEquals("Decoded field1 id is not as expected.", field1.getId(), decodedField1.getId());
        Assert.assertEquals("Decoded field1 name is not as expected.", field1.getFieldName(), decodedField1.getFieldName());
        Assert.assertEquals("Decoded field1 type is not as expected.", field1.getTypeAsEnum(), decodedField1.getTypeAsEnum());
        Assert.assertEquals("Decoded field1 value is not as expected.", field1.getFieldValueAsString(), decodedField1.getFieldValueAsString());

        Field decodedField2 = decoded.getListing().get(1);
        Assert.assertEquals("Decoded field2 id is not as expected.", field2.getId(), decodedField2.getId());
        Assert.assertEquals("Decoded field2 name is not as expected.", field2.getFieldName(), decodedField2.getFieldName());
        Assert.assertEquals("Decoded field2 type is not as expected.", field2.getTypeAsEnum(), decodedField2.getTypeAsEnum());
        Assert.assertEquals("Decoded field2 value is not as expected.", field2.getFieldValueAsBoolean(), decodedField2.getFieldValueAsBoolean());

        System.out.println(BaseEncoding.base16().encode(raw));
    }
}
