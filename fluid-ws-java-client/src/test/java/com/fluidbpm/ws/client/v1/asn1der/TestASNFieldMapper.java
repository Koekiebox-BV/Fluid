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
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoice;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.ASNMultiChoiceField;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String fieldName = "field value testing";
        Field item = new Field();
        item.setFieldName(fieldName);

        List<ASNMultiChoiceField> mcFormField = new ArrayList<>();
        List<ASNMultiChoice> choices = new ArrayList<>();
        choices.add(new ASNMultiChoice(1L, "Option 1"));
        choices.add(new ASNMultiChoice(2L, "Option 2"));
        choices.add(new ASNMultiChoice(3L, "Option 3"));
        mcFormField.add(new ASNMultiChoiceField(fieldName, choices));
        PayloadPopulate payPop = new PayloadPopulate(
                mcFormField, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        ASNMapperField mapper = new ASNMapperField(payPop);
        ASNMapperForm mapForm = new ASNMapperForm(payPop);
        ASNTableFieldMapper tblFieldMapper = new ASNTableFieldMapper(mapForm);
        mapper.setMapTableField(tblFieldMapper);

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
            Assert.assertEquals("TrueFalse: Value is not as expected.", item.getFieldValueAsBoolean(), decodedVal.getFieldValueAsBoolean());
        }

        // DateTime:
        {
            item.setTypeAsEnum(Field.Type.DateTime);
            Date dateValue = new Date();
            item.setFieldValue(dateValue);

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("DateTime: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("DateTime: Value is not as expected.", item.getFieldValueAsDate().toString(), decodedVal.getFieldValueAsDate().toString());
            Assert.assertNotEquals("DateTime: Value is not as expected. Conversion issues expected.",
                    item.getFieldValueAsDate().getTime(), decodedVal.getFieldValueAsDate().getTime());
        }

        // Decimal:
        {
            double[] dblVals = new double[] {
                    123.45
            };

            for (double dblVal : dblVals) {
                item.setTypeAsEnum(Field.Type.Decimal);
                item.setFieldValue(123.45);

                Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

                Assert.assertEquals("Decimal: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
                Assert.assertEquals("Decimal: Value is not as expected.", item.getFieldValueAsBigDecimal(), decodedVal.getFieldValueAsBigDecimal());
            }
        }

        // MultipleChoice:
        {
            item.setTypeAsEnum(Field.Type.MultipleChoice);
            List<String> selected = new ArrayList<>();
            selected.add("Option 1");
            selected.add("Option 3");
            MultiChoice multiChoice = new MultiChoice(selected);
            item.setFieldValue(multiChoice);

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("MultipleChoice: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            MultiChoice decodedMultiChoice = decodedVal.getFieldValueAsMultiChoice();
            Assert.assertEquals("MultipleChoice: Value Size is not as expected.", multiChoice.getSelectedMultiChoices().size(), decodedMultiChoice.getSelectedMultiChoices().size());
            Assert.assertEquals("MultipleChoice: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }

        // ParagraphText:
        {
            item.setTypeAsEnum(Field.Type.ParagraphText);
            item.setFieldValue("This is a paragraph text\nwith multiple lines\nof content.");

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("ParagraphText: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("ParagraphText: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }

        // Table:
        {
            item.setTypeAsEnum(Field.Type.Table);
            TableField tblFld = new TableField();
            tblFld.setSumDecimals(Boolean.TRUE);
            tblFld.setTableRecords(new ArrayList<>());
            Form frmRecord1 = new Form("form type", "this is title");
            frmRecord1.setId(-1L);
            frmRecord1.setFieldValue("name", "benny", Field.Type.Text);
            frmRecord1.getFormFields().get(0).setId(-1L);
            tblFld.getTableRecords().add(frmRecord1);

            item.setFieldValue(tblFld);
            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("Table: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());

            TableField decodedTblFld = decodedVal.getFieldValueAsTableField();
            TableField itemTblFld = item.getFieldValueAsTableField();
            Assert.assertEquals("Table: SumDecimals is not as expected.", itemTblFld.getSumDecimals(), decodedTblFld.getSumDecimals());
            Assert.assertEquals("Table: Value is not as expected.", itemTblFld.toString(), decodedTblFld.toString());
        }

        // Label:
        {
            item.setTypeAsEnum(Field.Type.Label);
            item.setFieldValue("Label text for visual purposes");

            Field decodedVal = mapper.decode(seqBytes(mapper.encode(item)));

            Assert.assertEquals("Label: Value type is not as expected.", item.getTypeAsEnum(), decodedVal.getTypeAsEnum());
            Assert.assertEquals("Label: Value is not as expected.", item.getFieldValueAsString(), decodedVal.getFieldValueAsString());
        }
    }
}
