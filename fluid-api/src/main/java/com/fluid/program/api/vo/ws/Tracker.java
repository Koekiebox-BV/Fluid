package com.fluid.program.api.vo.ws;

import com.fluid.program.api.vo.ABaseFluidVO;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class Tracker extends ABaseFluidVO {

    private String successMessage;
    private String trackerId;

    private Long locatorId;

    /**
     *
     */
    public Tracker() {
        super();
    }

    /**
     *
     * @return
     */
    public String getSuccessMessage() {
        return this.successMessage;
    }

    /**
     *
     * @param successMessageParam
     */
    public void setSuccessMessage(String successMessageParam) {
        this.successMessage = successMessageParam;
    }

    /**
     *
     * @return
     */
    public String getTrackerId() {
        return this.trackerId;
    }

    /**
     *
     * @param trackerId
     */
    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    /**
     *
     * @return
     */
    public Long getLocatorId() {
        return this.locatorId;
    }

    /**
     *
     * @param locatorId
     */
    public void setLocatorId(Long locatorId) {
        this.locatorId = locatorId;
    }
}
