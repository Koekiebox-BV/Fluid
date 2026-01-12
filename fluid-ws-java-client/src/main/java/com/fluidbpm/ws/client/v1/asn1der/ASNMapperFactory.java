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
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import org.bouncycastle.asn1.ASN1Sequence;

/**
 *
 */
public class ASNMapperFactory {
    private ASNMapperBaseTransmission baseTransmission;
    public ASNMapperFactory(int type) {
        super();
        this.baseTransmission = new ASNMapperBaseTransmission(type);
    }

    public ABaseFluidVO readObjectFromReceived(ASN1Sequence baseTrans) {
        BaseTransmission bt = this.baseTransmission.decode(baseTrans);
        return bt.getTransmissionObject();
    }

    public byte[] writeObjectForSend(ABaseFluidVO objVo) {

        return null;
    }
}
