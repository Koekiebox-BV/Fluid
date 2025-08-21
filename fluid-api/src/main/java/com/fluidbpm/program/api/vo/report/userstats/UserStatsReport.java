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
import java.util.List;

/**
 * User statistics report.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 * 
 * @see ABaseFluidJSONObject
 * @see ViewOpenedAndSentOnEntry
 * @see PunchCardEntry
 * @see CreateUpdateLockUnlockEntry
 */
@Getter
@Setter
public class UserStatsReport extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	private List<PunchCardEntry> punchCardEntries;
	private List<ViewOpenedAndSentOnEntry> viewOpenedAndSentOnEntries;
	private List<CreateUpdateLockUnlockEntry> createUpdateLockUnlockEntries;

	private int numberOfLogins;
	private int numberOfLoginsPrevCycle;
	private int piCount;
	private int piLockedCount;

	/**
	 * The JSON mapping for the {@code UserStatsReport} object.
	 */
	public static class JSONMapping {
		public static final String PUNCH_CARD_ENTRIES = "punchCardEntries";
		public static final String VIEW_OPENED_AND_SENT_ON_ENTRIES = "viewOpenedAndSentOnEntries";
		public static final String CREATE_UPDATE_LOCK_UNLOCK_ENTRIES = "createUpdateLockUnlockEntries";


		public static final String NUMBER_OF_LOGINS = "numberOfLogins";
		public static final String NUMBER_OF_LOGINS_PREV_CYCLE = "numberOfLoginsPrevCycle";
		public static final String PI_COUNT = "piCount";
		public static final String PI_LOCKED_COUNT = "piLockedCount";
	}

	/**
	 * Default constructor.
	 */
	public UserStatsReport() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public UserStatsReport(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) {
			return;
		}

		if (this.jsonObject.isNull(JSONMapping.NUMBER_OF_LOGINS)) {
			this.setNumberOfLogins(0);
		} else {
			this.setNumberOfLogins(this.jsonObject.getInt(JSONMapping.NUMBER_OF_LOGINS));
		}

		if (this.jsonObject.isNull(JSONMapping.NUMBER_OF_LOGINS_PREV_CYCLE)) {
			this.setNumberOfLoginsPrevCycle(0);
		} else {
			this.setNumberOfLoginsPrevCycle(this.jsonObject.getInt(JSONMapping.NUMBER_OF_LOGINS_PREV_CYCLE));
		}

		if (this.jsonObject.isNull(JSONMapping.PI_COUNT)) {
			this.setPiCount(0);
		} else {
			this.setPiCount(this.jsonObject.getInt(JSONMapping.PI_COUNT));
		}

		if (this.jsonObject.isNull(JSONMapping.PI_LOCKED_COUNT)) {
			this.setPiLockedCount(0);
		} else {
			this.setPiLockedCount(this.jsonObject.getInt(JSONMapping.PI_LOCKED_COUNT));
		}

		if (!this.jsonObject.isNull(JSONMapping.PUNCH_CARD_ENTRIES)) {
			JsonArray listingArray = this.jsonObject.getJSONArray(JSONMapping.PUNCH_CARD_ENTRIES);
			List<PunchCardEntry> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new PunchCardEntry(listingArray.getJSONObject(index)));
			}
			this.setPunchCardEntries(listing);
		}

		if (!this.jsonObject.isNull(JSONMapping.VIEW_OPENED_AND_SENT_ON_ENTRIES)) {
			JsonArray listingArray = this.jsonObject.getJSONArray(JSONMapping.VIEW_OPENED_AND_SENT_ON_ENTRIES);
			List<ViewOpenedAndSentOnEntry> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new ViewOpenedAndSentOnEntry(listingArray.getJSONObject(index)));
			}
			this.setViewOpenedAndSentOnEntries(listing);
		}

		if (!this.jsonObject.isNull(JSONMapping.CREATE_UPDATE_LOCK_UNLOCK_ENTRIES)) {
			JsonArray listingArray = this.jsonObject.getJSONArray(JSONMapping.CREATE_UPDATE_LOCK_UNLOCK_ENTRIES);
			List<CreateUpdateLockUnlockEntry> listing = new ArrayList();
			for (int index = 0;index < listingArray.length();index++) {
				listing.add(new CreateUpdateLockUnlockEntry(listingArray.getJSONObject(index)));
			}
			this.setCreateUpdateLockUnlockEntries(listing);
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code UserStatsReport}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		returnVal.put(JSONMapping.NUMBER_OF_LOGINS, this.getNumberOfLogins());
		returnVal.put(JSONMapping.NUMBER_OF_LOGINS_PREV_CYCLE, this.getNumberOfLoginsPrevCycle());
		returnVal.put(JSONMapping.PI_COUNT, this.getPiCount());
		returnVal.put(JSONMapping.PI_LOCKED_COUNT, this.getPiLockedCount());

		if (this.getPunchCardEntries() != null && !this.getPunchCardEntries().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (PunchCardEntry toAdd :this.getPunchCardEntries()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.PUNCH_CARD_ENTRIES, jsonArray);
		}

		if (this.getViewOpenedAndSentOnEntries() != null && !this.getViewOpenedAndSentOnEntries().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (ViewOpenedAndSentOnEntry toAdd : this.getViewOpenedAndSentOnEntries()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.VIEW_OPENED_AND_SENT_ON_ENTRIES, jsonArray);
		}

		if (this.getCreateUpdateLockUnlockEntries() != null && !this.getCreateUpdateLockUnlockEntries().isEmpty()) {
			JsonArray jsonArray = new JsonArray();
			for (CreateUpdateLockUnlockEntry toAdd : this.getCreateUpdateLockUnlockEntries()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.CREATE_UPDATE_LOCK_UNLOCK_ENTRIES, jsonArray);
		}

		return returnVal;
	}
}
