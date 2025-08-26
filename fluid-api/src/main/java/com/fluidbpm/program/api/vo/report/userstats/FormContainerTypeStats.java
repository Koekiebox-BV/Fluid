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
 * User statistics for form instance stats.
 *
 * @author jasonbruwer on 2020-08-20
 * @see com.fluidbpm.program.api.vo.ABaseFluidGSONObject
 * @since v1.11
 */
@Getter
@Setter
public class FormContainerTypeStats extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private String formContainerType;

    private int countCreate;
    private int countUpdate;
    private int countLock;
    private int countUnlock;

    //Duplicates...
    private int duplicateLocks;
    private int duplicateUnLocks;
    private int duplicateUpdates;

    /**
     * The JSON mapping for the {@code FormContainerTypeStats} object.
     */
    public static class JSONMapping {
        public static final String FORM_CONTAINER_TYPE = "formContainerType";

        public static final String COUNT_CREATE = "countCreate";
        public static final String COUNT_UPDATE = "countUpdate";
        public static final String COUNT_LOCK = "countLock";
        public static final String COUNT_UNLOCK = "countUnlock";

        public static final String DUPLICATE_LOCKS = "duplicateLocks";
        public static final String DUPLICATE_UN_LOCKS = "duplicateUnLocks";
        public static final String DUPLICATE_UPDATES = "duplicateUpdates";
    }

    /**
     * Default constructor.
     */
    public FormContainerTypeStats() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormContainerTypeStats(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) {
            return;
        }

        this.setFormContainerType(this.getAsStringNullSafe(JSONMapping.FORM_CONTAINER_TYPE));
        this.setCountCreate(this.getAsIntegerNullSafe(JSONMapping.COUNT_CREATE) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.COUNT_CREATE));
        this.setCountUpdate(this.getAsIntegerNullSafe(JSONMapping.COUNT_UPDATE) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.COUNT_UPDATE));
        this.setCountLock(this.getAsIntegerNullSafe(JSONMapping.COUNT_LOCK) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.COUNT_LOCK));
        this.setCountUnlock(this.getAsIntegerNullSafe(JSONMapping.COUNT_UNLOCK) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.COUNT_UNLOCK));
        this.setDuplicateLocks(this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_LOCKS) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_LOCKS));
        this.setDuplicateUnLocks(this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_UN_LOCKS) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_UN_LOCKS));
        this.setDuplicateUpdates(this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_UPDATES) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.DUPLICATE_UPDATES));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FormContainerTypeStats}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.FORM_CONTAINER_TYPE, returnVal, this.getFormContainerType());
        this.setAsProperty(JSONMapping.COUNT_CREATE, returnVal, this.getCountCreate());
        this.setAsProperty(JSONMapping.COUNT_LOCK, returnVal, this.getCountLock());
        this.setAsProperty(JSONMapping.COUNT_UNLOCK, returnVal, this.getCountUnlock());
        this.setAsProperty(JSONMapping.COUNT_UPDATE, returnVal, this.getCountUpdate());
        this.setAsProperty(JSONMapping.DUPLICATE_LOCKS, returnVal, this.getDuplicateLocks());
        this.setAsProperty(JSONMapping.DUPLICATE_UN_LOCKS, returnVal, this.getDuplicateUnLocks());
        this.setAsProperty(JSONMapping.DUPLICATE_UPDATES, returnVal, this.getDuplicateUpdates());

        return returnVal;
    }
}
