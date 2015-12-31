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

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public abstract class ABaseFluidJSONObject extends ABaseFluidVO {

    protected JSONObject jsonObject;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String ID = "id";
        public static final String SERVICE_TICKET = "serviceTicket";
    }

    /**
     *
     */
    public ABaseFluidJSONObject() {
        super();
    }

    /**
     *
     * @param jsonObjectParam
     */
    public ABaseFluidJSONObject(JSONObject jsonObjectParam) {
        this();

        this.jsonObject = jsonObjectParam;

        if(this.jsonObject != null)
        {
            //Id...
            if (!this.jsonObject.isNull(JSONMapping.ID)) {
                this.setId(this.jsonObject.getLong(JSONMapping.ID));
            }

            //Service Ticket...
            if (!this.jsonObject.isNull(JSONMapping.SERVICE_TICKET)) {
                this.setServiceTicket(this.jsonObject.getString(JSONMapping.SERVICE_TICKET));
            }
        }
    }

    /**
     *
     * @return
     * @throws JSONException
     */
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = new JSONObject();

        //Id...
        if(this.getId() != null)
        {
            returnVal.put(JSONMapping.ID,this.getId());
        }
        //Service Ticket...
        if(this.getServiceTicket() != null)
        {
            returnVal.put(JSONMapping.SERVICE_TICKET, this.getServiceTicket());
        }

        return returnVal;
    }

    /**
     *
     * @param longValueParam
     * @return
     */
    public Date getLongAsDateFromJson(Long longValueParam)
    {
        if(longValueParam == null)
        {
            return null;
        }

        return new Date(longValueParam.longValue());
    }

    /**
     *
     * @param dateValueParam
     * @return
     */
    public Long getDateAsLongFromJson(Date dateValueParam)
    {
        if(dateValueParam == null)
        {
            return null;
        }

        return dateValueParam.getTime();
    }
}
