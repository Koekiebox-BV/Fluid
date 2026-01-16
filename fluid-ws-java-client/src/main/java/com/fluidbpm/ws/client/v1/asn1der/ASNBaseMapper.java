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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Abstract base class responsible for encoding and decoding objects of type {@code T}
 * using ASN.1 (Abstract Syntax Notation One) encoding and decoding mechanisms.
 *
 * @param <T> the type of the object that extends {@code ABaseFluidVO}.
 */
@RequiredArgsConstructor
public abstract class ASNBaseMapper<T extends ABaseFluidVO> {
    protected final InitType initType;
    public enum InitType {
        NONE,
        ID_ONLY,
        ALL
    }

    /**
     * A helper class providing utility methods for handling null values by substituting them
     * with defined default values. This class is designed to ensure that the calling code
     * does not encounter unintended `null` values, which could lead to `NullPointerException`.
     */
    public static class DefWhenNull {
        public static final String TXT = UtilGlobal.EMPTY;
        public static final int ID = -1;
        public static final int NUMBER_INT = -1;

        /**
         * Returns a non-null string value by replacing a null input with a predefined default string.
         *
         * @param txt the input string to be checked for null
         * @return the input string if it is not null, otherwise a predefined default string
         */
        public static String nullSafeTxt(String txt) {
            return txt == null ? TXT : txt;
        }

        /**
         * Returns a non-null Long value by replacing a null input with a predefined default identifier.
         *
         * @param id the input Long value to be checked for null
         * @return the input Long value if it is not null, otherwise a predefined default identifier
         */
        public static Long nullSafeId(Long id) {
            return id == null ? ID : id;
        }

        /**
         * Returns a non-null Integer value by replacing a null input with a predefined default value.
         *
         * @param numb the input Integer to be checked for null
         * @return the input Integer if it is not null, otherwise a predefined default value
         */
        public static Integer nullSafeNumber(Integer numb) {
            return numb == null ? NUMBER_INT : numb;
        }
    }

    /**
     * The {@code Map} class defines a set of static constants that can be used as
     * field identifiers for mapping ASN.1 sequences. These identifiers can be used to
     * access and assign specific fields in the data structures modeled from the ASN.1 schema.
     *
     * The class contains a sequence of constants derived from an internal counter
     * that increments as new fields are defined. These constants serve as tags
     * for specific fields and facilitate encoding and decoding operations between
     * data structures and ASN.1 sequences.
     *
     * Fields:
     * - {@code ID}: Identifier for the "id" field in the ASN.1 sequence.
     * - {@code SERVICE_TICKET}: Identifier for the "serviceTicket" field in the ASN.1 sequence.
     * - {@code REQ_UUID}: Identifier for the "requestUuid" field in the ASN.1 sequence.
     * - {@code ECHO}: Identifier for the "echo" field in the ASN.1 sequence.
     * - {@code USER}: Identifier for the "loggedInUserFromTicket" field representing a user in the ASN.1 sequence.
     * - {@code CONTINUE}: Marker indicating the end of the current field definitions, useful for extending this
     *   sequence in subclasses or other contexts.
     *
     * The structure of this class makes it suitable for extending in other classes where additional mappings
     * may be required. By starting new mappings at the {@code CONTINUE} counter, derived classes can append
     * their own field identifiers while maintaining compatibility with the base class.
     */
    public static class Map {
        public static int START = 0;

        public static final int ID = START++;
        public static final int SERVICE_TICKET = START++;
        public static final int REQ_UUID = START++;
        public static final int ECHO = START++;
        public static final int USER = START++;

        public static final int CONTINUE = (START);
    }

    /**
     * Decodes a given DER-encoded byte array into an object of type {@code T}.
     *
     * @param der the DER-encoded byte array to be decoded.
     * @return the decoded object of type {@code T}.
     */
    public abstract T decode(byte[] der);

    /**
     * Encodes a given value object of type {@code T} into a {@code DERSequence}.
     *
     * @param vo the value object of type {@code T} that needs to be encoded.
     *           This object contains the data that is converted into a
     *           {@code DERSequence}.
     * @return a {@code DERSequence} representation of the provided value object.
     */
    public abstract DERSequence encode(T vo);

