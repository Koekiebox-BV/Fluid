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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.user.UserNotification;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 18/04/05.
 * @since 1.8
 */
public class TestUserNotificationClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    @Before
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
        
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
    public void testCreateUserNotificationCRUD() {
        
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserNotificationClient userNotificationClient = new
                UserNotificationClient(BASE_URL, serviceTicket);

        //Create the User Notification...
        UserNotification userNotificationToCreate = new UserNotification();
        userNotificationToCreate.setMessage("This is a message.");
        userNotificationToCreate.setUserNotificationType(
                UserNotification.UserNotificationType.CRITICAL);

        //1. Create...
        UserNotification userNotificationCreated =
                userNotificationClient.createUserNotification(
                        userNotificationToCreate);

        Assert.assertNotNull(userNotificationCreated);
        Assert.assertNotNull(userNotificationCreated.getId());

        //Update the Notification...
        userNotificationCreated.setMessage("This is an UPDATED message.");
        UserNotification userNotificationUpdated =
                userNotificationClient.updateUserNotification(
                        userNotificationCreated);

        Assert.assertNotNull(userNotificationUpdated);
        Assert.assertNotNull(userNotificationUpdated.getId());

        //Get by Id...
        UserNotification byId =
                userNotificationClient.getUserNotificationById(
                        userNotificationUpdated.getId());

        Assert.assertNotNull(byId);
        Assert.assertNotNull(byId.getId());

        //Cleanup...
        userNotificationClient.deleteUserNotification(userNotificationCreated);

        userNotificationClient.closeAndClean();
    }

    /**
     *
     */
    @Test(expected = FluidClientException.class)
    public void testFetchUserInformationUnreadExcept() {

        if (!this.loginClient.isConnectionValid()) {

            throw new FluidClientException(
                    "Server is not up.",
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserNotificationClient userNotificationClient = new
                UserNotificationClient(BASE_URL, serviceTicket);

        //1. Create...
        List<UserNotification> allUserNotiForUser =
                userNotificationClient.getAllUnReadByLoggedInUser(
                        1000,0);

        Assert.assertNotNull(allUserNotiForUser);
        Assert.assertFalse(allUserNotiForUser.isEmpty());

        allUserNotiForUser.forEach(itm ->
        {
            userNotificationClient.deleteUserNotification(itm);
        });

        userNotificationClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testFetchUserInformationUnread() {

        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserNotificationClient userNotificationClient = new
                UserNotificationClient(BASE_URL, serviceTicket);

        //Create the User Notification...
        UserNotification userNotificationToCreate = new UserNotification();
        userNotificationToCreate.setMessage("This is a message.");
        userNotificationToCreate.setUserNotificationType(
                UserNotification.UserNotificationType.CRITICAL);

        //1. Create...
        userNotificationClient.createUserNotification(userNotificationToCreate);
        
        List<UserNotification> allUserNotiForUser =
                userNotificationClient.getAllUnReadByLoggedInUser(
                        1000,0);

        Assert.assertNotNull(allUserNotiForUser);
        Assert.assertFalse(allUserNotiForUser.isEmpty());

        allUserNotiForUser.forEach(itm ->
        {
            userNotificationClient.deleteUserNotification(itm);
        });
        
        userNotificationClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testFetchUserInformationRead() {

        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserNotificationClient userNotificationClient = new
                UserNotificationClient(BASE_URL, serviceTicket);

        //Create the User Notification...
        UserNotification userNotificationToCreate = new UserNotification();
        userNotificationToCreate.setMessage("This is a message.");
        userNotificationToCreate.setUserNotificationType(
                UserNotification.UserNotificationType.CRITICAL);

        //1. Create...
        UserNotification created =
                userNotificationClient.createUserNotification(userNotificationToCreate);

        //2. Mark as Read...
        userNotificationClient.markUserNotificationAsRead(created);

        //3. There should be one...
        List<UserNotification> allUserNotiForUser =
                userNotificationClient.getAllReadByLoggedInUser(
                        1000,0);

        Assert.assertNotNull(allUserNotiForUser);
        Assert.assertFalse(allUserNotiForUser.isEmpty());

        allUserNotiForUser.forEach(itm ->
        {
            userNotificationClient.deleteUserNotification(itm);
        });

        userNotificationClient.closeAndClean();
    }
}
