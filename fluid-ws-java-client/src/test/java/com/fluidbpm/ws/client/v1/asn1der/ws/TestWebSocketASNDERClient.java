/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2027] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.asn1der.ws;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.attachment.AttachmentListing;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestParameter;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flow.step.ABaseTestFlowStep;
import com.fluidbpm.ws.client.v1.flowitem.FlowItemClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.form.TestFormContainerClient;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;
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
 *
 */
@Log
public class TestWebSocketASNDERClient extends ABaseTestFlowStep {
    private Form formDef;
    private Flow flow;

    @Override
    @Before
    public void init() {
        super.init();
        PerfStats.reset();
    }

    @Test
    public void testAllMethodsForWebKitHighASNDERFreq() {
        if (this.isConnectionInValid) return;

        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FormContainerClient formContainerClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FlowStepRuleClient flowStepRuleClient = new FlowStepRuleClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormFieldClient ffc = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();

            Form toCreateFormDef = new Form(TestFormContainerClient.TestStatics.FORM_DEFINITION);
            toCreateFormDef.setTitle(TestFormContainerClient.TestStatics.FORM_TITLE_PREFIX+new Date().toString());

            List<Field> fields = new ArrayList<>();
            fields.add(new Field(TestFormContainerClient.TestStatics.FieldName.EMAIL_FROM_ADDRESS, "zd2@zool.com"));
            fields.add(new Field(TestFormContainerClient.TestStatics.FieldName.EMAIL_SUBJECT, "This subj..."));
            toCreateFormDef.setFormFields(fields);

            //Create...
            Form createdForm = formContainerClient.createFormContainer(toCreateFormDef);

            Attachment attCreate = new Attachment();
            attCreate.setName(String.format("Test Attachment(%s).json", UUID.randomUUID()));
            attCreate.setContentType("application/json");
            attCreate.setFormId(createdForm.getId());
            attCreate.setAttachmentData("{'name':'cool'}".getBytes());

            // Create attachments:
            BaseTransmission btCreateAtt = new BaseTransmission(ASNGlobal.Type.ATTACHMENT);
            btCreateAtt.setRequestObject(new RequestObject(ASNGlobal.Path.Attachment.ATTACHMENT_CREATE));
            btCreateAtt.setTransmissionObject(attCreate);

            BaseTransmission btAttCreatedWarmup = derClient.request(btCreateAtt);
            TestCase.assertNotNull(btAttCreatedWarmup);

            int count = 100;
            List<Attachment> attachmentsToDel = new ArrayList<>();
            for (int i = 0;i < count;i++) {
                String psAtt = PerfStats.timedStart();
                BaseTransmission btRsp = derClient.request(btCreateAtt);
                PerfStats.timedStop(PerfStats.Label.Asn1DerCreateAttachment, psAtt);

                attachmentsToDel.add((Attachment) btRsp.getTransmissionObject());
            }

            // List all the attachments:
            BaseTransmission btListAtt = new BaseTransmission(ASNGlobal.Type.FORM);// <= Req Type
            btListAtt.setRequestObject(new RequestObject(ASNGlobal.Path.Attachment.ATTACHMENTS_BY_FORM));
            btListAtt.setTransmissionObject(new Form(createdForm.getId()));

            BaseTransmission btAttList = derClient.request(btListAtt);
            AttachmentListing listing = (AttachmentListing) btAttList.getTransmissionObject();
            TestCase.assertNotNull(listing);
            TestCase.assertEquals(count+1, listing.getListing().size());

            // Delete all attachments:
            for (int i = 0;i < attachmentsToDel.size();i++) {
                String psDelAtt = PerfStats.timedStart();
                BaseTransmission btDelAtt = new BaseTransmission(ASNGlobal.Type.ATTACHMENT);
                btDelAtt.setRequestObject(new RequestObject(ASNGlobal.Path.Attachment.ATTACHMENT_DELETE));
                btDelAtt.setTransmissionObject(attachmentsToDel.get(i));
                derClient.request(btDelAtt);
                PerfStats.timedStop(PerfStats.Label.Asn1DerDeleteAttachment, psDelAtt);
            }

            // List attachments:
            String psListAtt = PerfStats.timedStart();
            BaseTransmission btAttListAfterDel = derClient.request(btListAtt);
            PerfStats.timedStop(PerfStats.Label.Asn1DerListAttachment, psListAtt);
            AttachmentListing listingAfterDel = (AttachmentListing) btAttListAfterDel.getTransmissionObject();
            TestCase.assertNotNull(listingAfterDel);
            TestCase.assertEquals(1, listingAfterDel.getListing().size());

            PerfStats.printOutcomes();

            // create the flow:
            final String flowName = "ASN1DER JUnit Assign Flow Test";
            this.flow = new Flow(flowName);
            try {
                this.flow = flowClient.createFlow(new Flow(flowName, "Testing ASN1DER."));
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() == FluidClientException.ErrorCode.DUPLICATE) {
                    flowClient.forceDeleteFlow(this.flow);
                    this.flow = flowClient.createFlow(new Flow(flowName, "Testing ASN1DER."));
                } else throw fce;
            }
            TestCase.assertNotNull(this.flow);

