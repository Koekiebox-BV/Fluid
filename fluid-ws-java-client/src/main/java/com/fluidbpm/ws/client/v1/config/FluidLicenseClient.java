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

package com.fluidbpm.ws.client.v1.config;

import org.apache.http.entity.ContentType;

import com.fluidbpm.program.api.vo.license.LicenseRequest;
import com.fluidbpm.program.api.vo.ws.WS.Path.License.Version1;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;

/**
 * Java Web Service Client for Fluid License related actions.
 *
 * @author jasonbruwer on 2018-05-06
 * @version v1.8
 *
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.License
 * @see com.fluidbpm.program.api.vo.license.LicenseRequest
 */
public class FluidLicenseClient extends ABaseFieldClient {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public FluidLicenseClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Request a new license based on the license request input.
	 *
	 * It is important to set the following fields.
	 * {@code Valid From}
	 * {@code Valid To}
	 * {@code User Count}
	 *
	 * @param licenseRequest The license request used to issue a license request from.
	 * @return The License Request file.
	 */
	public String requestLicense(LicenseRequest licenseRequest) {

		if (licenseRequest != null) licenseRequest.setServiceTicket(this.serviceTicket);

		return this.executeTxtReceiveTxt(
			HttpMethod.POST,
			null,
			false,
			(licenseRequest == null) ? null :
					licenseRequest.toJsonObject().toString(),
			ContentType.APPLICATION_JSON,
			Version1.licenseRequest()
		);
	}

	/**
	 * Applies a generated license for the server.
	 *
	 * @param licenseToApply The clear license to apply.
	 * @return The applied license.
	 */
	public LicenseRequest applyLicense(String licenseToApply) {
		LicenseRequest liceReq = new LicenseRequest();
		liceReq.setLicenseCipherText(licenseToApply);
		liceReq.setServiceTicket(this.serviceTicket);
		return new LicenseRequest(this.postJson(liceReq, Version1.licenseApply()));
	}
}
