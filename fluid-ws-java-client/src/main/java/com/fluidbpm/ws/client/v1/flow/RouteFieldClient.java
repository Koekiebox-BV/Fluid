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

package com.fluidbpm.ws.client.v1.flow;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.MultiChoice;
import com.fluidbpm.program.api.vo.ws.WS.Path.RouteField.Version1;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;

/**
 * Java Web Service Client for Route Field related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.RouteField
 * @see Field
 * @see ABaseFieldClient
 */
public class RouteFieldClient extends ABaseFieldClient {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public RouteFieldClient(
            String endpointBaseUrlParam,
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldCreate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
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
                formFieldParam, Version1.routeFieldUpdate()));
    }

    /**
     * Retrieves field information by {@code fieldIdParam}.
     *
     * @param fieldIdParam The field Primary Key.
     * @return Field Definition by primary key.
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
                field, Version1.getById()));
    }

    /**
     * Deletes a field from Fluid.
     *
     * @param fieldParam The field to delete. Important that Id is set.
     * @return Deleted Field.
     */
    public Field deleteField(Field fieldParam)
    {
        if(fieldParam != null && this.serviceTicket != null)
        {
            fieldParam.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(fieldParam, Version1.routeFieldDelete()));
    }

    /**
     * Forcefully Deletes a field from Fluid.
     *
     * Only 'admin' is allowed to make this call.
     *
     * @param fieldParam The field to delete. Important that Id is set.
     * @return Deleted Field.
     */
    public Field forceDeleteField(Field fieldParam)
    {
        if(fieldParam != null && this.serviceTicket != null)
        {
            fieldParam.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                fieldParam, Version1.routeFieldDelete(true)));
    }

}
