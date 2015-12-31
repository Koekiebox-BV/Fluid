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

package com.fluid.ws.client.v1.form;

import java.util.List;

import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.FormFlowHistoricData;
import com.fluid.program.api.vo.FormFlowHistoricDataContainer;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Created by jasonbruwer on 15/01/04.
 */
public class FormContainerClient extends ABaseClientWS {

    /**
     *
     */
    public FormContainerClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FormContainerClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     *
     * @param formParam
     * @return
     */
    public Form createFormContainer(Form formParam)
    {
        if(formParam != null && this.serviceTicket != null)
        {
            formParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.putJson(
                formParam, WS.Path.FormContainer.Version1.formContainerCreate()));
    }

    /**
     *
     * @param formParam
     * @return
     */
    public List<FormFlowHistoricData> getFormContainerHistoricData(Form formParam)
    {
        if(formParam != null && this.serviceTicket != null)
        {
            formParam.setServiceTicket(this.serviceTicket);
        }

        return new FormFlowHistoricDataContainer(this.postJson(
                formParam, WS.Path.FlowItemHistory.Version1.getByFormContainer())).getFormFlowHistoricDatas();
    }

}
