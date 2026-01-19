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

import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import org.bouncycastle.asn1.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The ASNMapperTableField class extends the ASNBaseMapper to serialize and deserialize
 * an instance of the TableField class into and from its corresponding ASN.1 representation.
 * This class uses an associated ASNMapperForm instance to process nested Form objects
 * within the TableField.
 *
 * The mapping definition is encapsulated in an inner static class `Map`, which defines
 * constants for field indices and their corresponding aliases used in the ASN.1 structure.
 */
public class ASNTableFieldMapper extends ASNBaseMapper<TableField> {
    private final ASNMapperForm asnMapForm;

    /**
     * Constructs an instance of ASNMapperTableField with the specified ASNMapperForm.
     * The provided ASNMapperForm is used to encode and decode Form objects that are
     * part of the TableField structure during the serialization/deserialization process.
     *
     * @param asnMapForm an instance of ASNMapperForm that provides mapping functionality
     *                   for Form objects contained within the TableField.
     */
    public ASNTableFieldMapper(ASNMapperForm asnMapForm) {
        super(InitType.NONE);
        this.asnMapForm = asnMapForm;
    }

    /**
     * The Map class is an extension of ASNBaseMapper.Map that defines additional static constants
     * and their aliases specific to the mapping of fields in the ASN.1 representation of a TableField
     * object. These constants are utilized as field indices and descriptors to enable encoding and
     * decoding operations within the implementation of ASNMapperTableField.
     *
     * Constants:
     * - SUM_DECIMALS: Represents the field identifier for the "sumDecimals" property.
     * - SUM_DECIMALS_ALIAS: A human-readable alias for the "sumDecimals" property.
     * - RECORDS: Represents the field identifier for the "records" property.
     * - RECORDS_ALIAS: A human-readable alias for the "records" property.
     *
     * This class provides a mechanism to map specific parts of the TableField object into
     * their corresponding locations in the ASN.1 sequence, ensuring proper serialization
     * and deserialization of the data structure.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int SUM_DECIMALS = 0;
        public static final String SUM_DECIMALS_ALIAS = "Sum Decimals";
        public static final int RECORDS = 1;
        public static final String RECORDS_ALIAS = "Records";
    }

    /**
     * Encodes a {@code TableField} object into a {@code DERSequence}.
     * This method converts the {@code TableField} into an ASN.1 DER structure using its underlying properties.
     * It incorporates detailed encoding for the {@code sumDecimals} property and the list of records it holds.
     *
     * @param vo the {@code TableField} object to encode. The object contains a boolean property
     *           ({@code sumDecimals}) and a list of {@code Form} objects ({@code tableRecords}) to be encoded.
     * @return a {@code DERSequence} representation of the {@code TableField},
     *         which includes the encoded {@code sumDecimals} and {@code tableRecords} properties.
     */
    @Override
    public DERSequence encode(TableField vo) {
        ASN1EncodableVector vecTblFld = new ASN1EncodableVector();

        ASN1EncodableVector recordsVector = new ASN1EncodableVector();
        List<Form> records = vo.getTableRecords();
        if (records != null) records.forEach(form -> recordsVector.add(this.asnMapForm.encode(form)));

        Boolean boolVal = vo.getSumDecimals();
        vecTblFld.add((boolVal != null && boolVal) ? ASN1Boolean.TRUE : ASN1Boolean.FALSE);//0
        vecTblFld.add(new DERSequence(recordsVector));//1
        return new DERSequence(vecTblFld);
    }

    /**
     * Decodes a byte array in DER format into a {@code TableField} object.
     * This method parses the provided DER-encoded data, initializes an {@code ASN1Sequence},
     * and extracts {@code TableField} properties including {@code sumDecimals} and {@code tableRecords}.
     *
     * @param der the DER-encoded byte array representing the {@code TableField}
     * @return the decoded {@code TableField} object
     */
    @Override
    public TableField decode(byte[] der) {
        return this.decode(this.initSeq(der));
    }

    /**
     * Decodes an {@link ASN1Sequence} into a {@link TableField} object.
     * The method processes the input sequence to extract the {@code sumDecimals} flag
     * and a list of {@link Form} objects that constitute the table records.
     *
     * @param seq the {@link ASN1Sequence} to decode, which should contain data
     *            corresponding to the {@code sumDecimals} flag and the table records
     *            in the form of an ASN.1 structure.
     * @return a {@link TableField} instance populated with the decoded {@code sumDecimals}
     *         flag and table records list.
     * @throws com.fluidbpm.ws.client.FluidClientException if the sequence does not conform to the expected structure or if decoding fails.
     */
    public TableField decode(ASN1Sequence seq) {
        boolean sumDecimals = asBool(seq.getObjectAt(Map.SUM_DECIMALS), Map.SUM_DECIMALS_ALIAS);
        List<Form> tableRecords = new ArrayList<>();
        ASN1Sequence seqTableRecords = this.asSeq(seq.getObjectAt(Map.RECORDS), Map.RECORDS_ALIAS);
        for (int i = 0; i < seqTableRecords.size(); i++) {
            tableRecords.add(this.asnMapForm.decode(asSeq(seqTableRecords.getObjectAt(i), Map.RECORDS_ALIAS)));
        }
        return new TableField(sumDecimals, tableRecords);
    }
}
