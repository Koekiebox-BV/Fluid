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
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import com.fluid.program.api.util.ABaseUtil;
import com.fluid.program.api.util.cache.CacheUtil;
import com.fluid.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.impl.SQLFormDefinitionUtil;
import com.fluid.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;

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
    public ESFormUtil(Connection connectionParam, Client esClientParam, CacheUtil cacheUtilParam) {
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

        //Only using Id....
        Form mainElectronicForm = this.getDocumentFormByPrimaryKey(
                null, electronicFormIdParam, null);

        if(mainElectronicForm == null)
        {
            return null;
        }

        Long ancestorId = mainElectronicForm.getAncestorId();
        if(ancestorId == null || ancestorId.longValue() < 1)
        {
            return null;
        }

        Form returnVal = this.getDocumentFormByPrimaryKey(
                null, ancestorId, null);

        if(this.fieldUtil == null)
        {
            throw new ElasticsearchException("FieldUtil is not set.");
        }

        //When field data must also be included...
        if(includeFieldDataParam)
        {
            Long formDefId = returnVal.getFormTypeId();
            List<SQLFormFieldUtil.FormFieldMapping> formFieldMappings =
                    this.fieldUtil.getFormFieldMappingForFormDefinition(formDefId);

            returnVal.populateFromElasticSearchJson(
                    returnVal.getJSONObject(),
                    returnVal.convertTo(formFieldMappings));

            //
            if(includeTableFieldsParam)
            {



            }
        }

        return returnVal;
    }

    /**
     *
     * @param formTypeIdParam
     * @param formIdParam
     * @param fieldsToRetrieveParam
     * @return
     */
    private Form getDocumentFormByPrimaryKey(
            Long formTypeIdParam,
            Long formIdParam,
            List<Field> fieldsToRetrieveParam)
    {
        GetResponse getResponse = null;

        if(formTypeIdParam == null)
        {
            //TODO need to query by id...
            //TODO sdsdsds
        }

        try
        {
            getResponse = this.client.prepareGet(
                    Index.DOCUMENT,
                    formTypeIdParam.toString(),
                    formIdParam.toString())
                    .get();
        }
        //When the index was not found...
        catch(IndexNotFoundException indexNotFound)
        {
            return null;
        }

        String sourceAsString;
        if(getResponse == null ||
                (sourceAsString = getResponse.getSourceAsString()) == null)
        {
            return null;
        }

        Form returnVal = new Form();
        returnVal.populateFromElasticSearchJson(
                new JSONObject(sourceAsString),
                fieldsToRetrieveParam);

        return returnVal;
    }


    /**
     *
     * @param byParentIdParam
     * @param fieldsToRetrieveParam
     * @return
     */
    private List<Form> getTableRecordFormsByParentId(
            Long byParentIdParam,
            List<Field> fieldsToRetrieveParam)
    {
        //TODO @Jason, complete here...

        return null;
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

    /**
     *
     * @param ancestorIdParam
     * @param fieldsToRetrieveParam
     * @return
     */
    private Form getAncestorByDescendantId(
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

            if(searchHits.getHits().length != totalHits)
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
                        fieldsParam);

                returnVal.add(formFromSource);
            }
        }

        return returnVal;
    }
}
