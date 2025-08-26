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

package com.fluidbpm.ws.client.v1.attachment;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.TestFormContainerClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.google.gson.JsonObject;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jasonbruwer on 18/03/5.
 */
public class TestAttachmentClient extends ABaseLoggedInTestCase {

	private LoginClient loginClient;

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
	public void testAttachmentCRUD() {
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, serviceTicket);
		AttachmentClient attachmentClient = new AttachmentClient(BASE_URL, serviceTicket);

		//1. Create the Form...
		Form toCreate = new Form(TestFormContainerClient.TestStatics.FORM_DEFINITION);
		toCreate.setTitle("Uploading a JSON attachment: "+new Date().toString());

		List<Field> fields = new ArrayList();
		fields.add(new Field(TestFormContainerClient.TestStatics.FieldName.EMAIL_FROM_ADDRESS, "zapper@zool.com"));
		toCreate.setFormFields(fields);

		Form createdForm = formContainerClient.createFormContainer(toCreate);

		//2.1 Create the attachment...
		Attachment attachmentCreate = new Attachment();
		attachmentCreate.setContentType("application/json");
		attachmentCreate.setFormId(createdForm.getId());

		JsonObject attachmentData = new JsonObject();
		attachmentData.addProperty("name","Jason");
		attachmentData.addProperty("surname","Pieta");

		attachmentCreate.setAttachmentDataBase64(
				UtilGlobal.encodeBase64(
						attachmentData.toString().getBytes()));
		attachmentCreate.setName("TheJSONFileToTestWith.json");

		Attachment createdAttachment =
				attachmentClient.createAttachment(attachmentCreate);

		//2.2 Fetch the attachment...
		Attachment attachmentById = attachmentClient.getAttachmentById(
				createdAttachment.getId(), true);

		TestCase.assertNotNull("GET-BY-ID-1: 'Attachment' needs to be set.", attachmentById);
		TestCase.assertNotNull("GET-BY-ID-1: 'Attachment Data' needs to be set.", attachmentById.getAttachmentDataBase64());

		//2.3 Create another with only filename set...
		Attachment attachmentCreateNoContentType = new Attachment();
		attachmentCreate.setFormId(createdForm.getId());
		attachmentCreate.setAttachmentDataBase64(
				UtilGlobal.encodeBase64(
						attachmentData.toString().getBytes()));
		attachmentCreate.setName("TheJSONFileToTestWith-2.json");

		Attachment createdAttachmentTwo =
				attachmentClient.createAttachment(attachmentCreate);


		//2.4 Get all attachments...
		List<Attachment> attachmentsByForm = attachmentClient.getAttachmentsByForm(
				new Form(attachmentById.getFormId()),
				false);

		TestCase.assertNotNull("GET-BY-ID-2: 'Attachment' needs to be set.", attachmentsByForm);
		TestCase.assertEquals("GET-BY-ID-2: 'Attachment' count.",
				2, attachmentsByForm.size());
		TestCase.assertNull("GET-BY-ID-2: 'Attachment Data' MUST NOT be set.",
				attachmentsByForm.get(0).getAttachmentDataBase64());

		TestCase.assertNotNull("CREATE: 'Attachment' needs to be set.", createdAttachment);

		//Cleanup...
		Attachment deletedAttachment = attachmentClient.deleteAttachment(createdAttachment);
		Attachment deletedAttachmentTwo = attachmentClient.deleteAttachment(createdAttachmentTwo);

		Form deletedForm = formContainerClient.deleteFormContainer(createdForm);

		formContainerClient.closeAndClean();
		attachmentClient.closeAndClean();
	}
}
