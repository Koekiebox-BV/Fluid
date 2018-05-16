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

package com.fluidbpm.ws.client.v1.config;

import java.util.List;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.config.GlobalFieldListing;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField.Version1;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;

/**
 * Java Web Service Client for Global Field related actions.
 *
 * @author jasonbruwer on 2018-05-05
 * @version v1.8
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField
 * @see Field
 * @see ABaseFieldClient
 */
public class GlobalFieldClient extends ABaseFieldClient {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public GlobalFieldClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Update an existing Global field value.
     *
     * @param globalFieldValueParam Field to Update.
     * @return Updated Field.
     */
    public Field updateFieldValue(Field globalFieldValueParam)
    {
        if(globalFieldValueParam != null && this.serviceTicket != null)
        {
            globalFieldValueParam.setServiceTicket(this.serviceTicket);
        }

        return new Field(this.postJson(
                globalFieldValueParam,
                Version1.globalFieldUpdateValue()));
    }

    /**
     * Retrieves field value by {@code fieldNameParam}.
     *
     * @param fieldNameParam The field name.
     * @return Field by primary key.
     */
    public Field getFieldValueByName(String fieldNameParam)
    {
        Field field = new Field();
        field.setFieldName(fieldNameParam);
        
        return this.getFieldValueBy(field);
    }

    /**
     * Retrieves field value by {@code fieldIdParam}.
     *
     * @param fieldIdParam The field id.
     * @return Field value by Global field primary key.
     */
    public Field getFieldValueByFieldId(Long fieldIdParam)
    {
        return this.getFieldValueBy(new Field(fieldIdParam));
    }

    /**
     * Retrieves field value by {@code fieldParam}.
     *
     * @param fieldParam The field name.
     * @return Field by primary key.
     */
    private Field getFieldValueBy(Field fieldParam)
    {
        if(fieldParam != null)
        {
            //Set for Payara server...
            fieldParam.setFieldValue(new MultiChoice());

            if(this.serviceTicket != null)
            {
                fieldParam.setServiceTicket(this.serviceTicket);
            }
        }

        return new Field(this.postJson(
                fieldParam, Version1.getValueBy()));
    }

    /**
     * Retrieve all the Global field values.
     *
     * @return Global Fields in the destination system.
     *
     * @see GlobalFieldListing
     * @see Field
     */
    public List<Field> getAllGlobalFieldValues()
    {
        Field field = new Field();

        //Set for Payara server...
        field.setFieldValue(new MultiChoice());
        
        if(this.serviceTicket != null)
        {
            field.setServiceTicket(this.serviceTicket);
        }

        return new GlobalFieldListing(this.postJson(
                field, Version1.getAllValues())).getListing();
    }

}
