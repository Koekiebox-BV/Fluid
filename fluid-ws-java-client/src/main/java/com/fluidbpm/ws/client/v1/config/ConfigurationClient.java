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

package com.fluidbpm.ws.client.v1.config;

import com.fluidbpm.program.api.vo.config.Configuration;
import com.fluidbpm.program.api.vo.config.ConfigurationListing;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifier;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifierListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONObject;

import java.util.List;

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
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Configuration
 * @see com.fluidbpm.program.api.vo.config.Configuration
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
		String serviceTicketParam
	) {
		super(endpointBaseUrlParam);
		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Retrieves a Configuration by Key.
	 *
	 * @param configurationKeyParam The Flow key.
	 * @return The Configuration.
	 */
	public Configuration getConfigurationByKey(String configurationKeyParam) {
		Configuration configuration = new Configuration();
		configuration.setKey(configurationKeyParam);
		if (this.serviceTicket != null) {
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
	public ConfigurationListing getAllConfigurations() {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		return new ConfigurationListing(this.postJson(
				configuration, WS.Path.Configuration.Version1.getAllConfigurations()));
	}

	/**
	 * Retrieves all basic Configurations.
	 *
	 * @return All the Configurations related to basic information.
	 */
	public ConfigurationListing getAllBasicConfigurations() {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		return new ConfigurationListing(this.postJson(
				configuration, WS.Path.Configuration.Version1.getAllBasicConfigurations()));
	}

	/**
	 * Inserts a configuration if it doesn't exists, update a configuration if it exists based on {@code configName}.
	 *
	 * @param configName The name of the config to update.
	 * @param configValue The new configuration value.
	 * @return Updated configuration.
	 * @see Configuration
	 */
	public Configuration upsertConfiguration(
		String configName,
		String configValue
	) {
		Configuration configuration = new Configuration(configName, configValue);
		configuration.setServiceTicket(this.serviceTicket);
		return new Configuration(this.postJson(
				configuration, WS.Path.Configuration.Version1.configUpsert()));
	}

	/**
	 * Retrieve the description of the SMTP config for system related SMTP's.
	 * @return SMTP Description of System mails.
	 */
	public String getSystemMailTransfer() {
		Configuration configuration = new Configuration();
		if (this.serviceTicket != null) {
			configuration.setServiceTicket(this.serviceTicket);
		}
		return new Configuration(this.postJson(
				configuration, WS.Path.Configuration.Version1.getSystemMailTransfer())).getValue();
	}

	/**
	 * Retrieve the description of the SMTP config for system related SMTP's.
	 * @return SMTP Description of System mails.
	 */
	public List<ThirdPartyLibraryTaskIdentifier> getAllThirdPartyTaskIdentifiers() {
		ThirdPartyLibraryTaskIdentifier third = new ThirdPartyLibraryTaskIdentifier();
		third.setServiceTicket(this.serviceTicket);

		return new ThirdPartyLibraryTaskIdentifierListing(this.postJson(
				third, WS.Path.Configuration.Version1.getAllThirdPartyTaskIdentifiers())).getListing();
	}
}
