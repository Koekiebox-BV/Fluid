package com.fluidbpm.ws.client.v1.netty.hsm;

import lombok.extern.java.Log;

import java.util.concurrent.CompletableFuture;

/**
 * Example usage of the Thales HSM client with the international command set.
 * Demonstrates common HSM operations including echo test, diagnostics,
 * and various cryptographic commands.
 *
 * @author jasonbruwer
 * @since 1.14
 */
@Log
public class ThalesHSMExample {

    /**
     * Example 1: Simple echo test to verify HSM connectivity.
     */
    public static void exampleEchoTest() throws Exception {
        // Connect to HSM (adjust host/port as needed)
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            log.info("Connected to HSM: " + client.getChannelId());

            // Send echo command
            String testData = "HELLO HSM";
            ThalesResponse response = client.echo(testData);

            // Check response
            if (response.isSuccess()) {
                log.info("Echo successful! Response: " + response.getResponseData());
            } else {
                log.severe("Echo failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 2: Run diagnostics to check HSM status.
     */
    public static void exampleDiagnostics() throws Exception {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Send diagnostics command (NC)
            ThalesResponse response = client.diagnostics();

            if (response.isSuccess()) {
                log.info("HSM is operational. LMK Check Value: " + response.getResponseData());
            } else {
                log.severe("Diagnostics failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 3: Generate a cryptographic key (ZMK/TMK/TPK).
     */
    public static void exampleGenerateKey() throws Exception {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Generate a double-length ZMK
            ThalesCommand command = ThalesCommand.Commands.generateKey("0", "X");
            ThalesResponse response = client.sendCommand(command);

            if (response.isSuccess()) {
                log.info("Key generated successfully!");
                log.info("Key under LMK: " + response.getResponseData());
            } else {
                log.severe("Key generation failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 4: Asynchronous command execution.
     */
    public static void exampleAsyncCommands() throws Exception {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Send multiple commands asynchronously
            CompletableFuture<ThalesResponse> echo1 = client.sendCommandAsync(
                    ThalesCommand.Commands.echo("TEST 1")
            );

            CompletableFuture<ThalesResponse> echo2 = client.sendCommandAsync(
                    ThalesCommand.Commands.echo("TEST 2")
            );

            CompletableFuture<ThalesResponse> diag = client.sendCommandAsync(
                    ThalesCommand.Commands.diagnostics()
            );

            // Wait for all to complete
            CompletableFuture.allOf(echo1, echo2, diag).join();

            // Process responses
            ThalesResponse r1 = echo1.get();
            ThalesResponse r2 = echo2.get();
            ThalesResponse r3 = diag.get();

            log.info("Echo 1: " + r1.getResponseData());
            log.info("Echo 2: " + r2.getResponseData());
            log.info("Diagnostics: " + (r3.isSuccess() ? "OK" : "FAILED"));
        }
    }

    /**
     * Example 5: Generate and verify MAC.
     */
    public static void exampleGenerateMAC() throws Exception {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Generate MAC on a message
            // Format: M0 + Key Type + MAC Key + Message Length + Message
            String keyType = "0"; // ZMK
            String macKey = "U1234567890ABCDEF1234567890ABCDE"; // Example key under LMK
            String message = "1234567890123456"; // Example message
            String messageLength = String.format("%04d", message.length());

            ThalesCommand macCommand = ThalesCommand.Commands.generateMAC(
                    keyType, macKey, messageLength, message
            );

            ThalesResponse response = client.sendCommand(macCommand);

            if (response.isSuccess()) {
                log.info("MAC generated: " + response.getResponseData());
            } else {
                log.severe("MAC generation failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 6: Custom command construction.
     */
    public static void exampleCustomCommand() throws Exception {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Build a custom command
            // Example: KQ - Import key (custom key exchange command)
            ThalesCommand customCmd = new ThalesCommand(
                    "KQ",  // Command code
                    "0U1234567890ABCDEF1234567890ABCDE",  // Command data
                    "REQUEST-001"  // Optional request ID for tracking
            );

            ThalesResponse response = client.sendCommand(customCmd);

            log.info("Custom command response: " + response);
        }
    }

    /**
     * Example 7: Error handling.
     */
    public static void exampleErrorHandling() {
        try (ThalesHSMClient client = new ThalesHSMClient("localhost", 1500, false)) {
            // Send a command that might fail
            ThalesCommand command = new ThalesCommand("XX", "INVALID");
            ThalesResponse response = client.sendCommand(command);

            if (!response.isSuccess()) {
                String errorCode = response.getErrorCode();
                String errorMsg = response.getErrorMessage();

                log.warning("Command failed with error code: " + errorCode);
                log.warning("Error message: " + errorMsg);

                // Handle specific errors
                switch (errorCode) {
                    case "15":
                        log.severe("Invalid command code - check HSM documentation");
                        break;
                    case "20":
                        log.severe("Syntax error - check command format");
                        break;
                    case "27":
                        log.severe("LMK error - check key configuration");
                        break;
                    default:
                        log.severe("Unexpected error: " + errorMsg);
                }
            }

        } catch (Exception e) {
            log.severe("HSM communication error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Example 8: SSL/TLS connection to HSM.
     */
    public static void exampleSecureConnection() throws Exception {
        // Connect with SSL/TLS enabled
        try (ThalesHSMClient client = new ThalesHSMClient("hsm.example.com", 1500, true)) {
            log.info("Secure connection established");

            ThalesResponse response = client.diagnostics();
            log.info("HSM Status: " + (response.isSuccess() ? "OK" : "ERROR"));
        }
    }

    /**
     * Main method to run examples.
     */
    public static void main(String[] args) {
        log.info("=== Thales HSM Client Examples ===");

        try {
            log.info("\n--- Example 1: Echo Test ---");
            exampleEchoTest();

            log.info("\n--- Example 2: Diagnostics ---");
            exampleDiagnostics();

            log.info("\n--- Example 4: Async Commands ---");
            exampleAsyncCommands();

            log.info("\n--- Example 7: Error Handling ---");
            exampleErrorHandling();

            // Uncomment these if you have proper HSM setup:
            // exampleGenerateKey();
            // exampleGenerateMAC();
            // exampleCustomCommand();
            // exampleSecureConnection();

        } catch (Exception e) {
            log.severe("Example failed: " + e.getMessage());
            e.printStackTrace();
        }

        log.info("\n=== Examples Complete ===");
    }
}
