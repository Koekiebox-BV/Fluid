/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.asn1der.transmission;

import com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper;
import com.fluidbpm.ws.client.v1.asn1der.ASNBaseTaggedMapper;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoiceField;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.FormFieldMetaData;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission.Map.PAYLOAD_POPULATE;

/**
 * A mapper class that handles encoding and decoding of {@code BaseTransmission} objects
 * to and from ASN.1 DER-encoded data. This class extends {@code ASNBaseMapper}
 * and provides custom logic for processing ASN.1 sequences for
 * {@code BaseTransmission} objects.
 */
public class ASNMapperBaseTransmission extends ASNBaseTaggedMapper<BaseTransmission> {
    public ASNMapperBaseTransmission() {
        super(InitType.ALL);
    }

    public static class Map extends ASNBaseMapper.Map {
        public static final int PAYLOAD_POPULATE = 5;
    }

    @Override
    protected Supplier<BaseTransmission> supplierForInstance() {
        return BaseTransmission::new;
    }

    @Override
    protected void encodeTaggedObject(BaseTransmission item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getTransmissionObject() != null) {
            //TODO
        }

        if (item.getRequestObject() != null) {
            //TODO
        }

        if (item.getPayloadPopulate() != null) {
            DERSequence derSeq = new DERSequence();
            PayloadPopulate payPop = item.getPayloadPopulate();

            //TODO Need to complete...

            vect.add(new DERTaggedObject(true, PAYLOAD_POPULATE, derSeq));
        }
    }

    @Override
    protected Void mapDecodedTaggedObject(TagObj<BaseTransmission> tag) {
        assert tag != null : "TagObj cannot be null.";

        BaseTransmission toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        List<ASNMultiChoiceField> mcFormField = new ArrayList<>();
        List<ASNMultiChoiceField> mcUserField = new ArrayList<>();
        List<ASNMultiChoiceField> mcRouteField = new ArrayList<>();
        List<ASNMultiChoiceField> mcGlobalField = new ArrayList<>();
        List<FormFieldMetaData> ffMetaData = new ArrayList<>();

        switch (tag.getTagNo()) {
            case PAYLOAD_POPULATE:
                ASN1Sequence seqPayPop = asSeq(obj, "Payload Populate");

                //TODO sdfddf
                /*toPop.setPayloadPopulate(
                        this.asnMapUser.decode(asSeq(obj, Form.JSONMapping.CURRENT_USER))
                );*/
                break;
        }

        PayloadPopulate payPop = new PayloadPopulate(
                mcFormField, mcUserField, mcRouteField, mcGlobalField, ffMetaData
        );
        toPop.setPayloadPopulate(payPop);

        return null;
    }
}
