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

package com.fluidbpm.ws.client.v1.user.auth0;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.auth0.AccessToken;
import com.fluidbpm.program.api.vo.auth0.AccessTokenRequest;
import com.fluidbpm.program.api.vo.auth0.NormalizedUserProfile;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for Auth0 related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Auth0
 */
public class Auth0Client extends ABaseClientWS {

	public static final String AUTHORIZATION_CODE = "authorization_code";

	/**
	 * Constructs Auth0 Client to a provided endpoint.
	 *
	 * @param urlToAuth0EndpointParam The endpoint to connect to.
	 */
	public Auth0Client(String urlToAuth0EndpointParam) {
		super(urlToAuth0EndpointParam);
	}

	/**
	 * Gets a Access Token from Auth0.
	 *
	 * @param clientIdParam The client id.
	 * @param clientSecretParam The client secret.
	 * @param codeParam The code.
	 * @param redirectUrlParam The redirect URL after successful authentication.
	 *
	 * @return Access Token.
	 *
	 * @see AccessToken
	 */
	public AccessToken getAccessToken(
			String clientIdParam,
			String clientSecretParam,
			String codeParam,
			String redirectUrlParam
	) {
		if (clientIdParam == null || clientIdParam.trim().isEmpty()) {
			throw new FluidClientException(
					"Client Id must be provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}
		
		if (clientSecretParam == null || clientSecretParam.trim().isEmpty()) {
			throw new FluidClientException(
					"Client Secret must be provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (codeParam == null || codeParam.trim().isEmpty()) {
			throw new FluidClientException(
					"Code must be provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		AccessTokenRequest tokenRequest = new AccessTokenRequest();

		tokenRequest.setClientId(clientIdParam);
		tokenRequest.setClientSecret(clientSecretParam);
		tokenRequest.setGrantType(AUTHORIZATION_CODE);
		tokenRequest.setCode(codeParam);
		tokenRequest.setRedirectUri(redirectUrlParam);

		return new AccessToken(this.postJson(
				false,
				tokenRequest, WS.Path.Auth0.Version1.userToken()));
	}

	/**
	 * Gets Auth0 Normalized User Profile info.
	 *
	 * @param accessTokenParam The access token used to get
	 *                         user profile info from.
	 * @return User Profile.
	 *
	 * @see NormalizedUserProfile
	 * @see AccessToken
	 */
	public NormalizedUserProfile getUserProfileInfo(AccessToken accessTokenParam) {
		if (accessTokenParam == null || (accessTokenParam.getAccessToken() == null ||
				accessTokenParam.getAccessToken().trim().isEmpty())) {
			throw new FluidClientException(
					"Code must be provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		try {
			String accessToken = accessTokenParam.getAccessToken();

			List<HeaderNameValue> headerListing = new ArrayList<HeaderNameValue>();

			headerListing.add(new HeaderNameValue(
					NormalizedUserProfile.HeaderMapping.AUTHORIZATION,
					"Bearer "+accessToken));

			return new NormalizedUserProfile(
					this.getJson(true, WS.Path.Auth0.Version1.userInfo(),headerListing));
		} catch (UnsupportedEncodingException e) {

			throw new FluidClientException(
					"Unable to Encode (Not Supported). "+ e.getMessage(),
					FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
		}
	}
}