    /**
     * Populates the base fields of the provided value object with data extracted
     * from the given ASN.1 sequence.
     *
     * @param vo   The value object of type {@code T} into which the fields will be populated.
     * @param seq  The ASN1Sequence from which the data will be extracted and mapped to the fields of the value object.
     */
    protected void popBaseFields(T vo, ASN1Sequence seq) {
        vo.setId(asLong(seq.getObjectAt(ASNBaseMapper.Map.ID), ABaseFluidGSONObject.JSONMapping.ID));
        if (this.initType == ASNBaseTaggedMapper.InitType.ID_ONLY) return;

        vo.setServiceTicket(asGeneralTxt(seq.getObjectAt(ASNBaseMapper.Map.SERVICE_TICKET), ABaseFluidGSONObject.JSONMapping.SERVICE_TICKET));
        vo.setRequestUuid(asGeneralTxt(seq.getObjectAt(ASNBaseMapper.Map.REQ_UUID), ABaseFluidGSONObject.JSONMapping.REQUEST_UUID));
        vo.setEcho(asGeneralTxt(seq.getObjectAt(ASNBaseMapper.Map.ECHO), ABaseFluidGSONObject.JSONMapping.ECHO));

        ASN1Sequence seqUser = asSeq(seq.getObjectAt(ASNBaseMapper.Map.USER), ABaseFluidGSONObject.JSONMapping.LOGGED_IN_USER);
        vo.setLoggedInUserFromTicket(new User(
                asLong(seqUser.getObjectAt(ASNBaseMapper.Map.ID), ABaseFluidGSONObject.JSONMapping.ID)
        ));

        if (seqUser.size() > 1) {
            ASN1TaggedObject tagUsername =
                    asTagged(seqUser.getObjectAt(ASNMapperUser.Map.USERNAME), User.JSONMapping.USERNAME);
            vo.getLoggedInUserFromTicket().setUsername(asUtf8(tagUsername, User.JSONMapping.USERNAME));
        }

        assert vo.getId() != null : "Id is null!";
        assert vo.getServiceTicket() != null : "Id is null!";
        assert vo.getRequestUuid() != null : "Id is null!";
        assert vo.getEcho() != null : "Id is null!";

        assert vo.getLoggedInUserFromTicket() != null : "User object is null!";
        assert vo.getLoggedInUserFromTicket().getId() != null : "UserId object is null!";
    }

    /**
     * Initializes an ASN1EncodableVector by mapping the properties of the given {@code ABaseFluidVO} object
     * and associated user details into ASN.1 format.
     *
     * @param vo The {@code ABaseFluidVO} object containing data to be encoded into the vector.
     *           If {@code null}, an empty vector will be returned.
     * @return An ASN1EncodableVector populated with the encoded fields of the provided {@code ABaseFluidVO} object
     *         and its associated user details.
     */
    protected ASN1EncodableVector initVector(ABaseFluidVO vo) {
        ASN1EncodableVector vect = new ASN1EncodableVector();
        if (vo == null) return vect;

        vect.add(new ASN1Integer(DefWhenNull.nullSafeId(vo.getId())));
        if (this.initType == ASNBaseTaggedMapper.InitType.ID_ONLY) return vect;

        vect.add(new DERGeneralString(DefWhenNull.nullSafeTxt(vo.getServiceTicket())));
        vect.add(new DERGeneralString(DefWhenNull.nullSafeTxt(vo.getRequestUuid())));
        vect.add(new DERGeneralString(DefWhenNull.nullSafeTxt(vo.getEcho())));

        // User:
        User userToUse = vo.getLoggedInUserFromTicket();
        if (userToUse == null) userToUse = new User();
        vect.add(new ASNMapperUser().encode(userToUse));

        assert vect.size() == Map.START : "Init sequence size is not as expected.";
        return vect;
    }

