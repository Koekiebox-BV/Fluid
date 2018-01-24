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

import static com.fluidbpm.program.api.vo.ws.WS.Path.UserQuery.Version1.QueryParam.POPULATE_ANCESTOR_ID;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidVO;

/**
 * <p>
 *     The Mapping class used for all Fluid Representational State Transfer (REST)
 *     JSON Based Web Services.
 *
 *     More can be read at:
 *     {@code https://docs.oracle.com/javaee/6/tutorial/doc/gijqy.html}
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
    public static final class QueryParam
    {
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
    public static enum Intent
    {
        Create,
        Update,
        Delete,
        All;

        /**
         * Retrieves the allowed options for intent.
         *
         * @return The {@code enum} for {@code Intent}.
         */
        public static String allowedOptions()
        {
            StringBuilder returnVal = new StringBuilder();

            for(Intent intent : Intent.values())
            {
                returnVal.append(intent);
                returnVal.append("|");
            }
            
            String toString = returnVal.toString();
            return toString.substring(0,toString.length() - 1);
        }

        /**
         * Retrieves the enum value from the {@code intentStrParam}.
         *
         * @param intentStrParam The intent for the action.
         * @return The {@code enum} for {@code Intent}.
         */
        public static Intent getIntentFromString(String intentStrParam)
        {
            if(intentStrParam == null || intentStrParam.trim().isEmpty())
            {
                return null;
            }

            String paramLowerTrimmed = intentStrParam.trim().toLowerCase();

            for(Intent intent : Intent.values())
            {
                String iterValIntentLowerTrim = intent.toString().toLowerCase();

                if(iterValIntentLowerTrim.equals(paramLowerTrimmed))
                {
                    return intent;
                }
            }

            return null;
        }
    }
    
    /**
     * The URL (Universal Resource Locator) Path mappings for Fluid's
     * Web Services.
     */
    public final static class Path
    {
        public static final String WEB_SOCKET = "/web_socket/";

        /**
         * The Version mapping for the Fluid Web Service.
         */
        public static final class Version
        {
            public static final String VERSION_1 = "v1";
        }

        /**
         * The Electronic Form (Document) Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Form
         */
        public static final class FormContainer
        {
            /**
             * Form Container mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/form_container");
                public static final String ROOT_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");

                //Update...
                public static final String UPDATE = ("/update");

                //Read...
                public static final String READ = ("/get_by_id");
                public static final String PRINT_AS_PDF = ("/print_as_pdf");

                //Lock and Unlock
                public static final String LOCK_FORM_CONTAINER = "/lock_form_container";
                public static final String UN_LOCK_FORM_CONTAINER = "/un_lock_form_container";

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    //Locking a Form Container...
                    public static final String JOB_VIEW = "job_view";
                    public static final String FORM_CONTAINER = "form_container";
                    public static final String INCLUDE_COMPANY_LOGO = "include_company_logo";
                    public static final String INCLUDE_ANCESTOR = "include_ancestor";
                    public static final String INCLUDE_DESCENDANTS = "include_descendants";
                    public static final String INCLUDE_FORM_PROPERTIES = "include_form_properties";
                    public static final String LOCK_FOR_USER_ID = "lock_for_user_id";
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
                 * @return {@code v1/form_container/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Electronic Form create.
                 *
                 * @return {@code v1/form_container/}
                 */
                public static final String formContainerCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Electronic Form create via Web Socket.
                 *
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code web_socket/v1/form_container/}
                 */
                public static final String formContainerCreateWebSocket(String serviceTicketParam)
                {
                    return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam);
                }

                /**
                 * URL Path for Form Container update.
                 *
                 * @return {@code v1/form_container/update}
                 */
                public static final String formContainerUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Form Container delete.
                 *
                 * @return {@code v1/form_container/delete} <b>without</b> force.
                 */
                public static final String formContainerDelete()
                {
                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for locking a {@code Form}.
                 *
                 * @param jobViewIdParam The view selected to lock the item from.
                 * @param lockingAsUserIdParam The form will be locked as this user.
                 *                             The logged in user must have permission to perform this action.
                 *
                 * @return {@code /v1/form_container/lock_form_container}
                 */
                public static final String lockFormContainer(
                        Long jobViewIdParam,
                        Long lockingAsUserIdParam)
                {
                    String base = Version.VERSION_1.concat(ROOT).concat(LOCK_FORM_CONTAINER);
                    
                    String additionString = "?";

                    if(jobViewIdParam != null &&
                            jobViewIdParam.longValue() > 0)
                    {
                        additionString += FormContainer.Version1.QueryParam.JOB_VIEW;
                        additionString += "=";
                        additionString += jobViewIdParam;
                        additionString += "&";
                    }

                    if(lockingAsUserIdParam != null &&
                            lockingAsUserIdParam.longValue() > 0)
                    {
                        additionString += QueryParam.LOCK_FOR_USER_ID;
                        additionString += "=";
                        additionString += lockingAsUserIdParam;
                        additionString += "&";
                    }

                    //Cut of the end bit...
                    additionString = additionString.substring(
                            0, additionString.length() - 1);

                    return base.concat(additionString);
                }

                /**
                 * URL Path for un-locking a {@code Form}.
                 *
                 * @param unLockingAsUserIdParam The form will be un-locked as this user.
                 *                             The logged in user must have permission to perform this action.
                 *
                 * @return {@code v1/form_container/un_lock_form_container}
                 */
                public static final String unLockFormContainer(
                        Long unLockingAsUserIdParam)
                {
                    String base = Version.VERSION_1.concat(ROOT).concat(
                            UN_LOCK_FORM_CONTAINER);

                    String additionString = "?";

                    if(unLockingAsUserIdParam != null &&
                            unLockingAsUserIdParam.longValue() > 0)
                    {
                        additionString += QueryParam.LOCK_FOR_USER_ID;
                        additionString += "=";
                        additionString += unLockingAsUserIdParam;
                        additionString += "&";
                    }

                    //Cut of the end bit...
                    additionString = additionString.substring(
                            0, additionString.length() - 1);

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
                        boolean includeFormPropertiesParam)
                {
                    String returnVal =
                            Version.VERSION_1.concat(ROOT).concat(
                                    PRINT_AS_PDF);

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
            }
        }

        /**
         * The Electronic Form (Document) Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Form
         */
        public static final class FormContainerTableRecord
        {
            /**
             * Form Container mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/form_container/table_record");
                public static final String CREATE = ("/");

                public static final String ROOT_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

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
                public static final String formContainerTableRecordCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Electronic Form Table Record create via Web Socket.
                 *
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code web_socket/v1/form_container/table_record/}
                 */
                public static final String formContainerTableRecordCreateWebSocket(String serviceTicketParam)
                {
                    return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam);
                }
            }
        }

        /**
         * The Attachment Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Attachment
         */
        public static final class Attachment
        {
            /**
             * Form Container mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/attachment");

                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");

                //Update...
                public static final String UPDATE = ("/update");

                //Read...
                public static final String READ_BY_FORM_CONTAINER_AND_INDEX =
                        ("/get_by_form_container_and_index");

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    public static final String FORM_CONTAINER_ID = "form_container";
                    public static final String ATTACHMENT_INDEX = "attachment_index";
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
                 * URL Path for Attachment get by Form Container and Index.
                 *
                 * @param formContainerIdParam The primary key for the form container.
                 * @param indexParam The attachment index for the form container.
                 *
                 * @return {@code v1/attachment/get_by_form_container_and_index}
                 */
                public static final String getByFormContainerAndIndex(
                        Long formContainerIdParam,
                        int indexParam)
                {
                    String returnVal =
                            Version.VERSION_1.concat(ROOT).concat(
                                    READ_BY_FORM_CONTAINER_AND_INDEX);

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
            }
        }

        /**
         * The Electronic Form Personal Inventory Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Form
         */
        public static final class PersonalInventory
        {
            /**
             * Personal Inventory mappings.
             */
            public static final class Version1
            {
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
                public static final String formContainerAddToPersonalInventory()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Form Containers get by logged in user.
                 *
                 * @return {@code v1/personal_inventory/get_all_by_logged_in_user}
                 */
                public static final String getAllByLoggedInUser()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for clearing the whole Personal Inventory.
                 *
                 * @return {@code v1/personal_inventory/remove_from_personal_inventory}
                 */
                public static final String removeFromPersonalInventory()
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            REMOVE_FROM_PERSONAL_INVENTORY);
                }

                /**
                 * URL Path for clearing Personal Inventory.
                 *
                 * @return {@code v1/personal_inventory/clear_personal_inventory}
                 */
                public static final String clearPersonalInventory()
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            CLEAR_PERSONAL_INVENTORY);
                }
            }
        }

        /**
         * The Form Field Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Field
         */
        public static final class FormField
        {
            /**
             * Mapping for frequently used HTTP parameters.
             */
            public static final class QueryParam
            {
                public static final String EDIT_ONLY = "edit_only";
            }
            
            /**
             * Form Field mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/form_field");

                //Create...
                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Update...
                public static final String UPDATE = ("/update");

                //Read...
                public static final String READ = ("/get_by_id");
                public static final String BY_NAME = ("/get_by_name");
                public static final String READ_BY_FORM_DEF_AND_LOGGED_IN_USER =
                        ("/get_by_form_definition_and_logged_in_user");

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
                public static final String formFieldCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Form Field delete.
                 *
                 * @return {@code v1/form_field/delete} <b>without</b> force.
                 */
                public static final String formFieldDelete()
                {
                    return formFieldDelete(false);
                }

                /**
                 * URL Path for Form Field delete.
                 *
                 * @param forceDeleteParam Whether to forcefulle delete.
                 *
                 * @return {@code v1/form_field/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String formFieldDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for Form Field update.
                 *
                 * @return {@code v1/form_field/update}
                 */
                public static final String formFieldUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Form Field get by id.
                 *
                 * @return {@code v1/form_field/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Form Field get by name.
                 *
                 * @return {@code v1/form_field/get_by_name}
                 */
                public static final String getByName()
                {
                    return Version.VERSION_1.concat(ROOT).concat(BY_NAME);
                }

                /**
                 * URL Path for Form Fields get by Form Definition and Logged In User.
                 *
                 * @param editOnlyFieldsParam Only return the fields that are editable.
                 *
                 * @return {@code v1/form_field/get_by_form_definition_and_logged_in_user}
                 */
                public static final String getByFormDefinitionAndLoggedInUser(
                        boolean editOnlyFieldsParam)
                {
                    ///delete?force=true
                    String returnVal = Version.VERSION_1.concat(ROOT).concat(
                            READ_BY_FORM_DEF_AND_LOGGED_IN_USER);

                    returnVal += ("?"+ QueryParam.EDIT_ONLY+"="+editOnlyFieldsParam);

                    return returnVal;
                }
            }
        }

        /**
         * The Route Field Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Field
         */
        public static final class RouteField
        {
            /**
             * Route Field mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/route_field");

                //Create...
                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Update...
                public static final String UPDATE = ("/update");

                //Read...
                public static final String READ = ("/get_by_id");

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
                public static final String routeFieldCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Route Field delete.
                 *
                 * @return {@code v1/route_field/delete} <b>without</b> force.
                 */
                public static final String routeFieldDelete()
                {
                    return routeFieldDelete(false);
                }

                /**
                 * URL Path for Route Field delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/route_field/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String routeFieldDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for Route Field update.
                 *
                 * @return {@code v1/route_field/update}
                 */
                public static final String routeFieldUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Route Field get by id.
                 *
                 * @return {@code v1/route_field/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }
            }
        }

        /**
         * The User Field Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Field
         */
        public static final class UserField
        {
            /**
             * User Field mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/user_field");

                //Create...
                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Update...
                public static final String UPDATE = ("/update");

                //Read...
                public static final String READ = ("/get_by_id");

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
                public static final String userFieldCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for User Field delete.
                 *
                 * @return {@code v1/user_field/delete} <b>without</b> force.
                 */
                public static final String userFieldDelete()
                {
                    return userFieldDelete(false);
                }

                /**
                 * URL Path for User Field delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/user_field/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String userFieldDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for User Field update.
                 *
                 * @return {@code v1/user_field/update}
                 */
                public static final String userFieldUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for User Field get by id.
                 *
                 * @return {@code v1/user_field/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }
            }
        }

        /**
         * The Form Definition Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.Form
         */
        public static final class FormDefinition
        {
            /**
             * Mapping for frequently used HTTP parameters.
             */
            public static final class QueryParam
            {
                public static final String FORM_DEFINITION = "form_definition";
            }

            /**
             * Form Definition mappings.
             */
            public static final class Version1
            {
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
                public static final String READ_ALL_BY_LOGGED_IN_USER =
                        ("/get_all_by_logged_in_user");

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
                public static final String formDefinitionCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Form Definition delete.
                 *
                 * @return {@code v1/form_definition/delete} <b>without</b> force.
                 */
                public static final String formDefinitionDelete()
                {
                    return formDefinitionDelete(false);
                }

                /**
                 * URL Path for Form Definition delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/form_definition/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String formDefinitionDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for User Definition update.
                 *
                 * @return {@code v1/form_definition/update}
                 */
                public static final String formDefinitionUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Form Definition get by id.
                 *
                 * @return {@code v1/form_definition/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Form Definition get by name.
                 *
                 * @return {@code v1/form_definition/get_by_name}
                 */
                public static final String getByName()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_BY_NAME);
                }

                /**
                 * URL Path for Form Definitions by logged in user.
                 *
                 * @return {@code v1/form_definition/get_all_by_logged_in_user}
                 */
                public static final String getAllByLoggedInUser()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL_BY_LOGGED_IN_USER);
                }
            }
        }

        /**
         * The Flow Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.flow.Flow
         */
        public static final class Flow
        {
            /**
             * Flow mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/flow");

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
                public static final String flowCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Flow delete.
                 *
                 * @return {@code v1/flow/delete} <b>without</b> force.
                 */
                public static final String flowDelete()
                {
                    return flowDelete(false);
                }

                /**
                 * URL Path for Form Definition delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/flow/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String flowDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for Flow update.
                 *
                 * @return {@code v1/flow/update}
                 */
                public static final String flowUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Flow get by id.
                 *
                 * @return {@code v1/flow/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Flow get by name.
                 *
                 * @return {@code v1/flow/get_by_name}
                 */
                public static final String getByName()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_BY_NAME);
                }
            }
        }

        /**
         * The Flow Step Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.flow.FlowStep
         */
        public static final class FlowStep
        {
            /**
             * Flow Step mappings.
             */
            public static final class Version1
            {
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

                public static final String READ_ALL_STEPS_BY_FLOW =
                        ("/get_steps_by_flow");

                public static final String READ_ALL_VIEWS_BY_STEP =
                        ("/get_views_by_step");

                public static final String READ_ALL_VIEWS_BY_LOGGED_IN_USER =
                        ("/get_views_by_logged_in_user");

                public static final String POLLING = ("/polling");

                public static final String ROOT_POLLING = (ROOT+POLLING);
                
                public static final String READ_ALL_BY_LOGGED_IN_USER =
                        ("/get_all_by_logged_in_user");

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
                public static final String flowStepCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Flow Step delete.
                 *
                 * @return {@code v1/flow_step/delete} <b>without</b> force.
                 */
                public static final String flowStepDelete()
                {
                    return flowStepDelete(false);
                }

                /**
                 * URL Path for Flow Step delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/flow_step/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String flowStepDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for Flow Step update.
                 *
                 * @return {@code v1/flow_step/update}
                 */
                public static final String flowStepUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Flow Step get by id.
                 *
                 * @return {@code v1/flow_step/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Flow Step get by Step details.
                 *
                 * @return {@code v1/flow_step/get_by_step}
                 */
                public static final String getByStep()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_BY_STEP);
                }

                /**
                 * URL Path for JobViews by Flow Step id or name.
                 *
                 * @return {@code v1/flow_step/get_views_by_step}
                 */
                public static final String getAllViewsByStep()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_STEP);
                }

                /**
                 * URL Path for JobViews by logged in user.
                 *
                 * @return {@code v1/flow_step/get_views_by_logged_in_user}
                 */
                public static final String getAllViewsByLoggedInUser()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_LOGGED_IN_USER);
                }

                /**
                 * URL Path for Steps by Flow.
                 *
                 * @return {@code v1/flow_step/get_steps_by_flow}
                 */
                public static final String getAllStepsByFlow()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL_STEPS_BY_FLOW);
                }

                /**
                 * URL Path for Polling Steps by logged in user.
                 *
                 * @return {@code v1/flow_step/polling/get_all_by_logged_in_user}
                 */
                public static final String getAllPollingStepsByLoggedInUser()
                {
                    return Version.VERSION_1.concat(ROOT_POLLING).concat(
                            READ_ALL_BY_LOGGED_IN_USER);
                }
            }
        }

        /**
         * The Flow Step Rule Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.flow.FlowStepRule
         */
        public static final class FlowStepRule
        {
            /**
             * Flow Step Rule mappings.
             */
            public static final class Version1
            {
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
                public static final String flowStepRuleEntryCreate()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(CREATE);
                }

                /**
                 * URL Path for Flow Step Exit Rule create.
                 *
                 * @return {@code /v1/flow_step_rule/exit}
                 */
                public static final String flowStepRuleExitCreate()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(CREATE);
                }

                /**
                 * URL Path for Flow Step View Rule create.
                 *
                 * @return {@code /v1/flow_step_rule/view}
                 */
                public static final String flowStepRuleViewCreate()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(CREATE);
                }

                /**
                 * URL Path for Flow Step Entry rule delete.
                 *
                 * @return {@code v1/flow_step_rule/entry/delete} <b>without</b> force.
                 */
                public static final String flowStepRuleDeleteEntry()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(DELETE);
                }

                /**
                 * URL Path for Flow Step Exit rule delete.
                 *
                 * @return {@code v1/flow_step_rule/exit/delete} <b>without</b> force.
                 */
                public static final String flowStepRuleDeleteExit()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(DELETE);
                }

                /**
                 * URL Path for Flow Step View rule delete.
                 *
                 * @return {@code v1/flow_step_rule/view/delete} <b>without</b> force.
                 */
                public static final String flowStepRuleDeleteView()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(DELETE);
                }

                /**
                 * URL Path for Flow Step Entry update.
                 *
                 * @return {@code v1/flow_step_rule/entry/update}
                 */
                public static final String flowStepRuleUpdateEntry()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(UPDATE);
                }

                /**
                 * URL Path for Flow Step Entry move up (Order).
                 *
                 * @return {@code v1/flow_step_rule/entry/move_up}
                 */
                public static final String flowStepRuleMoveEntryUp()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_UP);
                }

                /**
                 * URL Path for Flow Step Entry move down (Order).
                 *
                 * @return {@code v1/flow_step_rule/entry/move_down}
                 */
                public static final String flowStepRuleMoveEntryDown()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_DOWN);
                }

                /**
                 * URL Path for Flow Step Exit update.
                 *
                 * @return {@code v1/flow_step_rule/exit/update}
                 */
                public static final String flowStepRuleUpdateExit()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(UPDATE);
                }

                /**
                 * URL Path for Flow Step View update.
                 *
                 * @return {@code v1/flow_step_rule/view/update}
                 */
                public static final String flowStepRuleUpdateView()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(UPDATE);
                }

                /**
                 * URL Path for Flow Step Entry get by id.
                 *
                 * @return {@code v1/flow_step_rule/entry/get_by_id}
                 */
                public static final String getEntryById()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(READ);
                }

                /**
                 * URL Path for Flow Step Entry get next valid syntax.
                 *
                 * @return {@code v1/flow_step_rule/entry/get_next_valid_syntax}
                 */
                public static final String getNextValidEntrySyntax()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 * URL Path for Flow Step Entry {@code compile} syntax.
                 *
                 * @return {@code v1/flow_step_rule/entry/compile_syntax}
                 */
                public static final String compileEntrySyntax()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX);
                }

                /**
                 * URL Path for Flow Step Entry {@code compile} syntax and then
                 * {@code execute}.
                 *
                 * @return {@code v1/flow_step_rule/entry/compile_syntax_and_execute}
                 */
                public static final String compileEntrySyntaxAndExecute()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX_AND_EXECUTE);
                }

                /**
                 * URL Path for Flow Step Exit get by id.
                 *
                 * @return {@code v1/flow_step_rule/exit/get_by_id}
                 */
                public static final String getExitById()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(READ);
                }

                /**
                 * URL Path for Flow Step Exit get next valid syntax.
                 *
                 * @return {@code v1/flow_step_rule/exit/get_next_valid_syntax}
                 */
                public static final String getNextValidExitSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 * URL Path for Flow Step Exit {@code compile} syntax.
                 *
                 * @return {@code v1/flow_step_rule/exit/compile_syntax}
                 */
                public static final String compileExitSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(COMPILE_SYNTAX);
                }

                /**
                 * URL Path for Flow Step Exit {@code get_by_step} syntax.
                 *
                 * @return {@code v1/flow_step_rule/exit/get_by_step}
                 */
                public static final String getExitRulesByStep()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(READ_RULES_BY_STEP);
                }

                /**
                 * URL Path for Flow Step Exit {@code get_by_step} syntax.
                 *
                 * @return {@code v1/flow_step_rule/entry/get_by_step}
                 */
                public static final String getEntryRulesByStep()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(READ_RULES_BY_STEP);
                }

                /**
                 * URL Path for Flow Step View get by id.
                 *
                 * @return {@code v1/flow_step_rule/view/get_by_id}
                 */
                public static final String getViewById()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(READ);
                }

                /**
                 * URL Path for Flow Step View get next valid syntax.
                 *
                 * @return {@code v1/flow_step_rule/view/get_next_valid_syntax}
                 */
                public static final String getNextValidViewSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 * URL Path for Flow Step View {@code compile} syntax.
                 *
                 * @return {@code v1/flow_step_rule/view/compile_syntax}
                 */
                public static final String compileViewSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(COMPILE_SYNTAX);
                }

                /**
                 * URL Path for Flow Step View {@code compile} syntax and then
                 * {@code execute}.
                 *
                 * @return {@code v1/flow_step_rule/view/compile_syntax_and_execute}
                 */
                public static final String compileViewSyntaxAndExecute()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(COMPILE_SYNTAX_AND_EXECUTE);
                }
            }
        }

        /**
         * The Fluid Item Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.FluidItem
         */
        public static final class FlowItem
        {
            /**
             * Flow Item mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/flow_item");
                public static final String ROOT_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

                //Send On...
                public static final String SEND_ON = ("/send_on");


                //Send to Flow...
                public static final String SEND_TO_FLOW = ("/send_to_flow");
                public static final String SEND_TO_FLOW_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT + SEND_TO_FLOW);

                //Create...
                public static final String CREATE = ("/");

                //Get...
                public static final String GET_BY_JOB_VIEW = ("/get_by_job_view");

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    //List Job View content...
                    public static final String QUERY_LIMIT = "query_limit";
                    public static final String OFFSET = "offset";
                    public static final String SORT_FIELD = "sort_field";
                    public static final String SORT_ORDER = "sort_order";

                    //Locking a Form Container...
                    public static final String JOB_VIEW = "job_view";

                    //Wait for Rule execution completion...
                    public static final String WAIT_FOR_RULE_EXEC_COMPLETION =
                            "wait_for_rule_exec_completion";
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
                public static final String flowItemCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for sending a Flow Item to the next
                 * step in the workflow process.
                 *
                 * @return {@code /v1/flow_item/send_on}
                 */
                public static final String sendFlowItemOn()
                {
                    return Version.VERSION_1.concat(ROOT).concat(SEND_ON);
                }

                /**
                 * URL Path for sending a Form to a Flow to follow
                 * the workflow process.
                 *
                 * @return {@code /v1/flow_item/send_to_flow/}
                 */
                public static final String sendFlowItemToFlow()
                {
                    return Version.VERSION_1.concat(ROOT).concat(SEND_TO_FLOW);
                }

                /**
                 * URL Path for Send to Flow via Web Socket.
                 *
                 * @param waitForRuleExecCompleteParam Wait for all the program rules to finish execution
                 *                                     before returning web socket message is sent.
                 *                                     The response message will include the result.
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code web_socket/v1/flow_item/send_to_flow/}
                 */
                public static final String sendToFlowWebSocket(
                        boolean waitForRuleExecCompleteParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            SEND_TO_FLOW_WEB_SOCKET.concat(
                                    CREATE).concat(
                                            serviceTicketParam + "?" +
                                                    QueryParam.WAIT_FOR_RULE_EXEC_COMPLETION+"=" +
                                                    waitForRuleExecCompleteParam);

                    return returnVal;
                }

                /**
                 * URL Path for Flow Item get by {@link com.fluidbpm.program.api.vo.flow.JobView}.
                 *
                 * @param queryLimitParam The query limit.
                 * @param offsetParam The query offset.
                 * @param sortFieldParam The field to sort.
                 * @param sortOrderParam The sort order.
                 *
                 * @return {@code /v1/flow_item/get_by_job_view}
                 */
                public static final String getByJobView(
                        int queryLimitParam,
                        int offsetParam,
                        String sortFieldParam,
                        String sortOrderParam)
                {
                    String base = Version.VERSION_1.concat(ROOT).concat(GET_BY_JOB_VIEW);
                    String additionString = "?";
                    
                    if(queryLimitParam > 0)
                    {
                        additionString += QueryParam.QUERY_LIMIT;
                        additionString += "=";
                        additionString += queryLimitParam;
                        additionString += "&";
                    }

                    if(offsetParam > -1)
                    {
                        additionString += QueryParam.OFFSET;
                        additionString += "=";
                        additionString += offsetParam;
                        additionString += "&";
                    }

                    if(sortFieldParam != null && !sortFieldParam.trim().isEmpty())
                    {
                        additionString += QueryParam.SORT_FIELD;
                        additionString += "=";
                        additionString += sortFieldParam;
                        additionString += "&";
                    }

                    if(sortOrderParam != null && !sortOrderParam.trim().isEmpty())
                    {
                        additionString += QueryParam.SORT_ORDER;
                        additionString += "=";
                        additionString += sortOrderParam;
                        additionString += "&";
                    }

                    //Cut of the end bit...
                    additionString = additionString.substring(
                            0, additionString.length() - 1);
                    
                    return base.concat(additionString);
                }
            }
        }

        /**
         * The Fluid Item History Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.FormFlowHistoricData
         */
        public static final class FlowItemHistory
        {
            /**
             * Flow Item History mappings.
             */
            public static final class Version1
            {
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
                 * URL Path for Form Container Flow History by
                 * Form Container.
                 *
                 * @return {@code v1/flow_item_history/get_by_form_container}
                 */
                public static final String getByFormContainer()
                {
                    return Version.VERSION_1.concat(ROOT).concat(BY_FORM_CONTAINER);
                }
            }
        }

        /**
         * The Test Web Service mappings.
         */
        public static final class Test
        {
            /**
             * Test mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/test");
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
                public static final String testConnection()
                {
                    return Version.VERSION_1.concat(ROOT);
                }
            }
        }

        /**
         * The User Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.user.User
         */
        public static final class User
        {
            /**
             * User mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/user");

                public static final String INIT_SESSION = "/init";
                public static final String ISSUE_TOKEN = "/issue_token";
                public static final String TOKEN_STATUS = "/token_status";
                public static final String INFORMATION = "/info";

                //Create...
                public static final String CREATE = ("/");

                //Update...
                public static final String UPDATE = ("/update");

                //Activate / DeActivate...
                public static final String DE_ACTIVATE = ("/de_activate");
                public static final String ACTIVATE = ("/activate");
                public static final String INCREMENT_INVALID_LOGIN =
                        ("/increment_invalid_login");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Read...
                public static final String READ = ("/get_by_id");
                public static final String READ_BY_USERNAME = ("/get_by_username");
                public static final String READ_BY_EMAIL = ("/get_by_email");
                public static final String READ_USER_FIELD_VALUES_BY_USER = ("/get_user_field_values_by_user");
                public static final String READ_ALL = ("/get_all_users");

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    public static final String USERNAME = "username";
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
                 * Root for User initialise session.
                 *
                 * @return {@code /user/init}
                 *
                 * @see com.fluidbpm.program.api.vo.auth0.AccessToken
                 * @see com.fluidbpm.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluidbpm.program.api.vo.user.User
                 */
                public static final String userInitSession()
                {
                    return Version.VERSION_1.concat(ROOT).concat(INIT_SESSION);
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
                public static final String userIssueToken()
                {
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
                public static final String userTokenStatus()
                {
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
                public static final String userInformation()
                {
                    return Version.VERSION_1.concat(ROOT).concat(INFORMATION);
                }

                /**
                 * URL Path for User create.
                 *
                 * @return {@code /v1/user/}
                 */
                public static final String userCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for User update.
                 *
                 * @return {@code v1/user/update}
                 */
                public static final String userUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Deleting a User.
                 *
                 * @return {@code v1/user/delete}
                 */
                public static final String userDelete()
                {
                    return userDelete(false);
                }

                /**
                 * URL Path for User delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/user/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String userDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for De-Activating a User.
                 *
                 * @return {@code v1/user/de_activate}
                 */
                public static final String userDeActivate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(DE_ACTIVATE);
                }


                /**
                 * URL Path for Incrementing the invalid login count for a User.
                 *
                 * @return {@code v1/user/increment_invalid_login}
                 */
                public static final String incrementInvalidLogin()
                {
                    return Version.VERSION_1.concat(ROOT).concat(INCREMENT_INVALID_LOGIN);
                }

                /**
                 * URL Path for De-Activating a User.
                 *
                 * @return {@code v1/user/activate}
                 */
                public static final String userActivate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(ACTIVATE);
                }

                /**
                 * URL Path for User get by id.
                 *
                 * @return {@code v1/user/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for User get by username.
                 *
                 * @return {@code v1/user/get_by_username}
                 */
                public static final String getByUsername()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_BY_USERNAME);
                }

                /**
                 * URL Path for User get by Email.
                 *
                 * @return {@code v1/user/get_by_email}
                 */
                public static final String getByEmail()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_BY_EMAIL);
                }

                /**
                 * URL Path for User Field Values by User.
                 *
                 * @return {@code v1/user/get_user_field_values_by_user}
                 */
                public static final String getUserFieldValuesByUser()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_USER_FIELD_VALUES_BY_USER);
                }

                /**
                 * URL Path for User get by id.
                 *
                 * @return {@code v1/user/get_all_users}
                 */
                public static final String getAllUsers()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
                }
            }
        }

        /**
         * The Role Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.role.Role
         */
        public static final class Role
        {
            /**
             * Role mappings.
             */
            public static final class Version1
            {
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
                public static final String roleCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for Role update.
                 *
                 * @return {@code v1/role/update}
                 */
                public static final String roleUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Deleting a Role.
                 *
                 * @return {@code v1/role/delete}
                 */
                public static final String roleDelete()
                {
                    return roleDelete(false);
                }

                /**
                 * URL Path for Role delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/role/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String roleDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for Role get by id.
                 *
                 * @return {@code v1/role/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Role get all.
                 *
                 * @return {@code v1/role/get_all_roles}
                 */
                public static final String getAllRoles()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
                }
            }
        }

        /**
         * The Mandrill Inbound Mail Web Service mappings.
         */
        public static final class MandrillInbound
        {
            /**
             * Role mappings.
             */
            public static final class Version1
            {
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
         * The Configuration Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.config.Configuration
         */
        public static final class Configuration
        {
            /**
             * Role mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/configuration");

                //Update...
                //public static final String UPDATE = ("/update");

                //Read...
                public static final String READ = ("/get_by_key");
                public static final String READ_ALL = ("/get_all_configurations");

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
                public static final String getByKey()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for Configuration get all.
                 *
                 * @return {@code v1/configuration/get_all_configurations}
                 */
                public static final String getAllConfigurations()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
                }
            }
        }

        /**
         * The Custom Runner Destination Config Web Service mappings.
         */
        public static final class CustomRunnerDestinationConfig
        {
            /**
             * Custom runner mappings.
             */
            public static final class Version1
            {
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
                public static final String getEnvironmentConfig()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for LOCKING Customer Runner Destination configuration.
                 *
                 * @return {@code v1/custom_runner/destination_config/lock_environment_config}
                 */
                public static final String lockEnvironmentConfig()
                {
                    return Version.VERSION_1.concat(ROOT).concat(LOCK);
                }
            }
        }

        /**
         * The Fluid Custom Runner Web Socket mappings.
         */
        public static final class CustomRunner
        {
            /**
             * Custom runner mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/custom_runner");

                public static final String ROOT_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

                //Custom Web...
                public static final String CUSTOM_WEB = ("/custom_web");
                public static final String CUSTOM_WEB_WEB_SOCKET =
                        (ROOT_WEB_SOCKET + CUSTOM_WEB);

                //Custom Flow Program...
                public static final String CUSTOM_FLOW_PROGRAM = ("/custom_flow_program");
                public static final String CUSTOM_FLOW_PROGRAM_WEB_SOCKET =
                        (ROOT_WEB_SOCKET + CUSTOM_FLOW_PROGRAM);

                //Custom Flow Program...
                public static final String CUSTOM_SCHEDULE = ("/custom_schedule");
                public static final String CUSTOM_SCHEDULE_WEB_SOCKET =
                        (CUSTOM_SCHEDULE + CUSTOM_FLOW_PROGRAM);

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class PathParam
                {
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
                public static final String executeCustomWebWebSocket(
                        String taskIdentifierParam)
                throws UnsupportedEncodingException
                {
                    String encodedValue = "";
                    if(taskIdentifierParam != null)
                    {
                        encodedValue = URLEncoder.encode(taskIdentifierParam, "UTF-8");
                    }

                    String returnVal =
                            (CUSTOM_WEB_WEB_SOCKET +
                                    "/" + encodedValue+"/");

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
                public static final String executeCustomFlowProgramWebSocket(
                        String taskIdentifierParam)
                        throws UnsupportedEncodingException
                {
                    String encodedValue = "";
                    if(taskIdentifierParam != null)
                    {
                        encodedValue = URLEncoder.encode(taskIdentifierParam, "UTF-8");
                    }

                    String returnVal =
                            (CUSTOM_FLOW_PROGRAM_WEB_SOCKET +
                                    "/" + encodedValue+"/");

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
                public static final String executeCustomScheduleWebSocket(
                        String taskIdentifierParam)
                        throws UnsupportedEncodingException
                {
                    String encodedValue = "";
                    if(taskIdentifierParam != null)
                    {
                        encodedValue = URLEncoder.encode(taskIdentifierParam, "UTF-8");
                    }

                    String returnVal =
                            (CUSTOM_SCHEDULE_WEB_SOCKET +
                                    "/" + encodedValue+"/");

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
        public static final class SQLUtil
        {
            /**
             * User Query mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/sql_util");

                public static final String ROOT_WEB_SOCKET =
                        (Path.WEB_SOCKET + Version.VERSION_1 + ROOT);

                //[[[ EXECUTE ]]]...
                //Form...
                public static final String SQL_UTIL_FORM_GET_TABLE_FORMS =
                        ("/form/get_table_forms_by_electronic_form_id");

                public static final String SQL_UTIL_FORM_GET_DESCENDANTS =
                        ("/form/get_descendants_by_electronic_form_id");

                public static final String SQL_UTIL_FORM_GET_ANCESTOR =
                        ("/form/get_ancestor_by_electronic_form_id");

                public static final String SQL_UTIL_FORM_EXECUTE_SQL =
                        ("/form/execute_sql");

                //Field...
                public static final String SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER =
                        ("/form_field/get_fields_by_electronic_form_id");

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    public static final String INCLUDE_FIELD_DATA = "include_field_data";
                    public static final String INCLUDE_TABLE_FIELDS = "include_table_fields";
                    public static final String MASS_FETCH = "mass_fetch";
                }

                /**
                 * Mapping for frequently used HTTP or Web Socket Path parameters.
                 */
                public static final class PathParam
                {
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
                 * URL Path for retrieving Table records for electronic
                 * form with id {@code electronicFormIdParam}.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 *
                 * @return {@code v1/sql_util/form/get_table_forms_by_electronic_form_id}
                 */
                public static final String getTableForms(
                        boolean includeFieldDataParam)
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            SQL_UTIL_FORM_GET_TABLE_FORMS).concat(
                            "?" +
                            QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam
                    );
                }

                /**
                 * URL Path for retrieving Table records for electronic
                 * form with id {@code electronicFormIdParam}.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form/get_table_forms_by_electronic_form_id}
                 */
                public static final String getTableFormsWebSocket(
                        boolean includeFieldDataParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(
                                    SQL_UTIL_FORM_GET_TABLE_FORMS).concat(
                                    "/"+ serviceTicketParam + "?" +
                                            QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam);

                    return returnVal;
                }

                /**
                 * URL Path for retrieving descendants for electronic
                 * form with id {@code electronicFormIdParam}.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 * @param includeTableFieldsParam Does table field data need to be included.
                 *
                 * @return {@code v1/sql_util/form/get_descendants_by_electronic_form_id}
                 */
                public static final String getDescendants(
                        boolean includeFieldDataParam,
                        boolean includeTableFieldsParam)
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            SQL_UTIL_FORM_GET_DESCENDANTS).concat(
                            "?" +
                            QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam +
                            "&" +
                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam
                    );
                }

                /**
                 * URL Path for retrieving Table records for electronic
                 * form with id {@code electronicFormIdParam}.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 * @param includeTableFieldsParam Does table field data need to be included.
                 * @param massFetchParam Is the fetch a large fetch.
                 *                       
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form/get_descendants_by_electronic_form_id}
                 */
                public static final String getDescendantsWebSocket(
                        boolean includeFieldDataParam,
                        boolean includeTableFieldsParam,
                        boolean massFetchParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(
                                    SQL_UTIL_FORM_GET_DESCENDANTS).concat(
                                    "/"+ serviceTicketParam + 
                                    "?" +
                                    QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam +
                                    "&" +
                                    QueryParam.MASS_FETCH+"=" + massFetchParam +
                                    "&" +
                                    QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam);

                    return returnVal;
                }

                /**
                 * URL Path for executing SQL on the Fluid core database.
                 *
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form/execute_sql}
                 */
                public static final String getExecuteSQLWebSocket(String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(SQL_UTIL_FORM_EXECUTE_SQL).concat(
                                    "/"+ serviceTicketParam);

                    return returnVal;
                }

                /**
                 * URL Path for retrieving ancestor for electronic
                 * form.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 * @param includeTableFieldsParam Does table field data need to be included.
                 *
                 * @return {@code v1/sql_util/form/get_ancestor_by_electronic_form_id}
                 */
                public static final String getAncestor(
                        boolean includeFieldDataParam,
                        boolean includeTableFieldsParam)
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            SQL_UTIL_FORM_GET_ANCESTOR).concat(
                            "?" +
                            QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam +
                            "&" +
                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam
                    );
                }

                /**
                 * URL Path for retrieving ancestor data via Web Socket.
                 *
                 * @param includeFieldDataParam Does field data need to be included.
                 * @param includeTableFieldsParam Does table field data need to be included.
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form/get_ancestor_by_electronic_form_id}
                 */
                public static final String getAncestorWebSocket(
                        boolean includeFieldDataParam,
                        boolean includeTableFieldsParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(
                                    SQL_UTIL_FORM_GET_ANCESTOR).concat(
                                    "/"+ serviceTicketParam +
                                            "?" +
                                            QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam +
                                            "&" +
                                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam);

                    return returnVal;
                }

                /**
                 * URL Path for retrieving Form Fields for electronic
                 * form.
                 *
                 * @param includeTableFieldsParam Does Table Field data need to be included?
                 *
                 * @return {@code v1/sql_util/form_field/get_fields_by_electronic_form_id}
                 */
                public static final String getFormFields(
                        boolean includeTableFieldsParam)
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER).concat(
                            "?" +
                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam
                    );
                }

                /**
                 * URL Path for retrieving Form Fields for electronic
                 * form using a Web Socket.
                 *
                 * @param includeTableFieldsParam Does Table Field data need to be included?
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form_field/get_fields_by_electronic_form_id}
                 */
                public static final String getFormFieldsWebSocket(
                        boolean includeTableFieldsParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(
                                    SQL_UTIL_FORM_FIELDS_GET_BY_CONTAINER).concat(
                                    "/"+ serviceTicketParam + "?" +
                                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam);

                    return returnVal;
                }
            }
        }

        /**
         * The User Query Web Service mappings.
         *
         * @see com.fluidbpm.program.api.vo.userquery.UserQuery
         */
        public static final class UserQuery
        {
            /**
             * User Query mappings.
             */
            public static final class Version1
            {
                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
                    public static final String POPULATE_ANCESTOR_ID = "populate_ancestor_id";
                }
                
                public static final String ROOT = ("/user_query");

                //Create...
                public static final String CREATE = ("/");

                //Update...
                public static final String UPDATE = ("/update");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Read...
                public static final String READ = ("/get_by_id");
                public static final String READ_ALL = ("/get_all_user_queries");
                
                public static final String READ_ALL_USER_QUERIES_BY_LOGGED_IN_USER =
                        ("/get_all_user_queries_by_logged_in_user");

                //Execute...
                public static final String EXECUTE = ("/execute");
                public static final String EXECUTE_POPULATE_ANCESTOR_ID =
                                ("/execute?"+POPULATE_ANCESTOR_ID+"=true");

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
                 *
                 * @return {@code v1/user_query/execute}
                 */
                public static final String executeUserQuery(
                        boolean populateAncestorIdParam)
                {
                    if(populateAncestorIdParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(EXECUTE_POPULATE_ANCESTOR_ID);
                    }
                    
                    return Version.VERSION_1.concat(ROOT).concat(EXECUTE);
                }
                
                /**
                 * URL Path for executing a {@code UserQuery}.
                 *
                 * @return {@code v1/user_query/execute}
                 */
                public static final String executeUserQuery()
                {
                    return executeUserQuery(true);
                }

                /**
                 * URL Path for {@code UserQuery} create.
                 *
                 * @return {@code /v1/user_query/}
                 */
                public static final String userQueryCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 * URL Path for {@code UserQuery} update.
                 *
                 * @return {@code v1/user_query/update}
                 */
                public static final String userQueryUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 * URL Path for Deleting a {@code UserQuery}.
                 *
                 * @return {@code v1/user_query/delete}
                 */
                public static final String userQueryDelete()
                {
                    return userQueryDelete(false);
                }

                /**
                 * URL Path for {@code UserQuery} delete.
                 *
                 * @param forceDeleteParam Whether to forcefully delete.
                 *
                 * @return {@code v1/user_query/delete?force=forceDeleteParam} <b>with / without</b> force.
                 */
                public static final String userQueryDelete(boolean forceDeleteParam)
                {
                    if(forceDeleteParam)
                    {
                        return Version.VERSION_1.concat(ROOT).concat(DELETE_FORCE);
                    }

                    return Version.VERSION_1.concat(ROOT).concat(DELETE);
                }

                /**
                 * URL Path for {@code UserQuery} get by id.
                 *
                 * @return {@code v1/user_query/get_by_id}
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }

                /**
                 * URL Path for UserQuery get all.
                 *
                 * @return {@code v1/user_query/get_all_user_queries}
                 */
                public static final String getAllUserQueries()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL);
                }

                /**
                 * URL Path for UserQuery get all by logged in {@code User}.
                 *
                 * @return {@code v1/user_query/get_all_user_queries_by_logged_in_user}
                 */
                public static final String getAllUserQueriesByLoggedInUser()
                {
                    return Version.VERSION_1.concat(ROOT).concat(
                            READ_ALL_USER_QUERIES_BY_LOGGED_IN_USER);
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
        public static final class Auth0
        {
            /**
             * Auth0 mappings.
             */
            public static final class Version1
            {
                public static final String ROOT = ("/oauth");

                public static final String TOKEN = "/token";
                public static final String USER_INFO_WITH_ACCESS_TOKEN =
                        "/userinfo/?access_token=";
                public static final String USER_INFO = "/userinfo";

                /**
                 * Mapping for frequently used HTTP parameters.
                 */
                public static final class QueryParam
                {
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
                public static final String userToken()
                {
                    return ROOT.concat(TOKEN);
                }

                /**
                 * Used to get {@code User} information via the
                 * {@code accessTokenValueParam}.
                 *
                 * @param accessTokenValueParam The access token to get user
                 *                              information from.
                 * @return {@code /oath/userinfo?access_token=accessTokenValueParam}
                 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
                 */
                public static final String userInfo(String accessTokenValueParam)
                throws UnsupportedEncodingException
                {
                    String encodedValue = "";

                    if(accessTokenValueParam != null)
                    {
                        encodedValue = URLEncoder.encode(accessTokenValueParam, "UTF-8");
                    }

                    return USER_INFO_WITH_ACCESS_TOKEN.concat(encodedValue);
                }

                /**
                 * Used to get {@code User} information via the
                 * {@code accessTokenValueParam} in the HTTP header.
                 *
                 * @return {@code /oath/userinfo?access_token=accessTokenValueParam}
                 * @throws UnsupportedEncodingException When UTF-8 encoding is not supported.
                 */
                public static final String userInfo()
                        throws UnsupportedEncodingException
                {
                    return USER_INFO;
                }
            }
        }
    }
}
