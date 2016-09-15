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

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.json.JSONObject;

import com.fluid.program.api.util.ABaseUtil;
import com.fluid.program.api.util.cache.CacheUtil;
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
     * Retrieves the Table field records as {@code List<Form>}.
     *
     * @param electronicFormIdParam The Form Identifier.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
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
}
