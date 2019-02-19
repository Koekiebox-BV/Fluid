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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.sqlutil.*;

/**
 * Wrapper class used for when WebSockets is not a
 * possibility, the wrapper will fallback to REST.
 *
 * @author jasonbruwer on 3/20/18.
 * @since 1.8
 */
public class SQLUtilWebSocketRESTWrapper {

	//Instance...
	private String baseURL;
	private User loggedInUser;

	private long timeoutMillis;

	//Clients...
	private SQLUtilWebSocketGetAncestorClient getAncestorClient = null;
	private SQLUtilWebSocketGetDescendantsClient getDescendantsClient = null;
	private SQLUtilWebSocketGetTableFormsClient getTableFormsClient = null;
	private SQLUtilWebSocketGetFormFieldsClient getFormFieldsClient = null;

	private final SQLUtilClient sqlUtilClient;

	public static boolean COMPRESS_RSP = true;
	public static String COMPRESS_RSP_CHARSET = UtilGlobal.EMPTY;

	//Mode...
	private Mode mode = null;

	private static boolean DISABLE_WS;

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
			DISABLE_WS = Boolean.parseBoolean(
					System.getProperty(
							PropName.FLUID_API_DISABLE_WEB_SOCKETS,
							String.valueOf(false)).trim());

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
	 * @param baseURLParam Example {@code https://[instance].fluidbpm.com/fluid-ws/}
	 * @param serviceTicketParam The service ticket for the logged in user.
	 * @param timeoutMillisParam The timeout of the request in millis.
	 */
	public SQLUtilWebSocketRESTWrapper(
			String baseURLParam,
			String serviceTicketParam,
			long timeoutMillisParam
	) {
		super();

		this.baseURL = baseURLParam;

		this.loggedInUser = new User();
		this.loggedInUser.setServiceTicket(serviceTicketParam);
		this.timeoutMillis = timeoutMillisParam;

		this.sqlUtilClient = new SQLUtilClient(
				this.baseURL,
				this.loggedInUser.getServiceTicket());
	}

	/**
	 * New wrapper instance.
	 *
	 * @param baseURLParam Example {@code https://[instance].fluidbpm.com/fluid-ws/}
	 * @param loggedInUserParam The currently logged in user.
	 * @param timeoutMillisParam The timeout of the request in millis.
	 */
	public SQLUtilWebSocketRESTWrapper(
			String baseURLParam,
			User loggedInUserParam,
			long timeoutMillisParam
	) {
		super();

		this.baseURL = baseURLParam;
		this.loggedInUser = loggedInUserParam;
		this.timeoutMillis = timeoutMillisParam;

		this.sqlUtilClient = new SQLUtilClient(
				this.baseURL,
				this.loggedInUser.getServiceTicket());
	}

	/**
	 * Retrieves the Ancestor for the {@code formToGetAncestorForParam}.
	 *
	 * @param formToGetAncestorForParam The Fluid Form to get Ancestor for.
	 * @param includeFieldDataParam Should Ancestor (Form) Field data be included?
	 * @param includeTableFieldsParam Should Table Record (Form) Field data be included?
	 *
	 * @return The {@code formToGetAncestorForParam} Ancestor as {@code Form}'s.
	 */
	public Form getAncestor(
			Form formToGetAncestorForParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam
	) {
		if(DISABLE_WS) {
			this.mode = Mode.RESTfulActive;
		}

		//ANCESTOR...
		try {
			//When mode is null or [WebSocketActive]...
			if(this.getAncestorClient == null &&
					Mode.RESTfulActive != this.mode) {
				this.getAncestorClient = new SQLUtilWebSocketGetAncestorClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldDataParam,
						includeTableFieldsParam,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET);

				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if(clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}

			this.mode = Mode.RESTfulActive;
		}

		Form formToUse = (formToGetAncestorForParam == null) ? null:
				new Form(formToGetAncestorForParam.getId());

		return (this.getAncestorClient == null) ?
				this.sqlUtilClient.getAncestor(
						formToUse,
						includeFieldDataParam,
						includeTableFieldsParam):
				this.getAncestorClient.getAncestorSynchronized(formToUse);
	}

	/**
	 * Retrieves all the Descendants (Forms) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldDataParam Should Ancestor (Form) Field data be included?
	 * @param includeTableFieldsParam Should Table Record (Form) Field data be included?
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 * @param massFetchParam Is the fetch a large fetch.
	 * @param formsToGetDescForParam The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getDescendants(
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean includeTableFieldFormRecordInfoParam,
			boolean massFetchParam,
			Form ... formsToGetDescForParam)
	{
		if(DISABLE_WS) {
			this.mode = Mode.RESTfulActive;
		}
		
		//DESCENDANTS...
		try {
			//When mode is null or [WebSocketActive]...
			if(this.getDescendantsClient == null && Mode.RESTfulActive != this.mode) {
				this.getDescendantsClient = new SQLUtilWebSocketGetDescendantsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldDataParam,
						includeTableFieldsParam,
						includeTableFieldFormRecordInfoParam,
						massFetchParam,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET);

				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if(clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if(formsToGetDescForParam == null || formsToGetDescForParam.length < 1) {
			return null;
		}

		Form[] formsToFetchFor =
				new Form[formsToGetDescForParam.length];
		for(int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetDescForParam[index].getId());
		}

		if(this.getDescendantsClient != null) {
			return this.getDescendantsClient.getDescendantsSynchronized(
					formsToFetchFor);
		} else {
			List<FormListing> returnVal = new ArrayList<>();

			for(Form formToFetchFor : formsToFetchFor) {
				List<Form> listOfForms =
						this.sqlUtilClient.getDescendants(
								formToFetchFor,
								includeFieldDataParam,
								includeTableFieldsParam,
								includeTableFieldFormRecordInfoParam);

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
	 * @param includeFieldDataParam Should Field data be included?
	 * @param formsToGetTableFormsForParam The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
	 */
	public List<FormListing> getTableForms(
			boolean includeFieldDataParam,
			Form ... formsToGetTableFormsForParam
	) {
		if(DISABLE_WS) {
			this.mode = Mode.RESTfulActive;
		}

		//DESCENDANTS...
		try {
			//When mode is null or [WebSocketActive]...
			if(this.getTableFormsClient == null &&
					Mode.RESTfulActive != this.mode) {
				this.getTableFormsClient = new SQLUtilWebSocketGetTableFormsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldDataParam,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET);

				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if(clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}

			this.mode = Mode.RESTfulActive;
		}

		if(formsToGetTableFormsForParam == null || formsToGetTableFormsForParam.length < 1) {
			return null;
		}

		Form[] formsToFetchFor =
				new Form[formsToGetTableFormsForParam.length];
		for(int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetTableFormsForParam[index].getId());
		}

		if(this.getTableFormsClient != null) {
			return this.getTableFormsClient.getTableFormsSynchronized(formsToFetchFor);
		} else {
			List<FormListing> returnVal = new ArrayList<>();

			for(Form formToFetchFor : formsToFetchFor) {
				List<Form> listOfForms =
						this.sqlUtilClient.getTableForms(
								formToFetchFor,
								includeFieldDataParam);

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
	 * @param includeFieldDataParam Should Field data be included?
	 * @param formsToGetFieldsForParam The Fluid Form to get Descendants for.
	 *
	 * @return The {@code formsToGetFieldsForParam} Fields as {@code Field}'s.
	 */
	public List<FormFieldListing> getFormFields(
			boolean includeFieldDataParam,
			Form ... formsToGetFieldsForParam
	) {
		if(DISABLE_WS) {
			this.mode = Mode.RESTfulActive;
		}

		//FORM FIELDS...
		try {
			//When mode is null or [WebSocketActive]...
			if(this.getFormFieldsClient == null && Mode.RESTfulActive != this.mode) {
				this.getFormFieldsClient = new SQLUtilWebSocketGetFormFieldsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldDataParam,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET);

				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if(clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}
			this.mode = Mode.RESTfulActive;
		}

		if(formsToGetFieldsForParam == null ||
				formsToGetFieldsForParam.length < 1) {
			return null;
		}

		Form[] formsToFetchFor =
				new Form[formsToGetFieldsForParam.length];
		for(int index = 0;index < formsToFetchFor.length;index++) {
			formsToFetchFor[index] = new Form(formsToGetFieldsForParam[index].getId());
		}

		if(this.getFormFieldsClient != null) {
			return this.getFormFieldsClient.getFormFieldsSynchronized(
					formsToFetchFor);
		} else {
			List<FormFieldListing> returnVal = new ArrayList<>();

			for(Form formToFetchFor : formsToFetchFor) {
				List<Field> listOfFields =
						this.sqlUtilClient.getFormFields(
								formToFetchFor,
								includeFieldDataParam);

				FormFieldListing toAdd = new FormFieldListing();
				toAdd.setListing(listOfFields);
				toAdd.setListingCount((listOfFields == null) ? 0 : listOfFields.size());
				returnVal.add(toAdd);
			}

			return returnVal;
		}
	}

	/**
	 * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
	 *
	 * @param includeFieldDataParam Should Field data be included?
	 * @param formsToPopulateFormFieldsForParam The Fluid Form to get Descendants for.
	 */
	public void massPopulateFormFields(
			boolean includeFieldDataParam,
			Form ... formsToPopulateFormFieldsForParam
	) {
		if(DISABLE_WS) {
			this.mode = Mode.RESTfulActive;
		}

		//FORM FIELDS...
		try {
			//When mode is null or [WebSocketActive]...
			if(this.getFormFieldsClient == null && Mode.RESTfulActive != this.mode) {
				this.getFormFieldsClient = new SQLUtilWebSocketGetFormFieldsClient(
						this.baseURL,
						null,
						this.loggedInUser.getServiceTicketAsHexUpper(),
						this.timeoutMillis,
						includeFieldDataParam,
						COMPRESS_RSP,
						COMPRESS_RSP_CHARSET);

				this.mode = Mode.WebSocketActive;
			}
		} catch (FluidClientException clientExcept) {
			if(clientExcept.getErrorCode() !=
					FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) {
				throw clientExcept;
			}

			this.mode = Mode.RESTfulActive;
		}

		//Nothing to do...
		if(formsToPopulateFormFieldsForParam == null ||
				formsToPopulateFormFieldsForParam.length < 1) {
			return;
		}

		//Populate a known echo for all of the local form caches...
		Form[] formsToFetchForLocalCacheArr =
				new Form[formsToPopulateFormFieldsForParam.length];
		for(int index = 0;index < formsToFetchForLocalCacheArr.length;index++) {

			formsToFetchForLocalCacheArr[index] = new Form(formsToPopulateFormFieldsForParam[index].getId());
			formsToFetchForLocalCacheArr[index].setEcho(UUID.randomUUID().toString());
		}

		List<FormFieldListing> listingReturnFieldValsPopulated = new ArrayList<>();

		//Fetch all of the values in a single go...
		if(this.getFormFieldsClient != null) {
			listingReturnFieldValsPopulated =
					this.getFormFieldsClient.getFormFieldsSynchronized(formsToFetchForLocalCacheArr);
		} else {
			//Old Rest way of fetching all of the values...
			for(Form formToFetchFor : formsToFetchForLocalCacheArr) {
				List<Field> listOfFields =
						this.sqlUtilClient.getFormFields(
								formToFetchFor,
								includeFieldDataParam);

				FormFieldListing toAdd = new FormFieldListing();
				toAdd.setListing(listOfFields);
				toAdd.setListingCount((listOfFields == null) ? 0 : listOfFields.size());
				toAdd.setEcho(formToFetchFor.getEcho());
				listingReturnFieldValsPopulated.add(toAdd);
			}
		}

		//Populate each of the form from the param...
		for(Form formToSetFieldsOn : formsToPopulateFormFieldsForParam) {
			formToSetFieldsOn.setFormFields(
					this.getFieldValuesForFormFromCache(
							formToSetFieldsOn.getId(),
							listingReturnFieldValsPopulated,
							formsToFetchForLocalCacheArr));
		}
	}

	/**
	 * Populate the field values from the cache.
	 *
	 * @param formIdParam The id of the form to populate.
	 * @param listingReturnFieldValsPopulatedParam The cache of field values.
	 * @param formsToFetchForLocalCacheArrParam The forms that contain the ids and echos.
	 *
	 * @return The field values for form with id {@code formIdParam}.
	 */
	private List<Field> getFieldValuesForFormFromCache(
			Long formIdParam,
			List<FormFieldListing> listingReturnFieldValsPopulatedParam,
			Form[] formsToFetchForLocalCacheArrParam){

		if(formIdParam == null || formIdParam.longValue() < 1) {
			return null;
		}

		if(listingReturnFieldValsPopulatedParam == null ||
				listingReturnFieldValsPopulatedParam.isEmpty()) {
			return null;
		}

		if(formsToFetchForLocalCacheArrParam == null ||
				formsToFetchForLocalCacheArrParam.length == 0) {
			return null;
		}

		for(Form formIter : formsToFetchForLocalCacheArrParam) {
			//Form is a match...
			if(formIdParam.equals(formIter.getId())) {
				String echoToUse = formIter.getEcho();
				for(FormFieldListing fieldListing : listingReturnFieldValsPopulatedParam) {
					if(echoToUse.equals(fieldListing.getEcho())) {
						return fieldListing.getListing();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Close any clients used during lifetime of {@code this} object.
	 */
	public void closeAndClean() {
		if(this.sqlUtilClient != null) {
			this.sqlUtilClient.closeAndClean();
		}

		if(this.getAncestorClient != null) {
			this.getAncestorClient.closeAndClean();
		}

		if(this.getDescendantsClient != null) {
			this.getDescendantsClient.closeAndClean();
		}

		if(this.getTableFormsClient != null) {
			this.getTableFormsClient.closeAndClean();
		}

		if(this.getFormFieldsClient != null) {
			this.getFormFieldsClient.closeAndClean();
		}
	}
}
