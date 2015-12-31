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

import com.fluid.program.api.vo.ws.WS;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.program.api.vo.ws.auth.AuthEncryptedData;
import com.fluid.program.api.vo.ws.auth.AuthRequest;
import com.fluid.program.api.vo.ws.auth.AuthResponse;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.google.common.io.BaseEncoding;

/**
 * Created by jasonbruwer on 14/12/21.
 */
public class LoginClient extends ABaseClientWS {


    /**
     *
     */
    public LoginClient() {
        super();
    }

    /**
     * @param urlParam
     */
    public LoginClient(String urlParam) {
        super(urlParam);
    }

    /**
     * @param usernameParam
     * @param passwordParam
     * @return
     */
    public AppRequestToken login(
            String usernameParam, String passwordParam) {

        //Default login is for 9 hours.
        return this.login(usernameParam, passwordParam, TimeUnit.HOURS.toSeconds(9));
    }

    /**
     * @param usernameParam
     * @param passwordParam
     * @param sessionLifespanSecondsParam
     * @return
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
        AuthResponse authResponse = null;
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
     *
     * @param passwordParam
     * @param authResponseParam
     * @return
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
     *
     * @param serviceTicketBase64Param
     * @param usernameParam
     * @param authEncryptDataParam
     * @return
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
