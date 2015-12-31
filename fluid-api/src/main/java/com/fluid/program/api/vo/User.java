package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class User extends ABaseFluidJSONObject {

    private String username;
    private String passwordSha256;
    private String salt;
    private List<String> roles;
    private List<Field> userFields;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String USERNAME = "username";
        public static final String PASSWORD_SHA_256 = "passwordSha256";
        public static final String ROLES = "roles";
        public static final String SALT = "salt";
    }

    /**
     *
     */
    public User() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public User(JSONObject jsonObjectParam) throws JSONException {
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

            List<String> roleListingList = new ArrayList<String>();

            for(int index = 0;index < roleListing.length();index++)
            {
                roleListingList.add(roleListing.getString(index));
            }

            this.setRoles(roleListingList);
        }
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     *
     * @param usernameParam
     */
    public void setUsername(String usernameParam) {
        this.username = usernameParam;
    }

    /**
     *
     * @return
     */
    public String getPasswordSha256() {
        return this.passwordSha256;
    }

    /**
     *
     * @param passwordSha256Param
     */
    public void setPasswordSha256(String passwordSha256Param) {
        this.passwordSha256 = passwordSha256Param;
    }

    /**
     *
     * @return
     */
    public List<String> getRoles() {
        return this.roles;
    }

    /**
     *
     * @param rolesParam
     */
    public void setRoles(List<String> rolesParam) {
        this.roles = rolesParam;
    }

    /**
     *
     * @return
     */
    public String getSalt() {
        return this.salt;
    }

    /**
     *
     * @param saltParam
     */
    public void setSalt(String saltParam) {
        this.salt = saltParam;
    }

    /**
     *
     * @return
     */
    public List<Field> getUserFields() {
        return this.userFields;
    }

    /**
     *
     * @param userFieldsParam
     */
    public void setUserFields(List<Field> userFieldsParam) {
        this.userFields = userFieldsParam;
    }

    /**
     *
     * @return
     * @throws JSONException
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
            JSONArray formFieldsArr = new JSONArray();
            for(String toAdd :this.getRoles())
            {
                formFieldsArr.put(toAdd);
            }

            returnVal.put(JSONMapping.ROLES,formFieldsArr);
        }

        return returnVal;
    }
}
