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

package com.fluidbpm.program.api.vo.userquery;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code UserQuery}s.
 * </p>
 *
 * @author jasonbruwer
 * @see UserQuery
 * @since v1.1
 */
public class UserQueryListing extends ABaseGSONListing<UserQuery> {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public UserQueryListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public UserQueryListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code UserQuery} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code UserQuery}.
     * @return New {@code UserQuery} instance.
     */
    @Override
    @XmlTransient
    public UserQuery getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new UserQuery(jsonObjectParam);
    }
}
