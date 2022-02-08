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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User statistics for form update/lock and unlock.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 * 
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class CreateUpdateLockUnlockEntry extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	private Date entryDay;
	private List<FormContainerTypeStats> formContainerTypeStats;
	private List<FormEntryTypeStats> formEntryTypeStats;

	/**
	 * The JSON mapping for the {@code CreateUpdateLockUnlockEntry} object.
	 */
	public static class JSONMapping {
		public static final String ENTRY_DAY = "entryDay";
		public static final String FORM_CONTAINER_TYPE_STATS = "formContainerTypeStats";
		public static final String FORM_ENTRY_TYPE_STATS = "formEntryTypeStats";
	}

	/**
	 * Default constructor.
	 */
	public CreateUpdateLockUnlockEntry() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public CreateUpdateLockUnlockEntry(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}
		this.setEntryDay(this.getDateFieldValueFromFieldWithName(JSONMapping.ENTRY_DAY));

		if (!this.jsonObject.isNull(JSONMapping.FORM_CONTAINER_TYPE_STATS)) {
			JSONArray listingArray = this.jsonObject.getJSONArray(JSONMapping.FORM_CONTAINER_TYPE_STATS);
			List<FormContainerTypeStats> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new FormContainerTypeStats(listingArray.getJSONObject(index)));
			}
			this.setFormContainerTypeStats(listing);
		}

		if (!this.jsonObject.isNull(JSONMapping.FORM_ENTRY_TYPE_STATS)) {
			JSONArray listingArray = this.jsonObject.getJSONArray(JSONMapping.FORM_ENTRY_TYPE_STATS);
			List<FormEntryTypeStats> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new FormEntryTypeStats(listingArray.getJSONObject(index)));
			}
			this.setFormEntryTypeStats(listing);
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code CreateUpdateLockUnlockEntry}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		if (this.getEntryDay() != null) {
			returnVal.put(JSONMapping.ENTRY_DAY,
					this.getDateAsObjectFromJson(this.getEntryDay()));
		}

		if (this.getFormContainerTypeStats() != null && !this.getFormContainerTypeStats().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (FormContainerTypeStats toAdd : this.getFormContainerTypeStats()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.FORM_CONTAINER_TYPE_STATS, jsonArray);
		}

		if (this.getFormEntryTypeStats() != null && !this.getFormEntryTypeStats().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (FormEntryTypeStats toAdd : this.getFormEntryTypeStats()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.FORM_ENTRY_TYPE_STATS, jsonArray);
		}

		return returnVal;
	}
}
