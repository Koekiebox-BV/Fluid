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

package com.fluid.ws.client.v1.form;

import java.util.List;

import org.json.JSONObject;

import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.FormFlowHistoricData;
import com.fluid.program.api.vo.FormFlowHistoricDataContainer;
import com.fluid.program.api.vo.form.TableRecord;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for Electronic Form related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.FormContainer
 * @see Form
 */
public class FormContainerClient extends ABaseClientWS {

    /**
     * Default constructor.
     */
    public FormContainerClient() {
        super();
    }

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FormContainerClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Create a new Form Container / Electronic Forms.
     *
     * @param formParam The Form to create.
     * @return Created Form Container / Electronic Form.
     *
     * @see com.fluid.program.api.vo.Field
     */
    public Form createFormContainer(Form formParam)
    {
        if(formParam != null && this.serviceTicket != null)
        {
            formParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.putJson(
                formParam, WS.Path.FormContainer.Version1.formContainerCreate()));
    }


    /**
     * Create a new Table Record.
     *
     * @param tableRecordParam The Table Record to create.
     * @return Created Table Record.
     *
     * @see TableRecord
     */
    public TableRecord createTableRecord(TableRecord tableRecordParam)
    {
        if(tableRecordParam != null && this.serviceTicket != null)
        {
            tableRecordParam.setServiceTicket(this.serviceTicket);
        }

        return new TableRecord(this.putJson(
                tableRecordParam,
                WS.Path.FormContainerTableRecord.Version1.formContainerTableRecordCreate()));
    }

    /**
     * Update a new Form Container / Electronic Forms.
     *
     * @param formParam The Form to update.
     * @return Updated Form Container / Electronic Form.
     *
     * @see com.fluid.program.api.vo.Field
     */
    public Form updateFormContainer(Form formParam)
    {
        if(formParam != null && this.serviceTicket != null)
        {
            formParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                formParam, WS.Path.FormContainer.Version1.formContainerUpdate()));
    }

    /**
     * Deletes the Form Container provided.
     * Id must be set on the Form Container.
     *
     * @param formContainerParam The Form Container to Delete.
     * @return The deleted Form Container.
     */
    public Form deleteFormContainer(Form formContainerParam)
    {
        if(formContainerParam != null && this.serviceTicket != null)
        {
            formContainerParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(formContainerParam,
                WS.Path.FormContainer.Version1.formContainerDelete()));
    }

    /**
     * Retrieves Electronic Form Workflow historic information.
     *
     * The Form Id must be provided.
     *
     * @param formParam The form to retrieve historic data for.
     * @return Electronic Form historic data.
     */
    public List<FormFlowHistoricData> getFormContainerHistoricData(Form formParam)
    {
        if(formParam != null && this.serviceTicket != null)
        {
            formParam.setServiceTicket(this.serviceTicket);
        }

        return new FormFlowHistoricDataContainer(this.postJson(
                formParam, WS.Path.FlowItemHistory.Version1.getByFormContainer())).getFormFlowHistoricDatas();
    }

    /**
     * Retrieves the Form Container by Primary key.
     *
     * @param formContainerIdParam The Form Container primary key.
     * @return Form by Primary key.
     */
    public Form getFormContainerById(Long formContainerIdParam)
    {
        Form form = new Form(formContainerIdParam);

        if(this.serviceTicket != null)
        {
            form.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                form, WS.Path.FormContainer.Version1.getById()));
    }
}
