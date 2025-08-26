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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * A notification for a user.
 * The user notifications are divided into categories.
 * </p>
 *
 * @author jasonbruwer on 2018-03-27
 * @see User
 * @since v1.8
 */
@Getter
@Setter
public class UserNotification extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private Date dateRead;
    private Date dateCreated;

    private String message;
    private String expiringLink;

    private User user;
    private int userNotificationType;

    /**
     * The type of notification a notification call fall under.
     */
    public static class UserNotificationType {
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
    public static class JSONMapping {
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
    public UserNotification(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        // User
        this.setUser(this.extractObject(JSONMapping.USER, User::new));

        // Dates
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateRead(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_READ));

        // Strings
        this.setMessage(this.getAsStringNullSafe(JSONMapping.MESSAGE));
        this.setExpiringLink(this.getAsStringNullSafe(JSONMapping.EXPIRING_LINK));

        // Type
        Integer notifType = this.getAsIntegerNullSafe(JSONMapping.USER_NOTIFICATION_TYPE);
        if (notifType != null) this.setUserNotificationType(notifType);
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code UserNotification}
     * 
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        // User
        this.setAsObj(JSONMapping.USER, returnVal, this::getUser);

        // Dates
        this.setAsProperty(JSONMapping.DATE_CREATED, returnVal, this.getDateAsLongFromJson(this.getDateCreated()));
        this.setAsProperty(JSONMapping.DATE_READ, returnVal, this.getDateAsLongFromJson(this.getDateRead()));

        // Strings
        this.setAsProperty(JSONMapping.EXPIRING_LINK, returnVal, this.getExpiringLink());
        this.setAsProperty(JSONMapping.MESSAGE, returnVal, this.getMessage());

        // Type
        this.setAsProperty(JSONMapping.USER_NOTIFICATION_TYPE, returnVal, this.getUserNotificationType());
        return returnVal;
    }
}
