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
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

import java.io.IOException;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * The ASNMapperFactory class is responsible for handling the creation,
 * encoding, and decoding of transmission objects based on ASN1 sequence
 * structures. This class leverages an ASNMapperBaseTransmission instance
 * to facilitate the encoding and decoding process of transmission objects.
 */
public class ASNMapperFactory {
    private ASNMapperBaseTransmission baseTransmission;

    /**
     * Constructs an instance of {@code ASNMapperFactory}. This constructor initializes
     * the {@code baseTransmission} field with a new {@code ASNMapperBaseTransmission} object
     * based on the provided {@code type}.
     *
     * @param type An integer representing the type of ASN mapping that the factory will
     *             handle. This parameter determines the specific behavior or configuration
     *             of the underlying {@code ASNMapperBaseTransmission} instance created by this factory.
     */
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
     * Decodes the provided byte array into a {@link BaseTransmission} object.
     * This method interprets the byte array as ASN.1 data, converts it into
     * an {@link ASN1Sequence}, and then uses the {@code baseTransmission}
     * instance to decode the sequence into a {@link BaseTransmission} object.
     *
     * @param derBytes The byte array representing ASN.1 encoded data to be
     *                 decoded into a {@link BaseTransmission} object.
     * @return The decoded {@link BaseTransmission} object populated with the
     *         data from the provided byte array.
     * @throws FluidClientException If the byte array is invalid or cannot be
     *                              parsed as ASN.1 data.
     */
    public BaseTransmission readBaseTransmission(byte[] derBytes) {
        try {
            return this.baseTransmission.decode(
                    (ASN1Sequence)ASN1Primitive.fromByteArray(derBytes)
            );
        } catch (
                IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr,
                    FluidClientException.ErrorCode.ASN_1_ERROR);
        }
    }

    /**
     * Decodes the provided byte array into an {@link ABaseFluidVO} object by converting the
     * byte array into an {@link ASN1Sequence} and delegating the decoding process to an
     * overloaded {@code readObjectFromReceived} method.
     *
     * @param derBytes The byte array representing the encoded data to be decoded into an
     *                 {@link ABaseFluidVO} object. This byte array is expected to contain
     *                 ASN.1 encoded data.
     * @return An instance of {@link ABaseFluidVO} populated with the decoded data.
     * @throws FluidClientException If an error occurs during the decoding process, such as
     *                              when the byte array is invalid or cannot be parsed as
     *                              ASN.1 data.
     */
    public ABaseFluidVO readObjectFromReceived(byte[] derBytes) {
        try {
            return this.readObjectFromReceived((ASN1Sequence)ASN1Primitive.fromByteArray(derBytes));
        } catch (
                IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr,
                    FluidClientException.ErrorCode.ASN_1_ERROR);
        }
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

    /**
     * Updates the transmission object type for the underlying {@code baseTransmission} instance.
     *
     * @param type An integer representing the type of the transmission object. This value
     *             determines the specific configuration or behavior of the associated
     *             transmission object in the {@code baseTransmission} component.
     */
    public void setType(int type) {
        this.baseTransmission.setTransmissionObjectType(type);
    }

    /**
     * Delegates the processing of a transmission object to the underlying {@code baseTransmission}
     * instance without specifying an additional {@code ASN1Object}.
     *
     * @param type an integer representing the type of the transmission object to be processed.
     *             This parameter influences how the transmission object is handled internally.
     * @param toPop the {@link BaseTransmission} object that will be utilized for the
     *              processing. It may contain the payload, request object, and other data
     *              related to the transmission.
     */
    public ABaseFluidVO proceedWithTransmissionObject(int type, BaseTransmission toPop) {
        return this.baseTransmission.proceedWithTransmissionObject(type, toPop, null);
    }
}
