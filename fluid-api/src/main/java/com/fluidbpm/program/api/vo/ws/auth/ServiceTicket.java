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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * Status of a previously issued authentication token.
 *
 * @author jasonbruwer
 * @see ABaseFluidJSONObject
 * @see TokenStatus
 * @see AppRequestToken
 * @since v1.0
 */
public class ServiceTicket extends ABaseFluidJSONObject {

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
    public ServiceTicket(JSONObject jsonObjectParam) {
        super();

        //Client...
        if (!jsonObjectParam.isNull(JSONMapping.CLIENT)) {
            this.setPrincipalClient(jsonObjectParam.getString(JSONMapping.CLIENT));
        }

        //Session Key...
        if (!jsonObjectParam.isNull(JSONMapping.SESSION_KEY)) {
            this.setSessionKeyBase64(jsonObjectParam.getString(JSONMapping.SESSION_KEY));
        }

        //Ticket Expires...
        if (!jsonObjectParam.isNull(JSONMapping.TICKET_EXPIRES)) {
            this.setTicketExpires(jsonObjectParam.getLong(JSONMapping.TICKET_EXPIRES));
        }

        //Username...
        if (!jsonObjectParam.isNull(JSONMapping.AUTHORISED_USERNAME)) {
            this.setAuthorisedUsername(jsonObjectParam.getString(JSONMapping.AUTHORISED_USERNAME));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code ServiceTicket}
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = new JSONObject();

        returnVal.put(JSONMapping.CLIENT, this.getPrincipalClient());
        returnVal.put(JSONMapping.SESSION_KEY, this.getSessionKeyBase64());
        returnVal.put(JSONMapping.TICKET_EXPIRES, this.getTicketExpires());
        returnVal.put(JSONMapping.AUTHORISED_USERNAME, this.getAuthorisedUsername());

        return returnVal;
    }

    /**
     * Gets the Principal Client.
     *
     * @return {@code String} representation of the client.
     */
    public String getPrincipalClient() {
        return this.principalClient;
    }

    /**
     * Sets the Principal Client.
     *
     * @param principalClient representation of the client.
     */
    public void setPrincipalClient(String principalClient) {
        this.principalClient = principalClient;
    }

    /**
     * Gets when the ticket expires.
     *
     * @return The time the ticket expires.
     */
    public Long getTicketExpires() {
        return this.ticketExpires;
    }

    /**
     * Sets when the ticket expires.
     *
     * @param ticketExpiresParam The time the ticket expires.
     */
    public void setTicketExpires(Long ticketExpiresParam) {
        this.ticketExpires = ticketExpiresParam;
    }

    /**
     * Gets the Session Key in Base-64 format.
     *
     * @return Session Key.
     */
    public String getSessionKeyBase64() {
        return this.sessionKeyBase64;
    }

    /**
     * Sets the Session Key in Base-64 format.
     *
     * @param sessionKeyBase64Param Session Key.
     */
    public void setSessionKeyBase64(String sessionKeyBase64Param) {
        this.sessionKeyBase64 = sessionKeyBase64Param;
    }

    /**
     * Gets the Authorised username.
     *
     * @return The Authorised Username.
     */
    public String getAuthorisedUsername() {
        return this.authorisedUsername;
    }

    /**
     * Sets the Authorised User username.
     *
     * @param authorisedUsername The Authorised User Username.
     */
    public void setAuthorisedUsername(String authorisedUsername) {
        this.authorisedUsername = authorisedUsername;
    }
}
