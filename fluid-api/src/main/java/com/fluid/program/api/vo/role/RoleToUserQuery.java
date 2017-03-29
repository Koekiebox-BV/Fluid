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

package com.fluid.program.api.vo.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.userquery.UserQuery;

/**
 * <p>
 *     Represents what UserQueries a {@code Role} has access to.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see com.fluid.program.api.vo.userquery.UserQuery
 * @see Role
 */
public class RoleToUserQuery extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private UserQuery userQuery;
    private Role role;

    /**
     * The JSON mapping for the {@code RoleToUserQuery} object.
     */
    public static class JSONMapping
    {
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
    public RoleToUserQuery(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

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
     * Gets the User Query.
     *
     * @return User Query.
     */
    public UserQuery getUserQuery() {
        return this.userQuery;
    }

    /**
     * Sets the User Query for the Role.
     *
     * @param userQueryParam View Rule.
     */
    public void setUserQuery(UserQuery userQueryParam) {
        this.userQuery = userQueryParam;
    }

    /**
     * Gets the Role for the View.
     *
     * @return View Rule.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Sets the Role for the View.
     *
     * @param roleParam View Rule.
     */
    public void setRole(Role roleParam) {
        this.role = roleParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToUserQuery}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //User Query...
        if(this.getUserQuery() != null)
        {
            returnVal.put(JSONMapping.USER_QUERY,
                    this.getUserQuery().toJsonObject());
        }

        //Role...
        if(this.getRole() != null)
        {
            returnVal.put(JSONMapping.ROLE,
                    this.getRole().toJsonObject());
        }

        return returnVal;
    }
}
