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

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoice;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoiceField;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.FormFieldMetaData;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**/
public class TestPayloadPopulate extends ABaseTestCase {
    @Test
    public void testFetch() {
        PayloadPopulate pop = testPayloadPopulate();

        // Meta-data:
        String ffValNe = pop.getMetaDataValue("not-exist");
        Assert.assertNull(ffValNe);

        String ffValCool = pop.getMetaDataValue("cool-field");
        Assert.assertNotNull(ffValCool);

        // Multi Choice Form:
        long[] selectedFieldNotKnown = pop.getMultiChoiceFormValues("ff-mc-not-known", new ArrayList<>());
        Assert.assertEquals(0, selectedFieldNotKnown.length);

        List<String> selectedMCNotKnown = new ArrayList<>();
        selectedMCNotKnown.add("mc-opt-not-known");
        long[] selectedFieldMCNotKnown = pop.getMultiChoiceFormValues("ff-mc", selectedMCNotKnown);
        Assert.assertEquals(0, selectedFieldMCNotKnown.length);

        List<String> selectedMCForms = new ArrayList<>();
        selectedMCForms.add("ff-mc-opt-1");
        selectedMCForms.add("ff-mc-opt-3");
        long[] selectedLongs = pop.getMultiChoiceFormValues("ff-mc", selectedMCForms);
        Assert.assertArrayEquals(new long[]{123L, 789L}, selectedLongs);
    }

    private PayloadPopulate testPayloadPopulate() {

        List<ASNMultiChoice> mcListForm = new ArrayList<>();
        mcListForm.add(new ASNMultiChoice(123L, "ff-mc-opt-1"));
        mcListForm.add(new ASNMultiChoice(456L, "ff-mc-opt-2"));
        mcListForm.add(new ASNMultiChoice(789L, "ff-mc-opt-3"));

        List<ASNMultiChoiceField> mcForm = new ArrayList<>();
        mcForm.add(new ASNMultiChoiceField("ff-mc", mcListForm));
        List<ASNMultiChoiceField> mcUser = new ArrayList<>();
        mcUser.add(new ASNMultiChoiceField("uf-mc", mcListForm));
        List<ASNMultiChoiceField> mcRoute = new ArrayList<>();
        mcRoute.add(new ASNMultiChoiceField("rf-mc", mcListForm));
        List<ASNMultiChoiceField> mcGlobal = new ArrayList<>();
        mcGlobal.add(new ASNMultiChoiceField("gf-mc", mcListForm));
        List<FormFieldMetaData> ffmd = new ArrayList<>();
        ffmd.add(new FormFieldMetaData("cool-field", "Meta-Deee[:]"));
        ffmd.add(new FormFieldMetaData("cool-field2", "Meta-Deee[:]"));
        PayloadPopulate pop = new PayloadPopulate(mcForm,mcUser, mcRoute, mcGlobal, ffmd);

        return pop;
    }
}
