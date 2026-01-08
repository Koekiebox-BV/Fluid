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

import com.fluidbpm.program.api.vo.item.FluidItem;
import org.bouncycastle.asn1.*;

/**
 * ASNMapperFluidItem is a mapper class responsible for encoding and decoding
 * instances of the {@code FluidItem} class into and from ASN.1 DER format.
 *
 * This class extends {@code ASNBaseMapper} specifically to provide mapping functionality
 * for the {@code FluidItem} object within the context of the Abstract Syntax Notation One (ASN.1)
 * representation.
 */
public class ASNMapperFluidItem extends ASNBaseMapper<FluidItem> {
    public static class Map extends ASNBaseMapper.Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;

        public static final int FORM = START++;
        public static final int STEP_ENTERED_TIME = START++;
    }

    /**
     * Encodes a {@code FluidItem} object into an ASN.1 DER sequence.
     * The method takes the relevant fields of the {@code FluidItem}, converts them
     * into appropriate ASN.1 structures, and includes them as tagged objects in the DER sequence.
     *
     * @param item the {@code FluidItem} object to be encoded. Fields such as form and
     *             stepEnteredTime are conditionally included if they are not {@code null}.
     * @return a {@code DERSequence} representation of the {@code FluidItem} object,
     *         containing its corresponding encoded ASN.1 components.
     */
    public DERSequence encode(FluidItem item) {
        ASN1EncodableVector vect = initVector(item);

        if (item.getForm() != null) {
            vect.add(new DERTaggedObject(true, Map.FORM, new ASNMapperForm().encode(item.getForm())));
        }

        if (item.getStepEnteredTime() != null) {
            vect.add(new DERTaggedObject(true, Map.STEP_ENTERED_TIME, new ASN1GeneralizedTime(item.getStepEnteredTime())));
        }
        return new DERSequence(vect);
    }

    /**
     * Decodes a byte array in ASN.1 DER format into a {@code FluidItem} object.
     * The method parses the DER-formatted byte array, extracts its components, and populates
     * a new {@code FluidItem} instance with the decoded data.
     *
     * @param der a byte array containing the DER-encoded ASN.1 data representing a {@code FluidItem}.
     * @return a {@code FluidItem} object populated with the data decoded from the provided DER byte array.
     */
    public FluidItem decode(byte[] der) {
        ASN1Sequence seq = initSeq(der);
        FluidItem itm = new FluidItem();
        this.popBaseFields(itm, seq);

        for (int i = Map.START; i < seq.size(); i++) {
            ASN1Encodable e = seq.getObjectAt(i);
            ASN1TaggedObject t = ASN1TaggedObject.getInstance(e);
            int tagNo = t.getTagNo();
            ASN1Object prim = t.getBaseObject();

            switch (tagNo) {
                //case FORM:

                    

                    //returnVal.setUsername(asUtf8(prim, User.JSONMapping.USERNAME));
                    //break;
                default:break;
            }
        }

        return itm;
    }
}
