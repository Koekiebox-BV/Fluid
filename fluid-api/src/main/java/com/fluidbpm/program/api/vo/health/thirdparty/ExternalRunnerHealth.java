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
 * forbidden unless prior written permission is obtained from Koekiebox Innovations.
 */

package com.fluidbpm.program.api.vo.health.thirdparty;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.health.Health;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Custom runner packet that will provide destination environment info.
 *
 * @author jasonbruwer on 8/31/17.
 * @since 1.2
 */
@Getter
@Setter
@NoArgsConstructor
public class ExternalRunnerHealth extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String fluidAPIVersion;
    private Long timestamp;
    private String secretInstanceUUID;
    private Health runnerHealth;
    private String connectionInfo;
    private String uri;
    private Long connectObtainDurationMillis;

    private List<CustomRunnerAction> customRunnerActions;

    /**
     * The JSON mapping for the {@code CustomRunnerConfig} object.
     */
    public static class JSONMapping {
        public static final String FLUID_API_VERSION = "fluidAPIVersion";
        public static final String CUSTOM_RUNNER_ACTIONS = "customRunnerActions";
        public static final String TIMESTAMP = "timestamp";
        public static final String SECRET_INSTANCE_UUID = "secretInstanceUUID";
        public static final String RUNNER_HEALTH = "runnerHealth";
        public static final String CONNECTION_INFO = "connectionInfo";
        public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
        public static final String URI = "uri";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ExternalRunnerHealth(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFluidAPIVersion(this.getAsStringNullSafe(JSONMapping.FLUID_API_VERSION));
        this.setSecretInstanceUUID(this.getAsStringNullSafe(JSONMapping.SECRET_INSTANCE_UUID));
        this.setTimestamp(this.getAsLongNullSafe(JSONMapping.TIMESTAMP));
        String runnerHealthStr = this.getAsStringNullSafe(JSONMapping.RUNNER_HEALTH);
        if (UtilGlobal.isNotBlank(runnerHealthStr)) {
            this.setRunnerHealth(Health.valueOf(runnerHealthStr));
        }
        this.setConnectionInfo(this.getAsStringNullSafe(JSONMapping.CONNECTION_INFO));
        this.setUri(this.getAsStringNullSafe(JSONMapping.URI));
        this.setConnectObtainDurationMillis(this.getAsLongNullSafe(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
        this.setCustomRunnerActions(this.extractObjects(JSONMapping.CUSTOM_RUNNER_ACTIONS, CustomRunnerAction::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code ExternalRunnerHealth}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.FLUID_API_VERSION, returnVal, this.getFluidAPIVersion());
        this.setAsProperty(JSONMapping.SECRET_INSTANCE_UUID, returnVal, this.getSecretInstanceUUID());
        this.setAsProperty(JSONMapping.TIMESTAMP, returnVal, this.getTimestamp());
        this.setAsProperty(JSONMapping.RUNNER_HEALTH, returnVal, this.getRunnerHealth());
        this.setAsProperty(JSONMapping.CONNECTION_INFO, returnVal, this.getConnectionInfo());
        this.setAsProperty(JSONMapping.URI, returnVal, this.getUri());
        this.setAsProperty(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, returnVal, this.getConnectObtainDurationMillis());
        this.setAsObjArray(JSONMapping.CUSTOM_RUNNER_ACTIONS, returnVal, this::getCustomRunnerActions);

        return returnVal;
    }
}
