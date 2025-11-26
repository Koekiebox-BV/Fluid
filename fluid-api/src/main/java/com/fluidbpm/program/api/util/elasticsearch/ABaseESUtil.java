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

package com.fluidbpm.program.api.util.elasticsearch;

import com.fluidbpm.program.api.util.ABaseUtil;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ElasticSearch base Utility class.
 *
 * @author jasonbruwer on 2016/08/19.
 * @see ABaseUtil
 * @see ABaseSQLUtil
 * @since 1.3
 */
public abstract class ABaseESUtil extends ABaseSQLUtil {

    /**
     * The `RestHighLevelClient` instance used for interactions with an Elasticsearch server.
     * This client provides the means to execute Elasticsearch operations such as indexing, searching,
     * or managing indices within the scope of the {@code ABaseESUtil} class.
     *
     * The client is protected, ensuring it can only be accessed within this class or its subclasses.
     * It is initialized either through a constructor or at runtime, enabling core Elasticsearch
     * functionalities to be utilized as defined by the methods of this utility class.
     */
    protected RestHighLevelClient client;
    protected SQLFormFieldUtil fieldUtil = null;

    protected static final int DEFAULT_OFFSET = 0;
    protected static final int MAX_NUMBER_OF_TABLE_RECORDS = 10000;

    //public static final String[] NO_FIELDS_MAPPER = {"_none_"};
    public static final String[] NO_FIELDS_MAPPER = {"_id", "_type"};
    public static final List<String> NO_FIELDS_MAPPER_LIST = new ArrayList<>();

    static {
        for (String field : NO_FIELDS_MAPPER) {
            NO_FIELDS_MAPPER_LIST.add(field);
        }
    }

    /**
     * The index type.
     */
    public static final class Index {
        public static final String DOCUMENT = "document";
        public static final String FOLDER = "folder";
        public static final String TABLE_RECORD = "table_record";

