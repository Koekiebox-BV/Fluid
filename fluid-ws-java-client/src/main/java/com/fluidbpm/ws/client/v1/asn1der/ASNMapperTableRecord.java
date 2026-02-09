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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.TableRecord;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * The ASNMapperTableRecord class is responsible for encoding and decoding
 * instances of {@code TableRecord} into and from ASN.1 DER format.
 */
public class ASNMapperTableRecord extends ASNBaseTaggedMapper<TableRecord> {
    public static class Map extends ASNBaseMapper.Map {
        public static final int FORM_CONTAINER = 1;
        public static final int PARENT_FORM_CONTAINER = 2;
        public static final int PARENT_FORM_FIELD = 3;
    }

    private final ASNMapperForm asnMapForm;
    private final ASNMapperField asnMapField;

    public ASNMapperTableRecord(ASNMapperForm asnMapForm, ASNMapperField asnMapField) {
        super(InitType.ID_ONLY);
        this.asnMapForm = asnMapForm;
        this.asnMapField = asnMapField;
    }

    @Override
    protected Supplier<TableRecord> supplierForInstance() {
        return TableRecord::new;
    }

    @Override
    protected Void mapDecodedTaggedObject(TagObj<TableRecord> tag) {
        assert tag != null : "Arguments cannot be null.";

        TableRecord toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.FORM_CONTAINER:
                toPop.setFormContainer(
                        this.asnMapForm.decode(asSeq(obj, TableRecord.JSONMapping.FORM_CONTAINER))
                );
                break;
            case Map.PARENT_FORM_CONTAINER:
                toPop.setParentFormContainer(
                        this.asnMapForm.decode(asSeq(obj, TableRecord.JSONMapping.PARENT_FORM_CONTAINER))
                );
                break;
            case Map.PARENT_FORM_FIELD:
                toPop.setParentFormField(
                        this.asnMapField.decode(asSeq(obj, TableRecord.JSONMapping.PARENT_FORM_FIELD))
                );
                break;
        }
        return null;
    }

    @Override
    protected void encodeTaggedObject(TableRecord item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        Form formContainer = item.getFormContainer();
        if (formContainer != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_CONTAINER, this.asnMapForm.encode(formContainer)));
        }

        Form parentFormContainer = item.getParentFormContainer();
        if (parentFormContainer != null) {
            vect.add(new DERTaggedObject(true, Map.PARENT_FORM_CONTAINER, this.asnMapForm.encode(parentFormContainer)));
        }

        Field parentFormField = item.getParentFormField();
        if (parentFormField != null) {
            vect.add(new DERTaggedObject(true, Map.PARENT_FORM_FIELD, this.asnMapField.encode(parentFormField)));
        }
    }
}
