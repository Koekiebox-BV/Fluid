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

package com.fluidbpm.program.api.vo.report.userstats;

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
public class ViewOpenedAndSentOnEntry extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	private String viewName;
	private int viewClicks;
	private int openedFromViewCounts;
	private int sentOn;

	/**
	 * The JSON mapping for the {@code UserStatsReport} object.
	 */
	public static class JSONMapping {
		public static final String VIEW_NAME = "viewName";
		public static final String VIEW_CLICKS = "viewClicks";
		public static final String OPENED_FROM_VIEW_COUNTS = "openedFromViewCounts";
		public static final String SENT_ON = "sentOn";
	}

	/**
	 * Default constructor.
	 */
	public ViewOpenedAndSentOnEntry() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public ViewOpenedAndSentOnEntry(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (this.jsonObject.isNull(JSONMapping.VIEW_NAME)) {
			this.setViewName(null);
		} else {
			this.setViewName(this.jsonObject.getString(JSONMapping.VIEW_NAME));
		}

		if (this.jsonObject.isNull(JSONMapping.VIEW_CLICKS)) {
			this.setViewClicks(0);
		} else {
			this.setViewClicks(this.jsonObject.getInt(JSONMapping.VIEW_CLICKS));
		}

		if (this.jsonObject.isNull(JSONMapping.OPENED_FROM_VIEW_COUNTS)) {
			this.setOpenedFromViewCounts(0);
		} else {
			this.setOpenedFromViewCounts(this.jsonObject.getInt(JSONMapping.OPENED_FROM_VIEW_COUNTS));
		}

		if (this.jsonObject.isNull(JSONMapping.SENT_ON)) {
			this.setSentOn(0);
		} else {
			this.setSentOn(this.jsonObject.getInt(JSONMapping.SENT_ON));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code ViewOpenedAndSentOnEntry}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		if (this.getViewName() != null) {
			returnVal.put(JSONMapping.VIEW_NAME, this.getViewName());
		}

		returnVal.put(JSONMapping.VIEW_CLICKS, this.getViewClicks());
		returnVal.put(JSONMapping.OPENED_FROM_VIEW_COUNTS, this.getOpenedFromViewCounts());
		returnVal.put(JSONMapping.SENT_ON, this.getSentOn());

		return returnVal;
	}
}
