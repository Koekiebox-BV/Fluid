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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * Created by jasonbruwer on 14/12/30.
 */
public class AuthEncryptedData extends ABaseFluidJSONObject {

    private String sessionKeyBase64;
    private Long ticketExpires;
    private String roleListing;

    public static final class JsonStructure {
        public static final String ROLE_LISTING = "roleListing";
        public static final String TICKET_EXPIRES = "ticketExpiration";
        public static final String SESSION_KEY = "sessionKeyBase64";
    }

    /**
     *
     */
    public AuthEncryptedData() {
        super();
    }

    /**
     * 
     * @param jsonObjectParam
     * @throws JSONException
     */
    public AuthEncryptedData(JSONObject jsonObjectParam) throws JSONException {
        super();

        //Role Listing...
        if (!jsonObjectParam.isNull(JsonStructure.ROLE_LISTING)) {
            this.setRoleListing(jsonObjectParam.getString(JsonStructure.ROLE_LISTING));
        }

        //Session Key...
        if (!jsonObjectParam.isNull(JsonStructure.SESSION_KEY)) {
            this.setSessionKeyBase64(jsonObjectParam.getString(JsonStructure.SESSION_KEY));
        }

        //Ticket Expires...
        if (!jsonObjectParam.isNull(JsonStructure.TICKET_EXPIRES)) {
            this.setTicketExpires(jsonObjectParam.getLong(JsonStructure.TICKET_EXPIRES));
        }
    }

    /**
     *
     * @return
     */
    public Long getTicketExpires() {
        return this.ticketExpires;
    }

    /**
     *
     *
     * @param ticketExpirationParam
     */
    public void setTicketExpires(Long ticketExpirationParam) {
        this.ticketExpires = ticketExpirationParam;
    }

    /**
     *
     * @return
     */
    public String getSessionKeyBase64() {
        return this.sessionKeyBase64;
    }

    /**
     *
     * @param sessionKeyBase64
     */
    public void setSessionKeyBase64(String sessionKeyBase64) {
        this.sessionKeyBase64 = sessionKeyBase64;
    }

    /**
     *
     * @return
     */
    public String getRoleListing() {
        return this.roleListing;
    }

    /**
     *
     * @param roleListingParam
     */
    public void setRoleListing(String roleListingParam) {
        this.roleListing = roleListingParam;
    }

    /**
     *
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = new JSONObject();

        returnVal.put(JsonStructure.TICKET_EXPIRES, this.getTicketExpires());
        returnVal.put(JsonStructure.ROLE_LISTING, this.getRoleListing());
        returnVal.put(JsonStructure.SESSION_KEY, this.getSessionKeyBase64());

        return returnVal;
    }
}
