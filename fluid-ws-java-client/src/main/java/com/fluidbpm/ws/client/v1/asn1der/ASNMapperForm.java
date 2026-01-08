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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import org.bouncycastle.asn1.*;

import java.io.IOException;

public class ASNMapperForm extends ASNBaseMapper<Form> {
    public static class Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;

        public static final int FORM_TYPE = START++;
        public static final int FORM_TYPE_ID = START++;
    }

    public Form decode(byte[] der) {
        ASN1Sequence seq = this.initSeq(der);
        User returnVal = new User();
        returnVal.setId(asLong(seq.getObjectAt(ASNMapperForm.Map.ID), ABaseFluidGSONObject.JSONMapping.ID));
        seq.getObjectAt(ASNMapperForm.Map.USERNAME);

        for (int i = 1; i < seq.size(); i++) {
            ASN1Encodable e = seq.getObjectAt(i);
            ASN1TaggedObject t = ASN1TaggedObject.getInstance(e);
            int tagNo = t.getTagNo();
            ASN1Primitive prim = t.getLoadedObject();

            switch (tagNo) {
                case Map.USERNAME:
                    returnVal.setUsername(asUtf8(prim, User.JSONMapping.USERNAME));
                break;
                default:break;
            }
        }

        return returnVal;
    }

    public byte[] encode(Form item) {

        try {
            return seq(item).getEncoded(ASN1Encoding.DER);
        } catch (IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr, FluidClientException.ErrorCode.ASN_1_ERROR);
        }
    }

    public static ASN1EncodableVector vector(Form item) {
        ASN1EncodableVector vect = initVector(item);

        if (item.getFormType() != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_TYPE, new DERUTF8String(item.getFormType())));
        }

        if (item.getFormType() != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_TYPE, new DERUTF8String(item.getFormType())));
        }

        vect.add(new DERUTF8String(item.getTitle() == null ? -1 : item.getId()));

        vect.add(new DERSequence(vect));

        return ;
    }

}
