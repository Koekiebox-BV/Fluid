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
import com.fluidbpm.program.api.vo.health.thirdparty.ExternalRunnerHealth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Extended server info status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @since 1.13
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
@NoArgsConstructor
public class ExtendedServerHealth extends ABaseFluidJSONObject {
	private Date timestampHealthStart;
	private Date timestampHealthEnd;

	private List<DSHealth> dsHealth;
	private CacheHealth cacheHealth;
	private ESHealth esHealth;
	private List<DSHealth> smtpHealth;
	private List<DSHealth> imapPopHealth;
	private ExternalRunnerHealth externalRunnerHealth;
	private DSHealth contentStorageHealth;

	private DSHealth raygunHealth;
	private DSHealth googleMapsApiHealth;

	/**
	 * The JSON mapping for the {@code ExtendedServerHealth} object.
	 */
	public static class JSONMapping {
		public static final String TIMESTAMP_START = "timestampHealthStart";
		public static final String TIMESTAMP_END = "timestampHealthEnd";
		public static final String DS_HEALTH = "dsHealth";
		public static final String CACHE_HEALTH = "cacheHealth";
		public static final String ES_HEALTH = "esHealth";
		public static final String SMTP_HEALTH = "smtpHealth";
		public static final String IMAP_HEALTH = "imapPopHealth";
		public static final String EXT_RUNNER_HEALTH = "externalRunnerHealth";
		public static final String CONTENT_HEALTH = "contentStorageHealth";
		public static final String RAYGUN_HEALTH = "raygunHealth";
		public static final String GOOGLE_MAPS_API_HEALTH = "googleMapsApiHealth";
	}

	/**
	 * Populates local variables with {@code jsonObject}.
	 *
	 * @param jsonObject The JSON Object.
	 */
	public ExtendedServerHealth(JsonObject jsonObject) {
		super(jsonObject);
		if (this.jsonObject == null) return;

		this.setTimestampHealthStart(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP_START));
		this.setTimestampHealthEnd(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP_END));

		if (!this.jsonObject.isNull(JSONMapping.DS_HEALTH)) {
			JsonArray arr = this.jsonObject.getJSONArray(JSONMapping.DS_HEALTH);
			List<DSHealth> listOfItems = new ArrayList();
			for (int index = 0; index < arr.length(); index++) {
				listOfItems.add(new DSHealth(arr.getJSONObject(index)));
			}
			this.setDsHealth(listOfItems);
		}

		if (!this.jsonObject.isNull(JSONMapping.CACHE_HEALTH)) {
			this.setCacheHealth(new CacheHealth(this.jsonObject.getJSONObject(JSONMapping.CACHE_HEALTH)));
		}

		if (!this.jsonObject.isNull(JSONMapping.ES_HEALTH)) {
			this.setEsHealth(new ESHealth(this.jsonObject.getJSONObject(JSONMapping.ES_HEALTH)));
		}

		if (!this.jsonObject.isNull(JSONMapping.EXT_RUNNER_HEALTH)) {
			this.setExternalRunnerHealth(new ExternalRunnerHealth(this.jsonObject.getJSONObject(JSONMapping.EXT_RUNNER_HEALTH)));
		}

		if (!this.jsonObject.isNull(JSONMapping.SMTP_HEALTH)) {
			JsonArray arr = this.jsonObject.getJSONArray(JSONMapping.SMTP_HEALTH);
			/*List<SmtpDSHealth> listOfItems = new ArrayList();
			for (int index = 0; index < arr.length(); index++) {
				listOfItems.add(new DSHealth(arr.getJSONObject(index)));
			}
			this.setDsHealth(listOfItems);*/
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code ExtendedServerHealth}.
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		if (this.getTimestampHealthStart() != null) {
			returnVal.put(JSONMapping.TIMESTAMP_START, this.getDateAsObjectFromJson(this.getTimestampHealthStart()));
		}
		if (this.getTimestampHealthEnd() != null) {
			returnVal.put(JSONMapping.TIMESTAMP_END, this.getDateAsObjectFromJson(this.getTimestampHealthEnd()));
		}
		if (this.getDsHealth() != null) {
			JsonArray arr = new JsonArray();
			this.getDsHealth().forEach(itm -> arr.put(itm.toJsonObject()));
			returnVal.put(JSONMapping.DS_HEALTH, arr);
		}
		if (this.getCacheHealth() != null) {
			returnVal.put(JSONMapping.CACHE_HEALTH, this.getCacheHealth().toJsonObject());
		}
		if (this.getEsHealth() != null) {
			returnVal.put(JSONMapping.ES_HEALTH, this.getEsHealth().toJsonObject());
		}
		if (this.getExternalRunnerHealth() != null) {
			returnVal.put(JSONMapping.EXT_RUNNER_HEALTH, this.getExternalRunnerHealth().toJsonObject());
		}
		if (this.getSmtpHealth() != null) {
			JsonArray arr = new JsonArray();
			this.getSmtpHealth().forEach(itm -> arr.put(itm.toJsonObject()));
			returnVal.put(JSONMapping.SMTP_HEALTH, arr);
		}
		return returnVal;
	}
}
