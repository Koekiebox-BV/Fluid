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

import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;

import java.util.function.Supplier;

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
public class ASNMapperFormHistoricDataListing extends
        ASNBaseTaggedListingMapper<FormHistoricData, FormHistoricDataListing> {

    /**
     * Constructs an instance of the ASNMapperFormHistoricDataListing class using the specified
     * form historic data mapper and payload populate parameters.
     *
     * @param mapHistData an instance of {@code ASNMapperFormHistoricData} representing the
     *                         form historic data mappings for ASN.1 encoding and decoding.
     */
    public ASNMapperFormHistoricDataListing(ASNMapperFormHistoricData mapHistData) {
        super(mapHistData);
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
}
