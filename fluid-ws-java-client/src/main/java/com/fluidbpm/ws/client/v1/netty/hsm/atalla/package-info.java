/**
 * Netty-based Atalla AT1000 HSM client implementation.
 *
 * <h2>Overview</h2>
 * This package provides a client for communicating with Utimaco / Atalla AT1000 Hardware
 * Security Modules over the host command interface described in the
 * <i>AT1000 HSM Command Reference Manual</i>. It mirrors the structure of the Thales client
 * in {@code com.fluidbpm.ws.client.v1.netty.hsm.thales}.
 *
 * <h2>Protocol Details</h2>
 * <p>
 * The AT1000 host protocol is ASCII text with no length prefix:
 * <pre>
 * Request:  &lt;CMDID#FIELD 1#FIELD 2#...#FIELD N#[^Context Tag#]&gt;
 * Response: &lt;RESPID#FIELD 1#FIELD 2#...#FIELD N#[^Context Tag#]&gt;[CRLF]
 * </pre>
 * <ul>
 *   <li>{@code CMDID} is 2-4 characters; the response id is the command id with its first
 *       digit incremented ({@code 10} answers as {@code 20}).</li>
 *   <li>Every field, including the last, is terminated by {@code #}. The characters
 *       {@code < > # ^} and CR may not appear in field data.</li>
 *   <li>Errors are returned as {@code <00#XXYYZZ#>}: error number, field in error, software
 *       version (Table 11-1). Error number {@code 00} is the reply to the echo command.</li>
 *   <li>The optional context tag ({@code ^...}) is returned unmodified. The client assigns a
 *       unique tag to every in-flight command and matches each response by that tag, so
 *       commands can be pipelined on one connection. Set
 *       {@code AtallaHSMClientConfig.contextTagEnabled(false)} to send untagged commands,
 *       which are then matched in send order.</li>
 * </ul>
 * <p>
 * Example (tag {@code 0001}):
 * <pre>
 * Request:  &lt;00#This is a test.#^0001#&gt;
 * Response: &lt;00#000081#This is a test.#^0001#&gt;CRLF
 * </pre>
 *
 * <h2>Common Commands</h2>
 * <ul>
 *   <li><b>00</b> - Echo Test Message</li>
 *   <li><b>1101</b> - HSM Software Version</li>
 *   <li><b>10</b> - Generate 3DES Working Key</li>
 *   <li><b>93</b> - Generate Random Number</li>
 * </ul>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * try (AtallaHSMClient client = new AtallaHSMClient("localhost", 7000, false)) {
 *     AtallaResponse response = client.echo("This is a test.");
 *
 *     if (response.isSuccess()) {
 *         System.out.println("Echo successful: " + response.getEchoMessage());
 *     } else {
 *         System.err.println("Error: " + response.getErrorMessage());
 *     }
 * }
 * }</pre>
 *
 * <h2>Mutual TLS</h2>
 * <pre>{@code
 * AtallaHSMClientConfig config = AtallaHSMClientConfig.builder()
 *     .host("hsm.example.com").port(13502)
 *     .sslContext(AtallaSslContexts.mutualTls(certPem, keyPkcs8Pem, caChainPem))
 *     .build();
 * try (AtallaHSMClient client = new AtallaHSMClient(config)) {
 *     client.echo("HELLO");
 * }
 * }</pre>
 *
 * <h2>Architecture</h2>
 * <pre>
 * [SSL Handler]
 *      ↓
 * [Idle State Handler]
 *      ↓
 * [Frame Decoder] ← isolates one &lt;...&gt; message, drops the CRLF trailer
 *      ↓
 * [Response Decoder] ← bytes to AtallaResponse (fields and context tag split out)
 *      ↓
 * [Command Encoder] ← AtallaCommand to &lt;id#field#...#[^tag#]&gt; bytes
 *      ↓
 * [HSM Client Handler] ← business logic
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * The AtallaHSMClient is thread-safe and can be used from multiple threads.
 * A single client instance can handle multiple concurrent commands.
 *
 * @author jasonbruwer
 * @since 1.15
 * @see com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaHSMClient
 * @see com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaCommand
 * @see com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaResponse
 */
package com.fluidbpm.ws.client.v1.netty.hsm.atalla;
