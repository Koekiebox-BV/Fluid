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

package com.fluid.ws.client.v1.userquery;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.MultiChoice;
import com.fluid.program.api.vo.item.FluidItemListing;
import com.fluid.program.api.vo.userquery.UserQuery;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestUserQueryClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String NAME = "JUnit Run the Query";
        public static final String DESCRIPTION = "This is a JUnit test query.";

        public static final String RULE_NR_1 = "[Form Type] = INPUT_VALUE";
        public static final Long RESULT_FIELD_1 = 1L;
        public static final String RESULT_FIELD_1_NAME = "Email From Address";

        public static final String UPDATE_NAME = "JUnit Run the Query - Updated";
        public static final String UPDATE_DESCRIPTION = "This is a JUnit test query. Updated";

        /**
         *
         * @param ruleParam
         * @return
         */
        public static List<String> toRuleListing(String ruleParam)
        {
            List<String> returnVal = new ArrayList();

            if(ruleParam != null){
                returnVal.add(ruleParam);
            }

            return returnVal;
        }

        /**
         *
         * @param fieldIdParam
         * @param nameParam
         * @return
         */
        public static List<Field> toFieldListing(
                Long fieldIdParam,
                String nameParam)
        {
            List<Field> returnVal = new ArrayList();

            Field field = new Field(fieldIdParam);
            field.setFieldName(nameParam);

            returnVal.add(field);

            return returnVal;
        }

    }

    /**
     *
     */
    @Before
    public void init()
    {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        this.loginClient = new LoginClient(BASE_URL);
    }

    /**
     *
     */
    @After
    public void destroy()
    {
        this.loginClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testUserQuery_CRUD()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserQueryClient userQueryClient = new UserQueryClient(BASE_URL, serviceTicket);

        //1. Text...
        UserQuery toCreate = new UserQuery();
        toCreate.setName(TestStatics.NAME);
        toCreate.setDescription(TestStatics.DESCRIPTION);
        toCreate.setRules(TestStatics.toRuleListing(TestStatics.RULE_NR_1));
        toCreate.setInputs(TestStatics.toFieldListing(
                TestStatics.RESULT_FIELD_1,
                TestStatics.RESULT_FIELD_1_NAME));

        //2. Create...
        UserQuery createdUserQuery = userQueryClient.createUserQuery(toCreate);

        TestCase.assertNotNull("The 'Id' needs to be set.", createdUserQuery.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.NAME, createdUserQuery.getName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.DESCRIPTION,
                createdUserQuery.getDescription());

        //3. Update...
        createdUserQuery.setName(TestStatics.UPDATE_NAME);
        createdUserQuery.setDescription(TestStatics.UPDATE_DESCRIPTION);
        UserQuery updatedUserQuery = userQueryClient.updateUserQuery(createdUserQuery);

        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedUserQuery.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.UPDATE_NAME,
                updatedUserQuery.getName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.",
                TestStatics.UPDATE_DESCRIPTION, updatedUserQuery.getDescription());

        //4. Get by Id...
        UserQuery byIdUserQuery = userQueryClient.getUserQueryById(updatedUserQuery.getId());

        TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdUserQuery.getId());
        TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdUserQuery.getName());
        TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdUserQuery.getDescription());

        //5. Delete...
        UserQuery deletedUserQuery = userQueryClient.deleteUserQuery(byIdUserQuery,true);
        TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedUserQuery.getId());
    }


    /**
     *
     */
    @Test
    @Ignore
    public void executeUserQueryWithSpecificName()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        UserQueryClient userQueryClient = new UserQueryClient(BASE_URL, serviceTicket);

        UserQuery userQueryToExec = new UserQuery();
        userQueryToExec.setName("Client is Input");

        List<Field> inputs = new ArrayList();

        List<String> selectedClients = new ArrayList();
        selectedClients.add("PPC");

        MultiChoice selectedMultiChoices = new MultiChoice(selectedClients);

        inputs.add(new Field("Client",selectedMultiChoices));
        userQueryToExec.setInputs(inputs);

        long start = System.currentTimeMillis();

        FluidItemListing itemListing =
                userQueryClient.executeUserQuery(userQueryToExec);

        if(itemListing.getListingCount() > 0)
        {
            for(FluidItem returnVal :itemListing.getListing())
            {
                System.out.println("*** [[[ " + returnVal.getForm().getFormType() + " - "+
                        returnVal.getForm().getTitle() + " ]]] ***");

                for(Field formField : returnVal.getForm().getFormFields())
                {
                    System.out.println(formField.getFieldName() + " : "+formField.getFieldValue());
                }
            }
        }

        long took = (System.currentTimeMillis() - start);
        System.out.println("Took '"+took+"' millis for '"+itemListing.getListingCount()+
                "' records.");
    }
}
