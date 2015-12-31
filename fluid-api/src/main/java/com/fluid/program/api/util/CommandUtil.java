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

/**
 * User: jbruwer Date: 10/1/13 Time: 1:07 PM
 */
public class CommandUtil {

    /**
     *
     */
    public static final class CommandResult {
        private int exitCode;
        private String[] resultLines;

        /**
         *
         * @param exitCodeParam
         * @param resultLinesParam
         */
        private CommandResult(int exitCodeParam, String[] resultLinesParam) {
            this.exitCode = exitCodeParam;
            this.resultLines = resultLinesParam;
        }

        /**
         *
         * @return
         */
        public int getExitCode() {
            return this.exitCode;
        }

        /**
         *
         * @return
         */
        public String[] getResultLines() {
            return this.resultLines;
        }

        /**
         *
         * @return
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
     *
     * @param commandParams
     * @return
     */
    public static CommandResult executeCommand(String... commandParams) throws IOException {
        if (commandParams == null || commandParams.length == 0) {
            throw new IOException("Unable to execute command. No commands provided.");
        }

        List<String> returnedLines = new ArrayList<>();

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
     *
     * @param objectCommandParam
     * @return
     */
    public static CommandResult executeCommand(String objectCommandParam) throws Exception {
        if (objectCommandParam == null) {
            return new CommandResult(333, new String[] {
                    "No Object Command provided. 'null' not allowed." });
        }

        return CommandUtil.executeCommand(new String[] { objectCommandParam });
    }
}
