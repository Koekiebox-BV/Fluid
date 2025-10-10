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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestGlobalFieldClient extends ABaseLoggedInTestCase {

    private LoginClient loginClient;
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
     * Fetching of all global fields.
     */
    @Test
    public void testFetchAllGlobalFields() {
        if (this.isConnectionInValid) return;

        GlobalFieldClient globalFieldClient = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
        List<Field> allGlobals = globalFieldClient.getAllGlobalFields();

        TestCase.assertNotNull("Fields need to be set.", allGlobals);
    }

    /**
     * Fetching of all global fields.
     */
    @Test
    public void testFetchByName() {
        if (this.isConnectionInValid) return;

        GlobalFieldClient globalFieldClient = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);

        Field nrOfUsers = globalFieldClient.getFieldValueByName("Number of Users");
        TestCase.assertNotNull("Field need to be set.", nrOfUsers);
    }
}
