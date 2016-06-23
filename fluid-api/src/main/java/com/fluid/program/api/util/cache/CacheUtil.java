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

import com.fluid.program.api.util.cache.exception.FluidCacheException;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.MultiChoice;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

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
     * The FlowJob data type description mappings.
     * See Fluid configuration.
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
     * Enum for mapping the Fluid data types to Flow-Job.
     */
    private enum FlowJobTypeMapping
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
         * Maps the Fluid to the Flow-Job data types.
         *
         * @param flowJobDataTypeDescParam The Flow-Job text data type.
         * @param fluidTypeParam The Fluid enum type.
         */
        FlowJobTypeMapping(
                String flowJobDataTypeDescParam,
                Field.Type fluidTypeParam)
        {
            this.flowJobDataTypeDesc = flowJobDataTypeDescParam;
            this.fluidType = fluidTypeParam;
        }

        /**
         * Retrieves the Fluid data type from the Flow-Hob data
         * type description.
         *
         * @param flowJobTypeParam The Flow-Job type.
         * @return Fluid Field Type from Flow-Job type.
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
     * Fluid API cached field value.
     */
    public static class CachedFieldValue implements Serializable
    {
        private Object cachedFieldValue;
        private String dataType;

        /**
         * Converts the cached value to Fluid Field.
         *
         * @return Fluid Field.
         *
         * @see Field
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

        Object objWithKey;
        try {
            objWithKey = this.memcachedClient.get(storageKey);
        }
        //Changed for Java 1.6 compatibility...
        catch (MemcachedException e) {

            throw new FluidCacheException("Unable to get Field value for '"+storageKey+"'." +
                    "Contact administrator. "+e.getMessage(),e);
        } catch (TimeoutException e) {

            throw new FluidCacheException("Unable to get Field value for '"+storageKey+"'." +
                    "Contact administrator. "+e.getMessage(),e);
        } catch (InterruptedException e) {

            throw new FluidCacheException("Unable to get Field value for '"+storageKey+"'." +
                    "Contact administrator. "+e.getMessage(),e);
        }

        return this.getCacheFieldValueFromObject(objWithKey);
    }

    /**
     * Converts the {@code objWithKeyParam} Object to {@code CachedFieldValue}.
     *
     * @param objWithKeyParam The retrieved cached object.
     *
     * @return CachedFieldValue from {@code objWithKeyParam}.
     */
    @SuppressWarnings("unchecked")
    private CachedFieldValue getCacheFieldValueFromObject(Object objWithKeyParam)
    {
        if(objWithKeyParam == null)
        {
            return null;
        }

        //Get Word...
        Method methodGetWord = CacheUtil.getMethod(
                objWithKeyParam.getClass(),
                CustomCode.IWord.METHOD_getWord);

        //Get Value...
        Method methodGetValue = CacheUtil.getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.ADataType.METHOD_getValue);

        //Word...
        Object getWordObj = CacheUtil.invoke(methodGetWord, objWithKeyParam);
        String getWordVal = null;
        if(getWordObj instanceof String)
        {
            getWordVal = (String)getWordObj;
        }

        //Value...
        Object getValueObj;
        if(FlowJobType.MULTIPLE_CHOICE.equals(getWordVal))
        {
            MultiChoice multiChoice = new MultiChoice();

            //Available Choices...
            Method methodAvailableChoices = getMethod(
                    objWithKeyParam.getClass(),
                    CustomCode.MultipleChoice.METHOD_getAvailableChoices);

            Object availChoicesObj =
                    CacheUtil.invoke(methodAvailableChoices, objWithKeyParam);

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
            getValueObj = CacheUtil.invoke(methodGetValue, objWithKeyParam);
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
     * Retrieves the java method from class {@code clazzParam}.
     *
     * @param clazzParam The class.
     * @param nameParam The class name.
     *
     * @return Method from {@code clazzParam} and {@code nameParam}.
     */
    @SuppressWarnings("unchecked")
    private static Method getMethod(Class clazzParam, String nameParam)
    {
        try {
            if(clazzParam == null || nameParam == null)
            {
                return null;
            }

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
     * Invokes the {@code methodParam} method on {@code objParam}.
     *
     * @param methodParam The method to invoke.
     * @param objParam The object to invoke the method on.
     *
     * @return The result of the invoked object.
     */
    private static Object invoke(Method methodParam, Object objParam)
    {
        try {
            return methodParam.invoke(objParam);
        }
        //Changed for Java 1.6 compatibility...
        catch (InvocationTargetException e) {

            throw new FluidCacheException(
                    "Unable to invoke method '"+
                            methodParam.getName() +"'. "+e.getMessage(),e);
        } catch (IllegalAccessException e) {

            throw new FluidCacheException(
                    "Unable to invoke method '"+
                            methodParam.getName() +"'. "+e.getMessage(),e);
        } catch (IllegalArgumentException e) {

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
        StringBuilder stringBuff = new StringBuilder();

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
     * Creates an instance of MemcachedClient.
     *
     * @return MemcachedClient
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
