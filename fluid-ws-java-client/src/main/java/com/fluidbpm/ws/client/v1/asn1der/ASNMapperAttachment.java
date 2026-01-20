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

import com.fluidbpm.program.api.vo.attachment.Attachment;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * This class represents a mapper for the `Attachment` object to and from ASN.1 encoded format.
 * It extends the functionality of the {@code ASNBaseTaggedMapper} and performs custom mapping
 * for the `Attachment` object, including decoding tagged ASN.1 objects into {@code Attachment}
 * instances and encoding {@code Attachment} instances into ASN.1 tagged representations.
 */
public class ASNMapperAttachment extends ASNBaseTaggedMapper<Attachment> {
    /**
     * The {@code Map} class provides a collection of static constants representing field identifiers
     * that can be used in the mapping of ASN.1 sequences or structures with specific fields
     * associated with an {@code Attachment} instance. These constants serve as tags facilitating
     * encoding and decoding operations.
     *
     * This class extends the {@code ASNBaseMapper.Map} class, inheriting its mapping capabilities
     * and defining additional field constants relevant to the {@code Attachment} domain. Each constant
     * corresponds to a unique field within the {@code Attachment} model, allowing efficient field
     * identification and manipulation.
     *
     * Fields:
     * - {@code NAME}: Identifier for the "name" field.
     * - {@code VERSION}: Identifier for the "version" field.
     * - {@code PATH}: Identifier for the "path" field.
     * - {@code CONTENT_TYPE}: Identifier for the "contentType" field.
     * - {@code DATE_LAST_UPDATED}: Identifier for the "dateLastUpdated" field.
     * - {@code DATE_CREATED}: Identifier for the "dateCreated" field.
     * - {@code ATTACHMENT_DATA}: Identifier for the "attachmentData" field.
     * - {@code FORM_ID}: Identifier for the "formId" field.
     *
     * These constants support extensibility and consistency in operations involving ASN.1
     * encoded data. By associating specific fields in the {@code Attachment} with unique
     * tag identifiers, the class simplifies encoding, decoding, and data transformation activities.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int NAME = 1;
        public static final int VERSION = 2;
        public static final int PATH = 3;
        public static final int CONTENT_TYPE = 4;
        public static final int DATE_LAST_UPDATED = 5;
        public static final int DATE_CREATED = 6;
        public static final int ATTACHMENT_DATA = 7;
        public static final int FORM_ID = 8;
    }

    public ASNMapperAttachment() {
        super(InitType.ID_ONLY);
    }

    /**
     * Provides a supplier for creating new instances of the {@code Attachment} class.
     *
     * @return a {@code Supplier} that supplies new instances of {@code Attachment}.
     */
    @Override
    protected Supplier<Attachment> supplierForInstance() {
        return Attachment::new;
    }

    /**
     * Maps a decoded tagged object to populate fields of an {@code Attachment} instance based on the tag number.
     * The method processes the provided tag and assigns values to the corresponding properties of the {@code Attachment}.
     *
     * @param tag the {@code TagObj} containing the tag number, the associated {@code ASN1Object},
     *            and the {@code Attachment} instance to populate. It must not be null,
     *            and its components (the {@code TagObj} instance itself, the {@code Attachment},
     *            and the {@code ASN1Object}) must not be null.
     * @return always returns {@code null}.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<Attachment> tag) {
        assert tag != null : "Arguments cannot be null.";

        Attachment toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.NAME:
                toPop.setName(asGeneralTxt(obj, Attachment.JSONMapping.NAME));
                break;
            case Map.VERSION:
                toPop.setVersion(asGeneralTxt(obj, Attachment.JSONMapping.VERSION));
                break;
            case Map.PATH:
                toPop.setPath(asGeneralTxt(obj, Attachment.JSONMapping.PATH));
                break;
            case Map.CONTENT_TYPE:
                toPop.setContentType(asGeneralTxt(obj, Attachment.JSONMapping.CONTENT_TYPE));
                break;
            case Map.DATE_LAST_UPDATED:
                toPop.setDateLastUpdated(asDate(obj, Attachment.JSONMapping.DATE_LAST_UPDATED));
                break;
            case Map.DATE_CREATED:
                toPop.setDateCreated(asDate(obj, Attachment.JSONMapping.DATE_CREATED));
                break;
            case Map.ATTACHMENT_DATA:
                toPop.setAttachmentData(asOctetString(obj, Attachment.JSONMapping.ATTACHMENT_DATA_BASE64));
                break;
            case Map.FORM_ID:
                toPop.setFormId(asLong(obj, Attachment.JSONMapping.FORM_ID));
                break;
        }
        return null;
    }

    /**
     * Encodes the properties of the provided {@code Attachment} instance into a series of ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * Each non-null property of the {@code Attachment} instance is mapped to a corresponding tagged object,
     * with the associated tag defined by the {@code Map} constants. The method ensures that the values of
     * these properties are converted into appropriate ASN.1 encodable types.
     *
     * @param item the {@code Attachment} object containing properties to encode. It must not be {@code null}.
     * @param vect the {@code ASN1EncodableVector} to which the encoded tagged objects are added. It must not be {@code null}.
     *             The vector must already contain at least one element before invoking this method.
     */
    @Override
    public void encodeTaggedObject(Attachment item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getName() != null) {
            vect.add(new DERTaggedObject(true, Map.NAME, new DERGeneralString(item.getName())));
        }

        if (item.getVersion() != null) {
            vect.add(new DERTaggedObject(true, Map.VERSION, new DERGeneralString(item.getVersion())));
        }

        if (item.getPath() != null) {
            vect.add(new DERTaggedObject(true, Map.PATH, new DERGeneralString(item.getPath())));
        }

        if (item.getContentType() != null) {
            vect.add(new DERTaggedObject(true, Map.CONTENT_TYPE, new DERGeneralString(item.getContentType())));
        }

        if (item.getDateLastUpdated() != null) {
            vect.add(new DERTaggedObject(true, Map.DATE_LAST_UPDATED, new ASN1GeneralizedTime(item.getDateLastUpdated())));
        }

        if (item.getDateCreated() != null) {
            vect.add(new DERTaggedObject(true, Map.DATE_CREATED, new ASN1GeneralizedTime(item.getDateCreated())));
        }

        if (item.getAttachmentData() != null && item.getAttachmentData().length > 0) {
            vect.add(new DERTaggedObject(true, Map.ATTACHMENT_DATA, new DEROctetString(item.getAttachmentData())));
        }

        if (item.getFormId() != null) {
            vect.add(new DERTaggedObject(true, Map.FORM_ID, new ASN1Integer(item.getFormId())));
        }
    }
}
