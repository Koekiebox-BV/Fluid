package com.fluidbpm.ws.client.v1.netty.hsm;

import javax.net.ServerSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Minimal in-JVM Atalla AT1000 host-port simulator for unit tests.
 *
 * Speaks the text framing from the Command Reference Manual: {@code <ID#field#...#[^tag#]>}
 * with no length prefix, a CRLF trailer on responses (option 23, configurable here) and the
 * context tag echoed back.
 *
 * Behaviour is driven by field 1 of an echo ({@code 00}) command:
 * <ul>
 *   <li>{@code DELAY:<ms>:<text>} - wait before echoing {@code text}</li>
 *   <li>{@code DROP} - never respond</li>
 *   <li>{@code CLOSE} - close the connection without responding</li>
 *   <li>{@code CHUNK:<text>} - echo {@code text} but write the response 3 bytes at a time</li>
 *   <li>{@code NOTAG:<text>} - echo {@code text} without the context tag</li>
 *   <li>anything else - respond {@code <00#000085#text#>}</li>
 * </ul>
 * {@code 1101} answers with a version string; {@code 99} answers with an error
 * {@code <00#230085#201#>}; any other id is answered with the response id (first digit + 1)
 * and the request fields echoed.
 */
public class FakeAtallaHsmServer implements AutoCloseable {

    public static final String VERSION = "85";

    private final ServerSocket serverSocket;
    private final boolean appendCrlf;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final List<Socket> clients = new ArrayList<>();
    private volatile boolean running = true;

    public FakeAtallaHsmServer() throws IOException {
        this(true);
    }

    public FakeAtallaHsmServer(boolean appendCrlf) throws IOException {
        this(appendCrlf, ServerSocketFactory.getDefault());
    }

    public FakeAtallaHsmServer(boolean appendCrlf, ServerSocketFactory factory) throws IOException {
        this.appendCrlf = appendCrlf;
        this.serverSocket = factory.createServerSocket(0);
        pool.submit(this::acceptLoop);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket s = serverSocket.accept();
                synchronized (clients) { clients.add(s); }
                pool.submit(() -> serve(s));
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }
    }

    private void serve(Socket socket) {
        try (Socket s = socket;
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream()) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            while (running) {
                int b = in.read();
                if (b < 0) return;
                buf.write(b);
                if (b == '>') {
                    final String request = new String(buf.toByteArray(), StandardCharsets.ISO_8859_1);
                    buf.reset();
                    // Handle each request on its own thread so responses can overtake one another.
                    pool.submit(() -> respond(request, out));
                }
            }
        } catch (java.net.SocketException ignored) {
            // client went away, or CLOSE closed the socket under us
        } catch (Exception e) {
            if (running) e.printStackTrace();
        }
    }

    private void respond(String request, OutputStream out) {
        try {
            String body = request.substring(request.indexOf('<') + 1, request.lastIndexOf('>'));
            List<String> parts = new ArrayList<>(Arrays.asList(body.split("#", -1)));
            if (!parts.isEmpty() && parts.get(parts.size() - 1).isEmpty()) parts.remove(parts.size() - 1);
            String tag = null;
            if (parts.size() > 1 && parts.get(parts.size() - 1).startsWith("^")) {
                tag = parts.remove(parts.size() - 1).substring(1);
            }
            String id = parts.remove(0);

            boolean chunked = false;
            String response;
            if ("00".equals(id)) {
                String text = parts.isEmpty() ? "" : parts.get(0);
                if (text.startsWith("DELAY:")) {
                    String[] p = text.split(":", 3);
                    Thread.sleep(Long.parseLong(p[1]));
                    text = p[2];
                } else if ("DROP".equals(text)) {
                    return;
                } else if ("CLOSE".equals(text)) {
                    out.close();
                    return;
                } else if (text.startsWith("CHUNK:")) {
                    chunked = true;
                    text = text.substring("CHUNK:".length());
                } else if (text.startsWith("NOTAG:")) {
                    tag = null;
                    text = text.substring("NOTAG:".length());
                }
                response = "<00#0000" + VERSION + "#" + text + "#";
            } else if ("1101".equals(id)) {
                response = "<2101#Atalla HSM AT1000-AKB Version: 8.53, Date: May 26 2023, Time: 13:50:01#B19C#3#";
            } else if ("99".equals(id)) {
                response = "<00#2300" + VERSION + "#201#";
            } else {
                String rid = (char) (id.charAt(0) + 1) + id.substring(1);
                StringBuilder sb = new StringBuilder("<").append(rid).append('#');
                for (String f : parts) sb.append(f).append('#');
                response = sb.toString();
            }
            if (tag != null) response += "^" + tag + "#";
            response += ">";
            if (appendCrlf) response += "\r\n";

            byte[] bytes = response.getBytes(StandardCharsets.ISO_8859_1);
            synchronized (out) {
                if (chunked) {
                    for (int i = 0; i < bytes.length; i += 3) {
                        out.write(bytes, i, Math.min(3, bytes.length - i));
                        out.flush();
                        Thread.sleep(5);
                    }
                } else {
                    out.write(bytes);
                    out.flush();
                }
            }
        } catch (Exception e) {
            if (running) e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
        synchronized (clients) {
            for (Socket c : clients) {
                try { c.close(); } catch (IOException ignored) { }
            }
        }
        serverSocket.close();
        pool.shutdownNow();
        try { pool.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException ignored) { }
    }
}
