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

package com.fluid.ws.client.v1.config;

import org.json.JSONObject;

import com.fluid.program.api.vo.config.Configuration;
import com.fluid.program.api.vo.config.ConfigurationListing;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow's / Workflows.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.Configuration
 * @see com.fluid.program.api.vo.config.Configuration
 * @see ABaseClientWS
 */
public class ConfigurationClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public ConfigurationClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Retrieves a Configuration by Key.
     *
     * @param configurationKeyParam The Flow key.
     * @return The Configuration.
     */
    public Configuration getConfigurationByKey(String configurationKeyParam)
    {
        Configuration configuration = new Configuration();

        configuration.setKey(configurationKeyParam);

        if(this.serviceTicket != null)
        {
            configuration.setServiceTicket(this.serviceTicket);
        }

        return new Configuration(this.postJson(
                configuration, WS.Path.Configuration.Version1.getByKey()));
    }

    /**
     * Retrieves all Configurations.
     *
     * @return All the Configurations.
     */
    public ConfigurationListing getAllConfigurations()
    {
        Configuration configuration = new Configuration();

        if(this.serviceTicket != null)
        {
            configuration.setServiceTicket(this.serviceTicket);
        }

        return new ConfigurationListing(this.postJson(
                configuration, WS.Path.Configuration.Version1.getAllConfigurations()));
    }
}
