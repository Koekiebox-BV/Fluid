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

package com.fluidbpm.ws.client.v1.sqlutil.wrapper;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
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
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestSQLUtilWebSocketRESTWrapper extends ABaseLoggedInTestCase {

	private Form formTimesheetEntry;
	private Form formTimesheet;

	private boolean valBeforeDisableSQL;

	private static Field[] timesheetEntryFields() {
		Field[] returnVal = new Field[]{
				new Field("SQLUtilWS JUnit Job Description", null, Field.Type.Text),
				new Field("SQLUtilWS JUnit Duration", null, Field.Type.Decimal),
				new Field("SQLUtilWS JUnit Log Date", null, Field.Type.DateTime),
		};
		returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
		returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Decimal.PLAIN);
		returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE);

		for (Field fld : returnVal) fld.setFieldDescription(DESC);

		return returnVal;
	}

	private static Field[] timesheetFields() {
		Field[] returnVal = new Field[]{
				new Field("SQLUtilWS JUnit Employee Name", null, Field.Type.Text),
				new Field("SQLUtilWS JUnit Timesheet Entry", null, Field.Type.Table),
		};
		returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);

		for (Field fld : returnVal) fld.setFieldDescription(DESC);

		return returnVal;
	}

	@Before
	@Override
	public void init() {
		if (!this.isConnectionValid()) return;
		this.valBeforeDisableSQL = SQLUtilWebSocketRESTWrapper.DISABLE_WS;

		// Login:
		super.init();

		try (
				FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, this.serviceTicket);
				FormFieldClient ffClient = new FormFieldClient(BASE_URL, this.serviceTicket);
		) {
			// Create the Form Definition:
			this.formTimesheetEntry = createFormDef(fdClient, ffClient, "SQLUtilWS JUnit Timesheet Entry", null, timesheetEntryFields());
			this.formTimesheet = createFormDef(fdClient, ffClient, "SQLUtilWS Form Def SQL Util Timesheet", null, timesheetFields());
		}
	}

	@After
	@Override
	public void destroy() {
		super.destroy();
		SQLUtilWebSocketRESTWrapper.DISABLE_WS = this.valBeforeDisableSQL;

		try (
				FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, this.serviceTicket);
				FormFieldClient ffClient = new FormFieldClient(BASE_URL, this.serviceTicket);
				FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket);
				UserQueryClient uqClient = new UserQueryClient(BASE_URL, this.serviceTicket)
		) {
			// Timesheet Entry:
			deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formTimesheetEntry);
			deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formTimesheet);
		}
	}

	/**
	 * Plain SQL (no stored proc)
	 */
	@Test
	public void testNativeSQLExecutionQuery() {
		if (!this.isConnectionValid()) return;

		try (SQLUtilWebSocketRESTWrapper wClient = new SQLUtilWebSocketRESTWrapper(
				BASE_URL,
				this.serviceTicket,
				TimeUnit.SECONDS.toMillis(60));
		) {
			NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();
			nativeSQLQuery.setDatasourceName(FLUID_DS);
			nativeSQLQuery.setQuery("SELECT * FROM form_definition WHERE title = ?");

			List<SQLColumn> inputs = new ArrayList<>();
			inputs.add(new SQLColumn(null,1, Types.VARCHAR, "Email"));
			nativeSQLQuery.setSqlInputs(inputs);

			SQLUtilWebSocketRESTWrapper.DISABLE_WS = false;
			this.testMethods(wClient, nativeSQLQuery);
			SQLUtilWebSocketRESTWrapper.DISABLE_WS = true;
			this.testMethods(wClient, nativeSQLQuery);
		}
	}

	private void testMethods(SQLUtilWebSocketRESTWrapper wClient, NativeSQLQuery nativeSQLQuery) {
		List<SQLResultSet> resultSet = wClient.executeNativeSQL(nativeSQLQuery);

		TestCase.assertNotNull(resultSet);
		TestCase.assertEquals(1, resultSet.size());
		resultSet.forEach(itm -> {
			itm.getListing().forEach(row -> {
				TestCase.assertNotNull(row.getSqlColumns());
				TestCase.assertEquals(11, row.getSqlColumns().size());
			});
		});
	}

	@Test
	public void testAllMethodsModeWebSocketAndRest() {
		if (!this.isConnectionValid()) return;

 		try (SQLUtilWebSocketRESTWrapper wClient = new SQLUtilWebSocketRESTWrapper(
				BASE_URL,
				this.serviceTicket,
				TimeUnit.SECONDS.toMillis(60));
			 FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket)
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

			SQLUtilWebSocketRESTWrapper.DISABLE_WS = false;
			this.testMethods(wClient, createdForms, trCreateCount);// with WebSockets enabled.
			SQLUtilWebSocketRESTWrapper.DISABLE_WS = true;
			this.testMethods(wClient, createdForms, trCreateCount);// with REST enabled.
		}
	}

	private void testMethods(SQLUtilWebSocketRESTWrapper wClient, List<Form> createdForms, int trCreateCount) {
		// Single Form:
		List<FormListing> formListing = wClient.getTableForms(false, createdForms.get(0));
		TestCase.assertNotNull(formListing);

		// Fetch the table forms:
		formListing = wClient.getTableForms(true, createdForms);
		TestCase.assertEquals(createdForms.size(), formListing.size());
		formListing.forEach(listing -> TestCase.assertEquals(trCreateCount, listing.getListingCount().intValue()));
		formListing = wClient.getTableForms(false, createdForms);
		TestCase.assertEquals(createdForms.size(), formListing.size());
		formListing.forEach(listing -> TestCase.assertEquals(trCreateCount, listing.getListingCount().intValue()));

		// Fetch the table forms using Form Def Filter:
		formListing = wClient.getTableForms(true, this.formTimesheetEntry.getFormTypeId(), createdForms);
		TestCase.assertEquals(createdForms.size(), formListing.size());
		formListing.forEach(listing -> TestCase.assertEquals(trCreateCount, listing.getListingCount().intValue()));
		formListing = wClient.getTableForms(false, this.formTimesheetEntry.getFormTypeId(), createdForms);
		TestCase.assertEquals(createdForms.size(), formListing.size());
		formListing.forEach(listing -> TestCase.assertEquals(trCreateCount, listing.getListingCount().intValue()));

		// Fetch the Form Fields:
		formListing.forEach(listing -> {
			// Fetch Single:
			List<FormFieldListing> fieldListingFromSingle = wClient.getFormFields(true, listing.getListing().get(0));
			TestCase.assertNotNull(fieldListingFromSingle);
			fieldListingFromSingle = wClient.getFormFields(false, listing.getListing().get(0));
			TestCase.assertNotNull(fieldListingFromSingle);

			// Fetch All:
			List<FormFieldListing> fieldListing = wClient.getFormFields(true, listing.getListing());
			TestCase.assertNotNull(fieldListing);
			TestCase.assertEquals(trCreateCount, fieldListing.size());

			fieldListing = wClient.getFormFields(false, listing.getListing());
			TestCase.assertNotNull(fieldListing);
			TestCase.assertEquals(trCreateCount, fieldListing.size());

			listing.getListing().forEach(form -> {
				List<FormFieldListing> fieldListingInner = wClient.getFormFields(true, form);
				TestCase.assertNotNull(fieldListingInner);
				TestCase.assertEquals(1, fieldListingInner.size());

				fieldListingInner = wClient.getFormFields(false, form);
				TestCase.assertNotNull(fieldListingInner);
				TestCase.assertEquals(1, fieldListingInner.size());
			});
		});

		// Test Fetching the History for Form:
		formListing.forEach(listing -> {
			// Fetch Single:
			List<FormHistoricDataListing> histListingFromSingle = wClient.getFormHistoryByForm(
					true, false, listing.getListing().get(0));
			TestCase.assertNotNull(histListingFromSingle);
			TestCase.assertFalse(histListingFromSingle.get(0).isListingEmpty());

			List<FormHistoricDataListing> histListing = wClient.getFormHistoryByForm(
					false, false, listing.getListing().get(0));
			TestCase.assertNotNull(histListing);
			TestCase.assertFalse(histListing.get(0).isListingEmpty());

			// Fetch All:
			histListing = wClient.getFormHistoryByForm(
					true, true, listing.getListing());
			TestCase.assertNotNull(histListing);
			TestCase.assertEquals(trCreateCount, histListing.size());

			listing.getListing().forEach(form -> {
				List<FormFieldListing> fieldListingInner = wClient.getFormFields(true, form);
				TestCase.assertNotNull(fieldListingInner);
				TestCase.assertEquals(1, fieldListingInner.size());

				fieldListingInner = wClient.getFormFields(false, form);
				TestCase.assertNotNull(fieldListingInner);
				TestCase.assertEquals(1, fieldListingInner.size());
			});
		});

		// Test mass population of field data:
		List<Form> allFormResults = formListing.get(0).getListing();
		int subCount = 3;
		TestCase.assertNotNull(allFormResults);
		TestCase.assertTrue(allFormResults.size() > subCount);

		List<Form> emptyFieldForms = allFormResults.subList(0, subCount).stream()
				.map(itm -> new Form(itm.getId()))
				.collect(Collectors.toList());
		TestCase.assertEquals(subCount, emptyFieldForms.size());
		emptyFieldForms.forEach(form -> TestCase.assertNull(form.getFormFields()));

		wClient.massPopulateFormFields(false, emptyFieldForms);
		emptyFieldForms.forEach(form -> {
			TestCase.assertEquals(3, form.getFormFields().size());
			form.getFormFields().forEach(field -> {
				TestCase.assertNotNull(field.getFieldName());
				TestCase.assertNotNull(field.getFieldValue());
				TestCase.assertNotNull(field.getFieldType());
			});
		});
		emptyFieldForms.forEach(form -> form.setFormFields(null));

		wClient.massPopulateFormFields(true, emptyFieldForms);
		emptyFieldForms.forEach(form -> {
			TestCase.assertEquals(3, form.getFormFields().size());
			form.getFormFields().forEach(field -> {
				TestCase.assertNotNull(field.getFieldName());
				TestCase.assertNotNull(field.getFieldValue());
				TestCase.assertNotNull(field.getFieldType());
			});
		});
	}
}
