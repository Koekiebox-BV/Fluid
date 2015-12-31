package com.fluid.program.api.vo;

import java.util.List;

/**
 *
 */
public class TableField extends ABaseFluidVO {

    private List<Form> tableRecords;

    /**
     *
     * @return
     */
    public List<Form> getTableRecords() {
        return this.tableRecords;
    }

    /**
     *
     * @param tableRecordsParam
     */
    public void setTableRecords(List<Form> tableRecordsParam) {
        this.tableRecords = tableRecordsParam;
    }
}
