/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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

package com.fluid.program.api.vo;

import java.util.List;

/**
 * <p>
 *     Represents a {@code Form} Table Field.
 *     Table fields have the ability to store other {@code Field}s inside itself.
 *     This allows for a much richer Electronic Form.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see com.fluid.program.api.util.sql.impl.SQLFormFieldUtil
 */
public class TableField extends ABaseFluidVO {

    private List<Form> tableRecords;

    /**
     * Default constructor.
     */
    public TableField() {
        super();
    }

    /**
     * Gets List of Table Records for {@code TableField}.
     *
     * @return {@code List} of {@code Form}s for {@code TableField}.
     */
    public List<Form> getTableRecords() {
        return this.tableRecords;
    }

    /**
     * Gets List of Table Records for {@code TableField}.
     *
     * @param tableRecordsParam {@code List} of {@code Form}s for {@code TableField}.
     */
    public void setTableRecords(List<Form> tableRecordsParam) {
        this.tableRecords = tableRecordsParam;
    }
}
