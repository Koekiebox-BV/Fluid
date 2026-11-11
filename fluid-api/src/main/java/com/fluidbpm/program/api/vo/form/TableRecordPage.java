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

/**
 * Bounds shared by the paged table-record transports and SQL implementation.
 */
public final class TableRecordPage {
    public static final int DEFAULT_LIMIT = 25;
    public static final int MAX_LIMIT = 250;

    private TableRecordPage() {
        // Utility class.
    }

    /**
     * Validates the requested table-record page bounds.
     *
     * @param limit Maximum number of records to retrieve.
     * @param offset Zero-based offset of the first record.
     * @throws IllegalArgumentException If the limit or offset is outside the supported bounds.
     */
    public static void validate(int limit, int offset) {
        if (limit < 1 || limit > MAX_LIMIT || offset < 0) {
            throw new IllegalArgumentException("Table page requires limit 1..250 and offset >= 0.");
        }
    }
}
