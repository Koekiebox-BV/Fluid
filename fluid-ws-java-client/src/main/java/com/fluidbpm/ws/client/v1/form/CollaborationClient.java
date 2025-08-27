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

import com.fluidbpm.program.api.vo.form.Collaboration;
import com.fluidbpm.program.api.vo.form.CollaborationListing;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

import java.util.List;

/**
 * Java Web Service Client for Electronic Form related actions.
 *
 * @author jasonbruwer
 * @see WS.Path.FormContainer
 * @see Form
 * @since v1.0
 */
public class CollaborationClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam   The Server issued Service Ticket.
     */
    public CollaborationClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Create a new Form Container / Electronic Forms.
     *
     * @param collaborationsParam The Collaboration's to create.
     * @return Created Collaboration's.
     * @see Collaboration
     */
    public List<Collaboration> createCollaboration(
            List<Collaboration> collaborationsParam
    ) {
        CollaborationListing collaborationListing = new CollaborationListing();
        collaborationListing.setListing(collaborationsParam);

        if (this.serviceTicket != null) {
            collaborationListing.setServiceTicket(this.serviceTicket);
        }

        return new CollaborationListing(this.putJson(
                collaborationListing, WS.Path.Collaboration.Version1.collaborationCreate())).getListing();
    }

    /**
     * Retrieve all Collaboration items TO where logged in user.
     *
     * @return Created Collaboration's.
     * @see Collaboration
     */
    public List<Collaboration> getAllToByLoggedIn() {
        CollaborationListing collaborationListing = new CollaborationListing();
        if (this.serviceTicket != null) {
            collaborationListing.setServiceTicket(this.serviceTicket);
        }

        return new CollaborationListing(this.postJson(
                collaborationListing, WS.Path.Collaboration.Version1.getAllToByLoggedIn())).getListing();
    }

    /**
     * Retrieve all Collaboration items TO where {@code Form} is {@code formParam}.
     *
     * @param formParam The form to fetch collaborations to where sent 'TO'.
     * @return Created Collaboration's based on {@code formParam}.
     * @see Collaboration
     */
    public List<Collaboration> getAllToByForm(Form formParam) {
        if (formParam != null && this.serviceTicket != null) {
            formParam.setServiceTicket(this.serviceTicket);
        }
        return new CollaborationListing(this.postJson(
                formParam, WS.Path.Collaboration.Version1.getAllToByForm())
        ).getListing();
    }
}