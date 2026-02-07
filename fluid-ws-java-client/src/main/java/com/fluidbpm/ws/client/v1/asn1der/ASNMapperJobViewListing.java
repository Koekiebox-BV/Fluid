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

import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.flow.JobViewListing;

import java.util.function.Supplier;

/**
 * The ASNMapperJobViewListing class extends the functionality of ASNBaseTaggedListingMapper
 * to provide mapping capabilities specifically for JobView and JobViewListing objects.
 */
public class ASNMapperJobViewListing extends ASNBaseTaggedListingMapper<JobView, JobViewListing> {
    /**
     * Constructs an instance of ASNMapperJobViewListing with the specified ASNMapperJobView.
     *
     * @param mapJobView the ASNMapperJobView instance used to provide mapping functionality
     *                   for encoding and decoding tagged ASN.1 representations of JobView
     *                   and JobViewListing objects.
     */
    public ASNMapperJobViewListing(ASNMapperJobView mapJobView) {
        super(mapJobView);
    }

    /**
     * Provides a {@code Supplier} that generates an instance of {@code JobViewListing}.
     *
     * @return A {@code Supplier} capable of creating a new {@code JobViewListing} instance.
     */
    @Override
    protected Supplier<JobViewListing> supplierForInstance() {
        return JobViewListing::new;
    }
}
