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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test the form historic data on war
 */
@Log
public class TestFormContainerDataHistoryClient extends ABaseLoggedInTestCase {
    private Form formDef;
    private Form formDefCreate;

    private static Field[] fields() {
        List<String> options = new ArrayList<>();
        options.add("Male");
        options.add("Female");
        options.add("Both");

        Field[] returnVal = new Field[]{
                new Field("JUnit Employee Name", null, Field.Type.Text),
                new Field("JUnit Employee Surname", null, Field.Type.Text),
                new Field("JUnit Employee Bio", null, Field.Type.ParagraphText),
                new Field("JUnit Employee Secret", null, Field.Type.TextEncrypted),
                new Field("JUnit Employee Gender", new MultiChoice(options, options), Field.Type.MultipleChoice),
                new Field("JUnit Employee Age", null, Field.Type.Decimal),
                new Field("JUnit Employee Birth Date", null, Field.Type.DateTime),
                new Field("JUnit Employee Is Director", null, Field.Type.TrueFalse),
        };
        returnVal[0].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[1].setTypeMetaData(ABaseFieldClient.FieldMetaData.Text.PLAIN);
        returnVal[2].setTypeMetaData(ABaseFieldClient.FieldMetaData.ParagraphText.PLAIN);
        returnVal[3].setTypeMetaData(ABaseFieldClient.FieldMetaData.EncryptedText.PLAIN);
        returnVal[4].setTypeMetaData(ABaseFieldClient.FieldMetaData.MultiChoice.PLAIN);
        returnVal[5].setTypeMetaData(ABaseFieldClient.FieldMetaData.Decimal.PLAIN);
        returnVal[6].setTypeMetaData(ABaseFieldClient.FieldMetaData.DateTime.DATE_AND_TIME);
        returnVal[7].setTypeMetaData(ABaseFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE);

        for (Field fld : returnVal) fld.setFieldDescription(DESC);

        return returnVal;
    }

