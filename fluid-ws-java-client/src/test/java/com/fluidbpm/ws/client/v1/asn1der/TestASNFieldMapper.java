/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the functionality of the ASNMapperField class, specifically
 * the encode and decode operations. This class extends ABaseTestCase to provide a
 * standardized testing environment.
 *
 * The test ensures that a Field object can be encoded into raw bytes and subsequently
 * decoded back into a Field object while maintaining data integrity.
 *
 * Test case:
 * - Encodes an instance of Field into a byte array using ASNMapperField.
 * - Decodes the byte array back into a Field object.
 * - Verifies that the decoded Field object matches the original Field instance
 *   in terms of ID and field name.
 *
 * Dependencies:
 * - Requires the ASNMapperField class for encoding and decoding Field objects.
 * - Uses Assert for validation of expected outcomes.
 */
public class TestASNFieldMapper extends ABaseTestCase {
    @Test
    public void testEncodeDecodeBasic() {
        Field item = new Field(765L);
        item.setFieldName("field name");

        PayloadPopulate payPop = new PayloadPopulate();
        ASNMapperField mapper = new ASNMapperField(payPop);

        byte[] raw = seqBytes(mapper.encode(item));
        Field decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded field name is not as expected.", item.getFieldName(), decoded.getFieldName());
    }

    @Test
    public void testEncodeDecodeFieldValues() {
        Field item = new Field();

        PayloadPopulate payPop = new PayloadPopulate();
        ASNMapperField mapper = new ASNMapperField(payPop);

        // Text:
        {
            item.setTypeAsEnum(Field.Type.Text);
            item.setFieldValue("field val of the th val");

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("Text: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("Text: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }

        // Text Encrypted:
        {
            item.setTypeAsEnum(Field.Type.TextEncrypted);
            item.setFieldValue("field val of the th val");

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("TextEnc: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("TextEnc: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }

        // True False:
        {
            item.setTypeAsEnum(Field.Type.TrueFalse);
            item.setFieldValue(Boolean.TRUE);

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("TrueFalse: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("TrueFalse: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }


    }
}
