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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.user.UserFieldListing;
import com.fluidbpm.program.api.vo.user.UserListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for User related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.User
 * @see User
 */
public class UserClient extends ABaseClientWS {

    private static final String JSON_TAG_DATA = "data";

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
     * @see com.fluidbpm.program.api.vo.user.User
     * @see com.fluidbpm.program.api.vo.Field
     * @see com.fluidbpm.program.api.vo.role.Role
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
     * @see com.fluidbpm.program.api.vo.user.User
     * @see com.fluidbpm.program.api.vo.Field
     * @see com.fluidbpm.program.api.vo.role.Role
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
     * @see com.fluidbpm.program.api.vo.user.User
     * @see com.fluidbpm.program.api.vo.Field
     * @see com.fluidbpm.program.api.vo.role.Role
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
     * @see com.fluidbpm.program.api.vo.user.User
     * @see com.fluidbpm.program.api.vo.Field
     * @see com.fluidbpm.program.api.vo.role.Role
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
     * @see com.fluidbpm.program.api.vo.user.User
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
     * Retrieves user information for the provided {@code emailAddressParam}.
     *
     * The email address must be confirmed.
     *
     * @param emailAddressParam The confirmed Email of the user to retrieve info for.
     * @return User information.
     *
     * @see User
     */
    public User getUserWhereEmail(String emailAddressParam)
    {
        User userToGetInfoFor = new User();

        if(emailAddressParam != null)
        {
            List<String> emailAdd = new ArrayList();
            emailAdd.add(emailAddressParam);

            userToGetInfoFor.setEmailAddresses(emailAdd);
        }

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new User(this.postJson(
                    userToGetInfoFor, WS.Path.User.Version1.getByEmail()));
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

    /**
     * Retrieve the gravatar bytes by email.
     * The size will be 50x50.
     * 
     * @param emailAddressParam The email to use for the gravatar.
     *
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForEmail(
            String emailAddressParam)
    {
        return this.getGravatarForEmail(
                emailAddressParam,
                50);
    }

    /**
     * Retrieve the gravatar bytes by email.
     *
     * @param emailAddressParam The email to use for the gravatar.
     * @param sizeParam The pixel dimension for the image.
     *
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForEmail(
            String emailAddressParam,
            int sizeParam)
    {
        try {
            JSONObject gravatarJSONObj =
                    this.getJson(
                            WS.Path.User.Version1.getGravatarByEmail(
                                    emailAddressParam, sizeParam));

            String base64Text = gravatarJSONObj.optString(JSON_TAG_DATA,"");
            if(base64Text == null || base64Text.isEmpty())
            {
                return null;
            }
            
            return UtilGlobal.decodeBase64(base64Text);
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
        //
        catch (UnsupportedEncodingException unsEncExcept) {
            throw new FluidClientException(unsEncExcept.getMessage(),
                    unsEncExcept, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Retrieve the gravatar bytes for Fluid user.
     * The size will be 50x50.
     *
     * @param userParam The user to get the gravatar for.
     *
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForUser(User userParam)
    {
        return this.getGravatarForUser(userParam, 50);
    }
    
    /**
     * Retrieve the gravatar bytes for Fluid user.
     *
     * @param userParam The user to get the gravatar for.
     * @param sizeParam The pixel dimension for the image.
     *
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForUser(User userParam, int sizeParam)
    {
        if(userParam == null)
        {
            return null;
        }

        try {
            JSONObject gravatarJSONObj = this.postJson(
                            userParam,
                            WS.Path.User.Version1.getGravatarByUser(sizeParam));

            String base64Text = gravatarJSONObj.optString(JSON_TAG_DATA,"");
            if(base64Text == null || base64Text.isEmpty())
            {
                return null;
            }

            return UtilGlobal.decodeBase64(base64Text);
        }
        //JSON problem...
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
