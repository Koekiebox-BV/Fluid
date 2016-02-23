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
package com.fluid.ws.client.v1.user;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.user.User;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.program.api.vo.ws.auth.AuthEncryptedData;
import com.fluid.program.api.vo.ws.auth.AuthRequest;
import com.fluid.program.api.vo.ws.auth.AuthResponse;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.google.common.io.BaseEncoding;

/**
 * Java Web Service Client for User Login related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.Auth0
 * @see User
 */
public class LoginClient extends ABaseClientWS {


    /**
     * Default constructor.
     */
    public LoginClient() {
        super();
    }

    /**
     * Constructor which sets the login URL.
     *
     * @param urlParam The login URL.
     */
    public LoginClient(String urlParam) {
        super(urlParam);
    }

    /**
     * Performs the necessary login actions against Fluid.
     *
     * Please note that the actual password never travels over the wire / connection.
     *
     * @param usernameParam The users Username.
     * @param passwordParam The users password.
     * @return Session token.
     *
     * @see AppRequestToken
     */
    public AppRequestToken login(
            String usernameParam, String passwordParam) {

        //Default login is for 9 hours.
        return this.login(usernameParam, passwordParam, TimeUnit.HOURS.toSeconds(9));
    }

    /**
     * Performs the necessary login actions against Fluid.
     *
     * Please note that the actual password never travels over the wire / connection.
     *
     * @param usernameParam The users Username.
     * @param passwordParam The users password.
     * @param sessionLifespanSecondsParam The requested duration of the session in seconds.
     * @return Session token.
     *
     * @see AppRequestToken
     */
    public AppRequestToken login(
            String usernameParam, String passwordParam, Long sessionLifespanSecondsParam) {
        if (this.isEmpty(usernameParam) || this.isEmpty(passwordParam)) {
            throw new FluidClientException(
                    "Username and Password required.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        AuthRequest authRequest = new AuthRequest();

        authRequest.setUsername(usernameParam);
        authRequest.setLifetime(sessionLifespanSecondsParam);

        //Init the session...
        //Init the session to get the salt...
        AuthResponse authResponse;
        try {
            authResponse = new AuthResponse(
                    this.postJson(authRequest, WS.Path.User.Version1.userInitSession()));
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(
                    e.getMessage(),e,FluidClientException.ErrorCode.JSON_PARSING);
        }

        AuthEncryptedData authEncData =
                this.initializeSession(passwordParam, authResponse);

        //Issue the token...
        AppRequestToken appReqToken = this.issueAppRequestToken(
                authResponse.getServiceTicketBase64(),
                usernameParam, authEncData);

        appReqToken.setRoleString(authEncData.getRoleListing());
        appReqToken.setSalt(authResponse.getSalt());

        return appReqToken;
    }

    /**
     * Performs HMAC and encryption to initialize the session.
     *
     * @param passwordParam The user password.
     * @param authResponseParam Response of the initial authentication request (handshake).
     * @return Authenticated encrypted data.
     */
    private AuthEncryptedData initializeSession(
            String passwordParam,
            AuthResponse authResponseParam)
    {
        //IV...
        byte[] ivBytes = BaseEncoding.base64().decode(authResponseParam.getIvBase64());

        //Seed...
        byte[] seedBytes = BaseEncoding.base64().decode(authResponseParam.getSeedBase64());

        //Encrypted Data...
        byte[] encryptedData = BaseEncoding.base64().decode(authResponseParam.getEncryptedDataBase64());

        //HMac from Response...
        byte[] hMacFromResponse = BaseEncoding.base64().decode(authResponseParam.getEncryptedDataHmacBase64());

        //Local HMac...
        byte[] localGeneratedHMac = AES256Local.generateLocalHMAC(
                encryptedData, passwordParam, authResponseParam.getSalt(), seedBytes);

        if (!Arrays.equals(hMacFromResponse, localGeneratedHMac)) {
            throw new FluidClientException(
                    "Login attempt failure.",
                    FluidClientException.ErrorCode.LOGIN_FAILURE);
        }

        //Decrypted Initialization Data...
        byte[] decryptedEncryptedData =
                AES256Local.decryptInitPacket(encryptedData, passwordParam, authResponseParam.getSalt(), ivBytes, seedBytes);

        try
        {
            JSONObject jsonObj = new JSONObject(new String(decryptedEncryptedData));

            return new AuthEncryptedData(jsonObj);
        }
        catch (JSONException jsonExcept)
        {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Issue a new {@code AppRequestToken} from provided params.
     *
     * @param serviceTicketBase64Param The service ticket from authentication.
     * @param usernameParam The users username.
     * @param authEncryptDataParam The encrypted packet.
     * @return Request Token.
     *
     * @see AppRequestToken
     */
    private AppRequestToken issueAppRequestToken(
            String serviceTicketBase64Param,
            String usernameParam,
            AuthEncryptedData authEncryptDataParam)
    {
        byte[] iv = AES256Local.generateRandom(AES256Local.IV_SIZE_BYTES);
        byte[] seed = AES256Local.generateRandom(AES256Local.SEED_SIZE_BYTES);

        byte[] sessionKey =
                BaseEncoding.base64().decode(authEncryptDataParam.getSessionKeyBase64());

        byte[] dataToEncrypt = usernameParam.getBytes();

        byte[] encryptedData = AES256Local.encrypt(
                sessionKey,
                dataToEncrypt,
                iv);

        byte[] encryptedDataHMac =
                AES256Local.generateLocalHMACForReqToken(encryptedData, sessionKey, seed);

        AppRequestToken requestToServer = new AppRequestToken();

        requestToServer.setEncryptedDataBase64(BaseEncoding.base64().encode(encryptedData));
        requestToServer.setIvBase64(BaseEncoding.base64().encode(iv));
        requestToServer.setSeedBase64(BaseEncoding.base64().encode(seed));
        requestToServer.setEncryptedDataHmacBase64(BaseEncoding.base64().encode(encryptedDataHMac));
        requestToServer.setServiceTicket(serviceTicketBase64Param);

        try {
            return new AppRequestToken(
                    this.postJson(requestToServer, WS.Path.User.Version1.userIssueToken()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
