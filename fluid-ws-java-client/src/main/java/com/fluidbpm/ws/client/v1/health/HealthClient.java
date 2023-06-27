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

package com.fluidbpm.ws.client.v1.health;

import com.fluidbpm.program.api.vo.health.ConnectStatus;
import com.fluidbpm.program.api.vo.health.ExtendedServerHealth;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONObject;

/**
 * Java Web Service Client for {@code Health} check related actions.
 *
 * @author jasonbruwer
 * @since v1.13
 *
 * @see JSONObject
 * @see WS.Path.Report
 * @see com.fluidbpm.program.api.vo.health.Health
 */
public class HealthClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public HealthClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Retrieve core Fluid version info as well as basic health check information.
	 *
	 * @return ConnectStatus information.
	 *
	 * @see ConnectStatus
	 */
	public ConnectStatus getHealthAndServerInfo() {
		return new ConnectStatus(this.getJson(false, WS.Path.Test.Version1.healthAndServerInfo()));
	}

	/**
	 * Retrieve extended health information in regard to Fluid being able to connect and operate on external services.
	 * These include:
	 * - 3rd party integrations
	 * - Elasticsearch
	 * - Redis
	 * - Email
	 * - Additional data-sources
	 * - etc.
	 *
	 * @return ConnectStatus information.
	 *
	 * @see ExtendedServerHealth
	 */
	public ExtendedServerHealth getExtendedHealthAndServerInfo() {
		ExtendedServerHealth req = new ExtendedServerHealth();
		req.setServiceTicket(this.serviceTicket);

		return new ExtendedServerHealth(this.postJson(req, WS.Path.Test.Version1.extendedHealthAndServerInfo()));
	}
}
