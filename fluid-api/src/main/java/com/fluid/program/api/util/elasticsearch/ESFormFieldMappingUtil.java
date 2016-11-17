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

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.json.JSONObject;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.fluid.program.api.util.ABaseUtil;
import com.fluid.program.api.util.elasticsearch.exception.FluidElasticSearchException;
import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Form;

/**
 * ElasticSearch Utility class used for {@code Field} mappings.
 *
 * @author jasonbruwer on 2016/11/18.
 * @since 1.3
 * @see ABaseUtil
 * @see ABaseESUtil
 * @see com.fluid.program.api.vo.Field.ElasticSearchType
 *
 */
public class ESFormFieldMappingUtil extends ABaseESUtil{

    /**
     * Initialise with the ElasticSearch client.
     *
     * @param esClientParam The ES Client.
     */
    public ESFormFieldMappingUtil(Client esClientParam) {
        super(esClientParam);
    }

    /**
     * Creates / Updates the index {@code indexParam} with mappings
     * provided in {@code fluidFormMappingToUpdateParam}.
     *
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
            Form fluidFormMappingToUpdateParam)
    {
        if(indexParam == null)
        {
            throw new FluidElasticSearchException(
                    "Index name '"+indexParam+"' is invalid.");
        }

        //The Form mapping to update...
        if(fluidFormMappingToUpdateParam == null)
        {
            throw new FluidElasticSearchException(
                    "Form for mapping not set.");
        }

        //Form Type Id...
        if(fluidFormMappingToUpdateParam.getFormTypeId() == null ||
                fluidFormMappingToUpdateParam.getFormTypeId().longValue() < 1)
        {
            throw new FluidElasticSearchException(
                    "Form 'FormType' not set for mapping.");
        }

        String formTypeString =
                fluidFormMappingToUpdateParam.getFormTypeId().toString();

        JSONObject newContentMappingBuilder =
                fluidFormMappingToUpdateParam.toJsonMappingForElasticSearch();

        //Retrieve and update...
        if(this.doesIndexExist(indexParam))
        {
            GetIndexResponse getExistingIndex =
                    this.client.admin().indices().prepareGetIndex().get();

            JSONObject propsToUpdate = null;

            for(ObjectCursor mappingKey : getExistingIndex.getMappings().keys())
            {
                //Found index...
                if(!mappingKey.value.toString().equals(indexParam))
                {
                    continue;
                }

                //Found a match...
                Object obj = getExistingIndex.getMappings().get(mappingKey.value.toString());
                if(obj instanceof ImmutableOpenMap)
                {
                    ImmutableOpenMap casted = (ImmutableOpenMap)obj;

                    //Type...
                    if(casted.containsKey(formTypeString) &&
                            casted.get(formTypeString) instanceof MappingMetaData)
                    {
                        MappingMetaData mappingMetaData = (MappingMetaData)casted.get(formTypeString);

                        try {
                            propsToUpdate = new JSONObject(mappingMetaData.source().string());
                        }
                        //throw by mappingMetaData.source()
                        catch (IOException eParam) {
                            throw new FluidElasticSearchException(
                                    "Unable to retrieve source from 'Mapping Meta-Data'. "+
                                    eParam.getMessage(),eParam);
                        }
                    }
                }
            }

            if(propsToUpdate == null)
            {
                throw new FluidElasticSearchException(
                        "Unable to retrieve existing properties for update.");
            }

            JSONObject existingPropertiesUpdated =
                    propsToUpdate.getJSONObject(formTypeString).getJSONObject(
                            ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES);

            //Merge existing with new...
            for(String key : newContentMappingBuilder.keySet())
            {
                if(existingPropertiesUpdated.has(key))
                {
                    continue;
                }

                newContentMappingBuilder.put(key,
                        newContentMappingBuilder.get(key));
            }

            //Update the properties to new values...
            propsToUpdate.put(
                    ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
                    newContentMappingBuilder);

            //Push the change...
            PutMappingResponse putMappingResponse =
                    this.client.admin().indices().preparePutMapping(indexParam).setType(
                            formTypeString).setSource(propsToUpdate.toString()).get();

            if(!putMappingResponse.isAcknowledged())
            {
                throw new FluidElasticSearchException(
                        "Index Update for '"+
                                indexParam+"' and type '"+
                                formTypeString+"' not acknowledged by ElasticSearch.");
            }
        }
        //Create...
        else
        {
            CreateIndexRequestBuilder createIndexRequestBuilder =
                    this.client.admin().indices().prepareCreate(indexParam);

            createIndexRequestBuilder.addMapping(
                    formTypeString,
                    newContentMappingBuilder.toString());

            // MAPPING DONE
            CreateIndexResponse mappingCreateResponse =
                    createIndexRequestBuilder.execute().actionGet();

            if(!mappingCreateResponse.isAcknowledged())
            {
                throw new FluidElasticSearchException(
                        "Index Creation for '"+
                                indexParam+"' not acknowledged by ElasticSearch.");
            }
        }
    }
}
