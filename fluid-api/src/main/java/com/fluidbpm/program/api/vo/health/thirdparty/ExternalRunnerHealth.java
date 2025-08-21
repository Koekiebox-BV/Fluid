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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.health.Health;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
public class ExternalRunnerHealth extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

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

        //Flow Item Execute result...
        if (!this.jsonObject.isNull(JSONMapping.FLUID_API_VERSION)) {
            this.setFluidAPIVersion(this.jsonObject.getString(JSONMapping.FLUID_API_VERSION));
        }

        //Instance secret...
        if (!this.jsonObject.isNull(JSONMapping.SECRET_INSTANCE_UUID)) {
            this.setSecretInstanceUUID(this.jsonObject.getString(JSONMapping.SECRET_INSTANCE_UUID));
        }

        //Timestamp...
        if (!this.jsonObject.isNull(JSONMapping.TIMESTAMP)) {
            this.setTimestamp(this.jsonObject.getLong(JSONMapping.TIMESTAMP));
        }

        //Runner Health...
        if (!this.jsonObject.isNull(JSONMapping.RUNNER_HEALTH)) {
            this.setRunnerHealth(this.jsonObject.getEnum(Health.class, JSONMapping.RUNNER_HEALTH));
        }

        //Connection Info...
        if (!this.jsonObject.isNull(JSONMapping.CONNECTION_INFO)) {
            this.setConnectionInfo(this.jsonObject.getString(JSONMapping.CONNECTION_INFO));
        }

        //URI...
        if (!this.jsonObject.isNull(JSONMapping.URI)) {
            this.setUri(this.jsonObject.getString(JSONMapping.URI));
        }

        //Duration...
        if (!this.jsonObject.isNull(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS)) {
            this.setConnectObtainDurationMillis(this.jsonObject.getLong(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
        }

        //Custom Runner Actions...
        if (!this.jsonObject.isNull(JSONMapping.CUSTOM_RUNNER_ACTIONS)) {
            JsonArray jsonPropArray = this.jsonObject.getJSONArray(JSONMapping.CUSTOM_RUNNER_ACTIONS);
            List<CustomRunnerAction> customerRunnerActions = new ArrayList();
            for (int index = 0;index < jsonPropArray.length();index++) {
                customerRunnerActions.add(new CustomRunnerAction(jsonPropArray.getJSONObject(index)));
            }

            this.setCustomRunnerActions(customerRunnerActions);
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FlowItemExecutePacket}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        //Fluid API Version...
        if (this.getFluidAPIVersion() != null) {
            returnVal.put(JSONMapping.FLUID_API_VERSION, this.getFluidAPIVersion());
        }

        //Secret Instance UUID...
        if (this.getSecretInstanceUUID() != null) {
            returnVal.put(JSONMapping.SECRET_INSTANCE_UUID, this.getSecretInstanceUUID());
        }

        //Timestamp...
        if (this.getTimestamp() != null) returnVal.put(JSONMapping.TIMESTAMP, this.getTimestamp());
        //Runner Health...
        if (this.getRunnerHealth() != null) returnVal.put(JSONMapping.RUNNER_HEALTH, this.getRunnerHealth());
        //Connection Info...
        if (this.getConnectionInfo() != null) returnVal.put(JSONMapping.CONNECTION_INFO, this.getConnectionInfo());
        //URI...
        if (this.getUri() != null) returnVal.put(JSONMapping.URI, this.getUri());
        //Connection Obtain Duration...
        if (this.getConnectObtainDurationMillis() != null) {
            returnVal.put(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, this.getConnectObtainDurationMillis());
        }

        //Custom Runner Actions...
        if (this.getCustomRunnerActions() != null && !this.getCustomRunnerActions().isEmpty()) {
            JsonArray customRunnersArr = new JsonArray();
            for (CustomRunnerAction toAdd :this.getCustomRunnerActions()) {
                customRunnersArr.put(toAdd.toJsonObject());
            }
            returnVal.put(JSONMapping.CUSTOM_RUNNER_ACTIONS, customRunnersArr);
        }
        return returnVal;
    }
}
