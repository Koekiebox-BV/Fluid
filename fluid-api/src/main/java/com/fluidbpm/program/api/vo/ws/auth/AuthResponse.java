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
 * Response to an authorization request.
 *
 * @author jasonbruwer
 * @see ABaseFluidJSONObject
 * @see TokenStatus
 * @see AppRequestToken
 * @see AuthRequest
 * @see AuthEncryptedData
 * @since v1.0
 */
public class AuthResponse extends ABaseFluidJSONObject {

    private static final long serialVersionUID = 1L;

    private String salt;

    //Payload...
    private String encryptedDataBase64;//Contains [TicketExpires], [Roles] and [SessionKey]...
    private String encryptedDataHmacBase64;
    private String ivBase64;
    private String seedBase64;

    //Service Ticket...
    private String serviceTicketBase64;

    /**
     * The JSON mapping for the {@code AuthResponse} object.
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
     * Default constructor.
     */
    public AuthResponse() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AuthResponse(JSONObject jsonObjectParam) {
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
     * Gets 20 Random ASCII {@code Character}s
     *
     * @return 20 Random ASCII {@code Character}s paired with the {@code User} password.
     */
    public String getSalt() {
        return this.salt;
    }

    /**
     * Sets 20 Random ASCII {@code Character}s
     *
     * @param saltParam 20 Random ASCII {@code Character}s paired with the {@code User} password.
     */
    public void setSalt(String saltParam) {
        this.salt = saltParam;
    }

    /**
     * Gets the Encrypted Data in Base-64 format.
     *
     * @return Encrypted Data in Base-64 format.
     */
    public String getEncryptedDataBase64() {
        return this.encryptedDataBase64;
    }

    /**
     * Sets the Encrypted Data in Base-64 format.
     *
     * @param encryptedDataBase64 Encrypted Data in Base-64 format.
     */
    public void setEncryptedDataBase64(String encryptedDataBase64) {
        this.encryptedDataBase64 = encryptedDataBase64;
    }

    /**
     * Gets the Encrypted Data for HMAC in Base-64 format.
     *
     * @return HMAC Encrypted Data in Base-64 format.
     */
    public String getEncryptedDataHmacBase64() {
        return this.encryptedDataHmacBase64;
    }

    /**
     * Sets the Encrypted Data for HMAC in Base-64 format.
     *
     * @param encryptedDataHmacBase64 HMAC Encrypted Data in Base-64 format.
     */
    public void setEncryptedDataHmacBase64(String encryptedDataHmacBase64) {
        this.encryptedDataHmacBase64 = encryptedDataHmacBase64;
    }

    /**
     * Gets the initialization vector in Base-64 format.
     *
     * @return Initialization Vector in Base-64.
     */
    public String getIvBase64() {
        return this.ivBase64;
    }

    /**
     * Sets the initialization vector in Base-64 format.
     *
     * @param ivBase64 Initialization Vector in Base-64.
     */
    public void setIvBase64(String ivBase64) {
        this.ivBase64 = ivBase64;
    }

    /**
     * Gets the seed in Base-64 format.
     * <p>
     * https://en.wikipedia.org/wiki/SEED
     *
     * @return Seed in Base-64 format.
     */
    public String getSeedBase64() {
        return this.seedBase64;
    }

    /**
     * Sets the seed in Base-64 format.
     * <p>
     * https://en.wikipedia.org/wiki/SEED
     *
     * @param seedBase64 Seed in Base-64 format.
     */
    public void setSeedBase64(String seedBase64) {
        this.seedBase64 = seedBase64;
    }

    /**
     * Gets the Service Ticket in Base-64 format.
     *
     * @return Service Ticket Base-64.
     */
    public String getServiceTicketBase64() {
        return this.serviceTicketBase64;
    }

    /**
     * Sets the Service Ticket in Base-64 format.
     *
     * @param serviceTicketBase64 Service Ticket Base-64.
     */
    public void setServiceTicketBase64(String serviceTicketBase64) {
        this.serviceTicketBase64 = serviceTicketBase64;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AuthResponse}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
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
