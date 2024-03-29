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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.TableRecord;
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
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestSQLUtilClient extends ABaseLoggedInTestCase {
    private Form formTimesheetEntry;
    private Form formTimesheet;

    private static Field[] timesheetEntryFields() {
        Field[] returnVal = new Field[]{
                new Field("Rest JUnit Job Description", null, Field.Type.Text),
                new Field("Rest JUnit Duration", null, Field.Type.Decimal),
                new Field("Rest JUnit Log Date", null, Field.Type.DateTime),
        };
        returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Decimal.PLAIN);
        returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE);

        for (Field fld : returnVal) fld.setFieldDescription(DESC);

        return returnVal;
    }

    private static Field[] timesheetFields() {
        Field[] returnVal = new Field[]{
                new Field("Rest JUnit Employee Name", null, Field.Type.Text),
                new Field("Rest JUnit Timesheet Entry", null, Field.Type.Table),
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
            this.formTimesheetEntry = createFormDef(fdClient, ffClient, "Rest JUnit Timesheet Entry", null, timesheetEntryFields());
            this.formTimesheet = createFormDef(fdClient, ffClient, "Rest Form Def SQL Util Timesheet", null, timesheetFields());
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
    public void testSQLUtilMethods() {
        if (this.isConnectionInValid) return;

        try (
                SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            // Create forms:
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
                                    String.format("Timesheet Entry '%d' of '%d'", trIdx + 1, trCreateCount)
                            );
                            trFormCont.setFieldValue(
                                    timesheetEntryFields()[0].getFieldName(),
                                    UUID.randomUUID().toString(),
                                    timesheetEntryFields()[0].getTypeAsEnum()
                            );// Rest JUnit Job Description
                            trFormCont.setFieldValue(
                                    timesheetEntryFields()[1].getFieldName(),
                                    Math.random() * 1000,
                                    timesheetEntryFields()[1].getTypeAsEnum()
                            );// Rest JUnit Duration
                            trFormCont.setFieldValue(
                                    timesheetEntryFields()[2].getFieldName(),
                                    new Date(),
                                    timesheetEntryFields()[2].getTypeAsEnum()
                            );// Rest JUnit Log Date

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

            sleepForSeconds(7);
            TestCase.assertEquals(ITEM_COUNT_PER_SUB, createdForms.size());

            // Fetch by Form Definition:
            List<Form> tableFormsByDefFilter = sqlUtilClient.getTableForms(
                    createdForms.get(0),
                    false,
                    this.formTimesheetEntry.getFormTypeId()
            );
            TestCase.assertNotNull(tableFormsByDefFilter);
            TestCase.assertEquals(trCreateCount, tableFormsByDefFilter.size());

            // Fetch the forms:
            createdForms.forEach(created -> {
                List<Form> forms = sqlUtilClient.getTableForms(created, true);
                TestCase.assertEquals(trCreateCount, forms.size());

                List<Field> withoutTblFields = sqlUtilClient.getFormFields(created, false);
                TestCase.assertNotNull(withoutTblFields);
                TestCase.assertEquals(1, withoutTblFields.size());

                List<Field> withTblFields = sqlUtilClient.getFormFields(created, true);
                TestCase.assertNotNull(withTblFields);
                TestCase.assertEquals(2, withTblFields.size());

                forms.forEach(tblForm -> {
                    TestCase.assertNotNull(tblForm.getId());
                    TestCase.assertNotNull(tblForm.getFormType());
                    TestCase.assertNotNull(tblForm.getFormTypeId());
                    TestCase.assertNotNull(tblForm.getTitle());
                    TestCase.assertNotNull(tblForm.getCurrentUser());
                    TestCase.assertNotNull(tblForm.getDateCreated());
                    TestCase.assertNotNull(tblForm.getDateLastUpdated());
                    TestCase.assertNotNull(tblForm.getFormFields());
                    TestCase.assertEquals(3, tblForm.getFormFields().size());
                });
            });
        }
    }

    @Test
    public void testSQLUtilExecuteNativeSQLStoredProc() {
        if (this.isConnectionInValid) return;

        try (
                SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            long start = System.currentTimeMillis();
            int numberOfRecords = 1;

            NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();

            nativeSQLQuery.setDatasourceName(FLUID_DS);
            nativeSQLQuery.setStoredProcedure("{call Fluid_GetFormFieldsForFormDefinition(?)}");

            List<SQLColumn> inputs = new ArrayList<>();
            inputs.add(new SQLColumn(null,1, Types.BIGINT, 1L));
            nativeSQLQuery.setSqlInputs(inputs);

            SQLResultSet resultListing = sqlUtilClient.executeSQL(nativeSQLQuery);
            long took = (System.currentTimeMillis() - start);
            start = System.currentTimeMillis();

            log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
            this.validateResultSet(resultListing, 6);
            log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
        }
    }

    @Test
    public void testSQLUtilExecuteNativeSQLOnly() {
        if (this.isConnectionInValid) return;

        try (
                SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            long start = System.currentTimeMillis();
            int numberOfRecords = 1;

            NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();

            nativeSQLQuery.setDatasourceName(FLUID_DS);
            nativeSQLQuery.setQuery("SELECT * FROM form_definition WHERE title = ?");

            List<SQLColumn> inputs = new ArrayList<>();
            inputs.add(new SQLColumn(null,1,Types.VARCHAR, "Email"));
            nativeSQLQuery.setSqlInputs(inputs);

            SQLResultSet resultListing = sqlUtilClient.executeSQL(nativeSQLQuery);
            long took = (System.currentTimeMillis() - start);
            start = System.currentTimeMillis();

            log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
            this.validateResultSet(resultListing, 11);
            log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
        }
    }

    @Test
    @Ignore
    public void testGetDescendants() {
        if (this.isConnectionInValid) return;

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);

        //TODO need to move to Clone step logic.
        
        List<Form> descendants = sqlUtilClient.getDescendants(
                new Form(2909L),
                true,
                true,
                true);

        TestCase.assertNotNull(descendants);
    }

    @Test
    @Ignore
    public void testGetAncestor() {
        if (this.isConnectionInValid) return;

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);

        //TODO need to move to Clone step logic.

        Form ancestor = sqlUtilClient.getAncestor(
                new Form(4071L),
                true,
                false);

        TestCase.assertNotNull(ancestor);
    }

    private void validateResultSet(SQLResultSet resultSet, int expectedColumns) {
        TestCase.assertNotNull(resultSet);
        resultSet.getListing().forEach(row -> {
            TestCase.assertNotNull(row.getSqlColumns());
            TestCase.assertEquals(expectedColumns, row.getSqlColumns().size());
        });
    }
}
