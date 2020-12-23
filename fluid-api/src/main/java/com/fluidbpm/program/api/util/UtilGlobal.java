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

package com.fluidbpm.program.api.util;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.google.common.io.BaseEncoding;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Global utility class for the {@code com.fluidbpm.program.api.util} package.
 *
 * @author jasonbruwer on 2016/03/09.
 * @since 1.0
 */
public class UtilGlobal {

	/**
	 * Raygun API key for error tracking.
	 */
	public static final class Raygun {
		public static String API_KEY = null;
	}

	public static final String EMPTY = "";
	public static final String PIPE = "|";
	public static final String COMMA = ",";
	public static final String COMMA_SPACE = ", ";
	public static final String FORWARD_SLASH = "/";
	public static final String NONE = "[None]";

	//RegEx...
	public static final String REG_EX_SPACE = "\\ ";
	public static final String REG_EX_COMMA = "\\,";
	public static final String REG_EX_PIPE = "\\|";

	public static final String ZERO = "0";

	public static final String ENCODING_UTF_8 = "UTF-8";

	/**
	 * The field type id mappings.
	 */
	public static class FieldTypeId {
		public static final int _1_TEXT = 1;
		public static final int _2_TRUE_FALSE = 2;
		public static final int _3_PARAGRAPH_TEXT = 3;
		public static final int _4_MULTI_CHOICE = 4;
		public static final int _5_DATE_TIME = 5;
		public static final int _6_DECIMAL = 6;
		public static final int _7_TABLE_FIELD = 7;
		public static final int _8_TEXT_ENCRYPTED = 8;
		public static final int _9_LABEL = 9;
	}

	/**
	 * Utility method for creating camel-case-upper from {@code inputParam}
	 *
	 * @param inputParam The input to convert.
	 * @return The converted value.
	 */
	public String toCamelUpperCase(String inputParam) {
		if (inputParam == null) {
			return null;
		}

		if (inputParam.isEmpty()) {
			return EMPTY;
		}

		char[] original = inputParam.toCharArray();
		StringBuilder returnBuffer =
				new StringBuilder(Character.toString(
						Character.toLowerCase(original[0])));

		boolean nextTitleCase = false;
		for (int index = 1;index < original.length;index++) {
			char c = original[index];

			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
				continue;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			returnBuffer.append(c);
		}

		return returnBuffer.toString();
	}

	/**
	 * Extract and returns Latitude from {@code textToCheckParam} applicable to
	 * Fluid.
	 *
	 * @param textToCheckParam The Latitude and Longitude to extract Latitude from.
	 * @return Geo Latitude from {@code textToCheckParam}.
	 */
	public String getLatitudeFromFluidText(String textToCheckParam) {

		if (textToCheckParam == null || textToCheckParam.isEmpty()) {
			return EMPTY;
		}

		String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
		if (latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
			latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
		}

		if (latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
			return ZERO;
		}

		if (latitudeAndLongitude.length > 1)
		{
			return toGoeSafe(latitudeAndLongitude[0]);
		}

		return ZERO;
	}

	/**
	 * Extract and returns Longitude from {@code textToCheckParam}
	 * applicable to ElasticSearch.
	 *
	 * @param textToCheckParam The Latitude and Longitude to extract Longitude from.
	 * @return Geo Longitude from {@code textToCheckParam}.
	 */
	public String getLongitudeFromFluidText(String textToCheckParam) {
		if (textToCheckParam == null || textToCheckParam.trim().isEmpty())
		{
			return EMPTY;
		}

		String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
		if (latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
			latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
		}

		if (latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
			return ZERO;
		}

		if (latitudeAndLongitude.length > 1) {
			return this.toGoeSafe(latitudeAndLongitude[1]);
		}

		return ZERO;
	}

	/**
	 * Extract and returns Latitude from {@code textToCheckParam}
	 * applicable to ElasticSearch.
	 *
	 * @param textToCheckParam The Latitude and Longitude to extract Latitude from.
	 * @return Geo Latitude from {@code textToCheckParam}.
	 */
	public double getLatitudeFromElasticSearchText(String textToCheckParam)
	{
		if (textToCheckParam == null || textToCheckParam.isEmpty()) {
			return 0.0;
		}

		String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
		if (latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
			latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
		}

		if (latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
			return 0.0;
		}

		if (latitudeAndLongitude.length > 1) {
			return toDoubleSafe(latitudeAndLongitude[0]);
		}

		return 0.0;
	}

