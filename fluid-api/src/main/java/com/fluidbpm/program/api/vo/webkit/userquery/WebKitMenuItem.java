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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see JobView
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class WebKitMenuItem extends ABaseFluidGSONObject {
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
        this(new JsonObject());
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
    public WebKitMenuItem(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setMenuId(this.getAsStringNullSafe(JSONMapping.MENU_ID));
        this.setParentMenuId(this.getAsStringNullSafe(JSONMapping.PARENT_MENU_ID));
        this.setMenuLabel(this.getAsStringNullSafe(JSONMapping.MENU_LABEL));
        this.setMenuIcon(this.getAsStringNullSafe(JSONMapping.MENU_ICON));
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
        this.setAsProperty(JSONMapping.MENU_ID, returnVal, this.getMenuId());
        this.setAsProperty(JSONMapping.PARENT_MENU_ID, returnVal, this.getParentMenuId());
        this.setAsProperty(JSONMapping.MENU_LABEL, returnVal, this.getMenuLabel());
        this.setAsProperty(JSONMapping.MENU_ICON, returnVal, this.getMenuIcon());
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
