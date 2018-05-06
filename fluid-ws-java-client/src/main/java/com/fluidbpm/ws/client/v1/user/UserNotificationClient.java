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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.user.UserNotification;
import com.fluidbpm.program.api.vo.user.UserNotificationListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for User Notification related actions.
 *
 * @author jasonbruwer
 * @since v1.8 2018-04-05
 *
 * @see JSONObject
 * @see WS.Path.UserNotification
 * @see com.fluidbpm.program.api.vo.user.UserNotification
 */
public class UserNotificationClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public UserNotificationClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code UserNotification} for a user.
     * The user will be notified through the Fluid User Dashboard or 3rd Party application.
     *
     * @param userNotificationParam The {@code UserNotification} to create.
     * @return The Created User Notification.
     *
     * @see com.fluidbpm.program.api.vo.user.User
     * @see UserNotification
     */
    public UserNotification createUserNotification(UserNotification userNotificationParam)
    {
        if(userNotificationParam != null && this.serviceTicket != null)
        {
            userNotificationParam.setServiceTicket(this.serviceTicket);
        }

        return new UserNotification(this.putJson(
                userNotificationParam,
                WS.Path.UserNotification.Version1.userNotificationCreate()));
    }

    /**
     * Creates a new {@code UserNotification} for a user.
     * The user will be notified through the Fluid User Dashboard or 3rd Party application.
     *
     * @param userNotificationParam The User Notification to update.
     * @return The Updated UserNotification.
     *
     * @see com.fluidbpm.program.api.vo.user.User
     * @see UserNotification
     */
    public UserNotification updateUserNotification(
            UserNotification userNotificationParam)
    {
        if(userNotificationParam != null && this.serviceTicket != null)
        {
            userNotificationParam.setServiceTicket(this.serviceTicket);
        }

        return new UserNotification(this.postJson(
                userNotificationParam,
                WS.Path.UserNotification.Version1.userNotificationUpdate()));
    }

    /**
     * Marks the {@code userNotificationParam} notification as read.
     *
     * Notification marked as read asynchronously.
     *
     * @param userNotificationParam The User Notification to mark as read.
     *
     * @return The Updated UserNotification.
     *
     * @see com.fluidbpm.program.api.vo.user.User
     * @see UserNotification
     */
    public UserNotification markUserNotificationAsRead(
            UserNotification userNotificationParam)
    {
        return this.markUserNotificationAsRead(userNotificationParam,
                true);
    }
    
    /**
     * Marks the {@code userNotificationParam} notification as read.
     *
     * @param userNotificationParam The User Notification to mark as read.
     * @param asyncParam Should the notification mark-as-read asynchronously.
     *                   
     * @return The Updated UserNotification.
     *
     * @see com.fluidbpm.program.api.vo.user.User
     * @see UserNotification
     */
    public UserNotification markUserNotificationAsRead(
            UserNotification userNotificationParam,
            boolean asyncParam)
    {
        if(userNotificationParam != null && this.serviceTicket != null)
        {
            userNotificationParam.setServiceTicket(this.serviceTicket);
        }

        return new UserNotification(this.postJson(
                userNotificationParam,
                WS.Path.UserNotification.Version1.userNotificationMarkAsRead(asyncParam)));
    }

    /**
     * Deletes the {@code UserNotification} provided.
     * Id must be set on the {@code UserNotification}.
     *
     * @param userNotificationToDeleteParam The User Notification to Delete.
     * @return The deleted User Notification.
     */
    public UserNotification deleteUserNotification(
            UserNotification userNotificationToDeleteParam)
    {
        if(userNotificationToDeleteParam != null && this.serviceTicket != null)
        {
            userNotificationToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new UserNotification(this.postJson(userNotificationToDeleteParam,
                WS.Path.UserNotification.Version1.userNotificationDelete()));
    }

    /**
     * Deletes the {@code UserNotification} provided.
     * Id must be set on the {@code UserNotification}.
     *
     * @param userNotificationPkParam The Id of the User Notification to fetch.
     * @return The User Notification by PK.
     */
    public UserNotification getUserNotificationById(
            Long userNotificationPkParam)
    {
        UserNotification userNoti = new UserNotification();
        userNoti.setId(userNotificationPkParam);

        if(this.serviceTicket != null)
        {
            userNoti.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserNotification(this.postJson(
                    userNoti, WS.Path.UserNotification.Version1.getById()));
        }
        //Json format issues...
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all {@code READ} User Notification items for the logged in user.
     *
     * @param queryLimitParam The query limit.
     * @param offsetParam The query offset.
     *
     * @return The User Notification items for the logged in {@code User}.
     */
    public List<UserNotification> getAllReadByLoggedInUser(
            int queryLimitParam,
            int offsetParam)
    {
        User loggedInUser = new User();

        if(this.serviceTicket != null)
        {
            loggedInUser.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserNotificationListing(this.postJson(
                    loggedInUser,
                    WS.Path.UserNotification.Version1.getAllReadByUser(
                            queryLimitParam,
                            offsetParam))).getListing();
        }
        //rethrow as a Fluid Client exception.
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all {@code UNREAD} User Notification items for the logged in user.
     *
     * @param queryLimitParam The query limit.
     * @param offsetParam The query offset.
     *
     * @return The User Notification items for the logged in {@code User}.
     */
    public List<UserNotification> getAllUnReadByLoggedInUser(
            int queryLimitParam,
            int offsetParam) {
        
        User loggedInUser = new User();

        if(this.serviceTicket != null)
        {
            loggedInUser.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserNotificationListing(this.postJson(
                    loggedInUser,
                    WS.Path.UserNotification.Version1.getAllUnReadByUser(
                            queryLimitParam, offsetParam
                    ))).getListing();
        }
        //rethrow as a Fluid Client exception.
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
