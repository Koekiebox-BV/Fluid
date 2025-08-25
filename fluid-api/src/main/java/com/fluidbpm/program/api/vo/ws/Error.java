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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

/**
 * <p>
 * The Mapping object used for any errors thrown
 * from the Fluid Web Service.
 * <p>
 * A check may be added to check whether {@code errorCode}
 * is present.
 *
 * @author jasonbruwer
 * @see ABaseFluidJSONObject
 * @see WS
 * @since v1.0
 */
@Getter
@Setter
public class Error extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private int errorCode;
    private String errorMessage;

    /**
     * The JSON mapping for {@code this} {@code Error} object.
     */
    public static class JSONMapping {
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
     * @param errorCode    Error Code.
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
        if (this.jsonObject == null) return;

        //Error Code...
        Long errorCodeLong = this.getAsLongNullSafe(JSONMapping.ERROR_CODE);
        if (errorCodeLong == null) {
            errorCodeLong = this.getAsLongNullSafe(JSONMapping.ERROR_CODE_OTHER);
        }
        if (errorCodeLong != null) {
            this.setErrorCode(errorCodeLong.intValue());
        }

        //Error Message...
        String errorMessage = this.getAsStringNullSafe(JSONMapping.ERROR_MESSAGE);
        if (errorMessage == null) {
            errorMessage = this.getAsStringNullSafe(JSONMapping.ERROR_MESSAGE_OTHER);
        }
        this.setErrorMessage(errorMessage);
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JsonObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JsonObject} representation of {@code ABaseFluidGSONObject}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see com.google.gson.JsonObject
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.ERROR_CODE, returnVal, this.getErrorCode());
        this.setAsProperty(JSONMapping.ERROR_CODE_OTHER, returnVal, this.getErrorCode());

        //Error Message...
        this.setAsProperty(JSONMapping.ERROR_MESSAGE, returnVal, this.getErrorMessage());
        this.setAsProperty(JSONMapping.ERROR_MESSAGE_OTHER, returnVal, this.getErrorMessage());

        return returnVal;
    }
}
