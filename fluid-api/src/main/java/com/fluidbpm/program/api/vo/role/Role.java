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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     Represents the Fluid framework user Role.
 * </p>
 *
 * The following fields are mandatory for creating or updating a Role;
 *
 * Name (create and update)
 * Description (create and update)
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see User
 * @see RoleToFormDefinition
 * @see RoleToJobView
 * @see UserQuery
 */
public class Role extends ABaseFluidJSONObject {

	public static final long serialVersionUID = 1L;

	private String name;
	private String description;

	private List<String> adminPermissions;
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
		public static final String ROLE_TO_FORM_DEFINITIONS = "roleToFormDefinitions";
		public static final String ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS =
				"roleToFormFieldToFormDefinitions";
		public static final String ROLE_TO_JOB_VIEWS = "roleToJobViews";
		public static final String ROLE_TO_USER_QUERIES = "roleToUserQueries";
	}

	/**
	 * Default constructor.
	 */
	public Role() {
		super();
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
	public Role(JSONObject jsonObjectParam){
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		//Name...
		if (!this.jsonObject.isNull(JSONMapping.NAME)) {
			this.setName(this.jsonObject.getString(JSONMapping.NAME));
		}

		//Description...
		if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
			this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
		}

		//Admin Permissions...
		if (!this.jsonObject.isNull(JSONMapping.ADMIN_PERMISSIONS)) {
			JSONArray adminPermissionListing =
					this.jsonObject.getJSONArray(JSONMapping.ADMIN_PERMISSIONS);
			List<String> adminPermissionList = new ArrayList();
			for (int index = 0;index < adminPermissionListing.length();index++) {
				adminPermissionList.add(adminPermissionListing.getString(index));
			}
			this.setAdminPermissions(adminPermissionList);
		}

		//Role to Form Definitions...
		if (!this.jsonObject.isNull(JSONMapping.ROLE_TO_FORM_DEFINITIONS)) {
			JSONArray roleToFormDefArray = this.jsonObject.getJSONArray(
					JSONMapping.ROLE_TO_FORM_DEFINITIONS);
			List<RoleToFormDefinition> roleToFormDefListing = new ArrayList();
			for (int index = 0;index < roleToFormDefArray.length();index++) {
				roleToFormDefListing.add(
						new RoleToFormDefinition(roleToFormDefArray.getJSONObject(index)));
			}
			this.setRoleToFormDefinitions(roleToFormDefListing);
		}

		//Role to Form Field to Form Definitions...
		if (!this.jsonObject.isNull(JSONMapping.ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS)) {
			JSONArray roleToFormDefArray = this.jsonObject.getJSONArray(
					JSONMapping.ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS);
			List<RoleToFormFieldToFormDefinition> roleToFormDefListing = new ArrayList();
			for (int index = 0;index < roleToFormDefArray.length();index++) {
				roleToFormDefListing.add(
						new RoleToFormFieldToFormDefinition(roleToFormDefArray.getJSONObject(index)));
			}
			this.setRoleToFormFieldToFormDefinitions(roleToFormDefListing);
		}

		//Role to Job Views...
		if (!this.jsonObject.isNull(JSONMapping.ROLE_TO_JOB_VIEWS)) {
			JSONArray roleToJobViewDefArray = this.jsonObject.getJSONArray(
					JSONMapping.ROLE_TO_JOB_VIEWS);

			List<RoleToJobView> roleToFormDefListing = new ArrayList();
			for (int index = 0;index < roleToJobViewDefArray.length();index++) {
				roleToFormDefListing.add(
						new RoleToJobView(roleToJobViewDefArray.getJSONObject(index)));
			}
			this.setRoleToJobViews(roleToFormDefListing);
		}

		//Role to User Queries...
		if (!this.jsonObject.isNull(JSONMapping.ROLE_TO_USER_QUERIES)) {
			JSONArray userQueryArray = this.jsonObject.getJSONArray(
					JSONMapping.ROLE_TO_USER_QUERIES);
			List<RoleToUserQuery> userQueryListing = new ArrayList();
			for (int index = 0;index < userQueryArray.length();index++) {
				userQueryListing.add(
						new RoleToUserQuery(userQueryArray.getJSONObject(index)));
			}
			this.setRoleToUserQueries(userQueryListing);
		}
	}

	/**
	 * Convert the comma separated list of roles as objects.
	 *
	 * @param roleListingParam The comma separated role listing.
	 *                         Example; {@code "Admin, Super Admin, HR, Finance"}
	 *
	 * @return The {@code roleListingParam} as {@code Role} objects.
	 */
	@XmlTransient
	public static List<Role> convertToObjects(String roleListingParam) {
		if (roleListingParam == null || roleListingParam.trim().isEmpty()) {
			return null;
		}

		String[] listOfRoles = roleListingParam.split(UtilGlobal.REG_EX_COMMA);

		List<Role> returnVal = new ArrayList<>();
		for (String roleName : listOfRoles) {
			Role roleToAdd = new Role();
			roleToAdd.setName(roleName.trim());
			returnVal.add(roleToAdd);
		}

		return returnVal;
	}

	/**
	 * Gets {@code Role} name.
	 *
	 * @return A {@code Role}s name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets {@code Role} name.
	 *
	 * @param nameParam A {@code Role} name.
	 */
	public void setName(String nameParam) {
		this.name = nameParam;
	}

	/**
	 * Gets {@code Role} description.
	 *
	 * @return A {@code Role}s description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets {@code Role} description.
	 *
	 * @param descriptionParam A {@code Role}s description.
	 */
	public void setDescription(String descriptionParam) {
		this.description = descriptionParam;
	}

	/**
	 * Gets {@code Role} associated {@code RoleToFormDefinition}s.
	 *
	 * @return A {@code Role}s {@code RoleToFormDefinition}s.
	 *
	 * @see RoleToFormDefinition
	 */
	public List<RoleToFormDefinition> getRoleToFormDefinitions() {
		return this.roleToFormDefinitions;
	}

	/**
	 * Sets {@code Role} associated {@code RoleToFormDefinition}s.
	 *
	 * @param roleToFormDefinitionsParam A {@code Role}s {@code RoleToFormDefinition}s.
	 *
	 * @see RoleToFormDefinition
	 */
	public void setRoleToFormDefinitions(List<RoleToFormDefinition> roleToFormDefinitionsParam) {
		this.roleToFormDefinitions = roleToFormDefinitionsParam;
	}

	/**
	 * Gets {@code Role} associated {@code RoleToFormFieldToFormDefinition}s.
	 *
	 * @return A {@code Role}s {@code RoleToFormFieldToFormDefinition}s.
	 *
	 * @see RoleToFormFieldToFormDefinition
	 */
	public List<RoleToFormFieldToFormDefinition> getRoleToFormFieldToFormDefinitions() {
		return this.roleToFormFieldToFormDefinitions;
	}

	/**
	 * Gets {@code Role} associated {@code RoleToFormFieldToFormDefinition}s.
	 *
	 * @param roleToFormFieldToFormDefinitionsParam
	 * A {@code Role}s {@code RoleToFormFieldToFormDefinition}s.
	 *
	 * @see RoleToFormFieldToFormDefinition
	 */
	public void setRoleToFormFieldToFormDefinitions(
			List<RoleToFormFieldToFormDefinition> roleToFormFieldToFormDefinitionsParam) {
		this.roleToFormFieldToFormDefinitions = roleToFormFieldToFormDefinitionsParam;
	}

	/**
	 * Gets {@code Role} Administration Permissions.
	 *
	 * @return A {@code Role}s Administration Permissions.
	 */
	public List<String> getAdminPermissions() {
		return this.adminPermissions;
	}

	/**
	 * Sets {@code Role} Administration Permissions.
	 *
	 * @param adminPermissionsParam A {@code Role}s Administration Permissions.
	 */
	public void setAdminPermissions(List<String> adminPermissionsParam) {
		this.adminPermissions = adminPermissionsParam;
	}

	/**
	 * Gets {@code Role} Job Views.
	 *
	 * @return A {@code Role}s Job Views.
	 */
	public List<RoleToJobView> getRoleToJobViews() {
		return this.roleToJobViews;
	}

	/**
	 * Sets {@code Role} Job Views.
	 *
	 * @param roleToJobViewsParam A {@code Role}s Job Views.
	 */
	public void setRoleToJobViews(List<RoleToJobView> roleToJobViewsParam) {
		this.roleToJobViews = roleToJobViewsParam;
	}

	/**
	 * Gets {@code RoleToUserQuery}s.
	 *
	 * @return A {@code Role}s associated {@code RoleToUserQuery}s.
	 *
	 * @see UserQuery
	 */
	public List<RoleToUserQuery> getRoleToUserQueries() {
		return this.roleToUserQueries;
	}

	/**
	 * Sets {@code RoleToUserQuery}s.
	 *
	 * @param userQueriesParam A {@code Role}s associated {@code RoleToUserQuery}s.
	 *
	 * @see UserQuery
	 */
	public void setRoleToUserQueries(List<RoleToUserQuery> userQueriesParam) {
		this.roleToUserQueries = userQueriesParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code Role}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Name...
		if (this.getName() != null) {
			returnVal.put(JSONMapping.NAME,this.getName());
		}

		//Description...
		if (this.getDescription() != null) {
			returnVal.put(JSONMapping.DESCRIPTION,this.getDescription());
		}

		//Admin Permissions...
		if (this.getAdminPermissions() != null && !this.getAdminPermissions().isEmpty()) {
			JSONArray adminPerArr = new JSONArray();
			for (String toAdd : this.getAdminPermissions()) {
				adminPerArr.put(toAdd);
			}

			returnVal.put(JSONMapping.ADMIN_PERMISSIONS, adminPerArr);
		}

		//Role to Form Definitions...
		if (this.getRoleToFormDefinitions() != null && !this.getRoleToFormDefinitions().isEmpty()) {
			JSONArray roleToFormDefArr = new JSONArray();
			for (RoleToFormDefinition toAdd :this.getRoleToFormDefinitions()) {
				roleToFormDefArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROLE_TO_FORM_DEFINITIONS,roleToFormDefArr);
		}

		//Role To Form Field To Form Definition...
		if (this.getRoleToFormFieldToFormDefinitions() != null && !this.getRoleToFormFieldToFormDefinitions().isEmpty()) {
			JSONArray roleToJobViewArr = new JSONArray();
			for (RoleToFormFieldToFormDefinition toAdd :this.getRoleToFormFieldToFormDefinitions()) {
				roleToJobViewArr.put(toAdd.toJsonObject());
			}

			returnVal.put(JSONMapping.ROLE_TO_FORM_FIELD_TO_FORM_DEFINITIONS, roleToJobViewArr);
		}

		//Role to Job Views...
		if (this.getRoleToJobViews() != null && !this.getRoleToJobViews().isEmpty()) {
			JSONArray roleToJobViewArr = new JSONArray();
			for (RoleToJobView toAdd :this.getRoleToJobViews()) {
				roleToJobViewArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROLE_TO_JOB_VIEWS, roleToJobViewArr);
		}

		//Role to User Queries...
		if (this.getRoleToUserQueries() != null && !this.getRoleToUserQueries().isEmpty()) {
			JSONArray userQueriesArr = new JSONArray();
			for (RoleToUserQuery toAdd :this.getRoleToUserQueries()) {
				userQueriesArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROLE_TO_USER_QUERIES, userQueriesArr);
		}

		return returnVal;
	}
}
