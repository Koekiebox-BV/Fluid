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

import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.form.TableRecordListing;

import java.util.function.Supplier;

/**
 * Maps a {@code TableRecordListing} to and from tagged ASN.1 objects.
 *
 * @see ASNMapperTableRecord
 * @see TableRecordListing
 */
public class ASNMapperTableRecordListing extends ASNBaseTaggedListingMapper<TableRecord, TableRecordListing> {

    /**
     * Constructor that sets the mapper for individual table records.
     *
     * @param mapper The table-record mapper.
     */
    public ASNMapperTableRecordListing(ASNMapperTableRecord mapper) {
        super(mapper);
    }

    @Override
    protected Supplier<TableRecordListing> supplierForInstance() {
        return TableRecordListing::new;
    }
}
