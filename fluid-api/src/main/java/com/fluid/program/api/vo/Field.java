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

package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.util.UtilGlobal;
import com.fluid.program.api.util.elasticsearch.exception.FluidElasticSearchException;

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

    private String fieldName;
    private Object fieldValue;
    private String fieldDescription;

    private String type;
    private String typeMetaData;

    private static final String LATITUDE_AND_LONGITUDE = "Latitude and Longitude";

    /**
     * The JSON mapping for the {@code Field} object.
     */
    public static class JSONMapping
    {
        public static final String FIELD_NAME= "fieldName";
        public static final String FIELD_DESCRIPTION = "fieldDescription";
        public static final String FIELD_VALUE = "fieldValue";
        public static final String TYPE = "type";
        public static final String TYPE_META_DATA = "typeMetaData";

        /**
         * The JSON mapping for Elastic Search data types.
         * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
         */
        public static final class Elastic
        {
            public static final String TYPE = "type";
        }
    }

    /**
     * <p>
     *     The types of Fields and Value type mapping is;
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
     *             <td>{@code com.fluid.program.api.vo.TableField}</td>
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
        //Table field only supported by Database (Memcached statement)....
        Table
    }

    /**
     * The field type for Elastic Search.
     * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}
     */
    public static class ElasticSearchType
    {
        //String...
        public static final String TEXT = "text";
        public static final String KEYWORD = "keyword";

        //Numeric...
        public static final String DOUBLE = "double";
        public static final String LONG = "long";

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
    }

    /**
     * Constructor to set the Id, Field Name, Value and Type.
     *
     * @param fieldIdParam Field Id.
     * @param fieldNameParam Sets Field Name.
     * @param fieldValueParam Sets Field Value.
     * @param fieldTypeParam Sets Field Type.
     */
    public Field(Long fieldIdParam,
                 String fieldNameParam,
                 Object fieldValueParam,
                 Type fieldTypeParam) {
        this(fieldIdParam);

        this.setFieldName(fieldNameParam);
        this.setFieldValue(fieldValueParam);
        this.setTypeAsEnum(fieldTypeParam);
    }

    /**
     * Sets the Id associated with a Field.
     *
     * @param fieldIdParam Field Id.
     */
    public Field(Long fieldIdParam) {
        super();

        this.setId(fieldIdParam);
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
        this.setFieldValue(fieldValueParam);
        this.setTypeAsEnum(fieldTypeParam);
    }

    /**
     * Constructor to set the Field Name, Value.
     *
     * @param fieldNameParam Sets Field Name.
     * @param fieldValueParam Sets Field Value.
     */
    public Field(String fieldNameParam, Object fieldValueParam) {
        this.setFieldName(fieldNameParam);
        this.type = null;
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
        this.setFieldValue(multiChoiceParam);
        this.setTypeAsEnum(Field.Type.MultipleChoice);
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

        //Type...
        if (!this.jsonObject.isNull(JSONMapping.TYPE)) {
            this.setType(this.jsonObject.getString(JSONMapping.TYPE));
        }

        //Meta Data...
        if (!this.jsonObject.isNull(JSONMapping.TYPE_META_DATA)) {
            this.setTypeMetaData(this.jsonObject.getString(JSONMapping.TYPE_META_DATA));
        }

        //Field Value...
        if (!this.jsonObject.isNull(JSONMapping.FIELD_VALUE)) {

            Object objFromKey = this.jsonObject.get(JSONMapping.FIELD_VALUE);

            if(objFromKey instanceof JSONObject)
            {
                JSONObject jsonObject = this.jsonObject.getJSONObject(JSONMapping.FIELD_VALUE);

                //Multi Choices Selected Multi Choices...
                if(jsonObject.has(MultiChoice.JSONMapping.SELECTED_MULTI_CHOICES) ||
                        jsonObject.has(MultiChoice.JSONMapping.SELECTED_CHOICES))
                {
                    this.setFieldValue(new MultiChoice(jsonObject));
                }
                //Table Field...
                else if(jsonObject.has(TableField.JSONMapping.TABLE_RECORDS))
                {
                    this.setFieldValue(new TableField(jsonObject));
                }
            }
            else if(objFromKey instanceof Long)
            {
                Long castedLong = this.jsonObject.getLong(JSONMapping.FIELD_VALUE);

                if(this.getTypeAsEnum() != null && this.getTypeAsEnum() == Type.DateTime)
                {
                    this.setFieldValue(new Date(castedLong));
                }
                else
                {
                    this.setFieldValue(castedLong);
                }
            }
            else if(objFromKey instanceof Number)
            {
                this.setFieldValue(this.jsonObject.getLong(JSONMapping.FIELD_VALUE));
            }
            else if(objFromKey instanceof Boolean)
            {
                this.setFieldValue(this.jsonObject.getBoolean(JSONMapping.FIELD_VALUE));
            }
            else
            {
                this.setFieldValue(this.jsonObject.getString(JSONMapping.FIELD_VALUE));
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
     * Converts the {@code getFieldName} to upper_camel_case.
     *
     * @return {@code getFieldName()} as upper_camel_case.
     */
    public String getFieldNameAsUpperCamel()
    {
        return new UtilGlobal().toCamelUpperCase(this.getFieldName());
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
     * Sets the value of {@code this} {@code Field}.
     *
     * @param fieldValueParam The New Field Value.
     *
     * @see Type
     */
    public void setFieldValue(Object fieldValueParam) {
        this.fieldValue = fieldValueParam;

        if(this.getType() == null && fieldValueParam != null)
        {
            //Date...
            if(fieldValueParam instanceof Date)
            {
                this.setTypeAsEnum(Type.DateTime);
            }
            //Number...
            else if(fieldValueParam instanceof Number)
            {
                this.setTypeAsEnum(Type.Decimal);
            }
            //MultiChoice...
            else if(fieldValueParam instanceof MultiChoice)
            {
                this.setTypeAsEnum(Type.MultipleChoice);
            }
            //Table Field...
            else if(fieldValueParam instanceof TableField)
            {
                this.setTypeAsEnum(Type.Table);
            }
            //Text...
            else if(fieldValueParam instanceof String)
            {
                this.setTypeAsEnum(Type.Text);
            }
            //Boolean...
            else if(fieldValueParam instanceof Boolean)
            {
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
    public String getType() {
        return this.type;
    }

    /**
     * Sets the Type of {@code this} {@code Field} as {@code String}.
     *
     * @param typeParam The Field type.
     *
     * @see Type
     */
    public void setType(String typeParam) {
        this.type = typeParam;
    }

    /**
     * Sets the Type of {@code this} {@code Field} as {@code enum}.
     *
     * @param typeParam The Field type.
     *
     * @see Type
     */
    @XmlTransient
    public void setTypeAsEnum(Type typeParam) {

        if(typeParam == null)
        {
            this.type = null;
            return;
        }

        this.type = typeParam.name();
    }

    /**
     * Gets the Type of {@code this} {@code Field} as {@code enum}.
     *
     * @return The Field type.
     *
     * @see Type
     */
    @XmlTransient
    public Type getTypeAsEnum()
    {
        if(this.getType() == null || this.getType().trim().isEmpty())
        {
            return null;
        }

        return Type.valueOf(this.getType());
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
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Field Name...
        if(this.getFieldName() != null)
        {
            returnVal.put(JSONMapping.FIELD_NAME,this.getFieldName());
        }

        //Field Description...
        if(this.getFieldDescription() != null)
        {
            returnVal.put(JSONMapping.FIELD_DESCRIPTION,this.getFieldDescription());
        }

        //Field Value...
        if(this.getFieldValue() != null)
        {
            //Text...
            if(this.getFieldValue() instanceof String)
            {
                returnVal.put(JSONMapping.FIELD_VALUE, this.getFieldValue());
            }
            //Decimal...
            else if(this.getFieldValue() instanceof Number)
            {
                returnVal.put(JSONMapping.FIELD_VALUE,
                        ((Number)this.getFieldValue()).doubleValue());
            }
            //True False...
            else if(this.getFieldValue() instanceof Boolean)
            {
                returnVal.put(JSONMapping.FIELD_VALUE,
                        (Boolean)this.getFieldValue());
            }
            //Date Time...
            else if(this.getFieldValue() instanceof Date)
            {
                returnVal.put(JSONMapping.FIELD_VALUE,
                        this.getDateAsLongFromJson((Date)this.getFieldValue()));
            }
            //Multi Choice...
            else if(this.getFieldValue() instanceof MultiChoice)
            {
                returnVal.put(JSONMapping.FIELD_VALUE,
                        ((MultiChoice)this.getFieldValue()).toJsonObject());
            }
            //Table Field...
            else if(this.getFieldValue() instanceof TableField)
            {
                returnVal.put(JSONMapping.FIELD_VALUE,
                        ((TableField)this.getFieldValue()).toJsonObject());
            }
            else
            {
                returnVal.put(JSONMapping.FIELD_VALUE, this.getFieldValue());
            }
        }

        //Type...
        if(this.getType() != null)
        {
            returnVal.put(JSONMapping.TYPE, this.getType());
        }

        //Type Meta Data...
        if(this.getTypeMetaData() != null)
        {
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
    public JSONObject toJsonMappingForElasticSearch() throws JSONException {

        String fieldNameUpperCamel = this.getFieldNameAsUpperCamel();
        if(fieldNameUpperCamel == null)
        {
            return null;
        }

        String elasticType = this.getElasticSearchFieldType();
        if(elasticType == null)
        {
            return null;
        }

        JSONObject returnVal = new JSONObject();

        returnVal.put(JSONMapping.Elastic.TYPE, elasticType);

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
    public JSONObject toJsonForElasticSearch() throws JSONException {

        if(!this.doesFieldQualifyForElasticSearchInsert())
        {
            return null;
        }

        JSONObject returnVal = new JSONObject();

        String fieldIdAsString = this.getFieldNameAsUpperCamel();
        Object fieldValue = this.getFieldValue();

        //Table Field...
        if(fieldValue instanceof TableField)
        {
            TableField tableField = (TableField)this.getFieldValue();

            if(tableField.getTableRecords() != null &&
                    !tableField.getTableRecords().isEmpty())
            {
                JSONArray array = new JSONArray();
                for(Form record : tableField.getTableRecords())
                {
                    if(record.getId() == null)
                    {
                        continue;
                    }

                    array.put(record.getId());
                }

                returnVal.put(fieldIdAsString, array);
            }
        }
        //Multiple Choice...
        else if(fieldValue instanceof MultiChoice)
        {
            MultiChoice multiChoice = (MultiChoice)this.getFieldValue();

            if(multiChoice.getSelectedMultiChoices() != null &&
                    !multiChoice.getSelectedMultiChoices().isEmpty())
            {
                JSONArray array = new JSONArray();

                for(String selectedChoice : multiChoice.getSelectedMultiChoices())
                {
                    Long selectedChoiceAsLong = null;

                    try{
                        if(!selectedChoice.isEmpty() &&
                                Character.isDigit(selectedChoice.charAt(0)))
                        {
                            selectedChoiceAsLong = Long.parseLong(selectedChoice);
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        selectedChoiceAsLong = null;
                    }

                    //When not long, store as is...
                    if(selectedChoiceAsLong == null)
                    {
                        array.put(selectedChoice);
                    }
                    else
                    {
                        array.put(selectedChoiceAsLong.longValue());
                    }
                }

                returnVal.put(fieldIdAsString, array);
            }
        }
        //Other valid types...
        else if((fieldValue instanceof Number || fieldValue instanceof Boolean) ||
                fieldValue instanceof String)
        {
            if((fieldValue instanceof String) &&
                    LATITUDE_AND_LONGITUDE.equals(this.getTypeMetaData()))
            {
                String formFieldValueStr = fieldValue.toString();

                UtilGlobal utilGlobal = new UtilGlobal();

                double latitude = utilGlobal.getLatitudeFromFluidText(formFieldValueStr);
                double longitude = utilGlobal.getLongitudeFromFluidText(formFieldValueStr);

                fieldValue = (latitude + UtilGlobal.COMMA + longitude);
            }

            returnVal.put(fieldIdAsString, fieldValue);
        }
        //Date...
        else if(fieldValue instanceof Date)
        {
            returnVal.put(fieldIdAsString, ((Date)fieldValue).getTime());
        }
        //Problem
        else {
            throw new FluidElasticSearchException(
                    "Field Value of type '"+fieldValue.getClass().getSimpleName()
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
    public Field populateFromElasticSearchJson(
            JSONObject jsonObjectParam) throws JSONException {

        if(this.getFieldNameAsUpperCamel() == null)
        {
            return null;
        }

        String fieldIdAsString = this.getFieldNameAsUpperCamel();

        if(jsonObjectParam.isNull(fieldIdAsString))
        {
            return null;
        }

        Field.Type type;
        if((type = this.getTypeAsEnum()) == null)
        {
            return null;
        }

        Object formFieldValue = jsonObjectParam.get(fieldIdAsString);

        Field fieldToAdd = null;

        switch (type)
        {
            case DateTime:
                if(formFieldValue instanceof Long)
                {
                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            new Date(((Long)formFieldValue).longValue()),
                            type);
                }
                break;
            case Decimal:
                if(formFieldValue instanceof Number)
                {
                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            ((Number)formFieldValue).doubleValue(),
                            type);
                }
                break;
            case MultipleChoice:
                if(formFieldValue instanceof JSONArray)
                {
                    JSONArray casted = (JSONArray)formFieldValue;
                    List<String> selectedChoices = new ArrayList();
                    for(int index = 0;index < casted.length();index++)
                    {
                        selectedChoices.add(casted.get(index).toString());
                    }

                    if(selectedChoices.isEmpty())
                    {
                        return null;
                    }

                    MultiChoice multiChoiceToSet = new MultiChoice(selectedChoices);

                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            multiChoiceToSet,
                            type);
                }
                break;
            case Table:
                if(formFieldValue instanceof JSONArray)
                {
                    JSONArray casted = (JSONArray)formFieldValue;
                    List<Form> tableRecords = new ArrayList();
                    for(int index = 0;index < casted.length();index++)
                    {
                        Object obAtIndex = casted.get(index);

                        if(obAtIndex instanceof Number)
                        {
                            tableRecords.add(new Form(((Number)obAtIndex).longValue()));
                        }
                    }

                    if(tableRecords.isEmpty())
                    {
                        return null;
                    }

                    TableField tableField = new TableField();
                    tableField.setTableRecords(tableRecords);

                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            tableField,
                            type);
                }
                break;
            case Text:
            case ParagraphText:
                if(formFieldValue instanceof String)
                {
                    //Latitude and Longitude storage...
                    if(LATITUDE_AND_LONGITUDE.equals(this.getTypeMetaData()))
                    {
                        String formFieldValueStr = formFieldValue.toString();

                        UtilGlobal utilGlobal = new UtilGlobal();

                        double latitude = utilGlobal.getLatitudeFromElasticSearchText(formFieldValueStr);
                        double longitude = utilGlobal.getLongitudeFromElasticSearchText(formFieldValueStr);

                        String newFieldVal =
                                (latitude + UtilGlobal.PIPE + longitude + UtilGlobal.PIPE);

                        fieldToAdd = new Field(
                                this.getId(),
                                this.getFieldName(),
                                newFieldVal,
                                type);
                    }
                    //Other...
                    else {
                        fieldToAdd = new Field(
                                this.getId(),
                                this.getFieldName(),
                                formFieldValue.toString(),
                                type);
                    }
                }
            case TrueFalse:
                if(formFieldValue instanceof Boolean)
                {
                    fieldToAdd = new Field(
                            this.getId(),
                            this.getFieldName(),
                            formFieldValue,
                            type);
                }
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
    public void populateFromElasticSearchJson(
            JSONObject jsonObjectParam, List<Field> formFieldsParam) throws JSONException {

        throw new FluidElasticSearchException(
                "Method not implemented. Make use of other ");
    }

    /**
     * Returns the ElasticSearch equivalent data type from the Fluid datatype.
     *
     * @return ElasticSearch type.
     *
     * @see ElasticSearchType
     */
    @XmlTransient
    public String getElasticSearchFieldType()
    {
        Type fieldType = this.getTypeAsEnum();

        //Get the type by Fluid field type...
        switch (fieldType)
        {
            case ParagraphText:
                return ElasticSearchType.TEXT;
            case Text:

                String metaData = this.getTypeMetaData();
                if(metaData == null || metaData.isEmpty())
                {
                    return ElasticSearchType.TEXT;
                }

                if(LATITUDE_AND_LONGITUDE.equals(metaData))
                {
                    return ElasticSearchType.GEO_POINT;
                }

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
     * Checks whether the provided {@code fieldParam} qualifies for
     * insert into Elastic Search.
     *
     * @return Whether the Field Qualifies.
     */
    private boolean doesFieldQualifyForElasticSearchInsert()
    {
        //Test Value...
        Field.Type fieldType;
        if(((this.getFieldValue()) == null) ||
                ((fieldType = this.getTypeAsEnum()) == null))
        {
            return false;
        }

        //Test the Name...
        if(this.getFieldName() == null ||
                this.getFieldName().trim().isEmpty())
        {
            return false;
        }

        //Confirm the type is supported...
        switch (fieldType){

            case DateTime:
            case Decimal:
            case MultipleChoice:
            case Table:
            case Text:
            case TrueFalse:
                return true;
            default:
                return false;
        }
    }
}
