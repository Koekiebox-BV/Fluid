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

package com.fluidbpm.program.api.vo.report.userstats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * User statistics for form entry types.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 */
@Getter
@Setter
public class FormEntryTypeStats extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private String formContainerType;
    private int countDocument;
    private int countFolder;
    private int countTableRecord;

    /**
     * The JSON mapping for the {@code FormEntryTypeStats} object.
     */
    public static class JSONMapping {
        public static final String FORM_CONTAINER_TYPE = "formContainerType";
        public static final String COUNT_DOCUMENT = "countDocument";
        public static final String COUNT_FOLDER = "countFolder";
        public static final String COUNT_TABLE_RECORD = "countTableRecord";
    }

    /**
     * Default constructor.
     */
    public FormEntryTypeStats() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormEntryTypeStats(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFormContainerType(this.getAsStringNullSafe(JSONMapping.FORM_CONTAINER_TYPE));
        this.setCountDocument(this.getAsIntegerNullSafeStrictVal(JSONMapping.COUNT_DOCUMENT));
        this.setCountFolder(this.getAsIntegerNullSafeStrictVal(JSONMapping.COUNT_FOLDER));
        this.setCountTableRecord(this.getAsIntegerNullSafeStrictVal(JSONMapping.COUNT_TABLE_RECORD));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FormEntryTypeStats}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.FORM_CONTAINER_TYPE, returnVal, this.getFormContainerType());
        this.setAsProperty(JSONMapping.COUNT_DOCUMENT, returnVal, this.getCountDocument());
        this.setAsProperty(JSONMapping.COUNT_FOLDER, returnVal, this.getCountFolder());
        this.setAsProperty(JSONMapping.COUNT_TABLE_RECORD, returnVal, this.getCountTableRecord());

        return returnVal;
    }
}
