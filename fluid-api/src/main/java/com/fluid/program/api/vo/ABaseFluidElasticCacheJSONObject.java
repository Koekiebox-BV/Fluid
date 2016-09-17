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
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.util.UtilGlobal;
import com.fluid.program.api.util.sql.impl.SQLFormFieldUtil;

/**
 * <p>
 *     The Base class for any sub-class that wants to make use of the
 *     ElasticSearch. See {@code https://www.elastic.co}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidVO
 * @see JSONObject
 */
public abstract class ABaseFluidElasticCacheJSONObject extends ABaseFluidJSONObject{

    /**
     * Default constructor.
     */
    public ABaseFluidElasticCacheJSONObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidElasticCacheJSONObject(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Conversion to {@code JSONObject} for storage in ElasticSearch.
     *
     * @return {@code JSONObject} representation of {@code Form}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    public abstract JSONObject toJsonForElasticSearch() throws JSONException;

    /**
     * Populate the object based on the ElasticSearch JSON structure.
     *
     * @param jsonObjectParam The JSON object to populate from.
     * @param formFieldsParam The Form Fields to use.
     *
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    public abstract void populateFromElasticSearchJson(
            JSONObject jsonObjectParam,
            List<Field> formFieldsParam) throws JSONException;

    /**
     * Convert the {@code SQLFormFieldUtil.FormFieldMapping} to {@code Field}.
     *
     * @param formFieldMappingsParam The {@code formFieldMappingsParam} to convert.
     * @return The converted {@code Field}s.
     */
    public List<Field> convertTo(List<SQLFormFieldUtil.FormFieldMapping> formFieldMappingsParam)
    {
        if(formFieldMappingsParam == null)
        {
            return null;
        }

        List<Field> returnVal = new ArrayList();

        for(SQLFormFieldUtil.FormFieldMapping mappingToConvert : formFieldMappingsParam)
        {
            returnVal.add(this.convertTo(mappingToConvert));
        }

        return returnVal;
    }

    /**
     * Utility method to convert from a {@code SQLFormFieldUtil.FormFieldMapping} to a {@code Field}.
     *
     * @param formFieldMappingParam The {@code SQLFormFieldUtil.FormFieldMapping} to convert.
     *
     * @return Converted {@code Field} from {@code SQLFormFieldUtil.FormFieldMapping}.
     */
    public Field convertTo(SQLFormFieldUtil.FormFieldMapping formFieldMappingParam)
    {
        switch (formFieldMappingParam.dataType.intValue())
        {
            //Text...
            case UtilGlobal.FieldTypeId._1_TEXT:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null, Field.Type.Text);

            //True False...
            case UtilGlobal.FieldTypeId._2_TRUE_FALSE:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null, Field.Type.TrueFalse);
            //Paragraph Text...
            case UtilGlobal.FieldTypeId._3_PARAGRAPH_TEXT:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null,
                        Field.Type.ParagraphText);
            //Multiple Choice...
            case UtilGlobal.FieldTypeId._4_MULTI_CHOICE:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null,
                        Field.Type.MultipleChoice);
            //Date Time...
            case UtilGlobal.FieldTypeId._5_DATE_TIME:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null,
                        Field.Type.DateTime);
            //Decimal...
            case UtilGlobal.FieldTypeId._6_DECIMAL:
                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null,
                        Field.Type.Decimal);
            //Table Field...
            case UtilGlobal.FieldTypeId._7_TABLE_FIELD:

                return new Field(
                        formFieldMappingParam.formFieldId,
                        formFieldMappingParam.name,
                        null,
                        Field.Type.Table);

                //TODO __8__ encrypted field...
            default:
                throw new IllegalStateException("Unable to map '"+
                        formFieldMappingParam.dataType.intValue() +"', to Form Field value.");
        }
    }
}
