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

import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestParameter;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.flow.step.ABaseTestFlowStep;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The {@code ABaseTestASNDER} class is a specialized implementation that extends the functionality
 * of {@code ABaseTestFlowStep}. It provides a variety of operations related to form handling,
 * fluid item processing, and WebSocket communications in the ASNDER workflow.
 * <p>
 * Fields:
 * - {@code formDefsToCleanup}: Manages a collection of form definitions that need to be cleaned up after execution.
 * - {@code log}: Used for logging messages to facilitate debugging and monitoring.
 * <p>
 * Methods:
 * - {@code init()}: Initializes the class and sets up any necessary state or resources.
 * <p>
 * - {@code submitCycle(PayloadPopulate pop, int itemCount, int threadPoolCount, String flowName, JobView viewWorkView, Supplier<FluidItem> itemSupplier)}:
 * Executes a submission cycle by leveraging the specified parameters to populate payloads,
 * manage threading, and associate with specific workflow views and flows.
 * <p>
 * - {@code fluidItemByFormId(WebSocketASNDERClient derClient, Long id)}:
 * Retrieves a {@code FluidItem} based on the form ID using the specified WebSocket client.
 * <p>
 * - {@code sendOn(WebSocketASNDERClient derClient, FluidItem flItm)}:
 * Sends a given {@code FluidItem} using the provided WebSocket client.
 * <p>
 * - {@code clearPI(WebSocketASNDERClient derClient, User usrToClearFor)}:
 * Clears process instances for a given user using the specified WebSocket client, returning the updated
 * {@code FluidItemListing}.
 * <p>
 * - {@code lockFormContainer(WebSocketASNDERClient derClient, Form formToLock, JobView viewToLock, User lockAs)}:
 * Locks a form container to prevent concurrent edits, associating it with the provided job view and user.
 * <p>
 * - {@code updateFormContainer(WebSocketASNDERClient derClient, Form formToUpdate)}:
 * Updates the specified form container using the provided WebSocket client.
 * <p>
 * - {@code executeUntilOrTOFromView(WebSocketASNDERClient derClient, JobView view, int attemptCount, int maxWaitSeconds)}:
 * Executes operations iteratively for a given view until a condition is met or a timeout occurs.
 * <p>
 * - {@code getCurrentViewCount(WebSocketASNDERClient derClient, JobView view)}:
 * Retrieves the current count of items in the specified job view using the given WebSocket client.
 * <p>
 * - {@code extractListingCount(FluidItemListing listing)}:
 * Extracts and returns the count of items from a given {@code FluidItemListing}.
 * <p>
 * - {@code destroy()}:
 * Cleans up resources and performs shutdown tasks when the class is no longer needed.
 * <p>
 * - {@code cleanupFormDef(UserQueryClient uqClient, FormContainerClient fcClient, Form formDef)}:
 * Performs cleanup for a specified form definition by interacting with the user query and form container clients.
 */
@Log
public abstract class ABaseTestASNDER extends ABaseTestFlowStep {
    protected List<Form> formDefsToCleanup = new CopyOnWriteArrayList<>();

    @Override
    public void init() {
        super.init();
        PerfStats.reset();
    }

