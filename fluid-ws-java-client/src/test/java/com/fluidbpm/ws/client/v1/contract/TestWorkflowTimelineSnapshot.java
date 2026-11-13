package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricDataListing;
import com.google.gson.JsonObject;
import org.junit.Test;
import java.util.Collections;
import java.util.Date;
import static org.junit.Assert.*;

public class TestWorkflowTimelineSnapshot {
    @Test public void snapshotAndStepTypeSurviveJsonRoundTripWithoutShiftingInstants() {
        FormFlowHistoricDataListing data = new FormFlowHistoricDataListing();
        data.setListing(Collections.emptyList());
        FlowStep step = new FlowStep(); step.setId(3L); step.setName("Wait"); step.setFlowStepType("Polling");
        data.setCurrentStep(step);
        data.setCurrentStepEntered(new Date(1794528000000L));
        data.setSnapshotTime(new Date(1794528060000L));
        FormFlowHistoricDataListing copy = new FormFlowHistoricDataListing(data.toJsonObject());
        assertEquals("Polling", copy.getCurrentStep().getFlowStepType());
        assertEquals(data.getCurrentStepEntered(), copy.getCurrentStepEntered());
        assertEquals(data.getSnapshotTime(), copy.getSnapshotTime());
        assertEquals(data.getCurrentStep().getId(), copy.getCurrentStep().getId());
    }

    @Test public void legacyHistoryWithoutSnapshotMetadataStillParses() {
        FormFlowHistoricDataListing history = new FormFlowHistoricDataListing(new JsonObject());
        assertNull(history.getCurrentStep());
        assertNull(history.getCurrentStepEntered());
        assertNull(history.getSnapshotTime());
    }
}
