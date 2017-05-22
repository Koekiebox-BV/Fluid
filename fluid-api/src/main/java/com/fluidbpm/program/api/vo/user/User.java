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

package com.fluidbpm.program.api.vo.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.role.Role;

/**
 * <p>
 *     Represents the Fluid framework User.
 * </p>
 *
 * The following fields are mandatory for creating or updating a User;
 *
 * Username (create and update)
 * Password (create). Needs to be at least 5 characters.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
 * @see com.fluidbpm.program.api.vo.auth0.Connection
 * @see com.fluidbpm.program.api.vo.auth0.NormalizedUserProfile
 */
public class User extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String username;
    private String passwordSha256;
    private String passwordClear;

    private String salt;
    private List<Role> roles;
    private List<String> emailAddresses;
    private List<Field> userFields;

    private boolean active;
    private int invalidLoginCount;

    private Date passwordChangedAt;
    private Date loggedInDateTime;

    private Date dateCreated;
    private Date dateLastUpdated;

    /**
     * The JSON mapping for the {@code User} object.
     */
    public static class JSONMapping
    {
        public static final String USERNAME = "username";

        public static final String ACTIVE = "active";
        public static final String INVALID_LOGIN_COUNT = "invalidLoginCount";

        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
        public static final String PASSWORD_CHANGED_AT = "passwordChangedAt";
        public static final String LOGGED_IN_DATE_TIME = "loggedInDateTime";

        public static final String PASSWORD_SHA_256 = "passwordSha256";
        public static final String PASSWORD_CLEAR = "passwordClear";

        public static final String ROLES = "roles";
        public static final String EMAIL_ADDRESSES = "emailAddresses";
        public static final String SALT = "salt";
        public static final String USER_FIELDS = "userFields";

        /**
         * Elastic specific properties.
         */
        public static final class Elastic
        {
            public static final String USER_ID = "userId";
        }
    }

    /**
     * Default constructor.
     */
    public User() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public User(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Username...
        if (!this.jsonObject.isNull(JSONMapping.USERNAME)) {
            this.setUsername(this.jsonObject.getString(JSONMapping.USERNAME));
        }

        //Password - sha256...
        if (!this.jsonObject.isNull(JSONMapping.PASSWORD_SHA_256)) {
            this.setPasswordSha256(this.jsonObject.getString(JSONMapping.PASSWORD_SHA_256));
        }

        //Password - Clear...
        if (!this.jsonObject.isNull(JSONMapping.PASSWORD_CLEAR)) {
            this.setPasswordClear(this.jsonObject.getString(JSONMapping.PASSWORD_CLEAR));
        }

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_CREATED));

        //Date Last Updated...
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_LAST_UPDATED));

        //Password Changed At...
        this.setPasswordChangedAt(this.getDateFieldValueFromFieldWithName(
                JSONMapping.PASSWORD_CHANGED_AT));

        //Logged In Date Time...
        this.setLoggedInDateTime(this.getDateFieldValueFromFieldWithName(
                JSONMapping.LOGGED_IN_DATE_TIME));

        //Salt...
        if (!this.jsonObject.isNull(JSONMapping.SALT)) {
            this.setSalt(this.jsonObject.getString(JSONMapping.SALT));
        }

        //Active...
        if (!this.jsonObject.isNull(JSONMapping.ACTIVE)) {
            this.setActive(this.jsonObject.getBoolean(JSONMapping.ACTIVE));
        }

        //Invalid Login Count...
        if (!this.jsonObject.isNull(JSONMapping.INVALID_LOGIN_COUNT)) {
            this.setInvalidLoginCount(this.jsonObject.getInt(
                    JSONMapping.INVALID_LOGIN_COUNT));
        }

        //Roles...
        if (!this.jsonObject.isNull(JSONMapping.ROLES)) {

            JSONArray roleListing = this.jsonObject.getJSONArray(JSONMapping.ROLES);

            List<Role> roleListingList = new ArrayList();

            for(int index = 0;index < roleListing.length();index++)
            {
                roleListingList.add(new Role(roleListing.getJSONObject(index)));
            }

            this.setRoles(roleListingList);
        }

        //Email Addresses...
        if (!this.jsonObject.isNull(JSONMapping.EMAIL_ADDRESSES)) {

            JSONArray emailListing =
                    this.jsonObject.getJSONArray(JSONMapping.EMAIL_ADDRESSES);

            List<String> emailAddressList = new ArrayList();

            for(int index = 0;index < emailListing.length();index++)
            {
                emailAddressList.add(emailListing.getString(index));
            }

            this.setEmailAddresses(emailAddressList);
        }

        //User Fields...
        if (!this.jsonObject.isNull(JSONMapping.USER_FIELDS)) {

            JSONArray userFieldListing = this.jsonObject.getJSONArray(JSONMapping.USER_FIELDS);

            List<Field> userFieldListingList = new ArrayList();

            for(int index = 0;index < userFieldListing.length();index++)
            {
                userFieldListingList.add(
                        new Field(userFieldListing.getJSONObject(index)));
            }

            this.setUserFields(userFieldListingList);
        }
    }

    /**
     * Gets whether a user is active.
     *
     * @return A Users state.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether a user is active.
     *
     * @param activeParam A Users active status.
     */
    public void setActive(boolean activeParam) {
        this.active = activeParam;
    }

    /**
     * Gets the {@code Date} when the password was last changed.
     *
     * @return When password was last changed.
     */
    public Date getPasswordChangedAt() {
        return this.passwordChangedAt;
    }

    /**
     * Sets the {@code Date} when the password was last changed.
     *
     * @param passwordChangedAtParam Password Changed at.
     */
    public void setPasswordChangedAt(Date passwordChangedAtParam) {
        this.passwordChangedAt = passwordChangedAtParam;
    }

    /**
     * Gets Users username.
     *
     * @return A Users username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets Users username.
     *
     * @param usernameParam A Users username.
     */
    public void setUsername(String usernameParam) {
        this.username = usernameParam;
    }

    /**
     * Gets The invalid login count for the {@code User}.
     *
     * @return Invalid Login Count.
     */
    public int getInvalidLoginCount() {
        return this.invalidLoginCount;
    }

    /**
     * Sets The invalid login count for the {@code User}.
     *
     * @param invalidLoginCountParam Invalid Login Count.
     */
    public void setInvalidLoginCount(int invalidLoginCountParam) {
        this.invalidLoginCount = invalidLoginCountParam;
    }

    /**
     * Gets The {@code Date} the User was created.
     *
     * @return Date Created.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets The {@code Date} the User was created.
     *
     * @param dateCreatedParam Date Created.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * Gets The {@code Date} the User was last updated.
     *
     * @return Date Last Updated.
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     * Sets The {@code Date} the User was last updated.
     *
     * @param dateLastUpdatedParam Date Last Updated.
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     * Gets The {@code Date} the User last logged in.
     *
     * @return Date Last Logged in.
     */
    public Date getLoggedInDateTime() {
        return this.loggedInDateTime;
    }

    /**
     * Sets The {@code Date} the User last logged in.
     *
     * @param loggedInDateTimeParam Date Last Logged in.
     */
    public void setLoggedInDateTime(Date loggedInDateTimeParam) {
        this.loggedInDateTime = loggedInDateTimeParam;
    }

    /**
     * Gets Users password in Sha256 format.
     *
     * @return Password in Sha256 Base16 format.
     */
    public String getPasswordSha256() {
        return this.passwordSha256;
    }

    /**
     * Sets Users password in Sha256 format.
     *
     * @param passwordSha256Param Password in Sha256 Base16 format.
     */
    public void setPasswordSha256(String passwordSha256Param) {
        this.passwordSha256 = passwordSha256Param;
    }

    /**
     * Gets Users password in the clear (For user create and update).
     *
     * @return Password in clear format.
     */
    public String getPasswordClear() {
        return this.passwordClear;
    }

    /**
     * Sets Users password in the clear (For user create and update).
     *
     * @param passwordClearParam Password in clear.
     */
    public void setPasswordClear(String passwordClearParam) {
        this.passwordClear = passwordClearParam;
    }

    /**
     * Gets List of {@code Role}s for user.
     *
     * @return {@code List} of Roles for {@code User}.
     *
     * @see Role
     */
    public List<Role> getRoles() {
        return this.roles;
    }

    /**
     * Sets List of {@code Role}s for user.
     *
     * @param rolesParam {@code List} of roles associated with a {@code User}.
     *
     * @see Role
     */
    public void setRoles(List<Role> rolesParam) {
        this.roles = rolesParam;
    }

    /**
     * Gets List of Email Addresses for a user.
     *
     * @return {@code List} of Emails for the {@code User}.
     */
    public List<String> getEmailAddresses() {
        return this.emailAddresses;
    }

    /**
     * Sets List of Email addresses for user.
     *
     * @param emailAddressesParam {@code List} of email addresses for a {@code User}.
     */
    public void setEmailAddresses(List<String> emailAddressesParam) {
        this.emailAddresses = emailAddressesParam;
    }

    /**
     * Gets 20 Random ASCII {@code Character}s
     *
     * @return 20 Random ASCII {@code Character}s paired with the {@code User} password.
     */
    public String getSalt() {
        return this.salt;
    }

    /**
     * Sets 20 Random ASCII {@code Character}s
     *
     * @param saltParam 20 Random ASCII {@code Character}s paired with the {@code User} password.
     */
    public void setSalt(String saltParam) {
        this.salt = saltParam;
    }

    /**
     * Gets Customized Fluid Fields associated with a {@code User}.
     *
     * @return {@code List} of {@code User}s
     * @see Field
     */
    public List<Field> getUserFields() {
        return this.userFields;
    }

    /**
     * Sets Customized Fluid Fields associated with a {@code User}.
     *
     * @param userFieldsParam {@code List} of Custom {@code User} fields.
     */
    public void setUserFields(List<Field> userFieldsParam) {
        this.userFields = userFieldsParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code User}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Active...
        returnVal.put(JSONMapping.ACTIVE, this.isActive());

        //Invalid Login Count...
        returnVal.put(JSONMapping.INVALID_LOGIN_COUNT,
                this.getInvalidLoginCount());

        //Username...
        if(this.getUsername() != null)
        {
            returnVal.put(JSONMapping.USERNAME,this.getUsername());
        }

        //Password Sha 256...
        if(this.getPasswordSha256() != null)
        {
            returnVal.put(JSONMapping.PASSWORD_SHA_256,this.getPasswordSha256());
        }

        //Password Clear...
        if(this.getPasswordClear() != null)
        {
            returnVal.put(JSONMapping.PASSWORD_CLEAR,this.getPasswordClear());
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(User.JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(User.JSONMapping.DATE_LAST_UPDATED,
                    this.getDateAsLongFromJson(this.getDateLastUpdated()));
        }

        //Password Changed At...
        if(this.getPasswordChangedAt() != null)
        {
            returnVal.put(JSONMapping.PASSWORD_CHANGED_AT,
                    this.getDateAsLongFromJson(this.getPasswordChangedAt()));
        }

        //Logged In Date Time...
        if(this.getLoggedInDateTime() != null)
        {
            returnVal.put(JSONMapping.LOGGED_IN_DATE_TIME,
                    this.getDateAsLongFromJson(this.getLoggedInDateTime()));
        }

        //SALT...
        if(this.getSalt() != null)
        {
            returnVal.put(JSONMapping.SALT,this.getSalt());
        }

        //Roles...
        if(this.getRoles() != null && !this.getRoles().isEmpty())
        {
            JSONArray rolesArr = new JSONArray();
            for(Role toAdd :this.getRoles())
            {
                rolesArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.ROLES,rolesArr);
        }

        //Email Addresses...
        if(this.getEmailAddresses() != null &&
                !this.getEmailAddresses().isEmpty())
        {
            JSONArray emailArr = new JSONArray();
            for(String toAdd :this.getEmailAddresses())
            {
                emailArr.put(toAdd);
            }

            returnVal.put(JSONMapping.EMAIL_ADDRESSES, emailArr);
        }

        //User Fields...
        if(this.getUserFields() != null && !this.getUserFields().isEmpty())
        {
            JSONArray userFieldsArr = new JSONArray();
            for(Field toAdd :this.getUserFields())
            {
                userFieldsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.USER_FIELDS,userFieldsArr);
        }

        return returnVal;
    }
}