    /**
     * Submits a cycle of tasks to create items asynchronously and verifies the created items.
     *
     * @param pop             The payload populator responsible for augmenting transmission objects.
     * @param itemCount       The total number of items to be created during the cycle.
     * @param threadPoolCount The number of threads to be used for concurrent processing.
     * @param flowName        The name of the flow to associate with the items being created.
     * @param viewWorkView    The job view context in which the items are created and fetched.
     * @param itemSupplier    A supplier providing instances of the items to be created.
     * @return A list of form IDs corresponding to the forms created during the cycle.
     */
    protected List<Long> submitCycle(
            PayloadPopulate pop,
            int itemCount,
            int threadPoolCount,
            String flowName,
            JobView viewWorkView,
            Supplier<FluidItem> itemSupplier
    ) {
        List<Long> createdFormIds = new CopyOnWriteArrayList<>();
        List<WebSocketASNDERClient> wsClients = new ArrayList<>(threadPoolCount);
        int itemWaitSeconds = 15;
        for (int idx = 0; idx < threadPoolCount; idx++) {
            WebSocketASNDERClient asnDerClient = new WebSocketASNDERClient(
                    BASE_URL,
                    ADMIN_SERVICE_TICKET_HEX,
                    TimeUnit.SECONDS.toMillis(60)
            );
            asnDerClient.setPayloadPopulate(pop);
            wsClients.add(asnDerClient);
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

            AtomicInteger fldMaxCreate = new AtomicInteger(), fldMinCreate = new AtomicInteger(10000);
            for (int cycleTimes = 0; cycleTimes < itemCount; cycleTimes++) {
                executor.submit(() -> {
                    FluidItem itm = itemSupplier.get();
                    try {
                        WebSocketASNDERClient wsClient = wsClientLocal.get();
                        int fldCount = itm.getForm().getFormFields().size();
                        fldMaxCreate.set(Math.max(fldMaxCreate.get(), fldCount));
                        fldMinCreate.set(Math.min(fldMinCreate.get(), fldCount));

                        BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FLUID_ITEM);// <= Req Type
                        btFldItmReq.setPayloadPopulate(pop);
                        btFldItmReq.setRequestObject(new RequestObject(ASNGlobal.Path.FlowItem.ITEM_CREATE));
                        itm.setFlow(flowName);
                        btFldItmReq.setTransmissionObject(itm);

                        String ref = PerfStats.timedStart();
                        BaseTransmission btCreatedItm = wsClient.request(btFldItmReq);
                        PerfStats.timedStop(PerfStats.Label.Asn1Der_CreateFluidItem, ref);
                        FluidItem created = (FluidItem) btCreatedItm.getTransmissionObject();

                        TestCase.assertNotNull(created);
                        TestCase.assertNotNull(created.getId());
                        TestCase.assertNotNull(created.getForm().getId());
                        TestCase.assertNotNull(created.getForm().getFormFields());
                        TestCase.assertEquals(fldCount, created.getForm().getFormFields().size());

                        created.getForm().getFormFields().forEach(fld -> {
                            if (fld.getFieldValueAsMultiChoice() instanceof MultiChoice) {
                                MultiChoice mc = fld.getFieldValueAsMultiChoice();
                                List<String> selected = mc.getSelectedMultiChoices();
                                TestCase.assertNotNull(selected);
                                TestCase.assertFalse(selected.isEmpty());
                            }
                        });
                        createdFormIds.add(created.getForm().getId());
                    } catch (Exception e) {
                        String err = "Failed to create item ["+itm.getForm().getFormType()+"]: " + e.getMessage();
                        log.severe(err);
                        TestCase.fail(err);
                    }
                });
            }
            executor.shutdown();
            // Fetch items from View:
            List<FluidItem> itemsFromLookup = this.executeUntilOrTOFromView(
                    derClient, viewWorkView, newExpected, itemWaitSeconds
            );
            TestCase.assertNotNull("Items for lookup is not set!", itemsFromLookup);
            TestCase.assertEquals(newExpected, itemsFromLookup.size());

            long timeTakenInMs = (System.currentTimeMillis() - starter);
            log.info(String.format("ASN1DER-TOOK   [%d (create-only):%d (fetch)]ms to create [%d] items.",
                    PerfStats.totalFor(PerfStats.Label.Asn1Der_CreateFluidItem), timeTakenInMs, itemCount));

            // Verify the stored data:
            AtomicInteger maxCount = new AtomicInteger(0);
            createdFormIds.forEach(id -> {
                FluidItem byId = this.fluidItemByFormId(derClient, id);

                TestCase.assertNotNull(byId);
                TestCase.assertTrue(
                        "The min amount is not reached (" + fldMinCreate.get() + ") ! At " + byId.getForm().getFormFields().size() + " ->\n" + byId.getForm(),
                        byId.getForm().getFormFields().size() >= fldMinCreate.get()
                );
                maxCount.set(Math.max(maxCount.get(), byId.getForm().getFormFields().size()));
            });

            try {
                if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            String err = "Failed to submit cycle: " + e.getMessage();
            log.severe(err);
            TestCase.fail(err);
        } finally {
            wsClients.forEach(WebSocketASNDERClient::close);
        }
        return createdFormIds;
    }

