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

import com.fluidbpm.program.api.util.UtilGlobal;
import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * <p>
 *     Represents a {@code DecimalMetaFormat} value for a Form field.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.3
 * @version v1.3
 *
 * @see Field
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DecimalMetaFormat {
	private String type;
	private String prefix;
	private Number min;
	private Number max;
	private Number stepFactor;

	@Setter
	private Currency amountCurrency;

	private static final String MIN = "Min";
	private static final String MAX = "Max";
	private static final String STEP_FACTOR = "StepFactor";
	private static final String PREFIX = "Prefix";
	public static final String AMOUNT_MINOR_PREFIX = "AmountMinorCurrency";

	/**
	 * Parse a Fluid meta-data value to create {@code DecimalMetaFormat}.
	 * @param theStringToProcessParam To parse.
	 * @return Created
	 */
	public static final DecimalMetaFormat parse(String theStringToProcessParam) {
		if (theStringToProcessParam == null || theStringToProcessParam.isEmpty()) return new DecimalMetaFormat();

		UtilGlobal ug = new UtilGlobal();

		String[] initialSplit = theStringToProcessParam.split("\\_");
		if (initialSplit == null || initialSplit.length == 0) return new DecimalMetaFormat();

		if (initialSplit.length != 5) return new DecimalMetaFormat();

		// Type...
		String type = initialSplit[0];

		// Min...
		String minString = getValueFrom(MIN, initialSplit[1]);
		Number min = ug.toDoubleSafe(minString);
		if (isPrecisionZero(min)) min = Long.valueOf(min.longValue());

		// Max...
		String maxString = getValueFrom(MAX, initialSplit[2]);
		Number max = ug.toDoubleSafe(maxString);
		if (isPrecisionZero(max)) max = Long.valueOf(max.longValue());

		// Step Factor...
		String stepFactorString = getValueFrom(STEP_FACTOR,initialSplit[3]);
		Number stepFactor = ug.toDoubleSafe(stepFactorString);
		if (isPrecisionZero(stepFactor)) stepFactor = Long.valueOf(stepFactor.longValue());

		// Prefix...
		String prefix = getValueFrom(PREFIX, initialSplit[4]);
		Currency currency = null;
		if (UtilGlobal.isNotBlank(prefix) && prefix.startsWith(AMOUNT_MINOR_PREFIX)) {
			String currencyTxt = prefix.substring(AMOUNT_MINOR_PREFIX.length());
			currency = Currency.getAvailableCurrencies().stream()
					.filter(itm -> currencyTxt.equalsIgnoreCase(itm.getCurrencyCode()))
					.findFirst()
					.orElse(null);
		}
		return new DecimalMetaFormat(type, prefix, min, max, stepFactor, currency);
	}

	public static final String UNDERSCORE = "_";
	public static final String LEFT_SQ_BRACKET = "[";
	public static final String RIGHT_SQ_BRACKET = "]";

	/**
	 * Format the {@code toFormatParam} to text.
	 *
	 * @param toFormat The {@code DecimalMetaFormat} to format.
	 * @return {@code Spinner_Min[0]_Max[1]_StepFactor[1]_Prefix[$];} formatted decimal format.
	 */
	public static final String format(DecimalMetaFormat toFormat, Currency newCurrency) {
		StringBuilder returnVal = new StringBuilder();
		if (newCurrency != null) {
			String txtVal = String.format("%1$" + newCurrency.getDefaultFractionDigits()+ "s", "1").replace(' ', '0');
			txtVal = String.format("0.%s", txtVal);
			toFormat.stepFactor = Double.parseDouble(txtVal);
			toFormat.prefix = DecimalMetaFormat.AMOUNT_MINOR_PREFIX.concat(newCurrency.getCurrencyCode());
			toFormat.min = 0;
			toFormat.max = 999999999999L;
		}

		returnVal.append(toFormat.type);//               Spinner
		returnVal.append(UNDERSCORE);//          _
		returnVal.append(MIN);//                                Min
		returnVal.append(LEFT_SQ_BRACKET);//     [
		returnVal.append(toFormat.min);//                  0.0
		returnVal.append(RIGHT_SQ_BRACKET);//    ]
		returnVal.append(UNDERSCORE);//          _
		returnVal.append(MAX);//                                Max
		returnVal.append(LEFT_SQ_BRACKET);//     [
		returnVal.append(toFormat.max);//                  0.0
		returnVal.append(RIGHT_SQ_BRACKET);//    ]
		returnVal.append(UNDERSCORE);//          _
		returnVal.append(STEP_FACTOR);//                        StepFactor
		returnVal.append(LEFT_SQ_BRACKET);//     [
		returnVal.append(toFormat.stepFactor);//           0.01
		returnVal.append(RIGHT_SQ_BRACKET);//    ]
		returnVal.append(UNDERSCORE);//          _
		returnVal.append(PREFIX);//                             Prefix
		returnVal.append(LEFT_SQ_BRACKET);//     [
		returnVal.append(toFormat.prefix);//               $
		returnVal.append(RIGHT_SQ_BRACKET);//    ]

		return returnVal.toString();
	}

	private static String getValueFrom(String variableNameParam, String toRetrieveFromParam) {
		if (variableNameParam == null || toRetrieveFromParam == null) return null;

		int startIndex = variableNameParam.length();
		return toRetrieveFromParam.substring(startIndex + 1, toRetrieveFromParam.length() - 1);
	}

	private static boolean isPrecisionZero(Number value) {
		BigDecimal bd = new BigDecimal(value.doubleValue());
		return bd.precision() == 1;
	}

	/**
	 * @return {@code true} if precision is zero.
	 */
	public boolean isPrecisionZeroForStepFactor() {
		return DecimalMetaFormat.isPrecisionZero(this.getStepFactor());
	}

	/**
	 * Is Amount minor amount with a currency.
	 * 
	 * @return {@code true} If currency is set.
	 */
	public boolean isAmountMinorWithCurrency() {
		return this.amountCurrency != null;
	}

	/**
	 * If {@code amountCurrency} is set, the currency symbol is returned.
	 * Otherwise, the configured prefix.
	 * @return The Prefix for the decimal.
	 */
	public String getPrefix() {
		if (this.isAmountMinorWithCurrency()) return String.format("%s ", this.amountCurrency.getSymbol());
		return this.prefix;
	}
}
