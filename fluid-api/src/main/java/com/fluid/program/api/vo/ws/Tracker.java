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
