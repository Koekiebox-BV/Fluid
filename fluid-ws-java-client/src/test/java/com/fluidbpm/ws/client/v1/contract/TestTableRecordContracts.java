package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.form.*;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.form.WebSocketTableRecordBatchCreateClient;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetTableFormsPageClient;
import com.fluidbpm.ws.client.v1.asn1der.*;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.PayloadPopulate;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class TestTableRecordContracts {
    private TableRecordListing batch() {
        TableRecordListing batch = new TableRecordListing();
        batch.setListing(Arrays.asList(new TableRecord(new Form(11L), new Form(12L), new Field(13L)),
                new TableRecord(new Form(21L), new Form(12L), new Field(13L))));
        batch.setListingCount(2);
        return batch;
    }
    @Test public void binaryTransmissionRecognizesNewListingType() {
        ASNMapperFactory factory = new ASNMapperFactory(ASNGlobal.Type.TABLE_RECORD_LISTING);
        com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission transmission =
            new com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission(ASNGlobal.Type.TABLE_RECORD_LISTING);
        transmission.setTransmissionObject(batch());
        transmission.setRequestObject(new com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject(
            ASNGlobal.Path.FormContainer.FORM_CONT_CREATE_TABLE_RECORDS_BATCH));
        transmission.setEcho("batch-der");
        com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission decoded =
            factory.readBaseTransmission(factory.writeObjectForSend(transmission));
        assertEquals("batch-der",decoded.getEcho());
        assertEquals(ASNGlobal.Path.FormContainer.FORM_CONT_CREATE_TABLE_RECORDS_BATCH,decoded.getRequestObject().getPath());
        assertEquals(2,((TableRecordListing)decoded.getTransmissionObject()).getListing().size());
    }
    @Test(timeout=30000) public void binaryClientBatchAndPageContracts() throws Exception {
        try (LocalWebSocketFixture server=new LocalWebSocketFixture();
             com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient client=
                 new com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient(server.endpoint(),"010203",3000)) {
            java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
            server.binaryResponse=bytes -> {
                org.bouncycastle.asn1.ASN1Sequence seq=new ASNMapperError().initSeq(bytes);
                int type=org.bouncycastle.asn1.ASN1Integer.getInstance(seq.getObjectAt(0)).getValue().intValue();
                ASNMapperFactory factory=new ASNMapperFactory(type);
                com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission request=factory.readBaseTransmission(bytes);
                calls.incrementAndGet();
                if (type==ASNGlobal.Type.TABLE_RECORD_LISTING) {
                    assertEquals(ASNGlobal.Path.FormContainer.FORM_CONT_CREATE_TABLE_RECORDS_BATCH,request.getRequestObject().getPath());
                    assertEquals(2,((TableRecordListing)request.getTransmissionObject()).getListing().size());
                    return factory.writeObjectForSend(request);
                }
                assertEquals(ASNGlobal.Path.FormContainer.FORM_CONT_GET_TABLE_FORMS_PAGE,request.getRequestObject().getPath());
                assertEquals(25,request.getRequestObject().getRequestParamInteger("query_limit"));
                assertEquals(50,request.getRequestObject().getRequestParamInteger("offset"));
                FormListing page=new FormListing();page.setListing(Collections.singletonList(new Form(51L)));
                page.setListingCount(10003);page.setListingIndex(50);page.setListingPage(2);
                com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission response=
                    new com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission(ASNGlobal.Type.FORM_LISTING);
                response.setTransmissionObject(page);response.setEcho(request.getEcho());
                return new ASNMapperFactory(ASNGlobal.Type.FORM_LISTING).writeObjectForSend(response);
            };
            assertEquals(2,client.createTableRecordsBatch(batch()).getListing().size());
            FormListing page=client.getTableFormsPage(new Form(12L),true,7,25,50);
            assertEquals(Integer.valueOf(10003),page.getListingCount());
            assertEquals(Integer.valueOf(50),page.getListingIndex());
            assertEquals(Long.valueOf(51),page.getListing().get(0).getId());
            assertEquals(2,calls.get());
        }
    }
    @Test public void bounds() {
        TableRecordPage.validate(1, 0);
        TableRecordPage.validate(250, 60000);
        for (int[] bounds : new int[][]{{0,0},{251,0},{25,-1}}) {
            try { TableRecordPage.validate(bounds[0],bounds[1]); fail("Invalid bounds accepted"); }
            catch (IllegalArgumentException expected) { }
        }
    }
    @Test public void jsonAndDerPreserveBatchLinksAndOrder() {
        TableRecordListing original = batch();
        TableRecordListing json = new TableRecordListing(original.toJsonObject());
        PayloadPopulate population = new PayloadPopulate();
        ASNMapperTableRecordListing mapper = new ASNMapperTableRecordListing(
                new ASNMapperTableRecord(new ASNMapperForm(population), new ASNMapperField(population)));
        TableRecordListing der = mapper.decode(ASNBaseMapper.seqBytes(mapper.encode(original)));
        for (TableRecordListing decoded : Arrays.asList(json, der)) {
            assertEquals(2, decoded.getListingCount().intValue());
            assertEquals(Long.valueOf(11), decoded.getListing().get(0).getFormContainer().getId());
            assertEquals(Long.valueOf(21), decoded.getListing().get(1).getFormContainer().getId());
            assertEquals(Long.valueOf(12), decoded.getListing().get(1).getParentFormContainer().getId());
            assertEquals(Long.valueOf(13), decoded.getListing().get(1).getParentFormField().getId());
        }
    }
    @Test(timeout=30000) public void textWebSocketBatchUsesOneRequestAndReportsErrors() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture();
             WebSocketTableRecordBatchCreateClient client = new WebSocketTableRecordBatchCreateClient(server.endpoint(), "010203", 3000)) {
            assertEquals(1, client.createTableRecordsBatch(batch()).getListingCount().intValue());
            assertEquals(1, server.requests.size());
            assertEquals(2, server.requests.get(0).getAsJsonArray("listing").size());
            assertTrue(server.uri.contains("/batch/010203"));
            server.error = true;
            try { client.createTableRecordsBatch(batch()); fail("Error must propagate"); }
            catch (FluidClientException expected) { assertEquals(1001, expected.getErrorCode()); }
            assertEquals(2, server.requests.size());
        }
    }
    @Test(timeout=30000) public void textWebSocketPagePreservesEnvelopeAndParameters() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture();
             SQLUtilWebSocketGetTableFormsPageClient client = new SQLUtilWebSocketGetTableFormsPageClient(server.endpoint(), "010203", 3000, true, 9, 25, 50)) {
            FormListing page = client.getTableFormsPage(new Form(12L));
            assertEquals(1, page.getListingCount().intValue());
            assertEquals(Long.valueOf(918), page.getListing().get(0).getId());
            assertTrue(server.uri.contains("query_limit=25&offset=50"));
            assertEquals(12, server.requests.get(0).get("id").getAsInt());
        }
    }
}
