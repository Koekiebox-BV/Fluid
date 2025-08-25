/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2022] Koekiebox (Pty) Ltd
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
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import javax.xml.bind.annotation.XmlTransient;

/**
 * The type of NewInstanceDefault that stores the defaults for newly created form instances.
 */
@Getter
@Setter
public class NewInstanceDefault extends ABaseFluidGSONObject {
    public static final String FIELD = "field";
    public static final String DEFAULT = "defaultVal";

    private String field;
    private String defaultVal;

    /**
     * Default.
     */
    public NewInstanceDefault() {
        this(new JsonObject());
    }


    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param field    The field.
     * @param defValue The default value.
     */
    public NewInstanceDefault(String field, String defValue) {
        this();
        this.setField(field);
        this.setDefaultVal(defValue);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public NewInstanceDefault(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setField(this.getAsStringNullSafe(FIELD));
        this.setDefaultVal(this.getAsStringNullSafe(DEFAULT));
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JSONObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JSONObject} representation of {@code NewInstanceDefault}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see org.json.JSONObject
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(FIELD, returnVal, this.getField());
        this.setAsProperty(DEFAULT, returnVal, this.getDefaultVal());
        return returnVal;
    }
}
