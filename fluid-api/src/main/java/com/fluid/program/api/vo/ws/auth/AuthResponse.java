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
public class AuthResponse extends ABaseFluidJSONObject {

    private String salt;

    //Payload...
    private String encryptedDataBase64;//Contains [TicketExpires], [Roles] and [SessionKey]...
    private String encryptedDataHmacBase64;
    private String ivBase64;
    private String seedBase64;

    //Service Ticket...
    private String serviceTicketBase64;

    /**
     *
     */
    public static class JSONMapping {
        public static final String SALT = "salt";
        public static final String ENCRYPTED_DATA_BASE_64 = "encryptedDataBase64";
        public static final String ENCRYPTED_DATA_HMAC_BASE_64 = "encryptedDataHmacBase64";
        public static final String IV_BASE_64 = "ivBase64";
        public static final String SEED_BASE_64 = "seedBase64";
        public static final String SERVICE_TICKET_BASE_64 = "serviceTicketBase64";
    }

    /**
     *
     */
    public AuthResponse() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     */
    public AuthResponse(JSONObject jsonObjectParam) throws JSONException {
        super(jsonObjectParam);

        //Salt...
        if (!this.jsonObject.isNull(JSONMapping.SALT)) {
            this.setSalt(this.jsonObject.getString(JSONMapping.SALT));
        }

        //Encrypted Data Base64
        if (!this.jsonObject.isNull(JSONMapping.ENCRYPTED_DATA_BASE_64)) {
            this.setEncryptedDataBase64(this.jsonObject.getString(JSONMapping.ENCRYPTED_DATA_BASE_64));
        }

        //Encrypted Data HMAC Base64
        if (!this.jsonObject.isNull(JSONMapping.ENCRYPTED_DATA_HMAC_BASE_64)) {
            this.setEncryptedDataHmacBase64(this.jsonObject.getString(JSONMapping.ENCRYPTED_DATA_HMAC_BASE_64));
        }

        //IV Base 64...
        if (!this.jsonObject.isNull(JSONMapping.IV_BASE_64)) {
            this.setIvBase64(this.jsonObject.getString(JSONMapping.IV_BASE_64));
        }

        //IV Base 64...
        if (!this.jsonObject.isNull(JSONMapping.SEED_BASE_64)) {
            this.setSeedBase64(this.jsonObject.getString(JSONMapping.SEED_BASE_64));
        }

        //Service Ticket 64...
        if (!this.jsonObject.isNull(JSONMapping.SERVICE_TICKET_BASE_64)) {
            this.setServiceTicketBase64(this.jsonObject.getString(JSONMapping.SERVICE_TICKET_BASE_64));
        }
    }

    /**
     *
     * @return
     */
    public String getSalt() {
        return this.salt;
    }

    /**
     *
     * @param salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     *
     * @return
     */
    public String getEncryptedDataBase64() {
        return this.encryptedDataBase64;
    }

    /**
     *
     * @param encryptedDataBase64
     */
    public void setEncryptedDataBase64(String encryptedDataBase64) {
        this.encryptedDataBase64 = encryptedDataBase64;
    }

    /**
     *
     * @return
     */
    public String getEncryptedDataHmacBase64() {
        return this.encryptedDataHmacBase64;
    }

    /**
     *
     * @param encryptedDataHmacBase64
     */
    public void setEncryptedDataHmacBase64(String encryptedDataHmacBase64) {
        this.encryptedDataHmacBase64 = encryptedDataHmacBase64;
    }

    /**
     *
     * @return
     */
    public String getIvBase64() {
        return this.ivBase64;
    }

    /**
     *
     * @param ivBase64
     */
    public void setIvBase64(String ivBase64) {
        this.ivBase64 = ivBase64;
    }

    /**
     *
     * @return
     */
    public String getSeedBase64() {
        return this.seedBase64;
    }

    /**
     *
     * @param seedBase64
     */
    public void setSeedBase64(String seedBase64) {
        this.seedBase64 = seedBase64;
    }

    /**
     *
     * @return
     */
    public String getServiceTicketBase64() {
        return this.serviceTicketBase64;
    }

    /**
     *
     * @param serviceTicketBase64
     */
    public void setServiceTicketBase64(String serviceTicketBase64) {
        this.serviceTicketBase64 = serviceTicketBase64;
    }

    /**
     *
     * @return
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Salt...
        if (this.getSalt() != null) {
            returnVal.put(JSONMapping.SALT, this.getSalt());
        }

        //Encrypted Data Base 64...
        if (this.getEncryptedDataBase64() != null) {
            returnVal.put(JSONMapping.ENCRYPTED_DATA_BASE_64, this.getEncryptedDataBase64());
        }

        //Encrypted Data HMAC Base 64...
        if (this.getEncryptedDataHmacBase64() != null) {
            returnVal.put(JSONMapping.ENCRYPTED_DATA_HMAC_BASE_64, this.getEncryptedDataHmacBase64());
        }

        //IV Base 64...
        if (this.getIvBase64() != null) {
            returnVal.put(JSONMapping.IV_BASE_64, this.getIvBase64());
        }

        //Seed Base 64...
        if (this.getSeedBase64() != null) {
            returnVal.put(JSONMapping.SEED_BASE_64, this.getSeedBase64());
        }

        //Service Ticket Base 64...
        if (this.getServiceTicketBase64() != null) {
            returnVal.put(JSONMapping.SERVICE_TICKET_BASE_64, this.getServiceTicketBase64());
        }

        return returnVal;
    }
}
