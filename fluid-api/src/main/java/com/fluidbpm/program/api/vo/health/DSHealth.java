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
public class DSHealth extends ABaseFluidGSONObject {
    private String name;
    private String driver;
    private String uri;
    private String username;
    private Health databaseHealth;
    private String connectionInfo;
    private Long connectObtainDurationMillis;

    /**
     * The JSON mapping for the {@code ConnectStatus} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String DRIVER = "driver";
        public static final String URI = "uri";
        public static final String USERNAME = "username";
        public static final String DATABASE_HEALTH = "databaseHealth";
        public static final String CONNECTION_INFO = "connectionInfo";
        public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public DSHealth(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setDriver(this.getAsStringNullSafe(JSONMapping.DRIVER));
        this.setUri(this.getAsStringNullSafe(JSONMapping.URI));
        this.setConnectionInfo(this.getAsStringNullSafe(JSONMapping.CONNECTION_INFO));

        String dbHealth = this.getAsStringNullSafe(JSONMapping.DATABASE_HEALTH);
        if (UtilGlobal.isNotBlank(dbHealth)) {
            this.setDatabaseHealth(Health.valueOf(dbHealth));
        }

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

        this.setAsProperty(JSONMapping.NAME, returnVal, this.getName());
        this.setAsProperty(JSONMapping.DRIVER, returnVal, this.getDriver());
        this.setAsProperty(JSONMapping.URI, returnVal, this.getUri());
        this.setAsProperty(JSONMapping.CONNECTION_INFO, returnVal, this.getConnectionInfo());
        this.setAsProperty(JSONMapping.DATABASE_HEALTH, returnVal, this.getDatabaseHealth());
        this.setAsProperty(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, returnVal, this.getConnectObtainDurationMillis());

        return returnVal;
    }
}
