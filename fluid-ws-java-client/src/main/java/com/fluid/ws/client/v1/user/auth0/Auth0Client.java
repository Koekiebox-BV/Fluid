package com.fluid.ws.client.v1.user.auth0;

import java.io.UnsupportedEncodingException;

import com.fluid.program.api.vo.auth0.AccessToken;
import com.fluid.program.api.vo.auth0.AccessTokenRequest;
import com.fluid.program.api.vo.auth0.NormalizedUserProfile;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Created by jasonbruwer on 2015/11/06.
 */
public class Auth0Client extends ABaseClientWS {

    /**
     *
     */
    public static final class FormElements
    {
        public static final String CLIENT_ID = "client_id";
        public static final String REDIRECT_URI = "redirect_uri";
        public static final String CLIENT_SECRET = "client_secret";
        public static final String CODE = "code";
        public static final String GRANT_TYPE = "grant_type";

        /**
         *
         */
        public static final class Value
        {
            public static final String AUTHORIZATION_CODE = "authorization_code";
        }
    }

    /**
     *
     */
    public Auth0Client() {
        super();
    }

    /**
     *
     * @param urlToAuth0EndpointParam
     */
    public Auth0Client(String urlToAuth0EndpointParam) {
        super(urlToAuth0EndpointParam);
    }

    /**
     *
     * @param clientIdParam
     * @param clientSecretParam
     * @param codeParam
     * @param redirectUrlParam
     * @return
     */
    public AccessToken getAccessToken(
            String clientIdParam,
            String clientSecretParam,
            String codeParam,
            String redirectUrlParam)
    {

        if(clientIdParam == null || clientIdParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Client Id must be provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }


        if(clientSecretParam == null || clientSecretParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Client Secret must be provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        if(codeParam == null || codeParam.trim().isEmpty())
        {
            throw new FluidClientException(
                    "Code must be provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        AccessTokenRequest tokenRequest = new AccessTokenRequest();

        tokenRequest.setClientId(clientIdParam);
        tokenRequest.setClientSecret(clientSecretParam);
        tokenRequest.setGrantType(FormElements.Value.AUTHORIZATION_CODE);
        tokenRequest.setCode(codeParam);
        tokenRequest.setRedirectUri(redirectUrlParam);

        return new AccessToken(this.postJson(
                true, tokenRequest, WS.Path.Auth0.Version1.userToken()));
    }

    /**
     *
     * @param accessTokenParam
     * @return
     */
    public NormalizedUserProfile getUserProfileInfo(AccessToken accessTokenParam)
    {
        if(accessTokenParam == null || (accessTokenParam.getAccessToken() == null ||
                accessTokenParam.getAccessToken().trim().isEmpty()))
        {
            throw new FluidClientException(
                    "Code must be provided.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        try {
            return new NormalizedUserProfile(
                    this.getJson(true, WS.Path.Auth0.Version1.userInfo(
                    accessTokenParam.getAccessToken())));
        }
        //
        catch (UnsupportedEncodingException e) {

            throw new FluidClientException(
                    "Unable to Encode (Not Supported). "+ e.getMessage(),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
    }
}
