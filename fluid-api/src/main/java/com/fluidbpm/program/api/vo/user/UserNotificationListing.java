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

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code UserNotification}s.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.8
 *
 * @see UserNotification
 * @see ABaseListing
 */
public class UserNotificationListing extends ABaseListing<UserNotification> {

    public static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public UserNotificationListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public UserNotificationListing(JSONObject jsonObjectParam){
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code UserNotification} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code UserNotification}.
     * @return New {@code UserNotification} instance.
     */
    @Override
    @XmlTransient
    public UserNotification getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new UserNotification(jsonObjectParam);
    }
}
