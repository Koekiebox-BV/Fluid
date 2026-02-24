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

import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.userquery.UserQueryListing;

import java.util.function.Supplier;

/**
 * The ASNMapperUserQueryListing class is a specialized implementation of the {@code ASNBaseTaggedListingMapper}
 * designed to handle mapping operations for {@code UserQuery} objects and their corresponding {@code UserQueryListing}.
 * It provides functionality to facilitate encoding and decoding of tagged ASN.1 objects associated with user queries.
 * This class leverages the underlying tagging mechanisms defined in its superclass to manage mappings for list-based
 * structures in ASN.1 schemas.
 *
 * This class requires an instance of {@code ASNMapperUserQuery} to function, which acts as the core mapper for handling
 * individual {@code UserQuery} objects within the listing.
 */
public class ASNMapperUserQueryListing extends ASNBaseTaggedListingMapper<UserQuery, UserQueryListing> {
    /**
     * Constructs an instance of ASNMapperUserQueryListing using the specified {@code ASNMapperUserQuery}.
     *
     * @param mapUserQuery the mapper to be used for encoding and decoding {@code UserQuery} objects
     */
    public ASNMapperUserQueryListing(ASNMapperUserQuery mapUserQuery) {
        super(mapUserQuery);
    }

    /**
     * Provides a {@code Supplier} that generates an instance of {@code JobViewListing}.
     *
     * @return A {@code Supplier} capable of creating a new {@code JobViewListing} instance.
     */
    @Override
    protected Supplier<UserQueryListing> supplierForInstance() {
        return UserQueryListing::new;
    }
}
