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
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

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

        //User Query...
        if (!this.jsonObject.isNull(JSONMapping.USER_QUERY)) {
            this.setUserQuery(new UserQuery(this.jsonObject.getJSONObject(
                    JSONMapping.USER_QUERY)));
        }

        //Role...
        if (!this.jsonObject.isNull(JSONMapping.ROLE)) {
            this.setRole(new Role(this.jsonObject.getJSONObject(
                    JSONMapping.ROLE)));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToUserQuery}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JSONObject toJsonObject() {
        JSONObject returnVal = super.toJsonObject();

        //User Query...
        if (this.getUserQuery() != null) {
            returnVal.put(JSONMapping.USER_QUERY,
                    this.getUserQuery().toJsonObject());
        }

        //Role...
        if (this.getRole() != null) {
            returnVal.put(JSONMapping.ROLE,
                    this.getRole().toJsonObject());
        }

        return returnVal;
    }
}
