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
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserFetchByEmailClient extends ABaseLoggedInTestCase {

	private LoginClient loginClient;
	private UserClient userClient;

	private User createdUser = null;

	private String serviceTicket = null;

	/**
	 *
	 */
	public static final class TestStatics
	{
		public static final class Create
		{
			public static final String USERNAME = "junitTestingUser";
			public static final String PASSWORD = "password";

			public static final String EMAIL = "patel@kapper.com";
		}

		public static final class Update
		{
			public static final String USERNAME = "junitTestingUserUpdated";
		}
	}

	/**
	 *
	 */
	@Before
	public void init() {

		this.serviceTicket = null;

		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

		this.loginClient = new LoginClient(BASE_URL);
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		this.serviceTicket = appRequestToken.getServiceTicket();
		this.userClient = new UserClient(BASE_URL, this.serviceTicket);

		try
		{
			this.createdUser = this.userClient.getUserWhereUsername(
					TestStatics.Create.USERNAME);

			if (this.createdUser != null)
			{
				this.userClient.deleteUser(
						this.createdUser, true);
			}
		}
		catch (FluidClientException fce)
		{}

		//Create the user...
		User userToCreate = new User();
		userToCreate.setUsername(TestStatics.Create.USERNAME);
		userToCreate.setPasswordClear(TestStatics.Create.PASSWORD);

		List<String> emails = new ArrayList();
		emails.add(TestStatics.Create.EMAIL);
		userToCreate.setEmailAddresses(emails);

		this.createdUser = this.userClient.createUser(userToCreate);
	}

	/**
	 *
	 */
	@Test
	public void testFetchUserByEmailWhereNotValidated() {
		if (this.isConnectionInValid) return;

		User userByEmail = null;
		try {
			userByEmail = this.userClient.getUserWhereEmail(TestStatics.Create.EMAIL);
		} catch (FluidClientException fce) {
			if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) {
				TestCase.fail(fce.getMessage());
			}
		}

		TestCase.assertNull("Cannot have user by that Email. Confirmed.", userByEmail);
	}


	/**
	 *
	 */
	@Test
	public void testFetchUserByEmail() {
		if (this.isConnectionInValid) return;

		/*User userByEmail = this.userClient.getUserWhereEmail(
				TestStatics.Create.EMAIL);

		TestCase.assertNotNull(
				"No user by email created.",
				userByEmail);
				*/
	}

	/**
	 *
	 */
	@After
	public void destroy()
	{
		if (this.loginClient != null)
		{
			this.loginClient.closeAndClean();
		}

		//Delete the instances...
		if (this.createdUser != null &&
				this.createdUser.getId() != null)
		{
			if (this.userClient != null)
			{
				this.userClient.deleteUser(this.createdUser,true);
			}
		}

		if (this.userClient != null)
		{
			this.userClient.closeAndClean();
		}
	}
}
