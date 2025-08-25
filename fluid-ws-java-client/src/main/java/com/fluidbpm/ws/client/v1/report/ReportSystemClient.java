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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.report.system.SystemUptimeReport;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Java Web Service Client for {@code SystemUptimeReport} related actions.
 *
 * @author jasonbruwer
 * @see JSONObject
 * @see WS.Path.Report
 * @see SystemUptimeReport
 * @since v1.11
 */
public class ReportSystemClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam   The Server issued Service Ticket.
     */
    public ReportSystemClient(
            String endpointBaseUrlParam,
            String serviceTicketParam
    ) {
        super(endpointBaseUrlParam);
        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Retrieve a system report of all up/down entries for the last 31 days.
     *
     * @return SystemUptimeReport information.
     * @see SystemUptimeReport
     */
    public SystemUptimeReport getSystemReport() {
        return this.getSystemReport(false, null);
    }

    /**
     * Retrieve a system report of all up/down entries for the last 31 days.
     *
     * @param compressResponseParam        Compress the Form Field Result in Base-64.
     * @param compressResponseCharsetParam Compress response using provided charset.
     * @return SystemUptimeReport information.
     * @see SystemUptimeReport
     */
    public SystemUptimeReport getSystemReport(
            boolean compressResponseParam,
            String compressResponseCharsetParam
    ) {
        User userGetInfoFor = new User();
        if (this.serviceTicket != null) {
            userGetInfoFor.setServiceTicket(this.serviceTicket);
        }
        try {
            SystemUptimeReport wsReturnVal = new SystemUptimeReport(this.postJson(
                    userGetInfoFor, WS.Path.Report.Version1.getAll(
                            compressResponseParam,
                            compressResponseCharsetParam)));
            if (compressResponseParam) {
                String base64Data = (wsReturnVal.getCompressedResponse() == null) ? null :
                        wsReturnVal.getCompressedResponse().getDataBase64();
                try {
                    byte[] uncompressed = UtilGlobal.uncompress(BaseEncoding.base64().decode(base64Data), null);
                    return new SystemUptimeReport(
                            JsonParser.parseString(new String(uncompressed)).getAsJsonObject()
                    );
                } catch (IOException ioErr) {
                    throw new FluidClientException(String.format(
                            "Unable to unzip compressed data. %s",
                            ioErr.getMessage()), ioErr, FluidClientException.ErrorCode.IO_ERROR);
                }
            }

            return wsReturnVal;
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieve a system report of all up entries for the last 31 days.
     *
     * @return SystemUptimeReport information.
     * @see SystemUptimeReport
     */
    public SystemUptimeReport getUptimeSystemReport() {
        User userGetInfoFor = new User();
        if (this.serviceTicket != null) {
            userGetInfoFor.setServiceTicket(this.serviceTicket);
        }
        try {
            return new SystemUptimeReport(this.postJson(
                    userGetInfoFor, WS.Path.Report.Version1.getAllSystemUptime()));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieve a system report of all downtime entries for the last 31 days.
     *
     * @return SystemUptimeReport information.
     * @see SystemUptimeReport
     */
    public SystemUptimeReport getDowntimeSystemReport() {
        User userGetInfoFor = new User();
        if (this.serviceTicket != null) {
            userGetInfoFor.setServiceTicket(this.serviceTicket);
        }
        try {
            return new SystemUptimeReport(this.postJson(
                    userGetInfoFor, WS.Path.Report.Version1.getAllSystemDowntime()));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
