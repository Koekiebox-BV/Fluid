package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.auth0.AccessToken;
import com.fluidbpm.program.api.vo.ws.auth.*;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.user.*;
import com.fluidbpm.ws.client.v1.user.auth0.Auth0Client;
import com.google.gson.*;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

public class TestAuthenticationContracts {
    private static JsonObject json(String text) { return JsonParser.parseString(text).getAsJsonObject(); }

    @Test(timeout = 30000) public void basicAuthenticationSendsHeaderAndDecodesUser() throws Exception {
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            assertEquals("GET", req.method); assertEquals("/v1/user/auth_basic", req.path);
            assertEquals("BASIC " + Base64.getEncoder().encodeToString("alice:secret".getBytes(StandardCharsets.UTF_8)), req.authorization);
            return json("{\"id\":17,\"username\":\"alice\"}");
        }); LoginClient client = new LoginClient(http.endpoint())) {
            assertEquals("alice", client.loginBasic("alice", "secret").getUsername());
            assertNull(client.loginBasic(null, "secret"));
            assertNull(client.loginBasic("alice", null));
            assertEquals(1, http.requests.size());
        }
    }

    @Test(timeout = 30000) public void auth0TokenAndProfileContracts() throws Exception {
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            if (req.path.endsWith("/oauth/token")) {
                assertEquals("POST", req.method);
                assertEquals("client", req.body.get("client_id").getAsString());
                assertEquals("secret", req.body.get("client_secret").getAsString());
                assertEquals("code", req.body.get("code").getAsString());
                assertEquals("authorization_code", req.body.get("grant_type").getAsString());
                assertEquals("https://example.invalid/callback", req.body.get("redirect_uri").getAsString());
                return json("{\"access_token\":\"token\",\"token_type\":\"Bearer\"}");
            }
            assertTrue(req.path.endsWith("/userinfo")); assertEquals("GET", req.method);
            assertEquals("Bearer token", req.authorization);
            return json("{\"email\":\"alice@example.invalid\"}");
        }); Auth0Client client = new Auth0Client(http.endpoint())) {
            AccessToken token = client.getAccessToken("client", "secret", "code", "https://example.invalid/callback");
            assertEquals("token", token.getAccessToken());
            assertEquals("alice@example.invalid", client.getUserProfileInfo(token).getEmail());
            assertEquals(2, http.requests.size());
        }
    }

    @Test(timeout = 30000) public void fluidAuth0ProfileEncodesRedirect() throws Exception {
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            assertEquals("/v1/user/auth0_user_profile", req.path); assertEquals("GET", req.method);
            assertTrue(req.query.contains("https%3A%2F%2Fexample.invalid%2Fcallback"));
            return json("{\"id\":17}");
        }); LoginClient client = new LoginClient(http.endpoint())) {
            assertEquals(Long.valueOf(17), client.loginAuth0UserProfile("code", "https://example.invalid/callback", "host", "agent").getId());
        }
    }

    @Test(timeout = 30000) public void encryptedLoginOverloadsExchangeValidChallengeAndToken() throws Exception {
        exerciseLogin(false); exerciseLogin(true);
    }
    private void exerciseLogin(boolean explicitLifetime) throws Exception {
        byte[] sessionKey = new byte[32]; Arrays.fill(sessionKey, (byte) 7);
        String salt = "fixture-salt";
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            if (req.path.contains("/test")) return new JsonObject();
            if (req.path.equals("/v1/user/init")) {
                assertEquals("POST", req.method);
                assertEquals("alice", req.body.get("username").getAsString());
                assertEquals(explicitLifetime ? 60L : 32400L, req.body.get("lifetime").getAsLong());
                assertFalse(req.body.toString().contains("secret"));
                byte[] iv = new byte[16], seed = new byte[32];
                byte[] derived = AES256Local.sha256(UtilGlobal.addAll(AES256Local.sha256(("secret" + salt).getBytes(StandardCharsets.UTF_8)), seed));
                AuthEncryptedData packet = new AuthEncryptedData();
                packet.setSessionKeyBase64(UtilGlobal.encodeBase64(sessionKey));
                packet.setRoleListing("fixture-role");
                byte[] encrypted = AES256Local.encrypt(derived, packet.toJsonObject().toString().getBytes(StandardCharsets.UTF_8), iv);
                AuthResponse response = new AuthResponse();
                response.setSalt(salt); response.setIvBase64(UtilGlobal.encodeBase64(iv)); response.setSeedBase64(UtilGlobal.encodeBase64(seed));
                response.setEncryptedDataBase64(UtilGlobal.encodeBase64(encrypted));
                response.setEncryptedDataHmacBase64(UtilGlobal.encodeBase64(AES256Local.generateLocalHMAC(encrypted, "secret", salt, seed)));
                response.setServiceTicketBase64("challenge-ticket");
                return response.toJsonObject();
            }
            assertEquals("/v1/user/issue_token", req.path); assertEquals("POST", req.method);
            AppRequestToken request = new AppRequestToken(req.body);
            assertEquals("challenge-ticket", request.getServiceTicket());
            byte[] encrypted = UtilGlobal.decodeBase64(request.getEncryptedDataBase64());
            assertEquals("alice", new String(AES256Local.decrypt(sessionKey, encrypted, UtilGlobal.decodeBase64(request.getIvBase64())), StandardCharsets.UTF_8));
            assertArrayEquals(AES256Local.generateLocalHMACForReqToken(encrypted, sessionKey, UtilGlobal.decodeBase64(request.getSeedBase64())), UtilGlobal.decodeBase64(request.getEncryptedDataHmacBase64()));
            return json("{\"serviceTicket\":\"issued-ticket\"}");
        }); LoginClient client = new LoginClient(http.endpoint())) {
            AppRequestToken token = explicitLifetime ? client.login("alice", "secret", 60L) : client.login("alice", "secret");
            assertEquals("issued-ticket", token.getServiceTicket()); assertEquals(salt, token.getSalt());
            assertEquals("fixture-role", token.getRoleString());
            assertEquals(3, http.requests.size());
        }
    }
}
