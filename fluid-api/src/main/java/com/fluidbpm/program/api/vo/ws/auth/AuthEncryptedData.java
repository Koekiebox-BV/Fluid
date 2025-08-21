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
 * Authorization encrypted data.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidJSONObject
 * @see TokenStatus
 * @see AppRequestToken
 * @see AuthRequest
 * @see AuthResponse
 * @see AuthEncryptedData
 */
public class AuthEncryptedData extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String sessionKeyBase64;
    private Long ticketExpires;
    private String roleListing;

    /**
     * The JSON mapping for the {@code AuthEncryptedData} object.
     */
    public static final class JSONMapping {
        public static final String ROLE_LISTING = "roleListing";
        public static final String TICKET_EXPIRES = "ticketExpiration";
        public static final String SESSION_KEY = "sessionKeyBase64";
    }

    /**
     * Default constructor.
     */
    public AuthEncryptedData() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AuthEncryptedData(JSONObject jsonObjectParam) {
        super();

        //Role Listing...
        if (!jsonObjectParam.isNull(JSONMapping.ROLE_LISTING)) {
            this.setRoleListing(jsonObjectParam.getString(JSONMapping.ROLE_LISTING));
        }

        //Session Key...
        if (!jsonObjectParam.isNull(JSONMapping.SESSION_KEY)) {
            this.setSessionKeyBase64(jsonObjectParam.getString(JSONMapping.SESSION_KEY));
        }

        //Ticket Expires...
        if (!jsonObjectParam.isNull(JSONMapping.TICKET_EXPIRES)) {
            this.setTicketExpires(jsonObjectParam.getLong(JSONMapping.TICKET_EXPIRES));
        }
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
     * @param ticketExpirationParam The time the ticket expires.
     */
    public void setTicketExpires(Long ticketExpirationParam) {
        this.ticketExpires = ticketExpirationParam;
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
     * Gets the role listing separated by ','.
     *
     * @return List of roles.
     */
    public String getRoleListing() {
        return this.roleListing;
    }

    /**
     * Sets the role listing separated by ','.
     *
     * @param roleListingParam List of roles.
     */
    public void setRoleListing(String roleListingParam) {
        this.roleListing = roleListingParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AuthEncryptedData}
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {

        JSONObject returnVal = new JSONObject();

        returnVal.put(JSONMapping.TICKET_EXPIRES, this.getTicketExpires());
        returnVal.put(JSONMapping.ROLE_LISTING, this.getRoleListing());
        returnVal.put(JSONMapping.SESSION_KEY, this.getSessionKeyBase64());

        return returnVal;
    }
}
