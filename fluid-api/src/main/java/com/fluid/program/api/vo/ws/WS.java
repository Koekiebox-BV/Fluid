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

package com.fluid.program.api.vo.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidVO;

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
        public static final String FORCE = "force";
        public static final String ASYNC = "async";
        public static final String ID = "id";
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
         * @see com.fluid.program.api.vo.Form
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
                    return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam + "/" );
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
            }
        }

        /**
         * The Electronic Form (Document) Web Service mappings.
         *
         * @see com.fluid.program.api.vo.Form
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
            }
        }

        /**
         * The Form Field Web Service mappings.
         *
         * @see com.fluid.program.api.vo.Field
         */
        public static final class FormField
        {
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
            }
        }

        /**
         * The Route Field Web Service mappings.
         *
         * @see com.fluid.program.api.vo.Field
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
         * @see com.fluid.program.api.vo.Field
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
         * @see com.fluid.program.api.vo.Form
         */
        public static final class FormDefinition
        {
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
            }
        }

        /**
         * The Flow Web Service mappings.
         *
         * @see com.fluid.program.api.vo.flow.Flow
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
         * @see com.fluid.program.api.vo.flow.FlowStep
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
                public static final String READ_ALL_VIEWS_BY_STEP =
                        ("/get_views_by_step_id");

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
                 * URL Path for JobViews by Flow Step id.
                 *
                 * @return {@code v1/flow_step/get_views_by_step_id}
                 */
                public static final String getAllViewsByStepId()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ_ALL_VIEWS_BY_STEP);
                }
            }
        }

        /**
         * The Flow Step Rule Web Service mappings.
         *
         * @see com.fluid.program.api.vo.flow.FlowStepRule
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
         * @see com.fluid.program.api.vo.FluidItem
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

                public static final String CREATE = ("/");

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
                 * URL Path for Flow Item create via Web Socket.
                 *
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code web_socket/v1/flow_item/}
                 */
                public static final String flowItemCreateWebSocket(String serviceTicketParam)
                {
                    return ROOT_WEB_SOCKET.concat(CREATE).concat(serviceTicketParam + "/");
                }
            }
        }

        /**
         * The Fluid Item History Web Service mappings.
         *
         * @see com.fluid.program.api.vo.FormFlowHistoricData
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
         * @see com.fluid.program.api.vo.user.User
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

                //DeActivate...
                public static final String DE_ACTIVATE = ("/de_activate");

                //Activate...
                public static final String ACTIVATE = ("/activate");

                //Delete...
                public static final String DELETE = ("/delete");
                public static final String DELETE_FORCE = ("/delete?force=true");

                //Read...
                public static final String READ = ("/get_by_id");
                public static final String READ_BY_USERNAME = ("/get_by_username");
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
                 * @see com.fluid.program.api.vo.auth0.AccessToken
                 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluid.program.api.vo.user.User
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
                 * @see com.fluid.program.api.vo.auth0.AccessToken
                 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluid.program.api.vo.user.User
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
                 * @see com.fluid.program.api.vo.auth0.AccessToken
                 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluid.program.api.vo.user.User
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
                 * @see com.fluid.program.api.vo.auth0.AccessToken
                 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluid.program.api.vo.user.User
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
         * @see com.fluid.program.api.vo.role.Role
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
         * @see com.fluid.program.api.vo.config.Configuration
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
         * The SQL Util Web Service mappings.
         *
         * @see com.fluid.program.api.util.sql.impl.SQLFormDefinitionUtil
         * @see com.fluid.program.api.util.sql.impl.SQLFormFieldUtil
         * @see com.fluid.program.api.util.sql.impl.SQLFormUtil
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
                }

                /**
                 * Mapping for frequently used HTTP or Web Socket Path parameters.
                 */
                public static final class PathParam
                {
                    public static final String SERVICE_TICKET = "serviceTicket";
                }

                /**
                 * Root for package {@code com.fluid.program.api.util.sql.impl}.
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
                                    "/"+ serviceTicketParam + "/" +
                                            "?" +
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
                 * @param serviceTicketParam The service ticket in hex-decimal text format.
                 *
                 * @return {@code /web_socket/v1/sql_util/form/get_descendants_by_electronic_form_id}
                 */
                public static final String getDescendantsWebSocket(
                        boolean includeFieldDataParam,
                        boolean includeTableFieldsParam,
                        String serviceTicketParam)
                {
                    String returnVal =
                            ROOT_WEB_SOCKET.concat(
                                    SQL_UTIL_FORM_GET_DESCENDANTS).concat(
                                    "/"+ serviceTicketParam + "/" +
                                    "?" +
                                    QueryParam.INCLUDE_FIELD_DATA+"=" + includeFieldDataParam +
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
                                    "/"+ serviceTicketParam + "/");

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
                                    "/"+ serviceTicketParam + "/" +
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
                                    "/"+ serviceTicketParam + "/" +
                                            "?" +
                                            QueryParam.INCLUDE_TABLE_FIELDS+"=" + includeTableFieldsParam);

                    return returnVal;
                }
            }
        }

        /**
         * The User Query Web Service mappings.
         *
         * @see com.fluid.program.api.vo.userquery.UserQuery
         */
        public static final class UserQuery
        {
            /**
             * User Query mappings.
             */
            public static final class Version1
            {
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

                //Execute...
                public static final String EXECUTE = ("/execute");

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
                 * @return {@code v1/user_query/execute}
                 */
                public static final String executeUserQuery()
                {
                    return Version.VERSION_1.concat(ROOT).concat(EXECUTE);
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

            }
        }

        /**
         * The Auth0 Web Service mappings.
         *
         * @see com.fluid.program.api.vo.auth0.AccessToken
         * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
         * @see com.fluid.program.api.vo.user.User
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
                 * @see com.fluid.program.api.vo.auth0.AccessToken
                 * @see com.fluid.program.api.vo.auth0.AccessTokenRequest
                 * @see com.fluid.program.api.vo.user.User
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
