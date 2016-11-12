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
import com.fluid.program.api.util.cache.CacheUtil;
import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Form;

/**
 * ElasticSearch Utility class used for {@code Field} related actions. A
 * {@code Field} in Fluid is the equivalent to a Document in ElasticSearch.
 *
 * @author jasonbruwer on 2016/08/19.
 * @since 1.3
 * @see ABaseUtil
 * @see ABaseESUtil
 */
public class ESFormFieldUtil extends ABaseESUtil{

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param connectionParam SQL Connection to use.
     * @param esClientParam The ES Client.
     * @param cacheUtilParam The Cache Util for better performance.
     */
    public ESFormFieldUtil(Connection connectionParam, Client esClientParam, CacheUtil cacheUtilParam) {

        super(connectionParam, esClientParam, cacheUtilParam);
    }

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param esClientParam The ES Client.
     */
    public ESFormFieldUtil(Client esClientParam) {
        super(esClientParam);
    }

    /**
     * Retrieves the Form Fields {@code VALUES} for the Electronic Form with id
     * {@code electronicFormIdParam}.
     *
     * @param electronicFormIdParam The Electronic Form to fetch fields for.
     * @param includeTableFieldsParam Whether to populate the table fields.
     * @return The Form Fields for Electronic Form
     *         {@code electronicFormIdParam}.
     */
    public Form getFormFields(Long electronicFormIdParam, boolean includeTableFieldsParam) {

        if (electronicFormIdParam == null) {
            return null;
        }

        //Query using the descendantId directly...
        StringBuffer primaryQuery = new StringBuffer(ABaseFluidJSONObject.JSONMapping.ID);
        primaryQuery.append(":\"");
        primaryQuery.append(electronicFormIdParam);
        primaryQuery.append("\"");

        //Search for the primary...
        List<Form> formsWithId = this.searchAndConvertHitsToFormWithAllFields(
                QueryBuilders.queryStringQuery(primaryQuery.toString()), Index.DOCUMENT, 1, null);

        Form returnVal = null;
        if (formsWithId != null && !formsWithId.isEmpty()) {
            returnVal = formsWithId.get(0);
        }

        //No result...
        if (returnVal == null) {
            return null;
        }

        //Skip Table fields...
        if (!includeTableFieldsParam) {
            return returnVal;
        }

        //Populate the Table Fields...
        this.populateTableFields(false, true, returnVal.getFormFields());

        return returnVal;
    }
}
