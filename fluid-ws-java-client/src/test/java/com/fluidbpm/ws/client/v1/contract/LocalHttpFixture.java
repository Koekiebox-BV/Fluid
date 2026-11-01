package com.fluidbpm.ws.client.v1.contract;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/** Loopback-only fixture; exceptions are retained and surfaced on the test thread. */
final class LocalHttpFixture implements AutoCloseable {
    static final class Request {
        final String method, path, query, authorization;
        final JsonObject body;
        Request(HttpExchange exchange) throws IOException {
            method = exchange.getRequestMethod(); path = exchange.getRequestURI().getPath();
            query = exchange.getRequestURI().getRawQuery(); authorization = exchange.getRequestHeaders().getFirst("Authorization");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096]; int count;
            while ((count = exchange.getRequestBody().read(buffer)) != -1) bytes.write(buffer, 0, count);
            String text = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            body = text.isEmpty() ? new JsonObject() : JsonParser.parseString(text).getAsJsonObject();
        }
    }
    final List<Request> requests = Collections.synchronizedList(new ArrayList<>());
    private final HttpServer server;
    private volatile Throwable failure;
    LocalHttpFixture(Function<Request, JsonObject> response) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            try {
                Request req = new Request(exchange); requests.add(req);
                byte[] bytes = response.apply(req).toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes);
            } catch (Throwable e) { failure = e; exchange.sendResponseHeaders(500, -1); }
            finally { exchange.close(); }
        });
        server.start();
    }
    String endpoint() { return "http://127.0.0.1:" + server.getAddress().getPort() + "/"; }
    @Override public void close() {
        server.stop(0);
        if (failure != null) throw new AssertionError("HTTP fixture failed", failure);
    }
}