    /**
     * Encodes an {@link ASN1Sequence} into a DER-encoded byte array.
     *
     * @param seq the ASN1Sequence to be encoded.
     * @return a byte array representing the DER-encoded ASN1Sequence.
     * @throws FluidClientException if an error occurs during the encoding process.
     */
    public static byte[] seqBytes(ASN1Sequence seq) {
        try {
            return seq.getEncoded(ASN1Encoding.DER);
        } catch (IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr, FluidClientException.ErrorCode.ASN_1_ERROR);
        }
    }

    /**
     * Initializes an ASN1Sequence by decoding the provided DER-encoded byte array.
     *
     * @param der the DER-encoded byte array to be decoded into an ASN1Sequence
     * @return the decoded ASN1Sequence
     * @throws FluidClientException if there is an error decoding the byte array or if
     *         the provided byte array is invalid
     */
    public ASN1Sequence initSeq(byte[] der) {
        try {
            return (ASN1Sequence)ASN1Primitive.fromByteArray(der);
        } catch (IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr,
                    FluidClientException.ErrorCode.ASN_1_ERROR);
        }
    }

    /**
     * Converts an {@link ASN1Encodable} object to an integer value, ensuring that
     * the provided object is of type {@link ASN1Integer}.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted.
     *              Must be an instance of {@link ASN1Integer}.
     * @param field The name of the field being processed, used in error reporting
     *              if the conversion is unsuccessful.
     * @return The integer value extracted from the {@link ASN1Integer} instance.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not
     *                              of type {@link ASN1Integer}.
     */
    protected int asInt(ASN1Encodable e, String field) {
        if (e instanceof ASN1Integer) {
            ASN1Integer i = (ASN1Integer) e;
            return i.getValue().intValueExact();
        }
        throw new FluidClientException("Field " + field + " expected INTEGER, got " + e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR);
    }

    /**
     * Converts the given ASN1Encodable object to a double value using the specified field name.
     *
     * @param e the ASN1Encodable object to be converted
     * @param field the name of the field that contains the value to be converted
     * @return the double value obtained from the specified field in the ASN1Encodable object
     */
    protected double asReal(ASN1Encodable e, String field) {
        String txt = asGeneralTxt(e, field);
        return new BigDecimal(txt).doubleValue();
    }

    /**
     * Converts the given ASN1Encodable object into an octet string if it is an instance
     * of ASN1OctetString. Throws an exception if the input object is not of the expected type.
     *
     * @param e the ASN1Encodable object to be converted.
     * @param field the name of the field being processed, used in error messaging.
     * @return a byte array representing the octet string if the conversion is successful.
     * @throws FluidClientException if the input object is not an instance of ASN1OctetString.
     */
    protected byte[] asOctetString(ASN1Encodable e, String field) {
        if (e instanceof ASN1OctetString) {
            ASN1OctetString o = (ASN1OctetString) e;
            return o.getOctets();
        }
        throw new FluidClientException("Field " + field + " expected REAL, got " + e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR);
    }

    /**
     * Converts the provided {@link ASN1Encodable} object to a long value, ensuring that
     * the given object is of type {@link ASN1Integer}.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted. Must be an instance
     *              of {@link ASN1Integer}.
     * @param field The name of the field being processed, used in error reporting if
     *              the conversion is unsuccessful.
     * @return The long value extracted from the {@link ASN1Integer} instance.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not of type
     *                              {@link ASN1Integer}.
     */
    protected long asLong(ASN1Encodable e, String field) {
        if (e instanceof ASN1Integer) {
            ASN1Integer i = (ASN1Integer) e;
            return i.getValue().longValueExact();
        }
        throw new FluidClientException("Field " + field + " expected INTEGER, got " + e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR);
    }

    /**
     * Converts the provided {@link ASN1Encodable} object into an {@link ASN1TaggedObject},
     * ensuring that the given object is of the correct type.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted. Must be an instance
     *              of {@link ASN1TaggedObject}.
     * @param field The name of the field being processed, used in error reporting
     *              if the conversion is unsuccessful.
     * @return The {@link ASN1TaggedObject} instance extracted from the provided {@link ASN1Encodable} object.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not of type {@link ASN1TaggedObject}.
     */
    protected ASN1TaggedObject asTagged(ASN1Encodable e, String field) {
        if (e instanceof ASN1TaggedObject) {
            return ASN1TaggedObject.getInstance(e);
        }
        throw new FluidClientException(
                "Field " + field + " expected ASN1TaggedObject, got " +
                        e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Converts the provided {@link ASN1Encodable} object to an {@link ASN1Sequence},
     * ensuring that the given object is of the correct type.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted. Must be an
     *              instance of {@link ASN1Sequence}.
     * @param field The name of the field being processed, used in error reporting
     *              if the conversion is unsuccessful.
     * @return The {@link ASN1Sequence} instance extracted from the provided
     *         {@link ASN1Encodable} object.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not
     *                              of type {@link ASN1Sequence}.
     */
    protected ASN1Sequence asSeq(ASN1Encodable e, String field) {
        if (e instanceof ASN1Sequence) {
            return ASN1Sequence.getInstance(e);
        }
        throw new FluidClientException(
                "Field " + field + " expected ASN1Sequence, got " +
                        e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Converts the provided {@link ASN1Encodable} object to a boolean value, ensuring that
     * the given object is of type {@link ASN1Boolean}.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted.
     *              Must be an instance of {@link ASN1Boolean}.
     * @param field The name of the field being processed, used in error reporting
     *              if the conversion is unsuccessful.
     * @return {@code true} if the ASN.1 boolean has a true value, {@code false} otherwise.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not of
     *                              type {@link ASN1Boolean}.
     */
    protected boolean asBool(ASN1Encodable e, String field) {
        if (e instanceof ASN1Boolean) {
            ASN1Boolean b = (ASN1Boolean) e;
            return b.isTrue();
        }
        throw new FluidClientException(
                "Field " + field + " expected BOOLEAN, got " +
                e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Converts a given {@link ASN1TaggedObject} into a UTF-8 encoded string.
     * The method checks the underlying object of the {@link ASN1TaggedObject}
     * and extracts its string value if it is of type {@link DERUTF8String}
     * or {@link ASN1String}.
     *
     * @param e     The {@link ASN1TaggedObject} instance to be converted.
     * @param field The name of the field being processed, used in error
     *              reporting if the conversion is unsuccessful.
     * @return The UTF-8 encoded string representation extracted from the
     *         provided {@link ASN1TaggedObject}.
     * @throws FluidClientException if the underlying object of the {@link ASN1TaggedObject}
     *                              is not of type {@link DERUTF8String} or {@link ASN1String}.
     */
    protected String asUtf8(ASN1TaggedObject e, String field) {
        return asUtf8(e.getBaseObject(), field);
    }

    /**
     * Converts the provided {@link ASN1Encodable} object to a UTF-8 encoded string.
     * The method checks the type of the ASN.1 object and extracts its string value
     * if it is of type {@link DERUTF8String} or {@link ASN1String}.
     *
     * @param e     The {@link ASN1Encodable} instance to be converted. Must be either
     *              an instance of {@link DERUTF8String} or {@link ASN1String}.
     * @param field The name of the field being processed, used in error reporting
     *              if the conversion is unsuccessful.
     * @return The UTF-8 encoded string representation extracted from the
     *         provided {@link ASN1Encodable}.
     * @throws FluidClientException if the provided {@link ASN1Encodable} is not of
     *                              type {@link DERUTF8String} or {@link ASN1String}.
     */
    protected String asUtf8(ASN1Encodable e, String field) {
        if (e instanceof DERUTF8String) {
            DERUTF8String s = (DERUTF8String) e;
            return s.getString();
        } else if (e instanceof ASN1String) {
            ASN1String s = (ASN1String) e;
            return s.getString();
        }
        throw new FluidClientException(
                "Field " + field + " expected TEXT(utf-8), got " +
                        e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Converts the given ASN1Encodable object to a general text representation
     * if it is an instance of ASN1GeneralString. Throws an exception if the
     * object is not of the expected type.
     *
     * @param e the ASN1Encodable object to be converted
     * @param field the name of the field being processed, used for error reporting
     * @return the string representation of the ASN1GeneralString if conversion is successful
     * @throws FluidClientException if the given ASN1Encodable is not an instance of ASN1GeneralString
     */
    protected String asGeneralTxt(ASN1Encodable e, String field) {
        if (e instanceof ASN1GeneralString) {
            ASN1GeneralString s = (ASN1GeneralString) e;
            return s.getString();
        }
        throw new FluidClientException(
                "Field " + field + " expected TEXT(general), got " +
                        e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Converts an ASN1Encodable object to a Date if it is of type ASN1GeneralizedTime.
     *
     * @param e the ASN1Encodable object to be converted
     * @param field the field name used for error reporting
     * @return the Date representation of the ASN1GeneralizedTime object
     * @throws FluidClientException if the ASN1Encodable object is not of type ASN1GeneralizedTime
     *                              or if the date format is invalid
     */
    protected Date asDate(ASN1Encodable e, String field) {
        if (e instanceof ASN1GeneralizedTime) {
            ASN1GeneralizedTime t = ((ASN1GeneralizedTime) e);
            try {
                return t.getDate();
            } catch (ParseException parseErr) {
                throw new FluidClientException(
                        "Field " + field + " has invalid date format("+t.getTimeString()+"): " + parseErr.getMessage(),
                        FluidClientException.ErrorCode.ASN_1_ERROR
                );
            }
        }
        throw new FluidClientException(
                "Field " + field + " expected GeneralizedTime, got " +
                        e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR
        );
    }

    /**
     * Populates the provided ASN1EncodableVector with a DERTaggedObject containing
     * the encoded representations of the elements in the provided list.
     *
     * @param list the list of elements to be encoded and added to the vector
     * @param primVector the ASN1EncodableVector where the DERTaggedObject will be added
     * @param index the tag number used to create the DERTaggedObject
     */
    protected void setAsList(List<T> list, ASN1EncodableVector primVector, int index) {
        if (list != null && !list.isEmpty()) {
            ASN1EncodableVector fieldsVect = new ASN1EncodableVector();
            for (T field : list) fieldsVect.add(this.encode(field));
            primVector.add(new DERTaggedObject(true, index, new DERSequence(fieldsVect)));
        }
    }
}
