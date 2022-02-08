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
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an Fluid Field for Form, User, Route and Global.
 *
 * {@code Field} can be part of Electronic Form or Form Definition in Fluid.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Form
 * @see FluidItem
 * @see ABaseFluidElasticSearchJSONObject
 */
public class Field extends ABaseFluidElasticSearchJSONObject {
	public static final long serialVersionUID = 1L;

	private String fieldName;
	private Object fieldValue;
	private String fieldDescription;

	private String fieldType;
	private String typeMetaData;

	public static final String LATITUDE_AND_LONGITUDE = "Latitude and Longitude";
	public static final String PLAIN_KEYWORD = "Plain Keyword";

	/**
	 * The JSON mapping for the {@code Field} object.
	 */
	public static class JSONMapping {
		public static final String FIELD_NAME= "fieldName";
		public static final String FIELD_DESCRIPTION = "fieldDescription";
		public static final String FIELD_VALUE = "fieldValue";
		public static final String FIELD_TYPE = "fieldType";
		public static final String TYPE_META_DATA = "typeMetaData";

		/**
		 * The JSON mapping for Elastic Search data types.
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
	 *     The types of Fields and Value fieldType mapping is;
	 *
	 *     <table>
	 *         <caption>Field Fluid vs Java mapping</caption>
	 *         <tr>
	 *             <th>Field Type in Fluid</th>
	 *             <th>Field Type value in Java</th>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code Text}</td>
	 *             <td>{@code String}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code TrueFalse}</td>
	 *             <td>{@code Boolean}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code DateTime}</td>
	 *             <td>{@code Date}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code Decimal}</td>
	 *             <td>{@code java.lang.Double}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code MultipleChoice}</td>
	 *             <td>{@code MultiChoice}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code ParagraphText}</td>
	 *             <td>{@code String}</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code Table}</td>
	 *             <td>{@code com.fluidbpm.program.api.vo.field.TableField}</td>
	 *         </tr>
	 *     </table>
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
	 * The field fieldType for Elastic Search.
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
	 * Constructor to set the Id, Field Name, Value and Type.
	 *
	 * @param fieldId Field Id.
	 * @param fieldName Sets Field Name.
	 * @param fieldValue Sets Field Value.
	 * @param fieldType Sets Field Type.
	 */
	public Field(
		Long fieldId,
		String fieldName,
		Object fieldValue,
		Type fieldType
	) {
		this(fieldId);
		this.setFieldName(fieldName);
		this.setTypeAsEnum(fieldType);
		this.setFieldValue(fieldValue);
	}

	/**
	 * Sets the Id and name associated with a Field.
	 *
	 * @param fieldId Field Id.
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
	 * @param fieldNameParam Sets Field Name.
	 * @param fieldValueParam Sets Field Value.
	 * @param fieldTypeParam Sets Field Type.
	 */
	public Field(String fieldNameParam, Object fieldValueParam, Type fieldTypeParam) {
		this.setFieldName(fieldNameParam);
		this.setTypeAsEnum(fieldTypeParam);
		this.setFieldValue(fieldValueParam);
	}

	/**
	 * Constructor to set the Field Name, Value.
	 *
	 * @param fieldNameParam Sets Field Name.
	 * @param fieldValueParam Sets Field Value.
	 */
	public Field(String fieldNameParam, Object fieldValueParam) {
		this.setFieldName(fieldNameParam);
		this.fieldType = null;
		this.typeMetaData = null;
		this.setFieldValue(fieldValueParam);
	}

	/**
	 * Constructor to set the Field Name and {@code MultiChoice} Value.
	 *
	 * @param fieldNameParam Sets Field Name.
	 * @param multiChoiceParam Sets Field Value as {@code MultiChoice}.
	 */
	public Field(String fieldNameParam, MultiChoice multiChoiceParam) {
		this.setFieldName(fieldNameParam);
		this.setTypeAsEnum(Field.Type.MultipleChoice);
		this.setFieldValue(multiChoiceParam);
	}
	
