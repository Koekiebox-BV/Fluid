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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 18/03/30.
 */
public class TestPersonalInventoryClient extends ABaseTestCase {

    private LoginClient loginClient;

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
    public void testCreatePersonalInventory() {
        if (!this.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        PersonalInventoryClient personalInventoryClient = new
                PersonalInventoryClient(BASE_URL, serviceTicket);

        try
        {
            List<FluidItem> persInvItems =
                    personalInventoryClient.getPersonalInventoryItems();

            for (FluidItem itm : persInvItems)
            {
                System.out.println(
                        "--> "+itm.getForm().getTitle()+" <-- : "+
                                itm.getForm().getState());
            }
        }
        catch (FluidClientException clientExcept)
        {
            if (clientExcept.getErrorCode() !=
                    FluidClientException.ErrorCode.NO_RESULT)
            {
                throw clientExcept;
            }
        }
    }
}
