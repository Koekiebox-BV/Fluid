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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Connection status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @see ABaseFluidJSONObject
 * @since 1.13
 */
@Getter
@Setter
@NoArgsConstructor
public class ConnectStatus extends ABaseFluidJSONObject {
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
    public ConnectStatus(JSONObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setTimestamp(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP));

        if (!this.jsonObject.isNull(JSONMapping.SYSTEM_HEALTH)) {
            this.setSystemHealth(this.jsonObject.getEnum(Health.class, JSONMapping.SYSTEM_HEALTH));
        }

        if (!this.jsonObject.isNull(JSONMapping.DATABASE_HEALTH)) {
            this.setDatabaseHealth(this.jsonObject.getEnum(Health.class, JSONMapping.DATABASE_HEALTH));
        }

        if (!this.jsonObject.isNull(JSONMapping.VERSION)) {
            this.setVersion(this.jsonObject.getString(JSONMapping.VERSION));
        }

        if (!this.jsonObject.isNull(JSONMapping.FLUID_API_VERSION)) {
            this.setFluidAPIVersion(this.jsonObject.getString(JSONMapping.FLUID_API_VERSION));
        }

        if (!this.jsonObject.isNull(JSONMapping.FLUID_WEBKIT_VERSION)) {
            this.setFluidWebKitVersion(this.jsonObject.getString(JSONMapping.FLUID_WEBKIT_VERSION));
        }

        if (!this.jsonObject.isNull(JSONMapping.INTERNAL_TIMEZONE)) {
            this.setInternalTimeZone(this.jsonObject.getString(JSONMapping.INTERNAL_TIMEZONE));
        }

        if (!this.jsonObject.isNull(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS)) {
            this.setConnectObtainDurationMillis(this.jsonObject.getLong(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnVal = super.toJsonObject();

        if (this.getTimestamp() != null) {
            returnVal.put(JSONMapping.TIMESTAMP, this.getDateAsObjectFromJson(this.getTimestamp()));
        }

        if (this.getSystemHealth() != null) {
            returnVal.put(JSONMapping.SYSTEM_HEALTH, this.getSystemHealth());
        }

        if (this.getDatabaseHealth() != null) {
            returnVal.put(JSONMapping.DATABASE_HEALTH, this.getDatabaseHealth());
        }

        if (this.getVersion() != null) returnVal.put(JSONMapping.VERSION, this.getVersion());
        if (this.getFluidAPIVersion() != null) {
            returnVal.put(JSONMapping.FLUID_API_VERSION, this.getFluidAPIVersion());
        }
        if (this.getFluidWebKitVersion() != null) {
            returnVal.put(JSONMapping.FLUID_WEBKIT_VERSION, this.getFluidWebKitVersion());
        }
        if (this.getInternalTimeZone() != null)
            returnVal.put(JSONMapping.INTERNAL_TIMEZONE, this.getInternalTimeZone());
        if (this.getConnectObtainDurationMillis() != null) {
            returnVal.put(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, this.getConnectObtainDurationMillis());
        }

        return returnVal;
    }
}
