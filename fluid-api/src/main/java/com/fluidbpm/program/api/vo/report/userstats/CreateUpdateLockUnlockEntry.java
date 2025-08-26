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
import java.util.Date;
import java.util.List;

/**
 * User statistics for form update/lock and unlock.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 */
@Getter
@Setter
public class CreateUpdateLockUnlockEntry extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private Date entryDay;
    private List<FormContainerTypeStats> formContainerTypeStats;
    private List<FormEntryTypeStats> formEntryTypeStats;

    /**
     * The JSON mapping for the {@code CreateUpdateLockUnlockEntry} object.
     */
    public static class JSONMapping {
        public static final String ENTRY_DAY = "entryDay";
        public static final String FORM_CONTAINER_TYPE_STATS = "formContainerTypeStats";
        public static final String FORM_ENTRY_TYPE_STATS = "formEntryTypeStats";
    }

    /**
     * Default constructor.
     */
    public CreateUpdateLockUnlockEntry() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public CreateUpdateLockUnlockEntry(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) {
            return;
        }
        this.setEntryDay(this.getDateFieldValueFromFieldWithName(JSONMapping.ENTRY_DAY));
        this.setFormContainerTypeStats(this.extractObjects(JSONMapping.FORM_CONTAINER_TYPE_STATS, FormContainerTypeStats::new));
        this.setFormEntryTypeStats(this.extractObjects(JSONMapping.FORM_ENTRY_TYPE_STATS, FormEntryTypeStats::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code CreateUpdateLockUnlockEntry}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.ENTRY_DAY, returnVal, this.getDateAsLongFromJson(this.getEntryDay()));
        this.setAsObjArray(JSONMapping.FORM_CONTAINER_TYPE_STATS, returnVal, this::getFormContainerTypeStats);
        this.setAsObjArray(JSONMapping.FORM_ENTRY_TYPE_STATS, returnVal, this::getFormEntryTypeStats);

        return returnVal;
    }
}
