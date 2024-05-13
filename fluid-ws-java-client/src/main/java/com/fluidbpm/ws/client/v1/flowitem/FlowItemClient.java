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

package com.fluidbpm.ws.client.v1.flowitem;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Java Web Service Client for Fluid / Flow Item related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FlowItem
 * @see FluidItem
 */
public class FlowItemClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket The Server issued Service Ticket.
     */
    public FlowItemClient(String endpointBaseUrl, String serviceTicket) {
        super(endpointBaseUrl);
        this.setServiceTicket(serviceTicket);
    }

    /**
     * Retrieves the Fluid item by Primary key.
     *
     * @param formIdParam The Form primary key.
     * @param populateForm Should the FlowItem form be populated (even if FlowItem is not found)
     * @param executeCalculatedLabels Execute the calculated label if form should be populated {@code populateForm == true}.
     * @return Form by Primary key.
     */
    public FluidItem getFluidItemByFormId(
            Long formIdParam,
            boolean populateForm,
            boolean executeCalculatedLabels
    ) {
        return this.getFluidItemByFormId(formIdParam, populateForm, executeCalculatedLabels, false);
    }

    /**
     * Retrieves the Fluid item by Primary key.
     *
     * @param formIdParam The Form primary key.
     * @param populateForm Should the FlowItem form be populated (even if FlowItem is not found)
     * @param executeCalculatedLabels Execute the calculated label if form should be populated {@code populateForm == true}.
     * @param populateStepProgress Should the flow progress be populated as FluidItem properties.
     * @return Form by Primary key.
     */
    public FluidItem getFluidItemByFormId(
            Long formIdParam,
            boolean populateForm,
            boolean executeCalculatedLabels,
            boolean populateStepProgress
    ) {
        Form form = new Form(formIdParam);
        form.setServiceTicket(this.serviceTicket);
        return new FluidItem(this.postJson(form, WS.Path.FlowItem.Version1.getByForm(
                populateForm, executeCalculatedLabels, populateStepProgress)));
    }

    /**
     * Retrieves the Fluid item by Primary key.
     * Form will not be populated and label fields will not be calculated.
     *
     * @param formIdParam The Form primary key.
     * @return Form by Primary key.
     */
    public FluidItem getFluidItemByFormId(Long formIdParam) {
        Form form = new Form(formIdParam);
        form.setServiceTicket(this.serviceTicket);
        return new FluidItem(this.postJson(form, WS.Path.FlowItem.Version1.getByForm(false, false, false)));
    }

    /**
     * Creates a new Fluid Item that will be sent to the {@code flowJobItemParam}
     * Flow.
     *
     * @param flowJobItem The Fluid Item to create and send to Workflow.
     * @param flowName The name of the Flow where the Item must be sent.
     * @return The created Fluid item.
     */
    public FluidItem createFlowItem(FluidItem flowJobItem, String flowName) {
        if (flowJobItem != null && this.serviceTicket != null) {
            flowJobItem.setServiceTicket(this.serviceTicket);
        }

        //Flow Job Item Step etc...
        if (flowJobItem != null) {
            flowJobItem.setFlow(flowName);
        }

        try {
            return new FluidItem(this.putJson(flowJobItem, WS.Path.FlowItem.Version1.flowItemCreate()));
        } catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all items in an error state.
     * @return All the Fluid items in error state.
     * @see FluidItem
     */
    public List<FluidItem> getFluidItemsInError() {
        JobView jobView = new JobView();
        jobView.setServiceTicket(this.serviceTicket);
        try {
            return new FluidItemListing(this.postJson(
                    jobView,
                    WS.Path.FlowItem.Version1.getAllInError())).getListing();
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves items for the provided JobView.
     *
     * @param jobViewParam The {@link JobView} to retrieve items from.
     * @param queryLimitParam The query limit.
     * @param offsetParam The offset.
     * @param sortFieldParam The sort field.
     * @param sortOrderParam The sort order.
     * @return The Fluid items for the {@code jobViewParam}.
     */
    public FluidItemListing getFluidItemsForView(
        JobView jobViewParam,
        int queryLimitParam,
        int offsetParam,
        String sortFieldParam,
        String sortOrderParam
    ) {
        if (jobViewParam != null) jobViewParam.setServiceTicket(this.serviceTicket);

        try {
            return new FluidItemListing(this.postJson(
                    jobViewParam,
                    WS.Path.FlowItem.Version1.getByJobView(
                            queryLimitParam,
                            offsetParam,
                            sortFieldParam,
                            sortOrderParam
                    )));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves items for the provided JobView.
     * No sorting or ordering for this method.
     *
     * @param jobViewParam The {@link JobView} to retrieve items from.
     * @param queryLimitParam The query limit.
     * @param offsetParam The offset.
     * @return The Fluid items for the {@code jobViewParam}.
     */
    public FluidItemListing getFluidItemsForView(
        JobView jobViewParam,
        int queryLimitParam,
        int offsetParam
    ) {
        return this.getFluidItemsForView(
                jobViewParam,
                queryLimitParam,
                offsetParam,
                UtilGlobal.EMPTY,
                UtilGlobal.EMPTY);
    }

    /**
     * Send a workflow item currently in an {@code Assignment} step to.
     * Collaborator user send on is not allowed.
     *
     * @param flowJobItemParam The Fluid Item to {@code "Send On"} in the workflow process.
     *
     * @return The Fluid item that was sent on.
     */
    public FluidItem sendFlowItemOn(FluidItem flowJobItemParam) {
        return this.sendFlowItemOn(flowJobItemParam, false);
    }

    /**
     * Send a workflow item currently in an {@code Assignment} step to
     *
     * @param flowJobItem The Fluid Item to {@code "Send On"} in the workflow process.
     * @param allowCollaboratorToSendOn All a collaborator user to also send on.
     *
     * @return The Fluid item that was sent on.
     */
    public FluidItem sendFlowItemOn(FluidItem flowJobItem, boolean allowCollaboratorToSendOn) {
        if (flowJobItem != null) flowJobItem.setServiceTicket(this.serviceTicket);
        try {
            return new FluidItem(this.postJson(
                    flowJobItem, WS.Path.FlowItem.Version1.sendFlowItemOn(allowCollaboratorToSendOn))
            );
        } catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Send a form item to be part of a workflow.
     *
     * @param formToSendToFlow The Form to {@code "Send To Flow (introduction)"} in the workflow process.
     * @param flow The Flow the {@code formToSendToFlowParam} must be sent to.
     *
     * @return The Fluid item that was initiated in a workflow process.
     */
    public FluidItem sendFormToFlow(Form formToSendToFlow, String flow) {
        FluidItem itemToSend = new FluidItem();
        itemToSend.setForm(formToSendToFlow);
        itemToSend.setFlow(flow);
        itemToSend.setServiceTicket(this.serviceTicket);

        try {
            return new FluidItem(this.postJson(
                itemToSend, WS.Path.FlowItem.Version1.sendFlowItemToFlow())
            );
        } catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Remove a {@code FluidItem} from the workflow.
     *
     * @param fluidItem The Form to {@code "Send To Flow (introduction)"} in the workflow process.
     *
     * @return The Fluid item that was removed from the workflow process.
     *
     * @see FluidItem
     */
    public FluidItem removeFromFlow(FluidItem fluidItem) {
        if (fluidItem != null) fluidItem.setServiceTicket(this.serviceTicket);

        try {
            return new FluidItem(this.postJson(
                fluidItem, WS.Path.FlowItem.Version1.removeFluidItemFromFlow())
            );
        } catch (JSONException e) {
            throw new FluidClientException(e.getMessage(), e,
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
