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

package com.fluidbpm.program.api.vo.webkit.global;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * WebKit associated with personal inventory look and feels.
 * 
 * @see com.fluidbpm.program.api.vo.user.User
 */
@Getter
@Setter
public class WebKitPersonalInventory extends ABaseFluidJSONObject {
	private boolean personalInventoryEnabled = true;
	private boolean collaborationEnabled = false;

	private boolean showColumnID = false;
	private boolean showColumnTitle = true;
	private boolean showColumnFormType = true;
	private boolean showColumnAttachment = true;
	private boolean showColumnStatus = true;
	private boolean showColumnWorkflowStep;
	private boolean showColumnCurrentUser = true;
	private boolean showColumnLastUpdated = true;

	public enum VisibleColumnItems {
		showColumnID,
		showColumnTitle,
		showColumnFormType,
		showColumnAttachment,
		showColumnStatus,
		showColumnCurrentUser,
		showColumnWorkflowStep,
		showColumnLastUpdated,
		;

		public static List<String> asListFrom(WebKitPersonalInventory personalInventory) {
			List<String> returnVal = new ArrayList<>();
			if (personalInventory == null) return returnVal;

			if (personalInventory.isShowColumnID()) returnVal.add(VisibleColumnItems.showColumnID.name());
			if (personalInventory.isShowColumnTitle()) returnVal.add(VisibleColumnItems.showColumnTitle.name());
			if (personalInventory.isShowColumnFormType()) returnVal.add(VisibleColumnItems.showColumnFormType.name());
			if (personalInventory.isShowColumnAttachment()) returnVal.add(VisibleColumnItems.showColumnAttachment.name());
			if (personalInventory.isShowColumnStatus()) returnVal.add(VisibleColumnItems.showColumnStatus.name());
			if (personalInventory.isShowColumnCurrentUser()) returnVal.add(VisibleColumnItems.showColumnCurrentUser.name());
			if (personalInventory.isShowColumnWorkflowStep()) returnVal.add(VisibleColumnItems.showColumnWorkflowStep.name());
			if (personalInventory.isShowColumnLastUpdated()) returnVal.add(VisibleColumnItems.showColumnLastUpdated.name());
			return returnVal;
		}
	}

	/**
	 * The JSON mapping for the {@code WebKitPersonalInventory} object.
	 */
	public static class JSONMapping {
		public static final String PERSONAL_INVENTORY_ENABLED = "personalInventoryEnabled";
		public static final String COLLABORATION_ENABLED = "collaborationEnabled";

		public static final String SHOW_COLUMN_ID = "showColumnID";
		public static final String SHOW_COLUMN_TITLE = "showColumnTitle";
		public static final String SHOW_COLUMN_FORM_TYPE = "showColumnFormType";
		public static final String SHOW_COLUMN_ATTACHMENT = "showColumnAttachment";
		public static final String SHOW_COLUMN_STATUS = "showColumnStatus";
		public static final String SHOW_COLUMN_CURRENT_USER = "showColumnCurrentUser";
		public static final String SHOW_COLUMN_WORKFLOW_STEP = "showColumnWorkflowStep";
		public static final String SHOW_COLUMN_LAST_UPDATED = "showColumnLastUpdated";
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitPersonalInventory(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.PERSONAL_INVENTORY_ENABLED))
			this.setPersonalInventoryEnabled(this.jsonObject.getBoolean(JSONMapping.PERSONAL_INVENTORY_ENABLED));

		if (!this.jsonObject.isNull(JSONMapping.COLLABORATION_ENABLED))
			this.setCollaborationEnabled(this.jsonObject.getBoolean(JSONMapping.COLLABORATION_ENABLED));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_ID))
			this.setShowColumnID(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_ID));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_TITLE))
			this.setShowColumnTitle(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_TITLE));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_FORM_TYPE))
			this.setShowColumnFormType(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_FORM_TYPE));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_ATTACHMENT))
			this.setShowColumnAttachment(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_ATTACHMENT));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_STATUS))
			this.setShowColumnStatus(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_STATUS));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_CURRENT_USER))
			this.setShowColumnCurrentUser(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_CURRENT_USER));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_WORKFLOW_STEP))
			this.setShowColumnWorkflowStep(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_WORKFLOW_STEP));

		if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_LAST_UPDATED))
			this.setShowColumnLastUpdated(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_LAST_UPDATED));

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

		returnVal.put(JSONMapping.PERSONAL_INVENTORY_ENABLED, this.isPersonalInventoryEnabled());
		returnVal.put(JSONMapping.COLLABORATION_ENABLED, this.isCollaborationEnabled());
		returnVal.put(JSONMapping.SHOW_COLUMN_ID, this.isShowColumnID());
		returnVal.put(JSONMapping.SHOW_COLUMN_FORM_TYPE, this.isShowColumnID());
		returnVal.put(JSONMapping.SHOW_COLUMN_ATTACHMENT, this.isShowColumnAttachment());
		returnVal.put(JSONMapping.SHOW_COLUMN_CURRENT_USER, this.isShowColumnCurrentUser());
		returnVal.put(JSONMapping.SHOW_COLUMN_STATUS, this.isShowColumnStatus());
		returnVal.put(JSONMapping.SHOW_COLUMN_LAST_UPDATED, this.isShowColumnLastUpdated());
		returnVal.put(JSONMapping.SHOW_COLUMN_WORKFLOW_STEP, this.isShowColumnWorkflowStep());
		returnVal.put(JSONMapping.SHOW_COLUMN_TITLE, this.isShowColumnTitle());

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

		if (listing.contains(VisibleColumnItems.showColumnTitle.name())) {
			this.setShowColumnTitle(true);
		} else {
			this.setShowColumnTitle(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnFormType.name())) {
			this.setShowColumnFormType(true);
		} else {
			this.setShowColumnFormType(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnAttachment.name())) {
			this.setShowColumnAttachment(true);
		} else {
			this.setShowColumnAttachment(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnStatus.name())) {
			this.setShowColumnStatus(true);
		} else {
			this.setShowColumnStatus(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnCurrentUser.name())) {
			this.setShowColumnCurrentUser(true);
		} else {
			this.setShowColumnCurrentUser(false);
		}

		if (listing.contains(VisibleColumnItems.showColumnLastUpdated.name())) {
			this.setShowColumnLastUpdated(true);
		} else {
			this.setShowColumnLastUpdated(false);
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
