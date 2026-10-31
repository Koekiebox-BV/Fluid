package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.*;
import java.lang.reflect.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

/** HTTP contract checks run without a Fluid server or database. */
@RunWith(Parameterized.class)
public class TestRestClientContracts {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> cases() throws Exception {
        try (Reader reader = new InputStreamReader(TestRestClientContracts.class.getResourceAsStream(
                "/contracts/rest-client-methods.json"), StandardCharsets.UTF_8)) {
            List<Object[]> result = new ArrayList<>();
            for (JsonElement entry : new JsonParser().parse(reader).getAsJsonArray()) {
                JsonObject contract = entry.getAsJsonObject();
                result.add(new Object[]{contract.get("client").getAsString() + "." + contract.get("method").getAsString()
                        + contract.get("types"), contract});
            }
            return result;
        }
    }
    private final JsonObject contract;
    private volatile int status = 200;
    public TestRestClientContracts(String name, JsonObject contract) { this.contract = contract; }
    private HttpServer server;
    private ABaseClientWS client;
    private final List<String> paths = new ArrayList<>();
    private final List<String> verbs = new ArrayList<>();
    private final List<JsonObject> bodies = new ArrayList<>();
    private static final String TICKET = "contract-ticket";
    private static final String RESPONSE = "{\"id\":917,\"name\":\"response\",\"fieldName\":\"response\",\"message\":\"response\",\"value\":\"response\",\"data\":\"AQID\",\"nextValidSyntaxWords\":[\"response\"],\"existingValues\":[\"response\"],\"listing\":[{\"id\":918,\"message\":\"response\",\"viewType\":\"Standard\",\"viewName\":\"sample\"}],\"listingCount\":1}";

