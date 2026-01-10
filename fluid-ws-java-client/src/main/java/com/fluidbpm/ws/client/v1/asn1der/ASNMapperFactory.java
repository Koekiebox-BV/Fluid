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

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.vo.PayloadPopulate;
import org.bouncycastle.asn1.ASN1Sequence;

import static com.fluidbpm.ws.client.v1.asn1der.GlobalIDSpecial.Type.FLUID_ITEM;
import static com.fluidbpm.ws.client.v1.asn1der.GlobalIDSpecial.Type.FORM;

/**
 *
 */
public class ASNMapperFactory {
    private ASNMapperPayloadPopulate payloadPop = null;
    public ASNMapperFactory() {
        super();
        this.payloadPop = new ASNMapperPayloadPopulate();
    }

    public ABaseFluidVO readObject(ASN1Sequence seq, int type) {
        PayloadPopulate payloadPopulate = this.payloadPop.payloadPopulate(seq);

        // Mappers:
        ASNMapperUser asnMapUser = new ASNMapperUser();
        ASNMapperField asnMapField = new ASNMapperField(payloadPopulate);
        ASNMapperForm asnMapForm = new ASNMapperForm(asnMapUser, asnMapField, payloadPopulate);

        ASNBaseTaggedMapper mapper = null;
        switch (type) {
            case FLUID_ITEM: mapper = new ASNMapperFluidItem(asnMapForm);break;
            case FORM: mapper = asnMapForm;break;
            default:
                throw new FluidClientException("Invalid type code: " + type, FluidClientException.ErrorCode.ASN_1_ERROR);
        }
        assert mapper != null : "Mapper is null!";
        return mapper.decode(seq);
    }
}