	protected Field(Field toClone) {
		if (toClone == null) return;

		this.setId(toClone.getId());
		this.fieldName = toClone.fieldName;
		this.fieldDescription = toClone.fieldDescription;
		this.fieldType = toClone.fieldType;
		this.typeMetaData = toClone.typeMetaData;

		if (toClone.fieldValue instanceof String) {
			this.fieldValue = toClone.fieldValue.toString();
		} else if (toClone.fieldValue instanceof Boolean) {
			this.fieldValue = Boolean.valueOf((Boolean)toClone.fieldValue);
		} else if (toClone.fieldValue instanceof Date) {
			this.fieldValue = new Date(((Date)toClone.fieldValue).getTime());
		} else if (toClone.fieldValue instanceof Long) {
			this.fieldValue = Long.valueOf(((Long)toClone.fieldValue).longValue());
		} else if (toClone.fieldValue instanceof Number) {
			this.fieldValue = Double.valueOf(((Number)toClone.fieldValue).doubleValue());
		} else if (toClone.fieldValue instanceof MultiChoice) {
			this.fieldValue = ((MultiChoice)toClone.fieldValue).clone();
		} else if (toClone.fieldValue instanceof TableField) {
			this.fieldValue = ((TableField)toClone.fieldValue).clone();
		} else {
			this.fieldValue = null;
		}
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public Field(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		//Field Name...
		if (!this.jsonObject.isNull(JSONMapping.FIELD_NAME)) {
			this.setFieldName(this.jsonObject.getString(JSONMapping.FIELD_NAME));
		}

		//Field Description...
		if (!this.jsonObject.isNull(JSONMapping.FIELD_DESCRIPTION)) {
			this.setFieldDescription(this.jsonObject.getString(JSONMapping.FIELD_DESCRIPTION));
		}

		//Field Type...
		if (!this.jsonObject.isNull(JSONMapping.FIELD_TYPE)) {
			this.setFieldType(this.jsonObject.getString(JSONMapping.FIELD_TYPE));
		}

		//Meta Data...
		if (!this.jsonObject.isNull(JSONMapping.TYPE_META_DATA)) {
			this.setTypeMetaData(this.jsonObject.getString(JSONMapping.TYPE_META_DATA));
		}

		//Field Value...
		if (!this.jsonObject.isNull(JSONMapping.FIELD_VALUE)) {
			Object objFromKey = this.jsonObject.get(JSONMapping.FIELD_VALUE);

			if (objFromKey instanceof JSONObject) {
				JSONObject jsonObject = this.jsonObject.getJSONObject(JSONMapping.FIELD_VALUE);

				//Multi Choices Selected Multi Choices...
				if ((jsonObject.has(MultiChoice.JSONMapping.SELECTED_MULTI_CHOICES) ||
						jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES)) ||
						jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES_COMBINED)) {
					this.setFieldValue(new MultiChoice(jsonObject));
				} else if (Type.MultipleChoice.toString().equals(this.getFieldType()) &&
						(jsonObject.has(MultiChoice.JSONMapping.TYPE) && jsonObject.has(MultiChoice.JSONMapping.VALUE))) {
					//[Payara] mapping for rest...
					String typeVal = jsonObject.getString(MultiChoice.JSONMapping.TYPE);
					if (MultiChoice.JSONMapping.TYPE_STRING.equals(typeVal)) {
						this.setFieldValue(new MultiChoice(new JSONObject(jsonObject.getString(MultiChoice.JSONMapping.VALUE))));
					}
				} else if (jsonObject.has(TableField.JSONMapping.TABLE_RECORDS)) {
					//Table Field...
					this.setFieldValue(new TableField(jsonObject));
				} else if ((jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_MULTI_CHOICES) ||
						jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES)) ||
						jsonObject.has(MultiChoice.JSONMapping.AVAILABLE_CHOICES_COMBINED)) {
					this.setFieldValue(new MultiChoice(jsonObject));
				}
			} else if (objFromKey instanceof Long) {
				Long castedLong = this.jsonObject.getLong(JSONMapping.FIELD_VALUE);
				if (this.getTypeAsEnum() != null && this.getTypeAsEnum() == Type.DateTime) {
					this.setFieldValue(new Date(castedLong));
				} else {
					this.setFieldValue(castedLong);
				}
			} else if (objFromKey instanceof Number) {
				try {
					this.setFieldValue(this.jsonObject.getNumber(JSONMapping.FIELD_VALUE));
				} catch (NoSuchMethodError nsm) {
					//For older versions of org.json library...
					this.setFieldValue(this.jsonObject.getLong(JSONMapping.FIELD_VALUE));
				}
			} else if (objFromKey instanceof Boolean) {
				this.setFieldValue(this.jsonObject.getBoolean(JSONMapping.FIELD_VALUE));
			} else {
				String stringVal = this.jsonObject.getString(JSONMapping.FIELD_VALUE);
				if (this.getTypeAsEnum() == Type.MultipleChoice &&
						stringVal != null && stringVal.startsWith("{")) {
					this.setFieldValue(new MultiChoice(new JSONObject(stringVal)));
				} else {
					this.setFieldValue(stringVal);
				}
			}
		}
	}

	/**
	 * Default constructor.
	 */
	public Field() {
		super();
	}

	/**
	 * Gets the name of {@code this} {@code Field}.
	 *
	 * @return The Field Name.
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * Sets the name of {@code this} {@code Field}.
	 *
	 * @param fieldNameParam The New Field Name.
	 */
	public void setFieldName(String fieldNameParam) {
		this.fieldName = fieldNameParam;
	}

	/**
	 * Gets the description of {@code this} {@code Field}.
	 *
	 * @return The Field Description.
	 */
	public String getFieldDescription() {
		return this.fieldDescription;
	}

	/**
	 * Sets the description of {@code this} {@code Field}.
	 *
	 * @param fieldDescriptionParam The Field Description.
	 */
	public void setFieldDescription(String fieldDescriptionParam) {
		this.fieldDescription = fieldDescriptionParam;
	}

	/**
	 * Gets the value of {@code this} {@code Field}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 */
	public Object getFieldValue() {
		return this.fieldValue;
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code String}.
	 *
	 * @return The Field Value.
	 *
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
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Double getFieldValueAsDouble() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Double) return (Double)obj;

		if (obj instanceof Number) return ((Number)obj).doubleValue();

		return null;
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code Long}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Long getFieldValueAsLong() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Long) return (Long)obj;

		if (obj instanceof Number) return ((Number)obj).longValue();

		return null;
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code Integer}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Integer getFieldValueAsInteger() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Integer) return (Integer)obj;

		if (obj instanceof Number) return ((Number)obj).intValue();

		return null;
	}

	/**
	 * Set the field value as integer.
	 * @param intVal Set the field value to {@code intVal}.
	 */
	public void setFieldValueAsInteger(Integer intVal) {
		this.setFieldValue(intVal);
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code Number}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Number getFieldValueAsNumber() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Number) return (Number)obj;

		return null;
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code Boolean}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Boolean getFieldValueAsBoolean() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Boolean) return (Boolean)obj;

		return null;
	}

	/**
	 * Sets the field as boolean.
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
	 *
	 * @see Type
	 */
	@XmlTransient
	@JsonIgnore
	public Date getFieldValueAsDate() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof Date) {
			return (Date)obj;
		} else if (obj instanceof Number) {
			Number longValue = (Number)obj;
			if (longValue.longValue() > 0) return new Date(longValue.longValue());
		}
		return null;
	}

	/**
	 * Sets the field as date.
	 * @param date to set.
	 * @see Date
	 */
	@XmlTransient
	@JsonIgnore
	public void setFieldValueAsDate(Date date) {
		this.setFieldValue(date);
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code MultiChoice}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 * @see MultiChoice
	 */
	@XmlTransient
	@JsonIgnore
	public MultiChoice getFieldValueAsMultiChoice() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof MultiChoice) return (MultiChoice)obj;

		return null;
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code TableField}.
	 *
	 * @return The Field Value.
	 *
	 * @see Type
	 * @see TableField
	 */
	@XmlTransient
	@JsonIgnore
	public TableField getFieldValueAsTableField() {
		Object obj = this.getFieldValue();
		if (obj == null) return null;

		if (obj instanceof TableField) return (TableField)obj;

		return null;
	}

	/**
	 * Sets the value of {@code this} {@code Field}.
	 *
	 * @param fieldValueParam The New Field Value.
	 *
	 * @see Type
	 */
	public void setFieldValue(Object fieldValueParam) {
		this.fieldValue = fieldValueParam;

		if (this.getFieldType() == null && fieldValueParam != null) {
			//Date...
			if (fieldValueParam instanceof Date) {
				this.setTypeAsEnum(Type.DateTime);
			} else if (fieldValueParam instanceof Number) {
				//Number...
				this.setTypeAsEnum(Type.Decimal);
			} else if (fieldValueParam instanceof MultiChoice) {
				//MultiChoice...
				this.setTypeAsEnum(Type.MultipleChoice);
			} else if (fieldValueParam instanceof TableField) {
				//Table Field...
				this.setTypeAsEnum(Type.Table);
			} else if (fieldValueParam instanceof String) {
				//Text...
				this.setTypeAsEnum(Type.Text);
			} else if (fieldValueParam instanceof Boolean) {
				//Boolean...
				this.setTypeAsEnum(Type.TrueFalse);
			}
		}
	}

	/**
	 * Gets the Type of {@code this} {@code Field}.
	 *
	 * @return The Field Type.
	 *
	 * @see Type
	 */
	public String getFieldType() {
		return this.fieldType;
	}

	/**
	 * Sets the Type of {@code this} {@code Field} as {@code String}.
	 *
	 * @param fieldTypeParam The Field fieldType.
	 *
	 * @see Type
	 */
	public void setFieldType(String fieldTypeParam) {
		this.fieldType = fieldTypeParam;
	}

	/**
	 * Sets the Type of {@code this} {@code Field} as {@code enum}.
	 *
	 * @param typeParam The Field fieldType.
	 *
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
	 *
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
	 *
	 * @return The Field Meta-Data.
	 *
	 * @see Type
	 */
	public String getTypeMetaData() {
		return this.typeMetaData;
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
	 *
	 * @param typeMetaDataParam The Field Meta-Data.
	 *
	 * @see Type
	 */
	public void setTypeMetaData(String typeMetaDataParam) {
		this.typeMetaData = typeMetaDataParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code Field}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Field Name...
		if (this.getFieldName() != null) returnVal.put(JSONMapping.FIELD_NAME,this.getFieldName());

		//Field Description...
		if (this.getFieldDescription() != null) returnVal.put(JSONMapping.FIELD_DESCRIPTION,this.getFieldDescription());

		//Field Value...
		if (this.getFieldValue() != null) {
			//Text...
			if (this.getFieldValue() instanceof String) {
				returnVal.put(JSONMapping.FIELD_VALUE, this.getFieldValue());
			} else if (this.getFieldValue() instanceof Number) {
				//Decimal...
				returnVal.put(JSONMapping.FIELD_VALUE,
						((Number)this.getFieldValue()).doubleValue());
			} else if (this.getFieldValue() instanceof Boolean) {
				//True False...
				returnVal.put(JSONMapping.FIELD_VALUE,
						(Boolean)this.getFieldValue());
			} else if (this.getFieldValue() instanceof Date) {
				//Date Time...
				returnVal.put(JSONMapping.FIELD_VALUE,
						this.getDateAsObjectFromJson((Date)this.getFieldValue()));
			} else if (this.getFieldValue() instanceof MultiChoice) {
				//Multi Choice...
				returnVal.put(JSONMapping.FIELD_VALUE,
						((MultiChoice)this.getFieldValue()).toJsonObject());
			} else if (this.getFieldValue() instanceof TableField) {
				//Table Field...
				returnVal.put(JSONMapping.FIELD_VALUE,
						((TableField)this.getFieldValue()).toJsonObject());
			} else {
				returnVal.put(JSONMapping.FIELD_VALUE, this.getFieldValue());
			}
		}

		//Type...
		if (this.getFieldType() != null) {
			returnVal.put(JSONMapping.FIELD_TYPE, this.getFieldType());
		}

		//Type Meta Data...
		if (this.getTypeMetaData() != null) {
			returnVal.put(JSONMapping.TYPE_META_DATA,this.getTypeMetaData());
		}

		return returnVal;
	}

	/**
	 * Creates the mapping object required by Elastic Search when making
	 * use of enhanced data-types.
	 *
	 * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
	 *
	 * @return {@code JSONObject} representation of {@code Field} for
	 * ElasticSearch mapping.
	 * @throws JSONException If there is a problem with the JSON Body.
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonMappingForElasticSearch() throws JSONException {
		String fieldNameUpperCamel = this.getFieldNameAsUpperCamel();
		if (fieldNameUpperCamel == null) return null;

		String elasticType = this.getElasticSearchFieldType();
		if (elasticType == null) return null;

		JSONObject returnVal = new JSONObject();
		returnVal.put(JSONMapping.Elastic.MAPPING_ONLY_TYPE, elasticType);
		return returnVal;
	}

	/**
	 * Conversion to {@code JSONObject} for storage in ElasticSearch.
	 *
	 * @return {@code JSONObject} representation of {@code Field}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonForElasticSearch() throws JSONException {
		if (!this.doesFieldQualifyForElasticSearchInsert()) return null;

		JSONObject returnVal = new JSONObject();
		String fieldIdAsString = this.getFieldNameAsUpperCamel();
		Object fieldValue = this.getFieldValue();

		//Table Field...
		if (fieldValue instanceof TableField) {
			TableField tableField = (TableField)this.getFieldValue();
			if (tableField.getTableRecords() != null && !tableField.getTableRecords().isEmpty()) {
				JSONArray array = new JSONArray();
				for (Form record : tableField.getTableRecords()) {
					if (record.getId() == null) continue;

					array.put(record.getId());
				}

				returnVal.put(fieldIdAsString, array);
			}
		} else if (fieldValue instanceof MultiChoice) {
			//Multiple Choice...
			MultiChoice multiChoice = (MultiChoice)this.getFieldValue();

			if (multiChoice.getSelectedMultiChoices() != null &&
					!multiChoice.getSelectedMultiChoices().isEmpty()) {
				JSONArray array = new JSONArray();

				for (String selectedChoice : multiChoice.getSelectedMultiChoices()) {
					Long selectedChoiceAsLong = null;
					try {
						if (!selectedChoice.isEmpty() &&
								Character.isDigit(selectedChoice.charAt(0))) {
							selectedChoiceAsLong = Long.parseLong(selectedChoice);
						}
					} catch (NumberFormatException nfe) {
						selectedChoiceAsLong = null;
					}

					//When not long, store as is...
					if (selectedChoiceAsLong == null) {
						array.put(selectedChoice);
					} else {
						array.put(selectedChoiceAsLong.longValue());
					}
				}

				returnVal.put(fieldIdAsString, array);
			}
		} else if ((fieldValue instanceof Number || fieldValue instanceof Boolean) ||
				fieldValue instanceof String) {
			//Other valid types...
			if ((fieldValue instanceof String) && LATITUDE_AND_LONGITUDE.equals(this.getTypeMetaData())) {
				GeoUtil geo = new GeoUtil(fieldValue.toString());
				fieldValue = String.format("%s,%s", geo.getLatitude(), geo.getLongitude());
			}

			returnVal.put(fieldIdAsString, fieldValue);
		} else if (fieldValue instanceof Date) {
			//Date...
			returnVal.put(fieldIdAsString, ((Date)fieldValue).getTime());
		} else {
			//Problem...
			throw new FluidElasticSearchException(
					"Field Value of field-type '"+fieldValue.getClass().getSimpleName()
							+"' and Value '"+ fieldValue+"' is not supported.");
		}

		return returnVal;
	}

	/**
	 * Populate the object based on the ElasticSearch JSON structure.
	 *
	 * @param jsonObjectParam The JSON object to populate from.
	 * @return {@link Field} - The field to be added, if invalid a {@code null}
	 * will be returned.
	 *
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
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
							new Date(((Number)formFieldValue).longValue()),
							type);
				}
				break;
			case Decimal:
				if (formFieldValue instanceof Number) {
					fieldToAdd = new Field(
							this.getId(),
							this.getFieldName(),
							((Number)formFieldValue).doubleValue(),
							type);
				}
				break;
			case MultipleChoice:
				if (formFieldValue instanceof JSONArray) {
					JSONArray casted = (JSONArray)formFieldValue;
					List<String> selectedChoices = new ArrayList();
					for (int index = 0;index < casted.length();index++) {
						selectedChoices.add(casted.get(index).toString());
					}

					if (selectedChoices.isEmpty()) return null;

					MultiChoice multiChoiceToSet = new MultiChoice(selectedChoices);

					fieldToAdd = new Field(this.getId(), this.getFieldName(), multiChoiceToSet, type);
				}
				break;
			case Table:
				List<Form> tableRecords = new ArrayList();

				//When array already...
				if (formFieldValue instanceof JSONArray) {
					JSONArray casted = (JSONArray)formFieldValue;
					for (int index = 0;index < casted.length();index++) {
						Object obAtIndex = casted.get(index);
						if (obAtIndex instanceof Number) tableRecords.add(new Form(((Number)obAtIndex).longValue()));
					}
				}
				//When there is only a single number stored...
				else if (formFieldValue instanceof Number) {
					tableRecords.add(new Form(((Number)formFieldValue).longValue()));
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
						//Other...
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
	 *
	 * @throws JSONException Never.
	 * @throws FluidElasticSearchException Always.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public void populateFromElasticSearchJson(
		JSONObject jsonObjectParam,
		List<Field> formFieldsParam
	) throws JSONException {
		throw new FluidElasticSearchException(
				"Method not implemented. Make use of 'populateFromElasticSearchJson(JSONObject jsonObjectParam)' method.");
	}

	/**
	 * Returns the ElasticSearch equivalent data field-type from the Fluid datatype.
	 *
	 * @return ElasticSearch field-type.
	 *
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
	 * Confirm if field values are the same.
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
				if (((Number)fieldVal).doubleValue() == 0.0D && ((Number) compareFieldVal).doubleValue() == 0.0D) match = true;
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
			MultiChoice mc = (MultiChoice)fieldVal , compareMc = (MultiChoice)compareFieldVal;
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
			case Label: return false;
			case Table:
				TableField tblField = this.getFieldValueAsTableField();
				return (tblField == null || tblField.isTableRecordsEmpty());
			case TrueFalse:
				return (this.getFieldValueAsBoolean() == null);
			case DateTime:
				return (this.getFieldValueAsDate() == null);
			case Decimal:
				return (this.getFieldValueAsDouble() == null ||
						(this.getFieldValueAsDouble().isNaN() || this.getFieldValueAsDouble().intValue() == 0));
			case MultipleChoice:
				MultiChoice valMulti = this.getFieldValueAsMultiChoice();
				if (valMulti == null) return true;
				return valMulti.isSelectedValuesEmpty();
			default: return true;
		}
	}

	/**
	 * Checks whether the provided {@code fieldParam} qualifies for
	 * insert into Elastic Search.
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
