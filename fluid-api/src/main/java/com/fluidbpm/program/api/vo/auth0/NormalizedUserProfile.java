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

package com.fluidbpm.program.api.vo.auth0;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Fluid consolidation of a workflow.
 *
 * @author jasonbruwer
 * @see User
 * @since v1.0
 */
public class NormalizedUserProfile extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private String email;
    private boolean emailVerified;
    private String nickname;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;

    private List<Identity> identities;

    /**
     * The JSON mapping for the {@code NormalizedUserProfile} object.
     */
    public static class JSONMapping {
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
     * The Header mapping for the {@code NormalizedUserProfile} object.
     */
    public static class HeaderMapping {
        public static final String AUTHORIZATION = "Authorization";
    }

    /**
     * Identity related value object.
     *
     * @see ABaseFluidJSONObject
     */
    public static class Identity extends ABaseFluidGSONObject {
        private String accessToken;
        private String provider;
        private String userId;
        private String connection;
        private boolean isSocial;

        /**
         * The JSON mapping for the {@code Identity} object.
         */
        public static class JSONMapping {
            public static final String USER_ID = "user_id";
            public static final String ACCESS_TOKEN = "access_token";
            public static final String PROVIDER = "provider";
            public static final String CONNECTION = "connection";
            public static final String IS_SOCIAL = "isSocial";
        }

        /**
         * Default constructor.
         */
        public Identity() {
            super();
        }

        /**
         * Populates local variables with {@code jsonObjectParam}.
         *
         * @param jsonObjectParam The JSON Object.
         */
        public Identity(JsonObject jsonObjectParam) {
            super(jsonObjectParam);
            if (this.jsonObject == null) return;

            this.setAccessToken(this.getAsStringNullSafe(JSONMapping.ACCESS_TOKEN));
            this.setProvider(this.getAsStringNullSafe(JSONMapping.PROVIDER));
            this.setUserId(this.getAsStringNullSafe(JSONMapping.USER_ID));
            this.setConnection(this.getAsStringNullSafe(JSONMapping.CONNECTION));
            Boolean isSocialVal = this.getAsBooleanNullSafe(JSONMapping.IS_SOCIAL);
            if (isSocialVal != null) this.setSocial(isSocialVal);
        }

        /**
         * Gets the Access Token.
         *
         * @return Access Token.
         */
        public String getAccessToken() {
            return this.accessToken;
        }

        /**
         * Sets the Access Token.
         *
         * @param accessTokenParam Access Token.
         */
        public void setAccessToken(String accessTokenParam) {
            this.accessToken = accessTokenParam;
        }

        /**
         * Gets the provider.
         *
         * @return Authentication provider.
         */
        public String getProvider() {
            return this.provider;
        }

        /**
         * Sets the provider.
         *
         * @param providerParam Authentication provider.
         */
        public void setProvider(String providerParam) {
            this.provider = providerParam;
        }

        /**
         * Gets the User Unique Id.
         *
         * @return User Id.
         */
        public String getUserId() {
            return this.userId;
        }

        /**
         * Sets the User Unique Id.
         *
         * @param userIdParam User Id.
         */
        public void setUserId(String userIdParam) {
            this.userId = userIdParam;
        }

        /**
         * Gets the Connection.
         *
         * @return The Connection.
         */
        public String getConnection() {
            return this.connection;
        }

        /**
         * Sets the Connection.
         *
         * @param connectionParam The Connection.
         */
        public void setConnection(String connectionParam) {
            this.connection = connectionParam;
        }

        /**
         * Is the authenticated server a social one.
         *
         * @return Whether the connection is a social network connection.
         */
        public boolean isSocial() {
            return this.isSocial;
        }

        /**
         * Sets whether the connection is social.
         *
         * @param socialParam Whether the connection is sociall
         */
        public void setSocial(boolean socialParam) {
            this.isSocial = socialParam;
        }

        /**
         * Conversion to {@code JSONObject} from Java Object.
         *
         * @return {@code JSONObject} representation of {@code Identity}
         * @see ABaseFluidJSONObject#toJsonObject()
         */
        @Override
        public JsonObject toJsonObject() {
            JsonObject returnVal = super.toJsonObject();

            this.setAsProperty(JSONMapping.ACCESS_TOKEN, returnVal, this.getAccessToken());
            this.setAsProperty(JSONMapping.PROVIDER, returnVal, this.getProvider());
            this.setAsProperty(JSONMapping.USER_ID, returnVal, this.getUserId());
            this.setAsProperty(JSONMapping.CONNECTION, returnVal, this.getConnection());
            this.setAsProperty(JSONMapping.IS_SOCIAL, returnVal, Boolean.valueOf(this.isSocial()));

            return returnVal;
        }
    }

    /**
     * Default constructor.
     */
    public NormalizedUserProfile() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public NormalizedUserProfile(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setUserId(this.getAsStringNullSafe(JSONMapping.USER_ID));
        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setEmail(this.getAsStringNullSafe(JSONMapping.EMAIL));
        Boolean emailVerifiedVal = this.getAsBooleanNullSafe(JSONMapping.EMAIL_VERIFIED);
        if (emailVerifiedVal != null) this.setEmailVerified(emailVerifiedVal);
        this.setNickname(this.getAsStringNullSafe(JSONMapping.NICKNAME));
        this.setPicture(this.getAsStringNullSafe(JSONMapping.PICTURE));
        this.setGivenName(this.getAsStringNullSafe(JSONMapping.GIVEN_NAME));
        this.setFamilyName(this.getAsStringNullSafe(JSONMapping.FAMILY_NAME));
        this.setLocale(this.getAsStringNullSafe(JSONMapping.LOCALE));

        this.setIdentities(this.extractObjects(JSONMapping.IDENTITIES, Identity::new));
    }

    /**
     * Gets the User id.
     *
     * @return User Id.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the User id.
     *
     * @param userIdParam User Id.
     */
    public void setUserId(String userIdParam) {
        this.userId = userIdParam;
    }

    /**
     * Gets the User name.
     *
     * @return Name of the User.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the User name.
     *
     * @param nameParam Name of the User.
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets the users associated email.
     *
     * @return User Email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the users associated email.
     *
     * @param emailParam User Email.
     */
    public void setEmail(String emailParam) {
        this.email = emailParam;
    }

    /**
     * Gets whether the email is verified.
     *
     * @return Whether email is verified.
     */
    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    /**
     * Sets whether the email is verified.
     *
     * @param emailVerifiedParam Whether email is verified.
     */
    public void setEmailVerified(boolean emailVerifiedParam) {
        this.emailVerified = emailVerifiedParam;
    }

    /**
     * Gets the User nickname.
     *
     * @return User Nickname.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Sets the User nickname.
     *
     * @param nicknameParam User Nickname.
     */
    public void setNickname(String nicknameParam) {
        this.nickname = nicknameParam;
    }

    /**
     * Gets URL to the Users picture.
     *
     * @return Link to picture.
     */
    public String getPicture() {
        return this.picture;
    }

    /**
     * Sets URL to the Users picture.
     *
     * @param pictureParam Link to picture.
     */
    public void setPicture(String pictureParam) {
        this.picture = pictureParam;
    }

    /**
     * Gets the User's given name.
     *
     * @return Given name.
     */
    public String getGivenName() {
        return this.givenName;
    }

    /**
     * Sets the User's given name.
     *
     * @param givenNameParam Given name.
     */
    public void setGivenName(String givenNameParam) {
        this.givenName = givenNameParam;
    }

    /**
     * Gets the User's given name.
     *
     * @return User Family Name.
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     * Sets the User's given name.
     *
     * @param familyNameParam User Family Name.
     */
    public void setFamilyName(String familyNameParam) {
        this.familyName = familyNameParam;
    }

    /**
     * Gets the User's locale.
     *
     * @return Users Locale.
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * Sets the User's locale.
     *
     * @param localeParam Users Locale.
     */
    public void setLocale(String localeParam) {
        this.locale = localeParam;
    }

    /**
     * Gets the Identities associated with the user.
     *
     * @return User's Identities.
     */
    public List<Identity> getIdentities() {
        return this.identities;
    }

    /**
     * Sets the Identities associated with the user.
     *
     * @param identitiesParam User's Identities.
     */
    public void setIdentities(List<Identity> identitiesParam) {
        this.identities = identitiesParam;
    }

    /**
     * Gets a comma seperated list of providers from {@code Identity}'s.
     *
     * @return Text list of Providers for user {@code Identities}.
     * @see NormalizedUserProfile#getIdentities()
     */
    public String getListOfProvidersFromIdentities() {
        if (this.getIdentities() == null || this.getIdentities().isEmpty()) {
            return "";
        }

        StringBuilder returnVal = new StringBuilder();

        for (Identity identity : this.getIdentities()) {
            returnVal.append(identity.getProvider());
            returnVal.append(",");
        }

        String toString = returnVal.toString();
        return toString.substring(0, toString.length() - 1);
    }

    /**
     * Conversion to JsonObject from Java Object.
     *
     * @return JsonObject representation of NormalizedUserProfile
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.USER_ID, returnVal, this.getUserId());
        this.setAsProperty(JSONMapping.NAME, returnVal, this.getName());
        this.setAsProperty(JSONMapping.EMAIL, returnVal, this.getEmail());
        this.setAsProperty(JSONMapping.EMAIL_VERIFIED, returnVal, Boolean.valueOf(this.isEmailVerified()));
        this.setAsProperty(JSONMapping.NICKNAME, returnVal, this.getNickname());
        this.setAsProperty(JSONMapping.PICTURE, returnVal, this.getPicture());
        this.setAsProperty(JSONMapping.GIVEN_NAME, returnVal, this.getGivenName());
        this.setAsProperty(JSONMapping.FAMILY_NAME, returnVal, this.getFamilyName());
        this.setAsProperty(JSONMapping.LOCALE, returnVal, this.getLocale());

        this.setAsObjArray(JSONMapping.IDENTITIES, returnVal, this::getIdentities);

        return returnVal;
    }
}
