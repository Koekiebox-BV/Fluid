package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.*;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.Assert.*;

/** Adding a REST method or overload requires adding an executable contract case. */
public class TestRestContractInventory {
    @Test(timeout = 30000) public void everyRestOperationHasAContract() throws Exception {
        Set<String> expected = new TreeSet<>(), actual = new TreeSet<>();
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/contracts/rest-client-methods.json"), StandardCharsets.UTF_8)) {
            for (JsonElement e : JsonParser.parseReader(reader).getAsJsonArray()) {
                JsonObject c = e.getAsJsonObject();
                List<String> types = new ArrayList<>();
                for (JsonElement t : c.getAsJsonArray("types")) types.add(t.getAsString());
                assertTrue("Duplicate contract: " + c, expected.add(c.get("client").getAsString() + "." + c.get("method").getAsString() + types));
            }
        }
        Path root = Paths.get(ABaseClientWS.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (Stream<Path> files = Files.walk(root.resolve("com/fluidbpm/ws/client/v1"))) {
            for (Path file : (Iterable<Path>)files.filter(p -> p.toString().endsWith("Client.class"))::iterator) {
                String name = root.relativize(file).toString().replace(File.separatorChar, '.').replaceAll("\\.class$", "");
                // Authentication and WebSocket clients have protocol fixtures in their own test classes.
                // HSM/Pericard will be exercised in its dedicated environment.
                if (name.contains("WebSocket") || name.contains(".hsm.") || name.endsWith("LoginClient") || name.endsWith("Auth0Client") || name.contains("$")) continue;
                Class<?> c = Class.forName(name, false, getClass().getClassLoader());
                if (Modifier.isAbstract(c.getModifiers()) || !ABaseClientWS.class.isAssignableFrom(c)) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (!Modifier.isPublic(m.getModifiers()) || m.isSynthetic()) continue;
                    List<String> types = new ArrayList<>();
                    for (Class<?> t : m.getParameterTypes()) types.add(t.getSimpleName());
                    actual.add(name + "." + m.getName() + types);
                }
            }
        }
        assertEquals("REST contract inventory must match the public API", actual, expected);
    }
}
