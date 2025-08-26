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

import com.fluidbpm.program.api.vo.compress.CompressedResponse;
import com.fluidbpm.program.api.vo.report.ABaseFluidGSONReportObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import java.util.List;

/**
 * System uptime data.
 *
 * @author jasonbruwer on 2020-08-20
 * @see ABaseFluidGSONObject
 * @since v1.11
 */
@Getter
@Setter
public class SystemUptimeReport extends ABaseFluidGSONReportObject {
    private static final long serialVersionUID = 1L;
    //yyyy-DDD-kk-mm
    private List<SystemUpYearDay> uptimeEntries;
    private CompressedResponse compressedResponse;

    /**
     * The JSON mapping for the {@code SystemUptimeReport} object.
     */
    public static class JSONMapping {
        public static final String UPTIME_ENTRIES = "uptimeEntries";
        public static final String COMPRESSED_RESPONSE = "compressedResponse";
    }

    /**
     * Default constructor.
     */
    public SystemUptimeReport() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public SystemUptimeReport(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setUptimeEntries(this.extractObjects(JSONMapping.UPTIME_ENTRIES, SystemUpYearDay::new));
        this.setCompressedResponse(this.extractObject(JSONMapping.COMPRESSED_RESPONSE, CompressedResponse::new));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code SystemUptimeReport}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        this.setAsObjArray(JSONMapping.UPTIME_ENTRIES, returnVal, this::getUptimeEntries);
        this.setAsObj(JSONMapping.COMPRESSED_RESPONSE, returnVal, this::getCompressedResponse);
        return returnVal;
    }
}
