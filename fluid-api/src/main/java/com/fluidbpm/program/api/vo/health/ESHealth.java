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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Connection status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @since 1.13
 */
@Getter
@Setter
@NoArgsConstructor
public class ESHealth extends ABaseFluidGSONObject {
    private boolean enabled;
    private Health elasticsearchHealth;
    private String connectionInfo;
    private String connectionProperties;
    private Long connectObtainDurationMillis;
    private List<String> addresses;

    /**
     * The JSON mapping for the {@code ConnectStatus} object.
     */
    public static class JSONMapping {
        public static final String TIMESTAMP = "timestamp";
        public static final String SYSTEM_HEALTH = "systemHealth";
        public static final String DATABASE_HEALTH = "databaseHealth";
        public static final String VERSION = "version";
        public static final String FLUID_API_VERSION = "fluidAPIVersion";
        public static final String INTERNAL_TIMEZONE = "internalTimeZone";
        public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public ESHealth(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

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

        this.setAsProperty(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, returnVal, this.getConnectObtainDurationMillis());

        return returnVal;
    }
}
