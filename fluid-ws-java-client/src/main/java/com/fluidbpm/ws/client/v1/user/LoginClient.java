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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.program.api.vo.ws.auth.AuthEncryptedData;
import com.fluidbpm.program.api.vo.ws.auth.AuthRequest;
import com.fluidbpm.program.api.vo.ws.auth.AuthResponse;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.common.io.BaseEncoding;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java Web Service Client for User Login related actions.
 * <p>
 * The API is based on the <i>Kerberos mutual authentication model</i>. <br>
 * See https://en.wikipedia.org/wiki/Kerberos_(protocol).
 * <p>
 * Sequence for login is as follows;
 *
 * <br>
 * <h1>1. Pre Checks;</h1>
 * <br>
 * <p>
 * The API requires the following prerequisites;
 * <br>
 * <br>
 *
 * <ul>
 *     <li>Support for TLS 1.0 and above (For public / private RSA connection).</li>
 *     <li>SHA-256 hashing function (For password and HMAC).</li>
 *     <li>AES-256 (Session keys and encrypted data).</li>
 * </ul>
 * <p>
 * If Java is being used, the Unlimited Strength Jurisdiction Policy Files needs to be used
 * to enable AES-256. See http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 *
 * <br>
 * <h1>2. Authentication Steps</h1>
 * <p>
 *     The steps below explains the process of authenticating against the Fluid-API.
 * <br>
 *
 * <h2>2.1 Authentication Request and Response</h2>
 * <p>
 * 2.1.1. Send a {@code AuthRequest} JSON request {@code POST} to; <br><br>
 *     <b>{@code https://<server:port>/fluid-ws/v1/user/init}</b> <br>
 *         to initiate the authentication. <br>
 * <i>* Note: The lifetime is in seconds.</i><br><br>
 *
 * <b>Example of {@code AuthRequest} request JSON;</b><br>
 * <code>
 *  {<br>
 *           "username" : "johnny",<br>
 *           "lifetime", 1000<br>
 *  }<br>
 * </code>
 * <b>Breakdown of {@code AuthRequest};</b>
 * <br>
 * <ul>
 *     <li><b>{@code username}</b> : The Fluid username.</li>
 *     <li><b>{@code lifetime}</b> : The duration of the Service Ticket in seconds.</li>
 * </ul>
 *
 * <br><br>
 * <p>
 * <p>
 * 2.1.2. If the provided {@code AuthRequest} JSON is correct, the Fluid-API will return a {@code AuthResponse} in JSON format.
 * <br>
 * <br>
 * <br>
 *
 * <b>Example of {@code AuthResponse} response JSON;</b><br>
 *
 * <code>
 *  {<br>
 *      "salt": "jQJVm'T7zhG1d'z)!o!+",<br>
 *      "encryptedDataBase64": "IeWj7pcXhLXf2lEFtRVU",<br>
 *      "encryptedDataHmacBase64": "SdAU3W/VDk5CNCMlZL0k=",<br>
 *      "ivBase64": "cCWtbV+K2J4peTNx/7EZ5w==",<br>
 *      "seedBase64": "BRJyFMvUIm/FZq6VxaIzYC9nYnZCZmWEuBARuUQ5JTw=",<br>
 *      "serviceTicketBase64": "of6torcGONT6kjyNE3+pVutWZ4cz/eTWGsbP"<br>
 *  }<br>
 * </code>
 *
 * <br><br><br>
 *
 * <b>Breakdown of {@code AuthResponse};</b>
 * <br>
 * <ul>
 *     <li><b>{@code salt}</b> : In cryptography, a salt is random data that is used as an additional input to a one-way function that "hashes" a password or passphrase.</li>
 *     <li><b>{@code encryptedDataBase64}</b> : Contains encrypted JSON {@code (roleListing, ticketExpiration and sessionKeyBase64)} in Base64 format.</li>
 *     <li><b>{@code encryptedDataHmacBase64}</b> : HMAC for the {@code encryptedDataBase64} in Base64 format.</li>
 *     <li><b>{@code ivBase64}</b> : Session Initialization Vector in Base64 format.</li>
 *     <li><b>{@code seedBase64}</b> : SEED in Base64 format.</li>
 *     <li><b>{@code serviceTicketBase64}</b> : Service Ticket in Base64 format.</li>
 * </ul>
 *
 * <p>
 *     The process of decrypting {@code encryptedDataBase64}, is as follows;<br>
 *         1. Decode all the Base64 fields to binary.
 *         2. Perform a SHA-256 on the clear password followed by the {@code salt}. <br>Example: {@code byte[] passwordSha256 = sha256(passwordParam.concat(saltParam).getBytes());}
 *         3. Perform a SHA-256 on the <b>step 2</b> result data and {@code seedBase64}. <br>{@code byte[] derivedKey = sha256(ArrayUtils.addAll(passwordSha256, seedParam));}
 *         4. Perform a decrypt, using the <b>step 3</b> result as the key, and provided {@code ivBase64} in binary format.
 *         5. The result of the decrypt must now be used to parse as a JSON object ({@code AuthEncryptedData}). Which will house {@code roleListing}, {@code ticketExpiration} and {@code sessionKeyBase64}.
 * <br><br>
 *
 *
 * <h2>2.2 Issue Service Token</h2>
 * <p>
 * 2.1.1. Send a {@code AppRequestToken} JSON request {@code POST} to; <br><br>
 *     <b>{@code https://<server:port>/fluid-ws/v1/user/issue_token}</b> <br>
 *         to be issued a service ticket.<br><br>
 *
 * <b>Example of {@code AppRequestToken} request JSON;</b><br>
 * <code>
 *  {<br>
 *           "encryptedDataBase64" : "IeWj7pcXhLXf2lEFtRVU",<br>
 *           "encryptedDataHmacBase64", "SdAU3W/V"<br>
 *           "ivBase64", "cCWtbV+K2J4peTNx/7EZ5w=="<br>
 *           "seedBase64", "BRJyFMvUIm/FZq6VxaIzYC9nYnZCZmWEuBARuUQ5JTw="<br>
 *           "serviceTicket", "of6torc"<br>
 *  }<br>
 * </code>
 *
 * <br><br><br>
 *
 * <b>Breakdown of {@code AppRequestToken};</b>
 * <br>
 * <ul>
 *     <li><b>{@code encryptedDataBase64}</b> : Encrypted data in Base64 format. <br>A newly random generated IV will be used. <br>The {@code sessionKeyBase64} from {@code AuthEncryptedData} will be used as a encryption key.</li>
 *     <li><b>{@code encryptedDataHmacBase64}</b> : HMAC for encrypted data. Consists of newly generated SEED <b>(each of the bytes poisoned by XOR 222)</b> and derived key. <br> Example; {@code byte[] derivedKey = sha256(ArrayUtils.addAll(keyParam, poisonedSeed));}.</li>
 *     <li><b>{@code ivBase64}</b> : A newly random generated IV.</li>
 *     <li><b>{@code seedBase64}</b> : A newly random generated SEED.</li>
 *     <li><b>{@code serviceTicket}</b> : The serviceTicket from {@code AuthResponse}.</li>
 * </ul>
 *
 * <br><br>
 * <p>
 * The response from the Issue Token request will return a JSON object with a {@code serviceTicket}.
 * Each of the API Web Services will require the service ticket to perform functions.
 *
 * @author jasonbruwer
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Auth0
 * @see com.fluidbpm.program.api.vo.user.User
 * @since v1.0
 */
public class LoginClient extends ABaseClientWS {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "BASIC";

    /**
     * Constructor which sets the login URL.
     *
     * @param urlParam The login URL.
     */
    public LoginClient(String urlParam) {
        super(urlParam);
    }

    /**
     * Login user using BASIC authentication.
     * Not recommended when using non secure connections.
     * <p>
     * See; https://en.wikipedia.org/wiki/Basic_access_authentication
     *
     * @param usernameParam The username.
     * @param passwordParam The user password.
     * @return Logged in user.
     * @see User
     */
    public User loginBasic(String usernameParam, String passwordParam) {
        if (usernameParam == null || passwordParam == null) return null;

        try {
            List<HeaderNameValue> headNameVal = new ArrayList<>();
            String value = usernameParam.concat(":").concat(passwordParam);
            value = BaseEncoding.base64().encode(value.getBytes());
            HeaderNameValue basicHeader = new HeaderNameValue(AUTHORIZATION, BASIC.concat(" ").concat(value));
            headNameVal.add(basicHeader);

            return new User(this.getJson(
                    false,
                    WS.Path.User.Version1.userBasicAuth(),
                    headNameVal)
            );
        } catch (JSONException jsonException) {
            throw new FluidClientException(
                    jsonException.getMessage(), jsonException,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Performs the necessary login actions against Fluid.
     * <p>
     * Please note that the actual password never travels over the wire / connection.
     *
     * @param usernameParam The users Username.
     * @param passwordParam The users password.
     * @return Session token.
     * @see AppRequestToken
     */
    public AppRequestToken login(String usernameParam, String passwordParam) {

        //Default login is for 9 hours.
        return this.login(usernameParam, passwordParam, TimeUnit.HOURS.toSeconds(9));
    }

    /**
     * Performs the necessary login actions against Fluid.
     * <p>
     * Please note that the actual password never travels over the wire / connection.
     *
     * @param username               The users Username.
     * @param password               The users password.
     * @param sessionLifespanSeconds The requested duration of the session in seconds.
     * @return Session token.
     * @see AppRequestToken
     */
    public AppRequestToken login(
            String username,
            String password,
            Long sessionLifespanSeconds
    ) {
        if (this.isEmpty(username) || this.isEmpty(password)) {
            throw new FluidClientException(
                    "Username and Password required.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        AuthRequest authRequest = new AuthRequest();

        authRequest.setUsername(username);
        authRequest.setLifetime(sessionLifespanSeconds);

        //Init the session...
        //Init the session to get the salt...
        AuthResponse authResponse;
        try {
            authResponse = new AuthResponse(
                    this.postJson(
                            true,
                            authRequest,
                            WS.Path.User.Version1.userInitSession()));
        } catch (JSONException jsonException) {
            //JSON format problem...
            throw new FluidClientException(
                    jsonException.getMessage(), jsonException, FluidClientException.ErrorCode.JSON_PARSING);
        }

        AuthEncryptedData authEncData = this.initializeSession(password, authResponse);

        //Issue the token...
        AppRequestToken appReqToken = this.issueAppRequestToken(
                authResponse.getServiceTicketBase64(),
                username,
                authEncData
        );

        appReqToken.setRoleString(authEncData.getRoleListing());
        appReqToken.setSalt(authResponse.getSalt());

        return appReqToken;
    }

    /**
     * Performs HMAC and encryption to initialize the session.
     *
     * @param passwordParam     The user password in the clear.
     * @param authResponseParam Response of the initial authentication request (handshake).
     * @return Authenticated encrypted data.
     */
    private AuthEncryptedData initializeSession(
            String passwordParam,
            AuthResponse authResponseParam
    ) {
        //IV...
        byte[] ivBytes = UtilGlobal.decodeBase64(authResponseParam.getIvBase64());

        //Seed...
        byte[] seedBytes = UtilGlobal.decodeBase64(authResponseParam.getSeedBase64());

        //Encrypted Data...
        byte[] encryptedData = UtilGlobal.decodeBase64(authResponseParam.getEncryptedDataBase64());

        //HMac from Response...
        byte[] hMacFromResponse = UtilGlobal.decodeBase64(authResponseParam.getEncryptedDataHmacBase64());

        //Local HMac...
        byte[] localGeneratedHMac = AES256Local.generateLocalHMAC(
                encryptedData,
                passwordParam,
                authResponseParam.getSalt(),
                seedBytes
        );

        //Password mismatch...
        if (!Arrays.equals(hMacFromResponse, localGeneratedHMac)) {
            throw new FluidClientException(
                    "Login attempt failure.",
                    FluidClientException.ErrorCode.LOGIN_FAILURE);
        }

        //Decrypted Initialization Data...
        byte[] decryptedEncryptedData = AES256Local.decryptInitPacket(encryptedData,
                passwordParam,
                authResponseParam.getSalt(),
                ivBytes,
                seedBytes
        );

        try {
            return new AuthEncryptedData(new JSONObject(new String(decryptedEncryptedData)));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(), FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Issue a new {@code AppRequestToken} from provided params.
     *
     * @param serviceTicketBase64Param The service ticket from authentication.
     * @param usernameParam            The users username.
     * @param authEncryptDataParam     The encrypted packet.
     * @return Request Token.
     * @see AppRequestToken
     */
    private AppRequestToken issueAppRequestToken(
            String serviceTicketBase64Param,
            String usernameParam,
            AuthEncryptedData authEncryptDataParam
    ) {
        byte[] iv = AES256Local.generateRandom(AES256Local.IV_SIZE_BYTES);
        byte[] seed = AES256Local.generateRandom(AES256Local.SEED_SIZE_BYTES);

        byte[] sessionKey = UtilGlobal.decodeBase64(authEncryptDataParam.getSessionKeyBase64());

        byte[] dataToEncrypt = usernameParam.getBytes();

        byte[] encryptedData = AES256Local.encrypt(sessionKey, dataToEncrypt, iv);

        byte[] encryptedDataHMac = AES256Local.generateLocalHMACForReqToken(encryptedData, sessionKey, seed);

        AppRequestToken requestToServer = new AppRequestToken();

        requestToServer.setEncryptedDataBase64(UtilGlobal.encodeBase64(encryptedData));
        requestToServer.setEncryptedDataHmacBase64(UtilGlobal.encodeBase64(encryptedDataHMac));
        requestToServer.setIvBase64(UtilGlobal.encodeBase64(iv));
        requestToServer.setSeedBase64(UtilGlobal.encodeBase64(seed));
        requestToServer.setServiceTicket(serviceTicketBase64Param);

        try {
            return new AppRequestToken(this.postJson(requestToServer, WS.Path.User.Version1.userIssueToken()));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieve valid {@code User} information based on {@code codeParam} and {@code redirectUrlParam}.
     * The parameters {@code hostParam} and {@code userAgentInfoParam} is optional.
     *
     * @param codeParam          The code generated from Auth0 login.
     * @param redirectUrlParam   The redirect URL post login.
     * @param hostParam          Optional host information.
     * @param userAgentInfoParam Optional user-agent information.
     * @return {@code /user/auth0_user_profile}
     * @see WS.Path.User
     */
    public User loginAuth0UserProfile(
            String codeParam,
            String redirectUrlParam,
            String hostParam,
            String userAgentInfoParam
    ) {
        try {
            return new User(this.getJson(WS.Path.User.Version1.loginAuth0UserProfile(
                    codeParam,
                    redirectUrlParam,
                    hostParam,
                    userAgentInfoParam
            )));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        } catch (UnsupportedEncodingException unsEncExcept) {
            //Encoding not supported...
            throw new FluidClientException(unsEncExcept.getMessage(),
                    unsEncExcept, FluidClientException.ErrorCode.IO_ERROR);
        }
    }
}
