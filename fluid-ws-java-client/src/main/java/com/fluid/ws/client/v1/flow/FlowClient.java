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

package com.fluid.ws.client.v1.flow;

import org.json.JSONObject;

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow's / Workflows.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.Flow
 * @see Flow
 * @see ABaseClientWS
 */
public class FlowClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FlowClient(String endpointBaseUrlParam,
                      String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new Flow with an Introduction and Exit basic rule.
     *
     * @param flowParam The flow to Create.
     * @return The created flow.
     *
     * @see Flow
     */
    public Flow createFlow(Flow flowParam)
    {
        if(flowParam != null && this.serviceTicket != null)
        {
            flowParam.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.putJson(
                flowParam, WS.Path.Flow.Version1.flowCreate()));
    }

    /**
     * Updates an existing Flow.
     *
     * @param flowParam The flow to Update.
     * @return The updated flow.
     *
     * @see Flow
     */
    public Flow updateFlow(Flow flowParam)
    {
        if(flowParam != null && this.serviceTicket != null)
        {
            flowParam.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.postJson(
                flowParam, WS.Path.Flow.Version1.flowUpdate()));
    }

    /**
     * Retrieves a Flow by Primary Key.
     *
     * @param flowIdParam The Flow primary key.
     * @return The Flow.
     */
    public Flow getFlowById(Long flowIdParam)
    {
        Flow flow = new Flow(flowIdParam);

        if(this.serviceTicket != null)
        {
            flow.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.postJson(
                flow, WS.Path.Flow.Version1.getById()));
    }

    /**
     * Retrieves a Flow by unique Name.
     *
     * @param flowNameParam The Flow name.
     * @return The Flow.
     */
    public Flow getFlowByName(String flowNameParam)
    {
        Flow flow = new Flow();
        flow.setName(flowNameParam);

        if(this.serviceTicket != null)
        {
            flow.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.postJson(
                flow, WS.Path.Flow.Version1.getByName()));
    }


    /**
     * Delete an existing Flow.
     *
     * @param flowParam The Flow to delete.
     * @return The deleted Flow.
     */
    public Flow deleteFlow(Flow flowParam)
    {
        if(flowParam != null && this.serviceTicket != null)
        {
            flowParam.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.postJson(flowParam, WS.Path.Flow.Version1.flowDelete()));
    }

    /**
     * Forcefully Delete an existing Flow.
     *
     * Only 'admin' can forcefully delete a Flow.
     *
     * @param flowParam The Flow to delete.
     * @return The deleted Flow.
     */
    public Flow forceDeleteFlow(Flow flowParam)
    {
        if(flowParam != null && this.serviceTicket != null)
        {
            flowParam.setServiceTicket(this.serviceTicket);
        }

        return new Flow(this.postJson(flowParam, WS.Path.Flow.Version1.flowDelete(true)));
    }
}
