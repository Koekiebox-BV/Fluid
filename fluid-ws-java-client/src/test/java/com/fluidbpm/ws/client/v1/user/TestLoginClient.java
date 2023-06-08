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

package com.fluidbpm.ws.client.v1.user;

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestLoginClient extends ABaseLoggedInTestCase {

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
	public void testLogin() {
		if (this.isConnectionInValid) return;

		//First...
		AppRequestToken firstAppRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(firstAppRequestToken);

		String firstServiceTicket = firstAppRequestToken.getServiceTicket();
		TestCase.assertNotNull(firstServiceTicket);
		log.info("1st Token: "+firstServiceTicket);

		//Second...
		AppRequestToken secondAppRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(secondAppRequestToken);

		String secondServiceTicket = secondAppRequestToken.getServiceTicket();
		TestCase.assertNotNull(secondServiceTicket);
		log.info("2nd Token: "+secondServiceTicket);

		TestCase.assertNotSame("(1 and 2) Service Tickets not allowed to match."
				,firstServiceTicket,secondServiceTicket);

		//Third...
		AppRequestToken thirdAppRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(thirdAppRequestToken);

		String thirdServiceTicket = thirdAppRequestToken.getServiceTicket();
		TestCase.assertNotNull(thirdServiceTicket);
		log.info("3rd Token: "+thirdServiceTicket);

		TestCase.assertNotSame("(2 and 3) Service Tickets not allowed to match."
				,secondServiceTicket,thirdServiceTicket);
	}

	@Test
	public void testLoginBASIC() {
		if (this.isConnectionInValid) return;

		//First...
		User loggedInUser = this.loginClient.loginBasic(USERNAME, PASSWORD);
		TestCase.assertNotNull(loggedInUser);

		String firstServiceTicket = loggedInUser.getServiceTicket();
		TestCase.assertNotNull(firstServiceTicket);
		log.info("1st Token: "+firstServiceTicket);
	}
}
