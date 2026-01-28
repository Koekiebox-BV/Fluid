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
import com.fluidbpm.program.api.vo.ABaseGSONListing;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERTaggedObject;

/**
 * The ASNBaseTaggedListingMapper abstract class provides a mechanism for mapping tagged ASN.1 objects
 * to and from instances of listing-based data structures that are extensions of ABaseGSONListing.
 * It builds upon the functionality provided by ASNBaseTaggedMapper.
 *
 * @param <T> the type of the individual elements within the listing, extending ABaseFluidGSONObject
 * @param <L> the type of the listing object, extending ABaseGSONListing
 */
public abstract class ASNBaseTaggedListingMapper<
        T extends ABaseFluidGSONObject,
        L extends ABaseGSONListing<T>> extends ASNBaseTaggedMapper<L> {
    /**
     * The {@code Map} class serves as a specialized extension of the {@code ASNBaseMapper.Map} class.
     * It provides additional static constants that act as field identifiers for mapping specific
     * tagged objects in ASN.1 sequences. This allows for streamlined encoding and decoding of certain
     * field-related data in the context of the tagged listing mapper's operations.
     * Static Field Identifiers:
     * - {@code LISTING}: Field identifier associated with a generic listing element.
     * - {@code LISTING_COUNT}: Field identifier representing the count of listings.
     * - {@code LISTING_INDEX}: Field identifier denoting the index of a specific listing.
     * - {@code LISTING_PAGE}: Field identifier used for pagination and referencing a specific page
     *   of listings.
     * This class is utilized in scenarios requiring specialized handling of tagged objects for the
     * purpose of interoperability between mapped Java objects and ASN.1 schemas. It simplifies
     * operations by providing predefined constants for frequently accessed fields.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int LISTING = 1;
        public static final int LISTING_COUNT = 2;
        public static final int LISTING_INDEX = 3;
        public static final int LISTING_PAGE = 4;
    }
    
    private final ASNBaseTaggedMapper<T> mapper;

    /**
     * Constructs an instance of ASNBaseTaggedListingMapper with the specified mapper.
     * @param mapper the mapper to be used for encoding and decoding tagged objects
     */
    public ASNBaseTaggedListingMapper(ASNBaseTaggedMapper<T> mapper) {
        super(InitType.ALL);
        this.mapper = mapper;
    }

    /**
     * Maps a decoded tagged ASN.1 object to the corresponding fields of a given object.
     * This method processes the specified {@code tag}, extracts its tag number and associated
     * ASN.1 object, and assigns the decoded data to the appropriate properties of the
     * object referenced in the tag's {@code toPopulate} field, based on the tag's identifier.
     * The method verifies that the provided {@code tag} and its nested information are not null.
     * It uses predefined tag constants to determine the type of processing required, such as decoding
     * lists or extracting integer values, and populates the corresponding fields of the object being mapped.
     *
     * @param tag the tagged ASN.1 object encapsulating a tag number, the field to populate,
     *            and the ASN.1 object to decode; must not be null and must contain non-null
     *            inner objects.
     * @return always returns null, as this method modifies the {@code toPopulate} object
     *         in place for mapping purposes.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<L> tag) {
        assert tag != null : "TagObj cannot be null.";

        L toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.LISTING:
                toPop.setListing(
                        this.mapper.decodeAsList(asSeq(obj, ABaseGSONListing.JSONMapping.LISTING),
                                ABaseGSONListing.JSONMapping.LISTING)
                );
                break;
            case Map.LISTING_COUNT:
                toPop.setListingCount(asInt(obj, ABaseGSONListing.JSONMapping.LISTING_COUNT));
                break;
            case Map.LISTING_INDEX:
                toPop.setListingIndex(asInt(obj, ABaseGSONListing.JSONMapping.LISTING_INDEX));
                break;
            case Map.LISTING_PAGE:
                toPop.setListingPage(asInt(obj, ABaseGSONListing.JSONMapping.LISTING_PAGE));
                break;
        }

        return null;
    }

    /**
     * Encodes the specified object into an ASN1EncodableVector utilizing tagged ASN.1 objects.
     * This method constructs and appends tagged objects for various fields of the given item,
     * based on predefined field identifiers. It ensures that the input parameters are non-null
     * and that the vector has a valid size before encoding.
     *
     * @param item the object containing the data to be encoded; must not be null
     * @param vect the vector to which the encoded tagged objects are appended; must not be null and must have a size greater than zero
     */
    @Override
    public void encodeTaggedObject(L item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        this.mapper.setAsList(item.getListing(), vect, Map.LISTING);

        if (item.getListingCount() != null) {
            vect.add(new DERTaggedObject(true, Map.LISTING_COUNT, new ASN1Integer(item.getListingCount())));
        }

        if (item.getListingIndex() != null) {
            vect.add(new DERTaggedObject(true, Map.LISTING_INDEX, new ASN1Integer(item.getListingIndex())));
        }

        if (item.getListingPage() != null) {
            vect.add(new DERTaggedObject(true, Map.LISTING_PAGE, new ASN1Integer(item.getListingPage())));
        }
    }
}
