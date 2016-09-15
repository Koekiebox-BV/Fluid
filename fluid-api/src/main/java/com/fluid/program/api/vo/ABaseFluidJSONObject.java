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
 * <p>
 *     The Base class for any sub-class that wants to make use of the
 *     JSON based message format used by the Fluid RESTful Web Service.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidVO
 * @see JSONObject
 */
public abstract class ABaseFluidJSONObject extends ABaseFluidVO {

    protected JSONObject jsonObject;

    /**
     * The JSON mapping for the {@code ABaseFluidJSONObject} object.
     */
    public static class JSONMapping
    {
        public static final String ID = "id";
        public static final String SERVICE_TICKET = "serviceTicket";
        public static final String ECHO = "echo";
    }

    /**
     * Default constructor.
     */
    public ABaseFluidJSONObject() {
        super();
    }

    /**
     * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseFluidJSONObject(JSONObject jsonObjectParam) {
        this();

        this.jsonObject = jsonObjectParam;

        if(this.jsonObject != null)
        {
            //Id...
            if (!this.jsonObject.isNull(JSONMapping.ID)) {

                Object idObject = this.jsonObject.get(JSONMapping.ID);

                //Long Id...
                if(idObject instanceof Number)
                {
                    this.setId(this.jsonObject.getLong(JSONMapping.ID));
                }
                //String Id...
                else if(idObject instanceof String)
                {
                    String idStr = this.jsonObject.getString(JSONMapping.ID);

                    try
                    {
                        this.setId(Long.parseLong(idStr));
                    }
                    catch (NumberFormatException nfe)
                    {
                        this.setId(null);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Unable to parse Field '"+JSONMapping.ID+"'.");
                }
            }

            //Service Ticket...
            if (!this.jsonObject.isNull(JSONMapping.SERVICE_TICKET)) {
                this.setServiceTicket(this.jsonObject.getString(JSONMapping.SERVICE_TICKET));
            }

            //Echo...
            if (!this.jsonObject.isNull(JSONMapping.ECHO)) {
                this.setEcho(this.jsonObject.getString(JSONMapping.ECHO));
            }
        }
    }

    /**
     * <p>
     * Base {@code toJsonObject} that creates a {@code JSONObject}
     * with the Id and ServiceTicket set.
     * </p>
     *
     * @return {@code JSONObject} representation of {@code ABaseFluidJSONObject}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see org.json.JSONObject
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
        //Echo...
        if(this.getEcho() != null)
        {
            returnVal.put(JSONMapping.ECHO, this.getEcho());
        }

        return returnVal;
    }

    /**
     * Converts the {@code Long} timestamp into a {@code Date} object.
     *
     * Returns {@code null} if {@code longValueParam} is {@code null}.
     *
     * @param longValueParam The milliseconds since January 1, 1970, 00:00:00 GMT
     * @return {@code Date} Object from {@code longValueParam}
     *
     */
    public Date getLongAsDateFromJson(Long longValueParam)
    {
        if(longValueParam == null)
        {
            return null;
        }

        return new Date(longValueParam);
    }

    /**
     * Converts the {@code Date} object into a {@code Long} timestamp.
     *
     * Returns {@code null} if {@code dateValueParam} is {@code null}.
     *
     * @param dateValueParam {@code Long} Object from {@code dateValueParam}
     * @return The milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Long getDateAsLongFromJson(Date dateValueParam)
    {
        if(dateValueParam == null)
        {
            return null;
        }

        return dateValueParam.getTime();
    }

    /**
     * Returns the local JSON object.
     * Only set through constructor.
     *
     * @return The local set {@code JSONObject} object.
     */
    public JSONObject getJSONObject()
    {
        return this.jsonObject;
    }
}
