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

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.ASN1Sequence;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * The ASNMapperFactory class is responsible for handling the creation,
 * encoding, and decoding of transmission objects based on ASN1 sequence
 * structures. This class leverages an ASNMapperBaseTransmission instance
 * to facilitate the encoding and decoding process of transmission objects.
 */
public class ASNMapperFactory {
    private ASNMapperBaseTransmission baseTransmission;
    public ASNMapperFactory(int type) {
        super();
        this.baseTransmission = new ASNMapperBaseTransmission(type);
    }

    /**
     * Decodes the provided {@link ASN1Sequence} into a {@link BaseTransmission} object and
     * retrieves its transmission object of type {@link ABaseFluidVO}.
     *
     * @param baseTrans The {@link ASN1Sequence} representing the encoded transmission data.
     * @return An instance of {@link ABaseFluidVO} populated with the decoded data.
     */
    public ABaseFluidVO readObjectFromReceived(ASN1Sequence baseTrans) {
        BaseTransmission bt = this.baseTransmission.decode(baseTrans);
        return bt.getTransmissionObject();
    }

    /**
     * Serializes the given {@link ABaseFluidVO} object into a byte array for transmission.
     * This method prepares the required request object and payload before delegating
     * the serialization process to an overloaded writeObjectForSend method.
     *
     * @param objVo The instance of {@link ABaseFluidVO} to be serialized and prepared for transmission.
     * @return A byte array representing the serialized form of the provided {@link ABaseFluidVO} instance.
     */
    public byte[] writeObjectForSend(
            ABaseFluidVO objVo
    ) {
        PayloadPopulate payPop = new PayloadPopulate();
        RequestObject reqObj = new RequestObject();
        return this.writeObjectForSend(payPop, reqObj, objVo);
    }

    /**
     * Serializes the provided transmission data into a byte array for transmission.
     * This method processes and encodes the payload, request object, and the transmission object.
     *
     * @param payloadPopulate An instance of {@link PayloadPopulate} which contains
     *                        information about the payload to be included in the transmission.
     * @param reqObj          An instance of {@link RequestObject} representing the metadata
     *                        or details associated with the current request.
     * @param objVo           An instance of {@link ABaseFluidVO} which holds the data
     *                        to be transmitted as the primary object.
     * @return A byte array that represents the serialized and encoded transmission data
     *         ready for sending.
     */
    public byte[] writeObjectForSend(PayloadPopulate payloadPopulate, RequestObject reqObj, ABaseFluidVO objVo) {
        BaseTransmission bt = new BaseTransmission(this.baseTransmission.getTransmissionObjectType());
        bt.setPayloadPopulate(payloadPopulate);
        bt.setRequestObject(reqObj);
        bt.setTransmissionObject(objVo);
        return seqBytes(this.baseTransmission.encode(bt));
    }
}
