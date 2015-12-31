package com.fluid.program.api.vo.ws.auth;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRequest extends ABaseFluidJSONObject {

    private String username;
    private Long lifetime;

    /**
     *
     */
    public static class JSONMapping {
        public static final String USERNAME = "username";
        public static final String LIFETIME = "lifetime";
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
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public Long getLifetime() {
        return this.lifetime;
    }

    /**
     *
     * @param lifetime
     */
    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
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
        if (this.getUsername() != null) {
            returnVal.put(JSONMapping.USERNAME, this.getUsername());
        }

        //Lifetime...
        if (this.getLifetime() != null) {
            returnVal.put(JSONMapping.LIFETIME, this.getLifetime());
        }

        return returnVal;
    }
}
