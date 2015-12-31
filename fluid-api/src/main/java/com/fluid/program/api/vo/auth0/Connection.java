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

package com.fluid.program.api.vo.auth0;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 *
 */
public class Connection extends ABaseFluidJSONObject {

    private String userId;
    private String name;
    private String email;
    private boolean emailVerified;
    private String nickname;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;

    private List<Client> identities;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String EMAIL_VERIFIED = "email_verified";
        public static final String NICKNAME = "nickname";
        public static final String PICTURE = "picture";
        public static final String GIVEN_NAME = "given_name";
        public static final String FAMILY_NAME = "family_name";


        public static final String IDENTITIES = "identities";//Array...
        //Done with identities...

        public static final String LOCALE = "locale";
    }

    /**
     * TODO @Jason, always look for at least one client before allowing that type...
     */
    public static class Client extends ABaseFluidJSONObject
    {
        private String accessToken;
        private String provider;
        private String userId;
        private String connection;
        private boolean isSocial;


        /**
         *
         */
        public static class JSONMapping
        {
            public static final String USER_ID = "user_id";
            public static final String ACCESS_TOKEN = "access_token";
            public static final String PROVIDER = "provider";
            public static final String CONNECTION = "connection";
            public static final String IS_SOCIAL = "isSocial";
        }

        /**
         *
         */
        public Client() {
            super();
        }

        /**
         *
         * @param jsonObjectParam
         */
        public Client(JSONObject jsonObjectParam) {
            super(jsonObjectParam);

            if(this.jsonObject == null)
            {
                return;
            }

            //Access Token...
            if (!this.jsonObject.isNull(JSONMapping.ACCESS_TOKEN)) {
                this.setAccessToken(this.jsonObject.getString(
                        JSONMapping.ACCESS_TOKEN));
            }

            //Provider...
            if (!this.jsonObject.isNull(JSONMapping.PROVIDER)) {
                this.setProvider(this.jsonObject.getString(
                        JSONMapping.PROVIDER));
            }

            //User Id...
            if (!this.jsonObject.isNull(Connection.JSONMapping.USER_ID)) {
                this.setUserId(this.jsonObject.getString(
                        JSONMapping.USER_ID));
            }

            //Connection...
            if (!this.jsonObject.isNull(JSONMapping.CONNECTION)) {
                this.setConnection(this.jsonObject.getString(
                        JSONMapping.CONNECTION));
            }

            //Is Social...
            if (!this.jsonObject.isNull(JSONMapping.IS_SOCIAL)) {
                this.setSocial(this.jsonObject.getBoolean(
                        JSONMapping.IS_SOCIAL));
            }
        }

        /**
         *
         * @return
         */
        public String getAccessToken() {
            return this.accessToken;
        }

        /**
         *
         * @param accessTokenParam
         */
        public void setAccessToken(String accessTokenParam) {
            this.accessToken = accessTokenParam;
        }

        /**
         *
         * @return
         */
        public String getProvider() {
            return this.provider;
        }

        /**
         *
         * @param providerParam
         */
        public void setProvider(String providerParam) {
            this.provider = providerParam;
        }

        /**
         *
         * @return
         */
        public String getUserId() {
            return this.userId;
        }

        /**
         *
         * @param userIdParam
         */
        public void setUserId(String userIdParam) {
            this.userId = userIdParam;
        }

        /**
         *
         * @return
         */
        public String getConnection() {
            return this.connection;
        }

        /**
         *
         * @param connectionParam
         */
        public void setConnection(String connectionParam) {
            this.connection = connectionParam;
        }

        /**
         *
         * @return
         */
        public boolean isSocial() {
            return this.isSocial;
        }

        /**
         *
         * @param socialParam
         */
        public void setSocial(boolean socialParam) {
            this.isSocial = socialParam;
        }

        /**
         *
         * @return
         * @throws JSONException
         */
        @Override
        public JSONObject toJsonObject() throws JSONException {

            JSONObject returnVal = super.toJsonObject();

            //Access Token...
            if(this.getAccessToken() != null)
            {
                returnVal.put(JSONMapping.ACCESS_TOKEN, this.getAccessToken());
            }

            //Provider...
            if(this.getProvider() != null)
            {
                returnVal.put(JSONMapping.PROVIDER, this.getProvider());
            }

            //User Id...
            if(this.getUserId() != null)
            {
                returnVal.put(JSONMapping.USER_ID, this.getUserId());
            }

            //Connection...
            if(this.getConnection() != null)
            {
                returnVal.put(JSONMapping.CONNECTION, this.getConnection());
            }

            //Is Social...
            returnVal.put(JSONMapping.IS_SOCIAL, this.isSocial());

            return returnVal;
        }
    }

    /**
     *
     */
    public Connection() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public Connection(JSONObject jsonObjectParam) throws JSONException {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //User Id...
        if (!this.jsonObject.isNull(JSONMapping.USER_ID)) {
            this.setUserId(this.jsonObject.getString(JSONMapping.USER_ID));
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(JSONMapping.NAME));
        }

        //Email...
        if (!this.jsonObject.isNull(JSONMapping.EMAIL)) {
            this.setEmail(this.jsonObject.getString(JSONMapping.EMAIL));
        }

        //Email Verified...
        if (!this.jsonObject.isNull(JSONMapping.EMAIL_VERIFIED)) {
            this.setEmailVerified(this.jsonObject.getBoolean(JSONMapping.EMAIL_VERIFIED));
        }

        //Nickname...
        if (!this.jsonObject.isNull(JSONMapping.NICKNAME)) {
            this.setNickname(this.jsonObject.getString(JSONMapping.NICKNAME));
        }

        //Picture...
        if (!this.jsonObject.isNull(JSONMapping.PICTURE)) {
            this.setPicture(this.jsonObject.getString(JSONMapping.PICTURE));
        }

        //Given Name...
        if (!this.jsonObject.isNull(JSONMapping.GIVEN_NAME)) {
            this.setGivenName(this.jsonObject.getString(JSONMapping.GIVEN_NAME));
        }

        //Family Name...
        if (!this.jsonObject.isNull(JSONMapping.FAMILY_NAME)) {
            this.setFamilyName(this.jsonObject.getString(JSONMapping.FAMILY_NAME));
        }

        //Locale...
        if (!this.jsonObject.isNull(JSONMapping.LOCALE)) {
            this.setLocale(this.jsonObject.getString(JSONMapping.LOCALE));
        }

        //Roles...
        if (!this.jsonObject.isNull(JSONMapping.IDENTITIES)) {

            this.identities = new ArrayList<Client>();

            JSONArray identityListing = this.jsonObject.getJSONArray(JSONMapping.IDENTITIES);

            for(int index = 0;index < identityListing.length();index++)
            {
                this.identities.add(new Client(identityListing.getJSONObject(index)));
            }
        }
    }

    /**
     *
     * @return
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     *
     * @param userIdParam
     */
    public void setUserId(String userIdParam) {
        this.userId = userIdParam;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param nameParam
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return this.email;
    }

    /**
     *
     * @param emailParam
     */
    public void setEmail(String emailParam) {
        this.email = emailParam;
    }

    /**
     *
     * @return
     */
    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    /**
     *
     * @param emailVerifiedParam
     */
    public void setEmailVerified(boolean emailVerifiedParam) {
        this.emailVerified = emailVerifiedParam;
    }

    /**
     *
     * @return
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     *
     * @param nicknameParam
     */
    public void setNickname(String nicknameParam) {
        this.nickname = nicknameParam;
    }

    /**
     *
     * @return
     */
    public String getPicture() {
        return this.picture;
    }

    /**
     *
     * @param pictureParam
     */
    public void setPicture(String pictureParam) {
        this.picture = pictureParam;
    }

    /**
     *
     * @return
     */
    public String getGivenName() {
        return this.givenName;
    }

    /**
     *
     * @param givenNameParam
     */
    public void setGivenName(String givenNameParam) {
        this.givenName = givenNameParam;
    }

    /**
     *
     * @return
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     *
     * @param familyNameParam
     */
    public void setFamilyName(String familyNameParam) {
        this.familyName = familyNameParam;
    }

    /**
     *
     * @return
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     *
     * @param localeParam
     */
    public void setLocale(String localeParam) {
        this.locale = localeParam;
    }

    /**
     *
     * @return
     */
    public List<Client> getIdentities() {
        return this.identities;
    }

    /**
     *
     * @param identitiesParam
     */
    public void setIdentities(List<Client> identitiesParam) {
        this.identities = identitiesParam;
    }

    /**
     *
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //User Id...
        if(this.getUserId() != null)
        {
            returnVal.put(JSONMapping.USER_ID, this.getUserId());
        }

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME, this.getName());
        }

        //Email...
        if(this.getEmail() != null)
        {
            returnVal.put(JSONMapping.EMAIL, this.getEmail());
        }

        //Email Verified...
        returnVal.put(JSONMapping.EMAIL_VERIFIED, this.isEmailVerified());

        //Nickname...
        if(this.getNickname() != null)
        {
            returnVal.put(JSONMapping.NICKNAME, this.getNickname());
        }

        //Picture...
        if(this.getPicture() != null)
        {
            returnVal.put(JSONMapping.PICTURE, this.getPicture());
        }

        //Given Name...
        if(this.getGivenName() != null)
        {
            returnVal.put(JSONMapping.GIVEN_NAME, this.getGivenName());
        }

        //Family Name...
        if(this.getFamilyName() != null)
        {
            returnVal.put(JSONMapping.FAMILY_NAME, this.getFamilyName());
        }

        //Locale...
        if(this.getLocale() != null)
        {
            returnVal.put(JSONMapping.LOCALE, this.getLocale());
        }

        //Identities...
        if(this.getIdentities() != null && !this.getIdentities().isEmpty())
        {
            JSONArray identitiesArr = new JSONArray();
            for(Client toAdd :this.getIdentities())
            {
                identitiesArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.IDENTITIES,identitiesArr);
        }

        return returnVal;
    }

    /**
     *
     * @return
     */
    public String getListOfProvidersFromIdentities()
    {
        if(this.getIdentities() == null || this.getIdentities().isEmpty())
        {
            return "";
        }

        StringBuilder returnVal = new StringBuilder();

        for(Client client : this.getIdentities())
        {
            returnVal.append(client.getProvider());
            returnVal.append(",");
        }

        String toString = returnVal.toString();
        return toString.substring(0,toString.length() - 1);
    }
}
