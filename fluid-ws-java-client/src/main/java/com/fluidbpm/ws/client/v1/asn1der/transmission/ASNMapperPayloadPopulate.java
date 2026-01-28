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

import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;

import java.util.Enumeration;
import java.util.Map;

/**
 *
 */
@Deprecated
public class ASNMapperPayloadPopulate {
    public ASNMapperPayloadPopulate() {
        super();
    }

    public PayloadPopulate payloadPopulate(ASN1Sequence seq) {
        PayloadPopulate payloadPopulate = null;//new PayloadPopulate();

        Enumeration<ASN1Object> enumeration = seq.getObjects();
        while (enumeration.hasMoreElements()) {
            ASN1Object obj = enumeration.nextElement();
            if (obj instanceof ASN1TaggedObject) {
                ASN1TaggedObject taggedObject = (ASN1TaggedObject) obj;
                if (taggedObject.getTagNo() == ASNGlobal.Tag.TAG_PAYLOAD_POPULATE) {
                    this.populate(payloadPopulate, ASN1TaggedObject.getInstance(taggedObject));
                    break;
                }
            }
        }
        return payloadPopulate;
    }


    private void populate(
            PayloadPopulate payloadPopulate,
            ASN1TaggedObject tagged
    ) {

        //TODO need to set the properties on payloadPopulate.
    }

    public void writePayloadPopulateAtEnd(
            PayloadPopulate payloadPopulate,
            ASN1Sequence seq,
            Map<Integer, String> availableChoicesForm,
            Map<Integer, String> availableChoicesRoute,
            Map<Integer, String> availableChoicesUser,
            Map<Integer, String> availableChoicesGlobal,
            Map<String, String> fieldMetaData
    ) {
        //TODO need to add the optional tagged sequence for PayloadPopulate at the end of the sequence.


    }
}
