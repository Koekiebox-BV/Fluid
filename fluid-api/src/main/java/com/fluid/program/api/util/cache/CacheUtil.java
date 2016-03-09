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

package com.fluid.program.api.util.cache;


import java.io.Serializable;

import com.fluid.program.api.vo.Field;

/**
 * Cache Utility class used for {@code Field} value retrieval actions.
 *
 * @author jasonbruwer on 2016/02/29.
 * @since 1.0
 */
public class CacheUtil {

    private static final String NULL = "null";
    private static final String DASH = "-";

    private String cacheHost = null;
    private int cachePort = -1;

    /**
     *
     */
    public static class CachedFieldValue implements Serializable
    {
        public Long fieldValueForCachingId;
        public Object cachedFieldValue;
        public int cacheFieldTypeId;

        /**
         *
         * @return
         */
        public Field getCachedFieldValueAsField()
        {
            return null;
        }
    }


    /**
     * New instance of cache util using the
     * provided Host {@code cacheHostParam} and
     * Port {@code cachePortParam}.
     *
     * @param cacheHostParam The MemCache Host IP or hostname.
     * @param cachePortParam The MemCache Port.
     */
    public CacheUtil(
            String cacheHostParam,
            int cachePortParam) {

        this.cacheHost = cacheHostParam;
        this.cachePort = cachePortParam;
    }

    /**
     * Generates the storage key the provided parameters.
     *
     * @param formDefIdParam The Form Definition Id.
     * @param formContIdParam The Form Container Id.
     * @param formFieldIdParam The Form Field Id.
     *
     * @return Storage Key
     */
    private String getStorageKeyFrom(
            Long formDefIdParam,
            Long formContIdParam,
            Long formFieldIdParam)
    {
        StringBuffer stringBuff = new StringBuffer();

        //Form Definition...
        if(formDefIdParam == null)
        {
            stringBuff.append(NULL);
        }
        else
        {
            stringBuff.append(formDefIdParam.toString());
        }

        stringBuff.append(DASH);

        //Form Container...
        if(formContIdParam == null)
        {
            stringBuff.append(NULL);
        }
        else
        {
            stringBuff.append(formContIdParam.toString());
        }

        stringBuff.append(DASH);

        //Form Field...
        if(formFieldIdParam == null)
        {
            stringBuff.append(NULL);
        }
        else {
            stringBuff.append(formFieldIdParam.toString());
        }

        return stringBuff.toString();
    }

    /**
     * Retrieves the {@code CachedFieldValue} value stored under
     * the params.
     *
     * @param formDefIdParam The Form Definition Id.
     * @param formContIdParam The Form Container Id.
     * @param formFieldIdParam The Form Field Id.
     *
     * @return Storage Key
     */
    public CachedFieldValue getCachedFieldValueFrom(
            Long formDefIdParam,
            Long formContIdParam,
            Long formFieldIdParam)
    {

        CachedFieldValue returnVal = null;



        return returnVal;
    }



    //TODO can start off by fetching values only...
    //TODO use the  [getWord()] to determine the data type.
    //TODO use      [getValue()] to get the field value.
    //TODO use      [fieldValueForCachingId] to get the FieldValueId.
}
