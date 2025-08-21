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

import com.fluidbpm.program.api.util.exception.UtilException;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.common.io.BaseEncoding;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Global utility class for the {@code com.fluidbpm.program.api.util} package.
 *
 * @author jasonbruwer on 2016/03/09.
 * @since 1.0
 */
public class UtilGlobal {
    private static String FLUID_WS_URL = "http://localhost:80/fluid-ws/";
    private static String FLUID_CONFIG_USER = "admin";
    private static String FLUID_CONFIG_USER_PASSWORD = "12345";

    private static String JSON_LINES = "lines";

    private static Boolean enableTrace = null;

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
     * Extracts the field name from description.
     *
     * @param text The Field value to extract the field name from.
     * @return Field name extracted from {@code text}.
     */
    public static String extractFieldNameFromText(String text) {
        if (isBlank(text)) return null;

        int lastOfOpen = text.lastIndexOf("[["), lastClose = text.lastIndexOf("]]");
        if ((lastOfOpen > -1 && lastClose > -1) && lastOfOpen < lastClose) {
            return text.substring(lastOfOpen + 2, lastClose);
        }
        return null;
    }

    /**
     * Extracts the field name from description.
     *
     * @param text The Field value to extract the field name from.
     * @return Field name extracted from {@code text}.
     */
    public static String removeFieldNameFromText(String text) {
        if (isBlank(text)) return null;

        String fieldName;
        while ((fieldName = extractFieldNameFromText(text)) != null) {
            text = text.replace(String.format("[[%s]]", fieldName), EMPTY);
        }
        return text;
    }

