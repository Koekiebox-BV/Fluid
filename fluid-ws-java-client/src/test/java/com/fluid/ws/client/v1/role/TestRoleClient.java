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

package com.fluid.ws.client.v1.role;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.role.Role;
import com.fluid.program.api.vo.role.RoleListing;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestRoleClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    @Before
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        //this.loginClient = new LoginClient("http://fluid.sahousingclub.co.za/fluid-ws/");
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @After
    public void destroy()
    {
        this.loginClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testCreateRole() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RoleClient roleClient = new RoleClient(serviceTicket);

        Role roleToCreate = new Role();
        roleToCreate.setName("junit Testing Role");

        List<String> roleAdminPerm = new ArrayList<>();
        roleAdminPerm.add("change_own_password");
        roleAdminPerm.add("edit_roles");
        roleAdminPerm.add("view_roles");
        roleAdminPerm.add("view_users");
        roleToCreate.setAdminPermissions(roleAdminPerm);

        roleToCreate = roleClient.createRole(roleToCreate);

        TestCase.assertNotNull(roleToCreate);

        roleClient.deleteRole(roleToCreate,true);
    }


    /**
     *
     */
    @Test
    public void testUpdateRole() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RoleClient roleClient = new RoleClient(serviceTicket);

        Role roleToCreate = new Role();
        roleToCreate.setName("junit Testing Role");

        List<String> roleAdminPerm = new ArrayList<>();
        roleAdminPerm.add("change_own_password");
        roleAdminPerm.add("edit_roles");
        roleAdminPerm.add("view_roles");
        roleAdminPerm.add("view_users");
        roleToCreate.setAdminPermissions(roleAdminPerm);

        Role roleToUpdate = roleClient.createRole(roleToCreate);
        TestCase.assertNotNull(roleToCreate);

        roleToCreate.setName("junit Testing Role Updated");
        roleToUpdate = roleClient.updateRole(roleToUpdate);
        TestCase.assertNotNull(roleToUpdate);

        roleClient.deleteRole(roleToCreate,true);
    }

    /**
     *
     */
    @Test
    public void testGetAllRoleInfo() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RoleClient roleClient = new RoleClient(serviceTicket);

        RoleListing roleListing = roleClient.getAllRoles();

        TestCase.assertNotNull(roleListing);
        TestCase.assertTrue("Role listing must be greater than '0'.",
                roleListing.getListingCount() > 0);
        TestCase.assertNotNull("Role Listing must be set.",roleListing.getListing());
        TestCase.assertNotNull("Role must be set.",roleListing.getListing().get(0));
    }

    /**
     *
     */
    @Test
    public void testGetRoleInfoById() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        RoleClient userClient = new RoleClient(serviceTicket);

        Role role = userClient.getRoleById(1L);
        TestCase.assertNotNull(role);
    }
}
