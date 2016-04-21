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


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import com.fluid.program.api.util.cache.exception.FluidCacheException;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.MultiChoice;

/**
 * Cache Utility class used for {@code Field} value retrieval actions.
 *
 * @author jasonbruwer on 2016/02/29.
 * @since 1.0
 */
public class CacheUtil {

    //TODO can start off by fetching values only...
    //TODO use the  [getWord()] to determine the data type.
    //TODO use      [getValue()] to get the field value.
    //TODO use      [fieldValueForCachingId] to get the FieldValueId.

    private static final String NULL = "null";
    private static final String DASH = "-";

    private transient MemcachedClient memcachedClient;

    private String cacheHost = null;
    private int cachePort = -1;

    //Cached Methods.
    private Method methodGetWord;
    private Method methodGetValue;

    /**
     *
     */
    private static class FlowJobTypeMappings
    {
        public static final String DATE_TIME = "Date Time";
        public static final String DECIMAL = "Decimal";
        public static final String MULTIPLE_CHOICE = "Multiple Choice";
        public static final String PARAGRAPH_TEXT = "Paragraph Text";
        public static final String TABLE_FIELD = "Table Field";
        public static final String TEXT = "Text";
        public static final String TEXT_ENCRYPTED = "Text Encrypted";
        public static final String TRUE_FALSE = "True / False";
    }

    /**
     *
     */
    public static class CachedFieldValue implements Serializable
    {
        public Object cachedFieldValue;
        public String dataType;

        /**
         *
         * @return
         */
        public Field getCachedFieldValueAsField()
        {
            Field returnVal = new Field();

            if(FlowJobTypeMappings.MULTIPLE_CHOICE.equals(this.dataType) &&
                    cachedFieldValue != null)
            {
                MultiChoice multiChoice = new MultiChoice();

                List<String> availableChoices = null;//TODO
                List<String> selectedChoices = null;//TODO

                multiChoice.setAvailableMultiChoices(availableChoices);
                multiChoice.setSelectedMultiChoices(selectedChoices);

                returnVal.setFieldValue(multiChoice);
            }
            //No table field...
            else if(FlowJobTypeMappings.TABLE_FIELD.equals(this.dataType))
            {
                return null;
            }
            else
            {
                returnVal.setFieldValue(this.cachedFieldValue);
            }

            return returnVal;
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

        if(this.cacheHost == null || this.cacheHost.trim().isEmpty())
        {
            throw new FluidCacheException("Cache Host cannot be empty.");
        }

        if(this.cachePort < 1 || this.cachePort > 65535)
        {
            throw new FluidCacheException("Cache Port number '"+this.cachePort+"' is invalid.");
        }

        this.initXMemcachedClient();
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
        if((formDefIdParam == null || formContIdParam == null) ||
                formFieldIdParam == null)
        {
            return null;
        }

        String storageKey = this.getStorageKeyFrom(
                formDefIdParam,
                formContIdParam,
                formFieldIdParam);

        Object objWithKey = null;
        try {
            objWithKey = this.memcachedClient.get(storageKey);
        }
        //
        catch (MemcachedException | TimeoutException | InterruptedException e) {

            throw new FluidCacheException("Unable to get Field value for '"+storageKey+"'." +
                    "Contact administrator. "+e.getMessage(),e);
        }

        return this.getCacheFieldValueFromObject(objWithKey);
    }

    /**
     *
     * @param objWithKeyParam
     * @return
     */
    private CachedFieldValue getCacheFieldValueFromObject(Object objWithKeyParam)
    {
        if(objWithKeyParam == null)
        {
            return null;
        }

        //Get Word...
        if(this.methodGetWord == null)
        {
            this.methodGetWord = this.getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.IWord.METHOD_getWord);
        }

        //Get Value...
        if(this.methodGetValue == null)
        {
            this.methodGetValue = this.getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.ADataType.METHOD_getValue);
        }

        //Word...
        Object getWordObj = this.invoke(this.methodGetWord, objWithKeyParam);
        String getWordVal = null;
        if(getWordObj instanceof String)
        {
            getWordVal = (String)getWordObj;
        }

        //Value...
        Object getValueObj = this.invoke(this.methodGetValue, objWithKeyParam);
        if(getValueObj == null)
        {
            return null;
        }

        if(getWordVal == null)
        {
            throw new FluidCacheException(
                    "Get Word value is 'null'. Not allowed.");
        }

        CachedFieldValue returnVal = new CachedFieldValue();

        returnVal.dataType = getWordVal;
        returnVal.cachedFieldValue = getValueObj;

        return returnVal;
    }

    /**
     *
     * @param clazzParam
     * @param nameParam
     * @return
     */
    private Method getMethod(Class clazzParam, String nameParam)
    {
        try {
            Method returnVal = clazzParam.getDeclaredMethod(nameParam);
            returnVal.setAccessible(true);
            return returnVal;
        }
        //
        catch (NoSuchMethodException e) {

            throw new FluidCacheException(
                    "Unable to get method '"+
                            nameParam +"'. "+e.getMessage(),e);
        }
    }

    /**
     *
     * @param methodParam
     * @param objParam
     * @return
     */
    private Object invoke(Method methodParam,Object objParam)
    {
        try {
            return methodParam.invoke(objParam);
        }
        //
        catch (InvocationTargetException | IllegalAccessException e) {

            throw new FluidCacheException(
                    "Unable to invoke method '"+
                            methodParam.getName() +"'. "+e.getMessage(),e);
        }
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
     *
     * @return
     */
    private MemcachedClient initXMemcachedClient()
    {
        if(this.memcachedClient != null && !this.memcachedClient.isShutdown())
        {
            return this.memcachedClient;
        }

        try{
            this.memcachedClient = new XMemcachedClient(
                    this.cacheHost,this.cachePort);

            return this.memcachedClient;
        }
        //Unable to create client with connection.
        catch (IOException e) {

            throw new FluidCacheException(
                    "Unable to create MemCache client. "+e.getMessage(), e);
        }
    }
}
