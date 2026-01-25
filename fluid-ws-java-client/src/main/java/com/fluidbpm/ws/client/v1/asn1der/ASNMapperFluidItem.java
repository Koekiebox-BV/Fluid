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
import com.fluidbpm.program.api.vo.item.FluidItem;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

/**
 * ASNMapperFluidItem is a mapper class responsible for encoding and decoding
 * instances of the {@code FluidItem} class into and from ASN.1 DER format.
 *
 * This class extends {@code ASNBaseMapper} specifically to provide mapping functionality
 * for the {@code FluidItem} object within the context of the Abstract Syntax Notation One (ASN.1)
 * representation.
 */
public class ASNMapperFluidItem extends ASNBaseTaggedMapper<FluidItem> {
    /**/
    public static class Map extends ASNBaseMapper.Map {
        public static final int FORM = 1;
        public static final int STEP_ENTERED_TIME = 2;
        public static final int USER_FIELDS = 3;
        public static final int ROUTE_FIELDS = 4;
        public static final int GLOBAL_FIELDS = 5;
        public static final int ATTACHMENTS = 6;
        public static final int CUSTOM_PROPERTIES = 7;
        public static final int FLOW_STATE = 8;
        public static final int FLOW = 9;
        public static final int STEP = 10;
        public static final int IN_CASE_OF_CREATE_LINK_TO_PARENT = 11;
        public static final int TABLE_FIELD_PARENT_FORM = 12;
        public static final int TABLE_FIELD_NAME_ON_PARENT_FORM = 13;
    }

    private final ASNMapperForm asnMapForm;
    private final ASNMapperField asnMapField;
    private final ASNMapperAttachment asnMapAttachment;
    private final ASNMapperFluidItemCustomProperty asnMapCusProp;

    /**
     * 
     * @param asnMapForm
     * @param asnMapField
     */
    public ASNMapperFluidItem(
            ASNMapperForm asnMapForm,
            ASNMapperField asnMapField
    ) {
        super(InitType.ID_ONLY);
        this.asnMapForm = asnMapForm;
        this.asnMapField = asnMapField;
        this.asnMapAttachment = new ASNMapperAttachment();
        this.asnMapCusProp = new ASNMapperFluidItemCustomProperty();
    }

    /**
     * Provides a supplier for creating new instances of {@code FluidItem}.
     *
     * This method returns a {@code Supplier} functional interface that generates
     * fresh {@code FluidItem} objects, which are used in associative mappings
     * facilitated by the parent class.
     *
     * @return a {@code Supplier} functional interface for generating {@code FluidItem} instances.
     */
    @Override
    protected Supplier<FluidItem> supplierForInstance() {
        return FluidItem::new;
    }

    /**
     * Maps a decoded tagged object into the corresponding {@code FluidItem} instance.
     *
     * This method processes an ASN.1 encoded tagged object represented by the {@code TagObj<FluidItem>}
     * parameter and populates a {@code FluidItem} instance based on the tag number and its content.
     * The supported tag numbers determine the mapping behaviors and populate specific fields of the
     * {@code FluidItem} instance according to the defined ASN.1 structure.
     *
     * @param tag an object of type {@code TagObj<FluidItem>} that contains the decoded ASN.1 object
     *            and the {@code FluidItem} instance to be populated. This object must not be null.
     * @return always returns {@code null} as this method operates by side effects, updating the
     *         referenced {@code FluidItem} instance.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<FluidItem> tag) {
        assert tag != null : "TagObj cannot be null.";

        FluidItem toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.FORM:
                toPop.setForm(this.asnMapForm.decode(asSeq(obj, FluidItem.JSONMapping.FORM)));
                break;
            case Map.STEP_ENTERED_TIME:
                toPop.setStepEnteredTime(asDate(obj, FluidItem.JSONMapping.STEP_ENTERED_TIME));
                break;
            case Map.USER_FIELDS:
                toPop.setUserFields(
                        this.asnMapField.decodeAsList(asSeq(obj, FluidItem.JSONMapping.USER_FIELDS), FluidItem.JSONMapping.USER_FIELDS)
                );
                break;
            case Map.ROUTE_FIELDS:
                toPop.setRouteFields(
                        this.asnMapField.decodeAsList(asSeq(obj, FluidItem.JSONMapping.ROUTE_FIELDS), FluidItem.JSONMapping.ROUTE_FIELDS)
                );
                break;
            case Map.GLOBAL_FIELDS:
                toPop.setGlobalFields(
                        this.asnMapField.decodeAsList(asSeq(obj, FluidItem.JSONMapping.GLOBAL_FIELDS), FluidItem.JSONMapping.GLOBAL_FIELDS)
                );
                break;
            case Map.ATTACHMENTS:
                toPop.setAttachments(
                        this.asnMapAttachment.decodeAsList(asSeq(obj, FluidItem.JSONMapping.ATTACHMENTS),
                                FluidItem.JSONMapping.ATTACHMENTS)
                );
                break;
            case Map.CUSTOM_PROPERTIES:
                toPop.setCustomProperties(
                        this.asnMapCusProp.decodeAsList(asSeq(obj, FluidItem.JSONMapping.CUSTOM_PROPERTIES),
                                FluidItem.JSONMapping.CUSTOM_PROPERTIES)
                );
                break;
            case Map.FLOW_STATE:
                FluidItem.FlowState flowState = FluidItem.FlowState.valueOfSafe(
                        asGeneralTxt(obj, FluidItem.JSONMapping.FLOW_STATE)
                );
                toPop.setFlowState(flowState);
                break;
            case Map.FLOW:
                toPop.setFlow(asUtf8(obj, FluidItem.JSONMapping.FLOW));
                break;
            case Map.STEP:
                toPop.setStep(asUtf8(obj, FluidItem.JSONMapping.STEP));
                break;
            case Map.IN_CASE_OF_CREATE_LINK_TO_PARENT:
                toPop.setInCaseOfCreateLinkToParent(asBool(obj, FluidItem.JSONMapping.IN_CASE_OF_CREATE_LINK_TO_PARENT));
                break;
            case Map.TABLE_FIELD_PARENT_FORM:
                toPop.setTableFieldParentForm(
                        this.asnMapForm.decode(asSeq(obj, FluidItem.JSONMapping.TABLE_FIELD_PARENT_FORM))
                );
                break;
            case Map.TABLE_FIELD_NAME_ON_PARENT_FORM:
                toPop.setTableFieldNameOnParentForm(asGeneralTxt(obj, FluidItem.JSONMapping.TABLE_FIELD_NAME_ON_PARENT_FORM));
                break;
        }
        return null;
    }

    /**
     * Encodes a {@code FluidItem} into an ASN.1 tagged object and adds it to the given {@code ASN1EncodableVector}.
     *
     * This method processes the {@code FluidItem} by checking its non-null fields and encoding them as
     * ASN.1 tagged objects. Each encoded tagged object is then appended to the provided {@code ASN1EncodableVector}.
     * Ensures that the vector is populated with the correct ASN.1 format for the fluid item attributes.
     *
     * @param item The {@code FluidItem} instance to be encoded. Must not be null.
     * @param vect The {@code ASN1EncodableVector} to which the encoded tagged objects will be added. Must not be null.
     */
    @Override
    protected void encodeTaggedObject(FluidItem item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getForm() != null) {
            vect.add(new DERTaggedObject(true, Map.FORM, this.asnMapForm.encode(item.getForm())));
        }

