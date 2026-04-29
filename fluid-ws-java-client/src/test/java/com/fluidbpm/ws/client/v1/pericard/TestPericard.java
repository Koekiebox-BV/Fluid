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
import com.fluidbpm.program.api.vo.field.TableField;
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
import com.fluidbpm.ws.client.v1.crypto.asymmetric.pgp.PGPUtil;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilClient;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.user.UserClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import com.google.common.io.BaseEncoding;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
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
    private String lastZmk;
    private String lastDek;
    private String lastSdek;
    private String lastBdk;
    private String lastPgpKeypair;
    private String lastPgpPublicKey;

    // Sender:
    private PGPUtil.PGPKeyPairResult senderPgpKey;
    private char[] senderPasswordForPgpKP;

    // Receiver:
    private String receiverPgpPublicKey;


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

            // Approve the Request to create the org:
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
                    orgsPP.size()+", before "+orgsPPBefore+"): "+orgsPP, orgsPP.contains(newOrgName)
            );

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
            Form keyStore = descKSReq.get(0);

            TestCase.assertEquals("Keystore", keyStore.getFormType());
            TestCase.assertEquals(this.lastKeystoreOrg, keyStore.getFieldValueAsString("Organisation"));
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
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testHSMHostConfigRequest)");
                return;
            }

            if (UtilGlobal.isBlank(this.lastKeystoreOrg, lastKeystoreAlias)) {
                TestCase.fail("Org. and Keystore not yet created!");
                return;
            }

            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Host Communication Keystore"));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("HSM Host Endpoint"));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("HSM Host"));

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

            // Ensure the host is created correctly:
            FluidItem hsmHostById = this.fluidItemByFormId(
                    derClient, hsmHost.getId(),
                    false// Include route fields.
            );
            TestCase.assertNotNull(hsmHostById);
            Form hsmHostForm = hsmHostById.getForm();
            TestCase.assertNotNull(hsmHostForm);
            TestCase.assertEquals(this.lastHostAlias, hsmHostForm.getFieldValueAsString("Alias"));
            TestCase.assertEquals(this.lastKeystoreOrg, hsmHostForm.getFieldValueAsString("Organisation"));
            TestCase.assertEquals("NotInFlow", hsmHostForm.getFlowState());
            TestCase.assertEquals("Open", hsmHostForm.getState());
            TestCase.assertNull(hsmHostForm.getCurrentUser());
            TestCase.assertNotNull(hsmHostForm.getFieldValueAsString("HSM Command Type"));
            TestCase.assertEquals(1, hsmHostForm.getFieldValueAsTableField("Host Communication Keystore Entries").getTableRecords().size());
            TestCase.assertEquals(1, hsmHostForm.getFieldValueAsTableField("Host Endpoint Entries").getTableRecords().size());
        }
    }

    @Test
    public void testGenerateHSMZoneMasterKeyRequest() {
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
                TimeUnit.SECONDS.toMillis(60)
        );
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             SQLUtilClient sqlUtl = new SQLUtilClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGenerateHSMKeyRequest)");
                return;
            }

            this.formDefsToCleanup.add(fdc.getFormDefinitionByName("Zone Master Key"));
            
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
                    "AliasGenZMK",
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
            this.lastZmk = keyAlias;
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

            sleepForSeconds(3);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, keyReqType);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No ZMK on alias "+keyAlias+"!",1, keysWithAlias.size());

            // Wait for the workflow to create the ZMK:
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
            // HSM Invoked:
            TestCase.assertNotNull(zmkForm.getFieldValueAsString("Key Check Value"));
            TestCase.assertNotNull(zmkForm.getFieldValueAsString("HSM Key Block Cryptogram LMK MFK"));
            TestCase.assertEquals(this.lastHostAlias, zmkForm.getFieldValueAsString("Key Generation Host"));
            TestCase.assertEquals(this.lastKeystoreOrg, zmkForm.getFieldValueAsString("Organisation"));

            // Once approved, we have the request linked to the ZMK:
            descGenKeyReq = sqlUtl.getDescendants(
                    keyGenReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descGenKeyReq);
            TestCase.assertEquals("Expected one descendant. The Generated ZMK!",1, descGenKeyReq.size());
            TestCase.assertTrue(
                    "Expected ZEK to be active.",
                    descGenKeyReq.get(0).getFieldValueAsBoolean("Is Active")
            );
        }
    }

    @Test
    public void testGenerateKeyDEK() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastZmk)) {
            // We need a ZMK!
            this.testGenerateHSMZoneMasterKeyRequest();
        }
        TestCase.assertNotNull("Expected 'ZMK'!!!", this.lastZmk);

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
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGenerateDataKeys)");
                return;
            }

            String keyReqType = "Data Encryption Key";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(keyReqType));

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

            String keyUsage = "Data Encryption using DEK";
            FluidItem generateKeyRequest = generateHsmKeyRequestItem(
                    "AliasGenDEK",
                    UUID.randomUUID().toString(),
                    this.lastHostAlias,
                    keyUsage
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
            Form genDEKReqForm = keyGenReqById.getForm();
            String keyAlias = genDEKReqForm.getFieldValueAsString("Alias");

            // Approve the Key Gen Request (Gen the DEK):
            createdIdsGenHsmKeyReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            // Wait for the DEK to be created:
            sleepForSeconds(3);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, keyReqType);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No DEK on alias "+keyAlias+"!",1, keysWithAlias.size());

            FluidItem dekItm = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(dekItm);
            Form dekForm = dekItm.getForm();
            TestCase.assertNotNull(dekForm);

            // HSM Invoked:
            TestCase.assertNotNull(dekForm.getFieldValueAsString("Key Check Value"));
            TestCase.assertNotNull(dekForm.getFieldValueAsString("HSM Key Block Cryptogram LMK MFK"));
            TestCase.assertEquals(this.lastHostAlias, dekForm.getFieldValueAsString("Key Generation Host"));
            TestCase.assertEquals(this.lastKeystoreOrg, dekForm.getFieldValueAsString("Organisation"));
            // Data Key has usage:
            TestCase.assertEquals(keyUsage, dekForm.getFieldValueAsString("HSM Key Type or Usage"));

            // Once approved, we have the request linked to the ZMK:
            List<Form> descGenKeyReq = sqlUtl.getDescendants(
                    genDEKReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descGenKeyReq);
            TestCase.assertEquals("Expected one descendant. The Generated DEK!",1, descGenKeyReq.size());
            Form dekFormFinal = descGenKeyReq.get(0);
            TestCase.assertTrue(
                    "Expected DEK to be active.",
                    dekFormFinal.getFieldValueAsBoolean("Is Active")
            );
            this.lastDek = dekFormFinal.getFieldValueAsString("Alias");
        }
    }

    @Test
    public void testGenerateHSMProtectedSoftwareDataKey() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastDek)) {
            // We need an HSM Data Key!
            this.testGenerateKeyDEK();
        }
        TestCase.assertNotNull("Expected 'HSM Data Key (DEK)'!!!", this.lastDek);

        int itemCount = 1, threadCount = 1;
        String flowNameGenHsmKey = "Generate HSM Key";
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGenerateHSMProtectedSoftwareDataKey)");
                return;
            }

            String keyReqType = "Generate Software Data Encryption Key Request",
                    resultSoftKeyFrmType = "Software Data Encryption Key",
                    keyRecordFOrmType = "Software Data Key Version History";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(keyReqType));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(resultSoftKeyFrmType));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(keyRecordFOrmType));

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

            String keyUsage = "Software DEK Protected under LMK";
            String identifier = UUID.randomUUID().toString();
            Form frm = new Form(keyReqType, new Date().toString()+ " "+identifier);
            String aliasToCreateForSdek = String.format("GenSoftDataKey-%s", identifier);
            frm.setFieldValue("Alias", aliasToCreateForSdek, Field.Type.Text);
            frm.setFieldValue("Data Encryption Key", new MultiChoice(this.lastDek), Field.Type.MultipleChoice);
            frm.setFieldValue("Key Purpose", String.format("Encrypting... Duh. ID: %s", identifier), Field.Type.ParagraphText);
            frm.setFieldValue("Software Key Cycle Interval", new MultiChoice("Weekly"), Field.Type.MultipleChoice);
            frm.setFieldValue("Software Key Type", new MultiChoice("AES 256 bits (32 bytes)"), Field.Type.MultipleChoice);
            frm.setFieldValue("Software Key Cipher Mode", new MultiChoice("GCM (Galois/Counter Mode)"), Field.Type.MultipleChoice);
            frm.setFieldValue("Encrypted Data Padding", new MultiChoice("PKCS5"), Field.Type.MultipleChoice);

            FluidItem generateSOftKeyRequest = new FluidItem(frm);

            List<Long> createdIdsGenHsmKeyReq = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameGenHsmKey, viewCheckerGenKey,
                    () -> generateSOftKeyRequest
            );
            TestCase.assertNotNull(createdIdsGenHsmKeyReq);
            TestCase.assertEquals(itemCount, createdIdsGenHsmKeyReq.size());

            FluidItem keyGenReqById = this.fluidItemByFormId(
                    derClient, createdIdsGenHsmKeyReq.get(0),
                    true// Include route fields.
            );

            Form genDEKReqForm = keyGenReqById.getForm();
            String keyAlias = genDEKReqForm.getFieldValueAsString("Alias");

            // Approve the Gen Soft Key Request (Gen the SDEK):
            createdIdsGenHsmKeyReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            // Wait for the DEK to be created:
            sleepForSeconds(3);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, resultSoftKeyFrmType);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No SDEK on alias "+keyAlias+"!",1, keysWithAlias.size());

            FluidItem dekItm = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(dekItm);
            Form sdekForm = dekItm.getForm();
            TestCase.assertNotNull(sdekForm);
            TestCase.assertEquals("Software Data Encryption Key", sdekForm.getFormType());
            TestCase.assertEquals("Open", sdekForm.getState());
            TestCase.assertEquals("NotInFlow", sdekForm.getFlowState());
            TestCase.assertNull(sdekForm.getCurrentUser());
            TestCase.assertEquals(1, sdekForm.getFieldValueAsInt("Software Key Version").intValue());
            String sdekAlias = sdekForm.getFieldValueAsString("Alias");
            TestCase.assertEquals(aliasToCreateForSdek, sdekAlias);
            this.lastSdek = sdekAlias;
            TestCase.assertEquals(keyUsage, sdekForm.getFieldValueAsString("HSM Key Type or Usage"));
            TestCase.assertNotNull(sdekForm.getFieldValueAsString("Key Purpose"));
            TestCase.assertEquals(this.lastDek, sdekForm.getFieldValueAsString("Data Encryption Key"));
            TestCase.assertEquals("Weekly", sdekForm.getFieldValueAsString("Software Key Cycle Interval"));
            TestCase.assertEquals(frm.getFieldValueAsString("Software Key Type"), sdekForm.getFieldValueAsString("Software Key Type"));
            TestCase.assertEquals(frm.getFieldValueAsString("Software Key Cipher Mode"), sdekForm.getFieldValueAsString("Software Key Cipher Mode"));
            TestCase.assertEquals(frm.getFieldValueAsString("Encrypted Data Padding"), sdekForm.getFieldValueAsString("Encrypted Data Padding"));

            // HSM Invoked:
            TestCase.assertNotNull(sdekForm.getFieldValueAsString("Key Check Value"));
            TestCase.assertNotNull(sdekForm.getFieldValueAsString("Key Block Protection Key"));

            List<Form> tableRecords = this.tableRecords(derClient, dekItm.getForm(), null);
            TestCase.assertNotNull(tableRecords);
            TestCase.assertEquals(1, tableRecords.size());

            Form firstKey = tableRecords.get(0);
            TestCase.assertNotNull(firstKey.getTitle());
            TestCase.assertEquals(keyRecordFOrmType, firstKey.getFormType());
            TestCase.assertNotNull(firstKey.getFieldValueAsString("Key Check Value"));
            TestCase.assertEquals(1, firstKey.getFieldValueAsInt("Software Key Version").intValue());
            TestCase.assertNotNull(firstKey.getFieldValueAsString("Software Encrypted Key Block"));
        }
    }

    @Test
    public void testUsingSoftwareDataKeyHSMProtected() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastSdek)) {
            // We need an HSM Data Key!
            this.testGenerateHSMProtectedSoftwareDataKey();
        }
        TestCase.assertNotNull("Expected 'HSM Protected Data Key (SDEK)'!!!", this.lastSdek);

        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testUsingSoftwareDataKeyHSMProtected)");
                return;
            }

            // Refresh payload populate for the HSM key req:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();

            // Obtain Clear SDEK Key:
            Form frm = new Form("Software Data Encryption Key", new Date().toString());
            frm.setFieldValue("Alias", this.lastSdek, Field.Type.Text);

            // Obtain clear key (KBPK):
            CustomWebAction cwAct = this.execCustomAction(
                    derClient, payPop, new CustomWebAction(frm, "Obtain Cleartext Key")
            );
            Form execResultForm = cwAct.getForm();
            TestCase.assertNotNull(execResultForm);
            TestCase.assertNotNull(execResultForm.getTitle());
            TestCase.assertEquals(frm.getFormType(), execResultForm.getFormType());
            TestCase.assertNotNull(execResultForm.getFieldValueAsString("Cleartext Key"));

            // Obtain clear key for Key Version:
            frm.setFieldValue("Software Key Version", 1, Field.Type.Decimal);
            cwAct = this.execCustomAction(
                    derClient, payPop, new CustomWebAction(frm, "Obtain Cleartext Key")
            );
            execResultForm = cwAct.getForm();

            // TODO Encrypt
            frm.setFieldValue("Clear Payload",
                    BaseEncoding.base64().encode(new byte[] {0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7}), Field.Type.Text
            );
            frm.setFieldValue("Initialization Vector",
                    BaseEncoding.base16().encode(new byte[] {0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7}), Field.Type.Text
            );
            cwAct = this.execCustomAction(
                    derClient, payPop, new CustomWebAction(frm, "Software Protected Encrypt Data")
            );
            execResultForm = cwAct.getForm();

            // TODO Decrypt
            // TODO HMAC
        }
    }

    @Test
    public void testGenerateKeyBDK() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastZmk)) {
            // We need a ZMK!
            this.testGenerateHSMZoneMasterKeyRequest();
        }
        TestCase.assertNotNull("Expected 'ZMK'!!!", this.lastZmk);

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
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGenerateKeyBDK)");
                return;
            }

            String keyReqType = "DUKPT Base Derivation Key";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(keyReqType));

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

            String keyUsage = "DUKPT Base Derivation Key PIN";//DUKPT Base Derivation Key (BDK-1)
            FluidItem generateKeyRequest = generateHsmKeyRequestItem(
                    "AliasGenBDK",
                    UUID.randomUUID().toString(),
                    this.lastHostAlias,
                    keyUsage
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
            Form genBDKReqForm = keyGenReqById.getForm();
            String keyAlias = genBDKReqForm.getFieldValueAsString("Alias");

            // Approve the Key Gen Request (Gen the BDK):
            createdIdsGenHsmKeyReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            // Wait for the BDK to be created:
            sleepForSeconds(3);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, keyReqType);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No BDK on alias "+keyAlias+"!",1, keysWithAlias.size());

            FluidItem bdkItm = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(bdkItm);
            Form bdkForm = bdkItm.getForm();
            TestCase.assertNotNull(bdkForm);

            // HSM Invoked:
            TestCase.assertNotNull(bdkForm.getFieldValueAsString("Key Check Value"));
            TestCase.assertNotNull(bdkForm.getFieldValueAsString("HSM Key Block Cryptogram LMK MFK"));
            TestCase.assertEquals(this.lastHostAlias, bdkForm.getFieldValueAsString("Key Generation Host"));
            TestCase.assertEquals(this.lastKeystoreOrg, bdkForm.getFieldValueAsString("Organisation"));
            // Data Key has usage:
            TestCase.assertEquals(keyUsage, bdkForm.getFieldValueAsString("HSM Key Type or Usage"));

            // Once approved, we have the request linked to the BDK:
            List<Form> descGenKeyReq = sqlUtl.getDescendants(
                    genBDKReqForm,
                    true,
                    true,
                    true
            );
            TestCase.assertNotNull(descGenKeyReq);
            TestCase.assertEquals("Expected one descendant. The Generated BDK!",1, descGenKeyReq.size());
            Form bdkFormFinal = descGenKeyReq.get(0);
            TestCase.assertTrue(
                    "Expected BDK to be active.",
                    bdkFormFinal.getFieldValueAsBoolean("Is Active")
            );
            this.lastBdk = bdkFormFinal.getFieldValueAsString("Alias");
        }
    }

    @Test
    public void testGenerateAndImportPGP() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastDek)) {
            // We need an HSM Data Key!
            this.testGenerateKeyDEK();
        }
        TestCase.assertNotNull("Expected 'Data Key (DEK)'!!!", this.lastDek);

        String flowNameProv = "Cryptographic Key Provisioning";
        int itemCount = 1, threadCount = 1;
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testGeneratePGP)");
                return;
            }

            String genPgpReq = "Generate PGP Keypair Request",
                    pgpKeypair = "PGP Keypair",
                    pgpSubkey = "PGP Subkey";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(genPgpReq));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(pgpKeypair));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(pgpSubkey));

            JobView viewCheckerGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameProv,
                    "Key Provisioning Checker",
                    "Key Provisioning Checker"
            );
            JobView viewProcResultGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameProv,
                    "Key Provisioning Processed Result",
                    "Key Provisioning Processed Result"
            );

            // Refresh payload populate for the HSM key req:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();

            String identifier = UUID.randomUUID().toString();
            Form frm = new Form(genPgpReq, new Date().toString()+ " "+identifier);
            String aliasToCreateForPgp = String.format("GenPGPKeypair-%s", identifier);
            frm.setFieldValue("Alias", aliasToCreateForPgp, Field.Type.Text);
            frm.setFieldValue("Organisation", new MultiChoice(this.lastKeystoreOrg), Field.Type.MultipleChoice);
            frm.setFieldValue("PGP Algorithm", new MultiChoice("RSA"), Field.Type.MultipleChoice);
            frm.setFieldValue("PGP Name", "Roger Waters", Field.Type.Text);
            frm.setFieldValue("PGP Email", "roger@floyd.org", Field.Type.Text);
            frm.setFieldValue("Data Encryption Key", new MultiChoice(this.lastDek), Field.Type.MultipleChoice);
            frm.setFieldValue("Publish Public Key", Boolean.TRUE, Field.Type.TrueFalse);

            FluidItem genPgpKeyReq = new FluidItem(frm);
            List<Long> createdIdsGenPgpReq = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameProv, viewCheckerGenKey,
                    () -> genPgpKeyReq
            );
            TestCase.assertNotNull(createdIdsGenPgpReq);
            TestCase.assertEquals(itemCount, createdIdsGenPgpReq.size());

            FluidItem keyGenReqById = this.fluidItemByFormId(
                    derClient, createdIdsGenPgpReq.get(0),
                    true// Include route fields.
            );

            Form genPgpReqForm = keyGenReqById.getForm();
            String keyAlias = genPgpReqForm.getFieldValueAsString("Alias");

            // Approve the Gen Soft Key Request (Gen the SDEK):
            createdIdsGenPgpReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            // Wait for the PGP to be created:
            sleepForSeconds(5);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, "PGP Keypair");
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No PGP on alias "+keyAlias+"!",1, keysWithAlias.size());

            FluidItem pgpItm = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(pgpItm);
            Form pgpForm = pgpItm.getForm();
            TestCase.assertNotNull(pgpForm);
            TestCase.assertEquals("PGP Keypair", pgpForm.getFormType());
            TestCase.assertEquals("Open", pgpForm.getState());
            TestCase.assertEquals("NotInFlow", pgpForm.getFlowState());
            TestCase.assertNull(pgpForm.getCurrentUser());
            String pgpAlias = pgpForm.getFieldValueAsString("Alias");
            TestCase.assertEquals(aliasToCreateForPgp, pgpAlias);
            this.lastPgpKeypair = pgpAlias;

            TestCase.assertTrue(pgpForm.getFieldValueAsBoolean("Is Active"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("Alias").startsWith("GenPGPKeypair-"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("Organisation").startsWith("OrgName-"));
            TestCase.assertEquals("RSA", pgpForm.getFieldValueAsString("PGP Algorithm"));
            TestCase.assertEquals("Roger Waters", pgpForm.getFieldValueAsString("PGP Name"));
            TestCase.assertEquals("roger@floyd.org", pgpForm.getFieldValueAsString("PGP Email"));
            // PGP Key Created:
            TestCase.assertEquals("Roger Waters <roger@floyd.org>", pgpForm.getFieldValueAsString("PGP User ID"));
            TestCase.assertNotNull(pgpForm.getFieldValueAsString("PGP Key ID"));
            TestCase.assertNotNull(pgpForm.getFieldValueAsString("PGP Fingerprint"));
            TestCase.assertEquals(4096, pgpForm.getFieldValueAsInt("PGP Bit Strength").intValue());
            this.receiverPgpPublicKey = pgpForm.getFieldValueAsString("PGP Public Key Armored");
            TestCase.assertTrue(this.receiverPgpPublicKey.startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("PGP Secret Key Armored").startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----"));

            List<Form> tableRecords = this.tableRecords(derClient, pgpItm.getForm(), null);
            TestCase.assertNotNull(tableRecords);
            TestCase.assertEquals(4, tableRecords.size());

            tableRecords.forEach(subItm -> {
                TestCase.assertNotNull(subItm.getTitle());
                TestCase.assertEquals(pgpSubkey, subItm.getFormType());

                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Key ID"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Fingerprint"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Master Key"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Signing Key"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Encryption Key"));
                // TODO @kb, the multi choice is empty (not set / empty string <"">)
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Key Type"));
            });
        }
    }

    @Test
    public void testImportAndTrustPGPPublicKey() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastDek)) {
            // We need an HSM Data Key!
            this.testGenerateKeyDEK();
        }
        TestCase.assertNotNull("Expected 'Data Key (DEK)'!!!", this.lastDek);

        String flowNameProv = "Cryptographic Key Provisioning";
        int itemCount = 1, threadCount = 1;
        try (WebSocketASNDERClient derClient = new WebSocketASNDERClient(
                BASE_URL,
                ADMIN_SERVICE_TICKET_HEX,
                TimeUnit.SECONDS.toMillis(60));
             FlowStepClient flowStepClient = new FlowStepClient(BASE_URL, ADMIN_SERVICE_TICKET);
             GlobalFieldClient gfc = new GlobalFieldClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserClient uc = new UserClient(BASE_URL, ADMIN_SERVICE_TICKET);
             UserQueryClient uqc = new UserQueryClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET);
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testImportAndTrustPGPPublicKey)");
                return;
            }

            String importPgpKeyReq = "PGP Public Key Import Request",
                    pgpTrustPub = "PGP Trusted Public Key",
                    pgpSubkey = "PGP Subkey";
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(importPgpKeyReq));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(pgpTrustPub));
            this.formDefsToCleanup.add(fdc.getFormDefinitionByName(pgpSubkey));

            JobView viewCheckerGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameProv,
                    "Key Provisioning Checker",
                    "Key Provisioning Checker"
            );
            JobView viewProcResultGenKey = flowStepClient.getStandardJobViewBy(
                    flowNameProv,
                    "Key Provisioning Processed Result",
                    "Key Provisioning Processed Result"
            );

            // Refresh payload populate for the HSM key req:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();

            String identifier = UUID.randomUUID().toString();
            Form frm = new Form(importPgpKeyReq, new Date().toString()+ " "+identifier);
            String aliasToCreateForPgp = String.format("ImportPGPPubKey-%s", identifier);
            frm.setFieldValue("Alias", aliasToCreateForPgp, Field.Type.Text);
            frm.setFieldValue("Organisation", new MultiChoice(this.lastKeystoreOrg), Field.Type.MultipleChoice);

            this.senderPasswordForPgpKP = null;

            //TODO Need to test with a password in the test case:::
            //TODO this.senderPasswordForPgpKP = "zool".toCharArray();
            String pgpPubArmored = null;
            try {
                this.senderPgpKey = PGPUtil.generateKeyPair(
                        PGPUtil.KeyType.RSA,
                        "Peter Pan <peter.pan@neverland.com>",
                        this.senderPasswordForPgpKP
                );
                TestCase.assertNotNull("No keypair generated!", this.senderPgpKey);
                TestCase.assertNotNull("No public key!", this.senderPgpKey.getPublicKeyRing());
                pgpPubArmored = PGPUtil.armor(this.senderPgpKey.getPublicKeyRing());
            } catch (PGPException e) {
                TestCase.fail("PGP-Err: Failed to generate PGP Keypair: "+e.getMessage());
            } catch (GeneralSecurityException e) {
                TestCase.fail("GenSec-Err: Failed to generate PGP Keypair: "+e.getMessage());
            } catch (IOException e) {
                TestCase.fail("IO-Err: Failed to generate PGP Keypair: "+e.getMessage());
            }
            frm.setFieldValue("PGP Public Key Armored", pgpPubArmored, Field.Type.ParagraphText);

            FluidItem genPgpKeyReq = new FluidItem(frm);
            List<Long> createdIdsImportPgpReq = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameProv, viewCheckerGenKey,
                    () -> genPgpKeyReq
            );
            TestCase.assertNotNull(createdIdsImportPgpReq);
            TestCase.assertEquals(itemCount, createdIdsImportPgpReq.size());

            FluidItem keyImportReqById = this.fluidItemByFormId(
                    derClient, createdIdsImportPgpReq.get(0),
                    true// Include route fields.
            );

            Form importPgpReqForm = keyImportReqById.getForm();
            String keyAlias = importPgpReqForm.getFieldValueAsString("Alias");
            TestCase.assertNotNull(importPgpReqForm);
            TestCase.assertEquals(importPgpKeyReq, importPgpReqForm.getFormType());
            TestCase.assertEquals("Open", importPgpReqForm.getState());
            TestCase.assertEquals("WorkInProgress", importPgpReqForm.getFlowState());

            TestCase.assertNotNull(keyAlias);
            TestCase.assertNotNull(importPgpReqForm.getFieldValueAsString("User Maker"));
            TestCase.assertEquals(this.lastKeystoreOrg, importPgpReqForm.getFieldValueAsString("Organisation"));
            TestCase.assertEquals("RSA", importPgpReqForm.getFieldValueAsString("PGP Algorithm"));
            TestCase.assertNotNull(importPgpReqForm.getFieldValueAsString("PGP Key ID"));
            TestCase.assertNotNull(importPgpReqForm.getFieldValueAsString("PGP Public Key Armored"));
            TableField pubReqPubFields = importPgpReqForm.getFieldValueAsTableField("PGP Subkeys");
            TestCase.assertNotNull(pubReqPubFields);
            TestCase.assertEquals("Expected 2 Pub Keys from keyring.", 2, pubReqPubFields.getTableRecords().size());

            // Approve the PGP Public Key:
            createdIdsImportPgpReq.forEach(id -> {
                this.approveFormId(derClient, uc, fcc, id, viewCheckerGenKey, viewProcResultGenKey);
            });

            // Wait for the PGP to be created:
            sleepForSeconds(6);

            List<FluidItem> keysWithAlias = formsByAliasAndType(uqc, keyAlias, pgpTrustPub);
            TestCase.assertNotNull(keysWithAlias);
            TestCase.assertEquals("No PGP Public Key on alias "+keyAlias+"!",1, keysWithAlias.size());

            FluidItem pgpItm = this.fluidItemByFormId(
                    derClient, keysWithAlias.get(0).getForm().getId(),
                    false// No route fields.
            );
            TestCase.assertNotNull(pgpItm);
            Form pgpForm = pgpItm.getForm();
            TestCase.assertNotNull(pgpForm);
            TestCase.assertEquals(pgpTrustPub, pgpForm.getFormType());
            TestCase.assertEquals("Open", pgpForm.getState());
            TestCase.assertEquals("NotInFlow", pgpForm.getFlowState());
            TestCase.assertNull(pgpForm.getCurrentUser());
            String pgpAlias = pgpForm.getFieldValueAsString("Alias");
            TestCase.assertEquals(aliasToCreateForPgp, pgpAlias);
            this.lastPgpPublicKey = pgpAlias;

            TestCase.assertTrue(pgpForm.getFieldValueAsBoolean("Is Active"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("Alias").startsWith("ImportPGPPubKey-"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("Organisation").startsWith("OrgName-"));
            TestCase.assertEquals("RSA", pgpForm.getFieldValueAsString("PGP Algorithm"));
            TestCase.assertEquals("Peter Pan", pgpForm.getFieldValueAsString("PGP Name"));
            TestCase.assertEquals("peter.pan@neverland.com", pgpForm.getFieldValueAsString("PGP Email"));
            // PGP Key Created:
            TestCase.assertEquals("Peter Pan <peter.pan@neverland.com>", pgpForm.getFieldValueAsString("PGP User ID"));
            TestCase.assertNotNull(pgpForm.getFieldValueAsString("PGP Key ID"));
            TestCase.assertTrue(pgpForm.getFieldValueAsString("PGP Public Key Armored").startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----"));

            List<Form> tableRecords = this.tableRecords(derClient, pgpForm, null);
            TestCase.assertNotNull(tableRecords);
            TestCase.assertEquals(2, tableRecords.size());

            tableRecords.forEach(subItm -> {
                TestCase.assertNotNull(subItm.getTitle());
                TestCase.assertEquals(pgpSubkey, subItm.getFormType());

                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Key ID"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Fingerprint"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Master Key"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Signing Key"));
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Is Encryption Key"));
                // TODO @kb, the multi choice is empty (not set / empty string <"">)
                TestCase.assertNotNull(subItm.getFieldValueAsString("PGP Key Type"));
            });
        }
    }

    @Test
    public void testImportTerminalMasterKeyUnderZoneMasterKeyUsingPGP() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastZmk)) {
            // 1. We need a BDK
            this.testGenerateHSMZoneMasterKeyRequest();
        }
        if (UtilGlobal.isBlank(this.lastPgpKeypair)) {
            // 2. We need a PGP Keypair
            this.testGenerateAndImportPGP();
        }
        if (UtilGlobal.isBlank(this.lastPgpPublicKey)) {
            // 3. We need a PGP Public Key
            this.testImportAndTrustPGPPublicKey();
        }

        TestCase.assertNotNull("Expected 'ZMK'!!!", this.lastZmk);
        TestCase.assertNotNull("Expected 'PGP Keypair'!!!", this.lastPgpKeypair);
        TestCase.assertNotNull("Expected 'PGP Public Key'!!!", this.lastPgpPublicKey);

        int itemCount = 1, threadCount = 1;
        String flowNameContIngest = "Content Ingestion";
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
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testImportTerminalMasterKeyUnderZoneMasterKeyUsingPGP)");
                return;
            }

            PGPPublicKeyRing pub = PGPUtil.publicKeyRingFromArmor(this.receiverPgpPublicKey);
            String receiverUserId = pub.getPublicKey().getUserIDs().next();
            TestCase.assertEquals("Roger Waters <roger@floyd.org>", receiverUserId);

            String senderUserId = this.senderPgpKey.getPublicKeyRing().getPublicKey().getUserIDs().next();
            TestCase.assertEquals("Peter Pan <peter.pan@neverland.com>", senderUserId);

            byte[] rawDataToEncAndSign = this.generateCSVTMK();
            byte[] encryptedAndSigned = PGPUtil.encryptAndSign(
                    rawDataToEncAndSign,
                    pub,
                    this.senderPgpKey.getOpenPGPKey(),
                    this.senderPasswordForPgpKP
            );
            String asciiArmored = PGPUtil.armorMessage(encryptedAndSigned);

            String emailType = "Email";
            //TODO this.formDefsToCleanup.add(fdc.getFormDefinitionByName(keyReqType));
            JobView viewProcResultContIngest = flowStepClient.getStandardJobViewBy(
                    flowNameContIngest,
                    "Content Ingestion Processed Result",
                    "Content Ingestion Processed Result"
            );

            // Refresh payload populate for the HSM key req:
            PayloadPopulate payPop = derClient.requestFullPayloadPopulate();

            String identifier = UUID.randomUUID().toString();
            Form frmEmail = new Form(emailType, new Date().toString()+ " "+identifier);
            frmEmail.setFieldValue("Email From Address", "test@example.com", Field.Type.Text);
            frmEmail.setFieldValue("Email To Address", "recipient@example.com", Field.Type.Text);
            frmEmail.setFieldValue("Email Subject", "Test Email Subject", Field.Type.Text);
            frmEmail.setFieldValue("Email Sent Date", new Date(), Field.Type.DateTime);
            frmEmail.setFieldValue("Email Received Date", new Date(), Field.Type.DateTime);
            frmEmail.setFieldValue("Email Unique Identifier", Math.random(), Field.Type.Decimal);

            FluidItem tmksEmail = new FluidItem(frmEmail);
            tmksEmail.setAttachments(UtilGlobal.toListSafe(
                    new Attachment(rawDataToEncAndSign, "tmk_content.csv", "text/csv"),
                    new Attachment(asciiArmored.getBytes(), "tmk_content.csv.enc", "text/csv")
            ));
            List<Long> createdIdsTmkImport = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameContIngest, viewProcResultContIngest,
                    () -> tmksEmail
            );
            TestCase.assertNotNull(createdIdsTmkImport);
            TestCase.assertEquals(itemCount, createdIdsTmkImport.size());

            FluidItem tmkImportReqById = this.fluidItemByFormId(
                    derClient, createdIdsTmkImport.get(0),
                    true// Include route fields.
            );
            Form importTmkForm = tmkImportReqById.getForm();

            // HSM Invoked:
            TestCase.assertEquals("Email", importTmkForm.getFormType());
            //TODO Need to approve from the KEY PROVISIONING flow... (We expect the TMK Request would be created)
        } catch (IOException | PGPException err) {
            err.printStackTrace();
            TestCase.fail("IO-Err: "+err.getMessage());
        }
    }

    private byte[] generateCSVTMK() {
        String content =
                "'Serial No', 'TMK Under ZMK', 'KCV'\n" +
                "'SN123'    , 'KB123'        , 'AABBCC'\n"+
                "'SN456'    , 'KB456'        , '112233'";
        return content.getBytes();
    }

    //TODO 1. import TMK

    //TODO 2.1. Gen BDK-1
    //TODO 2.2. Gen BDK-2
            /*
             Alias
             KCV
             Key Set Identifier (KSI): [303950] (6)
             */

    //TODO 2.3. Gen IPEK (SRED/PIN)

    //TODO 3. Gen PGP
    //TODO 4. Add PGP for Organization

    //TODO 5. TMK Import

    /*

.
A Key Set Identifier and Device ID for deriving the Initial Key. Right justified and
padded with 'F's. (NOTE: there is no Transaction Counter, as there would be with
a KSN sent in transaction data from a terminal.)
For a 3DES BDK except BDK5, the KSN is 15 H.
Example: For a KSI + DID of 303950 + 12342468 (14 hex characters), the KSN field would be "F30395012342468".
The last hex character (8) must be even.

For an AES BDK and a 3DES BDK5, the KSN is 16 H.
AES Example: For BDK ID + DID of 30395059 + 12345678 (16 hex characters),
the KSN field would be 3039505912345678. There is no need for the last
character to be even.
     */

    /*
    |---> POS Terminal:
           - Serial No
           - Device ID (DID): 12342468              (Later during RKI)
           - Manufacturer
           - Model
                ------------------
                |---> IPEK/IKEY                          (Later during RKI)
                        - Key under TMK
                        - Key under LMK
                        - TMK KCV (if more, we know which one imported it)
                        - KCV
                        - IPEK Purpose (PIN / SRED)
                        - BDK Alias (we will get the KSI (Key Set Identifier/KSI) from this)
                        - Device ID/DID
                        - The KSN would be KSI+DID with F left-padded: F30395012342468
                ------------------
                |---> TMK
                        - Key under ZMK (TR-31/TR-34)
                        - ZMK Alias (set on CSV import/load)
                        - Key under LMK
                        - KCV (same for both)
        Actions |====>>>
                -> Translate PIN -> (Manufacturer/Model/ Serial/PAN/PinBlock/PinBlockFormat)
                -> DUKPT Decrypt -> (Manufacturer/Model/ Serial+Counter)
                -> DUKPT Encrypt -> (Manufacturer/Model/ Serial+Counter)
     */

    @Test
    public void testImportKeyTMKs() {
        if (this.isConnectionInValid) return;

        if (UtilGlobal.isBlank(this.lastZmk)) {
            // We need a ZMK!
            this.testGenerateHSMZoneMasterKeyRequest();
        }
        TestCase.assertNotNull("Expected 'ZMK'!!!", this.lastZmk);

        int itemCount = 1, threadCount = 1;
        String flowNameImportTmks = "Import TMK";
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
             FormDefinitionClient fdc = new FormDefinitionClient(BASE_URL, ADMIN_SERVICE_TICKET);
        ) {
            if (!isPericardEnabled(gfc)) {
                log.warning("Pericard is not enabled. Skipping test. (testImportKeyTMKs)");
                return;
            }


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
        frmHost.setFieldValue("Organisation", new MultiChoice(existingOrg), Field.Type.MultipleChoice);
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

        this.unLockFormContainer(derClient, createdHostForm, false, true, null);

        return createdHostForm;
    }

    private static FluidItem generateHsmKeyRequestItem(
            String identifierPrefix,
            String identifier,
            String keyGenerationHost,
            String keyUsage
    ) {
        Form frm = new Form("Generate HSM Key Request", new Date().toString()+ " "+identifier);
        frm.setFieldValue("Alias", String.format("%s-%s", identifierPrefix, identifier), Field.Type.Text);
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
