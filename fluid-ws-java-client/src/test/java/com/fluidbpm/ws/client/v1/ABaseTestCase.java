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

package com.fluidbpm.ws.client.v1;

import java.util.concurrent.TimeUnit;

import com.fluidbpm.ws.client.v1.user.LoginClient;

/**
 * Base test case for all Fluid WS Test Cases.
 *
 * {@code FLUID_WS_URL}
 * {@code FLUID_WS_USERNAME}
 * {@code FLUID_WS_PASSWORD}
 *
 * Created by jasonbruwer on 15/01/19.
 */
public class ABaseTestCase {

    public static String BASE_URL = getTestBASE_URL();
    public static String USERNAME = getTestUsername();
    public static String PASSWORD = getTestPassword();

    /**
     * 
     * @return
     */
    private static String getTestBASE_URL()
    {
        return System.getProperty(
                "FLUID_WS_URL",
                "http://localhost:8080/fluid-ws/");
    }

    /**
     *
     * @return
     */
    private static String getTestUsername()
    {
        return System.getProperty(
                "FLUID_WS_USERNAME",
                "admin");
    }

    /**
     *
     * @return
     */
    private static String getTestPassword()
    {
        return System.getProperty(
                "FLUID_WS_PASSWORD",
                "12345");
    }

    /**
     *
     * @param secondsToMillisParam
     */
    public void sleepForSeconds(int secondsToMillisParam)
    {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(secondsToMillisParam));
        }
        //
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void sleepForASecond()
    {
        this.sleepForSeconds(1);
    }


    /**
     * Check whether the {@code BASE_URL} is valid.
     * 
     * @return {@code true} if the connection is valid, otherwise {@code false}.
     */
    protected boolean isConnectionValid() {

        LoginClient loginClient = new LoginClient(BASE_URL);

        try {
            boolean isConValid = loginClient.isConnectionValid();

            if (!isConValid)
            {
                System.err.println("Connection to '"
                        +BASE_URL+"' is not valid!");
            }

            return isConValid;
        }
        finally {
            loginClient.closeAndClean();
        }
    }
}
