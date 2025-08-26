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

package com.fluidbpm.program.api.vo.webkit;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * WebKit used for any table expansions.
 *
 * @see com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewSub
 * @see com.fluidbpm.program.api.vo.webkit.userquery.WebKitUserQuery
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RowExpansion extends ABaseFluidGSONObject {
    private boolean tableExpansionDisplayAttachments;
    private boolean tableExpansionDisplayAncestor;
    private boolean tableExpansionDisplayDescendant;
    private boolean tableExpansionDisplayRecords;
    private boolean tableExpansionDisplayRecordsInlineEdit;
    private boolean tableExpansionDisplayFlowHistory;
    private boolean tableExpansionDisplayFormHistory;

    /**
     * The JSON mapping for the {@code WebKitForm} object.
     */
    public static class JSONMapping {
        public static final String TABLE_EXPANSION_DISPLAY_ATTACHMENTS = "tableExpansionDisplayAttachments";
        public static final String TABLE_EXPANSION_DISPLAY_ANCESTOR = "tableExpansionDisplayAncestor";
        public static final String TABLE_EXPANSION_DISPLAY_DESCENDANT = "tableExpansionDisplayDescendant";
        public static final String TABLE_EXPANSION_DISPLAY_RECORDS = "tableExpansionDisplayRecords";
        public static final String TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT = "tableExpansionDisplayRecordsInlineEdit";
        public static final String TABLE_EXPANSION_DISPLAY_FLOW_HISTORY = "tableExpansionDisplayFlowHistory";
        public static final String TABLE_EXPANSION_DISPLAY_FORM_HISTORY = "tableExpansionDisplayFormHistory";
    }

    /**
     * Enum of expansion items for easy use on a front-end.
     */
    public enum TableExpansionItems {
        tableExpansionDisplayAttachments,
        tableExpansionDisplayAncestor,
        tableExpansionDisplayDescendant,
        tableExpansionDisplayRecords,
        tableExpansionDisplayRecordsInlineEdit,
        tableExpansionDisplayFlowHistory,
        tableExpansionDisplayFormHistory,
        ;

        public static List<String> asListFrom(RowExpansion rowExpansion) {
            List<String> returnVal = new ArrayList<>();
            if (rowExpansion == null) return returnVal;

            if (rowExpansion.isTableExpansionDisplayAttachments())
                returnVal.add(TableExpansionItems.tableExpansionDisplayAttachments.name());

            if (rowExpansion.isTableExpansionDisplayAncestor())
                returnVal.add(TableExpansionItems.tableExpansionDisplayAncestor.name());

            if (rowExpansion.isTableExpansionDisplayDescendant())
                returnVal.add(TableExpansionItems.tableExpansionDisplayDescendant.name());

            if (rowExpansion.isTableExpansionDisplayRecords())
                returnVal.add(TableExpansionItems.tableExpansionDisplayRecords.name());

            if (rowExpansion.isTableExpansionDisplayRecordsInlineEdit())
                returnVal.add(TableExpansionItems.tableExpansionDisplayRecordsInlineEdit.name());

            if (rowExpansion.isTableExpansionDisplayFlowHistory())
                returnVal.add(TableExpansionItems.tableExpansionDisplayFlowHistory.name());

            if (rowExpansion.isTableExpansionDisplayFormHistory())
                returnVal.add(TableExpansionItems.tableExpansionDisplayFormHistory.name());

            return returnVal;
        }
    }

    /**
     * Default.
     */
    public RowExpansion() {
        this(new JsonObject());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RowExpansion(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setTableExpansionDisplayAncestor(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_ANCESTOR));
        this.setTableExpansionDisplayDescendant(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_DESCENDANT));
        this.setTableExpansionDisplayRecords(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS));
        this.setTableExpansionDisplayRecordsInlineEdit(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT));
        this.setTableExpansionDisplayAttachments(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_ATTACHMENTS));
        this.setTableExpansionDisplayFlowHistory(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_FLOW_HISTORY));
        this.setTableExpansionDisplayFormHistory(this.getAsBooleanNullSafe(JSONMapping.TABLE_EXPANSION_DISPLAY_FORM_HISTORY));
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JSONObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JSONObject} representation of {@code ABaseFluidGSONObject}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see org.json.JSONObject
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_ANCESTOR, returnVal, this.isTableExpansionDisplayAncestor());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_ATTACHMENTS, returnVal, this.isTableExpansionDisplayAttachments());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_DESCENDANT, returnVal, this.isTableExpansionDisplayDescendant());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_FLOW_HISTORY, returnVal, this.isTableExpansionDisplayFlowHistory());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_FORM_HISTORY, returnVal, this.isTableExpansionDisplayFormHistory());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS, returnVal, this.isTableExpansionDisplayRecords());
        this.setAsProperty(JSONMapping.TABLE_EXPANSION_DISPLAY_RECORDS_INLINE_EDIT, returnVal, this.isTableExpansionDisplayRecordsInlineEdit());

        return returnVal;
    }

    /**
     * Verifies whether any of the table expressions have been enabled.
     *
     * @return {@code true} If any row expansion is active, otherwise {@code false}.
     */
    public boolean isAnyExpansion() {
        boolean[] values = new boolean[]{
                this.isTableExpansionDisplayAttachments(),
                this.isTableExpansionDisplayAncestor(),
                this.isTableExpansionDisplayDescendant(),
                this.isTableExpansionDisplayFlowHistory(),
                this.isTableExpansionDisplayRecords(),
                this.isTableExpansionDisplayRecordsInlineEdit(),
                this.isTableExpansionDisplayFormHistory(),
        };
        for (boolean val : values) if (val) return true;
        return false;
    }

    /**
     * @return Expansion sections as {@code List<String>}.
     */
    public List<String> getTableExpansionsAsList() {
        return TableExpansionItems.asListFrom(this);
    }

    /**
     * Set expansion sections as {@code List<String>}.
     *
     * @param listing The list
     */
    public void setTableExpansionsAsList(List<String> listing) {
        if (listing == null) return;

        if (listing.contains(TableExpansionItems.tableExpansionDisplayAttachments.name()))
            this.setTableExpansionDisplayAttachments(true);
        else this.setTableExpansionDisplayAttachments(false);

        if (listing.contains(TableExpansionItems.tableExpansionDisplayAncestor.name()))
            this.setTableExpansionDisplayAncestor(true);
        else this.setTableExpansionDisplayAncestor(false);

        if (listing.contains(TableExpansionItems.tableExpansionDisplayDescendant.name()))
            this.setTableExpansionDisplayDescendant(true);
        else this.setTableExpansionDisplayDescendant(false);

        if (listing.contains(TableExpansionItems.tableExpansionDisplayFlowHistory.name()))
            this.setTableExpansionDisplayFlowHistory(true);
        else this.setTableExpansionDisplayFlowHistory(false);

        if (listing.contains(TableExpansionItems.tableExpansionDisplayRecords.name()))
            this.setTableExpansionDisplayRecords(true);
        else this.setTableExpansionDisplayRecords(false);

        if (listing.contains(TableExpansionItems.tableExpansionDisplayRecordsInlineEdit.name()))
            this.setTableExpansionDisplayRecordsInlineEdit(true);
        else this.setTableExpansionDisplayRecordsInlineEdit(false);
    }
}
