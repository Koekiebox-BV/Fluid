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

import com.fluidbpm.program.api.util.ABaseUtil;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;

/**
 * ElasticSearch base Utility class.
 *
 * @author jasonbruwer on 2016/08/19.
 * @since 1.3
 * @see ABaseUtil
 * @see ABaseSQLUtil
 */
public abstract class ABaseESUtil extends ABaseSQLUtil {

	protected Client client;
	protected SQLFormFieldUtil fieldUtil = null;

	protected static final int DEFAULT_OFFSET = 0;
	protected static final int MAX_NUMBER_OF_TABLE_RECORDS = 10000;

	//public static final String[] NO_FIELDS_MAPPER = {"_none_"};
	public static final String[] NO_FIELDS_MAPPER = {"_id","_type"};

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
		 * @param indexParam The index to check.
		 * @return {@code true} for valid index and {@code false} for invalid index.
		 */
		public static boolean isIndexValid(String indexParam) {
			if (DOCUMENT.equals(indexParam)) {
				return true;
			} else if (FOLDER.equals(indexParam)) {
				return true;
			} else if (TABLE_RECORD.equals(indexParam)) {
				return true;
			}
			return false;
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
							"Index name '"+ indexParam+"' is invalid. " +
									"See character at index '"+ charIndex+":"+character+"' which is of type '"+charType+"'.");
				}
				charIndex++;
			}
		}
	}

	/**
	 * Initialise with the ElasticSearch client.
	 *
	 * @param connectionParam SQL Connection to use.
	 * @param esClientParam The ES Client.
	 * @param cacheUtilParam The Cache Util for better performance.
	 */
	public ABaseESUtil(
			Connection connectionParam,
			Client esClientParam,
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
	public ABaseESUtil(Client esClientParam) {
		super(null);
		this.client = esClientParam;
	}

	/*******************************************************/
	/**
	 * Performs a search against the Elasticsearch instance with the {@code qbParam}.
	 *
	 * @param qbParam The @{code QueryBuilder} to search with.
	 * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The number of results to return.
	 * @param formTypesParam The Id's for the Form Definitions to be returned.
	 * @return The {@code ElasticTypeAndId} {@code List}.
	 *
	 * @see Index
	 * @see ElasticTypeAndId
	 */
	public final List<ElasticTypeAndId> searchAndConvertHitsToIdsOnly(
			QueryBuilder qbParam,
			String indexParam,
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		SearchHits searchHits = this.searchWithHits(
				qbParam,
				indexParam,
				true,
				offsetParam,
				limitParam,
				formTypesParam);

		List<ElasticTypeAndId> returnVal = null;

		long totalHits;
		if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {

			returnVal = new ArrayList();

			if((searchHits.getHits().length != totalHits) &&
					(searchHits.getHits().length != limitParam)) {
				throw new FluidElasticSearchException(
						"The Hits and fetch count has mismatch. Total hits is '"+totalHits
								+"' while hits is '"+
								searchHits.getHits().length+"'.");
			}

			long iterationMax = totalHits;
			if (limitParam > 0 && totalHits > limitParam) {
				iterationMax = limitParam;
			}

			//Iterate...
			for (int index = 0;index < iterationMax;index++) {
				SearchHit searchHit = searchHits.getAt(index);
				String idAsString;
				if((idAsString = searchHit.getId()) == null) {
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
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The max number of results to return.
	 * @param formTypesParam The Id's for the Form Definitions to be returned.
	 *
	 * @return The Elasticsearch {@code SearchHits}.
	 */
	public final SearchHits searchWithHits(
			QueryBuilder qbParam,
			String indexParam,
			boolean withNoFieldsParam,
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		if (this.client == null) {
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
			searchRequestBuilder = searchRequestBuilder.storedFields(NO_FIELDS_MAPPER);
		}

		//The requested number of results...
		if (limitParam > 0) {
			searchRequestBuilder = searchRequestBuilder.setSize(limitParam);
		}

		if (offsetParam > -1) {
			searchRequestBuilder = searchRequestBuilder.setFrom(offsetParam);
		}

		if (formTypesParam == null) {
			formTypesParam = new Long[]{};
		}

		//If Types is set...
		if (formTypesParam != null && formTypesParam.length > 0) {
			String[] formTypesAsString = new String[formTypesParam.length];
			for (int index = 0;index < formTypesParam.length;index++) {
				Long formTypeId = formTypesParam[index];
				if (formTypeId == null) {
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
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The number of results to return.
	 * @param formTypesParam The Id's for the Form Definitions to be returned.
	 *
	 * @return {@code true} if there were any {@code SearchHits} from the lookup.
	 */
	public final boolean searchContainHits(
			QueryBuilder qbParam,
			String indexParam,
			boolean withNoFieldsParam,
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		SearchHits searchHits = this.searchWithHits(
				qbParam,
				indexParam,
				withNoFieldsParam,
				offsetParam,
				limitParam,
				formTypesParam);

		return (searchHits != null && searchHits.getTotalHits() > 0);
	}

	/**
	 * Retrieves the {@code Form}'s via the provided {@code formIdsParam}.
	 *
	 * @param indexParam The ElasticSearch index to be used for lookup.
	 * @param formIdsParam {@code List} of Identifiers for the Forms to return.
	 * @param includeFieldDataParam Whether to populate the return {@code Form} table fields.
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The max number of results to return.
	 *
	 * @return {@code List<Form>} List of Forms.
	 */
	public final List<Form> getFormsByIds(
			String indexParam,
			List<Long> formIdsParam,
			boolean includeFieldDataParam,
			int offsetParam,
			int limitParam
	) {
		if (formIdsParam == null || formIdsParam.isEmpty()) {
			return null;
		}

		if (indexParam == null || indexParam.trim().isEmpty()) {
			throw new FluidElasticSearchException(
					"Index is mandatory for lookup.");
		}

		//Query using the descendantId directly...
		StringBuffer byIdQuery = new StringBuffer();

		for (Long formId : formIdsParam) {
			byIdQuery.append(ABaseFluidJSONObject.JSONMapping.ID);
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
					indexParam,
					offsetParam,
					limitParam,
					new Long[]{});
		} else {
			returnVal = this.searchAndConvertHitsToFormWithNoFields(
					QueryBuilders.queryStringQuery(queryByIdsToString),
					indexParam,
					offsetParam,
					limitParam,
					new Long[]{});
		}

		if (returnVal == null || returnVal.isEmpty()) {
			return null;
		}

		return returnVal;
	}

	/**
	 * Performs a search against the Elasticsearch instance with the {@code qbParam}.
	 *
	 * @param qbParam The @{code QueryBuilder} to search with.
	 * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The max number of results to return.
	 * @param formTypesParam The Id's for the Form Definitions to be returned.
	 *
	 * @return The {@code SearchHits} as Fluid {@code Form}.
	 *
	 * @see Form
	 * @see SearchHits
	 */
	public final List<Form> searchAndConvertHitsToFormWithAllFields(
			QueryBuilder qbParam,
			String indexParam,
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		SearchHits searchHits = this.searchWithHits(
				qbParam,
				indexParam,
				false,
				offsetParam,
				limitParam,
				formTypesParam);

		List<Form> returnVal = null;

		long totalHits;
		if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {
			returnVal = new ArrayList();

			if((searchHits.getHits().length != totalHits) &&
					(searchHits.getHits().length != limitParam)) {
				throw new FluidElasticSearchException(
						"The Hits and fetch count has mismatch. Total hits is '"+totalHits+"' while hits is '"+
								searchHits.getHits().length+"'.");
			}

			long iterationMax = totalHits;
			if (limitParam > 0 && totalHits > limitParam) {
				iterationMax = limitParam;
			}

			//Iterate...
			for (int index = 0;index < iterationMax;index++) {
				SearchHit searchHit = searchHits.getAt(index);

				String source;
				if((source = searchHit.getSourceAsString()) == null) {
					continue;
				}

				this.printInfoOnSourceFromES(searchHit);

				Form formFromSource = new Form();

				JSONObject jsonObject = new JSONObject(source);
				List<Field> fieldsForForm = null;
				//Is Form Type available...
				if (jsonObject.has(Form.JSONMapping.FORM_TYPE_ID)) {
					if (this.fieldUtil == null) {
						throw new FluidElasticSearchException(
								"Field Util is not set. Use a different constructor.");
					}

					fieldsForForm = formFromSource.convertTo(
							this.fieldUtil.getFormFieldMappingForFormDefinition(
									jsonObject.getLong(Form.JSONMapping.FORM_TYPE_ID)));
				}

				formFromSource.populateFromElasticSearchJson(
						jsonObject, fieldsForForm);

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
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The max number of results to return.
	 * @param formTypesParam The Id's for the Form Definitions to be returned.
	 *
	 * @return The {@code SearchHits} as Fluid {@code Form}.
	 *
	 * @see Form
	 * @see SearchHits
	 */
	public final List<Form> searchAndConvertHitsToFormWithNoFields(
			QueryBuilder qbParam,
			String indexParam,
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		SearchHits searchHits = this.searchWithHits(
				qbParam,
				indexParam,
				false,
				offsetParam,
				limitParam,
				formTypesParam);

		List<Form> returnVal = null;

		long totalHits;
		if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {
			returnVal = new ArrayList();

			if((searchHits.getHits().length != totalHits) &&
					(searchHits.getHits().length != limitParam)) {
				throw new FluidElasticSearchException(
						"The Hits and fetch count has mismatch. Total hits is '"+
								totalHits+"' while hits is '"+
								searchHits.getHits().length+"'.");
			}

			long iterationMax = totalHits;
			if (limitParam > 0 && totalHits > limitParam) {
				iterationMax = limitParam;
			}

			//Iterate...
			for (int index = 0;index < iterationMax;index++) {
				SearchHit searchHit = searchHits.getAt(index);

				String source;
				if((source = searchHit.getSourceAsString()) == null) {
					continue;
				}

				this.printInfoOnSourceFromES(searchHit);

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
	 * Performs a search against the Elasticsearch instance with the {@code qbParam}.
	 *
	 * @param qbParam The @{code QueryBuilder} to search with.
	 * @param indexParam The Elasticsearch Index to use {@code (document, folder or table_field)}.
	 * @param fieldsParam The Fluid {@code Field}'s to return after lookup.
	 * @param offsetParam The offset for the results to return.
	 * @param limitParam The max number of results to return.
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
			int offsetParam,
			int limitParam,
			Long ... formTypesParam
	) {
		SearchHits searchHits = this.searchWithHits(
				qbParam,
				indexParam,
				false,
				offsetParam,
				limitParam,
				formTypesParam);

		List<Form> returnVal = null;

		long totalHits;
		if (searchHits != null && (totalHits = searchHits.getTotalHits()) > 0) {
			returnVal = new ArrayList();

			if((searchHits.getHits().length != totalHits) &&
					(searchHits.getHits().length != limitParam)) {
				throw new FluidElasticSearchException(
						"The Hits and fetch count has mismatch. Total hits is '"+totalHits
								+"' while hits is '"+
								searchHits.getHits().length+"'.");
			}

			long iterationMax = totalHits;
			if (limitParam > 0 && totalHits > limitParam) {
				iterationMax = limitParam;
			}

			//Iterate...
			for (int index = 0;index < iterationMax;index++) {
				SearchHit searchHit = searchHits.getAt(index);

				String source;
				if((source = searchHit.getSourceAsString()) == null) {
					continue;
				}

				this.printInfoOnSourceFromES(searchHit);

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
	 * Populate all the Table Field values from the Table index.
	 *
	 * @param addAllTableRecordsForReturnParam Whether to include all the Table Records as a return value.
	 * @param includeFieldDataParam Whether to populate the return {@code Form} table fields.
	 * @param formFieldsParam The {@code Field}s to populate.
	 *
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

			TableField tableField = (TableField)descendantField.getFieldValue();

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
					Index.TABLE_RECORD,
					formIdsOnly,
					includeFieldDataParam,
					DEFAULT_OFFSET, MAX_NUMBER_OF_TABLE_RECORDS);

			if (addAllTableRecordsForReturnParam && populatedTableRecords != null) {
				allTableRecordsFromAllFields.addAll(populatedTableRecords);
			}

			tableField.setTableRecords(populatedTableRecords);
			descendantField.setFieldValue(tableField);
		}

		return allTableRecordsFromAllFields;
	}

	/**
	 * Confirms whether index with the name {@code indexToCheckParam} exists.
	 *
	 * @param indexToCheckParam ElasticSearch index to check for existance.
	 * @return {@code true} if ElasticSearch index {@code indexToCheckParam} exists, otherwise {@code false}.
	 */
	public boolean doesIndexExist(String indexToCheckParam) {
		if (indexToCheckParam == null || indexToCheckParam.trim().isEmpty()) {
			return false;
		}

		if (this.client == null) {
			throw new FluidElasticSearchException(
					"ElasticSearch client is not initialized.");
		}

		return this.client.admin().cluster()
				.prepareState().execute()
				.actionGet().getState()
				.getMetaData().hasIndex(indexToCheckParam);
	}

	/**
	 * Close the SQL and ElasticSearch Connection.
	 */
	@Override
	public void closeConnection() {

		CloseConnectionRunnable closeConnectionRunnable =
				new CloseConnectionRunnable(this);

		Thread closeConnThread = new Thread(
				closeConnectionRunnable,"Close ABaseES Connection");
		closeConnThread.start();
	}

	/**
	 * Close the SQL and ElasticSearch Connection, but not in
	 * a separate {@code Thread}.
	 */
	public void closeConnectionNonThreaded() {
		super.closeConnection();

		if (this.client != null) {
			this.client.close();
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
	 * @param searchHitParam Search Hit to perform functions on.
	 */
	private void printInfoOnSourceFromES(SearchHit searchHitParam) { }
}
