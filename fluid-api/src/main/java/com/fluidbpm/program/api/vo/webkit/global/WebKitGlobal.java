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
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitMenuItem;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * WebKit associated with global look and feels.
 * "layoutMode" : "light/dark/dim",
 * "formType" : "outlined/filled",
 * "layoutColors" : "",
 * "topbarTheme" : "light/dark/dim/colored",
 * "menuTheme" : "light/dark/dim",
 * "menuModeDefault" : "static/overlay/horizontal/slim/slim-plus",
 * "menuTypeDefault" : "grouped/ungrouped",
 * "profileModeDefault" : "popup/inline",
 * "componentColors" : "",
 *
 * @see com.fluidbpm.program.api.vo.form.Form
 */
@Getter
@Setter
public class WebKitGlobal extends ABaseFluidGSONObject {
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
    public WebKitGlobal(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setLayoutMode(this.getAsStringNullSafe(JSONMapping.LAYOUT_MODE));
        this.setInputStyleAddition(this.getAsStringNullSafe(JSONMapping.INPUT_STYLE_ADDITION));
        this.setFormType(this.getAsStringNullSafe(JSONMapping.FORM_TYPE));
        this.setLayoutColors(this.getAsStringNullSafe(JSONMapping.LAYOUT_COLORS));
        this.setTopbarTheme(this.getAsStringNullSafe(JSONMapping.TOP_BAR_THEME));
        this.setMenuTheme(this.getAsStringNullSafe(JSONMapping.MENU_THEME));
        this.setMenuModeDefault(this.getAsStringNullSafe(JSONMapping.MENU_MODE_DEFAULT));
        this.setMenuTypeDefault(this.getAsBooleanNullSafe(JSONMapping.MENU_TYPE_DEFAULT));
        this.setComponentColors(this.getAsStringNullSafe(JSONMapping.COMPONENT_COLORS));
        this.setComponentColorsHex(this.getAsStringNullSafe(JSONMapping.COMPONENT_COLORS_HEX));
        
        if (UtilGlobal.isBlank(this.getComponentColorsHex())) {
            this.setComponentColorsHex(this.getComponentColors());
        }
        
        this.setProfileModeDefault(this.getAsStringNullSafe(JSONMapping.PROFILE_MODE_DEFAULT));
        this.setWebKitMenuItems(this.extractObjects(JSONMapping.WEB_KIT_MENU_ITEMS, WebKitMenuItem::new));
    }

    /**
     * Returns the local JSON object.
     * Only set through constructor.
     *
     * @return The local set {@code JsonObject} object.
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.LAYOUT_MODE, returnVal, this.getLayoutMode());
        this.setAsProperty(JSONMapping.INPUT_STYLE_ADDITION, returnVal, this.getInputStyleAddition());
        this.setAsProperty(JSONMapping.FORM_TYPE, returnVal, this.getFormType());
        this.setAsProperty(JSONMapping.LAYOUT_COLORS, returnVal, this.getLayoutColors());
        this.setAsProperty(JSONMapping.TOP_BAR_THEME, returnVal, this.getTopbarTheme());
        this.setAsProperty(JSONMapping.MENU_THEME, returnVal, this.getMenuTheme());
        this.setAsProperty(JSONMapping.MENU_MODE_DEFAULT, returnVal, this.getMenuModeDefault());
        this.setAsProperty(JSONMapping.MENU_TYPE_DEFAULT, returnVal, this.getMenuTypeDefault());
        this.setAsProperty(JSONMapping.COMPONENT_COLORS, returnVal, this.getComponentColors());

        this.setComponentColorsHex(this.hexFromComponentColors());
        this.setAsProperty(JSONMapping.COMPONENT_COLORS_HEX, returnVal, this.getComponentColorsHex());
        this.setAsProperty(JSONMapping.PROFILE_MODE_DEFAULT, returnVal, this.getProfileModeDefault());

        this.setAsObjArray(JSONMapping.WEB_KIT_MENU_ITEMS, returnVal, this::getWebKitMenuItems);

        return returnVal;
    }

    private String hexFromComponentColors() {
        String color = this.getComponentColors();
        if (UtilGlobal.isBlank(color)) return color;

        color = color.toLowerCase();

        switch (color) {
            case "blue":
                return "2C84D8";
            case "wisteria":
                return "A964AE";
            case "cyan":
                return "23A4D4";
            case "amber":
                return "DB8519";
            case "pink":
                return "F5487F";
            case "orange":
                return "CB623A";
            case "victoria":
                return "594790";
            case "chateau-green":
                return "3D9462";
            case "paradiso":
                return "3B9195";
            case "chambray":
                return "3161BA";
            case "tapestry":
                return "A2527F";

            default:
                return color;
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
