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

    /**
     * Default constructor for the WebKitMenuItem class.
     * Initializes an instance with a new, empty JsonObject.
     * This constructor is primarily used to create an empty WebKit menu item
     * which can later be populated with specific attributes.
     */
    public WebKitMenuItem() {
        this(new JsonObject());
    }

    /**
     * Constructs a new instance of the {@code WebKitMenuItem} class with the specified menu ID.
     *
     * This constructor initializes a {@code WebKitMenuItem} object and sets its menu ID
     * using the provided {@code menuId} parameter. It also utilizes the default constructor
     * to initialize the base state of the object.
     *
     * @param menuId The unique identifier for the menu item. This value is used
     *               to distinguish the menu item within the application's menu hierarchy.
     */
    public WebKitMenuItem(String menuId) {
        this();
        this.setMenuId(menuId);
    }

    /**
     * Constructs a {@code WebKitMenuItem} instance with the specified attributes for menu ID,
     * parent menu ID, menu label, and menu icon.
     *
     * This constructor initializes the object using the default constructor and then
     * assigns the provided values to the corresponding fields. These fields represent key
     * attributes required for defining a menu item in a WebKit-based menu system.
     *
     * @param menuId The unique identifier for the menu item. This is used for referencing the item
     *               within the application's menu hierarchy.
     * @param parentMenuId The unique identifier of the parent menu to which this menu item belongs.
     *                     If the menu item is a top-level menu, this can be set to null or left empty.
     * @param menuLabel The label or display text of the menu item, representing its purpose in the UI.
     * @param menuIcon The icon associated with the menu item, typically used for visual representation
     *                 in the application's UI.
     */
    public WebKitMenuItem(String menuId, String parentMenuId, String menuLabel, String menuIcon) {
        this();
        this.setMenuId(menuId);
        this.setParentMenuId(parentMenuId);
        this.setMenuLabel(menuLabel);
        this.setMenuIcon(menuIcon);
    }

    /**
     * Constructs a new instance of the {@code WebKitMenuItem} class with the specified menu ID,
     * menu label, and menu icon.
     *
     * This constructor initializes a {@code WebKitMenuItem} object with the provided attributes
     * and utilizes the default constructor to set up the base state of the object. It then assigns
     * the given values to the respective properties.
     *
     * @param menuId The unique identifier for the menu item. This value is used to distinguish
     *               the menu item within the application's menu hierarchy.
     * @param menuLabel The label or display text associated with the menu item.
     * @param menuIcon The icon representation associated with the menu item, typically used for
     *                 visual rendering in the UI.
     */
    public WebKitMenuItem(String menuId, String menuLabel, String menuIcon) {
        this();
        this.setMenuId(menuId);
        this.setParentMenuId(parentMenuId);
        this.setMenuLabel(menuLabel);
        this.setMenuIcon(menuIcon);
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
