/**
 * Netty-based Thales HSM client implementation using the international command set.
 *
 * <h2>Overview</h2>
 * This package provides a production-ready, high-performance client for communicating
 * with Thales Hardware Security Modules (HSMs) using the Thales international command set.
 * The implementation leverages Netty's asynchronous I/O for optimal performance.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Full support for Thales international command set</li>
 *   <li>SSL/TLS encryption for secure communication</li>
 *   <li>Asynchronous command execution with CompletableFuture</li>
 *   <li>Automatic message framing and parsing</li>
 *   <li>Request/response correlation</li>
 *   <li>Connection pooling support</li>
 *   <li>Comprehensive error handling</li>
 *   <li>Production-ready with timeouts and reconnection</li>
 * </ul>
 *
 * <h2>Protocol Details</h2>
 * <p>
 * Thales HSM protocol uses a simple frame format:
 * <pre>
 * Request:  [4-byte length][2-char command code][command data]
 * Response: [4-byte length][2-char response code][2-char error code][response data]
 * </pre>
 * <p>
 * Example:
 * <pre>
 * Request:  "0010NOTEST DATA"  (Echo "TEST DATA")
 * Response: "0010NP00TEST DATA" (Success with echoed data)
 * </pre>
 *
 * <h2>Common Commands</h2>
 * <ul>
 *   <li><b>NO</b> - Echo test (diagnostic command)</li>
 *   <li><b>NC</b> - Perform diagnostics, returns HSM status and LMK check value</li>
 *   <li><b>A0</b> - Generate a ZMK, TMK, TPK or PVK</li>
 *   <li><b>EC</b> - Verify a terminal PIN</li>
 *   <li><b>BA</b> - Translate a PIN from TPK to ZPK</li>
 *   <li><b>M0</b> - Generate a MAC</li>
 * </ul>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * // Connect to HSM
 * try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
 *     // Send echo command
 *     ThalesResponse response = client.echo("TEST DATA");
 *
 *     if (response.isSuccess()) {
 *         System.out.println("Echo successful: " + response.getResponseData());
 *     } else {
 *         System.err.println("Error: " + response.getErrorMessage());
 *     }
 * }
 * }</pre>
 *
 * <h2>Advanced Usage</h2>
 * <pre>{@code
 * // Asynchronous command execution
 * ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, true);
 *
 * CompletableFuture<ThalesResponse> future = client.sendCommandAsync(
 *     ThalesCommand.Commands.diagnostics()
 * );
 *
 * future.thenAccept(response -> {
 *     System.out.println("HSM Status: " + response);
 * });
 * }</pre>
 *
 * <h2>Error Codes</h2>
 * <ul>
 *   <li><b>00</b> - No error (success)</li>
 *   <li><b>01</b> - Verification failure</li>
 *   <li><b>02</b> - Key parity error</li>
 *   <li><b>10</b> - Invalid input data</li>
 *   <li><b>15</b> - Invalid command code</li>
 *   <li><b>20</b> - Syntax error</li>
 *   <li><b>27</b> - LMK error</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 * <p>
 * The Netty pipeline is configured as follows:
 * <pre>
 * [SSL Handler]
 *      ↓
 * [Idle State Handler]
 *      ↓
 * [Frame Decoder] ← strips 4-byte length header
 *      ↓
 * [Frame Encoder] ← adds 4-byte length header
 *      ↓
 * [Response Decoder] ← bytes to ThalesResponse
 *      ↓
 * [Command Encoder] ← ThalesCommand to bytes
 *      ↓
 * [HSM Client Handler] ← business logic
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * The ThalesHSMClient is thread-safe and can be used from multiple threads.
 * A single client instance can handle multiple concurrent commands.
 *
 * <h2>Performance</h2>
 * <p>
 * The Netty-based implementation provides:
 * <ul>
 *   <li>Non-blocking I/O for high throughput</li>
 *   <li>Low latency command execution</li>
 *   <li>Efficient connection management</li>
 *   <li>Minimal memory footprint</li>
 * </ul>
 *
 * @author jasonbruwer
 * @since 1.14
 * @see com.fluidbpm.ws.client.v1.netty.hsm.ThalesHSMClient
 * @see com.fluidbpm.ws.client.v1.netty.hsm.ThalesCommand
 * @see com.fluidbpm.ws.client.v1.netty.hsm.ThalesResponse
 */
package com.fluidbpm.ws.client.v1.netty.hsm;
