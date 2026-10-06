package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmSslContexts;
import lombok.extern.java.Log;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Example usage of the Atalla AT1000 HSM client.
 * Demonstrates the echo test, the software version query, asynchronous
 * commands, error handling and a mutual-TLS connection.
 *
 * @author jasonbruwer
 * @since 1.15
 */
@Log
public class AtallaHSMExample {

    /**
     * Example 1: Echo test to verify HSM connectivity (command 00).
     *
     * @throws Exception If an error occurs communicating with the HSM.
     */
    public static void exampleEchoTest() throws Exception {
        try (AtallaHSMClient client = new AtallaHSMClient("localhost", 7000, false)) {
            log.info("Connected to HSM: " + client.getChannelId());

            String testData = "This is a test.";
            AtallaResponse response = client.echo(testData);

            // <00#000081#This is a test.#>
            if (response.isSuccess()) {
                log.info("Echo successful! Message: " + response.getEchoMessage()
                        + ", software version: " + response.getSoftwareVersion());
            } else {
                log.severe("Echo failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 2: Query the HSM software version (command 1101).
     *
     * @throws Exception If an error occurs communicating with the HSM.
     */
    public static void exampleSoftwareVersion() throws Exception {
        try (AtallaHSMClient client = new AtallaHSMClient("localhost", 7000, false)) {
            AtallaResponse response = client.softwareVersion();

            // <2101#Atalla HSM AT1000-AKB Version: 8.53, ...#B19C#3#>
            if (response.isSuccess()) {
                log.info("Version: " + response.getField(1)
                        + ", image CRC: " + response.getField(2)
                        + ", product code: " + response.getField(3));
            } else {
                log.severe("Version query failed: " + response.getErrorMessage());
            }
        }
    }

    /**
     * Example 3: Asynchronous, pipelined commands correlated by context tag.
     *
     * @throws Exception If an error occurs communicating with the HSM.
     */
    public static void exampleAsyncCommands() throws Exception {
        try (AtallaHSMClient client = new AtallaHSMClient("localhost", 7000, false)) {
            CompletableFuture<AtallaResponse> echo1 = client.sendCommandAsync(AtallaCommand.Commands.echo("TEST 1"));
            CompletableFuture<AtallaResponse> echo2 = client.sendCommandAsync(AtallaCommand.Commands.echo("TEST 2"));
            CompletableFuture<AtallaResponse> version = client.sendCommandAsync(AtallaCommand.Commands.softwareVersion());

            CompletableFuture.allOf(echo1, echo2, version).join();

            log.info("Echo 1: " + echo1.get().getEchoMessage());
            log.info("Echo 2: " + echo2.get().getEchoMessage());
            log.info("Version: " + (version.get().isSuccess() ? version.get().getField(1) : "FAILED"));
        }
    }

    /**
     * Example 4: Custom command construction and error decoding.
     */
    public static void exampleCustomCommandAndErrors() {
        try (AtallaHSMClient client = new AtallaHSMClient("localhost", 7000, false)) {
            // 10 - Generate 3DES Working Key: <10#3PUNE000##D#>
            AtallaCommand generateKey = new AtallaCommand("10", "3PUNE000", "", "D");
            AtallaResponse response = client.sendCommand(generateKey);

            if (response.isSuccess()) {
                log.info("Working key: " + response.getField(1) + ", check digits: " + response.getField(3));
            } else {
                // <00#XXYYZZ#[detail#]>
                log.warning("Error " + response.getErrorCode() + " (" + response.getErrorMessage()
                        + ") in field " + response.getErrorFieldNumber()
                        + (response.getErrorDetail() != null ? ", detail " + response.getErrorDetail() : ""));
            }
        } catch (Exception e) {
            log.severe("HSM communication error: " + e.getMessage());
        }
    }

    /**
     * Example 5: Mutual TLS using the PEM files from the MYHSM sample kit.
     *
     * @throws Exception If an error occurs communicating with the HSM.
     */
    public static void exampleSecureConnection() throws Exception {
        AtallaHSMClientConfig config = AtallaHSMClientConfig.builder()
                .host("hsm.example.com")
                .port(13502)
                .sslContext(HsmSslContexts.mutualTls(
                        new File("client_MYHSM.crt"),
                        new File("client_MYHSM.key"),
                        new File("CA_MYHSM_chain.crt")))
                .build();

        try (AtallaHSMClient client = new AtallaHSMClient(config)) {
            AtallaResponse response = client.echo("HELLO");
            log.info("HSM status: " + (response.isSuccess() ? "OK" : "ERROR " + response.getErrorMessage()));
        }
    }

    /**
     * Main method to run examples.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        log.info("=== Atalla HSM Client Examples ===");

        try {
            log.info("\n--- Example 1: Echo Test ---");
            exampleEchoTest();

            log.info("\n--- Example 2: Software Version ---");
            exampleSoftwareVersion();

            log.info("\n--- Example 3: Async Commands ---");
            exampleAsyncCommands();

            log.info("\n--- Example 4: Custom Command / Errors ---");
            exampleCustomCommandAndErrors();

            // Requires certificates:
            // exampleSecureConnection();

        } catch (Exception e) {
            log.severe("Example failed: " + e.getMessage());
            e.printStackTrace();
        }

        log.info("\n=== Examples Complete ===");
    }
}
