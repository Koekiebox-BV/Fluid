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

package com.fluidbpm.program.api.vo.ws.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Status of a previously issued authentication token.
 *
 * @author jasonbruwer
 * @see TokenStatus
 * @see AppRequestToken
 * @since v1.0
 */
@Getter
@Setter
public class ServiceTicket extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String principalClient;
    private Long ticketExpires;
    private String sessionKeyBase64;
    private String authorisedUsername;

    /**
     * The JSON mapping for the {@code ServiceTicket} object.
     */
    public static final class JSONMapping {
        public static final String CLIENT = "client";
        public static final String TICKET_EXPIRES = "ticketExpiration";
        public static final String SESSION_KEY = "sessionKeyBase64";

        //Only send this on app request token...
        public static final String AUTHORISED_USERNAME = "authorisedUsername";
    }

    /**
     * Default constructor.
     */
    public ServiceTicket() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ServiceTicket(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setPrincipalClient(this.getAsStringNullSafe(JSONMapping.CLIENT));
        this.setSessionKeyBase64(this.getAsStringNullSafe(JSONMapping.SESSION_KEY));
        this.setTicketExpires(this.getAsLongNullSafe(JSONMapping.TICKET_EXPIRES));
        this.setAuthorisedUsername(this.getAsStringNullSafe(JSONMapping.AUTHORISED_USERNAME));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code ServiceTicket}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.CLIENT, returnVal, this.getPrincipalClient());
        this.setAsProperty(JSONMapping.SESSION_KEY, returnVal, this.getSessionKeyBase64());
        this.setAsProperty(JSONMapping.TICKET_EXPIRES, returnVal, this.getTicketExpires());
        this.setAsProperty(JSONMapping.AUTHORISED_USERNAME, returnVal, this.getAuthorisedUsername());

        return returnVal;
    }
}
