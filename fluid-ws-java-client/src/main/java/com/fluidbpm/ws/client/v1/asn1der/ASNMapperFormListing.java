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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;

import java.util.function.Supplier;

/**
 * The ASNMapperFormListing class extends the functionality of ASNBaseTaggedListingMapper
 * to provide mapping capabilities specifically for Form and FormListing objects.
 * It leverages the ASNMapperForm mapper to encode and decode tagged ASN.1 representations
 * of the Form and FormListing objects.
 */
public class ASNMapperFormListing extends ASNBaseTaggedListingMapper<Form, FormListing> {
    /**
     * Constructs an instance of ASNMapperFormListing with the specified ASNMapperForm.
     *
     * @param mapForm the ASNMapperForm instance used to provide mapping functionality
     *                for encoding and decoding tagged ASN.1 representations of Form
     *                and FormListing objects.
     */
    public ASNMapperFormListing(ASNMapperForm mapForm) {
        super(mapForm);
    }

    /**
     * Provides a {@code Supplier} that generates an instance of {@code FormListing}.
     *
     * @return A {@code Supplier} capable of creating a new {@code FormListing} instance.
     */
    @Override
    protected Supplier<FormListing> supplierForInstance() {
        return FormListing::new;
    }
}
