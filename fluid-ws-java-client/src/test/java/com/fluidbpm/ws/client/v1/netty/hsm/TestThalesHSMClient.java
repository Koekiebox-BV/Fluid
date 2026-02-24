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

import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesHSMClient;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesResponse;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * Test cases for Thales HSM client implementation.
 *
 * NOTE: These tests require a Thales HSM simulator or actual HSM to be running.
 * Set the following system properties to run tests:
 * - thales.hsm.host (default: localhost)
 * - thales.hsm.port (default: 1500)
 * - thales.hsm.ssl (default: false)
 * - thales.hsm.test.enabled (default: false)
 */
@Log
public class TestThalesHSMClient extends TestCase {

    private static final String HSM_HOST = System.getProperty("thales.hsm.host", "localhost");
    private static final int HSM_PORT = Integer.parseInt(System.getProperty("thales.hsm.port", "1500"));
    private static final boolean HSM_SSL = Boolean.parseBoolean(System.getProperty("thales.hsm.ssl", "false"));
    private static final boolean TEST_ENABLED = Boolean.parseBoolean(System.getProperty("thales.hsm.test.enabled", "false"));

    /**
     * Test ThalesCommand creation and formatting.
     */
    @Test
    public void testThalesCommandCreation() {
        // Test echo command
        ThalesCommand echoCmd = ThalesCommand.Commands.echo("TEST DATA");
        assertEquals("NO", echoCmd.getCommandCode());
        assertEquals("TEST DATA", echoCmd.getCommandData());
        assertEquals("NOTEST DATA", echoCmd.getFullCommand());

        // Test diagnostics command
        ThalesCommand diagCmd = ThalesCommand.Commands.diagnostics();
        assertEquals("NC", diagCmd.getCommandCode());
        assertEquals("", diagCmd.getCommandData());
        assertEquals("NC", diagCmd.getFullCommand());

        // Test key generation command
        ThalesCommand keyGenCmd = ThalesCommand.Commands.generateKey("0", "U");
        assertEquals("A0", keyGenCmd.getCommandCode());
        assertEquals("0U", keyGenCmd.getCommandData());
        assertEquals("A00U", keyGenCmd.getFullCommand());
    }

    /**
     * Test ThalesResponse parsing.
     */
    @Test
    public void testThalesResponseParsing() {
        // Test successful response
        String successResponse = "NP00TEST DATA";
        ThalesResponse response = new ThalesResponse(successResponse.getBytes());
        assertEquals("NP", response.getResponseCode());
        assertEquals("00", response.getErrorCode());
        assertEquals("TEST DATA", response.getResponseData());
        assertTrue(response.isSuccess());
        assertEquals("No error - Command executed successfully", response.getErrorMessage());

        // Test error response
        String errorResponse = "NP01";
        ThalesResponse errorResp = new ThalesResponse(errorResponse.getBytes());
        assertEquals("NP", errorResp.getResponseCode());
        assertEquals("01", errorResp.getErrorCode());
        assertFalse(errorResp.isSuccess());
        assertEquals("Verification failure", errorResp.getErrorMessage());

        // Test various error codes
        assertEquals("Key parity error",
                new ThalesResponse("NP02".getBytes()).getErrorMessage());
        assertEquals("Invalid input data",
                new ThalesResponse("NP10".getBytes()).getErrorMessage());
        assertEquals("Syntax error in command",
                new ThalesResponse("NP20".getBytes()).getErrorMessage());
    }

