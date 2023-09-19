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

package com.fluidbpm.program.api.vo.ws;

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import lombok.NonNull;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

import static com.fluidbpm.program.api.util.UtilGlobal.EMPTY;
import static com.fluidbpm.program.api.util.UtilGlobal.ENCODING_UTF_8;
import static com.fluidbpm.program.api.vo.ws.WS.Path.FlowItem.Version1.QueryParam.EXECUTE_CALCULATED_LABELS;
import static com.fluidbpm.program.api.vo.ws.WS.Path.FormHistory.QueryParam.INCLUDE_CURRENT;
import static com.fluidbpm.program.api.vo.ws.WS.Path.FormHistory.QueryParam.LABEL_FIELD_NAME;
import static com.fluidbpm.program.api.vo.ws.WS.Path.RouteField.Version1.QueryParam.FLUID_ITEM;
import static com.fluidbpm.program.api.vo.ws.WS.Path.UserQuery.Version1.QueryParam.POPULATE_ANCESTOR_ID;

/**
 * <p> The Mapping class used for all Fluid Representational State Transfer
 * (REST) JSON Based Web Services.
 *
 * More can be read at:
 * {@code https://docs.oracle.com/javaee/6/tutorial/doc/gijqy.html}
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidVO
 * @see JSONObject
 */
public class WS {

	public static final String PRODUCES = "application/json";
	public static final String CONSUMES = "application/json";

	/**
	 * Mapping for frequently used HTTP parameters.
	 */
	public static final class QueryParam {
		//Forcefully perform action...
		public static final String FORCE = "force";

		//Execution process in a asynchronous manner...
		public static final String ASYNC = "async";

		//Id...
		public static final String ID = "id";

		//The intent of the action...
		public static final String INTENT = "intent";
	}

	/**
	 * The intent of the action being performed.
	 */
	public static enum Intent {
		Create,
		Update,
		Delete,
		All;

		/**
		 * Retrieves the allowed options for intent.
		 *
		 * @return The {@code enum} for {@code Intent}.
		 */
		public static String allowedOptions() {
			StringBuilder returnVal = new StringBuilder();

			for (Intent intent : Intent.values()) {
				returnVal.append(intent);
				returnVal.append("|");
			}

			String toString = returnVal.toString();
			return toString.substring(0, toString.length() - 1);
		}

		/**
		 * Retrieves the enum value from the {@code intentStrParam}.
		 *
		 * @param intentStrParam The intent for the action.
		 * @return The {@code enum} for {@code Intent}.
		 */
		public static Intent getIntentFromString(String intentStrParam) {
			if (intentStrParam == null || intentStrParam.trim().isEmpty()) {
				return null;
			}

			String paramLowerTrimmed = intentStrParam.trim().toLowerCase();

			for (Intent intent : Intent.values()) {
				String iterValIntentLowerTrim = intent.toString().toLowerCase();

				if (iterValIntentLowerTrim.equals(paramLowerTrimmed)) {
					return intent;
				}
			}

			return null;
		}
	}

	/**
	 * The URL (Universal Resource Locator) Path mappings for Fluid's Web Services.
	 */
	public final static class Path {
		public static final String WEB_SOCKET = "/web_socket/";

		/**
		 * The Version mapping for the Fluid Web Service.
		 */
		public static final class Version {
			public static final String VERSION_1 = "v1";
		}

		/**
		 * The Electronic Form (Document) Web Service mappings.
		 *
		 * @see Form
		 */
		public static final class FormContainer {
			/**
			 * Form Container mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/form_container");
				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");

				//Update...
				public static final String UPDATE = ("/update");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_ANCESTOR_BY_CHILD = ("/get_ancestor_by_child");
				public static final String PRINT_AS_PDF = ("/print_as_pdf");

				//Lock and Unlock
				public static final String LOCK_FORM_CONTAINER = "/lock_form_container";
				public static final String UN_LOCK_FORM_CONTAINER = "/un_lock_form_container";

				//Execute Web Action...
				public static final String EXECUTE_CUSTOM_WEB_ACTION = ("/execute_custom_web_action");

				//Lookup by title...
				public static final String READ_BY_TITLE_CONTAINS = "/get_by_title_contains";

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					//Locking a Form Container...
					public static final String JOB_VIEW = "job_view";
					public static final String FORM_CONTAINER = "form_container";
					public static final String INCLUDE_COMPANY_LOGO = "include_company_logo";
					public static final String PRINT_AS_PDF_ACCESS_TOKEN = "print_as_pdf_access_token";
					public static final String INCLUDE_ANCESTOR = "include_ancestor";
					public static final String INCLUDE_DESCENDANTS = "include_descendants";
					public static final String INCLUDE_FORM_PROPERTIES = "include_form_properties";
					public static final String LOCK_FOR_USER_ID = "lock_for_user_id";
					public static final String ADD_TO_PERSONAL_INVENTORY = "add_to_personal_inventory";

					//Remove from Personal Inventory...
					public static final String REMOVE_FROM_PERSONAL_INVENTORY = "remove_from_personal_inventory";

					//Web Action...
					public static final String CUSTOM_WEB_ACTION = "custom_web_action";
					public static final String IS_TABLE_RECORD = "is_table_record";
					public static final String FORM_TABLE_RECORD_BELONGS_TO = "form_table_record_belongs_to";

					public static final String QUERY_LIMIT = "query_limit";
					public static final String OFFSET = "offset";

					public static final String EXECUTE_CALCULATED_LABELS = "execute_calculated_labels";
				}

				/**
				 * Root for Form Container.
				 *
				 * @return {@code /form_container}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Form Container get by id.
				 *
				 * @param executeCalculatedLabels Should calculated labels be executed.
				 *
				 * @return {@code v1/form_container/get_by_id}
				 */
				public static final String getById(boolean executeCalculatedLabels) {
					return String.format("%s?%s=%s",
							Version.VERSION_1.concat(ROOT).concat(READ),
							QueryParam.EXECUTE_CALCULATED_LABELS,
							executeCalculatedLabels);
				}

				/**
				 * URL Path for Form Container Ancestor get by Child id.
				 *
				 * @return {@code v1/form_container/get_ancestor_by_child}
				 */
				public static final String getAncestorByChild() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ANCESTOR_BY_CHILD);
				}

				/**
				 * URL Path for Form Container get by title containing.
				 *
				 * @param limit The max number of results.
				 * @param offset The starting offset.
				 *
				 * @return {@code v1/form_container/get_by_title_contains}
				 */
				public static final String getByTitleContains(int limit, int offset) {
					String base = Version.VERSION_1.concat(ROOT).concat(READ_BY_TITLE_CONTAINS);
					String additionString = "?";

					if (limit > 0) {
						additionString += FlowItem.Version1.QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += limit;
						additionString += "&";
					}

					if (offset > -1) {
						additionString += FlowItem.Version1.QueryParam.OFFSET;
						additionString += "=";
						additionString += offset;
						additionString += "&";
					}
					return base.concat(additionString);
				}

				/**
				 * URL Path for Electronic Form create.
				 *
				 * @param addToPersonalInventory Should the item be added to the Personal Inventory after created?
				 *
				 * @return {@code v1/form_container/}
				 */
				public static final String formContainerCreate(boolean addToPersonalInventory) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(CREATE);

					returnVal += "?";
					returnVal += QueryParam.ADD_TO_PERSONAL_INVENTORY;
					returnVal += "=";
					returnVal += addToPersonalInventory;
					
