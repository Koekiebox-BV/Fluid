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
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * WebKit associated with job view group look and feels.
 *
 * @see com.fluidbpm.program.api.vo.flow.JobView
 */
@Getter
@Setter
public class WebKitViewGroup extends ABaseFluidJSONObject {
	private Long jobViewGroupId;
	private String jobViewGroupName;
	private String jobViewGroupIcon;
	private int groupOrder;

	private boolean enableGroupSubsInMenu;//is the left click a root (false-> primary renders it)

	private boolean showColumnFormType;
	private boolean showColumnTitle;
	private boolean showColumnStepEntryTime;

	private boolean showColumnCurrentFlow;
	private boolean showColumnCurrentStep;
	private boolean showColumnCurrentView;

	private boolean showColumnProgressPercentage;
	private boolean showColumnAttachment;
	private String attachmentColumnLabel = "Image";

	//Bulk item for same form type...
	private boolean showButtonBulkUpdate;
	private boolean showButtonExport;
	private boolean showButtonSendOn;
	private boolean showButtonDelete;
	private boolean showButtonLock;
	private boolean showButtonAddToPI;

	/*
	 * combined (Single Table for all)
	 * combined_no_duplicates (Single Table for all)
	 * table_per_sub
	 * table_per_sub_no_duplicates
	 * table_per_view
	 * table_per_view_no_duplicates
	 */
	private String tableGenerateMode;

	//null for no limit...
	private Integer tableMaxCountPerPage;

	private List<WebKitViewSub> webKitViewSubs;

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String JOB_VIEW_GROUP_ID = "jobViewGroupId";
		public static final String JOB_VIEW_GROUP_NAME = "jobViewGroupName";
		public static final String jobViewGroupIcon = "jobViewGroupIcon";
		public static final String groupOrder = "groupOrder";
		public static final String ENABLE_GROUP_SUBS_IN_MENU = "enableGroupSubsInMenu";
		public static final String SHOW_COLUMN_FORM_TYPE = "showColumnFormType";
		public static final String SHOW_COLUMN_TITLE = "showColumnTitle";
		public static final String SHOW_COLUMN_STEP_ENTRY_TIME = "showColumnStepEntryTime";
		public static final String SHOW_COLUMN_CURRENT_FLOW = "showColumnCurrentFlow";
		public static final String SHOW_COLUMN_CURRENT_STEP = "showColumnCurrentStep";
		public static final String SHOW_COLUMN_CURRENT_VIEW = "showColumnCurrentView";
		public static final String SHOW_COLUMN_PROGRESS_PERCENTAGE = "showColumnProgressPercentage";
		public static final String SHOW_COLUMN_ATTACHMENT = "showColumnAttachment";
		public static final String ATTACHMENT_COLUMN_LABEL = "attachmentColumnLabel";

		public static final String SHOW_BUTTON_BULK_UPDATE = "showButtonBulkUpdate";
		public static final String SHOW_BUTTON_EXPORT = "showButtonExport";
		public static final String SHOW_BUTTON_SEND_ON = "showButtonSendOn";
		public static final String SHOW_BUTTON_DELETE = "showButtonDelete";
		public static final String SHOW_BUTTON_LOCK = "showButtonLock";
		public static final String SHOW_BUTTON_ADD_TO_PI = "showButtonAddToPI";

		public static final String TABLE_GENERATE_MODE = "tableGenerateMode";
		public static final String TABLE_MAX_COUNT_PER_PAGE = "tableMaxCountPerPage";

		public static final String WEB_KIT_VIEW_SUBS = "webKitViewSubs";
	}

	public WebKitViewGroup() {
		this(new JSONObject());
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitViewGroup(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) {
			return;
		}

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW_GROUP_ID)) {
			this.setJobViewGroupId(this.jsonObject.getLong(JSONMapping.JOB_VIEW_GROUP_ID));
		}

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW_GROUP_NAME)) {
			this.setJobViewGroupName(this.jsonObject.getString(JSONMapping.JOB_VIEW_GROUP_NAME));
		}

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_GROUP_SUBS_IN_MENU)) {
			this.setEnableGroupSubsInMenu(this.jsonObject.getBoolean(JSONMapping.ENABLE_GROUP_SUBS_IN_MENU));
		}

	}

	/**
	 * Populates only group name.
	 *
	 * @param groupName The View Group name.
	 */
	public WebKitViewGroup(String groupName) {
		this();
		this.setJobViewGroupName(groupName);
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
		returnVal.put(JSONMapping.ENABLE_GROUP_SUBS_IN_MENU, this.isEnableGroupSubsInMenu());

		if (this.getJobViewGroupId() != null) {
			returnVal.put(JSONMapping.JOB_VIEW_GROUP_ID, this.getJobViewGroupId());
		}
		if (this.getJobViewGroupName() != null) {
			returnVal.put(JSONMapping.JOB_VIEW_GROUP_NAME, this.getJobViewGroupName());
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
}
