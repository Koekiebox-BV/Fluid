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

package com.fluidbpm.program.api.vo.field;

import com.fluidbpm.ws.client.v1.ABaseTestCase;
import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigDecimal;

/**
 *
 */
public class TestDecimalMetaFormat extends ABaseTestCase {
	private double[] TO_TEST_SCALE_NOT_ZERO = {
			0.1212d,
			5.01d,
			5.1d,
			5.12d,
			1212.1212d,
			1.2d,
			1.05d,
			1.0000001d,
			0.1221d,
			0.0001d,
			0.1d
	};

	private double[] TO_TEST_SCALE_ZERO = {
			12121212233.0d,
			122121212.0,
			1.0000d,
			123.0d,
			123d,
			206d,
			1010122211d,
			0d,
			1d,
			3d,
			-12012d
	};

	@Test
	public void testZeroAndNonZeroValues() {
		for (double notZero: TO_TEST_SCALE_NOT_ZERO) {
			TestCase.assertFalse(
					String.format("Expected '%s' to be [false]. Scale at '%d'", notZero,
							BigDecimal.valueOf(notZero).scale()),
					new DecimalMetaFormat(null, "", 1, 1, notZero, null).isScaleNaturalNumberForStepFactor()
			);
		}

		for (double zero: TO_TEST_SCALE_ZERO) {
			TestCase.assertTrue(
					String.format("Expected '%s' to be [true]. Scale at '%d'", zero ,
							BigDecimal.valueOf(zero).scale()),
					new DecimalMetaFormat(null, "", 1, 1, zero, null).isScaleNaturalNumberForStepFactor()
			);
		}
	}

	@Test
	public void testSpecificNumber() {
		new DecimalMetaFormat(null, "", 1, 1, 5.01, null).isScaleNaturalNumberForStepFactor();
	}
}
