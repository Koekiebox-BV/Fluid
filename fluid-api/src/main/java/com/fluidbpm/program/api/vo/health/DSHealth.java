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

/**
 * Connection status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @since 1.13
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
@NoArgsConstructor
public class DSHealth extends ABaseFluidJSONObject {
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
	public DSHealth(JSONObject jsonObject) {
		super(jsonObject);
		if (this.jsonObject == null) return;


		if (!this.jsonObject.isNull(JSONMapping.DATABASE_HEALTH)) {
			this.setDatabaseHealth(this.jsonObject.getEnum(Health.class, JSONMapping.DATABASE_HEALTH));
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
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		if (this.getDatabaseHealth() != null) {
			returnVal.put(JSONMapping.DATABASE_HEALTH, this.getDatabaseHealth());
		}
		
		if (this.getConnectObtainDurationMillis() != null) {
			returnVal.put(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, this.getConnectObtainDurationMillis());
		}

		return returnVal;
	}
}
