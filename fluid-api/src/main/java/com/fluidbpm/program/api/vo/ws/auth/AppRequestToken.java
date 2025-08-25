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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * Authorization request token data.
 *
 * @author jasonbruwer
 * @see ABaseFluidJSONObject
 * @see TokenStatus
 * @see AuthRequest
 * @see AuthResponse
 * @see AuthEncryptedData
 * @since v1.0
 */
public class AppRequestToken extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    //Payload...
    private String encryptedDataBase64;//Contains [TicketExpires], [Roles] and [SessionKey]...
    private String encryptedDataHmacBase64;
    private String ivBase64;
    private String seedBase64;

    //
    private String principalClient;
    private String salt;
    private String roleString;
    private Long timestamp;

    /**
     * The JSON mapping for the {@code AppRequestToken} object.
     */
    public static class JSONMapping {
        public static final String ENCRYPTED_DATA_BASE_64 = "encryptedDataBase64";
        public static final String ENCRYPTED_DATA_HMAC_BASE_64 = "encryptedDataHmacBase64";
        public static final String IV_BASE_64 = "ivBase64";
        public static final String SEED_BASE_64 = "seedBase64";

        public static final String SALT = "salt";
        public static final String PRINCIPAL_CLIENT = "principalClient";
        public static final String ROLE_STRING = "roleString";
        public static final String TIMESTAMP = "timestamp";
    }

    /**
     * Default constructor.
     */
    public AppRequestToken() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AppRequestToken(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

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

        //Seed Base 64...
        if (!this.jsonObject.isNull(JSONMapping.SEED_BASE_64)) {
            this.setSeedBase64(this.jsonObject.getString(JSONMapping.SEED_BASE_64));
        }

        //Service Ticket 64...
        if (!this.jsonObject.isNull(ABaseFluidJSONObject.JSONMapping.SERVICE_TICKET)) {
            this.setServiceTicket(this.jsonObject.getString(ABaseFluidJSONObject.JSONMapping.SERVICE_TICKET));
        }

        //Salt...
        if (!this.jsonObject.isNull(JSONMapping.SALT)) {
            this.setSalt(this.jsonObject.getString(JSONMapping.SALT));
        }

        //Principal Client...
        if (!this.jsonObject.isNull(JSONMapping.PRINCIPAL_CLIENT)) {
            this.setPrincipalClient(this.jsonObject.getString(JSONMapping.PRINCIPAL_CLIENT));
        }

        //Role String...
        if (!this.jsonObject.isNull(JSONMapping.ROLE_STRING)) {
            this.setRoleString(this.jsonObject.getString(JSONMapping.ROLE_STRING));
        }

        //Timestamp...
        if (!this.jsonObject.isNull(JSONMapping.TIMESTAMP)) {
            this.setTimestamp(this.jsonObject.getLong(JSONMapping.TIMESTAMP));
        }
    }

    /**
     * Gets the Principal Client.
     *
     * @return Principal Client.
     */
    public String getPrincipalClient() {
        return this.principalClient;
    }

    /**
     * Sets the Principal Client.
     *
     * @param principalClient Principal Client.
     */
    public void setPrincipalClient(String principalClient) {
        this.principalClient = principalClient;
    }

    /**
     * Gets the Timestamp.
     *
     * @return The timestamp milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Sets the Timestamp.
     *
     * @param timestamp The timestamp milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the EncryptedData in Base64.
     *
     * @return EncryptedData in Base64.
     */
    public String getEncryptedDataBase64() {
        return this.encryptedDataBase64;
    }

    /**
     * Sets the EncryptedData in Base64.
     *
     * @param encryptedDataBase64 EncryptedData in Base64.
     */
    public void setEncryptedDataBase64(String encryptedDataBase64) {
        this.encryptedDataBase64 = encryptedDataBase64;
    }

    /**
     * Gets the EncryptedData for HMAC in Base64.
     *
     * @return Encrypted Data in Hmac Base64.
     */
    public String getEncryptedDataHmacBase64() {
        return this.encryptedDataHmacBase64;
    }

    /**
     * Sets the EncryptedData for HMAC in Base64.
     *
     * @param encryptedDataHmacBase64 Encrypted Data in Hmac Base64.
     */
    public void setEncryptedDataHmacBase64(String encryptedDataHmacBase64) {
        this.encryptedDataHmacBase64 = encryptedDataHmacBase64;
    }

    /**
     * Gets the Initialization Vector in Base-64.
     *
     * @return Initialization Vector in Base-64 format.
     */
    public String getIvBase64() {
        return this.ivBase64;
    }

    /**
     * Sets the Initialization Vector in Base-64.
     *
     * @param ivBase64Param Initialization Vector in Base-64 format.
     */
    public void setIvBase64(String ivBase64Param) {
        this.ivBase64 = ivBase64Param;
    }

    /**
     * Gets the SEED in Base-64 format.
     *
     * @return SEED in Base-64 format.
     */
    public String getSeedBase64() {
        return this.seedBase64;
    }

    /**
     * Sets the SEED in Base-64 format.
     *
     * @param seedBase64 SEED in Base-64 format.
     */
    public void setSeedBase64(String seedBase64) {
        this.seedBase64 = seedBase64;
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
     * Gets the role listing separated by ','.
     *
     * @return List of roles.
     */
    public String getRoleString() {
        return this.roleString;
    }

    /**
     * Sets the role listing separated by ','.
     *
     * @param roleStringParam List of roles.
     */
    public void setRoleString(String roleStringParam) {
        this.roleString = roleStringParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AppRequestToken}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();

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
        if (this.getServiceTicket() != null) {
            returnVal.put(ABaseFluidJSONObject.JSONMapping.SERVICE_TICKET, this.getServiceTicket());
        }

        //Salt...
        if (this.getSalt() != null) {
            returnVal.put(JSONMapping.SALT, this.getSalt());
        }

        //Principal Client...
        if (this.getPrincipalClient() != null) {
            returnVal.put(JSONMapping.PRINCIPAL_CLIENT, this.getPrincipalClient());
        }

        //Role String...
        if (this.getRoleString() != null) {
            returnVal.put(JSONMapping.ROLE_STRING, this.getRoleString());
        }

        //Timestamp...
        if (this.getTimestamp() != null) {
            returnVal.put(JSONMapping.TIMESTAMP, this.getTimestamp());
        }

        return returnVal;
    }
}
