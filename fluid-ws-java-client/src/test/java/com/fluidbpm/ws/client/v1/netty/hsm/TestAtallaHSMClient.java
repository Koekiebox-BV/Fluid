/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaHSMClient;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaHSMClientConfig;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaResponse;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaSslContexts;
import io.netty.handler.ssl.SslContext;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Test cases for the Atalla AT1000 HSM client.
 *
 * NOTE: The connectivity tests require an AT1000 (or hosted test service) to be reachable.
 * Set the following system properties to run them:
 * - atalla.hsm.test.enabled (default: false)
 * - atalla.hsm.host (default: localhost)
 * - atalla.hsm.port (default: 7000)
 * - atalla.hsm.ssl (default: false) - insecure TLS when no client certificate is given
 * - atalla.hsm.tls.cert / atalla.hsm.tls.key / atalla.hsm.tls.ca - PEM files for mutual TLS
 *   (e.g. docs/samples/client_MYHSM.crt, client_MYHSM.key, CA_MYHSM_chain.crt)
 * - atalla.hsm.contextTag (default: true) - set false if the HSM does not echo the ^tag field
 */
@Log
public class TestAtallaHSMClient extends TestCase {

    private static final String HSM_HOST = System.getProperty("atalla.hsm.host", "localhost");
    private static final int HSM_PORT = Integer.parseInt(System.getProperty("atalla.hsm.port", "7000"));
    private static final boolean HSM_SSL = Boolean.parseBoolean(System.getProperty("atalla.hsm.ssl", "false"));
    private static final String TLS_CERT = System.getProperty("atalla.hsm.tls.cert");
    private static final String TLS_KEY = System.getProperty("atalla.hsm.tls.key");
    private static final String TLS_CA = System.getProperty("atalla.hsm.tls.ca");
    private static final boolean CONTEXT_TAG = Boolean.parseBoolean(System.getProperty("atalla.hsm.contextTag", "true"));
    private static final boolean TEST_ENABLED = Boolean.parseBoolean(System.getProperty("atalla.hsm.test.enabled", "false"));

    private static AtallaHSMClient connect() throws Exception {
        SslContext ssl = null;
        if (TLS_CERT != null && TLS_KEY != null && TLS_CA != null) {
            ssl = AtallaSslContexts.mutualTls(new File(TLS_CERT), new File(TLS_KEY), new File(TLS_CA));
        } else if (HSM_SSL) {
            ssl = AtallaSslContexts.insecure();
        }
        return new AtallaHSMClient(AtallaHSMClientConfig.builder()
                .host(HSM_HOST)
                .port(HSM_PORT)
                .sslContext(ssl)
                .contextTagEnabled(CONTEXT_TAG)
                .build());
    }

    /**
     * Test AtallaCommand creation and formatting.
     */
    @Test
    public void testAtallaCommandCreation() {
        AtallaCommand echoCmd = AtallaCommand.Commands.echo("TEST DATA");
        assertEquals("00", echoCmd.getCommandId());
        assertEquals("TEST DATA", echoCmd.getField(1));
        assertEquals("<00#TEST DATA#>", echoCmd.getFullCommand());

        AtallaCommand versionCmd = AtallaCommand.Commands.softwareVersion();
        assertEquals("1101", versionCmd.getCommandId());
        assertEquals(0, versionCmd.getFields().size());
        assertEquals("<1101#>", versionCmd.getFullCommand());

        AtallaCommand keyGenCmd = new AtallaCommand("10", "3PUNE000", "", "D");
        assertEquals("<10#3PUNE000##D#>", keyGenCmd.getFullCommand());
        assertEquals("<10#3PUNE000##D#^R1#>", keyGenCmd.toWireString("R1"));
    }

