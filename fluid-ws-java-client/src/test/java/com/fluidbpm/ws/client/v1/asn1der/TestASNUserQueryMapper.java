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
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperUserQuery to encode and decode UserQuery objects.
 */
public class TestASNUserQueryMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        UserQuery item = new UserQuery(123L);
        item.setName("Active Users Query");
        item.setDescription("Query to fetch all active users in the system");
        item.setRules(Arrays.asList("rule1", "rule2", "rule3"));

        Field input1 = new Field("Status", "Active");
        Field input2 = new Field("Department", "Engineering");
        item.setInputs(Arrays.asList(input1, input2));

        item.setDateCreated(new Date(1767906456000L));
        item.setDateLastUpdated(new Date(1767904356000L));

        ASNMapperField fldMapper = new ASNMapperField(new PayloadPopulate());
        ASNMapperUserQuery mapper = new ASNMapperUserQuery(fldMapper);

        byte[] raw = seqBytes(mapper.encode(item));
        UserQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded name is not as expected.", item.getName(), decoded.getName());
        Assert.assertEquals("Decoded description is not as expected.", item.getDescription(), decoded.getDescription());
        Assert.assertEquals("Decoded rules size is not as expected.", item.getRules().size(), decoded.getRules().size());
        Assert.assertEquals("Decoded first rule is not as expected.", item.getRules().get(0), decoded.getRules().get(0));
        Assert.assertEquals("Decoded inputs size is not as expected.", item.getInputs().size(), decoded.getInputs().size());
        Assert.assertEquals("Decoded first input name is not as expected.", item.getInputs().get(0).getFieldName(), decoded.getInputs().get(0).getFieldName());
        Assert.assertEquals("Decoded date created is not as expected.", item.getDateCreated().getTime(), decoded.getDateCreated().getTime());
        Assert.assertEquals("Decoded date last updated is not as expected.", item.getDateLastUpdated().getTime(), decoded.getDateLastUpdated().getTime());
    }
}
