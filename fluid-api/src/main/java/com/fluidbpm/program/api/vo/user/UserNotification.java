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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * <p>
 *     A notification for a user.
 *     The user notifications are divided into categories.
 * </p>
 *
 * @author jasonbruwer on 2018-03-27
 * @since v1.8
 *
 * @see User
 */
public class UserNotification extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private Date dateRead;
	private Date dateCreated;

	private String message;
	private String expiringLink;

	private User user;
	private int userNotificationType;

	/**
	 * The type of notification a notification call fall under.
	 */
	public static class UserNotificationType
	{
		public static final int GLOBAL = 1;
		public static final int EMAIL_PROCESSED = 2;
		public static final int COLLEAGUE_COLLABORATION = 3;
		public static final int VERSION_RELEASE = 4;
		public static final int SYSTEM_ADMINISTRATIVE_CHANGE = 5;
		public static final int CRITICAL = 6;
	}

	/**
	 * The JSON mapping for the {@code RoleToJobView} object.
	 */
	public static class JSONMapping
	{
		public static final String DATE_READ = "dateRead";
		public static final String DATE_CREATED = "dateCreated";
		public static final String MESSAGE = "message";
		public static final String EXPIRING_LINK = "expiringLink";
		public static final String USER = "user";
		public static final String USER_NOTIFICATION_TYPE = "userNotificationType";
	}

	/**
	 * Default constructor.
	 */
	public UserNotification() {
		super();
	}

	/**
	 * Sets the Id associated with a 'Role To Job View'.
	 *
	 * @param roleToJobViewIdParam RoleToJobView Id.
	 */
	public UserNotification(Long roleToJobViewIdParam) {
		super();

		this.setId(roleToJobViewIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public UserNotification(JSONObject jsonObjectParam){
		super(jsonObjectParam);

		if (this.jsonObject == null)
		{
			return;
		}

		//User...
		if (!this.jsonObject.isNull(JSONMapping.USER)) {
			this.setUser(new User(this.jsonObject.getJSONObject(
					JSONMapping.USER)));
		}

		//Date Created...
		this.setDateCreated(this.getDateFieldValueFromFieldWithName(
				JSONMapping.DATE_CREATED));

		//Date Read...
		this.setDateRead(this.getDateFieldValueFromFieldWithName(
				JSONMapping.DATE_READ));

		//Message...
		if (!this.jsonObject.isNull(JSONMapping.MESSAGE)) {
			this.setMessage(this.jsonObject.getString(JSONMapping.MESSAGE));
		}

		//Expiring Link...
		if (!this.jsonObject.isNull(JSONMapping.EXPIRING_LINK)) {
			this.setExpiringLink(this.jsonObject.getString(
					JSONMapping.EXPIRING_LINK));
		}

		//User Notification Type...
		if (!this.jsonObject.isNull(JSONMapping.USER_NOTIFICATION_TYPE)) {
			this.setUserNotificationType(this.jsonObject.getInt(
					JSONMapping.USER_NOTIFICATION_TYPE));
		}
	}


	/**
	 * Gets The {@code Date} the User Notification was created.
	 *
	 * @return Date Created.
	 */
	public Date getDateCreated() {
		return this.dateCreated;
	}

	/**
	 * Sets The {@code Date} the User Notification was created.
	 *
	 * @param dateCreatedParam Date Created.
	 */
	public void setDateCreated(Date dateCreatedParam) {
		this.dateCreated = dateCreatedParam;
	}

	/**
	 * Gets The {@code Date} the User Notification was read by the user.
	 *
	 * @return Date Read.
	 */
	public Date getDateRead() {
		return this.dateRead;
	}

	/**
	 * Sets {@code Date} the User Notification was read by the user.
	 *
	 * @param dateReadParam Date Read.
	 */
	public void setDateRead(Date dateReadParam) {
		this.dateRead = dateReadParam;
	}

	/**
	 * Gets the notification text message.
	 *
	 * @return Message.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the notification text message.
	 *
	 * @param messageParam Message.
	 */
	public void setMessage(String messageParam) {
		this.message = messageParam;
	}

	/**
	 * Gets the notification expiring link (if applicable).
	 *
	 * @return Expiring Link.
	 */
	public String getExpiringLink() {
		return this.expiringLink;
	}

	/**
	 * Sets the notification expiring link (if applicable).
	 *
	 * @param expiringLinkParam Expiring Link.
	 */
	public void setExpiringLink(String expiringLinkParam) {
		this.expiringLink = expiringLinkParam;
	}

	/**
	 * The {@code User} the notification is for.
	 *
	 * @return The {@code User}
	 *
	 * @see User
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * The {@code User} the notification is for.
	 *
	 * @param userParam The {@code User}
	 *
	 * @see User
	 */
	public void setUser(User userParam) {
		this.user = userParam;
	}

	/**
	 * The notification type.
	 *
	 * @return The {@code User} notification type.
	 *
	 * @see UserNotificationType
	 */
	public int getUserNotificationType() {
		return this.userNotificationType;
	}

	/**
	 * The notification type.
	 *
	 * @param userNotificationTypeParam The notification type.
	 *
	 * @see UserNotificationType
	 */
	public void setUserNotificationType(int userNotificationTypeParam) {
		this.userNotificationType = userNotificationTypeParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code UserNotification}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {

		JsonObject returnVal = super.toJsonObject();

		//User...
		if (this.getUser() != null)
		{
			returnVal.put(JSONMapping.USER,
					this.getUser().toJsonObject());
		}

		//Date Created...
		if (this.getDateCreated() != null) {
			returnVal.put(JSONMapping.DATE_CREATED,
					this.getDateAsObjectFromJson(this.getDateCreated()));
		}

		//Date Read...
		if (this.getDateRead() != null) {
			returnVal.put(JSONMapping.DATE_READ, this.getDateAsObjectFromJson(this.getDateRead()));
		}

		//Expiring Link...
		if (this.getExpiringLink() != null) {
			returnVal.put(JSONMapping.EXPIRING_LINK, this.getExpiringLink());
		}

		//Message...
		if (this.getMessage() != null) {
			returnVal.put(JSONMapping.MESSAGE, this.getMessage());
		}

		//User Notification Type...
		returnVal.put(JSONMapping.USER_NOTIFICATION_TYPE, this.getUserNotificationType());

		return returnVal;
	}
}
