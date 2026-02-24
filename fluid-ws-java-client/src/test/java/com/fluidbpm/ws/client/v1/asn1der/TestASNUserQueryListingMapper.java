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

import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.userquery.UserQueryListing;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperUserQueryListing to encode and decode UserQueryListing objects.
 */
public class TestASNUserQueryListingMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        UserQueryListing item = new UserQueryListing();
        item.setId(999L);

        UserQuery query1 = new UserQuery(123L);
        query1.setName("Active Users Query");
        query1.setDescription("Query to fetch all active users");
        query1.setDateCreated(new Date(1767906456000L));

        UserQuery query2 = new UserQuery(456L);
        query2.setName("Inactive Users Query");
        query2.setDescription("Query to fetch all inactive users");
        query2.setDateCreated(new Date(1767904356000L));

        item.setListing(Arrays.asList(query1, query2));

        ASNMapperField fldMapper = new ASNMapperField(new PayloadPopulate());
        ASNMapperUserQuery mapperUQ = new ASNMapperUserQuery(fldMapper);
        ASNMapperUserQueryListing mapper = new ASNMapperUserQueryListing(mapperUQ);

        byte[] raw = seqBytes(mapper.encode(item));
        UserQueryListing decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded listing size is not as expected.", item.getListing().size(), decoded.getListing().size());
        Assert.assertEquals("Decoded first query id is not as expected.", item.getListing().get(0).getId(), decoded.getListing().get(0).getId());
        Assert.assertEquals("Decoded first query name is not as expected.", item.getListing().get(0).getName(), decoded.getListing().get(0).getName());
        Assert.assertEquals("Decoded second query id is not as expected.", item.getListing().get(1).getId(), decoded.getListing().get(1).getId());
        Assert.assertEquals("Decoded second query name is not as expected.", item.getListing().get(1).getName(), decoded.getListing().get(1).getName());
    }
}
