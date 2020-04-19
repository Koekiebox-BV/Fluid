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

package com.fluidbpm.ws.client.v1;

import org.json.JSONObject;

/**
 * Base class for all Field REST related calls.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FormField
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.UserField
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.RouteField
 */
public abstract class ABaseFieldClient extends ABaseClientWS {

	/**
	 * Creates a new client and sets the Base Endpoint URL.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 * @param requestUuidParam The unique identifier per request.
	 */
	public ABaseFieldClient(
			String endpointBaseUrlParam,
			String serviceTicketParam,
			String requestUuidParam
	) {
		super(endpointBaseUrlParam, serviceTicketParam, requestUuidParam);
	}

	/**
	 * Creates a new client and sets the Base Endpoint URL.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public ABaseFieldClient(
			String endpointBaseUrlParam,
			String serviceTicketParam
	) {
		super(endpointBaseUrlParam, serviceTicketParam, null);
	}

	/**
	 * Creates a new client and sets the Base Endpoint URL.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 */
	public ABaseFieldClient(String endpointBaseUrlParam
	) {
		super(endpointBaseUrlParam, null, null);
	}

	/**
	 * Meta-Data types for Field.
	 */
	public static final class FieldMetaData {
		/**
		 * Meta-Data types for Text fields specifically.
		 */
		public static final class Text {
			public static final String PLAIN = "Plain";
			public static final String MASKED = "Masked";
			public static final String BARCODE = "Barcode";
			public static final String LATITUDE_AND_LONGITUDE = "Latitude and Longitude";
		}

		/**
		 * Meta-Data types for Encrypted Text fields specifically.
		 */
		public static final class EncryptedText {
			public static final String PLAIN = "Plain";
			public static final String MASKED = "Masked";
		}

		/**
		 * Meta-Data types for True False fields specifically.
		 */
		public static final class TrueFalse {
			public static final String TRUE_FALSE = "True False";
		}

		/**
		 * Meta-Data types for Paragraph Text fields specifically.
		 */
		public static final class ParagraphText {
			public static final String PLAIN = "Plain";
			public static final String HTML = "HTML";
		}

		/**
		 * Meta-Data types for Multi Choice fields specifically.
		 */
		public static final class MultiChoice {
			public static final String PLAIN = "Plain";
			public static final String PLAIN_SEARCH = "Plain with Search";

			public static final String SELECT_MANY = "Select Many";
			public static final String SELECT_MANY_SEARCH = "Select Many with Search";
		}

		/**
		 * Meta-Data types for Date Time fields specifically.
		 */
		public static final class DateTime {
			public static final String DATE = "Date";
			public static final String DATE_AND_TIME = "Date and Time";
		}

		/**
		 * Meta-Data types for Decimal fields specifically.
		 */
		public static final class Decimal {
			public static final String PLAIN = "Plain";
			public static final String RATING = "Rating";
			public static final String SPINNER = "Spinner";
			public static final String SLIDER = "Slider";

			//Keywords...
			public static final String UNDERSCORE = "_";
			public static final String SQ_OPEN = "[";
			public static final String SQ_CLOSE = "]";

			public static final String MIN = "Min";
			public static final String MAX = "Max";
			public static final String STEP_FACTOR = "StepFactor";
			public static final String PREFIX = "Prefix";
		}

		/**
		 * Meta-Data types for Table Field fields specifically.
		 */
		public static final class TableField {
			public static final String SUM_DECIMALS = "SumDecimals";
			public static final String UNDERSCORE = "_";
			public static final String SQ_OPEN = "[";
			public static final String SQ_CLOSE = "]";
		}
	}

	/**
	 * Construct the correct Meta-Data from parameters.
	 *
	 * @param metaDataPrefixParam Prefix for return val to indicate type. Example;
	 *            Spinner.
	 * @param minParam The min allowed value.
	 * @param maxParam The max allowed value.
	 * @param stepFactorParam The allowed step increments.
	 * @param prefixParam The prefix for the decimal, like $ for currency.
	 *
	 * @return Correctly formatted Decimal type.
	 */
	protected String getMetaDataForDecimalAs(
		String metaDataPrefixParam,
		double minParam,
		double maxParam,
		double stepFactorParam,
		String prefixParam
	) {
		StringBuffer returnBuffer = new StringBuffer();

		if (metaDataPrefixParam != null && !metaDataPrefixParam.isEmpty()) {
			returnBuffer.append(metaDataPrefixParam);
		}

		//Min...
		returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);
		returnBuffer.append(FieldMetaData.Decimal.MIN);
		returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
		returnBuffer.append(minParam);
		returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
		returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

		//Max...
		returnBuffer.append(FieldMetaData.Decimal.MAX);
		returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
		returnBuffer.append(maxParam);
		returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
		returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

		//Step Factor...
		returnBuffer.append(FieldMetaData.Decimal.STEP_FACTOR);
		returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
		returnBuffer.append(stepFactorParam);
		returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
		returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

		//Prefix
		String prefix = (prefixParam == null) ? "" : prefixParam;

		returnBuffer.append(FieldMetaData.Decimal.PREFIX);
		returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
		returnBuffer.append(prefix);
		returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);

		return returnBuffer.toString();
	}
}
