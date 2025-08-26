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

package com.fluidbpm.program.api.vo.report;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import org.json.JSONObject;

/**
 * <p>
 * The Base class for any sub-class that wants to make use of the
 * JSON based message format used by the Fluid RESTful Web Service for reports.
 *
 * @author jasonbruwer
 * @see JSONObject
 * @since v1.11
 */
public abstract class ABaseFluidGSONReportObject extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ABaseFluidGSONReportObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidGSONReportObject(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }
}
