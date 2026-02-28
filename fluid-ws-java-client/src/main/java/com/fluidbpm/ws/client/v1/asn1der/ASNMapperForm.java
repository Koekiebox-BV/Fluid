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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperForm.Map.*;

/**
 * The ASNMapperForm class is responsible for encoding and decoding objects of type
 * {@code Form} into ASN.1 DER-encoded sequences with specific tagged mappings. This
 * mapper extends the {@code ASNBaseTaggedMapper} class and specializes in handling
 * {@code Form} instances, applying domain-specific logic for the serialization and
 * deserialization processes of tagged data objects.
 *
 * It provides mechanisms for:
 * - Creating new {@code Form} instances for decoding purposes.
 * - Assigning tagged data to the appropriate fields of {@code Form}.
 * - Encoding {@code Form} objects into ASN.1 DER-compliant tagged structures.
 *
 * This mapper is typically employed in scenarios where standardized encoding
 * of {@code Form}-related data is essential for interoperability or persistence.
 */
public class ASNMapperForm extends ASNBaseTaggedMapper<Form> {
    private final ASNMapperUser asnMapUser;
    private final ASNMapperField asnMapField;

    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} and provides a set of static constants
     * to represent field identifiers for mapping ASN.1 sequences specific to Form objects.
     * These constants facilitate the identification and mapping of fields during encoding
     * and decoding operations between ASN.1 sequences and internal data structures.
     *
     * Field Identifiers:
     * - {@code FORM_TYPE}: Identifier for the "formType" field.
     * - {@code FORM_TYPE_ID}: Identifier for the "formTypeId" field.
     * - {@code TITLE}: Identifier for the "title" field.
     * - {@code FLOW_STATE}: Identifier for the "flowState" field.
     * - {@code STATE}: Identifier for the "state" field.
     * - {@code CURRENT_USER}: Identifier for the "currentUser" field.
     * - {@code DATE_CREATED}: Identifier for the "dateCreated" field.
     * - {@code DATE_LAST_UPDATED}: Identifier for the "dateLastUpdated" field.
     * - {@code FORM_FIELDS}: Identifier for the "formFields" field.
     *
     * This class enables developers to work with tagged ASN.1 data by associating each
     * field in a Form object with a unique constant, simplifying the mapping process.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int FORM_TYPE = 1;
        public static final int FORM_TYPE_ID = 2;
        public static final int TITLE = 3;
        public static final int FLOW_STATE = 4;
        public static final int STATE = 5;
        public static final int CURRENT_USER = 6;

        public static final int DATE_CREATED = 7;
        public static final int DATE_LAST_UPDATED = 8;
        public static final int FORM_FIELDS = 9;
    }

    /**
     * Constructs an instance of the ASNMapperForm class using the specified user and field
     * parameters.
     *
     * @param user an instance of {@code ASNMapperUser} representing the user-related
     *             mappings for ASN.1 encoding and decoding.
     * @param field an instance of {@code ASNMapperField} containing the field-related mappings
     *              for handling ASN.1 structured data.
     */
    public ASNMapperForm(ASNMapperUser user, ASNMapperField field) {
        super(InitType.ID_ONLY);
        this.asnMapUser = user;
        this.asnMapField = field;
    }

    /**
     * Constructs an instance of the ASNMapperForm class using the specified {@code PayloadPopulate} object.
     * This constructor initializes the {@code ASNMapperForm} with default instances of {@code ASNMapperUser}
     * and a new {@code ASNMapperField} initialized with the provided {@code PayloadPopulate}.
     *
     * @param payloadPopulate an instance of {@code PayloadPopulate} used to initialize the {@code ASNMapperField}.
     */
    public ASNMapperForm(PayloadPopulate payloadPopulate) {
        this(new ASNMapperUser(), new ASNMapperField(payloadPopulate));
    }

    /**
     * Provides a supplier for creating new instances of the {@code Form} class.
     * This method is typically used to generate fresh {@code Form} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code Form} instance.
     */
    @Override
    protected Supplier<Form> supplierForInstance() {
        return Form::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<Form>} object to the appropriate attributes
     * of the associated {@code Form} instance. The method processes the tag number and updates
     * the corresponding fields in {@code Form} based on the tag's content.
     *
     * @param tag the {@code TagObj<Form>} object that contains the tag number and data to be
     *            mapped to the {@code Form} instance.
     *
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<Form> tag) {
        assert tag != null : "TagObj cannot be null.";

        Form toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case FORM_TYPE:
                toPop.setFormType(asUtf8(obj, Form.JSONMapping.FORM_TYPE));
                break;
            case FORM_TYPE_ID:
                toPop.setFormTypeId(asLong(obj, Form.JSONMapping.FORM_TYPE_ID));
                break;
            case TITLE:
                toPop.setTitle(asUtf8(obj, Form.JSONMapping.TITLE));
                break;
            case FLOW_STATE:
                toPop.setFlowState(asGeneralTxt(obj, Form.JSONMapping.FLOW_STATE));
                break;
            case STATE:
                toPop.setState(asGeneralTxt(obj, Form.JSONMapping.STATE));
                break;
            case CURRENT_USER:
                toPop.setCurrentUser(
                        this.asnMapUser.decode(asSeq(obj, Form.JSONMapping.CURRENT_USER))
                );
                break;
            case DATE_CREATED:
                toPop.setDateCreated(asDate(obj, Form.JSONMapping.DATE_CREATED));
                break;
            case DATE_LAST_UPDATED:
                toPop.setDateLastUpdated(asDate(obj, Form.JSONMapping.DATE_LAST_UPDATED));
                break;
            case FORM_FIELDS:
                toPop.setFormFields(
                        this.asnMapField.decodeAsList(asSeq(obj, Form.JSONMapping.FORM_FIELDS), Form.JSONMapping.FORM_FIELDS)
                );
                break;
        }
        return null;
    }

    /**
     * Encodes the attributes of a {@code Form} object as ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * Depending on the attributes present in the given {@code Form} object,
     * this method creates tagged objects for fields like form type, form type ID,
     * title, and date created, and adds them to the provided vector.
     *
     * @param item   The {@code Form} object to be encoded. Must not be null.
     * @param vect  The {@code ASN1EncodableVector} where the encoded tagged
     *              objects will be added. Must not be null.
     */
    @Override
    public void encodeTaggedObject(Form item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getFormType() != null) {
            vect.add(new DERTaggedObject(true, FORM_TYPE, new DERUTF8String(item.getFormType())));
        }

        Long formTypeId = item.getFormTypeId();
        if (formTypeId != null) {
            vect.add(new DERTaggedObject(true, FORM_TYPE_ID, new ASN1Integer(formTypeId)));
        }

        if (item.getTitle() != null) {
            vect.add(new DERTaggedObject(true, TITLE, new DERUTF8String(item.getTitle())));
        }

        if (item.getFlowState() != null) {
            vect.add(new DERTaggedObject(true, FLOW_STATE, new DERGeneralString(item.getFlowState())));
        }

        if (item.getState() != null) {
            vect.add(new DERTaggedObject(true, STATE, new DERGeneralString(item.getState())));
        }

        if (item.getCurrentUser() != null) {
            vect.add(new DERTaggedObject(true, CURRENT_USER, this.asnMapUser.encode(item.getCurrentUser())));
        }

        if (item.getDateCreated() != null) {
            vect.add(new DERTaggedObject(true, DATE_CREATED, new ASN1GeneralizedTime(item.getDateCreated())));
        }

        if (item.getDateLastUpdated() != null) {
            vect.add(new DERTaggedObject(true, DATE_LAST_UPDATED, new ASN1GeneralizedTime(item.getDateLastUpdated())));
        }

        // Form Fields:
        this.asnMapField.setAsList(item.getFormFields(), vect, FORM_FIELDS);
    }

}
