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

package com.fluidbpm.ws.client.v1.flow;

import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowListing;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewGroup;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewGroupListing;
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
 * @since v1.0
 * @version v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.Flow
 * @see Flow
 * @see ABaseClientWS
 */
public class FlowClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public FlowClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Creates a new Flow with an Introduction and Exit basic rule.
	 *
	 * @param flow The flow to Create.
	 * @return The created flow.
	 *
	 * @see Flow
	 */
	public Flow createFlow(Flow flow) {
		if (flow != null) flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.putJson(flow, WS.Path.Flow.Version1.flowCreate()));
	}

	/**
	 * Updates an existing Flow.
	 *
	 * @param flow The flow to Update.
	 * @return The updated flow.
	 *
	 * @see Flow
	 */
	public Flow updateFlow(Flow flow) {
		if (flow != null) flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.postJson(flow, WS.Path.Flow.Version1.flowUpdate()));
	}

	/**
	 * Retrieves a Flow by Primary Key.
	 *
	 * @param flowId The Flow primary key.
	 * @return The Flow.
	 */
	public Flow getFlowById(Long flowId) {
		Flow flow = new Flow(flowId);
		flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.postJson(flow, WS.Path.Flow.Version1.getById()));
	}

	/**
	 * Retrieves a Flow by unique Name.
	 *
	 * @param flowName The Flow name.
	 * @return The Flow.
	 */
	public Flow getFlowByName(String flowName) {
		Flow flow = new Flow();
		flow.setName(flowName);
		flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.postJson(flow, WS.Path.Flow.Version1.getByName()));
	}

	/**
	 * Retrieve the Flow View Group configuration.
	 * @return The complete view group config.
	 * @see WebKitViewGroupListing
	 */
	public List<WebKitViewGroup> getViewGroupsWebKit() {
		Flow flow = new Flow();
		flow.setServiceTicket(this.serviceTicket);
		return new WebKitViewGroupListing(this.postJson(flow, WS.Path.Flow.Version1.getJobViewGroupsWebKit())).getListing();
	}

	/**
	 * Retrieve the Flow View Group configuration by name.
	 * @return The view group config by name.
	 * @see WebKitViewGroupListing
	 */
	public WebKitViewGroup getViewGroupWebKitByName(String name) {
		WebKitViewGroup flow = new WebKitViewGroup();
		flow.setServiceTicket(this.serviceTicket);
		flow.setJobViewGroupName(name);
		return new WebKitViewGroup(this.postJson(flow, WS.Path.Flow.Version1.getJobViewGroupWebKitBy()));
	}

	/**
	 * Update and insert the Flow View Group configuration.
	 * @param listing The ViewGroupWebKit listing to upsert.
	 * @return The complete view group config.
	 * @see WebKitViewGroupListing
	 */
	public List<WebKitViewGroup> upsertViewGroupsWebKit(WebKitViewGroupListing listing) {
		if (listing == null) return null;

		listing.setServiceTicket(this.serviceTicket);
		return new WebKitViewGroupListing(this.postJson(listing, WS.Path.Flow.Version1.flowViewGroupsUpsert())).getListing();
	}

	/**
	 * Update and insert the Flow View Group configuration.
	 * @param wkViewGrp The ViewGroupWebKit to upsert.
	 * @return The complete view group config.
	 * @see WebKitViewGroupListing
	 */
	public WebKitViewGroup upsertViewGroupsWebKit(WebKitViewGroup wkViewGrp) {
		if (wkViewGrp == null) return null;

		wkViewGrp.setServiceTicket(this.serviceTicket);
		return new WebKitViewGroup(this.postJson(wkViewGrp, WS.Path.Flow.Version1.flowViewGroupUpsert()));
	}

	/**
	 * Retrieves all the flows.
	 *
	 * @return The Flow listing.
	 * @see FlowListing
	 */
	public List<Flow> getAllFlows() {
		Flow flow = new Flow();
		flow.setServiceTicket(this.serviceTicket);

		return new FlowListing(this.postJson(flow, WS.Path.Flow.Version1.getAllFlows())).getListing();
	}

	/**
	 * Delete an existing Flow.
	 *
	 * @param flow The Flow to delete.
	 * @return The deleted Flow.
	 */
	public Flow deleteFlow(Flow flow) {
		if (flow != null) flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.postJson(flow, WS.Path.Flow.Version1.flowDelete()));
	}

	/**
	 * Forcefully Delete an existing Flow.
	 *
	 * Only 'admin' can forcefully delete a Flow.
	 *
	 * @param flow The Flow to delete.
	 * @return The deleted Flow.
	 */
	public Flow forceDeleteFlow(Flow flow) {
		if (flow != null) flow.setServiceTicket(this.serviceTicket);

		return new Flow(this.postJson(flow, WS.Path.Flow.Version1.flowDelete(true)));
	}
}
