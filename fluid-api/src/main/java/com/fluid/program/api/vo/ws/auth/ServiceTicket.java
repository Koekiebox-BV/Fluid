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
 * Created by jasonbruwer on 14/12/22.
 */
public class ServiceTicket extends ABaseFluidJSONObject {

    private String principalClient;
    private Long ticketExpires;
    private String sessionKeyBase64;
    private String authorisedUsername;

    public static final class JsonStructure {
        public static final String CLIENT = "client";
        public static final String TICKET_EXPIRES = "ticketExpiration";
        public static final String SESSION_KEY = "sessionKeyBase64";

        //Only send this on app request token...
        public static final String AUTHORISED_USERNAME = "authorisedUsername";
    }

    /**
     *
     */
    public ServiceTicket() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public ServiceTicket(JSONObject jsonObjectParam) throws JSONException {
        super();

        //Client...
        if (!jsonObjectParam.isNull(JsonStructure.CLIENT)) {
            this.setPrincipalClient(jsonObjectParam.getString(JsonStructure.CLIENT));
        }

        //Session Key...
        if (!jsonObjectParam.isNull(JsonStructure.SESSION_KEY)) {
            this.setSessionKeyBase64(jsonObjectParam.getString(JsonStructure.SESSION_KEY));
        }

        //Ticket Expires...
        if (!jsonObjectParam.isNull(JsonStructure.TICKET_EXPIRES)) {
            this.setTicketExpires(jsonObjectParam.getLong(JsonStructure.TICKET_EXPIRES));
        }

        //Username...
        if (!jsonObjectParam.isNull(JsonStructure.AUTHORISED_USERNAME)) {
            this.setAuthorisedUsername(jsonObjectParam.getString(JsonStructure.AUTHORISED_USERNAME));
        }
    }

    /**
     *
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = new JSONObject();

        returnVal.put(JsonStructure.CLIENT, this.getPrincipalClient());
        returnVal.put(JsonStructure.SESSION_KEY, this.getSessionKeyBase64());
        returnVal.put(JsonStructure.TICKET_EXPIRES, this.getTicketExpires());
        returnVal.put(JsonStructure.AUTHORISED_USERNAME, this.getAuthorisedUsername());

        return returnVal;
    }

    /**
     *
     * @return
     */
    public String getPrincipalClient() {
        return this.principalClient;
    }

    /**
     *
     * @param principalClient
     */
    public void setPrincipalClient(String principalClient) {
        this.principalClient = principalClient;
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
     * @param ticketExpires
     */
    public void setTicketExpires(Long ticketExpires) {
        this.ticketExpires = ticketExpires;
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
    public String getAuthorisedUsername() {
        return this.authorisedUsername;
    }

    /**
     *
     * @param authorisedUsername
     */
    public void setAuthorisedUsername(String authorisedUsername) {
        this.authorisedUsername = authorisedUsername;
    }
}