    /**
     * Test AtallaResponse parsing.
     */
    @Test
    public void testAtallaResponseParsing() {
        AtallaResponse echo = new AtallaResponse("<00#000081#This is a test.#>".getBytes());
        assertEquals("00", echo.getResponseId());
        assertTrue(echo.isSuccess());
        assertEquals("This is a test.", echo.getEchoMessage());
        assertEquals("Response to test message", echo.getErrorMessage());

        AtallaResponse error = new AtallaResponse("<00#010280#>".getBytes());
        assertFalse(error.isSuccess());
        assertEquals("01", error.getErrorCode());
        assertEquals("02", error.getErrorFieldNumber());
        assertEquals("Length out of range", error.getErrorMessage());

        assertEquals("Invalid character", new AtallaResponse("<00#020180#>".getBytes()).getErrorMessage());
        assertEquals("Non-existent command or option", new AtallaResponse("<00#220080#>".getBytes()).getErrorMessage());
        assertEquals("Header mismatch", new AtallaResponse("<00#730180#>".getBytes()).getErrorMessage());
    }

    /**
     * Test HSM connection and echo command (requires HSM).
     */
    @Test
    public void testHSMEchoCommand() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set atalla.hsm.test.enabled=true to run");
            return;
        }

        try (AtallaHSMClient client = connect()) {
            assertTrue("Should be connected to HSM", client.isConnected());
            assertNotNull("Channel ID should not be null", client.getChannelId());

            String testData = "HELLO HSM";
            AtallaResponse response = client.echo(testData);

            assertNotNull("Response should not be null", response);
            assertTrue("Echo command should succeed: " + response, response.isSuccess());
            assertEquals("00", response.getResponseId());
            assertEquals(testData, response.getEchoMessage());
            assertNotNull("Echo reply carries the software version", response.getSoftwareVersion());

            assertTrue("Should have sent at least 1 command", client.getSentCommands() >= 1);
            assertTrue("Should have received at least 1 response", client.getReceivedResponses() >= 1);

            log.info("Echo test successful: " + response.getFullResponse());

        } catch (Exception e) {
            fail("Echo command failed: " + e.getMessage());
        }
    }

    /**
     * Test HSM software version command (requires HSM).
     */
    @Test
    public void testHSMSoftwareVersion() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set atalla.hsm.test.enabled=true to run");
            return;
        }

        try (AtallaHSMClient client = connect()) {
            AtallaResponse response = client.softwareVersion();

            assertNotNull("Response should not be null", response);
            assertTrue("1101 should succeed: " + response, response.isSuccess());
            assertEquals("2101", response.getResponseId());
            assertNotNull("Response should contain the version string", response.getField(1));

            log.info("Software version: " + response.getFullResponse());

        } catch (Exception e) {
            fail("Software version command failed: " + e.getMessage());
        }
    }

    /**
     * Test multiple pipelined commands (requires HSM).
     */
    @Test(timeout = 30000)
    public void testConcurrentCommands() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set atalla.hsm.test.enabled=true to run");
            return;
        }

        try (AtallaHSMClient client = connect()) {
            int commandCount = 10;
            CompletableFuture<?>[] futures = new CompletableFuture<?>[commandCount];
            for (int i = 0; i < commandCount; i++) {
                final String testData = "TEST " + i;
                futures[i] = client.sendCommandAsync(AtallaCommand.Commands.echo(testData))
                        .thenAccept(r -> assertEquals(testData, r.getEchoMessage()));
            }
            CompletableFuture.allOf(futures).join();

            assertEquals(commandCount, client.getSentCommands());
            assertEquals(commandCount, client.getReceivedResponses());
            assertEquals(0, client.getPendingCount());

            log.info("Concurrent commands test successful");

        } catch (Exception e) {
            fail("Concurrent commands test failed: " + e.getMessage());
        }
    }

    /**
     * Test connection to invalid HSM address.
     */
    @Test(timeout = 10000)
    public void testInvalidConnection() {
        try {
            AtallaHSMClient client = new AtallaHSMClient("invalid-host-does-not-exist.local", 9999, false);
            client.close();
            fail("Should throw exception for invalid connection");
        } catch (Exception e) {
            assertTrue("Exception message should mention connection failure",
                    e.getMessage().contains("Failed to"));
        }
    }
}
