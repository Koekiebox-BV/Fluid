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

package com.fluidbpm.ws.client.v1.form;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.MultiChoice;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;

/**
 * Java Web Service Client for Form Field related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FormField
 * @see Field
 */
public class FormFieldClient extends ABaseFieldClient {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FormFieldClient(String endpointBaseUrlParam,
                           String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Create a new Plain Text field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldTextPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.PLAIN);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Text Masked field.
     *
     * @param formFieldParam Field to Create.
     * @param maskValueParam The masked value. Example: (999) 999 xxx.
     * @return Created Field.
     */
    public Field createFieldTextMasked(
            Field formFieldParam, String maskValueParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(maskValueParam == null || maskValueParam.trim().isEmpty())
        {
            maskValueParam = "";
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.MASKED.concat(maskValueParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Text Barcode field.
     *
     * @param formFieldParam Field to Create.
     * @param barcodeTypeParam The type of barcode.
     * @return Created Field.
     *
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Text
     */
    public Field createFieldTextBarcode(
            Field formFieldParam, String barcodeTypeParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(barcodeTypeParam == null || barcodeTypeParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Barcode type cannot be empty.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.BARCODE.concat(barcodeTypeParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Text field with latitude and longitude.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldTextLatitudeAndLongitude(
            Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.LATITUDE_AND_LONGITUDE);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new True False field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldTrueFalse(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.TrueFalse);
            formFieldParam.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Paragraph Text field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldParagraphTextPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
            formFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Paragraph HTML field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldParagraphTextHTML(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
            formFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice field.
     *
     * @param formFieldParam Field to Create.
     * @param multiChoiceValuesParam The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoicePlain(
            Field formFieldParam, List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null)
        {
            multiChoiceValuesParam = new ArrayList();
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice field with Search abilities.
     *
     * @param formFieldParam Field to Create.
     * @param multiChoiceValuesParam The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoicePlainWithSearch(
            Field formFieldParam, List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN_SEARCH);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice select Many field.
     *
     * @param formFieldParam Field to Create.
     * @param multiChoiceValuesParam The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoiceSelectMany(
            Field formFieldParam, List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice select Many field with Search.
     *
     * @param formFieldParam Field to Create.
     * @param multiChoiceValuesParam The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoiceSelectManyWithSearch(
            Field formFieldParam, List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY_SEARCH);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Date only field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldDateTimeDate(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.DateTime);
            formFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Date and time field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldDateTimeDateAndTime(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.DateTime);
            formFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Decimal field.
     *
     * @param formFieldParam Field to Create.
     * @return Created Field.
     */
    public Field createFieldDecimalPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Decimal Spinner field.
     *
     * @param formFieldParam Field to Create.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     * @param stepFactorParam The increment factor.
     * @param prefixParam The Prefix of the spinner. Example. $.
     * @return Created Field.
     */
    public Field createFieldDecimalSpinner(
            Field formFieldParam,
            double minParam,
            double maxParam,
            double stepFactorParam,
            String prefixParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SPINNER,
                            minParam,maxParam, stepFactorParam, prefixParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Decimal Slider field.
     *
     * @param formFieldParam Field to Create.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     * @param stepFactorParam The increment factor.
     * @return Created Field.
     */
    public Field createFieldDecimalSlider(
            Field formFieldParam,
            double minParam,
            double maxParam,
            double stepFactorParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SLIDER,
                            minParam,maxParam, stepFactorParam,
                            null));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Decimal Rating field.
     *
     * @param formFieldParam Field to Create.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     * @return Created Field.
     */
    public Field createFieldDecimalRating(
            Field formFieldParam,
            double minParam,
            double maxParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SLIDER,
                            minParam,maxParam, 0.0, null));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Creates a new Table Field.
     *
     * @param formFieldParam The Base field to create.
     * @param formDefinitionParam The Form Definition for the Table Field.
     * @param sumDecimalsParam Whether decimals should be summed.
     * @return Table Field.
     */
    public Field createFieldTable(
            Field formFieldParam,
            Form formDefinitionParam,
            boolean sumDecimalsParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Table);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForTableField(
                            formDefinitionParam, sumDecimalsParam));
        }

        return new Field(this.putJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Update an existing Text field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTextPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.PLAIN);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Masked Text field.
     *
     * @param formFieldParam Field to Update.
     * @param maskValueParam The new masked value.
     * @return Updated Field.
     */
    public Field updateFieldTextMasked(Field formFieldParam, String maskValueParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(maskValueParam == null || maskValueParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Masked value cannot be empty.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.MASKED.concat(maskValueParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Barcode Text field.
     *
     * @param formFieldParam Field to Update.
     * @param barcodeTypeParam The new type of barcode image.
     * @return Updated Field.
     */
    public Field updateFieldTextBarcode(Field formFieldParam, String barcodeTypeParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(barcodeTypeParam == null || barcodeTypeParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Barcode type cannot be empty.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.BARCODE.concat(barcodeTypeParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Latitude Longitude Text field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTextLatitudeAndLongitude(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Text);
            formFieldParam.setTypeMetaData(FieldMetaData.Text.LATITUDE_AND_LONGITUDE);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing True False field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTrueFalse(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.TrueFalse);
            formFieldParam.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Paragraph Text field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldParagraphTextPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
            formFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Paragraph HTML field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldParagraphTextHTML(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
            formFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Multi Choice field.
     *
     * @param formFieldParam Field to Update.
     * @param multiChoiceValuesParam New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoicePlain(
            Field formFieldParam,
            List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Multi Choice field with Search.
     *
     * @param formFieldParam Field to Update.
     * @param multiChoiceValuesParam New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoicePlainWithSearch(
            Field formFieldParam,
            List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN_SEARCH);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Multi Choice select many field.
     *
     * @param formFieldParam Field to Update.
     * @param multiChoiceValuesParam New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoiceSelectMany(
            Field formFieldParam,
            List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Multi Choice select many field with Search.
     *
     * @param formFieldParam Field to Update.
     * @param multiChoiceValuesParam New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoiceSelectManyWithSearch(
            Field formFieldParam,
            List<String> multiChoiceValuesParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(multiChoiceValuesParam == null ||
                multiChoiceValuesParam.isEmpty())
        {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
            formFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY_SEARCH);
            formFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Date field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDateTimeDate(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.DateTime);
            formFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Date and Time field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDateTimeDateAndTime(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.DateTime);
            formFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Decimal field.
     *
     * @param formFieldParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDecimalPlain(Field formFieldParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates the Decimal Spinner field.
     *
     * @param formFieldParam The Field.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     * @param stepFactorParam The increments or step factor.
     * @param prefixParam The prefix to the decimal. Example is $ for currency.
     *
     * @return Updated Decimal Spinner.
     */
    public Field updateFieldDecimalSpinner(
            Field formFieldParam,
            double minParam,
            double maxParam,
            double stepFactorParam,
            String prefixParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SPINNER,
                            minParam,maxParam, stepFactorParam, prefixParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates the Decimal Slider field.
     *
     * @param formFieldParam The Field.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     * @param stepFactorParam The increments or step factor.
     *
     * @return Updated Decimal Slider.
     */
    public Field updateFieldDecimalSlider(
            Field formFieldParam,
            double minParam,
            double maxParam,
            double stepFactorParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SLIDER,
                            minParam,maxParam, stepFactorParam, null));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates the Decimal Rating field.
     *
     * @param formFieldParam The Field.
     * @param minParam The min allowed value.
     * @param maxParam The max allowed value.
     *
     * @return Updated Decimal Rating.
     */
    public Field updateFieldDecimalRating(
            Field formFieldParam,
            double minParam,
            double maxParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Decimal);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SLIDER,
                            minParam,maxParam, 0.0, null));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates a Table Field.
     *
     * @param formFieldParam The Field to Update.
     * @param formDefinitionParam The Form Definition to Update.
     * @param sumDecimalsParam Whether decimals should be summed.
     *
     * @return Updated Field.
     */
    public Field updateFieldTable(
            Field formFieldParam,
            Form formDefinitionParam,
            boolean sumDecimalsParam)
    {
        if(formFieldParam != null && this.serviceTicket != null)
        {
            formFieldParam.setServiceTicket(this.serviceTicket);
        }

        if(formFieldParam != null)
        {
            formFieldParam.setTypeAsEnum(Field.Type.Table);
            formFieldParam.setTypeMetaData(
                    this.getMetaDataForTableField(
                            formDefinitionParam, sumDecimalsParam));
        }

        return new Field(this.postJson(
                formFieldParam, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Retrieve a Form Field via Primary key.
     *
     * @param fieldIdParam The field Primary Key.
     * @return Field and properties.
     */
    public Field getFieldById(Long fieldIdParam)
    {
        Field field = new Field(fieldIdParam);

        //Set for Payara server...
        field.setFieldValue(new MultiChoice());

        if(this.serviceTicket != null)
        {
            field.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                field, WS.Path.FormField.Version1.getById()));
    }

    /**
     * Retrieve a Form Field via name.
     *
     * @param fieldNameParam The field name.
     * @return Field and properties.
     */
    public Field getFieldByName(String fieldNameParam)
    {
        Field field = new Field();
        field.setFieldName(fieldNameParam);

        if(this.serviceTicket != null)
        {
            field.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                field, WS.Path.FormField.Version1.getByName()));
    }

    /**
     * Deletes the provided field.
     *
     * Ensure that Id is set.
     *
     * @param fieldParam The Field to delete.
     * @return The deleted Field.
     */
    public Field deleteField(Field fieldParam)
    {
        if(fieldParam != null && this.serviceTicket != null)
        {
            fieldParam.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(fieldParam, WS.Path.FormField.Version1.formFieldDelete()));
    }

    /**
     * Forcefully deletes the provided field.
     *
     * Ensure that Id is set.
     *
     * Only 'admin' is able to forcefully delete a field.
     *
     * @param fieldParam The Field to delete.
     * @return The deleted Field.
     */
    public Field forceDeleteField(Field fieldParam)
    {
        if(fieldParam != null && this.serviceTicket != null)
        {
            fieldParam.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                fieldParam, WS.Path.FormField.Version1.formFieldDelete(true)));
    }

    /**
     * Generates the Meta Data for a table field.
     *
     * @param formDefinitionParam The Form Definition to use.
     * @param sumDecimalsParam Whether decimal values should be summarized.
     * @return Meta Data for the Table Field.
     */
    private String getMetaDataForTableField(
            Form formDefinitionParam,
            boolean sumDecimalsParam)
    {
        StringBuilder returnBuffer = new StringBuilder();

        Long definitionId =
                (formDefinitionParam == null) ? -1L:
                        formDefinitionParam.getId();

        //Min...
        returnBuffer.append(definitionId);
        returnBuffer.append(FieldMetaData.TableField.UNDERSCORE);
        returnBuffer.append(FieldMetaData.TableField.SUM_DECIMALS);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(sumDecimalsParam);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);

        return returnBuffer.toString();
    }

}
