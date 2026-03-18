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
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifier;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibraryTaskIdentifierListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;

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
 * @see JsonObject
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
	 * @param configurationKey The Flow key.
	 * @return The Configuration.
	 */
	public Configuration getConfigurationByKey(String configurationKey) {
		Configuration configuration = new Configuration();
		configuration.setKey(configurationKey);
		configuration.setServiceTicket(this.serviceTicket);

		return new Configuration(this.postJson(
				configuration, WS.Path.Configuration.Version1.getByKey())
		);
	}


	/**
	 * Retrieves the company logo configuration.
	 *
	 * @return A {@code Configuration} object representing the company logo.
	 */
	public Configuration getCompanyLogo() {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		return new Configuration(this.postJson(configuration, WS.Path.Configuration.Version1.getCompanyLogo()));
	}

	/**
	 * Retrieves the small version of the company logo configuration.
	 * This method constructs a {@code Configuration} object, assigns
	 * the current service ticket to it, and sends a request to fetch
	 * the small company logo settings from the remote service.
	 *
	 * @return A {@code Configuration} object containing the small company logo configuration.
	 */
	public Configuration getCompanyLogoSmall() {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		return new Configuration(this.postJson(configuration, WS.Path.Configuration.Version1.getCompanyLogoSmall()));
	}

	/**
	 * Loads a small version of the company logo by encoding the provided logo data and
	 * creating a Configuration object. The service ticket is included in the configuration,
	 * and the small company logo is updated via a call to the appropriate web service endpoint.
	 *
	 * @param logoData The raw byte array representing the company logo to be loaded.
	 * @return A {@code Configuration} object representing the updated configuration for the small company logo.
	 */
	public Configuration loadCompanyLogoSmall(byte[] logoData) {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		configuration.setValue(BaseEncoding.base64().encode(logoData));
		return new Configuration(this.putJson(configuration, WS.Path.Configuration.Version1.upsertCompanyLogoSmall()));
	}

	/**
	 * Loads a large company logo into the system by encoding the provided logo data,
	 * creating a {@code Configuration} object, and sending a request to update the
	 * large company logo configuration via the appropriate web service endpoint.
	 *
	 * @param logoData The raw byte array representing the company logo to be loaded.
	 * @return A {@code Configuration} object representing the updated configuration
	 *         for the large company logo.
	 */
	public Configuration loadCompanyLogo(byte[] logoData) {
		Configuration configuration = new Configuration();
		configuration.setServiceTicket(this.serviceTicket);
		configuration.setValue(BaseEncoding.base64().encode(logoData));
		return new Configuration(this.putJson(configuration, WS.Path.Configuration.Version1.upsertCompanyLogoLarge()));
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
	 * Inserts a third party library if it doesn't exist,
	 * update a configuration if it exists based on {@code id} being set.
	 *
	 * @param library The library to create or update.
	 * @return stored third party library.
	 * @see ThirdPartyLibrary
	 */
	public ThirdPartyLibrary upsertThirdPartyLibrary(ThirdPartyLibrary library) {
		return this.upsertThirdPartyLibrary(library, false);
	}

	/**
	 * Inserts a third party library if it doesn't exist,
	 * update a configuration if it exists based on {@code id} being set.
	 *
	 * @param library The library to create or update.
	 * @return stored third party library.
	 * @see ThirdPartyLibrary
	 */
	public ThirdPartyLibrary upsertThirdPartyLibrary(
			ThirdPartyLibrary library,
			boolean upsertFromExternalRunner
	) {
		library.setServiceTicket(this.serviceTicket);
		return new ThirdPartyLibrary(this.putJson(
				library, WS.Path.Configuration.Version1.thirdPartyTaskUpsert(upsertFromExternalRunner))
		);
	}

	/**
	 * Deletes a third party library.
	 *
	 * @param library The library to delete.
	 * @return deleted third party library.
	 * @see ThirdPartyLibrary
	 */
	public ThirdPartyLibrary deleteThirdPartyLibrary(
			ThirdPartyLibrary library
	) {
		library.setServiceTicket(this.serviceTicket);
		return new ThirdPartyLibrary(this.postJson(
				library, WS.Path.Configuration.Version1.thirdPartyTaskDelete())
		);
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
				third, WS.Path.Configuration.Version1.getAllThirdPartyTaskIdentifiers())
		).getListing();
	}
}
