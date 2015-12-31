package com.fluid.ws.client.v1.flowitem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Attachment;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.flow.FlowClient;
import com.fluid.ws.client.v1.flow.TestFlowClient;
import com.fluid.ws.client.v1.user.LoginClient;
import com.google.common.io.BaseEncoding;

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
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @Test
    public void testCreateEmailFormAndSendToWorkflow()
    {
        if(!this.loginClient.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        FlowItemClient flowItmClient = new FlowItemClient(serviceTicket);
        FlowClient flowClient = new FlowClient(serviceTicket);

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
                BaseEncoding.base64().encode(jsonObject.toString().getBytes()));

        attachmentToAdd.setName("Test assessment JSON.json");
        attachmentToAdd.setContentType("application/json");

        //Second Attachment...
        Attachment secondToAdd = new Attachment();
        secondToAdd.setAttachmentDataBase64(
                BaseEncoding.base64().encode("De Beers".toString().getBytes()));

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
}
