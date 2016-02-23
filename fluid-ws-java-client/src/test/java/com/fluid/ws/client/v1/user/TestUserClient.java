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

package com.fluid.ws.client.v1.user;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.user.User;
import com.fluid.program.api.vo.user.UserListing;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserClient extends ABaseTestCase {

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
    public void testCreateUser() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User userToCreate = new User();
        userToCreate.setUsername("junitTestingUser");

        userToCreate = userClient.createUser(userToCreate);

        TestCase.assertNotNull(userToCreate);

        userClient.deActivateUser(userToCreate);
    }

    /**
     *
     */
    @Test
    public void testUpdateUser() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User userToCreate = new User();
        userToCreate.setUsername("junitTestingUser");

        User userToUpdate = userClient.createUser(userToCreate);

        TestCase.assertNotNull(userToUpdate);

        userToUpdate.setUsername("junitTestingUser");
        userToUpdate = userClient.updateUser(userToUpdate);

        userClient.deActivateUser(userToUpdate);
    }

    /**
     *
     */
    @Test
    public void testGetLoggedUserInfo() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User loggedInUser = userClient.getLoggedInUserInformation();
        TestCase.assertNotNull(loggedInUser);
    }

    /**
     *
     */
    @Test
    public void testGetAllUserInfo() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

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
    public void testGetUserInfoByUsername() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User user = userClient.getUserWhereUsername("admin");
        TestCase.assertNotNull(user);
    }

    /**
     *
     */
    @Test
    public void testGetUserInfoById() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User user = userClient.getUserWhereUsername("admin");
        TestCase.assertNotNull(user);

        User userById = userClient.getUserById(user.getId());
        TestCase.assertNotNull(userById);
    }
}
