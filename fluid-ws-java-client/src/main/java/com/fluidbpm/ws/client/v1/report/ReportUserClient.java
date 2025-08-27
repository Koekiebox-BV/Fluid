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

package com.fluidbpm.ws.client.v1.report;

import com.fluidbpm.program.api.vo.report.userstats.UserStatsReport;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * Java Web Service Client for {@code UserStatsReport} related actions.
 *
 * @author jasonbruwer
 * @see JsonObject
 * @see WS.Path.Report
 * @see UserStatsReport
 * @since v1.11
 */
public class ReportUserClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket   The Server issued Service Ticket.
     */
    public ReportUserClient(String endpointBaseUrl, String serviceTicket) {
        super(endpointBaseUrl);
        this.setServiceTicket(serviceTicket);
    }

    /**
     * Retrieve a system report of all up/down entries for the last 31 days.
     *
     * @return UserStatsReport information.
     * @see UserStatsReport
     */
    public UserStatsReport getLoggedInUserStats() {
        return this.getLoggedInUserStats(null, null);
    }

    /**
     * Retrieve a system report of all up/down entries from {@code fromDate} until {@code toDate}.
     *
     * @param fromDate The from date.
     * @param toDate   The to date.
     * @return UserStatsReport information.
     * @see UserStatsReport
     */
    public UserStatsReport getLoggedInUserStats(Date fromDate, Date toDate) {
        User userGetInfoFor = new User();
        userGetInfoFor.setServiceTicket(this.serviceTicket);
        return new UserStatsReport(this.postJson(
                userGetInfoFor, WS.Path.Report.Version1.getUserStatsReportForLoggedInUser(
                        fromDate == null ? -1 : fromDate.getTime(),
                        toDate == null ? -1 : toDate.getTime()))
        );
    }
}
