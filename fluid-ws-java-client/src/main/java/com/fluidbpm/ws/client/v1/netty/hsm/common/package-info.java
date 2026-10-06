/**
 * Vendor-neutral building blocks shared by the Netty HSM clients.
 *
 * <h2>Overview</h2>
 * Payment HSMs differ in wire format (payShield: binary length prefix + fixed-position codes;
 * AT1000: {@code <id#field#...#>} text) but every client needs the same machinery: a Netty
 * bootstrap with optional (mutual) TLS, an idle-state handler, a registry of in-flight commands
 * matched to responses by an echoed correlation id, synchronous/asynchronous send with
 * timeouts, counters and clean shutdown. That machinery lives here; the vendor packages
 * ({@code ..hsm.thales}, {@code ..hsm.atalla}) contribute only their message model, codecs and
 * an {@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmProtocol} strategy.
 *
 * <h2>Pieces</h2>
 * <ul>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmCommand} /
 *       {@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmResponse} - minimal contracts a
 *       vendor message must satisfy (request id, wire bytes, success/error view).</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmProtocol} - codec pipeline plus
 *       request-id allocation/validation rules.</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.PendingRequests} - thread-safe
 *       in-flight registry (by id, with optional FIFO for untagged commands).</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.AbstractHsmClient} - the client.</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientConfig} - connection
 *       settings base ({@code @SuperBuilder}); vendors add their own fields.</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientInitializer},
 *       {@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmClientHandler},
 *       {@link com.fluidbpm.ws.client.v1.netty.hsm.common.IHsmResponseHandler} - Netty pipeline.</li>
 *   <li>{@link com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts} - TLS context factories.</li>
 * </ul>
 *
 * <h2>Adding a vendor</h2>
 * <ol>
 *   <li>Implement {@code XCommand implements HsmCommand} and {@code XResponse implements HsmResponse}.</li>
 *   <li>Write the framing/codec handlers.</li>
 *   <li>Implement {@code HsmProtocol<XCommand, XResponse>} returning those codecs and the id rules.</li>
 *   <li>Extend {@code HsmClientConfig} (add {@code defaultPort()} and vendor fields) and
 *       {@code AbstractHsmClient<XCommand, XResponse>} (add convenience methods such as {@code echo}).</li>
 * </ol>
 *
 * @author jasonbruwer
 * @since 1.15
 */
package com.fluidbpm.ws.client.v1.netty.hsm.common;