	/**
	 * Extract and returns Longitude from {@code textToCheckParam}
	 * applicable to ElasticSearch.
	 *
	 * @param textToCheckParam The Latitude and Longitude to extract Longitude from.
	 * @return Geo Longitude from {@code textToCheckParam}.
	 */
	public double getLongitudeFromElasticSearchText(String textToCheckParam)
	{
		if (textToCheckParam == null || textToCheckParam.trim().isEmpty()) {
			return 0.0;
		}

		String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
		if (latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
			latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
		}

		if (latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
			return 0.0;
		}

		if (latitudeAndLongitude.length > 1) {
			return this.toDoubleSafe(latitudeAndLongitude[1]);
		}

		return 0.0;
	}

	/**
	 * Convert the {@code toParseParam} to a double.
	 *
	 * @param toParseParam The {@code String} value to convert to {@code double}.
	 *
	 * @return {@code toParseParam} converted to a {@code double}.
	 */
	public final double toDoubleSafe(String toParseParam) {
		if (toParseParam == null || toParseParam.trim().isEmpty()) {
			return 0D;
		}
		try {
			return Double.parseDouble(toParseParam);
		} catch (NumberFormatException e) {
			return 0D;
		}
	}

	/**
	 * Convert the {@code toParseParam} to a double.
	 *
	 * @param toParseParam The {@code String} value to convert to {@code double}.
	 *
	 * @return {@code toParseParam} converted to a {@code double}.
	 */
	public final String toGoeSafe(String toParseParam) {
		if (toParseParam == null || toParseParam.trim().isEmpty()) {
			return ZERO;
		}
		try {
			for (char charToCheck : toParseParam.toCharArray()){

				if (!Character.isDigit(charToCheck) && '.' != charToCheck){
					return ZERO;
				}
			}

			if (toParseParam.length() > 12){
				return toParseParam.substring(0,12);
			}

			return toParseParam;
		} catch (NumberFormatException e) {
			return ZERO;
		}
	}

	/**
	 * Decodes the {@code base64StringParam} as {@code byte[]}.
	 *
	 * @param base64StringParam String to convert to {@code byte[]}.
	 * @return byte[].
	 */
	public static byte[] decodeBase64(String base64StringParam) {
		if (base64StringParam == null) {
			return null;
		}

		if (base64StringParam.isEmpty()) {
			return new byte[]{};
		}

		return BaseEncoding.base64().decode(base64StringParam);
	}

	/**
	 * Encodes the {@code bytesParam} as {@code java.lang.String}.
	 *
	 * @param bytesParam bytes to convert to Hex.
	 * @return Hex String in upper case.
	 */
	public static String encodeBase16(byte[] bytesParam) {
		if (bytesParam == null)
		{
			return null;
		}

		if (bytesParam.length == 0)
		{
			return UtilGlobal.EMPTY;
		}

		return DatatypeConverter.printHexBinary(bytesParam).toUpperCase();
	}

