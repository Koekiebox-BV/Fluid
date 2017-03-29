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

import java.util.Date;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * Status of a previously issued authentication token.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidJSONObject
 * @see ServiceTicket
 * @see AppRequestToken
 */
public class TokenStatus extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private Long expirationTime;
    private String tokenMessage;

    /**
     * Default constructor.
     */
    public TokenStatus() {
        super();
    }

    /**
     * Gets the Expiration time in millis.
     *
     * @return Expiration Time.
     */
    public Long getExpirationTime() {
        return this.expirationTime;
    }

    /**
     * Sets the Expiration time in millis.
     *
     * @param expirationTime The expiration time.
     */
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Gets the token message.
     *
     * @return The token message.
     */
    public String getTokenMessage() {
        return this.tokenMessage;
    }

    /**
     * Sets the token message.
     *
     * @param tokenMessage The token message to set.
     */
    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    /**
     * Check the {@code getExpirationTime} to confirm whether
     * the ticket has expired.
     *
     * @return Whether the ticket has expired.
     */
    public boolean isExpired()
    {
        if(this.getExpirationTime() == null)
        {
            return true;
        }

        Date expirationTime = new Date(this.getExpirationTime());

        return (expirationTime.before(new Date()));
    }
}
