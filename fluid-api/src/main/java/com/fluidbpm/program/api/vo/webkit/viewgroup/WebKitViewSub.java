/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2020] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.webkit.viewgroup;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see com.fluidbpm.program.api.vo.flow.JobView
 */
@Getter
@Setter
public class WebKitViewSub extends ABaseFluidJSONObject {
	/*
	 * data_table - Table
	 * data_view_list - See https://www.primefaces.org/rain/list.xhtml
	 * data_view_grid
	 */
	private String listingMode;
	private String label;
	private String icon;

	private int subOrder = 1;

	private List<JobView> jobViews;
	private List<WebKitWorkspaceRouteField> routeFields;

	private boolean tableExpansionDisplayAncestor;
	private boolean tableExpansionDisplayDescendant;
	private boolean tableExpansionDisplayRecords;
	private boolean tableExpansionDisplayRecordsInlineEdit;
	private boolean tableExpansionDisplayAttachments;
	private boolean tableExpansionDisplayFlowHistory;

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String LISTING_MODE = "listingMode";
		public static final String LABEL = "label";
		public static final String SUB_ORDER = "subOrder";
		public static final String TABLE_EXPANSION_DISPLAY_ANCESTOR = "tableExpansionDisplayAncestor";
		public static final String TABLE_EXPANSION_DISPLAY_DESCENDANT = "tableExpansionDisplayDescendant";
		public static final String TABLE_EXPANSION_DISPLAY_RECORDS = "tableExpansionDisplayRecords";
		public static final String TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT = "tableExpansionDisplayRecordsInlineEdit";
		public static final String TABLE_EXPANSION_DISPLAY_ATTACHMENTS = "tableExpansionDisplayAttachments";
		public static final String TABLE_EXPANSION_DISPLAY_FLOW_HISTORY = "tableExpansionDisplayFlowHistory";
		public static final String JOB_VIEWS = "jobViews";
		public static final String ROUTE_FIELDS = "routeFields";
	}

	public WebKitViewSub() {
		this(new JSONObject());
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitViewSub(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.LISTING_MODE)) {
			this.setListingMode(this.jsonObject.getString(JSONMapping.LISTING_MODE));
		}

		if (!this.jsonObject.isNull(JSONMapping.LABEL)) {
			this.setLabel(this.jsonObject.getString(JSONMapping.LABEL));
		}

		if (!this.jsonObject.isNull(JSONMapping.SUB_ORDER)) {
			this.setSubOrder(this.jsonObject.getInt(JSONMapping.SUB_ORDER));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_ANCESTOR)) {
			this.setTableExpansionDisplayAncestor(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_ANCESTOR));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_DESCENDANT)) {
			this.setTableExpansionDisplayDescendant(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_DESCENDANT));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS)) {
			this.setTableExpansionDisplayRecords(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT)) {
			this.setTableExpansionDisplayRecordsInlineEdit(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_ATTACHMENTS)) {
			this.setTableExpansionDisplayAttachments(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_ATTACHMENTS));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_EXPANSION_DISPLAY_FLOW_HISTORY)) {
			this.setTableExpansionDisplayFlowHistory(this.jsonObject.getBoolean(JSONMapping.TABLE_EXPANSION_DISPLAY_FLOW_HISTORY));
		}

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEWS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.JOB_VIEWS);
			List<JobView> objs = new ArrayList();
			for (int index = 0;index < jsonArray.length();index++) {
				objs.add(new JobView(jsonArray.getJSONObject(index)));
			}
			this.setJobViews(objs);
		}

		if (!this.jsonObject.isNull(JSONMapping.ROUTE_FIELDS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.ROUTE_FIELDS);
			List<WebKitWorkspaceRouteField> objs = new ArrayList();
			for (int index = 0;index < jsonArray.length();index++) {
				objs.add(new WebKitWorkspaceRouteField(jsonArray.getJSONObject(index)));
			}
			this.setRouteFields(objs);
		}
	}

	/**
	 * Returns the local JSON object.
	 * Only set through constructor.
	 *
	 * @return The local set {@code JSONObject} object.
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonObject() {
		JSONObject returnVal = super.toJsonObject();

		if (this.getLabel() != null) {
			returnVal.put(JSONMapping.LABEL,this.getLabel());
		}

		returnVal.put(JSONMapping.SUB_ORDER,this.getSubOrder());

		if (this.getListingMode() != null) {
			returnVal.put(JSONMapping.LISTING_MODE, this.getListingMode());
		}

		if (this.getJobViews() != null && !this.getJobViews().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (JobView toAdd :this.getJobViews()) {
				JobView reducedJobView = new JobView(toAdd.getId());
				jsonArray.put(reducedJobView.toJsonObject());
			}
			returnVal.put(JSONMapping.JOB_VIEWS, jsonArray);
		}

		if (this.getRouteFields() != null && !this.getRouteFields().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (WebKitWorkspaceRouteField toAdd : this.getRouteFields()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROUTE_FIELDS, jsonArray);
		}

		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_ANCESTOR, this.isTableExpansionDisplayAncestor());
		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_ATTACHMENTS, this.isTableExpansionDisplayAttachments());
		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_DESCENDANT, this.isTableExpansionDisplayDescendant());
		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_FLOW_HISTORY, this.isTableExpansionDisplayFlowHistory());
		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS, this.isTableExpansionDisplayRecords());
		returnVal.put(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT, this.isTableExpansionDisplayRecordsInlineEdit());

		return returnVal;
	}

	/**
	 * Return the Text representation of {@code this} object.
	 *
	 * @return JSON body of {@code this} object.
	 */
	@Override
	@XmlTransient
	public String toString() {
		return super.toString();
	}
}
