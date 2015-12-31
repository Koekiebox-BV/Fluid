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
