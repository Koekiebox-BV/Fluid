package com.fluid.program.api.vo.auth0;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 *
 */
public class AccessToken extends ABaseFluidJSONObject {

    private String accessToken;
    private String idToken;
    private String tokenType;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String ACCESS_TOKEN = "access_token";
        public static final String ID_TOKEN = "id_token";
        public static final String TOKEN_TYPE = "token_type";
    }

    /**
     *
     */
    public AccessToken() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public AccessToken(JSONObject jsonObjectParam) throws JSONException {
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
    public String getIdToken() {
        return this.idToken;
    }

    /**
     *
     * @param idTokenParam
     */
    public void setIdToken(String idTokenParam) {
        this.idToken = idTokenParam;
    }

    /**
     *
     * @return
     */
    public String getTokenType() {
        return this.tokenType;
    }

    /**
     *
     * @param tokenTypeParam
     */
    public void setTokenType(String tokenTypeParam) {
        this.tokenType = tokenTypeParam;
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