    @Before public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096]; int count;
            while ((count = exchange.getRequestBody().read(buffer)) != -1) body.write(buffer, 0, count);
            paths.add(exchange.getRequestURI().getPath());
            verbs.add(exchange.getRequestMethod());
            String text = new String(body.toByteArray(), StandardCharsets.UTF_8);
            bodies.add(text.isEmpty() ? new JsonObject() : new JsonParser().parse(text).getAsJsonObject());
            byte[] response = RESPONSE.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.length);
            exchange.getResponseBody().write(response); exchange.close();
        });
        server.start();
        String endpoint = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
        Class<?> type = Class.forName(contract.get("client").getAsString());
        try { client = (ABaseClientWS)type.getConstructor(String.class, String.class).newInstance(endpoint, TICKET); }
        catch (NoSuchMethodException e) { client = (ABaseClientWS)type.getConstructor(String.class).newInstance(endpoint); }
    }
    @After public void stopServer() { if (client != null) client.close(); if (server != null) server.stop(0); }

    @Test(timeout = 30000) public void requestAndResponseContract() throws Throwable {
        Method method = Arrays.stream(client.getClass().getMethods()).filter(m -> {
            if (!m.getName().equals(contract.get("method").getAsString())) return false;
            JsonArray types = contract.getAsJsonArray("types");
            if (m.getParameterCount() != types.size()) return false;
            for (int i = 0; i < types.size(); i++) if (!m.getParameterTypes()[i].getSimpleName().equals(types.get(i).getAsString())) return false;
            return true;
        }).findFirst().orElseThrow(() -> new AssertionError("Contract method no longer exists: " + contract));
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < args.length; i++) args[i] = fixture(method.getGenericParameterTypes()[i]);
        Object result;
        try { result = method.invoke(client, args); }
        catch (InvocationTargetException e) { throw e.getCause(); }
        assertEquals("Expected one HTTP request", 1, paths.size());
        assertTrue("Unexpected path: " + paths, contains(contract.getAsJsonArray("paths"), paths.get(0)));
        assertTrue("Unexpected verb: " + verbs, contains(contract.getAsJsonArray("verbs"), verbs.get(0)));
        if (!verbs.get(0).equals("GET")) assertFalse("Request must contain a payload", bodies.get(0).entrySet().isEmpty());
        if (!verbs.get(0).equals("GET") && !method.getName().equals("sendPasswordResetRequest") && !method.getName().equals("getGravatarForUser") && !method.getName().equals("createAdminUser")) {
            assertTrue("Authenticated request needs service ticket", bodies.get(0).has("serviceTicket"));
            assertEquals(TICKET, bodies.get(0).get("serviceTicket").getAsString());
        }
        if (method.getName().startsWith("createField") || method.getName().startsWith("updateField")) {
            String expected = null, name = method.getName();
            if (name.contains("ParagraphText")) expected = "ParagraphText";
            else if (name.contains("TrueFalse")) expected = "TrueFalse";
            else if (name.contains("DateTime")) expected = "DateTime";
            else if (name.contains("Decimal")) expected = "Decimal";
            else if (name.contains("MultiChoice")) expected = "MultipleChoice";
            else if (name.contains("Label")) expected = "Label";
            else if (name.contains("TextPlain") || name.contains("TextMasked")) expected = "Text";
            if (expected != null) assertEquals("Field type", expected, bodies.get(0).get("fieldType").getAsString());
        }
        if (method.getReturnType() == void.class) return;
        assertNotNull("Response must be decoded", result);
        if (bodies.get(0).has("serviceTicket")) assertEquals(TICKET, bodies.get(0).get("serviceTicket").getAsString());
        if (result instanceof List) assertEquals("Decode response listing", 1, ((List<?>)result).size());
        else if (result instanceof String) assertEquals(method.getName().equals("getSystemMailTransfer") ? "response" : RESPONSE, result);
        else if (result instanceof byte[]) assertArrayEquals(new byte[]{1, 2, 3}, (byte[])result);
        else if (result instanceof ABaseFluidGSONObject) {
            JsonObject decoded = ((ABaseFluidGSONObject)result).toJsonObject();
            if (decoded.has("listing")) assertEquals(1, decoded.getAsJsonArray("listing").size());
            else if (decoded.has("id")) assertEquals(method.getName().equals("getStandardJobViewBy") ? 918L : 917L, decoded.get("id").getAsLong());
        }
    }
    @Test(timeout = 30000) public void endpointNotFoundIsReported() throws Throwable {
        status = 404;
        try {
            requestAndResponseContract();
            fail("Expected missing endpoint to be reported");
        } catch (com.fluidbpm.ws.client.FluidClientException e) {
            assertEquals(com.fluidbpm.ws.client.FluidClientException.ErrorCode.CONNECT_ERROR, e.getErrorCode());
        }
    }
    private static boolean contains(JsonArray array, String value) {
        for (JsonElement e : array) if (value.equals(e.getAsString())) return true;
        return false;
    }
    static Object fixture(Type type) throws Exception {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            if (pt.getRawType() == List.class) return new ArrayList<>(Collections.singletonList(fixture(pt.getActualTypeArguments()[0])));
            if (pt.getRawType() == Optional.class) return Optional.of(fixture(pt.getActualTypeArguments()[0]));
        }
        Class<?> c = (Class<?>)type;
        if (c == String.class) return "sample";
        if (c == int.class || c == Integer.class) return 7;
        if (c == long.class || c == Long.class) return 7L;
        if (c == double.class || c == Double.class) return 7.0;
        if (c == boolean.class || c == Boolean.class) return false;
        if (c == Date.class) return new Date(1234567890000L);
        if (c == byte[].class) return new byte[]{1, 2, 3};
        if (c == Object[].class) return new Object[0];
        if (c.isArray()) { Object a = Array.newInstance(c.getComponentType(), 1); Array.set(a, 0, fixture(c.getComponentType())); return a; }
        Object value = c.getConstructor().newInstance();
        for (String name : Arrays.asList("setId", "setFieldName", "setName", "setFormType", "setUsername", "setEmail", "setQuery", "setTitle", "setDatasourceName")) {
            try { c.getMethod(name, name.equals("setId") ? Long.class : String.class).invoke(value, name.equals("setId") ? 7L : "sample"); }
            catch (NoSuchMethodException ignored) { /* Different VO types expose different identity properties. */ }
        }
        if (value instanceof Field) { ((Field)value).setTypeAsEnum(Field.Type.Text); ((Field)value).setTypeMetaData("Plain"); }
        if (value instanceof com.fluidbpm.program.api.vo.form.Form) {
            Field sql = new Field("SQL", "SELECT * FROM sample", Field.Type.Text);
            ((com.fluidbpm.program.api.vo.form.Form)value).setFormFields(Collections.singletonList(sql));
        }
        return value;
    }
}
