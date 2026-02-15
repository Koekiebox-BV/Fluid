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
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An abstract base class for mapping and decoding tagged ASN.1 fields into a specific object type.
 *
 * This class provides an extensible framework for decoding tagged fields from ASN.1 sequences
 * and mapping them into fields of a target object of type {@code T}. It manages tagged decoding logic
 * through customizable field-specific functions and supports the processing of DER-encoded input.
 *
 * Subclasses are expected to implement specific initialization logic for creating instances of {@code T}
 * and provide the mapping logic for tagged fields using abstract methods.
 *
 * @param <T> The type of the target object that extends {@link ABaseFluidVO}.
 */
public abstract class ASNBaseTaggedMapper<T extends ABaseFluidVO> extends ASNBaseMapper<T> {
    protected final java.util.Map<Integer, BiConsumer<T, ASN1Object>> decoders = new HashMap<>();

    public ASNBaseTaggedMapper(InitType type) {
        super(type);
    }

    /**
     * Example of decoder implementation:
     * decoders.put(Map.FORM_TYPE, (f, p) to f.setFormType(asUtf8(p, Form.JSONMapping.FORM_TYPE)));
     * Maps and decodes tagged fields from the given {@link ASN1Sequence} into the provided object {@code obj}.
     * The decoding is handled by looking up field-specific decoder functions in the {@code decoders} map.
     *
     * @param seq        The {@link ASN1Sequence} containing tagged ASN.1 objects to be decoded.
     * @param obj        The target object of type {@code T} into which the decoded fields will be populated.
     * @param startIndex The index in the {@link ASN1Sequence} from which decoding should begin.
     */
    protected void mapDecodeTaggedFieldsFrom(ASN1Sequence seq, T obj, int startIndex) {
        for (int i = startIndex; i < seq.size(); i++) {
            ASN1TaggedObject tag = asTagged(seq.getObjectAt(i), "Tag " + i);
            int tagNo = tag.getTagNo();
            ASN1Object prim = tag.getBaseObject();

            BiConsumer<T, ASN1Object> decoder = this.decoders.get(tagNo);
            if (decoder != null) decoder.accept(obj, prim);
        }
    }

    /**
     * Represents a tagged ASN.1 object with its associated metadata.
     *
     * This class encapsulates the tag number, field name, and the ASN.1 object
     * related to a specific field or data structure. It is used to simplify
     * handling and processing of tagged fields during ASN.1 decoding.
     *
     * Instances of this class are created to associate the tag number and field name
     * with their corresponding ASN.1 object. These instances can then be used
     * by processing functions or decoders to extract or transform data.
     *
     * Fields:
     * - tagNo: The numeric identifier of the ASN.1 tag associated with the field.
     * - fieldName: The name of the field corresponding to the tag for readability or processing context.
     * - obj: The ASN.1 object encapsulating the data for the tagged field.
     */
    @RequiredArgsConstructor
    @Getter
    protected static final class TagObj<T> {
        public final int tagNo;
        public final String fieldName;
        public final ASN1Object obj;
        public final T toPopulate;
    }

    /**
     * Decodes an ASN.1 tagged object from the given sequence and populates the provided object
     * using a specified decoding function.
     *
     * @param seq the tag or identifier within the ASN.1 sequence that determines the
     *            type of the tagged object.
     * @param toPopulate the instance of type T that will be populated with the decoded
     *                   information from the tagged object.
     * @param decodeMappingFunc a function that specifies the decoding logic for the tagged object
     *                          and maps it onto the provided object of type T.
     */
    protected void decodeTaggedObject(
            ASN1Sequence seq,
            T toPopulate,
            Function<TagObj<T>, Void> decodeMappingFunc
    ) {
        int startIndex = this.getStartIndexForTaggedObjects();
        this.decodeTaggedObject(seq, toPopulate, decodeMappingFunc, startIndex);
    }

    /**
     * Returns the starting index for decoding based on the initialization type.
     * @return the starting index for decoding
     */
    protected int getStartIndexForTaggedObjects() {
        return this.initType == InitType.ID_ONLY ? 1 : ASNBaseMapper.Map.CONTINUE;
    }

