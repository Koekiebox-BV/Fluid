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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.user.UserFieldListing;
import com.fluidbpm.program.api.vo.user.UserListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.role.RoleClient;
import com.fluidbpm.ws.client.v1.role.TestRoleClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics
    {
        public static final class Create
        {
            public static final String USERNAME = "junitTestingUser";
            public static final String PASSWORD = "password";

            public static final String EMAIL = "patel@kapper.com";
        }

        public static final class Update
        {
            public static final String USERNAME = "junitTestingUserUpdated";
        }
    }

    /**
     *
     */
    @Before
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        //this.loginClient = new LoginClient("http://fluid.sahousingclub.co.za/fluid-ws/");
        this.loginClient = new LoginClient(BASE_URL);
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
    public void testCreateUser() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User userToCreate = new User();
        userToCreate.setUsername(TestStatics.Create.USERNAME);
        userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

        userToCreate = userClient.createUser(userToCreate);

        TestCase.assertNotNull(
                "No user created or not returned.",
                userToCreate);

        userClient.deleteUser(userToCreate);
    }

    /**
     * 
     */
    @Test
    public void testCreateAndUpdateUser() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User userToCreate = new User();
        userToCreate.setUsername(TestStatics.Create.USERNAME);
        userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

        User userToUpdate = userClient.createUser(userToCreate);

        TestCase.assertNotNull(userToUpdate);

        userToUpdate.setUsername(TestStatics.Update.USERNAME);

        try
        {
            userToUpdate = userClient.updateUser(userToUpdate);
        }
        finally {
            userClient.deleteUser(userToUpdate, true);
        }
    }


    /**
     *
     */
    @Test
    public void testActivateAndDeactivateUser() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User userToCreate = new User();
        userToCreate.setUsername(TestStatics.Create.USERNAME);
        userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

        User userToUpdate = userClient.createUser(userToCreate);

        TestCase.assertNotNull(userToUpdate);
        TestCase.assertTrue("User should be active after creation.",userToUpdate.isActive());

        //User should now be de-activated...
        userToUpdate = userClient.deActivateUser(userToUpdate);

        TestCase.assertNotNull(userToUpdate);
        TestCase.assertFalse("User should NOT be active after creation.",userToUpdate.isActive());

        //Activate user...
        userToUpdate = userClient.activateUser(userToUpdate);

        TestCase.assertNotNull(userToUpdate);
        TestCase.assertTrue("User should be active after activation.",userToUpdate.isActive());

        userClient.deleteUser(userToUpdate, true);
    }

    /**
     *
     */
    @Test
    public void testCreateUserWithRole() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        //Create the role...
        RoleClient roleClient = new RoleClient(BASE_URL, serviceTicket);

        Role roleToAssociate = new Role();
        roleToAssociate.setName(TestRoleClient.TestStatics.Create.ROLE_NAME);
        roleToAssociate.setDescription(TestRoleClient.TestStatics.Create.ROLE_DESCRIPTION);
        roleToAssociate.setAdminPermissions(TestRoleClient.TestStatics.Create.PERMISSIONS);

        roleToAssociate = roleClient.createRole(roleToAssociate);
        roleToAssociate.setId(null);

        //Created... Test...
        TestCase.assertNotNull(roleToAssociate);

        User userToCreate = new User();
        userToCreate.setUsername(TestStatics.Create.USERNAME);
        userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

        List<Role> roleList = new ArrayList();
        roleList.add(roleToAssociate);
        userToCreate.setRoles(roleList);

        userClient.createUser(userToCreate);

        //Fetch the user and check role...
        User createdUser = userClient.getUserWhereUsername(userToCreate.getUsername());

        TestCase.assertNotNull(createdUser);
        TestCase.assertFalse("Role listing must be greater than '0'.",
                createdUser.getRoles().isEmpty());

        //Delete...
        userClient.deleteUser(createdUser, true);
        roleClient.deleteRole(roleToAssociate, true);
    }

    /**
     *
     */
    @Test
    public void testUpdateUserRole() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        //Create the role...
        RoleClient roleClient = new RoleClient(BASE_URL, serviceTicket);

        Role roleToAssociate = new Role();
        roleToAssociate.setName(TestRoleClient.TestStatics.Create.ROLE_NAME);
        roleToAssociate.setDescription(TestRoleClient.TestStatics.Create.ROLE_DESCRIPTION);
        roleToAssociate.setAdminPermissions(TestRoleClient.TestStatics.Create.PERMISSIONS);

        roleToAssociate = roleClient.createRole(roleToAssociate);

        //Created... Test...
        TestCase.assertNotNull(roleToAssociate);

        User userToCreate = new User();
        userToCreate.setUsername(TestStatics.Create.USERNAME);
        userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

        User userToUpdate = userClient.createUser(userToCreate);
        TestCase.assertNotNull(userToUpdate);

        List<Role> roleList = new ArrayList();

        Role roleToAdd = new Role();
        roleToAdd.setName(TestRoleClient.TestStatics.Create.ROLE_NAME);

        roleList.add(roleToAdd);
        userToUpdate.setRoles(roleList);

        userToUpdate = userClient.updateUser(userToUpdate);

        TestCase.assertNotNull(userToUpdate);
        TestCase.assertFalse("Role listing must be greater than '0'.",
                userToUpdate.getRoles().isEmpty());

        //Delete...
        userClient.deleteUser(userToUpdate, true);
        roleClient.deleteRole(roleToAssociate, true);
    }

    /**
     *
     */
    @Test
    public void testGetLoggedUserInfo() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User loggedInUser = userClient.getLoggedInUserInformation();
        TestCase.assertNotNull(loggedInUser);
    }

    /**
     *
     */
    @Test
    public void testGetAllUserInfo() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        UserListing userListing = userClient.getAllUsers();

        TestCase.assertNotNull(userListing);
        TestCase.assertTrue("User listing must be greater than '0'.",
                userListing.getListingCount() > 0);
        TestCase.assertNotNull("User Listing must be set.",userListing.getListing());
        TestCase.assertNotNull("User must be set.",userListing.getListing().get(0));
    }

    /**
     *
     */
    @Test
    public void testIncrementInvalidLogin() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User invalidLogin = new User();
        invalidLogin.setUsername(USERNAME);

        User user = userClient.incrementInvalidLoginForUser(
                invalidLogin);

        TestCase.assertNotNull(user);
    }

    /**
     *
     */
    @Test
    public void testGetAllUserFieldValues() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User loggedIn = userClient.getLoggedInUserInformation();
        
        UserFieldListing userFieldListing = userClient.getAllUserFieldValuesByUser(loggedIn);

        TestCase.assertNotNull(userFieldListing);
        TestCase.assertTrue("User Field Value listing must be greater than '0'.",
                userFieldListing.getListingCount() > 0);
        TestCase.assertNotNull("User Field Value Listing must be set.",userFieldListing.getListing());
        TestCase.assertNotNull("User Field Value must be set.",userFieldListing.getListing().get(0));
    }

    /**
     *
     */
    @Test
    public void testGetUserInfoByUsername() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User user = userClient.getUserWhereUsername(ABaseTestCase.USERNAME);
        TestCase.assertNotNull(user);
    }

    /**
     *
     */
    @Test
    public void testGetUserInfoById() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        User user = userClient.getUserWhereUsername(ABaseTestCase.USERNAME);
        TestCase.assertNotNull(user);

        User userById = userClient.getUserById(user.getId());
        TestCase.assertNotNull(userById);
    }

    /**
     *
     */
    @Test
    public void testGetUserGravatar() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        byte[] ralfGrav = userClient.getGravatarForEmail("info@ralfebert.de");
        TestCase.assertNotNull(ralfGrav);
        TestCase.assertTrue("Expected some bytes.",ralfGrav.length > 0);
    }

    /**
     *
     */
    @Test
    @Ignore
    public void testGetUserGravatarByUser() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(BASE_URL, serviceTicket);

        byte[] ralfGrav = userClient.getGravatarForUser(new User("admin"));
        TestCase.assertNotNull(ralfGrav);
        TestCase.assertTrue("Expected some bytes.",ralfGrav.length > 0);
    }
}