    /**
     * Test HSM connection and echo command (requires HSM).
     */
    @Test
    public void testHSMEchoCommand() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set thales.hsm.test.enabled=true to run");
            return;
        }

        try (ThalesHSMClient client = new ThalesHSMClient(HSM_HOST, HSM_PORT, HSM_SSL)) {
            // Verify connection
            assertTrue("Should be connected to HSM", client.isConnected());
            assertNotNull("Channel ID should not be null", client.getChannelId());

            // Send echo command
            String testData = "HELLO HSM";
            ThalesResponse response = client.echo(testData);

            // Verify response
            assertNotNull("Response should not be null", response);
            assertTrue("Echo command should succeed: " + response.getErrorMessage(), response.isSuccess());
            assertEquals("NP", response.getResponseCode());
            assertEquals(testData, response.getResponseData());

            // Verify counters
            assertTrue("Should have sent at least 1 command", client.getSentCommands() >= 1);
            assertTrue("Should have received at least 1 response", client.getReceivedResponses() >= 1);

            log.info("Echo test successful: " + response);

        } catch (Exception e) {
            if (TEST_ENABLED) {
                fail("Echo command failed: " + e.getMessage());
            }
        }
    }

    /**
     * Test HSM diagnostics command (requires HSM).
     */
    @Test
    public void testHSMDiagnostics() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set thales.hsm.test.enabled=true to run");
            return;
        }

        try (ThalesHSMClient client = new ThalesHSMClient(HSM_HOST, HSM_PORT, HSM_SSL)) {
            // Send diagnostics command
            ThalesResponse response = client.diagnostics();

            // Verify response
            assertNotNull("Response should not be null", response);
            assertTrue("Diagnostics command should succeed: " + response.getErrorMessage(),
                    response.isSuccess());
            assertEquals("ND", response.getResponseCode());
            assertNotNull("Response data should contain LMK check value", response.getResponseData());

            log.info("Diagnostics test successful: " + response);

        } catch (Exception e) {
            if (TEST_ENABLED) {
                fail("Diagnostics command failed: " + e.getMessage());
            }
        }
    }

    /**
     * Test multiple concurrent commands (requires HSM).
     */
    @Test(timeout = 30000)
    public void testConcurrentCommands() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set thales.hsm.test.enabled=true to run");
            return;
        }

        try (ThalesHSMClient client = new ThalesHSMClient(HSM_HOST, HSM_PORT, HSM_SSL)) {
            int commandCount = 10;

            // Send multiple echo commands concurrently
            for (int i = 0; i < commandCount; i++) {
                String testData = "TEST " + i;
                ThalesResponse response = client.echo(testData);

                assertNotNull("Response " + i + " should not be null", response);
                assertTrue("Command " + i + " should succeed", response.isSuccess());
                assertEquals("Response data should match", testData, response.getResponseData());
            }

            // Verify all commands were sent and received
            assertEquals("Should have sent " + commandCount + " commands",
                    commandCount, client.getSentCommands());
            assertEquals("Should have received " + commandCount + " responses",
                    commandCount, client.getReceivedResponses());

            log.info("Concurrent commands test successful");

        } catch (Exception e) {
            if (TEST_ENABLED) {
                fail("Concurrent commands test failed: " + e.getMessage());
            }
        }
    }

    /**
     * Test connection to invalid HSM address.
     */
    @Test(timeout = 10000)
    public void testInvalidConnection() {
        try {
            ThalesHSMClient client = new ThalesHSMClient("invalid-host-does-not-exist.local", 9999, false);
            client.close();
            fail("Should throw exception for invalid connection");
        } catch (Exception e) {
            // Expected - connection should fail
            assertTrue("Exception message should mention connection failure",
                    e.getMessage().contains("Failed to"));
        }
    }

    /**
     * Test command with request ID tracking.
     */
    @Test
    public void testCommandWithRequestId() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set thales.hsm.test.enabled=true to run");
            return;
        }

        try (ThalesHSMClient client = new ThalesHSMClient(HSM_HOST, HSM_PORT, HSM_SSL)) {
            String requestId = "TEST-REQUEST-123";
            ThalesCommand command = new ThalesCommand("NO", "TEST", requestId);

            ThalesResponse response = client.sendCommand(command);

            assertNotNull("Response should not be null", response);
            assertTrue("Command should succeed", response.isSuccess());

            log.info("Request ID tracking test successful");

        } catch (Exception e) {
            if (TEST_ENABLED) {
                fail("Request ID test failed: " + e.getMessage());
            }
        }
    }

    /**
     * Test async command execution.
     */
    @Test(timeout = 10000)
    public void testAsyncCommand() {
        if (!TEST_ENABLED) {
            log.info("Skipping HSM test - set thales.hsm.test.enabled=true to run");
            return;
        }

        try (ThalesHSMClient client = new ThalesHSMClient(HSM_HOST, HSM_PORT, HSM_SSL)) {
            ThalesCommand echoCmd = ThalesCommand.Commands.echo("ASYNC TEST");

            // Send command asynchronously
            CompletableFuture<ThalesResponse> future = client.sendCommandAsync(echoCmd);

            // Wait for response
            ThalesResponse response = future.get();

            assertNotNull("Response should not be null", response);
            assertTrue("Command should succeed", response.isSuccess());
            assertEquals("ASYNC TEST", response.getResponseData());

            log.info("Async command test successful");

        } catch (Exception e) {
            if (TEST_ENABLED) {
                fail("Async command test failed: " + e.getMessage());
            }
        }
    }
}
