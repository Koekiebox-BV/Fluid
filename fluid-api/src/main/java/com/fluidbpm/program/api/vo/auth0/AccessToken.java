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
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.xml.bind.annotation.XmlTransient;

/**
 * An Auth0 Access Token used by Fluid.
 * <p>
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @see NormalizedUserProfile
 * @see AccessTokenRequest
 * @see ABaseFluidJSONObject
 * @since v1.0
 */
public class AccessToken extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String accessToken;

    @Getter
    @Setter
    private String idToken;

    @Getter
    @Setter
    private String tokenType;

    /**
     * The JSON mapping for the {@code AccessToken} object.
     */
    public static class JSONMapping {
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
    public AccessToken(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setAccessToken(this.getAsStringNullSafe(JSONMapping.ACCESS_TOKEN));
        this.setIdToken(this.getAsStringNullSafe(JSONMapping.ID_TOKEN));
        this.setTokenType(this.getAsStringNullSafe(JSONMapping.TOKEN_TYPE));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code AccessToken}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.ACCESS_TOKEN, returnVal, this.getAccessToken());
        this.setAsProperty(JSONMapping.ID_TOKEN, returnVal, this.getIdToken());
        this.setAsProperty(JSONMapping.TOKEN_TYPE, returnVal, this.getTokenType());

        return returnVal;
    }
}
