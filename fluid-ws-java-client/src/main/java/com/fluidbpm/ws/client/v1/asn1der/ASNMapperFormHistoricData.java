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

import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperFormHistoricData.Map.*;

/**
 * The ASNMapperFormHistoricData class is responsible for encoding and decoding objects of type
 * {@code FormHistoricData} into ASN.1 DER-encoded sequences with specific tagged mappings. This
 * mapper extends the {@code ASNBaseTaggedMapper} class and specializes in handling
 * {@code FormHistoricData} instances, applying domain-specific logic for the serialization and
 * deserialization processes of tagged data objects.
 *
 * It provides mechanisms for:
 * - Creating new {@code FormHistoricData} instances for decoding purposes.
 * - Assigning tagged data to the appropriate fields of {@code FormHistoricData}.
 * - Encoding {@code FormHistoricData} objects into ASN.1 DER-compliant tagged structures.
 *
 * This mapper is typically employed in scenarios where standardized encoding
 * of {@code FormHistoricData}-related data is essential for interoperability or persistence.
 */
public class ASNMapperFormHistoricData extends ASNBaseTaggedMapper<FormHistoricData> {
    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} and provides a set of static constants
     * to represent field identifiers for mapping ASN.1 sequences specific to FormHistoricData objects.
     * These constants facilitate the identification and mapping of fields during encoding
     * and decoding operations between ASN.1 sequences and internal data structures.
     *
     * Field Identifiers:
     * - {@code DATE_AND_FIELD_NAME}: Identifier for the "dateAndFieldName" field.
     * - {@code DATE}: Identifier for the "date" field.
     * - {@code DATE_TIMESTAMP}: Identifier for the "dateTimestamp" field.
     * - {@code FORM_CONTAINER_FIELD_VALUES_JSON}: Identifier for the "formContainerFieldValuesJSON" field.
     * - {@code LOG_ENTRY_TYPE}: Identifier for the "logEntryType" field.
     * - {@code HISTORIC_ENTRY_TYPE}: Identifier for the "historicEntryType" field.
     * - {@code IS_FIELD_DIFFERENT_FROM_PREVIOUS}: Identifier for the "isFieldDifferentFromPrevious" field.
     * - {@code IS_FIELD_TYPE_SIGNATURE}: Identifier for the "isFieldTypeSignature" field.
     * - {@code IS_ESCAPE_TEXT}: Identifier for the "isEscapeText" field.
     * - {@code DESCRIPTION}: Identifier for the "description" field.
     * - {@code USER}: Identifier for the "user" field.
     * - {@code FIELD}: Identifier for the "field" field.
     * - {@code FORM_FOR_AUDIT_CREATE}: Identifier for the "formForAuditCreate" field.
     *
     * This class enables developers to work with tagged ASN.1 data by associating each
     * field in a FormHistoricData object with a unique constant, simplifying the mapping process.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int DATE_AND_FIELD_NAME = 1;
        public static final int DATE = 2;
        public static final int DATE_TIMESTAMP = 3;
        public static final int FORM_CONTAINER_FIELD_VALUES_JSON = 4;
        public static final int LOG_ENTRY_TYPE = 5;
        public static final int HISTORIC_ENTRY_TYPE = 6;
        public static final int IS_FIELD_DIFFERENT_FROM_PREVIOUS = 7;
        public static final int IS_FIELD_TYPE_SIGNATURE = 8;
        public static final int IS_ESCAPE_TEXT = 9;
        public static final int DESCRIPTION = 10;
        public static final int USER = 11;
        public static final int FIELD = 12;
        public static final int FORM_FOR_AUDIT_CREATE = 13;
    }

    private final ASNMapperUser asnMapUser;
    private final ASNMapperField asnMapField;
    private final ASNMapperForm asnMapForm;

    /**
     * Constructs an instance of the ASNMapperFormHistoricData class using the specified user, field,
     * form, and payload populate parameters.
     *
     * @param user an instance of {@code ASNMapperUser} representing the user-related
     *             mappings for ASN.1 encoding and decoding.
     * @param field an instance of {@code ASNMapperField} containing the field-related mappings
     *              for handling ASN.1 structured data.
     * @param form an instance of {@code ASNMapperForm} representing the form-related
     *             mappings for ASN.1 encoding and decoding.
     */
    public ASNMapperFormHistoricData(
            ASNMapperUser user,
            ASNMapperField field,
            ASNMapperForm form
    ) {
        super(InitType.ID_ONLY);
        this.asnMapUser = user;
        this.asnMapField = field;
        this.asnMapForm = form;
    }

    /**
     * Provides a supplier for creating new instances of the {@code FormHistoricData} class.
     * This method is typically used to generate fresh {@code FormHistoricData} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code FormHistoricData} instance.
     */
    @Override
    protected Supplier<FormHistoricData> supplierForInstance() {
        return FormHistoricData::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<FormHistoricData>} object to the appropriate attributes
     * of the associated {@code FormHistoricData} instance. The method processes the tag number and updates
     * the corresponding fields in {@code FormHistoricData} based on the tag's content.
     *
     * @param tag the {@code TagObj<FormHistoricData>} object that contains the tag number and data to be
     *            mapped to the {@code FormHistoricData} instance.
     *
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<FormHistoricData> tag) {
        assert tag != null : "TagObj cannot be null.";

        FormHistoricData toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case DATE_AND_FIELD_NAME:
                toPop.setDateAndFieldName(asUtf8(obj, FormHistoricData.JSONMapping.DATE_AND_FIELD_NAME));
                break;
            case DATE:
                toPop.setDate(asDate(obj, FormHistoricData.JSONMapping.DATE));
                break;
            case DATE_TIMESTAMP:
                toPop.setDateTimestamp(asLong(obj, FormHistoricData.JSONMapping.DATE));
                break;
            case FORM_CONTAINER_FIELD_VALUES_JSON:
                toPop.setFormContainerFieldValuesJSON(asGeneralTxt(obj, FormHistoricData.JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON));
                break;
            case LOG_ENTRY_TYPE:
                toPop.setLogEntryType(asGeneralTxt(obj, FormHistoricData.JSONMapping.LOG_ENTRY_TYPE));
                break;
            case HISTORIC_ENTRY_TYPE:
                toPop.setHistoricEntryType(asGeneralTxt(obj, FormHistoricData.JSONMapping.HISTORIC_ENTRY_TYPE));
                break;
            case IS_FIELD_DIFFERENT_FROM_PREVIOUS:
                toPop.setIsFieldDifferentFromPrevious(asBool(obj, FormHistoricData.JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS));
                break;
            case IS_FIELD_TYPE_SIGNATURE:
                toPop.setIsFieldTypeSignature(asBool(obj, FormHistoricData.JSONMapping.IS_FIELD_TYPE_SIGNATURE));
                break;
            case IS_ESCAPE_TEXT:
                toPop.setIsEscapeText(asBool(obj, FormHistoricData.JSONMapping.IS_ESCAPE_TEXT));
                break;
            case DESCRIPTION:
                toPop.setDescription(asGeneralTxt(obj, FormHistoricData.JSONMapping.DESCRIPTION));
                break;
            case USER:
                toPop.setUser(
                        this.asnMapUser.decode(asSeq(obj, FormHistoricData.JSONMapping.USER))
                );
                break;
            case FIELD:
                toPop.setField(
                        this.asnMapField.decode(asSeq(obj, FormHistoricData.JSONMapping.FIELD))
                );
                break;
            case FORM_FOR_AUDIT_CREATE:
                toPop.setFormForAuditCreate(
                        this.asnMapForm.decode(asSeq(obj, FormHistoricData.JSONMapping.FORM_FOR_AUDIT_CREATE))
                );
                break;
        }
        return null;
    }

    /**
     * Encodes the attributes of a {@code FormHistoricData} object as ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * Depending on the attributes present in the given {@code FormHistoricData} object,
     * this method creates tagged objects for fields like date, user, field, and form audit data,
     * and adds them to the provided vector.
     *
     * @param item   The {@code FormHistoricData} object to be encoded. Must not be null.
     * @param vect  The {@code ASN1EncodableVector} where the encoded tagged
     *              objects will be added. Must not be null.
     */
    @Override
    public void encodeTaggedObject(FormHistoricData item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getDateAndFieldName() != null) {
            vect.add(new DERTaggedObject(true, DATE_AND_FIELD_NAME, new DERUTF8String(item.getDateAndFieldName())));
        }

        if (item.getDate() != null) {
            vect.add(new DERTaggedObject(true, DATE, new ASN1GeneralizedTime(item.getDate())));
        }

        if (item.getDateTimestamp() != null) {
            vect.add(new DERTaggedObject(true, DATE_TIMESTAMP, new ASN1Integer(item.getDateTimestamp())));
        }

        if (item.getFormContainerFieldValuesJSON() != null) {
            vect.add(new DERTaggedObject(true, FORM_CONTAINER_FIELD_VALUES_JSON, new DERGeneralString(item.getFormContainerFieldValuesJSON())));
        }

        if (item.getLogEntryType() != null) {
            vect.add(new DERTaggedObject(true, LOG_ENTRY_TYPE, new DERGeneralString(item.getLogEntryType())));
        }

        if (item.getHistoricEntryType() != null) {
            vect.add(new DERTaggedObject(true, HISTORIC_ENTRY_TYPE, new DERGeneralString(item.getHistoricEntryType())));
        }

        if (item.getIsFieldDifferentFromPrevious() != null) {
            boolean boolPrim = item.getIsFieldDifferentFromPrevious();
            vect.add(new DERTaggedObject(true, IS_FIELD_DIFFERENT_FROM_PREVIOUS,
                    boolPrim ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
        }

        if (item.getIsFieldTypeSignature() != null) {
            boolean boolPrim = item.getIsFieldTypeSignature();
            vect.add(new DERTaggedObject(true, IS_FIELD_TYPE_SIGNATURE,
                    boolPrim ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
        }

        if (item.getIsEscapeText() != null) {
            boolean boolPrim = item.getIsEscapeText();
            vect.add(new DERTaggedObject(true, IS_ESCAPE_TEXT,
                    boolPrim ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
        }

        if (item.getDescription() != null) {
            vect.add(new DERTaggedObject(true, DESCRIPTION, new DERGeneralString(item.getDescription())));
        }

        if (item.getUser() != null) {
            vect.add(new DERTaggedObject(true, USER, this.asnMapUser.encode(item.getUser())));
        }

        if (item.getField() != null) {
            vect.add(new DERTaggedObject(true, FIELD, this.asnMapField.encode(item.getField())));
        }

        if (item.getFormForAuditCreate() != null) {
            vect.add(new DERTaggedObject(true, FORM_FOR_AUDIT_CREATE, this.asnMapForm.encode(item.getFormForAuditCreate())));
        }
    }

}
