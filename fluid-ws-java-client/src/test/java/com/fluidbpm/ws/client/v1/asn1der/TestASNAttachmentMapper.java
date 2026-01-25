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
import com.google.common.io.BaseEncoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperAttachment to encode and decode Attachment objects.
 */
public class TestASNAttachmentMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Attachment item = new Attachment(546L);
        item.setName("test-document.pdf");
        item.setVersion("v2.1");
        item.setPath("/uploads/documents/test-document.pdf");
        item.setContentType("application/pdf");
        item.setDateCreated(new Date(1767906456000L));
        item.setDateLastUpdated(new Date(1767904356000L));
        item.setAttachmentData("sample binary data content".getBytes());
        item.setFormId(9876L);

        ASNMapperAttachment mapper = new ASNMapperAttachment();

        byte[] raw = seqBytes(mapper.encode(item));
        Attachment decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded name is not as expected.", item.getName(), decoded.getName());
        Assert.assertEquals("Decoded version is not as expected.", item.getVersion(), decoded.getVersion());
        Assert.assertEquals("Decoded path is not as expected.", item.getPath(), decoded.getPath());
        Assert.assertEquals("Decoded content type is not as expected.", item.getContentType(), decoded.getContentType());
        Assert.assertEquals("Decoded date created is not as expected.", item.getDateCreated().getTime(), decoded.getDateCreated().getTime());
        Assert.assertEquals("Decoded date last updated is not as expected.", item.getDateLastUpdated().getTime(), decoded.getDateLastUpdated().getTime());
        Assert.assertArrayEquals("Decoded attachment data is not as expected.", item.getAttachmentData(), decoded.getAttachmentData());
        Assert.assertEquals("Decoded form id is not as expected.", item.getFormId(), decoded.getFormId());

        System.out.println(BaseEncoding.base16().encode(raw));
    }
}
