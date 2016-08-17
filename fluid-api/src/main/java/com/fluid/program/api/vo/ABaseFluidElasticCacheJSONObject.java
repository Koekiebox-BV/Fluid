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

package com.fluid.program.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     The Base class for any sub-class that wants to make use of the
 *     ElasticSearch. See {@code https://www.elastic.co}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidVO
 * @see JSONObject
 */
public abstract class ABaseFluidElasticCacheJSONObject extends ABaseFluidJSONObject{

    /**
     * Default constructor.
     */
    public ABaseFluidElasticCacheJSONObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidElasticCacheJSONObject(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Conversion to {@code JSONObject} for storage in ElasticCache.
     *
     * @return {@code JSONObject} representation of {@code Form}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    public abstract JSONObject toJsonForEC() throws JSONException;
}
