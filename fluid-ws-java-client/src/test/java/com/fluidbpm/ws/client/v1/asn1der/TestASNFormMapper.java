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
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.google.common.io.BaseEncoding;
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
		item.setFormTypeId(88L);
		item.setFormDescription("form desc");
		item.setTitle("form title");
		item.setFlowState("form flow state");
		item.setState("form state");
		item.setCurrentUser(new User(44L, "pete-the-user"));
		item.setDateCreated(new Date(1767906456000L));
		item.setDateLastUpdated(new Date(1767906456000L));

		ASNMapperForm mapper = new ASNMapperForm();

		byte[] raw = seqBytes(mapper.encode(item));
		Form decoded = mapper.decode(raw);
		Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
		Assert.assertEquals("Decoded form type is not as expected.", item.getFormType(), decoded.getFormType());
		Assert.assertEquals("Decoded form type id is not as expected.", item.getFormTypeId(), decoded.getFormTypeId());
		Assert.assertNull("Decoded form desc is not expected.", decoded.getFormDescription());
		Assert.assertEquals("Decoded form title is not as expected.", item.getTitle(), decoded.getTitle());
		Assert.assertEquals("Decoded form flow state is not as expected.", item.getFlowState(), decoded.getFlowState());
		Assert.assertEquals("Decoded form state is not as expected.", item.getState(), decoded.getState());
		Assert.assertEquals("Decoded form user id is not as expected.", item.getCurrentUser().getId(), decoded.getCurrentUser().getId());
		Assert.assertEquals("Decoded form user username is not as expected.", item.getCurrentUser().getUsername(), decoded.getCurrentUser().getUsername());
		Assert.assertEquals("Decoded form date created is not as expected.", item.getDateCreated().getTime(), decoded.getDateCreated().getTime());
		Assert.assertEquals("Decoded form date last updated is not as expected.", item.getDateLastUpdated().getTime(), decoded.getDateLastUpdated().getTime());


		System.out.println(BaseEncoding.base16().encode(raw));
	}
}
