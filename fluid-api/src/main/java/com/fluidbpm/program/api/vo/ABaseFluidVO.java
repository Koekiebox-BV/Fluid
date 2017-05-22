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

package com.fluidbpm.program.api.vo;

import java.io.Serializable;
import java.util.Base64;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.user.User;

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
 * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
 */
public class ABaseFluidVO implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    protected String serviceTicket;
    protected String echo;

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
     * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
     */
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    /**
     * Gets the Service Ticket associated with the Fluid session as HEX.
     *
     * @return Service Ticket of Fluid session in Hex format.
     *
     * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
     */
    public String getServiceTicketAsHexUpper() {

        String serviceTicket = this.getServiceTicket();

        if(serviceTicket == null)
        {
            return null;
        }

        if(serviceTicket.isEmpty())
        {
            return serviceTicket;
        }

        byte[] base64Bytes = Base64.getDecoder().decode(serviceTicket);

        return this.bytesToHex(base64Bytes);
    }

    final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Convert the byte[] to a HEX string as upper case.
     *
     * @param bytesToConvert The {@code byte[]} to convert.
     * @return String with upper case HEX.
     */
    private String bytesToHex(byte[] bytesToConvert) {

        if(bytesToConvert == null)
        {
            return null;
        }

        if(bytesToConvert.length == 0)
        {
            return UtilGlobal.EMPTY;
        }

        char[] hexChars = new char[bytesToConvert.length * 2];

        for ( int index = 0; index < bytesToConvert.length; index++ ) {
            int andWith127 = (bytesToConvert[index] & 0xFF);
            hexChars[index * 2] = HEX_ARRAY[andWith127 >>> 4];
            hexChars[index * 2 + 1] = HEX_ARRAY[andWith127 & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * Sets the Service Ticket associated with the Fluid session.
     *
     * @param serviceTicketParam Service Ticket of Fluid session.
     *
     * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
     */
    public void setServiceTicket(String serviceTicketParam) {
        this.serviceTicket = serviceTicketParam;
    }

    /**
     * Sets the Echo message for tracking async messages.
     *
     * @return Echo message.
     */
    public String getEcho() {
        return this.echo;
    }

    /**
     * Sets the Echo message for tracking async messages.
     *
     * @param echoParam Echo message.
     */
    public void setEcho(String echoParam) {
        this.echo = echoParam;
    }

    /**
     * Gets the {@code User} associated with the session.
     *
     * @return The Logged in user associated with the session.
     *
     * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
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
     * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
     * @see User
     */
    public void setLoggedInUserFromTicket(User loggedInUserFromTicket) {
        this.loggedInUserFromTicket = loggedInUserFromTicket;
    }
}
