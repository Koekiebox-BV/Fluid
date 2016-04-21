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

import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow Steps.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.FlowStep
 * @see FlowStep
 * @see ABaseClientWS
 */
public class FlowStepClient extends ABaseClientWS {

    /**
     * Default constructor.
     */
    public FlowStepClient() {
        super();
    }

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FlowStepClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new Flow Step.
     *
     * @param flowStepParam The step to create.
     * @return The created step.
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
     * Updates an existing Flow Step.
     *
     * @param flowStepParam The updated Flow Step values.
     * @return The updated Step.
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
     * Retrieves an existing Flow Step via Primary key.
     *
     * @param flowIdParam The Flow Step Primary Key.
     * @param flowStepTypeParam The type of step.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public FlowStep getFlowStepById(
            Long flowIdParam, String flowStepTypeParam)
    {
        FlowStep flow = new FlowStep(flowIdParam);
        flow.setFlowStepType(flowStepTypeParam);

        if(this.serviceTicket != null)
        {
            flow.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flow, WS.Path.FlowStep.Version1.getById()));
    }

    /**
     * Delete an existing Flow Step.
     *
     * @param flowStepParam The Flow Step to delete.
     * @return The deleted Flow Step.
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
     * Forcefully delete an existing Flow Step.
     *
     * Only 'admin' can forcefully delete a step.
     *
     * @param flowStepParam The Flow Step to delete.
     * @return The deleted Flow Step.
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
