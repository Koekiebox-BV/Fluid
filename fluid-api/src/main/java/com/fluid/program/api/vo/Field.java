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

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

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
 */
public class Field extends ABaseFluidJSONObject {

    private String fieldName;
    private Object fieldValue;
    private String fieldDescription;

    private String type;
    private String typeMetaData;

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
        TrueFalse,
        DateTime,
        Decimal,
        MultipleChoice,
        ParagraphText,
        //Table field only supported by Database....
        Table
    }

    /**
     *
     * @param fieldIdParam
     */
    public Field(Long fieldIdParam) {
        super();

        this.setId(fieldIdParam);
    }

    /**
     * 
     * @param fieldNameParam
     * @param fieldValueParam
     */
    public Field(String fieldNameParam, Object fieldValueParam, Type fieldTypeParam) {
        this.setFieldName(fieldNameParam);
        this.setFieldValue(fieldValueParam);
        this.setTypeAsEnum(fieldTypeParam);
    }

    /**
     *
     * @param fieldNameParam
     * @param fieldValueParam
     */
    public Field(String fieldNameParam, Object fieldValueParam) {
        this.setFieldName(fieldNameParam);
        this.type = null;
        this.typeMetaData = null;
        this.setFieldValue(fieldValueParam);
    }


    /**
     *
     * @param fieldNameParam
     * @param multiChoiceParam
     */
    public Field(String fieldNameParam,MultiChoice multiChoiceParam) {

        this.setFieldName(fieldNameParam);
        this.setFieldValue(multiChoiceParam);
        this.setTypeAsEnum(Field.Type.MultipleChoice);
    }

    /**
     *
     * @param jsonObjectParam
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

        //Field Value...
        if (!this.jsonObject.isNull(JSONMapping.FIELD_VALUE)) {

            Object objFromKey = this.jsonObject.get(JSONMapping.FIELD_VALUE);

            if(objFromKey instanceof JSONObject)
            {
                JSONObject jsonObject = this.jsonObject.getJSONObject(JSONMapping.FIELD_VALUE);
                if(jsonObject.has(MultiChoice.JSONMapping.SELECTED_MULTI_CHOICES))
                {
                    this.setFieldValue(new MultiChoice(jsonObject));
                }
            }
            else if(objFromKey instanceof Long)
            {
                this.setFieldValue(this.jsonObject.getLong(JSONMapping.FIELD_VALUE));
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

        //Type...
        if (!this.jsonObject.isNull(JSONMapping.TYPE)) {
            this.setType(this.jsonObject.getString(JSONMapping.TYPE));
        }

        //Meta Data...
        if (!this.jsonObject.isNull(JSONMapping.TYPE_META_DATA)) {
            this.setTypeMetaData(this.jsonObject.getString(JSONMapping.TYPE_META_DATA));
        }
    }

    /**
	 *
	 */
    public Field() {
        super();
    }

    /**
     * 
     * @return
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * 
     * @param fieldNameParam
     */
    public void setFieldName(String fieldNameParam) {
        this.fieldName = fieldNameParam;
    }

    /**
     * 
     * @return
     */
    public Object getFieldValue() {
        return this.fieldValue;
    }

    /**
     *
     * @return
     */
    public String getFieldDescription() {
        return this.fieldDescription;
    }

    /**
     *
     * @param fieldDescriptionParam
     */
    public void setFieldDescription(String fieldDescriptionParam) {
        this.fieldDescription = fieldDescriptionParam;
    }

    /**
     * 
     * @param fieldValueParam
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
     *
     * @return
     */
    public String getType() {
        return this.type;
    }

    /**
     *
     * @param typeParam
     */
    public void setType(String typeParam) {
        this.type = typeParam;
    }

    /**
     * 
     * @param typeParam
     */
    @JsonIgnore
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
     *
     * @return
     */
    @JsonIgnore
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
     *
     * @return
     */
    public String getTypeMetaData() {
        return this.typeMetaData;
    }

    /**
     *
     * @param typeMetaDataParam
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
}
