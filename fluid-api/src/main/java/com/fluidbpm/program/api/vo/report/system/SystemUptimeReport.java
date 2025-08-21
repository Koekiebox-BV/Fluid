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
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.program.api.vo.report.system;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.compress.CompressedResponse;
import com.fluidbpm.program.api.vo.report.ABaseFluidJSONReportObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * System uptime data.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 * 
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class SystemUptimeReport extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	//yyyy-DDD-kk-mm
	private List<SystemUpYearDay> uptimeEntries;
	private CompressedResponse compressedResponse;

	/**
	 * The JSON mapping for the {@code SystemUptimeReport} object.
	 */
	public static class JSONMapping {
		public static final String UPTIME_ENTRIES = "uptimeEntries";
		public static final String COMPRESSED_RESPONSE = "compressedResponse";
	}

	/**
	 * Default constructor.
	 */
	public SystemUptimeReport() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public SystemUptimeReport(JsonObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.UPTIME_ENTRIES)) {
			JsonArray listingArray = this.jsonObject.getJSONArray(JSONMapping.UPTIME_ENTRIES);
			List<SystemUpYearDay> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new SystemUpYearDay(listingArray.getJSONObject(index)));
			}
			this.setUptimeEntries(listing);
		}

		if (!this.jsonObject.isNull(JSONMapping.COMPRESSED_RESPONSE)) {
			this.setCompressedResponse(new CompressedResponse(
					this.jsonObject.getJSONObject(JSONMapping.COMPRESSED_RESPONSE)));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code SystemUptimeReport}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		if (this.getUptimeEntries() != null && !this.getUptimeEntries().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (SystemUpYearDay toAdd : this.getUptimeEntries()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.UPTIME_ENTRIES, jsonArray);
		}

		if (this.getCompressedResponse() != null) {
			returnVal.put(JSONMapping.COMPRESSED_RESPONSE, this.getCompressedResponse().toJsonObject());
		}

		return returnVal;
	}
}