            // create the fields and form:
            this.formDef = this.createFormDefTerminal(fdc, ffc);

            // create the assignment flow step and update the rules:
            FlowStep introductionStep = flowStepClient.getFlowStepByStep(new FlowStep("Introduction", flow));
            List<FlowStepRule> introductionExitRules = flowStepRuleClient.getExitRulesByStep(introductionStep);
            TestCase.assertNotNull(introductionExitRules);
            TestCase.assertEquals(1, introductionExitRules.size());
            flowStepRuleClient.deleteFlowStepExitRule(introductionExitRules.get(0));

            String assignStepName = "ASN Assign Mail Items";
            FlowStep assignStep = new FlowStep(assignStepName, "Step for assignment.");
            assignStep.setFlow(flow);
            assignStep.setFlowStepType(FlowStep.StepType.ASSIGNMENT);
            assignStep = flowStepClient.createFlowStep(assignStep);

            TestCase.assertNotNull(assignStep);

            // update the rules to send to the assignment step and from assignment to exit:
            flowStepRuleClient.createFlowStepExitRule(
                    new FlowStepRule(flow, introductionStep, String.format("ROUTE TO '%s'", assignStepName))
            );
            flowStepRuleClient.createFlowStepExitRule(new FlowStepRule(flow, assignStep, "ROUTE TO 'Exit'"));

            List<JobView> viewsForAssignStep =
                    flowStepClient.getJobViewsByStep(assignStep).getListing();
            TestCase.assertNotNull(viewsForAssignStep);
            TestCase.assertEquals(2, viewsForAssignStep.size());
            JobView viewWorkView = viewsForAssignStep.get(1);

            // ASN1DER: create the work-items:
            PerfStats.reset();
            sleepForSeconds(1);
            log.info("1 THREAD STATS - 100 ITEMS:");
            List<Long> createdFormIds = this.submitCycle(payPop, 100, 1, flowName, viewWorkView);
            PerfStats.printOutcomes();

