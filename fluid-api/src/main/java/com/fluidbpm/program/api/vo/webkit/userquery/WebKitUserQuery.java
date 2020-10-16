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
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with user query look and feels.
 *
 * @see UserQuery
 */
@Getter
@Setter
@EqualsAndHashCode
public class WebKitUserQuery extends ABaseFluidJSONObject {
	private UserQuery userQuery;
	private WebKitMenuItem menuItem;

	private boolean enableForTopBar;
	private String queryInputLayoutStyle;//Vertical|Horizontal|Vertical Grid|Help Text|

	/**
	 * The JSON mapping for the {@code WebKitWorkspaceJobView} object.
	 */
	public static class JSONMapping {
		public static final String USER_QUERY = "userQuery";
		public static final String MENU_ITEM = "menuItem";
		public static final String ENABLE_FOR_TOP_BAR = "enableForTopBar";
		public static final String QUERY_INPUT_LAYOUT_STYLE = "queryInputLayoutStyle";
	}

	public WebKitUserQuery() {
		this(new JSONObject());
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitUserQuery(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.USER_QUERY)) {
			this.setUserQuery(new UserQuery(this.jsonObject.getJSONObject(JSONMapping.USER_QUERY)));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_ITEM)) {
			this.setMenuItem(new WebKitMenuItem(this.jsonObject.getJSONObject(JSONMapping.MENU_ITEM)));
		}

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_FOR_TOP_BAR)) {
			this.setEnableForTopBar(this.jsonObject.getBoolean(JSONMapping.ENABLE_FOR_TOP_BAR));
		}

		if (!this.jsonObject.isNull(JSONMapping.QUERY_INPUT_LAYOUT_STYLE)) {
			this.setQueryInputLayoutStyle(this.jsonObject.getString(JSONMapping.QUERY_INPUT_LAYOUT_STYLE));
		}
	}

	/**
	 * @param userQuery The user query associated.
	 * @see UserQuery
	 */
	public WebKitUserQuery(UserQuery userQuery) {
		this();
		this.setUserQuery(userQuery);
	}

	/**
	 * Returns the local JSON object.
	 * Only set through constructor.
	 *
	 * @return The local set {@code JSONObject} object.
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonObject() {
		JSONObject returnVal = super.toJsonObject();
		returnVal.put(JSONMapping.ENABLE_FOR_TOP_BAR, this.isEnableForTopBar());
		returnVal.put(JSONMapping.QUERY_INPUT_LAYOUT_STYLE, this.getQueryInputLayoutStyle());

		if (this.getUserQuery() != null) {
			UserQuery reduced = new UserQuery(this.getUserQuery().getId());
			returnVal.put(JSONMapping.USER_QUERY, reduced.toJsonObject());
		}

		if (this.getMenuItem() != null) {
			WebKitMenuItem reduced = new WebKitMenuItem(this.getMenuItem().getMenuId());
			returnVal.put(JSONMapping.MENU_ITEM, reduced.toJsonObject());
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
