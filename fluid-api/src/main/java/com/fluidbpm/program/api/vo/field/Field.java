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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.GeoUtil;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluidbpm.program.api.vo.ABaseFluidElasticSearchJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.FluidJSONException;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;

/**
 * Represents a Fluid Field for Form, User, Route and Global.
 * {@code Field} can be part of Electronic Form or Form Definition in Fluid.
 *
 * @author jasonbruwer
 * @see Form
 * @see FluidItem
 * @see ABaseFluidElasticSearchJSONObject
 * @since v1.0
 */
public class Field extends ABaseFluidElasticSearchJSONObject {
    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @SerializedName(JSONMapping.FIELD_NAME)
    private String fieldName;

    @Getter
    private Object fieldValue;

    @Setter
    @Getter
    private String fieldDescription;

    @Setter
    @Getter
    private String fieldType;

    @Getter
    private String typeMetaData;
    private DecimalMetaFormat decimalMetaFormat;

    public static final String LATITUDE_AND_LONGITUDE = "Latitude and Longitude";
    public static final String PLAIN_KEYWORD = "Plain Keyword";
    private static String TEMPLATE_DECIMAL_FORMAT = "###,###,###,##0.";
    private static String DEF_DECIMAL_FORMAT = TEMPLATE_DECIMAL_FORMAT.concat("00");

    private static final String LEFT_SQ_BRACKET = "[";
    private static final String RIGHT_SQ_BRACKET = "]";

    /**
     * The JSON mapping for the {@code Field} object.
     */
    public static class JSONMapping {
        public static final String FIELD_NAME = "fieldName";
        public static final String FIELD_DESCRIPTION = "fieldDescription";
        public static final String FIELD_VALUE = "fieldValue";
        public static final String FIELD_TYPE = "fieldType";
        public static final String TYPE_META_DATA = "typeMetaData";

        /**
         * The JSON mapping for Elasticsearch data types.
         * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
         */
        public static final class Elastic {
            public static final String FIELD_TYPE = "fieldType";

            //Used only for mapping types in ES...
            public static final String MAPPING_ONLY_TYPE = "type";
        }
    }

    /**
     * <p>
     * The types of Fields and Value fieldType mapping is;
     *
     * <table>
     *     <caption>Field Fluid vs Java mapping</caption>
     *     <tr>
     *         <th>Field Type in Fluid</th>
     *         <th>Field Type value in Java</th>
     *     </tr>
     *     <tr>
     *         <td>{@code Text}</td>
     *         <td>{@code String}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code TrueFalse}</td>
     *         <td>{@code Boolean}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code DateTime}</td>
     *         <td>{@code Date}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code Decimal}</td>
     *         <td>{@code java.lang.Double}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code MultipleChoice}</td>
     *         <td>{@code MultiChoice}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code ParagraphText}</td>
     *         <td>{@code String}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code Table}</td>
     *         <td>{@code com.fluidbpm.program.api.vo.field.TableField}</td>
     *     </tr>
     * </table>
     */
    public static enum Type {
        Text,
        TextEncrypted,
        TrueFalse,
        DateTime,
        Decimal,
        MultipleChoice,
        ParagraphText,
        //Table field only supported by Database (Memcached statement)...
        Table,
        //Only for visual purposes...
        Label
    }

    /**
     * The field fieldType for Elasticsearch.
     * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}
     */
    public static class ElasticSearchType {
        //String...
        public static final String TEXT = "text";
        public static final String KEYWORD = "keyword";
        public static final String IP = "ip";

        //Numeric...
        public static final String DOUBLE = "double";
        public static final String LONG = "long";
        public static final String INTEGER = "integer";

        //Date...
        public static final String DATE = "date";

        //Boolean...
        public static final String BOOLEAN = "boolean";

        //Binary...
        public static final String BINARY = "binary";

        //Geo...
        public static final String GEO_POINT = "geo_point";//41.12,-71.34 (Lat, Lon)

        //Nested...
        public static final String NESTED = "nested";

        //Object...
        public static final String OBJECT = "object";
    }

    /**
     * Default constructor.
     */
    public Field() {
        super();
    }

    /**
     * Constructor to set the Id, Field Name, Value and Type.
     *
     * @param fieldId    Field Id.
     * @param fieldName  Sets Field Name.
     * @param fieldValue Sets Field Value.
     * @param fieldType  Sets Field Type.
     */
    public Field(Long fieldId, String fieldName, Object fieldValue, Type fieldType) {
        this(fieldId);
        this.setFieldName(fieldName);
        this.setTypeAsEnum(fieldType);
        this.setFieldValue(fieldValue);
    }

    /**
     * Sets the Id and name associated with a Field.
     *
     * @param fieldId   Field Id.
     * @param fieldName Field Name.
     */
    public Field(Long fieldId, String fieldName) {
        super();
        this.setId(fieldId);
        this.setFieldName(fieldName);
    }

    /**
     * Sets the Id associated with a Field.
     *
     * @param fieldId Field Id.
     */
    public Field(Long fieldId) {
        super();
        this.setId(fieldId);
    }

    /**
     * Sets the Name associated with a Field.
     *
     * @param fieldName Field Name.
     */
    public Field(String fieldName) {
        super();
        this.setFieldName(fieldName);
    }

