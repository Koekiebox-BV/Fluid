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

package com.fluidbpm.ws.client.v1.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.role.RoleListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for {@code Role} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Role
 * @see com.fluidbpm.program.api.vo.role.Role
 */
public class RoleClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public RoleClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code Role} with the privileges inside the {@code roleParam}.
     *
     * @param roleParam The {@code Role} to create.
     * @return The Created Role.
     *
     * @see Role
     * @see com.fluidbpm.program.api.vo.role.RoleToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToFormFieldToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToJobView
     */
    public Role createRole(Role roleParam)
    {
        if(roleParam != null && this.serviceTicket != null)
        {
            roleParam.setServiceTicket(this.serviceTicket);
        }

        return new Role(this.putJson(
                roleParam, WS.Path.Role.Version1.roleCreate()));
    }

    /**
     * Updates an existing {@code Role} with the privileges inside the {@code roleParam}.
     *
     * @param roleParam The {@code Role} to update.
     * @return The Created Role.
     *
     * @see Role
     * @see com.fluidbpm.program.api.vo.role.RoleToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToFormFieldToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToJobView
     */
    public Role updateRole(Role roleParam)
    {
        if(roleParam != null && this.serviceTicket != null)
        {
            roleParam.setServiceTicket(this.serviceTicket);
        }

        return new Role(this.postJson(
                roleParam, WS.Path.Role.Version1.roleUpdate()));
    }

    /**
     * Deletes the {@code Role} provided.
     * Id must be set on the {@code Role}.
     *
     * @param roleToDeleteParam The Role to Delete.
     * @return The deleted Role.
     */
    public Role deleteRole(
            Role roleToDeleteParam)
    {
        if(roleToDeleteParam != null && this.serviceTicket != null)
        {
            roleToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new Role(this.postJson(roleToDeleteParam,
                WS.Path.Role.Version1.roleDelete()));
    }

    /**
     * Deletes the {@code Role} provided.
     * Id must be set on the {@code Role}.
     *
     * @param roleToDeleteParam The Role to Delete.
     * @param forcefullyDeleteParam Delete the Role forcefully.
     * @return The deleted Role.
     */
    public Role deleteRole(
            Role roleToDeleteParam,
            boolean forcefullyDeleteParam)
    {
        if(roleToDeleteParam != null && this.serviceTicket != null)
        {
            roleToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new Role(this.postJson(roleToDeleteParam,
                WS.Path.Role.Version1.roleDelete(forcefullyDeleteParam)));
    }

    /**
     * Retrieves role information for the provided {@code roleIdParam}.
     *
     * @param roleIdParam The ID of the {@code Role} to retrieve info for.
     * @return Role information.
     *
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public Role getRoleById(Long roleIdParam)
    {
        Role roleToGetInfoFor = new Role();
        roleToGetInfoFor.setId(roleIdParam);

        if(this.serviceTicket != null)
        {
            roleToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new Role(this.postJson(
                    roleToGetInfoFor, WS.Path.Role.Version1.getById()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all role information.
     *
     * @return Role information.
     *
     * @see com.fluidbpm.program.api.vo.role.RoleListing
     */
    public RoleListing getAllRoles()
    {
        RoleListing roleToGetInfoFor = new RoleListing();

        if(this.serviceTicket != null)
        {
            roleToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new RoleListing(this.postJson(
                    roleToGetInfoFor, WS.Path.Role.Version1.getAllRoles()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
