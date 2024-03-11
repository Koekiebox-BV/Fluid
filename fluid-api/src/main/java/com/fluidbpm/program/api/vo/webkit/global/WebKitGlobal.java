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

package com.fluidbpm.program.api.vo.webkit.global;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitMenuItem;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * WebKit associated with global look and feels.
 * "layoutMode" : "light/dark/dim",
 *  "formType" : "outlined/filled",
 *  "layoutColors" : "",
 *  "topbarTheme" : "light/dark/dim/colored",
 *  "menuTheme" : "light/dark/dim",
 *  "menuModeDefault" : "static/overlay/horizontal/slim/slim-plus",
 *  "menuTypeDefault" : "grouped/ungrouped",
 *  "profileModeDefault" : "popup/inline",
 *  "componentColors" : "",
 *
 * @see com.fluidbpm.program.api.vo.form.Form
 */
@Getter
@Setter
public class WebKitGlobal extends ABaseFluidJSONObject {
	private String layoutMode;
	private String formType;
	private String layoutColors;
	private String topbarTheme;
	private String menuTheme;
	private String menuModeDefault;
	private Boolean menuTypeDefault;
	private String componentColors;
	private String componentColorsHex;
	private String profileModeDefault;
	private String inputStyleAddition;
	private List<WebKitMenuItem> webKitMenuItems;

	/**
	 * The JSON mapping for the {@code WebKitGlobal} object.
	 */
	public static class JSONMapping {
		public static final String LAYOUT_MODE = "layoutMode";
		public static final String FORM_TYPE = "formType";
		public static final String LAYOUT_COLORS = "layoutColors";
		public static final String TOP_BAR_THEME = "topbarTheme";
		public static final String MENU_THEME = "menuTheme";
		public static final String MENU_MODE_DEFAULT = "menuModeDefault";
		public static final String MENU_TYPE_DEFAULT = "menuTypeDefault";
		public static final String COMPONENT_COLORS = "componentColors";
		public static final String COMPONENT_COLORS_HEX = "componentColorsHex";
		public static final String PROFILE_MODE_DEFAULT = "profileModeDefault";
		public static final String INPUT_STYLE_ADDITION = "inputStyleAddition";
		public static final String WEB_KIT_MENU_ITEMS = "webKitMenuItems";
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitGlobal(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.LAYOUT_MODE)) {
			this.setLayoutMode(this.jsonObject.getString(JSONMapping.LAYOUT_MODE));
		}

		if (!this.jsonObject.isNull(JSONMapping.INPUT_STYLE_ADDITION)) {
			this.setInputStyleAddition(this.jsonObject.getString(JSONMapping.INPUT_STYLE_ADDITION));
		}

