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
	private String icon = "pi pi-table";

	private RowExpansion rowExpansion;

	private boolean showColumnID;
	private boolean showColumnFormType;
	private boolean showColumnTitle;

	private boolean showColumnStepEntryTime;
	private boolean showColumnDateCreated;
	private boolean showColumnDateLastUpdated;

	private boolean showColumnCurrentFlow;
	private boolean showColumnCurrentStep;
	private boolean showColumnCurrentView;

	private boolean showColumnProgressPercentage;
	private boolean showColumnAttachment;

	private int subOrder = 1;

	private List<WebKitWorkspaceJobView> jobViews;
	private List<WebKitWorkspaceRouteField> routeFields;

	public enum VisibleColumnItems {
		showColumnID,
		showColumnFormType,
		showColumnTitle,
		showColumnStepEntryTime,
		showColumnDateCreated,
		showColumnDateLastUpdated,
		showColumnCurrentFlow,
		showColumnCurrentStep,
		showColumnCurrentView,
		showColumnProgressPercentage,
		showColumnAttachment;

		public static List<String> asListFrom(WebKitViewSub sub) {
			List<String> returnVal = new ArrayList<>();
			if (sub == null) return returnVal;

			if (sub.isShowColumnID()) returnVal.add(VisibleColumnItems.showColumnID.name());
			if (sub.isShowColumnFormType()) returnVal.add(VisibleColumnItems.showColumnFormType.name());
			if (sub.isShowColumnTitle()) returnVal.add(VisibleColumnItems.showColumnTitle.name());
			if (sub.isShowColumnStepEntryTime()) returnVal.add(VisibleColumnItems.showColumnStepEntryTime.name());
			if (sub.isShowColumnDateCreated()) returnVal.add(VisibleColumnItems.showColumnDateCreated.name());
			if (sub.isShowColumnDateLastUpdated()) returnVal.add(VisibleColumnItems.showColumnDateLastUpdated.name());
			if (sub.isShowColumnCurrentFlow()) returnVal.add(VisibleColumnItems.showColumnCurrentFlow.name());
			if (sub.isShowColumnCurrentStep()) returnVal.add(VisibleColumnItems.showColumnCurrentStep.name());
			if (sub.isShowColumnCurrentView()) returnVal.add(VisibleColumnItems.showColumnCurrentView.name());
			if (sub.isShowColumnProgressPercentage()) returnVal.add(VisibleColumnItems.showColumnProgressPercentage.name());
			if (sub.isShowColumnAttachment()) returnVal.add(VisibleColumnItems.showColumnAttachment.name());

			return returnVal;
		}
	}

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String LISTING_MODE = "listingMode";
		public static final String LABEL = "label";
		public static final String ICON = "icon";
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
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.LISTING_MODE)) {
			this.setListingMode(this.jsonObject.getString(JSONMapping.LISTING_MODE));
		}

		if (!this.jsonObject.isNull(JSONMapping.ICON)) {
			this.setIcon(this.jsonObject.getString(JSONMapping.ICON));
		}

		if (!this.jsonObject.isNull(JSONMapping.LABEL)) {
			this.setLabel(this.jsonObject.getString(JSONMapping.LABEL));
		}

		if (!this.jsonObject.isNull(JSONMapping.SUB_ORDER)) {
			this.setSubOrder(this.jsonObject.getInt(JSONMapping.SUB_ORDER));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_FORM_TYPE)) {
			this.setShowColumnFormType(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_FORM_TYPE));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ID)) {
			this.setShowColumnID(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ID));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_TITLE)) {
			this.setShowColumnTitle(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_TITLE));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME)) {
			this.setShowColumnStepEntryTime(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_CREATED)) {
			this.setShowColumnDateCreated(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_CREATED));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED)) {
			this.setShowColumnDateLastUpdated(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_FLOW)) {
			this.setShowColumnCurrentFlow(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_FLOW));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_STEP)) {
			this.setShowColumnCurrentStep(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_STEP));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_VIEW)) {
			this.setShowColumnCurrentView(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_VIEW));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE)) {
			this.setShowColumnProgressPercentage(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE));
		}

		if (!this.jsonObject.isNull(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ATTACHMENT)) {
			this.setShowColumnAttachment(this.jsonObject.getBoolean(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ATTACHMENT));
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
		if (this.getIcon() != null) returnVal.put(JSONMapping.ICON,this.getIcon());

		returnVal.put(JSONMapping.SUB_ORDER, this.getSubOrder());

		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ID, this.isShowColumnID());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_FORM_TYPE, this.isShowColumnFormType());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_TITLE, this.isShowColumnTitle());

		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME, this.isShowColumnStepEntryTime());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_CREATED, this.isShowColumnDateCreated());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED, this.isShowColumnDateLastUpdated());

		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_FLOW, this.isShowColumnCurrentFlow());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_STEP, this.isShowColumnCurrentStep());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_VIEW, this.isShowColumnCurrentView());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE, this.isShowColumnProgressPercentage());
		returnVal.put(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ATTACHMENT, this.isShowColumnAttachment());

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
	 * @return Visible columns as {@code List<String>}.
	 */
	@XmlTransient
	public List<String> getVisibleColumnsAsList() {
		return VisibleColumnItems.asListFrom(this);
	}

	/**
	 * Set visible columns as {@code List<String>}.
	 *
	 * @param listing The list
	 */
	@XmlTransient
	public void setVisibleColumnsAsList(List<String> listing) {
		if (listing == null) return;

		if (listing.contains(VisibleColumnItems.showColumnID.name())) {
			this.setShowColumnID(true);
		} else {
			this.setShowColumnID(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnFormType.name())) {
			this.setShowColumnFormType(true);
		} else {
			this.setShowColumnFormType(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnTitle.name())) {
			this.setShowColumnTitle(true);
		} else {
			this.setShowColumnTitle(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnStepEntryTime.name())) {
			this.setShowColumnStepEntryTime(true);
		} else {
			this.setShowColumnStepEntryTime(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnDateCreated.name())) {
			this.setShowColumnDateCreated(true);
		} else {
			this.setShowColumnDateCreated(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnDateLastUpdated.name())) {
			this.setShowColumnDateLastUpdated(true);
		} else {
			this.setShowColumnDateLastUpdated(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnCurrentFlow.name())) {
			this.setShowColumnCurrentFlow(true);
		} else {
			this.setShowColumnCurrentFlow(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnCurrentStep.name())) {
			this.setShowColumnCurrentStep(true);
		} else {
			this.setShowColumnCurrentStep(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnCurrentView.name())) {
			this.setShowColumnCurrentView(true);
		} else {
			this.setShowColumnCurrentView(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnProgressPercentage.name())) {
			this.setShowColumnProgressPercentage(true);
		} else {
			this.setShowColumnProgressPercentage(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnAttachment.name())) {
			this.setShowColumnAttachment(true);
		} else {
			this.setShowColumnAttachment(false);
		}

	
		
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