    /**
     * Populates a given object using tagged objects within an ASN1 sequence.
     *
     * This method iterates through the tagged objects within the specified ASN1 sequence
     * and applies a factory function to generate or process the tagged object data for the target object.
     *
     * @param seq the ASN1 sequence containing the tagged objects to process
     * @param toPopulate the target object to populate using the tagged object data
     * @param decodeMappingFunc a function that accepts a {@code TagObj} containing the tag number, field name, and
     *                base object, and performs a desired operation or transformation
     * @param startIndex the index in the sequence from which decoding should begin.
     */
    protected void decodeTaggedObject(
            ASN1Sequence seq,
            T toPopulate,
            Function<TagObj<T>, Void> decodeMappingFunc,
            int startIndex
    ) {
        for (int i = startIndex; i < seq.size(); i++) {
            String fieldName = "Tag "+i+" on "+toPopulate.getClass().getSimpleName();
            ASN1TaggedObject tag = this.asTagged(seq.getObjectAt(i), fieldName);
            int tagNo = tag.getTagNo();
            ASN1Object prim = tag.getBaseObject();
            decodeMappingFunc.apply(new TagObj<>(tagNo, fieldName, prim, toPopulate));
        }
    }

    /**
     * Decodes a DER-encoded byte array into an object of type {@code T}.
     *
     * This method performs the following steps:
     * 1. Initializes an {@link ASN1Sequence} from the provided DER-encoded byte array.
     * 2. Creates an initial instance of type {@code T} using the {@link #supplierForInstance()} method.
     * 3. Populates the base fields of the instance from the ASN.1 sequence.
     * 4. Processes and populates additional fields in the instance using the tagged objects
     *    contained in the sequence, guided by the field-specific decoder logic provided
     *    by {@link #decodeMapTagsMethod()}.
     *
     * @param der the DER-encoded byte array to decode
     * @return an object of type {@code T} populated with data from the decoded ASN.1 sequence
     */
    @Override
    public final T decode(byte[] der) {
        return this.decode(this.initSeq(der));
    }

    /**
     * Decodes the provided {@link ASN1Sequence} into an object of type {@code T}.
     *
     * This method performs the following steps:
     * 1. Initializes an instance of type {@code T} using the {@code supplierForInstance()} method.
     * 2. Populates the base fields of the instance from the ASN.1 sequence.
     * 3. Decodes tagged objects from the sequence into the corresponding fields of the instance
     *    using the decoding logic defined in {@code decodeMapTagsMethod()}.
     *
     * @param seq The {@link ASN1Sequence} containing ASN.1 objects to be decoded.
     * @return An object of type {@code T} populated with data from the decoded {@link ASN1Sequence}.
     */
    public final T decode(ASN1Sequence seq) {
        String tsDec = PerfStats.timedStart();

        T returnVal = this.supplierForInstance().get();
        this.popBaseFields(returnVal, seq);

        // Populate all the tagged values.
        this.decodeTaggedObject(seq, returnVal, this.decodeMapTagsMethod());
        PerfStats.timedStop(PerfStats.Label.Asn1DerMapper_BTDecode, tsDec);
        return returnVal;
    }

    /**
     * Decodes the given ASN1Sequence into a list of objects of type T.
     *
     * @param fieldsSeq the ASN1Sequence to decode, representing a sequence of encoded objects
     * @param fieldName the name of the field being processed, used for error handling or logging
     * @return a List of objects of type T decoded from the provided ASN1Sequence
     */
    public final List<T> decodeAsList(ASN1Sequence fieldsSeq, String fieldName) {
        List<T> returnVal = new ArrayList<>();
        for (int j = 0; j < fieldsSeq.size(); j++) {
            returnVal.add(this.decode(this.asSeq(fieldsSeq.getObjectAt(j), fieldName)));
        }
        return returnVal;
    }

