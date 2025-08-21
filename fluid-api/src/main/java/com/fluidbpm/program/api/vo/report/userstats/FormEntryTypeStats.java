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
 * User statistics for form entry types.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 * 
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class FormEntryTypeStats extends ABaseFluidJSONReportObject {
	public static final long serialVersionUID = 1L;

	private String formContainerType;

	private int countDocument;
	private int countFolder;
	private int countTableRecord;

	/**
	 * The JSON mapping for the {@code FormEntryTypeStats} object.
	 */
	public static class JSONMapping {
		public static final String FORM_CONTAINER_TYPE = "formContainerType";
		public static final String COUNT_DOCUMENT = "countDocument";
		public static final String COUNT_FOLDER = "countFolder";
		public static final String COUNT_TABLE_RECORD = "countTableRecord";
	}

	/**
	 * Default constructor.
	 */
	public FormEntryTypeStats() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FormEntryTypeStats(JsonObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (this.jsonObject.isNull(JSONMapping.FORM_CONTAINER_TYPE)) {
			this.setFormContainerType(null);
		} else {
			this.setFormContainerType(this.jsonObject.getString(JSONMapping.FORM_CONTAINER_TYPE));
		}

		if (this.jsonObject.isNull(JSONMapping.COUNT_DOCUMENT)) {
			this.setCountDocument(0);
		} else {
			this.setCountDocument(this.jsonObject.getInt(JSONMapping.COUNT_DOCUMENT));
		}

		if (this.jsonObject.isNull(JSONMapping.COUNT_FOLDER)) {
			this.setCountFolder(0);
		} else {
			this.setCountFolder(this.jsonObject.getInt(JSONMapping.COUNT_FOLDER));
		}

		if (this.jsonObject.isNull(JSONMapping.COUNT_TABLE_RECORD)) {
			this.setCountTableRecord(0);
		} else {
			this.setCountTableRecord(this.jsonObject.getInt(JSONMapping.COUNT_TABLE_RECORD));
		}

	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code PunchCardEntry}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();

		if (this.getFormContainerType() != null) {
			returnVal.put(JSONMapping.FORM_CONTAINER_TYPE, this.getFormContainerType());
		}

		returnVal.put(JSONMapping.COUNT_DOCUMENT, this.getCountDocument());
		returnVal.put(JSONMapping.COUNT_FOLDER, this.getCountFolder());
		returnVal.put(JSONMapping.COUNT_TABLE_RECORD, this.getCountTableRecord());

		return returnVal;
	}
}
