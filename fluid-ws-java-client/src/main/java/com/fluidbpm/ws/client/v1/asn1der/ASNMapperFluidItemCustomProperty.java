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
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code ASNMapperFluidItemCustomProperty} class is responsible for encoding and decoding
 * {@link FluidItem.FluidItemProperty} objects to and from ASN.1 DER-encoded data. It extends
 * {@link ASNBaseMapper} to provide specialized handling for custom properties within the context
 * of fluid items.
 *
 * This class provides methods to:
 * - Decode DER-encoded byte arrays or {@link ASN1Sequence} instances into {@code FluidItem.FluidItemProperty} objects.
 * - Encode {@code FluidItem.FluidItemProperty} objects into {@link DERSequence} representations.
 *
 * The class relies on positional mapping defined in the nested {@code Map} class to extract
 * name-value pairs from ASN.1 sequences.
 */
public class ASNMapperFluidItemCustomProperty extends ASNBaseMapper<FluidItem.FluidItemProperty> {
    public static class Map {
        public static final int NAME = 0;
        public static final int VALUE = 1;
    }

    public ASNMapperFluidItemCustomProperty() {
        super(InitType.NONE);
    }

    /**
     * Decodes a DER-encoded byte array into a {@link FluidItem.FluidItemProperty} object.
     * This method initializes an {@link ASN1Sequence} from the provided byte array
     * and delegates the decoding to another method that maps the sequence to the object.
     *
     * @param der the DER-encoded byte array representing a {@link FluidItem.FluidItemProperty}.
     *            It is expected to contain the encoded `name` and `value` fields.
     * @return a {@link FluidItem.FluidItemProperty} instance populated with the decoded
     *         `name` and `value` fields extracted from the input byte array.
     */
    public final FluidItem.FluidItemProperty decode(byte[] der) {
        return this.decode(this.initSeq(der));
    }

    /**
     * Decodes an {@link ASN1Sequence} into a {@link FluidItem.FluidItemProperty} object.
     * This method extracts the `name` and `value` components from the provided sequence
     * and maps them to the corresponding properties of a {@link FluidItem.FluidItemProperty} instance.
     *
     * @param seq the {@link ASN1Sequence} to be decoded. It is expected to contain
     *            objects corresponding to the `name` and `value` fields at predefined positions.
     * @return a {@link FluidItem.FluidItemProperty} instance populated with the decoded `name`
     *         and `value` fields extracted from the input sequence.
     */
    public final FluidItem.FluidItemProperty decode(ASN1Sequence seq) {
        FluidItem.FluidItemProperty returnVal = new FluidItem.FluidItemProperty();
        returnVal.setName(asGeneralTxt(seq.getObjectAt(Map.NAME), FluidItem.FluidItemProperty.JSONMapping.NAME));
        returnVal.setValue(asGeneralTxt(seq.getObjectAt(Map.VALUE), FluidItem.FluidItemProperty.JSONMapping.VALUE));
        return returnVal;
    }

    /**
     * Encodes a {@link FluidItem.FluidItemProperty} object into a DER-encoded sequence.
     * The method converts the `name` and `value` fields of the {@code item} into
     * DERGeneralString objects and combines them into a DERSequence.
     *
     * @param item the {@link FluidItem.FluidItemProperty} object to be encoded.
     *             It contains the `name` and `value` fields to be transformed
     *             into DER-encoded components.
     * @return a {@link DERSequence} object containing the DER-encoded `name`
     *         and `value` of the input {@link FluidItem.FluidItemProperty}.
     */
    public DERSequence encode(FluidItem.FluidItemProperty item) {
        ASN1EncodableVector vectUser = new ASN1EncodableVector();
        vectUser.add(new DERGeneralString(DefWhenNull.nullSafeTxt(item.getName())));
        vectUser.add(new DERGeneralString(DefWhenNull.nullSafeTxt(item.getValue())));

        assert vectUser.size() == 2 : "Field size is not as expected.";

        return new DERSequence(vectUser);
    }

    /**
     * Decodes an {@link ASN1Sequence} into a list of {@link FluidItem.FluidItemProperty} objects.
     * This method iterates over the elements of the provided sequence, decodes each element
     * into a {@link FluidItem.FluidItemProperty}, and adds it to the resulting list.
     *
     * @param fieldsSeq the {@link ASN1Sequence} to be decoded. It is expected to contain
     *                  multiple elements that can be mapped to {@link FluidItem.FluidItemProperty} objects.
     * @param fieldName the name of the field being processed. It is used
     *                  to provide additional context during the decoding process.
     * @return a list of {@link FluidItem.FluidItemProperty} objects populated with the decoded
     *         data extracted from the input sequence.
     */
    public final List<FluidItem.FluidItemProperty> decodeAsList(ASN1Sequence fieldsSeq, String fieldName) {
        List<FluidItem.FluidItemProperty> returnVal = new ArrayList<>();
        for (int j = 0; j < fieldsSeq.size(); j++) {
            returnVal.add(this.decode(this.asSeq(fieldsSeq.getObjectAt(j), fieldName)));
        }
        return returnVal;
    }
}
