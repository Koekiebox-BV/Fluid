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

import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.function.Supplier;

import static com.fluidbpm.program.api.vo.ABaseGSONListing.JSONMapping;
import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperFormHistoricDataListing.Map.*;

/**
 * The ASNMapperFormHistoricDataListing class is responsible for encoding and decoding objects of type
 * {@code FormHistoricDataListing} into ASN.1 DER-encoded sequences with specific tagged mappings. This
 * mapper extends the {@code ASNBaseTaggedMapper} class and specializes in handling
 * {@code FormHistoricDataListing} instances, applying domain-specific logic for the serialization and
 * deserialization processes of tagged data objects.
 * <p>
 * It provides mechanisms for:
 * - Creating new {@code FormHistoricDataListing} instances for decoding purposes.
 * - Assigning tagged data to the appropriate fields of {@code FormHistoricDataListing}.
 * - Encoding {@code FormHistoricDataListing} objects into ASN.1 DER-compliant tagged structures.
 * <p>
 * This mapper is typically employed in scenarios where standardized encoding
 * of {@code FormHistoricDataListing}-related data is essential for interoperability or persistence.
 */
public class ASNMapperFormHistoricDataListing extends ASNBaseTaggedMapper<FormHistoricDataListing> {
    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} and provides a set of static constants
     * to represent field identifiers for mapping ASN.1 sequences specific to FormHistoricDataListing objects.
     * These constants facilitate the identification and mapping of fields during encoding
     * and decoding operations between ASN.1 sequences and internal data structures.
     * <p>
     * Field Identifiers:
     * - {@code LISTING}: Identifier for the "listing" field.
     * - {@code LISTING_COUNT}: Identifier for the "listingCount" field.
     * - {@code LISTING_INDEX}: Identifier for the "listingIndex" field.
     * - {@code LISTING_PAGE}: Identifier for the "listingPage" field.
     * <p>
     * This class enables developers to work with tagged ASN.1 data by associating each
     * field in a FormHistoricDataListing object with a unique constant, simplifying the mapping process.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int LISTING = 1;
        public static final int LISTING_COUNT = 2;
        public static final int LISTING_INDEX = 3;
        public static final int LISTING_PAGE = 4;
    }

    private final ASNMapperFormHistoricData asnMapFormHistoricData;

    /**
     * Constructs an instance of the ASNMapperFormHistoricDataListing class using the specified
     * form historic data mapper and payload populate parameters.
     *
     * @param mapHistData an instance of {@code ASNMapperFormHistoricData} representing the
     *                         form historic data mappings for ASN.1 encoding and decoding.
     */
    public ASNMapperFormHistoricDataListing(ASNMapperFormHistoricData mapHistData) {
        super(InitType.ALL);
        this.asnMapFormHistoricData = mapHistData;
    }

    /**
     * Provides a supplier for creating new instances of the {@code FormHistoricDataListing} class.
     * This method is typically used to generate fresh {@code FormHistoricDataListing} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code FormHistoricDataListing} instance.
     */
    @Override
    protected Supplier<FormHistoricDataListing> supplierForInstance() {
        return FormHistoricDataListing::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<FormHistoricDataListing>} object to the appropriate attributes
     * of the associated {@code FormHistoricDataListing} instance. The method processes the tag number and updates
     * the corresponding fields in {@code FormHistoricDataListing} based on the tag's content.
     *
     * @param tag the {@code TagObj<FormHistoricDataListing>} object that contains the tag number and data to be
     *            mapped to the {@code FormHistoricDataListing} instance.
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<FormHistoricDataListing> tag) {
        assert tag != null : "TagObj cannot be null.";

        FormHistoricDataListing toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case LISTING:
                toPop.setListing(
                        this.asnMapFormHistoricData.decodeAsList(asSeq(obj, JSONMapping.LISTING), JSONMapping.LISTING)
                );
                break;
            case LISTING_COUNT:
                toPop.setListingCount(asInt(obj, JSONMapping.LISTING_COUNT));
                break;
            case LISTING_INDEX:
                toPop.setListingIndex(asInt(obj, JSONMapping.LISTING_INDEX));
                break;
            case LISTING_PAGE:
                toPop.setListingPage(asInt(obj, JSONMapping.LISTING_PAGE));
                break;
        }

        return null;
    }

    /**
     * Encodes the attributes of a {@code FormHistoricDataListing} object as ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     * <p>
     * Depending on the attributes present in the given {@code FormHistoricDataListing} object,
     * this method creates tagged objects for fields and adds them to the provided vector.
     *
     * @param item The {@code FormHistoricDataListing} object to be encoded. Must not be null.
     * @param vect The {@code ASN1EncodableVector} where the encoded tagged
     *             objects will be added. Must not be null.
     */
    @Override
    public void encodeTaggedObject(FormHistoricDataListing item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        // Listing:
        this.asnMapFormHistoricData.setAsList(item.getListing(), vect, LISTING);

        if (item.getListingCount() != null) {
            vect.add(new DERTaggedObject(true, LISTING_COUNT, new ASN1Integer(item.getListingCount())));
        }

        if (item.getListingIndex() != null) {
            vect.add(new DERTaggedObject(true, LISTING_INDEX, new ASN1Integer(item.getListingIndex())));
        }

        if (item.getListingPage() != null) {
            vect.add(new DERTaggedObject(true, LISTING_PAGE, new ASN1Integer(item.getListingPage())));
        }
    }

}
