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

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.flow.JobViewListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestParameter;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.*;
import org.bouncycastle.asn1.DERSequence;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Unit test class for validating the functionality of ASNMapperBaseTransmission class.
 * This test class extends the base test case functionality from {@code ABaseTestCase}.
 * It provides test cases to verify the encoding and decoding of {@code BaseTransmission} objects
 * using the {@code ASNMapperBaseTransmission} class.
 *
 * The tests ensure the correctness of encoding into ASN.1 format and decoding back to the original object,
 * while also evaluating performance for repeated encode-decode cycles.
 */
public class TestASNMapperBaseTransmission extends ABaseTestCase {
    @Test
    public void testEncodeDecode() {
        BaseTransmission item = testBaseTransmission();
        PayloadPopulate pop = item.getPayloadPopulate();

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.FIELD);

        DERSequence seqEnc = mapper.encode(item);
        byte[] raw = seqBytes(seqEnc);
        Assert.assertEquals("Encoded size expected to be different!", 359, raw.length);
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());

        // Payload Populate:
        Assert.assertEquals("Decoded payload populate is not as expected.",
                pop.getMcFormField().get(0).getId(), decoded.getPayloadPopulate().getMcFormField().get(0).getId());
    }

    @Test
    public void testEncodeDecodeLightPerformance() {
        BaseTransmission item = testBaseTransmission();
        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.FIELD);
        long start = System.currentTimeMillis();
        int count = 1000;
        for (int i = 0;i < count;i++) {
            DERSequence seqEnc = mapper.encode(item);
            byte[] raw = seqBytes(seqEnc);
            BaseTransmission decoded = mapper.decode(raw);
            Assert.assertNotNull(decoded);
        }
        long took = System.currentTimeMillis() - start;

        System.out.println("Took '"+took+"' millis for encoding and decoding '"+count+"' items.");
        Assert.assertTrue("Took too long! Took "+took, took < 500);
    }

    private BaseTransmission testBaseTransmission(ABaseFluidVO to) {
        return this.testBaseTransmission(ASNGlobal.Type.FIELD, to);
    }

    private BaseTransmission testBaseTransmission(int type, ABaseFluidVO to) {
        BaseTransmission item = new BaseTransmission(type);

        List<ASNMultiChoice> mcList = new ArrayList<>();
        mcList.add(new ASNMultiChoice(123L, "mc-opt-1"));
        mcList.add(new ASNMultiChoice(456L, "mc-opt-2"));

        List<ASNMultiChoiceField> mcForm = new ArrayList<>();
        mcForm.add(new ASNMultiChoiceField("ff-mc", mcList));
        List<ASNMultiChoiceField> mcUser = new ArrayList<>();
        mcUser.add(new ASNMultiChoiceField("uf-mc", mcList));
        List<ASNMultiChoiceField> mcRoute = new ArrayList<>();
        mcRoute.add(new ASNMultiChoiceField("rf-mc", mcList));
        List<ASNMultiChoiceField> mcGlobal = new ArrayList<>();
        mcGlobal.add(new ASNMultiChoiceField("gf-mc", mcList));
        List<FormFieldMetaData> ffmd = new ArrayList<>();
        ffmd.add(new FormFieldMetaData("cool-field", "Meta-Deee[:]"));
        PayloadPopulate pop = new PayloadPopulate(mcForm,mcUser, mcRoute, mcGlobal, ffmd);

        List<RequestParameter> reqParams = new ArrayList<>();
        reqParams.add(new RequestParameter("param-name", "param-value"));
        reqParams.add(new RequestParameter("param-surname", "param-petec"));
        RequestObject ro = new RequestObject("me-path/is-this!", reqParams);

        item.setPayloadPopulate(pop);
        item.setRequestObject(ro);
        item.setTransmissionObject(to);

        return item;
    }

    private BaseTransmission testBaseTransmission() {
        Field to = new Field(765L);
        to.setFieldName("field name testing as transmission obj!");
        return this.testBaseTransmission(to);
    }

    @Test
    public void testEncodeDecodeFormHistoricDataListing() {
        FormHistoricDataListing to = new FormHistoricDataListing();
        to.setId(890L);
        to.setListingCount(10);
        to.setListingIndex(1);
        to.setListingPage(1);

        BaseTransmission item = testBaseTransmission(to);
        PayloadPopulate pop = item.getPayloadPopulate();

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.FORM_HISTORIC_DATA_LISTING);

        DERSequence seqEnc = mapper.encode(item);
        byte[] raw = seqBytes(seqEnc);
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());

        // Payload Populate:
        Assert.assertEquals("Decoded payload populate is not as expected.",
                pop.getMcFormField().get(0).getId(), decoded.getPayloadPopulate().getMcFormField().get(0).getId());

        // Transmission Object:
        FormHistoricDataListing decodedTo = (FormHistoricDataListing) decoded.getTransmissionObject();
        Assert.assertEquals("Decoded transmission object id is not as expected.", to.getId(), decodedTo.getId());
        Assert.assertEquals("Decoded transmission object listing count is not as expected.", to.getListingCount(), decodedTo.getListingCount());
    }

    @Test
    public void testEncodeDecodeJobView() {
        JobView to = new JobView(456L);
        to.setRule("rule-value");
        to.setViewName("view-name");
        to.setViewGroupName("view-group");
        to.setViewStepName("view-step");
        to.setViewFlowName("view-flow");
        to.setViewPriority(2);
        to.setViewOrder(11L);
        to.setViewType("STANDARD");

        BaseTransmission item = testBaseTransmission(ASNGlobal.Type.JOB_VIEW, to);
        PayloadPopulate pop = item.getPayloadPopulate();

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.JOB_VIEW);

        DERSequence seqEnc = mapper.encode(item);
        byte[] raw = seqBytes(seqEnc);
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());

        Assert.assertEquals("Decoded payload populate is not as expected.",
                pop.getMcFormField().get(0).getId(), decoded.getPayloadPopulate().getMcFormField().get(0).getId());

        JobView decodedTo = (JobView) decoded.getTransmissionObject();
        Assert.assertEquals("Decoded job view id is not as expected.", to.getId(), decodedTo.getId());
        Assert.assertEquals("Decoded rule is not as expected.", to.getRule(), decodedTo.getRule());
        Assert.assertEquals("Decoded view name is not as expected.", to.getViewName(), decodedTo.getViewName());
        Assert.assertEquals("Decoded view group name is not as expected.", to.getViewGroupName(), decodedTo.getViewGroupName());
        Assert.assertEquals("Decoded view step name is not as expected.", to.getViewStepName(), decodedTo.getViewStepName());
        Assert.assertEquals("Decoded view flow name is not as expected.", to.getViewFlowName(), decodedTo.getViewFlowName());
        Assert.assertEquals("Decoded view priority is not as expected.", to.getViewPriority(), decodedTo.getViewPriority());
        Assert.assertEquals("Decoded view order is not as expected.", to.getViewOrder(), decodedTo.getViewOrder());
        Assert.assertEquals("Decoded view type is not as expected.", to.getViewType(), decodedTo.getViewType());
    }

    @Test
    public void testEncodeDecodeJobViewListing() {
        JobViewListing listing = new JobViewListing();
        listing.setId(901L);
        listing.setServiceTicket("svc-ticket-1");
        listing.setRequestUuid("req-uuid-1");
        listing.setEcho("echo-1");
        listing.setLoggedInUserFromTicket(new com.fluidbpm.program.api.vo.user.User(77L, "ticket-user"));
        listing.setListingCount(2);
        listing.setListingIndex(0);
        listing.setListingPage(1);
        listing.setListing(new ArrayList<>());

        JobView view1 = new JobView(1001L);
        view1.setViewName("view-1");
        view1.setViewGroupName("group-1");
        view1.setViewStepName("step-1");
        view1.setViewFlowName("flow-1");
        view1.setViewPriority(1);
        view1.setViewOrder(3L);
        view1.setViewType("STANDARD");

        JobView view2 = new JobView(1002L);
        view2.setViewName("view-2");
        view2.setViewGroupName("group-2");
        view2.setViewStepName("step-2");
        view2.setViewFlowName("flow-2");
        view2.setViewPriority(2);
        view2.setViewOrder(4L);
        view2.setViewType("READ_ONLY");

        listing.getListing().add(view1);
        listing.getListing().add(view2);

        BaseTransmission item = testBaseTransmission(ASNGlobal.Type.JOB_VIEW_LISTING, listing);
        PayloadPopulate pop = item.getPayloadPopulate();

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.JOB_VIEW_LISTING);

        DERSequence seqEnc = mapper.encode(item);
        byte[] raw = seqBytes(seqEnc);
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());

        Assert.assertEquals("Decoded payload populate is not as expected.",
                pop.getMcFormField().get(0).getId(), decoded.getPayloadPopulate().getMcFormField().get(0).getId());

        JobViewListing decodedTo = (JobViewListing) decoded.getTransmissionObject();
        Assert.assertEquals("Decoded listing id is not as expected.", listing.getId(), decodedTo.getId());
        Assert.assertEquals("Decoded listing count is not as expected.", listing.getListingCount(), decodedTo.getListingCount());
        Assert.assertEquals("Decoded listing index is not as expected.", listing.getListingIndex(), decodedTo.getListingIndex());
        Assert.assertEquals("Decoded listing page is not as expected.", listing.getListingPage(), decodedTo.getListingPage());
        Assert.assertEquals("Decoded service ticket is not as expected.", listing.getServiceTicket(), decodedTo.getServiceTicket());
        Assert.assertEquals("Decoded request uuid is not as expected.", listing.getRequestUuid(), decodedTo.getRequestUuid());
        Assert.assertEquals("Decoded echo is not as expected.", listing.getEcho(), decodedTo.getEcho());
        Assert.assertEquals("Decoded logged in user id is not as expected.",
                listing.getLoggedInUserFromTicket().getId(), decodedTo.getLoggedInUserFromTicket().getId());
        Assert.assertEquals("Decoded logged in user username is not as expected.",
                listing.getLoggedInUserFromTicket().getUsername(), decodedTo.getLoggedInUserFromTicket().getUsername());

        Assert.assertNotNull("Decoded listing should not be null.", decodedTo.getListing());
        Assert.assertEquals("Decoded listing size is not as expected.", 2, decodedTo.getListing().size());

        JobView decodedView1 = decodedTo.getListing().get(0);
        Assert.assertEquals("Decoded view1 id is not as expected.", view1.getId(), decodedView1.getId());
        Assert.assertEquals("Decoded view1 name is not as expected.", view1.getViewName(), decodedView1.getViewName());
        Assert.assertEquals("Decoded view1 group is not as expected.", view1.getViewGroupName(), decodedView1.getViewGroupName());
        Assert.assertEquals("Decoded view1 step is not as expected.", view1.getViewStepName(), decodedView1.getViewStepName());
        Assert.assertEquals("Decoded view1 flow is not as expected.", view1.getViewFlowName(), decodedView1.getViewFlowName());
        Assert.assertEquals("Decoded view1 priority is not as expected.", view1.getViewPriority(), decodedView1.getViewPriority());
        Assert.assertEquals("Decoded view1 order is not as expected.", view1.getViewOrder(), decodedView1.getViewOrder());
        Assert.assertEquals("Decoded view1 type is not as expected.", view1.getViewType(), decodedView1.getViewType());

        JobView decodedView2 = decodedTo.getListing().get(1);
        Assert.assertEquals("Decoded view2 id is not as expected.", view2.getId(), decodedView2.getId());
        Assert.assertEquals("Decoded view2 name is not as expected.", view2.getViewName(), decodedView2.getViewName());
        Assert.assertEquals("Decoded view2 group is not as expected.", view2.getViewGroupName(), decodedView2.getViewGroupName());
        Assert.assertEquals("Decoded view2 step is not as expected.", view2.getViewStepName(), decodedView2.getViewStepName());
        Assert.assertEquals("Decoded view2 flow is not as expected.", view2.getViewFlowName(), decodedView2.getViewFlowName());
        Assert.assertEquals("Decoded view2 priority is not as expected.", view2.getViewPriority(), decodedView2.getViewPriority());
        Assert.assertEquals("Decoded view2 order is not as expected.", view2.getViewOrder(), decodedView2.getViewOrder());
        Assert.assertEquals("Decoded view2 type is not as expected.", view2.getViewType(), decodedView2.getViewType());
    }
}