        if (item.getStepEnteredTime() != null) {
            vect.add(new DERTaggedObject(true, Map.STEP_ENTERED_TIME, new ASN1GeneralizedTime(item.getStepEnteredTime())));
        }

        if (item.getUserFields() != null && !item.getUserFields().isEmpty()) {
            this.asnMapField.setAsList(item.getUserFields(), vect, Map.USER_FIELDS);
        }

        if (item.getRouteFields() != null && !item.getRouteFields().isEmpty()) {
            this.asnMapField.setAsList(item.getRouteFields(), vect, Map.ROUTE_FIELDS);
        }

        if (item.getGlobalFields() != null && !item.getGlobalFields().isEmpty()) {
            this.asnMapField.setAsList(item.getGlobalFields(), vect, Map.GLOBAL_FIELDS);
        }

        if (item.getAttachments() != null && !item.getAttachments().isEmpty()) {
            this.asnMapAttachment.setAsList(item.getAttachments(), vect, Map.ATTACHMENTS);
        }

        if (item.getCustomProperties() != null && !item.getCustomProperties().isEmpty()) {
            ASN1EncodableVector propsVector = new ASN1EncodableVector();
            item.getCustomProperties().forEach(prop -> {
                if (UtilGlobal.isBlank(prop.getName(), prop.getValue())) return;

                propsVector.add(this.asnMapCusProp.encode(prop));
            });
            vect.add(new DERTaggedObject(true, Map.CUSTOM_PROPERTIES, new DERSequence(propsVector)));
        }

        if (item.getFlowState() != null) {
            vect.add(new DERTaggedObject(true, Map.FLOW_STATE, new DERGeneralString(
                    item.getFlowState().name()
            )));
        }

        if (item.getFlow() != null) {
            vect.add(new DERTaggedObject(true, Map.FLOW, new DERGeneralString(item.getFlow())));
        }

        if (item.getStep() != null) {
            vect.add(new DERTaggedObject(true, Map.STEP, new DERGeneralString(item.getStep())));
        }

        if (item.getInCaseOfCreateLinkToParent() != null) {
            boolean boolPrim = item.getInCaseOfCreateLinkToParent();
            vect.add(new DERTaggedObject(true, Map.IN_CASE_OF_CREATE_LINK_TO_PARENT,
                    boolPrim ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
        }

        if (item.getTableFieldParentForm() != null) {
            vect.add(new DERTaggedObject(true, Map.TABLE_FIELD_PARENT_FORM,
                    this.asnMapForm.encode(item.getTableFieldParentForm())));
        }

        if (item.getTableFieldNameOnParentForm() != null) {
            vect.add(new DERTaggedObject(true, Map.TABLE_FIELD_NAME_ON_PARENT_FORM, new DERGeneralString(
                    item.getTableFieldNameOnParentForm())
            ));
        }
    }
}
