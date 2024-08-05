/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Base test case for a client where you are already logged in.
 */
@Log
public class ABaseLoggedInTestCase extends ABaseTestCase {
	protected static String ADMIN_SERVICE_TICKET;
	protected static String ADMIN_SERVICE_TICKET_HEX;
	protected static Boolean isConnectionInValid;

	protected static String DESC = "Created by step Unit tests.";

	protected int ITEM_COUNT_PER_SUB = 15;

	// Perform global operation for all tests cases.
	static {
		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;
		try (LoginClient loginClient = new LoginClient(BASE_URL)) {
			if (isConnectionInValid == null) isConnectionInValid = !loginClient.isConnectionValid();

			if (!isConnectionInValid) {
				ADMIN_SERVICE_TICKET = loginClient.login(USERNAME, PASSWORD).getServiceTicket();
				ADMIN_SERVICE_TICKET_HEX = UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(ADMIN_SERVICE_TICKET));
			}
		}
	}

	/**
	 * Initialize.
	 */
	@Before
	public void init() {

	}

	/**
	 * Teardown.
	 */
	@After
	public void destroy() {
		// Do nothing.
	}

	protected static Form createFormDef(
			FormDefinitionClient fdClient,
			FormFieldClient ffClient,
			String formDef,
			List<Flow> associatedFlows,
			Field... fields
	) {
		if (fields == null || fields.length < 1) return null;

		try {
			Form formToCreate = new Form(formDef);
			if (associatedFlows != null && !associatedFlows.isEmpty()) {
				formToCreate.setAssociatedFlows(associatedFlows);
			}
			formToCreate.setFormDescription(DESC);

			List<Field> fieldsCreated = new ArrayList<>();
			Stream.of(fields).forEach(itm -> {
				try {
					switch (itm.getTypeAsEnum()) {
						case MultipleChoice:
							fieldsCreated.add(ffClient.createField(itm,
									itm.getFieldValueAsMultiChoice().getAvailableMultiChoices()));
						break;
						case Table:
							fieldsCreated.add(ffClient.createFieldTable(itm, new Form(itm.getFieldName()), false));
						break;
						default:
							fieldsCreated.add(ffClient.createField(itm));
					}
				} catch (FluidClientException fce) {
					if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

					fieldsCreated.add(itm);
				}
			});
			formToCreate.setFormFields(fieldsCreated);

			return fdClient.createFormDefinition(formToCreate);
		} catch (FluidClientException fce) {
			if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

			return fdClient.getFormDefinitionByName(formDef);
		}
	}

	protected static void deleteAllFormData(
			UserQueryClient uqClient,
			FormDefinitionClient fdClient,
			FormFieldClient ffClient,
			FormContainerClient fcClient,
			Form formDefToRemove
	) {
		if (formDefToRemove == null) return;

		UserQuery uqCleanup = userQueryForFormType(uqClient, formDefToRemove.getFormType(),
				formDefToRemove.getFormFields().get(0).getFieldName());

		// Delete the Form Containers and User Query:
		deleteFormContainersAndUserQuery(uqClient, fcClient, uqCleanup);

		// Remove the Definition:
		fdClient.deleteFormDefinition(formDefToRemove);

		if (formDefToRemove.getFormFields() != null) {
			formDefToRemove.getFormFields().forEach(fldItm -> {
				ffClient.forceDeleteField(fldItm);
			});
		}
	}

	protected static void deleteFormContainersAndUserQuery(
			UserQueryClient uqClient,
			FormContainerClient fcClient,
			UserQuery queryToExecAndDel
	) {
		List<FluidItem> toDelete = uqClient.executeUserQuery(
				queryToExecAndDel,
				false,
				10000,
				0
		);
		if (toDelete != null) {
			toDelete.stream()
					.map(itm -> itm.getForm())
					.forEach(itm -> fcClient.deleteFormContainer(itm));
		} else log.warning("No items to delete/cleanup!");

		if (uqClient != null) uqClient.deleteUserQuery(queryToExecAndDel, true);
	}

	protected static UserQuery userQueryForFormType(
			UserQueryClient uqClient,
			String formDef,
			String ... formFields
	) {
		List<Field> inputs = new ArrayList<>();
		if (formFields != null && formFields.length > 0) {
			for (String field : formFields) inputs.add(new Field(field));
		}

		List<String> ruleByFormType = new ArrayList<>();
		ruleByFormType.add(String.format("[Form Type] = '%s'", formDef));
		String uqName = String.format("JUnit By Form Type %s", formDef);
		UserQuery toCreate = new UserQuery(uqName, inputs);
		toCreate.setRules(ruleByFormType);
		toCreate.setInputs(inputs);
		toCreate.setDescription(DESC);

		try {
			return uqClient.createUserQuery(toCreate);
		} catch (FluidClientException fce) {
			if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

			return uqClient.getUserQueryByName(uqName);
		}
	}

	protected static FluidItem item(
			String identifier,
			String formType,
			boolean includeAttachment,
			Field ... fields
	) {
		//Fluid Item...
		Form frm = new Form(formType, new Date().toString()+ " "+identifier);
		Stream.of(fields).forEach(itm -> {
			if (itm.getTypeAsEnum() == null) {
				log.warning("Field ["+itm+"] does not have a type set!");
				return;
			}

			switch (itm.getTypeAsEnum()) {
				case Text:
				case TextEncrypted:
				case ParagraphText:
					frm.setFieldValue(itm.getFieldName(), UUID.randomUUID().toString(), itm.getTypeAsEnum());
					break;
				case TrueFalse:
					frm.setFieldValue(itm.getFieldName(), Boolean.TRUE, itm.getTypeAsEnum());
					break;
				case MultipleChoice:
					MultiChoice multiChoice = itm.getFieldValueAsMultiChoice();
					if (multiChoice != null &&
							multiChoice.getAvailableMultiChoices() != null && !multiChoice.getAvailableMultiChoices().isEmpty()) {
						multiChoice.setSelectedMultiChoice(
								multiChoice.getAvailableMultiChoices().get(0)
						);
					}
					frm.setFieldValue(itm.getFieldName(), multiChoice, itm.getTypeAsEnum());
					break;
				case DateTime:
					frm.setFieldValue(itm.getFieldName(), new Date(), itm.getTypeAsEnum());
					break;
				case Decimal:
					frm.setFieldValue(itm.getFieldName(), Math.random() * 90, itm.getTypeAsEnum());
					break;
			}
		});

		FluidItem toCreate = new FluidItem(frm);
		if (!includeAttachment) return toCreate;

		//2. Attachments...
		List<Attachment> attachments = new ArrayList();

		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject jsonMemberObject = new JSONObject();
			jsonMemberObject.put("firstname","Piet"+identifier);
			jsonMemberObject.put("lastname", "Brarer"+identifier);
			jsonMemberObject.put("id_number",identifier);
			jsonMemberObject.put("cellphone","1111");
			jsonMemberObject.put("member_number","ZOOOOL");
			jsonObject.put("member",jsonMemberObject);
		} catch (JSONException e) {
			Assert.fail(e.getMessage());
			return toCreate;
		}

		//First Attachment...
		Attachment attachmentToAdd = new Attachment();
		attachmentToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64(jsonObject.toString().getBytes()));

		attachmentToAdd.setName("Test assessment JSON.json");
		attachmentToAdd.setContentType("application/json");

		//Second Attachment...
		Attachment secondToAdd = new Attachment();
		secondToAdd.setAttachmentDataBase64(UtilGlobal.encodeBase64("De Beers".toString().getBytes()));

		secondToAdd.setName("Test Text Plain.txt");
		secondToAdd.setContentType("text/plain");

		attachments.add(attachmentToAdd);
		attachments.add(secondToAdd);

		toCreate.setAttachments(attachments);

		return toCreate;
	}
}
