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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.user.User;
import com.fluid.program.api.vo.user.UserFieldListing;
import com.fluid.program.api.vo.user.UserListing;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for User related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.User
 * @see User
 */
public class UserClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public UserClient(String endpointBaseUrlParam, String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code User} with the Email, Fields and
     * Roles inside the {@code userParam}.
     *
     * @param userParam The {@code User} to create.
     * @return The Created User.
     *
     * @see com.fluid.program.api.vo.user.User
     * @see com.fluid.program.api.vo.Field
     * @see com.fluid.program.api.vo.role.Role
     */
    public User createUser(User userParam)
    {
        if(userParam != null && this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.putJson(
                userParam, WS.Path.User.Version1.userCreate()));
    }

    /**
     * Updates an existing {@code User} with the Email, Fields and
     * Roles inside the {@code userParam}.
     *
     * @param userParam The User to update.
     * @return The Updated User.
     *
     * @see com.fluid.program.api.vo.user.User
     * @see com.fluid.program.api.vo.Field
     * @see com.fluid.program.api.vo.role.Role
     */
    public User updateUser(User userParam)
    {
        if(userParam != null && this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(
                userParam, WS.Path.User.Version1.userUpdate()));
    }

    /**
     * Activate an existing {@code User} that is currently
     * Deactivated.
     *
     * @param userParam The User to activate.
     * @return The Activated User.
     *
     * @see com.fluid.program.api.vo.user.User
     * @see com.fluid.program.api.vo.Field
     * @see com.fluid.program.api.vo.role.Role
     */
    public User activateUser(User userParam)
    {
        if(userParam != null && this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(
                userParam,
                WS.Path.User.Version1.userActivate()));
    }

    /**
     * Deactivate an existing {@code User} that is currently
     * Active.
     *
     * @param userParam The User to De-Activate.
     * @return The DeActivated User.
     *
     * @see com.fluid.program.api.vo.user.User
     * @see com.fluid.program.api.vo.Field
     * @see com.fluid.program.api.vo.role.Role
     */
    public User deActivateUser(User userParam)
    {
        if(userParam != null && this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(
                userParam,
                WS.Path.User.Version1.userDeActivate()));
    }

    /**
     * Increment the invalid login count for {@code userParam}.
     *
     * @param userParam The User to increment invalid count for.
     * @return The User where invalid count performed.
     *
     * @see com.fluid.program.api.vo.user.User
     */
    public User incrementInvalidLoginForUser(User userParam)
    {
        if(userParam != null && this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(
                userParam,
                WS.Path.User.Version1.incrementInvalidLogin()));
    }

    /**
     * Deletes the {@code User} provided.
     * Id must be set on the {@code User}.
     *
     * @param userToDeleteParam The User to Delete.
     * @return The deleted User.
     */
    public User deleteUser(User userToDeleteParam)
    {
        if(userToDeleteParam != null && this.serviceTicket != null)
        {
            userToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(userToDeleteParam,
                WS.Path.User.Version1.userDelete()));
    }

    /**
     * Deletes the {@code User} provided.
     * Id must be set on the {@code User}.
     *
     * @param userToDeleteParam The User to Delete.
     * @param forcefullyDeleteParam Delete the User forcefully.
     * @return The deleted User.
     */
    public User deleteUser(
            User userToDeleteParam,
            boolean forcefullyDeleteParam)
    {
        if(userToDeleteParam != null && this.serviceTicket != null)
        {
            userToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new User(this.postJson(userToDeleteParam,
                WS.Path.User.Version1.userDelete(forcefullyDeleteParam)));
    }

    /**
     * Retrieves user information for the logged in {@code User}.
     *
     * @return User information.
     *
     * @see User
     */
    public User getLoggedInUserInformation()
    {
        User userToGetInfoFor = new User();

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new User(this.postJson(
                    userToGetInfoFor,
                    WS.Path.User.Version1.userInformation()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves user information for the provided {@code usernameParam}.
     *
     * @param usernameParam The username of the user to retrieve info for.
     * @return User information.
     *
     * @see User
     */
    public User getUserWhereUsername(String usernameParam)
    {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setUsername(usernameParam);

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new User(this.postJson(
                    userToGetInfoFor, WS.Path.User.Version1.getByUsername()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves user information for the provided {@code userIdParam}.
     *
     * @param userIdParam The ID of the {@code User} to retrieve info for.
     * @return User information.
     *
     * @see User
     */
    public User getUserById(Long userIdParam)
    {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setId(userIdParam);

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new User(this.postJson(
                    userToGetInfoFor, WS.Path.User.Version1.getById()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all user information.
     *
     * @return User information.
     *
     * @see UserListing
     */
    public UserListing getAllUsers()
    {
        UserListing userToGetInfoFor = new UserListing();

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserListing(this.postJson(
                    userToGetInfoFor,
                    WS.Path.User.Version1.getAllUsers()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all user field values information by the {@code userParam}.
     *
     * @param userParam The {@code User} to retrieve the field values for.
     * @return User information.
     *
     * @see UserFieldListing
     */
    public UserFieldListing getAllUserFieldValuesByUser(User userParam)
    {
        if(userParam == null)
        {
            return null;
        }

        if(this.serviceTicket != null)
        {
            userParam.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserFieldListing(this.postJson(
                    userParam,
                    WS.Path.User.Version1.getUserFieldValuesByUser()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

}
