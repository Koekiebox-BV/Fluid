package com.fluid.program.api.vo.auth0;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 *
 */
public class AccessTokenRequest extends ABaseFluidJSONObject {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;
    private String code;

    /**
     *
     *
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
     *
     */
    public AccessTokenRequest() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public AccessTokenRequest(JSONObject jsonObjectParam) throws JSONException {
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
     *
     * @return
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * 
     * @param clientIdParam
     */
    public void setClientId(String clientIdParam) {
        this.clientId = clientIdParam;
    }

    /**
     *
     * @return
     */
    public String getClientSecret() {
        return this.clientSecret;
    }

    /**
     *
     * @param clientSecretParam
     */
    public void setClientSecret(String clientSecretParam) {
        this.clientSecret = clientSecretParam;
    }

    /**
     *
     * @return
     */
    public String getRedirectUri() {
        return this.redirectUri;
    }

    /**
     *
     * @param redirectUriParam
     */
    public void setRedirectUri(String redirectUriParam) {
        this.redirectUri = redirectUriParam;
    }

    /**
     *
     * @return
     */
    public String getGrantType() {
        return this.grantType;
    }

    /**
     *
     * @param grantTypeParam
     */
    public void setGrantType(String grantTypeParam) {
        this.grantType = grantTypeParam;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return this.code;
    }

    /**
     *
     * @param codeParam
     */
    public void setCode(String codeParam) {
        this.code = codeParam;
    }

    /**
     *
     * @return
     * @throws JSONException
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
