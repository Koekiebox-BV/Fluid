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
import java.util.List;

/**
 * User statistics report.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ViewOpenedAndSentOnEntry
 * @see PunchCardEntry
 * @see CreateUpdateLockUnlockEntry
 * @since v1.11
 */
@Getter
@Setter
public class UserStatsReport extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private List<PunchCardEntry> punchCardEntries;
    private List<ViewOpenedAndSentOnEntry> viewOpenedAndSentOnEntries;
    private List<CreateUpdateLockUnlockEntry> createUpdateLockUnlockEntries;

    private int numberOfLogins;
    private int numberOfLoginsPrevCycle;
    private int piCount;
    private int piLockedCount;

    /**
     * The JSON mapping for the {@code UserStatsReport} object.
     */
    public static class JSONMapping {
        public static final String PUNCH_CARD_ENTRIES = "punchCardEntries";
        public static final String VIEW_OPENED_AND_SENT_ON_ENTRIES = "viewOpenedAndSentOnEntries";
        public static final String CREATE_UPDATE_LOCK_UNLOCK_ENTRIES = "createUpdateLockUnlockEntries";


        public static final String NUMBER_OF_LOGINS = "numberOfLogins";
        public static final String NUMBER_OF_LOGINS_PREV_CYCLE = "numberOfLoginsPrevCycle";
        public static final String PI_COUNT = "piCount";
        public static final String PI_LOCKED_COUNT = "piLockedCount";
    }

    /**
     * Default constructor.
     */
    public UserStatsReport() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public UserStatsReport(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setNumberOfLogins(this.getAsIntegerNullSafeStrictVal(JSONMapping.NUMBER_OF_LOGINS));
        this.setNumberOfLoginsPrevCycle(this.getAsIntegerNullSafeStrictVal(JSONMapping.NUMBER_OF_LOGINS_PREV_CYCLE));
        this.setPiCount(this.getAsIntegerNullSafeStrictVal(JSONMapping.PI_COUNT));
        this.setPiLockedCount(this.getAsIntegerNullSafeStrictVal(JSONMapping.PI_LOCKED_COUNT));

        this.setPunchCardEntries(this.extractObjects(JSONMapping.PUNCH_CARD_ENTRIES, PunchCardEntry::new));
        this.setViewOpenedAndSentOnEntries(this.extractObjects(JSONMapping.VIEW_OPENED_AND_SENT_ON_ENTRIES, ViewOpenedAndSentOnEntry::new));
        this.setCreateUpdateLockUnlockEntries(this.extractObjects(JSONMapping.CREATE_UPDATE_LOCK_UNLOCK_ENTRIES, CreateUpdateLockUnlockEntry::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code UserStatsReport}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.NUMBER_OF_LOGINS, returnVal, this.getNumberOfLogins());
        this.setAsProperty(JSONMapping.NUMBER_OF_LOGINS_PREV_CYCLE, returnVal, this.getNumberOfLoginsPrevCycle());
        this.setAsProperty(JSONMapping.PI_COUNT, returnVal, this.getPiCount());
        this.setAsProperty(JSONMapping.PI_LOCKED_COUNT, returnVal, this.getPiLockedCount());

        this.setAsObjArray(JSONMapping.PUNCH_CARD_ENTRIES, returnVal, this::getPunchCardEntries);
        this.setAsObjArray(JSONMapping.VIEW_OPENED_AND_SENT_ON_ENTRIES, returnVal, this::getViewOpenedAndSentOnEntries);
        this.setAsObjArray(JSONMapping.CREATE_UPDATE_LOCK_UNLOCK_ENTRIES, returnVal, this::getCreateUpdateLockUnlockEntries);

        return returnVal;
    }
}
