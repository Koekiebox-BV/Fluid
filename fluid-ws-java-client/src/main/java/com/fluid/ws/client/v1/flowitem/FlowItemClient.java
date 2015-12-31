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

package com.fluid.ws.client.v1.flowitem;

import org.json.JSONException;

import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Created by jasonbruwer on 15/01/04.
 */
public class FlowItemClient extends ABaseClientWS {

    /**
     *
     */
    public FlowItemClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FlowItemClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * @param flowJobItemParam
     * @param flowNameParam
     * @return
     */
    public FluidItem createFlowItem(
            FluidItem flowJobItemParam,
            String flowNameParam) {

        if (flowJobItemParam != null && this.serviceTicket != null) {
            flowJobItemParam.setServiceTicket(this.serviceTicket);
        }

        //Flow Job Item Step etc...
        if(flowJobItemParam != null)
        {
            flowJobItemParam.setFlow(flowNameParam);
        }

        try {

            return new FluidItem(this.putJson(
                    flowJobItemParam, WS.Path.FlowItem.Version1.flowItemCreate()));
        }
        //
        catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
