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

import java.io.Serializable;
import java.util.Properties;

/**
 * Base class for any utility classes used in the Fluid framework.
 *
 * @author jasonbruwer on 2016/08/20.
 * @since 1.0
 */
public abstract class ABaseUtil implements Serializable {

    /**
     * Retrieves a property and returns the value as {@code java.lang.String}.
     *
     * @param propertiesParam The origin of the properties.
     * @param propertyKeyParam The property key.
     * @return The property value.
     */
    protected static String getStringPropertyFromProperties(
            Properties propertiesParam,
            String propertyKeyParam)
    {
        if(propertiesParam == null || propertiesParam.isEmpty())
        {
            return null;
        }

        return propertiesParam.getProperty(propertyKeyParam);
    }

    /**
     * Retrieves a property and returns the value as {@code int}.
     *
     * @param propertiesParam The origin of the properties.
     * @param propertyKeyParam The property key.
     * @return The property value as an {@code int}.
     */
    protected static int getIntPropertyFromProperties(
            Properties propertiesParam,
            String propertyKeyParam)
    {
        String strProp = getStringPropertyFromProperties(
                propertiesParam, propertyKeyParam);

        if(strProp == null || strProp.trim().isEmpty())
        {
            return -1;
        }

        try
        {
            return Integer.parseInt(strProp);
        }
        //
        catch(NumberFormatException nfe)
        {
            return -1;
        }
    }
}
