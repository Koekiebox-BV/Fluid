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

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.*;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoiceField;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.FormFieldMetaData;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ANSGlobal.Type.*;
import static com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission.Map.PAYLOAD_POPULATE;

/**
 * A mapper class that handles encoding and decoding of {@code BaseTransmission} objects
 * to and from ASN.1 DER-encoded data. This class extends {@code ASNBaseMapper}
 * and provides custom logic for processing ASN.1 sequences for
 * {@code BaseTransmission} objects.
 */
public class ASNMapperBaseTransmission extends ASNBaseTaggedMapper<BaseTransmission> {
    private final int transmissionObjectType;
    private PayloadPopulate payloadPopulate;
    private ASNMapperUser asnMapUser;
    private ASNMapperField asnMapField;
    private ASNMapperForm asnMapForm;

    public ASNMapperBaseTransmission(int transmissionObjectType) {
        super(InitType.ALL);
        this.transmissionObjectType = transmissionObjectType;
        this.asnMapUser = new ASNMapperUser();
    }

    public static class Map extends ASNBaseMapper.Map {
        public static final int PAYLOAD_POPULATE = 5;
        public static final String PAYLOAD_POPULATE_ALIAS = "Payload Populate";
        public static final int REQUEST_OBJECT = 6;
        public static final String REQUEST_OBJECT_ALIAS = "Request Object";
        public static final int TRANSMISSION_OBJECT = 7;
        public static final String TRANSMISSION_OBJECT_ALIAS = "Transmission Object";

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
        }
    }

    @Override
    protected Supplier<BaseTransmission> supplierForInstance() {
        return BaseTransmission::new;
    }

    @Override
    protected void encodeTaggedObject(BaseTransmission item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        this.payloadPopulate = item.getPayloadPopulate();
        if (this.payloadPopulate == null) {
            this.payloadPopulate = new PayloadPopulate();
        }

        this.asnMapField = new ASNMapperField(this.payloadPopulate);
        this.asnMapForm = new ASNMapperForm(this.asnMapUser, this.asnMapField, this.payloadPopulate);
        ASNMapperTableField fld = new ASNMapperTableField(new ASNMapperForm(this.asnMapUser, this.asnMapField, this.payloadPopulate));
        this.asnMapField.setAsnMapTableField(fld);

        DERSequence seqPayPop = new DERSequence();
        DERSequence seqMcFormField = new DERSequence();

        // [5] -> Payment Populate:
        vect.add(seqPayPop);

        if (item.getRequestObject() != null) {
            DERSequence derSeq = new DERSequence();

            //TODO Need to complete...

            //TODO
            vect.add(new DERTaggedObject(true, Map.REQUEST_OBJECT, derSeq));
        }

        if (item.getTransmissionObject() != null) {
            DERSequence derSeq = new DERSequence();

            //TODO Need to complete...
            //TODO
            vect.add(new DERTaggedObject(true, Map.TRANSMISSION_OBJECT, derSeq));
        }
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
        this.asnMapField = new ASNMapperField(this.payloadPopulate);
        this.asnMapForm = new ASNMapperForm(this.asnMapUser, this.asnMapField, this.payloadPopulate);
        ASNMapperTableField fld = new ASNMapperTableField(new ASNMapperForm(this.asnMapUser, this.asnMapField, this.payloadPopulate));
        this.asnMapField.setAsnMapTableField(fld);

        assert this.payloadPopulate != null : "Payload Populate is null!";
        assert vo.getPayloadPopulate() != null : "Payload Populate is null in VO!";
    }

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
        //TODO sdsd
        return new ASNMultiChoiceField();
    }

    private FormFieldMetaData mapAsFormFieldMetaData(ASN1Sequence seq) {
        //TODO sdsd
        return new FormFieldMetaData();
    }

    @Override
    protected Void mapDecodedTaggedObject(TagObj<BaseTransmission> tag) {
        assert tag != null : "TagObj cannot be null.";

        BaseTransmission toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.TRANSMISSION_OBJECT:
                ASNBaseTaggedMapper mapper = null;
                switch (this.transmissionObjectType) {
                    case FLUID_ITEM: mapper = new ASNMapperFluidItem(this.asnMapForm);break;
                    case FORM: mapper = this.asnMapForm;break;
                    case FIELD: mapper = this.asnMapField;break;
                    default:
                        throw new FluidClientException(
                                "Invalid type code: " + this.transmissionObjectType,
                                FluidClientException.ErrorCode.ASN_1_ERROR
                        );
                }
                assert mapper != null : "Mapper is null!";

                ABaseFluidVO returnVal = mapper.decode(asSeq(obj, Map.TRANSMISSION_OBJECT_ALIAS));
                toPop.setTransmissionObject(returnVal);
                break;
            case Map.REQUEST_OBJECT:
                ASN1Sequence seqReqObj = asSeq(obj, Map.REQUEST_OBJECT_ALIAS);

                RequestObject reqObj = new RequestObject();

                //TODO sdsd

                toPop.setRequestObject(reqObj);
                break;
        }
        return null;
    }
}
