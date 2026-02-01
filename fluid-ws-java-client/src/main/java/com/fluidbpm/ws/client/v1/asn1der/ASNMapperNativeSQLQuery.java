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

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import org.bouncycastle.asn1.*;

import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperNativeSQLQuery.Map.*;

/**
 * The ASNMapperNativeSQLQuery class is responsible for encoding and decoding objects of type
 * {@code NativeSQLQuery} into ASN.1 DER-encoded sequences with specific tagged mappings. This
 * mapper extends the {@code ASNBaseTaggedMapper} class and specializes in handling
 * {@code NativeSQLQuery} instances, applying domain-specific logic for the serialization and
 * deserialization processes of tagged data objects.
 *
 * It provides mechanisms for:
 * - Creating new {@code NativeSQLQuery} instances for decoding purposes.
 * - Assigning tagged data to the appropriate fields of {@code NativeSQLQuery}.
 * - Encoding {@code NativeSQLQuery} objects into ASN.1 DER-compliant tagged structures.
 *
 * This mapper is typically employed in scenarios where standardized encoding
 * of {@code NativeSQLQuery}-related data is essential for interoperability or persistence.
 */
public class ASNMapperNativeSQLQuery extends ASNBaseTaggedMapper<NativeSQLQuery> {
    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} and provides a set of static constants
     * to represent field identifiers for mapping ASN.1 sequences specific to NativeSQLQuery objects.
     * These constants facilitate the identification and mapping of fields during encoding
     * and decoding operations between ASN.1 sequences and internal data structures.
     *
     * Field Identifiers:
     * - {@code DATASOURCE_NAME}: Identifier for the "datasourceName" field.
     * - {@code QUERY}: Identifier for the "query" field.
     * - {@code STORED_PROCEDURE}: Identifier for the "storedProcedure" field.
     * - {@code SQL_INPUTS}: Identifier for the "sqlInputs" field.
     *
     * This class enables developers to work with tagged ASN.1 data by associating each
     * field in a NativeSQLQuery object with a unique constant, simplifying the mapping process.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int DATASOURCE_NAME = 1;
        public static final int QUERY = 2;
        public static final int STORED_PROCEDURE = 3;
        public static final int SQL_INPUTS = 4;
    }

    private final ASNMapperSQLColumn asnMapSQLColumn;

    /**
     * Constructs an instance of the ASNMapperNativeSQLQuery class.
     */
    public ASNMapperNativeSQLQuery() {
        super(InitType.ID_ONLY);
        this.asnMapSQLColumn = new ASNMapperSQLColumn();
    }

    /**
     * Provides a supplier for creating new instances of the {@code NativeSQLQuery} class.
     * This method is typically used to generate fresh {@code NativeSQLQuery} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code NativeSQLQuery} instance.
     */
    @Override
    protected Supplier<NativeSQLQuery> supplierForInstance() {
        return NativeSQLQuery::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<NativeSQLQuery>} object to the appropriate attributes
     * of the associated {@code NativeSQLQuery} instance. The method processes the tag number and updates
     * the corresponding fields in {@code NativeSQLQuery} based on the tag's content.
     *
     * @param tag the {@code TagObj<NativeSQLQuery>} object that contains the tag number and data to be
     *            mapped to the {@code NativeSQLQuery} instance.
     *
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<NativeSQLQuery> tag) {
        assert tag != null : "TagObj cannot be null.";

        NativeSQLQuery toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case DATASOURCE_NAME:
                toPop.setDatasourceName(asUtf8(obj, NativeSQLQuery.JSONMapping.DATASOURCE_NAME));
                break;
            case QUERY:
                toPop.setQuery(asUtf8(obj, NativeSQLQuery.JSONMapping.QUERY));
                break;
            case STORED_PROCEDURE:
                toPop.setStoredProcedure(asUtf8(obj, NativeSQLQuery.JSONMapping.STORED_PROCEDURE));
                break;
            case SQL_INPUTS:
                toPop.setSqlInputs(
                        this.asnMapSQLColumn.decodeAsList(asSeq(obj, NativeSQLQuery.JSONMapping.SQL_INPUTS), NativeSQLQuery.JSONMapping.SQL_INPUTS)
                );
                break;
        }
        return null;
    }

    /**
     * Encodes the attributes of a {@code NativeSQLQuery} object as ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * Depending on the attributes present in the given {@code NativeSQLQuery} object,
     * this method creates tagged objects for fields like datasource name, query,
     * stored procedure, and SQL inputs, and adds them to the provided vector.
     *
     * @param item   The {@code NativeSQLQuery} object to be encoded. Must not be null.
     * @param vect  The {@code ASN1EncodableVector} where the encoded tagged
     *              objects will be added. Must not be null.
     */
    @Override
    public void encodeTaggedObject(NativeSQLQuery item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getDatasourceName() != null) {
            vect.add(new DERTaggedObject(true, DATASOURCE_NAME, new DERUTF8String(item.getDatasourceName())));
        }

        if (item.getQuery() != null) {
            vect.add(new DERTaggedObject(true, QUERY, new DERUTF8String(item.getQuery())));
        }

        if (item.getStoredProcedure() != null) {
            vect.add(new DERTaggedObject(true, STORED_PROCEDURE, new DERUTF8String(item.getStoredProcedure())));
        }

        // SQL Inputs:
        this.asnMapSQLColumn.setAsList(item.getSqlInputs(), vect, SQL_INPUTS);
    }
}
