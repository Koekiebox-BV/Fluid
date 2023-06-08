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
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestReportUserClient extends ABaseLoggedInTestCase {

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
	public void testReport_OneWeek() {
		if (this.isConnectionInValid) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		ReportUserClient reportUserClient = new ReportUserClient(BASE_URL, serviceTicket);

		long now = System.currentTimeMillis();
		long twoMonths = now - TimeUnit.DAYS.toMillis(61);
		UserStatsReport report = reportUserClient.getLoggedInUserStats(new Date(twoMonths), new Date(now));
		long took = (System.currentTimeMillis() - now);
		TestCase.assertNotNull("SystemReport needs to be set.", report);
		TestCase.assertNotNull("SystemReport entries need to be set.", report.getPunchCardEntries());
		TestCase.assertFalse("SystemReport entries need to be set.", report.getPunchCardEntries().isEmpty());
		TestCase.assertTrue("SystemReport timed out.", took < 1000);
	}

	@Test(timeout = 5000)
	public void testReport_Default() {
		if (this.isConnectionInValid) {
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
		//PunchCard...
		TestCase.assertNotNull("PunchCard entries need to be set.", report.getPunchCardEntries());
		TestCase.assertFalse("PunchCard entries need to be set.", report.getPunchCardEntries().isEmpty());
		//CreateUpdateLockUnlock...
		//TestCase.assertNotNull("CreateUpdateLockUnlock entries need to be set.", report.getCreateUpdateLockUnlockEntries());
		//TestCase.assertFalse("CreateUpdateLockUnlock entries need to be set.", report.getCreateUpdateLockUnlockEntries().isEmpty());
		//CreateUpdateLockUnlock...
		//TestCase.assertNotNull("View entries need to be set.", report.getViewOpenedAndSentOnEntries());
		//TestCase.assertFalse("View entries need to be set.", report.getViewOpenedAndSentOnEntries().isEmpty());
		TestCase.assertTrue("SystemReport timed out.", took < 1000);
	}
}
