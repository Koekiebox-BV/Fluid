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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.webkit.RowExpansion;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
public class WebKitUserQuery extends ABaseFluidGSONObject {
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
     * Default with new instance of {@code JsonObject}.
     */
    public WebKitUserQuery() {
        this(new JsonObject());
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
    public WebKitUserQuery(JsonObject jsonObject, UserQuery userQuery) {
        this(jsonObject);
        this.setUserQuery(userQuery);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public WebKitUserQuery(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setRowExpansion(this.extractObject(JSONMapping.ROW_EXPANSION, RowExpansion::new));
        this.setUserQuery(this.extractObject(JSONMapping.USER_QUERY, UserQuery::new));
        this.setMenuItem(this.extractObject(JSONMapping.MENU_ITEM, WebKitMenuItem::new));
        
        this.setEnableForTopBar(this.getAsBooleanNullSafe(JSONMapping.ENABLE_FOR_TOP_BAR));
        this.setEnableCalculatedLabels(this.getAsBooleanNullSafe(JSONMapping.ENABLE_CALCULATED_LABELS));
        this.setQueryInputLayoutStyle(this.getAsStringNullSafe(JSONMapping.QUERY_INPUT_LAYOUT_STYLE));
        
        this.setShowColumnFormType(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_FORM_TYPE));
        this.setShowColumnID(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_ID));
        this.setShowColumnTitle(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_TITLE));
        this.setShowColumnAttachment(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_ATTACHMENT));
        this.setShowColumnDateCreated(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_DATE_CREATED));
        this.setShowColumnDateLastUpdated(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED));
        this.setShowColumnUser(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_USER));
        this.setShowColumnState(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_STATE));
        this.setShowColumnFlowState(this.getAsBooleanNullSafe(JSONMapping.SHOW_COLUMN_FLOW_STATE));
        
        this.setAttachmentHeader(this.getAsStringNullSafe(JSONMapping.ATTACHMENT_HEADER));
        this.setAttachmentThumbnailSize(this.getAsIntegerNullSafeStrictVal(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE));
        this.setTopBarDialogWidth(this.getAsIntegerNullSafeStrictVal(JSONMapping.TOP_BAR_DIALOG_WIDTH));
        this.setPaginatorRows(this.getAsIntegerNullSafeStrictVal(JSONMapping.PAGINATOR_ROWS));
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
        
        this.setAsProperty(JSONMapping.ENABLE_FOR_TOP_BAR, returnVal, this.isEnableForTopBar());
        this.setAsProperty(JSONMapping.ENABLE_CALCULATED_LABELS, returnVal, this.isEnableCalculatedLabels());
        this.setAsProperty(JSONMapping.QUERY_INPUT_LAYOUT_STYLE, returnVal, this.getQueryInputLayoutStyle());

        this.setAsProperty(JSONMapping.ATTACHMENT_HEADER, returnVal, this.getAttachmentHeader());
        this.setAsProperty(JSONMapping.ATTACHMENT_THUMBNAIL_SIZE, returnVal, this.getAttachmentThumbnailSize());
        this.setAsProperty(JSONMapping.TOP_BAR_DIALOG_WIDTH, returnVal, this.getTopBarDialogWidth());

        this.setAsProperty(JSONMapping.SHOW_COLUMN_ID, returnVal, this.isShowColumnID());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_FORM_TYPE, returnVal, this.isShowColumnFormType());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_TITLE, returnVal, this.isShowColumnTitle());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_ATTACHMENT, returnVal, this.isShowColumnAttachment());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_DATE_CREATED, returnVal, this.isShowColumnDateCreated());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_DATE_LAST_UPDATED, returnVal, this.isShowColumnDateLastUpdated());

        this.setAsProperty(JSONMapping.SHOW_COLUMN_USER, returnVal, this.isShowColumnUser());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_STATE, returnVal, this.isShowColumnState());
        this.setAsProperty(JSONMapping.SHOW_COLUMN_FLOW_STATE, returnVal, this.isShowColumnFlowState());

        this.setAsProperty(JSONMapping.PAGINATOR_ROWS, returnVal, this.getPaginatorRows());

        if (this.getUserQuery() != null) {
            if (this.jsonIncludeAll) {
                this.setAsObj(JSONMapping.USER_QUERY, returnVal, this::getUserQuery);
            } else {
                UserQuery reduced = new UserQuery(this.getUserQuery().getId());
                reduced.setName(this.getUserQuery().getName());
                JsonObject userQueryJson = reduced.toJsonObject();
                returnVal.add(JSONMapping.USER_QUERY, userQueryJson);
            }
        }

        if (this.getMenuItem() != null) {
            WebKitMenuItem reduced = new WebKitMenuItem(this.getMenuItem().getMenuId());
            returnVal.add(JSONMapping.MENU_ITEM, reduced.toJsonObject());
        }

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