    /**
     * Constructor to set the Field Name, Value and Type.
     *
     * @param fieldName  Sets Field Name.
     * @param fieldValue Sets Field Value.
     * @param fieldType  Sets Field Type.
     */
    public Field(String fieldName, Object fieldValue, Type fieldType) {
        this.setFieldName(fieldName);
        this.setTypeAsEnum(fieldType);
        this.setFieldValue(fieldValue);
    }

    /**
     * Constructor to set the Field Name, Value.
     *
     * @param fieldName  Sets Field Name.
     * @param fieldValue Sets Field Value.
     */
    public Field(String fieldName, Object fieldValue) {
        this.setFieldName(fieldName);
        this.fieldType = null;
        this.typeMetaData = null;
        this.setFieldValue(fieldValue);
    }

    /**
     * Constructor to set the Field Name and {@code MultiChoice} Value.
     *
     * @param fieldName   Sets Field Name.
     * @param multiChoice Sets Field Value as {@code MultiChoice}.
     */
    public Field(String fieldName, MultiChoice multiChoice) {
        this.setFieldName(fieldName);
        this.setTypeAsEnum(Field.Type.MultipleChoice);
        this.setFieldValue(multiChoice);
    }

    protected Field(Field toClone) {
        if (toClone == null) return;

        this.setId(toClone.getId());
        this.fieldName = toClone.fieldName;
        this.fieldDescription = toClone.fieldDescription;
        this.fieldType = toClone.fieldType;
        this.setTypeMetaData(toClone.typeMetaData);

        if (toClone.fieldValue instanceof String) {
            this.fieldValue = toClone.fieldValue.toString();
        } else if (toClone.fieldValue instanceof Boolean) {
            this.fieldValue = Boolean.valueOf((Boolean) toClone.fieldValue);
        } else if (toClone.fieldValue instanceof Date) {
            this.fieldValue = new Date(((Date) toClone.fieldValue).getTime());
        } else if (toClone.fieldValue instanceof Long) {
            this.fieldValue = Long.valueOf(((Long) toClone.fieldValue).longValue());
        } else if (toClone.fieldValue instanceof Number) {
            this.fieldValue = Double.valueOf(((Number) toClone.fieldValue).doubleValue());
        } else if (toClone.fieldValue instanceof MultiChoice) {
            this.fieldValue = ((MultiChoice) toClone.fieldValue).clone();
        } else if (toClone.fieldValue instanceof TableField) {
            this.fieldValue = ((TableField) toClone.fieldValue).clone();
        } else {
            this.fieldValue = null;
        }
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Field(JsonObject jsonObjectParam) {
        super(jsonObjectParam);

        this.setFieldName(this.getAsStringNullSafe(jsonObjectParam, JSONMapping.FIELD_NAME));
        this.setFieldDescription(this.getAsStringNullSafe(jsonObjectParam, JSONMapping.FIELD_DESCRIPTION));
        this.setFieldType(this.getAsStringNullSafe(jsonObjectParam, JSONMapping.FIELD_TYPE));
        this.setTypeMetaData(this.getAsStringNullSafe(jsonObjectParam, JSONMapping.TYPE_META_DATA));

        //Field Value...
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.FIELD_VALUE)) {
            JsonElement objFromKey = this.jsonObject.get(JSONMapping.FIELD_VALUE);
            String fieldType = this.getFieldType();
            if (UtilGlobal.isBlank(fieldType)) {
                this.setValueBasedOnObjectType(objFromKey);
            } else {
                Type typeEnum = this.getTypeAsEnum();
                this.setValueBasedOnFieldType(typeEnum, objFromKey);
            }
        }
    }

    /**
     * Based on the object type the value will be set.
     *
     * @param objFromKey The object value.
     */
    private void setValueBasedOnObjectType(JsonElement objFromKey) {
        if (objFromKey.isJsonObject()) {
            JsonObject jsonObject = objFromKey.getAsJsonObject();
            //Multi Choices Selected Multi Choices...
            if ((jsonObject.has(MultiChoice.JSONMapping.SELECTED_MULTI_CHOICES) ||
                    jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES)) ||
                    jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES_COMBINED)) {
                this.setFieldValue(new MultiChoice(jsonObject));
            } else if (Type.MultipleChoice.toString().equals(this.getFieldType()) &&
                    (jsonObject.has(MultiChoice.JSONMapping.TYPE) && jsonObject.has(MultiChoice.JSONMapping.VALUE))) {
                //[Payara] mapping for rest...
                String typeVal = jsonObject.get(MultiChoice.JSONMapping.TYPE).getAsString();
                if (MultiChoice.JSONMapping.TYPE_STRING.equals(typeVal)) {
                    this.setFieldValue(new MultiChoice(
                            this.stringAsJsonObject(jsonObject.get(MultiChoice.JSONMapping.VALUE).getAsString())
                    ));
                }
            } else if (jsonObject.has(TableField.JSONMapping.TABLE_RECORDS)) {
                //Table Field...
                this.setFieldValue(new TableField(jsonObject));
            } else if ((jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_MULTI_CHOICES) ||
                    jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES)) ||
                    jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES_COMBINED)) {
                this.setFieldValue(new MultiChoice(jsonObject));
            }
        } else if (this.isPropertyNumber(this.jsonObject, JSONMapping.FIELD_VALUE)) {
            Number castedNumb = objFromKey.getAsNumber();
            if (castedNumb instanceof Double || castedNumb instanceof Float) {
                this.setFieldValue(castedNumb.doubleValue());
            } else {
                if (this.getTypeAsEnum() != null && this.getTypeAsEnum() == Type.DateTime) {
                    this.setFieldValue(new Date(castedNumb.longValue()));
                } else this.setFieldValue(castedNumb.longValue());
            }
        } else if (this.isPropertyBoolean(this.jsonObject, JSONMapping.FIELD_VALUE)) {
            this.setFieldValue(objFromKey.getAsBoolean());
        } else {
            String stringVal = objFromKey.getAsString();
            if (this.getTypeAsEnum() == Type.MultipleChoice && stringVal != null && stringVal.startsWith("{")) {
                this.setFieldValue(new MultiChoice(this.stringAsJsonObject(stringVal)));
            } else {
                this.setFieldValue(stringVal);
            }
        }
    }

    /**
     * Based on the object type the value will be set.
     *
     * @param objFromKey The object value.
     */
    private void setValueBasedOnFieldType(Type fieldType, JsonElement objFromKey) {
        Objects.requireNonNull(fieldType);
        if (objFromKey == null || objFromKey.isJsonNull()) {
            this.setFieldValue(null);
            return;
        }

        JsonElement jsonEl = this.jsonObject.get(JSONMapping.FIELD_VALUE);
        switch (fieldType) {
            case Text:
            case ParagraphText:
            case TextEncrypted:
            case Label:
                if (this.isPropertyString(this.jsonObject, JSONMapping.FIELD_VALUE)) {
                    this.setFieldValue(jsonEl.getAsString());
                } else if (jsonEl.isJsonObject()) {
                    this.setFieldValue(jsonEl.getAsJsonObject().toString());
                } else {
                    throw new IllegalArgumentException(String.format(
                            "Label field value '%s:%s' is not supported.",
                            objFromKey, jsonEl)
                    );
                }
                break;
            case TrueFalse:
                if (this.isPropertyBoolean(this.jsonObject,JSONMapping.FIELD_VALUE)) {
                    this.setFieldValue(jsonEl.getAsBoolean());
                } else if (jsonEl.isJsonObject()) {
                    this.setFieldValue(Boolean.FALSE);
                } else {
                    throw new IllegalArgumentException(String.format(
                            "TrueFalse field value '%s:%s' is not supported.",
                            objFromKey, jsonEl));
                }
                break;
            case DateTime:
                this.setFieldValue(new Date(this.valueAsLongFromNumber(objFromKey)));
                break;
            case Decimal:
                if (jsonEl.isJsonPrimitive() && jsonEl.getAsJsonPrimitive().isNumber()) {
                    this.setFieldValueAsDouble(jsonEl.getAsDouble());
                } else if (jsonEl.isJsonObject()) this.setFieldValue(0.0D);
                else throw new IllegalArgumentException(String.format(
                            "Decimal data type received value '%s' of type '%s'. Not allowed.",
                            objFromKey, objFromKey.getClass()
                    ));
                break;
            case MultipleChoice:
                if (jsonEl.isJsonObject()) {
                    JsonObject jsonObject = jsonEl.getAsJsonObject();
                    //Multi Choices Selected Multi Choices...
                    if ((jsonObject.has(MultiChoice.JSONMapping.SELECTED_MULTI_CHOICES) ||
                            jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES)) ||
                            jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES_COMBINED)) {
                        this.setFieldValue(new MultiChoice(jsonObject));
                    } else if (jsonObject.has(MultiChoice.JSONMapping.TYPE) && jsonObject.has(MultiChoice.JSONMapping.VALUE)) {
                        //[Payara] mapping for rest...
                        String typeVal = this.getAsStringNullSafe(jsonObject,MultiChoice.JSONMapping.TYPE);
                        if (MultiChoice.JSONMapping.TYPE_STRING.equals(typeVal)) {
                            this.setFieldValue(new MultiChoice(
                                    this.stringAsJsonObject(this.getAsStringNullSafe(jsonObject, MultiChoice.JSONMapping.VALUE))
                            ));
                        }
                    } else if ((jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_MULTI_CHOICES) ||
                            jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES)) ||
                            jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES_COMBINED)) {
                        this.setFieldValue(new MultiChoice(jsonObject));
                    }
                } else {
                    if (this.isPropertyString(this.jsonObject, JSONMapping.FIELD_VALUE)) {
                        String stringVal = jsonEl.getAsString();
                        if (stringVal.startsWith("{")) {
                            this.setFieldValue(new MultiChoice(
                                    this.stringAsJsonObject(stringVal))
                            );
                        } else this.setFieldValue(new MultiChoice(stringVal));
                    } else if (this.isPropertyNumber(this.jsonObject, JSONMapping.FIELD_VALUE)) {
                        Number numbVal = jsonEl.getAsNumber();
                        this.setFieldValue(new MultiChoice(String.valueOf(numbVal)));
                    } else {
                        throw new IllegalArgumentException(String.format(
                                "Multi-Choice data type received value '%s'. Unable to extract value.", jsonEl
                        ));
                    }
                }
                break;
            case Table:
                if (objFromKey.isJsonObject()) {
                    this.setFieldValue(new TableField(objFromKey.getAsJsonObject()));
                } else {
                    throw new IllegalArgumentException(String.format(
                            "Table data type received value '%s' of type '%s'. Not allowed.",
                            objFromKey, objFromKey.getClass()
                    ));
                }
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Unsupported data type received value '%s' of data type '%s' and class type '%s'. Not allowed.",
                        objFromKey, fieldType, objFromKey.getClass()
                ));
        }
    }

    /**
     * As long as {@code objFromJson} is a number, the value will be returned as long.
     *
     * @param objFromJson value.
     * @return Long value from {@code objFromJson}.
     */
    private long valueAsLongFromNumber(JsonElement objFromJson) {
        long returnVal = 0;
        if (objFromJson.isJsonPrimitive() && objFromJson.getAsJsonPrimitive().isNumber()) {
            return objFromJson.getAsLong();
        }
        return returnVal;
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code String}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldValueAsString() {
        Object returnObj = this.getFieldValue();
        return (returnObj == null) ? null : returnObj.toString();
    }

    /**
     * Converts the {@code getFieldName} to upper_camel_case.
     *
     * @return {@code getFieldName()} as upper_camel_case.
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldNameAsUpperCamel() {
        return new UtilGlobal().toCamelUpperCase(this.getFieldName());
    }

    /**
     * Extracts the field name from the field description if set.
     * Example of field name from display; [[Field Name]].
     * Example of field name from display; [[Field Name Two]].
     *
     * @return {@code getFieldName()} or 'Display Field Name' extracted from {@code getFieldDescription()}
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldNameDisplayValue() {
        String fieldNameFromDesc = UtilGlobal.extractFieldNameFromText(this.getFieldDescription());
        if (fieldNameFromDesc != null) return fieldNameFromDesc;
        return this.getFieldName();
    }

    /**
     * Removes the field name from the field description if set.
     *
     * @return {@code getFieldName()} or 'Display Field Name' extracted from {@code getFieldDescription()}
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldDescriptionDisplayValue() {
        return UtilGlobal.removeFieldNameFromText(this.getFieldDescription());
    }

    /**
     * Sets the field as double.
     *
     * @param val to set.
     * @see Boolean
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValueAsDouble(Double val) {
        this.setFieldValue(val);
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Double}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Double getFieldValueAsDouble() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        final double doubleValue;
        if (obj instanceof Double) doubleValue = (Double) obj;
        else if (obj instanceof Number) doubleValue = ((Number) obj).doubleValue();
        else return null;

        if (!this.isAmountMinorWithCurrency()) return doubleValue;

        return new BigDecimal(doubleValue)
                .round(MathContext.UNLIMITED)
                .movePointLeft(
                        this.decimalMetaFormat.getAmountCurrency().getDefaultFractionDigits()
                ).doubleValue();
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Long}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Long getFieldValueAsLong() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof Long) return (Long) obj;

        if (obj instanceof Number) return ((Number) obj).longValue();

        return null;
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Integer}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Integer getFieldValueAsInteger() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof Integer) return (Integer) obj;

        if (obj instanceof Number) return ((Number) obj).intValue();

        return null;
    }

    /**
     * Set the field value as integer.
     *
     * @param intVal Set the field value to {@code intVal}.
     */
    public void setFieldValueAsInteger(Integer intVal) {
        this.setFieldValue(intVal);
    }

    /**
     * Set the field value as long.
     *
     * @param longVal Set the field value to {@code longVal}.
     */
    public void setFieldValueAsLong(Long longVal) {
        this.setFieldValue(longVal);
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Number}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Number getFieldValueAsNumber() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof Number) return (Number) obj;

        return null;
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Boolean}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Boolean getFieldValueAsBoolean() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof Boolean) return (Boolean) obj;

        return null;
    }

    /**
     * Sets the field as boolean.
     *
     * @param trueFalse to set.
     * @see Boolean
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValueAsBoolean(Boolean trueFalse) {
        this.setFieldValue(trueFalse);
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code Date}.
     *
     * @return The Field Value.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Date getFieldValueAsDate() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof Date) return (Date) obj;
        else if (obj instanceof Number) return new Date(((Number) obj).longValue());

        return null;
    }

    /**
     * Sets the field as date.
     *
     * @param date to set.
     * @see Date
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValueAsDate(Date date) {
        this.setFieldValue(date);
    }

    /**
     * Sets the field as date.
     *
     * @param instant to set.
     * @see Date
     */
    @XmlTransient
    @JsonIgnore
    public void setFieldValueAsDate(Instant instant) {
        this.setFieldValue(instant);
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code MultiChoice}.
     *
     * @return The Field Value.
     * @see Type
     * @see MultiChoice
     */
    @XmlTransient
    @JsonIgnore
    public MultiChoice getFieldValueAsMultiChoice() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof MultiChoice) return (MultiChoice) obj;

        return null;
    }

    /**
     * Gets the value of {@code this} {@code Field} as a {@code TableField}.
     *
     * @return The Field Value.
     * @see Type
     * @see TableField
     */
    @XmlTransient
    @JsonIgnore
    public TableField getFieldValueAsTableField() {
        Object obj = this.getFieldValue();
        if (obj == null) return null;

        if (obj instanceof TableField) return (TableField) obj;

        return null;
    }

    /**
     * Boolean indicator whether the decimal is currency formatted.
     *
     * @return {@code true} if decimal format and {@code isAmountMinorWithCurrency}.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isAmountMinorWithCurrency() {
        return (this.decimalMetaFormat == null) ? false : this.decimalMetaFormat.isAmountMinorWithCurrency();
    }

    /**
     * Sets the value of the field and returns the updated Field object.
     *
     * @param fieldValue the value to be set for the field
     * @return the updated Field object after setting the value
     */
    @XmlTransient
    @JsonIgnore
    public Field setFieldValueAndReturn(Object fieldValue) {
        this.setFieldValue(fieldValue);
        return this;
    }

    /**
     * Sets the Type of {@code this} {@code Field} as {@code enum}.
     * @param type The Field fieldType.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Field setTypeAsEnumAndReturn(Type type) {
        this.setTypeAsEnum(type);
        return this;
    }

    /**
     * Sets the value of {@code this} {@code Field}.
     *
     * @param fieldValue The New Field Value.
     * @see Type
     */
    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;

        if (this.getFieldType() == null && fieldValue != null) {
            //Date...
            if (fieldValue instanceof Instant) {
                this.setTypeAsEnum(Type.DateTime);
                Instant asInstant = (Instant) fieldValue;
                this.fieldValue = Date.from(asInstant);
            } else if (fieldValue instanceof Date) {
                this.setTypeAsEnum(Type.DateTime);
            } else if (fieldValue instanceof Number) {
                //Number...
                this.setTypeAsEnum(Type.Decimal);
            } else if (fieldValue instanceof MultiChoice) {
                //MultiChoice...
                this.setTypeAsEnum(Type.MultipleChoice);
            } else if (fieldValue instanceof TableField) {
                //Table Field...
                this.setTypeAsEnum(Type.Table);
            } else if (fieldValue instanceof String) {
                //Text...
                this.setTypeAsEnum(Type.Text);
            } else if (fieldValue instanceof Boolean) {
                //Boolean...
                this.setTypeAsEnum(Type.TrueFalse);
            }
        } else if (this.getTypeAsEnum() == Type.MultipleChoice && fieldValue instanceof String) {
            this.fieldValue = new MultiChoice((String) fieldValue);
        }
    }

    /**
     * Sets the Type of {@code this} {@code Field} as {@code enum}.
     *
     * @param typeParam The Field fieldType.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public void setTypeAsEnum(Type typeParam) {
        if (typeParam == null) {
            this.fieldType = null;
            return;
        }
        this.fieldType = typeParam.name();
    }

    /**
     * Gets the Type of {@code this} {@code Field} as {@code enum}.
     *
     * @return The Field fieldType.
     * @see Type
     */
    @XmlTransient
    @JsonIgnore
    public Type getTypeAsEnum() {
        if (this.getFieldType() == null || this.getFieldType().trim().isEmpty()) return null;

        return Type.valueOf(this.getFieldType());
    }

    /**
     * <p>
     * Gets the Meta-Data of {@code this} {@code Field}.
     *
     * <p>
     * Also referred to as Field Type sub-data.
     * This defines the {@code Field} even further.
     *
     * <p>
     * Example: Text field may be {@code Plain}, {@code Masked}, {@code Latitude and Longitude}.
     * <p>
     * In addition, the {@code decimalMetaFormat} will be created from {@code typeMetaDataParam}
     *
     * @param typeMetaData The Field Meta-Data.
     * @see Type
     * @see DecimalMetaFormat#parse(String) The parsing.
     */
    public void setTypeMetaData(String typeMetaData) {
        this.typeMetaData = typeMetaData;
        this.decimalMetaFormat = DecimalMetaFormat.parse(this.typeMetaData);
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Field}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        returnVal.addProperty(JSONMapping.FIELD_NAME, this.getFieldName());
        returnVal.addProperty(JSONMapping.FIELD_DESCRIPTION, this.getFieldDescription());
        
        //Field Value...
        if (this.getFieldValue() != null) {
            //Text...
            if (this.getFieldValue() instanceof String) {
                returnVal.addProperty(JSONMapping.FIELD_VALUE, this.getFieldValueAsString());
            } else if (this.getFieldValue() instanceof Number) {
                //Decimal...
                returnVal.addProperty(JSONMapping.FIELD_VALUE, ((Number) this.getFieldValue()).doubleValue());
            } else if (this.getFieldValue() instanceof Boolean) {
                //True False...
                returnVal.addProperty(JSONMapping.FIELD_VALUE, (Boolean) this.getFieldValue());
            } else if (this.getFieldValue() instanceof Date) {
                //Date Time...
                returnVal.addProperty(JSONMapping.FIELD_VALUE, this.getDateAsLongFromJson((Date) this.getFieldValue()));
            } else if (this.getFieldValue() instanceof MultiChoice) {
                //Multi Choice...
                returnVal.add(JSONMapping.FIELD_VALUE, ((MultiChoice) this.getFieldValue()).toJsonObject());
            } else if (this.getFieldValue() instanceof TableField) {
                //Table Field...
                returnVal.add(JSONMapping.FIELD_VALUE, ((TableField) this.getFieldValue()).toJsonObject());
            } else {
                throw new FluidJSONException(
                        String.format("Object value '%s' of type '%s' is not supported.",
                                this.getFieldValue(),
                                this.getFieldValue().getClass().getName()
                        )
                );
            }
        }
        returnVal.addProperty(JSONMapping.FIELD_TYPE, this.getFieldType());
        returnVal.addProperty(JSONMapping.TYPE_META_DATA, this.getTypeMetaData());
        return returnVal;
    }

    /**
     * Creates the mapping object required by Elasticsearch when making
     * use of enhanced data-types.
     * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
     *
     * @return {@code JSONObject} representation of {@code Field} for
     * ElasticSearch mapping.
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonMappingForElasticSearch() throws JSONException {
        String fieldNameUpperCamel = this.getFieldNameAsUpperCamel();
        if (fieldNameUpperCamel == null) return null;

        String elasticType = this.getElasticSearchFieldType();
        if (elasticType == null) return null;

        JsonObject returnVal = new JsonObject();
        returnVal.addProperty(JSONMapping.Elastic.MAPPING_ONLY_TYPE, elasticType);
        return returnVal;
    }

    /**
     * Conversion to {@code JSONObject} for storage in ElasticSearch.
     *
     * @return {@code JSONObject} representation of {@code Field}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonForElasticSearch() throws JSONException {
        if (!this.doesFieldQualifyForElasticSearchInsert()) return null;

        JsonObject returnVal = new JsonObject();
        String fieldIdAsString = this.getFieldNameAsUpperCamel();
        Object fieldValue = this.getFieldValue();

        //Table Field...
        if (fieldValue instanceof TableField) {
            TableField tableField = (TableField) this.getFieldValue();
            if (tableField.getTableRecords() != null && !tableField.getTableRecords().isEmpty()) {
                JsonArray array = new JsonArray();
                for (Form record : tableField.getTableRecords()) {
                    if (record.getId() == null) continue;
                    array.add(record.getId());
                }
                returnVal.add(fieldIdAsString, array);
            }
        } else if (fieldValue instanceof MultiChoice) {
            //Multiple Choice...
            MultiChoice multiChoice = (MultiChoice) this.getFieldValue();

            if (multiChoice.getSelectedMultiChoices() != null &&
                    !multiChoice.getSelectedMultiChoices().isEmpty()) {
                JsonArray array = new JsonArray();
                for (String selectedChoice : multiChoice.getSelectedMultiChoices()) {
                    Long selectedChoiceAsLong = null;
                    try {
                        if (!selectedChoice.isEmpty() &&
                                Character.isDigit(selectedChoice.charAt(0))) {
                            selectedChoiceAsLong = Long.parseLong(selectedChoice);
                        }
                    } catch (NumberFormatException nfe) {
                        // No need, already null.
                    }
                    //When not long, store as is...
                    if (selectedChoiceAsLong == null) {
                        array.add(selectedChoice);
                    } else array.add(selectedChoiceAsLong);
                }
                returnVal.add(fieldIdAsString, array);
            }
        } else if ((fieldValue instanceof Number || fieldValue instanceof Boolean) || fieldValue instanceof String) {
            //Other valid types...
            if ((fieldValue instanceof String) && LATITUDE_AND_LONGITUDE.equals(this.getTypeMetaData())) {
                GeoUtil geo = new GeoUtil(fieldValue.toString());
                returnVal.addProperty(fieldIdAsString, String.format("%s,%s", geo.getLatitude(), geo.getLongitude()));
            } else if (fieldValue instanceof String) {
                returnVal.addProperty(fieldIdAsString, (String) fieldValue);
            } else if (fieldValue instanceof Boolean) {
                returnVal.addProperty(fieldIdAsString, (Boolean) fieldValue);
            } else if (fieldValue instanceof Number) {
                returnVal.addProperty(fieldIdAsString, (Number) fieldValue);
            }
        } else if (fieldValue instanceof Date) {
            //Date...
            returnVal.addProperty(fieldIdAsString, ((Date) fieldValue).getTime());
        } else {
            //Problem...
            throw new FluidElasticSearchException(
                    "Field Value of field-type '" + fieldValue.getClass().getSimpleName()
                            + "' and Value '" + fieldValue + "' is not supported.");
        }

        return returnVal;
    }

    /**
     * Populate the object based on the ElasticSearch JSON structure.
     *
     * @param jsonObjectParam The JSON object to populate from.
     * @return {@link Field} - The field to be added, if invalid a {@code null} will be returned.
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @XmlTransient
    @JsonIgnore
    public Field populateFromElasticSearchJson(JSONObject jsonObjectParam) throws JSONException {
        if (this.getFieldNameAsUpperCamel() == null) return null;

        String fieldIdAsString = this.getFieldNameAsUpperCamel();
        if (jsonObjectParam.isNull(fieldIdAsString)) return null;

        Field.Type type;
        if ((type = this.getTypeAsEnum()) == null) return null;

        Object formFieldValue = jsonObjectParam.get(fieldIdAsString);
        Field fieldToAdd = null;
        switch (type) {
            case DateTime:
                if (formFieldValue instanceof Number) {
                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            new Date(((Number) formFieldValue).longValue()),
                            type);
                }
                break;
            case Decimal:
                if (formFieldValue instanceof Number) {
                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            ((Number) formFieldValue).doubleValue(),
                            type);
                }
                break;
            case MultipleChoice:
                if (formFieldValue instanceof JSONArray) {
                    JsonArray casted = (JSONArray) formFieldValue;
                    List<String> selectedChoices = new ArrayList<>();
                    for (int index = 0; index < casted.length(); index++) {
                        selectedChoices.add(casted.get(index).toString());
                    }

                    if (selectedChoices.isEmpty()) return null;

                    MultiChoice multiChoiceToSet = new MultiChoice(selectedChoices);

                    fieldToAdd = new Field(this.getId(), this.getFieldName(), multiChoiceToSet, type);
                }
                break;
            case Table:
                List<Form> tableRecords = new ArrayList<>();

                //When array already...
                if (formFieldValue instanceof JSONArray) {
                    JsonArray casted = (JSONArray) formFieldValue;
                    for (int index = 0; index < casted.length(); index++) {
                        Object obAtIndex = casted.get(index);
                        if (obAtIndex instanceof Number) tableRecords.add(new Form(((Number) obAtIndex).longValue()));
                    }
                }
                //When there is only a single number stored...
                else if (formFieldValue instanceof Number) {
                    tableRecords.add(new Form(((Number) formFieldValue).longValue()));
                }

                if (tableRecords.isEmpty()) return null;

                fieldToAdd = new Field(this.getId(), this.getFieldName(), new TableField(tableRecords), type);
                break;
            case Text:
            case TextEncrypted:
            case ParagraphText:
                if (formFieldValue instanceof String) {
                    //Latitude and Longitude storage...
                    if (LATITUDE_AND_LONGITUDE.equals(this.getTypeMetaData())) {
                        GeoUtil geoUtil = new GeoUtil(formFieldValue.toString());
                        fieldToAdd = new Field(this.getId(), this.getFieldName(), geoUtil.toString(), type);
                    } else {
                        fieldToAdd = new Field(this.getId(), this.getFieldName(), formFieldValue.toString(), type);
                    }
                }
                break;
            case TrueFalse:
                if (formFieldValue instanceof Boolean)
                    fieldToAdd = new Field(this.getId(), this.getFieldName(), formFieldValue, type);
                break;
        }
        return fieldToAdd;
    }

    /**
     * Not allowed to call this method.
     *
     * @param jsonObjectParam The JSON object to populate from.
     * @param formFieldsParam The Form Fields to use.
     * @throws JSONException               Never.
     * @throws FluidElasticSearchException Always.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public void populateFromElasticSearchJson(
            JsonObject jsonObjectParam,
            List<Field> formFieldsParam
    ) throws JSONException {
        throw new FluidElasticSearchException(
                "Method not implemented. Make use of 'populateFromElasticSearchJson(JSONObject jsonObjectParam)' method.");
    }

    /**
     * Returns the Elasticsearch equivalent data field-type from the Fluid datatype.
     *
     * @return Elasticsearch field-type.
     * @see ElasticSearchType
     */
    @XmlTransient
    @JsonIgnore
    public String getElasticSearchFieldType() {
        Type fieldType = this.getTypeAsEnum();
        if (fieldType == null) return null;

        //Get the fieldType by Fluid field fieldType...
        switch (fieldType) {
            case ParagraphText:
                return ElasticSearchType.TEXT;
            case Text:
                String metaData = this.getTypeMetaData();
                if (metaData == null || metaData.isEmpty()) return ElasticSearchType.TEXT;

                if (LATITUDE_AND_LONGITUDE.equals(metaData)) return ElasticSearchType.GEO_POINT;

                if (PLAIN_KEYWORD.equals(metaData)) return ElasticSearchType.KEYWORD;

                return ElasticSearchType.TEXT;
            case TrueFalse:
                return ElasticSearchType.BOOLEAN;
            case DateTime:
                return ElasticSearchType.DATE;
            case Decimal:
                return ElasticSearchType.DOUBLE;
            case MultipleChoice:
                return ElasticSearchType.KEYWORD;
        }
        return null;
    }

    /**
     * @return Copy of {@code this}.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public Field clone() {
        return new Field(this);
    }

    /**
     * @return Decimal Meta Format.
     */
    @XmlTransient
    @JsonIgnore
    public DecimalMetaFormat getDecimalMetaFormat() {
        return this.decimalMetaFormat;
    }

    /**
     * Retrieve the decimal format based on {code decimalMetaFormat}.
     *
     * @return The decimal format.
     */
    @XmlTransient
    @JsonIgnore
    public String getDecimalFormat() {
        if (this.getDecimalMetaFormat() == null) return DEF_DECIMAL_FORMAT;

        DecimalMetaFormat dmf = this.getDecimalMetaFormat();
        Currency currencyFromPrefix = dmf.getAmountCurrency();
        if (currencyFromPrefix == null) return DEF_DECIMAL_FORMAT;

        String txtVal = String.format("%1$" + currencyFromPrefix.getDefaultFractionDigits() + "s", "0").replace(' ', '0');
        txtVal = String.format("%s%s", TEMPLATE_DECIMAL_FORMAT, txtVal);
        return txtVal;
    }

    /**
     * Applies the decimal format to {@code getFieldValueAsDouble}.
     *
     * @return Double value formatted as {@code getDecimalFormat}.
     */
    @XmlTransient
    @JsonIgnore
    public String getFieldValueAsDoubleDecimalFormat() {
        DecimalFormat df = new DecimalFormat(this.getDecimalFormat());

        Double dblVal = this.getFieldValueAsDouble();
        if (dblVal == null) dblVal = 0.0;

        String formatted = df.format(dblVal);

        if (this.decimalMetaFormat != null && UtilGlobal.isNotBlank(this.decimalMetaFormat.getPrefix())) {
            return String.format("%s%s", this.decimalMetaFormat.getPrefix(), formatted);
        }

        return formatted;
    }

    /**
     * Confirm if field values are the same.
     *
     * @param field The field to match against {@code this}
     * @return {@code true} if {@code field.fieldName} and {@code field.fieldValue} matches {@code this}
     */
    @XmlTransient
    @JsonIgnore
    public boolean valueEquals(Field field) {
        if (field == null) return false;

        final String fieldName;
        if (UtilGlobal.isBlank(fieldName = this.getFieldName())) return false;

        String compareFieldName = field.getFieldName();
        if (!fieldName.equals(compareFieldName)) return false;

        Object fieldVal = this.getFieldValue();
        Object compareFieldVal = field.getFieldValue();

        if (fieldVal == null && compareFieldVal == null) return true;
        if (fieldVal == null || compareFieldVal == null) return false;

        if (fieldVal instanceof Number) {
            boolean match = fieldVal.equals(compareFieldVal);
            if (!match && compareFieldVal instanceof Number) {
                double fieldValDbl = ((Number) fieldVal).doubleValue();
                double compareFieldValDbl = ((Number) compareFieldVal).doubleValue();
                return (fieldValDbl == compareFieldValDbl);
            }
            return match;
        } else if (!fieldVal.getClass().isAssignableFrom(compareFieldVal.getClass())) return false;

        if (fieldVal instanceof String) {
            return fieldVal.equals(compareFieldVal);
        } else if (fieldVal instanceof Boolean) {
            return fieldVal.equals(compareFieldVal);
        } else if (fieldVal instanceof Date) {
            return fieldVal.equals(compareFieldVal);
        } else if (fieldVal instanceof MultiChoice) {
            MultiChoice mc = (MultiChoice) fieldVal, compareMc = (MultiChoice) compareFieldVal;
            return mc.selectedEquals(compareMc);
        } else return false;
    }

    /**
     * Verify whether the fieldValue is empty.
     *
     * @return {@code true} if empty, otherwise {@code false}.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFieldValueEmpty() {
        if (this.getFieldValue() == null) return true;

        if (this.getTypeAsEnum() == null) return true;

        switch (this.getTypeAsEnum()) {
            case Text:
            case ParagraphText:
            case TextEncrypted:
                return UtilGlobal.isBlank(this.getFieldValueAsString());
            case Label:
                return false;
            case Table:
                TableField tblField = this.getFieldValueAsTableField();
                return (tblField == null || tblField.isTableRecordsEmpty());
            case TrueFalse:
                return (this.getFieldValueAsBoolean() == null);
            case DateTime:
                return (this.getFieldValueAsDate() == null);
            case Decimal:
                return ((this.getFieldValueAsDouble() == null || this.getFieldValueAsDouble().isNaN()) ||
                        (this.getFieldValueAsInteger() == 0 && this.getFieldValueAsDouble() < 0.0001));
            case MultipleChoice:
                MultiChoice valMulti = this.getFieldValueAsMultiChoice();
                if (valMulti == null) return true;
                return valMulti.isSelectedValuesEmpty();
            default:
                return true;
        }
    }

    /**
     * Return the meta-data without the custom action.
     * Examples:
     * - Plain
     * - Plain with Search
     * - Plain with Search and Scroll
     * - Select Many
     * - Select Many with Search
     *
     * @return Meta-data without the custom action.
     */
    @XmlTransient
    @JsonIgnore
    public String getTypeMetaDataWithoutCustomAction() {
        String typeMetaData = this.getTypeMetaData();
        if (UtilGlobal.isBlank(typeMetaData)) return null;

        int indexBracket = typeMetaData.indexOf(LEFT_SQ_BRACKET);
        if (indexBracket < 0) return typeMetaData;

        return typeMetaData.substring(0, indexBracket);
    }

    /**
     * Return the action to be executed for a multi-choice field that has a custom action enabled.
     * Otherwise {@code null}.
     *
     * @return Custom action or {@code null}
     */
    @XmlTransient
    @JsonIgnore
    public String getTypeMetaDataMultiChoiceCustomAction() {
        String typeMetaData = this.getTypeMetaData();
        if (UtilGlobal.isBlank(typeMetaData)) return null;

        int indexFrom = typeMetaData.indexOf(LEFT_SQ_BRACKET);
        if (indexFrom < 0) return null;

        int indexTo = typeMetaData.lastIndexOf(RIGHT_SQ_BRACKET);
        if (indexTo <= indexFrom) return null;

        return typeMetaData.substring(indexFrom + 1, indexTo);
    }

    /**
     * Verify if type is multi-choice with a custom action.
     *
     * @return {@code true} if a custom action, otherwise {@code false}.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isTypeMetaDataMultiChoiceWithCustomAction() {
        String customAction = this.getTypeMetaDataMultiChoiceCustomAction();
        return !UtilGlobal.isBlank(customAction);
    }

    /**
     * @return {@code true} if instance is {@code MultiChoice}.
     * @see MultiChoice
     */
    @XmlTransient
    @JsonIgnore
    public boolean isValueInstanceTypeMultiChoice() {
        return (this.fieldValue instanceof MultiChoice);
    }

    /**
     * @return {@code true} if instance is {@code String}.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isValueInstanceTypeString() {
        return (this.fieldValue instanceof String);
    }

    /**
     * Checks whether the provided {@code fieldParam} qualifies for
     * insert into Elasticsearch.
     *
     * @return Whether the Field Qualifies.
     */
    private boolean doesFieldQualifyForElasticSearchInsert() {
        //Test Value...
        Field.Type fieldType;
        if (((this.getFieldValue()) == null) || ((fieldType = this.getTypeAsEnum()) == null)) return false;

        //Test the Name...
        if (this.getFieldName() == null || this.getFieldName().trim().isEmpty()) return false;

        //Confirm the fieldType is supported...
        switch (fieldType) {
            case DateTime:
            case Decimal:
            case MultipleChoice:
            case Table:
            case Text:
            case ParagraphText:
            case TrueFalse:
            case TextEncrypted:
                return true;
            default:
                return false;
        }
    }
}
