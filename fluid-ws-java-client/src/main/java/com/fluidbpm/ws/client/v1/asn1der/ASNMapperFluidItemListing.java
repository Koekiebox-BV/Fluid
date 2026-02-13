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
import com.fluidbpm.program.api.vo.item.FluidItemListing;

import java.util.function.Supplier;

/**
 * The ASNMapperFluidItemListing class extends the functionality of ASNBaseTaggedListingMapper
 * to provide mapping capabilities specifically for FluidItem and FluidItemListing objects.
 */
public class ASNMapperFluidItemListing extends ASNBaseTaggedListingMapper<FluidItem, FluidItemListing> {
    /**
     * Constructs an instance of ASNMapperFluidItemListing with the specified ASNMapperJobView.
     *
     * @param mapJobView the ASNMapperJobView instance used to provide mapping functionality
     *                   for encoding and decoding tagged ASN.1 representations of JobView
     *                   and JobFluidItemListingListing objects.
     */
    public ASNMapperFluidItemListing(ASNMapperFluidItem mapJobView) {
        super(mapJobView);
    }

    /**
     * Provides a {@code Supplier} that generates an instance of {@code JobFluidItemListingListing}.
     *
     * @return A {@code Supplier} capable of creating a new {@code FluidItemListing} instance.
     */
    @Override
    protected Supplier<FluidItemListing> supplierForInstance() {
        return FluidItemListing::new;
    }
}