    /**
     * Utility method for creating camel-case-upper from {@code inputParam}
     *
     * @param inputParam The input to convert.
     * @return The converted value.
     */
    public String toCamelUpperCase(String inputParam) {
        if (inputParam == null) return null;

        if (inputParam.isEmpty()) return EMPTY;

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
     * Convert the {@code toParseParam} to a double.
     *
     * @param toParseParam The {@code String} value to convert to {@code double}.
     *
     * @return {@code toParseParam} converted to a {@code double}.
     */
    public static final double toDoubleSafe(String toParseParam) {
        if (toParseParam == null || toParseParam.trim().isEmpty()) return 0D;

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
        if (toParseParam == null || toParseParam.trim().isEmpty()) return ZERO;

        try {
            for (char charToCheck : toParseParam.toCharArray()){
                if (!Character.isDigit(charToCheck) && '.' != charToCheck) return ZERO;
            }

            if (toParseParam.length() > 12) return toParseParam.substring(0,12);

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
        if (base64StringParam == null) return null;

        if (base64StringParam.isEmpty()) return new byte[]{};

        return BaseEncoding.base64().decode(base64StringParam);
    }

    /**
     * Encodes the {@code bytesParam} as {@code java.lang.String}.
     *
     * @param bytesParam bytes to convert to Hex.
     * @return Hex String in upper case.
     */
    public static String encodeBase16(byte[] bytesParam) {
        if (bytesParam == null) return null;

        if (bytesParam.length == 0) return UtilGlobal.EMPTY;

        return DatatypeConverter.printHexBinary(bytesParam).toUpperCase();
    }

    /**
     * Encodes the {@code stringParam} as {@code byte[]}.
     *
     * @param stringParam to convert to bytes.
     * @return bytes from {@code stringParam}.
     */
    public static byte[] decodeBase16(String stringParam) {
        if (stringParam == null) return null;

        if (stringParam.trim().isEmpty()) return new byte[]{};


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
        if (bytesParam == null) return null;

        if (bytesParam.length == 0) return UtilGlobal.EMPTY;

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
        if (array1 == null) return clone(array2);
        else if (array2 == null) return clone(array1);

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
        if (array == null) return null;
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
        JSONObject objectToSetFieldOnParam
    ) {
        if (fieldToExtractFromParam == null) return;

        String fieldName = fieldToExtractFromParam.getFieldNameAsUpperCamel();
        if (fieldName == null || fieldName.trim().isEmpty()) return;

        String completeFieldName = fieldNamePrefixParam.concat(fieldName);
        String completeFieldNameId = fieldNameIdPrefixParam.concat(fieldName);

        objectToSetFieldOnParam.put(completeFieldNameId, fieldToExtractFromParam.getId());
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
            if (multiChoice.getSelectedMultiChoices() == null || multiChoice.getSelectedMultiChoices().isEmpty()) {
                objectToSetFieldOnParam.put(completeFieldName, JSONObject.NULL);
                return;
            }

            StringBuilder builder = new StringBuilder();
            multiChoice.getSelectedMultiChoices().forEach(selectedChoice -> {
                builder.append(selectedChoice);
                builder.append(", ");
            });

            String selectVal = builder.toString();
            if (selectVal != null && !selectVal.trim().isEmpty()) {
                selectVal = selectVal.substring(0, selectVal.length() - 2);
            }

            objectToSetFieldOnParam.put(completeFieldName, selectVal);
        } else if ((fieldValue instanceof Number || fieldValue instanceof Boolean) || fieldValue instanceof String) {
            //Other valid types...
            if ((fieldValue instanceof String) && Field.LATITUDE_AND_LONGITUDE.equals(fieldToExtractFromParam.getTypeMetaData())) {
                GeoUtil geo = new GeoUtil(fieldValue.toString());
                fieldValue = String.format("%s,%s", geo.getLatitude(), geo.getLongitude());
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
     * @param toParse The {@code String} value to parse to {@code long}.
     * @return long for {@code toParseParam}.
     */
    public static final long toLongSafe(String toParse) {
        if (toParse == null || toParse.isEmpty()) return -1;
        try {
            return Double.valueOf(toParse).longValue();
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    /**
     * Returns false if there is a problem with conversion.
     *
     * @param toParse The {@code String} value to parse to {@code boolean}.
     * @return boolean for {@code toParseParam}.
     */
    public static final boolean toBooleanSafe(String toParse) {
        if (toParse == null || toParse.isEmpty()) return false;
        try {
            return Boolean.valueOf(toParse.trim()).booleanValue();
        } catch (NumberFormatException e) {
            return false;
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
     * @return JsonArray from {@code list}
     * @see ABaseFluidJSONObject
     * @see JSONArray
     */
    public static <T extends ABaseFluidJSONObject> JsonArray toJSONArray(List<T> list) {
        if (list == null) return null;

        JsonArray jsonArray = new JsonArray();

        for (T toAdd :list) jsonArray.put(toAdd.toJsonObject());

        return jsonArray;
    }

    /**
     * Write the contents of {@code inputStream} to {@code outFile}.
     *
     * @param inputStream The input-stream of content to write.
     * @param outFile The file to write content to.
     * @throws IOException Any IO errors
     */
    public static void writeStreamToFile(InputStream inputStream, File outFile) throws IOException {
        try (OutputStream fos = new FileOutputStream(outFile,false)) {
            int readByte = -1;
            while ((readByte = inputStream.read()) != -1) {
                fos.write(readByte);
            }
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }

    /**
     * Read the content of a file.
     *
     * @param fileToRead The file to read.
     * @return The {@code byte}s for the read file.
     * @throws IOException Any IO errors
     */
    public static byte[] readFileBytes(File fileToRead) throws IOException {
        if (fileToRead == null) return null;
        return Files.readAllBytes(fileToRead.toPath());
    }

    /**
     * Retrieve the value of the {@code FLUID_WS_URL} property.
     * @param existing Existing properties to attempt to retrieve from.
     * @return property value for {@code FLUID_WS_URL}
     */
    public static String getConfigURLFromSystemProperty(Properties existing) {
        return UtilGlobal.getProperty(
                existing,
                "FLUID_WS_URL",
                FLUID_WS_URL);
    }

    /**
     * Retrieve the value of the {@code CONFIG_USER} property.
     * @param existing Existing properties to attempt to retrieve from.
     * @return property value for {@code CONFIG_USER}
     */
    public static String getConfigUserProperty(Properties existing) {
        return UtilGlobal.getProperty(
                existing,
                "CONFIG_USER",
                FLUID_CONFIG_USER);
    }

    /**
     * Retrieve the value of the {@code CONFIG_USER_PASSWORD} property.
     * @param existing Existing properties to attempt to retrieve from.
     * @return property value for {@code CONFIG_USER_PASSWORD}
     */
    public static String getConfigUserPasswordProperty(Properties existing) {
        return UtilGlobal.getProperty(
                existing,
                "CONFIG_USER_PASSWORD",
                FLUID_CONFIG_USER_PASSWORD);
    }

    /**
     * Creates an SVG XML image from {@code signature}.
     *
     * @param signature The signature JSON data.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return SVG data as text.
     */
    public static String toSvg(String signature, int width, int height) {

        JSONObject jsonObj = new JSONObject(signature);
        if (!jsonObj.has(JSON_LINES)) throw new UtilException("Signature does not have any lines.", UtilException.ErrorCode.GENERAL);

        JsonArray jsonArr = jsonObj.getJSONArray(JSON_LINES);

        final List<String> paths = new ArrayList<>();
        jsonArr.forEach(line -> paths.add(toSvgPath(line)));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n", width, height));
        paths.forEach(sb::append);
        sb.append("</svg>");

        return sb.toString();
    }

    private static String toSvgPath(Object line) {
        StringBuilder sb = new StringBuilder("<path d=\"");

        if (line instanceof JSONArray) {
            JsonArray lineCasted = (JSONArray)line;
            for (int index = 0; index < lineCasted.length(); index++) {
                JsonArray coords = lineCasted.getJSONArray(index);
                sb.append(String.format("%s%s %s ",
                        (index == 0 ? "M" : "L"),
                        coords.getDouble(0),
                        coords.getDouble(1)));
            }
        }

        sb.append("\" stroke=\"black\" fill=\"white\"/>\n");
        return sb.toString();
    }

    /**
     * Retrieve environment or system property.
     *
     * @param existing Existing properties.
     * @param name The name of the property to fetch.
     * @param defaultVal The default value.
     * @return The value.
     */
    public static String getProperty(Properties existing, String name, String defaultVal) {
        //From provided properties...
        if (existing != null) {
            String val = existing.getProperty(name);
            if (val != null && !val.trim().isEmpty()) return val;
        }

        //From System Properties...
        String val = System.getProperties().getProperty(name);
        if (val != null && !val.trim().isEmpty()) return val;

        //From System...
        return System.getProperty(name, defaultVal);
    }

    /**
     * Removes all the whitespace from {@code text}.
     *
     * @param text The text to clear of whitespace.
     * @return New String without whitespace.
     */
    public static String removeWhitespace(String text) {
        if (text == null) return null;

        return text.replace(" ", EMPTY).replace("\t", EMPTY).replace("\n", EMPTY);
    }

    /**
     * Performs an {@code out.println} using {@code template} and {@code params}.
     * Tracing will only be enabled if {@code ENABLE_TRACE=true}
     *
     * @param template Template to apply.
     * @param params The params to parse to {@code template}
     */
    public static void sysOut(String template, Object ... params) {
        if (enableTrace == null) {
            enableTrace = toBooleanSafe(
                    UtilGlobal.getProperty(System.getProperties(),
                            "ENABLE_TRACE", "false").trim().toLowerCase());
        }

        if (!enableTrace) return;

        System.out.println(String.format(template, params));
    }

    /**
     * Check if any one of the {@code booleansToCheckParam} values are {@code true}.
     *
     * @param booleansToCheck The boolean values to check.
     * @return {@code true} If any one of the elements for {@code booleansToCheck} is {@code true}.
     */
    public static final boolean isAnyTrue(boolean ... booleansToCheck) {
        if (booleansToCheck == null || booleansToCheck.length == 0) return false;

        for (boolean toCheck : booleansToCheck) if (toCheck) return true;

        return false;
    }

    /**
     * Check if all of the {@code booleansToCheckParam} values are {@code true}.
     *
     * @param booleansToCheck The boolean values to check.
     * @return {@code true} If ALL of the elements for {@code booleansToCheck} is {@code true}.
     */
    public static final boolean isAllTrue(boolean ... booleansToCheck) {
        if (booleansToCheck == null || booleansToCheck.length == 0) return false;

        for (boolean toCheck : booleansToCheck) if (!toCheck) return false;

        return true;
    }

    /**
     * Check whether a value has a {@code boolean} value of {@code true}.
     * @param txt The value to test.
     * @return {@code true} if yes/1/true, otherwise {@code false}
     */
    public static boolean isValYesNoTrue(String txt) {
        txt = txt.trim().toLowerCase();
        return UtilGlobal.isAnyTrue(
                "true".equals(txt),
                "1".equals(txt),
                "yes".equals(txt)
        );
    }

    /**
     * Convert {@code Object} array into list.
     * @param objs Array to be converted to list.
     * @return Arraylist from {@code objs}
     * @param <T> Type.
     */
    @SafeVarargs
    public static <T> List<T> toListSafe(T... objs) {
        return new ArrayList<>(Arrays.asList(objs));
    }

    /**
     * New instance of field from args.
     * @param fieldName Sets Field Name.
     * @param fieldVal Sets Field Value.
     * @param type Sets Field Type.
     */
    public static Field newField(String fieldName, Object fieldVal, Field.Type type) {
        return new Field(fieldName, fieldVal, type);
    }

    /**
     * New instance of field from args.
     * @param fieldName Sets Field Name.
     * @param multiChoice Sets Field Value as Multichoice.
     */
    public static Field newField(String fieldName, MultiChoice multiChoice) {
        return new Field(fieldName, multiChoice);
    }

    /**
     *
     * @param formType The Form Definition.
     * @param formTitle The Form instance Title.
     * @param formFields The fields for the form instance.
     * @return new instance of {@code Form}
     */
    public static Form newForm(
            String formType,
            String formTitle,
            Field ... formFields
    ) {
        Form returnVal = new Form(formType, formTitle);
        returnVal.setFormFields(toListSafe(formFields));
        return returnVal;
    }

    /**
     * Copies fields from the source JSON object to the target JSON object only if the fields
     * are not already present in the target. Fields already existing in the target are not overwritten.
     *
     * @param source the JsonObject containing the source data fields to be copied
     * @param target the JsonObject to which the fields should be copied, if not already set
     */
    public static void copyJSONFieldsNotSet(JSONObject source, JsonObject target) {
        Iterator<String> keys = source.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (target.has(key)) continue;

            target.put(key, source.get(key));
        }
    }
}
