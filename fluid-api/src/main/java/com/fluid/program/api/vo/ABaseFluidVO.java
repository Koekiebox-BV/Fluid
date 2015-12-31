package com.fluid.program.api.vo;

import java.io.Serializable;

/**
 * User: jasonbruwer Date: 2014/07/07 Time: 10:01 AM
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
	 * 
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
