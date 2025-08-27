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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.webkit.RowExpansion;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
@EqualsAndHashCode(callSuper = false)
public class WebKitViewSub extends ABaseFluidGSONObject {
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
            if (sub.isShowColumnProgressPercentage())
                returnVal.add(VisibleColumnItems.showColumnProgressPercentage.name());
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
        this(new JsonObject());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitViewSub(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setListingMode(this.getAsStringNullSafe(JSONMapping.LISTING_MODE));
        this.setIcon(this.getAsStringNullSafe(JSONMapping.ICON));
        this.setLabel(this.getAsStringNullSafe(JSONMapping.LABEL));
        this.setSubOrder(this.getAsIntegerNullSafeStrictVal(JSONMapping.SUB_ORDER));
        this.setShowColumnFormType(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_FORM_TYPE));
        this.setShowColumnID(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ID));
        this.setShowColumnTitle(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_TITLE));
        this.setShowColumnStepEntryTime(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME));
        this.setShowColumnDateCreated(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_CREATED));
        this.setShowColumnDateLastUpdated(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED));
        this.setShowColumnCurrentFlow(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_FLOW));
        this.setShowColumnCurrentStep(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_STEP));
        this.setShowColumnCurrentView(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_VIEW));
        this.setShowColumnProgressPercentage(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE));
        this.setShowColumnAttachment(this.getAsBooleanNullSafe(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ATTACHMENT));

        this.setRowExpansion(this.extractObject(JSONMapping.ROW_EXPANSION, RowExpansion::new));
        this.setJobViews(this.extractObjects(JSONMapping.JOB_VIEWS, WebKitWorkspaceJobView::new));
        this.setRouteFields(this.extractObjects(JSONMapping.ROUTE_FIELDS, WebKitWorkspaceRouteField::new));
    }

    /**
     * Returns the local JSON object.
     * Only set through constructor.
     *
     * @return The local set {@code JSONObject} object.
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.LABEL, returnVal, this.getLabel());
        this.setAsProperty(JSONMapping.ICON, returnVal, this.getIcon());
        this.setAsProperty(JSONMapping.SUB_ORDER, returnVal, this.getSubOrder());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ID, returnVal, this.isShowColumnID());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_FORM_TYPE, returnVal, this.isShowColumnFormType());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_TITLE, returnVal, this.isShowColumnTitle());

        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME, returnVal, this.isShowColumnStepEntryTime());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_CREATED, returnVal, this.isShowColumnDateCreated());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED, returnVal, this.isShowColumnDateLastUpdated());

        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_FLOW, returnVal, this.isShowColumnCurrentFlow());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_STEP, returnVal, this.isShowColumnCurrentStep());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_CURRENT_VIEW, returnVal, this.isShowColumnCurrentView());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE, returnVal, this.isShowColumnProgressPercentage());
        this.setAsProperty(WebKitViewGroup.JSONMapping.SHOW_COLUMN_ATTACHMENT, returnVal, this.isShowColumnAttachment());
        this.setAsProperty(JSONMapping.LISTING_MODE, returnVal, this.getListingMode());
        this.setAsObjArray(JSONMapping.JOB_VIEWS, returnVal, this::getJobViews);
        this.setAsObjArray(JSONMapping.ROUTE_FIELDS, returnVal, this::getRouteFields);
        this.setAsObj(JSONMapping.ROW_EXPANSION, returnVal, this::getRowExpansion);

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
     * @return JSON body of {@code this} object.
     */
    @Override
    @XmlTransient
    public String toString() {
        return super.toString();
    }
}
