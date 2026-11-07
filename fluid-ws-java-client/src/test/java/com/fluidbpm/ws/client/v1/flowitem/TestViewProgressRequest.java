package com.fluidbpm.ws.client.v1.flowitem;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestViewProgressRequest {
    @Test public void progressIsOptInAndQuerySeparatorsAreValid() {
        RecordingClient client = new RecordingClient();
        client.getFluidItemsForView(new JobView(), 25, 0);
        assertFalse(client.path.contains("populate_flow_progress"));
        client.getFluidItemsForView(new JobView(), 25, 0, "", "", true);
        assertTrue(client.path.contains("&populate_flow_progress=true"));
        client.getFluidItemsForView(new JobView(), -1, -1, "", "", true);
        assertTrue(client.path.endsWith("?populate_flow_progress=true"));
    }
    private static class RecordingClient extends FlowItemClient {
        String path;
        RecordingClient() { super("http://localhost", "test"); }
        @Override protected JsonObject postJson(ABaseFluidGSONObject item, String path) {
            this.path = path;
            return new JsonObject();
        }
    }
}
