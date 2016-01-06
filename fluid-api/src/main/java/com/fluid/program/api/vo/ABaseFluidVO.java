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
 * <p>
 *     Contains common properties such as {@code id} and {@code serviceTicket}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Serializable
 * @see User
 * @see com.fluid.program.api.vo.ws.auth.ServiceTicket
 */
public class ABaseFluidVO implements Serializable {

    private Long id;
    protected String serviceTicket;

    private User loggedInUserFromTicket;

    /**
     * Sets the Id associated with any Fluid entity.
     *
     * @param idParam Unique Identifier.
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
     * Gets the Id associated with any Fluid entity.
     *
     * @return Id associated with {@code this} entity.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the Id associated with any Fluid entity.
     * 
     * @param idParam Id associated with {@code this} entity.
     */
    public void setId(Long idParam) {
        this.id = idParam;
    }

    /**
     * Gets the Service Ticket associated with the Fluid session.
     *
     * @return Service Ticket of Fluid session.
     *
     * @see com.fluid.program.api.vo.ws.auth.ServiceTicket
     */
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    /**
     * Sets the Service Ticket associated with the Fluid session.
     *
     * @param serviceTicketParam Service Ticket of Fluid session.
     *
     * @see com.fluid.program.api.vo.ws.auth.ServiceTicket
     */
    public void setServiceTicket(String serviceTicketParam) {
        this.serviceTicket = serviceTicketParam;
    }

    /**
     * Gets the {@code User} associated with the session.
     *
     * @return The Logged in user associated with the session.
     *
     * @see com.fluid.program.api.vo.ws.auth.ServiceTicket
     * @see User
     */
    public User getLoggedInUserFromTicket() {
        return this.loggedInUserFromTicket;
    }

    /**
     * Sets the {@code User} associated with the session.
     *
     * @param loggedInUserFromTicket The Logged in user associated with the session.
     *
     * @see com.fluid.program.api.vo.ws.auth.ServiceTicket
     * @see User
     */
    public void setLoggedInUserFromTicket(User loggedInUserFromTicket) {
        this.loggedInUserFromTicket = loggedInUserFromTicket;
    }
}
