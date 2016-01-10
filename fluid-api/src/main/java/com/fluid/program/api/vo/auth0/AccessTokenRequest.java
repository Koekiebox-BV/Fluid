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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * An Auth0 Access Token Request used by Fluid.
 *
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see NormalizedUserProfile
 * @see ABaseFluidJSONObject
 */
public class AccessTokenRequest extends ABaseFluidJSONObject {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;
    private String code;

    /**
     * The JSON mapping for the {@code AccessTokenRequest} object.
     */
    public static class JSONMapping
    {
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
    public AccessTokenRequest(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Client Id...
        if (!this.jsonObject.isNull(JSONMapping.CLIENT_ID)) {
            this.setClientId(this.jsonObject.getString(JSONMapping.CLIENT_ID));
        }

        //Client Secret...
        if (!this.jsonObject.isNull(JSONMapping.CLIENT_SECRET)) {
            this.setClientSecret(this.jsonObject.getString(JSONMapping.CLIENT_SECRET));
        }

        //Code...
        if (!this.jsonObject.isNull(JSONMapping.CODE)) {
            this.setCode(this.jsonObject.getString(JSONMapping.CODE));
        }

        //Grant Type...
        if (!this.jsonObject.isNull(JSONMapping.GRANT_TYPE)) {
            this.setGrantType(this.jsonObject.getString(JSONMapping.GRANT_TYPE));
        }

        //Redirect URI...
        if (!this.jsonObject.isNull(JSONMapping.REDIRECT_URI)) {
            this.setRedirectUri(this.jsonObject.getString(JSONMapping.REDIRECT_URI));
        }
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
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AccessTokenRequest}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Client ID...
        if(this.getClientId() != null)
        {
            returnVal.put(JSONMapping.CLIENT_ID,this.getClientId());
        }

        //Client Secret...
        if(this.getClientSecret() != null)
        {
            returnVal.put(JSONMapping.CLIENT_SECRET,this.getClientSecret());
        }

        //Code...
        if(this.getCode() != null)
        {
            returnVal.put(JSONMapping.CODE,this.getCode());
        }

        //Grant Type...
        if(this.getGrantType() != null)
        {
            returnVal.put(JSONMapping.GRANT_TYPE,this.getGrantType());
        }

        //Redirect URI...
        if(this.getRedirectUri() != null)
        {
            returnVal.put(JSONMapping.REDIRECT_URI,this.getRedirectUri());
        }

        return returnVal;
    }
}
