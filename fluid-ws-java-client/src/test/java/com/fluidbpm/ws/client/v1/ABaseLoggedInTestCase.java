/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1;

import com.fluidbpm.ws.client.v1.user.LoginClient;
import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;

/**
 * Base test case for a client where you are already logged in.
 */
@Log
public class ABaseLoggedInTestCase extends ABaseTestCase {
	private LoginClient loginClient;
	protected String serviceTicket;

	/**
	 * Initialize.
	 */
	@Before
	public void init() {
		if (!this.isConnectionValid()) return;

		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
		this.loginClient = new LoginClient(BASE_URL);
		this.serviceTicket = this.loginClient.login(USERNAME, PASSWORD).getServiceTicket();
	}

	/**
	 * Teardown.
	 */
	@After
	public void destroy() {
		if (!this.isConnectionValid()) return;
		this.loginClient.closeAndClean();
	}
}
