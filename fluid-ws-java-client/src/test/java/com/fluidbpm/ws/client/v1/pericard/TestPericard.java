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

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.fluidbpm.ws.client.v1.asn1der.ws.ABaseTestASNDER;
import com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient;
import com.fluidbpm.ws.client.v1.crypto.KeystoreTestUtil;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.user.UserClient;
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

import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 *
 */
@Log
public class TestPericard extends ABaseTestASNDER {

    @Override
    @Before
    public void init() {
        super.init();
    }

    //@Ignore
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
             FormContainerClient fcc = new FormContainerClient(BASE_URL, ADMIN_SERVICE_TICKET)
        ) {
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
                approveFormId(derClient, uc, fcc, id, viewCheckerOrg, viewProcResultOrg);
            });

            // 2. REQUEST KEYSTORE:
            String ksType = "JKS", ksPass = "testpass", ksKeyPass = "testkey";
            byte[] keystoreBytes;
            try {
                keystoreBytes = KeystoreTestUtil.testKeystore(
                        ksType, ksKeyPass.toCharArray(), ksPass.toCharArray()
                );
            } catch (Exception e) {
                log.severe("Failed to create keystore: "+e.getMessage());
                throw new RuntimeException(e);
            }
            log.info("Keystore: "+keystoreBytes.length+" bytes.");

            // Refresh payload populate for the org:
            payPop = derClient.requestFullPayloadPopulate();

            String newOrgName = flItmOrgOnReq.getForm().getFieldValueAsString("Entity Name");
            List<String> orgsPP = payPop.getAvailableMultiChoicesForm("Organisation");

            TestCase.assertTrue("New Org '"+newOrgName+"' not found in PayloadPopulate! Only have("+
                    orgsPP.size()+", before "+orgsPPBefore+"): "+orgsPP, orgsPP.contains(newOrgName));

            FluidItem flItmKSLoadReq = ksLoadItem(
                    newOrgName,
                    ksType,
                    PROVIDER_NAME,
                    ksPass,
                    ksKeyPass,
                    UUID.randomUUID().toString(),
                    keystoreBytes
            );
            List<Long> createdFormIdsKS = this.submitCycle(
                    payPop, itemCount, threadCount, flowNameKeystore, viewCheckerKS,
                    () -> flItmKSLoadReq
            );
            TestCase.assertNotNull(createdFormIdsKS);
            TestCase.assertEquals(itemCount, createdFormIdsKS.size());

            // Approve the Request:
            createdFormIdsKS.forEach(id -> {
                approveFormId(derClient, uc, fcc, id, viewCheckerKS, viewProcResultKS);
            });
        }
        PerfStats.printOutcomes();
    }

    private void approveFormId(
            WebSocketASNDERClient derClient,
            UserClient uc,
            FormContainerClient fcClient,
            Long id,
            JobView viewChecker,
            JobView viewProcResult
    ) {
        FluidItem byId = this.fluidItemByFormId(derClient, id);
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

    @Override
    @After
    public void destroy() {
        log.info("Pericard: Destroying test and cleaning up...");
        super.destroy();
    }
}
