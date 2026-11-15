/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.form;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.google.gson.JsonObject;

/**
 * Ordered table records for a bounded, synchronous creation batch.
 *
 * @see TableRecord
 * @see ABaseGSONListing
 */
public class TableRecordListing extends ABaseGSONListing<TableRecord> {
    private static final long serialVersionUID = 1L;

    public static final int MAX_BATCH_SIZE = 250;

    /**
     * Default constructor.
     */
    public TableRecordListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON object.
     */
    public TableRecordListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code TableRecord} object.
     *
     * @param jsonObjectParam The JSON object to convert.
     * @return New {@code TableRecord} instance.
     */
    @Override
    public TableRecord getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new TableRecord(jsonObjectParam);
    }
}
