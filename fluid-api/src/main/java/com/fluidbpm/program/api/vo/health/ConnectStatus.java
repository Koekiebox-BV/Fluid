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

import java.util.Date;

/**
 * Connection status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @see ABaseFluidGSONObject
 * @since 1.13
 */
@Getter
@Setter
@NoArgsConstructor
public class ConnectStatus extends ABaseFluidGSONObject {
    private Date timestamp;
    private Health systemHealth;
    private Health databaseHealth;
    private String version;
    private String fluidAPIVersion;
    private String fluidWebKitVersion;
    private String internalTimeZone;
    private Long connectObtainDurationMillis;

    /**
     * The JSON mapping for the {@code ConnectStatus} object.
     */
    public static class JSONMapping {
        public static final String TIMESTAMP = "timestamp";
        public static final String SYSTEM_HEALTH = "systemHealth";
        public static final String DATABASE_HEALTH = "databaseHealth";
        public static final String VERSION = "version";
        public static final String FLUID_API_VERSION = "fluidAPIVersion";
        public static final String FLUID_WEBKIT_VERSION = "fluidWebKitVersion";
        public static final String INTERNAL_TIMEZONE = "internalTimeZone";
        public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public ConnectStatus(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setTimestamp(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP));

        String systemHealthStr = this.getAsStringNullSafe(JSONMapping.SYSTEM_HEALTH);
        if (systemHealthStr != null && !systemHealthStr.isEmpty()) {
            try {
                this.setSystemHealth(Health.valueOf(systemHealthStr));
            } catch (IllegalArgumentException ignored) {
                // ignore invalid value
            }
        }

        String databaseHealthStr = this.getAsStringNullSafe(JSONMapping.DATABASE_HEALTH);
        if (databaseHealthStr != null && !databaseHealthStr.isEmpty()) {
            try {
                this.setDatabaseHealth(Health.valueOf(databaseHealthStr));
            } catch (IllegalArgumentException ignored) {
                // ignore invalid value
            }
        }

        this.setVersion(this.getAsStringNullSafe(JSONMapping.VERSION));
        this.setFluidAPIVersion(this.getAsStringNullSafe(JSONMapping.FLUID_API_VERSION));
        this.setFluidWebKitVersion(this.getAsStringNullSafe(JSONMapping.FLUID_WEBKIT_VERSION));
        this.setInternalTimeZone(this.getAsStringNullSafe(JSONMapping.INTERNAL_TIMEZONE));
        this.setConnectObtainDurationMillis(this.getAsLongNullSafe(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code ConnectStatus}.
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.TIMESTAMP, returnVal, this.getDateAsLongFromJson(this.getTimestamp()));
        if (this.getSystemHealth() != null) {
            this.setAsProperty(JSONMapping.SYSTEM_HEALTH, returnVal, this.getSystemHealth().toString());
        }
        if (this.getDatabaseHealth() != null) {
            this.setAsProperty(JSONMapping.DATABASE_HEALTH, returnVal, this.getDatabaseHealth().toString());
        }
        this.setAsProperty(JSONMapping.VERSION, returnVal, this.getVersion());
        this.setAsProperty(JSONMapping.FLUID_API_VERSION, returnVal, this.getFluidAPIVersion());
        this.setAsProperty(JSONMapping.FLUID_WEBKIT_VERSION, returnVal, this.getFluidWebKitVersion());
        this.setAsProperty(JSONMapping.INTERNAL_TIMEZONE, returnVal, this.getInternalTimeZone());
        this.setAsProperty(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, returnVal, this.getConnectObtainDurationMillis());

        return returnVal;
    }
}
