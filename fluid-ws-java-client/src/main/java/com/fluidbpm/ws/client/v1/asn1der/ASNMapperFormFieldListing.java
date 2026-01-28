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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.FormFieldListing;

import java.util.function.Supplier;

/**
 * The ASNMapperFormFieldListing class is responsible for mapping ASN.1 tagged objects
 * to and from {@code FormFieldListing} instances. It extends the generic functionality
 * of {@code ASNBaseTaggedListingMapper} and provides specific implementations for handling
 * the {@code FormFieldListing} type.
 */
public class ASNMapperFormFieldListing extends ASNBaseTaggedListingMapper<Field, FormFieldListing> {
    /**
     * Constructs an instance of the ASNMapperFormFieldListing class.
     *
     * @param mapField The instance of ASNMapperField used for mapping operations
     *                 and providing necessary mapping configurations.
     */
    public ASNMapperFormFieldListing(ASNMapperField mapField) {
        super(mapField);
    }

    /**
     * Provides a {@code Supplier} that generates an instance of {@code FormListing}.
     *
     * @return A {@code Supplier} capable of creating a new {@code FormListing} instance.
     */
    @Override
    protected Supplier<FormFieldListing> supplierForInstance() {
        return FormFieldListing::new;
    }
}
