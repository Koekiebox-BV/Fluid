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
     * The JSON mapping for the {@code AuthRequest} object.
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
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AuthRequest}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
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
