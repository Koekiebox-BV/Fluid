package com.fluidbpm.ws.client.v1.netty.hsm;

import javax.net.ServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Minimal in-JVM payShield host-port simulator for unit tests.
 *
 * Speaks the TCP framing from the Host Programmers Manual (2-byte binary length,
 * then message header + command code + data) and echoes the header back.
 *
 * Behaviour is driven by the command data of an {@code NO} command:
 * <ul>
 *   <li>{@code DELAY:<ms>:<text>} - wait before responding with {@code NP00<text>}</li>
 *   <li>{@code DROP} - never respond</li>
 *   <li>{@code CLOSE} - close the connection without responding</li>
 *   <li>{@code BIN} - respond with {@code NP00} followed by bytes 0x00..0xFF</li>
 *   <li>anything else - respond {@code NP00<data>}</li>
 * </ul>
 * Any other command code is answered with the response code (second char + 1) and error {@code 00}.
 */
public class FakeThalesHsmServer implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final int headerLength;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final List<Socket> clients = new ArrayList<>();
    private volatile boolean running = true;

    public FakeThalesHsmServer(int headerLength) throws IOException {
        this(headerLength, ServerSocketFactory.getDefault());
    }

    public FakeThalesHsmServer(int headerLength, ServerSocketFactory factory) throws IOException {
        this.headerLength = headerLength;
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
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            while (running) {
                int length = in.readUnsignedShort();
                byte[] msg = new byte[length];
                in.readFully(msg);

                final byte[] request = msg;
                // Handle each request on its own thread so responses can overtake one another,
                // as a real HSM with several worker threads would.
                pool.submit(() -> respond(request, out));
            }
        } catch (EOFException ignored) {
            // client went away
        } catch (Exception e) {
            if (running) e.printStackTrace();
        }
    }


    private void respond(byte[] msg, DataOutputStream out) {
        try {
            byte[] header = new byte[Math.min(headerLength, msg.length)];
            System.arraycopy(msg, 0, header, 0, header.length);
            String code = new String(msg, headerLength, 2, StandardCharsets.ISO_8859_1);
            byte[] data = new byte[msg.length - headerLength - 2];
            System.arraycopy(msg, headerLength + 2, data, 0, data.length);
            String text = new String(data, StandardCharsets.ISO_8859_1);

            byte[] responseBody;
            if ("NO".equals(code)) {
                if (text.startsWith("DELAY:")) {
                    String[] parts = text.split(":", 3);
                    Thread.sleep(Long.parseLong(parts[1]));
                    responseBody = ("NP00" + parts[2]).getBytes(StandardCharsets.ISO_8859_1);
                } else if ("DROP".equals(text)) {
                    return;
                } else if ("CLOSE".equals(text)) {
                    out.close();
                    return;
                } else if ("BIN".equals(text)) {
                    responseBody = new byte[4 + 256];
                    System.arraycopy("NP00".getBytes(StandardCharsets.ISO_8859_1), 0, responseBody, 0, 4);
                    for (int i = 0; i < 256; i++) responseBody[4 + i] = (byte) i;
                } else {
                    responseBody = ("NP00" + text).getBytes(StandardCharsets.ISO_8859_1);
                }
            } else {
                String rc = "" + code.charAt(0) + (char) (code.charAt(1) + 1);
                responseBody = (rc + "00").getBytes(StandardCharsets.ISO_8859_1);
            }

            byte[] frame = new byte[header.length + responseBody.length];
            System.arraycopy(header, 0, frame, 0, header.length);
            System.arraycopy(responseBody, 0, frame, header.length, responseBody.length);
            synchronized (out) {
                out.writeShort(frame.length);
                out.write(frame);
                out.flush();
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
