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
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the encoding and decoding functionality of {@code ASNMapperJobView}
 * with {@code JobView} objects.
 */
public class TestASNJobViewMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        JobView item = new JobView(123L);
        item.setRule("rule-eval");
        item.setViewName("view-name");
        item.setViewGroupName("view-group");
        item.setViewStepName("view-step");
        item.setViewFlowName("view-flow");
        item.setViewPriority(4);
        item.setViewOrder(12L);
        item.setViewType("STANDARD");

        ASNMapperJobView mapper = new ASNMapperJobView();

        byte[] raw = seqBytes(mapper.encode(item));
        JobView decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded rule is not as expected.", item.getRule(), decoded.getRule());
        Assert.assertEquals("Decoded view name is not as expected.", item.getViewName(), decoded.getViewName());
        Assert.assertEquals("Decoded view group name is not as expected.", item.getViewGroupName(), decoded.getViewGroupName());
        Assert.assertEquals("Decoded view step name is not as expected.", item.getViewStepName(), decoded.getViewStepName());
        Assert.assertEquals("Decoded view flow name is not as expected.", item.getViewFlowName(), decoded.getViewFlowName());
        Assert.assertEquals("Decoded view priority is not as expected.", item.getViewPriority(), decoded.getViewPriority());
        Assert.assertEquals("Decoded view order is not as expected.", item.getViewOrder(), decoded.getViewOrder());
        Assert.assertEquals("Decoded view type is not as expected.", item.getViewType(), decoded.getViewType());
    }
}
