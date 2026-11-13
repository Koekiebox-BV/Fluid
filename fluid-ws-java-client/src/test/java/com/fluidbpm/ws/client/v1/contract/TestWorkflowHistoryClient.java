package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestWorkflowHistoryClient {
    @Test public void utcIsOptInAndBothRequestsUseTheUsersTicket() {
        class Client extends FormContainerClient {
            String path;
            Client() { super("http://localhost/", "user-ticket"); }
            @Override protected JsonObject postJson(ABaseFluidGSONObject request, String path) {
                this.path = path;
                assertEquals("user-ticket", request.getServiceTicket());
                assertEquals(Long.valueOf(42), request.getId());
                return new JsonObject();
            }
        }
        Client client = new Client();
        client.getFormFlowHistoricData(new Form(42L));
        String original = client.path;
        assertFalse(original.contains("?"));
        client.getFormFlowTimelineData(new Form(42L));
        assertEquals(original + "?utc=true", client.path);
    }
}
