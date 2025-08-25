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

package com.fluidbpm.program.api.vo.webkit.viewgroup;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see Field
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class WebKitWorkspaceRouteField extends ABaseFluidGSONObject {
    private Field routeField;
    private int fieldOrder;
    private boolean grouped;
    private String cssStyle;
    private String cssClass;

    @XmlTransient
    private boolean selected;

    /**
     * The JSON mapping for the {@code WebKitForm} object.
     */
    public static class JSONMapping {
        public static final String ROUTE_FIELD = "routeField";
        public static final String FIELD_ORDER = "fieldOrder";
        public static final String GROUPED = "grouped";
        public static final String CSS_STYLE = "cssStyle";
        public static final String CSS_CLASS = "cssClass";
    }

    public WebKitWorkspaceRouteField() {
        this(new JsonObject());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitWorkspaceRouteField(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setRouteField(this.extractObject(JSONMapping.ROUTE_FIELD, Field::new));
        this.setFieldOrder(this.getAsIntegerNullSafe(JSONMapping.FIELD_ORDER));
        this.setCssClass(this.getAsStringNullSafe(JSONMapping.CSS_CLASS));
        this.setCssStyle(this.getAsStringNullSafe(JSONMapping.CSS_STYLE));
        this.setGrouped(this.getAsBooleanNullSafe(JSONMapping.GROUPED));
    }

    /**
     * @param routeField The route field associated.
     * @see Field
     */
    public WebKitWorkspaceRouteField(Field routeField) {
        this();
        this.setRouteField(routeField);
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

        if (this.getRouteField() != null) {
            Field reducedField = new Field(this.getRouteField().getId());
            returnVal.add(JSONMapping.ROUTE_FIELD, reducedField.toJsonObject());
        }

        this.setAsProperty(JSONMapping.FIELD_ORDER, returnVal, this.getFieldOrder());
        this.setAsProperty(JSONMapping.GROUPED, returnVal, this.isGrouped());
        this.setAsProperty(JSONMapping.CSS_CLASS, returnVal, this.getCssClass());
        this.setAsProperty(JSONMapping.CSS_STYLE, returnVal, this.getCssStyle());

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

    /**
     * Id for WebKit Field.
     *
     * @return Primary Key of {@code routeField}.
     */
    @Override
    public Long getId() {
        return (this.routeField == null) ? null : this.routeField.getId();
    }
}
