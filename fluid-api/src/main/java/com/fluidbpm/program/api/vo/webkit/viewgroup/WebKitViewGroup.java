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
public class WebKitViewGroup extends ABaseFluidJSONObject {
	private Long jobViewGroupId;
	private String jobViewGroupName;
	private String jobViewGroupIcon;

	private String tableGenerateMode = "combined";

	private String attachmentColumnLabel = "Image";
	private int attachmentThumbnailSize = 100;
	private int attachmentPreviewSize = 800;
	private int attachmentColumnMaxImageCount = 1;
	//vertical/horizontal
	private String attachmentColumnLayout;

	private boolean enableGroupSubsInMenu;//is the left click a root (false-> primary renders it)
	private boolean enableRenderEmptyTable;

	private int groupOrder = 1;
	//null for no limit...
	private Integer tableMaxCountPerPage;

	private boolean showColumnFormType;
	private boolean showColumnTitle;
	private boolean showColumnStepEntryTime;

	private boolean showColumnCurrentFlow;
	private boolean showColumnCurrentStep;
	private boolean showColumnCurrentView;

	private boolean showColumnProgressPercentage;
	private boolean showColumnAttachment;

	//Bulk item for same form type...
	private boolean showButtonBulkUpdate;
	private boolean showButtonExport;
	private boolean showButtonSendOn;
	private boolean showButtonDelete;
	private boolean showButtonLock;
	private boolean showButtonAddToPI;

	private List<WebKitViewSub> webKitViewSubs;
	/**
	 * The types of table generation modes for the View Group.
	 */
	public static class TableGenerateMode {
		public static final String COMBINED = "combined";//(Single Table for all)
		public static final String COMBINED_NO_DUPLICATES = "combined_no_duplicates";
		public static final String TABLE_PER_SUB = "table_per_sub";
		public static final String TABLE_PER_SUB_NO_DUPLICATES = "table_per_sub_no_duplicates";
		public static final String TABLE_PER_VIEW = "table_per_view";
		public static final String TABLE_PER_VIEW_NO_DUPLICATES = "table_per_view_no_duplicates";
	}

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String JOB_VIEW_GROUP_ID = "jobViewGroupId";
		public static final String JOB_VIEW_GROUP_NAME = "jobViewGroupName";
		public static final String JOB_VIEW_GROUP_ICON = "jobViewGroupIcon";

		public static final String ATTACHMENT_COLUMN_LABEL = "attachmentColumnLabel";
		public static final String ATTACHMENT_THUMBNAIL_SIZE = "attachmentThumbnailSize";
		public static final String ATTACHMENT_PREVIEW_SIZE = "attachmentPreviewSize";
		public static final String ATTACHMENT_COLUMN_MAX_IMAGE_COUNT = "attachmentColumnMaxImageCount";
		public static final String ATTACHMENT_COLUMN_LAYOUT = "attachmentColumnLayout";

		public static final String GROUP_ORDER = "groupOrder";
		public static final String ENABLE_GROUP_SUBS_IN_MENU = "enableGroupSubsInMenu";
		public static final String ENABLE_RENDER_EMPTY_TABLE = "enableRenderEmptyTable";

		public static final String SHOW_COLUMN_FORM_TYPE = "showColumnFormType";
		public static final String SHOW_COLUMN_TITLE = "showColumnTitle";
		public static final String SHOW_COLUMN_STEP_ENTRY_TIME = "showColumnStepEntryTime";
		public static final String SHOW_COLUMN_CURRENT_FLOW = "showColumnCurrentFlow";
		public static final String SHOW_COLUMN_CURRENT_STEP = "showColumnCurrentStep";
		public static final String SHOW_COLUMN_CURRENT_VIEW = "showColumnCurrentView";
		public static final String SHOW_COLUMN_PROGRESS_PERCENTAGE = "showColumnProgressPercentage";
		public static final String SHOW_COLUMN_ATTACHMENT = "showColumnAttachment";

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

