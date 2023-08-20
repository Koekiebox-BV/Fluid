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

package com.fluidbpm.ws.client.v1.util;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestUtilGlobal extends ABaseTestCase {
	public static final String SIGNATURE = "{\"lines\":[[[145,38.81],[144,38.81],[137,42.81],[117,46.81],[89,51.81],[71,56.81],[57,64.81],[43,73.81],[38,79.81],[37,82.81],[39,84.81],[44,86.81],[61,88.81],[77,85.81],[103,71.81],[132,47.81],[154,28.81],[166,15.81],[167,10.81],[164,8.81],[160,7.81],[149,10.81],[116,22.81],[79,44.81],[55,66.81],[51,76.81],[53,78.81],[55,78.81],[59,79.81],[60,79.81],[60,81.81],[57,83.81],[56,84.81],[56,85.81],[57,86.81],[69,91.81],[99,100.81],[127,109.81],[149,118.81],[161,123.81],[172,126.81],[179,126.81],[179,122.81],[175,113.81],[174,100.81],[177,78.81],[191,53.81],[200,38.81],[200,36.81],[199,36.81],[199,40.81],[203,56.81],[205,86.81],[190,115.81],[162,140.81],[136,148.81],[123,149.81],[107,143.81],[90,131.81],[76,116.81],[64,93.81],[61,72.81],[68,62.81],[80,60.81],[93,60.81],[108,63.81],[120,67.81],[122,69.81],[125,73.81],[146,80.81],[201,84.81],[257,84.81],[267,81.81],[266,81.81],[260,81.81],[233,85.81],[197,90.81],[151,94.81],[112,92.81],[106,90.81],[112,85.81],[135,77.81],[176,71.81],[224,61.81],[280,53.81],[302,49.81],[300,49.81],[259,59.81],[183,73.81],[127,87.81],[117,91.81],[118,91.81],[118,91.81],[119,91.81],[121,91.81],[124,90.81],[131,89.81],[140,89.81],[155,88.81]]]}";
	
	@Test
	public void testSVGFromSignature() {
		String svg = UtilGlobal.toSvg(SIGNATURE, 300, 200);
		System.out.println(svg);
	}

	@Test
	public void testExtractFieldName() {
		String val = UtilGlobal.extractFieldNameFromText("This is a [[Field Name]].");
		Assert.assertEquals("Field Name", val);

		String val2 = UtilGlobal.extractFieldNameFromText("This is a [[Field Name].");
		Assert.assertNull(val2);

		String val3 = UtilGlobal.removeFieldNameFromText("This is a [[Field Name]].");
		Assert.assertEquals("This is a .", val3);
	}
}
