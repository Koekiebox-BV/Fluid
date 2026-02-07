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
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperFormListing to encode and decode FormListing objects.
 *
 * The test ensures that the encoded byte array produced by {@code ASNMapperFormListing.encode(FormListing)}
 * can be properly decoded back into a {@code FormListing} object using {@code ASNMapperFormListing.decode(byte[])}.
 * All relevant fields of the FormListing object, including nested Form entries, are verified for consistency
 * between the original and decoded objects.
 */
public class TestASNFormListingMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        FormListing listing = new FormListing();
        listing.setId(789L);
        listing.setServiceTicket("svc-ticket-123");
        listing.setRequestUuid("req-uuid-456");
        listing.setEcho("echo-789");
        listing.setLoggedInUserFromTicket(new User(88L, "ticket-user"));
        listing.setListingCount(2);
        listing.setListingIndex(0);
        listing.setListingPage(1);

        listing.setListing(new ArrayList<>());

        Form form1 = new Form();
        form1.setId(1001L);
        form1.setFormType("form type");
        form1.setFormTypeId(88L);
        form1.setFormDescription("form desc");
        form1.setTitle("form title 1");
        form1.setFlowState("flow state 1");
        form1.setState("state 1");
        form1.setCurrentUser(new User(44L, "pete-the-user"));
        form1.setDateCreated(new Date(1767906456000L));
        form1.setDateLastUpdated(new Date(1767906456000L));
        form1.setFormFields(new ArrayList<>());
        form1.getFormFields().add(new Field(111L, "nickname"));
        form1.getFormFields().add(new Field(222L, "surname"));

        Form form2 = new Form();
        form2.setId(1002L);
        form2.setFormType("form type 2");
        form2.setTitle("form title 2");
        form2.setFlowState("flow state 2");
        form2.setState("state 2");
        form2.setCurrentUser(new User(55L, "jane-doe"));
        form2.setDateCreated(new Date(1767906456000L));
        form2.setDateLastUpdated(new Date(1767906456000L));
        form2.setFormFields(new ArrayList<>());
        form2.getFormFields().add(new Field(333L, "email"));

        listing.getListing().add(form1);
        listing.getListing().add(form2);

        PayloadPopulate payPop = new PayloadPopulate();
        ASNMapperFormListing mapper = new ASNMapperFormListing(new ASNMapperForm(payPop));

        byte[] raw = seqBytes(mapper.encode(listing));
        FormListing decoded = mapper.decode(raw);

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

        Form decodedForm1 = decoded.getListing().get(0);
        Assert.assertEquals("Decoded form1 id is not as expected.", form1.getId(), decodedForm1.getId());
        Assert.assertEquals("Decoded form1 form type is not as expected.", form1.getFormType(), decodedForm1.getFormType());
        Assert.assertEquals("Decoded form1 form type id is not as expected.", form1.getFormTypeId(), decodedForm1.getFormTypeId());
        Assert.assertNull("Decoded form1 form desc is not expected.", decodedForm1.getFormDescription());
        Assert.assertEquals("Decoded form1 title is not as expected.", form1.getTitle(), decodedForm1.getTitle());
        Assert.assertEquals("Decoded form1 flow state is not as expected.", form1.getFlowState(), decodedForm1.getFlowState());
        Assert.assertEquals("Decoded form1 state is not as expected.", form1.getState(), decodedForm1.getState());
        Assert.assertEquals("Decoded form1 user id is not as expected.", form1.getCurrentUser().getId(), decodedForm1.getCurrentUser().getId());
        Assert.assertEquals("Decoded form1 user username is not as expected.",
                form1.getCurrentUser().getUsername(), decodedForm1.getCurrentUser().getUsername());
        Assert.assertEquals("Decoded form1 date created is not as expected.",
                form1.getDateCreated().getTime(), decodedForm1.getDateCreated().getTime());
        Assert.assertEquals("Decoded form1 date last updated is not as expected.",
                form1.getDateLastUpdated().getTime(), decodedForm1.getDateLastUpdated().getTime());
        Assert.assertEquals("Decoded form1 fields count is not as expected.",
                form1.getFormFields().size(), decodedForm1.getFormFields().size());
        Assert.assertEquals("Decoded form1 fields [0] id is not as expected.",
                form1.getFormFields().get(0).getId(), decodedForm1.getFormFields().get(0).getId());
        Assert.assertEquals("Decoded form1 fields [0] name is not as expected.",
                form1.getFormFields().get(0).getFieldName(), decodedForm1.getFormFields().get(0).getFieldName());
        Assert.assertEquals("Decoded form1 fields [1] id is not as expected.",
                form1.getFormFields().get(1).getId(), decodedForm1.getFormFields().get(1).getId());
        Assert.assertEquals("Decoded form1 fields [1] name is not as expected.",
                form1.getFormFields().get(1).getFieldName(), decodedForm1.getFormFields().get(1).getFieldName());

        Form decodedForm2 = decoded.getListing().get(1);
        Assert.assertEquals("Decoded form2 id is not as expected.", form2.getId(), decodedForm2.getId());
        Assert.assertEquals("Decoded form2 form type is not as expected.", form2.getFormType(), decodedForm2.getFormType());
        Assert.assertEquals("Decoded form2 title is not as expected.", form2.getTitle(), decodedForm2.getTitle());
        Assert.assertEquals("Decoded form2 flow state is not as expected.", form2.getFlowState(), decodedForm2.getFlowState());
        Assert.assertEquals("Decoded form2 state is not as expected.", form2.getState(), decodedForm2.getState());
        Assert.assertEquals("Decoded form2 user id is not as expected.", form2.getCurrentUser().getId(), decodedForm2.getCurrentUser().getId());
        Assert.assertEquals("Decoded form2 user username is not as expected.",
                form2.getCurrentUser().getUsername(), decodedForm2.getCurrentUser().getUsername());
        Assert.assertEquals("Decoded form2 date created is not as expected.",
                form2.getDateCreated().getTime(), decodedForm2.getDateCreated().getTime());
        Assert.assertEquals("Decoded form2 date last updated is not as expected.",
                form2.getDateLastUpdated().getTime(), decodedForm2.getDateLastUpdated().getTime());
        Assert.assertEquals("Decoded form2 fields count is not as expected.",
                form2.getFormFields().size(), decodedForm2.getFormFields().size());
        Assert.assertEquals("Decoded form2 fields [0] id is not as expected.",
                form2.getFormFields().get(0).getId(), decodedForm2.getFormFields().get(0).getId());
        Assert.assertEquals("Decoded form2 fields [0] name is not as expected.",
                form2.getFormFields().get(0).getFieldName(), decodedForm2.getFormFields().get(0).getFieldName());
    }
}
