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
