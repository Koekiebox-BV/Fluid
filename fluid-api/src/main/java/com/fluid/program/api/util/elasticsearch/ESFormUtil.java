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

package com.fluid.program.api.util.elasticsearch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import com.fluid.program.api.util.ABaseUtil;
import com.fluid.program.api.util.cache.CacheUtil;
import com.fluid.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.impl.SQLFormDefinitionUtil;
import com.fluid.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.TableField;

/**
 * ElasticSearch Utility class used for {@code Form} related actions.
 * A {@code Form} in Fluid is the equivalent to a Document in ElasticSearch.
 *
 * @author jasonbruwer on 2016/08/19.
 * @since 1.3
 * @see ABaseUtil
 */
public class ESFormUtil extends ABaseSQLUtil {

    //TODO This needs to be completed once...
    private Client client;

    private SQLFormDefinitionUtil formDefUtil = null;
    private SQLFormFieldUtil fieldUtil = null;

    private static final int MAX_NUMBER_OF_TABLE_RECORDS = 10000;

    /**
     * The index type.
     */
    public static final class Index {

        public static final String DOCUMENT = "document";
        public static final String FOLDER = "folder";

        public static final String TABLE_RECORD = "table_record";
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param connectionParam SQL Connection to use.
     * @param esClientParam The ES Client.
     * @param cacheUtilParam The Cache Util for better performance.
     */
    public ESFormUtil(
            Connection connectionParam,
            Client esClientParam,
            CacheUtil cacheUtilParam) {

        super(connectionParam);

        this.client = esClientParam;
        this.formDefUtil = new SQLFormDefinitionUtil(connectionParam);
        this.fieldUtil = new SQLFormFieldUtil(connectionParam, cacheUtilParam);
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param esClientParam The ES Client.
     */
    public ESFormUtil(Client esClientParam) {
        super(null);

        this.client = esClientParam;
    }

    /**
     * Retrieves the Table field records as {@code List<Form>}.
     *
     * @param electronicFormIdParam The Form Identifier.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
     *
     * @return {@code List<Form>} records.
     */
    public List<Form> getFormTableForms(
            Long electronicFormIdParam,
            boolean includeFieldDataParam)
    {
        List<Form> returnVal = new ArrayList();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }





        return returnVal;
    }

    /**
     * Gets the descendants for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormIdParam Identifier for the Form.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code List<Form>} table fields.
     *
     * @return {@code List<Form>} descendants.
     *
     * @see Form
     */
    public List<Form> getFormDescendants(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        List<Form> returnVal = new ArrayList();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        return returnVal;
    }

    /**
     * Gets the ancestor for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormIdParam Identifier for the Form.
     * @param includeFieldDataParam Whether to populate the return {@code Form} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code Form} table fields.
     *
     * @return {@code Form} descendants.
     *
     * @see Form
     */
    public Form getFormAncestor(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        if(electronicFormIdParam == null)
        {
            return null;
        }

        //Query using the descendantId directly...
        StringBuffer ancestorQuery = new StringBuffer(
                Form.JSONMapping.DESCENDANT_IDS);
        ancestorQuery.append(":\"");
        ancestorQuery.append(electronicFormIdParam);
        ancestorQuery.append("\"");

        //Search for the Ancestor...
        List<Form> ancestorForms = null;
        if(includeFieldDataParam)
        {
            ancestorForms = this.searchAndConvertHitsToFormWithAllFields(
                    QueryBuilders.queryStringQuery(ancestorQuery.toString()),
                    Index.DOCUMENT,
                    1,
                    null);
        }
        else
        {
            ancestorForms = this.searchAndConvertHitsToFormWithNoFields(
                    QueryBuilders.queryStringQuery(ancestorQuery.toString()),
                    Index.DOCUMENT,
                    1,
                    null);
        }

        Form returnVal = null;
        if(ancestorForms != null && !ancestorForms.isEmpty())
        {
            returnVal = ancestorForms.get(0);
        }

        //No result...
        if(returnVal == null)
        {
            return null;
        }

        //Whether table field data should be included...
        if(!includeTableFieldsParam)
        {
            return returnVal;
        }

        //No Form Fields present, just return...
        if(returnVal.getFormFields() == null ||
                returnVal.getFormFields().isEmpty())
        {
            return returnVal;
        }

        for(Field ancestorField : returnVal.getFormFields())
        {
            //Skip if not Table Field...
            if(!(ancestorField.getFieldValue() instanceof TableField))
            {
                continue;
            }

            TableField tableField = (TableField)ancestorField.getFieldValue();
            List<Form> tableRecordWithIdOnly = tableField.getTableRecords();
            if(tableRecordWithIdOnly == null || tableRecordWithIdOnly.isEmpty())
            {
                continue;
            }

            //Populate the ids for lookup...
            List<Long> formIdsOnly = new ArrayList();
            for(Form tableRecord : tableRecordWithIdOnly)
            {
                formIdsOnly.add(tableRecord.getId());
            }

            List<Form> populatedTableRecords =
                    this.getFormsByIds(
                            Index.TABLE_RECORD,
                            formIdsOnly,
                            includeFieldDataParam,
                            MAX_NUMBER_OF_TABLE_RECORDS);

            tableField.setTableRecords(populatedTableRecords);
            ancestorField.setFieldValue(tableField);
        }

        return returnVal;
    }

