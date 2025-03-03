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

import com.fluidbpm.program.api.vo.flow.*;
import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONObject;

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
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FlowStep
 * @see FlowStep
 * @see ABaseClientWS
 */
public class FlowStepClient extends ABaseClientWS {

	/**
	 * The View types.
	 */
	public static class ViewType {
		public static final String STANDARD = "Standard";
		public static final String ERROR = "Error";
	}

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public FlowStepClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);

		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Creates a new Flow Step.
	 *
	 * @param flowStep The step to create.
	 * @return The created step.
	 */
	public FlowStep createFlowStep(FlowStep flowStep) {
		if (flowStep != null) flowStep.setServiceTicket(this.serviceTicket);
		return new FlowStep(this.putJson(flowStep, WS.Path.FlowStep.Version1.flowStepCreate()));
	}

	/**
	 * Updates an existing Flow Step.
	 *
	 * @param flowStep The updated Flow Step values.
	 * @return The updated Step.
	 */
	public FlowStep updateFlowStep(FlowStep flowStep) {
		if (flowStep != null) flowStep.setServiceTicket(this.serviceTicket);
		return new FlowStep(this.postJson(flowStep, WS.Path.FlowStep.Version1.flowStepUpdate()));
	}

	/**
	 * Retrieves an existing Flow Step via Primary key.
	 *
	 * @param flowStepId The Flow Step Primary Key.
	 * @param flowStepType The type of step.
	 * @return The Step retrieved by Primary key.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 */
	public FlowStep getFlowStepById(Long flowStepId, String flowStepType) {
		FlowStep flowStep = new FlowStep(flowStepId);
		flowStep.setFlowStepType(flowStepType);
		flowStep.setServiceTicket(this.serviceTicket);

		return new FlowStep(this.postJson(flowStep, WS.Path.FlowStep.Version1.getById()));
	}

	/**
	 * Retrieves an existing Flow Step via Step.
	 *
	 * Lookup will include id, then;
	 * Name.
	 *
	 * @param flowStepParam The Flow Step to use as lookup.
	 * @return The Step retrieved by provided {@code flowStepParam}.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 */
	public FlowStep getFlowStepByStep(FlowStep flowStepParam) {
		if (this.serviceTicket != null && flowStepParam != null) {
			flowStepParam.setServiceTicket(this.serviceTicket);
		}

		return new FlowStep(this.postJson(flowStepParam, WS.Path.FlowStep.Version1.getByStep()));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * via Flow Step Primary key.
	 *
	 * @param flowStepIdParam The Flow Step Primary Key.
	 * @return The Step retrieved by Primary key.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 */
	public JobViewListing getJobViewsByStepId(Long flowStepIdParam) {
		return this.getJobViewsByStep(new FlowStep(flowStepIdParam));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * via Flow Step Name key.
	 *
	 * @param flowStepNameParam The Flow Step Name.
	 * @param flowParam The Flow.
	 * @return The Step retrieved by Primary key.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 */
	public JobViewListing getJobViewsByStepName(
		String flowStepNameParam, Flow flowParam
	) {
		FlowStep step = new FlowStep();
		step.setName(flowStepNameParam);
		step.setFlow(flowParam);
		return this.getJobViewsByStep(step);
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * via Flow Step Name key.
	 *
	 * @param flowNameParam The Flow Name.
	 * @param flowStepNameParam The Flow Step Name.
	 * @param flowViewNameParam The Step View Name.
	 *
	 * @return The View that matches the credentials.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 * @see JobView
	 */
	public JobView getStandardJobViewBy(
		String flowNameParam,
		String flowStepNameParam,
		String flowViewNameParam
	) {
		if (flowNameParam == null ||
				flowNameParam.trim().isEmpty()) {
			throw new FluidClientException(
					"Flow name not provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (flowStepNameParam == null ||
				flowStepNameParam.trim().isEmpty()) {
			throw new FluidClientException(
					"Step name not provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (flowViewNameParam == null ||
				flowViewNameParam.trim().isEmpty()) {
			throw new FluidClientException(
					"View name not provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		JobViewListing jobViewListing = this.getJobViewsByStepName(
				flowStepNameParam, new Flow(flowNameParam));

		JobView returnVal = null;
		if (jobViewListing.getListingCount().intValue() > 1) {
			for (JobView jobView : jobViewListing.getListing()) {
				if (ViewType.STANDARD.equals(jobView.getViewType()) &&
						jobView.getViewName().equalsIgnoreCase(flowViewNameParam)) {
					returnVal = jobView;
					break;
				}
			}
		}

		if (returnVal == null) {
			throw new FluidClientException(
					"No View found for Flow '"+
							flowNameParam +"', Step '"+
							flowStepNameParam+"' and View '"+
							flowViewNameParam+"'.",
					FluidClientException.ErrorCode.NO_RESULT);
		}

		return returnVal;
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * via Flow Step Primary key.
	 *
	 * @param flowStepParam The Flow Step.
	 * @return The Step retrieved by Primary key.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep.StepType
	 */
	public JobViewListing getJobViewsByStep(FlowStep flowStepParam) {
		if (this.serviceTicket != null && flowStepParam != null) {
			flowStepParam.setServiceTicket(this.serviceTicket);
		}

		return new JobViewListing(this.postJson(flowStepParam, WS.Path.FlowStep.Version1.getAllViewsByStep()));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * via logged in {@code User}.
	 *
	 * @return The JobView's by logged in user.
	 */
	public JobViewListing getJobViewsByLoggedInUser() {
		FlowStep flowStep = new FlowStep();
		flowStep.setServiceTicket(this.serviceTicket);
		return new JobViewListing(this.postJson(flowStep, WS.Path.FlowStep.Version1.getAllViewsByLoggedInUser()));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * for {@code User}.
	 *
	 * @param userParam The {@link User} to get job views for.
	 *
	 * @return The JobView's that user {@code userParam} has access to.
	 */
	public JobViewListing getJobViewsByUser(User userParam) {
		if (this.serviceTicket != null && userParam != null) {
			userParam.setServiceTicket(this.serviceTicket);
		}
		return new JobViewListing(this.postJson(userParam, WS.Path.FlowStep.Version1.getAllViewsByUser()));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * for {@code Role}.
	 *
	 * @param roleParam The {@link Role} to get job views for.
	 *
	 * @return The JobView's that user {@code roleParam} has access to.
	 */
	public JobViewListing getJobViewsByRole(Role roleParam) {
		if (this.serviceTicket != null && roleParam != null) {
			roleParam.setServiceTicket(this.serviceTicket);
		}
		return new JobViewListing(this.postJson(roleParam, WS.Path.FlowStep.Version1.getAllViewsByRole()));
	}

	/**
	 * Retrieves all Assignment {@link com.fluidbpm.program.api.vo.flow.JobView}s
	 * for {@code Flow}.
	 *
	 * @param flowParam The flow to get {@link JobView}'s for.
	 *
	 * @return The JobView's associated with Flow {@code flowParam}.
	 */
	public JobViewListing getJobViewsByFlow(Flow flowParam) {
		if (this.serviceTicket != null && flowParam != null) {
			flowParam.setServiceTicket(this.serviceTicket);
		}
		return new JobViewListing(this.postJson(flowParam, WS.Path.FlowStep.Version1.getAllViewsByFlow()));
	}

	/**
	 * Retrieves all Steps via Flow.
	 *
	 * @param flowParam The Flow.
	 * @return The Step retrieved by Primary key.
	 *
	 * @see com.fluidbpm.program.api.vo.flow.Flow
	 * @see FlowStepListing
	 */
	public FlowStepListing getStepsByFlow(Flow flowParam) {
		if (this.serviceTicket != null && flowParam != null) {
			flowParam.setServiceTicket(this.serviceTicket);
		}
		return new FlowStepListing(this.postJson(flowParam, WS.Path.FlowStep.Version1.getAllStepsByFlow()));
	}

	/**
	 * Delete an existing Flow Step.
	 *
	 * @param flowStepParam The Flow Step to delete.
	 * @return The deleted Flow Step.
	 */
	public FlowStep deleteFlowStep(FlowStep flowStepParam) {
		if (flowStepParam != null && this.serviceTicket != null) {
			flowStepParam.setServiceTicket(this.serviceTicket);
		}

		return new FlowStep(this.postJson(flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete()));
	}

	/**
	 * Forcefully delete an existing Flow Step.
	 *
	 * Only 'admin' can forcefully delete a step.
	 *
	 * @param flowStepParam The Flow Step to delete.
	 * @return The deleted Flow Step.
	 */
	public FlowStep forceDeleteFlowStep(FlowStep flowStepParam) {
		if (flowStepParam != null && this.serviceTicket != null) {
			flowStepParam.setServiceTicket(this.serviceTicket);
		}

		return new FlowStep(this.postJson(flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete(true)));
	}
}
