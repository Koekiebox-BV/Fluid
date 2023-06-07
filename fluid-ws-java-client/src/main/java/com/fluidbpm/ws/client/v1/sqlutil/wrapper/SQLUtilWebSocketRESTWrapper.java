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

package com.fluidbpm.ws.client.v1.sqlutil.wrapper;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.WebSocketGetFormHistoryByFormClient;
import com.fluidbpm.ws.client.v1.sqlutil.*;
import com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper class used for when WebSockets is not a
 * possibility, the wrapper will fallback to REST.
 *
 * @author jasonbruwer on 3/20/18.
 * @since 1.8
 */
public class SQLUtilWebSocketRESTWrapper extends ABaseClientWS implements Closeable {
	private final String baseURL;
	private final User loggedInUser;

	private final long timeoutMillis;

	//Clients...
	private SQLUtilWebSocketGetAncestorClient getAncestorClient = null;
	private SQLUtilWebSocketGetDescendantsClient getDescendantsClient = null;
	private SQLUtilWebSocketGetTableFormsClient getTableFormsClient = null;
	private SQLUtilWebSocketGetFormFieldsClient getFormFieldsClient = null;
	private WebSocketGetFormHistoryByFormClient getFormHistoryByFormClient = null;

	private SQLUtilWebSocketExecuteNativeSQLClient sqlUtilWebSocketExecNativeClient = null;

	private final SQLUtilClient sqlUtilClient;
	private final FormContainerClient fcClient;

	public static boolean COMPRESS_RSP = true;
	public static String COMPRESS_RSP_CHARSET = UtilGlobal.EMPTY;

	//Mode...
	private Mode mode = null;

	public static boolean DISABLE_WS;

	/**
	 * Aliases for properties used by the {@code SQLUtilWebSocketRESTWrapper}.
	 */
	private static class PropName {
		/**
		 * Keep Web Sockets disabled.
		 */
		public static final String FLUID_API_DISABLE_WEB_SOCKETS = "FluidAPIDisableWebSocket";
	}

	//Assign the property whether ti dis
	static {
		try {
			DISABLE_WS = Boolean.parseBoolean(System.getProperty(
					PropName.FLUID_API_DISABLE_WEB_SOCKETS,
					String.valueOf(false)).trim()
			);
		} catch (NumberFormatException nfe) {
			DISABLE_WS = false;
		}
	}

	/**
	 * The mode.
	 */
	private enum Mode {
		WebSocketActive,
		RESTfulActive
	}

	/**
	 * New wrapper instance.
	 *
	 * @param baseURL Example {@code https://[instance].fluidbpm.com/fluid-ws/}
	 * @param serviceTicket The service ticket for the logged in user.
	 * @param timeoutMillis The timeout of the request in millis.
	 */
	public SQLUtilWebSocketRESTWrapper(String baseURL, String serviceTicket, long timeoutMillis) {
		super(baseURL, serviceTicket);
		this.baseURL = baseURL;

		this.loggedInUser = new User();
		this.loggedInUser.setServiceTicket(serviceTicket);
		this.timeoutMillis = timeoutMillis;

		this.sqlUtilClient = new SQLUtilClient(this.baseURL, this.loggedInUser.getServiceTicket());
		this.fcClient = new FormContainerClient(this.baseURL, this.loggedInUser.getServiceTicket());
	}

	/**
	 * New wrapper instance.
	 *
	 * @param baseURL Example {@code https://[instance].fluidbpm.com/fluid-ws/}
	 * @param loggedInUser The currently logged in user.
	 * @param timeoutMillis The timeout of the request in millis.
	 */
	public SQLUtilWebSocketRESTWrapper(String baseURL, User loggedInUser, long timeoutMillis) {
		super(baseURL, loggedInUser == null ? null : loggedInUser.getServiceTicket());
		this.baseURL = baseURL;
		this.loggedInUser = loggedInUser;
		this.timeoutMillis = timeoutMillis;

		this.sqlUtilClient = new SQLUtilClient(this.baseURL, this.loggedInUser.getServiceTicket());
		this.fcClient = new FormContainerClient(this.baseURL, this.loggedInUser.getServiceTicket());
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
		this.clearClientsIfRest();

		//ANCESTOR...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getAncestorClient == null && Mode.RESTfulActive != this.mode) {
				this.getAncestorClient = new SQLUtilWebSocketGetAncestorClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldData,
						includeTableFields,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		Form formToUse = (formToGetAncestorFor == null) ? null: new Form(formToGetAncestorFor.getId());
		return (this.getAncestorClient == null) ?
				this.sqlUtilClient.getAncestor(formToUse, includeFieldData, includeTableFields) :
				this.getAncestorClient.getAncestorSynchronized(formToUse);
	}

