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

import com.fluidbpm.program.api.vo.role.Role;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * The {@code ASNMapperRole} class is a specialized implementation of {@code ASNBaseTaggedMapper<Role>}
 * designed to map ASN.1 sequences or structures to and from {@code Role} entities. It provides
 * functionality to encode and decode {@code Role} objects into ASN.1 representations and vice versa.
 *
 * This class primarily serves in environments where serialization and deserialization of
 * {@code Role}-related data using the ASN.1 standard is required.
 */
public class ASNMapperRole extends ASNBaseTaggedMapper<Role> {

    /**
     * The Map class extends the ASNBaseMapper.Map class and provides a set of static constants
     * that are used as field identifiers for specific mappings. These identifiers are commonly
     * used in the context of ASN.1 sequence processing for tagging and retrieving values.
     *
     * Constants:
     * - NAME: Identifier for the "name" field.
     * - DESCRIPTION: Identifier for the "description" field.
     * - ADMIN_PERMISSIONS: Identifier for the "adminPermissions" field.
     * - CUSTOM_PERMISSIONS: Identifier for the "customPermissions" field.
     *
     * This class is designed to work within the hierarchy of ASNBaseMapper.Map,
     * allowing for extension and compatibility with derived classes or additional mappings.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int NAME = 1;
        public static final int DESCRIPTION = 2;
        public static final int ADMIN_PERMISSIONS = 3;
        public static final int CUSTOM_PERMISSIONS = 4;
    }

    public ASNMapperRole() {
        super(InitType.ID_ONLY);
    }

    /**
     * Provides a supplier for creating new instances of the {@code Role} class.
     *
     * @return a {@code Supplier} that supplies new instances of {@code Role}.
     */
    @Override
    protected Supplier<Role> supplierForInstance() {
        return Role::new;
    }

    /**
     * Maps a decoded tagged object to populate fields of a {@code Role} instance based on the tag number.
     *
     * @param tag the {@code TagObj} containing the tag number, the associated {@code ASN1Object},
     *            and the {@code Role} instance to populate.
     * @return always returns {@code null}.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<Role> tag) {
        assert tag != null : "Arguments cannot be null.";

        Role toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.NAME:
                toPop.setName(asGeneralTxt(obj, Role.JSONMapping.NAME));
                break;
            case Map.DESCRIPTION:
                toPop.setDescription(asGeneralTxt(obj, Role.JSONMapping.DESCRIPTION));
                break;
            case Map.ADMIN_PERMISSIONS:
                toPop.setAdminPermissions(this.decodeAsGeneralStringList(
                                asSeq(obj, Role.JSONMapping.ADMIN_PERMISSIONS), Role.JSONMapping.ADMIN_PERMISSIONS
                        )
                );
                break;
            case Map.CUSTOM_PERMISSIONS:
                toPop.setCustomPermissions(this.decodeAsGeneralStringList(
                                asSeq(obj, Role.JSONMapping.CUSTOM_PERMISSIONS), Role.JSONMapping.CUSTOM_PERMISSIONS
                        )
                );
                break;
        }
        return null;
    }

    /**
     * Encodes the properties of the provided {@code UserQuery} instance into a series of ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * @param item the {@code UserQuery} object containing properties to encode.
     * @param vect the {@code ASN1EncodableVector} to which the encoded tagged objects are added.
     */
    @Override
    public void encodeTaggedObject(Role item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getName() != null) {
            vect.add(new DERTaggedObject(true, Map.NAME, new DERGeneralString(item.getName())));
        }

        if (item.getDescription() != null) {
            vect.add(new DERTaggedObject(true, Map.DESCRIPTION, new DERGeneralString(item.getDescription())));
        }

        if (item.getAdminPermissions() != null && !item.getAdminPermissions().isEmpty()) {
            this.setAsGeneralStringList(item.getAdminPermissions(), vect, Map.ADMIN_PERMISSIONS);
        }

        if (item.getCustomPermissions() != null && !item.getCustomPermissions().isEmpty()) {
            this.setAsGeneralStringList(item.getCustomPermissions(), vect, Map.CUSTOM_PERMISSIONS);
        }
    }
}
