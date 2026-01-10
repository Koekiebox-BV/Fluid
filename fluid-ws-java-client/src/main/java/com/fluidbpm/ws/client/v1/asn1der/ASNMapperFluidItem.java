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
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperFluidItem.Map.FORM;

/**
 * ASNMapperFluidItem is a mapper class responsible for encoding and decoding
 * instances of the {@code FluidItem} class into and from ASN.1 DER format.
 *
 * This class extends {@code ASNBaseMapper} specifically to provide mapping functionality
 * for the {@code FluidItem} object within the context of the Abstract Syntax Notation One (ASN.1)
 * representation.
 */
public class ASNMapperFluidItem extends ASNBaseTaggedMapper<FluidItem> {
    private final ASNMapperForm asnMapForm;

    /**
     * Constructs a new instance of {@code ASNMapperFluidItem} with the specified {@code ASNMapperForm}.
     *
     * @param asnMapForm the {@code ASNMapperForm} instance used for configuring the mapper. This parameter
     *                   provides the mapping logic and form-specific settings necessary for processing fluid
     *                   item data in ASN.1 DER format.
     */
    public ASNMapperFluidItem(ASNMapperForm asnMapForm) {
        super(InitType.ID_ONLY);
        this.asnMapForm = asnMapForm;
    }

    /**
     * The {@code Map} class is a specialized extension of the {@code ASNBaseMapper.Map} class.
     * It introduces additional static constants catering specifically to the mapping requirements
     * defined within its context. These constants represent additional field identifiers used for
     * encoding and decoding ASN.1 sequences or records.
     *
     * Constants:
     * - {@code START}: Inherits the value of {@code CONTINUE} from the parent {@code ASNBaseMapper.Map},
     *   serving as the starting index for defining new mappings in this class.
     * - {@code FORM}: Represents an identifier for mapping a specific "form" field.
     * - {@code STEP_ENTERED_TIME}: Represents an identifier for mapping a "step entered time" field.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int FORM = 1;
        public static final int STEP_ENTERED_TIME = 2;
    }

    /**
     * Provides a supplier for creating new instances of {@code FluidItem}.
     *
     * This method returns a {@code Supplier} functional interface that generates
     * fresh {@code FluidItem} objects, which are used in associative mappings
     * facilitated by the parent class.
     *
     * @return a {@code Supplier} functional interface for generating {@code FluidItem} instances.
     */
    @Override
    protected Supplier<FluidItem> supplierForInstance() {
        return FluidItem::new;
    }

    /**
     * Maps a decoded tagged object into the corresponding {@code FluidItem} instance.
     *
     * This method processes an ASN.1 encoded tagged object represented by the {@code TagObj<FluidItem>}
     * parameter and populates a {@code FluidItem} instance based on the tag number and its content.
     * The supported tag numbers determine the mapping behaviors and populate specific fields of the
     * {@code FluidItem} instance according to the defined ASN.1 structure.
     *
     * @param tag an object of type {@code TagObj<FluidItem>} that contains the decoded ASN.1 object
     *            and the {@code FluidItem} instance to be populated. This object must not be null.
     * @return always returns {@code null} as this method operates by side effects, updating the
     *         referenced {@code FluidItem} instance.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<FluidItem> tag) {
        assert tag != null : "TagObj cannot be null.";

        FluidItem toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case FORM:
                toPop.setForm(
                        this.asnMapForm.decode(asSeq(obj, FluidItem.JSONMapping.FORM))
                );
                break;
            case Map.STEP_ENTERED_TIME:
                toPop.setStepEnteredTime(asDate(obj, FluidItem.JSONMapping.STEP_ENTERED_TIME));
                break;
        }
        return null;
    }

    /**
     * Encodes a {@code FluidItem} into an ASN.1 tagged object and adds it to the given {@code ASN1EncodableVector}.
     *
     * This method processes the {@code FluidItem} by checking its non-null fields and encoding them as
     * ASN.1 tagged objects. Each encoded tagged object is then appended to the provided {@code ASN1EncodableVector}.
     * Ensures that the vector is populated with the correct ASN.1 format for the fluid item attributes.
     *
     * @param item The {@code FluidItem} instance to be encoded. Must not be null.
     * @param vect The {@code ASN1EncodableVector} to which the encoded tagged objects will be added. Must not be null.
     */
    @Override
    protected void encodeTaggedObject(FluidItem item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getForm() != null) {
            vect.add(new DERTaggedObject(true, FORM, this.asnMapForm.encode(item.getForm())));
        }

        if (item.getStepEnteredTime() != null) {
            vect.add(new DERTaggedObject(true, Map.STEP_ENTERED_TIME, new ASN1GeneralizedTime(item.getStepEnteredTime())));
        }
    }


}
