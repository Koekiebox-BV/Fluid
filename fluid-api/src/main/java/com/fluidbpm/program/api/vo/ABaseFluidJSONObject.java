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
import java.util.Date;

/**
 * <p>
 * The Base class for any sub-class that wants to make use of the
 * JSON based message format used by the Fluid RESTful Web Service.
 *
 * @author jasonbruwer
 * @see ABaseFluidVO
 * @since v1.0
 */
public abstract class ABaseFluidJSONObject extends ABaseFluidVO {

    private static final long serialVersionUID = 1L;
    public static int JSON_INDENT_FACTOR = 2;

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
    public ABaseFluidJSONObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidJSONObject(JsonObject jsonObjectParam) {
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
                throw new IllegalArgumentException(
                        "Unable to parse Field '" + JSONMapping.ID + "'.");
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
     * @param jsonObject The JSON object containing the property.
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
     * @param jsonObject The JSON object containing the property to retrieve.
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
     * @param jsonObject The JSON object containing the property to retrieve.
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
     * Checks if the specified property in the given JSON object is a numeric value.
     *
     * @param jsonObject The JSON object containing the property to check.
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
     * @param jsonObject The JSON object containing the property to check.
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
     * @param jsonObject The JSON object containing the property to check.
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
     *         If the input string is invalid or not properly formatted JSON,
     *         an exception may be thrown during parsing.
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
        returnVal.addProperty(JSONMapping.ID, this.getId());
        returnVal.addProperty(JSONMapping.SERVICE_TICKET, this.getServiceTicket());
        returnVal.addProperty(JSONMapping.REQUEST_UUID, this.getRequestUuid());
        returnVal.addProperty(JSONMapping.ECHO, this.getEcho());
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
        if (UtilGlobal.isBlank(fieldNameParam) || this.isPropertyNull(this.jsonObject, fieldNameParam)) {
            return null;
        }
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
     * @return The local set {@code JSONObject} object.
     */
    @XmlTransient
    @JsonIgnore
    public JsonObject getJSONObject() {
        return this.jsonObject;
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
