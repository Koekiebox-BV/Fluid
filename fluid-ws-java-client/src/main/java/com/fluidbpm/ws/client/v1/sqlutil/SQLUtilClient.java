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

package com.fluidbpm.ws.client.v1.sqlutil;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for {@code SQLUtil} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see WS.Path.FlowItem
 * @see FluidItem
 */
public class SQLUtilClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public SQLUtilClient(String endpointBaseUrlParam, String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Retrieves all the Table Records (Forms) for the {@code formToGetTableFormsForParam}.
     *
     * @param formToGetTableFormsForParam The Fluid Form to get Table Fields for.
     * @param includeFieldDataParam Should Table Record (Form) Field data be included?
     *
     * @return The {@code formToGetTableFormsForParam} Table Records as {@code Form}'s.
     */
    public List<Form> getTableForms(
            Form formToGetTableFormsForParam,
            boolean includeFieldDataParam) {

        if (formToGetTableFormsForParam != null && this.serviceTicket != null) {
            formToGetTableFormsForParam.setServiceTicket(this.serviceTicket);
        }

        try {

            FormListing formListing = new FormListing(
                    this.postJson(formToGetTableFormsForParam,
                            WS.Path.SQLUtil.Version1.getTableForms(
                                    includeFieldDataParam)));

            return formListing.getListing();
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all Descendants for the {@code formToGetTableFormsForParam}.
     *
     * @param formToGetDescendantsForParam The Fluid Form to get Descendants for.
     * @param includeFieldDataParam Should Descendant (Form) Field data be included?
     * @param includeTableFieldsParam Should Table Record (Form) Field data be included?
     *
     * @return The {@code formToGetTableFormsForParam} Descendants as {@code Form}'s.
     */
    public List<Form> getDescendants(
            Form formToGetDescendantsForParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {

        if (formToGetDescendantsForParam != null && this.serviceTicket != null) {
            formToGetDescendantsForParam.setServiceTicket(this.serviceTicket);
        }

        try {
            FormListing formListing = new FormListing(
                    this.postJson(formToGetDescendantsForParam,
                            WS.Path.SQLUtil.Version1.getDescendants(
                                    includeFieldDataParam,
                                    includeTableFieldsParam)));

            return formListing.getListing();
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves the Ancestor for the {@code formToGetAncestorForParam}.
     *
     * @param formToGetAncestorForParam The Fluid Form to get Ancestor for.
     * @param includeFieldDataParam Should Ancestor (Form) Field data be included?
     * @param includeTableFieldsParam Should Table Record (Form) Field data be included?
     *
     * @return The {@code formToGetAncestorForParam} Ancestor as {@code Form}'s.
     */
    public Form getAncestor(
            Form formToGetAncestorForParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {

        if (formToGetAncestorForParam != null && this.serviceTicket != null) {
            formToGetAncestorForParam.setServiceTicket(this.serviceTicket);
        }

        try {
            return new Form(
                    this.postJson(formToGetAncestorForParam,
                            WS.Path.SQLUtil.Version1.getAncestor(
                                    includeFieldDataParam,
                                    includeTableFieldsParam)));
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all Fields for the {@code formToGetFieldsForParam}.
     *
     * @param formToGetFieldsForParam The Fluid Form to get Fields for.
     * @param includeTableFieldsParam Should Table Field data be included?
     *
     * @return The {@code formToGetFieldsForParam} Fields as {@code Field}'s.
     */
    public List<Field> getFormFields(
            Form formToGetFieldsForParam,
            boolean includeTableFieldsParam) {

        if (formToGetFieldsForParam != null && this.serviceTicket != null) {
            formToGetFieldsForParam.setServiceTicket(this.serviceTicket);
        }

        try {
            FormFieldListing formFieldListing = new FormFieldListing(
                    this.postJson(formToGetFieldsForParam,
                            WS.Path.SQLUtil.Version1.getFormFields(
                                    includeTableFieldsParam)));

            return formFieldListing.getListing();
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
