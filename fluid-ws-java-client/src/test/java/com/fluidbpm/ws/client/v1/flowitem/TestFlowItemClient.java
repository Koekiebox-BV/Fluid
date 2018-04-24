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

package com.fluidbpm.ws.client.v1.flowitem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.TestFlowClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.fluidbpm.ws.client.v1.user.PersonalInventoryClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestFlowItemClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics{
        public static final String FORM_DEFINITION = "Email";
        public static final String FORM_TITLE_PREFIX = "Test api doc with email...";
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
    public void testCreateEmailFormAndSendToWorkflow()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, serviceTicket);
        FlowClient flowClient = new FlowClient(BASE_URL, serviceTicket);

        //1. Form...
        Form frm = new Form(TestStatics.FORM_DEFINITION);
        frm.setTitle(TestStatics.FORM_TITLE_PREFIX+new Date().toString());

        //Fluid Item...
        FluidItem toCreate = new FluidItem();
        toCreate.setForm(frm);

        //2. Attachments...
        List<Attachment> attachments = new ArrayList<Attachment>();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonMemberObject = new JSONObject();

            jsonMemberObject.put("firstname","Jason");
            jsonMemberObject.put("lastname", "Bruwer");
            jsonMemberObject.put("id_number","81212211122");
            jsonMemberObject.put("cellphone","1111");
            jsonMemberObject.put("member_number","ZOOOOL");

            jsonObject.put("member",jsonMemberObject);
        }
        //
        catch (JSONException e) {
            Assert.fail(e.getMessage());
            return;
        }

        //First Attachment...
        Attachment attachmentToAdd = new Attachment();
        attachmentToAdd.setAttachmentDataBase64(
                UtilGlobal.encodeBase64(jsonObject.toString().getBytes()));

        attachmentToAdd.setName("Test assessment JSON.json");
        attachmentToAdd.setContentType("application/json");

        //Second Attachment...
        Attachment secondToAdd = new Attachment();
        secondToAdd.setAttachmentDataBase64(
                UtilGlobal.encodeBase64("De Beers".toString().getBytes()));

        secondToAdd.setName("Test Text Plain.txt");
        secondToAdd.setContentType("text/plain");

        attachments.add(attachmentToAdd);
        attachments.add(secondToAdd);

        toCreate.setAttachments(attachments);

        //0. Create the Flow...
        Flow flowToCreate = new Flow();
        flowToCreate.setName(TestFlowClient.TestStatics.FLOW_NAME);
        flowToCreate.setDescription(TestFlowClient.TestStatics.FLOW_DESCRIPTION);

        Flow createdFlow = flowClient.createFlow(flowToCreate);

        //Create...
        flowItmClient.createFlowItem(toCreate, createdFlow.getName());

        //Wait for 3 seconds...
        sleepForSeconds(3);

        //TODO @Jason Test if item went into introduction an out at exit.
        //TODO @Jason current state must also be NOT IN FLOW..

        //Confirm item is no longer in flow due to exit rule...
        try{
            flowClient.deleteFlow(createdFlow);

            TestCase.fail("Not allowed to Delete Flow ");
        }
        //
        catch(FluidClientException fluidExcept)
        {
            TestCase.assertEquals("Expected Error Code mismatch.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE,
                    fluidExcept.getErrorCode());
        }

        //Cleanup...
        flowClient.forceDeleteFlow(createdFlow);
    }

    /**
     *
     */
    @Test
    @Ignore
    public void testViewItemsForWorkflowStep()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowStepClient flowStepClient =
                new FlowStepClient(BASE_URL, serviceTicket);

        String stepName = "Have a look";//Make config...
        String flowName = "Approve Bin";//Make config...
        String viewName = "Have a look";//Make config...

        JobView foundView = flowStepClient.getStandardJobViewBy(
                flowName, stepName, viewName);

        FlowItemClient flowItemClient =
                new FlowItemClient(BASE_URL, serviceTicket);

        FluidItemListing itemListingFromView = flowItemClient.getFluidItemsForView(
                        foundView, 10,0,null,null);

        TestCase.assertNotNull("Item listing not set.",itemListingFromView);
        TestCase.assertTrue("Item listing not set",
                itemListingFromView.getListingCount().intValue() > 0);

        FluidItem fluidItemToSendOn = null;
        for(FluidItem fluidItem : itemListingFromView.getListing())
        {
            if(fluidItemToSendOn == null)
            {
                fluidItemToSendOn = fluidItem;
            }

            System.out.println(" *** START *** ");

            System.out.println("Id - "+fluidItem.getId());
            System.out.println("Form Id - "+fluidItem.getForm().getId());
            System.out.println("Form Title - "+fluidItem.getForm().getTitle());

            if(fluidItem.getRouteFields() != null)
            {
                for(Field routeField : fluidItem.getRouteFields())
                {
                    System.out.println("RF["+
                            routeField.getFieldName()+"] : "+
                            routeField.getFieldValue());
                }
            }

            //We have a match... Lock and Send Send on...
            System.out.println(" *** END *** ");
        }
        
        //Fluid item is not set... just stop...
        if(fluidItemToSendOn == null)
        {
            return;
        }

        //Form Container Client...
        FormContainerClient formContClient =
                new FormContainerClient(BASE_URL, serviceTicket);

        Form lockedFormCont = formContClient.lockFormContainer(
                fluidItemToSendOn.getForm(),
                foundView);

        PersonalInventoryClient personalInventoryClient =
                new PersonalInventoryClient(BASE_URL, serviceTicket);

        List<FluidItem> fluidItemListing =
                personalInventoryClient.getPersonalInventoryItems();

        boolean contains = false;
        for(FluidItem fluidItem : fluidItemListing)
        {
            if(lockedFormCont.getId().equals(fluidItem.getForm().getId()))
            {
                contains = true;
                break;
            }
        }

        if(!contains)
        {
            TestCase.fail("Personal Inventory does not contain '"+
                    lockedFormCont.getTitle()+"'.");
        }

        FluidItem itemToSentOn =
                flowItemClient.sendFlowItemOn(fluidItemToSendOn);

    }
}
