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

package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestSQLUtilWebSocketClient extends ABaseLoggedInTestCase {
	private Form formTimesheetEntry;
	private Form formTimesheet;

	private static Field[] timesheetEntryFields() {
		Field[] returnVal = new Field[]{
				new Field("JUnit Job Description", null, Field.Type.Text),
				new Field("JUnit Duration", null, Field.Type.Decimal),
				new Field("JUnit Log Date", null, Field.Type.DateTime),
		};
		returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
		returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Decimal.PLAIN);
		returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE);

		for (Field fld : returnVal) fld.setFieldDescription(DESC);

		return returnVal;
	}

	private static Field[] timesheetFields() {
		Field[] returnVal = new Field[]{
				new Field("JUnit Employee Name", null, Field.Type.Text),
				new Field("JUnit Timesheet Entry", null, Field.Type.Table),
		};
		returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);

		for (Field fld : returnVal) fld.setFieldDescription(DESC);

		return returnVal;
	}

	@Before
	@Override
	public void init() {
		if (this.isConnectionInValid) return;

		// Login:
		super.init();

		try (
				FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
				FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
		) {
			// Create the Form Definition:
			this.formTimesheetEntry = createFormDef(fdClient, ffClient, "JUnit Timesheet Entry", null, timesheetEntryFields());
			this.formTimesheet = createFormDef(fdClient, ffClient, "Form Def SQL Util Timesheet", null, timesheetFields());
		}
	}

	@After
	@Override
	public void destroy() {
		super.destroy();

		try (
				FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
				FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
				FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
				UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET)
		) {
			// Timesheet Entry:
			deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formTimesheetEntry);
			deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formTimesheet);
		}
	}

	@Test
	public void testGetTableFormsWithFormId() {
		if (this.isConnectionInValid) return;

		try (SQLUtilWebSocketGetTableFormsClient webSocketClient = new SQLUtilWebSocketGetTableFormsClient(
				BASE_URL,
				null,
				ADMIN_SERVICE_TICKET_HEX,
				TimeUnit.SECONDS.toMillis(60), true);
			 FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET)
		) {
			List<Form> createdForms = new CopyOnWriteArrayList<>();
			ExecutorService executor = Executors.newFixedThreadPool(6);
			int trCreateCount = 5;
			for (int cycleTimes = 0; cycleTimes < ITEM_COUNT_PER_SUB; cycleTimes++) {
				executor.submit(() -> {
					try {
						FluidItem toCreate = item(
								UUID.randomUUID().toString(),
								this.formTimesheet.getFormType(),
								false,
								timesheetFields()
						);
						Form created = fcClient.createFormContainer(toCreate.getForm());
						createdForms.add(created);
						TestCase.assertNotNull(created);
						TestCase.assertNotNull(created.getId());

						// Create
						for (int trIdx = 0; trIdx < trCreateCount; trIdx++) {
							Form trFormCont = new Form(
									this.formTimesheetEntry.getFormType(),
									String.format("Timesheet Entry '%d' of '%d'", trIdx, trCreateCount)
							);
							trFormCont.setFieldValue(
									timesheetEntryFields()[0].getFieldName(),
									UUID.randomUUID().toString(),
									timesheetEntryFields()[0].getTypeAsEnum()
							);//JUnit Job Description
							trFormCont.setFieldValue(
									timesheetEntryFields()[1].getFieldName(),
									Math.random() * 1000,
									timesheetEntryFields()[1].getTypeAsEnum()
							);//JUnit Duration
							trFormCont.setFieldValue(
									timesheetEntryFields()[2].getFieldName(),
									new Date(),
									timesheetEntryFields()[2].getTypeAsEnum()
							);//JUnit Log Date

							TableRecord tr = new TableRecord(trFormCont, created, timesheetFields()[1]);//JUnit Timesheet Entry
							TableRecord createdTR = fcClient.createTableRecord(tr);
							TestCase.assertNotNull(createdTR);
							TestCase.assertNotNull(createdTR.getFormContainer().getId());
						}
					} catch (Exception err) {
						err.printStackTrace();
						log.warning(err.getMessage());
						TestCase.fail(err.getMessage());
					}
				});
			}

			sleepForSeconds(8);
			TestCase.assertEquals(ITEM_COUNT_PER_SUB, createdForms.size());

			// Fetch the forms:
			List<FormListing> formListing = webSocketClient.getTableFormsSynchronized(createdForms);

			TestCase.assertEquals(formListing.size(), createdForms.size());
			formListing.forEach(listing -> TestCase.assertEquals(trCreateCount, listing.getListingCount().intValue()));
		}
	}

	@Test
	@Ignore
	public void testGetAncestorFormWithSpecificId() {
		if (this.isConnectionInValid) return;

		//TODO need to move to Clone step logic.

		SQLUtilWebSocketGetAncestorClient webSocketClient =
				new SQLUtilWebSocketGetAncestorClient(
						BASE_URL,
						null, ADMIN_SERVICE_TICKET_HEX, TimeUnit.SECONDS.toMillis(60), true, true);

		long start = System.currentTimeMillis();

		Form toGetParentFor = new Form(4071L);
		toGetParentFor.setEcho("event-patoel");

		Form ancestorForm = webSocketClient.getAncestorSynchronized(toGetParentFor);

		TestCase.assertNotNull("Ancestor Form not set.", ancestorForm);

		long took = (System.currentTimeMillis() - start);

		webSocketClient.closeAndClean();

		System.out.println("Took '"+took+"' millis for lookup.");

		System.out.println(ancestorForm.getFormType() +
				" - " + ancestorForm.getTitle());

		if (ancestorForm.getFormFields() != null)
		{
			for (Field field : ancestorForm.getFormFields())
			{
				System.out.println("["+field.getFieldName()+"] = '"+
						field.getFieldValue()+"'");
			}
		}
	}

	@Test
	@Ignore
	public void testGetDescendantFormsWithSpecificId() {
		if (this.isConnectionInValid) return;

		//TODO need to move to Clone step logic.

		SQLUtilWebSocketGetDescendantsClient webSocketClient =
				new SQLUtilWebSocketGetDescendantsClient(
						BASE_URL,
						null,
						ADMIN_SERVICE_TICKET_HEX,
						TimeUnit.SECONDS.toMillis(60),
						true,
						true,
						true,
						false);

		long start = System.currentTimeMillis();

		int numberOfRecords = 1;
		List<FormListing> formListing = webSocketClient.getDescendantsSynchronized(
				generateLotsOfFormsFor(numberOfRecords,
						2575));

		long took = (System.currentTimeMillis() - start);

		webSocketClient.closeAndClean();

		System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

		if (formListing != null)
		{
			for (FormListing listing : formListing)
			{
				//System.out.println("Response For ::: "+listing.getEcho());

				List<Form> descendantsForms = listing.getListing();

				if (descendantsForms == null)
				{
					continue;
				}

				for (Form form : descendantsForms)
				{
					System.out.println("\n--> "+form.getFormType() +
							" - " +
							form.getTitle());

					System.out.println("--> Current User: "+
							((form.getCurrentUser() == null) ? "[Not Set]" :
									form.getCurrentUser().getId()));

					if (form.getFormFields() != null)
					{
						for (Field field : form.getFormFields())
						{
							System.out.println("["+field.getFieldName()+"] = '"+
									field.getFieldValue()+"'");

							if (field.getTypeAsEnum() == Field.Type.Table)
							{
								TableField tableField =
										form.getFieldValueAsTableField(field.getFieldName());

								if (tableField == null ||
										(tableField.getTableRecords() == null ||
												tableField.getTableRecords().isEmpty()))
								{
									continue;
								}

								for (Form tableRecord : tableField.getTableRecords())
								{
									if (tableRecord.getFormFields() == null)
									{
										continue;
									}

									for (Field tableRecordField : tableRecord.getFormFields())
									{
										System.out.println("["+field.getFieldName()+":"+
												tableRecordField.getFieldName()+"] = '"+
												tableRecordField.getFieldValue()+"'");
									}
								}
							}
						}
					}
				}
			}
		} else {
			System.out.println("Nothing...");
		}

		System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	/**
	 * Make use of a WebSocket to test the SQL execution function.
	 */
	@Test
	public void testExecuteSQLWhereIdGreaterThan() {
		if (this.isConnectionInValid) return;

		try (SQLUtilWebSocketExecuteSQLClient webSocketClient = new SQLUtilWebSocketExecuteSQLClient(
				BASE_URL,
				null,
				ADMIN_SERVICE_TICKET_HEX,
				TimeUnit.SECONDS.toMillis(10));
			 FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET)
		) {
			// Create forms:
			List<Form> createdForms = new CopyOnWriteArrayList<>();
			ExecutorService executor = Executors.newFixedThreadPool(6);
			for (int cycleTimes = 0; cycleTimes < ITEM_COUNT_PER_SUB; cycleTimes++) {
				executor.submit(() -> {
					try {
						FluidItem toCreate = item(
								UUID.randomUUID().toString(),
								this.formTimesheet.getFormType(),
								false,
								timesheetFields()
						);
						Form created = fcClient.createFormContainer(toCreate.getForm());
						createdForms.add(created);
						TestCase.assertNotNull(created);
						TestCase.assertNotNull(created.getId());
					} catch (Exception err) {
						err.printStackTrace();
						log.warning(err.getMessage());
						TestCase.fail(err.getMessage());
					}
				});
			}

			sleepForSeconds(5);
			TestCase.assertEquals(ITEM_COUNT_PER_SUB, createdForms.size());

			// Execute the SQL:
			Form formToUse = new Form();
			formToUse.setEcho(UUID.randomUUID().toString());

			//Set the SQL Query as a Field...
			formToUse.setFieldValue("SQL Query", "SELECT * FROM form_container WHERE id > ?;");
			formToUse.getFormFields().add(new Field(1L,"id",new Long(1L), Field.Type.Decimal));

			long start = System.currentTimeMillis();
			List<FormListing> formListing = webSocketClient.executeSQLSynchronized(formToUse);
			long took = (System.currentTimeMillis() - start);
			TestCase.assertNotNull(formListing);
			TestCase.assertFalse(formListing.isEmpty());
			FormListing firstResult = formListing.get(0);
			TestCase.assertFalse("0-Expected results! Count is "+firstResult.getListing().size(), firstResult.getListing().isEmpty());
			TestCase.assertTrue("1-Expected results! Count is "+firstResult.getListingCount(),
					firstResult.getListingCount() >= ITEM_COUNT_PER_SUB);
			TestCase.assertTrue("Took too long. Took "+took,took < 1_000);

			firstResult.getListing().stream()
					.forEach(itm -> {
						TestCase.assertNotNull(itm.getId());
						TestCase.assertNotNull(itm.getFormFields());
						TestCase.assertNotNull(itm.getDateCreated());
						TestCase.assertNotNull(itm.getDateLastUpdated());
					});
		}
	}

	public static Form[] generateLotsOfFormsFor(int numberOfFormsParam,long ... idsToPicFrom) {
		Form[] returnVal = new Form[numberOfFormsParam];
		for (int index = 0;index < numberOfFormsParam; index++) {
			long randomId = idsToPicFrom[0];

			String uuidForm1 = UtilGlobal.randomUUID();
			Form form1 = new Form(randomId);
			form1.setEcho(uuidForm1);

			returnVal[index] = form1;
		}

		return returnVal;
	}
}
