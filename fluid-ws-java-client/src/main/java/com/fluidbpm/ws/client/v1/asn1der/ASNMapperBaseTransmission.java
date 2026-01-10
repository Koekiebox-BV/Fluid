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

import com.fluidbpm.ws.client.v1.asn1der.vo.BaseTransmission;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/**
 * A mapper class that handles encoding and decoding of {@code BaseTransmission} objects
 * to and from ASN.1 DER-encoded data. This class extends {@code ASNBaseMapper}
 * and provides custom logic for processing ASN.1 sequences for
 * {@code BaseTransmission} objects.
 */
public class ASNMapperBaseTransmission extends ASNBaseMapper<BaseTransmission> {
    public ASNMapperBaseTransmission() {
        super(InitType.ALL);
    }

    /**
     * Decodes an ASN.1 sequence into a {@code BaseTransmission} object by populating its fields
     * using the provided sequence.
     *
     * @param seq the ASN.1 sequence containing the encoded data used to populate the {@code BaseTransmission} object.
     * @return the decoded {@code BaseTransmission} object with fields populated from the ASN.1 sequence.
     */
    public BaseTransmission decode(ASN1Sequence seq) {
        BaseTransmission returnVal = new BaseTransmission();
        this.popBaseFields(returnVal, seq);
        return returnVal;
    }

    /**
     * Decodes a DER-encoded byte array into a {@code BaseTransmission} object.
     * The method initializes an ASN1 sequence from the provided byte array and
     * populates a {@code BaseTransmission} instance with the data.
     *
     * @param der the DER-encoded byte array containing the data to be decoded.
     * @return the decoded {@code BaseTransmission} object with fields populated
     *         from the ASN.1 sequence.
     * @throws com.fluidbpm.ws.client.FluidClientException if the provided byte array is invalid or cannot
     *         be decoded into a valid ASN.1 sequence.
     */
    public BaseTransmission decode(byte[] der) {
        return this.decode(this.initSeq(der));
    }

    /**
     * Encodes a {@code BaseTransmission} object into a DER-encoded ASN.1 sequence.
     * The method initializes an {@code ASN1EncodableVector} with the properties of the
     * provided {@code BaseTransmission} object and creates a {@code DERSequence} from it.
     *
     * @param item the {@code BaseTransmission} object to be encoded. This object contains
     *             the data to be converted into a DER-encoded ASN.1 sequence.
     * @return a {@code DERSequence} representing the ASN.1 encoding of the provided
     *         {@code BaseTransmission} object.
     * @throws AssertionError if the size of the initialized {@code ASN1EncodableVector}
     *                        does not match the expected value defined in {@code Map.START}.
     */
    public DERSequence encode(BaseTransmission item) {
        ASN1EncodableVector vect = initVector(item);
        assert vect.size() == Map.START : "Vector size is not as expected. "+vect.size()+" vs "+ Map.START;
        return new DERSequence(vect);
    }
}
