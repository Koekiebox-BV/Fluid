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

import com.fluid.program.api.vo.flow.FlowStep;
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
public class FlowStepClient extends ABaseClientWS {

    /**
     *
     */
    public FlowStepClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FlowStepClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }


    /**
     *
     * @param flowStepParam
     * @return
     */
    public FlowStep createFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.putJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepCreate()));
    }

    /**
     *
     * @param flowStepParam
     * @return
     */
    public FlowStep updateFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepUpdate()));
    }

    /**
     *
     * @param flowStepParam
     * @return
     */
    public FlowStep deleteFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete()));
    }

    /**
     *
     * @param flowStepParam
     * @return
     */
    public FlowStep forceDeleteFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete(true)));
    }
}
