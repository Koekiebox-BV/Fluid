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
 * Created by jasonbruwer on 14/12/22.
 */
public class TokenStatus extends ABaseFluidJSONObject {

    private Long expirationTime;
    private String tokenMessage;

    /**
     *
     */
    public TokenStatus() {
        super();
    }


    /**
     *
     * @return
     */
    public Long getExpirationTime() {
        return this.expirationTime;
    }

    /**
     *
     * @param expirationTime
     */
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     *
     * @return
     */
    public String getTokenMessage() {
        return this.tokenMessage;
    }

    /**
     *
     * @param tokenMessage
     */
    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    /**
     *
     * @return
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
