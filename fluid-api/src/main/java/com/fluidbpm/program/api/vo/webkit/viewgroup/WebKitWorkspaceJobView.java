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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see JobView
 */
@Getter
@Setter
@EqualsAndHashCode
public class WebKitWorkspaceJobView extends ABaseFluidJSONObject {
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
		this(new JSONObject());
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitWorkspaceJobView(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW)) {
			this.setJobView(new JobView(this.jsonObject.getJSONObject(JSONMapping.JOB_VIEW)));
		}

		if (!this.jsonObject.isNull(JSONMapping.FETCH_LIMIT)) {
			this.setFetchLimit(this.jsonObject.getInt(JSONMapping.FETCH_LIMIT));
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
	 * @return The local set {@code JSONObject} object.
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonObject() {
		JSONObject returnVal = super.toJsonObject();
		if (this.getJobView() != null) {
			JobView reducedView = new JobView(this.getJobView().getId());
			returnVal.put(JSONMapping.JOB_VIEW, reducedView.toJsonObject());
		}

		if (this.getFetchLimit() != null) {
			returnVal.put(JSONMapping.FETCH_LIMIT, this.getFetchLimit());
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
	public String toString() {
		return super.toString();
	}

	/**
	 * Return the name of the view.
	 * @return {@code this.getJobView().getViewName()}
	 */
	@XmlTransient
	public String getViewStepName() {
		return (this.getJobView() == null) ? null : this.getJobView().getViewStepName();
	}

	/**
	 * Return the name of the flow.
	 * @return {@code this.getJobView().getViewFlowName()}
	 */
	@XmlTransient
	public String getViewFlowName() {
		return (this.getJobView() == null) ? null : this.getJobView().getViewFlowName();
	}

	/**
	 * Return the priority of the view.
	 * @return {@code this.getJobView().getViewPriority()}
	 */
	@XmlTransient
	public Integer getViewPriority() {
		return (this.getJobView() == null) ? null : this.getJobView().getViewPriority();
	}

	/**
	 * Return the order of the view.
	 * @return {@code this.getJobView().getViewOrder()}
	 */
	@XmlTransient
	public Long getViewOrder() {
		return (this.getJobView() == null) ? null : this.getJobView().getViewOrder();
	}

	/**
	 * Extract the flow, step and view order.
	 * @return "%s_%s_%010d" FlowName StepName and ViewOrder with 10 places 0 filled.
	 */
	public String getViewFlowStepViewOrder() {
		String returnVal = String.format("%s_%s_%010d",
				this.getViewFlowName(),
				this.getViewStepName(),
				this.getViewOrder());
		return returnVal;
	}

	/**
	 * Id for WebKit Job view.
	 * @return Primary Key of {@code jobView}.
	 */
	@Override
	public Long getId() {
		return (this.jobView == null) ? null :
				this.jobView.getId();
	}
}
