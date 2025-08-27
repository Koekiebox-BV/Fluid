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
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * User statistics.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ABaseFluidGSONObject
 * @since v1.11
 */
@Getter
@Setter
public class SystemUpHourMin extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private int hour;
    private int minute;
    private State state = State.Unknown;

    public static enum State {
        Unknown, Up, Down
    }

    /**
     * The JSON mapping for the {@code SystemUpHourMin} object.
     */
    public static class JSONMapping {
        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";
        public static final String STATE = "state";
    }

    /**
     * Default constructor.
     */
    public SystemUpHourMin() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SystemUpHourMin(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) {
            return;
        }

        this.setHour(this.getAsIntegerNullSafeStrictVal(JSONMapping.HOUR));
        this.setMinute(this.getAsIntegerNullSafeStrictVal(JSONMapping.MINUTE));
        
        String stateStr = this.getAsStringNullSafe(JSONMapping.STATE);
        if (stateStr != null && !stateStr.isEmpty()) {
            this.setState(State.valueOf(stateStr));
        } else {
            this.setState(State.Unknown);
        }
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code SystemUpHourMin}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.HOUR, returnVal, this.getHour());
        this.setAsProperty(JSONMapping.MINUTE, returnVal, this.getMinute());
        if (this.getState() != null) {
            this.setAsProperty(JSONMapping.STATE, returnVal, this.getState().toString());
        }

        return returnVal;
    }

    /**
     * Comparing the hour and minute.
     *
     * @return {@code ((this.getHour() * 100) + this.getMinute())}
     */
    public long comparingHourMinute() {
        return ((this.getHour() * 100) + this.getMinute());
    }
}