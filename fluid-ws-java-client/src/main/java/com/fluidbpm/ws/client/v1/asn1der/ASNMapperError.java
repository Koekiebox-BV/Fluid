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

import com.fluidbpm.program.api.vo.ws.Error;
import org.bouncycastle.asn1.*;

/**
 * The ASNMapperError class is responsible for encoding and decoding
 * objects of type {@code Error} to and from ASN.1 format. It extends
 * the {@code ASNBaseMapper} class and provides specific implementations
 * for mapping error-related data.
 *
 * This class includes an inner static {@code Map} class, which defines
 * a set of constants used to map and access specific fields in the ASN.1 sequence.
 * The constants are used to identify positional indexes for error-related
 * fields like {@code CODE} and {@code MESSAGE}.
 *
 * Key methods provided by this class:
 * - {@code decode(byte[] der)}: Decodes an ASN.1 DER-encoded byte array into
 *   an {@code Error} object. The method parses the provided sequence, retrieves
 *   relevant fields, and sets the corresponding properties of the {@code Error} object.
 * - {@code encode(Error item)}: Encodes an {@code Error} object into an ASN.1 DER
 *   sequence. This method aggregates the {@code Error} fields into an encodable
 *   vector and returns the result as a {@code DERSequence}.
 *
 * Usage of this class assumes that the structure of the {@code Error} object
 * aligns with the mapping logic in both the encode and decode methods.
 */
public class ASNMapperError extends ASNBaseMapper<Error> {
    public static class Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;
        public static final int CODE = START++;;
        public static final int MESSAGE = START++;;
    }

    /**
     * Decodes a DER-encoded byte array into an {@code Error} object.
     * The method parses the given ASN.1 sequence, retrieves relevant fields
     * such as error code and error message, and sets them on a new {@code Error} object.
     *
     * @param der the byte array containing the DER-encoded representation of an {@code Error} object
     * @return a populated {@code Error} object with fields decoded from the ASN.1 sequence
     * @throws com.fluidbpm.ws.client.FluidClientException if the provided byte array is invalid or cannot be parsed into an ASN.1 sequence
     */
    public Error decode(byte[] der) {
        ASN1Sequence seq = this.initSeq(der);
        Error returnVal = new Error();
        this.popBaseFields(returnVal, seq);

        returnVal.setErrorCode(asInt(seq.getObjectAt(Map.CODE), Error.JSONMapping.ERROR_CODE));
        returnVal.setErrorMessage(asUtf8(seq.getObjectAt(Map.MESSAGE), Error.JSONMapping.ERROR_MESSAGE));
        return returnVal;
    }

    /**
     * Encodes an {@code Error} object into an ASN.1 DER sequence.
     * This method maps the properties of the provided {@code Error} object
     * (such as error code and error message) into an ASN.1 encodable vector
     * and returns it as a {@code DERSequence}.
     *
     * @param item the {@code Error} object to encode; must contain an error code and message
     *             to be included in the DER sequence
     * @return a {@code DERSequence} representing the encoded {@code Error} object
     * @throws IllegalArgumentException if the vector size does not match the expected size
     */
    public DERSequence encode(Error item) {
        ASN1EncodableVector vect = initVector(item);
        vect.add(new ASN1Integer(item.getErrorCode()));
        vect.add(new DERUTF8String(DefWhenNull.nullSafeTxt(item.getErrorMessage())));

        assert vect.size() == Map.START : "Vector size is not as expected. "+vect.size()+" vs "+Map.MESSAGE;

        return new DERSequence(vect);
    }
}
