package com.fluid.ws.client.v1;

import java.util.concurrent.TimeUnit;

/**
 * Created by jasonbruwer on 15/01/19.
 */
public class ABaseTestCase {

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "12345";

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
}
