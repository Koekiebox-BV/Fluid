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

/**
 * Created by jasonbruwer on 14/12/21.
 */
public class WS {

    public static final String PRODUCES = "application/json";
    public static final String CONSUMES = "application/json";

    /**
     *
     */
    public static final class QueryParam
    {
        public static final String FORCE = "force";
        public static final String ASYNC = "async";
        public static final String ID = "id";
    }


    /**
     *
     */
    public final static class Path
    {
        public static final class Version
        {
            public static final String VERSION_1 = "v1";
        }

        /**
         *
         */
        public static final class FormContainer
        {
            /**
             *
             */
            public static final class Version1
            {
                public static final String ROOT = ("/form_container");
                public static final String CRUD = ("/");


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String formContainerCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CRUD);
                }
            }
        }

        /**
         *
         */
        public static final class FormField
        {
            /**
             *
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


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String formFieldCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String formFieldDelete()
                {
                    return formFieldDelete(false);
                }

                /**
                 *
                 * @return
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
                 *
                 * @return
                 */
                public static final String formFieldUpdate()
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
        public static final class RouteField
        {
            /**
             *
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


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String formFieldCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String formFieldDelete()
                {
                    return formFieldDelete(false);
                }

                /**
                 *
                 * @return
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
                 *
                 * @return
                 */
                public static final String formFieldUpdate()
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
        public static final class FormDefinition
        {
            /**
             *
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


                @Override
                public String toString() {
                    return ROOT;
                }

                /**
                 *
                 * @return
                 */
                public static final String formDefinitionCreate()
                {
                    return Version.VERSION_1.concat(ROOT).concat(CREATE);
                }

                /**
                 *
                 * @return
                 */
                public static final String formDefinitionDelete()
                {
                    return formDefinitionDelete(false);
                }

                /**
                 *
                 * @return
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
                 *
                 * @return
                 */
                public static final String formDefinitionUpdate()
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
        public static final class Flow
        {
            /**
             *
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

                public static final String ROOT_ENTRY = (ROOT.concat("/entry"));
                public static final String ROOT_EXIT = (ROOT.concat("/exit"));
                public static final String ROOT_VIEW = (ROOT.concat("/view"));

                //Create...
                public static final String CREATE = ("/");

                //Delete...
                public static final String DELETE = ("/delete");

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
                public static final String getExitById()
                {
                    return Version.VERSION_1.concat(ROOT_EXIT).concat(READ);
                }

                /**
                 *
                 * @return
                 */
                public static final String getViewById()
                {
                    return Version.VERSION_1.concat(ROOT_VIEW).concat(READ);
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
