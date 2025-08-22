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

package com.fluidbpm.program.api.util.cache;


import com.fluidbpm.program.api.util.ABaseUtil;
import com.fluidbpm.program.api.util.cache.exception.FluidCacheException;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.form.Form;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.fluidbpm.program.api.util.UtilGlobal.FieldTypeId.*;

/**
 * Cache Utility class used for {@code Field} value retrieval actions.
 *
 * @author jasonbruwer on 2016/02/29.
 * @see ABaseUtil
 * @since 1.0
 */
public class CacheUtil extends ABaseUtil {
    public static final String CLUSTER_DISABLED = "ERR This instance has cluster support disabled";

    private static final String NULL = "null";
    public static final String FORWARD_SLASH = "/";

    private transient MemcachedClient memcachedClient;
    private transient JedisCluster jedisCluster;
    private transient Jedis jedis;

    private String cacheHost = null;
    private CacheType type = CacheType.MEMCACHED;
    private int cachePort = -1;

    public static final String MEMCACHE_PREFIX_VAL = "FORM_FIELD_VAL_";

    /**
     * Aliases for properties used by the {@code CacheUtil}.
     */
    private static class PropName {
        public static final String MEMORY_CACHE_TYPE = "MemoryCacheType";
        public static final String MEMORY_CACHE_HOSTNAME = "MemoryCacheHostname";
        public static final String MEMORY_CACHE_PORT_NUMBER = "MemoryCachePortNumber";
    }

    /**
     * Extract the cache type from the property.
     *
     * @param propValue The value of the property.
     * @return CacheType
     * @see CacheType
     */
    private static CacheType getCacheTypeFromProp(String propValue) {
        return "redis".equals(propValue == null ? null : propValue.toLowerCase().trim()) ?
                CacheType.REDIS : CacheType.MEMCACHED;
    }

    /**
     * The JSON structure of the cached field value.
     */
    private static final class JSONStruct {
        public static final String DATA_TYPE_ID = "dataTypeId";
        public static final String WORD = "word";

        //Field Value Specific Tags...
        public static final String DATE_VALUE = "dateValue";
        public static final String DECIMAL_VALUE = "decimalValue";
        public static final String LABEL = "label";

        //Multi Choice...
        public static final String VALUE = "value";
        public static final String VALUE_IN_STRING_FORM = "valueInStringForm";
        public static final String FOREIGN_ID = "foreignId";
        public static final String FOREIGN_IDS = "foreignIds";
        public static final String AVAILABLE_CHOICES = "availableChoices";
        public static final String SELECTED_CHOICES = "selectedChoices";

        public static final String TEXT_VALUE = "textValue";

        public static final String VALUE_WITHOUT_QUOTES = "valueWithoutQuotes";

        //Table Field...
        public static final String FORM_CONTAINERS = "formContainers";

        //Yes/No...
        public static final String BOOLEAN_VALUE = "boolValue";
    }

    /**
     * The FlowJob data type description mappings.
     * See Fluid configuration.
     */
    private static class FlowJobType {
        public static final String DATE_TIME = "Date Time";
        public static final String DECIMAL = "Decimal";
        public static final String MULTIPLE_CHOICE = "Multiple Choice";
        public static final String PARAGRAPH_TEXT = "Paragraph Text";
        public static final String TABLE_FIELD = "Table Field";
        public static final String TEXT = "Text";
        public static final String TEXT_ENCRYPTED = "Text Encrypted";
        public static final String TRUE_FALSE = "True / False";
        public static final String LABEL = "Label";
    }

    /**
     * Enum for the type of cache.
     */
    public enum CacheType {
        REDIS,
        MEMCACHED,
        INTERNAL,
        NONE
    }

    /**
     * Enum for mapping the Fluid data types to Flow-Job.
     */
    private enum FlowJobTypeMapping {
        DateTime(FlowJobType.DATE_TIME, Field.Type.DateTime),
        Decimal(FlowJobType.DECIMAL, Field.Type.Decimal),
        MultiChoice(FlowJobType.MULTIPLE_CHOICE, Field.Type.MultipleChoice),
        ParagraphText(FlowJobType.PARAGRAPH_TEXT, Field.Type.ParagraphText),
        TableField(FlowJobType.TABLE_FIELD, Field.Type.Table),
        Text(FlowJobType.TEXT, Field.Type.Text),
        TextEncrypted(FlowJobType.TEXT_ENCRYPTED, Field.Type.TextEncrypted),
        TrueFalse(FlowJobType.TRUE_FALSE, Field.Type.TrueFalse),
        Label(FlowJobType.LABEL, Field.Type.Label),
        ;

