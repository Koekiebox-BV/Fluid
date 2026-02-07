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

import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.flow.JobViewListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperJobViewListing to encode and decode
 * JobViewListing objects.
 */
public class TestASNJobViewListingMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        JobViewListing listing = new JobViewListing();
        listing.setId(789L);
        listing.setServiceTicket("svc-ticket-123");
        listing.setRequestUuid("req-uuid-456");
        listing.setEcho("echo-789");
        listing.setLoggedInUserFromTicket(new User(88L, "ticket-user"));
        listing.setListingCount(2);
        listing.setListingIndex(0);
        listing.setListingPage(1);

        listing.setListing(new ArrayList<>());

        JobView view1 = new JobView(1001L);
        view1.setRule("rule-1");
        view1.setViewName("view-name-1");
        view1.setViewGroupName("view-group-1");
        view1.setViewStepName("view-step-1");
        view1.setViewFlowName("view-flow-1");
        view1.setViewPriority(3);
        view1.setViewOrder(10L);
        view1.setViewType("STANDARD");

        JobView view2 = new JobView(1002L);
        view2.setRule("rule-2");
        view2.setViewName("view-name-2");
        view2.setViewGroupName("view-group-2");
        view2.setViewStepName("view-step-2");
        view2.setViewFlowName("view-flow-2");
        view2.setViewPriority(5);
        view2.setViewOrder(20L);
        view2.setViewType("READ_ONLY");

        listing.getListing().add(view1);
        listing.getListing().add(view2);

        ASNMapperJobViewListing mapper = new ASNMapperJobViewListing(new ASNMapperJobView());

        byte[] raw = seqBytes(mapper.encode(listing));
        JobViewListing decoded = mapper.decode(raw);

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

        JobView decodedView1 = decoded.getListing().get(0);
        Assert.assertEquals("Decoded view1 id is not as expected.", view1.getId(), decodedView1.getId());
        Assert.assertEquals("Decoded view1 rule is not as expected.", view1.getRule(), decodedView1.getRule());
        Assert.assertEquals("Decoded view1 name is not as expected.", view1.getViewName(), decodedView1.getViewName());
        Assert.assertEquals("Decoded view1 group is not as expected.", view1.getViewGroupName(), decodedView1.getViewGroupName());
        Assert.assertEquals("Decoded view1 step is not as expected.", view1.getViewStepName(), decodedView1.getViewStepName());
        Assert.assertEquals("Decoded view1 flow is not as expected.", view1.getViewFlowName(), decodedView1.getViewFlowName());
        Assert.assertEquals("Decoded view1 priority is not as expected.", view1.getViewPriority(), decodedView1.getViewPriority());
        Assert.assertEquals("Decoded view1 order is not as expected.", view1.getViewOrder(), decodedView1.getViewOrder());
        Assert.assertEquals("Decoded view1 type is not as expected.", view1.getViewType(), decodedView1.getViewType());

        JobView decodedView2 = decoded.getListing().get(1);
        Assert.assertEquals("Decoded view2 id is not as expected.", view2.getId(), decodedView2.getId());
        Assert.assertEquals("Decoded view2 rule is not as expected.", view2.getRule(), decodedView2.getRule());
        Assert.assertEquals("Decoded view2 name is not as expected.", view2.getViewName(), decodedView2.getViewName());
        Assert.assertEquals("Decoded view2 group is not as expected.", view2.getViewGroupName(), decodedView2.getViewGroupName());
        Assert.assertEquals("Decoded view2 step is not as expected.", view2.getViewStepName(), decodedView2.getViewStepName());
        Assert.assertEquals("Decoded view2 flow is not as expected.", view2.getViewFlowName(), decodedView2.getViewFlowName());
        Assert.assertEquals("Decoded view2 priority is not as expected.", view2.getViewPriority(), decodedView2.getViewPriority());
        Assert.assertEquals("Decoded view2 order is not as expected.", view2.getViewOrder(), decodedView2.getViewOrder());
        Assert.assertEquals("Decoded view2 type is not as expected.", view2.getViewType(), decodedView2.getViewType());
    }
}
