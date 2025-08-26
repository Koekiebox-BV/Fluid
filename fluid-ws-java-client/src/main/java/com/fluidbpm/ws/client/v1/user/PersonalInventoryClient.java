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

package com.fluidbpm.ws.client.v1.user;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Java Web Service Client for Personal inventory related actions.
 *
 * @author jasonbruwer
 * @see JsonObject
 * @see WS.Path.FlowItem
 * @see FluidItem
 * @since v1.4 2017-02-13
 */
public class PersonalInventoryClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam   The Server issued Service Ticket.
     */
    public PersonalInventoryClient(
            String endpointBaseUrlParam,
            String serviceTicketParam
    ) {
        super(endpointBaseUrlParam);
        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Retrieves all Personal Inventory items for the logged in user.
     *
     * @return The Personal Inventory items for the logged in {@code User}.
     */
    public List<FluidItem> getPersonalInventoryItems() {
        User loggedInUser = new User();
        loggedInUser.setServiceTicket(this.serviceTicket);

        return new FluidItemListing(this.postJson(
                loggedInUser,
                WS.Path.PersonalInventory.Version1.getAllByLoggedInUser())).getListing();
    }

    /**
     * Remove the item {@code formToRemoveParam} from the personal inventory.
     *
     * @param formToRemoveParam The {@code Form} to remove from personal inventory.
     * @return The Personal Inventory items removed for {@code User}.
     */
    public Form removeFromPersonalInventory(Form formToRemoveParam) {
        if (formToRemoveParam != null) formToRemoveParam.setServiceTicket(this.serviceTicket);

        return new Form(this.postJson(
                formToRemoveParam,
                WS.Path.PersonalInventory.Version1.removeFromPersonalInventory())
        );
    }

    /**
     * Clears all Personal Inventory items for the logged in user.
     *
     * @return The cleared Personal Inventory items for the logged in {@code User}.
     */
    public List<FluidItem> clearPersonalInventoryItems() {
        User loggedInUser = new User();
        loggedInUser.setServiceTicket(this.serviceTicket);
        return new FluidItemListing(this.postJson(
                loggedInUser,
                WS.Path.PersonalInventory.Version1.clearPersonalInventory())).getListing();
    }
}
