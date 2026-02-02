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

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.attachment.AttachmentListing;

import java.util.function.Supplier;

/**
 * The ASNMapperAttachmentListing class is a specific implementation of ASNBaseTaggedListingMapper
 * designed for mapping Attachment objects and their respective AttachmentListing representations
 * in the context of ASN.1 schema conversions.
 *
 * This class extends the functionality provided by ASNBaseTaggedListingMapper, enabling specialized
 * processing for handling `Attachment` and `AttachmentListing` data structures. It leverages the
 * provided mapping mechanics to decode, encode, and map object listings while maintaining consistency
 * with the associated ASN.1 schema.
 */
public class ASNMapperAttachmentListing extends
        ASNBaseTaggedListingMapper<Attachment, AttachmentListing> {

    /**
     * Constructs an instance of ASNMapperAttachmentListing with the specified ASNMapperAttachment.
     * This constructor initializes the mapping logic for handling Attachment and AttachmentListing
     * objects in the context of ASN.1 schema transformations.
     *
     * @param mapAtt the ASNMapperAttachment instance used to support the mapping functionality
     *               for Attachment and AttachmentListing objects.
     */
    public ASNMapperAttachmentListing(ASNMapperAttachment mapAtt) {
        super(mapAtt);
    }

    /**
     * Provides a {@code Supplier} implementation for creating new instances of {@code AttachmentListing}.
     *
     * This method is used to supply fresh {@code AttachmentListing} objects, typically for processing
     * or mapping tasks in the context of ASN.1 schema-based operations. The supplied objects adhere to the
     * structure and behavior of the {@code AttachmentListing} class.
     *
     * @return a {@code Supplier} that generates new {@code AttachmentListing} instances.
     */
    @Override
    protected Supplier<AttachmentListing> supplierForInstance() {
        return AttachmentListing::new;
    }
}
