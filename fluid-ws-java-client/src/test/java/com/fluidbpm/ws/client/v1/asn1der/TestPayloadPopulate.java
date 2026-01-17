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

        String ffValNe = pop.getMetaDataValue("not-exist");
        Assert.assertNull(ffValNe);

        String ffValCool = pop.getMetaDataValue("cool-field");
        Assert.assertNotNull(ffValCool);

        
    }

    private PayloadPopulate testPayloadPopulate() {

        List<ASNMultiChoice> mcList = new ArrayList<>();
        mcList.add(new ASNMultiChoice(123L, "mc-opt-1"));
        mcList.add(new ASNMultiChoice(456L, "mc-opt-2"));

        List<ASNMultiChoiceField> mcForm = new ArrayList<>();
        mcForm.add(new ASNMultiChoiceField("ff-mc", mcList));
        List<ASNMultiChoiceField> mcUser = new ArrayList<>();
        mcUser.add(new ASNMultiChoiceField("uf-mc", mcList));
        List<ASNMultiChoiceField> mcRoute = new ArrayList<>();
        mcRoute.add(new ASNMultiChoiceField("rf-mc", mcList));
        List<ASNMultiChoiceField> mcGlobal = new ArrayList<>();
        mcGlobal.add(new ASNMultiChoiceField("gf-mc", mcList));
        List<FormFieldMetaData> ffmd = new ArrayList<>();
        ffmd.add(new FormFieldMetaData("cool-field", "Meta-Deee[:]"));
        ffmd.add(new FormFieldMetaData("cool-field2", "Meta-Deee[:]"));
        PayloadPopulate pop = new PayloadPopulate(mcForm,mcUser, mcRoute, mcGlobal, ffmd);

        return pop;
    }
}
