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
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.user.User;
import org.bouncycastle.asn1.*;

public class ASNMapperFluidItem extends ASNBaseMapper<FluidItem> {
    public static class Map extends ASNBaseMapper.Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;

        public static final int FORM = START++;
        public static final int STEP_ENTERED_TIME = START++;
    }


    public FluidItem decode(byte[] der) {
        ASN1Sequence seq = initSeq(der);
        FluidItem itm = new FluidItem();
        this.popFirstFields(itm, seq);


        for (int i = Map.START; i < seq.size(); i++) {
            ASN1Encodable e = seq.getObjectAt(i);
            ASN1TaggedObject t = ASN1TaggedObject.getInstance(e);
            int tagNo = t.getTagNo();
            ASN1Primitive prim = t.getLoadedObject();

            switch (tagNo) {
                case Map.FORM:

                    

                    //returnVal.setUsername(asUtf8(prim, User.JSONMapping.USERNAME));
                    break;
                default:break;
            }
        }

        //ASN1GeneralizedTime

        

        return itm;
    }

    public byte[] encode(FluidItem item) {
        ASN1EncodableVector vector = this.initVec(item);

        


        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(id));
        

        v.add(ASN1Boolean.getInstance(success));
        v.add(new DERUTF8String(Objects.toString(message, "")));

        DERSequence seq = new DERSequence(v);
        return seq.getEncoded(ASN1Encoding.DER);
    }

}
