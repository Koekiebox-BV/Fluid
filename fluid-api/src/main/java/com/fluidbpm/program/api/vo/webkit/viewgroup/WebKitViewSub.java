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
import com.fluidbpm.program.api.vo.webkit.RowExpansion;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class WebKitViewSub extends ABaseFluidJSONObject {
	/*
	 * data_table - Table
	 * data_view_list - See https://www.primefaces.org/rain/list.xhtml
	 * data_view_grid
	 */
	private String listingMode;
	private String label;
	private String icon;

	private RowExpansion rowExpansion;

	private int subOrder = 1;

	private List<WebKitWorkspaceJobView> jobViews;
	private List<WebKitWorkspaceRouteField> routeFields;


	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String LISTING_MODE = "listingMode";
		public static final String LABEL = "label";
		public static final String SUB_ORDER = "subOrder";

		public static final String ROW_EXPANSION = "rowExpansion";

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

		if (this.jsonObject.isNull(JSONMapping.ROW_EXPANSION)) this.setRowExpansion(new RowExpansion(new JSONObject()));
		else this.setRowExpansion(new RowExpansion(this.jsonObject.getJSONObject(JSONMapping.ROW_EXPANSION)));

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEWS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.JOB_VIEWS);
			List<WebKitWorkspaceJobView> objs = new ArrayList();
			for (int index = 0; index < jsonArray.length(); index++) {
				objs.add(new WebKitWorkspaceJobView(jsonArray.getJSONObject(index)));
			}
			this.setJobViews(objs);
		}

		if (!this.jsonObject.isNull(JSONMapping.ROUTE_FIELDS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.ROUTE_FIELDS);
			List<WebKitWorkspaceRouteField> objs = new ArrayList();
			for (int index = 0; index < jsonArray.length(); index++) {
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

		if (this.getLabel() != null) returnVal.put(JSONMapping.LABEL,this.getLabel());

		returnVal.put(JSONMapping.SUB_ORDER,this.getSubOrder());

		if (this.getListingMode() != null) returnVal.put(JSONMapping.LISTING_MODE, this.getListingMode());

		if (this.getJobViews() != null && !this.getJobViews().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (WebKitWorkspaceJobView toAdd :this.getJobViews()) jsonArray.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.JOB_VIEWS, jsonArray);
		}

		if (this.getRouteFields() != null && !this.getRouteFields().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (WebKitWorkspaceRouteField toAdd : this.getRouteFields()) jsonArray.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.ROUTE_FIELDS, jsonArray);
		}

		if (this.getRowExpansion() != null) returnVal.put(JSONMapping.ROW_EXPANSION, this.getRowExpansion().toJsonObject());

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
