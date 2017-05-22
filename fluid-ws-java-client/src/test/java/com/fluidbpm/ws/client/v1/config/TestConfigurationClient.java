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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.program.api.vo.config.ConfigurationListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 16/04/16.
 */
public class TestConfigurationClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String DateAndTimeFormat = "DateAndTimeFormat";
    }

    /**
     *
     */
    @Before
    public void init()
    {
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
    public void testConfigurationFetchByKey()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

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

    /**
     *
     */
    @Test
    public void testConfigurationFetchAll()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        ConfigurationClient configurationClient = new ConfigurationClient(BASE_URL, serviceTicket);

        //1. Fetch...
        ConfigurationListing fetchedConfigs = configurationClient.getAllConfigurations();

        TestCase.assertNotNull("The 'Configurations' needs to be set.", fetchedConfigs);
        TestCase.assertTrue("There must be configurations",fetchedConfigs.getListingCount() > 0);
    }
}
