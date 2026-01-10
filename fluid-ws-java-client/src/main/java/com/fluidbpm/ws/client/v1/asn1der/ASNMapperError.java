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
    public ASNMapperError() {
        super(InitType.ALL);
    }

    /**
     * A static nested class that provides constant mappings for error components.
     * This class defines unique identifiers for different fields of an error
     * object, such as a code and a message, to facilitate mapping and encoding.
     *
     * The constants defined here are typically used in conjunction with the
     * parent class's functionality to process error objects in DER (Distinguished
     * Encoding Rules)-encoded format.
     *
     * Fields:
     * - START: Used to initialize the mapping sequence.
     * - CODE: Represents the unique identifier for error codes.
     * - MESSAGE: Represents the unique identifier for error messages.
     */
    public static class Map {
        public static int START = ASNBaseMapper.Map.CONTINUE;
        public static final int CODE = START++;;
        public static final int MESSAGE = START++;;
    }

    /**
     * Decodes an {@code ASN1Sequence} into an {@code Error} object by mapping the sequence
     * fields to the corresponding properties of the {@code Error} object.
     *
     * This method leverages the {@code popBaseFields} method to populate common fields and then
     * explicitly maps the error code and error message from the sequence.
     *
     * @param seq the {@code ASN1Sequence} containing the encoded error data.
     *            It is expected to have fields corresponding to {@code Map.CODE} and {@code Map.MESSAGE}.
     * @return an {@code Error} object populated with data from the provided {@code ASN1Sequence}.
     */
    public Error decode(ASN1Sequence seq) {
        Error returnVal = new Error();
        this.popBaseFields(returnVal, seq);

        returnVal.setErrorCode(asInt(seq.getObjectAt(Map.CODE), Error.JSONMapping.ERROR_CODE));
        returnVal.setErrorMessage(asUtf8(seq.getObjectAt(Map.MESSAGE), Error.JSONMapping.ERROR_MESSAGE));
        return returnVal;
    }

    /**
     * Decodes a DER-encoded byte array into an {@code Error} object.
     *
     * This method initializes an {@code ASN1Sequence} from the provided byte array
     * and then maps the sequence fields to populate the properties of the {@code Error} object.
     *
     * @param der the byte array containing the DER-encoded data to be decoded
     * @return an {@code Error} object populated with data extracted from the provided byte array
     * @throws com.fluidbpm.ws.client.FluidClientException if the byte array cannot be decoded into a valid {@code ASN1Sequence}
     */
    public Error decode(byte[] der) {
        ASN1Sequence seq = this.initSeq(der);
        return this.decode(seq);
    }

    /**
     * Converts the provided {@link ASN1Encodable} object to an integer value.
     * This method delegates the conversion logic to the superclass implementation.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted.
     *              Must be an instance of {@link ASN1Integer}.
     * @param field The name of the field being processed; used in error reporting
     *              if the conversion fails.
     * @return The integer value extracted from the {@link ASN1Encodable} instance.
     */
    @Override
    public int asInt(ASN1Encodable e, String field) {
        return super.asInt(e, field);
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
