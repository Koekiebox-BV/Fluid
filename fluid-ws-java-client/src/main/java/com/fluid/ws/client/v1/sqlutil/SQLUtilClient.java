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

package com.fluid.ws.client.v1.sqlutil;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.form.FormListing;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

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
     * Default constructor.
     */
    public SQLUtilClient() {
        super();
    }

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public SQLUtilClient(String serviceTicketParam) {
        super();

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
     * @param formToGetDescendantsForParam The Fluid Form to get Table Fields for.
     * @param includeFieldDataParam Should Descendant (Form) Field data be included?
     * @param includeTableFieldsParam Should Table Record (Form) Field data be included?
     *
     * @return The {@code formToGetTableFormsForParam} Table Records as {@code Form}'s.
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

}
