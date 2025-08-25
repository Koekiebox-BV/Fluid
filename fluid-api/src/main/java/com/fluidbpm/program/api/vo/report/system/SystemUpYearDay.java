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

package com.fluidbpm.program.api.vo.report.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * User statistics.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ABaseFluidJSONObject
 * @since v1.11
 */
@Getter
@Setter
public class SystemUpYearDay extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;
    private int year;
    private int day;
    private List<SystemUpHourMin> systemUpHourMins;

    /**
     * The JSON mapping for the {@code SystemUpYearDay} object.
     */
    public static class JSONMapping {
        public static final String YEAR = "year";
        public static final String DAY = "day";
        public static final String SYSTEM_UP_HOUR_MINS = "systemUpHourMins";
    }

    /**
     * Default constructor.
     */
    public SystemUpYearDay() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SystemUpYearDay(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setYear(this.getAsIntegerNullSafe(JSONMapping.YEAR));
        this.setDay(this.getAsIntegerNullSafe(JSONMapping.DAY));
        this.setSystemUpHourMins(this.extractObjects(JSONMapping.SYSTEM_UP_HOUR_MINS, SystemUpHourMin::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code SystemUpYearDay}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.YEAR, returnVal, this.getYear());
        this.setAsProperty(JSONMapping.DAY, returnVal, this.getDay());
        this.setAsObjArray(JSONMapping.SYSTEM_UP_HOUR_MINS, returnVal, this::getSystemUpHourMins);

        return returnVal;
    }

    /**
     * Comparing the year and day.
     *
     * @return {@code ((this.getYear() * 1000) + this.getDay())}
     */
    public long comparingYearDay() {
        return ((this.getYear() * 1000) + this.getDay());
    }

    /**
     * Returns the year and day as {@code String}.
     *
     * @return {@code year-day}
     */
    public String getYearDayKey() {
        return ("" + this.getYear() + "-" + this.getDay());
    }
}
