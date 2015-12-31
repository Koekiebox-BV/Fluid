package com.fluid.ws.client.v1.form;

import java.util.ArrayList;
import java.util.List;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.MultiChoice;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseFieldClient;

/**
 * Created by jasonbruwer on 15/01/04.
 */
public class FormFieldClient extends ABaseFieldClient {

    /**
     *
     */
    public FormFieldClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FormFieldClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     *
     * @param formFieldParam
     * @return
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
     * @param formFieldParam
     * @param maskValueParam
     * @return
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
     *
     * @param formFieldParam
     * @param barcodeTypeParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
            multiChoiceValuesParam = new ArrayList<String>();
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @param formDefinitionParam
     * @param sumDecimalsParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @param barcodeTypeParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @param multiChoiceValuesParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @return
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
     *
     * @param formFieldParam
     * @param minParam
     * @param maxParam
     * @param stepFactorParam
     * @param prefixParam
     * @return
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
     *
     * @param formFieldParam
     * @param minParam
     * @param maxParam
     * @param stepFactorParam
     * @return
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
     *
     * @param formFieldParam
     * @param minParam
     * @param maxParam
     * @return
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
     *
     * @param formFieldParam
     * @param formDefinitionParam
     * @param sumDecimalsParam
     * @return
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
     *
     * @param fieldIdParam
     * @return
     */
    public Field getFieldById(Long fieldIdParam)
    {
        Field flow = new Field(fieldIdParam);

        if(this.serviceTicket != null)
        {
            flow.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                flow, WS.Path.FormField.Version1.getById()));
    }

    /**
     *
     * @param fieldParam
     * @return
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
     *
     * @param fieldParam
     * @return
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
     *
     * @param formDefinitionParam
     * @param sumDecimalsParam
     * @return
     */
    private String getMetaDataForTableField(
            Form formDefinitionParam,
            boolean sumDecimalsParam)
    {
        StringBuffer returnBuffer = new StringBuffer();

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
