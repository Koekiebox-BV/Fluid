/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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

package com.fluid.program.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.auth0.AccessTokenRequest;
import com.fluid.program.api.vo.auth0.NormalizedUserProfile;

/**
 * Utility class used for executing command line operations.
 *
 * See more at: https://auth0.com/
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see NormalizedUserProfile
 * @see AccessTokenRequest
 * @see ABaseFluidJSONObject
 */
public class CommandUtil {

    /**
     * Result value object when a command line operation is finish.
     */
    public static final class CommandResult {
        private int exitCode;
        private String[] resultLines;

        /**
         * Sets the exit code and result lines.
         *
         * @param exitCodeParam The exit code.
         * @param resultLinesParam The result lines.
         */
        private CommandResult(int exitCodeParam, String[] resultLinesParam) {
            this.exitCode = exitCodeParam;
            this.resultLines = resultLinesParam;
        }

        /**
         * Gets the exit code.
         *
         * @return Exit Code.
         */
        public int getExitCode() {
            return this.exitCode;
        }

        /**
         * Gets the result lines.
         *
         * @return Result lines.
         */
        public String[] getResultLines() {
            return this.resultLines;
        }

        /**
         * Returns the {@code String[]} result lines
         * as a single {@code String}.
         *
         * @return Result as a {@code String}.
         */
        @Override
        public String toString() {

            if (this.resultLines == null) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String line : this.resultLines) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
    }

    /**
     * Executes the {@code commandParams} and returns the result.
     *
     * @param commandParams The command and parameters to execute.
     * @return The result of the execution.
     *
     * @throws IOException If execution of the command fails.
     *
     * @see CommandResult
     */
    public static CommandResult executeCommand(String... commandParams) throws IOException {
        if (commandParams == null || commandParams.length == 0) {
            throw new IOException("Unable to execute command. No commands provided.");
        }

        List<String> returnedLines = new ArrayList();

        try {
            Process process = null;
            if (commandParams.length == 1) {
                process = Runtime.getRuntime().exec(commandParams[0]);
            }
            //
            else {
                process = Runtime.getRuntime().exec(commandParams);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                returnedLines.add(readLine);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((readLine = errorReader.readLine()) != null) {
                returnedLines.add(readLine);
            }

            int exitValue = -1000;
            try {
                exitValue = process.waitFor();
            }
            //
            catch (InterruptedException e) {
                String commandString = (commandParams == null || commandParams.length == 0) ? "<unknown>" : commandParams[0];

                throw new IOException("Unable to wait for command [" + commandString + "] to exit. " + e.getMessage(), e);
            }

            String[] rtnArr = {};
            return new CommandResult(exitValue, returnedLines.toArray(rtnArr));
        }
        //
        catch (IOException ioExeption) {

            String commandString = (commandParams == null || commandParams.length == 0) ? "<unknown>" : commandParams[0];

            throw new IOException("Unable to execute command/s [" + commandString + "]. " + ioExeption.getMessage(), ioExeption);
        }
    }

    /**
     * Executes the {@code objectCommandParam} and returns the result.
     *
     * @param objectCommandParam The command to execute.
     * @return The result of the execution.
     *
     * @see CommandUtil#executeCommand(String...)
     *
     * @throws Exception If anything goes wrong during execution.
     */
    public static CommandResult executeCommand(String objectCommandParam) throws Exception {
        if (objectCommandParam == null) {
            return new CommandResult(333, new String[] {
                    "No Object Command provided. 'null' not allowed." });
        }

        return CommandUtil.executeCommand(new String[] { objectCommandParam });
    }
}
