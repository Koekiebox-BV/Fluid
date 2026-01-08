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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 *
 */
public class TestASNFormMapper extends ABaseTestCase {

	@Test
	public void testEncodeDecode() {
		Form item = new Form();
		item.setId(453L);
		item.setFormType("form type");
		item.setTitle("form title");
		item.setFormTypeId(88L);
		item.setDateCreated(new Date(1767906456000L));

		ASNMapperForm mapper = new ASNMapperForm();

		byte[] raw = seqBytes(mapper.encode(item));
		Form decoded = mapper.decode(raw);
		Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
		Assert.assertEquals("Decoded form type is not as expected.", item.getFormType(), decoded.getFormType());
		Assert.assertEquals("Decoded form type id is not as expected.", item.getFormTypeId(), decoded.getFormTypeId());
		Assert.assertEquals("Decoded form title is not as expected.", item.getTitle(), decoded.getTitle());
		Assert.assertEquals("Decoded form date created is not as expected.", item.getDateCreated().getTime(), decoded.getDateCreated().getTime());

		//System.out.println(BaseEncoding.base16().encode(raw));
	}
}
