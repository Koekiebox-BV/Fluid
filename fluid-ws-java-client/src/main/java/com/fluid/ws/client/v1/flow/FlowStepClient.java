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
