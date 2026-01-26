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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import com.google.common.io.BaseEncoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the ability of ASNMapperFormHistoricDataListing to encode and decode FormHistoricDataListing objects.
 * This class extends {@code ABaseTestCase} and uses JUnit for testing.
 *
 * The test ensures that the encoded byte array produced by {@code ASNMapperFormHistoricDataListing.encode(FormHistoricDataListing)}
 * can be properly decoded back into a {@code FormHistoricDataListing} object using {@code ASNMapperFormHistoricDataListing.decode(byte[])}.
 * All relevant fields of the FormHistoricDataListing object, including nested fields like the listing items,
 * are verified for consistency between the original and decoded objects.
 *
 * Methods tested:
 * - {@code ASNMapperFormHistoricDataListing.encode(FormHistoricDataListing)}
 * - {@code ASNMapperFormHistoricDataListing.decode(byte[])}
 *
 * Assertions:
 * - Validates that primary fields such as ID, listing count, and listing items match between the original
 *   and the decoded FormHistoricDataListing object.
 * - Checks that nested and complex fields, including individual FormHistoricData objects, user, field, and form,
 *   are correctly encoded and decoded.
 *
 * Dependencies:
 * - ASNMapperFormHistoricDataListing for encoding/decoding operations.
 * - PayloadPopulate for populating necessary dependencies of {@code ASNMapperFormHistoricDataListing}.
 * - BaseEncoding for byte array representation.
 *
 * Output:
 * - Prints the encoded byte array in Base16 format for debugging purposes.
 */
public class TestASNFormHistoricDataListingMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        FormHistoricDataListing item = new FormHistoricDataListing();
        item.setId(789L);
        item.setServiceTicket("svc-ticket-123");
        item.setRequestUuid("req-uuid-456");
        item.setEcho("echo-789");
        item.setLoggedInUserFromTicket(new User(88L, "ticket-user"));
        item.setListingCount(2);
        item.setListingIndex(0);
        item.setListingPage(1);

        item.setListing(new ArrayList<>());

        // First FormHistoricData item
        FormHistoricData data1 = new FormHistoricData();
        data1.setId(101L);
        data1.setDateAndFieldName("2025-01-22 Field1");
        data1.setDate(new Date(1767906456000L));
        data1.setDateTimestamp(1767906456000L);
        data1.setFormContainerFieldValuesJSON("{\"field\":\"value1\"}");
        data1.setLogEntryType("UPDATE");
        data1.setHistoricEntryType(FormHistoricData.HistoricEntryType.FIELD_AND_VALUE);
        data1.setIsFieldDifferentFromPrevious(true);
        data1.setIsFieldTypeSignature(false);
        data1.setIsEscapeText(true);
        data1.setDescription("First historic entry");
        data1.setUser(new User(55L, "john-doe"));
        data1.setField(new Field(333L, "testField"));
        data1.setFormForAuditCreate(new Form("form type", "this is a title"));
        data1.getFormForAuditCreate().setId(4664L);

        // Second FormHistoricData item
        FormHistoricData data2 = new FormHistoricData();
        data2.setId(102L);
        data2.setDateAndFieldName("2025-01-22 Field2");
        data2.setDate(new Date(1767906456000L));
        data2.setDateTimestamp(1767906456000L);
        data2.setLogEntryType("CREATE");
        data2.setHistoricEntryType(FormHistoricData.HistoricEntryType.FORM_CONTAINER);
        data2.setDescription("Second historic entry");
        data2.setUser(new User(66L, "jane-smith"));

        item.getListing().add(data1);
        item.getListing().add(data2);

        PayloadPopulate payPop = new PayloadPopulate();
        ASNMapperFormHistoricData mapHistData = new ASNMapperFormHistoricData(
                new ASNMapperUser(),
                new ASNMapperField(payPop),
                new ASNMapperForm(payPop)
        );
        ASNMapperFormHistoricDataListing mapper = new ASNMapperFormHistoricDataListing(mapHistData);

        byte[] raw = seqBytes(mapper.encode(item));
        FormHistoricDataListing decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded id is not as expected.", item.getId(), decoded.getId());
        Assert.assertEquals("Decoded listing count is not as expected.", item.getListingCount(), decoded.getListingCount());
        Assert.assertEquals("Decoded listing index is not as expected.", item.getListingIndex(), decoded.getListingIndex());
        Assert.assertEquals("Decoded listing page is not as expected.", item.getListingPage(), decoded.getListingPage());
        Assert.assertEquals("Decoded service ticket is not as expected.", item.getServiceTicket(), decoded.getServiceTicket());
        Assert.assertEquals("Decoded request uuid is not as expected.", item.getRequestUuid(), decoded.getRequestUuid());
        Assert.assertEquals("Decoded echo is not as expected.", item.getEcho(), decoded.getEcho());
        Assert.assertEquals("Decoded logged in user id is not as expected.", item.getLoggedInUserFromTicket().getId(), decoded.getLoggedInUserFromTicket().getId());
        Assert.assertEquals("Decoded logged in user username is not as expected.", item.getLoggedInUserFromTicket().getUsername(), decoded.getLoggedInUserFromTicket().getUsername());

        Assert.assertNotNull("Decoded listing should not be null.", decoded.getListing());
        Assert.assertEquals("Decoded listing size is not as expected.", 2, decoded.getListing().size());

        // Verify first FormHistoricData item
        FormHistoricData decodedData1 = decoded.getListing().get(0);
        Assert.assertEquals("Decoded data1 id is not as expected.", data1.getId(), decodedData1.getId());
        Assert.assertEquals("Decoded data1 dateAndFieldName is not as expected.", data1.getDateAndFieldName(), decodedData1.getDateAndFieldName());
        Assert.assertEquals("Decoded data1 date is not as expected.", data1.getDate().getTime(), decodedData1.getDate().getTime());
        Assert.assertEquals("Decoded data1 dateTimestamp is not as expected.", data1.getDateTimestamp(), decodedData1.getDateTimestamp());
        Assert.assertEquals("Decoded data1 formContainerFieldValuesJSON is not as expected.", data1.getFormContainerFieldValuesJSON(), decodedData1.getFormContainerFieldValuesJSON());
        Assert.assertEquals("Decoded data1 logEntryType is not as expected.", data1.getLogEntryType(), decodedData1.getLogEntryType());
        Assert.assertEquals("Decoded data1 historicEntryType is not as expected.", data1.getHistoricEntryType(), decodedData1.getHistoricEntryType());
        Assert.assertEquals("Decoded data1 isFieldDifferentFromPrevious is not as expected.", data1.getIsFieldDifferentFromPrevious(), decodedData1.getIsFieldDifferentFromPrevious());
        Assert.assertEquals("Decoded data1 isFieldTypeSignature is not as expected.", data1.getIsFieldTypeSignature(), decodedData1.getIsFieldTypeSignature());
        Assert.assertEquals("Decoded data1 isEscapeText is not as expected.", data1.getIsEscapeText(), decodedData1.getIsEscapeText());
        Assert.assertEquals("Decoded data1 description is not as expected.", data1.getDescription(), decodedData1.getDescription());
        Assert.assertEquals("Decoded data1 user id is not as expected.", data1.getUser().getId(), decodedData1.getUser().getId());
        Assert.assertEquals("Decoded data1 user username is not as expected.", data1.getUser().getUsername(), decodedData1.getUser().getUsername());
        Assert.assertEquals("Decoded data1 field id is not as expected.", data1.getField().getId(), decodedData1.getField().getId());
        Assert.assertEquals("Decoded data1 field name is not as expected.", data1.getField().getFieldName(), decodedData1.getField().getFieldName());
        Assert.assertEquals("Decoded data1 formForAuditCreate id is not as expected.", data1.getFormForAuditCreate().getId(), decodedData1.getFormForAuditCreate().getId());
        Assert.assertEquals("Decoded data1 formForAuditCreate formType is not as expected.", data1.getFormForAuditCreate().getFormType(), decodedData1.getFormForAuditCreate().getFormType());

        // Verify second FormHistoricData item
        FormHistoricData decodedData2 = decoded.getListing().get(1);
        Assert.assertEquals("Decoded data2 id is not as expected.", data2.getId(), decodedData2.getId());
        Assert.assertEquals("Decoded data2 dateAndFieldName is not as expected.", data2.getDateAndFieldName(), decodedData2.getDateAndFieldName());
        Assert.assertEquals("Decoded data2 date is not as expected.", data2.getDate().getTime(), decodedData2.getDate().getTime());
        Assert.assertEquals("Decoded data2 logEntryType is not as expected.", data2.getLogEntryType(), decodedData2.getLogEntryType());
        Assert.assertEquals("Decoded data2 historicEntryType is not as expected.", data2.getHistoricEntryType(), decodedData2.getHistoricEntryType());
        Assert.assertEquals("Decoded data2 description is not as expected.", data2.getDescription(), decodedData2.getDescription());
        Assert.assertEquals("Decoded data2 user id is not as expected.", data2.getUser().getId(), decodedData2.getUser().getId());
        Assert.assertEquals("Decoded data2 user username is not as expected.", data2.getUser().getUsername(), decodedData2.getUser().getUsername());

        System.out.println(BaseEncoding.base16().encode(raw));
    }
}