            log.info("5 THREAD STATS - 300 ITEMS:");
            PerfStats.reset();
            sleepForSeconds(1);
            createdFormIds.addAll(this.submitCycle(payPop,300, 5, flowName, viewWorkView));
            PerfStats.printOutcomes();
        }
    }

    private List<Long> submitCycle(
            PayloadPopulate pop,
            int itemCount,
            int threadPoolCount,
            String flowName,
            JobView viewWorkView
    ) {
        List<Long> createdFormIds = new CopyOnWriteArrayList<>();
        List<WebSocketASNDERClient> wsClients = new ArrayList<>(threadPoolCount);
        for (int idx = 0; idx < threadPoolCount; idx++) {
            wsClients.add(new WebSocketASNDERClient(
                    BASE_URL,
                    ADMIN_SERVICE_TICKET_HEX,
                    TimeUnit.SECONDS.toMillis(60)));
        }
        AtomicInteger rrIndex = new AtomicInteger(0);
        ThreadLocal<WebSocketASNDERClient> wsClientLocal = ThreadLocal.withInitial(() ->
                wsClients.get(Math.floorMod(rrIndex.getAndIncrement(), wsClients.size())));
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60))
        ) {
            ExecutorService executor = Executors.newFixedThreadPool(threadPoolCount);
            long starter = System.currentTimeMillis();
            int currentCount = this.getCurrentViewCount(derClient, viewWorkView);
            int newExpected = currentCount + itemCount;

            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    WebSocketASNDERClient wsClient = wsClientLocal.get();
                    FluidItem termItm = terminalItem(UUID.randomUUID().toString());

                    String ref = PerfStats.timedStart();
                    BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FLUID_ITEM);// <= Req Type
                    btFldItmReq.setPayloadPopulate(pop);
                    btFldItmReq.setRequestObject(new RequestObject(ASNGlobal.Path.FlowItem.ITEM_CREATE));
                    termItm.setFlow(flowName);
                    btFldItmReq.setTransmissionObject(termItm);

                    BaseTransmission btCreatedItm = wsClient.request(btFldItmReq);
                    FluidItem toCreate = (FluidItem) btCreatedItm.getTransmissionObject();
                    PerfStats.timedStop(PerfStats.Label.Asn1DerCreateFluidItem, ref);

                    TestCase.assertNotNull(toCreate);
                    TestCase.assertNotNull(toCreate.getId());
                    createdFormIds.add(toCreate.getForm().getId());
                });
            }
            executor.shutdown();
            // Fetch items from View:
            List<FluidItem> itemsFromLookup = this.executeUntilOrTOFromView(
                    derClient, viewWorkView, newExpected, 20
            );
            TestCase.assertNotNull("Items for lookup is not set!", itemsFromLookup);
            TestCase.assertEquals(newExpected, itemsFromLookup.size());

            long timeTakenInMs = (System.currentTimeMillis() - starter);
            log.info(String.format("ASN1DER-TOOK   [%d (create-only):%d (fetch)]ms to create [%d] items.",
                    PerfStats.totalFor(PerfStats.Label.Asn1DerCreateFluidItem), timeTakenInMs, itemCount));

            // Verify the stored data:
            AtomicInteger maxCount = new AtomicInteger(0);
            createdFormIds.forEach(id -> {
                long start = System.currentTimeMillis();
                BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FORM);// <= Req Type
                btFldItmReq.setRequestObject(new RequestObject(
                        ASNGlobal.Path.FlowItem.ITEM_BY_FORM_ID,
                        new RequestParameter(WS.Path.FlowItem.Version1.QueryParam.POPULATE_FORM, Boolean.TRUE))
                );
                btFldItmReq.setTransmissionObject(new Form(id));

                BaseTransmission btCreatedItm = derClient.request(btFldItmReq);
                FluidItem byId = (FluidItem) btCreatedItm.getTransmissionObject();
                PerfStats.increment(PerfStats.Label.Asn1DerGetFluidItemByForm, System.currentTimeMillis() - start);

                TestCase.assertNotNull(byId);
                TestCase.assertTrue("The min amount is not reached!", byId.getForm().getFormFields().size() >= 6);
                maxCount.set(Math.max(maxCount.get(), byId.getForm().getFormFields().size()));
            });
            TestCase.assertEquals("Not all fields set!", this.formDef.getFormFields().size(), maxCount.get());

            try {
                if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            wsClients.forEach(WebSocketASNDERClient::close);
        }

        sleepForSeconds(1);

        // Traditional REST over JSON:
        try (FlowItemClient flowItmClient = new FlowItemClient(BASE_URL, ADMIN_SERVICE_TICKET)) {
            ExecutorService executor = Executors.newFixedThreadPool(threadPoolCount);
            long starter = System.currentTimeMillis();

            int currentCount = this.getCurrentViewCount(flowItmClient, viewWorkView);
            int newExpected = currentCount + itemCount;

            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem termItm = terminalItem(UUID.randomUUID().toString());

                    String ref = PerfStats.timedStart();
                    FluidItem toCreate = flowItmClient.createFlowItem(termItm, flowName);
                    PerfStats.timedStop(PerfStats.Label.RestCreateFluidItem, ref);

                    TestCase.assertNotNull(toCreate);
                    TestCase.assertNotNull(toCreate.getId());
                });
            }
            // Fetch items from View:
            List<FluidItem> itemsFromLookup = this.executeUntilOrTOFromView(
                    flowItmClient, viewWorkView, newExpected, 20
            );
            TestCase.assertNotNull("Items for lookup is not set!", itemsFromLookup);
            TestCase.assertEquals(newExpected, itemsFromLookup.size());

            long timeTakenInMs = (System.currentTimeMillis() - starter);
            log.info(String.format("REST-JSON-TOOK [%d (create-only):%d (fetch)]ms to create [%d] items.",
                    PerfStats.totalFor(PerfStats.Label.RestCreateFluidItem), timeTakenInMs, itemCount));

            createdFormIds.forEach(id -> {
                long start = System.currentTimeMillis();
                FluidItem byId = flowItmClient.getFluidItemByFormId(id);
                PerfStats.increment(PerfStats.Label.RestGetFluidItemByForm, System.currentTimeMillis() - start);

                TestCase.assertNotNull(byId);
            });
        }
        return createdFormIds;
    }

    private static FluidItem terminalItem(String identifier) {
        Form frm = new Form(FDTerminal.TYPE, new Date().toString()+ " "+identifier);
        frm.setFieldValue(FDTerminal.Field.SERIAL, String.format("SN-%s", identifier), Field.Type.Text);
        frm.setFieldValue(FDTerminal.Field.RKI_PERFORMED, new Boolean(Math.random() < 0.5), Field.Type.TrueFalse);
        frm.setFieldValue(FDTerminal.Field.STATUS, new MultiChoice(
                FDTerminal.Field.STATUS_OPTIONS[(int) (Math.random() * FDTerminal.Field.STATUS_OPTIONS.length)]
        ), Field.Type.MultipleChoice);
        List<String> selectedFeatures = new ArrayList<>();
        for (String option : FDTerminal.Field.FEATURES_OPTIONS) {
            if (Math.random() < 0.5) selectedFeatures.add(option);
        }
        if (selectedFeatures.isEmpty()) {
            selectedFeatures.add(
                    FDTerminal.Field.FEATURES_OPTIONS[(int) (Math.random() * FDTerminal.Field.FEATURES_OPTIONS.length)]
            );
        }
        frm.setFieldValue(FDTerminal.Field.FEATURES, new MultiChoice(selectedFeatures), Field.Type.MultipleChoice);
        frm.setFieldValue(FDTerminal.Field.DATE_MANUFACTURED, new Date(), Field.Type.DateTime);
        frm.setFieldValue(FDTerminal.Field.TIMESTAMP_RKI, new Date(), Field.Type.DateTime);
        frm.setFieldValue(FDTerminal.Field.PRICE_IN_EURO, Math.random(), Field.Type.Decimal);
        return new FluidItem(frm);
    }

    @Override
    @After
    public void destroy() {
        log.info("Destroying test and cleaning up...");
        super.destroy();

        if (this.formDef == null) return;

        try (
                FlowClient flowClient = new FlowClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormDefinitionClient fdClient = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormFieldClient ffClient = new FormFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            // ensure the correct steps have taken place:
            UserQuery uqCleanup = userQueryForFormType(uqClient, this.formDef.getFormType(),
                    this.formDef.getFormFields().get(0).getFieldName()
            );
            deleteFormContainersAndUserQuery(
                    uqClient,
                    fcClient,
                    uqCleanup
            );

            // cleanup:
            if (this.flow == null) this.flow = flowClient.getFlowByName(this.flow.getName());
            flowClient.forceDeleteFlow(this.flow);

            if (this.formDef != null) fdClient.deleteFormDefinition(this.formDef);
            if (this.formDef != null && this.formDef.getFormFields() != null) {
                this.formDef.getFormFields().forEach(fldItm -> {
                    ffClient.forceDeleteField(fldItm);
                });
            }
        }
    }

    private static final class FDTerminal {
        public static final String TYPE = "ASNDer Terminal";

        public static final class Field {
            public static final String SERIAL = "ASN Serial";//Text
            public static final String RKI_PERFORMED = "ASN RKI Performed";//TrueFalse
            public static final String STATUS = "ASN Status";//MC
            public static final String[] STATUS_OPTIONS = {"Active", "Pending", "Inactive", "Terminated"};
            public static final String FEATURES = "ASN Features";//MC
            public static final String[] FEATURES_OPTIONS = {"Dip", "Tap", "Signature", "QR"};
            public static final String DATE_MANUFACTURED = "ASN Date Manufactured";
            public static final String TIMESTAMP_RKI = "ASN Timestamp RKI";
            public static final String PRICE_IN_EURO = "ASN Euro Price";
        }
    }

    private Form createFormDefTerminal(FormDefinitionClient fdClient, FormFieldClient ffClient) {
        Form createdFormDef = new Form(FDTerminal.TYPE);
        try {
            createdFormDef = fdClient.getFormDefinitionByName(createdFormDef.getFormType());
        } catch(FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) {
                TestCase.fail(fce.getMessage());
            }
            createdFormDef.setFormDescription("The terminal which is really great.");

            createdFormDef = createFormDef(
                    fdClient,
                    ffClient,
                    FDTerminal.TYPE,
                    UtilGlobal.toListSafe(this.flow),
                    new Field(FDTerminal.Field.SERIAL, Field.Type.Text, ABaseFieldClient.FieldMetaData.Text.PLAIN),
                    new Field(FDTerminal.Field.RKI_PERFORMED, Field.Type.TrueFalse, ABaseFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE),
                    new Field(
                            FDTerminal.Field.STATUS,
                            Field.Type.MultipleChoice,
                            ABaseFieldClient.FieldMetaData.MultiChoice.PLAIN,
                            new MultiChoice(
                                    UtilGlobal.toListSafe(FDTerminal.Field.STATUS_OPTIONS),
                                    UtilGlobal.toListSafe(FDTerminal.Field.STATUS_OPTIONS)
                            )
                    ),
                    new Field(
                            FDTerminal.Field.FEATURES,
                            Field.Type.MultipleChoice,
                            ABaseFieldClient.FieldMetaData.MultiChoice.SELECT_MANY,
                            new MultiChoice(
                                    UtilGlobal.toListSafe(FDTerminal.Field.FEATURES_OPTIONS),
                                    UtilGlobal.toListSafe(FDTerminal.Field.FEATURES_OPTIONS)
                            )
                    ),
                    new Field(FDTerminal.Field.DATE_MANUFACTURED, Field.Type.DateTime, ABaseFieldClient.FieldMetaData.DateTime.DATE),
                    new Field(FDTerminal.Field.TIMESTAMP_RKI, Field.Type.DateTime, ABaseFieldClient.FieldMetaData.DateTime.DATE_AND_TIME),
                    new Field(FDTerminal.Field.PRICE_IN_EURO, Field.Type.Decimal, ABaseFieldClient.FieldMetaData.Decimal.PLAIN)
            );
        }
        return createdFormDef;
    }

    protected List<FluidItem> executeUntilOrTOFromView(
            WebSocketASNDERClient derClient,
            JobView view,
            int attemptCount,
            int maxWaitSeconds
    ) {
        BaseTransmission btListAtt = new BaseTransmission(ASNGlobal.Type.JOB_VIEW);// <= Req Type
        btListAtt.setRequestObject(new RequestObject(ASNGlobal.Path.FlowItem.ITEMS_FOR_VIEW));
        btListAtt.setTransmissionObject(view);
        
        for (int iter = 0; iter < maxWaitSeconds; iter++) {
            this.sleepForSeconds(3);
            try {
                BaseTransmission btItems = derClient.request(btListAtt);
                FluidItemListing flItmListing = (FluidItemListing) btItems.getTransmissionObject();
                List<FluidItem> attempt = flItmListing.getListing();
                if (attempt != null && attempt.size() == attemptCount) return attempt;
                else if (attempt != null) {
                    log.info("DER: Not yet at "+attemptCount+", at "+attempt.size()+" items.");
                }
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
                if (attemptCount == 0) return null;
            }
        }
        return null;
    }

    private int getCurrentViewCount(WebSocketASNDERClient derClient, JobView view) {
        BaseTransmission btListAtt = new BaseTransmission(ASNGlobal.Type.JOB_VIEW);// <= Req Type
        btListAtt.setRequestObject(new RequestObject(ASNGlobal.Path.FlowItem.ITEMS_FOR_VIEW));
        btListAtt.setTransmissionObject(view);

        try {
            BaseTransmission btItems = derClient.request(btListAtt);
            FluidItemListing flItmListing = (FluidItemListing) btItems.getTransmissionObject();
            return extractListingCount(flItmListing);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            return 0;
        }
    }

    private int getCurrentViewCount(FlowItemClient flowItmClient, JobView view) {
        try {
            FluidItemListing flItmListing = flowItmClient.getFluidItemsForView(view, 10_000, 0);
            return extractListingCount(flItmListing);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            return 0;
        }
    }

    private int extractListingCount(FluidItemListing listing) {
        if (listing == null) return 0;
        Integer count = listing.getListingCount();
        if (count != null) return count;
        List<FluidItem> items = listing.getListing();
        return items == null ? 0 : items.size();
    }
}
