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

package com.fluidbpm.program.api.vo.webkit.userquery;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.webkit.RowExpansion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * WebKit associated with user query look and feels.
 *
 * @see UserQuery
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class WebKitUserQuery extends ABaseFluidJSONObject {
    private UserQuery userQuery;
    private WebKitMenuItem menuItem;
    private RowExpansion rowExpansion;

    private boolean enableForTopBar;
    private boolean enableCalculatedLabels;
    private String queryInputLayoutStyle;//Vertical|Horizontal|Vertical Grid|Help Text|

    private boolean showColumnID;
    private boolean showColumnFormType;
    private boolean showColumnTitle;
    private boolean showColumnDateCreated;
    private boolean showColumnDateLastUpdated;
    private boolean showColumnAttachment;
    private boolean showColumnUser;
    private boolean showColumnState;
    private boolean showColumnFlowState;

    private String attachmentHeader = "Attachments";
    private int attachmentThumbnailSize = 75;
    private int topBarDialogWidth = 1024;
    private int paginatorRows = 15;

    public enum VisibleColumnItems {
        showColumnID,
        showColumnFormType,
        showColumnTitle,
        showColumnDateCreated,
        showColumnDateLastUpdated,
        showColumnAttachment,
        showColumnUser,
        showColumnState,
        showColumnFlowState,
        ;

        public static List<String> asListFrom(WebKitUserQuery userQuery) {
            List<String> returnVal = new ArrayList<>();
            if (userQuery == null) return returnVal;

            if (userQuery.isShowColumnID()) returnVal.add(VisibleColumnItems.showColumnID.name());
            if (userQuery.isShowColumnFormType()) returnVal.add(VisibleColumnItems.showColumnFormType.name());
            if (userQuery.isShowColumnTitle()) returnVal.add(VisibleColumnItems.showColumnTitle.name());
            if (userQuery.isShowColumnAttachment()) returnVal.add(VisibleColumnItems.showColumnAttachment.name());
            if (userQuery.isShowColumnDateCreated()) returnVal.add(VisibleColumnItems.showColumnDateCreated.name());
            if (userQuery.isShowColumnDateLastUpdated())
                returnVal.add(VisibleColumnItems.showColumnDateLastUpdated.name());
            if (userQuery.isShowColumnUser()) returnVal.add(VisibleColumnItems.showColumnUser.name());
            if (userQuery.isShowColumnState()) returnVal.add(VisibleColumnItems.showColumnState.name());
            if (userQuery.isShowColumnFlowState()) returnVal.add(VisibleColumnItems.showColumnFlowState.name());
            return returnVal;
        }
    }

    /**
     * The JSON mapping for the {@code WebKitWorkspaceJobView} object.
     */
    public static class JSONMapping {
        public static final String USER_QUERY = "userQuery";
        public static final String MENU_ITEM = "menuItem";
        public static final String ENABLE_FOR_TOP_BAR = "enableForTopBar";
        public static final String ENABLE_CALCULATED_LABELS = "enableCalculatedLabels";
        public static final String QUERY_INPUT_LAYOUT_STYLE = "queryInputLayoutStyle";

        public static final String SHOW_COLUMN_ID = "showColumnID";
        public static final String SHOW_COLUMN_FORM_TYPE = "showColumnFormType";
        public static final String SHOW_COLUMN_TITLE = "showColumnTitle";
        public static final String SHOW_COLUMN_ATTACHMENT = "showColumnAttachment";
        public static final String SHOW_COLUMN_DATE_CREATED = "showColumnDateCreated";
        public static final String SHOW_COLUMN_DATE_LAST_UPDATED = "showColumnDateLastUpdated";

        public static final String SHOW_COLUMN_USER = "showColumnUser";
        public static final String SHOW_COLUMN_STATE = "showColumnState";
        public static final String SHOW_COLUMN_FLOW_STATE = "showColumnFlowState";

        public static final String ATTACHMENT_HEADER = "attachmentHeader";
        public static final String ATTACHMENT_THUMBNAIL_SIZE = "attachmentThumbnailSize";
        public static final String TOP_BAR_DIALOG_WIDTH = "topBarDialogWidth";

        public static final String PAGINATOR_ROWS = "paginatorRows";

        public static final String ROW_EXPANSION = "rowExpansion";
    }

    /**
     * Default with new instance of {@code JSONObject}.
     */
    public WebKitUserQuery() {
        this(new JSONObject());
    }

    /**
     * @param userQuery The user query associated.
     * @see UserQuery
     */
    public WebKitUserQuery(UserQuery userQuery) {
        this();
        this.setUserQuery(userQuery);
    }

    /**
     * Set the WebKit JSON from {@code jsonObject} and then {@code userQuery} only.
     *
     * @param jsonObject The JSON WebKit Obj
     * @param userQuery  The {@code UserQuery}
     * @see UserQuery
     */
    public WebKitUserQuery(JSONObject jsonObject, UserQuery userQuery) {
        this(jsonObject);
        this.setUserQuery(userQuery);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public WebKitUserQuery(JSONObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        if (this.jsonObject.isNull(JSONMapping.ROW_EXPANSION)) this.setRowExpansion(new RowExpansion(new JSONObject()));
        else this.setRowExpansion(new RowExpansion(this.jsonObject.getJSONObject(JSONMapping.ROW_EXPANSION)));

        if (!this.jsonObject.isNull(JSONMapping.USER_QUERY))
            this.setUserQuery(new UserQuery(this.jsonObject.getJSONObject(JSONMapping.USER_QUERY)));

        if (!this.jsonObject.isNull(JSONMapping.MENU_ITEM))
            this.setMenuItem(new WebKitMenuItem(this.jsonObject.getJSONObject(JSONMapping.MENU_ITEM)));

        if (!this.jsonObject.isNull(JSONMapping.ENABLE_FOR_TOP_BAR))
            this.setEnableForTopBar(this.jsonObject.getBoolean(JSONMapping.ENABLE_FOR_TOP_BAR));

        if (!this.jsonObject.isNull(JSONMapping.ENABLE_CALCULATED_LABELS))
            this.setEnableCalculatedLabels(this.jsonObject.getBoolean(JSONMapping.ENABLE_CALCULATED_LABELS));

        if (!this.jsonObject.isNull(JSONMapping.QUERY_INPUT_LAYOUT_STYLE))
            this.setQueryInputLayoutStyle(this.jsonObject.getString(JSONMapping.QUERY_INPUT_LAYOUT_STYLE));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_FORM_TYPE))
            this.setShowColumnFormType(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_FORM_TYPE));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_ID))
            this.setShowColumnID(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_ID));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_TITLE))
            this.setShowColumnTitle(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_TITLE));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_ATTACHMENT))
            this.setShowColumnAttachment(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_ATTACHMENT));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_DATE_CREATED))
            this.setShowColumnDateCreated(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_DATE_CREATED));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED))
            this.setShowColumnDateLastUpdated(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_USER))
            this.setShowColumnUser(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_USER));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_STATE))
            this.setShowColumnState(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_STATE));

        if (!this.jsonObject.isNull(JSONMapping.SHOW_COLUMN_FLOW_STATE))
            this.setShowColumnFlowState(this.jsonObject.getBoolean(JSONMapping.SHOW_COLUMN_FLOW_STATE));

        if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_HEADER))
            this.setAttachmentHeader(this.jsonObject.getString(JSONMapping.ATTACHMENT_HEADER));

        if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE))
            this.setAttachmentThumbnailSize(this.jsonObject.getInt(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE));

        if (!this.jsonObject.isNull(JSONMapping.TOP_BAR_DIALOG_WIDTH))
            this.setTopBarDialogWidth(this.jsonObject.getInt(JSONMapping.TOP_BAR_DIALOG_WIDTH));

        if (!this.jsonObject.isNull(JSONMapping.PAGINATOR_ROWS))
            this.setPaginatorRows(this.jsonObject.getInt(JSONMapping.PAGINATOR_ROWS));
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
        returnVal.put(JSONMapping.ENABLE_FOR_TOP_BAR, this.isEnableForTopBar());
        returnVal.put(JSONMapping.ENABLE_CALCULATED_LABELS, this.isEnableCalculatedLabels());
        returnVal.put(JSONMapping.QUERY_INPUT_LAYOUT_STYLE, this.getQueryInputLayoutStyle());

        returnVal.put(JSONMapping.ATTACHMENT_HEADER, this.getAttachmentHeader());
        returnVal.put(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE, this.getAttachmentThumbnailSize());
        returnVal.put(JSONMapping.TOP_BAR_DIALOG_WIDTH, this.getTopBarDialogWidth());

        returnVal.put(JSONMapping.SHOW_COLUMN_ID, this.isShowColumnID());
        returnVal.put(JSONMapping.SHOW_COLUMN_FORM_TYPE, this.isShowColumnFormType());
        returnVal.put(JSONMapping.SHOW_COLUMN_TITLE, this.isShowColumnTitle());
        returnVal.put(JSONMapping.SHOW_COLUMN_ATTACHMENT, this.isShowColumnAttachment());
        returnVal.put(JSONMapping.SHOW_COLUMN_DATE_CREATED, this.isShowColumnDateCreated());
        returnVal.put(JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED, this.isShowColumnDateLastUpdated());

        returnVal.put(JSONMapping.SHOW_COLUMN_USER, this.isShowColumnUser());
        returnVal.put(JSONMapping.SHOW_COLUMN_STATE, this.isShowColumnState());
        returnVal.put(JSONMapping.SHOW_COLUMN_FLOW_STATE, this.isShowColumnFlowState());

        returnVal.put(JSONMapping.PAGINATOR_ROWS, this.getPaginatorRows());

        if (this.getUserQuery() != null) {
            if (this.jsonIncludeAll) {
                returnVal.put(JSONMapping.USER_QUERY, this.getUserQuery().toJsonObject());
            } else {
                UserQuery reduced = new UserQuery(this.getUserQuery().getId());
                reduced.setName(this.getUserQuery().getName());
                returnVal.put(JSONMapping.USER_QUERY, reduced.toJsonObject());
            }
        }

        if (this.getMenuItem() != null) {
            WebKitMenuItem reduced = new WebKitMenuItem(this.getMenuItem().getMenuId());
            returnVal.put(JSONMapping.MENU_ITEM, reduced.toJsonObject());
        }

        if (this.getRowExpansion() != null) {
            returnVal.put(JSONMapping.ROW_EXPANSION, this.getRowExpansion().toJsonObject());
        }

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

        if (listing.contains(VisibleColumnItems.showColumnAttachment.name())) {
            this.setShowColumnAttachment(true);
        } else {
            this.setShowColumnAttachment(false);
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

        if (listing.contains(VisibleColumnItems.showColumnUser.name())) {
            this.setShowColumnUser(true);
        } else {
            this.setShowColumnUser(false);
        }

        if (listing.contains(VisibleColumnItems.showColumnState.name())) {
            this.setShowColumnState(true);
        } else {
            this.setShowColumnState(false);
        }

        if (listing.contains(VisibleColumnItems.showColumnFlowState.name())) {
            this.setShowColumnFlowState(true);
        } else {
            this.setShowColumnFlowState(false);
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
