package com.fluid.ws.client.v1.user;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.User;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseTestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    @Before
    public void init() {
        //this.loginClient = new LoginClient("http://fluid.sahousingclub.co.za/fluid-ws/");
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @Test
    public void testGetUserInfo() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        //AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserClient userClient = new UserClient(serviceTicket);

        User loggedInUser =
                userClient.getUserInformationWhereUsername("admin");

        TestCase.assertNotNull(loggedInUser);

    }
}
