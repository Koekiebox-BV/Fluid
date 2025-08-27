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

import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.role.RoleListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Java Web Service Client for {@code Role} related actions.
 *
 * @author jasonbruwer
 * @see JsonObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Role
 * @see com.fluidbpm.program.api.vo.role.Role
 * @since v1.0
 */
public class RoleClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket   The Server issued Service Ticket.
     */
    public RoleClient(String endpointBaseUrl, String serviceTicket) {
        super(endpointBaseUrl);

        this.setServiceTicket(serviceTicket);
    }

    /**
     * Creates a new {@code Role} with the privileges inside the {@code roleParam}.
     *
     * @param role The {@code Role} to create.
     * @return The Created Role.
     * @see Role
     * @see com.fluidbpm.program.api.vo.role.RoleToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToFormFieldToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToJobView
     */
    public Role createRole(Role role) {
        if (role != null) role.setServiceTicket(this.serviceTicket);

        return new Role(this.putJson(role, WS.Path.Role.Version1.roleCreate()));
    }

    /**
     * Updates an existing {@code Role} with the privileges inside the {@code roleParam}.
     *
     * @param role The {@code Role} to update.
     * @return The Created Role.
     * @see Role
     * @see com.fluidbpm.program.api.vo.role.RoleToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToFormFieldToFormDefinition
     * @see com.fluidbpm.program.api.vo.role.RoleToJobView
     */
    public Role updateRole(Role role) {
        if (role != null) role.setServiceTicket(this.serviceTicket);

        return new Role(this.postJson(role, WS.Path.Role.Version1.roleUpdate()));
    }

    /**
     * Deletes the {@code Role} provided.
     * Id must be set on the {@code Role}.
     *
     * @param roleToDelete The Role to Delete.
     * @return The deleted Role.
     */
    public Role deleteRole(Role roleToDelete) {
        if (roleToDelete != null) roleToDelete.setServiceTicket(this.serviceTicket);

        return new Role(this.postJson(roleToDelete, WS.Path.Role.Version1.roleDelete()));
    }

    /**
     * Deletes the {@code Role} provided.
     * Id must be set on the {@code Role}.
     *
     * @param roleToDelete     The Role to Delete.
     * @param forcefullyDelete Delete the Role forcefully.
     * @return The deleted Role.
     */
    public Role deleteRole(Role roleToDelete, boolean forcefullyDelete) {
        if (roleToDelete != null) roleToDelete.setServiceTicket(this.serviceTicket);

        return new Role(this.postJson(roleToDelete, WS.Path.Role.Version1.roleDelete(forcefullyDelete)));
    }

    /**
     * Retrieves role information for the provided {@code roleIdParam}.
     *
     * @param roleId The ID of the {@code Role} to retrieve info for.
     * @return Role information.
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public Role getRoleById(Long roleId) {
        Role roleToGetInfoFor = new Role();
        roleToGetInfoFor.setId(roleId);
        roleToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new Role(this.postJson(
                roleToGetInfoFor, WS.Path.Role.Version1.getById())
        );
    }

    /**
     * Retrieves role information for the provided {@code roleIdParam}.
     *
     * @param roleName The Name of the {@code Role} to retrieve info for.
     * @return Role information.
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public Role getRoleByName(String roleName) {
        Role roleToGetInfoFor = new Role();
        roleToGetInfoFor.setName(roleName);
        roleToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new Role(this.postJson(
                roleToGetInfoFor, WS.Path.Role.Version1.getById())
        );
    }

    /**
     * Retrieves all role information.
     *
     * @return Role information.
     * @see com.fluidbpm.program.api.vo.role.RoleListing
     */
    public List<Role> getAllRoles() {
        RoleListing roleToGetInfoFor = new RoleListing();
        roleToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new RoleListing(this.postJson(
                roleToGetInfoFor, WS.Path.Role.Version1.getAllRoles())
        ).getListing();
    }
}
