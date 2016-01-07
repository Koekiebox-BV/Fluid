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
                public static final String CRUD = ("/");


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
                 * URL Path for Electronic Form create.
                 *
                 * @return {@code v1/form_container/}
                 */
                public static final String formContainerCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CRUD);
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

                /**
                 * Root for Form Definition.
                 *
                 * @return {@code /flow}
                 */
                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String flowCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowDelete()
                {
                    return flowDelete(false);
                }

                /**
                 *
                 * @return
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
                 *
                 * @return
                 */
                public static final String flowUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }
            }
        }

        /**
         *
         */
        public static final class FlowStep
        {
            /**
             *
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

                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepDelete()
                {
                    return flowStepDelete(false);
                }

                /**
                 *
                 * @return
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
                 *
                 * @return
                 */
                public static final String flowStepUpdate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(UPDATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String getById()
                {
                    return Version.VERSION_1.concat(ROOT).concat(READ);
                }
            }
        }

        /**
         *
         */
        public static final class FlowStepRule
        {
            /**
             *
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

                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleEntryCreate()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleExitCreate()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleViewCreate()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleDeleteEntry()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(DELETE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleDeleteExit()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(DELETE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleDeleteView()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(DELETE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleUpdateEntry()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(UPDATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleMoveEntryUp()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_UP);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleMoveEntryDown()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(MOVE_DOWN);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleUpdateExit()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(UPDATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String flowStepRuleUpdateView()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(UPDATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String getEntryById()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(READ);
                }

                /**
                 *
                 * @return
                 */
                public static final String getNextValidEntrySyntax()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 *
                 * @return
                 */
                public static final String compileEntrySyntax()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX);
                }

                /**
                 *
                 * @return
                 */
                public static final String compileSyntaxAndExecute()
                {
                    return Version.VERSION_1.concat(ROOT_ENTRY).concat(COMPILE_SYNTAX_AND_EXECUTE);
                }

                /**
                 *
                 * @return
                 */
                public static final String getExitById()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(READ);
                }

                /**
                 *
                 * @return
                 */
                public static final String getNextValidExitSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 *
                 * @return
                 */
                public static final String compileExitSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(COMPILE_SYNTAX);
                }

                /**
                 *
                 * @return
                 */
                public static final String getViewById()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(READ);
                }

                /**
                 *
                 * @return
                 */
                public static final String getNextValidViewSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(GET_NEXT_VALID_SYNTAX);
                }

                /**
                 *
                 * @return
                 */
                public static final String compileViewSyntax()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(COMPILE_SYNTAX);
                }
            }
        }

        /**
         *
         */
        public static final class FlowItem
        {
            /**
             *
             */
            public static final class Version1
            {
                public static final String ROOT = ("/flow_item");
                public static final String CREATE = ("/");


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String flowItemCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }
            }
        }

        /**
         *
         */
        public static final class FlowItemHistory
        {
            /**
             *
             */
            public static final class Version1
            {
                public static final String ROOT = ("/flow_item_history");

                //Read...
                public static final String BY_FORM_CONTAINER = ("/get_by_form_container");

                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String getByFormContainer()
                {
                    return Version.VERSION_1.concat(ROOT).concat(BY_FORM_CONTAINER);
                }
            }
        }

        /**
         *
         */
        public static final class Test
        {
            /**
             *
             */
            public static final class Version1
            {
                public static final String ROOT = ("/test");
                public static final String TEST = ("/");

                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String testConnection()
                {
                    return Version.VERSION_1.concat(ROOT);
                }
            }
        }

        /**
         *
         */
        public static final class User
        {
            /**
             *
             */
            public static final class Version1
            {
                public static final String ROOT = ("/user");

                public static final String INIT_SESSION = "/init";
                public static final String ISSUE_TOKEN = "/issue_token";
                public static final String TOKEN_STATUS = "/token_status";
                public static final String INFORMATION = "/info";

                public static final class QueryParam
                {
                    public static final String USERNAME = "username";
                }


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String userInitSession()
                {
                    return Version.VERSION_1.concat(ROOT).concat(INIT_SESSION);
                }

                /**
                 *
                 * @return
                 */
                public static final String userIssueToken()
                {
                    return Version.VERSION_1.concat(ROOT).concat(ISSUE_TOKEN);
                }

                /**
                 *
                 * @return
                 */
                public static final String userTokenStatus()
                {
                    return Version.VERSION_1.concat(ROOT).concat(TOKEN_STATUS);
                }

                /**
                 *
                 * @return
                 */
                public static final String userInformation()
                {
                    return Version.VERSION_1.concat(ROOT).concat(INFORMATION);
                }
            }
        }

        /**
         *
         */
        public static final class Auth0
        {
            public static final class Version1
            {
                public static final String ROOT = ("/oauth");

                public static final String TOKEN = "/token";
                public static final String USER_INFO = "/userinfo/?access_token=";

                public static final class QueryParam
                {
                    public static final String USERNAME = "username";
                }


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String userToken()
                {
                    return ROOT.concat(TOKEN);
                }

                /**
                 *
                 * @param accessTokenValueParam
                 * @return
                 * @throws UnsupportedEncodingException
                 */
                public static final String userInfo(String accessTokenValueParam)
                throws UnsupportedEncodingException
                {
                    String encodedValue = "";

                    if(accessTokenValueParam != null)
                    {
                        encodedValue = URLEncoder.encode(accessTokenValueParam, "UTF-8");
                    }

                    return ROOT.concat(USER_INFO).concat(encodedValue);
                }
            }
        }
    }

}
