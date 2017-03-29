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
 * An Auth0 Access Token used by Fluid.
 *
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see NormalizedUserProfile
 * @see AccessTokenRequest
 * @see ABaseFluidJSONObject
 */
public class AccessToken extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String accessToken;
    private String idToken;
    private String tokenType;

    /**
     * The JSON mapping for the {@code AccessToken} object.
     */
    public static class JSONMapping
    {
        public static final String ACCESS_TOKEN = "access_token";
        public static final String ID_TOKEN = "id_token";
        public static final String TOKEN_TYPE = "token_type";
    }

    /**
     * Default constructor.
     */
    public AccessToken() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AccessToken(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Access Token...
        if (!this.jsonObject.isNull(JSONMapping.ACCESS_TOKEN)) {
            this.setAccessToken(this.jsonObject.getString(JSONMapping.ACCESS_TOKEN));
        }

        //Id Token...
        if (!this.jsonObject.isNull(JSONMapping.ID_TOKEN)) {
            this.setIdToken(this.jsonObject.getString(JSONMapping.ID_TOKEN));
        }

        //Token Type...
        if (!this.jsonObject.isNull(JSONMapping.TOKEN_TYPE)) {
            this.setTokenType(this.jsonObject.getString(JSONMapping.TOKEN_TYPE));
        }
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
     * Gets the Id Token.
     *
     * @return Id Token.
     */
    public String getIdToken() {
        return this.idToken;
    }

    /**
     * Sets the Id Token.
     *
     * @param idTokenParam Id Token.
     */
    public void setIdToken(String idTokenParam) {
        this.idToken = idTokenParam;
    }

    /**
     * Gets the Token Type.
     *
     * @return Token Type.
     */
    public String getTokenType() {
        return this.tokenType;
    }

    /**
     * Sets the Token Type.
     *
     * @param tokenTypeParam Token Type.
     */
    public void setTokenType(String tokenTypeParam) {
        this.tokenType = tokenTypeParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AccessToken}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Access Token...
        if(this.getAccessToken() != null)
        {
            returnVal.put(JSONMapping.ACCESS_TOKEN,this.getAccessToken());
        }

        //Id Token...
        if(this.getIdToken() != null)
        {
            returnVal.put(JSONMapping.ID_TOKEN,this.getIdToken());
        }

        //Token Type...
        if(this.getTokenType() != null)
        {
            returnVal.put(JSONMapping.TOKEN_TYPE,this.getTokenType());
        }

        return returnVal;
    }
}
