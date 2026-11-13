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

package com.fluidbpm.program.api.vo.historic;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * <p>
 * Represents a {@code List} of {@code FormFlowHistoricData}s.
 * </p>
 *
 * @author jasonbruwer
 * @see FormFlowHistoricData
 * @since v1.8
 */
public class FormFlowHistoricDataListing extends ABaseGSONListing<FormFlowHistoricData> {
    private static final long serialVersionUID = 1L;

    // Optional UTC timeline snapshot. Null currentStep means the item has left workflow.
    @Getter @Setter private FlowStep currentStep;
    @Getter @Setter private Date currentStepEntered;
    @Getter @Setter private Date snapshotTime;

    /**
     * Default constructor.
     */
    public FormFlowHistoricDataListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormFlowHistoricDataListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;
        this.currentStep = this.extractObject("currentStep", FlowStep::new);
        this.currentStepEntered = this.getDateFieldValueFromFieldWithName("currentStepEntered");
        this.snapshotTime = this.getDateFieldValueFromFieldWithName("snapshotTime");
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject json = super.toJsonObject();
        this.setAsObj("currentStep", json, this::getCurrentStep);
        this.setAsProperty("currentStepEntered", json, this.currentStepEntered);
        this.setAsProperty("snapshotTime", json, this.snapshotTime);
        return json;
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code FormFlowHistoricData} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code FormFlowHistoricData}.
     * @return New {@code FormFlowHistoricData} instance.
     */
    @Override
    @XmlTransient
    public FormFlowHistoricData getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new FormFlowHistoricData(jsonObjectParam);
    }
}
