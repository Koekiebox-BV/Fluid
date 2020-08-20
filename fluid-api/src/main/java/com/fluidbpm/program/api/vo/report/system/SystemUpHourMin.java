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
import org.json.JSONException;
import org.json.JSONObject;

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
public class SystemUpHourMin extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	private int hour;
	private int minute;

	/**
	 * The JSON mapping for the {@code SystemUpHourMin} object.
	 */
	public static class JSONMapping {
		public static final String HOUR = "hour";
		public static final String MINUTE = "minute";
	}

	/**
	 * Default constructor.
	 */
	public SystemUpHourMin() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public SystemUpHourMin(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (this.jsonObject.isNull(JSONMapping.HOUR)) {
			this.setHour(0);
		} else {
			this.setHour(this.jsonObject.getInt(JSONMapping.HOUR));
		}

		if (this.jsonObject.isNull(JSONMapping.MINUTE)) {
			this.setMinute(0);
		} else {
			this.setMinute(this.jsonObject.getInt(JSONMapping.MINUTE));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code SystemUpHourMin}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		returnVal.put(JSONMapping.HOUR, this.getHour());
		returnVal.put(JSONMapping.MINUTE, this.getMinute());

		return returnVal;
	}
}
