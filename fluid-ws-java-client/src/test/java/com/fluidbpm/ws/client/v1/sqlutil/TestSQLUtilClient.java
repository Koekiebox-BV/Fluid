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
        if (!this.isConnectionValid()) return;

        // Login:
        super.init();

        try (
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, this.serviceTicket);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, this.serviceTicket);
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

    @Test
    public void testGetTableFormsWithFormId() {
        if (!this.isConnectionValid()) return;

        try (
                SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, this.serviceTicket);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, this.serviceTicket)
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
                                    String.format("Timesheet Entry '%d' of '%d'", trIdx, trCreateCount)
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

            sleepForSeconds(8);
            TestCase.assertEquals(ITEM_COUNT_PER_SUB, createdForms.size());

            // Fetch the forms:
            createdForms.forEach(created -> {
                List<Form> forms = sqlUtilClient.getTableForms(created, true);
                TestCase.assertEquals(trCreateCount, forms.size());
            });
        }
    }

    @Test
    @Ignore
    public void testGetDescendants() {
        if (!this.isConnectionValid()) return;

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, this.serviceTicket);

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
        if (!this.isConnectionValid()) return;

        SQLUtilClient sqlUtilClient = new SQLUtilClient(BASE_URL, this.serviceTicket);

        //TODO need to move to Clone step logic.

        Form ancestor = sqlUtilClient.getAncestor(
                //new Form(2575L),
                new Form(4071L),
                true,
                false);

        TestCase.assertNotNull(ancestor);
    }
}
