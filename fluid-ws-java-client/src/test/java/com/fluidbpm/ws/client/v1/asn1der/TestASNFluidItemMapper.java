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
 * Test class for the {@code ASNMapperFluidItem} which verifies the encoding
 * and decoding of {@code FluidItem} objects using ASN (Abstract Syntax Notation).
 * The test suite includes scenarios for various configurations and edge cases.
 *
 * The primary objective of this test class is to ensure that serialized data generated
 * by the {@code ASNMapperFluidItem} can be correctly deserialized back into Java objects
 * with expected properties.
 *
 * Test cases include:
 * - Encoding and decoding of a {@code FluidItem} with basic properties.
 * - Validation of custom properties applied to {@code FluidItem}.
 * - Support for all flow states available in {@code FluidItem.FlowState}.
 * - Handling and validation of parent-child relationships using table field parents.
 * - Behavior with empty lists for fields, attachments, and other collections.
 * - Handling of {@code FluidItem} objects with a {@code null} form.
 * - Proper behavior when the "createLinkToParent" flag is explicitly set or unset.
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

    @Test
    public void testEncodeDecodeWithCustomProperties() {
        Form form = new Form();
        form.setId(100L);
        form.setFormType("test form");

        FluidItem item = new FluidItem(form);

        List<FluidItem.FluidItemProperty> customProps = new ArrayList<>();
        customProps.add(new FluidItem.FluidItemProperty("prop1", "value1"));
        customProps.add(new FluidItem.FluidItemProperty("prop2", "value2"));
        item.setCustomProperties(customProps);

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);

        Assert.assertNotNull("Custom properties should not be null", decoded.getCustomProperties());
        Assert.assertEquals("Custom properties count", 2, decoded.getCustomProperties().size());
        Assert.assertEquals("First property name", "prop1", decoded.getCustomProperties().get(0).getName());
        Assert.assertEquals("First property value", "value1", decoded.getCustomProperties().get(0).getValue());
    }

    @Test
    public void testEncodeDecodeWithAllFlowStates() {
        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        for (FluidItem.FlowState flowState : FluidItem.FlowState.values()) {
            Form form = new Form();
            form.setId(100L);

            FluidItem item = new FluidItem(form);
            item.setFlowState(flowState);

            byte[] raw = seqBytes(mapper.encode(item));
            FluidItem decoded = mapper.decode(raw);

            Assert.assertEquals("Flow state should match for " + flowState, flowState, decoded.getFlowState());
        }
    }

    @Test
    public void testEncodeDecodeWithTableFieldParent() {
        Form parentForm = new Form();
        parentForm.setId(999L);
        parentForm.setFormType("parent form");
        parentForm.setTitle("Parent Form Title");

        Form childForm = new Form();
        childForm.setId(100L);
        childForm.setFormType("child form");

        FluidItem item = new FluidItem(childForm);
        item.setTableFieldParentForm(parentForm);
        item.setTableFieldNameOnParentForm("child_table_field");
        item.setInCaseOfCreateLinkToParent(true);

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);

        Assert.assertNotNull("Table field parent form should not be null", decoded.getTableFieldParentForm());
        Assert.assertEquals("Parent form ID", Long.valueOf(999L), decoded.getTableFieldParentForm().getId());
        Assert.assertEquals("Table field name", "child_table_field", decoded.getTableFieldNameOnParentForm());
        Assert.assertTrue("Link to parent flag", decoded.getInCaseOfCreateLinkToParent());
    }

    @Test
    public void testEncodeDecodeWithEmptyLists() {
        Form form = new Form();
        form.setId(100L);

        FluidItem item = new FluidItem(form);
        item.setUserFields(new ArrayList<>());
        item.setRouteFields(new ArrayList<>());
        item.setGlobalFields(new ArrayList<>());
        item.setAttachments(new ArrayList<>());
        item.setCustomProperties(new ArrayList<>());

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);

        Assert.assertNotNull("Decoded item should not be null", decoded);
        Assert.assertNotNull("Form should not be null", decoded.getForm());
    }

    @Test
    public void testEncodeDecodeWithNullForm() {
        FluidItem item = new FluidItem();
        item.setFlow("test-flow");
        item.setStep("test-step");

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);

        Assert.assertEquals("Flow should match", "test-flow", decoded.getFlow());
        Assert.assertEquals("Step should match", "test-step", decoded.getStep());
    }

    @Test
    public void testEncodeDecodeInCaseOfCreateLinkToParentFalse() {
        Form form = new Form();
        form.setId(100L);

        FluidItem item = new FluidItem(form);
        item.setInCaseOfCreateLinkToParent(false);

        PayloadPopulate pp = new PayloadPopulate();
        ASNMapperForm mapForm = new ASNMapperForm(pp);
        ASNMapperField mapField = new ASNMapperField(pp);
        ASNMapperFluidItem mapper = new ASNMapperFluidItem(mapForm, mapField);

        byte[] raw = seqBytes(mapper.encode(item));
        FluidItem decoded = mapper.decode(raw);

        Assert.assertFalse("Link to parent should be false", decoded.getInCaseOfCreateLinkToParent());
    }
}
