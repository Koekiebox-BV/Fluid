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
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperNativeSQLQuery to encode and decode NativeSQLQuery objects.
 * This class extends {@code ABaseTestCase} and uses JUnit for testing.
 *
 * The test ensures that the encoded byte array produced by {@code ASNMapperNativeSQLQuery.encode(NativeSQLQuery)}
 * can be properly decoded back into a {@code NativeSQLQuery} object using {@code ASNMapperNativeSQLQuery.decode(byte[])}.
 * All relevant fields of the NativeSQLQuery object, including nested fields like {@code sqlInputs},
 * are verified for consistency between the original and decoded objects.
 *
 * Methods tested:
 * - {@code ASNMapperNativeSQLQuery.encode(NativeSQLQuery)}
 * - {@code ASNMapperNativeSQLQuery.decode(byte[])}
 *
 * Assertions:
 * - Validates that primary fields such as ID, datasourceName, query, and storedProcedure match between
 *   the original and the decoded NativeSQLQuery object.
 * - Checks that nested and complex fields, including {@code sqlInputs}, are correctly encoded and decoded.
 *
 * Dependencies:
 * - ASNMapperNativeSQLQuery for encoding/decoding operations.
 * - BaseEncoding for byte array representation.
 *
 * Output:
 * - Prints the encoded byte array in Base16 format for debugging purposes.
 */
public class TestASNNativeSQLQueryMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecodeBasic() {
        NativeSQLQuery item = new NativeSQLQuery();
        item.setId(777L);
        item.setDatasourceName("primary_db");
        item.setQuery("SELECT * FROM users WHERE id = ?");

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded datasource name is not as expected.", item.getDatasourceName(), decoded.getDatasourceName());
        Assert.assertEquals("Decoded query is not as expected.", item.getQuery(), decoded.getQuery());
        Assert.assertNull("Decoded stored procedure should be null.", decoded.getStoredProcedure());
    }

    @Test
    public void testEncodeDecodeWithStoredProcedure() {
        NativeSQLQuery item = new NativeSQLQuery();
        item.setId(888L);
        item.setDatasourceName("analytics_db");
        item.setStoredProcedure("sp_calculate_metrics");

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded datasource name is not as expected.", item.getDatasourceName(), decoded.getDatasourceName());
        Assert.assertEquals("Decoded stored procedure is not as expected.", item.getStoredProcedure(), decoded.getStoredProcedure());
        Assert.assertNull("Decoded query should be null.", decoded.getQuery());
    }

    @Test
    public void testEncodeDecodeWithSqlInputs() {
        NativeSQLQuery item = new NativeSQLQuery();
        item.setId(999L);
        item.setDatasourceName("customer_db");
        item.setQuery("INSERT INTO orders (customer_id, amount, status, created_at) VALUES (?, ?, ?, ?)");

        item.setSqlInputs(new ArrayList<>());
        item.addSqlInput(new SQLColumn("customer_id", 1, Types.BIGINT, 12345L));
        item.addSqlInput(new SQLColumn("amount", 2, Types.DOUBLE, 199.99));
        item.addSqlInput(new SQLColumn("status", 3, Types.VARCHAR, "PENDING"));
        item.addSqlInput(new SQLColumn("created_at", 4, Types.TIMESTAMP, new Date(1767906456000L)));

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded datasource name is not as expected.", item.getDatasourceName(), decoded.getDatasourceName());
        Assert.assertEquals("Decoded query is not as expected.", item.getQuery(), decoded.getQuery());

        Assert.assertNotNull("Decoded SQL inputs should not be null.", decoded.getSqlInputs());
        Assert.assertEquals("Decoded SQL inputs count is not as expected.", item.getSqlInputs().size(), decoded.getSqlInputs().size());

        // Verify first input (BIGINT)
        SQLColumn input0 = decoded.getSqlInputs().get(0);
        Assert.assertEquals("Input[0]: Column name is not as expected.", "customer_id", input0.getColumnName());
        Assert.assertEquals("Input[0]: Column index is not as expected.", Integer.valueOf(1), input0.getColumnIndex());
        Assert.assertEquals("Input[0]: SQL type is not as expected.", Integer.valueOf(Types.BIGINT), input0.getSqlType());
        Assert.assertEquals("Input[0]: SQL value is not as expected.", 12345L, input0.getSqlValue());

        // Verify second input (DOUBLE)
        SQLColumn input1 = decoded.getSqlInputs().get(1);
        Assert.assertEquals("Input[1]: Column name is not as expected.", "amount", input1.getColumnName());
        Assert.assertEquals("Input[1]: Column index is not as expected.", Integer.valueOf(2), input1.getColumnIndex());
        Assert.assertEquals("Input[1]: SQL type is not as expected.", Integer.valueOf(Types.DOUBLE), input1.getSqlType());
        Assert.assertEquals("Input[1]: SQL value is not as expected.",
                199.99, Double.parseDouble(input1.getSqlValue().toString()), 0.01);

        // Verify third input (VARCHAR)
        SQLColumn input2 = decoded.getSqlInputs().get(2);
        Assert.assertEquals("Input[2]: Column name is not as expected.", "status", input2.getColumnName());
        Assert.assertEquals("Input[2]: SQL value is not as expected.", "PENDING", input2.getSqlValue());

        // Verify fourth input (TIMESTAMP)
        SQLColumn input3 = decoded.getSqlInputs().get(3);
        Assert.assertEquals("Input[3]: Column name is not as expected.", "created_at", input3.getColumnName());
        Assert.assertTrue("Input[3]: SQL value should be a Date.", input3.getSqlValue() instanceof Date);
    }

    @Test
    public void testEncodeDecodeComplex() {
        NativeSQLQuery item = new NativeSQLQuery(1001L);
        item.setDatasourceName("reporting_db");
        item.setQuery("UPDATE products SET name = ?, price = ?, active = ? WHERE id = ?");

        item.setSqlInputs(new ArrayList<>());
        item.addSqlInput(new SQLColumn("name", 1, Types.VARCHAR, "Updated Product Name"));
        item.addSqlInput(new SQLColumn("price", 2, Types.DOUBLE, 49.99));
        item.addSqlInput(new SQLColumn("active", 3, Types.BOOLEAN, true));
        item.addSqlInput(new SQLColumn("id", 4, Types.INTEGER, 567));

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Complex: ID is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Complex: Datasource name is not as expected.", item.getDatasourceName(), decoded.getDatasourceName());
        Assert.assertEquals("Complex: Query is not as expected.", item.getQuery(), decoded.getQuery());
        Assert.assertEquals("Complex: SQL inputs size is not as expected.", 4, decoded.getSqlInputs().size());

        // Verify all inputs are present
        for (int i = 0; i < item.getSqlInputs().size(); i++) {
            SQLColumn original = item.getSqlInputs().get(i);
            SQLColumn decodedInput = decoded.getSqlInputs().get(i);
            Assert.assertEquals("Complex: Input[" + i + "] column name is not as expected.",
                    original.getColumnName(), decodedInput.getColumnName());
            Assert.assertEquals("Complex: Input[" + i + "] column index is not as expected.",
                    original.getColumnIndex(), decodedInput.getColumnIndex());
            Assert.assertEquals("Complex: Input[" + i + "] SQL type is not as expected.",
                    original.getSqlType(), decodedInput.getSqlType());
        }
    }

    @Test
    public void testEncodeDecodeEmptySqlInputs() {
        NativeSQLQuery item = new NativeSQLQuery();
        item.setId(1111L);
        item.setDatasourceName("test_db");
        item.setQuery("SELECT COUNT(*) FROM users");
        
        item.setSqlInputs(new ArrayList<>());

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Empty: ID is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Empty: Query is not as expected.", item.getQuery(), decoded.getQuery());
        Assert.assertNull("Empty: SQL inputs should be null if empty or null.", decoded.getSqlInputs());
    }

    @Test
    public void testEncodeDecodeNullSqlInputs() {
        NativeSQLQuery item = new NativeSQLQuery();
        item.setId(2222L);
        item.setDatasourceName("main_db");
        item.setQuery("DELETE FROM logs WHERE created_at < ?");
        item.setSqlInputs(null);

        ASNMapperNativeSQLQuery mapper = new ASNMapperNativeSQLQuery();

        byte[] raw = seqBytes(mapper.encode(item));
        NativeSQLQuery decoded = mapper.decode(raw);

        Assert.assertEquals("Null: ID is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Null: Query is not as expected.", item.getQuery(), decoded.getQuery());
        Assert.assertNull("Null: SQL inputs should be null.", decoded.getSqlInputs());
    }
}
