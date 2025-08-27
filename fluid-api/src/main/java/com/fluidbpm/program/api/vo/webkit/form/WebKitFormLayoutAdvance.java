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

package com.fluidbpm.program.api.vo.webkit.form;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit Advanced layout configuration for {@code WebKitForm} definitions.
 *
 * @see WebKitForm
 * @see Field
 */
@Getter
@Setter
public class WebKitFormLayoutAdvance extends ABaseFluidGSONObject {
    private int colSpan = 6;
    private Field field;

    /**
     * Default.
     */
    public WebKitFormLayoutAdvance() {
        this(new JsonObject());
    }

    /**
     * Set the field.
     *
     * @param field The field to set for advance.
     */
    public WebKitFormLayoutAdvance(Field field) {
        this();
        this.setField(field);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitFormLayoutAdvance(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setColSpan(this.getAsIntegerNullSafeStrictVal(JSONMapping.COLUMN_SPAN));
        this.setField(this.extractObject(JSONMapping.FIELD, Field::new));
    }

    /**
     * The JSON mapping for the {@code WebKitFormLayoutAdvance} object.
     */
    public static class JSONMapping {
        public static final String COLUMN_SPAN = "colSpan";
        public static final String FIELD = "field";
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JSONObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JSONObject} representation of {@code WebKitFormLayoutAdvance}
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        if (this.getField() != null) {
            Field reducedField = new Field(this.getField().getId(), this.getField().getFieldName());
            returnVal.add(JSONMapping.FIELD, reducedField.toJsonObject());
        }
        this.setAsProperty(JSONMapping.COLUMN_SPAN, returnVal, this.getColSpan());
        return returnVal;
    }

    /**
     * {@code String} representation of {@code this} object.
     *
     * @return {@code super #toString}
     */
    @Override
    @XmlTransient
    public String toString() {
        return super.toString();
    }
}
