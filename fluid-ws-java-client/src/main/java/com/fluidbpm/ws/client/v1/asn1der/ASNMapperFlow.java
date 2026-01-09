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

import com.fluidbpm.program.api.vo.flow.Flow;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.function.Supplier;

/**
 * A specialized mapper class for handling ASN.1 encoding and decoding of {@link Flow} objects.
 * This class extends {@code ASNBaseTaggedMapper} to provide a mapping utility that transforms
 * ASN.1 tagged objects into {@link Flow} instances and vice versa.
 */
public class ASNMapperFlow extends ASNBaseTaggedMapper<Flow> {
    public static class Map extends ASNBaseMapper.Map {
        public static final int NAME = 1;
    }

    /**
     * Constructs a new instance of the ASNMapperFlow class with the initialization type set to ID_ONLY.
     *
     * This constructor initializes the superclass with the {@code InitType.ID_ONLY} parameter,
     * which specifies that only identifier-related attributes should be initialized. This
     * approach ensures lightweight initialization suitable for scenarios where minimal setup
     * is required.
     */
    public ASNMapperFlow() {
        super(InitType.ID_ONLY);
    }

    /**
     * Provides a supplier that creates new instances of the {@link Flow} class.
     *
     * This method is used to supply fresh instances of the {@code Flow} object
     * during operations requiring dynamic object creation.
     *
     * @return A {@code Supplier} that generates new {@link Flow} instances.
     */
    @Override
    protected Supplier<Flow> supplierForInstance() {
        return Flow::new;
    }

    /**
     * Maps a decoded tagged ASN.1 object to a {@link Flow} instance based on its tag number.
     *
     * The method processes the provided {@link TagObj} by extracting the tag number and mapping
     * the associated data to the corresponding field in the {@link Flow} object. The mapping
     * occurs only for recognized tag numbers defined in {@link ASNMapperFlow.Map}.
     *
     * @param tag The tagged ASN.1 object containing the tag number, associated data, and
     *            the {@link Flow} instance to populate. This parameter must not be {@code null},
     *            and its internal references (e.g., {@code toPopulate} and {@code obj}) must
     *            also be non-null.
     * @return Always returns {@code null}.
     * @throws AssertionError If any of the following are true:
     *                        - {@code tag} is null.
     *                        - {@code tag.getToPopulate()} or {@code tag.getObj()} is null.
     *                        - The tag number is unrecognized.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<Flow> tag) {
        assert tag != null : "Arguments cannot be null.";

        Flow toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.NAME:
                toPop.setName(asGeneralTxt(obj, Flow.JSONMapping.NAME));
                break;
        }
        return null;
    }

    /**
     * Encodes a tagged ASN.1 object from the provided {@link Flow} item and appends it to
     * the given {@link ASN1EncodableVector}.
     *
     * This method checks that the input arguments are not null and that the vector has elements.
     * If the {@code Flow} object provided has a non-null name, it encodes the name as a
     * tagged ASN.1 object and adds it to the vector.
     *
     * @param item The identifier defining the specific tag for the ASN.1 object. In this implementation,
     *            {@code Map.NAME} is used to encode the "name" field of the {@link Flow} object.
     * @param vect The {@link ASN1EncodableVector} to which the encoded object will be appended.
     */
    @Override
    public void encodeTaggedObject(Flow item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getName() != null) {
            vect.add(new DERTaggedObject(true, Map.NAME, new DERGeneralString(item.getName())));
        }
    }
}
