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

package com.fluidbpm.ws.client.v1.sqlutil.wrapper;

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestSQLUtilWebSocketRESTWrapper extends ABaseTestCase {

	private LoginClient loginClient;

	/**
	 *
	 */
	public static final class TestStatics{
		public static final String FORM_DEFINITION = "Email";
		public static final String FORM_TITLE_PREFIX = "Test api doc with email...";
	}

	/**
	 *
	 */
	@Before
	public void init() {
		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
		this.loginClient = new LoginClient(BASE_URL);
	}

	/**
	 *
	 */
	@After
	public void destroy()
	{
		this.loginClient.closeAndClean();
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testExecuteSelect() {
		if (!this.isConnectionValid()) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, serviceTicket);

        NativeSQLQuery query = new NativeSQLQuery();
        query.setDatasourceName(FLUID_DS);
        query.setQuery("SELECT 1");

        SQLResultSet result = sqlUtilClient.executeSQL(query);
        System.out.println(result);
	}
}