        private String flowJobDataTypeDesc;
        private Field.Type fluidType;

        /**
         * Maps the Fluid to the Flow-Job data types.
         *
         * @param flowJobDataTypeDescParam The Flow-Job text data type.
         * @param fluidTypeParam           The Fluid enum type.
         */
        FlowJobTypeMapping(
                String flowJobDataTypeDescParam,
                Field.Type fluidTypeParam
        ) {
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
        public static Field.Type getFluidTypeFromFlowJobType(String flowJobTypeParam) {
            if (flowJobTypeParam == null || flowJobTypeParam.trim().isEmpty()) {
                return null;
            }

            for (FlowJobTypeMapping mapping : values()) {
                if (flowJobTypeParam.equals(mapping.flowJobDataTypeDesc)) {
                    return mapping.fluidType;
                }
            }

            return null;
        }
    }

    /**
     * Fluid API cached field value.
     */
    public static class CachedFieldValue implements Serializable {
        private Object cachedFieldValue;
        private String dataType;

        /**
         * Converts the cached value to Fluid Field.
         *
         * @return Fluid Field.
         * @see Field
         */
        public Field getCachedFieldValueAsField() {
            Field returnVal = new Field();
            //No table field...
            if (FlowJobType.TABLE_FIELD.equals(this.dataType)) {
                return null;
            } else {
                returnVal.setFieldValue(this.cachedFieldValue);
            }

            //Set the Type as Enum...
            returnVal.setTypeAsEnum(FlowJobTypeMapping.getFluidTypeFromFlowJobType(this.dataType));

            return returnVal;
        }
    }

    /**
     * New instance of cache util using the
     * provided {@code propertiesParam}.
     *
     * @param propertiesParam The Properties to extract the cache configs from.
     */
    public CacheUtil(Properties propertiesParam) {
        this(
                getCacheTypeFromProp(getStringPropertyFromProperties(
                        propertiesParam, PropName.MEMORY_CACHE_TYPE)),
                getStringPropertyFromProperties(
                        propertiesParam, PropName.MEMORY_CACHE_HOSTNAME),
                getIntPropertyFromProperties(
                        propertiesParam, PropName.MEMORY_CACHE_PORT_NUMBER)
        );
    }

    /**
     * New instance of cache util using the
     * provided Host {@code cacheHostParam} and
     * Port {@code cachePortParam}.
     *
     * @param cacheHostParam The MemCache Host IP or hostname.
     * @param cachePortParam The MemCache Port.
     * @see CacheType
     */
    public CacheUtil(
            String cacheHostParam,
            int cachePortParam
    ) {
        this(CacheType.MEMCACHED, cacheHostParam, cachePortParam);
    }

    /**
     * New instance of cache util using the
     * provided Host {@code cacheHostParam} and
     * Port {@code cachePortParam}.
     *
     * @param typeParam      The type of caching server.
     * @param cacheHostParam The MemCache/Redis Host IP or hostname.
     * @param cachePortParam The MemCache/Redis Port.
     * @see CacheType
     */
    public CacheUtil(
            CacheType typeParam,
            String cacheHostParam,
            int cachePortParam
    ) {
        this.type = typeParam;
        this.cacheHost = cacheHostParam;
        this.cachePort = cachePortParam;

        if (this.cacheHost == null || this.cacheHost.trim().isEmpty()) {
            throw new FluidCacheException("Cache Host cannot be empty.");
        }

        if (this.cachePort < 1 || this.cachePort > 65535) {
            throw new FluidCacheException(String.format(
                    "Cache Port number '%d' is invalid.", this.cachePort));
        }

        switch (this.type) {
            case MEMCACHED:
                this.initXMemcachedClient();
                break;
            case REDIS:
                this.initRedisClient();
                break;
        }
    }

