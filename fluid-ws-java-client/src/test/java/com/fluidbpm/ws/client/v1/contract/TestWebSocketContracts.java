package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.*;
import com.google.gson.JsonObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.lang.reflect.*;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestWebSocketContracts {
    private static final String[] CLIENTS = {
        "com.fluidbpm.ws.client.v1.flowitem.WebSocketSendToFlowClient",
        "com.fluidbpm.ws.client.v1.form.WebSocketFormContainerCreateClient",
        "com.fluidbpm.ws.client.v1.form.WebSocketGetFormHistoryByFormClient",
        "com.fluidbpm.ws.client.v1.form.WebSocketTableRecordCreateClient",
        "com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketExecuteSQLClient",
        "com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetAncestorClient",
        "com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetDescendantsClient",
        "com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetFormFieldsClient",
        "com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetTableFormsClient",
        "com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient"
    };
    @Parameterized.Parameters(name = "{0}") public static Collection<Object[]> methods() throws Exception {
        List<Object[]> result = new ArrayList<>();
        for (String name : CLIENTS) for (Method method : Class.forName(name).getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getName().endsWith("Synchronized"))
                result.add(new Object[]{method.toGenericString(), method});
        }
        return result;
    }
    private final Method method;
    public TestWebSocketContracts(String name, Method method) { this.method = method; }
    private LocalWebSocketFixture server;
    private ABaseClientWebSocket<?, ?> client;
    @Before public void connect() throws Exception {
        server = new LocalWebSocketFixture();
        Constructor<?> ctor = Arrays.stream(method.getDeclaringClass().getConstructors())
                .min(Comparator.comparingInt(Constructor::getParameterCount)).get();
        Object[] args = new Object[ctor.getParameterCount()];
        Class<?>[] types = ctor.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            Class<?> t = types[i];
            if (t == String.class) args[i] = i == 0 ? server.endpoint() : "contract-ticket";
            else if (t == long.class) args[i] = 3000L;
            else if (t == boolean.class) args[i] = false;
            else if (t == WebSocketClient.Mode.class) args[i] = WebSocketClient.Mode.Text;
            else if (t == Optional.class) args[i] = Optional.empty();
            else if (t == int.class) args[i] = 7;
        }
        client = (ABaseClientWebSocket<?, ?>)ctor.newInstance(args);
    }
    @After public void disconnect() { if (client != null) client.close(); if (server != null) server.close(); }
    private Object invoke() throws Throwable {
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < args.length; i++) args[i] = TestRestClientContracts.fixture(method.getGenericParameterTypes()[i]);
        try { return method.invoke(client, args); }
        catch (InvocationTargetException e) { throw e.getCause(); }
    }
    @Test(timeout = 30000) public void sendsCorrelatedRequestAndDecodesResponse() throws Throwable {
        Object response = invoke();
        assertNotNull(response);
        assertEquals(1, server.requests.size());
        JsonObject request = server.requests.get(0);
        assertEquals("contract-ticket", request.get("serviceTicket").getAsString());
        assertFalse(request.get("echo").getAsString().isEmpty());
        String path;
        switch (method.getDeclaringClass().getSimpleName()) {
            case "WebSocketSendToFlowClient": path = "flow_item/send_to_flow"; break;
            case "WebSocketFormContainerCreateClient": path = "form_container"; break;
            case "WebSocketTableRecordCreateClient": path = "form_container/table_record"; break;
            case "WebSocketGetFormHistoryByFormClient": path = "form_history/get_by_form_container"; break;
            case "SQLUtilWebSocketExecuteSQLClient": path = "sql_util/form/execute_sql"; break;
            case "SQLUtilWebSocketGetAncestorClient": path = "sql_util/form/get_ancestor_by_electronic_form_id"; break;
            case "SQLUtilWebSocketGetDescendantsClient": path = "sql_util/form/get_descendants_by_electronic_form_id"; break;
            case "SQLUtilWebSocketGetTableFormsClient": path = "sql_util/form/get_table_forms_by_electronic_form_id"; break;
            case "SQLUtilWebSocketGetFormFieldsClient": path = "sql_util/form_field/get_fields_by_electronic_form_id"; break;
            case "SQLUtilWebSocketExecuteNativeSQLClient": path = "sql_util/native/execute_query"; break;
            default: throw new AssertionError("Missing endpoint contract for " + method);
        }
        assertEquals("/web_socket/v1/" + path + "/contract-ticket", java.net.URI.create(server.uri).getPath());
        if (response instanceof List) {
            assertEquals(1, ((List<?>)response).size()); response = ((List<?>)response).get(0);
        }
        JsonObject decoded = ((ABaseFluidGSONObject)response).toJsonObject();
        assertEquals(request.get("echo"), decoded.get("echo"));
        if (decoded.has("listing")) assertEquals(918L, decoded.getAsJsonArray("listing").get(0).getAsJsonObject().get("id").getAsLong());
        else assertEquals(917L, decoded.get("id").getAsLong());
        assertNotNull(client.getNewHandlerInstance());
    }
    @Test(timeout = 30000) public void propagatesServerError() throws Throwable {
        server.error = true;
        try { invoke(); fail("Expected server error"); }
        catch (FluidClientException e) { assertEquals(1001, e.getErrorCode()); assertTrue(e.getMessage().contains("fixture failure")); }
        server.error = false;
        assertNotNull("An application error must not close a reusable socket", invoke());
    }
}
