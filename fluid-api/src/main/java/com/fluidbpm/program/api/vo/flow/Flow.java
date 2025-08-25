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

package com.fluidbpm.program.api.vo.flow;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Fluid consolidation of a workflow.
 *
 * @author jasonbruwer
 * @see FlowStep
 * @see FlowStepRule
 * @see ABaseFluidJSONObject
 * @since v1.0
 */
@Getter
@Setter
public class Flow extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Date dateCreated;
    private Date dateLastUpdated;
    private List<FlowStep> flowSteps;

    /**
     * The JSON mapping for the {@code Flow} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
        public static final String FLOW_STEPS = "flowSteps";
    }

    /**
     * Default constructor.
     */
    public Flow() {
        super();
    }

    /**
     * Constructor with the unique Flow identifier.
     *
     * @param flowIdParam The Flow primary key.
     */
    public Flow(Long flowIdParam) {
        super();
        this.setId(flowIdParam);
    }

    /**
     * Constructor with the Flow name.
     *
     * @param flowNameParam The Flow name.
     */
    public Flow(String flowNameParam) {
        super();
        this.setName(flowNameParam);
    }

    /**
     * Constructor with the Flow name.
     *
     * @param flowName        The Flow name.
     * @param flowDescription The Flow description.
     */
    public Flow(String flowName, String flowDescription) {
        this(flowName);
        this.setDescription(flowDescription);
    }

    /**
     * Constructor with the Flow id and name.
     *
     * @param flowId   The Flow id.
     * @param flowName The Flow name.
     */
    public Flow(Long flowId, String flowName) {
        this(flowId);
        this.setName(flowName);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Flow(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
        this.setFlowSteps(this.extractObjects(JSONMapping.FLOW_STEPS, FlowStep::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code Flow}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        returnVal.addProperty(JSONMapping.NAME, this.getName());
        returnVal.addProperty(JSONMapping.DESCRIPTION, this.getDescription());
        returnVal.add(JSONMapping.FLOW_STEPS, this.toJsonObjArray(this.getFlowSteps()));
        returnVal.addProperty(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(this.getDateCreated()));
        returnVal.addProperty(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(this.getDateLastUpdated()));
        return returnVal;
    }
}
