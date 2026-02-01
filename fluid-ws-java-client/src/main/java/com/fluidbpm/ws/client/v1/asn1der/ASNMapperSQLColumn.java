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

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import org.bouncycastle.asn1.*;

import java.util.Date;
import java.util.function.Supplier;

import static com.fluidbpm.ws.client.v1.asn1der.ASNMapperSQLColumn.Map.*;

/**
 * The ASNMapperSQLColumn class is responsible for encoding and decoding objects of type
 * {@code SQLColumn} into ASN.1 DER-encoded sequences with specific tagged mappings. This
 * mapper extends the {@code ASNBaseTaggedMapper} class and specializes in handling
 * {@code SQLColumn} instances, applying domain-specific logic for the serialization and
 * deserialization processes of tagged data objects.
 *
 * It provides mechanisms for:
 * - Creating new {@code SQLColumn} instances for decoding purposes.
 * - Assigning tagged data to the appropriate fields of {@code SQLColumn}.
 * - Encoding {@code SQLColumn} objects into ASN.1 DER-compliant tagged structures.
 *
 * This mapper is typically employed in scenarios where standardized encoding
 * of {@code SQLColumn}-related data is essential for interoperability or persistence.
 */
public class ASNMapperSQLColumn extends ASNBaseTaggedMapper<SQLColumn> {
    /**
     * The {@code Map} class extends {@code ASNBaseMapper.Map} and provides a set of static constants
     * to represent field identifiers for mapping ASN.1 sequences specific to SQLColumn objects.
     * These constants facilitate the identification and mapping of fields during encoding
     * and decoding operations between ASN.1 sequences and internal data structures.
     *
     * Field Identifiers:
     * - {@code COLUMN_NAME}: Identifier for the "columnName" field.
     * - {@code COLUMN_INDEX}: Identifier for the "columnIndex" field.
     * - {@code SQL_TYPE}: Identifier for the "sqlType" field.
     * - {@code SQL_VALUE_STRING}: Identifier for string-typed SQL values.
     * - {@code SQL_VALUE_INTEGER}: Identifier for integer-typed SQL values.
     * - {@code SQL_VALUE_LONG}: Identifier for long-typed SQL values.
     * - {@code SQL_VALUE_DOUBLE}: Identifier for double-typed SQL values.
     * - {@code SQL_VALUE_BOOLEAN}: Identifier for boolean-typed SQL values.
     * - {@code SQL_VALUE_DATE}: Identifier for date-typed SQL values.
     *
     * This class enables developers to work with tagged ASN.1 data by associating each
     * field in a SQLColumn object with a unique constant, simplifying the mapping process.
     */
    public static class Map extends ASNBaseMapper.Map {
        public static final int COLUMN_NAME = 1;
        public static final int COLUMN_INDEX = 2;
        public static final int SQL_TYPE = 3;
        public static final int SQL_VALUE_STRING = 4;
        public static final int SQL_VALUE_INTEGER = 5;
        public static final int SQL_VALUE_LONG = 6;
        public static final int SQL_VALUE_DOUBLE = 7;
        public static final int SQL_VALUE_BOOLEAN = 8;
        public static final int SQL_VALUE_DATE = 9;
    }

    /**
     * Constructs an instance of the ASNMapperSQLColumn class.
     */
    public ASNMapperSQLColumn() {
        super(InitType.ID_ONLY);
    }

    /**
     * Provides a supplier for creating new instances of the {@code SQLColumn} class.
     * This method is typically used to generate fresh {@code SQLColumn} objects for
     * operations related to ASN.1 DER sequence encoding or decoding processes.
     *
     * @return A {@code Supplier} that, when invoked, creates and returns a new {@code SQLColumn} instance.
     */
    @Override
    protected Supplier<SQLColumn> supplierForInstance() {
        return SQLColumn::new;
    }

