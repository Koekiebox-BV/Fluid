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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.AutoComplete;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.userquery.UserQueryListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Creates a new client and sets the Base Endpoint URL.
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket The Server issued Service Ticket.
     * @param requestUuid The unique identifier per request.
     */
    public FormFieldClient(
        String endpointBaseUrl,
        String serviceTicket,
        String requestUuid
    ) {
        super(endpointBaseUrl, serviceTicket, requestUuid);
    }

    /**
     * Creates a new client and sets the Base Endpoint URL.
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket The Server issued Service Ticket.
     */
    public FormFieldClient(String endpointBaseUrl, String serviceTicket) {
        super(endpointBaseUrl, serviceTicket);
    }

    /**
     * Create the field based on the {@code Field#getTypeAsEnum} and {@code Field#getTypeMetaData}.
     * @param formField Field to Create.
     * @param additionalInfo The meta-data additional info used for creation.
     * @return Created Field.
     * @throws FluidClientException If there are validation failures.
     * @see FluidClientException.ErrorCode#FIELD_VALIDATE
     */
    public Field createField(Field formField, Object ... additionalInfo) {
        if (formField.getTypeAsEnum() == null || formField.getTypeMetaData() == null) {
            throw new FluidClientException(
                    String.format("Field type and meta-data is mandatory. See '%s'.", formField.getFieldName()),
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        String metaData = formField.getTypeMetaData();
        String metaDataLower = metaData.toLowerCase();
        switch (formField.getTypeAsEnum()) {
            case Text:
                if (FieldMetaData.Text.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldTextPlain(formField);
                } else if (metaDataLower.contains(FieldMetaData.Text.MASKED.toLowerCase())) {
                    if (additionalInfo == null || additionalInfo.length < 1)
                        throw new FluidClientException(
                                String.format("No additional info for '%s' field '%s'.",
                                        FieldMetaData.Text.MASKED, formField.getFieldName()),
                                FluidClientException.ErrorCode.FIELD_VALIDATE
                        );
                    return this.createFieldTextMasked(formField, additionalInfo[0].toString());
                } else if (metaDataLower.contains(FieldMetaData.Text.BARCODE.toLowerCase())) {
                    if (additionalInfo == null || additionalInfo.length < 1)
                        throw new FluidClientException(
                                String.format("No additional info for '%s' field '%s'.",
                                        FieldMetaData.Text.BARCODE, formField.getFieldName()),
                                FluidClientException.ErrorCode.FIELD_VALIDATE
                        );
                    return this.createFieldTextBarcode(formField, additionalInfo[0].toString());
                } else if (metaDataLower.contains(FieldMetaData.Text.LATITUDE_AND_LONGITUDE.toLowerCase())) {
                    return this.createFieldTextLatitudeAndLongitude(formField);
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.",
                                formField.getTypeAsEnum(), metaData),
                        FluidClientException.ErrorCode.FIELD_VALIDATE
                );
            case TextEncrypted:
                if (FieldMetaData.EncryptedText.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldTextEncryptedPlain(formField);
                } else if (metaDataLower.contains(FieldMetaData.EncryptedText.MASKED.toLowerCase())) {
                    return this.createFieldTextEncryptedMasked(formField, additionalInfo[0].toString());
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.",
                                formField.getTypeAsEnum(), metaData
                        ),
                        FluidClientException.ErrorCode.FIELD_VALIDATE
                );
            case TrueFalse:
                return this.createFieldTrueFalse(formField);
            case DateTime:
                if (FieldMetaData.DateTime.DATE.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldDateTimeDate(formField);
                } else if (FieldMetaData.DateTime.DATE_AND_TIME.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldDateTimeDateAndTime(formField);
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.",
                                formField.getTypeAsEnum(),
                                metaData), FluidClientException.ErrorCode.FIELD_VALIDATE);
            case Decimal:
                if (FieldMetaData.Decimal.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldDecimalPlain(formField);
                } else if (metaDataLower.contains(FieldMetaData.Decimal.RATING.toLowerCase())) {
                    return this.createFieldDecimalRating(
                            formField, (Double)additionalInfo[0], (Double)additionalInfo[1]);
                } else if (metaDataLower.contains(FieldMetaData.Decimal.SLIDER.toLowerCase())) {
                    return this.createFieldDecimalSlider(
                            formField,
                            (Double)additionalInfo[0],
                            (Double)additionalInfo[1],
                            (Double)additionalInfo[2]);
                } else if (metaDataLower.contains(FieldMetaData.Decimal.SPINNER.toLowerCase())) {
                    return this.createFieldDecimalSpinner(
                            formField,
                            (Double)additionalInfo[0],
                            (Double)additionalInfo[1],
                            (Double)additionalInfo[2],
                            additionalInfo[3].toString());
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.", formField.getTypeAsEnum(), metaData),
                        FluidClientException.ErrorCode.FIELD_VALIDATE);
            case MultipleChoice:
                if (additionalInfo == null || additionalInfo.length < 1) {
                    throw new FluidClientException(
                            String.format("Unable to determine '%s' type for meta-data '%s'.", formField.getTypeAsEnum(), metaData),
                            FluidClientException.ErrorCode.FIELD_VALIDATE);
                }
                if (FieldMetaData.MultiChoice.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldMultiChoicePlain(formField, (List)additionalInfo[0]);
                } else if (FieldMetaData.MultiChoice.PLAIN_SEARCH.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldMultiChoicePlainWithSearch(formField, (List)additionalInfo[0]);
                } else if (FieldMetaData.MultiChoice.SELECT_MANY.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldMultiChoiceSelectMany(formField, (List)additionalInfo[0]);
                } else if (FieldMetaData.MultiChoice.SELECT_MANY_SEARCH.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldMultiChoiceSelectManyWithSearch(formField, (List)additionalInfo[0]);
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.", formField.getTypeAsEnum(), metaData),
                        FluidClientException.ErrorCode.FIELD_VALIDATE);
            case ParagraphText:
                if (FieldMetaData.ParagraphText.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldParagraphTextPlain(formField);
                } else if (FieldMetaData.ParagraphText.HTML.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldParagraphTextHTML(formField);
                } else if (FieldMetaData.ParagraphText.SIGNATURE.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldParagraphTextSignature(formField);
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.",
                                formField.getTypeAsEnum(), metaData
                        ),
                        FluidClientException.ErrorCode.FIELD_VALIDATE
                );
            case Table:
                return this.createFieldTable(formField, (Form)additionalInfo[0], (Boolean)additionalInfo[1]);
            case Label:
                if (FieldMetaData.Label.PLAIN.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldLabelPlain(formField);
                } else if (FieldMetaData.Label.MAKE_PAYMENT.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldLabelMakePayment(formField);
                } else if (FieldMetaData.Label.CUSTOM_WEB_ACTION.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldLabelCustomWebAction(formField);
                } else if (FieldMetaData.Label.ANCHOR.toLowerCase().equals(metaDataLower)) {
                    return this.createFieldLabelAnchor(formField, (String)additionalInfo[0]);
                }
                throw new FluidClientException(
                        String.format("Unable to determine '%s' type for meta-data '%s'.",
                                formField.getTypeAsEnum(), metaData),
                        FluidClientException.ErrorCode.FIELD_VALIDATE
                );
            default:
                throw new FluidClientException(
                        String.format("Unable to determine type for '%s'.", formField.getTypeAsEnum()),
                        FluidClientException.ErrorCode.FIELD_VALIDATE
                );
        }
    }

    /**
     * Create a new Plain Text field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldTextPlain(Field formField) {
        if (formField != null) formField.setServiceTicket(this.serviceTicket);

        if (formField != null) {
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.PLAIN);
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Plain Encrypted Text field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldTextEncryptedPlain(Field formField) {
        if (formField != null) formField.setServiceTicket(this.serviceTicket);

        if (formField != null) {
            formField.setTypeAsEnum(Field.Type.TextEncrypted);
            formField.setTypeMetaData(FieldMetaData.EncryptedText.PLAIN);
        }
        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Masked Encrypted Text field.
     * @param formField Field to Create.
     * @param maskedValue The mask value for the encrypted field.
     * @return Created Field.
     */
    public Field createFieldTextEncryptedMasked(Field formField, String maskedValue) {
        if (formField != null) formField.setServiceTicket(this.serviceTicket);

        if (formField != null) {
            formField.setTypeAsEnum(Field.Type.TextEncrypted);
            formField.setTypeMetaData(FieldMetaData.EncryptedText.MASKED.concat(
                    maskedValue == null ? UtilGlobal.EMPTY : maskedValue)
            );
        }
        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Text Masked field.
     * @param formField Field to Create.
     * @param maskValue The masked value. Example: (999) 999 xxx.
     * @return Created Field.
     */
    public Field createFieldTextMasked(Field formField, String maskValue) {
        if (formField != null) formField.setServiceTicket(this.serviceTicket);

        if (maskValue == null || maskValue.trim().isEmpty()) maskValue = UtilGlobal.EMPTY;

        if (formField != null) {
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.MASKED.concat(maskValue));
        }

        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Text Barcode field.
     * @param formField Field to Create.
     * @param barcodeType The type of barcode.
     * @return Created Field.
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Text
     */
    public Field createFieldTextBarcode(Field formField, String barcodeType) {
        if (UtilGlobal.isBlank(barcodeType)) {
            throw new FluidClientException("Barcode type cannot be empty.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.BARCODE.concat(barcodeType));
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Text field with latitude and longitude.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldTextLatitudeAndLongitude(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.LATITUDE_AND_LONGITUDE);
        }
        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Label Plain field.
     * @param formField Field to Create.
     * @return Created Field.
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Label
     */
    public Field createFieldLabelPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Label);
            formField.setTypeMetaData(FieldMetaData.Label.PLAIN);
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Label MakePayment field.
     * @param formField Field to Create.
     * @return Created Field.
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Label
     */
    public Field createFieldLabelMakePayment(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Label);
            formField.setTypeMetaData(FieldMetaData.Label.MAKE_PAYMENT);
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Label CustomWebAction field.
     * @param formField Field to Create.
     * @return Created Field.
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Label
     */
    public Field createFieldLabelCustomWebAction(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Label);
            formField.setTypeMetaData(FieldMetaData.Label.CUSTOM_WEB_ACTION);
        }
        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Label Anchor field.
     * @param formField Field to Create.
     * @param anchorValue The anchor value.
     * @return Created Field.
     * @see com.fluidbpm.ws.client.v1.ABaseFieldClient.FieldMetaData.Label
     */
    public Field createFieldLabelAnchor(Field formField, String anchorValue) {
        if (UtilGlobal.isBlank(anchorValue)) {
            throw new FluidClientException("Anchor cannot be empty.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Label);
            formField.setTypeMetaData(String.format("%s_%s", FieldMetaData.Label.ANCHOR, anchorValue));
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new True False field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldTrueFalse(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.TrueFalse);
            formField.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Paragraph Text field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldParagraphTextPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.ParagraphText);
            formField.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
        }

        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Paragraph HTML field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldParagraphTextHTML(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.ParagraphText);
            formField.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Paragraph Signature field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldParagraphTextSignature(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.ParagraphText);
            formField.setTypeMetaData(FieldMetaData.ParagraphText.SIGNATURE);
        }

        return new Field(this.putJson(formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice field.
     * @param formField Field to Create.
     * @param multiChoiceValues The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoicePlain(
        Field formField, List<String> multiChoiceValues
    ) {
        if (multiChoiceValues == null) multiChoiceValues = new ArrayList();

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Multi Choice field with Search abilities.
     * @param formField Field to Create.
     * @param multiChoiceValues The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoicePlainWithSearch(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE
            );
        }

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN_SEARCH);
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice select Many field.
     * @param formField Field to Create.
     * @param multiChoiceValues The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoiceSelectMany(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null ||
                multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }

        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate()));
    }

    /**
     * Create a new Multi Choice select Many field with Search.
     * @param formField Field to Create.
     * @param multiChoiceValues The available multi choice values.
     * @return Created Field.
     */
    public Field createFieldMultiChoiceSelectManyWithSearch(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY_SEARCH);
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Date only field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldDateTimeDate(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.DateTime);
            formField.setTypeMetaData(FieldMetaData.DateTime.DATE);
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Date and time field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldDateTimeDateAndTime(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.DateTime);
            formField.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Decimal field.
     * @param formField Field to Create.
     * @return Created Field.
     */
    public Field createFieldDecimalPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
        }

        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Decimal Spinner field.
     * @param formField Field to Create.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @param stepFactor The increment factor.
     * @param prefix The Prefix of the spinner. Example. $.
     * @return Created Field.
     */
    public Field createFieldDecimalSpinner(
        Field formField,
        double min,
        double max,
        double stepFactor,
        String prefix
    ) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(
                    this.getMetaDataForDecimalAs(FieldMetaData.Decimal.SPINNER, min, max, stepFactor, prefix)
            );
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Decimal Slider field.
     * @param formField Field to Create.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @param stepFactor The increment factor.
     * @return Created Field.
     */
    public Field createFieldDecimalSlider(Field formField, double min, double max, double stepFactor) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SLIDER,
                            min,max, stepFactor,
                            null));
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Create a new Decimal Rating field.
     * @param formField Field to Create.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @return Created Field.
     */
    public Field createFieldDecimalRating(Field formField, double min, double max) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(
                    this.getMetaDataForDecimalAs(FieldMetaData.Decimal.RATING,
                            min, max, 0.0, null)
            );
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Creates a new Table Field.
     * @param formField The Base field to create.
     * @param formDefinition The Form Definition for the Table Field.
     * @param sumDecimals Whether decimals should be summed.
     * @return Table Field.
     */
    public Field createFieldTable(Field formField, Form formDefinition, boolean sumDecimals) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Table);
            formField.setTypeMetaData(this.getMetaDataForTableField(formDefinition, sumDecimals));
        }
        return new Field(this.putJson(
                formField, WS.Path.FormField.Version1.formFieldCreate())
        );
    }

    /**
     * Update an existing Text field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTextPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.PLAIN);
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Masked Text field.
     * @param formField Field to Update.
     * @param maskValue The new masked value.
     * @return Updated Field.
     */
    public Field updateFieldTextMasked(Field formField, String maskValue) {
        if (UtilGlobal.isBlank(maskValue)) {
            throw new FluidClientException("Masked value cannot be empty.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.MASKED.concat(maskValue));
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Barcode Text field.
     * @param formField Field to Update.
     * @param barcodeType The new type of barcode image.
     * @return Updated Field.
     */
    public Field updateFieldTextBarcode(Field formField, String barcodeType) {
        if (UtilGlobal.isBlank(barcodeType)) {
            throw new FluidClientException("Barcode type cannot be empty.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.BARCODE.concat(barcodeType));
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Latitude Longitude Text field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTextLatitudeAndLongitude(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Text);
            formField.setTypeMetaData(FieldMetaData.Text.LATITUDE_AND_LONGITUDE);
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing True False field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldTrueFalse(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.TrueFalse);
            formField.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Paragraph Text field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldParagraphTextPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.ParagraphText);
            formField.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Paragraph HTML field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldParagraphTextHTML(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.ParagraphText);
            formField.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Update an existing Multi Choice field.
     * @param formField Field to Update.
     * @param multiChoiceValues New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoicePlain(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        List<String> beforeAvail = null, beforeSelected = null;
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);

            if (formField.getFieldValue() instanceof MultiChoice) {
                MultiChoice casted = (MultiChoice)formField.getFieldValue();
                beforeAvail = casted.getAvailableMultiChoices();
                beforeSelected = casted.getSelectedMultiChoices();
            }

            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        Field returnVal = new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
        if (formField != null) {
            formField.setFieldValue(new MultiChoice(beforeSelected, beforeAvail));
        }
        return returnVal;
    }

    /**
     * Update an existing Multi Choice field with Search.
     * @param formField Field to Update.
     * @param multiChoiceValues New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoicePlainWithSearch(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        List<String> beforeAvail = null, beforeSelected = null;
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN_SEARCH);

            if (formField.getFieldValue() instanceof MultiChoice) {
                MultiChoice casted = (MultiChoice)formField.getFieldValue();
                beforeAvail = casted.getAvailableMultiChoices();
                beforeSelected = casted.getSelectedMultiChoices();
            }

            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        Field returnVal = new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
        if (formField != null) {
            formField.setFieldValue(new MultiChoice(beforeSelected, beforeAvail));
        }
        return returnVal;
    }

    /**
     * Update an existing Multi Choice select many field.
     * @param formField Field to Update.
     * @param multiChoiceValues New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoiceSelectMany(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        List<String> beforeAvail = null, beforeSelected = null;
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
            if (formField.getFieldValue() instanceof MultiChoice){
                MultiChoice casted = (MultiChoice)formField.getFieldValue();
                beforeAvail = casted.getAvailableMultiChoices();
                beforeSelected = casted.getSelectedMultiChoices();
            }
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }

        Field returnVal = new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
        if (formField != null) {
            formField.setFieldValue(new MultiChoice(beforeSelected, beforeAvail));
        }
        return returnVal;
    }

    /**
     * Update an existing Multi Choice select many field with Search.
     *
     * @param formField Field to Update.
     * @param multiChoiceValues New available Multi-choices.
     * @return Updated Field.
     */
    public Field updateFieldMultiChoiceSelectManyWithSearch(Field formField, List<String> multiChoiceValues) {
        if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
            throw new FluidClientException(
                    "No Multi-choice values provided.", FluidClientException.ErrorCode.FIELD_VALIDATE);
        }
        List<String> beforeAvail = null, beforeSelected = null;
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.MultipleChoice);
            formField.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY_SEARCH);

            if (formField.getFieldValue() instanceof MultiChoice){
                MultiChoice casted = (MultiChoice)formField.getFieldValue();
                beforeAvail = casted.getAvailableMultiChoices();
                beforeSelected = casted.getSelectedMultiChoices();
            }
            formField.setFieldValue(new MultiChoice(multiChoiceValues));
        }
        Field returnVal = new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
        if (formField != null) {
            formField.setFieldValue(new MultiChoice(beforeSelected, beforeAvail));
        }
        return returnVal;
    }

    /**
     * Update an existing Date field.
     *
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDateTimeDate(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.DateTime);
            formField.setTypeMetaData(FieldMetaData.DateTime.DATE);
        }
        return new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Date and Time field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDateTimeDateAndTime(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.DateTime);
            formField.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
        }
        return new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Update an existing Decimal field.
     * @param formField Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldDecimalPlain(Field formField) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
        }
        return new Field(this.postJson(formField, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates the Decimal Spinner field.
     * @param formField The Field.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @param stepFactor The increments or step factor.
     * @param prefix The prefix to the decimal. Example is $ for currency.
     * @return Updated Decimal Spinner.
     */
    public Field updateFieldDecimalSpinner(Field formField, double min, double max, double stepFactor, String prefix) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(
                    this.getMetaDataForDecimalAs(
                            FieldMetaData.Decimal.SPINNER, min,max, stepFactor, prefix));
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Updates the Decimal Slider field.
     * @param formField The Field.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @param stepFactor The increments or step factor.
     * @return Updated Decimal Slider.
     */
    public Field updateFieldDecimalSlider(Field formField, double min, double max, double stepFactor) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(this.getMetaDataForDecimalAs(
                    FieldMetaData.Decimal.SLIDER, min,max, stepFactor, null));
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate()));
    }

    /**
     * Updates the Decimal Rating field.
     * @param formField The Field.
     * @param min The min allowed value.
     * @param max The max allowed value.
     * @return Updated Decimal Rating.
     */
    public Field updateFieldDecimalRating(Field formField, double min, double max) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Decimal);
            formField.setTypeMetaData(this.getMetaDataForDecimalAs(
                    FieldMetaData.Decimal.RATING, min,max, 0.0, null)
            );
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Updates a Table Field.
     * @param formField The Field to Update.
     * @param formDefinition The Form Definition to Update.
     * @param sumDecimals Whether decimals should be summed.
     * @return Updated Field.
     */
    public Field updateFieldTable(Field formField, Form formDefinition, boolean sumDecimals) {
        if (formField != null) {
            formField.setServiceTicket(this.serviceTicket);
            formField.setTypeAsEnum(Field.Type.Table);
            formField.setTypeMetaData(this.getMetaDataForTableField(formDefinition, sumDecimals));
        }
        return new Field(this.postJson(
                formField, WS.Path.FormField.Version1.formFieldUpdate())
        );
    }

    /**
     * Retrieve a Form Field via Primary key.
     * @param fieldId The field Primary Key.
     * @return Field and properties.
     */
    public Field getFieldById(Long fieldId) {
        Field field = new Field(fieldId);
        //Set for Payara server...
        field.setFieldValue(new MultiChoice());
        field.setServiceTicket(this.serviceTicket);

        if (this.requestUuid != null) field.setRequestUuid(this.requestUuid);
        return new Field(this.postJson(
                field, WS.Path.FormField.Version1.getById())
        );
    }

    /**
     * Retrieve a Form Field via name.
     * @param fieldName The field name.
     * @return Field and properties.
     */
    public Field getFieldByName(String fieldName) {
        Field field = new Field();
        field.setFieldName(fieldName);
        field.setServiceTicket(this.serviceTicket);
        return new Field(this.postJson(field, WS.Path.FormField.Version1.getByName()));
    }

    /**
     * Retrieve the Form Fields via Form Definition name.
     * @param formName The form definition name.
     * @param editOnlyFields Only return the fields that are editable.
     * @return Form Fields for {@code formNameParam}
     */
    public FormFieldListing getFieldsByFormNameAndLoggedInUser(String formName, boolean editOnlyFields) {
        return this.getFieldsByFormNameAndLoggedInUser(formName, editOnlyFields, false);
    }

    /**
     * Retrieve the Form Fields via Form Definition name.
     * @param formName The form definition name.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @return Form Fields for {@code formNameParam}
     */
    public FormFieldListing getFieldsByFormNameAndLoggedInUser(
        String formName,
        boolean editOnlyFields,
        boolean populateMultiChoiceFields
    ) {
        Form form = new Form();
        form.setFormType(formName);
        form.setServiceTicket(this.serviceTicket);
        if (this.requestUuid != null) form.setRequestUuid(this.requestUuid);

        return new FormFieldListing(this.postJson(
                form, WS.Path.FormField.Version1.getByFormDefinitionAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        );
    }

    /**
     * Retrieve the Form Fields via Form Definition names.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @param formNames The form definition names.
     * @return Form Fields for {@code formNameParam}
     */
    public List<Form> getFieldsByFormNamesAndLoggedInUser(
        boolean editOnlyFields,
        boolean populateMultiChoiceFields,
        List<String> formNames
    ) {
        if (formNames == null || formNames.isEmpty()) return new ArrayList<>();
        FormListing formListing = new FormListing();
        formListing.setListing(formNames.stream().map(itm -> {
                    Form form = new Form();
                    form.setFormType(itm);
                    return form;
                })
                .collect(Collectors.toList()));
        formListing.setServiceTicket(this.serviceTicket);

        if (this.requestUuid != null) formListing.setRequestUuid(this.requestUuid);

        return new FormListing(this.postJson(
                formListing, WS.Path.FormField.Version1.getByFormDefinitionsAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        ).getListing();
    }

    /**
     * Retrieve the Form Fields via Form Definition names.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @param formNames The form definition names.
     * @return Form Fields for {@code formNameParam}
     */
    public List<Form> getFieldsByFormNamesAndLoggedInUser(
        boolean editOnlyFields,
        boolean populateMultiChoiceFields,
        String ... formNames
    ) {
        if (formNames == null || formNames.length == 0) return new ArrayList<>();
        List<Form> formList = new ArrayList<>();
        for (String formName : formNames) {
            Form form = new Form();
            form.setFormType(formName);
            formList.add(form);
        }

        FormListing formListing = new FormListing();
        formListing.setListing(formList);
        formListing.setServiceTicket(this.serviceTicket);
        if (this.requestUuid != null) formListing.setRequestUuid(this.requestUuid);

        return new FormListing(this.postJson(
                formListing, WS.Path.FormField.Version1.getByFormDefinitionsAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        ).getListing();
    }


    /**
     * Retrieve the Form Fields via Form Definition names.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @param formIds The form definition names.
     * @return Form Fields for {@code formNameParam}
     */
    public List<Form> getFieldsByFormIdsAndLoggedInUser(
        boolean editOnlyFields,
        boolean populateMultiChoiceFields,
        List<Long> formIds
    ) {
        if (formIds == null || formIds.isEmpty()) return new ArrayList<>();
        FormListing formListing = new FormListing();
        formListing.setListing(formIds.stream().map(itm -> new Form(itm)).collect(Collectors.toList()));
        formListing.setServiceTicket(this.serviceTicket);
        if (this.requestUuid != null) formListing.setRequestUuid(this.requestUuid);

        return new FormListing(this.postJson(
                formListing, WS.Path.FormField.Version1.getByFormDefinitionsAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        ).getListing();
    }

    /**
     * Retrieve the Form Fields via Form Definition names.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @param formIds The form definition ids.
     * @return Form Fields for {@code formNameParam}
     */
    public List<Form> getFieldsByFormIdsAndLoggedInUser(
        boolean editOnlyFields,
        boolean populateMultiChoiceFields,
        Long ... formIds
    ) {
        if (formIds == null || formIds.length == 0) return new ArrayList<>();
        List<Form> formList = new ArrayList<>();
        for (Long formId : formIds) formList.add(new Form(formId));

        FormListing formListing = new FormListing();
        formListing.setListing(formList);
        formListing.setServiceTicket(this.serviceTicket);
        if (this.requestUuid != null) formListing.setRequestUuid(this.requestUuid);

        return new FormListing(this.postJson(
                formListing, WS.Path.FormField.Version1.getByFormDefinitionsAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        ).getListing();
    }

    /**
     * Retrieve the Form Fields via User Query.
     *
     * @param userQuery The user query.
     *
     * @return Form Fields for {@code userQuery}
     */
    public List<Field> getFormFieldsByUserQuery(UserQuery userQuery) {
        if (userQuery != null) userQuery.setServiceTicket(this.serviceTicket);

        return new FormFieldListing(this.postJson(
                userQuery, WS.Path.FormField.Version1.getByUserQuery())
        ).getListing();
    }

    /**
     * Retrieve the Form Fields via User Query.
     * @param userQueries The user queries to fetch fields for.
     * @return Form Fields for {@code userQuery}
     */
    public List<UserQuery> getFormFieldsByUserQueries(List<UserQuery> userQueries) {
        if (userQueries == null || userQueries.isEmpty()) return new ArrayList<>();

        UserQueryListing userQueryListing = new UserQueryListing();
        userQueryListing.setListing(userQueries.stream()
                .map(toAdd -> new UserQuery(toAdd.getId(), toAdd.getName()))
                .collect(Collectors.toList()));
        userQueryListing.setServiceTicket(this.serviceTicket);
        userQueryListing.setListingCount(userQueries.size());

        return new UserQueryListing(this.postJson(
                userQueryListing, WS.Path.FormField.Version1.getByUserQueries())
        ).getListing();
    }


    /**
     * Retrieve a list of existing field values
     * @param formField The field to execute the auto-complete for.
     * @param query The query to auto-complete for.
     * @param maxResults The maximum number of results to return.
     * @return Form Fields for {@code userQuery}
     */
    public List<String> autoCompleteBasedOnExisting(Field formField, String query, int maxResults) {
        if (UtilGlobal.isBlank(query)) return new ArrayList<>();

        AutoComplete req = new AutoComplete(formField, query, maxResults);
        req.setServiceTicket(this.serviceTicket);
        return new AutoComplete(this.postJson(req, WS.Path.FormField.Version1.autoCompleteExisting())).getExistingValues();
    }

    /**
     * Retrieve the Form Fields via Form Definition id.
     * @param formTypeId The form definition id.
     * @param editOnlyFields Only return the fields that are editable.
     * @param populateMultiChoiceFields Populate the multi-choice fields.
     * @return Form Fields for {@code formIdParam}
     */
    public FormFieldListing getFieldsByFormTypeIdAndLoggedInUser(Long formTypeId, boolean editOnlyFields, boolean populateMultiChoiceFields) {
        Form form = new Form();
        form.setFormTypeId(formTypeId);
        form.setServiceTicket(this.serviceTicket);

        return new FormFieldListing(this.postJson(
                form, WS.Path.FormField.Version1.getByFormDefinitionAndLoggedInUser(
                        editOnlyFields, populateMultiChoiceFields))
        );
    }

    /**
     * Deletes the provided field. Ensure that Id is set.
     * @param field The Field to delete.
     * @return The deleted Field.
     */
    public Field deleteField(Field field) {
        if (field != null) field.setServiceTicket(this.serviceTicket);
        return new Field(this.postJson(field, WS.Path.FormField.Version1.formFieldDelete()));
    }

    /**
     * Forcefully deletes the provided field.
     * Ensure that Id is set. Only 'admin' is able to forcefully delete a field.
     * @param field The Field to delete.
     * @return The deleted Field.
     */
    public Field forceDeleteField(Field field) {
        if (field != null) field.setServiceTicket(this.serviceTicket);
        return new Field(this.postJson(
                field, WS.Path.FormField.Version1.formFieldDelete(true))
        );
    }

    /**
     * Generates the Meta Data for a table field.
     * @param formDefinition The Form Definition to use.
     * @param sumDecimals Whether decimal values should be summarized.
     * @return Meta-Data for the Table Field.
     */
    private String getMetaDataForTableField(Form formDefinition, boolean sumDecimals) {
        StringBuilder returnBuffer = new StringBuilder();
        Long definitionId = (formDefinition == null ||
                formDefinition.getFormTypeId() == null) ? -1L: formDefinition.getFormTypeId();
        String formDefVal = String.valueOf(definitionId);
        if (definitionId < 1L) formDefVal = (formDefinition == null) ?
                UtilGlobal.EMPTY : formDefinition.getFormType();

        returnBuffer.append(formDefVal);
        returnBuffer.append(FieldMetaData.TableField.UNDERSCORE);
        returnBuffer.append(FieldMetaData.TableField.SUM_DECIMALS);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(sumDecimals);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);

        return returnBuffer.toString();
    }
}
