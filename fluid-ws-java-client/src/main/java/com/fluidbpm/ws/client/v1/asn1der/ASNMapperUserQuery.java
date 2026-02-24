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

import com.fluidbpm.program.api.vo.userquery.UserQuery;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * This class represents a mapper for the `UserQuery` object to and from ASN.1 encoded format.
 * It extends the functionality of the {@code ASNBaseTaggedMapper} and performs custom mapping
 * for the `UserQuery` object, including decoding tagged ASN.1 objects into {@code UserQuery}
 * instances and encoding {@code UserQuery} instances into ASN.1 tagged representations.
 */
public class ASNMapperUserQuery extends ASNBaseTaggedMapper<UserQuery> {
    private final ASNMapperField asnMapField;

    /**
     * The {@code Map} class provides a collection of static constants representing field identifiers
     * that can be used in the mapping of ASN.1 sequences or structures with specific fields
     * associated with a {@code UserQuery} instance.
     *
     * Fields:
     * - {@code NAME}: Identifier for the "name" field.
     * - {@code DESCRIPTION}: Identifier for the "description" field.
     * - {@code RULES}: Identifier for the "rules" field.
     * - {@code INPUTS}: Identifier for the "inputs" field.
     * - {@code DATE_CREATED}: Identifier for the "dateCreated" field.
     * - {@code DATE_LAST_UPDATED}: Identifier for the "dateLastUpdated" field.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int NAME = 1;
        public static final int DESCRIPTION = 2;
        public static final int RULES = 3;
        public static final int INPUTS = 4;
        public static final int DATE_CREATED = 5;
        public static final int DATE_LAST_UPDATED = 6;
    }

    public ASNMapperUserQuery(ASNMapperField fieldMapper) {
        super(InitType.ID_ONLY);
        this.asnMapField = fieldMapper;
    }

    /**
     * Provides a supplier for creating new instances of the {@code UserQuery} class.
     *
     * @return a {@code Supplier} that supplies new instances of {@code UserQuery}.
     */
    @Override
    protected Supplier<UserQuery> supplierForInstance() {
        return UserQuery::new;
    }

    /**
     * Maps a decoded tagged object to populate fields of a {@code UserQuery} instance based on the tag number.
     *
     * @param tag the {@code TagObj} containing the tag number, the associated {@code ASN1Object},
     *            and the {@code UserQuery} instance to populate.
     * @return always returns {@code null}.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<UserQuery> tag) {
        assert tag != null : "Arguments cannot be null.";

        UserQuery toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.NAME:
                toPop.setName(asGeneralTxt(obj, UserQuery.JSONMapping.NAME));
                break;
            case Map.DESCRIPTION:
                toPop.setDescription(asGeneralTxt(obj, UserQuery.JSONMapping.DESCRIPTION));
                break;
            case Map.RULES:
                toPop.setRules(
                        this.decodeAsGeneralStringList(
                                asSeq(obj, UserQuery.JSONMapping.RULES), UserQuery.JSONMapping.RULES
                        )
                );
                break;
            case Map.INPUTS:
                toPop.setInputs(
                        asnMapField.decodeAsList(asSeq(obj, UserQuery.JSONMapping.INPUTS), UserQuery.JSONMapping.INPUTS)
                );
                break;
            case Map.DATE_CREATED:
                toPop.setDateCreated(asDate(obj, UserQuery.JSONMapping.DATE_CREATED));
                break;
            case Map.DATE_LAST_UPDATED:
                toPop.setDateLastUpdated(asDate(obj, UserQuery.JSONMapping.DATE_LAST_UPDATED));
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
    public void encodeTaggedObject(UserQuery item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getName() != null) {
            vect.add(new DERTaggedObject(true, Map.NAME, new DERGeneralString(item.getName())));
        }

        if (item.getDescription() != null) {
            vect.add(new DERTaggedObject(true, Map.DESCRIPTION, new DERGeneralString(item.getDescription())));
        }

        if (item.getInputs() != null && !item.getInputs().isEmpty()) {
            this.asnMapField.setAsList(item.getInputs(), vect, Map.INPUTS);
        }

        if (item.getRules() != null && !item.getRules().isEmpty()) {
            this.setAsGeneralStringList(item.getRules(), vect, Map.RULES);
        }

        if (item.getDateCreated() != null) {
            vect.add(new DERTaggedObject(true, Map.DATE_CREATED, new ASN1GeneralizedTime(item.getDateCreated())));
        }

        if (item.getDateLastUpdated() != null) {
            vect.add(new DERTaggedObject(true, Map.DATE_LAST_UPDATED, new ASN1GeneralizedTime(item.getDateLastUpdated())));
        }
    }
}
