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

package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import lombok.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

/**
 * Java Web Service Client for {@code SQLUtil} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see WS.Path.FlowItem
 * @see FluidItem
 */
public class SQLUtilClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public SQLUtilClient(String endpointBaseUrlParam, String serviceTicketParam) {
		super(endpointBaseUrlParam);
		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Retrieves all the Table Records (Forms) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetTableFormsFor The Fluid Form to get Table Fields for.
	 * @param includeFieldData Should Table Record (Form) Field data be included?
	 *
	 * @return The {@code formToGetTableFormsForParam} Table Records as {@code Form}'s.
	 */
	public List<Form> getTableForms(Form formToGetTableFormsFor, boolean includeFieldData) {
		return this.getTableForms(formToGetTableFormsFor, includeFieldData, Optional.empty());
	}

	/**
	 * Retrieves all the Table Records (Forms) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetTableFormsFor The Fluid Form to get Table Fields for.
	 * @param includeFieldData Should Table Record (Form) Field data be included?
	 * @param formDefinitionId Optional Form Definition Id filter.
	 *
	 * @return The {@code formToGetTableFormsForParam} Table Records as {@code Form}'s.
	 */
	public List<Form> getTableForms(
			Form formToGetTableFormsFor,
			boolean includeFieldData,
			Long formDefinitionId
	) {
		return this.getTableForms(
				formToGetTableFormsFor,
				includeFieldData,
				Optional.ofNullable(formDefinitionId)
		);
	}

	/**
	 * Retrieves all the Table Records (Forms) for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetTableFormsFor The Fluid Form to get Table Fields for.
	 * @param includeFieldData Should Table Record (Form) Field data be included?
	 * @param formDefinitionId Optional Form Definition Id filter.
	 *
	 * @return The {@code formToGetTableFormsForParam} Table Records as {@code Form}'s.
	 */
	public List<Form> getTableForms(
		Form formToGetTableFormsFor,
		boolean includeFieldData,
		@NonNull
		Optional<Long> formDefinitionId
	) {
		Form lclForm = new Form();
		lclForm.setServiceTicket(this.serviceTicket);
		if (formToGetTableFormsFor != null) lclForm.setId(formToGetTableFormsFor.getId());

		try {
			FormListing formListing = new FormListing(
					this.postJson(lclForm, WS.Path.SQLUtil.Version1.getTableForms(
							includeFieldData,
							formDefinitionId))
			);
			return formListing.getListing();
		} catch (JSONException e) {
			throw new FluidClientException(e.getMessage(), e, FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Retrieves all Descendants for the {@code formToGetTableFormsForParam}.
	 *
	 * @param formToGetDescendantsFor The Fluid Form to get Descendants for.
	 * @param includeFieldData Should Descendant (Form) Field data be included?
	 * @param includeTableFields Should Table Record (Form) Field data be included?
	 * @param inclTableFieldFormInfo Include table record field info.
	 *
	 * @return The {@code formToGetTableFormsForParam} Descendants as {@code Form}'s.
	 */
	public List<Form> getDescendants(
		Form formToGetDescendantsFor,
		boolean includeFieldData,
		boolean includeTableFields,
		boolean inclTableFieldFormInfo
	) {
		Form lclForm = new Form();
		lclForm.setServiceTicket(this.serviceTicket);
		if (formToGetDescendantsFor != null) lclForm.setId(formToGetDescendantsFor.getId());

		try {
			FormListing formListing = new FormListing(
					this.postJson(lclForm, WS.Path.SQLUtil.Version1.getDescendants(
							includeFieldData,
							includeTableFields,
							inclTableFieldFormInfo)
					)
			);
			return formListing.getListing();
		} catch (JSONException e) {
			throw new FluidClientException(e.getMessage(), e, FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Retrieves the Ancestor for the {@code formToGetAncestorForParam}.
	 *
	 * @param formToGetAncestorFor The Fluid Form to get Ancestor for.
	 * @param includeFieldData Should Ancestor (Form) Field data be included?
	 * @param includeTableFields Should Table Record (Form) Field data be included?
	 *
	 * @return The {@code formToGetAncestorForParam} Ancestor as {@code Form}'s.
	 */
	public Form getAncestor(Form formToGetAncestorFor, boolean includeFieldData, boolean includeTableFields) {
		Form lclForm = new Form();
		lclForm.setServiceTicket(this.serviceTicket);
		if (formToGetAncestorFor != null) lclForm.setId(formToGetAncestorFor.getId());

		try {
			return new Form(
					this.postJson(lclForm, WS.Path.SQLUtil.Version1.getAncestor(
							includeFieldData,
							includeTableFields)
					)
			);
		} catch (JSONException e) {
			throw new FluidClientException(e.getMessage(), e,
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Retrieves all Fields for the {@code formToGetFieldsForParam}.
	 *
	 * @param formToGetFieldsFor The Fluid Form to get Fields for.
	 * @param includeTableFields Should Table Field data be included?
	 *
	 * @return The {@code formToGetFieldsForParam} Fields as {@code Field}'s.
	 */
	public List<Field> getFormFields(Form formToGetFieldsFor, boolean includeTableFields) {
		Form lclForm = new Form();
		lclForm.setServiceTicket(this.serviceTicket);
		if (formToGetFieldsFor != null) lclForm.setId(formToGetFieldsFor.getId());

		try {
			FormFieldListing formFieldListing = new FormFieldListing(
					this.postJson(lclForm, WS.Path.SQLUtil.Version1.getFormFields(includeTableFields))
			);

			return formFieldListing.getListing();
		} catch (JSONException e) {
			throw new FluidClientException(e.getMessage(), e,
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}

	/**
	 * Executes the sql query {@code sqlQueryParam} and returns
	 * the result as {@code SQLResultSet}
	 *
	 * @param sqlQuery The native SQL query to execute.
	 *
	 * @return The ResultSet in the form of {@code SQLResultSet}.
	 *
	 * @see SQLResultSet
	 * @see NativeSQLQuery
	 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn
	 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow
	 */
	public SQLResultSet executeSQL(NativeSQLQuery sqlQuery) {
		if (sqlQuery != null) sqlQuery.setServiceTicket(this.serviceTicket);

		try {
			SQLResultSet resultSet = new SQLResultSet(
					this.postJson(sqlQuery, WS.Path.SQLUtil.Version1.getExecuteNativeSQL())
			);
			return resultSet;
		} catch (JSONException e) {
			throw new FluidClientException(e.getMessage(), e,
					FluidClientException.ErrorCode.JSON_PARSING);
		}
	}
}
