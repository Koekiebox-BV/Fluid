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
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the functionality of the ASNMapperSQLColumn class, specifically
 * the encode and decode operations. This class extends ABaseTestCase to provide a
 * standardized testing environment.
 *
 * The test ensures that a SQLColumn object can be encoded into raw bytes and subsequently
 * decoded back into a SQLColumn object while maintaining data integrity.
 *
 * Test cases:
 * - Encodes an instance of SQLColumn into a byte array using ASNMapperSQLColumn.
 * - Decodes the byte array back into a SQLColumn object.
 * - Verifies that the decoded SQLColumn object matches the original SQLColumn instance
 *   in terms of ID, column name, column index, SQL type, and SQL value.
 *
 * Dependencies:
 * - Requires the ASNMapperSQLColumn class for encoding and decoding SQLColumn objects.
 * - Uses Assert for validation of expected outcomes.
 */
public class TestASNSQLColumnMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecodeBasic() {
        SQLColumn item = new SQLColumn();
        item.setId(123L);
        item.setColumnName("test_column");
        item.setColumnIndex(1);
        item.setSqlType(Types.VARCHAR);
        item.setSqlValue("test value");

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("Decoded column index is not as expected.", item.getColumnIndex(), decoded.getColumnIndex());
        Assert.assertEquals("Decoded SQL type is not as expected.", item.getSqlType(), decoded.getSqlType());
        Assert.assertEquals("Decoded SQL value is not as expected.", item.getSqlValue(), decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeStringValue() {
        SQLColumn item = new SQLColumn("name_column", 1, Types.VARCHAR, "String Value");
        item.setId(456L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("String: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("String: SQL value is not as expected.", item.getSqlValue(), decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeIntegerValue() {
        SQLColumn item = new SQLColumn("count_column", 2, Types.INTEGER, 42);
        item.setId(789L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Integer: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("Integer: SQL value is not as expected.", item.getSqlValue(), decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeLongValue() {
        SQLColumn item = new SQLColumn("bigint_column", 3, Types.BIGINT, 9223372036854775807L);
        item.setId(101L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Long: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("Long: SQL value is not as expected.", item.getSqlValue(), decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeDoubleValue() {
        SQLColumn item = new SQLColumn("price_column", 4, Types.DOUBLE, 123.456);
        item.setId(202L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Double: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("Double: SQL value is not as expected.",
                ((Double) item.getSqlValue()).doubleValue(),
                Double.parseDouble(decoded.getSqlValue().toString()),
                0.0001);
    }

    @Test
    public void testEncodeDecodeBooleanValue() {
        SQLColumn item = new SQLColumn("flag_column", 5, Types.BOOLEAN, true);
        item.setId(303L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Boolean: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertEquals("Boolean: SQL value is not as expected.", item.getSqlValue(), decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeDateValue() {
        Date testDate = new Date(1767906456000L);
        SQLColumn item = new SQLColumn("date_column", 6, Types.TIMESTAMP, testDate);
        item.setId(404L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Date: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertTrue("Date: SQL value is not as expected.", decoded.getSqlValue() instanceof Date);
        Assert.assertEquals("Date: SQL value time is not as expected.",
                ((Date) item.getSqlValue()).getTime(),
                ((Date) decoded.getSqlValue()).getTime());
    }

    @Test
    public void testEncodeDecodeNullValue() {
        SQLColumn item = new SQLColumn("nullable_column", 7, Types.VARCHAR, null);
        item.setId(505L);

        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        byte[] raw = seqBytes(mapper.encode(item));
        SQLColumn decoded = mapper.decode(raw);

        Assert.assertEquals("Null: Column name is not as expected.", item.getColumnName(), decoded.getColumnName());
        Assert.assertNull("Null: SQL value should be null.", decoded.getSqlValue());
    }

    @Test
    public void testEncodeDecodeMultipleTypes() {
        ASNMapperSQLColumn mapper = new ASNMapperSQLColumn();

        // Test various SQL types
        Object[][] testCases = {
                {Types.VARCHAR, "Text Value"},
                {Types.INTEGER, 100},
                {Types.BIGINT, 999999999999L},
                {Types.DOUBLE, 3.14159},
                {Types.BOOLEAN, false},
                {Types.TIMESTAMP, new Date()},
        };

        for (int i = 0; i < testCases.length; i++) {
            Integer sqlType = (Integer) testCases[i][0];
            Object sqlValue = testCases[i][1];

            SQLColumn item = new SQLColumn("column_" + i, i + 1, sqlType, sqlValue);
            item.setId((long) (600 + i));

            byte[] raw = seqBytes(mapper.encode(item));
            SQLColumn decoded = mapper.decode(raw);

            Assert.assertEquals("Multiple[" + i + "]: ID is not as expected.", item.getId(), decoded.getId());
            Assert.assertEquals("Multiple[" + i + "]: Column name is not as expected.",
                    item.getColumnName(), decoded.getColumnName());
            Assert.assertEquals("Multiple[" + i + "]: Column index is not as expected.",
                    item.getColumnIndex(), decoded.getColumnIndex());
            Assert.assertEquals("Multiple[" + i + "]: SQL type is not as expected.",
                    item.getSqlType(), decoded.getSqlType());
            Assert.assertNotNull("Multiple[" + i + "]: SQL value should not be null.", decoded.getSqlValue());
        }
    }
}
