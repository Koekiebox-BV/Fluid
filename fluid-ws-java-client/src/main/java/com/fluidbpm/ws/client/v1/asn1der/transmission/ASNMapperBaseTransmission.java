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

package com.fluidbpm.ws.client.v1.asn1der.transmission;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.*;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestParameter;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.*;
import lombok.Getter;
import org.bouncycastle.asn1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ANSGlobal.Type.*;
import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.DefWhenNull.nullSafeTxt;
import static com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission.Map.PAYLOAD_POPULATE;

/**
 * A mapper class that handles encoding and decoding of {@code BaseTransmission} objects
 * to and from ASN.1 DER-encoded data. This class extends {@code ASNBaseMapper}
 * and provides custom logic for processing ASN.1 sequences for
 * {@code BaseTransmission} objects.
 */
public class ASNMapperBaseTransmission extends ASNBaseTaggedMapper<BaseTransmission> {
    @Getter
    private final int transmissionObjectType;
    private PayloadPopulate payloadPopulate;
    private ASNMapperUser asnMapUser;
    private ASNMapperField asnMapField;
    private ASNMapperForm asnMapForm;
    private ASNMapperAttachment asnMapAtt;

    /**
     * Constructs an instance of the ASNMapperBaseTransmission class.
     * This constructor initializes the ASNMapperBaseTransmission by setting its
     * transmissionObjectType field and creating a new instance of ASNMapperUser.
     * The initialization type for the superclass is set to {@code InitType.ALL}.
     *
     * @param transmissionObjectType The type of transmission object being handled.
     *                               This value is used to determine specific
     *                               transmission-related configurations for the instance.
     */
    public ASNMapperBaseTransmission(int transmissionObjectType) {
        super(InitType.ALL);
        this.transmissionObjectType = transmissionObjectType;
        this.asnMapUser = new ASNMapperUser();
    }

    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} to provide additional
     * constants for identifying specific field mappings used in ASN.1 sequence processing.
     *
     * The class defines static constants and their corresponding aliases for
     * various field types, enabling efficient and structured interaction with
     * ASN.1 data structures.
     *
     * Fields include identifiers for general operations such as payload population,
     * request objects, and transmission objects. Additionally, nested constants
     * within the {@link PayloadPopulate} class represent more granular identifiers
     * for specific payload-related mappings.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int PAYLOAD_POPULATE = 5;
        public static final String PAYLOAD_POPULATE_ALIAS = "Payload Populate";
        public static final int REQUEST_OBJECT = 6;// Tagged Objects start here.
        public static final String REQUEST_OBJECT_ALIAS = "Request Object";
        public static final int TRANSMISSION_OBJECT = 7;
        public static final String TRANSMISSION_OBJECT_ALIAS = "Transmission Object";

        /**
         * The {@code PayloadPopulate} class defines constants and their corresponding aliases
         * that represent various types of payload field mappings. These constants are used
         * for categorizing and identifying specific multi-choice fields and metadata in
         * payload population scenarios.
         *
         * The constants include:
         * - {@link #MC_FORM} and {@link #MC_FORM_ALIAS}: Representing multi-choice form fields.
         * - {@link #MC_USER} and {@link #MC_USER_ALIAS}: Representing multi-choice user fields.
         * - {@link #MC_ROUTE} and {@link #MC_ROUTE_ALIAS}: Representing multi-choice route fields.
         * - {@link #MC_GLOBAL} and {@link #MC_GLOBAL_ALIAS}: Representing global multi-choice fields.
         * - {@link #FIELD_META_DATA} and {@link #FIELD_META_DATA_ALIAS}: Representing field metadata for payload mappings.
         *
         * These constants are key components in structured processing of payloads during ASN.1
         * sequence mappings.
         */
        public static class PayloadPopulate {
            public static final int MC_FORM = 0;
            public static final String MC_FORM_ALIAS = "MC Form";
            public static final int MC_USER = 1;
            public static final String MC_USER_ALIAS = "MC User";
            public static final int MC_ROUTE = 2;
            public static final String MC_ROUTE_ALIAS = "MC Route";
            public static final int MC_GLOBAL = 3;
            public static final String MC_GLOBAL_ALIAS = "MC Global";
            public static final int FIELD_META_DATA = 4;
            public static final String FIELD_META_DATA_ALIAS = "Field Meta Data";

            public static class MultiChoiceField {
                public static final int NAME = 0;
                public static final String NAME_ALIAS = "Field Name";
                public static final int MULTI_CHOICES = 1;
                public static final String MULTI_CHOICES_ALIAS = "Multi Choices";

                public static class MultiChoice {
                    public static final int ID = 0;
                    public static final String ID_ALIAS = "Id";
                    public static final int ALIAS = 1;
                    public static final String ALIAS_ALIAS = "Alias";
                }
            }

            public static class FormFieldMetaData {
                public static final int NAME = 0;
                public static final String NAME_ALIAS = "Field Name";
                public static final int META_DATA = 1;
                public static final String META_DATA_ALIAS = "Meta Data";
            }
        }

        public static class RequestObject {
            public static final int PATH = 0;
            public static final String PATH_ALIAS = "Path";
            public static final int REQUEST_PARAMS = 1;
            public static final String REQUEST_PARAMS_ALIAS = "Request Parameters";

            public static class RequestParam {
                public static final int ALIAS = 0;
                public static final String ALIAS_ALIAS = "Alias";
                public static final int VALUE = 1;
                public static final String VALUE_ALIAS = "Value";
            }
        }
    }

    /**
     * Provides a supplier that creates instances of {@link BaseTransmission}.
     * @return A {@link Supplier} that produces new {@link BaseTransmission} instances.
     */
    @Override
    protected Supplier<BaseTransmission> supplierForInstance() {
        return BaseTransmission::new;
    }

    /**
     * Encodes a tagged ASN.1 object by processing the provided {@link BaseTransmission} instance
     * and populating the given {@link ASN1EncodableVector} with the corresponding data.
     * The method handles various transmission-related objects such as payload populate,
     * request object, and transmission object by mapping their data to ASN.1 sequences
     * and tagged objects.
     *
     * @param item The object of type {@code T} to be encoded.
     *             This object contains data that will be transformed
     *             into an ASN.1-compliant representation.
     * @param vect The {@code ASN1EncodableVector} to which the encoded
     *               data will be appended. Each element in the vector
     *               corresponds to an encoded field or structure from
     *               the input object.
     */
    @Override
    protected void encodeTaggedObject(BaseTransmission item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        this.payloadPopulate = item.getPayloadPopulate();
        if (this.payloadPopulate == null) this.payloadPopulate = new PayloadPopulate();

        this.asnMapAtt = new ASNMapperAttachment();
        this.asnMapField = new ASNMapperField(this.payloadPopulate);
        this.asnMapForm = new ASNMapperForm(this.asnMapUser, this.asnMapField);
        this.asnMapField.setMapTableField(
                new ASNMapperTableField(new ASNMapperForm(this.asnMapUser, this.asnMapField))
        );

        // [5] -> Payment Populate:
        ASN1EncodableVector vecPayPop = new ASN1EncodableVector();
        vecPayPop.add(this.mcSeqEncodeMC(this.payloadPopulate.getMcFormField()));
        vecPayPop.add(this.mcSeqEncodeMC(this.payloadPopulate.getMcUserField()));
        vecPayPop.add(this.mcSeqEncodeMC(this.payloadPopulate.getMcRouteField()));
        vecPayPop.add(this.mcSeqEncodeMC(this.payloadPopulate.getMcGlobalField()));
        vecPayPop.add(this.mcSeqEncodeFieldMeta(this.payloadPopulate.getFfMetaData()));
        vect.add(new DERSequence(vecPayPop));

        // [6] -> Request Object:
        RequestObject reqObj = item.getRequestObject();
        if (reqObj != null) {
            ASN1EncodableVector reqObjFields = new ASN1EncodableVector();
            ASN1EncodableVector vecReqParams = new ASN1EncodableVector();
            List<RequestParameter> reqParams = reqObj.getRequestParameters();
            if (reqParams != null) {
                reqParams.forEach(reqParam -> {
                    ASN1EncodableVector innerFields = new ASN1EncodableVector();
                    innerFields.add(new DERGeneralString(reqParam.getAlias()));
                    innerFields.add(new DERGeneralString(reqParam.getValue()));
                    vecReqParams.add(new DERSequence(innerFields));
                });
            }

            reqObjFields.add(new DERGeneralString(nullSafeTxt(reqObj.getPath())));
            reqObjFields.add(new DERSequence(vecReqParams));
            vect.add(new DERTaggedObject(true, Map.REQUEST_OBJECT, new DERSequence(reqObjFields)));
        }

        // [7] -> Transmission Object:
        ABaseFluidVO transObj = item.getTransmissionObject();
        if (transObj != null) {
            DERSequence seqTransObj = null;
            switch (this.transmissionObjectType) {
                case FIELD:
                    seqTransObj = this.asnMapField.encode((Field)transObj);
                    break;
                case FORM:
                    seqTransObj = this.asnMapForm.encode((Form)transObj);
                    break;
                case FLUID_ITEM:
                    ASNMapperFluidItem mapFI = new ASNMapperFluidItem(this.asnMapForm, this.asnMapField);
                    seqTransObj = mapFI.encode((FluidItem) transObj);
                    break;
                case FORM_HISTORIC_DATA_LISTING:
                    ASNMapperFormHistoricData mapFormHistData = new ASNMapperFormHistoricData(
                            this.asnMapUser, this.asnMapField, this.asnMapForm
                    );
                    ASNMapperFormHistoricDataListing mapFormHistDataList = new ASNMapperFormHistoricDataListing(
                            mapFormHistData
                    );
                    seqTransObj = mapFormHistDataList.encode((FormHistoricDataListing)transObj);
                    break;
                default:
                    throw new FluidClientException(
                            "Transmission Object '"+ transObj +"' not supported!",
                            FluidClientException.ErrorCode.ASN_1_ERROR
                    );
            }
            vect.add(new DERTaggedObject(true, Map.TRANSMISSION_OBJECT, seqTransObj));
        }
    }

    /**
     * Encodes a list of {@link ASNMultiChoiceField} objects into an ASN.1 {@link DERSequence}.
     * Each entry in the list is transformed into a sequence that consists of the field name
     * and its associated multi-choice items. If the provided list is {@code null}, empty,
     * or contains invalid entries (e.g., blank field names or empty multi-choice items),
     * those entries are excluded from the resulting sequence.
     *
     * @param mcFields The list of {@link ASNMultiChoiceField} objects to encode.
     *                 Each object should have a non-blank field name and a non-empty list
     *                 of multi-choice items. Invalid or null entries will be skipped.
     * @return A {@link DERSequence} containing the encoded multi-choice fields.
     *         If no valid entries exist in the list, an empty {@link DERSequence} is returned.
     */
    private DERSequence mcSeqEncodeMC(List<ASNMultiChoiceField> mcFields) {
        if (mcFields == null || mcFields.isEmpty()) return new DERSequence();

        ASN1EncodableVector vecMcSeqObjs = new ASN1EncodableVector();
        for (ASNMultiChoiceField field : mcFields) {
            if (UtilGlobal.isBlank(field.getFieldName()) ||
                    field.getMultiChoices() == null || field.getMultiChoices().isEmpty()) continue;

            ASN1EncodableVector vecEntry = new ASN1EncodableVector();
            vecEntry.add(new DERUTF8String(field.getFieldName()));

            ASN1EncodableVector vecMcItems = new ASN1EncodableVector();
            field.getMultiChoices().forEach(mcItm -> {
                ASN1EncodableVector vecMcItm = new ASN1EncodableVector();
                vecMcItm.add(new ASN1Integer(mcItm.getId()));
                vecMcItm.add(new DERUTF8String(mcItm.getAlias()));
                vecMcItems.add(new DERSequence(vecMcItm));
            });
            vecEntry.add(new DERSequence(vecMcItems));

            vecMcSeqObjs.add(new DERSequence(vecEntry));
        }
        return new DERSequence(vecMcSeqObjs);
    }

    /**
     * Encodes a list of {@link FormFieldMetaData} objects into an ASN.1 {@link DERSequence}.
     * Each {@link FormFieldMetaData} entry is processed into a sequence containing the field's name
     * and metadata, provided that both values are non-blank. A sequence of these entries is then
     * returned as the final encoded result.
     *
     * @param ffMetaDatas A list of {@link FormFieldMetaData} objects to encode. Each object
     *                    should contain a non-blank field name and metadata. If the list is
     *                    {@code null} or empty, or if a particular entry has blank values,
     *                    those entries will be excluded from the encoding.
     * @return A {@link DERSequence} containing the encoded data. If no valid entries are present
     *         in the list, an empty {@link DERSequence} is returned.
     */
    private DERSequence mcSeqEncodeFieldMeta(List<FormFieldMetaData> ffMetaDatas) {
        if (ffMetaDatas == null || ffMetaDatas.isEmpty()) return new DERSequence();

        ASN1EncodableVector vecMetaDatas = new ASN1EncodableVector();
        for (FormFieldMetaData field : ffMetaDatas) {
            if (UtilGlobal.isBlank(field.getFieldName()) || UtilGlobal.isBlank(field.getMetaData())) continue;

            ASN1EncodableVector vecEntry = new ASN1EncodableVector();
            vecEntry.add(new DERUTF8String(field.getFieldName()));
            vecEntry.add(new DERUTF8String(field.getMetaData()));
            vecMetaDatas.add(new DERSequence(vecEntry));
        }
        return new DERSequence(vecMetaDatas);
    }

    /**
     * Populates the base fields of the {@link BaseTransmission} object with data extracted
     * from the {@link ASN1Sequence}. Additionally, processes and maps the payload-related fields
     * such as multi-choice fields and field metadata.
     *
     * @param vo   The {@link BaseTransmission} instance into which the fields will be populated.
     * @param seq  The {@link ASN1Sequence} containing the data to be extracted and mapped into the {@code vo}.
     */
    @Override
    protected void popBaseFields(BaseTransmission vo, ASN1Sequence seq) {
        // Populate the base fields, then the [PayloadPopulate]:
        super.popBaseFields(vo, seq);

        ASN1Sequence seqPayloadPop = asSeq(seq.getObjectAt(PAYLOAD_POPULATE), Map.PAYLOAD_POPULATE_ALIAS);
        List<ASNMultiChoiceField> mcFormField = this.extractInnerSeqForPayPop(
                Map.PayloadPopulate.MC_FORM_ALIAS,
                asSeq(seqPayloadPop.getObjectAt(Map.PayloadPopulate.MC_FORM), Map.PayloadPopulate.MC_FORM_ALIAS),
                this::mapAsASNMultiChoiceField
        );
        List<ASNMultiChoiceField> mcUserField = this.extractInnerSeqForPayPop(
                Map.PayloadPopulate.MC_USER_ALIAS,
                asSeq(seqPayloadPop.getObjectAt(Map.PayloadPopulate.MC_USER), Map.PayloadPopulate.MC_USER_ALIAS),
                this::mapAsASNMultiChoiceField
        );
        List<ASNMultiChoiceField> mcRouteField = this.extractInnerSeqForPayPop(
                Map.PayloadPopulate.MC_ROUTE_ALIAS,
                asSeq(seqPayloadPop.getObjectAt(Map.PayloadPopulate.MC_ROUTE), Map.PayloadPopulate.MC_ROUTE_ALIAS),
                this::mapAsASNMultiChoiceField
        );
        List<ASNMultiChoiceField> mcGlobalField = this.extractInnerSeqForPayPop(
                Map.PayloadPopulate.MC_GLOBAL_ALIAS,
                asSeq(seqPayloadPop.getObjectAt(Map.PayloadPopulate.MC_GLOBAL), Map.PayloadPopulate.MC_GLOBAL_ALIAS),
                this::mapAsASNMultiChoiceField
        );
        List<FormFieldMetaData> ffMetaData = this.extractInnerSeqForPayPop(
                Map.PayloadPopulate.FIELD_META_DATA_ALIAS,
                asSeq(seqPayloadPop.getObjectAt(Map.PayloadPopulate.FIELD_META_DATA), Map.PayloadPopulate.FIELD_META_DATA_ALIAS),
                this::mapAsFormFieldMetaData
        );

        this.payloadPopulate = new PayloadPopulate(mcFormField, mcUserField, mcRouteField, mcGlobalField, ffMetaData);
        vo.setPayloadPopulate(this.payloadPopulate);

        this.asnMapAtt = new ASNMapperAttachment();
        this.asnMapField = new ASNMapperField(this.payloadPopulate);
        this.asnMapForm = new ASNMapperForm(this.asnMapUser, this.asnMapField);
        this.asnMapField.setMapTableField(
                new ASNMapperTableField(new ASNMapperForm(this.asnMapUser, this.asnMapField))
        );

        assert this.payloadPopulate != null : "Payload Populate is null!";
        assert vo.getPayloadPopulate() != null : "Payload Populate is null in VO!";
    }

    /**
     * Extracts a list of objects from an {@link ASN1Sequence} by decoding each element of the sequence
     * using the provided decode function. The method ensures the objects extracted from the sequence
     * conform to the specified type parameter {@code L}, which extends {@link ABaseFluidVO}.
     *
     * @param <L>       The type of objects to be extracted from the ASN.1 sequence, extending {@link ABaseFluidVO}.
     * @param fieldName The name of the field being processed, used for error reporting in auxiliary methods.
     * @param seq       The {@link ASN1Sequence} from which objects will be extracted. Each element in the
     *                  sequence represents an encoded object.
     * @param decodeFunc A {@link Function} that decodes an {@link ASN1Sequence} into an object of type {@code L}.
     *                   This function is applied to each element in the sequence.
     * @return A {@link List} containing the decoded objects of type {@code L}, extracted from the specified
     *         {@link ASN1Sequence}.
     */
    private <L extends ABaseFluidVO> List<L> extractInnerSeqForPayPop(
            String fieldName,
            ASN1Sequence seq,
            Function<ASN1Sequence, L> decodeFunc
    ) {
        List<L> returnVal = new ArrayList<>();
        for (int j = 0; j < seq.size(); j++) {
            L instance = decodeFunc.apply(this.asSeq(seq.getObjectAt(j), fieldName));
            returnVal.add(instance);
        }
        return returnVal;
    }

    private ASNMultiChoiceField mapAsASNMultiChoiceField(ASN1Sequence seq) {
        String fieldName = this.asUtf8(
                seq.getObjectAt(Map.PayloadPopulate.MultiChoiceField.NAME),
                Map.PayloadPopulate.MultiChoiceField.NAME_ALIAS
        );
        ASN1Sequence seqMultiChoices = this.asSeq(
                seq.getObjectAt(Map.PayloadPopulate.MultiChoiceField.MULTI_CHOICES),
                Map.PayloadPopulate.MultiChoiceField.MULTI_CHOICES_ALIAS
        );

        List<ASNMultiChoice> choices = new ArrayList<>();
        for (int i = 0; i < seqMultiChoices.size(); i++) {
            ASN1Sequence innerSeq = this.asSeq(seqMultiChoices.getObjectAt(i), Map.PayloadPopulate.MultiChoiceField.MULTI_CHOICES_ALIAS);
            Long id = this.asLong(
                    innerSeq.getObjectAt(Map.PayloadPopulate.MultiChoiceField.MultiChoice.ID),
                    Map.PayloadPopulate.MultiChoiceField.MultiChoice.ID_ALIAS
            );
            String alias = this.asUtf8(
                    innerSeq.getObjectAt(Map.PayloadPopulate.MultiChoiceField.MultiChoice.ALIAS),
                    Map.PayloadPopulate.MultiChoiceField.MultiChoice.ALIAS_ALIAS
            );
            choices.add(new ASNMultiChoice(id, alias));
        }
        return new ASNMultiChoiceField(fieldName, choices);
    }

    private FormFieldMetaData mapAsFormFieldMetaData(ASN1Sequence seq) {
        String fieldName = this.asUtf8(
                seq.getObjectAt(Map.PayloadPopulate.FormFieldMetaData.NAME),
                Map.PayloadPopulate.FormFieldMetaData.NAME_ALIAS
        );
        String fieldMetaData = this.asUtf8(
                seq.getObjectAt(Map.PayloadPopulate.FormFieldMetaData.META_DATA),
                Map.PayloadPopulate.FormFieldMetaData.META_DATA_ALIAS
        );
        return new FormFieldMetaData(fieldName, fieldMetaData);
    }

    /**
     * @return The starting index for decoding, which is set to 6 in this implementation.
     * @see ASNBaseTaggedMapper#getStartIndexForTaggedObjects
     */
    @Override
    protected int getStartIndexForTaggedObjects() {
        int start = super.getStartIndexForTaggedObjects();
        start++;//Payload Populate.
        return start;
    }

    @Override
    protected Void mapDecodedTaggedObject(TagObj<BaseTransmission> tag) {
        assert tag != null : "TagObj cannot be null.";

        BaseTransmission toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.REQUEST_OBJECT:
                ASN1Sequence seqReqObj = asSeq(obj, Map.REQUEST_OBJECT_ALIAS);
                String formPath = this.asGeneralTxt(seqReqObj.getObjectAt(Map.RequestObject.PATH), Map.RequestObject.PATH_ALIAS);
                ASN1Sequence seqParams = asSeq(
                        seqReqObj.getObjectAt(Map.RequestObject.REQUEST_PARAMS),
                        Map.RequestObject.REQUEST_PARAMS_ALIAS
                );
                List<RequestParameter> params = new ArrayList<>();
                for (int j = 0; j < seqParams.size(); j++) {
                    ASN1Sequence iter = asSeq(seqParams.getObjectAt(j), Map.RequestObject.REQUEST_PARAMS_ALIAS);
                    params.add(new RequestParameter(
                            this.asGeneralTxt(iter.getObjectAt(Map.RequestObject.RequestParam.ALIAS), Map.RequestObject.RequestParam.ALIAS_ALIAS),
                            this.asGeneralTxt(iter.getObjectAt(Map.RequestObject.RequestParam.VALUE), Map.RequestObject.RequestParam.VALUE_ALIAS)
                    ));
                }
                toPop.setRequestObject(new RequestObject(formPath, params));
                break;
            case Map.TRANSMISSION_OBJECT:
                ASNBaseTaggedMapper mapper = null;
                switch (this.transmissionObjectType) {
                    case FIELD: mapper = this.asnMapField;break;
                    case FORM: mapper = this.asnMapForm;break;
                    case FLUID_ITEM: mapper = new ASNMapperFluidItem(this.asnMapForm, this.asnMapField);break;
                    case FORM_HISTORIC_DATA_LISTING:
                        ASNMapperFormHistoricData mapFormHistData = new ASNMapperFormHistoricData(
                                this.asnMapUser, this.asnMapField, this.asnMapForm
                        );
                        ASNMapperFormHistoricDataListing mapFrmHistDataList = new ASNMapperFormHistoricDataListing(mapFormHistData);
                        mapper = mapFrmHistDataList;
                        break;
                    default:
                        throw new FluidClientException(
                                "Invalid type code: " + this.transmissionObjectType,
                                FluidClientException.ErrorCode.ASN_1_ERROR
                        );
                }
                assert mapper != null : "Mapper is null!";

                ABaseFluidVO returnVal = mapper.decode(this.asSeq(obj, Map.TRANSMISSION_OBJECT_ALIAS));
                toPop.setTransmissionObject(returnVal);
                break;
            default:
                throw new FluidClientException(
                        "Invalid tag number: " + tag.getTagNo(),
                        FluidClientException.ErrorCode.ASN_1_ERROR
                );
        }
        return null;
    }
}
