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

import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Created by jasonbruwer on 15/01/04.
 */
public class FormDefinitionClient extends ABaseClientWS {

    /**
     *
     */
    public FormDefinitionClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FormDefinitionClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     *
     * @param formDefinitionParam
     * @return
     */
    public Form createFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.putJson(
                formDefinitionParam, WS.Path.FormDefinition.Version1.formDefinitionCreate()));
    }

    /**
     *
     * @param formDefinitionParam
     * @return
     */
    public Form updateFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                formDefinitionParam,
                WS.Path.FormDefinition.Version1.formDefinitionUpdate()));
    }

    /**
     *
     * @param formDefinitionIdParam
     * @return
     */
    public Form getFormDefinitionById(Long formDefinitionIdParam)
    {
        Form form = new Form(formDefinitionIdParam);

        if(this.serviceTicket != null)
        {
            form.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                form, WS.Path.FormDefinition.Version1.getById()));
    }

    /**
     *
     * @param formDefinitionParam
     * @return
     */
    public Form deleteFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(formDefinitionParam,
                WS.Path.FormDefinition.Version1.formDefinitionDelete()));
    }

}
