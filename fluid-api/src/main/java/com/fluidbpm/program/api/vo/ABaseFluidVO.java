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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.user.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *     Base Value Object for any Fluid Value Object.
 *
 * <p>
 *     Contains common properties such as {@code id}, {@code serviceTicket} and {@code requestUuid}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Serializable
 * @see User
 * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
 */
@Getter
@Setter
public class ABaseFluidVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	protected String serviceTicket;
	protected String requestUuid;
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
	 * Gets the Service Ticket associated with the Fluid session as HEX.
	 *
	 * @return Service Ticket of Fluid session in Hex format.
	 *
	 * @see com.fluidbpm.program.api.vo.ws.auth.ServiceTicket
	 */
	public String getServiceTicketAsHexUpper() {
		String serviceTicket = this.getServiceTicket();
		if (serviceTicket == null) return null;
		if (serviceTicket.isEmpty()) return serviceTicket;
		byte[] base64Bytes = UtilGlobal.decodeBase64(serviceTicket);
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
        if (bytesToConvert == null) return null;
        else if (bytesToConvert.length == 0) return UtilGlobal.EMPTY;

		char[] hexChars = new char[bytesToConvert.length * 2];
		for ( int index = 0; index < bytesToConvert.length; index++ ) {
			int andWith127 = (bytesToConvert[index] & 0xFF);
			hexChars[index * 2] = HEX_ARRAY[andWith127 >>> 4];
			hexChars[index * 2 + 1] = HEX_ARRAY[andWith127 & 0x0F];
		}
		return new String(hexChars);
	}
}
