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

import static com.fluidbpm.program.api.vo.ABaseFluidJSONObject.JSONMapping.Elastic.FORM_INDEX_PREFIX;

import java.io.IOException;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;

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
	private IndicesClient indicesClient;

	/**
	 * Initialise with the ElasticSearch client.
	 *
	 * @param indicesClient The ES Client.
	 */
	public ESFormFieldMappingUtil(IndicesClient indicesClient) {
		super(null);
		this.indicesClient = indicesClient;
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 * @param fluidFormMappingToUpdate The Fluid {@code Form} to be
	 *                                      update.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(Form fluidFormMappingToUpdate) {
		this.mergeMappingForIndex(null, fluidFormMappingToUpdate);
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 * @param parentType The {@code _parent} type to be set.
	 * @param fluidFormMappingToUpdate The Fluid {@code Form} to be
	 *                                      update.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(
		String parentType,
		Form fluidFormMappingToUpdate
	) {
		//The Form mapping to update...
		if (fluidFormMappingToUpdate == null) {
			throw new FluidElasticSearchException(
					"Form for mapping not set.");
		}

		//Form Type Id...
		if (fluidFormMappingToUpdate.getFormTypeId() == null ||
				fluidFormMappingToUpdate.getFormTypeId().longValue() < 1) {
			throw new FluidElasticSearchException(
					"Form 'FormType' not set for mapping.");
		}

		String formTypeString = fluidFormMappingToUpdate.getFormTypeId().toString();
		formTypeString = FORM_INDEX_PREFIX.concat(formTypeString);

		JSONObject newContentMappingBuilderFromParam =
				fluidFormMappingToUpdate.toJsonMappingForElasticSearch();

		this.mergeMappingForIndex(
				parentType,
				formTypeString,
				newContentMappingBuilderFromParam);
	}

	/**
	 * Creates / Updates the index {@code indexParam} with mappings
	 * provided in {@code fluidFormMappingToUpdateParam}.
	 *
	 * @param parentType The {@code _parent} type to be set.
	 * @param formTypeString The {@code _type} type to be set.
	 * @param newContentMappingBuilderFrom The new JSON mapping properties.
	 *
	 * @see Form
	 * @throws FluidElasticSearchException If validation or acknowledgement problems occur.
	 */
	public void mergeMappingForIndex(
		String parentType,
		String formTypeString,
		JSONObject newContentMappingBuilderFrom
	) {
		//The Form mapping to update...
		if (newContentMappingBuilderFrom == null) {
			throw new FluidElasticSearchException(
					"'JSON Object' for mapping not set.");
		}

		//Retrieve and update...
		GetIndexResponse getExistingIndex = this.getOrCreateIndex(formTypeString);
		JSONObject existingPropsToUpdate = null;

		for (String mappingKey : getExistingIndex.getMappings().keySet()) {
			//Found index...
			if (!mappingKey.equals(formTypeString)) {
				continue;
			}

			//Found a match...
			Object obj = getExistingIndex.getMappings().get(mappingKey);
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
		if (existingPropsToUpdate == null || existingPropsToUpdate.isEmpty()) {
			existingPropsToUpdate = new JSONObject();

			existingPropsToUpdate.put(
					ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
					newContentMappingBuilderFrom);

			//Set the additional properties...
			this.setAdditionalProps(existingPropsToUpdate, parentType);

			PutMappingRequest putMappingRequest = new PutMappingRequest(formTypeString);
			putMappingRequest.source(existingPropsToUpdate.toString(), XContentType.JSON);

			try {
				AcknowledgedResponse acknowledgedResponse = this.indicesClient.putMapping(
						putMappingRequest, RequestOptions.DEFAULT);
				if (!acknowledgedResponse.isAcknowledged()) {
					throw new FluidElasticSearchException(
							"Index Update for Creating '"+
									formTypeString+"' and type '"+
									formTypeString+"' not acknowledged by ElasticSearch.");
				}
			} catch (IOException ioErr) {
				throw new FluidElasticSearchException(
						String.format("Unable to create mapping for index '%s'. %s",
								formTypeString,
								ioErr.getMessage()), ioErr);
			}
			//Creation done.
			return;
		}

		//Merge existing with new...
		for (String existingKey : existingPropsToUpdate.keySet()) {
			newContentMappingBuilderFrom.put(existingKey,
					existingPropsToUpdate.get(existingKey));
		}

		//Check to see whether there are any new fields added...
		boolean noChanges = true;
		for (String possibleExistingKey : newContentMappingBuilderFrom.keySet()) {
			if (!existingPropsToUpdate.has(possibleExistingKey)) {
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
				newContentMappingBuilderFrom);

		//Set the additional properties...
		this.setAdditionalProps(
				existingPropsToUpdate,
				parentType);

		//Push the change...
		PutMappingRequest putMappingRequest = new PutMappingRequest(formTypeString);
		putMappingRequest.source(existingPropsToUpdate.toString(), XContentType.JSON);

		try {
			AcknowledgedResponse acknowledgedResponse = this.indicesClient.putMapping(
					putMappingRequest, RequestOptions.DEFAULT);
			if (!acknowledgedResponse.isAcknowledged()) {
				throw new FluidElasticSearchException(
						"Index Update for '"+
								formTypeString+"' and type '"+
								formTypeString+"' not acknowledged by ElasticSearch.");
			}
		} catch (IOException ioErr) {
			throw new FluidElasticSearchException(
					String.format("Unable to merge mapping for index '%s'. %s",
							formTypeString,
							ioErr.getMessage()), ioErr);
		}
	}

	/**
	 * Set the additional properties on the {@code existingPropsToUpdateParam} json object.
	 *
	 * @param existingPropsToUpdate The existing properties to update.
	 * @param parentType The {@code _parent} type to set.
	 */
	private void setAdditionalProps(
		JSONObject existingPropsToUpdate,
		String parentType
	) {
		if (parentType == null || parentType.trim().length() == 0) {
			return;
		}

		JSONObject typeJson = new JSONObject();
		typeJson.put(Field.JSONMapping.FIELD_TYPE, parentType);

		existingPropsToUpdate.put(
				Form.JSONMapping._PARENT, typeJson);
	}

	/**
	 * Confirms whether index with the name {@code indexToCheckParam} exists.
	 *
	 * @param indexToCheck ElasticSearch index to check for existance.
	 * @return {@code true} if ElasticSearch index {@code indexToCheckParam} exists, otherwise {@code false}.
	 */
	public boolean doesIndexExist(String indexToCheck) {
		if (indexToCheck == null || indexToCheck.trim().isEmpty()) {
			return false;
		}

		if (this.indicesClient == null) {
			throw new FluidElasticSearchException(
					"ElasticSearch IndicesClient is not initialized.");
		}

		GetIndexRequest getIndexReq = new GetIndexRequest(indexToCheck);

		try {
			return this.indicesClient.exists(getIndexReq, RequestOptions.DEFAULT);
		} catch (IOException ioErr) {
			throw new FluidElasticSearchException(
					String.format("Unable to check if index '%s' exists. %s",
							indexToCheck,
							ioErr.getMessage()), ioErr);
		}
	}

	/**
	 * Creates a new index or fetches existing index.
	 *
	 * @param index The name of the index to create in lower-case.
	 * @return Newly created or existing index.
	 *
	 * @see GetIndexResponse
	 */
	public GetIndexResponse getOrCreateIndex(String index) {
		if (this.doesIndexExist(index)) {
			GetIndexRequest getIndexRequest = new GetIndexRequest(index);
			try {
				return this.indicesClient.get(getIndexRequest, RequestOptions.DEFAULT);
			} catch (IOException ioErr) {
				throw new FluidElasticSearchException(
						String.format("Unable to get index '%s' exists. %s",
								index, ioErr.getMessage()), ioErr);
			}
		} else {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
			try {
				CreateIndexResponse createIndexResponse =
						this.indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
				if (!createIndexResponse.isAcknowledged()) {
					throw new FluidElasticSearchException(
							"Index Creation for '"+ index+"' not acknowledged by ElasticSearch.");
				}

				return this.indicesClient.get(new GetIndexRequest(index), RequestOptions.DEFAULT);
			} catch (IOException ioErr) {
				throw new FluidElasticSearchException(
						String.format("Unable to create index '%s'. %s",
								index, ioErr.getMessage()), ioErr);
			}
		}
	}
}
