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

import com.fluidbpm.program.api.vo.form.Form;
import org.bouncycastle.asn1.*;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperForm.Map.FORM_TYPE;
import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperForm.Map.TITLE;

public class ASNMapperForm extends ASNBaseMapper<Form> {
    public static class Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;

        public static final int FORM_TYPE = START++;
        public static final int FORM_TYPE_ID = START++;
        public static final int TITLE = START++;
        public static final int DATE_CREATED = START++;
    }

    public Form decode(byte[] der) {
        ASN1Sequence seq = this.initSeq(der);
        Form returnVal = new Form();
        this.popBaseFields(returnVal, seq);

        for (int i = ASNBaseMapper.Map.CONTINUE; i < seq.size(); i++) {
            ASN1TaggedObject tag = asTagged(seq.getObjectAt(i), "Tag "+i);
            int tagNo = tag.getTagNo();
            ASN1Object prim = tag.getBaseObject();

            if (tagNo == Map.FORM_TYPE) {
                returnVal.setFormType(asUtf8(prim, Form.JSONMapping.FORM_TYPE));
            } else if (tagNo == Map.FORM_TYPE_ID) {
                returnVal.setFormTypeId(asLong(prim, Form.JSONMapping.FORM_TYPE_ID));
            } else if (tagNo == Map.TITLE) {
                returnVal.setTitle(asUtf8(prim, Form.JSONMapping.TITLE));
            } else if (tagNo == Map.DATE_CREATED) {
                returnVal.setDateCreated(asDate(prim, Form.JSONMapping.DATE_CREATED));
            }
        }

        return returnVal;
    }

    public DERSequence encode(Form item) {
        ASN1EncodableVector vect = initVector(item);

        if (item.getFormType() != null) {
            vect.add(new DERTaggedObject(true, FORM_TYPE, new DERUTF8String(item.getFormType())));
        }

        Long formTypeId = item.getFormTypeId();
        if (formTypeId != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_TYPE_ID, new ASN1Integer(formTypeId)));
        }

        if (item.getTitle() != null) {
            vect.add(new DERTaggedObject(true, TITLE, new DERUTF8String(item.getTitle())));
        }

        if (item.getDateCreated() != null) {
            vect.add(new DERTaggedObject(true, Map.DATE_CREATED, new ASN1GeneralizedTime(item.getDateCreated())));
        }

        assert vect.size() <= ASNMapperForm.Map.START : "Vector size is not as expected. ";

        return new DERSequence(vect);
    }

}