    /**
     * Gets the ancestor for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormIdParam Identifier for the Form.
     * @param includeFieldDataParam Whether to populate the return {@code Form} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code Form} table fields.
     *
     * @return {@code Form} descendants.
     *
     * @see Form
     */

    /**
     * Retrieves the {@code Form}'s via the provided {@code formIdsParam}.
     *
     * @param indexParam The ElasticSearch index to be used for lookup.
     * @param formIdsParam {@code List} of Identifiers for the Forms to return.
     * @param includeFieldDataParam Whether to populate the return {@code Form} table fields.
     * @param maxNumberOfResultsParam The maximum number of results to return.
     *
     * @return {@code List<Form>} List of Forms.
     */
    public List<Form> getFormsByIds(
            String indexParam,
            List<Long> formIdsParam,
            boolean includeFieldDataParam,
            int maxNumberOfResultsParam)
    {
        if(formIdsParam == null || formIdsParam.isEmpty())
        {
            return null;
        }

        if(indexParam == null || indexParam.trim().isEmpty())
        {
            throw new FluidElasticSearchException(
                    "Index is mandatory for lookup.");
        }

        //Query using the descendantId directly...
        StringBuffer byIdQuery = new StringBuffer();

        for(Long formId : formIdsParam)
        {
            byIdQuery.append(ABaseFluidJSONObject.JSONMapping.ID);
            byIdQuery.append(":\"");
            byIdQuery.append(formId);
            byIdQuery.append("\" ");
        }

        String queryByIdsToString = byIdQuery.toString();
        queryByIdsToString = queryByIdsToString.substring(0, queryByIdsToString.length() - 1);

        List<Form> returnVal = null;
        if(includeFieldDataParam)
        {
            returnVal = this.searchAndConvertHitsToFormWithAllFields(
                    QueryBuilders.queryStringQuery(queryByIdsToString),
                    indexParam,
                    maxNumberOfResultsParam,
                    null);
        }
        else
        {
            returnVal = this.searchAndConvertHitsToFormWithNoFields(
                    QueryBuilders.queryStringQuery(queryByIdsToString),
                    indexParam,
                    maxNumberOfResultsParam,
                    null);
        }

        if(returnVal == null || returnVal.isEmpty())
        {
            return null;
        }

        return returnVal;
    }


    /**
     *
     * @param ancestorIdParam
     * @param fieldsToRetrieveParam
     * @return
     */
    private List<Form> getDescendantsByAncestorId(
            Long ancestorIdParam,
            List<Field> fieldsToRetrieveParam)
    {

        //TODO @Jason, complete here...

        return null;
    }


