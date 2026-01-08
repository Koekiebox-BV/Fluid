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

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * A test class for verifying the encoding and decoding functionality of the {@code ASNMapperUser}.
 * This class extends {@code ABaseTestCase}.
 *
 * The primary purpose of this class is to ensure that instances of the {@code User} class
 * are correctly serialized and deserialized using the {@code ASNMapperUser} implementation.
 *
 * Test Details:
 * - Validates that the {@code id} field of a {@code User} object is correctly encoded and decoded.
 * - Ensures that the {@code username} field is properly handled during encoding and decoding.
 * - Uses assertions to compare expected and actual outcomes of the encoding and decoding processes.
 */
public class TestASNUserMapper extends ABaseTestCase {

	@Test
	public void testEncodeDecode() {
		User item = new User();
		item.setId(453L);
		ASNMapperUser mapper = new ASNMapperUser();

		byte[] raw = seqBytes(mapper.encode(item));
		User decoded = mapper.decode(raw);
		Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
		Assert.assertNull(decoded.getUsername());

		item.setUsername("bb");

		raw = seqBytes(mapper.encode(item));
		decoded = mapper.decode(raw);
		Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
		Assert.assertEquals("Decoded username is not as expected.", item.getUsername(), decoded.getUsername());
	}
}
