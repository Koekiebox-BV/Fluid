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
 * forbidden unless prior written permission is obtained from Koekiebox Innovations.
 */

package com.fluidbpm.program.api.vo.health.thirdparty;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom runner packet that will provide destination environment info.
 *
 * @author jasonbruwer on 8/31/17.
 * @since 1.2
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomRunnerAction extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String actionIdentifier;
    private String actionLocation;
    private String actionType;

    private List<String> formDefinitions;

    /**
     * The JSON mapping for the {@code CustomRunnerAction} object.
     */
    public static class JSONMapping {
        public static final String ACTION_IDENTIFIER = "actionIdentifier";
        public static final String ACTION_LOCATION = "actionLocation";
        public static final String ACTION_TYPE = "actionType";
        
        public static final String FORM_DEFINITIONS = "formDefinitions";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public CustomRunnerAction(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if (this.jsonObject == null) return;

        //Action Identifier...
        if (!this.jsonObject.isNull(JSONMapping.ACTION_IDENTIFIER)) {
            this.setActionIdentifier(this.jsonObject.getString(JSONMapping.ACTION_IDENTIFIER));
        }

        //Action Location...
        if (!this.jsonObject.isNull(JSONMapping.ACTION_LOCATION)) {
            this.setActionLocation(this.jsonObject.getString(JSONMapping.ACTION_LOCATION));
        }

        //Action Type...
        if (!this.jsonObject.isNull(JSONMapping.ACTION_TYPE)) {
            this.setActionType(this.jsonObject.getString(JSONMapping.ACTION_TYPE));
        }

        //Form Definitions...
        if (!this.jsonObject.isNull(JSONMapping.FORM_DEFINITIONS)) {
            JSONArray jsonPropArray = this.jsonObject.getJSONArray(JSONMapping.FORM_DEFINITIONS);
            List<String> formDefinitions = new ArrayList();
            for (int index = 0;index < jsonPropArray.length();index++) {
                formDefinitions.add(jsonPropArray.getString(index));
            }
            this.setFormDefinitions(formDefinitions);
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code CustomRunnerAction}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnVal = super.toJsonObject();

        //Action Identifier...
        if (this.getActionIdentifier() != null) {
            returnVal.put(JSONMapping.ACTION_IDENTIFIER, this.getActionIdentifier());
        }

        //Action Location...
        if (this.getActionLocation() != null) {
            returnVal.put(JSONMapping.ACTION_LOCATION, this.getActionLocation());
        }

        //Action Type...
        if (this.getActionType() != null) {
            returnVal.put(JSONMapping.ACTION_TYPE, this.getActionType());
        }

        //Form Definitions...
        if (this.getFormDefinitions() != null && !this.getFormDefinitions().isEmpty()) {
            JSONArray formDeffsArr = new JSONArray();
            for (String toAdd :this.getFormDefinitions()) formDeffsArr.put(toAdd);
            returnVal.put(JSONMapping.FORM_DEFINITIONS, formDeffsArr);
        }
        
        return returnVal;
    }
}
