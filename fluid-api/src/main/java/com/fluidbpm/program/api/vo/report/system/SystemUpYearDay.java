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
import com.fluidbpm.program.api.vo.report.ABaseFluidJSONReportObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User statistics.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 * 
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class SystemUpYearDay extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;
	private int year;
	private int day;
	private List<SystemUpHourMin> systemUpHourMins;

	/**
	 * The JSON mapping for the {@code SystemUpYearDay} object.
	 */
	public static class JSONMapping {
		public static final String YEAR = "year";
		public static final String DAY = "day";
		public static final String SYSTEM_UP_HOUR_MINS = "systemUpHourMins";
	}

	/**
	 * Default constructor.
	 */
	public SystemUpYearDay() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public SystemUpYearDay(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (this.jsonObject.isNull(JSONMapping.YEAR)) {
			this.setYear(0);
		} else {
			this.setYear(this.jsonObject.getInt(JSONMapping.YEAR));
		}

		if (this.jsonObject.isNull(JSONMapping.DAY)) {
			this.setDay(0);
		} else {
			this.setDay(this.jsonObject.getInt(JSONMapping.DAY));
		}

		if (!this.jsonObject.isNull(JSONMapping.SYSTEM_UP_HOUR_MINS)) {
			JSONArray listingArray = this.jsonObject.getJSONArray(JSONMapping.SYSTEM_UP_HOUR_MINS);
			List<SystemUpHourMin> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new SystemUpHourMin(listingArray.getJSONObject(index)));
			}
			this.setSystemUpHourMins(listing);
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code SystemUpYearDay}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		returnVal.put(JSONMapping.YEAR, this.getYear());
		returnVal.put(JSONMapping.DAY, this.getDay());

		if (this.getSystemUpHourMins() != null && !this.getSystemUpHourMins().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (SystemUpHourMin toAdd : this.getSystemUpHourMins()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.SYSTEM_UP_HOUR_MINS, jsonArray);
		}

		return returnVal;
	}

	/**
	 * Comparing the year and day.
	 *
	 * @return {@code ((this.getYear() * 1000) + this.getDay())}
	 */
	public long comparingYearDay() {
		return ((this.getYear() * 1000) + this.getDay());
	}

	/**
	 * Returns the year and day as {@code String}.
	 *
	 * @return {@code year-day}
	 */
	public String getYearDayKey() {
		return (""+this.getYear()+"-"+this.getDay());
	}
}
