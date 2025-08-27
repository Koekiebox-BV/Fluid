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

package com.fluidbpm.program.api.vo.compress;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Fluid wrapper object for compressed {@code JSON} in {@code Base-64} format.
 *
 * @author jasonbruwer
 * @since v1.8
 */
@Getter
@Setter
public class CompressedResponse extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private String dataBase64;
    public static String DEFAULT_ZIP_ENTRY_NAME = "response.json";

    /**
     * The JSON mapping for the {@code CompressedResponse} object.
     */
    public static class JSONMapping {
        public static final String DATA_BASE_64 = "dataBase64";
    }

    /**
     * Default constructor.
     */
    public CompressedResponse() {
        super();
    }

    /**
     * Sets compressed data as Base-64.
     *
     * @param dataBase64Param Path to the attachment.
     */
    public CompressedResponse(String dataBase64Param) {
        super();
        this.setDataBase64(dataBase64Param);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public CompressedResponse(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setDataBase64(this.getAsStringNullSafe(JSONMapping.DATA_BASE_64));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MailMessageAttachment}
     * 
     * 
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.DATA_BASE_64, returnVal, this.getDataBase64());
        return returnVal;
    }
}
