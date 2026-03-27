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

package com.fluidbpm.ws.client.v1.pericard;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.asn1der.ws.ABaseTestASNDER;
import com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.config.GlobalFieldClient;
import com.fluidbpm.ws.client.v1.crypto.KeystoreTestUtil;
import com.fluidbpm.ws.client.v1.crypto.KeystoreUtil;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilClient;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.user.UserClient;
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
import java.util.concurrent.TimeUnit;

import static com.fluidbpm.ws.client.FluidClientException.ErrorCode.NO_RESULT;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 *
 */
@Log
public class TestPericard extends ABaseTestASNDER {
    private String lastKeystoreOrg;
    private String lastKeystoreAlias;
    private String lastHostAlias;

    @Override
    @Before
    public void init() {
        super.init();
    }

    @Test
    public void testOrgAndKeystoreOnboardRequest() {
        if (this.isConnectionInValid) return;

        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             SQLUtilClient sqlUtl = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testOrgAndKeystoreOnboardRequest)");
                return;
            }

            String flowNameOrg = "Organisation Onboard", flowNameKeystore = "Keystore Load";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Organisation Onboard Request"));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Organisation"));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Keystore Request"));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Keystore"));

            JobView viewCheckerOrg = flowStepClient.getStandardJobViewBy(
                    flowNameOrg,
                    "Organisation Onboard Checker",
                    "Organisation Onboard Checker"
            );
            JobView viewProcResultOrg = flowStepClient.getStandardJobViewBy(
                    flowNameOrg,
                    "Organisation Onboard Processed Result",
                    "Organisation Onboard Processed Result"
            );
            JobView viewCheckerKS = flowStepClient.getStandardJobViewBy(
                    flowNameKeystore,
                    "Keystore Load Checker",
                    "Keystore Load Checker"
            );
            JobView viewProcResultKS = flowStepClient.getStandardJobViewBy(
                    flowNameKeystore,
                    "Keystore Load Processed Result",
                    "Keystore Load Processed Result"
            );

            // ASN1DER: create the work-items:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();
            int orgsPPBefore = payPop.getAvailableMultiChoicesForm("Organisation").size();

            sleepForSeconds(1);
            int itemCount = 1, threadCount = 1;
            log.info("Onboard Request!: "+itemCount+", Thread Count: "+threadCount);
            // 1. ORGANISATION ONBOARD:
            FluidItem flItmOrgOnReq = orgOnboardItem(UUID.randomUUID().toString());
            List<Long> createdFormIdsOrg = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameOrg, viewCheckerOrg,
                    () -> flItmOrgOnReq
            );
            TestCase.assertNotNull(createdFormIdsOrg);
            TestCase.assertEquals(itemCount, createdFormIdsOrg.size());

            // Approve the Request:
            createdFormIdsOrg.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerOrg, viewProcResultOrg);
            });

            // 2. REQUEST KEYSTORE:
            String ksType = "PKCS12", ksPass = "testpass", keyPass = "testkey";
            byte[] keystoreBytes;
            try {
                char[] ksPassChars = ksPass.toCharArray();
                keystoreBytes = KeystoreTestUtil.testKeystore(
                        ksType, ksPassChars, keyPass.toCharArray()
                );

                String type = KeystoreUtil.detectKeystoreType(keystoreBytes, ksPassChars);
                TestCase.assertEquals(ksType, type);
            } catch (Exception e) {
                log.severe("Failed to create keystore: "+e.getMessage());
                TestCase.fail("Failed to create keystore: "+e.getMessage());
                return;
            }
            log.info("Keystore: "+keystoreBytes.length+" bytes.");

            // Refresh payload populate for the org:
            payPop = derClient.requestFullPayloadPopulate();

            String newOrgName = flItmOrgOnReq.getForm().getFieldValueAsString("Entity Name");
            this.lastKeystoreOrg = newOrgName;
            List<String> orgsPP = payPop.getAvailableMultiChoicesForm("Organisation");

            TestCase.assertTrue("New Org '"+newOrgName+"' not found in PayloadPopulate! Only have("+
                    orgsPP.size()+", before "+orgsPPBefore+"): "+orgsPP, orgsPP.contains(newOrgName));

            // Keystore Request (Load Keystore):
            FluidItem flItmKSLoadReq = ksLoadItem(
                    newOrgName,
                    ksType,
                    PROVIDER_NAME,
                    ksPass,
                    keyPass,
                    UUID.randomUUID().toString(),
                    keystoreBytes
            );
            List<Long> createdFormIdsKS = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameKeystore, viewCheckerKS,
                    () -> flItmKSLoadReq
            );
            TestCase.assertNotNull(createdFormIdsKS);
            TestCase.assertEquals(itemCount, createdFormIdsKS.size());

            // Verify the created keystore:
            FluidItem ksReqById = this.fluidItemByFormId(
                    derClient, createdFormIdsKS.get(0),
                    true// Include route fields.
            );
            TestCase.assertNotNull(ksReqById);
            Form ksReqForm = ksReqById.getForm();
            TestCase.assertNotNull(ksReqForm);
            String keyAlias = ksReqForm.getFieldValueAsString("Alias");
            TestCase.assertNotNull(keyAlias);
            this.lastKeystoreAlias = keyAlias;
            TestCase.assertNotNull(ksReqForm.getFieldValueAsString("Organisation"));
            TestCase.assertNotNull(ksReqForm.getFieldValueAsString("Keystore Type"));
            TestCase.assertNotNull(ksReqForm.getFieldValueAsString("Keystore Provider"));
            TestCase.assertNotNull(ksReqForm.getFieldValueAsString("Keystore Password"));
            TestCase.assertNotNull(ksReqForm.getFieldValueAsString("Keystore Private Key Password"));

            List<Form> tableRecords = this.tableRecords(derClient, ksReqById.getForm(), null);
            TestCase.assertNotNull(tableRecords);
            TestCase.assertEquals(3, tableRecords.size());

            // Now we have to approve the Keystore.
            List<Form> descKSReq = sqlUtl.getDescendants(
                    ksReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descKSReq);
            TestCase.assertEquals("No Keystore yet!",0, descKSReq.size());

            // Approve the Request to create the keystore!:
            createdFormIdsKS.forEach(id -> {
                approveFormId(derClient, uc, fcc, id, viewCheckerKS, viewProcResultKS);
            });

            // Once approved, we have the request linked to the Keystore:
            descKSReq = sqlUtl.getDescendants(
                    ksReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descKSReq);
            TestCase.assertEquals("Expected one descendant. The Keystore!",1, descKSReq.size());
        }
    }

    @Test
    public void testHSMHostConfigRequest() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastKeystoreOrg, lastKeystoreAlias)) {
            this.testOrgAndKeystoreOnboardRequest();
        }

        String host = String.format("Host-%s", UUID.randomUUID().toString());
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             ConfigurationClient confClient = new ConfigurationClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testHSMHostConfigRequest)");
                return;
            }

            if (UtilGlobal.isBlank(this.lastKeystoreOrg, lastKeystoreAlias)) {
                TestCase.fail("Org. and Keystore not yet created!");
                return;
            }

            PayloadPopulate pop = derClient.requestFullPayloadPopulate();

            //Update 3rd Part Lib:
            ThirdPartyLibrary _3rdPL = new ThirdPartyLibrary();
            _3rdPL.setDescription("Updated via Pericard module at "+new Date().toString()+"!");
            confClient.upsertThirdPartyLibrary(_3rdPL, true);

            // Host with entries:
            Form hsmHost = this.createHostWithEntriesAndExec(
                    derClient,
                    pop,
                    host,
                    this.lastKeystoreOrg,
                    this.lastKeystoreAlias
            );
            TestCase.assertNotNull(hsmHost);
            TestCase.assertNotNull(host, hsmHost.getFieldValueAsString("Alias"));
            this.lastHostAlias = host;
        }
    }

    @Test
    public void testGenerateHSMKeyRequest() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastHostAlias)) {
            this.testHSMHostConfigRequest();
        }
        TestCase.assertNotNull("Expected 'Key Generation Host'!!!", this.lastHostAlias);

        int itemCount = 1, threadCount = 1;
        String flowNameGenHsmKey = "Generate HSM Key";
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             SQLUtilClient sqlUtl = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGenerateHSMKeyRequest)");
                return;
            }
            
            JobView viewCheckerGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameGenHsmKey,
                    "Generate HSM Key Checker",
                    "Generate HSM Key Checker"
            );
            JobView viewProcResultGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameGenHsmKey,
                    "Generate HSM Key Processed Result",
                    "Generate HSM Key Processed Result"
            );

            // Refresh payload populate for the HSM key req:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();
            
            FluidItem generateKeyRequest = generateHsmKeyRequestItem(
                    UUID.randomUUID().toString(),
                    this.lastHostAlias,
                    "Zone Master Key"
            );

            List<Long> createdIdsGenHsmKeyReq = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameGenHsmKey, viewCheckerGenKey,
                    () -> generateKeyRequest
            );
            TestCase.assertNotNull(createdIdsGenHsmKeyReq);
            TestCase.assertEquals(itemCount, createdIdsGenHsmKeyReq.size());

            FluidItem keyGenReqById = this.fluidItemByFormId(
                    derClient, createdIdsGenHsmKeyReq.get(0),
                    true// Include route fields.
            );
            TestCase.assertNotNull(keyGenReqById);
            Form keyGenReqForm = keyGenReqById.getForm();
            TestCase.assertNotNull(keyGenReqForm);
            TestCase.assertEquals(FluidItem.FlowState.WorkInProgress, keyGenReqById.getFlowState());
            TestCase.assertEquals("Generate HSM Key Request", keyGenReqForm.getFormType());
            TestCase.assertEquals("admin", keyGenReqForm.getFieldValueAsString("User Maker"));
            String keyAlias = keyGenReqForm.getFieldValueAsString("Alias");
            TestCase.assertNotNull(keyAlias);
            String keyReqType = "Zone Master Key";
            TestCase.assertNotNull(keyGenReqForm.getFieldValueAsString("Key Generation Host"));
            TestCase.assertEquals(keyReqType, keyGenReqForm.getFieldValueAsString("HSM Key Type or Usage"));
            TestCase.assertNotNull(keyGenReqForm.getFieldValueAsString("Key Purpose"));
            TestCase.assertEquals("Thales Key Block", keyGenReqForm.getFieldValueAsString("HSM Key Scheme"));
            TestCase.assertEquals("Triple Length DES Key", keyGenReqForm.getFieldValueAsString("HSM Key Algorithm"));
            TestCase.assertEquals("None (No Restrictions)", keyGenReqForm.getFieldValueAsString("HSM Key Mode of Use"));
            TestCase.assertEquals(1, keyGenReqForm.getFieldValueAsInt("HSM Key Version Number").intValue());
            TestCase.assertEquals("Exported in a Trusted Key Block", keyGenReqForm.getFieldValueAsString("HSM Key Exportability"));

            List<Form> descGenKeyReq = sqlUtl.getDescendants(
                    keyGenReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descGenKeyReq);
            TestCase.assertEquals("No Key Generation request yet!",0, descGenKeyReq.size());

            // Approve the Key Gen Request:
            createdIdsGenHsmKeyReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, keyReqType);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No ZMK on alias "+keyAlias+"!",1, keysWithAlias.size());

            // Wait for the workflow to create the ZMK:
            sleepForSeconds(3);

            FluidItem zmk = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(zmk);
            Form zmkForm = zmk.getForm();
            TestCase.assertNotNull(zmkForm);
            TestCase.assertEquals(keyReqType, zmkForm.getFormType());
            //Ensure the item is done being created.
            TestCase.assertEquals(FluidItem.FlowState.NotInFlow, zmk.getFlowState());
            TestCase.assertEquals("Open", zmkForm.getState());
            TestCase.assertEquals(FluidItem.FlowState.NotInFlow.name(), zmkForm.getFlowState());
            TestCase.assertNotNull(zmkForm.getFieldValueAsString("Key Check Value"));
            TestCase.assertNotNull(zmkForm.getFieldValueAsString("HSM Key Block Cryptogram LMK MFK"));
            TestCase.assertEquals(this.lastHostAlias, zmkForm.getFieldValueAsString("Key Generation Host"));

            // Once approved, we have the request linked to the Keystore:
            descGenKeyReq = sqlUtl.getDescendants(
                    keyGenReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descGenKeyReq);
            TestCase.assertEquals("Expected one descendant. The Generated ZMK!",1, descGenKeyReq.size());
        }
    }

    private void approveFormId(
            WebSocketASNDERClient derClient,
            UserClient uc,
            FormContainerClient fcClient,
            Long id,
            JobView viewChecker,
            JobView viewProcResult
    ) {
        FluidItem byId = this.fluidItemByFormId(derClient, id, false);
        TestCase.assertNotNull(byId);

        Form form = byId.getForm();
        // Lock and approve.
        this.lockFormContainer(derClient, form, viewChecker, null);
        form.setFieldValue(
                "User Checker Decision",
                new MultiChoice("Approve")
                , Field.Type.MultipleChoice
        );
        this.updateFormContainer(derClient, form);

        // Approved to be processed!
        this.sendOn(derClient, byId);

        // Check if processed:
        int waitTimeSec = 10;
        List<FluidItem> processedItems = this.executeUntilOrTOFromView(
                derClient, viewProcResult, 1, waitTimeSec
        );
        if (processedItems == null) {
            List<FormHistoricData> getHistory = this.getHistory(derClient, form, true);
            List<FormFlowHistoricData> flowHistoryData = fcClient.getFormFlowHistoricData(form);

            System.out.println("-----> Field History Data:");
            getHistory.forEach(h -> {
                System.out.println(h.toString());
            });
            System.out.println("-----> Flow History Data:");
            flowHistoryData.forEach(h -> {
                System.out.println(h.toString());
            });
        }
        TestCase.assertNotNull("Processed items is not set after '"+waitTimeSec+"s'!", processedItems);

        // Clear the PI before we clean-up:
        clearPI(derClient, uc.getLoggedInUserInformation());
    }

    public static final String[] COUNTRIES = {"ZA", "NL", "AD", "FR"};

    private static FluidItem orgOnboardItem(String identifier) {
        Form frm = new Form("Organisation Onboard Request", new Date().toString()+ " "+identifier);
        frm.setFieldValue("Contact Number", String.format("CellNr-%s", identifier), Field.Type.Text);
        frm.setFieldValue("Entity Name", String.format("OrgName-%s", identifier), Field.Type.Text);
        frm.setFieldValue("Registration Number", String.format("Reg-%s", identifier), Field.Type.Text);
        
        frm.setFieldValue("Country", new MultiChoice(
                COUNTRIES[(int) (Math.random() * COUNTRIES.length)]
        ), Field.Type.MultipleChoice);
        return new FluidItem(frm);
    }

    private static FluidItem ksLoadItem(
            String org,
            String keystoreType,
            String keystoreProvider,
            String keystorePass,
            String keystoreKeyPass,
            String identifier,
            byte[] keystoreBytes
    ) {
        Form frm = new Form("Keystore Request", new Date().toString()+ " "+identifier);
        frm.setFieldValue("Alias", String.format("AliasKS-%s", identifier), Field.Type.Text);
        frm.setFieldValue("Keystore Password", keystorePass, Field.Type.TextEncrypted);
        frm.setFieldValue("Keystore Private Key Password", keystoreKeyPass, Field.Type.TextEncrypted);

        frm.setFieldValue("Organisation", new MultiChoice(org), Field.Type.MultipleChoice);
        frm.setFieldValue("Keystore Type", new MultiChoice(keystoreType), Field.Type.MultipleChoice);
        frm.setFieldValue("Keystore Provider", new MultiChoice(keystoreProvider), Field.Type.MultipleChoice);

        FluidItem fldItm = new FluidItem(frm);
        fldItm.setAttachments(new ArrayList<>());
        fldItm.getAttachments().add(new Attachment(keystoreBytes, "thestore.jks", "application/octet-stream"));
        return fldItm;
    }

    public static final String[] HSM_COMMAND_TYPES = {"Thales Host Command Set", "Atalla Command Language","Futurex Cryptographic Command Language"};

    private Form createHostWithEntriesAndExec(
            WebSocketASNDERClient derClient,
            PayloadPopulate payPop,
            String hostAlias,
            String existingOrg,
            String existingKSAlias
    ) {
        Form frmHost = new Form("HSM Host", new Date().toString());

        frmHost.setFieldValue("Alias", hostAlias, Field.Type.Text);
        frmHost.setFieldValue("Organisation", new MultiChoice(existingOrg), Field.Type.Text);
        frmHost.setFieldValue("HSM Command Type", new MultiChoice(
                HSM_COMMAND_TYPES[(int) (Math.random() * HSM_COMMAND_TYPES.length)]
        ), Field.Type.MultipleChoice);

        Form createdHostForm = this.createFormContainer(
                derClient,
                payPop,
                frmHost
        );
        Form parentForm = new Form(createdHostForm.getId());

        // TB - Keystore
        Form tbFormHostCommsKs = new Form("Host Communication Keystore", "Host Comms "+new Date().toString());
        tbFormHostCommsKs.setFieldValue("Keystore Alias", new MultiChoice(existingKSAlias), Field.Type.MultipleChoice);
        tbFormHostCommsKs.setFieldValue("Use Until", new Date(
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(31)
        ), Field.Type.DateTime);

        createdHostForm.setFieldValue(
                "Host Communication Keystore Entries",
                this.createTableRecord(
                        derClient,
                        payPop,
                        new TableRecord(
                                tbFormHostCommsKs,
                                parentForm,
                                new Field("Host Communication Keystore Entries")
                        )
                ),
                Field.Type.Table
        );

        // TB - Host Endpoint
        Form tbFormHostEndpoint = new Form("HSM Host Endpoint", "Host Endpoint "+new Date().toString());
        tbFormHostEndpoint.setFieldValue("HSM Host", "127.0.0.1", Field.Type.Text);
        tbFormHostEndpoint.setFieldValue("HSM Port", 21121, Field.Type.Decimal);
        createdHostForm.setFieldValue(
                "Host Endpoint Entries", this.createTableRecord(
                        derClient,
                        payPop,
                        new TableRecord(tbFormHostEndpoint, parentForm, new Field("Host Endpoint Entries"))
                ), Field.Type.Table
        );

        // 3. Save to create the alias.
        CustomWebAction cwAct = this.execCustomAction(
                derClient, payPop, new CustomWebAction(createdHostForm, "Save")
        );
        Form execResultForm = cwAct.getForm();
        TestCase.assertNotNull(execResultForm);

        return createdHostForm;
    }

    private static FluidItem generateHsmKeyRequestItem(
            String identifier,
            String keyGenerationHost,
            String keyUsage
    ) {
        Form frm = new Form("Generate HSM Key Request", new Date().toString()+ " "+identifier);
        frm.setFieldValue("Alias", String.format("AliasGenZMK-%s", identifier), Field.Type.Text);
        frm.setFieldValue("Key Purpose", String.format("This is purpose. ID: %s", identifier), Field.Type.ParagraphText);
        frm.setFieldValue("Key Generation Host", new MultiChoice(keyGenerationHost), Field.Type.MultipleChoice);
        frm.setFieldValue("HSM Key Type or Usage", new MultiChoice(keyUsage), Field.Type.MultipleChoice);
        frm.setFieldValue("HSM Key Scheme", new MultiChoice("Thales Key Block"), Field.Type.MultipleChoice);
        frm.setFieldValue("HSM Key Algorithm", new MultiChoice("Triple Length DES Key"), Field.Type.MultipleChoice);
        frm.setFieldValue("HSM Key Mode of Use", new MultiChoice("None (No Restrictions)"), Field.Type.MultipleChoice);
        frm.setFieldValue("HSM Key Version Number", 1, Field.Type.Decimal);
        frm.setFieldValue("HSM Key Exportability", new MultiChoice("Exported in a Trusted Key Block"), Field.Type.MultipleChoice);

        return new FluidItem(frm);
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
                if (attempt != null && attempt.size() >= attemptCount) return attempt;
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

    private List<FluidItem> formsByAliasAndType(
            UserQueryClient uqClient,
            String alias,
            String formType
    ) {
        UserQuery uq = new UserQuery();
        uq.setName("Form Type by Alias");
        uq.setInputs(UtilGlobal.toListSafe(
                new Field("Alias", alias),
                new Field("Form Type", new MultiChoice(formType))
        ));
        return uqClient.executeUserQuery(
                uq,
                true,//Ancestor
                true,//Calc Labels
                100,
                0,
                false// Force DB
        );
    }

    @Override
    @After
    public void destroy() {
        PerfStats.printOutcomes();

        log.info("Pericard: Destroying test and cleaning up...");
        super.destroy();
    }

    public static final String IS_ENABLED_PERICARD = "Is Enabled Pericard";
    private boolean isPericardEnabled(GlobalFieldClient gfc) {
        String sysPropVal = UtilGlobal.getProperty(
                System.getProperties(),
                IS_ENABLED_PERICARD,
                UtilGlobal.EMPTY
        );
        if (UtilGlobal.isNotBlank(sysPropVal)) {
            log.info("SystemProperty: Field enabled exists and of type Text: "+IS_ENABLED_PERICARD);
            return isModeEnabled(sysPropVal.trim().toLowerCase());
        }

        try {
            Field enabled = gfc.getFieldValueByName(IS_ENABLED_PERICARD);
            if (enabled == null) {
                return false;
            }
            Boolean boolVal = enabled.getFieldValueAsBoolean();
            if (boolVal != null && boolVal) return true;

            String asTxt = enabled.getFieldValueAsString();
            if (UtilGlobal.isNotBlank(asTxt)) {
                return isModeEnabled(asTxt.trim().toLowerCase());
            }
            else return false;
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != NO_RESULT) {
                throw fce;
            }
            return false;
        }
    }

    private static boolean isModeEnabled(String fieldValue) {
        return (UtilGlobal.isAnyTrue(
                "true".equals(fieldValue),
                "1".equals(fieldValue),
                "yes".equals(fieldValue)
        ));
    }

}
