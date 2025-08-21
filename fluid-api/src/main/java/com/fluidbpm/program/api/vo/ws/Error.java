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

package com.fluidbpm.program.api.vo.ws;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * <p>
 *     The Mapping object used for any errors thrown
 *     from the Fluid Web Service.
 *
 *     A check may be added to check whether {@code errorCode}
 *     is present.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidJSONObject
 * @see WS
 */
public class Error extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private int errorCode;
    private String errorMessage;

    /**
     * The JSON mapping for {@code this} {@code Error} object.
     */
    public static class JSONMapping
    {
        public static final String ERROR_CODE = "errorCode";
        public static final String ERROR_MESSAGE = "errorMessage";

        public static final String ERROR_CODE_OTHER = "error_code";
        public static final String ERROR_MESSAGE_OTHER = "error_message";
    }

    /**
     * Default constructor.
     */
    public Error() {
        super();
    }

    /**
     * Sets the Error Code and Message.
     *
     * @param errorCode Error Code.
     * @param errorMessage Error Message.
     */
    public Error(int errorCode, String errorMessage) {
        this.setErrorCode(errorCode);
        this.setErrorMessage(errorMessage);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Error(JsonObject jsonObjectParam) {
        super(jsonObjectParam);

        //Error Code...
        if (!this.jsonObject.isNull(JSONMapping.ERROR_CODE)) {
            this.setErrorCode(
                    Long.valueOf(this.jsonObject.getLong(JSONMapping.ERROR_CODE)).intValue());
        }
        else if (!this.jsonObject.isNull(JSONMapping.ERROR_CODE_OTHER)) {
            this.setErrorCode(
                    Long.valueOf(this.jsonObject.getLong(JSONMapping.ERROR_CODE_OTHER)).intValue());
        }

        //Error Message...
        if (!this.jsonObject.isNull(JSONMapping.ERROR_MESSAGE)) {
            this.setErrorMessage(this.jsonObject.getString(JSONMapping.ERROR_MESSAGE));
        }
        else if (!this.jsonObject.isNull(JSONMapping.ERROR_MESSAGE_OTHER)) {
            this.setErrorMessage(this.jsonObject.getString(JSONMapping.ERROR_MESSAGE_OTHER));
        }
    }

    /**
     * Gets the Error Code.
     *
     * @return Error Code.
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Sets the Error Code.
     *
     * @param errorCode Error Code.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the Error Message.
     *
     * @return Error Message.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Sets the Error Message.
     *
     * @param errorMessage Error Message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JSONObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JSONObject} representation of {@code ABaseFluidJSONObject}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see org.json.JSONObject
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {

        JsonObject returnVal = super.toJsonObject();

        returnVal.put(JSONMapping.ERROR_CODE, this.getErrorCode());
        returnVal.put(JSONMapping.ERROR_CODE_OTHER, this.getErrorCode());

        //Error Message...
        if (this.getErrorMessage() != null)
        {
            returnVal.put(JSONMapping.ERROR_MESSAGE,this.getErrorMessage());
            returnVal.put(JSONMapping.ERROR_MESSAGE_OTHER,this.getErrorMessage());
        }

        return returnVal;
    }
}
