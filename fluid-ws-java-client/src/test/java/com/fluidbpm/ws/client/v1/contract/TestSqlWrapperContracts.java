package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.sqlutil.wrapper.SQLUtilWebSocketRESTWrapper;
import com.google.gson.JsonParser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.lang.reflect.*;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestSqlWrapperContracts {
    @Parameterized.Parameters(name = "{0}") public static Collection<Object[]> methods() {
        List<Object[]> result = new ArrayList<>();
        for (Method method : SQLUtilWebSocketRESTWrapper.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && !method.getName().startsWith("close"))
                result.add(new Object[]{method.toGenericString(), method});
        }
        return result;
    }
    private final Method method;
    public TestSqlWrapperContracts(String name, Method method) { this.method = method; }
    @Test(timeout = 30000) public void restModeDelegatesAndDecodes() throws Throwable {
        boolean previous = SQLUtilWebSocketRESTWrapper.DISABLE_WS;
        SQLUtilWebSocketRESTWrapper.DISABLE_WS = true;
        try (LocalHttpFixture http = new LocalHttpFixture(req -> {
            assertEquals("POST", req.method);
            assertEquals("AQID", req.body.get("serviceTicket").getAsString());
            return JsonParser.parseString("{\"id\":917,\"listing\":[{\"id\":918,\"fieldName\":\"response\"}],\"listingCount\":1}").getAsJsonObject();
        }); SQLUtilWebSocketRESTWrapper client = new SQLUtilWebSocketRESTWrapper(http.endpoint(), "AQID", 3000)) {
            Object[] args = new Object[method.getParameterCount()];
            for (int i = 0; i < args.length; i++) args[i] = TestRestClientContracts.fixture(method.getGenericParameterTypes()[i]);
            Object response;
            try { response = method.invoke(client, args); }
            catch (InvocationTargetException e) { throw e.getCause(); }
            if (method.getName().equals("initGetNativeSQLClient")) {
                assertNull(response); assertTrue(http.requests.isEmpty()); return;
            }
            assertEquals(1, http.requests.size());
            String path;
            switch (method.getName()) {
                case "getAncestor": path = "sql_util/form/get_ancestor_by_electronic_form_id"; break;
                case "getDescendants": path = "sql_util/form/get_descendants_by_electronic_form_id"; break;
                case "getTableForms": path = "sql_util/form/get_table_forms_by_electronic_form_id"; break;
                case "getFormFields": case "massPopulateFormFields": path = "sql_util/form_field/get_fields_by_electronic_form_id"; break;
                case "getFormHistoryByForm": path = "form_history/get_by_form_container"; break;
                case "executeNativeSQL": path = "sql_util/native/execute_query"; break;
                case "executeNativeSQLUpdate": path = "sql_util/native/execute_update"; break;
                default: throw new AssertionError("Missing endpoint contract for " + method);
            }
            assertEquals("/v1/" + path, http.requests.get(0).path);
            if (method.getName().equals("massPopulateFormFields")) {
                Object forms = args[1];
                Form form = forms instanceof List ? (Form)((List<?>)forms).get(0) : ((Form[])forms)[0];
                assertEquals("response", form.getFormFields().get(0).getFieldName());
            } else if (response instanceof List) assertEquals(1, ((List<?>)response).size());
            else assertEquals(Long.valueOf(917), ((Form)response).getId());
        } finally { SQLUtilWebSocketRESTWrapper.DISABLE_WS = previous; }
    }
}
