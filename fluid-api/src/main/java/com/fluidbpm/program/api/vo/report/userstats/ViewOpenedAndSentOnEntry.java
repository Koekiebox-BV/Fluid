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
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * User statistics.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ABaseFluidJSONObject
 * @since v1.11
 */
@Getter
@Setter
public class ViewOpenedAndSentOnEntry extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private String viewName;
    private int viewClicks;
    private int openedFromViewCounts;
    private int sentOn;

    /**
     * The JSON mapping for the {@code UserStatsReport} object.
     */
    public static class JSONMapping {
        public static final String VIEW_NAME = "viewName";
        public static final String VIEW_CLICKS = "viewClicks";
        public static final String OPENED_FROM_VIEW_COUNTS = "openedFromViewCounts";
        public static final String SENT_ON = "sentOn";
    }

    /**
     * Default constructor.
     */
    public ViewOpenedAndSentOnEntry() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ViewOpenedAndSentOnEntry(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setViewName(this.getAsStringNullSafe(JSONMapping.VIEW_NAME));
        this.setViewClicks(this.getAsIntegerNullSafe(JSONMapping.VIEW_CLICKS) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.VIEW_CLICKS));
        this.setOpenedFromViewCounts(this.getAsIntegerNullSafe(JSONMapping.OPENED_FROM_VIEW_COUNTS) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.OPENED_FROM_VIEW_COUNTS));
        this.setSentOn(this.getAsIntegerNullSafe(JSONMapping.SENT_ON) == null ? 0 : this.getAsIntegerNullSafe(JSONMapping.SENT_ON));
    }


    /**
     * @return percentage of completeness as {@code int}.
     */
    public int getPercentageOfComplete() {
        if (this.sentOn < 1 || this.openedFromViewCounts < 1) {
            return 0;
        }

        float decimal = ((float) this.sentOn / (float) this.openedFromViewCounts);
        if (decimal == 1) return 100;

        String stringVal = Float.toString(decimal).substring(2);
        if (stringVal.length() > 1) {
            return UtilGlobal.toIntSafe(stringVal.substring(0, 2));
        }
        return UtilGlobal.toIntSafe(stringVal.substring(0, 1).concat(UtilGlobal.ZERO));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code ViewOpenedAndSentOnEntry}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.VIEW_NAME, returnVal, this.getViewName());
        this.setAsProperty(JSONMapping.VIEW_CLICKS, returnVal, this.getViewClicks());
        this.setAsProperty(JSONMapping.OPENED_FROM_VIEW_COUNTS, returnVal, this.getOpenedFromViewCounts());
        this.setAsProperty(JSONMapping.SENT_ON, returnVal, this.getSentOn());

        return returnVal;
    }
}
