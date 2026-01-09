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
import com.fluidbpm.ws.client.v1.asn1der.vo.PayloadPopulate;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.function.Supplier;

/**
 * The ASNMapperField class is responsible for mapping, decoding, and encoding operations
 * on {@code Field} objects that are part of the ASN.1 DER sequence processing. It extends
 * the {@code ASNBaseTaggedMapper} to provide custom implementations tailored for handling
 * {@code Field} instances.
 */
public class ASNMapperField extends ASNBaseTaggedMapper<Field> {
    private final PayloadPopulate payloadPopulate;

    public static class Map extends ASNBaseMapper.Map {
        public static final int NAME = 1;

        // Tag value based on type index:
        public static final int VALUE_1_TEXT = 2;
        public static final int VALUE_2_TRUE_FALSE = 3;
        public static final int VALUE_3_PARA_TEXT = 4;
        public static final int VALUE_4_MULTI = 5;
        public static final int VALUE_5_DATE_TIME = 6;
        public static final int VALUE_6_DECIMAL_INT = 7;
        public static final int VALUE_6_DECIMAL_REAL = 8;
        public static final int VALUE_7_TABLE = 9;
        public static final int VALUE_8_ENCRYPTED = 10;
        public static final int VALUE_9_LABEL = 11;
    }

    public ASNMapperField(PayloadPopulate payloadPopulate) {
        super(InitType.ID_ONLY);
        this.payloadPopulate = payloadPopulate;
    }

    /**
     * Provides a supplier for creating new instances of the {@code Form} class.
     * This method is typically used to generate fresh {@code Form} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code Field} instance.
     */
    @Override
    protected Supplier<Field> supplierForInstance() {
        return Field::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<Form>} object to the appropriate attributes
     * of the associated {@code Form} instance. The method processes the tag number and updates
     * the corresponding fields in {@code Form} based on the tag's content.
     *
     * @param tag the {@code TagObj<Form>} object that contains the tag number and data to be
     *            mapped to the {@code Form} instance.
     *
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<Field> tag) {
        assert tag != null : "Arguments cannot be null.";

        Field toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        String fieldName = null;
        switch (tag.getTagNo()) {
            case Map.NAME:
                toPop.setFieldName(fieldName = asGeneralTxt(obj, Field.JSONMapping.FIELD_NAME));
                break;
            case Map.VALUE_1_TEXT:
                toPop.setTypeAsEnum(Field.Type.Text);
                toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_2_TRUE_FALSE:
                toPop.setTypeAsEnum(Field.Type.TrueFalse);
                toPop.setFieldValue(asBool(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_3_PARA_TEXT:
                toPop.setTypeAsEnum(Field.Type.ParagraphText);
                toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_4_MULTI:
                toPop.setTypeAsEnum(Field.Type.MultipleChoice);
                //TODO set the avail
                //TODO set the selected

                // TODO toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_5_DATE_TIME:
                toPop.setTypeAsEnum(Field.Type.DateTime);
                toPop.setFieldValue(asDate(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_6_DECIMAL_INT:
                toPop.setTypeAsEnum(Field.Type.Decimal);
                toPop.setFieldValue(asLong(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_6_DECIMAL_REAL:
                toPop.setTypeAsEnum(Field.Type.Decimal);
                toPop.setFieldValue(asReal(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_7_TABLE:
                toPop.setTypeAsEnum(Field.Type.Table);
                // TODO toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_8_ENCRYPTED:
                toPop.setTypeAsEnum(Field.Type.TextEncrypted);
                toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
            case Map.VALUE_9_LABEL:
                toPop.setTypeAsEnum(Field.Type.Label);
                toPop.setFieldValue(asUtf8(obj, Field.JSONMapping.FIELD_VALUE));
                break;
        }

        toPop.setTypeMetaData(this.payloadPopulate.getMetaDataValue(fieldName));

        return null;
    }

    /**
     * Encodes a tagged object for the specified field and appends it to the provided ASN1EncodableVector.
     * The method ensures that the field's name, if present, is encoded as a DERTaggedObject.
     *
     * @param item the {@code Field} instance containing the data to be encoded. Must not be null.
     * @param vect the {@code ASN1EncodableVector} to which the encoded tagged object will be added.
     *             Must not be null and its size must be greater than zero.
     */
    @Override
    public void encodeTaggedObject(Field item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getFieldName() != null) {
            vect.add(new DERTaggedObject(true, Map.NAME, new DERGeneralString(item.getFieldName())));
        }

        Field.Type type = item.getTypeAsEnum();
        if (type != null && item.getFieldValue() != null) {
            switch (type) {
                case Text:
                case ParagraphText:
                case TrueFalse:
                case DateTime:
                case Decimal:
                case Label:
                    vect.add(new DERTaggedObject(true, Map.VALUE_1_TEXT,
                            new DERGeneralString(item.getFieldValue().toString())));
                    break;
                case MultipleChoice:
            }
        }
    }
}
