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

import com.fluidbpm.program.api.vo.report.system.SystemUptimeReport;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestReportSystemClient extends ABaseTestCase {

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

	@Test
	public void testReport_FullReport() {
		if (!this.isConnectionValid()) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		ReportSystemClient reportSystemClient = new ReportSystemClient(BASE_URL, serviceTicket);

		long now = System.currentTimeMillis();
		SystemUptimeReport report = reportSystemClient.getSystemReport(true, null);
		long took = (System.currentTimeMillis() - now);
		TestCase.assertNotNull("SystemReport needs to be set.", report);
		TestCase.assertNotNull("SystemReport entries need to be set.", report.getUptimeEntries());
		TestCase.assertFalse("SystemReport entries need to be set.", report.getUptimeEntries().isEmpty());
		System.out.printf("Took %d millis for %d entries and %d for subs.", took, report.getUptimeEntries().size(), report.getUptimeEntries().get(0).getSystemUpHourMins().size());

		report.getUptimeEntries().forEach(itmYearDay -> {
			System.out.printf("\nYearDay[%d:%d]", itmYearDay.getYear(), itmYearDay.getDay());
			if (itmYearDay.getSystemUpHourMins() == null) {
				return;
			}

			itmYearDay.getSystemUpHourMins().forEach(hourMin -> {
				System.out.printf("\nHourMin[%d:%d] - %s", hourMin.getHour(), hourMin.getMinute(), hourMin.getState());
			});
		});
	}

	@Test
	public void testReport_DownReport() {
		if (!this.isConnectionValid()) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		ReportSystemClient reportSystemClient = new ReportSystemClient(BASE_URL, serviceTicket);

		long now = System.currentTimeMillis();
		SystemUptimeReport report = reportSystemClient.getDowntimeSystemReport();
		long took = (System.currentTimeMillis() - now);
		TestCase.assertNotNull("SystemReport needs to be set.", report);
		if (report.getUptimeEntries() != null && !report.getUptimeEntries().isEmpty()) {
			System.out.printf("Took %d millis for %d entries and %d for subs.", took, report.getUptimeEntries().size(), report.getUptimeEntries().get(0).getSystemUpHourMins().size());
		} else {
			System.out.println("No downtime.");
		}
	}

	@Test(timeout = 5000)
	public void testReport_UpReport() {
		if (!this.isConnectionValid()) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();
		ReportSystemClient reportSystemClient = new ReportSystemClient(BASE_URL, serviceTicket);

		long now = System.currentTimeMillis();
		SystemUptimeReport report = reportSystemClient.getUptimeSystemReport();
		long took = (System.currentTimeMillis() - now);
		TestCase.assertNotNull("SystemReport needs to be set.", report);
		TestCase.assertNotNull("SystemReport entries need to be set.", report.getUptimeEntries());
		TestCase.assertFalse("SystemReport entries need to be set.", report.getUptimeEntries().isEmpty());
		TestCase.assertTrue("SystemReport timed out.", took < 1000);
		System.out.printf("Took %d millis for %d entries and %d for subs.", took, report.getUptimeEntries().size(), report.getUptimeEntries().get(0).getSystemUpHourMins().size());
	}
}
