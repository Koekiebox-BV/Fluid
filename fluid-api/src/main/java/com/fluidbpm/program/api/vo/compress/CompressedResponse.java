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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fluid wrapper object for compressed {@code JSON} in {@code Base-64} format.
 *
 * @author jasonbruwer
 * @since v1.8
 */
public class CompressedResponse extends ABaseFluidJSONObject {
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
    public CompressedResponse(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if (this.jsonObject == null) {
            return;
        }

        //Data-64...
        if (!this.jsonObject.isNull(JSONMapping.DATA_BASE_64)) {
            this.setDataBase64(this.jsonObject.getString(JSONMapping.DATA_BASE_64));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MailMessageAttachment}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnVal = super.toJsonObject();

        //Data Base64...
        if (this.getDataBase64() != null) {
            returnVal.put(JSONMapping.DATA_BASE_64,
                    this.getDataBase64());
        }

        return returnVal;
    }

    /**
     * Gets the data in {@code Base-64} format.
     *
     * @return Base-64 attachment content.
     */
    public String getDataBase64() {
        return this.dataBase64;
    }

    /**
     * Sets the data in {@code Base-64} format.
     *
     * @param attachmentDataBase64Param Base-64 attachment content.
     */
    public void setDataBase64(String attachmentDataBase64Param) {
        this.dataBase64 = attachmentDataBase64Param;
    }
}
