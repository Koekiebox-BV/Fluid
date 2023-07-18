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
 * Connection status for a Fluid instance cache.
 *
 * @author jasonbruwer on 2023-06-20.
 * @since 1.13
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
@NoArgsConstructor
public class CacheHealth extends ABaseFluidJSONObject {
	private String type;
	private String uri;
	private Health cacheHealth;
	private String connectionInfo;
	private Long connectObtainDurationMillis;

	/**
	 * The JSON mapping for the {@code CacheHealth} object.
	 */
	public static class JSONMapping {
		public static final String TYPE = "type";
		public static final String URI = "uri";
		public static final String CACHE_HEALTH = "cacheHealth";
		public static final String CONNECTION_INFO = "connectionInfo";
		public static final String CONNECT_OBTAIN_DURATION_MILLIS = "connectObtainDurationMillis";
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObject The JSON Object.
	 */
	public CacheHealth(JSONObject jsonObject) {
		super(jsonObject);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.TYPE)) {
			this.setType(this.jsonObject.getString(JSONMapping.TYPE));
		}

		if (!this.jsonObject.isNull(JSONMapping.URI)) {
			this.setUri(this.jsonObject.getString(JSONMapping.URI));
		}

		if (!this.jsonObject.isNull(JSONMapping.CACHE_HEALTH)) {
			this.setCacheHealth(this.jsonObject.getEnum(Health.class, JSONMapping.CACHE_HEALTH));
		}

		if (!this.jsonObject.isNull(JSONMapping.CONNECTION_INFO)) {
			this.setConnectionInfo(this.jsonObject.getString(JSONMapping.CONNECTION_INFO));
		}

		if (!this.jsonObject.isNull(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS)) {
			this.setConnectObtainDurationMillis(this.jsonObject.getLong(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code CacheHealth}.
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		if (this.getType() != null) returnVal.put(JSONMapping.TYPE, this.getType());
		if (this.getUri() != null) returnVal.put(JSONMapping.URI, this.getType());
		if (this.getCacheHealth() != null) returnVal.put(JSONMapping.CACHE_HEALTH, this.getCacheHealth());
		if (this.getConnectionInfo() != null) returnVal.put(JSONMapping.CONNECTION_INFO, this.getConnectionInfo());

		if (this.getConnectObtainDurationMillis() != null) {
			returnVal.put(JSONMapping.CONNECT_OBTAIN_DURATION_MILLIS, this.getConnectObtainDurationMillis());
		}

		return returnVal;
	}
}
