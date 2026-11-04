/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2027] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Exercises real client HTTP serialization without a deployed Fluid server or SMTP.
 * */
public class TestEmailLoginClient {
    private static final String EMAIL = "alice+login@example.invalid";
    private static final String TOKEN = "abcdefghijklmnopqrstuvwxyz0123456789ABCDE-_";

    @Test(timeout = 30000)
    public void requestSendsEmailAsUsernameWithoutAuthentication() throws Exception {
        exercise(false, false);
    }

    @Test(timeout = 30000)
    public void redeemSendsTokenInBodyAndDecodesUserAndTicket() throws Exception {
        exercise(true, false);
    }

    @Test(timeout = 30000)
    public void requestPropagatesApiError() throws Exception {
        exercise(false, true);
    }

    @Test(timeout = 30000)
    public void redeemPropagatesRejectedTokenError() throws Exception {
        exercise(true, true);
    }

    private void exercise(boolean redeem, boolean reject) throws Exception {
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            if (req.path.startsWith("/v1/test")) return new JsonObject();
            assertEquals("POST", req.method);
            assertEquals(redeem ? "/v1/user/redeem_login_url" : "/v1/user/request_login_url", req.path);
            assertNull("Credentials must not appear in the query string", req.query);
            assertNull("Email login does not require authorization", req.authorization);
            assertFalse(req.body.has("serviceTicket"));
            assertEquals(redeem ? TOKEN : EMAIL,
                    req.body.get(redeem ? "passwordClear" : "username").getAsString());
            assertFalse(req.body.has(redeem ? "username" : "passwordClear"));
            if (reject) return JsonParser.parseString(
                    "{\"errorCode\":10020,\"errorMessage\":\"Email login rejected\"}").getAsJsonObject();
            return JsonParser.parseString(redeem
                    ? "{\"id\":17,\"username\":\"alice\",\"email\":\"alice+login@example.invalid\",\"serviceTicket\":\"email-ticket\"}"
                    : "{\"message\":\"If this account is eligible, a login link will be emailed.\"}").getAsJsonObject();
        }); LoginClient client = new LoginClient(http.endpoint())) {
            try {
                if (redeem) {
                    User user = client.redeemLoginUrl(TOKEN);
                    assertEquals(Long.valueOf(17), user.getId());
                    assertEquals("alice", user.getUsername());
                    assertEquals("email-ticket", user.getServiceTicket());
                } else {
                    client.requestLoginUrl(EMAIL);
                }
                if (reject) fail("Expected the API error to propagate");
            } catch (FluidClientException error) {
                if (!reject) throw error;
                assertEquals(FluidClientException.ErrorCode.LOGIN_FAILURE, error.getErrorCode());
                assertEquals("Email login rejected", error.getMessage());
            }
            assertEquals("One connection check and one login request", 2, http.requests.size());
        }
    }
}
