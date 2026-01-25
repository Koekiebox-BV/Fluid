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

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.user.User;
import org.bouncycastle.asn1.*;

/**
 * The ASNMapperUser class provides functionality for encoding and decoding
 * User objects to and from ASN.1 DER format using predefined mappings.
 * It extends the ASNBaseMapper class and specializes in handling User object-related
 * transformations.
 *
 * This class ensures that the User's attributes, such as ID and username,
 * are correctly serialized and deserialized according to the specified ASN.1 structure.
 */
public class ASNMapperUser extends ASNBaseMapper<User> {
    public static class Map {
        public static final int ID = 0;
        public static final int USERNAME = 1;
    }

    public ASNMapperUser() {
        super(InitType.NONE);
    }

    /**
     * Decodes an ASN.1 DER-encoded byte array into a {@code User} object.
     * The method uses predefined mappings to parse and map the DER-encoded data.
     *
     * @param der the byte array containing the ASN.1 DER-encoded representation of a User.
     * @return a {@code User} object reconstructed from the provided DER-encoded data.
     */
    public final User decode(byte[] der) {
        return this.decode(this.initSeq(der));
    }

    /**
     * Decodes an ASN1Sequence into a {@code User} object. This method parses the sequence using
     * predefined mappings to extract and set the corresponding attributes of the User object.
     *
     * @param seq the ASN1Sequence containing the encoded representation of a User.
     * @return a {@code User} object reconstructed from the provided ASN1Sequence.
     */
    public final User decode(ASN1Sequence seq) {
        User returnVal = new User();
        returnVal.setId(asLong(seq.getObjectAt(ASNMapperUser.Map.ID), ABaseFluidGSONObject.JSONMapping.ID));

        if (seq.size() > 1) {
            ASN1TaggedObject tagUsername =
                    asTagged(seq.getObjectAt(ASNMapperUser.Map.USERNAME), User.JSONMapping.USERNAME);
            returnVal.setUsername(asUtf8(tagUsername, User.JSONMapping.USERNAME));
        }
        return returnVal;
    }

    /**
     * Encodes a {@code User} object into an ASN.1 DER sequence. The method serializes
     * the User's attributes into an ASN.1 format for standardized data representation.
     *
     * @param item the {@code User} object to be encoded. If the User's ID is {@code null},
     *             the method encodes the ID as {@code -1}. If the User's username is
     *             {@code null}, it is omitted from the encoding.
     * @return a {@code DERSequence} representing the ASN.1 encoded form of the provided {@code User} object.
     */
    public DERSequence encode(User item) {
        ASN1EncodableVector vectUser = new ASN1EncodableVector();
        vectUser.add(new ASN1Integer(DefWhenNull.nullSafeId(item.getId())));

        if (item.getUsername() != null) {
            vectUser.add(new DERTaggedObject(true, ASNMapperUser.Map.USERNAME, new DERUTF8String(item.getUsername())));
        }

        assert vectUser.size() > 0 && vectUser.size() < 3 : "Init User sequence size is not as expected.";

        return new DERSequence(vectUser);
    }
}
