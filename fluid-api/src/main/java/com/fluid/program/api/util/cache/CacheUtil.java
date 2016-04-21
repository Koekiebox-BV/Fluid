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

    private static final String NULL = "null";
    private static final String DASH = "-";

    private transient MemcachedClient memcachedClient;

    private String cacheHost = null;
    private int cachePort = -1;

    /**
     *
     */
    private static class FlowJobType
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
    private static enum FlowJobTypeMapping
    {
        DateTime(FlowJobType.DATE_TIME, Field.Type.DateTime),
        Decimal(FlowJobType.DECIMAL, Field.Type.Decimal),
        MultiChoice(FlowJobType.MULTIPLE_CHOICE, Field.Type.MultipleChoice),
        ParagraphText(FlowJobType.PARAGRAPH_TEXT, Field.Type.ParagraphText),
        TableField(FlowJobType.TABLE_FIELD, Field.Type.Table),
        Text(FlowJobType.TEXT, Field.Type.Text),
        TextEncrypted(FlowJobType.TEXT_ENCRYPTED, Field.Type.TextEncrypted),
        TrueFalse(FlowJobType.TRUE_FALSE, Field.Type.TrueFalse),
        ;

        private String flowJobDataTypeDesc;
        private Field.Type fluidType;

        /**
         *
         * @param flowJobDataTypeDescParam
         * @param fluidTypeParam
         */
        FlowJobTypeMapping(
                String flowJobDataTypeDescParam,
                Field.Type fluidTypeParam)
        {
            this.flowJobDataTypeDesc = flowJobDataTypeDescParam;
            this.fluidType = fluidTypeParam;
        }

        /**
         *
         * @param flowJobTypeParam
         * @return
         */
        public static Field.Type getFluidTypeFromFlowJobType(
                String flowJobTypeParam)
        {
            if(flowJobTypeParam == null || flowJobTypeParam.trim().isEmpty())
            {
                return null;
            }

            for(FlowJobTypeMapping mapping : values())
            {
                if(flowJobTypeParam.equals(mapping.flowJobDataTypeDesc))
                {
                    return mapping.fluidType;
                }
            }

            return null;
        }
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

            //No table field...
            if(FlowJobType.TABLE_FIELD.equals(this.dataType))
            {
                return null;
            }
            else
            {
                returnVal.setFieldValue(this.cachedFieldValue);
            }

            //Set the Type as Enum...
            returnVal.setTypeAsEnum(
                    FlowJobTypeMapping.getFluidTypeFromFlowJobType(dataType));

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
        Method methodGetWord = this.getMethod(
                objWithKeyParam.getClass(),
                CustomCode.IWord.METHOD_getWord);

        //Get Value...
        Method methodGetValue = this.getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.ADataType.METHOD_getValue);

        //Word...
        Object getWordObj = this.invoke(methodGetWord, objWithKeyParam);
        String getWordVal = null;
        if(getWordObj instanceof String)
        {
            getWordVal = (String)getWordObj;
        }

        //Value...
        Object getValueObj = null;
        if(FlowJobType.MULTIPLE_CHOICE.equals(getWordVal))
        {
            MultiChoice multiChoice = new MultiChoice();

            //Available Choices...
            Method methodAvailableChoices = getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.MultipleChoice.METHOD_getAvailableChoices);

            Object availChoicesObj =
                    invoke(methodAvailableChoices, objWithKeyParam);

            if(availChoicesObj instanceof List)
            {
                multiChoice.setAvailableMultiChoices((List)availChoicesObj);
            }

            //Selected...
            Method methodSelectedChoices = getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.MultipleChoice.METHOD_getSelectedChoices);

            Object selectedChoicesObj =
                    invoke(methodSelectedChoices, objWithKeyParam);

            if(selectedChoicesObj instanceof List)
            {
                multiChoice.setSelectedMultiChoices((List)selectedChoicesObj);
            }

            getValueObj = multiChoice;
        }
        else
        {
            getValueObj = this.invoke(methodGetValue, objWithKeyParam);
        }


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
    private static Method getMethod(Class clazzParam, String nameParam)
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
    private static Object invoke(Method methodParam, Object objParam)
    {
        try {
            return methodParam.invoke(objParam);
        }
        //
        catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {

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
