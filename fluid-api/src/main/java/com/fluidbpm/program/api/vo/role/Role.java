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
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Represents the Fluid framework user Role.
 * </p>
 * <p>
 * The following fields are mandatory for creating or updating a Role;
 * Name (create and update)
 * Description (create and update)
 *
 * @author jasonbruwer
 * @see User
 * @see RoleToFormDefinition
 * @see RoleToJobView
 * @see UserQuery
 * @since v1.1
 */
@Getter
@Setter
@NoArgsConstructor
public class Role extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    private List<String> adminPermissions;
    private List<String> customPermissions;
    private List<RoleToFormDefinition> roleToFormDefinitions;
    private List<RoleToFormFieldToFormDefinition> roleToFormFieldToFormDefinitions;

    private List<RoleToJobView> roleToJobViews;
    private List<RoleToUserQuery> roleToUserQueries;

    /**
     * The JSON mapping for the {@code Role} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";

        public static final String ADMIN_PERMISSIONS = "adminPermissions";
        public static final String CUSTOM_PERMISSIONS = "customPermissions";
        public static final String ROLE_TO_FORM_DEFINITIONS = "roleToFormDefinitions";
        public static final String ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS = "roleToFormFieldToFormDefinitions";
        public static final String ROLE_TO_JOB_VIEWS = "roleToJobViews";
        public static final String ROLE_TO_USER_QUERIES = "roleToUserQueries";
    }

    /**
     * Sets the Id associated with a 'Role'.
     *
     * @param roleIdParam Role Id.
     */
    public Role(Long roleIdParam) {
        super();
        this.setId(roleIdParam);
    }

    /**
     * Sets the Name associated with a 'Role'.
     *
     * @param roleNameParam Role Name.
     */
    public Role(String roleNameParam) {
        super();
        this.setName(roleNameParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Role(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));

        this.setAdminPermissions(this.extractStrings(JSONMapping.ADMIN_PERMISSIONS));
        this.setCustomPermissions(this.extractStrings(JSONMapping.CUSTOM_PERMISSIONS));
        this.setRoleToFormDefinitions(this.extractObjects(JSONMapping.ROLE_TO_FORM_DEFINITIONS, RoleToFormDefinition::new));
        this.setRoleToFormFieldToFormDefinitions(this.extractObjects(JSONMapping.ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS, RoleToFormFieldToFormDefinition::new));
        this.setRoleToJobViews(this.extractObjects(JSONMapping.ROLE_TO_JOB_VIEWS, RoleToJobView::new));
        this.setRoleToUserQueries(this.extractObjects(JSONMapping.ROLE_TO_USER_QUERIES, RoleToUserQuery::new));
    }

    /**
     * Convert the comma-separated list of roles as objects.
     *
     * @param roleListing The comma separated role listing.
     *                    Example; {@code "Admin, Super Admin, HR, Finance"}
     * @return The {@code roleListingParam} as {@code Role} objects.
     */
    @XmlTransient
    @JsonIgnore
    public static List<Role> convertToObjects(String roleListing) {
        if (roleListing == null || roleListing.trim().isEmpty()) return null;
        String[] listOfRoles = roleListing.split(UtilGlobal.REG_EX_COMMA);
        List<Role> returnVal = new ArrayList<>();
        for (String roleName : listOfRoles) {
            Role roleToAdd = new Role();
            roleToAdd.setName(roleName.trim());
            returnVal.add(roleToAdd);
        }
        return returnVal;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Role}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.NAME, returnVal, this.getName());
        this.setAsProperty(JSONMapping.DESCRIPTION, returnVal, this.getDescription());
        this.setAsStringArray(JSONMapping.ADMIN_PERMISSIONS, returnVal, this.getAdminPermissions());
        this.setAsStringArray(JSONMapping.CUSTOM_PERMISSIONS, returnVal, this.getCustomPermissions());
        this.setAsObjArray(JSONMapping.ROLE_TO_FORM_DEFINITIONS, returnVal, this::getRoleToFormDefinitions);
        this.setAsObjArray(JSONMapping.ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS, returnVal, this::getRoleToFormFieldToFormDefinitions);
        this.setAsObjArray(JSONMapping.ROLE_TO_JOB_VIEWS, returnVal, this::getRoleToJobViews);
        this.setAsObjArray(JSONMapping.ROLE_TO_USER_QUERIES, returnVal, this::getRoleToUserQueries);
        
        return returnVal;
    }
}
