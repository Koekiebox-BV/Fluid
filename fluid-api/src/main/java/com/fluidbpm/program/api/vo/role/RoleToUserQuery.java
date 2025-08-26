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

package com.fluidbpm.program.api.vo.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents what UserQueries a {@code Role} has access to.
 * </p>
 *
 * @author jasonbruwer
 * @see com.fluidbpm.program.api.vo.userquery.UserQuery
 * @see Role
 * @since v1.1
 */
@Getter
@Setter
public class RoleToUserQuery extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private UserQuery userQuery;
    private Role role;

    /**
     * The JSON mapping for the {@code RoleToUserQuery} object.
     */
    public static class JSONMapping {
        public static final String USER_QUERY = "userQuery";
        public static final String ROLE = "role";
    }

    /**
     * Default constructor.
     */
    public RoleToUserQuery() {
        super();
    }

    /**
     * Sets the Id associated with a 'Role To User Query'.
     *
     * @param roleToUserQueryIdParam RoleToUserQuery Id.
     */
    public RoleToUserQuery(Long roleToUserQueryIdParam) {
        super();

        this.setId(roleToUserQueryIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RoleToUserQuery(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setRole(this.extractObject(JSONMapping.ROLE, Role::new));
        this.setUserQuery(this.extractObject(JSONMapping.USER_QUERY, UserQuery::new));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToUserQuery}
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsObj(JSONMapping.USER_QUERY, returnVal, this::getUserQuery);
        this.setAsObj(JSONMapping.ROLE, returnVal, this::getRole);
        return returnVal;
    }
}