		if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW_GROUP_ICON)) {
			this.setJobViewGroupIcon(this.jsonObject.getString(JSONMapping.JOB_VIEW_GROUP_ICON));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_GENERATE_MODE)) {
			this.setTableGenerateMode(this.jsonObject.getString(JSONMapping.TABLE_GENERATE_MODE));
		}

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_COLUMN_LABEL)) {
			this.setAttachmentColumnLabel(this.jsonObject.getString(JSONMapping.ATTACHMENT_COLUMN_LABEL));
		}

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE)) {
			this.setAttachmentThumbnailSize(this.jsonObject.getInt(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE));
		}

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_PREVIEW_SIZE)) {
			this.setAttachmentPreviewSize(this.jsonObject.getInt(JSONMapping.ATTACHMENT_PREVIEW_SIZE));
		}

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_COLUMN_MAX_IMAGE_COUNT)) {
			this.setAttachmentColumnMaxImageCount(this.jsonObject.getInt(JSONMapping.ATTACHMENT_COLUMN_MAX_IMAGE_COUNT));
		}

		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_COLUMN_LAYOUT)) {
			this.setAttachmentColumnLayout(this.jsonObject.getString(JSONMapping.ATTACHMENT_COLUMN_LAYOUT));
		}

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_GROUP_SUBS_IN_MENU)) {
			this.setEnableGroupSubsInMenu(this.jsonObject.getBoolean(JSONMapping.ENABLE_GROUP_SUBS_IN_MENU));
		}

		if (!this.jsonObject.isNull(JSONMapping.ENABLE_RENDER_EMPTY_TABLE)) {
			this.setEnableRenderEmptyTable(this.jsonObject.getBoolean(JSONMapping.ENABLE_RENDER_EMPTY_TABLE));
		}

		if (!this.jsonObject.isNull(JSONMapping.GROUP_ORDER)) {
			this.setGroupOrder(this.jsonObject.getInt(JSONMapping.GROUP_ORDER));
		}

		if (!this.jsonObject.isNull(JSONMapping.TABLE_MAX_COUNT_PER_PAGE)) {
			this.setTableMaxCountPerPage(this.jsonObject.getInt(JSONMapping.TABLE_MAX_COUNT_PER_PAGE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_FORM_TYPE)) {
			this.setShowColumnFormType(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_FORM_TYPE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_TITLE)) {
			this.setShowColumnTitle(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_TITLE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME)) {
			this.setShowColumnStepEntryTime(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_CURRENT_FLOW)) {
			this.setShowColumnCurrentFlow(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_CURRENT_FLOW));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_CURRENT_STEP)) {
			this.setShowColumnCurrentStep(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_CURRENT_STEP));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_CURRENT_VIEW)) {
			this.setShowColumnCurrentView(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_CURRENT_VIEW));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE)) {
			this.setShowColumnProgressPercentage(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_ATTACHMENT)) {
			this.setShowColumnAttachment(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_ATTACHMENT));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_BULK_UPDATE)) {
			this.setShowButtonBulkUpdate(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_BULK_UPDATE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_EXPORT)) {
			this.setShowButtonExport(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_EXPORT));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_SEND_ON)) {
			this.setShowButtonSendOn(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_SEND_ON));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_DELETE)) {
			this.setShowButtonDelete(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_DELETE));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_LOCK)) {
			this.setShowButtonLock(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_LOCK));
		}

		if (!this.jsonObject.isNull(JSONMapping.SHOW_BUTTON_ADD_TO_PI)) {
			this.setShowButtonAddToPI(this.jsonObject.getBoolean(JSONMapping.SHOW_BUTTON_ADD_TO_PI));
		}

		if (!this.jsonObject.isNull(JSONMapping.WEB_KIT_VIEW_SUBS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.WEB_KIT_VIEW_SUBS);
			List<WebKitViewSub> objs = new ArrayList();
			for (int index = 0;index < jsonArray.length();index++) {
				objs.add(new WebKitViewSub(jsonArray.getJSONObject(index)));
			}
			this.setWebKitViewSubs(objs);
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
		returnVal.put(JSONMapping.ENABLE_RENDER_EMPTY_TABLE, this.isEnableRenderEmptyTable());
		returnVal.put(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE, this.getAttachmentThumbnailSize());
		returnVal.put(JSONMapping.ATTACHMENT_PREVIEW_SIZE, this.getAttachmentPreviewSize());
		returnVal.put(JSONMapping.ATTACHMENT_COLUMN_MAX_IMAGE_COUNT, this.getAttachmentColumnMaxImageCount());
		returnVal.put(JSONMapping.GROUP_ORDER, this.getGroupOrder());

		returnVal.put(JSONMapping.SHOW_COLUMN_FORM_TYPE, this.isShowColumnFormType());
		returnVal.put(JSONMapping.SHOW_COLUMN_TITLE, this.isShowColumnTitle());
		returnVal.put(JSONMapping.SHOW_COLUMN_STEP_ENTRY_TIME, this.isShowColumnStepEntryTime());
		returnVal.put(JSONMapping.SHOW_COLUMN_CURRENT_FLOW, this.isShowColumnCurrentFlow());
		returnVal.put(JSONMapping.SHOW_COLUMN_CURRENT_STEP, this.isShowColumnCurrentStep());
		returnVal.put(JSONMapping.SHOW_COLUMN_CURRENT_VIEW, this.isShowColumnCurrentView());
		returnVal.put(JSONMapping.SHOW_COLUMN_PROGRESS_PERCENTAGE, this.isShowColumnProgressPercentage());
		returnVal.put(JSONMapping.SHOW_COLUMN_ATTACHMENT, this.isShowColumnAttachment());

		returnVal.put(JSONMapping.SHOW_BUTTON_BULK_UPDATE, this.isShowButtonBulkUpdate());
		returnVal.put(JSONMapping.SHOW_BUTTON_EXPORT, this.isShowButtonExport());
		returnVal.put(JSONMapping.SHOW_BUTTON_SEND_ON, this.isShowButtonSendOn());
		returnVal.put(JSONMapping.SHOW_BUTTON_DELETE, this.isShowButtonDelete());
		returnVal.put(JSONMapping.SHOW_BUTTON_LOCK, this.isShowButtonLock());
		returnVal.put(JSONMapping.SHOW_BUTTON_ADD_TO_PI, this.isShowButtonAddToPI());

		if (this.getJobViewGroupId() != null) {
			returnVal.put(JSONMapping.JOB_VIEW_GROUP_ID, this.getJobViewGroupId());
		}
		if (this.getJobViewGroupName() != null) {
			returnVal.put(JSONMapping.JOB_VIEW_GROUP_NAME, this.getJobViewGroupName());
		}
		if (this.getJobViewGroupIcon() != null) {
			returnVal.put(JSONMapping.JOB_VIEW_GROUP_ICON, this.getJobViewGroupIcon());
		}
		if (this.getTableGenerateMode() != null) {
			returnVal.put(JSONMapping.TABLE_GENERATE_MODE, this.getTableGenerateMode());
		}
		if (this.getAttachmentColumnLabel() != null) {
			returnVal.put(JSONMapping.ATTACHMENT_COLUMN_LABEL, this.getAttachmentColumnLabel());
		}
		if (this.getAttachmentColumnLayout() != null) {
			returnVal.put(JSONMapping.ATTACHMENT_COLUMN_LAYOUT, this.getAttachmentColumnLayout());
		}
		if (this.getTableMaxCountPerPage() != null) {
			returnVal.put(JSONMapping.TABLE_MAX_COUNT_PER_PAGE, this.getTableMaxCountPerPage());
		}

		if (this.getWebKitViewSubs() != null && !this.getWebKitViewSubs().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (WebKitViewSub toAdd : this.getWebKitViewSubs()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.WEB_KIT_VIEW_SUBS, jsonArray);
		}

		return returnVal;
	}

	/**
	 * @return number of objects inside {@code webKitViewSubs}.
	 */
	@XmlTransient
	public int getWebKitViewSubsCount() {
		return this.getWebKitViewSubs() == null ? 0 :
				this.getWebKitViewSubs().size();
	}

	/**
	 * Verify if TGM is of type combined.
	 * @return {code true} if Table Generate Mode is any combined.
	 */
	@XmlTransient
	public boolean isTGMCombined() {
		final String tgm = this.getTableGenerateMode();
		if (tgm == null) return false;
		switch (tgm) {
			case TableGenerateMode.COMBINED:
			case TableGenerateMode.COMBINED_NO_DUPLICATES:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Verify if TGM is of type table per sub.
	 * @return {code true} if Table Generate Mode is any table per sub.
	 */
	@XmlTransient
	public boolean isTGMTablePerSub() {
		final String tgm = this.getTableGenerateMode();
		if (tgm == null) return false;
		switch (tgm) {
			case TableGenerateMode.TABLE_PER_SUB:
			case TableGenerateMode.TABLE_PER_SUB_NO_DUPLICATES:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Verify if TGM is of type table per view.
	 * @return {code true} if Table Generate Mode is any table per view.
	 */
	@XmlTransient
	public boolean isTGMTablePerView() {
		final String tgm = this.getTableGenerateMode();
		if (tgm == null) return false;
		switch (tgm) {
			case TableGenerateMode.TABLE_PER_VIEW:
			case TableGenerateMode.TABLE_PER_VIEW_NO_DUPLICATES:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Is the group configured for no duplicates.
	 * @return {code true} if Table Generate Mode is any 'no duplicates'.
	 */
	@XmlTransient
	public boolean isTGMNoDuplicates() {
		final String tgm = this.getTableGenerateMode();
		if (tgm == null) return false;
		switch (tgm) {
			case TableGenerateMode.COMBINED_NO_DUPLICATES:
			case TableGenerateMode.TABLE_PER_SUB_NO_DUPLICATES:
			case TableGenerateMode.TABLE_PER_VIEW_NO_DUPLICATES:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Retrieve a view sub with name {@code viewSubName}.
	 * Case sensitive method.
	 * @return {code WebKitViewSub} with name {@code viewSubName}.
	 */
	@XmlTransient
	public WebKitViewSub getViewSubWithName(String viewSubName) {
		if (this.getWebKitViewSubsCount() < 1) return null;
		return this.getWebKitViewSubs().stream()
				.filter(itm -> viewSubName.equals(itm.getLabel()))
				.findFirst()
				.orElse(null);
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
