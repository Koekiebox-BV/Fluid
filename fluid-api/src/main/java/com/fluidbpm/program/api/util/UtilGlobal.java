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

import java.util.Base64;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;

/**
 * Global utility class for the {@code com.fluidbpm.program.api.util} package.
 *
 * @author jasonbruwer on 2016/03/09.
 * @since 1.0
 */
public class UtilGlobal {

    public static final String EMPTY = "";
    public static final String PIPE = "|";
    public static final String COMMA = ",";
    public static final String COMMA_SPACE = ", ";
    public static final String FORWARD_SLASH = "/";

    //RegEx...
    public static final String REG_EX_COMMA = "\\,";
    public static final String REG_EX_PIPE = "\\|";

    public static final String ZERO = "0";

    public static final String ENCODING_UTF_8 = "UTF-8";

    /**
     * The field type id mappings.
     */
    public static class FieldTypeId
    {
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

        if(inputParam == null)
        {
            return null;
        }

        if(inputParam.isEmpty())
        {
            return EMPTY;
        }

        char[] original = inputParam.toCharArray();

        StringBuilder titleCase =
                new StringBuilder(Character.toString(
                        Character.toLowerCase(original[0])));

        boolean nextTitleCase = false;
        for(int index = 1;index < original.length;index++)
        {
            char c = original[index];

            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
                continue;
            }
            //Just add...
            else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    /**
     * Extract and returns Latitude from {@code textToCheckParam} applicable to
     * Fluid.
     *
     * @param textToCheckParam The Latitude and Longitude to extract Latitude from.
     * @return Geo Latitude from {@code textToCheckParam}.
     */
    public String getLatitudeFromFluidText(String textToCheckParam) {

        if(textToCheckParam == null || textToCheckParam.isEmpty()) {
            return EMPTY;
        }

        String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
            latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
        }

        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
            return ZERO;
        }

        if(latitudeAndLongitude.length > 1)
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
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty())
        {
            return EMPTY;
        }

        String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
            latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
        }
        
        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
            return ZERO;
        }

        if(latitudeAndLongitude.length > 1) {
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
        if(textToCheckParam == null || textToCheckParam.isEmpty()) {
            return 0.0;
        }

        String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
            latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
        }

        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1) {
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
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty()) {
            return 0.0;
        }

        String[] latitudeAndLongitude = textToCheckParam.split(REG_EX_COMMA);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length < 2) {
            latitudeAndLongitude = textToCheckParam.split(REG_EX_PIPE);
        }
        
        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0) {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1) {
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
            for(char charToCheck : toParseParam.toCharArray()){

                if(!Character.isDigit(charToCheck) && '.' != charToCheck){
                    return ZERO;
                }
            }

            if(toParseParam.length() > 12){
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
    public static byte[] decodeBase64(String base64StringParam)
    {
        if(base64StringParam == null)
        {
            return null;
        }

        if(base64StringParam.isEmpty())
        {
            return new byte[]{};
        }

        return Base64.getDecoder().decode(base64StringParam);
    }

    /**
     * Encodes the {@code bytesParam} as {@code java.lang.String}.
     *
     * @param bytesParam bytes to convert to Hex.
     * @return Hex String in upper case.
     */
    public static String encodeBase16(byte[] bytesParam)
    {
        if(bytesParam == null)
        {
            return null;
        }

        if(bytesParam.length == 0)
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

        if(stringParam == null)
        {
            return null;
        }

        if(stringParam.trim().isEmpty())
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
    public static String encodeBase64(byte[] bytesParam)
    {
        if(bytesParam == null)
        {
            return null;
        }

        if(bytesParam.length == 0)
        {
            return UtilGlobal.EMPTY;
        }

        return Base64.getEncoder().encodeToString(bytesParam);
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
        if(fieldToExtractFromParam == null)
        {
            return;
        }

        String fieldName = fieldToExtractFromParam.getFieldNameAsUpperCamel();
        if(fieldName == null || fieldName.trim().isEmpty())
        {
            return;
        }

        String completeFieldName = fieldNamePrefixParam.concat(fieldName);
        String completeFieldNameId = fieldNameIdPrefixParam.concat(fieldName);

        objectToSetFieldOnParam.put(completeFieldNameId,
                fieldToExtractFromParam.getId());

        Object fieldValue = fieldToExtractFromParam.getFieldValue();

        if(fieldValue == null)
        {
            objectToSetFieldOnParam.put(
                    completeFieldName,
                    JSONObject.NULL);
        }
        //Table field...
        else if(fieldValue instanceof TableField)
        {
            return;
        }
        //Multiple Choice...
        else if(fieldValue instanceof MultiChoice) {

            MultiChoice multiChoice = (MultiChoice) fieldValue;

            //Nothing provided...
            if(multiChoice.getSelectedMultiChoices() == null ||
                    multiChoice.getSelectedMultiChoices().isEmpty())
            {

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
            if(selectVal != null && !selectVal.trim().isEmpty())
            {
                selectVal = selectVal.substring(0, selectVal.length() - 2);
            }

            objectToSetFieldOnParam.put(completeFieldName, selectVal);
        }
        //Other valid types...
        else if((fieldValue instanceof Number || fieldValue instanceof Boolean) ||
                fieldValue instanceof String)
        {
            if((fieldValue instanceof String) &&
                    Field.LATITUDE_AND_LONGITUDE.equals(
                            fieldToExtractFromParam.getTypeMetaData()))
            {
                String formFieldValueStr = fieldValue.toString();

                String latitudeTxt = this.getLatitudeFromFluidText(formFieldValueStr);
                String longitudeTxt = this.getLongitudeFromFluidText(formFieldValueStr);

                fieldValue = (latitudeTxt.concat(UtilGlobal.COMMA).concat(longitudeTxt));
            }

            objectToSetFieldOnParam.put(completeFieldName, fieldValue);
        }
        //Date...
        else if(fieldValue instanceof Date)
        {
            objectToSetFieldOnParam.put(completeFieldName, ((Date)fieldValue).getTime());
        }
    }
}
