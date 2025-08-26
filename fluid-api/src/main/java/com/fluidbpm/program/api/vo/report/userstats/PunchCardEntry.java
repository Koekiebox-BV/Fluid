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
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * User statistic punch card entry.
 *
 * @author jasonbruwer on 2020-08-20
 * @since v1.11
 */
@Getter
@Setter
public class PunchCardEntry extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;

    private Date punchCardDay;
    private Date firstLoginForDay;
    private Date secondLastLogout;
    private Date secondLastLogin;
    private Date lastLogout;

    /**
     * The JSON mapping for the {@code PunchCardEntry} object.
     */
    public static class JSONMapping {
        public static final String PUNCH_CARD_DAY = "punchCardDay";
        public static final String FIRST_LOGIN_FOR_DAY = "firstLoginForDay";
        public static final String SECOND_LAST_LOGOUT = "secondLastLogout";
        public static final String SECOND_LAST_LOGIN = "secondLastLogin";
        public static final String LAST_LOGOUT = "lastLogout";
    }

    /**
     * Default constructor.
     */
    public PunchCardEntry() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public PunchCardEntry(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setPunchCardDay(this.getDateFieldValueFromFieldWithName(JSONMapping.PUNCH_CARD_DAY));

        this.setFirstLoginForDay(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.FIRST_LOGIN_FOR_DAY));

        this.setSecondLastLogout(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.SECOND_LAST_LOGOUT));

        this.setSecondLastLogin(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.SECOND_LAST_LOGIN));

        this.setLastLogout(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.LAST_LOGOUT));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code PunchCardEntry}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.PUNCH_CARD_DAY, returnVal, this.getDateAsLongFromJson(this.getPunchCardDay()));
        this.setAsProperty(JSONMapping.FIRST_LOGIN_FOR_DAY, returnVal, this.getDateAsLongFromJson(this.getFirstLoginForDay()));
        this.setAsProperty(JSONMapping.SECOND_LAST_LOGOUT, returnVal, this.getDateAsLongFromJson(this.getSecondLastLogout()));
        this.setAsProperty(JSONMapping.SECOND_LAST_LOGIN, returnVal, this.getDateAsLongFromJson(this.getSecondLastLogin()));
        this.setAsProperty(JSONMapping.LAST_LOGOUT, returnVal, this.getDateAsLongFromJson(this.getLastLogout()));

        return returnVal;
    }

    /**
     * Checks if all logins for the day is {@code null} / not set.
     *
     * @return {@code true} if FirstLoginForDay,SecondLastLogout,SecondLastLogin and LastLogout is all {@code null}.
     */
    public boolean isLogInsForDayEmpty() {
        return UtilGlobal.isAllNull(
                this.getFirstLoginForDay(),
                this.getSecondLastLogout(),
                this.getSecondLastLogin(),
                this.getLastLogout());
    }

    /**
     * Calculate the number of minutes a user was logged in for.
     *
     * @return int - Number of minutes from first login to logout.
     */
    public int getNumberOfMinutesLoggedInForDay() {
        int returnVal = 0;
        if (this.firstLoginForDay == null || this.lastLogout == null) {
            return returnVal;
        }
        return (int) TimeUnit.MILLISECONDS.toMinutes(this.lastLogout.getTime() - this.firstLoginForDay.getTime());
    }
}
