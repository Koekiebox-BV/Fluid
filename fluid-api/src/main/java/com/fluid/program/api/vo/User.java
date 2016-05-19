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

package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     Represents a Fluid User.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see com.fluid.program.api.vo.auth0.AccessToken
 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
 * @see com.fluid.program.api.vo.auth0.Connection
 * @see com.fluid.program.api.vo.auth0.NormalizedUserProfile
 */
public class User extends ABaseFluidJSONObject {

    private String username;
    private String passwordSha256;
    private String salt;
    private List<String> roles;
    private List<Field> userFields;

    /**
     * The JSON mapping for the {@code User} object.
     */
    public static class JSONMapping
    {
        public static final String USERNAME = "username";
        public static final String PASSWORD_SHA_256 = "passwordSha256";
        public static final String ROLES = "roles";
        public static final String SALT = "salt";
        public static final String USER_FIELDS = "userFields";
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

        //Password...
        if (!this.jsonObject.isNull(JSONMapping.PASSWORD_SHA_256)) {
            this.setPasswordSha256(this.jsonObject.getString(JSONMapping.PASSWORD_SHA_256));
        }

        //Salt...
        if (!this.jsonObject.isNull(JSONMapping.SALT)) {
            this.setSalt(this.jsonObject.getString(JSONMapping.SALT));
        }

        //Roles...
        if (!this.jsonObject.isNull(JSONMapping.ROLES)) {

            JSONArray roleListing = this.jsonObject.getJSONArray(JSONMapping.ROLES);

            List<String> roleListingList = new ArrayList<>();

            for(int index = 0;index < roleListing.length();index++)
            {
                roleListingList.add(roleListing.getString(index));
            }

            this.setRoles(roleListingList);
        }

        //User Fields...
        if (!this.jsonObject.isNull(JSONMapping.USER_FIELDS)) {

            JSONArray userFieldListing = this.jsonObject.getJSONArray(JSONMapping.USER_FIELDS);

            List<Field> userFieldListingList = new ArrayList<>();

            for(int index = 0;index < userFieldListing.length();index++)
            {
                userFieldListingList.add(
                        new Field(userFieldListing.getJSONObject(index)));
            }

            this.setUserFields(userFieldListingList);
        }
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
     * Gets List of Roles for user.
     *
     * @return {@code List} of Roles for {@code User}.
     */
    public List<String> getRoles() {
        return this.roles;
    }

    /**
     * Sets List of Roles for user.
     *
     * @param rolesParam {@code List} of roles associated with a {@code User}.
     */
    public void setRoles(List<String> rolesParam) {
        this.roles = rolesParam;
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
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

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

        //SALT...
        if(this.getSalt() != null)
        {
            returnVal.put(JSONMapping.SALT,this.getSalt());
        }

        //Roles...
        if(this.getRoles() != null && !this.getRoles().isEmpty())
        {
            JSONArray rolesArr = new JSONArray();
            for(String toAdd :this.getRoles())
            {
                rolesArr.put(toAdd);
            }

            returnVal.put(JSONMapping.ROLES,rolesArr);
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