    /**
     * Retrieves the {@code CachedFieldValue} value stored under
     * the params.
     *
     * @param formDefIdParam   The Form Definition Id.
     * @param formContIdParam  The Form Container Id.
     * @param formFieldIdParam The Form Field Id.
     * @return Storage Key
     */
    public CachedFieldValue getCachedFieldValueFrom(
            Long formDefIdParam,
            Long formContIdParam,
            Long formFieldIdParam
    ) {
        if ((formDefIdParam == null || formContIdParam == null) ||
                formFieldIdParam == null) {
            return null;
        }

        String storageKey = this.getStorageKeyFrom(formDefIdParam, formContIdParam, formFieldIdParam);
        String objWithKey = null;
        switch (this.type) {
            case MEMCACHED:
                try {
                    objWithKey = this.memcachedClient.get(storageKey);
                } catch (MemcachedException e) {
                    //Changed for Java 1.6 compatibility...
                    throw new FluidCacheException("Unable to get Field value for '" + storageKey + "'." +
                            "Contact administrator. " + e.getMessage(), e);
                } catch (TimeoutException e) {
                    throw new FluidCacheException("Unable to get Field value for '" + storageKey + "'." +
                            "Contact administrator. " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    throw new FluidCacheException("Unable to get Field value for '" + storageKey + "'." +
                            "Contact administrator. " + e.getMessage(), e);
                }
                break;
            case REDIS:
                if (this.jedisCluster != null) objWithKey = this.jedisCluster.get(storageKey);
                if (this.jedis != null) objWithKey = this.jedis.get(storageKey);
                break;
        }

        return this.getCacheFieldValueFromObject(objWithKey);
    }

    /**
     * Retrieves the MemCached/Redis server descriptions from the cache client.
     * Performs a connection test.
     *
     * @return Servers descriptions from cache client.
     * @see MemcachedClient#getServersDescription()
     * @see JedisCluster#getClusterNodes()
     */
    public List<String> getCacheServersDescription() {
        switch (this.type) {
            case REDIS:
                if (this.jedisCluster == null && this.jedis == null) {
                    throw new FluidCacheException("Redis client/cluster is not set.");
                }
                String uuid = UUID.randomUUID().toString();
                String response = null;
                List<String> returnVal = new ArrayList<>();
                if (this.jedisCluster != null) {
                    response = "<-EchoOnClusterNotAvailable->";
                    returnVal.addAll(new ArrayList<>(this.jedisCluster.getClusterNodes().keySet()));
                }
                if (this.jedis != null) {
                    response = this.jedis.echo(uuid);
                    returnVal.add(this.jedis.clientGetname());
                    returnVal.add(this.jedis.ping());
                }

                returnVal.add(String.format("Echo-Request-[%s]", uuid));
                returnVal.add(String.format("Echo-Response-[%s]", response));
                return returnVal;
            case MEMCACHED:
                if (this.memcachedClient == null) {
                    throw new FluidCacheException(
                            "MemCached client is not set.");
                }
                return this.memcachedClient.getServersDescription();
        }
        return null;
    }

    /**
     * Converts the {@code objWithKeyParam} Object to {@code CachedFieldValue}.
     *
     * @param objWithKeyParam The retrieved cached object.
     * @return CachedFieldValue from {@code objWithKeyParam}.
     */
    private CachedFieldValue getCacheFieldValueFromObject(String objWithKeyParam) {
        if (objWithKeyParam == null) {
            return null;
        }

        JSONObject jsonObjFromSrc = new JSONObject(objWithKeyParam);
        int dataTypeId = jsonObjFromSrc.optInt(JSONStruct.DATA_TYPE_ID);

        CachedFieldValue returnVal = new CachedFieldValue();
        returnVal.dataType = jsonObjFromSrc.optString(JSONStruct.WORD);

        switch (dataTypeId) {
            case _1_TEXT:
            case _3_PARAGRAPH_TEXT:
            case _8_TEXT_ENCRYPTED:
                returnVal.cachedFieldValue = jsonObjFromSrc.optString(JSONStruct.TEXT_VALUE);
                break;
            case _2_TRUE_FALSE:
                returnVal.cachedFieldValue = jsonObjFromSrc.optBoolean(JSONStruct.BOOLEAN_VALUE);
                break;
            case _4_MULTI_CHOICE:
                MultiChoice multiChoice = new MultiChoice();
                List<String> availChoices = new ArrayList<>(), selectedChoices = new ArrayList<>();
                JSONArray jsonAvailChoices = jsonObjFromSrc.optJSONArray(JSONStruct.AVAILABLE_CHOICES);
                if (jsonAvailChoices != null) {
                    for (int index = 0; index < jsonAvailChoices.length(); index++) {
                        availChoices.add(jsonAvailChoices.getString(index));
                    }
                }
                JSONArray jsonSelectedChoices = jsonObjFromSrc.optJSONArray(JSONStruct.SELECTED_CHOICES);
                if (jsonSelectedChoices != null) {
                    for (int index = 0; index < jsonSelectedChoices.length(); index++) {
                        selectedChoices.add(jsonSelectedChoices.getString(index));
                    }
                }

                multiChoice.setAvailableMultiChoices(availChoices);
                multiChoice.setSelectedMultiChoices(selectedChoices);
                returnVal.cachedFieldValue = multiChoice;
                break;
            case _5_DATE_TIME:
                returnVal.cachedFieldValue =
                        new Date(jsonObjFromSrc.optLong(JSONStruct.DATE_VALUE));
                break;
            case _6_DECIMAL:
                returnVal.cachedFieldValue = jsonObjFromSrc.optDouble(JSONStruct.DECIMAL_VALUE);
                break;
            case _7_TABLE_FIELD:
                List<Form> listOfForms = new ArrayList<>();
                JSONArray jsonFormIds = jsonObjFromSrc.optJSONArray(JSONStruct.FORM_CONTAINERS);
                if (jsonFormIds != null) {
                    for (int index = 0; index < jsonFormIds.length(); index++) {
                        listOfForms.add(new Form(jsonFormIds.getLong(index)));
                    }
                }
                returnVal.cachedFieldValue = listOfForms;
                break;
            case _9_LABEL:
                returnVal.cachedFieldValue = jsonObjFromSrc.optString(JSONStruct.LABEL);
                break;
        }

        if (returnVal.cachedFieldValue == null) {
            return null;
        }

        return returnVal;
    }

    /**
     * Generates the storage key the provided parameters.
     *
     * @param formDefIdParam   The Form Definition Id.
     * @param formContIdParam  The Form Container Id.
     * @param formFieldIdParam The Form Field Id.
     * @return Storage Key
     */
    private String getStorageKeyFrom(
            Long formDefIdParam,
            Long formContIdParam,
            Long formFieldIdParam
    ) {
        StringBuilder stringBuff = new StringBuilder(MEMCACHE_PREFIX_VAL);
        stringBuff.append(FORWARD_SLASH);

        //Form Definition...
        if (formDefIdParam == null) {
            stringBuff.append(NULL);
        } else {
            stringBuff.append(formDefIdParam.toString());
        }

        stringBuff.append(FORWARD_SLASH);

        //Form Container...
        if (formContIdParam == null) {
            stringBuff.append(NULL);
        } else {
            stringBuff.append(formContIdParam.toString());
        }

        stringBuff.append(FORWARD_SLASH);

        //Form Field...
        if (formFieldIdParam == null) {
            stringBuff.append(NULL);
        } else {
            stringBuff.append(formFieldIdParam.toString());
        }

        return stringBuff.toString();
    }

    /**
     * Creates an instance of JedisCluster.
     *
     * @return MemcachedClient
     */
    private Object initRedisClient() {
        if (this.jedisCluster != null) {
            return this.jedisCluster;
        }

        if (this.jedis != null) {
            return this.jedis;
        }

        try {
            this.jedisCluster = new JedisCluster(new HostAndPort(this.cacheHost, this.cachePort));
            return this.jedisCluster;
        } catch (JedisDataException jde) {
            if (jde.getMessage().startsWith(CLUSTER_DISABLED)) {
                this.jedis = new Jedis(new HostAndPort(this.cacheHost, this.cachePort));
                return this.jedis;
            }
        }
        return null;
    }

    /**
     * Creates an instance of MemcachedClient.
     *
     * @return MemcachedClient
     */
    private MemcachedClient initXMemcachedClient() {
        if (this.memcachedClient != null && !this.memcachedClient.isShutdown()) {
            return this.memcachedClient;
        }

        try {
            this.memcachedClient = new XMemcachedClient(this.cacheHost, this.cachePort);
            return this.memcachedClient;
        } catch (IOException e) {
            //Unable to create client with connection.
            throw new FluidCacheException(
                    "Unable to create MemCache client. " + e.getMessage(), e);
        }
    }

    /**
     * Closes the Memcached/Redis client connection.
     */
    public void shutdown() {
        switch (this.type) {
            case REDIS:
                if (this.jedisCluster != null) {
                    this.jedisCluster.close();
                }
                if (this.jedis != null) {
                    this.jedis.close();
                }
                break;
            case MEMCACHED:
                if (this.memcachedClient != null &&
                        !this.memcachedClient.isShutdown()) {
                    try {
                        this.memcachedClient.shutdown();
                    } catch (IOException eParam) {
                        throw new FluidCacheException(
                                "Unable to create shutdown MemCache client. " + eParam.getMessage(), eParam);
                    }
                }
                break;
        }
    }
}
