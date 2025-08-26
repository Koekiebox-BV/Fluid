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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
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
public class CustomRunnerAction extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

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
    public CustomRunnerAction(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setActionIdentifier(this.getAsStringNullSafe(JSONMapping.ACTION_IDENTIFIER));
        this.setActionLocation(this.getAsStringNullSafe(JSONMapping.ACTION_LOCATION));
        this.setActionType(this.getAsStringNullSafe(JSONMapping.ACTION_TYPE));

        this.setFormDefinitions(this.extractStrings(JSONMapping.FORM_DEFINITIONS));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code CustomRunnerAction}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.ACTION_IDENTIFIER, returnVal, this.getActionIdentifier());
        this.setAsProperty(JSONMapping.ACTION_LOCATION, returnVal, this.getActionLocation());
        this.setAsProperty(JSONMapping.ACTION_TYPE, returnVal, this.getActionType());
        this.setAsStringArray(JSONMapping.FORM_DEFINITIONS, returnVal, this.getFormDefinitions());

        return returnVal;
    }
}