    /**
     * Encodes the provided object of type {@code T} into a DER-encoded ASN.1 sequence.
     *
     * This method performs the encoding of the input object by initializing an {@link ASN1EncodableVector}
     * through {@code initVector(vo)}, adding the necessary fields using the {@code encode()} method,
     * and finally packaging the result into a {@link DERSequence}.
     *
     * @param vo The object of type {@code T} to be encoded into ASN.1 sequence format.
     *           This object holds the data that will be serialized into a DER-compliant structure.
     * @return A {@link DERSequence} representing the encoded data of the input object.
     *         This sequence contains all the fields transformed into their ASN.1 representation.
     * @throws AssertionError if the size of the {@code ASN1EncodableVector} is less than the expected minimum size.
     */
    @Override
    public final DERSequence encode(T vo) {
        String tsEnc = PerfStats.timedStart();
        ASN1EncodableVector vect = initVector(vo);
        this.encodeTaggedObject(vo, vect);

        DERSequence returnVal = new DERSequence(vect);
        PerfStats.timedStop(PerfStats.Label.Asn1DerMapper_BTEncode, tsEnc);
        return returnVal;
    }

    /**
     * Encodes the given item of type {@code T} into an {@code ASN1EncodableVector}.
     *
     * This method is responsible for converting the provided object and its
     * fields into an ASN.1-compatible format, represented by the
     * {@code ASN1EncodableVector}. The encoding process typically involves
     * mapping the object's fields into tagged elements within the vector
     * according to the ASN.1 schema definitions.
     *
     * @param item The object of type {@code T} to be encoded.
     *             This object contains data that will be transformed
     *             into an ASN.1-compliant representation.
     * @param vector The {@code ASN1EncodableVector} to which the encoded
     *               data will be appended. Each element in the vector
     *               corresponds to an encoded field or structure from
     *               the input object.
     */
    protected abstract void encodeTaggedObject(T item, ASN1EncodableVector vector);

    /**
     * Initializes and returns an instance of type {@code T}.
     *
     * This method serves as a factory for generating initial instances of the target object
     * type {@code T} before populating its fields with decoded values from the ASN.1 sequence.
     * It is an abstract method that must be implemented by subclasses to provide the specific
     * initialization logic required for the target type.
     *
     * @return An uninitialized instance of type {@code T}, ready to be populated with decoded data.
     */
    protected abstract Supplier<T> supplierForInstance();

    /**
     * Provides a mapping function for decoding tagged ASN.1 fields.
     *
     * This method returns a {@code Function<TagObj, Void>} that defines
     * the decoding logic for tagged fields based on their {@link TagObj} context.
     * The function processes tagged fields by associating their tag numbers and associated
     * metadata (like field name and data object) with specific decoding logic.
     * The returned function is intended to be used in methods like
     * {@code mapDecodeTaggedFieldsFrom()} or {@code populateFromTaggedObjects()}
     * to assist in decoding and transforming the tagged data into its target structure.
     *
     * The implementation of this method should provide the field-specific decoding
     * logic required to map tagged ASN.1 objects to their corresponding parameters
     * within an object of type {@code T}.
     *
     * @return a {@code Function<TagObj, Void>} that defines the decoding logic
     *         for tagged ASN.1 fields.
     */
    protected Function<TagObj<T>, Void> decodeMapTagsMethod() {
        return this::mapDecodedTaggedObject;
    }

    /**
     * Processes a tagged ASN.1 object encapsulated by the {@code TagObj} and performs
     * a specific mapping or decoding operation based on the tag's metadata.
     *
     * This method is intended to handle individual tagged objects and apply the required
     * logic to map the tag's associated data to a corresponding representation within
     * the context of the class implementing this method. The exact mapping logic is
     * defined in the subclass or through the decoding function provided.
     *
     * @param tag A {@code TagObj<T>} instance representing a tagged ASN.1 object,
     *            which includes its tag number, field name, and the associated ASN.1 data.
     * @return Always returns {@code null}.
     */
    protected abstract Void mapDecodedTaggedObject(TagObj<T> tag);
}