    /**
     * Test creating and fetching the form field historic data.
     */
    @Test
    public void testCreateFormHistoricData() {
        if (this.isConnectionInValid) return;

        User user = new User();
        user.setServiceTicket(ADMIN_SERVICE_TICKET);

        try (
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                WebSocketGetFormHistoryByFormClient wsGetFormHist = new WebSocketGetFormHistoryByFormClient(
                        BASE_URL,
                        null,
                        user.getServiceTicketAsHexUpper(),
                        TimeUnit.SECONDS.toMillis(15),
                        false,
                        false)
        ) {
            this.formDefCreate = createFormDef(fdClient, ffClient, "Employee Val Create History", null, fields());
            TestCase.assertNotNull(this.formDefCreate);

            FluidItem toCreate = item(
                    UUID.randomUUID().toString(),
                    this.formDefCreate.getFormType(),
                    false,
                    fields()
            );

            Form created = fcClient.createFormContainer(toCreate.getForm());
            TestCase.assertNotNull(created);
            TestCase.assertNotNull(created.getId());

            Date auditDate = new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(10));

            // Update the form to create history:
            String employeeName = UUID.randomUUID().toString(),
                    employeeSurname = UUID.randomUUID().toString(),
                    employeeSecret = UUID.randomUUID().toString(),
                    employeeBio = String.format(
                            "%s %s %s",
                            UUID.randomUUID().toString(),
                            UUID.randomUUID().toString(),
                            UUID.randomUUID().toString()
                    );
            Form auditForm = new Form(this.formDefCreate.getFormType(), "ThisIsADirectAuditCreate");
            auditForm.setFieldValue("JUnit Employee Name", employeeName, Field.Type.Text);
            auditForm.setFieldValue("JUnit Employee Surname", employeeSurname, Field.Type.Text);
            auditForm.setFieldValue("JUnit Employee Bio", employeeBio, Field.Type.ParagraphText);
            auditForm.setFieldValue("JUnit Employee Secret", employeeSecret, Field.Type.TextEncrypted);
            auditForm.setFieldValue("JUnit Employee Gender", new MultiChoice("Female"), Field.Type.MultipleChoice);
            auditForm.setFieldValue("JUnit Employee Age", Math.random() * 90, Field.Type.Decimal);
            auditForm.setFieldValue("JUnit Employee Birth Date", new Date(), Field.Type.DateTime);
            auditForm.setFieldValue("JUnit Employee Is Director", Boolean.FALSE, Field.Type.TrueFalse);
            auditForm.setId(created.getId());

            FormHistoricData historicData = new FormHistoricData(auditForm, auditDate);
            long start = System.currentTimeMillis();
            fcClient.createFormHistoricData(historicData);
            long roundTripTime = (System.currentTimeMillis() - start);
            TestCase.assertTrue(roundTripTime < 1_000);

            List<FormHistoricData> historicDataEntries =
                    fcClient.getFormAndFieldHistoricData(created, false, false);

            this.validateHistoricData(historicDataEntries);

            FormHistoricData adminUpdated = historicDataEntries.stream()
                    .filter(itm -> "Updated by 'admin'.".equals(itm.getDescription()))
                    .findFirst()
                    .orElse(null);
            TestCase.assertNotNull(adminUpdated);

            JSONObject adminJson = new JSONObject(adminUpdated.getFormContainerFieldValuesJSON());

            TestCase.assertEquals(auditForm.getTitle(), adminJson.get("title"));
            TestCase.assertEquals(9, adminJson.length());
        }
    }

    /**
     * Test fetching the form field historic data.
     */
    @Test
    public void testUpdateAndRetrieveFormHistoricData() {
        if (this.isConnectionInValid) return;

        User user = new User();
        user.setServiceTicket(ADMIN_SERVICE_TICKET);

        try (
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                WebSocketGetFormHistoryByFormClient wsGetFormHist = new WebSocketGetFormHistoryByFormClient(
                     BASE_URL,
                     null,
                     user.getServiceTicketAsHexUpper(),
                        TimeUnit.SECONDS.toMillis(15),
                     false,
                     false)
        ) {
            this.formDef = createFormDef(fdClient, ffClient, "Employee Val History", null, fields());
            TestCase.assertNotNull(this.formDef);

            List<Form> createdForms = new CopyOnWriteArrayList<>();
            ExecutorService executor = Executors.newFixedThreadPool(6);
            StringBuilder content = new StringBuilder();
            for (int cycleTimes = 0; cycleTimes < ITEM_COUNT_PER_SUB; cycleTimes++) {
                executor.submit(() -> {
                    try {
                        FluidItem toCreate = item(
                                UUID.randomUUID().toString(),
                                this.formDef.getFormType(),
                                false,
                                fields()
                        );

                        long start = System.currentTimeMillis();
                        Form created = fcClient.createFormContainer(toCreate.getForm());
                        long roundTripTime = (System.currentTimeMillis() - start);

                        createdForms.add(created);
                        TestCase.assertNotNull(created);
                        TestCase.assertNotNull(created.getId());
                        if (roundTripTime > 500) {
                            content.append(String.format("%nCreate: Item [%s] took [%d]millis!", created.getId(), roundTripTime));
                        }
                        TestCase.assertTrue(roundTripTime < 1_500);

                        // Update the form to create history:
                        String employeeName = UUID.randomUUID().toString(),
                                employeeSurname = UUID.randomUUID().toString(),
                                employeeSecret = UUID.randomUUID().toString(),
                                employeeBio = String.format(
                                        "%s %s %s",
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString()
                                );

                        created.setFieldValue("JUnit Employee Name", employeeName, Field.Type.Text);
                        created.setFieldValue("JUnit Employee Surname", employeeSurname, Field.Type.Text);
                        created.setFieldValue("JUnit Employee Bio", employeeBio, Field.Type.ParagraphText);
                        created.setFieldValue("JUnit Employee Secret", employeeSecret, Field.Type.TextEncrypted);
                        created.setFieldValue("JUnit Employee Gender", new MultiChoice("Female"), Field.Type.MultipleChoice);
                        created.setFieldValue("JUnit Employee Age", Math.random() * 90, Field.Type.Decimal);
                        created.setFieldValue("JUnit Employee Birth Date", new Date(), Field.Type.DateTime);
                        created.setFieldValue("JUnit Employee Is Director", Boolean.FALSE, Field.Type.TrueFalse);

                        start = System.currentTimeMillis();
                        fcClient.updateFormContainer(created);
                        roundTripTime = (System.currentTimeMillis() - start);

                        if (roundTripTime > 500) {
                            content.append(String.format("%nUpdate: Item [%s] took [%d]millis!",
                                    created.getId(), roundTripTime));
                        }
                        TestCase.assertTrue(roundTripTime < 1_500);
                    } catch (Exception err) {
                        err.printStackTrace();
                        log.warning(err.getMessage());
                        TestCase.fail(err.getMessage());
                    }
                });
            }

            sleepForSeconds(7);
            TestCase.assertEquals(createdForms.size(), ITEM_COUNT_PER_SUB);

            // Test the WebService:
            createdForms.forEach(form -> {
                TestCase.assertNotNull(form);

                long start = System.currentTimeMillis();
                List<FormHistoricData> historicDataEntries =
                        fcClient.getFormAndFieldHistoricData(form, false, false);
                long took = (System.currentTimeMillis() - start);
                TestCase.assertTrue(took < 1_000);

                this.validateHistoricData(historicDataEntries);
            });

            // Test the WebSocket fetching all:
            long start = System.currentTimeMillis();
            List<FormHistoricDataListing> historyListing = wsGetFormHist.getByFormSynchronized(createdForms);
            long took = (System.currentTimeMillis() - start);
            TestCase.assertTrue(took < 3_000);

            historyListing.stream()
                    .map(itm -> itm.getListing())
                    //.flatMap(Collection::stream)
                    .forEach(historyData -> {
                        this.validateHistoricData(historyData);
                    });

            // Test the WebSocket fetching single entries:
            AtomicInteger total = new AtomicInteger();
            createdForms.forEach(form -> {
                TestCase.assertNotNull(form);

                long startLcl = System.currentTimeMillis();
                List<FormHistoricData> historicDataEntries =
                        fcClient.getFormAndFieldHistoricData(form, false, false);
                long tookLcl = (System.currentTimeMillis() - startLcl);
                total.set(total.get() + (int)tookLcl);
                TestCase.assertTrue(tookLcl < 300);

                this.validateHistoricData(historicDataEntries);
            });
            TestCase.assertTrue(total.get() < 3_000);
        }
    }

    private void validateHistoricData(List<FormHistoricData> historicDataEntries) {
        TestCase.assertNotNull("No historic data!", historicDataEntries);

        boolean fcLogEntry = false, fieldAndVal = false, dash = false, update = false, create = false;
        for (FormHistoricData historicData : historicDataEntries) {
            switch (historicData.getHistoricEntryType()) {
                case "FormContainerLogEntry" : fcLogEntry = true; break;
                case "FieldAndValue" : fieldAndVal = true; break;
            }

            switch (historicData.getLogEntryType()) {
                case "-" : dash = true; break;
                case "Update" : update = true; break;
                case "Create" : create = true; break;
            }
        }
        TestCase.assertTrue("Not all types match! "+
                        new boolean[]{fcLogEntry, fieldAndVal, dash, update, create},
                UtilGlobal.isAllTrue(fcLogEntry, fieldAndVal, dash, update, create));

    }

    @Override
    public void destroy() {
        super.destroy();

        try (
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formDef);
            deleteAllFormData(uqClient, fdClient, ffClient, fcClient, this.formDefCreate);
        }
    }
}
