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

package com.fluidbpm.program.api.vo.auth0;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An Auth0 Access Token used by Fluid.
 *
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see NormalizedUserProfile
 * @see AccessTokenRequest
 * @see ABaseFluidJSONObject
 */
public class AccessToken extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String accessToken;

	@Getter
	@Setter
	private String idToken;

	@Getter
	@Setter
	private String tokenType;

	/**
	 * The JSON mapping for the {@code AccessToken} object.
	 */
	public static class JSONMapping
	{
		public static final String ACCESS_TOKEN = "access_token";
		public static final String ID_TOKEN = "id_token";
		public static final String TOKEN_TYPE = "token_type";
	}

	/**
	 * Default constructor.
	 */
	public AccessToken() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public AccessToken(JSONObject jsonObjectParam){
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		//Access Token...
		if (!this.jsonObject.isNull(JSONMapping.ACCESS_TOKEN)) {
			this.setAccessToken(this.jsonObject.getString(JSONMapping.ACCESS_TOKEN));
		}

		//Id Token...
		if (!this.jsonObject.isNull(JSONMapping.ID_TOKEN)) {
			this.setIdToken(this.jsonObject.getString(JSONMapping.ID_TOKEN));
		}

		//Token Type...
		if (!this.jsonObject.isNull(JSONMapping.TOKEN_TYPE)) {
			this.setTokenType(this.jsonObject.getString(JSONMapping.TOKEN_TYPE));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code AccessToken}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Access Token...
		if (this.getAccessToken() != null) {
			returnVal.put(JSONMapping.ACCESS_TOKEN,this.getAccessToken());
		}

		//Id Token...
		if (this.getIdToken() != null) {
			returnVal.put(JSONMapping.ID_TOKEN,this.getIdToken());
		}

		//Token Type...
		if (this.getTokenType() != null) {
			returnVal.put(JSONMapping.TOKEN_TYPE,this.getTokenType());
		}

		return returnVal;
	}
}
