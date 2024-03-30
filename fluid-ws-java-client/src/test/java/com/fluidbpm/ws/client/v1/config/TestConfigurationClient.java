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

import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.program.api.vo.config.ConfigurationListing;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifier;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by jasonbruwer on 16/04/16.
 */
public class TestConfigurationClient extends ABaseLoggedInTestCase {
	private LoginClient loginClient;

	public static final class TestStatics{
		public static final String DateAndTimeFormat = "DateAndTimeFormat";
	}

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
	public void testConfigurationFetchByKey() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		ConfigurationClient configurationClient = new ConfigurationClient(BASE_URL, serviceTicket);

		//1. Fetch...
		Configuration fetchedConfig =
				configurationClient.getConfigurationByKey(TestStatics.DateAndTimeFormat);

		TestCase.assertNotNull("The 'Configuration' needs to be set.", fetchedConfig);
		TestCase.assertNotNull("The 'Id' needs to be set.", fetchedConfig.getId());
		TestCase.assertNotNull("The 'Key' needs to be set.", fetchedConfig.getKey());
		TestCase.assertNotNull("The 'Value' needs to be set.", fetchedConfig.getValue());
	}

	@Test
	public void testConfigurationFetchAll() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		ConfigurationClient configurationClient = new ConfigurationClient(BASE_URL, serviceTicket);

		//1. Fetch...
		ConfigurationListing fetchedConfigs = configurationClient.getAllConfigurations();

		TestCase.assertNotNull("The 'Configurations' needs to be set.", fetchedConfigs);
		TestCase.assertTrue("There must be configurations",fetchedConfigs.getListingCount() > 0);
	}

	@Test
	public void testFetchAllThirdPartyLibTaskIdentifiers() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);
		String serviceTicket = appRequestToken.getServiceTicket();

		ConfigurationClient configurationClient = new ConfigurationClient(BASE_URL, serviceTicket);
		FormContainerClient ccClient = new FormContainerClient(BASE_URL, serviceTicket);

		//1. Fetch...
		try {
			long start = System.currentTimeMillis();
			List<ThirdPartyLibraryTaskIdentifier> fetchedConfigs = configurationClient.getAllThirdPartyTaskIdentifiers();
			long took = (System.currentTimeMillis() - start);
			System.out.println("Tasks took a total of " + took);

			TestCase.assertNotNull("The 'Third Party Libs' needs to be set.", fetchedConfigs);
			TestCase.assertFalse("The listing needs to be more than 0.", fetchedConfigs.isEmpty());

			fetchedConfigs.forEach(taskId -> {
				System.out.println(taskId.toJsonObject());
			});

			fetchedConfigs.stream().filter(itm -> itm.getThirdPartyLibraryTaskType() == ThirdPartyLibraryTaskIdentifier.ThirdPartyLibraryTaskType.CustomWebAction)
					.filter(itm -> itm.getFormDefinitions() != null)
					.forEach(webActionForms -> {
						String firstFormDef = webActionForms.getFormDefinitions().get(0).getFormType();

						Form formToExecOn = new Form(firstFormDef);
						//formToExecOn.setFieldValue("Manufacturer Name", "DaSpread");

						try {
							CustomWebAction executeResult = ccClient.executeCustomWebAction(webActionForms.getTaskIdentifier(), formToExecOn);
							System.out.println("Execute Result: \n"+executeResult.toJsonObject().toString());
						} catch (Exception err) {
							err.printStackTrace();
						}
					});

		} catch (FluidClientException fce) {
			if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
		}
	}

	@Test
	public void testConfigurationCompanyLogoAndSmall() {
		if (this.isConnectionInValid) return;

		ConfigurationClient confClient = new ConfigurationClient(BASE_URL, ADMIN_SERVICE_TICKET);

		Configuration compLogo = confClient.getCompanyLogo();
		TestCase.assertNotNull("The 'Company Logo' needs to be set.", compLogo);

		Configuration compLogoSmall = confClient.getCompanyLogoSmall();
		TestCase.assertNotNull("The 'Company Logo Small' needs to be set.", compLogoSmall);
	}
}