		if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE)) {
			this.setFormType(this.jsonObject.getString(JSONMapping.FORM_TYPE));
		}

		if (!this.jsonObject.isNull(JSONMapping.LAYOUT_COLORS)) {
			this.setLayoutColors(this.jsonObject.getString(JSONMapping.LAYOUT_COLORS));
		}

		if (!this.jsonObject.isNull(JSONMapping.TOP_BAR_THEME)) {
			this.setTopbarTheme(this.jsonObject.getString(JSONMapping.TOP_BAR_THEME));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_THEME)) {
			this.setMenuTheme(this.jsonObject.getString(JSONMapping.MENU_THEME));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_MODE_DEFAULT)) {
			this.setMenuModeDefault(this.jsonObject.getString(JSONMapping.MENU_MODE_DEFAULT));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_TYPE_DEFAULT)) {
			this.setMenuTypeDefault(this.jsonObject.getBoolean(JSONMapping.MENU_TYPE_DEFAULT));
		}

		if (!this.jsonObject.isNull(JSONMapping.MENU_MODE_DEFAULT)) {
			this.setMenuModeDefault(this.jsonObject.getString(JSONMapping.MENU_MODE_DEFAULT));
		}

		if (!this.jsonObject.isNull(JSONMapping.COMPONENT_COLORS)) {
			this.setComponentColors(this.jsonObject.getString(JSONMapping.COMPONENT_COLORS));
		}

		if (!this.jsonObject.isNull(JSONMapping.COMPONENT_COLORS_HEX)) {
			this.setComponentColorsHex(this.jsonObject.getString(JSONMapping.COMPONENT_COLORS_HEX));
		} else {
			this.setComponentColorsHex(this.getComponentColors());
		}

		if (!this.jsonObject.isNull(JSONMapping.PROFILE_MODE_DEFAULT)) {
			this.setProfileModeDefault(this.jsonObject.getString(JSONMapping.PROFILE_MODE_DEFAULT));
		}

		if (!this.jsonObject.isNull(JSONMapping.WEB_KIT_MENU_ITEMS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.WEB_KIT_MENU_ITEMS);
			List<WebKitMenuItem> objs = new ArrayList();
			for (int index = 0;index < jsonArray.length();index++) {
				objs.add(new WebKitMenuItem(jsonArray.getJSONObject(index)));
			}
			this.setWebKitMenuItems(objs);
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
	public JSONObject toJsonObject() {
		JSONObject returnVal = super.toJsonObject();

		if (this.getLayoutMode() != null) {
			returnVal.put(JSONMapping.LAYOUT_MODE,this.getLayoutMode());
		}

		if (this.getInputStyleAddition() != null) {
			returnVal.put(JSONMapping.INPUT_STYLE_ADDITION, this.getInputStyleAddition());
		}

		if (this.getFormType() != null) {
			returnVal.put(JSONMapping.FORM_TYPE,this.getFormType());
		}

		if (this.getLayoutColors() != null) {
			returnVal.put(JSONMapping.LAYOUT_COLORS,this.getLayoutColors());
		}

		if (this.getTopbarTheme() != null) {
			returnVal.put(JSONMapping.TOP_BAR_THEME,this.getTopbarTheme());
		}

		if (this.getMenuTheme() != null) {
			returnVal.put(JSONMapping.MENU_THEME,this.getMenuTheme());
		}

		if (this.getMenuModeDefault() != null) {
			returnVal.put(JSONMapping.MENU_MODE_DEFAULT,this.getMenuModeDefault());
		}

		if (this.getMenuTypeDefault() != null) {
			returnVal.put(JSONMapping.MENU_TYPE_DEFAULT,this.getMenuTypeDefault());
		}

		if (this.getComponentColors() != null) {
			returnVal.put(JSONMapping.COMPONENT_COLORS,this.getComponentColors());
		}

		this.setComponentColorsHex(this.hexFromComponentColors());
		if (this.getComponentColorsHex() != null) {
			returnVal.put(JSONMapping.COMPONENT_COLORS_HEX, this.getComponentColorsHex());
		}

		if (this.getProfileModeDefault() != null) {
			returnVal.put(JSONMapping.PROFILE_MODE_DEFAULT,this.getProfileModeDefault());
		}

		if (this.getWebKitMenuItems() != null && !this.getWebKitMenuItems().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (WebKitMenuItem toAdd : this.getWebKitMenuItems()) jsonArray.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.WEB_KIT_MENU_ITEMS, jsonArray);
		}

		return returnVal;
	}

	private String hexFromComponentColors() {
		String color = this.getComponentColors();
		if (UtilGlobal.isBlank(color)) return color;

		color = color.toLowerCase();

		switch (color) {
			case "blue": return "2C84D8";
			case "wisteria": return "A964AE";
			case "cyan": return "23A4D4";
			case "amber": return "DB8519";
			case "pink": return "F5487F";
			case "orange": return "CB623A";
			case "victoria": return "594790";
			case "chateau-green": return "3D9462";
			case "paradiso": return "3B9195";
			case "chambray": return "3161BA";
			case "tapestry": return "A2527F";

			default: return color;
		}
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
