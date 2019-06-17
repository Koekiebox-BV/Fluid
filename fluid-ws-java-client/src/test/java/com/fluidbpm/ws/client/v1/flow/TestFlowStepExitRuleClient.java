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

package com.fluidbpm.ws.client.v1.flow;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowStepExitRuleClient extends ABaseTestCase {

    private LoginClient loginClient;

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
    public void testFlowStepExitRule_GetNextValidSyntax()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL,serviceTicket);

        //Empty...
        try {
            flowStepRuleClient.getNextValidSyntaxWordsExitRule(null);
            Assert.fail("Expected to fail.");
        }
        //
        catch(FluidClientException prob)
        {

        }

        //S...
        try {
            flowStepRuleClient.getNextValidSyntaxWordsExitRule("S");
            Assert.fail("Expected to fail.");
        }
        //
        catch(FluidClientException prob)
        {

        }

        //SET...
        List<String> nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsExitRule("SET");

        Assert.assertNotNull("Next valid syntax must be set.",nextValidSyntaxWords);
        Assert.assertEquals("Expected number of rules missmatch.",
                4,nextValidSyntaxWords.size());

        boolean isScope = false;
        for (String iter : nextValidSyntaxWords)
        {
            if (iter.equals("FORM."))
            {
                isScope = true;
            }
        }

        Assert.assertTrue("Expected at least FORM.",isScope);

        //FORM.
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsExitRule("SET FORM.");

        Assert.assertTrue("Expected at least 5 fields.",
                nextValidSyntaxWords.size() > 5);

        for (String iter : nextValidSyntaxWords)
        {
            if (!iter.startsWith("FORM."))
            {
                Assert.fail("Did not start with FORM. "+iter);
            }
        }

        //SET FORM.FORM.Email Subject
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsExitRule(
                        "SET FORM.Email Subject");

        Assert.assertTrue("Expected only 1 value.",
                nextValidSyntaxWords.size() == 1);

        Assert.assertTrue("Expected TO.",
                nextValidSyntaxWords.get(0).equals("TO"));

        //SET FORM.FORM.Email Subject TO
        nextValidSyntaxWords =
                flowStepRuleClient.getNextValidSyntaxWordsExitRule(
                        "SET FORM.Email Subject TO");

        Assert.assertTrue("Expected 6 values. Got "+nextValidSyntaxWords.size()+" instead.",
                nextValidSyntaxWords.size() > 4);
    }


    /**
     *
     */
    @Test
    public void testFlowStepExitRule_CRUD()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

    }

    /**
     *
     */
    @Test
    public void testFlowStepExitRule_CompileSucceedAndFail()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();
    }


    /**
     *
     */
    @Test
    public void testFlowStepExitRule_CompileAndRunSucceed()
    {
        if (!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();
    }

}
