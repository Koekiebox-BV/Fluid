/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.health;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Connection status for a Fluid instance cache.
 *
 * @author jasonbruwer on 2023-06-20.
 * @see ABaseFluidGSONObject
 * @since 1.13
 */
@Getter
@Setter
@NoArgsConstructor
public class CacheHealth extends ABaseFluidGSONObject {
    private String type;
    private String uri;
    private Health cacheHealth;
    private String connectionInfo;
    private List<String> cacheConsumption;
    private Long connectObtainDurationMillis;

    /**
     * The JSON mapping for the {@code CacheHealth} object.
     */
    public static class JSONMapping {
        public static final String TYPE = "type";
        public static final String URI = "uri";
        public static final String CACHE_HEALTH = "cacheHealth";
        public static final String CONNECTION_INFO = "connectionInfo";
        public static final String CACHE_CONSUMPTION = "cacheConsumption";
        public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public CacheHealth(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setType(this.getAsStringNullSafe(JSONMapping.TYPE));
        this.setUri(this.getAsStringNullSafe(JSONMapping.URI));

        String ch = this.getAsStringNullSafe(JSONMapping.CACHE_HEALTH);
        if (UtilGlobal.isNotBlank(ch)) {
            this.setCacheHealth(Health.valueOf(ch));
        }

        this.setConnectionInfo(this.getAsStringNullSafe(JSONMapping.CONNECTION_INFO));
        this.setCacheConsumption(this.extractStrings(JSONMapping.CACHE_CONSUMPTION));
        this.setConnectObtainDurationMillis(this.getAsLongNullSafe(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
    }

    /**
     * Conversion to JsonObject from Java Object.
     *
     * @return JsonObject representation of this object.
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.TYPE, returnVal, this.getType());
        this.setAsProperty(JSONMapping.URI, returnVal, this.getUri());
        this.setAsProperty(JSONMapping.CACHE_HEALTH, returnVal, this.getCacheHealth());
        this.setAsProperty(JSONMapping.CONNECTION_INFO, returnVal, this.getConnectionInfo());
        this.setAsStringArray(JSONMapping.CACHE_CONSUMPTION, returnVal, this.getCacheConsumption());
        this.setAsProperty(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, returnVal, this.getConnectObtainDurationMillis());
        return returnVal;
    }
}
