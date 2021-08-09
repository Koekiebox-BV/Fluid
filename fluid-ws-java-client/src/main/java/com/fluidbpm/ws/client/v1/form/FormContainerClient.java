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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricDataListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Java Web Service Client for Electronic Form related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FormContainer
 * @see Form
 */
public class FormContainerClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public FormContainerClient(
		String endpointBaseUrlParam, String serviceTicketParam
	) {
		super(endpointBaseUrlParam);
		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Create a new Form Container / Electronic Forms.
	 *
	 * @param form The Form to create.
	 * @param addToPersonalInventory Should the form be added to the users P/I after creation.
	 *
	 * @return Created Form Container / Electronic Form.
	 *
	 * @see Field
	 */
	public Form createFormContainer(
		Form form,
		boolean addToPersonalInventory
	) {
		if (form != null) form.setServiceTicket(this.serviceTicket);

		return new Form(this.putJson(
				form, WS.Path.FormContainer.Version1.formContainerCreate(addToPersonalInventory)));
	}

	/**
	 * Create a new Form Container / Electronic Forms.
	 * The Form will not be added to the P/I.
	 *
	 * @param form The Form to create.
	 * @return Created Form Container / Electronic Form.
	 *
	 * @see Field
	 */
	public Form createFormContainer(Form form) {
		return this.createFormContainer(form, false);
	}

	/**
	 * Create a new history record for a {@code Form}.
	 *
	 * @param formHistory The FormHistoricData to create.
	 * @return Created Form Historic Data.
	 *
	 * @see FormHistoricData
	 */
	public FormHistoricData createFormHistoricData(FormHistoricData formHistory) {
		if (formHistory != null) formHistory.setServiceTicket(this.serviceTicket);

		return new FormHistoricData(this.putJson(formHistory, WS.Path.FormHistory.Version1.formHistoryCreate()));
	}

	/**
	 * Create a new Table Record.
	 *
	 * @param tableRecordParam The Table Record to create.
	 * @return Created Table Record.
	 *
	 * @see TableRecord
	 */
	public TableRecord createTableRecord(TableRecord tableRecordParam) {
		if (tableRecordParam != null) tableRecordParam.setServiceTicket(this.serviceTicket);

		return new TableRecord(this.putJson(
				tableRecordParam,
				WS.Path.FormContainerTableRecord.Version1.formContainerTableRecordCreate()));
	}

	/**
	 * Update a Form Container / Electronic Form.
	 * The table record forms may also be updated with
	 *
	 * @param formParam The Form to update.
	 * @return Updated Form Container / Electronic Form.
	 *
	 * @see Field
	 * @see TableRecord
	 * @see Form
	 */
	public Form updateFormContainer(Form formParam) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		return new Form(this.postJson(formParam, WS.Path.FormContainer.Version1.formContainerUpdate()));
	}

	/**
	 * Execute the Custom Program with action alias {@code customWebActionParam}.
	 * This method may be used for Form and Table Records.
	 *
	 * @param customWebActionParam The custom web action name. Action identifier.
	 * @param formParam The Form to send for 3rd Party execution.
	 *
	 * @return Result after the 3rd Party Custom Web Action completed.
	 *
	 * @see Field
	 * @see Form
	 * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
	 */
	public CustomWebAction executeCustomWebAction(String customWebActionParam, Form formParam) {
		return this.executeCustomWebAction(
				customWebActionParam,
				false,
				null,
				formParam);
	}

	/**
	 * Execute the Custom Program with action alias {@code customWebActionParam}.
	 * This method may be used for Form and Table Records.
	 *
	 * @param customWebActionParam The custom web action name. Action identifier.
	 * @param isTableRecordParam Is the form a table record form.
	 * @param formContainerTableRecordBelongsToParam The parent form container if table record.
	 * @param formParam The Form to send for 3rd Party execution.
	 *
	 * @return Result after the 3rd Party Custom Web Action completed.
	 *
	 * @see Field
	 * @see Form
	 * @see TableRecord
	 * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
	 */
	public CustomWebAction executeCustomWebAction(
		String customWebActionParam,
		boolean isTableRecordParam,
		Long formContainerTableRecordBelongsToParam,
		Form formParam
	) {
		if (customWebActionParam == null || customWebActionParam.trim().isEmpty()) throw new FluidClientException(
				"Custom Web Action is mandatory.", FluidClientException.ErrorCode.FIELD_VALIDATE);

		CustomWebAction action = new CustomWebAction();
		action.setTaskIdentifier(customWebActionParam);
		action.setServiceTicket(this.serviceTicket);
		action.setForm(formParam);
		action.setIsTableRecord(isTableRecordParam);
		action.setFormTableRecordBelongsTo(formContainerTableRecordBelongsToParam);

		return new CustomWebAction(this.postJson(action, WS.Path.FormContainer.Version1.executeCustomWebAction()));
	}

	/**
	 * Deletes the Form Container provided.
	 * Id must be set on the Form Container.
	 *
	 * @param formContainerParam The Form Container to Delete.
	 * @return The deleted Form Container.
	 */
	public Form deleteFormContainer(Form formContainerParam) {
		if (formContainerParam != null) formContainerParam.setServiceTicket(this.serviceTicket);

		return new Form(this.postJson(formContainerParam, WS.Path.FormContainer.Version1.formContainerDelete()));
	}

	/**
	 * Retrieves Electronic Form Workflow historic information.
	 *
	 * The Form Id must be provided.
	 *
	 * @param formParam The form to retrieve historic data for.
	 * @return Electronic Form Workflow historic data.
	 */
	public List<FormFlowHistoricData> getFormFlowHistoricData(Form formParam) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		return new FormFlowHistoricDataListing(this.postJson(
				formParam, WS.Path.FlowItemHistory.Version1.getByFormContainer())).getListing();
	}

	/**
	 * Retrieves Electronic Form and Field historic information.
	 *
	 * The Form Id must be provided.
	 *
	 * @param includeCurrentParam Whether the current values should be included.
	 *
	 * @param formParam The form to retrieve historic data for.
	 * @return Electronic Form and Field historic data.
	 */
	public List<FormHistoricData> getFormAndFieldHistoricData(
		Form formParam,
		boolean includeCurrentParam
	) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		return new FormHistoricDataListing(this.postJson(
				formParam, WS.Path.FormHistory.Version1.getByFormContainer(
						includeCurrentParam))).getListing();
	}

	/**
	 * Retrieves Electronic Form and Field historic information for
	 * the most recent modification.
	 *
	 * The Form Id must be provided.
	 *
	 * @param formParam The form to retrieve historic data for.
	 *
	 * @return Electronic Form and Field historic data.
	 */
	public FormHistoricData getMostRecentFormAndFieldHistoricData(Form formParam) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		return new FormHistoricData(this.postJson(
				formParam, WS.Path.FormHistory.Version1.getByMostRecentByFormContainer()));
	}

	/**
	 * Retrieves the Form Container by Primary key.
	 *
	 * @param formContainerId The Form Container primary key.
	 * @return Form by Primary key.
	 */
	public Form getFormContainerById(Long formContainerId) {
		return this.getFormContainerById(formContainerId, false);
	}

	/**
	 * Retrieves the Form Container by Primary key.
	 *
	 * @param formContainerId The Form Container primary key.
	 * @param executeCalculatedLabels Should calculated labels be executed.
	 * @return Form by Primary key.
	 */
	public Form getFormContainerById(Long formContainerId, boolean executeCalculatedLabels) {
		Form form = new Form(formContainerId);
		form.setServiceTicket(this.serviceTicket);

		return new Form(this.postJson(
				form, WS.Path.FormContainer.Version1.getById(executeCalculatedLabels)));
	}

	/**
	 * Fetch the parent/ancestor form for {@code form}
	 *
	 * @param form The child form.
	 *
	 * @return List of forms where child is {@code form}
	 *
	 * @see Form
	 */
	public List<Form> getAncestorFor(Form form) {
		if (form != null) form.setServiceTicket(this.serviceTicket);

		return new FormListing(this.postJson(
				form, WS.Path.FormContainer.Version1.getAncestorByChild())).getListing();
	}

	/**
	 * Performs a lookup on Title only.
	 *
	 * @param titleLookupText The lookup value. Matches will be found where title contains lookup text.
	 * @param limit The max number of results.
	 * @param offset The starting offset.
	 *
	 * @return FormListing where title contains {@code titleLookupText}
	 *
	 * @see FormListing
	 */
	public FormListing getFormContainersByTitleContains(String titleLookupText, int limit, int offset) {
		Form form = new Form();
		form.setTitle(titleLookupText);
		form.setServiceTicket(this.serviceTicket);

		return new FormListing(this.postJson(
				form, WS.Path.FormContainer.Version1.getByTitleContains(limit, offset)));
	}

	/**
	 * Lock the provided form container for logged in user.
	 *
	 * @param formParam The form to lock.
	 * @param jobViewParam If retrieved from a view, the lock to view from.
	 *
	 * @return The locked form.
	 */
	public Form lockFormContainer(Form formParam, JobView jobViewParam) {
		return this.lockFormContainer(formParam, jobViewParam, null);
	}

	/**
	 * Lock the provided form container for logged in user.
	 * If {@code userToLockAsParam} is provided and valid, that user will be used instead.
	 *
	 * @param formParam The form to lock.
	 * @param jobViewParam If retrieved from a view, the lock to view from.
	 * @param userToLockAsParam The form will be locked as this user.
	 *                          The logged in user must have permission to perform this action.
	 *
	 * @return The locked form.
	 */
	public Form lockFormContainer(Form formParam, JobView jobViewParam, User userToLockAsParam) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		Long jobViewId = (jobViewParam == null) ? null : jobViewParam.getId();
		Long lockAsUserId = (userToLockAsParam == null) ? null : userToLockAsParam.getId();

		try {
			return new Form(this.postJson(
					formParam,
					WS.Path.FormContainer.Version1.lockFormContainer(
							jobViewId, lockAsUserId)));
		} catch (JSONException jsonExcept) {
			throw new FluidClientException(jsonExcept.getMessage(),
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Unlock the provided form container from the logged in user.
	 * Item will not be removed from users Personal Inventory.
	 *
	 * @param formParam The form to unlock.
	 *
	 * @return The un-locked form.
	 */
	public Form unLockFormContainer(Form formParam) {
		return this.unLockFormContainer(formParam, null,true,false);
	}

	/**
	 * Unlock the provided form container from the logged in user.
	 * Item will not be removed from users Personal Inventory.
	 *
	 * @param formParam The form to unlock.
	 * @param unlockAsyncParam Should the unlock be performed asynchronous.
	 *
	 * @return The un-locked form.
	 */
	public Form unLockFormContainer(Form formParam, boolean unlockAsyncParam) {
		return this.unLockFormContainer(formParam, null, unlockAsyncParam, false);
	}

	/**
	 * Unlock the provided form container from the logged in user.
	 * The unlock will be performed asynchronous.
	 * Item will not be removed from users Personal Inventory.
	 *
	 * @param formParam The form to unlock.
	 * @param userToUnLockAsParam The form will be un-locked as this user.
	 *                          The logged in user must have permission to perform this action.
	 *
	 * @return The un-locked form.
	 */
	public Form unLockFormContainer(Form formParam, User userToUnLockAsParam) {
		return this.unLockFormContainer(formParam, userToUnLockAsParam, true, false);
	}

	/**
	 * Unlock the provided form container from the logged in user.
	 * The unlock will be performed asynchronous.
	 * Item will not be removed from users Personal Inventory.
	 *
	 * @param formParam The form to unlock.
	 * @param userToUnLockAsParam The form will be un-locked as this user.
	 *                          The logged in user must have permission to perform this action.
	 * @param unlockAsyncParam Should the unlock be performed asynchronous.
	 *
	 * @return The un-locked form.
	 */
	public Form unLockFormContainer(
		Form formParam,
		User userToUnLockAsParam,
		boolean unlockAsyncParam
	) {
		return this.unLockFormContainer(
				formParam,
				userToUnLockAsParam,
				unlockAsyncParam,
				false);
	}

	/**
	 * Unlock the provided form container from the logged in user.
	 *
	 * @param formParam The form to unlock.
	 * @param userToUnLockAsParam The form will be un-locked as this user.
	 *                          The logged in user must have permission to perform this action.
	 * @param unlockAsyncParam Should the unlock be performed asynchronous.
	 * @param removeFromPersonalInventoryParam Remove from Personal Inventory when unlocked.
	 *
	 * @return The un-locked form.
	 */
	public Form unLockFormContainer(
		Form formParam,
		User userToUnLockAsParam,
		boolean unlockAsyncParam,
		boolean removeFromPersonalInventoryParam
	) {
		if (formParam != null) formParam.setServiceTicket(this.serviceTicket);

		Long unLockAsUserId = (userToUnLockAsParam == null) ? null : userToUnLockAsParam.getId();

		try {
			return new Form(this.postJson(
					formParam,
					WS.Path.FormContainer.Version1.unLockFormContainer(
							unLockAsUserId,
							unlockAsyncParam,
							removeFromPersonalInventoryParam)));
		} catch (JSONException jsonExcept) {
			throw new FluidClientException(jsonExcept.getMessage(),
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Create a PDF version of the attachment for form {@code formToPrint}
	 * @param formToPrint The form to print.
	 * @param includeCompanyLogo Should the company logo be included.
	 * @param includeAncestor Should the ancestor form be included.
	 * @param includeDescendants Should the descendant form/s be included.
	 * @param includeFormProperties Should the form properties be included.
	 *
	 * @return Attachment with PDF content.
	 *
	 * @see Attachment
	 */
	public Attachment printForm(
		Form formToPrint,
		boolean includeCompanyLogo,
		boolean includeAncestor,
		boolean includeDescendants,
		boolean includeFormProperties
	) {
		formToPrint.setServiceTicket(this.serviceTicket);
		try {
			return new Attachment(this.postJson(
					formToPrint,
					WS.Path.FormContainer.Version1.printAsPDFAttachment(
							includeCompanyLogo,
							includeAncestor,
							includeDescendants,
							includeFormProperties)));
		} catch (JSONException jsonExcept) {
			throw new FluidClientException(jsonExcept.getMessage(),
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}
}