    /**
     * Maps the data contained in a {@code TagObj<SQLColumn>} object to the appropriate attributes
     * of the associated {@code SQLColumn} instance. The method processes the tag number and updates
     * the corresponding fields in {@code SQLColumn} based on the tag's content.
     *
     * @param tag the {@code TagObj<SQLColumn>} object that contains the tag number and data to be
     *            mapped to the {@code SQLColumn} instance.
     *
     * @return always returns {@code null} after completing the mapping operation.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<SQLColumn> tag) {
        assert tag != null : "TagObj cannot be null.";

        SQLColumn toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case COLUMN_NAME:
                toPop.setColumnName(asUtf8(obj, SQLColumn.JSONMapping.COLUMN_NAME));
                break;
            case COLUMN_INDEX:
                toPop.setColumnIndex(asInteger(obj, SQLColumn.JSONMapping.COLUMN_INDEX));
                break;
            case SQL_TYPE:
                toPop.setSqlType(asInteger(obj, SQLColumn.JSONMapping.SQL_TYPE));
                break;
            case SQL_VALUE_STRING:
                toPop.setSqlValue(asUtf8(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
            case SQL_VALUE_INTEGER:
                toPop.setSqlValue(asInteger(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
            case SQL_VALUE_LONG:
                toPop.setSqlValue(asLong(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
            case SQL_VALUE_DOUBLE:
                toPop.setSqlValue(asReal(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
            case SQL_VALUE_BOOLEAN:
                toPop.setSqlValue(asBool(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
            case SQL_VALUE_DATE:
                toPop.setSqlValue(asDate(obj, SQLColumn.JSONMapping.SQL_VALUE));
                break;
        }
        return null;
    }

    /**
     * Encodes the attributes of a {@code SQLColumn} object as ASN.1 tagged objects
     * and adds them to the specified {@code ASN1EncodableVector}.
     *
     * Depending on the attributes present in the given {@code SQLColumn} object,
     * this method creates tagged objects for fields like column name, column index,
     * SQL type, and SQL value, and adds them to the provided vector.
     *
     * @param item   The {@code SQLColumn} object to be encoded. Must not be null.
     * @param vect  The {@code ASN1EncodableVector} where the encoded tagged
     *              objects will be added. Must not be null.
     */
    @Override
    public void encodeTaggedObject(SQLColumn item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getColumnName() != null) {
            vect.add(new DERTaggedObject(true, COLUMN_NAME, new DERUTF8String(item.getColumnName())));
        }

        if (item.getColumnIndex() != null) {
            vect.add(new DERTaggedObject(true, COLUMN_INDEX, new ASN1Integer(item.getColumnIndex())));
        }

        if (item.getSqlType() != null) {
            vect.add(new DERTaggedObject(true, SQL_TYPE, new ASN1Integer(item.getSqlType())));
        }

        Object sqlValue = item.getSqlValue();
        if (sqlValue != null) {
            if (sqlValue instanceof String) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_STRING, new DERUTF8String((String) sqlValue)));
            } else if (sqlValue instanceof Integer) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_INTEGER, new ASN1Integer((Integer) sqlValue)));
            } else if (sqlValue instanceof Long) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_LONG, new ASN1Integer((Long) sqlValue)));
            } else if (sqlValue instanceof Double) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_DOUBLE, new DERGeneralString(sqlValue.toString())));
            } else if (sqlValue instanceof Boolean) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_BOOLEAN,
                        (Boolean) sqlValue ? ASN1Boolean.TRUE : ASN1Boolean.FALSE));
            } else if (sqlValue instanceof Date) {
                vect.add(new DERTaggedObject(true, SQL_VALUE_DATE, new ASN1GeneralizedTime((Date) sqlValue)));
            } else {
                // For other types, convert to string
                vect.add(new DERTaggedObject(true, SQL_VALUE_STRING, new DERUTF8String(sqlValue.toString())));
            }
        }
    }
}