	/**
	 * Encodes the {@code stringParam} as {@code byte[]}.
	 *
	 * @param stringParam to convert to bytes.
	 * @return bytes from {@code stringParam}.
	 */
	public static byte[] decodeBase16(String stringParam) {

		if (stringParam == null) {
			return null;
		}

		if (stringParam.trim().isEmpty())
		{
			return new byte[]{};
		}

		int len = stringParam.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(stringParam.charAt(i), 16) << 4)
					+ Character.digit(stringParam.charAt(i+1), 16));
		}

		return data;
	}

	/**
	 * Encodes the {@code bytesParam} as {@code java.lang.String}.
	 *
	 * @param bytesParam bytes to convert to Base64.
	 * @return Base64 String.
	 */
	public static String encodeBase64(byte[] bytesParam) {
		if (bytesParam == null) {
			return null;
		}

		if (bytesParam.length == 0) {
			return UtilGlobal.EMPTY;
		}

		return BaseEncoding.base64().encode(bytesParam);
	}

	/**
	 * <p>Adds all the elements of the given arrays into a new array.</p>
	 * <p>The new array contains all of the element of {@code array1} followed
	 * by all of the elements {@code array2}. When an array is returned, it is always
	 * a new array.</p>
	 *
	 * <pre>
	 * ArrayUtils.addAll(array1, null)   = cloned copy of array1
	 * ArrayUtils.addAll(null, array2)   = cloned copy of array2
	 * ArrayUtils.addAll([], [])         = []
	 * </pre>
	 *
	 * @param array1  the first array whose elements are added to the new array.
	 * @param array2  the second array whose elements are added to the new array.
	 * @return The new byte[] array.
	 * @since 2.1
	 */
	public static byte[] addAll(final byte[] array1, final byte... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		final byte[] joinedArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	/**
	 * <p>Clones an array returning a typecast result and handling
	 * {@code null}.</p>
	 *
	 * <p>This method returns {@code null} for a {@code null} input array.</p>
	 *
	 * @param array  the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static byte[] clone(final byte[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	/**
	 * Sets the flat value on the {@code objectToSetFieldOnParam} JSON object.
	 *
	 * If the {@code fieldToExtractFromParam} is not provided, nothing will be set.
	 *
	 * If {@code fieldNamePrefixParam} or {@code fieldNameIdPrefixParam} is not set, an
	 * {@link NullPointerException} will be thrown.
	 *
	 * @param fieldNamePrefixParam The prefix field name.
	 * @param fieldNameIdPrefixParam The prefix field name for id.
	 * @param fieldToExtractFromParam The field to extract the value from.
	 * @param objectToSetFieldOnParam The JSON object to set the field value on.
	 *
	 * @see JSONObject
	 * @see Field
	 */
	public void setFlatFieldOnJSONObj(
			String fieldNamePrefixParam,
			String fieldNameIdPrefixParam,
			Field fieldToExtractFromParam,
			JSONObject objectToSetFieldOnParam)
	{
		if (fieldToExtractFromParam == null)
		{
			return;
		}

		String fieldName = fieldToExtractFromParam.getFieldNameAsUpperCamel();
		if (fieldName == null || fieldName.trim().isEmpty())
		{
			return;
		}

		String completeFieldName = fieldNamePrefixParam.concat(fieldName);
		String completeFieldNameId = fieldNameIdPrefixParam.concat(fieldName);

		objectToSetFieldOnParam.put(completeFieldNameId,
				fieldToExtractFromParam.getId());

		Object fieldValue = fieldToExtractFromParam.getFieldValue();

		if (fieldValue == null) {
			objectToSetFieldOnParam.put(
					completeFieldName,
					JSONObject.NULL);
		} else if (fieldValue instanceof TableField) {
			//Table field...
			return;
		} else if (fieldValue instanceof MultiChoice) {
			//Multiple Choice...
			MultiChoice multiChoice = (MultiChoice) fieldValue;
			//Nothing provided...
			if (multiChoice.getSelectedMultiChoices() == null ||
					multiChoice.getSelectedMultiChoices().isEmpty()) {
				objectToSetFieldOnParam.put(
						completeFieldName,
						JSONObject.NULL);
				return;
			}

			StringBuilder builder = new StringBuilder();
			multiChoice.getSelectedMultiChoices().forEach(selectedChoice ->
			{
				builder.append(selectedChoice);
				builder.append(", ");
			});

			String selectVal = builder.toString();
			if (selectVal != null && !selectVal.trim().isEmpty()) {
				selectVal = selectVal.substring(0, selectVal.length() - 2);
			}

			objectToSetFieldOnParam.put(completeFieldName, selectVal);
		} else if((fieldValue instanceof Number || fieldValue instanceof Boolean) ||
				fieldValue instanceof String) {
			//Other valid types...
			if ((fieldValue instanceof String) &&
					Field.LATITUDE_AND_LONGITUDE.equals(
							fieldToExtractFromParam.getTypeMetaData())) {
				String formFieldValueStr = fieldValue.toString();

				String latitudeTxt = this.getLatitudeFromFluidText(formFieldValueStr);
				String longitudeTxt = this.getLongitudeFromFluidText(formFieldValueStr);

				fieldValue = (latitudeTxt.concat(UtilGlobal.COMMA).concat(longitudeTxt));
			}

			objectToSetFieldOnParam.put(completeFieldName, fieldValue);
		} else if (fieldValue instanceof Date) {
			//Date...
			objectToSetFieldOnParam.put(completeFieldName, ((Date)fieldValue).getTime());
		}
	}

	/**
	 * Create a random UUID using {@code UUID.randomUUID()}.
	 * Example; 91e4da00-b858-4ccc-a0c3-7acad7415911
	 *
	 * @return Random 36 character string.
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Performs a check whether the objects inside {@code toCheckForNullParam} is null.
	 *
	 * @param toCheckForNull Objects to check for {@code null}
	 * @return {@code true} if all objects in {@code toCheckForNullParam} is {@code null}
	 */
	public static final boolean isAllNull(Object ... toCheckForNull) {
		if (toCheckForNull == null || toCheckForNull.length == 0) return true;

		for (Object toCheck : toCheckForNull)
			if (toCheck != null) return false;

		return true;
	}

	/**
	 * Verify if one of the Strings in {@code stringsToCheck} is empty.
	 * If the array itself is {@code null} or empty, a {@code true} value will be returned.
	 *
	 * @param stringsToCheck The list of Strings to verify of being empty.
	 * @return {@code true} if any of the String's in {@code stringsToCheck} is {@code null} or empty.
	 */
	public static final boolean isBlank(String ... stringsToCheck) {
		if (stringsToCheck == null || stringsToCheck.length == 0) return true;
		for (String toCheck : stringsToCheck) if (toCheck == null || toCheck.trim().isEmpty()) return true;
		return false;
	}

	/**
	 * Verify if one of the Strings in {@code stringsToCheck} is NOT empty.
	 * If the array itself is {@code null} or empty, a {@code false} value will be returned.
	 *
	 * @param stringsToCheck The list of Strings to verify of NOT being empty.
	 * @return {@code true} if any of the String's in {@code stringsToCheck} is NOT {@code null} or empty.
	 */
	public static final boolean isNotBlank(String ... stringsToCheck) {
		if (stringsToCheck == null || stringsToCheck.length == 0) return false;
		for (String toCheck : stringsToCheck) if (toCheck == null || toCheck.trim().isEmpty()) return false;
		return true;
	}

	/**
	 * Convert String date {@code dateInStringParam} to {@code Date} applying format {@code formatParam}.
	 *
	 * @param dateInString The date in String format to parse.
	 * @param format The format to apply.
	 * @return {@code Date} object from {@code dateInString}
	 *
	 * @see Date
	 */
	public static Date fromStringToDateSafe(String dateInString, String format) {
		if (dateInString == null || format == null) {
			return null;
		}

		try {
			return new SimpleDateFormat(format).parse(dateInString);
		} catch (ParseException nfe) {
			return null;
		}
	}

	/**
	 * Returns -1 if there is a problem with conversion.
	 *
	 * @param toParseParam The {@code String} value to parse to {@code int}.
	 * @return int for {@code toParseParam}.
	 */
	public static final int toIntSafe(String toParseParam) {
		if (toParseParam == null || toParseParam.isEmpty()) {
			return -1;
		}
		try {
			return Double.valueOf(toParseParam).intValue();
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Returns -1 if there is a problem with conversion.
	 *
	 * @param toParseParam The {@code String} value to parse to {@code long}.
	 * @return long for {@code toParseParam}.
	 */
	public static final long toLongSafe(String toParseParam) {
		if (toParseParam == null || toParseParam.isEmpty()) {
			return -1;
		}
		try {
			return Double.valueOf(toParseParam).longValue();
		} catch (NumberFormatException e) {
			return -1L;
		}
	}

	/**
	 * Uncompress the raw {@code compressedBytesParam}.
	 *
	 * @param compressedBytes The compressed bytes to uncompress.
	 * @param charset The character set yo use.
	 *
	 * @return Uncompressed bytes.
	 *
	 * @throws IOException - If there is an issue during the un-compression.
	 */
	public static byte[] uncompress(
		byte[] compressedBytes,
		Charset charset
	) throws IOException {
		byte[] buffer = new byte[1024];
		byte[] returnVal = null;
		ZipInputStream zis = null;
		if (charset == null) {
			zis = new ZipInputStream(new ByteArrayInputStream(compressedBytes));
		} else {
			zis = new ZipInputStream(new ByteArrayInputStream(compressedBytes), charset);
		}

		//get the zip file content
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		//get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();
		if (ze == null){
			return returnVal;
		}
		int len;
		while ((len = zis.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}

		zis.closeEntry();
		zis.close();

		bos.flush();
		bos.close();
		returnVal = bos.toByteArray();
		return returnVal;
	}

	/**
	 * Encode {@code toEncodeParam} for URL using UTF-8 character set.
	 *
	 * @param toEncodeParam The text to encode.
	 * @return encoded {@code toEncodeParam} value in UTF-8 encoding
	 */
	public static String encodeURL(String toEncodeParam) {
		if (toEncodeParam == null || toEncodeParam.trim().isEmpty()) return UtilGlobal.EMPTY;

		try {
			return URLEncoder.encode(toEncodeParam, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return toEncodeParam;
		}
	}

	/**
	 * Convert the {@code list} to {@code JSONArray}
	 *
	 * @param list The list to convert to JSON Array.
	 * @param <T> The type of {@code ABaseFluidJSONObject}
	 * @return JSONArray from {@code list}
	 * @see ABaseFluidJSONObject
	 * @see JSONArray
	 */
	public static <T extends ABaseFluidJSONObject> JSONArray toJSONArray(List<T> list) {
		if (list == null) return null;

		JSONArray jsonArray = new JSONArray();
		for (T toAdd :list) jsonArray.put(toAdd.toJsonObject());
		return jsonArray;
	}
}
