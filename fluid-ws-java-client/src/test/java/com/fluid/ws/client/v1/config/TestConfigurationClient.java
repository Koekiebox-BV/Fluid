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

package com.fluid.ws.client.v1.config;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.config.Configuration;
import com.fluid.program.api.vo.config.ConfigurationListing;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

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

        this.loginClient = new LoginClient();
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

        ConfigurationClient configurationClient = new ConfigurationClient(serviceTicket);

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

        ConfigurationClient configurationClient = new ConfigurationClient(serviceTicket);

        //1. Fetch...
        ConfigurationListing fetchedConfigs = configurationClient.getAllConfigurations();

        TestCase.assertNotNull("The 'Configurations' needs to be set.", fetchedConfigs);
        TestCase.assertTrue("There must be configurations",fetchedConfigs.getListingCount() > 0);
    }
}
