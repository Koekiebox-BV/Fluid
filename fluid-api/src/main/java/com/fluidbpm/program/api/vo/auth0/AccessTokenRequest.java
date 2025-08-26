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
import com.google.gson.JsonObject;


/**
 * An Auth0 Access Token Request used by Fluid.
 * <p>
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @see NormalizedUserProfile
 * @see ABaseFluidGSONObject
 * @since v1.0
 */
public class AccessTokenRequest extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;
    private String code;

    /**
     * The JSON mapping for the {@code AccessTokenRequest} object.
     */
    public static class JSONMapping {
        public static final String CLIENT_ID = "client_id";
        public static final String CLIENT_SECRET = "client_secret";
        public static final String REDIRECT_URI = "redirect_uri";
        public static final String GRANT_TYPE = "grant_type";
        public static final String CODE = "code";
    }

    /**
     * Default constructor.
     */
    public AccessTokenRequest() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AccessTokenRequest(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setClientId(this.getAsStringNullSafe(JSONMapping.CLIENT_ID));
        this.setClientSecret(this.getAsStringNullSafe(JSONMapping.CLIENT_SECRET));
        this.setCode(this.getAsStringNullSafe(JSONMapping.CODE));
        this.setGrantType(this.getAsStringNullSafe(JSONMapping.GRANT_TYPE));
        this.setRedirectUri(this.getAsStringNullSafe(JSONMapping.REDIRECT_URI));
    }

    /**
     * Gets the Client Id.
     *
     * @return Client Id.
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Sets the Client Id.
     *
     * @param clientIdParam Client Id.
     */
    public void setClientId(String clientIdParam) {
        this.clientId = clientIdParam;
    }

    /**
     * Gets the Client Secret.
     *
     * @return Client Secret.
     */
    public String getClientSecret() {
        return this.clientSecret;
    }

    /**
     * Sets the Client Secret.
     *
     * @param clientSecretParam Client Secret.
     */
    public void setClientSecret(String clientSecretParam) {
        this.clientSecret = clientSecretParam;
    }

    /**
     * Gets the redirect URI.
     *
     * @return Redirect URI.
     */
    public String getRedirectUri() {
        return this.redirectUri;
    }

    /**
     * Sets the redirect URI.
     *
     * @param redirectUriParam Redirect URI.
     */
    public void setRedirectUri(String redirectUriParam) {
        this.redirectUri = redirectUriParam;
    }

    /**
     * Gets the Grant Type.
     *
     * @return Grant Type.
     */
    public String getGrantType() {
        return this.grantType;
    }

    /**
     * Sets the Grant Type.
     *
     * @param grantTypeParam Grant Type.
     */
    public void setGrantType(String grantTypeParam) {
        this.grantType = grantTypeParam;
    }

    /**
     * Gets the Code.
     *
     * @return Code.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Sets the Code.
     *
     * @param codeParam Code.
     */
    public void setCode(String codeParam) {
        this.code = codeParam;
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code AccessTokenRequest}
     * 
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.CLIENT_ID, returnVal, this.getClientId());
        this.setAsProperty(JSONMapping.CLIENT_SECRET, returnVal, this.getClientSecret());
        this.setAsProperty(JSONMapping.CODE, returnVal, this.getCode());
        this.setAsProperty(JSONMapping.GRANT_TYPE, returnVal, this.getGrantType());
        this.setAsProperty(JSONMapping.REDIRECT_URI, returnVal, this.getRedirectUri());

        return returnVal;
    }
}