	/**
	 * Retrieves all the Descendants (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Ancestor (Form) Field data be included?
	 * @param includeTableFields Should Table Record (Form) Field data be included?
	 * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
	 * @param massFetch Is the fetch a large fetch.
	 * @param formsToGetDescFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getDescendants(
			boolean includeFieldData,
			boolean includeTableFields,
			boolean includeTableFieldFormRecordInfo,
			boolean massFetch,
			Form ... formsToGetDescFor
	) {
		this.clearClientsIfRest();
		
		//DESCENDANTS...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getDescendantsClient == null && Mode.RESTfulActive != this.mode) {
				this.getDescendantsClient = new SQLUtilWebSocketGetDescendantsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldData,
						includeTableFields,
						includeTableFieldFormRecordInfo,
						massFetch,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if (formsToGetDescFor == null || formsToGetDescFor.length < 1) return null;

		Form[] formsToFetchFor =
				new Form[formsToGetDescFor.length];
		for (int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetDescFor[index].getId());
		}

		if (this.getDescendantsClient != null) {
			return this.getDescendantsClient.getDescendantsSynchronized(formsToFetchFor);
		} else {
			List<FormListing> returnVal = new ArrayList<>();
			for (Form formToFetchFor : formsToFetchFor) {
				List<Form> listOfForms = this.sqlUtilClient.getDescendants(
						formToFetchFor, includeFieldData, includeTableFields, includeTableFieldFormRecordInfo);
				FormListing toAdd = new FormListing();
				toAdd.setListing(listOfForms);
				toAdd.setListingCount((listOfForms == null) ? 0 : listOfForms.size());
				returnVal.add(toAdd);
			}
			return returnVal;
		}
	}

	/**
	 * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetTableFormsFor} as {@code Form}'s.
	 */
	public List<FormListing> getTableForms(boolean includeFieldData, List<Form> formsToGetTableFormsFor) {
		if (formsToGetTableFormsFor == null) return null;
		return this.getTableForms(includeFieldData, null, formsToGetTableFormsFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getTableForms(boolean includeFieldData, Form ... formsToGetTableFormsFor) {
		return this.getTableForms(includeFieldData, null, formsToGetTableFormsFor);
	}

	/**
	 * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formDefFilter The filter for form definitions.
	 * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getTableForms(boolean includeFieldData, Long formDefFilter, List<Form> formsToGetTableFormsFor) {
		if (formsToGetTableFormsFor == null) return null;
		return this.getTableForms(includeFieldData, formDefFilter, formsToGetTableFormsFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formDefFilter The filter for form definitions.
	 * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getTableForms(boolean includeFieldData, Long formDefFilter, Form ... formsToGetTableFormsFor) {
		this.clearClientsIfRest();

		//DESCENDANTS...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getTableFormsClient == null && Mode.RESTfulActive != this.mode) {
				this.getTableFormsClient = new SQLUtilWebSocketGetTableFormsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldData,
						formDefFilter,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if (formsToGetTableFormsFor == null || formsToGetTableFormsFor.length < 1) return null;

		Form[] formsToFetchFor = new Form[formsToGetTableFormsFor.length];
		for (int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetTableFormsFor[index].getId());
		}

		if (this.getTableFormsClient != null) {
			return this.getTableFormsClient.getTableFormsSynchronized(formsToFetchFor);
		} else {
			List<FormListing> returnVal = new ArrayList<>();
			for (Form formToFetchFor : formsToFetchFor) {
				List<Form> listOfForms = this.sqlUtilClient.getTableForms(
					formToFetchFor,
					includeFieldData,
					Optional.ofNullable(formDefFilter)
				);
				FormListing toAdd = new FormListing();
				toAdd.setListing(listOfForms);
				toAdd.setListingCount((listOfForms == null) ? 0 : listOfForms.size());
				returnVal.add(toAdd);
			}

			return returnVal;
		}
	}

	/**
	 * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeTableFieldData Should Table Record Field data be included?
	 * @param formsToGetFieldsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetFieldsForParam} Fields as {@code Field}'s.
	 */
	public List<FormFieldListing> getFormFields(boolean includeTableFieldData, List<Form> formsToGetFieldsFor) {
		if (formsToGetFieldsFor == null) return null;
		return this.getFormFields(includeTableFieldData, formsToGetFieldsFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeTableFieldData Should Table Record Field data be included?
	 * @param formsToGetFieldsFor The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetFieldsForParam} Fields as {@code Field}'s.
	 */
	public List<FormFieldListing> getFormFields(boolean includeTableFieldData, Form ... formsToGetFieldsFor) {
		this.clearClientsIfRest();

		//FORM FIELDS...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getFormFieldsClient == null && Mode.RESTfulActive != this.mode) {
				this.getFormFieldsClient = new SQLUtilWebSocketGetFormFieldsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeTableFieldData,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() != FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if (formsToGetFieldsFor == null || formsToGetFieldsFor.length < 1) return null;

		Form[] formsToFetchFor =
				new Form[formsToGetFieldsFor.length];
		for (int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetFieldsFor[index].getId());
		}

		if (this.getFormFieldsClient != null) {
			return this.getFormFieldsClient.getFormFieldsSynchronized(formsToFetchFor);
		} else {
			List<FormFieldListing> returnVal = new ArrayList<>();
			for (Form formToFetchFor : formsToFetchFor) {
				List<Field> listOfFields = this.sqlUtilClient.getFormFields(formToFetchFor, includeTableFieldData);
				FormFieldListing toAdd = new FormFieldListing();
				toAdd.setListing(listOfFields);
				toAdd.setListingCount((listOfFields == null) ? 0 : listOfFields.size());
				returnVal.add(toAdd);
			}
			return returnVal;
		}
	}

	/**
	 * Retrieves all the (FormHistoricData) for the {@code formsToGetFieldsFor}.
	 *
	 * @param includeCurrent Include the current field values.
	 * @param labelFieldName Make use of label field names.
	 * @param formsToGetHistoryFor The Fluid Form to get History for.
	 *
	 * @return The {@code formsToGetHistoryFor} Historic Data as {@code FormHistoricData}'s.
	 */
	public List<FormHistoricDataListing> getFormHistoryByForm(
			boolean includeCurrent,
			boolean labelFieldName,
			List<Form> formsToGetHistoryFor
	) {
		if (formsToGetHistoryFor == null) return null;
		return this.getFormHistoryByForm(includeCurrent, labelFieldName, formsToGetHistoryFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the (FormHistoricData) for the {@code formsToGetFieldsFor}.
	 *
	 * @param includeCurrent Include the current field values.
	 * @param labelFieldName Make use of label field names.
	 * @param formsToGetHistoryFor The Fluid Form to get History for.
	 *
	 * @return The {@code formsToGetHistoryFor} Historic Data as {@code FormHistoricData}'s.
	 */
	public List<FormHistoricDataListing> getFormHistoryByForm(
			boolean includeCurrent,
			boolean labelFieldName,
			Form ... formsToGetHistoryFor
	) {
		this.clearClientsIfRest();

		//FORM FIELDS...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getFormHistoryByFormClient == null && Mode.RESTfulActive != this.mode) {
				this.getFormHistoryByFormClient = new WebSocketGetFormHistoryByFormClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeCurrent,
						labelFieldName
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() != FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if (formsToGetHistoryFor == null || formsToGetHistoryFor.length < 1) return null;

		Form[] formsToFetchFor = new Form[formsToGetHistoryFor.length];
		for (int index = 0;index < formsToFetchFor.length; index++) {
			formsToFetchFor[index] = new Form(formsToGetHistoryFor[index].getId());
		}

		if (this.getFormHistoryByFormClient != null) {
			return this.getFormHistoryByFormClient.getByFormSynchronized(formsToFetchFor);
		} else {
			List<FormHistoricDataListing> returnVal = new ArrayList<>();
			for (Form formToFetchFor : formsToFetchFor) {
				List<FormHistoricData> listOfFields = this.fcClient.getFormAndFieldHistoricData(
						formToFetchFor,
						includeCurrent,
						labelFieldName
				);
				FormHistoricDataListing toAdd = new FormHistoricDataListing();
				toAdd.setListing(listOfFields);
				toAdd.setListingCount((listOfFields == null) ? 0 : listOfFields.size());
				returnVal.add(toAdd);
			}
			return returnVal;
		}
	}


	/**
	 * Executes all the sql queries {@code nativeSQLQueriesParam}.
	 *
	 * @param nativeSQLQueries The queries to execute.
	 *
	 * @return Each fo the ResultSets for {@code nativeSQLQueriesParam}.
	 *
	 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet
	 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn
	 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow
	 */
	public List<SQLResultSet> executeNativeSQL(NativeSQLQuery ... nativeSQLQueries) {
		//NATIVE SQL QUERIES...
		try {
			//When mode is null or [WebSocketActive]...
			this.initGetNativeSQLClient();
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() != FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) throw clientExcept;
			this.mode = Mode.RESTfulActive;
		}

		if (nativeSQLQueries == null || nativeSQLQueries.length < 1) return null;

		if (this.sqlUtilWebSocketExecNativeClient != null) {
			return this.sqlUtilWebSocketExecNativeClient.executeNativeSQLSynchronized(nativeSQLQueries);
		} else {
			List<SQLResultSet> returnVal = new ArrayList<>();
			for (NativeSQLQuery sqlToExec : nativeSQLQueries) {
				SQLResultSet resultSet = this.sqlUtilClient.executeSQL(sqlToExec);
				returnVal.add(resultSet);
			}
			return returnVal;
		}
	}

	/**
	 * Init and retrieve {@code SQLUtilWebSocketExecuteNativeSQLClient}.
	 * @return local instance of {@code SQLUtilWebSocketExecuteNativeSQLClient}
	 * @see SQLUtilWebSocketExecuteNativeSQLClient
	 */
	public SQLUtilWebSocketExecuteNativeSQLClient initGetNativeSQLClient() {
		this.clearClientsIfRest();

		if (this.sqlUtilWebSocketExecNativeClient == null && Mode.RESTfulActive != this.mode) {
			this.sqlUtilWebSocketExecNativeClient = new SQLUtilWebSocketExecuteNativeSQLClient(
					this.baseURL,
					null,
					this.loggedInUser.getServiceTicketAsHexUpper(),
					this.timeoutMillis,
					COMPRESS_RSP,
					COMPRESS_RSP_CHARSET
			);
			this.mode = Mode.WebSocketActive;
		}
		return this.sqlUtilWebSocketExecNativeClient;
	}

	/**
	 * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formsToPopulateFormFieldsFor The Fluid Form to get Descendants for.
	 */
	public void massPopulateFormFields(boolean includeFieldData, List<Form> formsToPopulateFormFieldsFor) {
		if (formsToPopulateFormFieldsFor == null) return;
		this.massPopulateFormFields(includeFieldData, formsToPopulateFormFieldsFor.toArray(new Form[]{}));
	}

	/**
	 * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldData Should Field data be included?
	 * @param formsToPopulateFormFieldsFor The Fluid Form to get Descendants for.
	 */
	public void massPopulateFormFields(boolean includeFieldData, Form ... formsToPopulateFormFieldsFor) {
		this.clearClientsIfRest();

		//FORM FIELDS...
		try {
			//When mode is null or [WebSocketActive]...
			if (this.getFormFieldsClient == null && Mode.RESTfulActive != this.mode) {
				this.getFormFieldsClient = new SQLUtilWebSocketGetFormFieldsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldData,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET
				);
				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if (clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		//Nothing to do...
		if (formsToPopulateFormFieldsFor == null || formsToPopulateFormFieldsFor.length < 1) return;

		//Populate a known echo for all of the local form caches...
		Form[] formsToFetchForLocalCacheArr = new Form[formsToPopulateFormFieldsFor.length];
		for (int index = 0; index < formsToFetchForLocalCacheArr.length; index++) {
			formsToFetchForLocalCacheArr[index] = new Form(formsToPopulateFormFieldsFor[index].getId());
			formsToFetchForLocalCacheArr[index].setEcho(UtilGlobal.randomUUID());
		}

		List<FormFieldListing> listingReturnFieldValsPopulated = new ArrayList<>();
		// Fetch all the values in a single go:
		if (this.getFormFieldsClient != null) {
			listingReturnFieldValsPopulated =
					this.getFormFieldsClient.getFormFieldsSynchronized(formsToFetchForLocalCacheArr);
		} else {
			// Old Rest way of fetching all the values:
			for (Form formToFetchFor : formsToFetchForLocalCacheArr) {
				List<Field> listOfFields = this.sqlUtilClient.getFormFields(formToFetchFor, includeFieldData);
				FormFieldListing toAdd = new FormFieldListing();
				toAdd.setListing(listOfFields);
				toAdd.setListingCount((listOfFields == null) ? 0 : listOfFields.size());
				toAdd.setEcho(formToFetchFor.getEcho());
				listingReturnFieldValsPopulated.add(toAdd);
			}
		}

		// Populate each of the form from the param:
		for (Form formToSetFieldsOn : formsToPopulateFormFieldsFor) {
			formToSetFieldsOn.setFormFields(
					this.getFieldValuesForFormFromCache(
							formToSetFieldsOn.getId(),
							listingReturnFieldValsPopulated,
							formsToFetchForLocalCacheArr
					)
			);
		}
	}

	/**
	 * Populate the field values from the cache.
	 *
	 * @param formId The id of the form to populate.
	 * @param listingReturnFieldsPopulated The cache of field values.
	 * @param formsToFetchForLocalCacheArr The forms that contain the ids and echos.
	 *
	 * @return The field values for form with id {@code formIdParam}.
	 */
	private List<Field> getFieldValuesForFormFromCache(
			Long formId,
			List<FormFieldListing> listingReturnFieldsPopulated,
			Form[] formsToFetchForLocalCacheArr
	) {
		if (formId == null || formId.longValue() < 1) return null;
		if (listingReturnFieldsPopulated == null || listingReturnFieldsPopulated.isEmpty()) return null;
		if (formsToFetchForLocalCacheArr == null || formsToFetchForLocalCacheArr.length == 0) return null;

		for (Form formIter : formsToFetchForLocalCacheArr) {
			//Form is a match...
			if (formId.equals(formIter.getId())) {
				String echoToUse = formIter.getEcho();
				for (FormFieldListing fieldListing : listingReturnFieldsPopulated) {
					if (echoToUse.equals(fieldListing.getEcho())) return fieldListing.getListing();
				}
			}
		}
		return null;
	}

	/**
	 * When mode is disabled WebSockets, the clients will be closed and cleared ({@code null}).
	 */
	private void clearClientsIfRest() {
		if (DISABLE_WS && this.mode != Mode.RESTfulActive) {
			this.closeAndClean();
			this.mode = Mode.RESTfulActive;
		}
	}

	/**
	 * Close any clients used during lifetime of {@code this} object.
	 */
	public void closeAndClean() {
		if (this.sqlUtilClient != null) this.sqlUtilClient.closeAndClean();
		if (this.fcClient != null) this.fcClient.closeAndClean();

		// WebSocket clients:
		if (this.getAncestorClient != null) this.getAncestorClient.closeAndClean();
		this.getAncestorClient = null;
		if (this.getDescendantsClient != null) this.getDescendantsClient.closeAndClean();
		this.getDescendantsClient = null;
		if (this.getTableFormsClient != null) this.getTableFormsClient.closeAndClean();
		this.getTableFormsClient = null;
		if (this.getFormFieldsClient != null) this.getFormFieldsClient.closeAndClean();
		this.getFormFieldsClient = null;
		if (this.sqlUtilWebSocketExecNativeClient != null) this.sqlUtilWebSocketExecNativeClient.closeAndClean();
		this.sqlUtilWebSocketExecNativeClient = null;
		if (this.getFormHistoryByFormClient != null) this.getFormHistoryByFormClient.closeAndClean();
		this.getFormHistoryByFormClient = null;
	}

	/**
	 * @see Closeable#close() 
	 */
	@Override
	public void close() {
		this.closeAndClean();
	}
}
