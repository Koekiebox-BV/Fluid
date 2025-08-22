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

package com.fluidbpm.program.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.google.gson.*;

import javax.xml.bind.annotation.XmlTransient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * The Base class for any sub-class that wants to make use of the
 * JSON based message format used by the Fluid RESTful Web Service.
 *
 * @author jasonbruwer
 * @see ABaseFluidVO
 * @since v1.0
 */
public abstract class ABaseFluidGSONObject extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;

    @XmlTransient
    @JsonIgnore
    protected JsonObject jsonObject;

    @XmlTransient
    @JsonIgnore
    public boolean jsonIncludeAll = false;

    private static String DATE_FORMAT_001 = "yyyy-MM-dd'T'HH:mm:sss";//2022-02-07T13:37:22.557Z[UTC]
    private static String DATE_FORMAT_002 = "yyyy-MM-dd'T'HH:mm:ssZ";//2022-02-07T13:37:22.55UTC
    private static String DATE_FORMAT_003 = "yyyy-MM-dd'T'HH:mm:ss";
    private static String DATE_FORMAT_004 = "h:mma";
    private static String DATE_FORMAT_005 = "yyyy-MM-dd";

    private static String[] SUPPORTED_FORMATS = {
            DATE_FORMAT_001, DATE_FORMAT_002, DATE_FORMAT_003, DATE_FORMAT_004, DATE_FORMAT_005
    };

    /**
     * The JSON mapping for the {@code ABaseFluidJSONObject} object.
     */
    public static class JSONMapping {
        public static final String ID = "id";
        public static final String SERVICE_TICKET = "serviceTicket";
        public static final String REQUEST_UUID = "requestUuid";
        public static final String ECHO = "echo";

        /**
         * Elastic specific properties.
         */
        public static final class Elastic {
            public static final String PROPERTIES = "properties";
            public static final String FORM_INDEX_PREFIX = "form_";
        }
    }

    /**
     * Default constructor.
     */
    public ABaseFluidGSONObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidGSONObject(JsonObject jsonObjectParam) {
        this();

        this.jsonObject = jsonObjectParam;
        if (this.jsonObject == null) return;

        //Id...
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.ID)) {
            JsonElement idObject = this.jsonObject.get(JSONMapping.ID);
            if (idObject.isJsonPrimitive() && idObject.getAsJsonPrimitive().isNumber()) {
                // Long Id...
                this.setId(idObject.getAsLong());
            } else if (idObject.isJsonPrimitive() && idObject.getAsJsonPrimitive().isString()) {
                // String Id...
                String idStr = idObject.getAsString();
                try {
                    this.setId(Long.parseLong(idStr));
                } catch (NumberFormatException nfe) {
                    this.setId(null);
                }
            } else {
                throw new IllegalArgumentException("Unable to parse Field '" + JSONMapping.ID + "'.");
            }
        }

        this.setServiceTicket(this.getAsStringNullSafe(this.jsonObject, JSONMapping.SERVICE_TICKET));
        this.setRequestUuid(this.getAsStringNullSafe(this.jsonObject, JSONMapping.REQUEST_UUID));
        this.setEcho(this.getAsStringNullSafe(this.jsonObject, JSONMapping.ECHO));
    }

    @XmlTransient
    @JsonIgnore
    protected boolean isPropertyNull(JsonObject jsonObject, String propertyName) {
        if (jsonObject == null) return true;
        if (!jsonObject.has(propertyName)) return true;
        return jsonObject.get(propertyName).isJsonNull();
    }

    @XmlTransient
    @JsonIgnore
    protected boolean isPropertyNotNull(JsonObject jsonObject, String propertyName) {
        return !this.isPropertyNull(jsonObject, propertyName);
    }

    /**
     * Safely retrieves the string value of the specified property from a given JSON object.
     * Returns null if the property does not exist or its value is null.
     *
     * @param jsonObject   The JSON object containing the property.
     * @param propertyName The name of the property to retrieve.
     * @return The string value of the specified property, or null if the property does not exist or its value is null.
     */
    @XmlTransient
    @JsonIgnore
    protected String getAsStringNullSafe(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        if (jsonElement == null || jsonElement.isJsonNull()) return null;
        return jsonElement.getAsString();
    }

    /**
     * Safely retrieves the boolean value of the specified property from a given JSON object.
     * Returns null if the property does not exist or its value is null.
     *
     * @param jsonObject   The JSON object containing the property to retrieve.
     * @param propertyName The name of the property to retrieve.
     * @return The boolean value of the specified property, or null if the property does not exist or its value is null.
     */
    @XmlTransient
    @JsonIgnore
    protected Boolean getAsBooleanNullSafe(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        if (jsonElement == null || jsonElement.isJsonNull()) return null;
        return jsonElement.getAsBoolean();
    }

    /**
     * Safely retrieves the integer value of the specified property from a given JSON object.
     * Returns null if the property does not exist or its value is null.
     *
     * @param jsonObject   The JSON object containing the property to retrieve.
     * @param propertyName The name of the property to retrieve.
     * @return The integer value of the specified property, or null if the property does not exist or its value is null.
     */
    @XmlTransient
    @JsonIgnore
    protected Integer getAsIntegerNullSafe(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        if (jsonElement == null || jsonElement.isJsonNull()) return null;
        return jsonElement.getAsInt();
    }

    /**
     * Safely retrieves the float value of the specified property from a given JSON object.
     * Returns null if the property does not exist or its value is null.
     *
     * @param jsonObject   The JSON object containing the property to retrieve.
     * @param propertyName The name of the property to retrieve.
     * @return The float value of the specified property, or null if the property does not exist
     * or its value is null.
     */
    @XmlTransient
    @JsonIgnore
    protected Float getAsFloatNullSafe(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        if (jsonElement == null || jsonElement.isJsonNull()) return null;
        return jsonElement.getAsFloat();
    }

    /**
     * Safely retrieves the long value of the specified property from a given JSON object.
     * Returns null if the property does not exist or its value is null.
     *
     * @param jsonObject   The JSON object containing the property to retrieve.
     * @param propertyName The name of the property to retrieve.
     * @return The long value of the specified property, or null if the property does not exist or its value is null.
     */
    @XmlTransient
    @JsonIgnore
    protected Long getAsLongNullSafe(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        if (jsonElement == null || jsonElement.isJsonNull()) return null;
        return jsonElement.getAsLong();
    }

    /**
     * Checks if the specified property in the given JSON object is a numeric value.
     *
     * @param jsonObject   The JSON object containing the property to check.
     * @param propertyName The name of the property to verify.
     * @return {@code true} if the property exists and its value is numeric; {@code false} otherwise.
     */
    @XmlTransient
    @JsonIgnore
    protected boolean isPropertyNumber(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber();
    }

    /**
     * Checks whether the specified property in the given JSON object
     * is a boolean type.
     *
     * @param jsonObject   The JSON object containing the property to check.
     * @param propertyName The name of the property to verify.
     * @return {@code true} if the property exists and its value is a boolean; {@code false} otherwise.
     */
    @XmlTransient
    @JsonIgnore
    protected boolean isPropertyBoolean(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean();
    }

    /**
     * Checks if the given property in the provided JSON object is a string type.
     *
     * @param jsonObject   The JSON object containing the property to check.
     * @param propertyName The name of the property to verify.
     * @return {@code true} if the property exists and its value is a string; {@code false} otherwise.
     */
    @XmlTransient
    @JsonIgnore
    protected boolean isPropertyString(JsonObject jsonObject, String propertyName) {
        JsonElement jsonElement = jsonObject.get(propertyName);
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
    }

    /**
     * Converts a JSON-formatted string into a {@code JsonObject}.
     *
     * @param value The JSON string to be converted into a {@code JsonObject}.
     * @return The parsed {@code JsonObject} representation of the input string.
     * If the input string is invalid or not properly formatted JSON,
     * an exception may be thrown during parsing.
     */
    @XmlTransient
    @JsonIgnore
    protected JsonObject stringAsJsonObject(String value) {
        return JsonParser.parseString(value).getAsJsonObject();
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JsonObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JsonObject} representation of {@code ABaseFluidJSONObject}
     * @see JsonObject
     */
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = new JsonObject();
        if (this.getId() != null) returnVal.addProperty(JSONMapping.ID, this.getId());
        if (this.getServiceTicket() != null) returnVal.addProperty(JSONMapping.SERVICE_TICKET, this.getServiceTicket());
        if (this.getRequestUuid() != null) returnVal.addProperty(JSONMapping.REQUEST_UUID, this.getRequestUuid());
        if (this.getEcho() != null) returnVal.addProperty(JSONMapping.ECHO, this.getEcho());
        return returnVal;
    }

    /**
     * Converts the {@code Long} timestamp into a {@code Date} object.
     * <p>
     * Returns {@code null} if {@code longValueParam} is {@code null}.
     *
     * @param longValue The milliseconds since January 1, 1970, 00:00:00 GMT
     * @return {@code Date} Object from {@code longValueParam}
     *
     */
    @XmlTransient
    @JsonIgnore
    private Date getLongAsDateFromJson(Long longValue) {
        if (longValue == null) return null;
        return new Date(longValue);
    }

    /**
     * Retrieves the value of field {@code fieldNameParam} as a timestamp.
     *
     * @param fieldNameParam The name of the JSON field to retrieve.
     * @return The value of the JSON Object as a {@code java.util.Date}.
     */
    @XmlTransient
    @JsonIgnore
    public Date getDateFieldValueFromFieldWithName(String fieldNameParam) {
        if (UtilGlobal.isBlank(fieldNameParam) || this.isPropertyNull(this.jsonObject, fieldNameParam)) return null;

        JsonElement objectAtIndex = this.jsonObject.get(fieldNameParam);
        if (this.isPropertyNumber(this.jsonObject, fieldNameParam)) {
            return this.getLongAsDateFromJson(this.jsonObject.get(fieldNameParam).getAsLong());
        } else if (this.isPropertyString(this.jsonObject, fieldNameParam)) {
            Date validDate = null;
            for (String format : SUPPORTED_FORMATS) {
                try {
                    validDate = new SimpleDateFormat(format).parse(objectAtIndex.getAsString());
                    if (validDate != null) break;
                } catch (ParseException parseExcept) {
                    // Do nothing, already null.
                }
            }
            return validDate;
        }
        return null;
    }

    /**
     * Converts the {@code Date} object into a {@code Long} timestamp.
     * <p>
     * Returns {@code null} if {@code dateValueParam} is {@code null}.
     *
     * @param dateValueParam {@code Long} Object from {@code dateValueParam}
     * @return The milliseconds since January 1, 1970, 00:00:00 GMT
     */
    @XmlTransient
    @JsonIgnore
    public Long getDateAsLongFromJson(Date dateValueParam) {
        if (dateValueParam == null) return null;
        return dateValueParam.getTime();
    }

    /**
     * Converts the {@code Date} object into a {@code Long} timestamp.
     * <p>
     * Returns {@code null} if {@code dateValueParam} is {@code null}.
     *
     * @param dateValue {@code Long} Object from {@code dateValueParam}
     * @return The milliseconds since January 1, 1970, 00:00:00 GMT
     */
    @XmlTransient
    @JsonIgnore
    public Object getDateAsObjectFromJson(Date dateValue) {
        return this.getDateAsLongFromJson(dateValue);
    }

    /**
     * Returns the local JSON object.
     * Only set through constructor.
     *
     * @return The local set {@code JsonObject} object.
     */
    @XmlTransient
    @JsonIgnore
    public JsonObject getJSONObject() {
        return this.jsonObject;
    }

    /**
     * Extracts a list of objects of type {@code T} from the specified JSON object.
     * This method looks for an array field in the JSON object with the given {@code fieldName},
     * applies the provided {@code factory} function to each element in the array,
     * and returns a list of the resulting objects.
     *
     * @param <T>       The type of objects to be extracted. Must extend {@code ABaseFluidGSONObject}.
     * @param object    The {@code JsonObject} containing the JSON data.
     * @param fieldName The name of the field in the {@code JsonObject} that represents
     *                  the array of objects to extract.
     * @param factory   A function that converts a {@code JsonObject} representing an element
     *                  of the array into an instance of {@code T}.
     * @return A list of objects of type {@code T} extracted from the specified JSON field.
     * If the field does not exist or is not a JSON array, an empty list is returned.
     */
    @XmlTransient
    @JsonIgnore
    protected <T extends ABaseFluidGSONObject> List<T> extractObjects(
            JsonObject object,
            String fieldName,
            Function<JsonObject, T> factory
    ) {
        List<T> list = new ArrayList<>();
        if (this.isPropertyNull(object, fieldName) || !object.isJsonArray()) return list;
        JsonArray listing = object.getAsJsonArray(fieldName);
        listing.forEach(itm -> list.add(factory.apply(itm.getAsJsonObject())));
        return list;
    }

    /**
     * Extracts and constructs an object of type T from the specified JSON object and field name
     * using the provided factory function.
     *
     * @param object the JSON object containing the data
     * @param fieldName the name of the field to extract from the JSON object
     * @param factory a function that takes a JsonObject and produces an object of type T
     * @return the constructed object of type T, or null if the field is null or the JSON object is invalid
     */
    @XmlTransient
    @JsonIgnore
    protected <T extends ABaseFluidGSONObject> T extractObject(
            JsonObject object,
            String fieldName,
            Function<JsonObject, T> factory
    ) {
        if (this.isPropertyNull(object, fieldName) || !object.isJsonObject()) return null;
        return factory.apply(object.get(fieldName).getAsJsonObject());
    }

    /**
     * Extracts a list of string values from the specified field of a JSON object.
     *
     * @param object    the JSON object containing the target field
     * @param fieldName the name of the field to extract string values from
     * @return a list of strings extracted from the specified field, or an empty list if the field is not a JSON array
     */
    @XmlTransient
    @JsonIgnore
    protected List<String> extractStrings(JsonObject object, String fieldName) {
        List<String> list = new ArrayList<>();
        if (this.isPropertyNull(object, fieldName) || !object.isJsonArray()) return list;
        JsonArray listing = object.getAsJsonArray(fieldName);
        listing.forEach(itm -> list.add(itm.getAsString()));
        return list;
    }


    /**
     * Extracts a list of long values from a specified field in a JsonObject.
     * If the field is null or not a JSON array, an empty list is returned.
     *
     * @param object    the JsonObject to extract data from
     * @param fieldName the name of the field to retrieve the values from
     * @return a List of Long values extracted from the specified field, or an empty list if the field is null or not a JSON array
     */
    @XmlTransient
    @JsonIgnore
    protected List<Long> extractLongs(JsonObject object, String fieldName) {
        List<Long> list = new ArrayList<>();
        if (this.isPropertyNull(object, fieldName) || !object.isJsonArray()) return list;
        JsonArray listing = object.getAsJsonArray(fieldName);
        listing.forEach(itm -> list.add(itm.getAsLong()));
        return list;
    }

    /**
     * Converts a list of strings into a JsonArray.
     *
     * @param list the list of strings to be converted into a JsonArray
     * @return a JsonArray containing the elements of the input list
     */
    @XmlTransient
    @JsonIgnore
    protected JsonArray toJsonArray(List<String> list) {
        JsonArray jsonArray = new JsonArray();
        if (list == null) return jsonArray;
        list.forEach(jsonArray::add);
        return jsonArray;
    }

    /**
     * Converts a list of Long values into a JsonArray where each Long value is added as an element.
     *
     * @param list the List of Long values to be converted into a JsonArray; can be null.
     * @return a JsonArray containing the Long values from the list, or an empty JsonArray if the input list is null.
     */
    @XmlTransient
    @JsonIgnore
    protected JsonArray toJsonLongArray(List<Long> list) {
        JsonArray jsonArray = new JsonArray();
        if (list == null) return jsonArray;
        list.forEach(jsonArray::add);
        return jsonArray;
    }

    /**
     * Converts a list of objects extending ABaseFluidGSONObject into a JsonArray.
     *
     * @param list the list of objects to be serialized into a JsonArray. Each object in the list should extend ABaseFluidGSONObject.
     * @return a JsonArray containing the serialized objects. If the input list is null, an empty JsonArray is returned.
     */
    @XmlTransient
    @JsonIgnore
    protected <T extends ABaseFluidGSONObject> JsonArray toJsonObjArray(List<T> list) {
        JsonArray jsonArray = new JsonArray();
        if (list == null) return jsonArray;
        list.forEach(itm -> jsonArray.add(itm.toJsonObject()));
        return jsonArray;
    }

    /**
     * Return the Text representation of {@code this} object.
     *
     * @return JSON body of {@code this} object.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public String toString() {
        JsonObject jsonObject = this.toJsonObject();
        return (jsonObject == null) ? null : jsonObject.toString();
    }

    /**
     * Return the Text representation of {@code this} object.
     *
     * @return JSON body of {@code this} object.
     */
    @XmlTransient
    @JsonIgnore
    public String toStringPretty() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject jsonObject = this.toJsonObject();
        return (jsonObject == null) ? null : gson.toJson(jsonObject);
    }
}
