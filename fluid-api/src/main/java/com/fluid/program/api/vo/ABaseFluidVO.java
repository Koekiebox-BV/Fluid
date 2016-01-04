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

package com.fluid.program.api.vo;

import java.io.Serializable;

/**
 * <p>
 *     Base Value Object for any Fluid Value Object.
 *
 *     Contains common properties such as {@code id} and {@code serviceTicket}.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Serializable
 */
public class ABaseFluidVO implements Serializable {

    private Long id;
    protected String serviceTicket;

    private User loggedInUserFromTicket;

    /**
     * 
     * @param idParam
     */
    public ABaseFluidVO(Long idParam) {
        super();
        this.id = idParam;
    }

    /**
	 * Default constructor.
	 */
    public ABaseFluidVO() {
        super();
    }

    /**
     * 
     * @return
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 
     * @param idParam
     */
    public void setId(Long idParam) {
        this.id = idParam;
    }

    /**
     *
     * @return
     */
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    /**
     *
     * @param serviceTicketParam
     */
    public void setServiceTicket(String serviceTicketParam) {
        this.serviceTicket = serviceTicketParam;
    }

    /**
     *
     * @return
     */
    public User getLoggedInUserFromTicket() {
        return this.loggedInUserFromTicket;
    }

    /**
     *
     * @param loggedInUserFromTicket
     */
    public void setLoggedInUserFromTicket(User loggedInUserFromTicket) {
        this.loggedInUserFromTicket = loggedInUserFromTicket;
    }
}
