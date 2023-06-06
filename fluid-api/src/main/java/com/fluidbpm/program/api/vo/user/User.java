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

package com.fluidbpm.program.api.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.role.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     Represents the Fluid framework User.
 * </p>
 *
 * The following fields are mandatory for creating or updating a User;
 * Username (create and update)
 * Password (create). Needs to be at least 5 characters.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
 * @see com.fluidbpm.program.api.vo.auth0.Connection
 * @see com.fluidbpm.program.api.vo.auth0.NormalizedUserProfile
 */
@XmlRootElement
@NoArgsConstructor
@Getter
@Setter
public class User extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private String username;
	private String passwordSha256;
	private String passwordClear;
	// Gets 20 Random ASCII {@code Character}s
	private String salt;
	private List<Role> roles;
	private boolean emailUserNotification;
	private List<String> emailAddresses;
	private List<Field> userFields;
	private boolean active;
	private int invalidLoginCount;
	private Date passwordChangedAt;
	private Date loggedInDateTime;
	private Date dateCreated;
	private Date dateLastUpdated;
	// See https://www.worldtimezone.com/
	private Float timezone;
	private String dateFormat;
	private String timeFormat;
	private String locale;

	/**
	 * Username of the administrator user.
	 */
	public static final String ADMIN_USERNAME = "admin";

	/**
	 * The JSON mapping for the {@code User} object.
	 */
	public static class JSONMapping {
		public static final String USERNAME = "username";

		public static final String ACTIVE = "active";
		public static final String INVALID_LOGIN_COUNT = "invalidLoginCount";

		public static final String DATE_CREATED = "dateCreated";
		public static final String DATE_LAST_UPDATED = "dateLastUpdated";
		public static final String PASSWORD_CHANGED_AT = "passwordChangedAt";
		public static final String LOGGED_IN_DATE_TIME = "loggedInDateTime";

		public static final String PASSWORD_SHA_256 = "passwordSha256";
		public static final String PASSWORD_CLEAR = "passwordClear";

		public static final String ROLES = "roles";
		public static final String EMAIL_ADDRESSES = "emailAddresses";
		public static final String EMAIL_USER_NOTIFICATION = "emailUserNotification";
		public static final String SALT = "salt";
		public static final String USER_FIELDS = "userFields";

		public static final String TIMEZONE = "timezone";
		public static final String DATE_FORMAT = "dateFormat";
		public static final String TIME_FORMAT = "timeFormat";
		public static final String LOCALE = "locale";

		/**
		 * Elastic specific properties.
		 */
		public static final class Elastic {
			public static final String USER_ID = "userId";
		}
	}

	/**
	 * New user object to set the username.
	 *
	 * @param username The username.
	 */
	public User(String username) {
		super();
		this.setUsername(username);
	}

	/**
	 * Creates a {@code new} of {@code this} with id set.
	 *
	 * @param userId The {@code User} primary key.
	 */
	public User(Long userId) {
		super();
		this.setId(userId);
	}

	/**
	 * Creates a {@code new} of {@code this} with id set.
	 *
	 * @param userId The {@code User} primary key.
	 * @param username The {@code User} username.
	 */
	public User(Long userId, String username) {
		super();
		this.setId(userId);
		this.setUsername(username);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObject The JSON Object.
	 */
	public User(JSONObject jsonObject){
		super(jsonObject);
		if (this.jsonObject == null) return;

		//Username...
		if (!this.jsonObject.isNull(JSONMapping.USERNAME)) {
			this.setUsername(this.jsonObject.getString(JSONMapping.USERNAME));
		}

		//Password - sha256...
		if (!this.jsonObject.isNull(JSONMapping.PASSWORD_SHA_256)) {
			this.setPasswordSha256(this.jsonObject.getString(JSONMapping.PASSWORD_SHA_256));
		}

		//Password - Clear...
		if (!this.jsonObject.isNull(JSONMapping.PASSWORD_CLEAR)) {
			this.setPasswordClear(this.jsonObject.getString(JSONMapping.PASSWORD_CLEAR));
		}

		//Date Created...
		this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));

		//Date Last Updated...
		this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));

		//Password Changed At...
		this.setPasswordChangedAt(this.getDateFieldValueFromFieldWithName(JSONMapping.PASSWORD_CHANGED_AT));

		//Logged In Date Time...
		this.setLoggedInDateTime(this.getDateFieldValueFromFieldWithName(JSONMapping.LOGGED_IN_DATE_TIME));

		//Salt...
		if (!this.jsonObject.isNull(JSONMapping.SALT)) {
			this.setSalt(this.jsonObject.getString(JSONMapping.SALT));
		}

		//Active...
		if (!this.jsonObject.isNull(JSONMapping.ACTIVE)) {
			this.setActive(this.jsonObject.getBoolean(JSONMapping.ACTIVE));
		}

		//Invalid Login Count...
		if (!this.jsonObject.isNull(JSONMapping.INVALID_LOGIN_COUNT)) {
			this.setInvalidLoginCount(this.jsonObject.getInt(JSONMapping.INVALID_LOGIN_COUNT));
		}

		//Timezone...
		if (!this.jsonObject.isNull(JSONMapping.TIMEZONE)) {
			this.setTimezone((float)this.jsonObject.getDouble(JSONMapping.TIMEZONE));
		}

		//Date format...
		if (!this.jsonObject.isNull(JSONMapping.DATE_FORMAT)) {
			this.setDateFormat(this.jsonObject.getString(JSONMapping.DATE_FORMAT));
		}

		//Time format...
		if (!this.jsonObject.isNull(JSONMapping.TIME_FORMAT)) {
			this.setTimeFormat(this.jsonObject.getString(JSONMapping.TIME_FORMAT));
		}

		//Locale...
		if (!this.jsonObject.isNull(JSONMapping.LOCALE)) {
			this.setLocale(this.jsonObject.getString(JSONMapping.LOCALE));
		}

		//Email User Notification...
		if (this.jsonObject.isNull(JSONMapping.EMAIL_USER_NOTIFICATION)) {
			this.setEmailUserNotification(false);
		} else{
			this.setEmailUserNotification(this.jsonObject.getBoolean(JSONMapping.EMAIL_USER_NOTIFICATION));
		}

		//Roles...
		if (!this.jsonObject.isNull(JSONMapping.ROLES)) {
			JSONArray roleListing = this.jsonObject.getJSONArray(JSONMapping.ROLES);
			List<Role> roleListingList = new ArrayList();
			for (int index = 0;index < roleListing.length();index++) {
				roleListingList.add(new Role(roleListing.getJSONObject(index)));
			}
			this.setRoles(roleListingList);
		}

		//Email Addresses...
		if (!this.jsonObject.isNull(JSONMapping.EMAIL_ADDRESSES)) {
			JSONArray emailListing = this.jsonObject.getJSONArray(JSONMapping.EMAIL_ADDRESSES);
			List<String> emailAddressList = new ArrayList();
			for (int index = 0;index < emailListing.length();index++) {
				emailAddressList.add(emailListing.getString(index));
			}
			this.setEmailAddresses(emailAddressList);
		}

		//User Fields...
		if (!this.jsonObject.isNull(JSONMapping.USER_FIELDS)) {
			JSONArray userFieldListing = this.jsonObject.getJSONArray(JSONMapping.USER_FIELDS);
			List<Field> userFieldListingList = new ArrayList();
			for (int index = 0;index < userFieldListing.length();index++) {
				userFieldListingList.add(new Field(userFieldListing.getJSONObject(index)));
			}
			this.setUserFields(userFieldListingList);
		}
	}

	/**
	 * Check whether {@code this} {@code User} has access
	 * to role {@code roleParam}.
	 *
	 * @param roleParam The role to check for.
	 * @return {@code true} if user has access, otherwise {@code false}.
	 *
	 * @see Role
	 */
	@XmlTransient
	@JsonIgnore
	public boolean doesUserHaveAccessToRole(Role roleParam) {
		if (roleParam == null) return false;

		return this.doesUserHaveAccessToRole(roleParam.getName());
	}

	/**
	 * Check whether {@code this} {@code User} has access
	 * to role with name {@code roleParam}.
	 *
	 * @param roleNameParam The role name to check for.
	 * @return {@code true} if user has access, otherwise {@code false}.
	 *
	 * @see Role
	 */
	@XmlTransient
	@JsonIgnore
	public boolean doesUserHaveAccessToRole(String roleNameParam) {
		if (roleNameParam == null || roleNameParam.trim().isEmpty()) return false;

		if (this.getRoles() == null || this.getRoles().isEmpty()) return false;

		String roleNameParamLower = roleNameParam.trim().toLowerCase();

		for (Role roleAtIndex : this.getRoles()) {
			if (roleAtIndex.getName() == null || roleAtIndex.getName().trim().isEmpty()) continue;

			String iterRoleNameLower = roleAtIndex.getName().trim().toLowerCase();
			if (roleNameParamLower.equals(iterRoleNameLower)) return true;
		}

		return false;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code User}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonObject() throws JSONException {

		JSONObject returnVal = super.toJsonObject();

		//Active...
		returnVal.put(JSONMapping.ACTIVE, this.isActive());

		//Invalid Login Count...
		returnVal.put(JSONMapping.INVALID_LOGIN_COUNT,
				this.getInvalidLoginCount());

		//Username...
		if (this.getUsername() != null) {
			returnVal.put(JSONMapping.USERNAME,this.getUsername());
		}

		//Password Sha 256...
		if (this.getPasswordSha256() != null) {
			returnVal.put(JSONMapping.PASSWORD_SHA_256,this.getPasswordSha256());
		}

		//Password Clear...
		if (this.getPasswordClear() != null) {
			returnVal.put(JSONMapping.PASSWORD_CLEAR,this.getPasswordClear());
		}

		//Date Created...
		if (this.getDateCreated() != null) {
			returnVal.put(User.JSONMapping.DATE_CREATED,
					this.getDateAsObjectFromJson(this.getDateCreated()));
		}

		//Date Last Updated...
		if (this.getDateLastUpdated() != null) {
			returnVal.put(User.JSONMapping.DATE_LAST_UPDATED,
					this.getDateAsObjectFromJson(this.getDateLastUpdated()));
		}

		//Password Changed At...
		if (this.getPasswordChangedAt() != null) {
			returnVal.put(JSONMapping.PASSWORD_CHANGED_AT,
					this.getDateAsObjectFromJson(this.getPasswordChangedAt()));
		}

		//Logged In Date Time...
		if (this.getLoggedInDateTime() != null) {
			returnVal.put(JSONMapping.LOGGED_IN_DATE_TIME,
					this.getDateAsObjectFromJson(this.getLoggedInDateTime()));
		}

		//SALT...
		if (this.getSalt() != null) returnVal.put(JSONMapping.SALT,this.getSalt());

		//Timezone...
		if (this.getTimezone() != null) {
			returnVal.put(JSONMapping.TIMEZONE, this.getTimezone().doubleValue());
		}

		//Date Format...
		if (this.getDateFormat() != null) {
			returnVal.put(JSONMapping.DATE_FORMAT, this.getDateFormat());
		}

		//Time Format...
		if (this.getTimeFormat() != null) {
			returnVal.put(JSONMapping.TIME_FORMAT, this.getTimeFormat());
		}

		//Locale...
		if (this.getLocale() != null) {
			returnVal.put(JSONMapping.LOCALE, this.getLocale());
		}

		//Email Notification...
		returnVal.put(JSONMapping.EMAIL_USER_NOTIFICATION, this.isEmailUserNotification());

		//Roles...
		if (this.getRoles() != null && !this.getRoles().isEmpty()) {
			JSONArray rolesArr = new JSONArray();
			for (Role toAdd :this.getRoles()) {
				rolesArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROLES,rolesArr);
		}

		//Email Addresses...
		if (this.getEmailAddresses() != null && !this.getEmailAddresses().isEmpty()) {
			JSONArray emailArr = new JSONArray();
			for (String toAdd :this.getEmailAddresses()) {
				emailArr.put(toAdd);
			}
			returnVal.put(JSONMapping.EMAIL_ADDRESSES, emailArr);
		}

		//User Fields...
		if (this.getUserFields() != null && !this.getUserFields().isEmpty()) {
			JSONArray userFieldsArr = new JSONArray();
			for (Field toAdd :this.getUserFields()) {
				userFieldsArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.USER_FIELDS,userFieldsArr);
		}

		return returnVal;
	}

	/**
	 * Verify whether {@code this} is equal to {@code objParam}.
	 *
	 * @param objParam The object to compare to
	 * @return {@code true} if {@code objParam} equals {@code this}, otherwise {@code false}.
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public boolean equals(Object objParam) {
		if (!(objParam instanceof User)) return false;

		if (this.getId() == null && this.getUsername() == null) return false;

		User paramCasted = (User)objParam;
		if (paramCasted.getId() == null && paramCasted.getUsername() == null) return false;

		if (this.getId() != null && paramCasted.getId() != null) {
			return (this.getId().equals(paramCasted.getId()));
		}

		if (this.getUsername() != null && paramCasted.getUsername() != null) {
			return (this.getUsername().equals(paramCasted.getUsername()));
		}
		return false;
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the User Field as in Fluid.
	 *
	 * @return The value for the Field as {@code Object}.
	 *
	 * @see Field
	 */
	@XmlTransient
	@JsonIgnore
	public Field getField(String fieldNameParam) {
		if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) return null;
		if (this.userFields == null || this.userFields.isEmpty()) return null;

		String fieldNameParamLower = fieldNameParam.trim().toLowerCase();
		for (Field field : this.userFields) {
			String fieldName = field.getFieldName();
			if (fieldName == null || fieldName.trim().isEmpty()) continue;

			String fieldNameLower = fieldName.trim().toLowerCase();
			if (fieldNameParamLower.equals(fieldNameLower)) return field;
		}
		return null;
	}

	/**
	 * Add the {@code emailToAdd} to the list of email addresses.
	 * @param emailToAdd Email to add.
	 */
	@XmlTransient
	@JsonIgnore
	public void addEmailToEmailAddresses(String emailToAdd) {
		if (UtilGlobal.isBlank(emailToAdd)) return;
		if (this.emailAddresses == null) this.emailAddresses = new ArrayList<>();
		this.emailAddresses.add(emailToAdd);
	}

	/**
	 * Gets the value of {@code this} {@code Field} as a {@code MultiChoice}.
	 *
	 * @param fieldNameParam The name of the field get get {@code MultiChoice} from.
	 *
	 * @return The Field Value or {@code null} if not found.
	 *
	 * @see Field.Type
	 * @see MultiChoice
	 */
	@XmlTransient
	@JsonIgnore
	public MultiChoice getFieldValueAsMultiChoice(String fieldNameParam) {
		Field fieldReturn = this.getField(fieldNameParam);
		return (fieldReturn == null) ? null: fieldReturn.getFieldValueAsMultiChoice();
	}

	/**
	 * Returns a hash code value for the object. This method is
	 * supported for the benefit of hash tables such as those provided by
	 * {@link java.util.HashMap}.
	 *
	 * @return HashCode for combined {@code id} and {@code username}.
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public int hashCode() {
		int hasRadix = 100000;

		if (this.getId() != null) hasRadix += this.getId().hashCode();
		if (this.getUsername() != null) hasRadix += this.getUsername().hashCode();

		return hasRadix;
	}
}
