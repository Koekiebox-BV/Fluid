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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see JobView
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WebKitWorkspaceJobView extends ABaseFluidGSONObject {
    private JobView jobView;
    private Integer fetchLimit;

    @XmlTransient
    private boolean selected;

    /**
     * The JSON mapping for the {@code WebKitWorkspaceJobView} object.
     */
    public static class JSONMapping {
        public static final String JOB_VIEW = "jobView";
        public static final String FETCH_LIMIT = "fetchLimit";
    }

    public WebKitWorkspaceJobView() {
        this(new JsonObject());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitWorkspaceJobView(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) {
            return;
        }

        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.JOB_VIEW)) {
            this.setJobView(new JobView(this.jsonObject.getAsJsonObject(JSONMapping.JOB_VIEW)));
        }

        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.FETCH_LIMIT)) {
            this.setFetchLimit(this.jsonObject.get(JSONMapping.FETCH_LIMIT).getAsInt());
        }
    }

    /**
     * @param jobView The view associated.
     * @see JobView
     */
    public WebKitWorkspaceJobView(JobView jobView) {
        this();
        this.setJobView(jobView);
    }

    /**
     * Returns the local JSON object.
     * Only set through constructor.
     *
     * @return The local set {@code JsonObject} object.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        if (this.getJobView() != null) {
            JobView reducedView = new JobView(this.getJobView().getId());
            returnVal.add(JSONMapping.JOB_VIEW, reducedView.toJsonObject());
        }

        if (this.getFetchLimit() != null) {
            returnVal.addProperty(JSONMapping.FETCH_LIMIT, this.getFetchLimit());
        }

        return returnVal;
    }

    /**
     * Return the Text representation of {@code this} object.
     *
     * @return JSON body of {@code this} object.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public String toString() {
        return super.toString();
    }

    /**
     * Return the name of the view.
     *
     * @return {@code this.getJobView().getViewName()}
     */
    @XmlTransient
    @JsonIgnore
    public String getViewStepName() {
        return (this.getJobView() == null) ? null : this.getJobView().getViewStepName();
    }

    /**
     * Return the name of the flow.
     *
     * @return {@code this.getJobView().getViewFlowName()}
     */
    @XmlTransient
    @JsonIgnore
    public String getViewFlowName() {
        return (this.getJobView() == null) ? null : this.getJobView().getViewFlowName();
    }

    /**
     * Return the priority of the view.
     *
     * @return {@code this.getJobView().getViewPriority()}
     */
    @XmlTransient
    @JsonIgnore
    public Integer getViewPriority() {
        return (this.getJobView() == null) ? null : this.getJobView().getViewPriority();
    }

    /**
     * Return the order of the view.
     *
     * @return {@code this.getJobView().getViewOrder()}
     */
    @XmlTransient
    @JsonIgnore
    public Long getViewOrder() {
        Long viewOrder = (this.getJobView() == null) ? null : this.getJobView().getViewOrder();
        return (viewOrder == null) ? 0L : viewOrder;
    }

    /**
     * Extract the flow, step and view order.
     *
     * @return "%s_%s_%010d" FlowName StepName and ViewOrder with 10 places 0 filled.
     */
    public String getViewFlowStepViewOrder() {
        return String.format("%s_%s_%010d", this.getViewFlowName(), this.getViewStepName(), this.getViewOrder());
    }

    /**
     * Id for WebKit Job view.
     *
     * @return Primary Key of {@code jobView}.
     */
    @Override
    public Long getId() {
        return (this.jobView == null) ? null : this.jobView.getId();
    }
}