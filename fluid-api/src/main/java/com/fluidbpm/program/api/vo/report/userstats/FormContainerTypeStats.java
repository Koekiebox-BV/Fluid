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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.report.ABaseFluidJSONReportObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User statistics for form instance stats.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ABaseFluidJSONObject
 * @since v1.11
 */
@Getter
@Setter
public class FormContainerTypeStats extends ABaseFluidJSONReportObject {
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
    public FormContainerTypeStats(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) {
            return;
        }

        if (this.jsonObject.isNull(JSONMapping.FORM_CONTAINER_TYPE)) {
            this.setFormContainerType(null);
        } else {
            this.setFormContainerType(this.jsonObject.getString(JSONMapping.FORM_CONTAINER_TYPE));
        }

        if (this.jsonObject.isNull(JSONMapping.COUNT_CREATE)) {
            this.setCountCreate(0);
        } else {
            this.setCountCreate(this.jsonObject.getInt(JSONMapping.COUNT_CREATE));
        }

        if (this.jsonObject.isNull(JSONMapping.COUNT_UPDATE)) {
            this.setCountUpdate(0);
        } else {
            this.setCountUpdate(this.jsonObject.getInt(JSONMapping.COUNT_UPDATE));
        }

        if (this.jsonObject.isNull(JSONMapping.COUNT_LOCK)) {
            this.setCountLock(0);
        } else {
            this.setCountLock(this.jsonObject.getInt(JSONMapping.COUNT_LOCK));
        }

        if (this.jsonObject.isNull(JSONMapping.COUNT_UNLOCK)) {
            this.setCountUnlock(0);
        } else {
            this.setCountUnlock(this.jsonObject.getInt(JSONMapping.COUNT_UNLOCK));
        }

        if (this.jsonObject.isNull(JSONMapping.DUPLICATE_LOCKS)) {
            this.setDuplicateLocks(0);
        } else {
            this.setDuplicateLocks(this.jsonObject.getInt(JSONMapping.DUPLICATE_LOCKS));
        }

        if (this.jsonObject.isNull(JSONMapping.DUPLICATE_UN_LOCKS)) {
            this.setDuplicateUnLocks(0);
        } else {
            this.setDuplicateUnLocks(this.jsonObject.getInt(JSONMapping.DUPLICATE_UN_LOCKS));
        }

        if (this.jsonObject.isNull(JSONMapping.DUPLICATE_UPDATES)) {
            this.setDuplicateUpdates(0);
        } else {
            this.setDuplicateUpdates(this.jsonObject.getInt(JSONMapping.DUPLICATE_UPDATES));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code PunchCardEntry}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnVal = super.toJsonObject();

        if (this.getFormContainerType() != null) {
            returnVal.put(JSONMapping.FORM_CONTAINER_TYPE, this.getFormContainerType());
        }

        returnVal.put(JSONMapping.COUNT_CREATE, this.getCountCreate());
        returnVal.put(JSONMapping.COUNT_LOCK, this.getCountLock());
        returnVal.put(JSONMapping.COUNT_UNLOCK, this.getCountUnlock());
        returnVal.put(JSONMapping.COUNT_UPDATE, this.getCountUpdate());

        returnVal.put(JSONMapping.DUPLICATE_LOCKS, this.getDuplicateLocks());
        returnVal.put(JSONMapping.DUPLICATE_UN_LOCKS, this.getDuplicateUnLocks());
        returnVal.put(JSONMapping.DUPLICATE_UPDATES, this.getDuplicateUpdates());

        return returnVal;
    }
}
