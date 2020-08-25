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

package com.fluidbpm.ws.client.v1.report;

import com.fluidbpm.program.api.vo.report.userstats.UserStatsReport;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestReportUserClient extends ABaseTestCase {

	private LoginClient loginClient;

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

	@Test(timeout = 5000)
	public void testReport_UpReport() {
		if (!this.isConnectionValid()) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		ReportUserClient reportUserClient = new ReportUserClient(BASE_URL, serviceTicket);

		long now = System.currentTimeMillis();
		UserStatsReport report = reportUserClient.getLoggedInUserStats();
		long took = (System.currentTimeMillis() - now);
		TestCase.assertNotNull("SystemReport needs to be set.", report);
		TestCase.assertNotNull("SystemReport entries need to be set.", report.getPunchCardEntries());
		TestCase.assertFalse("SystemReport entries need to be set.", report.getPunchCardEntries().isEmpty());
		TestCase.assertTrue("SystemReport timed out.", took < 1000);
	}
}