        /**
         * Checks to see whether {@code indexParam} is a valid
         * index type.
         *
         * @param index The index to check.
         * @return {@code true} for valid index and {@code false} for invalid index.
         */
        public static boolean isIndexValid(String index) {
            switch (index.toLowerCase()) {
                case DOCUMENT:
                case FOLDER:
                case TABLE_RECORD:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Checks to see whether {@code indexParam} is a valid
         * index type.
         *
         * @param indexParam The index to check.
         * @throws FluidElasticSearchException if validation fails.
         */
        public static void validateIndexName(String indexParam) {
            if (indexParam == null || indexParam.trim().isEmpty()) {
                throw new FluidElasticSearchException("Index name is empty. Not allowed.");
            }

            int charIndex = 1;
            for (char character : indexParam.toCharArray()) {
                int charType = Character.getType(character);
                if (Character.LOWERCASE_LETTER != charType && '_' != character) {
                    throw new FluidElasticSearchException(
                            "Index name '" + indexParam + "' is invalid. " +
                                    "See character at index '" + charIndex + ":" + character + "' which is of type '" + charType + "'.");
                }
                charIndex++;
            }
        }
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param connectionParam SQL Connection to use.
     * @param esClientParam   The ES Client.
     * @param cacheUtilParam  The Cache Util for better performance.
     */
    public ABaseESUtil(
            Connection connectionParam,
            RestHighLevelClient esClientParam,
            CacheUtil cacheUtilParam
    ) {
        super(connectionParam, cacheUtilParam);
        this.client = esClientParam;
        this.fieldUtil = new SQLFormFieldUtil(connectionParam, cacheUtilParam);
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param esClientParam The ES Client.
     */
    public ABaseESUtil(RestHighLevelClient esClientParam) {
        super(null);
        this.client = esClientParam;
    }

    /*******************************************************/
    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam      The @{code QueryBuilder} to search with.
     * @param offsetParam  The offset for the results to return.
     * @param limitParam   The number of results to return.
     * @param indicesParam The indexes to search. Optional.
     * @return The {@code ElasticTypeAndId} {@code List}.
     * @see Index
     * @see ElasticTypeAndId
     */
    public final List<ElasticTypeAndId> searchAndConvertHitsToIdsOnly(
            QueryBuilder qbParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        SearchHits searchHits = this.searchWithHits(
                qbParam,
                true,
                offsetParam,
                limitParam,
                indicesParam);

        List<ElasticTypeAndId> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits().value) > 0) {
            returnVal = new ArrayList();
            if ((searchHits.getHits().length != totalHits) && (searchHits.getHits().length != limitParam)) {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '" + totalHits
                                + "' while hits is '" +
                                searchHits.getHits().length + "'.");
            }

            long iterationMax = totalHits;
            if (limitParam > 0 && totalHits > limitParam) {
                iterationMax = limitParam;
            }

            //Iterate...
            for (int index = 0; index < iterationMax; index++) {
                SearchHit searchHit = searchHits.getAt(index);
                String idAsString;
                if ((idAsString = searchHit.getId()) == null) {
                    continue;
                }

                returnVal.add(new ElasticTypeAndId(
                        this.toLongSafe(idAsString),
                        searchHit.getType()));
            }
        }

        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param queryBuilder      The @{code QueryBuilder} to search with.
     * @param withNoFieldsParam Should fields be IGNORED returned.
     * @param offsetParam       The offset for the results to return.
     * @param limitParam        The max number of results to return.
     * @param indicesParam      The indexes to search. Optional.
     * @return The Elasticsearch {@code SearchHits}.
     */
    public final SearchHits searchWithHits(
            QueryBuilder queryBuilder,
            boolean withNoFieldsParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        if (this.client == null) {
            throw new ElasticsearchException("Elasticsearch client is not set.");
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(new TimeValue(30L, TimeUnit.SECONDS));
        searchSourceBuilder.from(0);
        searchSourceBuilder.explain(Boolean.FALSE);

        //No Fields...
        if (withNoFieldsParam) {
            searchSourceBuilder.storedFields(NO_FIELDS_MAPPER_LIST);
        }

        //The requested number of results...
        if (limitParam > 0) {
            searchSourceBuilder.size(limitParam);
        }

        if (offsetParam > -1) {
            searchSourceBuilder.from(offsetParam);
        }

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);

        if (indicesParam != null && indicesParam.length > 0) {
            searchRequest.indices(indicesParam);
        }

        SearchResponse searchResponse = null;
        try {
            searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException ioErr) {
            throw new FluidElasticSearchException(
                    String.format("Unable to search. %s", ioErr.getMessage()), ioErr);
        }

        if (searchResponse == null) {
            return null;
        }

        return searchResponse.getHits();
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam           The @{code QueryBuilder} to search with.
     * @param withNoFieldsParam Should fields be IGNORED returned.
     * @param offsetParam       The offset for the results to return.
     * @param limitParam        The number of results to return.
     * @param indicesParam      The indexes to search. Optional.
     * @return {@code true} if there were any {@code SearchHits} from the lookup.
     */
    public final boolean searchContainHits(
            QueryBuilder qbParam,
            boolean withNoFieldsParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        SearchHits searchHits = this.searchWithHits(
                qbParam,
                withNoFieldsParam,
                offsetParam,
                limitParam,
                indicesParam);

        return (searchHits != null && searchHits.getTotalHits().value > 0);
    }

    /**
     * Retrieves the {@code Form}'s via the provided {@code formIdsParam}.
     *
     * @param formIdsParam          {@code List} of Identifiers for the Forms to return.
     * @param includeFieldDataParam Whether to populate the return {@code Form} table fields.
     * @param offsetParam           The offset for the results to return.
     * @param limitParam            The max number of results to return.
     * @param indicesParam          The indexes to search. Optional.
     * @return {@code List<Form>} List of Forms.
     */
    public final List<Form> getFormsByIds(
            List<Long> formIdsParam,
            boolean includeFieldDataParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        if (formIdsParam == null || formIdsParam.isEmpty()) return null;

        //Query using the descendantId directly...
        StringBuffer byIdQuery = new StringBuffer();

        for (Long formId : formIdsParam) {
            byIdQuery.append(ABaseFluidGSONObject.JSONMapping.ID);
            byIdQuery.append(":\"");
            byIdQuery.append(formId);
            byIdQuery.append("\" ");
        }

        String queryByIdsToString = byIdQuery.toString();
        queryByIdsToString = queryByIdsToString.substring(0, queryByIdsToString.length() - 1);

        List<Form> returnVal = null;
        if (includeFieldDataParam) {
            returnVal = this.searchAndConvertHitsToFormWithAllFields(
                    QueryBuilders.queryStringQuery(queryByIdsToString),
                    offsetParam,
                    limitParam,
                    indicesParam);
        } else {
            returnVal = this.searchAndConvertHitsToFormWithNoFields(
                    QueryBuilders.queryStringQuery(queryByIdsToString),
                    offsetParam,
                    limitParam,
                    indicesParam);
        }

        if (returnVal == null || returnVal.isEmpty()) return null;
        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam      The @{code QueryBuilder} to search with.
     * @param offsetParam  The offset for the results to return.
     * @param limitParam   The max number of results to return.
     * @param indicesParam The indexes to search. Optional.
     * @return The {@code SearchHits} as Fluid {@code Form}.
     * @see Form
     * @see SearchHits
     */
    public final List<Form> searchAndConvertHitsToFormWithAllFields(
            QueryBuilder qbParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        SearchHits searchHits = this.searchWithHits(
                qbParam,
                false,
                offsetParam,
                limitParam,
                indicesParam);

        List<Form> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits().value) > 0) {
            returnVal = new ArrayList();

            if ((searchHits.getHits().length != totalHits) && (searchHits.getHits().length != limitParam))
                throw new FluidElasticSearchException("The Hits and fetch count has mismatch. Total hits is '" + totalHits + "' while hits is '" +
                        searchHits.getHits().length + "'.");

            long iterationMax = totalHits;
            if (limitParam > 0 && totalHits > limitParam) iterationMax = limitParam;

            //Iterate...
            for (int index = 0; index < iterationMax; index++) {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if ((source = searchHit.getSourceAsString()) == null) continue;

                this.printInfoOnSourceFromES(searchHit);

                Form formFromSource = new Form();
                JsonObject jsonObject = JsonParser.parseString(source).getAsJsonObject();
                List<Field> fieldsForForm = null;
                //Is Form Type available...
                if (jsonObject.has(Form.JSONMapping.FORM_TYPE_ID)) {
                    if (this.fieldUtil == null) throw new FluidElasticSearchException(
                            "Field Util is not set. Use a different constructor.");

                    fieldsForForm = formFromSource.convertTo(
                            this.fieldUtil.getFormFieldMappingForFormDefinition(
                                    jsonObject.get(Form.JSONMapping.FORM_TYPE_ID).getAsLong())
                    );
                }

                formFromSource.populateFromElasticSearchJson(jsonObject, fieldsForForm);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam      The @{code QueryBuilder} to search with.
     * @param offsetParam  The offset for the results to return.
     * @param limitParam   The max number of results to return.
     * @param indicesParam The indexes to search. Optional.
     * @return The {@code SearchHits} as Fluid {@code Form}.
     * @see Form
     * @see SearchHits
     */
    public final List<Form> searchAndConvertHitsToFormWithNoFields(
            QueryBuilder qbParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        SearchHits searchHits = this.searchWithHits(
                qbParam,
                false,
                offsetParam,
                limitParam,
                indicesParam);

        List<Form> returnVal = null;
        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits().value) > 0) {
            returnVal = new ArrayList();

            if ((searchHits.getHits().length != totalHits) &&
                    (searchHits.getHits().length != limitParam)) {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '" +
                                totalHits + "' while hits is '" +
                                searchHits.getHits().length + "'.");
            }

            long iterationMax = totalHits;
            if (limitParam > 0 && totalHits > limitParam) iterationMax = limitParam;

            //Iterate...
            for (int index = 0; index < iterationMax; index++) {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if ((source = searchHit.getSourceAsString()) == null) continue;

                this.printInfoOnSourceFromES(searchHit);

                Form formFromSource = new Form();
                formFromSource.populateFromElasticSearchJson(JsonParser.parseString(source).getAsJsonObject(), null);
                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam      The @{code QueryBuilder} to search with.
     * @param fieldsParam  The Fluid {@code Field}'s to return after lookup.
     * @param offsetParam  The offset for the results to return.
     * @param limitParam   The max number of results to return.
     * @param indicesParam The indexes to search. Optional.
     * @return The {@code SearchHits} as Fluid {@code Form}.
     * @see Form
     * @see SearchHits
     */
    public List<Form> searchAndConvertHitsToForm(
            QueryBuilder qbParam,
            List<Field> fieldsParam,
            int offsetParam,
            int limitParam,
            String... indicesParam
    ) {
        SearchHits searchHits = this.searchWithHits(
                qbParam,
                false,
                offsetParam,
                limitParam,
                indicesParam);

        List<Form> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits().value) > 0) {
            returnVal = new ArrayList();

            if ((searchHits.getHits().length != totalHits) &&
                    (searchHits.getHits().length != limitParam)) {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '" + totalHits
                                + "' while hits is '" +
                                searchHits.getHits().length + "'.");
            }

            long iterationMax = totalHits;
            if (limitParam > 0 && totalHits > limitParam) {
                iterationMax = limitParam;
            }

            //Iterate...
            for (int index = 0; index < iterationMax; index++) {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if ((source = searchHit.getSourceAsString()) == null) {
                    continue;
                }

                this.printInfoOnSourceFromES(searchHit);
                Form formFromSource = new Form();
                formFromSource.populateFromElasticSearchJson(JsonParser.parseString(source).getAsJsonObject(), fieldsParam);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     * Populate all the Table Field values from the Table index.
     *
     * @param addAllTableRecordsForReturnParam Whether to include all the Table Records as a return value.
     * @param includeFieldDataParam            Whether to populate the return {@code Form} table fields.
     * @param formFieldsParam                  The {@code Field}s to populate.
     * @return {@code List<Form>} of populated Table Field records.
     */
    protected final List<Form> populateTableFields(
            boolean addAllTableRecordsForReturnParam,
            boolean includeFieldDataParam,
            List<Field> formFieldsParam
    ) {
        if (formFieldsParam == null || formFieldsParam.isEmpty()) {
            return null;
        }

        List<Form> allTableRecordsFromAllFields = addAllTableRecordsForReturnParam ?
                new ArrayList() : null;

        //Populate each of the Table Fields...
        for (Field descendantField : formFieldsParam) {
            //Skip if not Table Field...
            if (!(descendantField.getFieldValue() instanceof TableField)) {
                continue;
            }

            TableField tableField = (TableField) descendantField.getFieldValue();

            List<Form> tableRecordWithIdOnly = tableField.getTableRecords();
            if (tableRecordWithIdOnly == null || tableRecordWithIdOnly.isEmpty()) {
                continue;
            }

            //Populate the ids for lookup...
            List<Long> formIdsOnly = new ArrayList();
            for (Form tableRecord : tableRecordWithIdOnly) {
                formIdsOnly.add(tableRecord.getId());
            }

            List<Form> populatedTableRecords = this.getFormsByIds(
                    formIdsOnly,
                    includeFieldDataParam,
                    DEFAULT_OFFSET,
                    MAX_NUMBER_OF_TABLE_RECORDS);

            if (addAllTableRecordsForReturnParam && populatedTableRecords != null) {
                allTableRecordsFromAllFields.addAll(populatedTableRecords);
            }

            tableField.setTableRecords(populatedTableRecords);
            descendantField.setFieldValue(tableField);
        }

        return allTableRecordsFromAllFields;
    }

    /**
     * @see Closeable#close()
     */
    @Override
    public void close() {
        this.closeConnection();
    }

    /**
     * Close the SQL and ElasticSearch Connection.
     */
    @Override
    public void closeConnection() {
        CloseConnectionRunnable closeConnectionRunnable =
                new CloseConnectionRunnable(this);

        Thread closeConnThread = new Thread(
                closeConnectionRunnable, "Close ABaseES Connection");
        closeConnThread.start();
    }

    /**
     * Close the SQL and ElasticSearch Connection, but not in
     * a separate {@code Thread}.
     */
    public void closeConnectionNonThreaded() {
        super.closeConnection();

        if (this.client != null) {
            try {
                this.client.close();
            } catch (IOException err) {
                throw new FluidElasticSearchException(err.getMessage(), err);
            }
        }

        this.client = null;
    }

    /**
     * Utility class to close the connection in a thread.
     */
    private static class CloseConnectionRunnable implements Runnable {
        private ABaseESUtil baseESUtil;

        /**
         * The resource to close.
         *
         * @param baseESUtilParam Base utility to close.
         */
        public CloseConnectionRunnable(ABaseESUtil baseESUtilParam) {
            this.baseESUtil = baseESUtilParam;
        }

        /**
         * Performs the threaded operation.
         */
        @Override
        public void run() {
            if (this.baseESUtil == null) {
                return;
            }

            this.baseESUtil.closeConnectionNonThreaded();
        }
    }

    /**
     * Do nothing.
     *
     * @param searchHitParam Search Hit to perform functions on.
     */
    private void printInfoOnSourceFromES(SearchHit searchHitParam) {
    }
}
