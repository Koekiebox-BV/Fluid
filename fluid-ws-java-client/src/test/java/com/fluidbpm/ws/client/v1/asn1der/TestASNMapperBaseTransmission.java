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

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestParameter;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.*;
import org.bouncycastle.asn1.DERSequence;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 *
 */
public class TestASNMapperBaseTransmission extends ABaseTestCase {
    @Test
    public void testEncodeDecode() {
        BaseTransmission item = testBaseTransmission();
        PayloadPopulate pop = item.getPayloadPopulate();

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ANSGlobal.Type.FIELD);

        DERSequence seqEnc = mapper.encode(item);
        byte[] raw = seqBytes(seqEnc);
        Assert.assertEquals("Encoded size expected to be different!", 359, raw.length);
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());

        // Payload Populate:
        Assert.assertEquals("Decoded payload populate is not as expected.",
                pop.getMcFormField().get(0).getId(), decoded.getPayloadPopulate().getMcFormField().get(0).getId());
    }

    @Test
    public void testEncodeDecodeLightPerformance() {
        BaseTransmission item = testBaseTransmission();
        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ANSGlobal.Type.FIELD);
        long start = System.currentTimeMillis();
        int count = 1000;
        for (int i = 0;i < count;i++) {
            DERSequence seqEnc = mapper.encode(item);
            byte[] raw = seqBytes(seqEnc);
            BaseTransmission decoded = mapper.decode(raw);
            Assert.assertNotNull(decoded);
        }
        long took = System.currentTimeMillis() - start;

        System.out.println("Took '"+took+"' millis for encoding and decoding '"+count+"' items.");
        Assert.assertTrue("Took too long! Took "+took, took < 500);
    }

    private BaseTransmission testBaseTransmission(ABaseFluidVO to) {
        BaseTransmission item = new BaseTransmission(ANSGlobal.Type.FIELD);

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
        PayloadPopulate pop = new PayloadPopulate(mcForm,mcUser, mcRoute, mcGlobal, ffmd);

        List<RequestParameter> reqParams = new ArrayList<>();
        reqParams.add(new RequestParameter("param-name", "param-value"));
        reqParams.add(new RequestParameter("param-surname", "param-petec"));
        RequestObject ro = new RequestObject("me-path/is-this!", reqParams);

        item.setPayloadPopulate(pop);
        item.setRequestObject(ro);
        item.setTransmissionObject(to);

        return item;
    }

    private BaseTransmission testBaseTransmission() {
        Field to = new Field(765L);
        to.setFieldName("field name testing as transmission obj!");
        return this.testBaseTransmission(to);
    }
}
