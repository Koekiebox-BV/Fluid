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

import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperRole to encode and decode Role objects.
 */
public class TestASNMapperRole extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Role item = new Role();
        item.setId(321L);

        ASNMapperRole mapper = new ASNMapperRole();

        byte[] raw = seqBytes(mapper.encode(item));
        Role decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertNull("Decoded name should be null.", decoded.getName());
        Assert.assertNull("Decoded description should be null.", decoded.getDescription());
        Assert.assertNull("Decoded admin permissions should be null.", decoded.getAdminPermissions());
        Assert.assertNull("Decoded custom permissions should be null.", decoded.getCustomPermissions());

        item.setName("Operators");
        item.setDescription("Ops role");
        item.setAdminPermissions(Arrays.asList("admin-1", "admin-2"));
        item.setCustomPermissions(Arrays.asList("custom-a", "custom-b"));

        raw = seqBytes(mapper.encode(item));
        decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded name is not as expected.", item.getName(), decoded.getName());
        Assert.assertEquals("Decoded description is not as expected.", item.getDescription(), decoded.getDescription());
        Assert.assertEquals("Decoded admin permissions size is not as expected.",
                item.getAdminPermissions().size(), decoded.getAdminPermissions().size());
        Assert.assertEquals("Decoded admin permissions [0] is not as expected.",
                item.getAdminPermissions().get(0), decoded.getAdminPermissions().get(0));
        Assert.assertEquals("Decoded admin permissions [1] is not as expected.",
                item.getAdminPermissions().get(1), decoded.getAdminPermissions().get(1));
        Assert.assertEquals("Decoded custom permissions size is not as expected.",
                item.getCustomPermissions().size(), decoded.getCustomPermissions().size());
        Assert.assertEquals("Decoded custom permissions [0] is not as expected.",
                item.getCustomPermissions().get(0), decoded.getCustomPermissions().get(0));
        Assert.assertEquals("Decoded custom permissions [1] is not as expected.",
                item.getCustomPermissions().get(1), decoded.getCustomPermissions().get(1));
    }
}
