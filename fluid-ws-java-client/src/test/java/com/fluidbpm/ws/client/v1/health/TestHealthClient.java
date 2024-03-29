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
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestHealthClient extends ABaseLoggedInTestCase {

	private LoginClient loginClient;

	/**
	 *
	 */
	@Before
	public void init() {
		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

		this.loginClient = new LoginClient(BASE_URL);
	}

	@After
	public void destroy()
	{
		this.loginClient.closeAndClean();
	}

	@Test
	public void testBasicHealthAndServerInfo() {
		if (this.isConnectionInValid) return;

		HealthClient healthClient = new HealthClient(BASE_URL, ADMIN_SERVICE_TICKET);
		ConnectStatus connectStatus = healthClient.getHealthAndServerInfo();
		TestCase.assertNotNull(connectStatus);
		TestCase.assertNotNull(connectStatus.getTimestamp());
		TestCase.assertNotNull(connectStatus.getDatabaseHealth());
		TestCase.assertNotNull(connectStatus.getSystemHealth());
		TestCase.assertNotNull(connectStatus.getVersion());
		TestCase.assertNotNull(connectStatus.getFluidAPIVersion());
		TestCase.assertNotNull(connectStatus.getInternalTimeZone());
		TestCase.assertNotNull(connectStatus.getConnectObtainDurationMillis());
	}

	@Test
	public void testExtendedHealthAndServerInfo() {
		if (this.isConnectionInValid) return;

		HealthClient healthClient = new HealthClient(BASE_URL, ADMIN_SERVICE_TICKET);
		ExtendedServerHealth ext = healthClient.getExtendedHealthAndServerInfo();
		TestCase.assertNotNull(ext);
	}
}
