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
import com.fluidbpm.program.api.util.IFormAction;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
		RestHighLevelClient esClientParam,
		CacheUtil cacheUtilParam
	) {
		super(connectionParam,
				esClientParam,
				cacheUtilParam);
	}

	/**
	 * Initialise with the ElasticSearch client.
	 *
	 * @param esClientParam The ES Client.
	 */
	public ESFormUtil(RestHighLevelClient esClientParam) {
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
			boolean includeTableFieldsParam
	) {
		if (electronicFormIdParam == null) {
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
		if (includeFieldDataParam) {
			ancestorForms = this.searchAndConvertHitsToFormWithAllFields(
					QueryBuilders.queryStringQuery(ancestorQuery.toString()),
					DEFAULT_OFFSET,
					1);
		} else {
			ancestorForms = this.searchAndConvertHitsToFormWithNoFields(
					QueryBuilders.queryStringQuery(ancestorQuery.toString()),
					DEFAULT_OFFSET,
					1);
		}

		Form returnVal = null;
		if (ancestorForms != null && !ancestorForms.isEmpty()) {
			returnVal = ancestorForms.get(0);
		}

		//No result...
		if (returnVal == null) {
			return null;
		}

		//Whether table field data should be included...
		if (!includeTableFieldsParam) {
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
	 * @param electronicFormId Identifier for the Form.
	 * @param includeFieldData Whether to populate the return {@code List<Form>} fields.
	 * @param includeTableFields Whether to populate the return {@code List<Form>} table fields.
	 * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
	 *
	 * @return {@code List<Form>} descendants.
	 *
	 * @see Form
	 */
	@Override
	public List<Form> getFormDescendants(
			Long electronicFormId,
			boolean includeFieldData,
			boolean includeTableFields,
			boolean includeTableFieldFormRecordInfo
	) {
		if (electronicFormId == null) return null;

		List<Long> electronicFormIds = new ArrayList();
		electronicFormIds.add(electronicFormId);

		//Get Form Descendants...
		return this.getFormDescendants(
			electronicFormIds,
			includeFieldData,
			includeTableFields,
			includeTableFieldFormRecordInfo
		);
	}

	/**
	 * Gets the descendants for the {@code electronicFormIdsParam} Forms.
	 *
	 * @param electronicFormIdsParam Identifiers for the Forms to retrieve.
	 * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
	 * @param includeTableFieldsParam Whether to populate the return {@code List<Form>} table fields.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 *
	 * @return {@code List<Form>} descendants.
	 *
	 * @see Form
	 */
	public List<Form> getFormDescendants(
			List<Long> electronicFormIdsParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean includeTableFieldFormRecordInfoParam)
	{
		if (electronicFormIdsParam == null ||
				electronicFormIdsParam.isEmpty())
		{
			return null;
		}

		//String stringQuery = "formType:(\"JIT Schedule\" \"SMT Training\") AND flowState:\"Not In Flow\"";

		//Query using the descendantId directly...
		StringBuffer descendantQuery = new StringBuffer(Form.JSONMapping.ANCESTOR_ID);
		descendantQuery.append(":(");

		for (Long electronicFormId : electronicFormIdsParam)
		{
			descendantQuery.append("\"");
			descendantQuery.append(electronicFormId);
			descendantQuery.append("\"");
			descendantQuery.append(" ");
		}

		String fullQueryToExec = descendantQuery.toString();
		fullQueryToExec = fullQueryToExec.substring(
				0, fullQueryToExec.length() - 1);
		fullQueryToExec = fullQueryToExec.concat(")");

		//Search for the Descendants...
		List<Form> returnVal = null;
		if (includeFieldDataParam) {
			returnVal = this.searchAndConvertHitsToFormWithAllFields(
					QueryBuilders.queryStringQuery(fullQueryToExec),
					DEFAULT_OFFSET, MAX_NUMBER_OF_TABLE_RECORDS);
		} else {
			returnVal = this.searchAndConvertHitsToFormWithNoFields(
					QueryBuilders.queryStringQuery(fullQueryToExec),
					DEFAULT_OFFSET, MAX_NUMBER_OF_TABLE_RECORDS);
		}

		//Whether table field data should be included...
		if (!includeTableFieldsParam) {
			return returnVal;
		}

		//No result...
		if (returnVal == null) {
			return returnVal;
		}

		//Populate in order to have table field data...
		for (Form descendantForm : returnVal) {
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
			boolean includeFieldDataParam
	) {
		if (electronicFormIdParam == null) {
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
		if (includeFieldDataParam) {
			formsWithId = this.searchAndConvertHitsToFormWithAllFields(
					QueryBuilders.queryStringQuery(primaryQuery.toString()),
					DEFAULT_OFFSET,
					1);
		} else {
			formsWithId = this.searchAndConvertHitsToFormWithNoFields(
					QueryBuilders.queryStringQuery(primaryQuery.toString()),
					DEFAULT_OFFSET,
					1);
		}

		Form returnVal = null;
		if (formsWithId != null && !formsWithId.isEmpty()) {
			returnVal = formsWithId.get(0);
		}

		//No result...
		if (returnVal == null) {
			return null;
		}

		//Populate the Table Fields...
		return this.populateTableFields(
				true,
				includeFieldDataParam,
				returnVal.getFormFields());
	}
}
