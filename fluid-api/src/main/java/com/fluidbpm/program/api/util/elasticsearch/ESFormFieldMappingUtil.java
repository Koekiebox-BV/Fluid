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

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.fluidbpm.program.api.util.ABaseUtil;
import com.fluidbpm.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;

/**
 * ElasticSearch Utility class used for {@code Field} mappings.
 *
 * @author jasonbruwer on 2016/11/18.
 * @since 1.3
 * @see ABaseUtil
 * @see ABaseESUtil
 * @see Field.ElasticSearchType
 *
 */
public class ESFormFieldMappingUtil extends ABaseESUtil {
	private AdminClient adminClient;

	/**
	 * Initialise with the ElasticSearch client.
	 *
	 * @param esClientParam The ES Client.
	 */
	public ESFormFieldMappingUtil(AdminClient esClientParam) {
		super(null);
		this.adminClient = esClientParam;
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 * @param indexParam The ElasticSearch index effected.
	 * @param fluidFormMappingToUpdateParam The Fluid {@code Form} to be
	 *                                      update.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(
			String indexParam,
			Form fluidFormMappingToUpdateParam) {
		this.mergeMappingForIndex(
				indexParam, null, fluidFormMappingToUpdateParam);
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 *
	 * @param indexParam The ElasticSearch index effected.
	 * @param parentTypeParam The {@code _parent} type to be set.
	 * @param fluidFormMappingToUpdateParam The Fluid {@code Form} to be
	 *                                      update.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(
		String indexParam,
		String parentTypeParam,
		Form fluidFormMappingToUpdateParam
	) {
		if (indexParam == null) {
			throw new FluidElasticSearchException(
					"Index name '"+indexParam+"' is invalid.");
		}

		//The Form mapping to update...
		if (fluidFormMappingToUpdateParam == null) {
			throw new FluidElasticSearchException(
					"Form for mapping not set.");
		}

		//Form Type Id...
		if (fluidFormMappingToUpdateParam.getFormTypeId() == null ||
				fluidFormMappingToUpdateParam.getFormTypeId().longValue() < 1) {
			throw new FluidElasticSearchException(
					"Form 'FormType' not set for mapping.");
		}

		String formTypeString =
				fluidFormMappingToUpdateParam.getFormTypeId().toString();

		JSONObject newContentMappingBuilderFromParam =
				fluidFormMappingToUpdateParam.toJsonMappingForElasticSearch();

		this.mergeMappingForIndex(
				indexParam,
				parentTypeParam,
				formTypeString,
				newContentMappingBuilderFromParam);
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 * @param indexParam The ElasticSearch index effected.
	 * @param parentTypeParam The {@code _parent} type to be set.
	 * @param formTypeString The {@code _type} type to be set.
	 * @param newContentMappingBuilderFromParam The new JSON mapping properties.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(
			String indexParam,
			String parentTypeParam,
			String formTypeString,
			JSONObject newContentMappingBuilderFromParam
	) {
		if (indexParam == null) {
			throw new FluidElasticSearchException(
					"Index name '"+indexParam+"' is invalid.");
		}

		//The Form mapping to update...
		if (newContentMappingBuilderFromParam == null) {
			throw new FluidElasticSearchException(
					"'JSON Object' for mapping not set.");
		}

		//Retrieve and update...
		GetIndexResponse getExistingIndex = this.getOrCreateIndex(indexParam);
		JSONObject existingPropsToUpdate = null;

		for (ObjectCursor mappingKey : getExistingIndex.getMappings().keys()) {
			//Found index...
			if (!mappingKey.value.toString().equals(indexParam)) {
				continue;
			}

			//Found a match...
			Object obj = getExistingIndex.getMappings().get(mappingKey.value.toString());
			if (obj instanceof ImmutableOpenMap) {
				ImmutableOpenMap casted = (ImmutableOpenMap)obj;

				//Type...
				if (casted.containsKey(formTypeString) &&
						casted.get(formTypeString) instanceof MappingMetaData) {
					MappingMetaData mappingMetaData = (MappingMetaData)casted.get(formTypeString);
					existingPropsToUpdate = new JSONObject(mappingMetaData.source().string());
				}
			}
		}

		//No mapping for the type create a new one...
		if (existingPropsToUpdate == null) {
			existingPropsToUpdate = new JSONObject();

			existingPropsToUpdate.put(
					ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
					newContentMappingBuilderFromParam);

			//Set the additional properties...
			this.setAdditionalProps(existingPropsToUpdate, parentTypeParam);

			PutMappingRequestBuilder putMappingRequestBuilder =
					this.adminClient.indices().preparePutMapping(indexParam);

			putMappingRequestBuilder = putMappingRequestBuilder.setType(formTypeString);
			putMappingRequestBuilder = putMappingRequestBuilder.setSource(
					existingPropsToUpdate.toString(), XContentType.JSON);

			AcknowledgedResponse acknowledgedResponse = putMappingRequestBuilder.get();
			if (!acknowledgedResponse.isAcknowledged()) {
				throw new FluidElasticSearchException(
						"Index Update for Creating '"+
								indexParam+"' and type '"+
								formTypeString+"' not acknowledged by ElasticSearch.");
			}

			//Creation done.
			return;
		}

		//Update the existing index...
		JSONObject existingPropertiesUpdated =
				existingPropsToUpdate.getJSONObject(formTypeString).getJSONObject(
						ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES);

		//Merge existing with new...
		for (String existingKey : existingPropertiesUpdated.keySet()) {
			newContentMappingBuilderFromParam.put(existingKey,
					existingPropertiesUpdated.get(existingKey));
		}

		//Check to see whether there are any new fields added...
		boolean noChanges = true;
		for (String possibleExistingKey : newContentMappingBuilderFromParam.keySet()) {
			if (!existingPropertiesUpdated.has(possibleExistingKey)) {
				noChanges = false;
				break;
			}
		}

		if (noChanges) {
			return;
		}

		//Update the properties to new values...
		existingPropsToUpdate.put(
				ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
				newContentMappingBuilderFromParam);

		//Set the additional properties...
		this.setAdditionalProps(
				existingPropsToUpdate,
				parentTypeParam);

		//Push the change...
		PutMappingRequestBuilder putMappingRequestBuilder =
				this.adminClient.indices().preparePutMapping(indexParam);

		putMappingRequestBuilder = putMappingRequestBuilder.setType(formTypeString);
		putMappingRequestBuilder = putMappingRequestBuilder.setSource(
				existingPropsToUpdate.toString(), XContentType.JSON);

		AcknowledgedResponse acknowledgedResponse = putMappingRequestBuilder.get();
		if (!acknowledgedResponse.isAcknowledged()) {
			throw new FluidElasticSearchException(
					"Index Update for '"+
							indexParam+"' and type '"+
							formTypeString+"' not acknowledged by ElasticSearch.");
		}
	}

	/**
	 * Set the additional properties on the {@code existingPropsToUpdateParam} json object.
	 *
	 * @param existingPropsToUpdateParam The existing properties to update.
	 * @param parentTypeParam The {@code _parent} type to set.
	 */
	private void setAdditionalProps(
			JSONObject existingPropsToUpdateParam,
			String parentTypeParam
	) {
		if (parentTypeParam == null || parentTypeParam.trim().length() == 0) {
			return;
		}

		JSONObject typeJson = new JSONObject();
		typeJson.put(Field.JSONMapping.FIELD_TYPE, parentTypeParam);

		existingPropsToUpdateParam.put(
				Form.JSONMapping._PARENT, typeJson);
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

		if (this.adminClient == null) {
			throw new FluidElasticSearchException(
					"ElasticSearch AdminClient is not initialized.");
		}

		return this.adminClient
				.cluster()
				.prepareState()
				.execute()
				.actionGet()
				.getState()
				.getMetaData()
				.hasIndex(indexToCheckParam);
	}

	/**
	 * Creates a new index or fetches existing index.
	 *
	 * @param indexParam The name of the index to create in lower-case.
	 * @return Newly created or existing index.
	 *
	 * @see GetIndexResponse
	 */
	public GetIndexResponse getOrCreateIndex(String indexParam) {
		if (this.doesIndexExist(indexParam)) {
			return this.adminClient.indices().prepareGetIndex().get();
		} else {
			CreateIndexRequestBuilder createIndexRequestBuilder =
					this.adminClient.indices().prepareCreate(indexParam);

			CreateIndexResponse mappingCreateResponse =
					createIndexRequestBuilder.execute().actionGet();

			if (!mappingCreateResponse.isAcknowledged()) {
				throw new FluidElasticSearchException(
						"Index Creation for '"+
								indexParam+"' not acknowledged by ElasticSearch.");
			}

			return this.adminClient.indices().prepareGetIndex().get();
		}
	}
}
