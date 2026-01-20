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
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * 
 */
public class TestASNAttachmentMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Attachment item = new Attachment(546L);

        ASNMapperAttachment mapper = new ASNMapperAttachment();

        byte[] raw = seqBytes(mapper.encode(item));
        Attachment decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded name in attachment is not as expected.", item.getName(), decoded.getName());
    }
}
