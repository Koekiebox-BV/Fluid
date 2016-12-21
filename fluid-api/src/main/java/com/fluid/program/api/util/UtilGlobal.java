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

package com.fluid.program.api.util;

import java.util.Base64;

/**
 * Global utility class for the {@code com.fluid.program.api.util} package.
 *
 * @author jasonbruwer on 2016/03/09.
 * @since 1.0
 */
public class UtilGlobal {

    public static final String EMPTY = "";
    public static final String REG_EX_PIPE = "\\|";
    public static final String PIPE = "|";
    public static final String COMMA = ",";

    public static final String REG_EX_COMMA = "\\,";

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

        //public static final int _8_TEXT_ENCRYPTED = 8;
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
    public double getLatitudeFromFluidText(String textToCheckParam)
    {
        if(textToCheckParam == null || textToCheckParam.isEmpty())
        {
            return 0.0;
        }

        String[] latitudeAndLongitude =
                textToCheckParam.split(REG_EX_PIPE);

        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0)
        {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1)
        {
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
    public double getLongitudeFromFluidText(String textToCheckParam)
    {
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty())
        {
            return 0.0;
        }

        String[] latitudeAndLongitude =
                textToCheckParam.split(REG_EX_PIPE);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0)
        {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1)
        {
            return this.toDoubleSafe(latitudeAndLongitude[1]);
        }

        return 0.0;
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
        if(textToCheckParam == null || textToCheckParam.isEmpty())
        {
            return 0.0;
        }

        String[] latitudeAndLongitude =
                textToCheckParam.split(REG_EX_COMMA);

        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0)
        {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1)
        {
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
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty())
        {
            return 0.0;
        }

        String[] latitudeAndLongitude =
                textToCheckParam.split(REG_EX_COMMA);
        if(latitudeAndLongitude == null || latitudeAndLongitude.length == 0)
        {
            return 0.0;
        }

        if(latitudeAndLongitude.length > 1)
        {
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

        StringBuilder sb = new StringBuilder();
        for (byte bte : bytesParam) {

            sb.append(Integer.toHexString(bte));
        }

        return sb.toString().toUpperCase();
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
}
