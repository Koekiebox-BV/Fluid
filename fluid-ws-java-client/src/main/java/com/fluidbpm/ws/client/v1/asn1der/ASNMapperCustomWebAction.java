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
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * The ASNMapperCustomWebAction class is responsible for encoding and decoding
 * instances of {@code CustomWebAction} into and from ASN.1 DER format.
 */
public class ASNMapperCustomWebAction extends ASNBaseTaggedMapper<CustomWebAction> {
    public static class Map extends ASNBaseMapper.Map {
        public static final int FORM = 1;
        public static final int TASK_IDENTIFIER = 2;
        public static final int IS_TABLE_RECORD = 3;
        public static final int FORM_TABLE_RECORD_BELONGS_TO = 4;
        public static final int EXECUTION_TIME_MILLIS = 5;
    }

    private final ASNMapperForm asnMapForm;

    public ASNMapperCustomWebAction(ASNMapperForm asnMapForm) {
        super(InitType.ID_ONLY);
        this.asnMapForm = asnMapForm;
    }

    @Override
    protected Supplier<CustomWebAction> supplierForInstance() {
        return CustomWebAction::new;
    }

    /**
     * Decodes and maps the provided tagged ASN.1 object into a {@code CustomWebAction} object based on its tag number.
     *
     * This method interprets the tag number of the provided {@code TagObj} instance and assigns the corresponding
     * value to a field of the {@code CustomWebAction} instance. The mapping is done based on predefined tags
     * representing specific fields of the {@code CustomWebAction}.
     *
     * The method uses the following mappings:
     * - FORM: Populates the {@code form} field of {@code CustomWebAction}.
     * - TASK_IDENTIFIER: Populates the {@code taskIdentifier} field.
     * - IS_TABLE_RECORD: Populates the {@code isTableRecord} field.
     * - FORM_TABLE_RECORD_BELONGS_TO: Populates the {@code formTableRecordBelongsTo} field.
     * - EXECUTION_TIME_MILLIS: Populates the {@code executionTimeMillis} field.
     *
     * The method performs assertions to ensure the {@code TagObj} instance and its contents are not null.
     * Throws an {@link AssertionError} if any required parameter or value is null.
     *
     * @param tag the tagged ASN.1 object containing metadata and the object to be decoded.
     *            Must not be null, and all its attributes must also be non-null.
     * @return always returns {@code null} after the decoding and mapping process.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<CustomWebAction> tag) {
        assert tag != null : "Arguments cannot be null.";

        CustomWebAction toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.FORM:
                toPop.setForm(this.asnMapForm.decode(asSeq(obj, CustomWebAction.JSONMapping.FORM)));
                break;
            case Map.TASK_IDENTIFIER:
                toPop.setTaskIdentifier(asGeneralTxt(obj, CustomWebAction.JSONMapping.TASK_IDENTIFIER));
                break;
            case Map.IS_TABLE_RECORD:
                toPop.setIsTableRecord(asBool(obj, CustomWebAction.JSONMapping.IS_TABLE_RECORD));
                break;
            case Map.FORM_TABLE_RECORD_BELONGS_TO:
                toPop.setFormTableRecordBelongsTo(
                        asLong(obj, CustomWebAction.JSONMapping.FORM_TABLE_RECORD_BELONGS_TO)
                );
                break;
            case Map.EXECUTION_TIME_MILLIS:
                toPop.setExecutionTimeMillis(
                        asLong(obj, CustomWebAction.JSONMapping.EXECUTION_TIME_MILLIS)
                );
                break;
        }
        return null;
    }

    /**
     * Encodes the given {@code CustomWebAction} object into an {@code ASN1EncodableVector} using
     * predefined tags to structure the data.
     *
     * @param item The object of type {@code T} to be encoded.
     *             This object contains data that will be transformed
     *             into an ASN.1-compliant representation.
     * @param vect The {@code ASN1EncodableVector} to which the encoded
     *               data will be appended. Each element in the vector
     *               corresponds to an encoded field or structure from
     *               the input object.
     */
    @Override
    protected void encodeTaggedObject(CustomWebAction item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        Form form = item.getForm();
        if (form != null) {
            vect.add(new DERTaggedObject(true, Map.FORM, this.asnMapForm.encode(form)));
        }

        if (item.getTaskIdentifier() != null) {
            vect.add(new DERTaggedObject(true, Map.TASK_IDENTIFIER,
                    new DERGeneralString(item.getTaskIdentifier())));
        }

        Boolean isTableRecord = item.getIsTableRecord();
        if (isTableRecord != null) {
            vect.add(new DERTaggedObject(true, Map.IS_TABLE_RECORD,
                    isTableRecord ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
        }

        Long recordBelongsTo = item.getFormTableRecordBelongsTo();
        if (recordBelongsTo != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_TABLE_RECORD_BELONGS_TO,
                    new ASN1Integer(recordBelongsTo)));
        }

        Long execTimeMillis = item.getExecutionTimeMillis();
        if (execTimeMillis != null) {
            vect.add(new DERTaggedObject(true, Map.EXECUTION_TIME_MILLIS,
                    new ASN1Integer(execTimeMillis)));
        }
    }
}
