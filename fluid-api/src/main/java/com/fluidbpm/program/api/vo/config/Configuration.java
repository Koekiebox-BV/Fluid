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

package com.fluidbpm.program.api.vo.config;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Fluid configurations.
 *
 * @author jasonbruwer
 * @see ABaseFluidGSONObject
 * @since v1.1
 */
@Getter
@Setter
@XmlRootElement
public class Configuration extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String key;
    private String value;

    public enum Key {
        MemoryCacheType,
        PrimeFacesTheme,
        WhiteLabel,
        WebKit,
        WebKitPersonalInventory,
        CustomPermissionMapping
    }

    /**
     * The JSON mapping for the {@code Configuration} object.
     */
    public static class JSONMapping {
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    /**
     * Default constructor.
     */
    public Configuration() {
        super();
    }

    /**
     * Sets the key and value.
     *
     * @param configKey   The config key.
     * @param configValue The value of the config.
     */
    public Configuration(String configKey, String configValue) {
        super();
        this.setKey(configKey);
        this.setValue(configValue);
    }

    /**
     * The unique Flow identifier.
     *
     * @param flowIdParam The Flow primary key.
     */
    public Configuration(Long flowIdParam) {
        super();
        this.setId(flowIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Configuration(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setKey(this.getAsStringNullSafe(JSONMapping.KEY));
        this.setValue(this.getAsStringNullSafe(JSONMapping.VALUE));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Configuration}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.KEY, returnVal, this.getKey());
        this.setAsProperty(JSONMapping.VALUE, returnVal, this.getValue());
        return returnVal;
    }
}