    /*******************************************************/
    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     * @return The {@code ElasticTypeAndId} {@code List}.
     *
     * @see Index
     * @see ElasticTypeAndId
     */
    public List<ElasticTypeAndId> searchAndConvertHitsToIdsOnly(
            QueryBuilder qbParam,
            String indexParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        SearchHits searchHits = this.searchWithHits(
                qbParam,
                indexParam,
                true,
                numberOfResultsParam,
                formTypesParam);

        List<ElasticTypeAndId> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {

            returnVal = new ArrayList();

            if(searchHits.getHits().length != totalHits)
            {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '"
                                +totalHits+"' while hits is '"+
                                searchHits.getHits().length+"'.");
            }

            //Iterate...
            for(int index = 0;index < totalHits;index++)
            {
                SearchHit searchHit = searchHits.getAt(index);
                String idAsString;
                if((idAsString = searchHit.getId()) == null)
                {
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
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param withNoFieldsParam Should fields be IGNORED returned.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     *
     * @return The Elasticsearch {@code SearchHits}.
     */
    public SearchHits searchWithHits(
            QueryBuilder qbParam,
            String indexParam,
            boolean withNoFieldsParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        if(this.client == null)
        {
            throw new ElasticsearchException("Elasticsearch client is not set.");
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(
                //Indexes...
                indexParam)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qbParam)
                .setFrom(0)
                .setExplain(false);

        //No Fields...
        if (withNoFieldsParam) {
            searchRequestBuilder = searchRequestBuilder.setNoFields();
        }

        //The requested number of results...
        if(numberOfResultsParam > 0)
        {
            searchRequestBuilder = searchRequestBuilder.setSize(
                    numberOfResultsParam);
        }

        if(formTypesParam == null)
        {
            formTypesParam = new Long[]{};
        }

        //If Types is set...
        if(formTypesParam != null && formTypesParam.length > 0)
        {
            String[] formTypesAsString = new String[formTypesParam.length];
            for(int index = 0;index < formTypesParam.length;index++)
            {
                Long formTypeId = formTypesParam[index];
                if(formTypeId == null)
                {
                    continue;
                }

                formTypesAsString[index] = formTypeId.toString();
            }
            searchRequestBuilder = searchRequestBuilder.setTypes(formTypesAsString);
        }

        //Perform the actual search...
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        if (searchResponse == null) {
            return null;
        }

        return searchResponse.getHits();
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param withNoFieldsParam Should fields be IGNORED returned.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     *
     * @return {@code true} if there were any {@code SearchHits} from the lookup.
     */
    public boolean searchContainHits(
            QueryBuilder qbParam,
            String indexParam,
            boolean withNoFieldsParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        SearchHits searchHits = this.searchWithHits(
                qbParam,
                indexParam,
                withNoFieldsParam,
                numberOfResultsParam,
                formTypesParam);

        return (searchHits != null && searchHits.getTotalHits() > 0);
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param fieldsParam The Fluid {@code Field}'s to return after lookup.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     *
     * @return The {@code SearchHits} as Fluid {@code Form}.
     *
     * @see Form
     * @see SearchHits
     */
    public List<Form> searchAndConvertHitsToForm(
            QueryBuilder qbParam,
            String indexParam,
            List<Field> fieldsParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        SearchHits searchHits = this.searchWithHits(
                qbParam,
                indexParam,
                false,
                numberOfResultsParam,
                formTypesParam);

        List<Form> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {

            returnVal = new ArrayList();

            if((searchHits.getHits().length != totalHits) &&
                    (searchHits.getHits().length != numberOfResultsParam))
            {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '"+totalHits+"' while hits is '"+
                                searchHits.getHits().length+"'.");
            }

            long iterationMax = totalHits;
            if(numberOfResultsParam > 0 &&
                    totalHits > numberOfResultsParam)
            {
                iterationMax = numberOfResultsParam;
            }

            //Iterate...
            for(int index = 0;index < iterationMax;index++)
            {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if((source = searchHit.sourceAsString()) == null)
                {
                    continue;
                }

                Form formFromSource = new Form();

                formFromSource.populateFromElasticSearchJson(
                        new JSONObject(source),
                        fieldsParam);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     *
     * @return The {@code SearchHits} as Fluid {@code Form}.
     *
     * @see Form
     * @see SearchHits
     */
    public List<Form> searchAndConvertHitsToFormWithAllFields(
            QueryBuilder qbParam,
            String indexParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        SearchHits searchHits = this.searchWithHits(
                qbParam,
                indexParam,
                false,
                numberOfResultsParam,
                formTypesParam);

        List<Form> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {

            returnVal = new ArrayList();

            if((searchHits.getHits().length != totalHits) &&
                    (searchHits.getHits().length != numberOfResultsParam))
            {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '"+totalHits+"' while hits is '"+
                                searchHits.getHits().length+"'.");
            }

            //Iterate...
            for(int index = 0;index < totalHits;index++)
            {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if((source = searchHit.sourceAsString()) == null)
                {
                    continue;
                }

                Form formFromSource = new Form();

                JSONObject jsonObject = new JSONObject(source);

                List<Field> fieldsForForm = null;
                //Is Form Type available...
                if(jsonObject.has(Form.JSONMapping.FORM_TYPE_ID))
                {
                    fieldsForForm = formFromSource.convertTo(
                            this.fieldUtil.getFormFieldMappingForFormDefinition(
                                    jsonObject.getLong(Form.JSONMapping.FORM_TYPE_ID)));
                }

                formFromSource.populateFromElasticSearchJson(
                        jsonObject,
                        fieldsForForm);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     * Performs a search against the Elasticsearch instance with the {@code qbParam}.
     *
     * @param qbParam The @{code QueryBuilder} to search with.
     * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
     * @param numberOfResultsParam The number of results to return.
     * @param formTypesParam The Id's for the Form Definitions to be returned.
     *
     * @return The {@code SearchHits} as Fluid {@code Form}.
     *
     * @see Form
     * @see SearchHits
     */
    public List<Form> searchAndConvertHitsToFormWithNoFields(
            QueryBuilder qbParam,
            String indexParam,
            int numberOfResultsParam,
            Long ... formTypesParam) {

        SearchHits searchHits = this.searchWithHits(
                qbParam,
                indexParam,
                false,
                numberOfResultsParam,
                formTypesParam);

        List<Form> returnVal = null;

        long totalHits;
        if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {

            returnVal = new ArrayList();

            if((searchHits.getHits().length != totalHits) &&
                    (searchHits.getHits().length != numberOfResultsParam))
            {
                throw new FluidElasticSearchException(
                        "The Hits and fetch count has mismatch. Total hits is '"+totalHits+"' while hits is '"+
                                searchHits.getHits().length+"'.");
            }

            //Iterate...
            for(int index = 0;index < totalHits;index++)
            {
                SearchHit searchHit = searchHits.getAt(index);

                String source;
                if((source = searchHit.sourceAsString()) == null)
                {
                    continue;
                }

                Form formFromSource = new Form();

                formFromSource.populateFromElasticSearchJson(
                        new JSONObject(source),
                        null);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }

    /**
     *
     */
    @Override
    public void closeConnection() {
        super.closeConnection();

        if(this.client != null)
        {
            this.client.close();
        }

        this.client = null;
    }
}
