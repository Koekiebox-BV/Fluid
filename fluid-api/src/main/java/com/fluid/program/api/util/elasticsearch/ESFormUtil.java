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
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

import com.fluid.program.api.util.ABaseUtil;
import com.fluid.program.api.util.IFormAction;
import com.fluid.program.api.util.cache.CacheUtil;
import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Form;

/**
 * ElasticSearch Utility class used for {@code Form} related actions.
 * A {@code Form} in Fluid is the equivalent to a Document in ElasticSearch.
 *
 * @author jasonbruwer on 2016/08/19.
 * @since 1.3
 * @see ABaseUtil
 * @see ABaseESUtil
 * @see IFormAction
 */
public class ESFormUtil extends ABaseESUtil implements IFormAction {

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

        super(connectionParam,
                esClientParam,
                cacheUtilParam);
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param esClientParam The ES Client.
     */
    public ESFormUtil(Client esClientParam) {
        super(esClientParam);
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

        //Populate the Table Fields...
        this.populateTableFields(
                false,
                includeFieldDataParam,
                returnVal.getFormFields());

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
        if(electronicFormIdParam == null)
        {
            return null;
        }

        //Query using the descendantId directly...
        StringBuffer descendantQuery = new StringBuffer(
                Form.JSONMapping.ANCESTOR_ID);
        descendantQuery.append(":\"");
        descendantQuery.append(electronicFormIdParam);
        descendantQuery.append("\"");

        //Search for the Descendants...
        List<Form> returnVal = null;
        if(includeFieldDataParam)
        {
            returnVal = this.searchAndConvertHitsToFormWithAllFields(
                    QueryBuilders.queryStringQuery(descendantQuery.toString()),
                    Index.DOCUMENT,
                    MAX_NUMBER_OF_TABLE_RECORDS,
                    null);
        }
        else
        {
            returnVal = this.searchAndConvertHitsToFormWithNoFields(
                    QueryBuilders.queryStringQuery(descendantQuery.toString()),
                    Index.DOCUMENT,
                    MAX_NUMBER_OF_TABLE_RECORDS,
                    null);
        }

        //Whether table field data should be included...
        if(!includeTableFieldsParam)
        {
            return returnVal;
        }

        //Populate in order to have table field data...
        for(Form descendantForm : returnVal)
        {
            this.populateTableFields(
                    false,
                    includeFieldDataParam,
                    descendantForm.getFormFields());
        }

        return returnVal;
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
        if(electronicFormIdParam == null)
        {
            return null;
        }

        //Query using the descendantId directly...
        StringBuffer primaryQuery = new StringBuffer(
                ABaseFluidJSONObject.JSONMapping.ID);
        primaryQuery.append(":\"");
        primaryQuery.append(electronicFormIdParam);
        primaryQuery.append("\"");

        //Search for the primary...
        List<Form> formsWithId = null;
        if(includeFieldDataParam)
        {
            formsWithId = this.searchAndConvertHitsToFormWithAllFields(
                    QueryBuilders.queryStringQuery(primaryQuery.toString()),
                    Index.DOCUMENT,
                    1,
                    null);
        }
        else
        {
            formsWithId = this.searchAndConvertHitsToFormWithNoFields(
                    QueryBuilders.queryStringQuery(primaryQuery.toString()),
                    Index.DOCUMENT,
                    1,
                    null);
        }

        Form returnVal = null;
        if(formsWithId != null && !formsWithId.isEmpty())
        {
            returnVal = formsWithId.get(0);
        }

        //No result...
        if(returnVal == null)
        {
            return null;
        }

        //Populate the Table Fields...
        return this.populateTableFields(
                true,
                includeFieldDataParam,
                returnVal.getFormFields());
    }
}
