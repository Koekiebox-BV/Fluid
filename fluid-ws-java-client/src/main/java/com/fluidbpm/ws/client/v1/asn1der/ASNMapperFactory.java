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
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperPayloadPopulate;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.ASN1Sequence;

import static com.fluidbpm.ws.client.v1.asn1der.ANSGlobal.Type.*;

/**
 *
 */
public class ASNMapperFactory {
    private ASNMapperPayloadPopulate payloadPop = null;
    private final ASNMapperBaseTransmission baseTransmission;
    public ASNMapperFactory() {
        super();
        this.payloadPop = new ASNMapperPayloadPopulate();
        this.baseTransmission = new ASNMapperBaseTransmission();
    }

    public ABaseFluidVO readObjectFromReceived(ASN1Sequence baseTrans, int type) {
        BaseTransmission bt = this.baseTransmission.decode(baseTrans);
        PayloadPopulate payloadPopulate = bt.getPayloadPopulate(); //TODO this.payloadPop.payloadPopulate(seq);
        ASN1Sequence transmissionObject = bt.getTransmissionObject();

        // Mappers:
        ASNMapperUser asnMapUser = new ASNMapperUser();
        ASNMapperField asnMapField = new ASNMapperField(payloadPopulate);
        ASNMapperForm asnMapForm = new ASNMapperForm(asnMapUser, asnMapField, payloadPopulate);

        ASNBaseTaggedMapper mapper = null;
        switch (type) {
            case FLUID_ITEM: mapper = new ASNMapperFluidItem(asnMapForm);break;
            case FORM: mapper = asnMapForm;break;
            case FIELD: mapper = asnMapField;break;
            default:
                throw new FluidClientException("Invalid type code: " + type, FluidClientException.ErrorCode.ASN_1_ERROR);
        }
        assert mapper != null : "Mapper is null!";

        ABaseFluidVO returnVal = mapper.decode(transmissionObject);
        returnVal.setServiceTicket(bt.getServiceTicket());
        returnVal.setRequestUuid(bt.getRequestUuid());
        returnVal.setEcho(bt.getEcho());
        returnVal.setLoggedInUserFromTicket(bt.getLoggedInUserFromTicket());
        return returnVal;
    }

    public byte[] writeObjectForSend(ABaseFluidVO objVo) {

        return null;
    }
}
