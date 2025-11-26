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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.user.UserFieldListing;
import com.fluidbpm.program.api.vo.user.UserListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Java Web Service Client for User related actions.
 *
 * @author jasonbruwer
 * @see com.fluidbpm.program.api.vo.ABaseFluidGSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.User
 * @see User
 * @since v1.0
 */
public class UserClient extends ABaseClientWS {

    private static final String JSON_TAG_DATA = "data";

    private static final String JSON_TAG_EXISTING = "existing";
    private static final String JSON_TAG_NEW = "new";
    private static final String JSON_TAG_CONFIRM_NEW = "confirm_new";

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam   The Server issued Service Ticket.
     */
    public UserClient(String endpointBaseUrlParam, String serviceTicketParam) {
        super(endpointBaseUrlParam);
        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code User} with the Email, Fields and
     * Roles inside the {@code userParam}.
     *
     * @param user The {@code User} to create.
     * @return The Created User.
     * @see com.fluidbpm.program.api.vo.user.User
     * @see Field
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public User createUser(User user) {
        if (user != null && this.serviceTicket != null) user.setServiceTicket(this.serviceTicket);

        return new User(this.putJson(
                user, WS.Path.User.Version1.userCreate()));
    }

    /**
     * Updates an existing {@code User} with the Email, Fields and
     * Roles inside the {@code userParam}.
     *
     * @param user The User to update.
     * @return The Updated User.
     * @see com.fluidbpm.program.api.vo.user.User
     * @see Field
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public User updateUser(User user) {
        if (user != null && this.serviceTicket != null) user.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(
                user, WS.Path.User.Version1.userUpdate()));
    }

    /**
     * Activate an existing {@code User} that is currently
     * Deactivated.
     *
     * @param user The User to activate.
     * @return The Activated User.
     * @see com.fluidbpm.program.api.vo.user.User
     * @see Field
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public User activateUser(User user) {
        if (user != null && this.serviceTicket != null) user.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(user, WS.Path.User.Version1.userActivate()));
    }

    /**
     * Deactivate an existing {@code User} that is currently
     * Active.
     *
     * @param user The User to De-Activate.
     * @return The DeActivated User.
     * @see com.fluidbpm.program.api.vo.user.User
     * @see Field
     * @see com.fluidbpm.program.api.vo.role.Role
     */
    public User deActivateUser(User user) {
        if (user != null && this.serviceTicket != null) user.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(
                user, WS.Path.User.Version1.userDeActivate()));
    }

    /**
     * Increment the invalid login count for {@code userParam}.
     *
     * @param user The User to increment invalid count for.
     * @return The User where invalid count performed.
     * @see com.fluidbpm.program.api.vo.user.User
     */
    public User incrementInvalidLoginForUser(User user) {
        if (user != null && this.serviceTicket != null) user.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(user, WS.Path.User.Version1.incrementInvalidLogin()));
    }

    /**
     * Request a password reset based on email or username.
     *
     * @param usernameEmail The username or email address to request a password reset.
     */
    public void sendPasswordResetRequest(String usernameEmail) {
        this.postJson(new User(usernameEmail), WS.Path.User.Version1.requestPasswordReset());
    }

    /**
     * Change the password for the currently logged in user.
     *
     * @param existingPasswordParam   The current password.
     * @param newPasswordParam        The new password.
     * @param confirmNewPasswordParam The new password again.
     * @return {@code User} which password was changed.
     */
    public User changePasswordForLoggedInUser(
            String existingPasswordParam,
            String newPasswordParam,
            String confirmNewPasswordParam
    ) {
        User toChangePasswordFor = new User();

        toChangePasswordFor.setServiceTicket(this.serviceTicket);

        String existingPassword =
                existingPasswordParam == null ? UtilGlobal.EMPTY : existingPasswordParam;
        String newPassword =
                newPasswordParam == null ? UtilGlobal.EMPTY : newPasswordParam;
        String confirmNewPassword =
                confirmNewPasswordParam == null ? UtilGlobal.EMPTY : confirmNewPasswordParam;

        JsonObject passwordClear = new JsonObject();
        passwordClear.addProperty(JSON_TAG_EXISTING, existingPassword);
        passwordClear.addProperty(JSON_TAG_NEW, newPassword);
        passwordClear.addProperty(JSON_TAG_CONFIRM_NEW, confirmNewPassword);

        toChangePasswordFor.setPasswordClear(passwordClear.toString());

        return new User(this.postJson(toChangePasswordFor, WS.Path.User.Version1.changePassword()));
    }

    /**
     * Deletes the {@code User} provided.
     * Id must be set on the {@code User}.
     *
     * @param userToDelete The User to Delete.
     * @return The deleted User.
     */
    public User deleteUser(User userToDelete) {
        if (userToDelete != null) userToDelete.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(userToDelete, WS.Path.User.Version1.userDelete()));
    }

    /**
     * Deletes the {@code User} provided.
     * Id must be set on the {@code User}.
     *
     * @param userToDelete     The User to Delete.
     * @param forcefullyDelete Delete the User forcefully.
     * @return The deleted User.
     */
    public User deleteUser(User userToDelete, boolean forcefullyDelete) {
        if (userToDelete != null) userToDelete.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(userToDelete, WS.Path.User.Version1.userDelete(forcefullyDelete)));
    }

    /**
     * Retrieves user information for the logged in {@code User}.
     *
     * @return User information.
     * @see User
     */
    public User getLoggedInUserInformation() {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(
                userToGetInfoFor,
                WS.Path.User.Version1.userInformation())
        );
    }

    /**
     * Retrieves user information for the provided {@code usernameParam}.
     *
     * @param username The username of the user to retrieve info for.
     * @return User information.
     * @see User
     */
    public User getUserWhereUsername(String username) {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setUsername(username);
        userToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(
                userToGetInfoFor, WS.Path.User.Version1.getByUsername())
        );
    }

    /**
     * Retrieves user information for the provided {@code emailAddressParam}.
     * <p>
     * The email address must be confirmed.
     *
     * @param emailAddress The confirmed Email of the user to retrieve info for.
     * @return User information.
     * @see User
     */
    public User getUserWhereEmail(String emailAddress) {
        User userToGetInfoFor = new User();

        if (emailAddress != null) {
            List<String> emailAdd = new ArrayList();
            emailAdd.add(emailAddress);
            userToGetInfoFor.setEmailAddresses(emailAdd);
        }

        userToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new User(this.postJson(
                userToGetInfoFor, WS.Path.User.Version1.getByEmail())
        );
    }

    /**
     * Retrieves user information for the provided {@code userIdParam}.
     *
     * @param userId The ID of the {@code User} to retrieve info for.
     * @return User information.
     * @see User
     */
    public User getUserById(Long userId) {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setId(userId);

        userToGetInfoFor.setServiceTicket(this.serviceTicket);
        return new User(this.postJson(userToGetInfoFor, WS.Path.User.Version1.getById()));
    }

    /**
     * Retrieves all user information.
     *
     * @return User information.
     * @see UserListing
     */
    public UserListing getAllUsers() {
        UserListing userToGetInfoFor = new UserListing();
        userToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new UserListing(this.postJson(
                userToGetInfoFor,
                WS.Path.User.Version1.getAllUsers())
        );
    }

    /**
     * Retrieves all Users by {@code jobViewParam}.
     *
     * @param jobView The {@link JobView} to get users for.
     * @return User information at {@code UserListing}
     * @see UserListing
     * @see JobView
     */
    public UserListing getAllUsersByJobView(JobView jobView) {
        if (jobView != null) jobView.setServiceTicket(this.serviceTicket);
        return new UserListing(this.postJson(
                jobView,
                WS.Path.User.Version1.getAllUsersByJobView())
        );
    }

    /**
     * Retrieves all Users by {@code roleParam}.
     *
     * @param role The {@link Role} to get users for.
     * @return User information at {@code UserListing}
     * @see UserListing
     * @see Role
     */
    public UserListing getAllUsersByRole(Role role) {
        if (role != null) role.setServiceTicket(this.serviceTicket);

        return new UserListing(this.postJson(
                role, WS.Path.User.Version1.getAllUsersByRole())
        );
    }

    /**
     * Retrieves all Users by {@code roleParam}.
     *
     * @param loggedInSince The date for last logged in.
     * @return User information at {@code UserListing}
     * @see UserListing
     */
    public UserListing getAllUsersWhereLoggedInSince(Date loggedInSince) {
        User userToPost = new User();
        userToPost.setLoggedInDateTime(loggedInSince);

        userToPost.setServiceTicket(this.serviceTicket);

        return new UserListing(this.postJson(
                userToPost, WS.Path.User.Version1.getAllUsersWhereLoggedInSince())
        );
    }

    /**
     * Retrieves all user field values information by the {@code userParam}.
     *
     * @param user The {@code User} to retrieve the field values for.
     * @return User information.
     * @see UserFieldListing
     */
    public UserFieldListing getAllUserFieldValuesByUser(User user) {
        if (user == null) return null;

        user.setServiceTicket(this.serviceTicket);
        return new UserFieldListing(this.postJson(
                user,
                WS.Path.User.Version1.getUserFieldValuesByUser())
        );
    }

    /**
     * Retrieve the gravatar bytes by email.
     * The size will be 50x50.
     *
     * @param emailAddress The email to use for the gravatar.
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForEmail(String emailAddress) {
        return this.getGravatarForEmail(emailAddress, 50);
    }

    /**
     * Retrieve the gravatar bytes by email.
     *
     * @param emailAddress The email to use for the gravatar.
     * @param size         The pixel dimension for the image.
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForEmail(String emailAddress, int size) {
        try {
            JsonObject gravatarJSONObj = this.getJson(WS.Path.User.Version1.getGravatarByEmail(emailAddress, size));
            if (!gravatarJSONObj.has(JSON_TAG_DATA) || gravatarJSONObj.get(JSON_TAG_DATA).isJsonNull()) return null;
            String base64Text = gravatarJSONObj.get(JSON_TAG_DATA).getAsString();
            if (UtilGlobal.isBlank(base64Text)) return null;

            return UtilGlobal.decodeBase64(base64Text);
        } catch (UnsupportedEncodingException unsEncExcept) {
            throw new FluidClientException(unsEncExcept.getMessage(),
                    unsEncExcept, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Retrieve the gravatar bytes for Fluid user.
     * The size will be 50x50.
     *
     * @param user The user to get the gravatar for.
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForUser(User user) {
        return this.getGravatarForUser(user, 50);
    }

    /**
     * Retrieve the gravatar bytes for Fluid user.
     *
     * @param user The user to get the gravatar for.
     * @param size The pixel dimension for the image.
     * @return JPEG image bytes.
     */
    public byte[] getGravatarForUser(User user, int size) {
        if (user == null) return null;

        if ((user.getId() == null || user.getId().longValue() < 1L) &&
                UtilGlobal.isBlank(user.getUsername())) return null;

        JsonObject gravatarJSONObj = this.postJson(
                user, WS.Path.User.Version1.getGravatarByUser(size)
        );

        if (!gravatarJSONObj.has(JSON_TAG_DATA) || gravatarJSONObj.get(JSON_TAG_DATA).isJsonNull()) {
            return null;
        }

        String base64Text = gravatarJSONObj.get(JSON_TAG_DATA).getAsString();
        if (UtilGlobal.isBlank(base64Text)) return null;
        return UtilGlobal.decodeBase64(base64Text);
    }
}
