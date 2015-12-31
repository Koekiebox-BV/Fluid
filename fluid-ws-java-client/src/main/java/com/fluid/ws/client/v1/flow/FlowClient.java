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

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow rules and
 * underlying steps and rules.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * Created by jasonbruwer on 15/12/19.
 */
public class FlowClient extends ABaseClientWS {

    /**
     *
     */
    public FlowClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FlowClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }


    /**
     *
     * @param flowParam
     * @return
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
     *
     * @param flowParam
     * @return
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
     *
     * @param flowIdParam
     * @return
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
     *
     * @param flowParam
     * @return
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
     *
     * @param flowParam
     * @return
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
