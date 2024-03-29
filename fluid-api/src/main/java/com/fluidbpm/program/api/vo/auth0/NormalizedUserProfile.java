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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.user.User;

/**
 * Fluid consolidation of a workflow.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see User
 */
public class NormalizedUserProfile extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private String userId;
	private String name;
	private String email;
	private boolean emailVerified;
	private String nickname;
	private String picture;
	private String givenName;
	private String familyName;
	private String locale;

	private List<Identity> identities;

	/**
	 * The JSON mapping for the {@code NormalizedUserProfile} object.
	 */
	public static class JSONMapping
	{
		public static final String USER_ID = "user_id";
		public static final String NAME = "name";
		public static final String EMAIL = "email";
		public static final String EMAIL_VERIFIED = "email_verified";
		public static final String NICKNAME = "nickname";
		public static final String PICTURE = "picture";
		public static final String GIVEN_NAME = "given_name";
		public static final String FAMILY_NAME = "family_name";


		public static final String IDENTITIES = "identities";//Array...
		//Done with identities...

		public static final String LOCALE = "locale";
	}

	/**
	 * The Header mapping for the {@code NormalizedUserProfile} object.
	 */
	public static class HeaderMapping
	{
		public static final String AUTHORIZATION = "Authorization";
	}

	/**
	 * Identity related value object.
	 *
	 * @see ABaseFluidJSONObject
	 */
	public static class Identity extends ABaseFluidJSONObject
	{
		private String accessToken;
		private String provider;
		private String userId;
		private String connection;
		private boolean isSocial;

		/**
		 * The JSON mapping for the {@code Identity} object.
		 */
		public static class JSONMapping
		{
			public static final String USER_ID = "user_id";
			public static final String ACCESS_TOKEN = "access_token";
			public static final String PROVIDER = "provider";
			public static final String CONNECTION = "connection";
			public static final String IS_SOCIAL = "isSocial";
		}

		/**
		 * Default constructor.
		 */
		public Identity() {
			super();
		}

		/**
		 * Populates local variables with {@code jsonObjectParam}.
		 *
		 * @param jsonObjectParam The JSON Object.
		 */
		public Identity(JSONObject jsonObjectParam) {
			super(jsonObjectParam);

			if (this.jsonObject == null)
			{
				return;
			}

			//Access Token...
			if (!this.jsonObject.isNull(JSONMapping.ACCESS_TOKEN)) {
				this.setAccessToken(this.jsonObject.getString(
						JSONMapping.ACCESS_TOKEN));
			}

			//Provider...
			if (!this.jsonObject.isNull(JSONMapping.PROVIDER)) {
				this.setProvider(this.jsonObject.getString(
						JSONMapping.PROVIDER));
			}

			//User Id...
			if (!this.jsonObject.isNull(NormalizedUserProfile.JSONMapping.USER_ID)) {
				this.setUserId(this.jsonObject.getString(
						JSONMapping.USER_ID));
			}

			//Connection...
			if (!this.jsonObject.isNull(JSONMapping.CONNECTION)) {
				this.setConnection(this.jsonObject.getString(
						JSONMapping.CONNECTION));
			}

			//Is Social...
			if (!this.jsonObject.isNull(JSONMapping.IS_SOCIAL)) {
				this.setSocial(this.jsonObject.getBoolean(
						JSONMapping.IS_SOCIAL));
			}
		}

		/**
		 * Gets the Access Token.
		 *
		 * @return Access Token.
		 */
		public String getAccessToken() {
			return this.accessToken;
		}

		/**
		 * Sets the Access Token.
		 *
		 * @param accessTokenParam Access Token.
		 */
		public void setAccessToken(String accessTokenParam) {
			this.accessToken = accessTokenParam;
		}

		/**
		 * Gets the provider.
		 *
		 * @return Authentication provider.
		 */
		public String getProvider() {
			return this.provider;
		}

		/**
		 * Sets the provider.
		 *
		 * @param providerParam Authentication provider.
		 */
		public void setProvider(String providerParam) {
			this.provider = providerParam;
		}

		/**
		 * Gets the User Unique Id.
		 *
		 * @return User Id.
		 */
		public String getUserId() {
			return this.userId;
		}

		/**
		 * Sets the User Unique Id.
		 *
		 * @param userIdParam User Id.
		 */
		public void setUserId(String userIdParam) {
			this.userId = userIdParam;
		}

		/**
		 * Gets the Connection.
		 *
		 * @return The Connection.
		 */
		public String getConnection() {
			return this.connection;
		}

		/**
		 * Sets the Connection.
		 *
		 * @param connectionParam The Connection.
		 */
		public void setConnection(String connectionParam) {
			this.connection = connectionParam;
		}

		/**
		 * Is the authenticated server a social one.
		 *
		 * @return Whether the connection is a social network connection.
		 */
		public boolean isSocial() {
			return this.isSocial;
		}

		/**
		 * Sets whether the connection is social.
		 *
		 * @param socialParam Whether the connection is sociall
		 */
		public void setSocial(boolean socialParam) {
			this.isSocial = socialParam;
		}

		/**
		 * Conversion to {@code JSONObject} from Java Object.
		 *
		 * @return {@code JSONObject} representation of {@code Identity}
		 * @throws JSONException If there is a problem with the JSON Body.
		 *
		 * @see ABaseFluidJSONObject#toJsonObject()
		 */
		@Override
		public JSONObject toJsonObject() throws JSONException {

			JSONObject returnVal = super.toJsonObject();

			//Access Token...
			if (this.getAccessToken() != null)
			{
				returnVal.put(JSONMapping.ACCESS_TOKEN, this.getAccessToken());
			}

			//Provider...
			if (this.getProvider() != null)
			{
				returnVal.put(JSONMapping.PROVIDER, this.getProvider());
			}

			//User Id...
			if (this.getUserId() != null)
			{
				returnVal.put(JSONMapping.USER_ID, this.getUserId());
			}

			//Connection...
			if (this.getConnection() != null)
			{
				returnVal.put(JSONMapping.CONNECTION, this.getConnection());
			}

			//Is Social...
			returnVal.put(JSONMapping.IS_SOCIAL, this.isSocial());

			return returnVal;
		}
	}

	/**
	 * Default constructor.
	 */
	public NormalizedUserProfile() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public NormalizedUserProfile(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null)
		{
			return;
		}

		//User Id...
		if (!this.jsonObject.isNull(JSONMapping.USER_ID)) {
			this.setUserId(this.jsonObject.getString(JSONMapping.USER_ID));
		}

		//Name...
		if (!this.jsonObject.isNull(JSONMapping.NAME)) {
			this.setName(this.jsonObject.getString(JSONMapping.NAME));
		}

		//Email...
		if (!this.jsonObject.isNull(JSONMapping.EMAIL)) {
			this.setEmail(this.jsonObject.getString(JSONMapping.EMAIL));
		}

		//Email Verified...
		if (!this.jsonObject.isNull(JSONMapping.EMAIL_VERIFIED)) {
			this.setEmailVerified(this.jsonObject.getBoolean(JSONMapping.EMAIL_VERIFIED));
		}

		//Nickname...
		if (!this.jsonObject.isNull(JSONMapping.NICKNAME)) {
			this.setNickname(this.jsonObject.getString(JSONMapping.NICKNAME));
		}

		//Picture...
		if (!this.jsonObject.isNull(JSONMapping.PICTURE)) {
			this.setPicture(this.jsonObject.getString(JSONMapping.PICTURE));
		}

		//Given Name...
		if (!this.jsonObject.isNull(JSONMapping.GIVEN_NAME)) {
			this.setGivenName(this.jsonObject.getString(JSONMapping.GIVEN_NAME));
		}

		//Family Name...
		if (!this.jsonObject.isNull(JSONMapping.FAMILY_NAME)) {
			this.setFamilyName(this.jsonObject.getString(JSONMapping.FAMILY_NAME));
		}

		//Locale...
		if (!this.jsonObject.isNull(JSONMapping.LOCALE)) {
			this.setLocale(this.jsonObject.getString(JSONMapping.LOCALE));
		}

		//Roles...
		if (!this.jsonObject.isNull(JSONMapping.IDENTITIES)) {

			this.identities = new ArrayList<Identity>();

			JSONArray identityListing = this.jsonObject.getJSONArray(JSONMapping.IDENTITIES);

			for (int index = 0;index < identityListing.length();index++)
			{
				this.identities.add(new Identity(identityListing.getJSONObject(index)));
			}
		}
	}

	/**
	 * Gets the User id.
	 *
	 * @return User Id.
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Sets the User id.
	 *
	 * @param userIdParam User Id.
	 */
	public void setUserId(String userIdParam) {
		this.userId = userIdParam;
	}

	/**
	 * Gets the User name.
	 *
	 * @return Name of the User.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the User name.
	 *
	 * @param nameParam Name of the User.
	 */
	public void setName(String nameParam) {
		this.name = nameParam;
	}

	/**
	 * Gets the users associated email.
	 *
	 * @return User Email.
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the users associated email.
	 *
	 * @param emailParam User Email.
	 */
	public void setEmail(String emailParam) {
		this.email = emailParam;
	}

	/**
	 * Gets whether the email is verified.
	 *
	 * @return Whether email is verified.
	 */
	public boolean isEmailVerified() {
		return this.emailVerified;
	}

	/**
	 * Sets whether the email is verified.
	 *
	 * @param emailVerifiedParam Whether email is verified.
	 */
	public void setEmailVerified(boolean emailVerifiedParam) {
		this.emailVerified = emailVerifiedParam;
	}

	/**
	 * Gets the User nickname.
	 *
	 * @return User Nickname.
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * Sets the User nickname.
	 *
	 * @param nicknameParam User Nickname.
	 */
	public void setNickname(String nicknameParam) {
		this.nickname = nicknameParam;
	}

	/**
	 * Gets URL to the Users picture.
	 *
	 * @return Link to picture.
	 */
	public String getPicture() {
		return this.picture;
	}

	/**
	 * Sets URL to the Users picture.
	 *
	 * @param pictureParam Link to picture.
	 */
	public void setPicture(String pictureParam) {
		this.picture = pictureParam;
	}

	/**
	 * Gets the User's given name.
	 *
	 * @return Given name.
	 */
	public String getGivenName() {
		return this.givenName;
	}

	/**
	 * Sets the User's given name.
	 *
	 * @param givenNameParam Given name.
	 */
	public void setGivenName(String givenNameParam) {
		this.givenName = givenNameParam;
	}

	/**
	 * Gets the User's given name.
	 *
	 * @return User Family Name.
	 */
	public String getFamilyName() {
		return this.familyName;
	}

	/**
	 * Sets the User's given name.
	 *
	 * @param familyNameParam User Family Name.
	 */
	public void setFamilyName(String familyNameParam) {
		this.familyName = familyNameParam;
	}

	/**
	 * Gets the User's locale.
	 *
	 * @return Users Locale.
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * Sets the User's locale.
	 *
	 * @param localeParam Users Locale.
	 */
	public void setLocale(String localeParam) {
		this.locale = localeParam;
	}

	/**
	 * Gets the Identities associated with the user.
	 *
	 * @return User's Identities.
	 */
	public List<Identity> getIdentities() {
		return this.identities;
	}

	/**
	 * Sets the Identities associated with the user.
	 *
	 * @param identitiesParam User's Identities.
	 */
	public void setIdentities(List<Identity> identitiesParam) {
		this.identities = identitiesParam;
	}

	/**
	 * Gets a comma seperated list of providers from {@code Identity}'s.
	 *
	 * @return Text list of Providers for user {@code Identities}.
	 *
	 * @see NormalizedUserProfile#getIdentities()
	 */
	public String getListOfProvidersFromIdentities()
	{
		if (this.getIdentities() == null || this.getIdentities().isEmpty())
		{
			return "";
		}

		StringBuilder returnVal = new StringBuilder();

		for (Identity identity : this.getIdentities())
		{
			returnVal.append(identity.getProvider());
			returnVal.append(",");
		}

		String toString = returnVal.toString();
		return toString.substring(0,toString.length() - 1);
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code NormalizedUserProfile}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {

		JSONObject returnVal = super.toJsonObject();

		//User Id...
		if (this.getUserId() != null)
		{
			returnVal.put(JSONMapping.USER_ID, this.getUserId());
		}

		//Name...
		if (this.getName() != null)
		{
			returnVal.put(JSONMapping.NAME, this.getName());
		}

		//Email...
		if (this.getEmail() != null)
		{
			returnVal.put(JSONMapping.EMAIL, this.getEmail());
		}

		//Email Verified...
		returnVal.put(JSONMapping.EMAIL_VERIFIED, this.isEmailVerified());

		//Nickname...
		if (this.getNickname() != null)
		{
			returnVal.put(JSONMapping.NICKNAME, this.getNickname());
		}

		//Picture...
		if (this.getPicture() != null)
		{
			returnVal.put(JSONMapping.PICTURE, this.getPicture());
		}

		//Given Name...
		if (this.getGivenName() != null)
		{
			returnVal.put(JSONMapping.GIVEN_NAME, this.getGivenName());
		}

		//Family Name...
		if (this.getFamilyName() != null)
		{
			returnVal.put(JSONMapping.FAMILY_NAME, this.getFamilyName());
		}

		//Locale...
		if (this.getLocale() != null)
		{
			returnVal.put(JSONMapping.LOCALE, this.getLocale());
		}

		//Identities...
		if (this.getIdentities() != null && !this.getIdentities().isEmpty())
		{
			JSONArray identitiesArr = new JSONArray();
			for (Identity toAdd :this.getIdentities())
			{
				identitiesArr.put(toAdd.toJsonObject());
			}

			returnVal.put(JSONMapping.IDENTITIES,identitiesArr);
		}

		return returnVal;
	}
}
