/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2020] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.webkit.userquery;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see JobView
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class WebKitMenuItem extends ABaseFluidJSONObject {
	private String menuLabel;
	private String menuIcon;
	private String menuId;
	private String parentMenuId;

	/**
	 * The JSON mapping for the {@code WebKitWorkspaceJobView} object.
	 */
	public static class JSONMapping {
		public static final String MENU_ID = "menuId";
		public static final String MENU_LABEL = "menuLabel";
		public static final String MENU_ICON = "menuIcon";
		public static final String PARENT_MENU_ID = "parentMenuId";
	}

	public WebKitMenuItem() {
		this(new JSONObject());
	}

	public WebKitMenuItem(String menuId) {
		this();
		this.setMenuId(menuId);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitMenuItem(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_ID)) {
			this.setMenuId(this.jsonObject.getString(JSONMapping.MENU_ID));
		}

		if (!this.jsonObject.isNull(JSONMapping.PARENT_MENU_ID)) {
			this.setParentMenuId(this.jsonObject.getString(JSONMapping.PARENT_MENU_ID));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_LABEL)) {
			this.setMenuLabel(this.jsonObject.getString(JSONMapping.MENU_LABEL));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_ICON)) {
			this.setMenuIcon(this.jsonObject.getString(JSONMapping.MENU_ICON));
		}

	}

	/**
	 * Returns the local JSON object.
	 * Only set through constructor.
	 *
	 * @return The local set {@code JSONObject} object.
	 */
	@Override
	@XmlTransient
	public JsonObject toJsonObject() {
		JsonObject returnVal = super.toJsonObject();

		if (this.getMenuId() != null) {
			returnVal.put(JSONMapping.MENU_ID, this.getMenuId());
		}

		if (this.getParentMenuId() != null) {
			returnVal.put(JSONMapping.PARENT_MENU_ID, this.getParentMenuId());
		}

		if (this.getMenuLabel() != null) {
			returnVal.put(JSONMapping.MENU_LABEL, this.getMenuLabel());
		}

		if (this.getMenuIcon() != null) {
			returnVal.put(JSONMapping.MENU_ICON, this.getMenuIcon());
		}

		return returnVal;
	}

	/**
	 * Return the Text representation of {@code this} object.
	 *
	 * @return JSON body of {@code this} object.
	 */
	@Override
	@XmlTransient
	public String toString() {
		return super.toString();
	}
}