					return returnVal;
				}

				/**
				 * URL Path for Form Container update.
				 *
				 * @return {@code v1/form_container/update}
				 */
				public static final String formContainerUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for executing Form Container custom web action.
				 *
				 * @return {@code v1/form_container/execute_custom_web_action}
				 */
				public static final String executeCustomWebAction() {
					return Version.VERSION_1.concat(ROOT).concat(EXECUTE_CUSTOM_WEB_ACTION);
				}

				/**
				 * URL Path for Electronic Form create via Web Socket.
				 *
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 *
				 * @return {@code web_socket/v1/form_container/}
				 */
				public static final String formContainerCreateWebSocket(String serviceTicketParam) {
					return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam);
				}

				/**
				 * URL Path for Form Container delete.
				 *
				 * @return {@code v1/form_container/delete} <b>without</b> force.
				 */
				public static final String formContainerDelete() {
					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for locking a {@code Form}.
				 *
				 * @param jobViewIdParam The view selected to lock the item from.
				 * @param lockingAsUserIdParam The form will be locked as this user. The logged
				 *            in user must have permission to perform this action.
				 *
				 * @return {@code /v1/form_container/lock_form_container}
				 */
				public static final String lockFormContainer(Long jobViewIdParam, Long lockingAsUserIdParam) {
					String base = Version.VERSION_1.concat(ROOT).concat(LOCK_FORM_CONTAINER);

					String additionString = "?";

					if (jobViewIdParam != null && jobViewIdParam.longValue() > 0) {
						additionString += FormContainer.Version1.QueryParam.JOB_VIEW;
						additionString += "=";
						additionString += jobViewIdParam;
						additionString += "&";
					}

					if (lockingAsUserIdParam != null && lockingAsUserIdParam.longValue() > 0) {
						additionString += QueryParam.LOCK_FOR_USER_ID;
						additionString += "=";
						additionString += lockingAsUserIdParam;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);
					return base.concat(additionString);
				}

				/**
				 * URL Path for un-locking a {@code Form}.
				 *
				 * @param unLockingAsUserIdParam The form will be un-locked as this user. The
				 *            logged in user must have permission to perform this action.
				 * @param unlockAsyncParam Should the unlock be performed asynchronous.
				 * @param removeFromPersonalInventoryParam Remove from Personal Inventory when
				 *            unlocked.
				 *
				 * @return {@code v1/form_container/un_lock_form_container}
				 */
				public static final String unLockFormContainer(Long unLockingAsUserIdParam, boolean unlockAsyncParam, boolean removeFromPersonalInventoryParam) {
					String base = Version.VERSION_1.concat(ROOT).concat(UN_LOCK_FORM_CONTAINER);

					String additionString = "?";

					if (unLockingAsUserIdParam != null && unLockingAsUserIdParam.longValue() > 0) {
						additionString += QueryParam.LOCK_FOR_USER_ID;
						additionString += "=";
						additionString += unLockingAsUserIdParam;
						additionString += "&";
					}

					//Asynchronous...
					additionString += WS.QueryParam.ASYNC;
					additionString += "=";
					additionString += unlockAsyncParam;
					additionString += "&";

					//Remove from Personal Inventory...
					additionString += QueryParam.REMOVE_FROM_PERSONAL_INVENTORY;
					additionString += "=";
					additionString += removeFromPersonalInventoryParam;
					additionString += "&";

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}

				/**
				 * URL Path for a PDF version of the {@code Form}.
				 *
				 * @param formContainerIdParam The form container id.
				 * @param includeAncestorParam Include the ancestor electronic form.
				 * @param includeCompanyLogoParam Include the company logo.
				 * @param includeDescendantsParam Include descendant forms.
				 * @param includeFormPropertiesParam Include form properties.
				 *
				 * @return {@code v1/form_container/print_as_pdf}
				 */
				public static final String getPrintAsPDF(
					Long formContainerIdParam,
					boolean includeCompanyLogoParam,
					boolean includeAncestorParam,
					boolean includeDescendantsParam,
					boolean includeFormPropertiesParam
				) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(PRINT_AS_PDF);

					returnVal += "?";

					//Form Container Id...
					returnVal += FormContainer.Version1.QueryParam.FORM_CONTAINER;
					returnVal += "=";
					returnVal += formContainerIdParam;
					returnVal += "&";

					//Include Ancestor...
					returnVal += QueryParam.INCLUDE_ANCESTOR;
					returnVal += "=";
					returnVal += includeAncestorParam;
					returnVal += "&";

					//Include Company Logo...
					returnVal += QueryParam.INCLUDE_COMPANY_LOGO;
					returnVal += "=";
					returnVal += includeCompanyLogoParam;
					returnVal += "&";

					//Include Descendants...
					returnVal += QueryParam.INCLUDE_DESCENDANTS;
					returnVal += "=";
					returnVal += includeDescendantsParam;
					returnVal += "&";

					//Form Properties...
					returnVal += QueryParam.INCLUDE_FORM_PROPERTIES;
					returnVal += "=";
					returnVal += includeFormPropertiesParam;

					return returnVal;
				}

				/**
				 * URL Path for a PDF version of the {@code Form}.
				 *
				 * @param includeAncestorParam Include the ancestor electronic form.
				 * @param includeCompanyLogoParam Include the company logo.
				 * @param includeDescendantsParam Include descendant forms.
				 * @param includeFormPropertiesParam Include form properties.
				 *
				 * @return {@code v1/form_container/print_as_pdf}
				 */
				public static final String printAsPDFAttachment(
					boolean includeCompanyLogoParam,
					boolean includeAncestorParam,
					boolean includeDescendantsParam,
					boolean includeFormPropertiesParam
				) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(PRINT_AS_PDF);

					returnVal += "?";
					
					//Include Ancestor...
					returnVal += QueryParam.INCLUDE_ANCESTOR;
					returnVal += "=";
					returnVal += includeAncestorParam;
					returnVal += "&";

					//Include Company Logo...
					returnVal += QueryParam.INCLUDE_COMPANY_LOGO;
					returnVal += "=";
					returnVal += includeCompanyLogoParam;
					returnVal += "&";

					//Include Descendants...
					returnVal += QueryParam.INCLUDE_DESCENDANTS;
					returnVal += "=";
					returnVal += includeDescendantsParam;
					returnVal += "&";

					//Form Properties...
					returnVal += QueryParam.INCLUDE_FORM_PROPERTIES;
					returnVal += "=";
					returnVal += includeFormPropertiesParam;

					return returnVal;
				}
			}
		}

		/**
		 * The Electronic Form (Document) Web Service mappings.
		 *
		 * @see Form
		 */
		public static final class FormContainerTableRecord {
			/**
			 * Form Container mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/form_container/table_record");
				public static final String CREATE = ("/");

				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

				/**
				 * Root for Form Container.
				 *
				 * @return {@code /form_container}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Electronic Form Table Record create.
				 *
				 * @return {@code /form_container/table_record/}
				 */
				public static final String formContainerTableRecordCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Electronic Form Table Record create via Web Socket.
				 *
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 *
				 * @return {@code web_socket/v1/form_container/table_record/}
				 */
				public static final String formContainerTableRecordCreateWebSocket(String serviceTicketParam) {
					return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam);
				}
			}
		}

		/**
		 * The Collaboration Web Service mappings.
		 *
		 * @see Collaboration
		 */
		public static final class Collaboration {
			/**
			 * Form Container mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/collaboration");

				public static final String CREATE = ("/");

				public static final String READ_ALL_TO_BY_LOGGED_IN = ("/get_all_to_by_logged_in");
				public static final String READ_ALL_TO_BY_FORM = ("/get_all_to_by_form");

				/**
				 * Root for Collaboration.
				 *
				 * @return {@code /collaboration}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Collaboration create.
				 *
				 * @return {@code v1/collaboration/}
				 */
				public static final String collaborationCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for fetching all Collaboration items by logged in.
				 *
				 * @return {@code v1/collaboration/get_all_to_by_logged_in}
				 *
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String getAllToByLoggedIn() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_TO_BY_LOGGED_IN);
				}

				/**
				 * URL Path for fetching all Collaboration items by Form.
				 *
				 * @return {@code v1/collaboration/get_all_to_by_form}
				 *
				 * @see Form
				 */
				public static final String getAllToByForm() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_TO_BY_FORM);
				}
			}
		}

		/**
		 * The Attachment Web Service mappings.
		 *
		 * @see Attachment
		 */
		public static final class Attachment {
			/**
			 * Form Container mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/attachment");

				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_RAW_BY_FORM_CONTAINER_AND_INDEX = ("/get_raw_by_form_container_and_index");
				public static final String READ_ALL_BY_FORM = ("/get_all_by_form");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String FORM_CONTAINER_ID = "form_container";
					public static final String ATTACHMENT_INDEX = "attachment_index";
					public static final String IMAGES_ONLY = "images_only";
					public static final String INCLUDE_ATTACHMENT_DATA = "include_attachment_data";
				}

				/**
				 * Root for Form Container.
				 *
				 * @return {@code /attachment}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Attachment get by id.
				 *
				 * @param includeAttachmentDataParam Should attachment data be included. Note
				 *            that only the latest versions will be retrieved.
				 *
				 * @return {@code v1/attachment/get_by_id}
				 *
				 * @see Attachment
				 */
				public static final String getById(boolean includeAttachmentDataParam) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(READ);

					returnVal += "?";

					//Include Attachment Data...
					returnVal += QueryParam.INCLUDE_ATTACHMENT_DATA;
					returnVal += "=";
					returnVal += includeAttachmentDataParam;

					return returnVal;
				}

				/**
				 * URL Path for Attachment create.
				 *
				 * Executing this more will cause new versions to be created.
				 *
				 * Versions are bumped.
				 *
				 * @return {@code v1/attachment/}
				 *
				 * @see Attachment
				 */
				public static final String attachmentCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Attachment delete.
				 *
				 * @return {@code v1/attachment/delete} <b>without</b> force.
				 */
				public static final String attachmentDelete() {
					return attachmentDelete(false);
				}

				/**
				 * URL Path for Attachment delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/attachment/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String attachmentDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Attachment get by Form Container and Index.
				 *
				 * @param formContainerIdParam The primary key for the form container.
				 * @param indexParam The attachment index for the form container.
				 *
				 * @return {@code v1/attachment/get_raw_by_form_container_and_index}
				 */
				public static final String getRawByFormContainerAndIndex(Long formContainerIdParam, int indexParam) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(READ_RAW_BY_FORM_CONTAINER_AND_INDEX);

					returnVal += "?";

					//Form Container Id...
					returnVal += QueryParam.FORM_CONTAINER_ID;
					returnVal += "=";
					returnVal += formContainerIdParam;
					returnVal += "&";

					//Attachment Index...
					returnVal += QueryParam.ATTACHMENT_INDEX;
					returnVal += "=";
					returnVal += indexParam;

					return returnVal;
				}

				/**
				 * URL Path for Attachment get by Form Container.
				 *
				 * @param includeAttachmentDataParam Should attachment data be included. Note
				 *            that only the latest versions will be retrieved.
				 *
				 * @param imagesOnlyParam Only retrieve attachments where there the content type
				 *            is of {@code image}.
				 *
				 * @return {@code v1/attachment/get_raw_by_form_container_and_index}
				 */
				public static final String getAllByFormContainer(boolean includeAttachmentDataParam, boolean imagesOnlyParam) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_FORM);

					returnVal += "?";

					//Include Attachment Data...
					returnVal += QueryParam.INCLUDE_ATTACHMENT_DATA;
					returnVal += "=";
					returnVal += includeAttachmentDataParam;
					returnVal += "&";

					//Images Only...
					returnVal += QueryParam.IMAGES_ONLY;
					returnVal += "=";
					returnVal += imagesOnlyParam;

					return returnVal;
				}
			}
		}

		/**
		 * The Electronic Form Personal Inventory Web Service mappings.
		 *
		 * @see Form
		 */
		public static final class PersonalInventory {
			/**
			 * Personal Inventory mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/personal_inventory");

				//Create...
				public static final String CREATE = ("/");

				//Read...
				public static final String READ = ("/get_all_by_logged_in_user");

				//Remove from Personal Inventory...
				public static final String CLEAR_PERSONAL_INVENTORY = "/clear_personal_inventory";
				public static final String REMOVE_FROM_PERSONAL_INVENTORY = "/remove_from_personal_inventory";

				/**
				 * Root for Personal Inventory.
				 *
				 * @return {@code /personal_inventory}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for adding Electronic Form to Personal Inventory.
				 *
				 * @return {@code v1/personal_inventory/}
				 */
				public static final String formContainerAddToPersonalInventory() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Form Containers get by logged in user.
				 *
				 * @return {@code v1/personal_inventory/get_all_by_logged_in_user}
				 */
				public static final String getAllByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for clearing the whole Personal Inventory.
				 *
				 * @return {@code v1/personal_inventory/remove_from_personal_inventory}
				 */
				public static final String removeFromPersonalInventory() {
					return Version.VERSION_1.concat(ROOT).concat(REMOVE_FROM_PERSONAL_INVENTORY);
				}

				/**
				 * URL Path for clearing Personal Inventory.
				 *
				 * @return {@code v1/personal_inventory/clear_personal_inventory}
				 */
				public static final String clearPersonalInventory() {
					return Version.VERSION_1.concat(ROOT).concat(CLEAR_PERSONAL_INVENTORY);
				}
			}
		}

		/**
		 * The Form Field Web Service mappings.
		 *
		 * @see Field
		 */
		public static final class FormField {
			/**
			 * Mapping for frequently used HTTP parameters.
			 */
			public static final class QueryParam {
				public static final String EDIT_ONLY = "edit_only";
				public static final String POPULATE_MULTI_CHOICE_FIELDS = "populate_multi_choice_fields";
			}

			/**
			 * Form Field mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/form_field");

				//Create...
				public static final String CREATE = ("/");

				//Update...
				public static final String UPDATE = ("/update");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String BY_NAME = ("/get_by_name");
				public static final String READ_BY_FORM_DEF_AND_LOGGED_IN_USER = ("/get_by_form_definition_and_logged_in_user");
				public static final String READ_BY_FORM_DEFS_AND_LOGGED_IN_USER = ("/get_by_form_definitions_and_logged_in_user");
				public static final String READ_BY_USER_QUERY = ("/get_by_user_query");
				public static final String READ_BY_USER_QUERIES = ("/get_by_user_queries");

				public static final String AUTO_COMPLETE_EXISTING = ("/auto_complete_existing");

				/**
				 * Root for Form Field.
				 *
				 * @return {@code /form_field}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Form Field create.
				 *
				 * @return {@code /v1/form_field/}
				 */
				public static final String formFieldCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Form Field delete.
				 *
				 * @return {@code v1/form_field/delete} <b>without</b> force.
				 */
				public static final String formFieldDelete() {
					return formFieldDelete(false);
				}

				/**
				 * URL Path for Form Field delete.
				 *
				 * @param forceDeleteParam Whether to forcefulle delete.
				 *
				 * @return {@code v1/form_field/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String formFieldDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Form Field update.
				 *
				 * @return {@code v1/form_field/update}
				 */
				public static final String formFieldUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Form Field get by id.
				 *
				 * @return {@code v1/form_field/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Form Field get by name.
				 *
				 * @return {@code v1/form_field/get_by_name}
				 */
				public static final String getByName() {
					return Version.VERSION_1.concat(ROOT).concat(BY_NAME);
				}

				/**
				 * URL Path for Form Fields get by Form Definition and Logged In User.
				 *
				 * @param editOnlyFieldsParam Only return the fields that are editable.
				 * @param populateMultiChoiceFields Populate the multi-choice fields.
				 *
				 * @return {@code v1/form_field/get_by_form_definition_and_logged_in_user}
				 */
				public static final String getByFormDefinitionAndLoggedInUser(
					boolean editOnlyFieldsParam,
					boolean populateMultiChoiceFields
				) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(READ_BY_FORM_DEF_AND_LOGGED_IN_USER);
					returnVal += ("?" + QueryParam.EDIT_ONLY + "=" + editOnlyFieldsParam);
					returnVal += ("&" + QueryParam.POPULATE_MULTI_CHOICE_FIELDS + "=" + populateMultiChoiceFields);
					return returnVal;
				}

				/**
				 * URL Path for Form Fields get by Form Definitions and Logged In User.
				 *
				 * @param editOnlyFieldsParam Only return the fields that are editable.
				 * @param populateMultiChoiceFields Populate the multi-choice fields.
				 *
				 * @return {@code v1/form_field/get_by_form_definitions_and_logged_in_user}
				 */
				public static final String getByFormDefinitionsAndLoggedInUser(
					boolean editOnlyFieldsParam,
					boolean populateMultiChoiceFields
				) {
					String returnVal = Version.VERSION_1.concat(ROOT).concat(READ_BY_FORM_DEFS_AND_LOGGED_IN_USER);
					returnVal += ("?" + QueryParam.EDIT_ONLY + "=" + editOnlyFieldsParam);
					returnVal += ("&" + QueryParam.POPULATE_MULTI_CHOICE_FIELDS + "=" + populateMultiChoiceFields);
					return returnVal;
				}

				/**
				 * URL Path for Form Fields get by User Query.
				 *
				 * @return {@code v1/form_field/get_by_user_query}
				 */
				public static final String getByUserQuery() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_USER_QUERY);
				}

				/**
				 * URL Path for Form Fields get by User Query.
				 *
				 * @return {@code v1/form_field/get_by_user_queries}
				 */
				public static final String getByUserQueries() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_USER_QUERIES);
				}

				/**
				 * URL Path for Form Fields auto complete by field.
				 *
				 * @return {@code v1/form_field/auto_complete_existing}
				 */
				public static String autoCompleteExisting() {
					return Version.VERSION_1.concat(ROOT).concat(AUTO_COMPLETE_EXISTING);
				}
			}
		}

		/**
		 * The Global Field Web Service mappings.
		 *
		 * @see Field
		 */
		public static final class GlobalField {

			/**
			 * Global Field mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/global_field");

				//Create...
				public static final String CREATE = ("/");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String UPDATE_VALUE = ("/update_value");

				//Read...
				public static final String READ_VALUE_BY = ("/get_value_by");
				public static final String READ_ALL_VALUES = ("/get_all_values");
				public static final String READ_ALL = ("/get_all");

				/**
				 * Root for Global Field.
				 *
				 * @return {@code /global_field}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Global Field Value update.
				 *
				 * @return {@code v1/global_field/update_value}
				 */
				public static final String globalFieldUpdateValue() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE_VALUE);
				}

				/**
				 * URL Path for Global Field value, get by name or id.
				 *
				 * @return {@code v1/global_field/get_value_by}
				 */
				public static final String getValueBy() {
					return Version.VERSION_1.concat(ROOT).concat(READ_VALUE_BY);
				}

				/**
				 * URL Path for retrieving all Global Field values.
				 *
				 * @return {@code v1/global_field/get_all_values}
				 */
				public static final String getAllValues() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VALUES);
				}

				/**
				 * URL Path for retrieving all Global Fields.
				 *
				 * @return {@code v1/global_field/get_all}
				 */
				public static final String getAllFields() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}

				/**
				 * URL Path for Global Field create.
				 *
				 * @return {@code /v1/global_field/}
				 */
				public static final String globalFieldCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Global Field update.
				 *
				 * @return {@code v1/global_field/update}
				 */
				public static final String globalFieldUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}
			}
		}

		/**
		 * The Route Field Web Service mappings.
		 *
		 * @see Field
		 */
		public static final class RouteField {
			/**
			 * Route Field mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/route_field");

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String UPDATE_VALUE = ("/update_value");
				public static final String CREATE_VALUE = ("/create_value");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_VALUES_BY = ("/get_values_by");
				public static final String READ_BY_VIEW_GROUP = ("/get_by_view_group");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String FLUID_ITEM = "fluid_item";
				}

				/**
				 * Root for Route Field.
				 *
				 * @return {@code /route_field}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Route Field create.
				 *
				 * @return {@code /v1/route_field/}
				 */
				public static final String routeFieldCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Route Field delete.
				 *
				 * @return {@code v1/route_field/delete} <b>without</b> force.
				 */
				public static final String routeFieldDelete() {
					return routeFieldDelete(false);
				}

				/**
				 * URL Path for Route Field delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/route_field/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String routeFieldDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Route Field update.
				 *
				 * @return {@code v1/route_field/update}
				 */
				public static final String routeFieldUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Route Field Value update.
				 *
				 * @return {@code v1/route_field/update_value}
				 */
				public static final String routeFieldUpdateValue() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE_VALUE);
				}

				/**
				 * URL Path for Route Field Value create.
				 *
				 * @param fluidItemIdParam The id of the Fluid item to create Route field for.
				 *
				 * @return {@code v1/route_field/create_value}
				 */
				public static final String routeFieldCreateValue(Long fluidItemIdParam) {
					if (fluidItemIdParam == null || fluidItemIdParam.longValue() < 1) {
						return Version.VERSION_1.concat(ROOT).concat(CREATE_VALUE);
					}

					return Version.VERSION_1.concat(ROOT).concat(CREATE_VALUE).concat(
							"?").concat(FLUID_ITEM).concat("=").concat(fluidItemIdParam.toString());
				}

				/**
				 * URL Path for Route Field get by id.
				 *
				 * @return {@code v1/route_field/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Route Field get by View Group.
				 *
				 * @return {@code v1/route_field/get_by_view_group}
				 */
				public static final String getByViewGroup() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_VIEW_GROUP);
				}

				/**
				 * URL Path for retrieving Route Field values by.
				 *
				 * @return {@code v1/route_field/get_values_by}
				 */
				public static final String getValuesBy() {
					return Version.VERSION_1.concat(ROOT).concat(READ_VALUES_BY);
				}
			}
		}

		/**
		 * The User Field Web Service mappings.
		 *
		 * @see Field
		 */
		public static final class UserField {
			/**
			 * User Field mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/user_field");

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String UPDATE_VALUE = ("/update_value");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_BY_NAME = ("/get_by_name");

				/**
				 * Root for User Field.
				 *
				 * @return {@code /user_field}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for User Field create.
				 *
				 * @return {@code /v1/user_field/}
				 */
				public static final String userFieldCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for User Field delete.
				 *
				 * @return {@code v1/user_field/delete} <b>without</b> force.
				 */
				public static final String userFieldDelete() {
					return userFieldDelete(false);
				}

				/**
				 * URL Path for User Field delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/user_field/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String userFieldDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for User Field update.
				 *
				 * @return {@code v1/user_field/update}
				 */
				public static final String userFieldUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for User Field Value update.
				 *
				 * @return {@code v1/user_field/update_value}
				 */
				public static final String userFieldUpdateValue() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE_VALUE);
				}

				/**
				 * URL Path for User Field get by id.
				 *
				 * @return {@code v1/user_field/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for User Field get by name.
				 *
				 * @return {@code v1/user_field/get_by_name}
				 */
				public static final String getByName() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_NAME);
				}
			}
		}

		/**
		 * The Form Definition Web Service mappings.
		 *
		 * @see Form
		 */
		public static final class FormDefinition {
			/**
			 * Mapping for frequently used HTTP parameters.
			 */
			public static final class QueryParam {
				public static final String FORM_DEFINITION = "form_definition";
				public static final String INCLUDE_TABLE_RECORDS = "include_table_records";
				public static final String INCLUDE_WORKFLOWS = "include_workflows";
			}

			/**
			 * Form Definition mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/form_definition");

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Update...
				public static final String UPDATE = ("/update");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_BY_NAME = ("/get_by_name");

				//Read - Logged in user...
				public static final String READ_ALL_BY_LOGGED_IN_USER = ("/get_all_by_logged_in_user");
				public static final String READ_ALL_BY_LOGGED_IN_USER_INCL_TABLE_DEFS = ("/get_all_by_logged_in_user_incl_table_defs");
				public static final String READ_ALL_BY_LOGGED_IN_CAN_CREATE = ("/get_all_by_logged_in_can_create");

				public static final String READ_ATTACHMENT_CAN_VIEW_BY_LOGGED_IN_USER = ("/get_all_attachment_can_view_by_logged_in_user");
				public static final String READ_ATTACHMENT_CAN_EDIT_BY_LOGGED_IN_USER = ("/get_all_attachment_can_edit_by_logged_in_user");
				public static final String UPSERT_FORM_DEFS_WEB_KIT = ("/upsert_forms_web_kit");
				public static final String READ_ALL_WEB_KIT = ("/get_all_forms_web_kit");

				/**
				 * Root for Form Definition.
				 *
				 * @return {@code /form_definition}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Form Definition create.
				 *
				 * @return {@code /v1/form_definition/}
				 */
				public static final String formDefinitionCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Form Definition delete.
				 *
				 * @return {@code v1/form_definition/delete} <b>without</b> force.
				 */
				public static final String formDefinitionDelete() {
					return formDefinitionDelete(false);
				}

				/**
				 * URL Path for Form Definition delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/form_definition/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String formDefinitionDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for User Definition update.
				 *
				 * @return {@code v1/form_definition/update}
				 */
				public static final String formDefinitionUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Form Definition get by id.
				 *
				 * @return {@code v1/form_definition/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Form Definition get by name.
				 *
				 * @return {@code v1/form_definition/get_by_name}
				 */
				public static final String getByName() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_NAME);
				}

				/**
				 * URL Path for Form Definitions by logged in user.
				 *
				 * @return {@code v1/form_definition/get_all_by_logged_in_user}
				 */
				public static final String getAllByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for Form Definitions by logged in user. Include the table record
				 * form definitions.
				 *
				 * @return {@code v1/form_definition/get_all_by_logged_in_user_incl_table_defs}
				 */
				public static final String getAllByLoggedInUserIncludeTableTypes() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_LOGGED_IN_USER_INCL_TABLE_DEFS);
				}

				/**
				 * URL Path for Form Definitions where logged in user can create instance of.
				 *
				 * @param includeTableRecordsParam Should Form Definitions that are part of table records also be included?
				 * @param includeWorkflowsParam Should include the associated workflows in the response?
				 *
				 * @return {@code v1/form_definition/get_all_by_logged_in_can_create}
				 */
				public static final String getAllByLoggedInAndCanCreateInstanceOf(
					boolean includeTableRecordsParam,
					boolean includeWorkflowsParam
				) {
					return String.format("%s%s%s?%s=%s&%s=%s",
							Version.VERSION_1,
							ROOT,
							READ_ALL_BY_LOGGED_IN_CAN_CREATE,
							QueryParam.INCLUDE_TABLE_RECORDS,
							includeTableRecordsParam,
							QueryParam.INCLUDE_WORKFLOWS,
							includeWorkflowsParam);
				}

				/**
				 * URL Path for Form Definitions where logged in user can view attachments for.
				 *
				 * @return {@code v1/form_definition/get_all_attachment_can_view_by_logged_in_user}
				 */
				public static final String getAllByLoggedInAndAttachmentsCanView() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ATTACHMENT_CAN_VIEW_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for Form Definitions where logged in user can edit attachments for.
				 *
				 * @return {@code v1/form_definition/get_all_attachment_can_edit_by_logged_in_user}
				 */
				public static final String getAllByLoggedInAndAttachmentsCanEdit() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ATTACHMENT_CAN_EDIT_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for Form Definition Web Kits.
				 *
				 * @return {@code v1/form_definition/get_all_forms_web_kit}
				 */
				public static final String getAllFormDefinitionWebKits() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_WEB_KIT);
				}

				/**
				 * URL Path for upserting the Form Definition web kit.
				 *
				 * @return {@code v1/form_definition/upsert_forms_web_kit}
				 */
				public static final String formDefinitionWebKitUpsert() {
					return Version.VERSION_1.concat(ROOT).concat(UPSERT_FORM_DEFS_WEB_KIT);
				}
			}
		}

		/**
		 * The Flow Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.flow.Flow
		 */
		public static final class Flow {
			/**
			 * Flow mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/flow");

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String UPSERT_VIEW_GROUPS_WEB_KIT = ("/upsert_view_groups_web_kit");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_VIEW_GROUP_WEB_KIT = ("/get_view_group_web_kit");
				public static final String READ_BY_NAME = ("/get_by_name");
				public static final String READ_ALL = ("/get_all_flows");

				/**
				 * Root for Flow.
				 *
				 * @return {@code /flow/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Flow create.
				 *
				 * @return {@code /v1/flow/}
				 */
				public static final String flowCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Flow delete.
				 *
				 * @return {@code v1/flow/delete} <b>without</b> force.
				 */
				public static final String flowDelete() {
					return flowDelete(false);
				}

				/**
				 * URL Path for Form Definition delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/flow/delete?force=forceDeleteParam} <b>with / without</b>
				 *         force.
				 */
				public static final String flowDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Flow update.
				 *
				 * @return {@code v1/flow/update}
				 */
				public static final String flowUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for upserting the Group View web kit.
				 *
				 * @return {@code v1/flow/upsert_view_groups_web_kit}
				 */
				public static final String flowViewGroupUpsert() {
					return Version.VERSION_1.concat(ROOT).concat(UPSERT_VIEW_GROUPS_WEB_KIT);
				}

				/**
				 * URL Path for Flow get by id.
				 *
				 * @return {@code v1/flow/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Flow get by name.
				 *
				 * @return {@code v1/flow/get_by_name}
				 */
				public static final String getByName() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_NAME);
				}

				/**
				 * URL Path for Flow get by name.
				 *
				 * @return {@code v1/flow/get_view_group_web_kit}
				 */
				public static final String getJobViewGroupWebKit() {
					return Version.VERSION_1.concat(ROOT).concat(READ_VIEW_GROUP_WEB_KIT);
				}

				/**
				 * URL Path for Flow get all.
				 *
				 * @return {@code v1/flow/get_all_flows}
				 */
				public static final String getAllFlows() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}
			}
		}

		/**
		 * The License Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.license.LicenseRequest
		 */
		public static final class License {

			/**
			 * License mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/license");

				//Request...
				public static final String REQUEST = ("/request");

				//Apply...
				public static final String APPLY = ("/apply");

				/**
				 * Root for License.
				 *
				 * @return {@code /license/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for License request.
				 *
				 * @return {@code v1/license/request}
				 */
				public static final String licenseRequest() {
					return Version.VERSION_1.concat(ROOT).concat(REQUEST);
				}

				/**
				 * URL Path for applying a License.
				 *
				 * @return {@code v1/license/apply}
				 */
				public static final String licenseApply() {
					return Version.VERSION_1.concat(ROOT).concat(APPLY);
				}
			}
		}

		/**
		 * The Flow Step Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.flow.FlowStep
		 */
		public static final class FlowStep {
			/**
			 * Flow Step mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/flow_step");

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Update...
				public static final String UPDATE = ("/update");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_BY_STEP = ("/get_by_step");

				public static final String READ_ALL_STEPS_BY_FLOW = ("/get_steps_by_flow");

				public static final String READ_ALL_VIEWS_BY_STEP = ("/get_views_by_step");

				public static final String READ_ALL_VIEWS_BY_LOGGED_IN_USER = ("/get_views_by_logged_in_user");

				public static final String READ_ALL_VIEWS_BY_USER = ("/get_views_by_user");

				public static final String READ_ALL_VIEWS_BY_FLOW = ("/get_views_by_flow");

				public static final String POLLING = ("/polling");

				public static final String ROOT_POLLING = (ROOT + POLLING);

				public static final String READ_ALL_BY_LOGGED_IN_USER = ("/get_all_by_logged_in_user");

				/**
				 * Root for Flow Step.
				 *
				 * @return {@code /flow_step/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Flow Step create.
				 *
				 * @return {@code /v1/flow_step/}
				 */
				public static final String flowStepCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Flow Step delete.
				 *
				 * @return {@code v1/flow_step/delete} <b>without</b> force.
				 */
				public static final String flowStepDelete() {
					return flowStepDelete(false);
				}

				/**
				 * URL Path for Flow Step delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/flow_step/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String flowStepDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Flow Step update.
				 *
				 * @return {@code v1/flow_step/update}
				 */
				public static final String flowStepUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Flow Step get by id.
				 *
				 * @return {@code v1/flow_step/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Flow Step get by Step details.
				 *
				 * @return {@code v1/flow_step/get_by_step}
				 */
				public static final String getByStep() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_STEP);
				}

				/**
				 * URL Path for JobViews by Flow Step id or name.
				 *
				 * @return {@code v1/flow_step/get_views_by_step}
				 */
				public static final String getAllViewsByStep() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_STEP);
				}

				/**
				 * URL Path for JobViews by logged in user.
				 *
				 * @return {@code v1/flow_step/get_views_by_logged_in_user}
				 */
				public static final String getAllViewsByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for JobViews by user.
				 *
				 * @return {@code v1/flow_step/get_views_by_user}
				 */
				public static final String getAllViewsByUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_USER);
				}

				/**
				 * URL Path for JobViews by {@code Flow}.
				 *
				 * @return {@code v1/flow_step/get_views_by_flow}
				 */
				public static final String getAllViewsByFlow() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_FLOW);
				}

				/**
				 * URL Path for Steps by Flow.
				 *
				 * @return {@code v1/flow_step/get_steps_by_flow}
				 */
				public static final String getAllStepsByFlow() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_STEPS_BY_FLOW);
				}

				/**
				 * URL Path for Polling Steps by logged in user.
				 *
				 * @return {@code v1/flow_step/polling/get_all_by_logged_in_user}
				 */
				public static final String getAllPollingStepsByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT_POLLING).concat(READ_ALL_BY_LOGGED_IN_USER);
				}
			}
		}

		/**
		 * The Flow Step Rule Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.flow.FlowStepRule
		 */
		public static final class FlowStepRule {
			/**
			 * Flow Step Rule mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/flow_step_rule");

				public static final String ROOT_ENTRY = "/flow_step_rule/entry";
				public static final String ROOT_EXIT = "/flow_step_rule/exit";
				public static final String ROOT_VIEW = "/flow_step_rule/view";

				//Create...
				public static final String CREATE = ("/");

				//Delete...
				public static final String DELETE = ("/delete");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String MOVE_UP = ("/move_up");
				public static final String MOVE_DOWN = ("/move_down");

				//Read...
				public static final String READ_RULES_BY_STEP = ("/get_by_step");

				public static final String READ = ("/get_by_id");
				public static final String COMPILE_SYNTAX = ("/compile_syntax");
				public static final String GET_NEXT_VALID_SYNTAX = ("/get_next_valid_syntax");
				public static final String COMPILE_SYNTAX_AND_EXECUTE = ("/compile_syntax_and_execute");

				/**
				 * Root for Flow Step Rule.
				 *
				 * @return {@code /flow_step_rule/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Flow Step Entry Rule create.
				 *
				 * @return {@code /v1/flow_step_rule/entry}
				 */
				public static final String flowStepRuleEntryCreate() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(CREATE);
				}

				/**
				 * URL Path for Flow Step Exit Rule create.
				 *
				 * @return {@code /v1/flow_step_rule/exit}
				 */
				public static final String flowStepRuleExitCreate() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(CREATE);
				}

				/**
				 * URL Path for Flow Step View Rule create.
				 *
				 * @return {@code /v1/flow_step_rule/view}
				 */
				public static final String flowStepRuleViewCreate() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(CREATE);
				}

				/**
				 * URL Path for Flow Step Entry rule delete.
				 *
				 * @return {@code v1/flow_step_rule/entry/delete} <b>without</b> force.
				 */
				public static final String flowStepRuleDeleteEntry() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(DELETE);
				}

				/**
				 * URL Path for Flow Step Exit rule delete.
				 *
				 * @return {@code v1/flow_step_rule/exit/delete} <b>without</b> force.
				 */
				public static final String flowStepRuleDeleteExit() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(DELETE);
				}

				/**
				 * URL Path for Flow Step View rule delete.
				 *
				 * @return {@code v1/flow_step_rule/view/delete} <b>without</b> force.
				 */
				public static final String flowStepRuleDeleteView() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(DELETE);
				}

				/**
				 * URL Path for Flow Step Entry update.
				 *
				 * @return {@code v1/flow_step_rule/entry/update}
				 */
				public static final String flowStepRuleUpdateEntry() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(UPDATE);
				}

				/**
				 * URL Path for Flow Step Entry move up (Order).
				 *
				 * @return {@code v1/flow_step_rule/entry/move_up}
				 */
				public static final String flowStepRuleMoveEntryUp() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_UP);
				}

				/**
				 * URL Path for Flow Step Entry move down (Order).
				 *
				 * @return {@code v1/flow_step_rule/entry/move_down}
				 */
				public static final String flowStepRuleMoveEntryDown() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_DOWN);
				}

				/**
				 * URL Path for Flow Step Exit update.
				 *
				 * @return {@code v1/flow_step_rule/exit/update}
				 */
				public static final String flowStepRuleUpdateExit() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(UPDATE);
				}

				/**
				 * URL Path for Flow Step View update.
				 *
				 * @return {@code v1/flow_step_rule/view/update}
				 */
				public static final String flowStepRuleUpdateView() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(UPDATE);
				}

				/**
				 * URL Path for Flow Step Entry get by id.
				 *
				 * @return {@code v1/flow_step_rule/entry/get_by_id}
				 */
				public static final String getEntryById() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(READ);
				}

				/**
				 * URL Path for Flow Step Entry get next valid syntax.
				 *
				 * @return {@code v1/flow_step_rule/entry/get_next_valid_syntax}
				 */
				public static final String getNextValidEntrySyntax() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(GET_NEXT_VALID_SYNTAX);
				}

				/**
				 * URL Path for Flow Step Entry {@code compile} syntax.
				 *
				 * @return {@code v1/flow_step_rule/entry/compile_syntax}
				 */
				public static final String compileEntrySyntax() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX);
				}

				/**
				 * URL Path for Flow Step Entry {@code compile} syntax and then {@code execute}.
				 *
				 * @return {@code v1/flow_step_rule/entry/compile_syntax_and_execute}
				 */
				public static final String compileEntrySyntaxAndExecute() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX_AND_EXECUTE);
				}

				/**
				 * URL Path for Flow Step Exit get by id.
				 *
				 * @return {@code v1/flow_step_rule/exit/get_by_id}
				 */
				public static final String getExitById() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(READ);
				}

				/**
				 * URL Path for Flow Step Exit get next valid syntax.
				 *
				 * @return {@code v1/flow_step_rule/exit/get_next_valid_syntax}
				 */
				public static final String getNextValidExitSyntax() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(GET_NEXT_VALID_SYNTAX);
				}

				/**
				 * URL Path for Flow Step Exit {@code compile} syntax.
				 *
				 * @return {@code v1/flow_step_rule/exit/compile_syntax}
				 */
				public static final String compileExitSyntax() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(COMPILE_SYNTAX);
				}

				/**
				 * URL Path for Flow Step Exit {@code get_by_step} syntax.
				 *
				 * @return {@code v1/flow_step_rule/exit/get_by_step}
				 */
				public static final String getExitRulesByStep() {
					return Version.VERSION_1.concat(ROOT_EXIT).concat(READ_RULES_BY_STEP);
				}

				/**
				 * URL Path for Flow Step Exit {@code get_by_step} syntax.
				 *
				 * @return {@code v1/flow_step_rule/entry/get_by_step}
				 */
				public static final String getEntryRulesByStep() {
					return Version.VERSION_1.concat(ROOT_ENTRY).concat(READ_RULES_BY_STEP);
				}

				/**
				 * URL Path for Flow Step View get by id.
				 *
				 * @return {@code v1/flow_step_rule/view/get_by_id}
				 */
				public static final String getViewById() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(READ);
				}

				/**
				 * URL Path for Flow Step View get next valid syntax.
				 *
				 * @return {@code v1/flow_step_rule/view/get_next_valid_syntax}
				 */
				public static final String getNextValidViewSyntax() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(GET_NEXT_VALID_SYNTAX);
				}

				/**
				 * URL Path for Flow Step View {@code compile} syntax.
				 *
				 * @return {@code v1/flow_step_rule/view/compile_syntax}
				 */
				public static final String compileViewSyntax() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(COMPILE_SYNTAX);
				}

				/**
				 * URL Path for Flow Step View {@code compile} syntax and then {@code execute}.
				 *
				 * @return {@code v1/flow_step_rule/view/compile_syntax_and_execute}
				 */
				public static final String compileViewSyntaxAndExecute() {
					return Version.VERSION_1.concat(ROOT_VIEW).concat(COMPILE_SYNTAX_AND_EXECUTE);
				}
			}
		}

		/**
		 * The Fluid Item Web Service mappings.
		 *
		 * @see FluidItem
		 */
		public static final class FlowItem {
			/**
			 * Flow Item mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/flow_item");
				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

				//Send On...
				public static final String SEND_ON = ("/send_on");

				//Send to Flow...
				public static final String SEND_TO_FLOW = ("/send_to_flow");
				public static final String REMOVE_FROM_FLOW = ("/remove_from_flow");
				public static final String SEND_TO_FLOW_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT + SEND_TO_FLOW);

				//Create...
				public static final String CREATE = ("/");

				//Get...
				public static final String GET_BY_JOB_VIEW = ("/get_by_job_view");
				public static final String GET_ALL_IN_ERROR = ("/get_all_error");
				public static final String READ = ("/get_by_id");
				public static final String READ_BY_FORM = ("/get_by_form");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					//List Job View content...
					public static final String QUERY_LIMIT = "query_limit";
					public static final String OFFSET = "offset";
					public static final String SORT_FIELD = "sort_field";
					public static final String SORT_ORDER = "sort_order";

					//Locking a Form Container...
					public static final String JOB_VIEW = "job_view";

					//Wait for Rule execution completion...
					public static final String WAIT_FOR_RULE_EXEC_COMPLETION = "wait_for_rule_exec_completion";

					//Allow a collaborator user to send the item on...
					public static final String ALLOW_COLLABORATOR_SEND_ON = "allow_collaborator_send_on";
					public static final String EXECUTE_CALCULATED_LABELS = "execute_calculated_labels";
					public static final String POPULATE_FORM = "populate_form";
				}

				/**
				 * Root for Flow / Fluid Item.
				 *
				 * @return {@code /flow_item/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Flow Item create.
				 *
				 * @return {@code /v1/flow_item/}
				 */
				public static final String flowItemCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for sending a Flow Item to the next step in the workflow process.
				 *
				 * @param allowCollaboratorToSendOnParam All a collaborator user to also send
				 *            on.
				 *
				 * @return {@code /v1/flow_item/send_on}
				 */
				public static final String sendFlowItemOn(boolean allowCollaboratorToSendOnParam) {
					return String.format("%s%s%s?%s=%s",
							Version.VERSION_1, ROOT, SEND_ON,
							QueryParam.ALLOW_COLLABORATOR_SEND_ON, allowCollaboratorToSendOnParam);
				}

				/**
				 * URL Path for sending a Form to a Flow to follow the workflow process.
				 *
				 * @return {@code /v1/flow_item/send_to_flow/}
				 */
				public static final String sendFlowItemToFlow() {
					return Version.VERSION_1.concat(ROOT).concat(SEND_TO_FLOW);
				}

				/**
				 * URL Path for removing a FluidItem from a Flow.
				 *
				 * @return {@code /v1/flow_item/remove_from_flow/}
				 */
				public static final String removeFluidItemFromFlow() {
					return Version.VERSION_1.concat(ROOT).concat(REMOVE_FROM_FLOW);
				}

				/**
				 * URL Path for Send to Flow via Web Socket.
				 *
				 * @param waitForRuleExecCompleteParam Wait for all the program rules to finish
				 *            execution before returning web socket message is sent. The
				 *            response message will include the result.
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 *
				 * @return {@code web_socket/v1/flow_item/send_to_flow/}
				 */
				public static final String sendToFlowWebSocket(boolean waitForRuleExecCompleteParam, String serviceTicketParam) {
					String returnVal = SEND_TO_FLOW_WEB_SOCKET.concat(CREATE)
							.concat(serviceTicketParam + "?" + QueryParam.WAIT_FOR_RULE_EXEC_COMPLETION + "=" + waitForRuleExecCompleteParam);

					return returnVal;
				}

				/**
				 * URL Path for Flow Item get by id.
				 *
				 * @return {@code v1/flow_item/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Flow Item get by Form.
				 *
				 * @param populateForm Should the FlowItem form be populated (even if FlowItem is not found)
				 * @param executeCalculatedLabels Execute the calculated label if form should be populated {@code populateForm == true}.
				 *
				 * @return {@code v1/flow_item/get_by_form}
				 */
				public static final String getByForm(boolean populateForm, boolean executeCalculatedLabels) {
					return String.format(
							"%s%s%s?%s=%s&%s=%s",
							Version.VERSION_1,
							ROOT,
							READ_BY_FORM,
							//params
							EXECUTE_CALCULATED_LABELS,
							executeCalculatedLabels,
							QueryParam.POPULATE_FORM,
							populateForm
					);
				}

				/**
				 * URL Path for Flow Item currently in error.
				 *
				 * @return {@code v1/flow_item/get_all_error}
				 */
				public static final String getAllInError() {
					return Version.VERSION_1.concat(ROOT).concat(GET_ALL_IN_ERROR);
				}

				/**
				 * URL Path for Flow Item get by
				 * {@link com.fluidbpm.program.api.vo.flow.JobView}.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 * @param sortFieldParam The field to sort.
				 * @param sortOrderParam The sort order.
				 *
				 * @return {@code /v1/flow_item/get_by_job_view}
				 */
				public static final String getByJobView(int queryLimitParam, int offsetParam, String sortFieldParam, String sortOrderParam) {
					String base = Version.VERSION_1.concat(ROOT).concat(GET_BY_JOB_VIEW);
					String additionString = "?";

					if (queryLimitParam > 0) {
						additionString += QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += queryLimitParam;
						additionString += "&";
					}

					if (offsetParam > -1) {
						additionString += QueryParam.OFFSET;
						additionString += "=";
						additionString += offsetParam;
						additionString += "&";
					}

					if (sortFieldParam != null && !sortFieldParam.trim().isEmpty()) {
						additionString += QueryParam.SORT_FIELD;
						additionString += "=";
						additionString += sortFieldParam;
						additionString += "&";
					}

					if (sortOrderParam != null && !sortOrderParam.trim().isEmpty()) {
						additionString += QueryParam.SORT_ORDER;
						additionString += "=";
						additionString += sortOrderParam;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}
			}
		}

		/**
		 * The Fluid Item History Web Service mappings.
		 *
		 * @see FormFlowHistoricData
		 */
		public static final class FlowItemHistory {
			/**
			 * Flow Item History mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/flow_item_history");

				//Read...
				public static final String BY_FORM_CONTAINER = ("/get_by_form_container");

				/**
				 * Root for Flow / Fluid Item History.
				 *
				 * @return {@code /flow_item_history/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Form Container Flow History by Form Container.
				 *
				 * @return {@code v1/flow_item_history/get_by_form_container}
				 */
				public static final String getByFormContainer() {
					return Version.VERSION_1.concat(ROOT).concat(BY_FORM_CONTAINER);
				}
			}
		}

		/**
		 * The Fluid Item History Web Service mappings.
		 *
		 * @see FormFlowHistoricData
		 */
		public static final class FormHistory {
			/**
			 * Mapping for frequently used HTTP parameters.
			 */
			public static final class QueryParam {
				public static final String INCLUDE_CURRENT = "include_current";
				public static final String LABEL_FIELD_NAME = "label_field_name";
			}

			/**
			 * Flow Item History mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/form_history");

				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);
				public static final String CREATE = ("/");

				//Read...
				public static final String BY_FORM_CONTAINER = ("/get_by_form_container");

				public static final String BY_FORM_CONTAINER_WEB_SOCKET = (ROOT_WEB_SOCKET + BY_FORM_CONTAINER);
				public static final String MOST_RECENT_BY_FORM_CONTAINER = ("/get_most_recent_by_form_container");

				/**
				 * Root for Form value Item History.
				 *
				 * @return {@code /form_history/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Electronic Form History create.
				 *
				 * @return {@code v1/form_history/}
				 */
				public static final String formHistoryCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Form History by 'Form Container'.
				 *
				 * @param includeCurrent Whether the current values should be included.
				 * @param makeUseOfLabelFieldName Should the label field name be used instead of the system name.
				 *
				 * @return {@code v1/form_history/get_by_form_container}
				 */
				public static final String getByFormContainer(
					boolean includeCurrent,
					boolean makeUseOfLabelFieldName
				) {
					return String.format(
						"%s?%s=%s&%s=%s",
						Version.VERSION_1.concat(ROOT).concat(BY_FORM_CONTAINER),
						INCLUDE_CURRENT,
						includeCurrent,
						LABEL_FIELD_NAME,
						makeUseOfLabelFieldName
					);
				}

				/**
				 * URL Path for retrieving Form historic data using a Web Socket.
				 *
				 * @param serviceTicket The service ticket in hex-decimal text format.
				 * @param includeCurrent Include the current field values for historic data.
				 * @param labelFieldName Make use of label field names.
				 *
				 * @return {@code /web_socket/v1/form_history_by_form_container}
				 */
				public static final String getByFormContainerWebSocket(
						String serviceTicket,
						boolean includeCurrent,
						boolean labelFieldName
				) {
					return String.format("%s/%s?%s=%s&%s=%s",
							BY_FORM_CONTAINER_WEB_SOCKET,
							serviceTicket,
							INCLUDE_CURRENT,
							includeCurrent,
							LABEL_FIELD_NAME,
							labelFieldName
					);
				}

				/**
				 * URL Path for Form most recent History by 'Form Container'.
				 *
				 * @return {@code v1/form_history/get_most_recent_by_form_container}
				 */
				public static final String getByMostRecentByFormContainer() {
					return Version.VERSION_1.concat(ROOT).concat(MOST_RECENT_BY_FORM_CONTAINER);
				}
			}
		}

		/**
		 * The Test Web Service mappings.
		 */
		public static final class Test {
			/**
			 * Test mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/test");
				public static final String HEALTH = ("/health");
				public static final String EXTENDED_HEALTH = ("/extended");
				public static final String TEST = ("/");

				/**
				 * Root for Test.
				 *
				 * @return {@code /test/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * Test the connection to the Fluid server.
				 *
				 * @return {@code v1/test/}
				 */
				public static final String testConnection() {
					return Version.VERSION_1.concat(ROOT);
				}

				/**
				 * Health and other server information.
				 *
				 * @return {@code v1/health/}
				 */
				public static final String healthAndServerInfo() {
					return Version.VERSION_1.concat(HEALTH);
				}

				/**
				 * Health and other server information.
				 *
				 * @return {@code v1/extended_health/}
				 */
				public static final String extendedHealthAndServerInfo() {
					return healthAndServerInfo().concat(EXTENDED_HEALTH);
				}
			}
		}

		/**
		 * The User Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.user.User
		 */
		public static final class User {
			/**
			 * User mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/user");

				public static final String INIT_SESSION = "/init";
				public static final String ISSUE_TOKEN = "/issue_token";
				public static final String TOKEN_STATUS = "/token_status";
				public static final String INFORMATION = "/info";
				public static final String AUTH_BASIC = "/auth_basic";
				public static final String AUTH0_USER_PROFILE = "/auth0_user_profile";

				//Create...
				public static final String CREATE = ("/");
				public static final String CREATE_ADMIN = ("/create_admin");

				//Update...
				public static final String UPDATE = ("/update");

				//Activate / DeActivate...
				public static final String DE_ACTIVATE = ("/de_activate");
				public static final String ACTIVATE = ("/activate");
				public static final String INCREMENT_INVALID_LOGIN = ("/increment_invalid_login");
				public static final String REQUEST_PASSWORD_RESET = ("/request_password_reset");
				public static final String CHANGE_PASSWORD = ("/change_password");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_BY_USERNAME = ("/get_by_username");
				public static final String READ_BY_EMAIL = ("/get_by_email");
				public static final String READ_USER_FIELD_VALUES_BY_USER = ("/get_user_field_values_by_user");
				public static final String READ_ALL = ("/get_all_users");
				public static final String READ_ALL_BY_JOB_VIEW = ("/get_all_users_by_job_view");
				public static final String READ_ALL_BY_ROLE = ("/get_all_users_by_role");
				public static final String READ_ALL_BY_LOGGED_IN_SINCE = ("/get_all_users_by_logged_in_since");

				//Gravatar...
				public static final String GET_GRAVATAR_BY_EMAIL = ("/get_gravatar_by_email");
				public static final String GET_GRAVATAR_BY_USER = ("/get_gravatar_by_user");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String USERNAME = "username";

					public static final String EMAIL = "email";
					public static final String SIZE = "size";

					//Auth0 params...
					public static final String CODE = "code";
					public static final String REDIRECT_URL = "redirect_url";
					public static final String HOST = "host";
					public static final String USER_AGENT = "user_agent";
				}

				/**
				 * Root for User.
				 *
				 * @return {@code /user/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * Authenticate using BASIC auth.
				 *
				 * @return {@code /user/auth_basic}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userBasicAuth() {
					return Version.VERSION_1.concat(ROOT).concat(AUTH_BASIC);
				}

				/**
				 * Root for User initialise session.
				 *
				 * @return {@code /user/init}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userInitSession() {
					return Version.VERSION_1.concat(ROOT).concat(INIT_SESSION);
				}

				/**
				 * Construct URL request for Auth0 user profile based on params.
				 *
				 * @param codeParam The code generated from Auth0 login.
				 * @param redirectUrlParam The redirect URL post login.
				 * @param hostParam Optional host information.
				 * @param userAgentInfoParam Optional user-agent information.
				 *
				 * @return {@code /user/auth0_user_profile}
				 *
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 * 
				 * @see User
				 */
				public static final String loginAuth0UserProfile(
					String codeParam,
					String redirectUrlParam,
					String hostParam,
					String userAgentInfoParam
				) throws UnsupportedEncodingException {
					StringBuilder completeUrl =
							new StringBuilder(Version.VERSION_1.concat(ROOT).concat(AUTH0_USER_PROFILE));
					completeUrl.append("?");

					if (codeParam != null) {
						completeUrl.append(QueryParam.CODE);
						completeUrl.append("=");
						completeUrl.append(codeParam);
					}

					if (redirectUrlParam != null) {
						completeUrl.append("&");
						completeUrl.append(QueryParam.REDIRECT_URL);
						completeUrl.append("=");
						redirectUrlParam = URLEncoder.encode(redirectUrlParam, ENCODING_UTF_8);
						completeUrl.append(redirectUrlParam);
					}

					if (hostParam != null) {
						completeUrl.append("&");
						completeUrl.append(QueryParam.HOST);
						completeUrl.append("=");
						hostParam = URLEncoder.encode(hostParam, ENCODING_UTF_8);
						completeUrl.append(hostParam);
					}

					if (userAgentInfoParam != null) {
						completeUrl.append("&");
						completeUrl.append(QueryParam.USER_AGENT);
						completeUrl.append("=");
						userAgentInfoParam = URLEncoder.encode(userAgentInfoParam, ENCODING_UTF_8);
						completeUrl.append(userAgentInfoParam);
					}
					
					return completeUrl.toString();
				}

				/**
				 * Root for User issuing token.
				 *
				 * @return {@code /user/issue_token}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userIssueToken() {
					return Version.VERSION_1.concat(ROOT).concat(ISSUE_TOKEN);
				}

				/**
				 * Root for User token status.
				 *
				 * @return {@code /user/token_status}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userTokenStatus() {
					return Version.VERSION_1.concat(ROOT).concat(TOKEN_STATUS);
				}

				/**
				 * Root for User information.
				 *
				 * @return {@code /user/info}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userInformation() {
					return Version.VERSION_1.concat(ROOT).concat(INFORMATION);
				}

				/**
				 * URL Path for User create.
				 *
				 * @return {@code /v1/user/}
				 */
				public static final String userCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Administrator User create.
				 *
				 * @return {@code /v1/user/create_admin}
				 */
				public static final String userCreateAdmin() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE_ADMIN);
				}

				/**
				 * URL Path for User update.
				 *
				 * @return {@code v1/user/update}
				 */
				public static final String userUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Deleting a User.
				 *
				 * @return {@code v1/user/delete}
				 */
				public static final String userDelete() {
					return userDelete(false);
				}

				/**
				 * URL Path for User delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/user/delete?force=forceDeleteParam} <b>with / without</b>
				 *         force.
				 */
				public static final String userDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for De-Activating a User.
				 *
				 * @return {@code v1/user/de_activate}
				 */
				public static final String userDeActivate() {
					return Version.VERSION_1.concat(ROOT).concat(DE_ACTIVATE);
				}

				/**
				 * URL Path for Incrementing the invalid login count for a User.
				 *
				 * @return {@code v1/user/increment_invalid_login}
				 */
				public static final String incrementInvalidLogin() {
					return Version.VERSION_1.concat(ROOT).concat(INCREMENT_INVALID_LOGIN);
				}

				/**
				 * URL Path for requesting password reset for a User.
				 *
				 * @return {@code v1/user/request_password_reset}
				 */
				public static final String requestPasswordReset() {
					return Version.VERSION_1.concat(ROOT).concat(REQUEST_PASSWORD_RESET);
				}

				/**
				 * URL Path for changing a User password.
				 *
				 * @return {@code v1/user/change_password}
				 */
				public static final String changePassword() {
					return Version.VERSION_1.concat(ROOT).concat(CHANGE_PASSWORD);
				}

				/**
				 * URL Path for De-Activating a User.
				 *
				 * @return {@code v1/user/activate}
				 */
				public static final String userActivate() {
					return Version.VERSION_1.concat(ROOT).concat(ACTIVATE);
				}

				/**
				 * URL Path for User get by id.
				 *
				 * @return {@code v1/user/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for User get by username.
				 *
				 * @return {@code v1/user/get_by_username}
				 */
				public static final String getByUsername() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_USERNAME);
				}

				/**
				 * URL Path for User get by Email.
				 *
				 * @return {@code v1/user/get_by_email}
				 */
				public static final String getByEmail() {
					return Version.VERSION_1.concat(ROOT).concat(READ_BY_EMAIL);
				}

				/**
				 * URL Path for User Field Values by User.
				 *
				 * @return {@code v1/user/get_user_field_values_by_user}
				 */
				public static final String getUserFieldValuesByUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ_USER_FIELD_VALUES_BY_USER);
				}

				/**
				 * URL Path for User get by id.
				 *
				 * @return {@code v1/user/get_all_users}
				 */
				public static final String getAllUsers() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}

				/**
				 * URL Path for Users get by {@code JobView}.
				 *
				 * @return {@code v1/user/get_all_users_by_job_view}
				 */
				public static final String getAllUsersByJobView() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_JOB_VIEW);
				}

				/**
				 * URL Path for Users get by {@code Role}.
				 *
				 * @return {@code v1/user/get_all_users_by_role}
				 */
				public static final String getAllUsersByRole() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_ROLE);
				}

				/**
				 * URL Path for Users get by last logged in.
				 *
				 * @return {@code v1/user/get_all_users_by_logged_in_since}
				 */
				public static final String getAllUsersWhereLoggedInSince() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_LOGGED_IN_SINCE);
				}

				/**
				 * URL Path for User gravatar by email address.
				 *
				 * @param emailParam The email to retrieve gravatar for.
				 * @param sizeParam The size of the image to be returned.
				 *
				 * @return {@code v1/user/get_gravatar_by_email?email=john@company.com&size=50}.
				 *
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String getGravatarByEmail(String emailParam, int sizeParam) throws UnsupportedEncodingException {
					StringBuffer returnVal = new StringBuffer(Version.VERSION_1.concat(ROOT).concat(GET_GRAVATAR_BY_EMAIL));

					returnVal.append("?");
					String encodedEmail = "";
					if (emailParam != null) {
						encodedEmail = URLEncoder.encode(emailParam, ENCODING_UTF_8);
					}

					if (sizeParam < 1 || sizeParam > 512) {
						sizeParam = 50;
					}

					returnVal.append(QueryParam.EMAIL);
					returnVal.append("=");
					returnVal.append(encodedEmail);
					returnVal.append("&");
					returnVal.append(QueryParam.SIZE);
					returnVal.append("=");
					returnVal.append(sizeParam);

					return returnVal.toString();
				}

				/**
				 * URL Path for User gravatar by user.
				 *
				 * @param size The size of the image to be returned.
				 *
				 * @return {@code v1/user/get_gravatar_by_user?size=50}.
				 */
				public static final String getGravatarByUser(int size) {
					StringBuffer returnVal = new StringBuffer(Version.VERSION_1.concat(ROOT).concat(GET_GRAVATAR_BY_USER));
					returnVal.append("?");
					if (size < 1 || size > 512) size = 50;

					returnVal.append(QueryParam.SIZE);
					returnVal.append("=");
					returnVal.append(size);

					return returnVal.toString();
				}
			}
		}

		/**
		 * The User Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.user.UserNotification
		 */
		public static final class UserNotification {
			/**
			 * User mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/user_notification");

				//Create...
				public static final String CREATE = ("/");

				//Update...
				public static final String UPDATE = ("/update");

				//Mark Notification as Read...
				public static final String MARK_AS_READ = ("/mark_as_read");

				//Delete...
				public static final String DELETE = ("/delete");

				//Read...
				public static final String READ = ("/get_by_id");

				public static final String READ_ALL_BY_USER_AND_UNREAD = ("/get_by_user_and_unread");
				public static final String READ_ALL_BY_USER_AND_READ = ("/get_by_user_and_read");
				public static final String READ_ALL_BY_USER_AND_DATE = ("/get_by_user_and_date");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String QUERY_LIMIT = "query_limit";
					public static final String OFFSET = "offset";
					public static final String DATE_FROM = "date_from";
					public static final String DATE_TO = "date_to";
				}

				/**
				 * Root for User Notification.
				 *
				 * @return {@code /user_notification/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for User Notification create.
				 *
				 * @return {@code /v1/user_notification/}
				 */
				public static final String userNotificationCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for User Notification update.
				 *
				 * @return {@code v1/user_notification/update}
				 */
				public static final String userNotificationUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Deleting a User Notification.
				 *
				 * @return {@code v1/user_notification/delete}
				 */
				public static final String userNotificationDelete() {
					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for marking User Notification as read.
				 *
				 * Mark notification as read can be done asynchronously.
				 *
				 * @param asyncParam Should the notification mark as read asynchronously.
				 *
				 * @return {@code v1/user_notification/mark_as_read?async={asyncParam}}
				 */
				public static final String userNotificationMarkAsRead(boolean asyncParam) {
					String additionString = "?";

					//Asynchronous...
					additionString += WS.QueryParam.ASYNC;
					additionString += "=";
					additionString += asyncParam;

					return Version.VERSION_1.concat(ROOT).concat(MARK_AS_READ.concat(additionString));
				}

				/**
				 * URL Path for User Notification get by id.
				 *
				 * @return {@code v1/user_notification/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Un-Read User Notifications by User.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 *
				 * @return {@code v1/user_notification/get_by_user_and_unread}
				 */
				public static final String getAllUnReadByUser(int queryLimitParam, int offsetParam) {

					String base = Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_USER_AND_UNREAD);

					String additionString = "?";

					if (queryLimitParam > 0) {
						additionString += QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += queryLimitParam;
						additionString += "&";
					}

					if (offsetParam > -1) {
						additionString += QueryParam.OFFSET;
						additionString += "=";
						additionString += offsetParam;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}

				/**
				 * URL Path for Read User Notifications by User.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 *
				 * @return {@code v1/user_notification/get_by_user_and_read}
				 */
				public static final String getAllReadByUser(int queryLimitParam, int offsetParam) {

					String base = Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_USER_AND_READ);

					String additionString = "?";

					if (queryLimitParam > 0) {
						additionString += QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += queryLimitParam;
						additionString += "&";
					}

					if (offsetParam > -1) {
						additionString += QueryParam.OFFSET;
						additionString += "=";
						additionString += offsetParam;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}

				/**
				 * URL Path for Read User Notifications by User and Date.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 * @param dateFromParam The from date in millis.
				 * @param dateToParam The to date in millis.
				 *
				 * @return {@code v1/user_notification/get_by_user_and_date}
				 */
				public static final String getAllByUserAndDate(
						int queryLimitParam,
						int offsetParam,
						long dateFromParam,
						long dateToParam) {

					String base = Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_USER_AND_DATE);

					String additionString = "?";

					if (queryLimitParam > 0) {
						additionString += QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += queryLimitParam;
						additionString += "&";
					}

					if (offsetParam > -1) {
						additionString += QueryParam.OFFSET;
						additionString += "=";
						additionString += offsetParam;
						additionString += "&";
					}

					additionString += QueryParam.DATE_FROM;
					additionString += "=";
					additionString += dateFromParam;
					additionString += "&";

					additionString += QueryParam.DATE_TO;
					additionString += "=";
					additionString += dateToParam;
					additionString += "&";

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}
			}
		}

		/**
		 * The Role Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.role.Role
		 */
		public static final class Role {
			/**
			 * Role mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/role");

				//Create...
				public static final String CREATE = ("/");

				//Update...
				public static final String UPDATE = ("/update");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_ALL = ("/get_all_roles");

				/**
				 * Root for Role.
				 *
				 * @return {@code /role/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Role create.
				 *
				 * @return {@code /v1/role/}
				 */
				public static final String roleCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for Role update.
				 *
				 * @return {@code v1/role/update}
				 */
				public static final String roleUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Deleting a Role.
				 *
				 * @return {@code v1/role/delete}
				 */
				public static final String roleDelete() {
					return roleDelete(false);
				}

				/**
				 * URL Path for Role delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/role/delete?force=forceDeleteParam} <b>with / without</b>
				 *         force.
				 */
				public static final String roleDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}

					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for Role get by id.
				 *
				 * @return {@code v1/role/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Role get all.
				 *
				 * @return {@code v1/role/get_all_roles}
				 */
				public static final String getAllRoles() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}
			}
		}

		/**
		 * The Mandrill Inbound Mail Web Service mappings.
		 */
		public static final class MandrillInbound {
			/**
			 * Mandrill mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/mandrill_inbound");

				//Create...
				public static final String CONSUME = ("/");

				/**
				 * Root for Mandrill inbound.
				 *
				 * @return {@code /mandrill_inbound/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}
			}
		}

		/**
		 * The SendGrid Inbound Mail Web Service mappings.
		 */
		public static final class SendGridInbound {
			/**
			 * SendGrid mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/sendgrid_inbound");

				//Create...
				public static final String CONSUME = ("/");

				/**
				 * Root for SendGrid inbound.
				 *
				 * @return {@code /sendgrid_inbound/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}
			}
		}

		/**
		 * The Payment Web Service mappings.
		 */
		public static final class Payment {
			/**
			 * Adyen mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/payment");
				public static final String ADYEN = ("/adyen");

				public static final String ADYEN_PAYMENT_NOTIFICATION = (ADYEN + "/notification");
				public static final String ADYEN_PAYMENT_LINK_REQUEST = (ADYEN + "/payment_link_request");

				//Create...
				public static final String CONSUME = ("/");

				/**
				 * Root for Payments.
				 *
				 * @return {@code /payment/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for requesting a Adyen Payment Link.
				 *
				 * @return {@code v1/payment/adyen/payment_link_request}
				 */
				public static final String adyenPaymentLinkRequest() {
					return String.format("%s%s%s", Version.VERSION_1, ROOT, ADYEN_PAYMENT_LINK_REQUEST);
				}
			}
		}

		/**
		 * The Configuration Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.config.Configuration
		 */
		public static final class Configuration {
			/**
			 * Role mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/configuration");

				//Read...
				public static final String READ = ("/get_by_key");
				public static final String READ_ALL = ("/get_all_configurations");
				public static final String READ_SYSTEM_MAIL_TRANSFER = ("/get_system_mail_transfer");

				public static final String READ_ALL_TASK_IDENTIFIERS = ("/get_all_third_party_task_identifiers");


				//Update/Insert...
				public static final String UPSERT = ("/upsert");

				/**
				 * Root for Configuration.
				 *
				 * @return {@code /configuration/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Configuration get by id.
				 *
				 * @return {@code v1/configuration/get_by_key}
				 */
				public static final String getByKey() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for Configuration get all.
				 *
				 * @return {@code v1/configuration/get_all_configurations}
				 */
				public static final String getAllConfigurations() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}

				/**
				 * URL Path for retrieving the system email transfers.
				 *
				 * @return {@code v1/configuration/get_system_mail_transfer}
				 */
				public static final String getSystemMailTransfer() {
					return Version.VERSION_1.concat(ROOT).concat(READ_SYSTEM_MAIL_TRANSFER);
				}

				/**
				 * URL Path for retrieving all 3rd party programs task identifiers.
				 *
				 * @return {@code v1/configuration/get_all_third_party_task_identifiers}
				 */
				public static final String getAllThirdPartyTaskIdentifiers() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_TASK_IDENTIFIERS);
				}

				/**
				 * URL Path for Config create.
				 *
				 * @return {@code /v1/configuration/upsert}
				 */
				public static final String configUpsert() {
					return Version.VERSION_1.concat(ROOT).concat(UPSERT);
				}
			}
		}

		/**
		 * The Custom Runner Destination Config Web Service mappings.
		 */
		public static final class CustomRunnerDestinationConfig {
			/**
			 * Custom runner mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/custom_runner/destination_config");

				//Read...
				public static final String READ = ("/get_environment_config");

				//Write...
				public static final String LOCK = ("/lock_environment_config");

				/**
				 * Root for Custom Runner Destination configuration.
				 *
				 * @return {@code /custom_runner/destination_config/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Customer Runner Destination configuration.
				 *
				 * @return {@code v1/custom_runner/destination_config/get_environment_config}
				 */
				public static final String getEnvironmentConfig() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for LOCKING Customer Runner Destination configuration.
				 *
				 * @return {@code v1/custom_runner/destination_config/lock_environment_config}
				 */
				public static final String lockEnvironmentConfig() {
					return Version.VERSION_1.concat(ROOT).concat(LOCK);
				}
			}
		}

		/**
		 * The Fluid Custom Runner Web Socket mappings.
		 */
		public static final class CustomRunner {
			/**
			 * Custom runner mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/custom_runner");

				// [/web_socket/v1/custom_runner]
				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

				//Custom Web...
				public static final String CUSTOM_WEB = ("/custom_web");
				// [/web_socket/v1/custom_runner/custom_web]
				public static final String CUSTOM_WEB_WEB_SOCKET = (ROOT_WEB_SOCKET + CUSTOM_WEB);

				//Custom Flow Program...
				public static final String CUSTOM_FLOW_PROGRAM = ("/custom_flow_program");
				// [/web_socket/v1/custom_runner/custom_flow_program]
				public static final String CUSTOM_FLOW_PROGRAM_WEB_SOCKET = (ROOT_WEB_SOCKET + CUSTOM_FLOW_PROGRAM);

				//Custom Flow Program...
				public static final String CUSTOM_SCHEDULE = ("/custom_schedule");
				// [/web_socket/v1/custom_runner/custom_schedule]
				public static final String CUSTOM_SCHEDULE_WEB_SOCKET = (ROOT_WEB_SOCKET + CUSTOM_SCHEDULE);

				//Custom View Filter...
				public static final String CUSTOM_VIEW_FILTER = ("/custom_view_filter");
				// [/web_socket/v1/custom_runner/custom_view_filter]
				public static final String CUSTOM_VIEW_FILTER_WEB_SOCKET = (ROOT_WEB_SOCKET + CUSTOM_VIEW_FILTER);

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class PathParam {
					public static final String TASK_IDENTIFIER = "taskIdentifier";
				}

				/**
				 * Root for Custom Runner.
				 *
				 * @return {@code /custom_runner/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for Custom Running a Web Form Action via Web Socket.
				 *
				 * @param taskIdentifierParam The task identifier to execute.
				 *
				 * @return {@code web_socket/v1/custom_runner/custom_web/}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String executeCustomWebWebSocket(String taskIdentifierParam) throws UnsupportedEncodingException {
					String encodedValue = "";
					if (taskIdentifierParam != null) {
						encodedValue = URLEncoder.encode(taskIdentifierParam, ENCODING_UTF_8);
					}

					String returnVal = (CUSTOM_WEB_WEB_SOCKET + "/" + encodedValue + "/");

					return returnVal;
				}

				/**
				 * URL Path for Custom Running a Flow Program via Web Socket.
				 *
				 * @param taskIdentifierParam The task identifier to execute.
				 *
				 * @return {@code web_socket/v1/custom_runner/custom_flow_program/}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String executeCustomFlowProgramWebSocket(String taskIdentifierParam) throws UnsupportedEncodingException {
					String encodedValue = "";
					if (taskIdentifierParam != null) {
						encodedValue = URLEncoder.encode(taskIdentifierParam, ENCODING_UTF_8);
					}

					String returnVal = (CUSTOM_FLOW_PROGRAM_WEB_SOCKET + "/" + encodedValue + "/");

					return returnVal;
				}

				/**
				 * URL Path for Custom Running a Flow Program via Web Socket.
				 *
				 * @param taskIdentifierParam The task identifier to execute.
				 *
				 * @return {@code web_socket/v1/custom_runner/custom_schedule/}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String executeCustomScheduleWebSocket(String taskIdentifierParam) throws UnsupportedEncodingException {
					String encodedValue = "";
					if (taskIdentifierParam != null) {
						encodedValue = URLEncoder.encode(taskIdentifierParam, ENCODING_UTF_8);
					}

					String returnVal = (CUSTOM_SCHEDULE_WEB_SOCKET + "/" + encodedValue + "/");

					return returnVal;
				}

				/**
				 * URL Path for Custom Running a View Filter via Web Socket.
				 *
				 * @param viewIdentifierParam The custom view identifier to execute.
				 *
				 * @return {@code web_socket/v1/custom_runner/custom_view_filter/}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String executeCustomViewFilterWebSocket(String viewIdentifierParam) throws UnsupportedEncodingException {
					String encodedValue = "";
					if (viewIdentifierParam != null) encodedValue = URLEncoder.encode(viewIdentifierParam, ENCODING_UTF_8);

					String returnVal = (CUSTOM_VIEW_FILTER_WEB_SOCKET + "/" + encodedValue + "/");
					return returnVal;
				}
			}
		}

		/**
		 * The SQL Util Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormDefinitionUtil
		 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil
		 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormUtil
		 */
		public static final class SQLUtil {
			/**
			 * User Query mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/sql_util");

				public static final String ROOT_WEB_SOCKET = (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

				//[[[ EXECUTE ]]]...
				//Form...
				public static final String SQL_UTIL_FORM_GET_TABLE_FORMS = ("/form/get_table_forms_by_electronic_form_id");

				public static final String SQL_UTIL_FORM_GET_DESCENDANTS = ("/form/get_descendants_by_electronic_form_id");

				public static final String SQL_UTIL_FORM_GET_ANCESTOR = ("/form/get_ancestor_by_electronic_form_id");

				public static final String SQL_UTIL_FORM_EXECUTE_SQL = ("/form/execute_sql");

				//Field...
				public static final String SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER = ("/form_field/get_fields_by_electronic_form_id");

				//Native SQL
				public static final String SQL_UTIL_NATIVE_QUERY = ("/native/execute_query");

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String INCLUDE_FIELD_DATA = "include_field_data";
					public static final String FORM_DEFINITION = "form_definition";
					public static final String INCLUDE_TABLE_FIELDS = "include_table_fields";
					public static final String INCLUDE_TABLE_FIELD_FORM_RECORD_INFO = "include_table_field_form_record_info";
					public static final String MASS_FETCH = "mass_fetch";

					public static final String CONNECTION_ALIAS = "connection_alias";

					public static final String COMPRESS_RESPONSE = "compress_response";
					public static final String COMPRESS_RESPONSE_CHARSET = "compress_response_charset";
				}

				/**
				 * Mapping for frequently used HTTP or Web Socket Path parameters.
				 */
				public static final class PathParam {
					public static final String SERVICE_TICKET = "serviceTicket";
				}

				/**
				 * Root for package {@code com.fluidbpm.program.api.util.sql.impl}.
				 *
				 * @return {@code /sql_util/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for retrieving Table records for electronic form with id
				 * {@code electronicFormIdParam}.
				 *
				 * @param includeFieldData Does field data need to be included.
				 * @param formDefinitionId Optional Form Definition Id filter.
				 *
				 * @return {@code v1/sql_util/form/get_table_forms_by_electronic_form_id}
				 */
				public static final String getTableForms(
					boolean includeFieldData,
					@NonNull
					Optional<Long> formDefinitionId
				) {
					StringBuilder strBld = new StringBuilder(Version.VERSION_1)
							.append(ROOT)
							.append(SQL_UTIL_FORM_GET_TABLE_FORMS)
							.append("?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldData);

					formDefinitionId.ifPresent(id -> strBld.append("&"+QueryParam.FORM_DEFINITION + "=" + id));

					return strBld.toString();
				}

				/**
				 * URL Path for retrieving Table records for electronic form with id
				 * {@code electronicFormIdParam}.
				 *
				 * @param includeFieldDataParam Does field data need to be included.
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the Descendant result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 * @param formDefinitionId Optional Form Definition Id filter.
				 *
				 * @return {@code /web_socket/v1/sql_util/form/get_table_forms_by_electronic_form_id}
				 */
				public static final String getTableFormsWebSocket(
					boolean includeFieldDataParam,
					String serviceTicketParam,
					boolean compressResponseParam,
					String compressResponseCharsetParam,
					Long formDefinitionId
				) {
					String returnVal = ROOT_WEB_SOCKET
							.concat(SQL_UTIL_FORM_GET_TABLE_FORMS)
							.concat("/" + serviceTicketParam + "?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldDataParam)
							.concat("&" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam)
							.concat("&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					if (formDefinitionId != null && formDefinitionId.longValue() > 0) {
						returnVal += ("&"+QueryParam.FORM_DEFINITION + "=" + formDefinitionId);
					}

					return returnVal;
				}

				/**
				 * URL Path for retrieving descendants for electronic form with id
				 * {@code electronicFormIdParam}.
				 *
				 * @param includeFieldDataParam Does field data need to be included.
				 * @param includeTableFieldsParam Does table field data need to be included.
				 * @param inclTableFieldFormInfoParam Include table record field info.
				 *
				 * @return {@code v1/sql_util/form/get_descendants_by_electronic_form_id}
				 */
				public static final String getDescendants(boolean includeFieldDataParam, boolean includeTableFieldsParam, boolean inclTableFieldFormInfoParam) {
					return Version.VERSION_1.concat(ROOT).concat(SQL_UTIL_FORM_GET_DESCENDANTS)
							.concat("?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldDataParam + "&" + QueryParam.INCLUDE_TABLE_FIELDS + "=" + includeTableFieldsParam + "&"
									+ QueryParam.INCLUDE_TABLE_FIELD_FORM_RECORD_INFO + "=" + inclTableFieldFormInfoParam);
				}

				/**
				 * URL Path for retrieving Table records for electronic form with id
				 * {@code electronicFormIdParam}.
				 *
				 * @param includeFieldDataParam Does field data need to be included.
				 * @param includeTableFieldsParam Does table field data need to be included.
				 * @param includeTableFieldFormRecordInfoParam Does table record form data need
				 *            to be included.
				 * @param massFetchParam Is the fetch a large fetch.
				 *
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the Descendant result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *
				 * @return {@code /web_socket/v1/sql_util/form/get_descendants_by_electronic_form_id}
				 */
				public static final String getDescendantsWebSocket(boolean includeFieldDataParam, boolean includeTableFieldsParam, boolean includeTableFieldFormRecordInfoParam,
						boolean massFetchParam, String serviceTicketParam, boolean compressResponseParam,
																   String compressResponseCharsetParam) {
					String returnVal = ROOT_WEB_SOCKET.concat(SQL_UTIL_FORM_GET_DESCENDANTS)
							.concat("/" + serviceTicketParam + "?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldDataParam + "&" + QueryParam.MASS_FETCH + "="
									+ massFetchParam + "&" + QueryParam.INCLUDE_TABLE_FIELDS + "=" + includeTableFieldsParam + "&" + QueryParam.INCLUDE_TABLE_FIELD_FORM_RECORD_INFO
									+ "=" + includeTableFieldFormRecordInfoParam + "&" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam
									+"&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					return returnVal;
				}

				/**
				 * URL Path for executing SQL on the Fluid core database.
				 *
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the SQL Result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *
				 * @return {@code /web_socket/v1/sql_util/form/execute_sql}
				 */
				public static final String getExecuteSQLWebSocket(
						String serviceTicketParam, boolean compressResponseParam,
						String compressResponseCharsetParam) {

					String returnVal = ROOT_WEB_SOCKET.concat(SQL_UTIL_FORM_EXECUTE_SQL)
							.concat("/" + serviceTicketParam + "?" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam+
									"&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					return returnVal;
				}

				/**
				 * URL Path for retrieving ancestor for electronic form.
				 *
				 * @param includeFieldDataParam Does field data need to be included.
				 * @param includeTableFieldsParam Does table field data need to be included.
				 *
				 * @return {@code v1/sql_util/form/get_ancestor_by_electronic_form_id}
				 */
				public static final String getAncestor(boolean includeFieldDataParam, boolean includeTableFieldsParam) {
					return Version.VERSION_1.concat(ROOT).concat(SQL_UTIL_FORM_GET_ANCESTOR)
							.concat("?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldDataParam + "&" + QueryParam.INCLUDE_TABLE_FIELDS + "=" + includeTableFieldsParam);
				}

				/**
				 * URL Path for retrieving ancestor data via Web Socket.
				 *
				 * @param includeFieldDataParam Does field data need to be included.
				 * @param includeTableFieldsParam Does table field data need to be included.
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the Ancestor Result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *                                                 
				 * @return {@code /web_socket/v1/sql_util/form/get_ancestor_by_electronic_form_id}
				 */
				public static final String getAncestorWebSocket(
						boolean includeFieldDataParam,
						boolean includeTableFieldsParam,
						String serviceTicketParam,
						boolean compressResponseParam,
						String compressResponseCharsetParam) {
					String returnVal = ROOT_WEB_SOCKET.concat(SQL_UTIL_FORM_GET_ANCESTOR)
							.concat("/" + serviceTicketParam + "?" + QueryParam.INCLUDE_FIELD_DATA + "=" + includeFieldDataParam + "&" + QueryParam.INCLUDE_TABLE_FIELDS + "="
									+ includeTableFieldsParam + "&" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam +
									"&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					return returnVal;
				}

				/**
				 * URL Path for retrieving Form Fields for electronic form.
				 *
				 * @param includeTableFieldsParam Does Table Field data need to be included?
				 *
				 * @return {@code v1/sql_util/form_field/get_fields_by_electronic_form_id}
				 */
				public static final String getFormFields(boolean includeTableFieldsParam) {
					return Version.VERSION_1.concat(ROOT).concat(SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER)
							.concat("?" + QueryParam.INCLUDE_TABLE_FIELDS + "=" + includeTableFieldsParam);
				}

				/**
				 * URL Path for retrieving Form Fields for electronic form using a Web Socket.
				 *
				 * @param includeTableFieldsParam Does Table Field data need to be included?
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the Form Field Result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *
				 * @return {@code /web_socket/v1/sql_util/form_field/get_fields_by_electronic_form_id}
				 */
				public static final String getFormFieldsWebSocket(
						boolean includeTableFieldsParam,
						String serviceTicketParam,
						boolean compressResponseParam,
						String compressResponseCharsetParam) {
					String returnVal = ROOT_WEB_SOCKET.concat(SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER).concat("/" + serviceTicketParam + "?" + QueryParam.INCLUDE_TABLE_FIELDS + "="
							+ includeTableFieldsParam + "&" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam +
							"&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					return returnVal;
				}

				/**
				 * URL Path for executing native SQL queries.
				 *
				 * @return {@code v1/sql_util/native/execute_query}
				 *
				 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery
				 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet
				 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn
				 * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow
				 */
				public static String getExecuteNativeSQL() {
					return Version.VERSION_1.concat(ROOT).concat(SQL_UTIL_NATIVE_QUERY);
				}

				/**
				 * URL Path for executing native SQL queries for web-sockets.
				 *
				 * @param serviceTicketParam The service ticket in hex-decimal text format.
				 * @param compressResponseParam Compress the SQL Result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *
				 * @return {@code /web_socket/v1/sql_util/native/execute_query}
				 *
				 * @see com.fluidbpm.program.api.vo.compress.CompressedResponse
				 */
				public static String getExecuteNativeSQLWebSocket(
						String serviceTicketParam,
						boolean compressResponseParam,
						String compressResponseCharsetParam
				) {
					String returnVal = ROOT_WEB_SOCKET.concat(SQL_UTIL_NATIVE_QUERY)
							.concat("/" + serviceTicketParam + "?" + QueryParam.COMPRESS_RESPONSE + "=" + compressResponseParam +
									"&"+QueryParam.COMPRESS_RESPONSE_CHARSET + "=" + compressResponseCharsetParam);

					return returnVal;
				}
			}
		}

		/**
		 * The User Query Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.userquery.UserQuery
		 */
		public static final class UserQuery {
			/**
			 * User Query mappings.
			 */
			public static final class Version1 {
				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String FETCH_INPUTS = "fetch_inputs";
					public static final String POPULATE_ANCESTOR_ID = "populate_ancestor_id";
					public static final String FORCE_USE_DATABASE = "force_use_database";

					//List Job View content...
					public static final String QUERY_LIMIT = "query_limit";
					public static final String OFFSET = "offset";
					public static final String EXECUTE_CALCULATED_LABELS = "execute_calculated_labels";
				}

				public static final String ROOT = ("/user_query");

				//Create...
				public static final String CREATE = ("/");

				//Update...
				public static final String UPDATE = ("/update");
				public static final String UPSERT_WEB_KIT = ("/upsert_web_kit");

				//Delete...
				public static final String DELETE = ("/delete");
				public static final String DELETE_FORCE = ("/delete?force=true");

				//Read...
				public static final String READ = ("/get_by_id");
				public static final String READ_ALL = ("/get_all_user_queries");
				public static final String READ_ALL_WEB_KIT = ("/get_all_user_queries_web_kit");

				public static final String READ_ALL_USER_QUERIES_BY_LOGGED_IN_USER = ("/get_all_user_queries_by_logged_in_user");

				//Execute...
				public static final String EXECUTE = ("/execute");
				public static final String EXECUTE_POPULATE_ANCESTOR_ID = ("/execute?" + POPULATE_ANCESTOR_ID + "=true");

				/**
				 * Root for {@code UserQuery}.
				 *
				 * @return {@code /user_query/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for executing a {@code UserQuery}.
				 *
				 * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
				 * @param executeCalculatedLabels Execute the calculate labels.
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 *
				 * @return {@code v1/user_query/execute}
				 */
				public static final String executeUserQuery(
					boolean populateAncestorIdParam,
					boolean executeCalculatedLabels,
					int queryLimitParam,
					int offsetParam
				) {

					return executeUserQuery(
							populateAncestorIdParam,
							false,
							executeCalculatedLabels,
							queryLimitParam,
							offsetParam);
				}

				/**
				 * URL Path for executing a {@code UserQuery}.
				 *
				 * @param populateAncestorIdParam - Whether the ancestor id should be populated
				 *            (when applicable).
				 * @param forceUseDatabaseParam Force to use underlying database.
				 * @param executeCalculatedLabels Execute the calculate labels.
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 *
				 * @return {@code v1/user_query/execute}
				 */
				public static final String executeUserQuery(
					boolean populateAncestorIdParam,
					boolean forceUseDatabaseParam,
					boolean executeCalculatedLabels,
					int queryLimitParam,
					int offsetParam
				) {
					String base = Version.VERSION_1.concat(ROOT).concat(EXECUTE);
					String additionString = "?";

					additionString += QueryParam.FORCE_USE_DATABASE;
					additionString += "=";
					additionString += forceUseDatabaseParam;
					additionString += "&";

					additionString += QueryParam.POPULATE_ANCESTOR_ID;
					additionString += "=";
					additionString += populateAncestorIdParam;
					additionString += "&";

					additionString += QueryParam.EXECUTE_CALCULATED_LABELS;
					additionString += "=";
					additionString += executeCalculatedLabels;
					additionString += "&";

					if (queryLimitParam > 0) {
						additionString += QueryParam.QUERY_LIMIT;
						additionString += "=";
						additionString += queryLimitParam;
						additionString += "&";
					}

					if (offsetParam > -1) {
						additionString += QueryParam.OFFSET;
						additionString += "=";
						additionString += offsetParam;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}

				/**
				 * URL Path for executing a {@code UserQuery}.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 * @param forceUseDatabaseParam Force to use underlying database.
				 *
				 * @return {@code v1/user_query/execute}
				 */
				public static final String executeUserQuery(
					int queryLimitParam,
					int offsetParam,
					boolean forceUseDatabaseParam
				) {
					return executeUserQuery(
							true,
							forceUseDatabaseParam,
							queryLimitParam,
							offsetParam);
				}

				/**
				 * URL Path for executing a {@code UserQuery}.
				 *
				 * @param queryLimitParam The query limit.
				 * @param offsetParam The query offset.
				 *
				 * @return {@code v1/user_query/execute}
				 */
				public static final String executeUserQuery(int queryLimitParam, int offsetParam) {
					return executeUserQuery(true, false, queryLimitParam ,offsetParam);
				}

				/**
				 * URL Path for {@code UserQuery} create.
				 *
				 * @return {@code /v1/user_query/}
				 */
				public static final String userQueryCreate() {
					return Version.VERSION_1.concat(ROOT).concat(CREATE);
				}

				/**
				 * URL Path for {@code UserQuery} update.
				 *
				 * @return {@code v1/user_query/update}
				 */
				public static final String userQueryUpdate() {
					return Version.VERSION_1.concat(ROOT).concat(UPDATE);
				}

				/**
				 * URL Path for Deleting a {@code UserQuery}.
				 *
				 * @return {@code v1/user_query/delete}
				 */
				public static final String userQueryDelete() {
					return userQueryDelete(false);
				}

				/**
				 * URL Path for {@code UserQuery} delete.
				 *
				 * @param forceDeleteParam Whether to forcefully delete.
				 *
				 * @return {@code v1/user_query/delete?force=forceDeleteParam} <b>with /
				 *         without</b> force.
				 */
				public static final String userQueryDelete(boolean forceDeleteParam) {
					if (forceDeleteParam) {
						return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
					}
					return Version.VERSION_1.concat(ROOT).concat(DELETE);
				}

				/**
				 * URL Path for {@code UserQuery} get by id.
				 *
				 * @return {@code v1/user_query/get_by_id}
				 */
				public static final String getById() {
					return Version.VERSION_1.concat(ROOT).concat(READ);
				}

				/**
				 * URL Path for UserQuery get all.
				 *
				 * @return {@code v1/user_query/get_all_user_queries}
				 */
				public static final String getAllUserQueries() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
				}

				/**
				 * URL Path for UserQuery get all with WebKit properties.
				 *
				 * @return {@code v1/user_query/get_all_user_queries_web_kit}
				 */
				public static final String getAllUserQueriesWebKit() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_WEB_KIT);
				}

				/**
				 * URL Path for UserQuery get all by logged in {@code User}.
				 *
				 * @return {@code v1/user_query/get_all_user_queries_by_logged_in_user}
				 */
				public static final String getAllUserQueriesByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT).concat(READ_ALL_USER_QUERIES_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for upserting the User Query web kit.
				 *
				 * @return {@code v1/user_query/upsert_web_kit}
				 */
				public static final String userQueryWebKitUpsert() {
					return Version.VERSION_1.concat(ROOT).concat(UPSERT_WEB_KIT);
				}
			}
		}

		/**
		 * Reporting Web Service mappings.
		 *
		 * @see Form
		 */
		public static final class Report {

			/**
			 * Report mappings.
			 */
			public static final class Version1 {
				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String TIMESTAMP_FROM = "timestamp_from";
					public static final String TIMESTAMP_TO = "timestamp_to";

					public static final String COMPRESS_RESPONSE = "compress_response";
					public static final String COMPRESS_RESPONSE_CHARSET = "compress_response_charset";
				}

				public static final String ROOT = ("/report");

				//Read...
				public static final String ROOT_USER_STATS = "/report/user_stats";
				public static final String ROOT_SYSTEM = "/report/system";
				public static final String READ_BY_LOGGED_IN_USER = ("/get_all_by_logged_in_user");
				public static final String READ_ALL_UP = ("/get_all_uptime");
				public static final String READ_ALL_DOWN = ("/get_all_downtime");
				public static final String READ_ALL = ("/get_all");

				/**
				 * Root for Report.
				 *
				 * @return {@code /report}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * URL Path for User statistics by logged in user.
				 *
				 * @return {@code v1/report/user_stats/get_all_by_logged_in_user}
				 */
				public static final String getUserStatsAllByLoggedInUser() {
					return Version.VERSION_1.concat(ROOT_USER_STATS).concat(READ_BY_LOGGED_IN_USER);
				}

				/**
				 * URL Path for system uptime entries.
				 *
				 * @return {@code v1/report/system/get_all_uptime}
				 */
				public static final String getAllSystemUptime() {
					return Version.VERSION_1.concat(ROOT_SYSTEM).concat(READ_ALL_UP);
				}

				/**
				 * URL Path for system downtime entries.
				 *
				 * @return {@code v1/report/system/get_all_downtime}
				 */
				public static final String getAllSystemDowntime() {
					return Version.VERSION_1.concat(ROOT_SYSTEM).concat(READ_ALL_DOWN);
				}

				/**
				 * URL Path for system downtime/uptime entries.
				 *
				 * @param compressResponseParam Compress the Descendant result in Base-64.
				 * @param compressResponseCharsetParam Compress response using provided charset.
				 *
				 * @return {@code v1/report/system/get_all}
				 */
				public static final String getAll(
					boolean compressResponseParam,
					String compressResponseCharsetParam
				) {
					if (compressResponseCharsetParam == null) {
						compressResponseCharsetParam = EMPTY;
					}

					String returnVal = Version.VERSION_1
							.concat(ROOT_SYSTEM)
							.concat(READ_ALL)
							.concat("?")
							.concat(QueryParam.COMPRESS_RESPONSE)
							.concat("=")
							.concat(Boolean.toString(compressResponseParam))
							.concat("&")
							.concat(QueryParam.COMPRESS_RESPONSE_CHARSET)
							.concat("=")
							.concat(compressResponseCharsetParam);
					return returnVal;
				}

				/**
				 * URL Path for executing the user statistics report.
				 *
				 * @param from - The timestamp to run the query from.
				 * @param to The timestamp to run the query until.
				 *
				 * @return {@code v1/user_query/execute}
				 */
				public static final String getUserStatsReportForLoggedInUser(
					long from,
					long to
				) {
					String base = Version.VERSION_1.concat(ROOT_USER_STATS).concat(READ_BY_LOGGED_IN_USER);
					String additionString = "?";

					if (from > 0) {
						additionString += QueryParam.TIMESTAMP_FROM;
						additionString += "=";
						additionString += from;
						additionString += "&";
					}

					if (to > 0) {
						additionString += QueryParam.TIMESTAMP_TO;
						additionString += "=";
						additionString += to;
						additionString += "&";
					}

					//Cut of the end bit...
					additionString = additionString.substring(0, additionString.length() - 1);

					return base.concat(additionString);
				}

			}
		}

		/**
		 * The Auth0 Web Service mappings.
		 *
		 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
		 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
		 * @see com.fluidbpm.program.api.vo.user.User
		 */
		public static final class Auth0 {
			/**
			 * Auth0 mappings.
			 */
			public static final class Version1 {
				public static final String ROOT = ("/oauth");

				public static final String TOKEN = "/token";
				public static final String USER_INFO_WITH_ACCESS_TOKEN = "/userinfo/?access_token=";
				public static final String USER_INFO = "/userinfo";

				/**
				 * Mapping for frequently used HTTP parameters.
				 */
				public static final class QueryParam {
					public static final String USERNAME = "username";
				}

				/**
				 * Root for 0Auth.
				 *
				 * @return {@code /oauth/}
				 */
				@Override
				public String toString() {
					return ROOT;
				}

				/**
				 * Root for User token.
				 *
				 * @return {@code /oath/token}
				 *
				 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
				 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
				 * @see com.fluidbpm.program.api.vo.user.User
				 */
				public static final String userToken() {
					return ROOT.concat(TOKEN);
				}

				/**
				 * Used to get {@code User} information via the {@code accessTokenValueParam}.
				 *
				 * @param accessTokenValueParam The access token to get user information from.
				 * @return {@code /oath/userinfo?access_token=accessTokenValueParam}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String userInfo(String accessTokenValueParam) throws UnsupportedEncodingException {
					String encodedValue = EMPTY;
					if (accessTokenValueParam != null) {
						encodedValue = URLEncoder.encode(accessTokenValueParam, ENCODING_UTF_8);
					}

					return USER_INFO_WITH_ACCESS_TOKEN.concat(encodedValue);
				}

				/**
				 * Used to get {@code User} information via the {@code accessTokenValueParam} in
				 * the HTTP header.
				 *
				 * @return {@code /oath/userinfo?access_token=accessTokenValueParam}
				 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
				 */
				public static final String userInfo() throws UnsupportedEncodingException {
					return USER_INFO;
				}
			}
		}
	}
}