    /**
     * Retrieves a FluidItem associated with a specific form ID by initiating a request
     * through the provided WebSocketASNDERClient. The method constructs the necessary
     * request object, sends it to the server, and returns the corresponding FluidItem
     * object received in the response.
     *
     * @param derClient The WebSocketASNDERClient instance used to communicate with the backend service.
     * @param id        The ID of the form for which the FluidItem needs to be fetched.
     * @return The FluidItem associated with the given form ID.
     */
    protected FluidItem fluidItemByFormId(WebSocketASNDERClient derClient, Long id) {
        long start = System.currentTimeMillis();
        BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FORM);// <= Req Type
        btFldItmReq.setRequestObject(new RequestObject(
                ASNGlobal.Path.FlowItem.ITEM_BY_FORM_ID,
                new RequestParameter(WS.Path.FlowItem.Version1.QueryParam.POPULATE_FORM, Boolean.TRUE),
                new RequestParameter(WS.Path.FlowItem.Version1.QueryParam.EXECUTE_CALCULATED_LABELS, Boolean.FALSE))
        );
        btFldItmReq.setTransmissionObject(new Form(id));

        BaseTransmission btCreatedItm = derClient.request(btFldItmReq);
        FluidItem byId = (FluidItem) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_GetFluidItemByForm, System.currentTimeMillis() - start);
        return byId;
    }

    protected List<Form> tableRecords(
            WebSocketASNDERClient derClient,
            Form form,
            Long formDefFilter
    ) {
        long start = System.currentTimeMillis();
        BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FORM);// <= Req Type
        btFldItmReq.setRequestObject(new RequestObject(
                ASNGlobal.Path.FormContainer.FORM_CONT_GET_TABLE_FORMS,
                new RequestParameter(WS.Path.SQLUtil.Version1.QueryParam.INCLUDE_FIELD_DATA, Boolean.TRUE),
                new RequestParameter(WS.Path.SQLUtil.Version1.QueryParam.FORM_DEFINITION,
                        formDefFilter == null ? -1 : formDefFilter))
        );
        btFldItmReq.setTransmissionObject(new Form(form.getId()));

        BaseTransmission btCreatedItm = derClient.request(btFldItmReq);
        FormListing byId = (FormListing) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_GetTableForms, System.currentTimeMillis() - start);
        return byId.getListing();
    }

    /**
     * Sends a FluidItem asynchronously through the specified WebSocketASNDERClient, allowing it
     * to be processed on the server side. Constructs the required transmission object, augments
     * it with the necessary parameters, and sends the request. The response contains the updated
     * FluidItem.
     *
     * @param derClient The WebSocketASNDERClient used to transmit the request and receive the response.
     * @param flItm     The FluidItem to be sent for processing.
     * @return The updated FluidItem received from the server.
     */
    protected FluidItem sendOn(WebSocketASNDERClient derClient, FluidItem flItm) {
        long start = System.currentTimeMillis();
        BaseTransmission btFldItmReq = new BaseTransmission(ASNGlobal.Type.FLUID_ITEM);
        btFldItmReq.setRequestObject(new RequestObject(
                ASNGlobal.Path.FlowItem.ITEM_SEND_ON,
                new RequestParameter(WS.Path.FlowItem.Version1.QueryParam.ALLOW_COLLABORATOR_SEND_ON, Boolean.TRUE))
        );
        btFldItmReq.setTransmissionObject(flItm);
        btFldItmReq.setPayloadPopulate(derClient.getPayloadPopulate());

        BaseTransmission btCreatedItm = derClient.request(btFldItmReq);
        FluidItem byId = (FluidItem) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_SendOn, System.currentTimeMillis() - start);
        return byId;
    }

    /**
     * Clears the Personal Inventory (PI) for a specified user by initiating a request
     * through the provided WebSocketASNDERClient. The method constructs the necessary
     * request object, sends it to the server, and returns the updated FluidItemListing
     * for the user.
     *
     * @param derClient     The WebSocketASNDERClient used to communicate with the backend service.
     * @param usrToClearFor The user whose Personal Inventory (PI) is to be cleared.
     * @return The updated FluidItemListing representing the cleared Personal Inventory for the user.
     */
    protected FluidItemListing clearPI(WebSocketASNDERClient derClient, User usrToClearFor) {
        long start = System.currentTimeMillis();
        BaseTransmission btUsrReq = new BaseTransmission(ASNGlobal.Type.USER);
        btUsrReq.setRequestObject(new RequestObject(ASNGlobal.Path.PersonalInventory.PI_CLEAR_FORMS));
        btUsrReq.setTransmissionObject(usrToClearFor);
        btUsrReq.setPayloadPopulate(derClient.getPayloadPopulate());

        BaseTransmission btCreatedItm = derClient.request(btUsrReq);
        FluidItemListing listingPI = (FluidItemListing) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_ClearPI, System.currentTimeMillis() - start);
        return listingPI;
    }

    /**
     * Locks a specific form in the Form Container for a given user, allowing it to be accessed in the
     * context of a specified job view. This method sends a request through the provided
     * WebSocketASNDERClient to perform the lock operation, and returns the locked form.
     *
     * @param derClient   The WebSocketASNDERClient instance used to communicate with the backend service.
     * @param formToLock  The form object to be locked.
     * @param viewToLock  The job view context in which the form is being locked. Can be null if no specific job view context is needed.
     * @param lockAs      The user on whose behalf the form is being locked. Can be null if no specific user context is provided.
     * @return            The locked form object returned by the server after the successful operation.
     */
    protected Form lockFormContainer(
            WebSocketASNDERClient derClient,
            Form formToLock,
            JobView viewToLock,
            User lockAs
    ) {
        long viewToLockId = viewToLock == null ? -1 : viewToLock.getId();
        long lockAsId = lockAs == null ? -1 : lockAs.getId();

        long start = System.currentTimeMillis();
        BaseTransmission btUsrReq = new BaseTransmission(ASNGlobal.Type.FORM);
        btUsrReq.setRequestObject(
                new RequestObject(
                        ASNGlobal.Path.FormContainer.FORM_CONT_LOCK,
                        new RequestParameter(WS.Path.FormContainer.Version1.QueryParam.JOB_VIEW, viewToLockId),
                        new RequestParameter(WS.Path.FormContainer.Version1.QueryParam.LOCK_FOR_USER_ID, lockAsId)
                )
        );
        btUsrReq.setTransmissionObject(formToLock);
        btUsrReq.setPayloadPopulate(derClient.getPayloadPopulate());

        BaseTransmission btCreatedItm = derClient.request(btUsrReq);
        Form formRet = (Form) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_LockForm, System.currentTimeMillis() - start);
        return formRet;
    }

    /**
     * Updates a specific form in the system through the provided WebSocketASNDERClient.
     * Constructs a transmission object containing the form to be updated, sends the request,
     * and returns the updated form as received from the backend service.
     *
     * @param derClient The WebSocketASNDERClient instance used to communicate with the backend service.
     * @param formToUpdate The form object to be updated in the system.
     * @return The updated Form object returned by the backend service.
     */
    protected Form updateFormContainer(WebSocketASNDERClient derClient, Form formToUpdate) {
        long start = System.currentTimeMillis();
        BaseTransmission btUsrReq = new BaseTransmission(ASNGlobal.Type.FORM);
        btUsrReq.setRequestObject(
                new RequestObject(ASNGlobal.Path.FormContainer.FORM_CONT_UPDATE)
        );
        btUsrReq.setTransmissionObject(formToUpdate);
        btUsrReq.setPayloadPopulate(derClient.getPayloadPopulate());

        BaseTransmission btCreatedItm = derClient.request(btUsrReq);
        Form formRet = (Form) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_UpdateForm, System.currentTimeMillis() - start);
        return formRet;
    }

    /**
     * Retrieves the historical data listing for the specified form using the given WebSocketASNDERClient.
     * This method constructs the necessary request object, sends it to the server, and processes the
     * response to obtain the historical data. Optionally, the method can include the current state
     * of the form in the historical data based on the provided flag.
     *
     * @param derClient      The WebSocketASNDERClient instance used to communicate with the backend service.
     * @param form           The form object for which the historical data needs to be retrieved.
     * @param histIncCurrent A boolean flag indicating whether to include the current state of the form
     *                       in the historical data (true) or not (false).
     * @return A FormHistoricDataListing object representing the historical data of the specified form.
     */
    protected List<FormHistoricData> getHistory(
            WebSocketASNDERClient derClient,
            Form form,
            boolean histIncCurrent
    ) {
        long start = System.currentTimeMillis();
        BaseTransmission btUsrReq = new BaseTransmission(ASNGlobal.Type.FORM);
        btUsrReq.setRequestObject(
                new RequestObject(
                        ASNGlobal.Path.FormContainer.FORM_CONT_HISTORIC_DATA,
                        new RequestParameter(WS.Path.FormHistory.QueryParam.INCLUDE_CURRENT, histIncCurrent),
                        new RequestParameter(WS.Path.FormHistory.QueryParam.LABEL_FIELD_NAME, false)
                )
        );
        btUsrReq.setTransmissionObject(form);
        btUsrReq.setPayloadPopulate(derClient.getPayloadPopulate());

        BaseTransmission btCreatedItm = derClient.request(btUsrReq);
        FormHistoricDataListing formRet = (FormHistoricDataListing) btCreatedItm.getTransmissionObject();
        PerfStats.increment(PerfStats.Label.Asn1Der_FormHistory, System.currentTimeMillis() - start);
        return formRet.getListing();
    }

    /**
     * Executes a series of requests to fetch a list of FluidItem objects associated with a given job view
     * until either the desired number of items is obtained or the specified timeout is reached.
     * Returns the list of items retrieved or null if the conditions are not met within the timeout period.
     *
     * @param derClient    The WebSocketASNDERClient instance used to communicate with the backend service.
     * @param view         The job view context for which the FluidItem objects are being retrieved.
     * @param attemptCount The desired number of items to be retrieved before completing the operation.
     * @param maxWaitSeconds The maximum number of seconds to wait for the desired number of items to be retrieved.
     * @return A list of FluidItem objects if the desired number of items is retrieved within the timeout period;
     *         otherwise, null.
     */
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
                if (attempt != null && attempt.size() >= attemptCount) return attempt;
                else if (attempt != null) {
                    log.info("DER: Not yet at " + attemptCount + ", at " + attempt.size() + " items.");
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

    private int extractListingCount(FluidItemListing listing) {
        if (listing == null) return 0;
        Integer count = listing.getListingCount();
        if (count != null) return count;
        List<FluidItem> items = listing.getListing();
        return items == null ? 0 : items.size();
    }

    @Override
    public void destroy() {
        log.info("BaseASNDer: Destroying test and cleaning up...");
        super.destroy();

        try (
                FormContainerClient fcClient = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
                UserQueryClient uqClient = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            this.formDefsToCleanup.forEach(formDef -> this.cleanupFormDef(uqClient, fcClient, formDef));
        }
    }

    private void cleanupFormDef(UserQueryClient uqClient, FormContainerClient fcClient, Form formDef) {
        if (formDef != null) {
            //log.info("Pericard: Cleanup for ... " + formDef.getFormType());
            UserQuery uqCleanupTerm = userQueryForFormType(uqClient, formDef.getFormType(),
                    formDef.getFormFields().get(0).getFieldName()
            );
            deleteFormContainersAndUserQuery(
                    uqClient,
                    fcClient,
                    uqCleanupTerm
            );
        }
    }
}
