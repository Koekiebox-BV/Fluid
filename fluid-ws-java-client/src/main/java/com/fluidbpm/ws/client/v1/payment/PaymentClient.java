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

package com.fluidbpm.ws.client.v1.payment;

import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.payment.PaymentLinkAdyen;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONObject;

/**
 * Used to change any of the Flow's / Workflows.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see JSONObject
 * @see WS.Path.Configuration
 * @see Configuration
 * @see ABaseClientWS
 */
public class PaymentClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public PaymentClient(
		String endpointBaseUrlParam,
		String serviceTicketParam
	) {
		super(endpointBaseUrlParam);
		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Request a new Adyen Payment Link for {@code fluidItem}.
	 * If there is a payment link already, the existing payment link will be sent.
	 * If a payment link has already been paid, an error will be generated.
	 *
	 * @param fluidItem The item to generate a Payment Link for.
	 * @return {@code PaymentLinkAdyen} with the redirection link (payment location).
	 * @see FluidItem
	 * @see PaymentLinkAdyen
	 */
	public PaymentLinkAdyen obtainPaymentLink(FluidItem fluidItem) {
		if (fluidItem != null) fluidItem.setServiceTicket(this.serviceTicket);
		
		return new PaymentLinkAdyen(this.postJson(fluidItem, WS.Path.Payment.Version1.adyenPaymentLinkRequest()));
	}
	
}
